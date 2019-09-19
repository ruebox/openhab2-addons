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

package rocks.xmpp.extensions.chatstates.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a chat state, i.e. composing, active, inactive, paused or gone.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0085.html">XEP-0085: Chat State Notifications</a>
 * @see <a href="https://xmpp.org/extensions/xep-0085.html#schema">XML Schema</a>
 */
@XmlTransient
@XmlSeeAlso({ChatState.Active.class, ChatState.Composing.class, ChatState.Gone.class, ChatState.Inactive.class, ChatState.Paused.class})
public abstract class ChatState {

    /**
     * User is actively participating in the chat session.
     */
    public static final ChatState ACTIVE = new Active();

    /**
     * User is composing a message.
     */
    public static final ChatState COMPOSING = new Composing();

    /**
     * User has effectively ended their participation in the chat session.
     */
    public static final ChatState GONE = new Gone();

    /**
     * User has not been actively participating in the chat session.
     */
    public static final ChatState INACTIVE = new Inactive();

    /**
     * User had been composing but now has stopped.
     */
    public static final ChatState PAUSED = new Paused();

    /**
     * http://jabber.org/protocol/chatstates
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/chatstates";

    private ChatState() {
    }

    @Override
    public final String toString() {
        return "Chat state: " + getClass().getSimpleName().toLowerCase();
    }

    /**
     * The implementation of the {@code <active/>} element in the {@code http://jabber.org/protocol/chatstates} namespace, which represents an active chat state.
     */
    @XmlRootElement
    @XmlType(factoryMethod = "create")
    static final class Active extends ChatState {
        private Active() {
        }

        private static Active create() {
            return (Active) ChatState.ACTIVE;
        }
    }

    /**
     * The implementation of the {@code <composing/>} element in the {@code http://jabber.org/protocol/chatstates} namespace, which represents a composing chat state.
     */
    @XmlRootElement
    @XmlType(factoryMethod = "create")
    static final class Composing extends ChatState {
        private Composing() {
        }

        private static Composing create() {
            return (Composing) ChatState.COMPOSING;
        }
    }

    /**
     * The implementation of the {@code <gone/>} element in the {@code http://jabber.org/protocol/chatstates} namespace, which represents a gone chat state.
     */
    @XmlRootElement
    @XmlType(factoryMethod = "create")
    static final class Gone extends ChatState {
        private Gone() {
        }

        private static Gone create() {
            return (Gone) ChatState.GONE;
        }
    }

    /**
     * The implementation of the {@code <inactive/>} element in the {@code http://jabber.org/protocol/chatstates} namespace, which represents an inactive chat state.
     */
    @XmlRootElement
    @XmlType(factoryMethod = "create")
    static final class Inactive extends ChatState {
        private Inactive() {
        }

        private static Inactive create() {
            return (Inactive) ChatState.INACTIVE;
        }
    }

    /**
     * The implementation of the {@code <paused/>} element in the {@code http://jabber.org/protocol/chatstates} namespace, which represents a paused chat state.
     */
    @XmlRootElement
    @XmlType(factoryMethod = "create")
    static final class Paused extends ChatState {
        private Paused() {
        }

        private static Paused create() {
            return (Paused) ChatState.PAUSED;
        }
    }
}
