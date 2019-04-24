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

package rocks.xmpp.extensions.avatar.model.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.util.Objects;

/**
 * The implementation of the {@code <data/>} element in the {@code urn:xmpp:avatar:data} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0084.html">XEP-0084: User Avatar</a>
 * @see <a href="https://xmpp.org/extensions/xep-0084.html#schema-data">XML Schema</a>
 */
@XmlRootElement(name = "data")
public final class AvatarData {

    /**
     * urn:xmpp:avatar:data
     */
    public static final String NAMESPACE = "urn:xmpp:avatar:data";

    @XmlValue
    private final byte[] data;

    private AvatarData() {
        this.data = null;
    }

    /**
     * @param data The image data.
     */
    public AvatarData(byte[] data) {
        this.data = Objects.requireNonNull(data).clone();
    }

    /**
     * Gets the image data.
     *
     * @return The image data.
     */
    public final byte[] getData() {
        return data.clone();
    }
}
