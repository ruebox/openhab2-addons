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

import javax.xml.bind.annotation.XmlTransient;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * A stanza which is extensible (can have more than one extension), i.e. {@link Message} and {@link Presence}.
 *
 * @author Christian Schudt
 */
@XmlTransient
public abstract class ExtensibleStanza extends Stanza {

    ExtensibleStanza(Jid to, Jid from, String id, Locale language, Collection<?> extensions, StanzaError error) {
        super(to, from, id, language, extensions, error);
    }

    /**
     * Adds an extension to this stanza.
     *
     * @param extension The extension.
     * @return Whether the extension was added.
     */
    public final boolean addExtension(Object extension) {
        return extension != null && getExtensions().add(extension);
    }

    /**
     * Adds extensions to this stanza.
     *
     * @param extensions The extensions.
     * @since 0.8.0
     */
    public final void addExtensions(Object... extensions) {
        addExtensions(Arrays.asList(extensions));
    }

    /**
     * Adds extensions to this stanza.
     *
     * @param extensions The extensions.
     * @since 0.8.0
     */
    public final void addExtensions(Collection<Object> extensions) {
        if (extensions != null) {
            extensions.forEach(this::addExtension);
        }
    }

    /**
     * Removes all extensions of the given type.
     *
     * @param clazz The extension class.
     * @return Whether the extension could be removed.
     */
    public final boolean removeExtension(Class<?> clazz) {
        return getExtensions().removeIf(extension -> clazz != null && clazz.isAssignableFrom(extension.getClass()));
    }

    /**
     * Replaces an existing extension of the same type or adds the extension, if it doesn't exist yet.
     * <p>
     * This is useful, if you want to make sure, that a stanza only has one extension of a specific type.
     *
     * @param extension The extension.
     * @since 0.8.0
     */
    public final void putExtension(Object extension) {
        if (extension != null) {
            removeExtension(extension.getClass());
        }
        addExtension(extension);
    }

    /**
     * Gets all extensions.
     *
     * @return The extensions.
     */
    public final List<Object> getExtensions() {
        return extensions;
    }

    /**
     * Gets the extensions of the given type. The returned list is unmodifiable.
     *
     * @param <T>   The extension type.
     * @param clazz The extension class.
     * @return The unmodifiable list of extensions which are of the given type.
     * @since 0.8.0
     */
    @SuppressWarnings("unchecked")
    public final <T> List<T> getExtensions(Class<T> clazz) {
        return Collections.unmodifiableList(this.extensions.stream()
                .filter(extension -> clazz.isAssignableFrom(extension.getClass()))
                .map(extension -> (T) extension)
                .collect(Collectors.toList()));
    }
}