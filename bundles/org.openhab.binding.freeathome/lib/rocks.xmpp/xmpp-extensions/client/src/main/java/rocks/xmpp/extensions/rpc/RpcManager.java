/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 Christian Schudt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package rocks.xmpp.extensions.rpc;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.AbstractIQHandler;
import rocks.xmpp.core.stanza.IQHandler;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.extensions.rpc.model.Rpc;
import rocks.xmpp.extensions.rpc.model.Value;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This manager allows you to call remote procedures and handle inbound calls, if enabled.
 * <p>
 * By default this manager is disabled. If you want to support RPC (i.e. handle inbound calls), {@linkplain #setEnabled(boolean) enable} it and {@linkplain #setRpcHandler(RpcHandler) set a RPC handler}, which allows you to handle inbound calls.
 * </p>
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0009.html">XEP-0009: Jabber-RPC</a>
 */
public final class RpcManager extends Manager {

    private static final Logger logger = Logger.getLogger(RpcManager.class.getName());

    private final IQHandler iqHandler;

    private RpcHandler rpcHandler;

    private RpcManager(final XmppSession xmppSession) {
        super(xmppSession);

        this.iqHandler = new AbstractIQHandler(IQ.Type.SET) {
            @Override
            protected IQ processRequest(IQ iq) {
                Rpc rpc = iq.getExtension(Rpc.class);
                // If there's an inbound RPC
                RpcHandler rpcHandler1;
                synchronized (this) {
                    rpcHandler1 = rpcHandler;
                }
                if (rpcHandler1 != null) {
                    final Rpc.MethodCall methodCall = rpc.getMethodCall();
                    final List<Value> parameters = new ArrayList<>(methodCall.getParameters());

                    try {
                        Value value = rpcHandler1.process(iq.getFrom(), methodCall.getMethodName(), parameters);
                        return iq.createResult(Rpc.ofMethodResponse(value));
                    } catch (RpcException e1) {
                        return iq.createResult(Rpc.ofFaultResponse(e1.getFaultCode(), e1.getFaultString()));
                    } catch (Throwable e1) {
                        logger.log(Level.WARNING, e1.getMessage(), e1);
                        return iq.createError(Condition.INTERNAL_SERVER_ERROR);
                    }
                }
                return iq.createError(Condition.SERVICE_UNAVAILABLE);
            }
        };
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        xmppSession.addIQHandler(Rpc.class, iqHandler);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        xmppSession.removeIQHandler(Rpc.class);
    }

    /**
     * Calls a remote procedure.
     *
     * @param jid        The JID, which will receive the RPC.
     * @param methodName The method name.
     * @param parameters The parameters.
     * @return The async result with the returned value.
     */
    public AsyncResult<Value> call(Jid jid, String methodName, Value... parameters) {
        AsyncResult<IQ> query = xmppSession.query(IQ.set(jid, Rpc.ofMethodCall(methodName, parameters)));
        return query.thenApply(result -> {
            if (result != null) {
                Rpc rpc = result.getExtension(Rpc.class);
                if (rpc != null) {
                    Rpc.MethodResponse methodResponse = rpc.getMethodResponse();
                    if (methodResponse != null) {
                        if (methodResponse.getFault() != null) {
                            throw new CompletionException(new RpcException(methodResponse.getFault().getFaultCode(), methodResponse.getFault().getFaultString()));
                        }
                        return methodResponse.getResponse();
                    }
                }
            }
            return null;
        });
    }

    /**
     * Sets the RPC handler, which will handle RPCs.
     *
     * @param rpcHandler The RPC handler.
     */
    public synchronized void setRpcHandler(RpcHandler rpcHandler) {
        this.rpcHandler = rpcHandler;
        setEnabled(rpcHandler != null);
    }

    @Override
    protected void dispose() {
        rpcHandler = null;
    }
}
