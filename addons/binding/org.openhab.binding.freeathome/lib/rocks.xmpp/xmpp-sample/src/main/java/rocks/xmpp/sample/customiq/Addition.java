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

package rocks.xmpp.sample.customiq;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

/**
 * This is a custom payload, which can be used to request the sum of two values and returns the sum.
 * It's only for illustrating purposes.
 *
 * @author Christian Schudt
 */
@XmlRootElement(name = "addition")
public final class Addition {

    @XmlElement(name = "summand1")
    private Integer summand1;

    @XmlElement(name = "summand2")
    private Integer summand2;

    @XmlElement(name = "sum")
    private Integer sum;

    /**
     * No-arg default constructor needed for JAXB.
     */
    private Addition() {
    }

    public Addition(Integer summand1, Integer summand2) {
        this.summand1 = Objects.requireNonNull(summand1);
        this.summand2 = Objects.requireNonNull(summand2);
    }

    public Addition(Integer sum) {
        this.sum = Objects.requireNonNull(sum);
    }

    public Integer getSummand1() {
        return summand1;
    }

    public Integer getSummand2() {
        return summand2;
    }

    @Override
    public String toString() {
        if (summand1 != null && summand2 != null) {
            return summand1 + " + " + summand2 + " = ???";
        }
        return "Sum: " + sum;
    }
}
