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

package rocks.xmpp.sample.component;

import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.session.XmppSessionConfiguration;
import rocks.xmpp.core.session.debug.ConsoleDebugger;
import rocks.xmpp.core.stanza.AbstractIQHandler;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.extensions.bytestreams.s5b.model.Socks5ByteStream;
import rocks.xmpp.extensions.component.accept.ExternalComponent;
import rocks.xmpp.extensions.disco.ServiceDiscoveryManager;
import rocks.xmpp.extensions.disco.model.info.Identity;
import rocks.xmpp.extensions.langtrans.model.LanguageTranslation;
import rocks.xmpp.extensions.langtrans.model.items.LanguageSupport;
import rocks.xmpp.extensions.muc.model.Muc;
import rocks.xmpp.nio.netty.client.NettyTcpConnectionConfiguration;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author Christian Schudt
 */
public class ExternalComponentSample {
    public static void main(String[] args) {

        Executors.newFixedThreadPool(1).execute(() -> {
            try {

                XmppSessionConfiguration configuration = XmppSessionConfiguration.builder()
                        .debugger(ConsoleDebugger.class)
                        .build();

                NettyTcpConnectionConfiguration connectionConfiguration = NettyTcpConnectionConfiguration.builder()
                        .hostname("localhost")
                        .port(5275)
                        .build();
                ExternalComponent myComponent = ExternalComponent.create("translation", "test", configuration, connectionConfiguration);

                ServiceDiscoveryManager serviceDiscoveryManager = myComponent.getManager(ServiceDiscoveryManager.class);

                // Add an identity for the component. This will be used by clients who want to discover the translation service.
                serviceDiscoveryManager.addIdentity(Identity.automationTranslation().withName("Translation Provider Service"));
                // Our component supports the XEP-0171 protocol, let's advertise it by including the protocol name in the feature list,
                // so that clients can discover our component as language translation service and can send queries to it.
                serviceDiscoveryManager.addFeature(LanguageTranslation.NAMESPACE);
                // Don't advertise the MUC feature. We are no chat service.
                serviceDiscoveryManager.removeFeature(Muc.NAMESPACE);
                // Don't advertise the SOCKS bytestreams feature. We are no stream proxy.
                serviceDiscoveryManager.removeFeature(Socks5ByteStream.NAMESPACE);

                myComponent.addIQHandler(LanguageSupport.class, new AbstractIQHandler(IQ.Type.GET) {
                    @Override
                    protected IQ processRequest(IQ iq) {
                        Collection<LanguageSupport.Item> items = new ArrayDeque<>();
                        items.add(new LanguageSupport.Item(Locale.ENGLISH, myComponent.getDomain(), Locale.GERMAN, "testEngine", true, null));
                        return iq.createResult(new LanguageSupport(items));
                    }
                });

                myComponent.addIQHandler(LanguageTranslation.class, new AbstractIQHandler(IQ.Type.GET) {
                    @Override
                    protected IQ processRequest(IQ iq) {

                        LanguageTranslation translation = iq.getExtension(LanguageTranslation.class);

                        Collection<LanguageTranslation.Translation> translations = translation.getTranslations().stream()
                                .map(t -> LanguageTranslation.Translation.forDestinationLanguage(t.getDestinationLanguage())
                                        .withSourceLanguage(translation.getSourceLanguage())
                                        .withTranslatedText("HALLO")).collect(Collectors.toCollection(ArrayDeque::new));
                        LanguageTranslation languageTranslation = new LanguageTranslation(translations);
                        return iq.createResult(languageTranslation);
                    }
                });

                // Connect
                myComponent.connect();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
