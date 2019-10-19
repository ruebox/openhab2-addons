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

package rocks.xmpp.extensions.langtrans;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.extensions.disco.ServiceDiscoveryManager;
import rocks.xmpp.extensions.disco.model.info.Identity;
import rocks.xmpp.extensions.disco.model.items.Item;
import rocks.xmpp.extensions.langtrans.model.LanguageTranslation;
import rocks.xmpp.extensions.langtrans.model.items.LanguageSupport;
import rocks.xmpp.util.concurrent.AsyncResult;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Manages XMPP language translation protocol.
 * <p>
 * It lets you discover language translation providers and ask them to translate a text.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0171.html">XEP-0171: Language Translation</a>
 */
public final class LanguageTranslationManager extends Manager {

    private final ServiceDiscoveryManager serviceDiscoveryManager;

    private LanguageTranslationManager(XmppSession xmppSession) {
        super(xmppSession);
        serviceDiscoveryManager = xmppSession.getManager(ServiceDiscoveryManager.class);
    }

    /**
     * Discovers the language provider on the connected server.
     *
     * @return The async result containing the list of translation providers.
     * @see <a href="https://xmpp.org/extensions/xep-0171.html#disco">4.2 Discovering Translation Providers</a>
     */
    public AsyncResult<List<Item>> discoverTranslationProviders() {
        return serviceDiscoveryManager.discoverServices(Identity.automationTranslation());
    }

    /**
     * Discovers language support for a translation provider.
     *
     * @param translationProvider The translation provider.
     * @return The list of supported languages with details.
     * @see <a href="https://xmpp.org/extensions/xep-0171.html#disco-lang">4.2.3 Discovering Language Support</a>
     */
    public AsyncResult<List<LanguageSupport.Item>> discoverLanguageSupport(Jid translationProvider) {
        return xmppSession.query(IQ.get(translationProvider, new LanguageSupport()), LanguageSupport.class).thenApply(LanguageSupport::getItems);
    }

    /**
     * Translates a text by sending a query to a translation provider.
     *
     * @param translationProvider The translation provider.
     * @param text                The text to be translated.
     * @param sourceLanguage      The source language.
     * @param destinationLanguage The destination language.
     * @return The translations.
     * @see <a href="https://xmpp.org/extensions/xep-0171.html#request">4.3 Requesting a Translation from a Service</a>
     */
    public AsyncResult<List<LanguageTranslation.Translation>> translate(Jid translationProvider, String text, Locale sourceLanguage, Locale... destinationLanguage) {
        Collection<LanguageTranslation.Translation> translations = new ArrayDeque<>();
        for (Locale dl : destinationLanguage) {
            translations.add(LanguageTranslation.Translation.forDestinationLanguage(dl));
        }
        return xmppSession.query(IQ.get(translationProvider, new LanguageTranslation(text, sourceLanguage, translations)), LanguageTranslation.class).thenApply(languageTranslation -> languageTranslation != null ? languageTranslation.getTranslations() : Collections.emptyList());
    }
}
