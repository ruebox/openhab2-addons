/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Christian Schudt
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

package rocks.xmpp.extensions.jingle.apps.filetransfer;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.hashes.model.Hash;
import rocks.xmpp.extensions.jingle.apps.filetransfer.model.JingleFileTransfer;
import rocks.xmpp.extensions.jingle.apps.filetransfer.model.errors.FileTransferError;
import rocks.xmpp.extensions.jingle.model.Jingle;
import rocks.xmpp.extensions.jingle.thumbs.model.Thumbnail;
import rocks.xmpp.extensions.jingle.transports.s5b.model.S5bTransportMethod;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class JingleFileTransferTest extends XmlTest {
    protected JingleFileTransferTest() throws JAXBException {
        super(Hash.class, Jingle.class, JingleFileTransfer.class, S5bTransportMethod.class, ClientIQ.class);
    }

    @Test
    public void unmarshalJingleFileTransfer() throws XMLStreamException, JAXBException {
        String xml = "<description xmlns='urn:xmpp:jingle:apps:file-transfer:4'>\n" +
                "        <file>\n" +
                "          <date>1969-07-21T02:56:15Z</date>\n" +
                "          <desc>This is a test. If this were a real file...</desc>\n" +
                "          <media-type>text/plain</media-type>\n" +
                "          <name>test.txt</name>\n" +
                "          <range/>\n" +
                "          <size>1022</size>\n" +
                "          <hash xmlns='urn:xmpp:hashes:2' algo='sha-1'>552da749930852c69ae5d2141d3766b1</hash>\n" +
                "        </file>\n" +
                "      </description>\n";

        JingleFileTransfer fileTransfer = unmarshal(xml, JingleFileTransfer.class);
        Assert.assertNotNull(fileTransfer.getFile());
        Assert.assertEquals(fileTransfer.getFile().getDescription(), "This is a test. If this were a real file...");
        Assert.assertEquals(fileTransfer.getFile().getMediaType(), "text/plain");
        Assert.assertEquals(fileTransfer.getFile().getName(), "test.txt");
        Assert.assertEquals(fileTransfer.getFile().getSize(), 1022);
    }

    @Test
    public void unmarshalJingleFileTransferChecksum() throws XMLStreamException, JAXBException {
        String xml = "<jingle xmlns='urn:xmpp:jingle:1'\n" +
                "          action='session-info'\n" +
                "          initiator='romeo@montague.lit/orchard'\n" +
                "          sid='a73sjjvkla37jfea'>\n" +
                "    <checksum xmlns='urn:xmpp:jingle:apps:file-transfer:4' \n" +
                "              creator='initiator' \n" +
                "              name='a-file-offer'>\n" +
                "      <file>\n" +
                "        <hash xmlns='urn:xmpp:hashes:2' \n" +
                "              algo='sha-1'>552da749930852c69ae5d2141d3766b1</hash>\n" +
                "      </file>\n" +
                "    </checksum>\n\n" +
                "  </jingle>\n";

        Jingle jingle = unmarshal(xml, Jingle.class);
        Assert.assertTrue(jingle.getPayload() instanceof JingleFileTransfer.Checksum);
        Assert.assertEquals(((JingleFileTransfer.Checksum) jingle.getPayload()).getCreator(), Jingle.Content.Creator.INITIATOR);
        Assert.assertEquals(((JingleFileTransfer.Checksum) jingle.getPayload()).getName(), "a-file-offer");
        Assert.assertEquals(((JingleFileTransfer.Checksum) jingle.getPayload()).getFile().getHashes().size(), 1);
    }

    @Test
    public void unmarshalJingleFileTransferReceived() throws XMLStreamException, JAXBException {
        String xml = "<jingle xmlns='urn:xmpp:jingle:1'\n" +
                "          action='session-info'\n" +
                "          initiator='romeo@montague.example/dr4hcr0st3lup4c'\n" +
                "          sid='a73sjjvkla37jfea'>\n" +
                "    <received xmlns='urn:xmpp:jingle:apps:file-transfer:4' \n" +
                "              creator='responder' \n" +
                "              name='a-file-offer' />\n" +
                "  </jingle>\n";

        Jingle jingle = unmarshal(xml, Jingle.class);
        Assert.assertTrue(jingle.getPayload() instanceof JingleFileTransfer.Received);
        Assert.assertEquals(((JingleFileTransfer.Received) jingle.getPayload()).getCreator(), Jingle.Content.Creator.RESPONDER);
        Assert.assertEquals(((JingleFileTransfer.Received) jingle.getPayload()).getName(), "a-file-offer");
    }

    @Test
    public void unmarshalFileNotAvailable() throws XMLStreamException, JAXBException {
        String xml = "<iq from='romeo@montague.example/dr4hcr0st3lup4c'\n" +
                "    id='wsn361c9'\n" +
                "    to='juliet@capulet.example/yn0cl4bnw0yr3vym'\n" +
                "    type='set'>\n" +
                "  <jingle xmlns='urn:xmpp:jingle:1'\n" +
                "          action='content-reject'\n" +
                "          sid='uj3b2'>\n" +
                "    <content creator='initiator' name='requesting-file' senders='initiator'/>\n" +
                "    <reason>\n" +
                "      <failed-application />\n" +
                "      <file-not-available xmlns='urn:xmpp:jingle:apps:file-transfer:errors:0' />\n" +
                "    </reason>\n" +
                "  </jingle>\n" +
                "</iq>";

        IQ iq = unmarshal(xml, IQ.class);
        Jingle jingle = iq.getExtension(Jingle.class);
        Assert.assertEquals(jingle.getReason().getExtension(), FileTransferError.FILE_NOT_AVAILABLE);
    }

    @Test
    public void unmarshalFileTooLarge() throws XMLStreamException, JAXBException {
        String xml = "<iq from='romeo@montague.example/dr4hcr0st3lup4c'\n" +
                "    id='wsn361c9'\n" +
                "    to='juliet@capulet.example/yn0cl4bnw0yr3vym'\n" +
                "    type='set'>\n" +
                "  <jingle xmlns='urn:xmpp:jingle:1'\n" +
                "          action='content-remove'\n" +
                "          sid='uj3b2'>\n" +
                "    <content creator='initiator' name='big-file' senders='initiator'/>\n" +
                "    <reason>\n" +
                "      <media-error />\n" +
                "      <file-too-large xmlns='urn:xmpp:jingle:apps:file-transfer:errors:0' />\n" +
                "    </reason>\n" +
                "  </jingle>\n" +
                "</iq>";

        IQ iq = unmarshal(xml, IQ.class);
        Jingle jingle = iq.getExtension(Jingle.class);
        Assert.assertEquals(jingle.getReason().getExtension(), FileTransferError.FILE_TOO_LARGE);
    }

    @Test
    public void unmarshalThumbnail() throws XMLStreamException, JAXBException {
        String xml = "<description xmlns='urn:xmpp:jingle:apps:file-transfer:4' senders='initiator'>\n" +
                "        <file>\n" +
                "          <media-type>image/jpeg</media-type>\n" +
                "          <name>image.jpg</name>\n" +
                "          <size>3032449</size>\n" +
                "          <hash xmlns='urn:xmpp:hashes:2' algo='sha-1'>552da749930852c69ae5d2141d3766b1</hash>\n" +
                "          <desc>This is a test. If this were a real file...</desc>\n" +
                "          <thumbnail xmlns='urn:xmpp:thumbs:1'\n" +
                "                     uri='cid:sha1+ffd7c8d28e9c5e82afea41f97108c6b4@bob.xmpp.org'\n" +
                "                     media-type='image/png'\n" +
                "                     width='128'\n" +
                "                     height='96'/>\n" +
                "        </file>\n" +
                "      </description>";

        JingleFileTransfer jingleFileTransfer = unmarshal(xml, JingleFileTransfer.class);
        Thumbnail thumbnail = jingleFileTransfer.getFile().getThumbnail();
        Assert.assertNotNull(thumbnail);
    }
}
