/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Christian Schudt
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

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.core.stanza.model.client.ClientMessage;
import rocks.xmpp.extensions.langtrans.model.LanguageTranslation;
import rocks.xmpp.extensions.langtrans.model.items.LanguageSupport;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.util.Locale;

/**
 * @author Christian Schudt
 */
public class LanguageTranslationTest extends XmlTest {
    protected LanguageTranslationTest() throws JAXBException {
        super(ClientMessage.class, ClientIQ.class, LanguageTranslation.class);
    }

    @Test
    public void marshalLanguageTranslation() throws JAXBException, XMLStreamException {
        LanguageTranslation.Translation translation = LanguageTranslation.Translation.forDestinationLanguage(Locale.ENGLISH).withSourceLanguage(Locale.FRENCH).withEngine("engine").withTranslatedText("text");
        LanguageTranslation translations = new LanguageTranslation(translation);

        String xml = marshal(translations);

        Assert.assertEquals(xml, "<x xmlns=\"urn:xmpp:langtrans\"><translation destination_lang=\"en\" source_lang=\"fr\" engine=\"engine\">text</translation></x>");
    }

    @Test
    public void unmarshalLanguageTranslation() throws XMLStreamException, JAXBException {
        String xml = "<message xml:lang='fr' from='bard@shakespeare.lit/globe' to='playwright@marlowe.lit/theatre'>\n" +
                "  <subject xml:lang='fr'>Bonjour</subject>\n" +
                "  <subject xml:lang='en'>Hello</subject>\n" +
                "  <subject xml:lang='ru'>x443;&#x43B;&#x442;&#x435;</subject>\n" +
                "  <body xml:lang='fr'>comment allez-vous?</body>\n" +
                "  <body xml:lang='en'>How are you?</body>\n" +
                "  <body xml:lang='ru'>&#x41A;&#x430;&#x43A; &#x432;&#x44B;?</body>\n" +
                "  <x xmlns='urn:xmpp:langtrans'>\n" +
                "    <translation destination_lang='en' engine='SYSTRANS' source_lang='fr'/>\n" +
                "    <translation destination_lang='ru' engine='SYSTRANS' source_lang='en'/>\n" +
                "  </x>\n" +
                "</message>\n";
        Message message = unmarshal(xml, Message.class);
        LanguageTranslation languageTranslation = message.getExtension(LanguageTranslation.class);
        Assert.assertNotNull(languageTranslation);
        Assert.assertEquals(languageTranslation.getTranslations().size(), 2);
        Assert.assertEquals(languageTranslation.getTranslations().get(0).getSourceLanguage(), Locale.FRENCH);
        Assert.assertEquals(languageTranslation.getTranslations().get(0).getDestinationLanguage(), Locale.ENGLISH);
        Assert.assertEquals(languageTranslation.getTranslations().get(0).getEngine(), "SYSTRANS");
    }

    @Test
    public void unmarshalLanguageQuery() throws XMLStreamException, JAXBException {
        String xml = "<iq type='result' to='bard@shakespeare.lit/globe' from=' translation.shakespeare.lit'>\n" +
                "  <query xmlns='urn:xmpp:langtrans:items'>\n" +
                "    <item source_lang='en' jid='translation.shakespeare.lit' destination_lang='fr' \n" +
                "          engine='SYSTRANS 2005 Release 2' pivotable='true' dictionary='medical'/>\n" +
                "    <item source_lang='en' jid='translation.shakespeare.lit' destination_lang='ko' \n" +
                "          engine='SYSTRANS 2005 Release 2' pivotable='true'/>\n" +
                "    <item source_lang='en' jid='translation.shakespeare.lit' destination_lang='ru' \n" +
                "          engine='SYSTRANS 2005 Release 2' pivotable='true'/>\n" +
                "    <item source_lang='en' jid='translation.shakespeare.lit' destination_lang='ru' \n" +
                "          engine='SYSTRANS 2005 Release 2' pivotable='true' dictionary='medical'/>\n" +
                "    <item source_lang='fr' jid='translation.shakespeare.lit' destination_lang='en' \n" +
                "          engine='SYSTRANS 2005 Release 2' pivotable='true' dictionary='standard'/>\n" +
                "    <item source_lang='ru' jid='translation.shakespeare.lit' destination_lang='en' \n" +
                "          engine='SYSTRANS 2005 Release 2' pivotable='true' dictionary='Medical 1.0'/>\n" +
                "    <item source_lang='ko' jid='translation.shakespeare.lit' destination_lang='en' \n" +
                "          engine='SYSTRANS 2005 Release 2' pivotable='true'/>\n" +
                "  </query>\n" +
                "</iq>\n";
        IQ iq = unmarshal(xml, IQ.class);
        LanguageSupport languageSupport = iq.getExtension(LanguageSupport.class);
        Assert.assertNotNull(languageSupport);
        Assert.assertEquals(languageSupport.getItems().size(), 7);
        LanguageSupport.Item item = languageSupport.getItems().get(0);
        Assert.assertEquals(item.getSourceLanguage(), Locale.ENGLISH);
        Assert.assertEquals(item.getJid(), Jid.of("translation.shakespeare.lit"));
        Assert.assertEquals(item.getDestinationLanguage(), Locale.FRENCH);
        Assert.assertEquals(item.getEngine(), "SYSTRANS 2005 Release 2");
        Assert.assertTrue(item.isPivotable());
        Assert.assertEquals(item.getDictionary(), "medical");
    }

    @Test
    public void unmarshalLanguageResponse() throws XMLStreamException, JAXBException {
        String xml = "<iq type='result' id='translationReq_4' from='translation.shakespeare.lit' to='bard@shakespeare.lt/globe'>\n" +
                "  <x xmlns='urn:xmpp:langtrans'>\n" +
                "    <source xml:lang='en'>How are you?</source>\n" +
                "    <translation destination_lang='it' source_lang='en' engine='default' dictionary='medical'>Come siete?</translation>\n" +
                "    <translation destination_lang='de' source_lang='en' engine='default'>Wie geht es Ihnen?</translation>\n" +
                "  </x>\n" +
                "</iq>";
        IQ iq = unmarshal(xml, IQ.class);
        LanguageTranslation languageTranslation = iq.getExtension(LanguageTranslation.class);
        Assert.assertNotNull(languageTranslation);
        Assert.assertEquals(languageTranslation.getSourceText(), "How are you?");
        Assert.assertEquals(languageTranslation.getSourceLanguage(), Locale.ENGLISH);
        Assert.assertEquals(languageTranslation.getTranslations().size(), 2);
        Assert.assertEquals(languageTranslation.getTranslations().get(0).getSourceLanguage(), Locale.ENGLISH);
        Assert.assertEquals(languageTranslation.getTranslations().get(0).getDestinationLanguage(), Locale.ITALIAN);
        Assert.assertEquals(languageTranslation.getTranslations().get(0).getEngine(), "default");
        Assert.assertEquals(languageTranslation.getTranslations().get(0).getTranslatedText(), "Come siete?");
        Assert.assertEquals(languageTranslation.getTranslations().get(0).getDictionary(), "medical");
    }
}
