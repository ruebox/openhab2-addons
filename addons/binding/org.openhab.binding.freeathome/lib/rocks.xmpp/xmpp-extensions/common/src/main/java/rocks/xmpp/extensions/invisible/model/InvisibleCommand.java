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

package rocks.xmpp.extensions.invisible.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * The invisible command, which is used to become invisible during the current XMPP session.
 *
 * @author Christian Schudt
 * @see #INVISIBLE
 * @see #VISIBLE
 * @see <a href="https://xmpp.org/extensions/xep-0186.html">XEP-0186: Invisible Command</a>
 */
@XmlTransient
@XmlSeeAlso({InvisibleCommand.Invisible.class, InvisibleCommand.Visible.class})
public abstract class InvisibleCommand {
    /**
     * urn:xmpp:invisible:0
     */
    public static final String NAMESPACE = "urn:xmpp:invisible:0";

    /**
     * The implementation of the {@code <invisible/>} element in the {@code urn:xmpp:invisible:0} namespace.
     */
    public static final InvisibleCommand INVISIBLE = new Invisible();

    /**
     * The implementation of the {@code <visible/>} element in the {@code urn:xmpp:invisible:0} namespace.
     */
    public static final InvisibleCommand VISIBLE = new Visible();

    private InvisibleCommand() {
    }


    /**
     * The implementation of the {@code <invisible/>} element in the {@code urn:xmpp:invisible:0} namespace.
     */
    @XmlRootElement
    @XmlType(factoryMethod = "create")
    static final class Invisible extends InvisibleCommand {
        private Invisible() {
        }

        private static Invisible create() {
            return (Invisible) INVISIBLE;
        }
    }

    /**
     * The implementation of the {@code <visible/>} element in the {@code urn:xmpp:invisible:0} namespace.
     */
    @XmlRootElement
    @XmlType(factoryMethod = "create")
    static final class Visible extends InvisibleCommand {
        private Visible() {
        }

        private static Visible create() {
            return (Visible) VISIBLE;
        }
    }
}
