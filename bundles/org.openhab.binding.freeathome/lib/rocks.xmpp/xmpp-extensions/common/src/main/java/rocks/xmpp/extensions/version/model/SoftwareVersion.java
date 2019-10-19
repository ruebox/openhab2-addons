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

package rocks.xmpp.extensions.version.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

/**
 * The implementation of the {@code <query/>} element in the {@code jabber:iq:version} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0092.html">XEP-0092: Software Version</a>
 * @see <a href="https://xmpp.org/extensions/xep-0092.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "query")
public final class SoftwareVersion {

    /**
     * jabber:iq:version
     */
    public static final String NAMESPACE = "jabber:iq:version";

    private final String name;

    private final String version;

    private final String os;

    public SoftwareVersion() {
        this.name = null;
        this.version = null;
        this.os = null;
    }

    /**
     * Creates a software version instance.
     * The operating system will be determined automatically.
     * <blockquote>
     * <p><cite><a href="https://xmpp.org/extensions/xep-0092.html#security">5. Security Considerations</a></cite></p>
     * <p>Revealing the application's underlying operating system may open the user or system to attacks directed against that operating system; therefore, an application MUST provide a way for a human user or administrator to disable sharing of information about the operating system.</p>
     * </blockquote>
     * If you want to hide OS information, use {@link #SoftwareVersion(String, String, String)} and pass null for the os parameter.
     *
     * @param name    The software's name.
     * @param version The software's version.
     */
    public SoftwareVersion(String name, String version) {
        this(name, version, System.getProperty("os.name"));
    }

    /**
     * Creates a software version instance.
     *
     * @param name    The software's name.
     * @param version The software's version.
     * @param os      The operating system.
     */
    public SoftwareVersion(String name, String version, String os) {
        this.name = Objects.requireNonNull(name);       // This element is REQUIRED in a result.
        this.version = Objects.requireNonNull(version); // This element is REQUIRED in a result.
        this.os = os;                                   // This element is OPTIONAL in a result
    }

    /**
     * Gets the operating system.
     *
     * @return The operating system.
     */
    public final String getOs() {
        return os;
    }

    /**
     * Gets the version.
     *
     * @return The version.
     */
    public final String getVersion() {
        return version;
    }

    /**
     * Gets the software's name.
     *
     * @return The software's name.
     */
    public final String getName() {
        return name;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder("Software Version: ");
        sb.append(name).append(' ').append(version);
        if (os != null) {
            sb.append(", ").append(os);
        }
        return sb.toString();
    }
}
