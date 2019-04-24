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

package rocks.xmpp.core.stanza.model;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.Text;
import rocks.xmpp.core.stanza.model.errors.Condition;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The implementation of the {@code <message/>} element.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/rfcs/rfc6121.html#message">5.  Exchanging Messages</a></cite></p>
 * <p>Once a client has authenticated with a server and bound a resource to an XML stream as described in [XMPP-CORE], an XMPP server will route XML stanzas to and from that client. One kind of stanza that can be exchanged is {@code <message/>} (if, that is, messaging functionality is enabled on the server). Exchanging messages is a basic use of XMPP and occurs when a user generates a message stanza that is addressed to another entity. As defined under Section 8, the sender's server is responsible for delivering the message to the intended recipient (if the recipient is on the same local server) or for routing the message to the recipient's server (if the recipient is on a remote server). Thus a message stanza is used to "push" information to another entity.</p>
 * </blockquote>
 * This class is thread-safe.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/rfcs/rfc6121.html#message-syntax">5.2.  Message Syntax</a>
 */
@XmlTransient
public class Message extends ExtensibleStanza {

    private final List<Text> subject = new CopyOnWriteArrayList<>();

    private final List<Text> body = new CopyOnWriteArrayList<>();

    @XmlAttribute
    private Type type;

    private ThreadElement thread;

    public Message() {
        this(null);
    }

    /**
     * Constructs an empty message.
     *
     * @param to The recipient.
     */
    public Message(Jid to) {
        this(to, null);
    }

    /**
     * Constructs a message with a type.
     *
     * @param to   The recipient.
     * @param type The message type.
     */
    public Message(Jid to, Type type) {
        this(to, type, null);
    }

    /**
     * Constructs a message with body and type.
     *
     * @param to   The recipient.
     * @param body The message body.
     * @param type The message type.
     */
    public Message(Jid to, Type type, String body) {
        this(to, type, body, null);
    }

    /**
     * Constructs a message with body and type.
     *
     * @param to      The recipient.
     * @param body    The message body.
     * @param type    The message type.
     * @param subject The subject.
     */
    public Message(Jid to, Type type, String body, String subject) {
        this(to, type, body, subject, null);
    }

    /**
     * Constructs a message with body and type.
     *
     * @param to      The recipient.
     * @param body    The message body.
     * @param type    The message type.
     * @param subject The subject.
     * @param thread  The thread.
     */
    public Message(Jid to, Type type, String body, String subject, String thread) {
        this(to, type, body != null ? Collections.singleton(new Text(body)) : Collections.emptyList(), subject != null ? Collections.singleton(new Text(subject)) : Collections.emptyList(), thread, null, null, null, null, null, null);
    }

    /**
     * Constructs a message with body and type.
     *
     * @param to           The recipient.
     * @param body         The message body.
     * @param type         The message type.
     * @param subject      The subject.
     * @param thread       The thread.
     * @param parentThread The parent thread.
     * @param from         The sender.
     * @param id           The id.
     * @param language     The language.
     * @param extensions   The extensions.
     * @param error        The error.
     */
    public Message(Jid to, Type type, String body, String subject, String thread, String parentThread, String id, Jid from, Locale language, Collection<?> extensions, StanzaError error) {
        this(to, type, body != null ? Collections.singleton(new Text(body)) : Collections.emptyList(), subject != null ? Collections.singleton(new Text(subject)) : Collections.emptyList(), thread, parentThread, id, from, language, extensions, error);
    }

    /**
     * Constructs a message with all possible values.
     *
     * @param to           The recipient.
     * @param bodies       The message bodies.
     * @param type         The message type.
     * @param subjects     The subjects.
     * @param thread       The thread.
     * @param parentThread The parent thread.
     * @param from         The sender.
     * @param id           The id.
     * @param language     The language.
     * @param extensions   The extensions.
     * @param error        The error.
     */
    public Message(Jid to, Type type, Collection<Text> bodies, Collection<Text> subjects, String thread, String parentThread, String id, Jid from, Locale language, Collection<?> extensions, StanzaError error) {
        super(to, from, id, language, extensions, error);
        this.type = type;
        if (bodies != null) {
            this.body.addAll(bodies);
        }
        if (subjects != null) {
            this.subject.addAll(subjects);
        }
        if (thread != null || parentThread != null) {
            this.thread = new ThreadElement(thread, parentThread);
        } else {
            this.thread = null;
        }
    }

    /**
     * Gets the bodies.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6121.html#message-syntax-body">5.2.3.  Body Element</a></cite></p>
     * <p>The {@code <body/>} element contains human-readable XML character data that specifies the textual contents of the message; this child element is normally included but is OPTIONAL.</p>
     * <p>There are no attributes defined for the {@code <body/>} element, with the exception of the 'xml:lang' attribute. Multiple instances of the {@code <body/>} element MAY be included in a message stanza for the purpose of providing alternate versions of the same body, but only if each instance possesses an 'xml:lang' attribute with a distinct language value.</p>
     * </blockquote>
     *
     * @return The list of bodies.
     * @see #getBody()
     */
    public final List<Text> getBodies() {
        return body;
    }

    /**
     * Gets the default body (which has no language attribute).
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6121.html#message-syntax-body">5.2.3.  Body Element</a></cite></p>
     * <p>The {@code <body/>} element contains human-readable XML character data that specifies the textual contents of the message; this child element is normally included but is OPTIONAL.</p>
     * </blockquote>
     * If there's no default body, but only bodies with a language attribute, the first body is returned.
     * <p>
     * If there are no bodies at all, null is returned.
     * </p>
     *
     * @return The body or null.
     * @see #getBodies()
     */
    public final String getBody() {
        for (Text body : this.body) {
            if (body.getLanguage() == null) {
                return body.getText();
            }
        }
        synchronized (this) {
            if (!body.isEmpty()) {
                return body.get(0).getText();
            }
        }
        return null;
    }

    /**
     * Sets the default body element.
     *
     * @param body The body text.
     * @see #getBody()
     */
    public final void setBody(String body) {
        if (body != null) {
            synchronized (this) {
                for (Text b : this.body) {
                    if (b.getLanguage() == null) {
                        this.body.remove(b);
                        break;
                    }
                }
                this.body.add(0, new Text(body));
            }
        } else {
            this.body.clear();
        }
    }

    /**
     * Gets the subjects.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6121.html#message-syntax-subject">5.2.4.  Subject Element</a></cite></p>
     * <p>The {@code <subject/>} element contains human-readable XML character data that specifies the topic of the message.</p>
     * <p>There are no attributes defined for the {@code <subject/>} element, with the exception of the 'xml:lang' attribute inherited from [XML]. Multiple instances of the {@code <subject/>} element MAY be included for the purpose of providing alternate versions of the same subject, but only if each instance possesses an 'xml:lang' attribute with a distinct language value.</p>
     * </blockquote>
     *
     * @return The list of subjects.
     * @see #getSubject()
     */
    public final List<Text> getSubjects() {
        return subject;
    }

    /**
     * Gets the default subject (which has no language attribute).
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6121.html#message-syntax-subject">5.2.4.  Subject Element</a></cite></p>
     * <p>The {@code <subject/>} element contains human-readable XML character data that specifies the topic of the message.</p>
     * </blockquote>
     * <p>If there's no default subject, but only subjects with a language attribute, the first subject is returned.</p>
     * <p>If there are no subjects at all, null is returned.</p>
     *
     * @return The subject or null.
     * @see #getSubjects()
     */
    public final String getSubject() {
        for (Text subject : this.subject) {
            if (subject.getLanguage() == null) {
                return subject.getText();
            }
        }
        synchronized (this) {
            if (!subject.isEmpty()) {
                return subject.get(0).getText();
            }
        }
        return null;
    }

    /**
     * Sets the default subject.
     *
     * @param subject The subject text.
     * @see #getSubject()
     */
    public final void setSubject(String subject) {
        if (subject != null) {
            synchronized (this) {
                for (Text s : this.subject) {
                    if (s.getLanguage() == null) {
                        this.subject.remove(s);
                        break;
                    }
                }
                this.subject.add(0, new Text(subject));
            }
        } else {
            this.subject.clear();
        }
    }

    /**
     * Gets the message type. This may also return null. If you want to check for a 'normal' message, you should prefer {@link #isNormal()}.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6121.html#message-syntax-type">5.2.2.  Type Attribute</a></cite></p>
     * <p>Common uses of the message stanza in instant messaging applications include: single messages; messages sent in the context of a one-to-one chat session; messages sent in the context of a multi-user chat room; alerts, notifications, or other information to which no reply is expected; and errors. These uses are differentiated via the 'type' attribute.</p>
     * </blockquote>
     *
     * @return The message type.
     * @see #isNormal()
     */
    public final synchronized Type getType() {
        return type;
    }

    /**
     * Sets the message type.
     *
     * @param type The message type.
     * @see #getType()
     */
    public final synchronized void setType(Type type) {
        this.type = type;
    }

    /**
     * Indicates, whether this message is a normal message, i.e. if the type or the message is either {@link Type#NORMAL}, null or otherwise unknown, this method returns true.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6121.html#message-syntax-type">5.2.2.  Type Attribute</a></cite></p>
     * <p>If an application receives a message with no 'type' attribute or the application does not understand the value of the 'type' attribute provided, it MUST consider the message to be of type "normal" (i.e., "normal" is the default).</p>
     * </blockquote>
     *
     * @return True, if this message's type is normal or unspecified.
     * @see #getType()
     */
    public final synchronized boolean isNormal() {
        return type == Type.NORMAL || type == null;
    }

    /**
     * Gets the thread.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6121.html#message-syntax-thread">5.2.5.  Thread Element</a></cite></p>
     * <p>The primary use of the XMPP {@code <thread/>} element is to uniquely identify a conversation thread or "chat session" between two entities instantiated by {@code <message/>} stanzas of type 'chat'. However, the XMPP {@code <thread/>} element MAY also be used to uniquely identify an analogous thread between two entities instantiated by {@code <message/>} stanzas of type 'headline' or 'normal', or among multiple entities in the context of a multi-user chat room instantiated by {@code <message/>} stanzas of type 'groupchat'. It MAY also be used for {@code <message/>} stanzas not related to a human conversation, such as a game session or an interaction between plugins. The {@code <thread/>} element is not used to identify individual messages, only conversations or messaging sessions.</p>
     * </blockquote>
     *
     * @return The thread.
     */
    public final synchronized String getThread() {
        if (thread != null) {
            return thread.value;
        }
        return null;
    }

    /**
     * Sets the thread.
     *
     * @param thread The thread.
     * @see #getThread()
     */
    public final synchronized void setThread(String thread) {
        this.thread = thread != null ? new ThreadElement(thread, null) : null;
    }

    /**
     * Gets the parent thread.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6121.html#message-syntax-thread">5.2.5.  Thread Element</a></cite></p>
     * <p>The {@code <thread/>} element MAY possess a 'parent' attribute that identifies another thread of which the current thread is an offshoot or child. The 'parent' attribute MUST conform to the syntax of the {@code <thread/>} element itself and its value MUST be different from the XML character data of the {@code <thread/>} element on which the 'parent' attribute is included.</p>
     * </blockquote>
     *
     * @return The parent thread.
     */
    public final synchronized String getParentThread() {
        if (thread != null) {
            return thread.parent;
        }
        return null;
    }

    /**
     * Sets the parent thread.
     *
     * @param parent The parent thread.
     * @see #getParentThread()
     */
    public final synchronized void setParentThread(String parent) {
        this.thread = new ThreadElement(thread != null ? thread.value : null, parent);
    }

    @Override
    public final Message createError(StanzaError error) {
        return new Message(getFrom(), Type.ERROR, getBodies(), getSubjects(), getThread(), getParentThread(), getId(), getTo(), getLanguage(), getExtensions(), error);
    }

    @Override
    public final Message createError(Condition condition) {
        return createError(new StanzaError(condition));
    }

    @Override
    public final synchronized String toString() {
        StringBuilder sb = new StringBuilder();
        if (type != null) {
            String sType = type.name();
            sb.append(sType.substring(0, 1)).append(sType.substring(1).toLowerCase()).append(' ');
        }
        sb.append("Message").append(super.toString());
        String body = getBody();
        if (body != null) {
            sb.append(": ").append(body);
        }
        return sb.toString();
    }

    /**
     * Represents a {@code <message/>} 'type' attribute.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6121.html#message-syntax-type">5.2.2.  Type Attribute</a></cite></p>
     * <p>Common uses of the message stanza in instant messaging applications include: single messages; messages sent in the context of a one-to-one chat session; messages sent in the context of a multi-user chat room; alerts, notifications, or other information to which no reply is expected; and errors. These uses are differentiated via the 'type' attribute.</p>
     * </blockquote>
     */
    @XmlType(name = "messageType")
    public enum Type {
        /**
         * The message is a standalone message that is sent outside the context of a one-to-one conversation or groupchat, and to which it is expected that the recipient will reply. Typically a receiving client will present a message of type "normal" in an interface that enables the recipient to reply, but without a conversation history. The default value of the 'type' attribute is "normal".
         */
        @XmlEnumValue(value = "normal")
        NORMAL,
        /**
         * The message is sent in the context of a one-to-one chat session. Typically an interactive client will present a message of type "chat" in an interface that enables one-to-one chat between the two parties, including an appropriate conversation history.
         */
        @XmlEnumValue(value = "chat")
        CHAT,
        /**
         * The message provides an alert, a notification, or other transient information to which no reply is expected (e.g., news headlines, sports updates, near-real-time market data, or syndicated content). Because no reply to the message is expected, typically a receiving client will present a message of type "headline" in an interface that appropriately differentiates the message from standalone messages, chat messages, and groupchat messages (e.g., by not providing the recipient with the ability to reply). If the 'to' address is the bare JID, the receiving server SHOULD deliver the message to all of the recipient's available resources with non-negative presence priority and MUST deliver the message to at least one of those resources; if the 'to' address is a full JID and there is a matching resource, the server MUST deliver the message to that resource; otherwise the server MUST either silently ignore the message or return an error.
         */
        @XmlEnumValue(value = "headline")
        HEADLINE,
        /**
         * The message is sent in the context of a multi-user chat environment (similar to that of [IRC]). Typically a receiving client will present a message of type "groupchat" in an interface that enables many-to-many chat between the parties, including a roster of parties in the chatroom and an appropriate conversation history. For detailed information about XMPP-based groupchat, refer to [XEP-0045].
         */
        @XmlEnumValue(value = "groupchat")
        GROUPCHAT,
        /**
         * The message is generated by an entity that experiences an error when processing a message received from another entity. A client that receives a message of type "error" SHOULD present an appropriate interface informing the original sender regarding the nature of the error.
         */
        @XmlEnumValue(value = "error")
        ERROR
    }

    /**
     * The implementation of the message's {@code <thread/>} element.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6121.html#message-syntax-thread">5.2.5.  Thread Element</a></cite></p>
     * <p>The primary use of the XMPP {@code <thread/>} element is to uniquely identify a conversation thread or "chat session" between two entities instantiated by {@code <message/>} stanzas of type 'chat'. However, the XMPP {@code <thread/>} element MAY also be used to uniquely identify an analogous thread between two entities instantiated by {@code <message/>} stanzas of type 'headline' or 'normal', or among multiple entities in the context of a multi-user chat room instantiated by {@code <message/>} stanzas of type 'groupchat'. It MAY also be used for {@code <message/>} stanzas not related to a human conversation, such as a game session or an interaction between plugins. The {@code <thread/>} element is not used to identify individual messages, only conversations or messaging sessions.</p>
     * </blockquote>
     */
    private static final class ThreadElement {

        @XmlAttribute
        private final String parent;

        @XmlValue
        private final String value;

        private ThreadElement() {
            this(null, null);
        }

        private ThreadElement(String value, String parent) {
            this.value = value;
            this.parent = parent;
        }

        @Override
        public final String toString() {
            return value;
        }
    }
}
