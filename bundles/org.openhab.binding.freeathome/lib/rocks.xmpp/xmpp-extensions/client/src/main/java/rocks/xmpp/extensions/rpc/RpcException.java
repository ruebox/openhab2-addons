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

/**
 * This exception can be thrown by a {@link RpcHandler} to indicate application-level errors.
 * <p>
 * By throwing this exception in the  {@link RpcHandler#process(Jid, String, java.util.List)} method a {@code <fault/>} will be returned.
 * </p>
 *
 * @author Christian Schudt
 * @see RpcHandler#process(Jid, String, java.util.List)
 */
public final class RpcException extends Exception {

    private final int faultCode;

    private final String faultString;

    /**
     * Creates a RPC exception.
     *
     * @param faultCode   The fault code. It is up to the server implementer, or higher-level standards to specify fault codes.
     * @param faultString The fault string, i.e. error message.
     */
    public RpcException(int faultCode, String faultString) {
        this.faultCode = faultCode;
        this.faultString = faultString;
    }

    /**
     * Gets the fault code.
     *
     * @return The fault code.
     */
    public int getFaultCode() {
        return faultCode;
    }

    /**
     * Gets the fault string.
     *
     * @return The fault string.
     */
    public String getFaultString() {
        return faultString;
    }
}
