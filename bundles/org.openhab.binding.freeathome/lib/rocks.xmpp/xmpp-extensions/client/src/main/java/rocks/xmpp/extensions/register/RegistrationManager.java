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

package rocks.xmpp.extensions.register;


import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stream.client.StreamFeaturesManager;
import rocks.xmpp.extensions.caps.EntityCapabilitiesManager;
import rocks.xmpp.extensions.register.model.Registration;
import rocks.xmpp.extensions.register.model.feature.RegisterFeature;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.util.concurrent.CompletableFuture;

/**
 * This manager allows to register, cancel an existing registration (i.e. remove an account) or change the password with a host.
 *
 * @author Christian Schudt
 */
public final class RegistrationManager extends Manager {

    private RegistrationManager(XmppSession xmppSession) {
        super(xmppSession);
    }

    /**
     * Determines, if in-band registration is supported by the server.
     *
     * @return The async result with true, if registration is supported by the server; otherwise false.
     */
    public final AsyncResult<Boolean> isRegistrationSupported() {
        // server returns a stream header to the client and MAY announce support for in-band registration by including the relevant stream feature.
        boolean isSupported = xmppSession.getManager(StreamFeaturesManager.class).getFeatures().containsKey(RegisterFeature.class);

        // Since the stream feature is only optional, discover the server features, too.
        if (!isSupported) {
            EntityCapabilitiesManager entityCapabilitiesManager = xmppSession.getManager(EntityCapabilitiesManager.class);
            return entityCapabilitiesManager.discoverCapabilities(xmppSession.getDomain())
                    .thenApply(infoNode -> infoNode.getFeatures().contains(Registration.NAMESPACE));
        }
        return new AsyncResult<>(CompletableFuture.completedFuture(true));
    }

    /**
     * Gets the registration data (instructions, fields and form) from the server.
     * <p>
     * In order to check if a field is required, you should check if a field is not null.
     * </p>
     * If you are already registered to the server, this method returns your registration data and {@link rocks.xmpp.extensions.register.model.Registration#isRegistered()} returns true.
     *
     * @return The async result with the registration data.
     * @see <a href="https://xmpp.org/extensions/xep-0077.html#usecases-register">3.1 Entity Registers with a Host</a>
     * @see rocks.xmpp.extensions.register.model.Registration
     */
    public final AsyncResult<Registration> getRegistration() {
        return xmppSession.query(IQ.get(Registration.empty()), Registration.class);
    }

    /**
     * Registers a new account. Call this method before authenticating.
     *
     * @param registration The registration.
     * @return The async result.
     * @see <a href="https://xmpp.org/extensions/xep-0077.html#usecases-register">3.1 Entity Registers with a Host</a>
     */
    public final AsyncResult<Void> register(Registration registration) {
        return xmppSession.query(IQ.set(registration), Void.class);
    }

    /**
     * Cancels a registration. This method must be called after having authenticated to the server.
     *
     * @return The async result.
     * @see <a href="https://xmpp.org/extensions/xep-0077.html#usecases-cancel">3.2 Entity Cancels an Existing Registration</a>
     */
    public final AsyncResult<Void> cancelRegistration() {
        return xmppSession.query(IQ.set(Registration.remove()), Void.class);
    }

    /**
     * Changes the password for the current user. This method must be called after having authenticated to the server.
     *
     * @param username The user name.
     * @param password The password.
     * @return The async result.
     * @see <a href="https://xmpp.org/extensions/xep-0077.html#usecases-changepw">3.3 User Changes Password</a>
     */

    public final AsyncResult<Void> changePassword(String username, String password) {
        return xmppSession.query(IQ.set(Registration.builder().username(username).password(password).build()), Void.class);
    }
}
