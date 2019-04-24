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

package rocks.xmpp.nio.codec;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.sasl.model.Auth;
import rocks.xmpp.core.sasl.model.Response;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.core.stanza.model.client.ClientMessage;
import rocks.xmpp.core.stream.model.StreamElement;
import rocks.xmpp.core.stream.model.StreamErrorException;
import rocks.xmpp.core.stream.model.StreamHeader;
import rocks.xmpp.core.stream.model.errors.Condition;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

/**
 * @author Christian Schudt
 */
public class XmppStreamDecoderTest {

    private static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(Auth.class, Response.class, ClientIQ.class, ClientMessage.class);
        } catch (JAXBException e) {
            throw new DataBindingException(e);
        }
    }

    private static final ThreadLocal<Unmarshaller> UNMARSHALLER = ThreadLocal.withInitial(() -> {
        try {
            return JAXB_CONTEXT.createUnmarshaller();
        } catch (JAXBException e) {
            throw new DataBindingException(e);
        }
    });

    @Test
    public void testValidXml() throws Exception {

        XmppStreamDecoder decoder = new XmppStreamDecoder(UNMARSHALLER::get);

        ByteBuffer buf1 = ByteBuffer.wrap("<?xml version='1.0' encoding='UTF-8'?><stream:stream to=\"localhost\" version=\"1.0\" xml:lang=\"de-DE\" xml".getBytes(StandardCharsets.UTF_8));
        ByteBuffer buf2 = ByteBuffer.wrap("ns=\"jabber:client\" xmlns:stream=\"http://etherx.jabber.org/stream".getBytes(StandardCharsets.UTF_8));
        ByteBuffer buf3 = ByteBuffer.wrap("s\" xmlns:foo=\"bar\">  <auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"\ud83d\ude0dSCRA\ud83d\ude0d".getBytes(StandardCharsets.UTF_8));
        ByteBuffer buf4 = ByteBuffer.wrap("M-SHA-1\">biwsbj1hZG1pbixyPTUyZm1vUjMybmFhOUlGd0dtYWloWVE9PQ==</auth>  <response".getBytes(StandardCharsets.UTF_8));
        ByteBuffer buf5 = ByteBuffer.wrap("   xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">biwsbj1hZG1pbixyPWRBMEUyTjhmQ0QycUVxNjZ5V0VHemc9PQ==</response> <".getBytes(StandardCharsets.UTF_8));
        ByteBuffer buf6 = ByteBuffer.wrap("iq></iq> ".getBytes(StandardCharsets.UTF_8));

        List<Object> out = new ArrayList<>();
        BiConsumer<String, StreamElement> consumer = (s, streamElement) -> out.add(streamElement);
        decoder.decode(buf1, consumer);
        Assert.assertEquals(out.size(), 0);

        out.clear();
        decoder.decode(buf2, consumer);
        Assert.assertEquals(out.size(), 0);

        out.clear();
        decoder.decode(buf3, consumer);
        Assert.assertEquals(out.size(), 1);
        Assert.assertTrue(out.get(0) instanceof StreamHeader);
        Assert.assertEquals(((StreamHeader) out.get(0)).getContentNamespace(), "jabber:client");
        Assert.assertEquals(((StreamHeader) out.get(0)).getAdditionalNamespaces().size(), 1);
        Assert.assertEquals(((StreamHeader) out.get(0)).getAdditionalNamespaces().get(0), new QName("bar", "", "foo"));

        out.clear();
        decoder.decode(buf4, consumer);
        Assert.assertEquals(out.size(), 1);

        out.clear();
        decoder.decode(buf5, consumer);
        Assert.assertEquals(out.size(), 1);

        out.clear();
        decoder.decode(buf6, consumer);

    }

    @Test(enabled = false)
    public void testPerformance() throws StreamErrorException {
        XmppStreamDecoder decoder = new XmppStreamDecoder(UNMARSHALLER::get);

        ByteBuffer buf1 = ByteBuffer.wrap("<?xml version='1.0' encoding='UTF-8'?><stream:stream to=\"localhost\" version=\"1.0\" xml:lang=\"de-DE\" xml".getBytes(StandardCharsets.UTF_8));
        ByteBuffer buf2 = ByteBuffer.wrap("ns=\"jabber:client\" xmlns:stream=\"http://etherx.jabber.org/stream".getBytes(StandardCharsets.UTF_8));
        ByteBuffer buf3 = ByteBuffer.wrap("s\" xmlns:foo=\"bar\">  <auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"SCRA".getBytes(StandardCharsets.UTF_8));
        ByteBuffer buf4 = ByteBuffer.wrap("M-SHA-1\">biwsbj1hZG1pbixyPTUyZm1vUjMybmFhOUlGd0dtYWloWVE9PQ==</auth>  <response".getBytes(StandardCharsets.UTF_8));
        ByteBuffer buf5 = ByteBuffer.wrap("   xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">biwsbj1hZG1pbixyPWRBMEUyTjhmQ0QycUVxNjZ5V0VHemc9PQ==</response> <".getBytes(StandardCharsets.UTF_8));
        ByteBuffer buf6 = ByteBuffer.wrap("iq></iq> ".getBytes(StandardCharsets.UTF_8));

        List<Object> out = new ArrayList<>();
        BiConsumer<String, StreamElement> consumer = (s, streamElement) -> out.add(streamElement);
        decoder.decode(buf1, consumer);
        decoder.decode(buf2, consumer);
        decoder.decode(buf3, consumer);
        decoder.decode(buf4, consumer);
        decoder.decode(buf5, consumer);
        decoder.decode(buf6, consumer);

        byte[] bytes = new byte[1024];
        Random random = new Random();
        random.nextBytes(bytes);
        String randomString = DatatypeConverter.printBase64Binary(bytes);
        long now = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            out.clear();
            ByteBuffer byteBuffer = ByteBuffer.wrap(("<message>" + randomString + "</message>").getBytes(StandardCharsets.UTF_8));
            decoder.decode(byteBuffer, consumer);
        }
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test(expectedExceptions = StreamErrorException.class)
    public void testNotWellFormed() throws Exception {
        XmppStreamDecoder decoder = new XmppStreamDecoder(UNMARSHALLER::get);

        ByteBuffer buf1 = ByteBuffer.wrap("<?xml version='1.0' encoding='UTF-8'?><stream:stream to=\"localhost version=\"1.0\" xml:lang=\"de-DE\" xml".getBytes(StandardCharsets.UTF_8));

        BiConsumer<String, StreamElement> consumer = (s, streamElement) -> {
        };
        try {
            decoder.decode(buf1, consumer);
        } catch (StreamErrorException e) {
            Assert.assertTrue(e.getError().getCondition() == Condition.NOT_WELL_FORMED);
            throw e;
        }
    }
}
