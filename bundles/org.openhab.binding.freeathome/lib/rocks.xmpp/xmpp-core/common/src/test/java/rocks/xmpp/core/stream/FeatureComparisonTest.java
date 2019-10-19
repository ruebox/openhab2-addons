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

package rocks.xmpp.core.stream;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.bind.model.Bind;
import rocks.xmpp.core.sasl.model.Mechanisms;
import rocks.xmpp.core.session.model.Session;
import rocks.xmpp.core.stream.model.StreamFeature;
import rocks.xmpp.core.tls.model.StartTls;
import rocks.xmpp.extensions.compress.model.feature.CompressionFeature;
import rocks.xmpp.util.ComparableTestHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Christian Schudt
 */
public class FeatureComparisonTest {

    @Test
    public void testCorrectFeatureNegotiationOrder() {

        CompressionFeature compression = new CompressionFeature(Collections.emptyList());
        StartTls startTls = new StartTls();
        Mechanisms mechanisms = new Mechanisms(Collections.emptyList());
        Bind bind = new Bind();
        Session session = new Session();
        List<StreamFeature> features = new ArrayList<>();
        features.add(compression);
        features.add(startTls);
        features.add(mechanisms);
        features.add(bind);
        features.add(session);

        Collections.shuffle(features);
        features.sort(null);

        Assert.assertEquals(features.get(0), startTls);
        Assert.assertEquals(features.get(1), mechanisms);
        Assert.assertEquals(features.get(2), compression);
        Assert.assertEquals(features.get(3), bind);
        Assert.assertEquals(features.get(4), session);

        ComparableTestHelper.checkCompareToContract(features);
    }
}
