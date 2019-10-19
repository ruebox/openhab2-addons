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

package rocks.xmpp.extensions.commands;


import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.commands.model.Command;
import rocks.xmpp.extensions.data.model.DataForm;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Christian Schudt
 */
public class CommandsTest extends XmlTest {
    protected CommandsTest() throws JAXBException {
        super(ClientIQ.class, Command.class, DataForm.class);
    }

    @Test
    public void unmarshalCommands() throws XMLStreamException, JAXBException {
        String xml = "<iq type='result' from='responder@domain' to='requester@domain' id='exec1'>\n" +
                "  <command xmlns='http://jabber.org/protocol/commands'\n" +
                "           sessionid='list:20020923T213616Z-700'\n" +
                "           node='list'\n" +
                "           status='completed'>\n" +
                "    <x xmlns='jabber:x:data' type='result'>\n" +
                "      <title>Available Services</title>\n" +
                "      <reported>\n" +
                "        <field var='service' label='Service'/>\n" +
                "        <field var='runlevel-1' label='Single-User mode'/>\n" +
                "        <field var='runlevel-2' label='Non-Networked Multi-User mode'/>\n" +
                "        <field var='runlevel-3' label='Full Multi-User mode'/>\n" +
                "        <field var='runlevel-5' label='X-Window mode'/>\n" +
                "      </reported>\n" +
                "      <item>\n" +
                "        <field var='service'><value>httpd</value></field>\n" +
                "        <field var='runlevel-1'><value>off</value></field>\n" +
                "        <field var='runlevel-2'><value>off</value></field>\n" +
                "        <field var='runlevel-3'><value>on</value></field>\n" +
                "        <field var='runlevel-5'><value>on</value></field>\n" +
                "      </item>\n" +
                "      <item>\n" +
                "        <field var='service'><value>postgresql</value></field>\n" +
                "        <field var='runlevel-1'><value>off</value></field>\n" +
                "        <field var='runlevel-2'><value>off</value></field>\n" +
                "        <field var='runlevel-3'><value>on</value></field>\n" +
                "        <field var='runlevel-5'><value>on</value></field>\n" +
                "      </item>\n" +
                "      <item>\n" +
                "        <field var='service'><value>jabberd</value></field>\n" +
                "        <field var='runlevel-1'><value>off</value></field>\n" +
                "        <field var='runlevel-2'><value>off</value></field>\n" +
                "        <field var='runlevel-3'><value>on</value></field>\n" +
                "        <field var='runlevel-5'><value>on</value></field>\n" +
                "      </item>\n" +
                "    </x>\n" +
                "  </command>\n" +
                "</iq>\n";

        IQ iq = unmarshal(xml, IQ.class);
        Command command = iq.getExtension(Command.class);
        Assert.assertNotNull(command);
        Assert.assertEquals(command.getSessionId(), "list:20020923T213616Z-700");
        Assert.assertEquals(command.getNode(), "list");
        Assert.assertEquals(command.getStatus(), Command.Status.COMPLETED);
        Assert.assertEquals(command.getActions(), Collections.emptyList());
        Assert.assertEquals(command.getPayloads().size(), 1);
        Assert.assertTrue(command.getPayloads().get(0) instanceof DataForm);
    }

    @Test
    public void unmarshalActions() throws XMLStreamException, JAXBException {
        String xml = "<iq type='result' from='responder@domain' to='requester@domain' id='exec1'>\n" +
                "  <command xmlns='http://jabber.org/protocol/commands'\n" +
                "           sessionid='config:20020923T213616Z-700'\n" +
                "           node='config'\n" +
                "           status='executing'>\n" +
                "    <actions execute='next'>\n" +
                "      <next/>\n" +
                "    </actions>\n" +
                "    <x xmlns='jabber:x:data' type='form'>\n" +
                "      <title>Configure Service</title>\n" +
                "      <instructions>\n" +
                "        Please select the service to configure.\n" +
                "      </instructions>\n" +
                "      <field var='service' label='Service' type='list-single'>\n" +
                "        <option><value>httpd</value></option>\n" +
                "        <option><value>jabberd</value></option>\n" +
                "        <option><value>postgresql</value></option>\n" +
                "      </field>\n" +
                "    </x>\n" +
                "  </command>\n" +
                "</iq>\n";

        IQ iq = unmarshal(xml, IQ.class);
        Command command = iq.getExtension(Command.class);
        Assert.assertNotNull(command);
        Assert.assertEquals(command.getStatus(), Command.Status.EXECUTING);
        Assert.assertEquals(command.getActions(), Arrays.asList(Command.Action.CANCEL, Command.Action.EXECUTE, Command.Action.NEXT));
    }

    @Test
    public void unmarshalBadAction() throws XMLStreamException, JAXBException {
        String xml = "<iq type='error' from='responder@domain' to='requester@domain/resource' id='exec1'>\n" +
                "  <command xmlns='http://jabber.org/protocol/commands'\n" +
                "           node='list'\n" +
                "           action='execute'\n" +
                "           xml:lang='fr-ca'/>\n" +
                "  <error type='modify' code='400'>\n" +
                "    <bad-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <bad-action xmlns='http://jabber.org/protocol/commands'/>\n" +
                "  </error>\n" +
                "</iq>\n";

        IQ iq = unmarshal(xml, IQ.class);
        Command command = iq.getExtension(Command.class);
        Assert.assertNotNull(command);
        Assert.assertTrue(iq.getError().getExtension() instanceof Command.BadAction);
    }

    @Test
    public void unmarshalError() throws XMLStreamException, JAXBException {
        String xml = "<iq type='error' from='responder@domain' to='requester@domain/resource' id='exec1'>\n" +
                "  <command xmlns='http://jabber.org/protocol/commands'\n" +
                "           node='list'\n" +
                "           action='execute'\n" +
                "           xml:lang='fr-ca'/>\n" +
                "  <error type='modify' code='400'>\n" +
                "    <bad-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <bad-locale xmlns='http://jabber.org/protocol/commands'/>\n" +
                "  </error>\n" +
                "</iq>\n";

        IQ iq = unmarshal(xml, IQ.class);
        Command command = iq.getExtension(Command.class);
        Assert.assertNotNull(command);
        Assert.assertTrue(iq.getError().getExtension() instanceof Command.BadLocale);
    }

    @Test
    public void unmarshalBadPayload() throws XMLStreamException, JAXBException {
        String xml = "<iq type='error' from='responder@domain' to='requester@domain/resource' id='exec1'>\n" +
                "  <command xmlns='http://jabber.org/protocol/commands'\n" +
                "           node='list'\n" +
                "           action='execute'\n" +
                "           xml:lang='fr-ca'/>\n" +
                "  <error type='modify' code='400'>\n" +
                "    <bad-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <bad-payload xmlns='http://jabber.org/protocol/commands'/>\n" +
                "  </error>\n" +
                "</iq>\n";

        IQ iq = unmarshal(xml, IQ.class);
        Command command = iq.getExtension(Command.class);
        Assert.assertNotNull(command);
        Assert.assertTrue(iq.getError().getExtension() instanceof Command.BadPayload);
    }

    @Test
    public void unmarshalBadSessionId() throws XMLStreamException, JAXBException {
        String xml = "<iq type='error' from='responder@domain' to='requester@domain/resource' id='exec1'>\n" +
                "  <command xmlns='http://jabber.org/protocol/commands'\n" +
                "           node='list'\n" +
                "           action='execute'\n" +
                "           xml:lang='fr-ca'/>\n" +
                "  <error type='modify' code='400'>\n" +
                "    <bad-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <bad-sessionid xmlns='http://jabber.org/protocol/commands'/>\n" +
                "  </error>\n" +
                "</iq>\n";

        IQ iq = unmarshal(xml, IQ.class);
        Command command = iq.getExtension(Command.class);
        Assert.assertNotNull(command);
        Assert.assertTrue(iq.getError().getExtension() instanceof Command.BadSessionId);
    }

    @Test
    public void unmarshalMalformedAction() throws XMLStreamException, JAXBException {
        String xml = "<iq type='error' from='responder@domain' to='requester@domain/resource' id='exec1'>\n" +
                "  <command xmlns='http://jabber.org/protocol/commands'\n" +
                "           node='list'\n" +
                "           action='execute'\n" +
                "           xml:lang='fr-ca'/>\n" +
                "  <error type='modify' code='400'>\n" +
                "    <bad-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <malformed-action xmlns='http://jabber.org/protocol/commands'/>\n" +
                "  </error>\n" +
                "</iq>\n";

        IQ iq = unmarshal(xml, IQ.class);
        Command command = iq.getExtension(Command.class);
        Assert.assertNotNull(command);
        Assert.assertTrue(iq.getError().getExtension() instanceof Command.MalformedAction);
    }

    @Test
    public void unmarshalSessionExpired() throws XMLStreamException, JAXBException {
        String xml = "<iq type='error' from='responder@domain' to='requester@domain/resource' id='exec1'>\n" +
                "  <command xmlns='http://jabber.org/protocol/commands'\n" +
                "           node='list'\n" +
                "           action='execute'\n" +
                "           xml:lang='fr-ca'/>\n" +
                "  <error type='modify' code='400'>\n" +
                "    <bad-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>\n" +
                "    <session-expired xmlns='http://jabber.org/protocol/commands'/>\n" +
                "  </error>\n" +
                "</iq>\n";

        IQ iq = unmarshal(xml, IQ.class);
        Command command = iq.getExtension(Command.class);
        Assert.assertNotNull(command);
        Assert.assertTrue(iq.getError().getExtension() instanceof Command.SessionExpired);
    }

    @Test
    public void marshalCommand() throws XMLStreamException, JAXBException {
        Command command = new Command("node", "sid", Command.Status.CANCELED, Collections.singleton(Command.Action.PREV), Command.Action.NEXT, Collections.emptyList());
        String xml = marshal(command);
        Assert.assertEquals(xml, "<command xmlns=\"http://jabber.org/protocol/commands\" node=\"node\" sessionid=\"sid\" status=\"canceled\"><actions execute=\"next\"><prev></prev></actions></command>");
    }
}
