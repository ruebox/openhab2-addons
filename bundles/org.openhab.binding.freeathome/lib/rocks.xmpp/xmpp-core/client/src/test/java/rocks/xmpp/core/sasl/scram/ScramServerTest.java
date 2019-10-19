/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Christian Schudt
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

package rocks.xmpp.core.sasl.scram;

import org.testng.Assert;
import org.testng.annotations.Test;

import javax.security.sasl.SaslException;

/**
 * @author Christian Schudt
 */
public class ScramServerTest {
    @Test(expectedExceptions = SaslException.class)
    public void testScramServerInvalidUsername() throws SaslException {
        ScramServer.validateAndGetUsername("=dw");
    }

    @Test
    public void testScramServerValidUsername() throws SaslException {
        Assert.assertEquals(ScramServer.validateAndGetUsername("test=2C"), "test,");
        Assert.assertEquals(ScramServer.validateAndGetUsername("aaa=3D"), "aaa=");
        Assert.assertEquals(ScramServer.validateAndGetUsername("aaa=3D=2C=2C=3D"), "aaa=,,=");
    }
}
