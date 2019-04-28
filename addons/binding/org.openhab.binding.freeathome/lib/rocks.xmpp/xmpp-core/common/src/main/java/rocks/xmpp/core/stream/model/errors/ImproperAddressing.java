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

package rocks.xmpp.core.stream.model.errors;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The implementation of the {@code <improper-addressing/>} stream error.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#streams-error-conditions-improper-addressing">4.9.3.7.  improper-addressing</a></cite></p>
 * <p>A stanza sent between two servers lacks a 'to' or 'from' attribute, the 'from' or 'to' attribute has no value, or the value violates the rules for XMPP addresses.</p>
 * </blockquote>
 * This class is a singleton.
 *
 * @see #IMPROPER_ADDRESSING
 */
@XmlRootElement(name = "improper-addressing")
@XmlType(factoryMethod = "create")
final class ImproperAddressing extends Condition {

    ImproperAddressing() {
    }

    private static ImproperAddressing create() {
        return (ImproperAddressing) IMPROPER_ADDRESSING;
    }
}
