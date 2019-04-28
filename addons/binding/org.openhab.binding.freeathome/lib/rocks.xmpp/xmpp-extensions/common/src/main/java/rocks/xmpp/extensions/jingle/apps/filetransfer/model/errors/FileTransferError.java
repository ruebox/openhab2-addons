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

package rocks.xmpp.extensions.jingle.apps.filetransfer.model.errors;

import rocks.xmpp.util.Strings;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Christian Schudt
 */
@XmlTransient
@XmlSeeAlso({FileTooLarge.class, FileNotAvailable.class})
public abstract class FileTransferError {

    /**
     * The {@code <file-too-large/>} error.
     */
    public static final FileTooLarge FILE_TOO_LARGE = new FileTooLarge();

    /**
     * The {@code <file-not-available/>} error.
     */
    public static final FileNotAvailable FILE_NOT_AVAILABLE = new FileNotAvailable();

    @Override
    public final String toString() {
        return "File transfer error: " + Strings.toDash(getClass().getSimpleName());
    }
}
