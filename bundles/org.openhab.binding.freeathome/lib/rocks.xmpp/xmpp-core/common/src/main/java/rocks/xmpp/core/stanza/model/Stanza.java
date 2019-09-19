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
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.core.stream.model.StreamElement;
import rocks.xmpp.util.adapters.LocaleAdapter;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The abstract base class for a XML stanza.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas">8.  XML Stanzas</a></cite>
 * <p>After a client and a server (or two servers) have completed stream negotiation, either party can send XML stanzas. Three kinds of XML stanza are defined for the 'jabber:client' and 'jabber:server' namespaces: {@code <message/>}, {@code <presence/>}, and {@code <iq/>}.</p>
 * </blockquote>
 *
 * @author Christian Schudt
 */
@XmlTransient
public abstract class Stanza implements StreamElement {

    @XmlAttribute
    private Jid from;

    @XmlAttribute
    private String id;

    @XmlAttribute
    private Jid to;

    @XmlAttribute(namespace = XMLConstants.XML_NS_URI)
    @XmlJavaTypeAdapter(LocaleAdapter.class)
    private Locale lang;

    @XmlAnyElement(lax = true)
    final List<Object> extensions = new CopyOnWriteArrayList<>();

    private StanzaError error;

    Stanza() {
        this.to = null;
        this.from = null;
        this.id = null;
        this.lang = null;
        this.error = null;
    }

    Stanza(Jid to, Jid from, String id, Locale language, Collection<?> extensions, StanzaError error) {
        this.to = to;
        this.from = from;
        this.id = id;
        this.lang = language;
        if (extensions != null) {
            this.extensions.addAll(extensions);
        }
        this.error = error;
    }

    /**
     * Gets the stanza's 'from' attribute.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-attributes-from">8.1.2.  from</a></cite></p>
     * <p>The 'from' attribute specifies the JID of the sender.</p>
     * </blockquote>
     *
     * @return The JID.
     */
    public final synchronized Jid getFrom() {
        return from;
    }

    /**
     * Sets the stanza's 'from' attribute, i.e. the sender.
     *
     * @param from The sender.
     * @see #getFrom() ()
     */
    public final synchronized void setFrom(Jid from) {
        this.from = from;
    }

    /**
     * Gets the stanza's 'id' attribute.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-attributes-id">8.1.3.  id</a></cite></p>
     * <p>The 'id' attribute is used by the originating entity to track any response or error stanza that it might receive in relation to the generated stanza from another entity (such as an intermediate server or the intended recipient).</p>
     * <p>It is up to the originating entity whether the value of the 'id' attribute is unique only within its current stream or unique globally.</p>
     * <p>For {@code <message/>} and {@code <presence/>} stanzas, it is RECOMMENDED for the originating entity to include an 'id' attribute; for {@code <iq/>} stanzas, it is REQUIRED.</p>
     * <p>If the generated stanza includes an 'id' attribute then it is REQUIRED for the response or error stanza to also include an 'id' attribute, where the value of the 'id' attribute MUST match that of the generated stanza.</p>
     * </blockquote>
     *
     * @return The id.
     */
    public final synchronized String getId() {
        return id;
    }

    /**
     * Sets the stanza's 'id' attribute.
     *
     * @param id The id.
     * @see #getId()
     */
    public final synchronized void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the stanza's 'to' attribute, i.e. the recipient.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-attributes-to">8.1.1.  to</a></cite></p>
     * <p>The 'to' attribute specifies the JID of the intended recipient for the stanza.</p>
     * </blockquote>
     *
     * @return The recipient.
     */
    public final synchronized Jid getTo() {
        return to;
    }

    /**
     * Sets the stanza's 'to' attribute, i.e. the recipient.
     *
     * @param to The recipient.
     * @see #getTo()
     */
    public final synchronized void setTo(Jid to) {
        this.to = to;
    }

    /**
     * Gets the stanza's 'xml:lang' attribute, i.e. its language.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-attributes-lang">8.1.5.  xml:lang</a></cite></p>
     * <p>A stanza SHOULD possess an 'xml:lang' attribute (as defined in Section 2.12 of [XML]) if the stanza contains XML character data that is intended to be presented to a human user (as explained in [CHARSETS], "internationalization is for humans"). The value of the 'xml:lang' attribute specifies the default language of any such human-readable XML character data.</p>
     * </blockquote>
     *
     * @return The language.
     */
    public final synchronized Locale getLanguage() {
        return lang;
    }

    /**
     * Sets the stanza's 'xml:lang' attribute, i.e. its language.
     *
     * @param language The language.
     * @see #getLanguage()
     */
    public final synchronized void setLanguage(Locale language) {
        this.lang = language;
    }

    /**
     * Gets the extension of the given type or null, if there's no such extension.
     *
     * @param <T>   The extension type.
     * @param clazz The extension class.
     * @return The extension.
     */
    @SuppressWarnings("unchecked")
    public final <T> T getExtension(Class<T> clazz) {
        for (Object extension : extensions) {
            if (clazz.isAssignableFrom(extension.getClass())) {
                return (T) extension;
            }
        }
        return null;
    }

    /**
     * Checks if the stanza has an extension of the given type.
     *
     * @param clazz The extension class.
     * @return If the extension could be removed.
     */
    public final boolean hasExtension(Class<?> clazz) {
        return getExtension(clazz) != null;
    }

    /**
     * Gets the stanza's 'error' element.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#stanzas-error">8.3.  Stanza Errors</a></cite></p>
     * <div>
     * Stanza-related errors are handled in a manner similar to stream errors. Unlike stream errors, stanza errors are recoverable; therefore, they do not result in termination of the XML stream and underlying TCP connection. Instead, the entity that discovers the error condition returns an error stanza, which is a stanza that:
     * <ul>
     * <li>is of the same kind (message, presence, or IQ) as the generated stanza that triggered the error</li>
     * <li>has a 'type' attribute set to a value of "error"</li>
     * <li>typically swaps the 'from' and 'to' addresses of the generated stanza</li>
     * <li>mirrors the 'id' attribute (if any) of the generated stanza that triggered the error</li>
     * <li>contains an {@code <error/>} child element that specifies the error condition and therefore provides a hint regarding actions that the sender might be able to take in an effort to remedy the error (however, it is not always possible to remedy the error)</li>
     * </ul>
     * </div>
     * </blockquote>
     *
     * @return The stanza error.
     */
    public final synchronized StanzaError getError() {
        return error;
    }

    /**
     * Sets the stanza's 'error' element.
     *
     * @param stanzaError The stanza error.
     * @see #getError()
     */
    public final synchronized void setError(StanzaError stanzaError) {
        this.error = stanzaError;
    }

    /**
     * Creates an error response for this stanza.
     *
     * @param error The error which is appended to the stanza.
     * @return The error response.
     * @see #getError()
     */
    public abstract Stanza createError(StanzaError error);

    /**
     * Creates an error response for this stanza.
     *
     * @param condition The error condition which is appended to the stanza.
     * @return The error response.
     * @see #getError()
     */
    public abstract Stanza createError(Condition condition);

    /**
     * Checks if a stanza is addressed either to itself or to the server. This is useful to check, if it's allowed to send a stanza before stream negotiation has completed.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#streams-negotiation-complete">4.3.5.  Completion of Stream Negotiation</a></cite></p>
     * <div>
     * The initiating entity MUST NOT attempt to send XML stanzas to entities other than itself (i.e., the client's connected resource or any other authenticated resource of the client's account) or the server to which it is connected until stream negotiation has been completed.
     * </div>
     * </blockquote>
     *
     * @param stanza            The stanza.
     * @param domain            The domain.
     * @param connectedResource The connected resource.
     * @return True, if the stanza is addressed to itself or to the server.
     */
    public static boolean isToItselfOrServer(final Stanza stanza, final CharSequence domain, final Jid connectedResource) {
        if (stanza instanceof Presence && stanza.getTo() == null) {
            return false;
        }
        if (stanza.getTo() == null) {
            return true;
        }
        final Jid toBare = stanza.getTo().asBareJid();
        return (connectedResource != null && toBare.equals(connectedResource.asBareJid()))
                || (domain != null && (toBare.equals(domain) || toBare.toString().endsWith("." + domain)));
    }

    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder();
        if (id != null) {
            sb.append(" (").append(id).append(')');
        }
        if (from != null) {
            sb.append(" from '").append(from).append('\'');
        }
        if (to != null) {
            sb.append(" to '").append(to).append('\'');
        }
        if (!extensions.isEmpty()) {
            sb.append("\nExtension(s):\n");
            for (Object o : extensions) {
                sb.append(o).append('\n');
            }
        }
        if (error != null) {
            sb.append('\n').append("Error: ").append(error);
        }
        return sb.toString();
    }
}
