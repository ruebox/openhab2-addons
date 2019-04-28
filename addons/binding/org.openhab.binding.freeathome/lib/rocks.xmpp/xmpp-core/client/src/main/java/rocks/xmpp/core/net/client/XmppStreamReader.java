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

package rocks.xmpp.core.net.client;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.session.debug.XmppDebugger;
import rocks.xmpp.core.session.model.SessionOpen;
import rocks.xmpp.core.stream.model.StreamError;
import rocks.xmpp.core.stream.model.StreamErrorException;
import rocks.xmpp.core.stream.model.StreamHeader;
import rocks.xmpp.core.stream.model.errors.Condition;
import rocks.xmpp.util.XmppUtils;

import javax.xml.XMLConstants;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import rocks.xmpp.util.concurrent.QueuedExecutorService;

/**
 * This class is responsible for reading the inbound XMPP stream. It starts one "reader thread", which keeps reading the XMPP document from the stream until the stream is closed or disconnected.
 * <p>
 * This class is thread-safe.
 *
 * @author Christian Schudt
 */
final class XmppStreamReader {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(XmppUtils.createNamedThreadFactory("XMPP Reader Thread"));

    private static final QName STREAM_ID = new QName("id");

    private static final QName FROM = new QName("from");

    private static final QName TO = new QName("to");

    private static final QName VERSION = new QName("version");

    private static final QName LANG = new QName(XMLConstants.XML_NS_URI, "lang");

    private final SocketConnection connection;

    private final XmppSession xmppSession;

    private final ExecutorService executorService;

    private final XmppDebugger debugger;

    private final Marshaller marshaller;

    private final Unmarshaller unmarshaller;

    private final String namespace;

    XmppStreamReader(String namespace, final SocketConnection connection, XmppSession xmppSession) {
        this.connection = connection;
        this.xmppSession = xmppSession;
        this.debugger = xmppSession.getDebugger();
        this.marshaller = xmppSession.createMarshaller();
        this.unmarshaller = xmppSession.createUnmarshaller();
        this.executorService = new QueuedExecutorService(EXECUTOR_SERVICE);
        this.namespace = namespace;
    }

    void startReading(final InputStream inputStream, final Consumer<SessionOpen> openedByPeer, final Runnable closedByPeer) {

        executorService.execute(() -> {
            boolean doRestart = false;
            XMLEventReader xmlEventReader = null;
            StreamErrorException streamError = null;
            try {

                InputStream xmppInputStream = null;
                if (debugger != null) {
                    xmppInputStream = debugger.createInputStream(inputStream);
                }
                if (xmppInputStream == null) {
                    xmppInputStream = inputStream;
                }
                XMLEvent startDocument = null;
                xmlEventReader = xmppSession.getConfiguration().getXmlInputFactory().createXMLEventReader(xmppInputStream, "UTF-8");
                XMLEvent xmlEvent;

                while (!doRestart && (xmlEvent = xmlEventReader.peek()) != null) {
                    StringWriter stringWriter = null;
                    if (debugger != null) {
                        stringWriter = new StringWriter();
                        if (xmlEvent.isStartDocument()) {
                            startDocument = xmlEvent;
                        }
                    }

                    if (xmlEvent.isStartElement()) {
                        StartElement startElement = xmlEvent.asStartElement();
                        if (StreamHeader.LOCAL_NAME.equals(startElement.getName().getLocalPart()) && StreamHeader.STREAM_NAMESPACE.equals(startElement.getName().getNamespaceURI())) {
                            Attribute idAttribute = startElement.getAttributeByName(STREAM_ID);
                            final Attribute fromAttribute = startElement.getAttributeByName(FROM);
                            final Attribute toAttribute = startElement.getAttributeByName(TO);
                            final Attribute versionAttribute = startElement.getAttributeByName(VERSION);
                            final Attribute langAttribute = startElement.getAttributeByName(LANG);
                            final Jid from = fromAttribute != null ? Jid.ofEscaped(fromAttribute.getValue()) : null;
                            final Jid to = toAttribute != null ? Jid.ofEscaped(toAttribute.getValue()) : null;
                            final String id = idAttribute != null ? idAttribute.getValue() : null;
                            final String version = versionAttribute != null ? versionAttribute.getValue() : null;
                            final Locale lang = langAttribute != null ? Locale.forLanguageTag(langAttribute.getValue()) : null;
                            StreamHeader streamHeader = StreamHeader.create(from, to, id, version, lang, namespace);
                            openedByPeer.accept(streamHeader);
                            if (debugger != null) {
                                XMLEventWriter writer = xmppSession.getConfiguration().getXmlOutputFactory().createXMLEventWriter(stringWriter);
                                writer.add(startDocument);
                                writer.add(xmlEvent);
                                writer.flush();
                                debugger.readStanza(stringWriter.append('>').toString(), null);
                                // Close the writer after the string has been created, otherwise some XMLEventWriters write the closing tag (</stream:stream>)
                                writer.close();
                            }
                            xmppSession.handleElement(streamHeader);
                            xmlEventReader.nextEvent();
                        } else {
                            Object object = unmarshaller.unmarshal(xmlEventReader);
                            if (debugger != null) {
                                // Marshal the inbound stanza. The byteArrayOutputStream cannot be used for that, even if we reset() it, because it could already contain the next stanza.
                                XMLStreamWriter xmlStreamWriter = XmppUtils.createXmppStreamWriter(xmppSession.getConfiguration().getXmlOutputFactory().createXMLStreamWriter(stringWriter));
                                xmlStreamWriter.setDefaultNamespace(namespace);
                                marshaller.marshal(object, xmlStreamWriter);
                                xmlStreamWriter.flush();
                                debugger.readStanza(stringWriter.toString(), object);
                            }
                            // Keep the reader open and only delegate the exception to the session and let it decide what to do with it.
                            try {
                                doRestart = xmppSession.handleElement(object);
                            } catch (StreamErrorException e) {
                                streamError = e;
                            } catch (XmppException e) {
                                xmppSession.notifyException(e);
                            }
                        }
                    } else {
                        xmlEventReader.nextEvent();
                    }
                    if (xmlEvent.isEndElement()) {
                        // The stream gets closed with </stream:stream>
                        closedByPeer.run();
                        if (debugger != null) {
                            QName qName = xmlEvent.asEndElement().getName();
                            debugger.readStanza("</" + qName.getPrefix() + ':' + qName.getLocalPart() + '>', null);
                        }
                    }
                }
                xmlEventReader.close();
                if (streamError != null) {
                    throw streamError;
                }
                if (!doRestart && xmppSession.getStatus() != XmppSession.Status.CLOSING) {
                    // The server initiated a graceful disconnect by sending <stream:stream/> without an stream error.
                    // In this case we want to reconnect, therefore throw an exception as if a stream error has occurred.
                    throw new StreamErrorException(new StreamError(Condition.UNDEFINED_CONDITION, "Stream closed by server", Locale.ENGLISH, null));
                }
            } catch (Exception e) {
                // shutdown the service, but don't await termination, in order to not block the reader thread.
                executorService.shutdown();

                if (streamError != null) {
                    // Recheck if there was a stream error. In this case don't report the original exception, but the stream error.
                    // This may happen, if the server doesn't gracefully close the stream after sending a stream error.
                    xmppSession.notifyException(streamError);
                } else {
                    xmppSession.notifyException(e);
                }
            } finally {
                if (xmlEventReader != null) {
                    try {
                        xmlEventReader.close();
                    } catch (XMLStreamException e) {
                        xmppSession.notifyException(e);
                    }
                }
                if (doRestart) {
                    connection.restartStream();
                }
            }
        });
    }

    /**
     * Shuts down the executor and waits maximal 0.5 seconds for the reader thread to finish, i.e. when the server sends a {@code </stream:stream>} response.
     */
    void shutdown() {
        executorService.shutdown();
        // Wait for the closing </stream> element to be received.
        try {
            if (!executorService.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
