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

package rocks.xmpp.extensions.compress.model.feature;

import rocks.xmpp.core.stream.model.StreamFeature;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The implementation of the {@code <compression/>} element in the {@code http://jabber.org/features/compress} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0138.html">XEP-0138: Stream Compression</a>
 * @see <a href="https://xmpp.org/extensions/xep-0138.html#schemas-stream">XML Schema</a>
 */
@XmlRootElement(name = "compression")
public final class CompressionFeature extends StreamFeature {

    private final List<String> method = new ArrayList<>();

    private CompressionFeature() {
    }

    public CompressionFeature(Collection<String> methods) {
        this.method.addAll(methods);
    }

    /**
     * Gets the available compression methods.
     *
     * @return The compression methods.
     */
    public final List<String> getMethods() {
        return Collections.unmodifiableList(method);
    }

    @Override
    public final int getPriority() {
        return 2;
    }

    @Override
    public boolean requiresRestart() {
        return true;
    }

    @Override
    public final String toString() {
        return "Stream Compression";
    }
}
