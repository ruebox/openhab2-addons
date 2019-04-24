/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Christian Schudt
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

package rocks.xmpp.extensions.data.validate.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

/**
 * The implementation of the {@code <validate/>} element in the {@code http://jabber.org/protocol/xdata-validate} namespace, which is used to validate form fields.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0122.html">XEP-0122: Data Forms Validation</a>
 * @see <a href="https://xmpp.org/extensions/xep-0122.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "validate")
public final class Validation {

    /**
     * http://jabber.org/protocol/xdata-validate
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/xdata-validate";

    @XmlAttribute(name = "datatype")
    private final String dataType;

    @XmlElements({@XmlElement(name = "basic", type = ValidationMethod.Basic.class),
            @XmlElement(name = "open", type = ValidationMethod.Open.class),
            @XmlElement(name = "range", type = ValidationMethod.Range.class),
            @XmlElement(name = "regex", type = ValidationMethod.Regex.class)})
    private final ValidationMethod validationMethod;

    @XmlElement(name = "list-range")
    private final ListRange listRange;

    private Validation() {
        this(null, null, null);
    }

    /**
     * Creates a validation with a data type.
     *
     * @param dataType The data type.
     */
    public Validation(String dataType) {
        this(dataType, null);
    }

    /**
     * Creates a validation with a validation method (and no data type, which is optional).
     *
     * @param validationMethod The validation method.
     */
    public Validation(ValidationMethod validationMethod) {
        this(null, validationMethod);
    }

    /**
     * Creates a validation with a data type and validation method.
     *
     * @param dataType         The data type.
     * @param validationMethod The validation method.
     */
    public Validation(String dataType, ValidationMethod validationMethod) {
        this(dataType, validationMethod, null);
    }

    /**
     * Creates a validation with a data type, validation method and list range.
     *
     * @param dataType         The data type.
     * @param validationMethod The validation method.
     * @param listRange        The list range, used for {@link rocks.xmpp.extensions.data.model.DataForm.Field.Type#LIST_MULTI} fields.
     */
    public Validation(String dataType, ValidationMethod validationMethod, ListRange listRange) {
        this.dataType = dataType;
        this.validationMethod = validationMethod;
        this.listRange = listRange;
    }

    /**
     * Gets the validation method.
     *
     * @return The validation method.
     * @see ValidationMethod.Basic
     * @see ValidationMethod.Open
     * @see ValidationMethod.Range
     * @see ValidationMethod.Regex
     */
    public final ValidationMethod getValidationMethod() {
        return validationMethod;
    }

    /**
     * Gets the data type of the field.
     *
     * @return The data type.
     */
    public final String getDataType() {
        return dataType;
    }

    /**
     * Gets the list range.
     *
     * @return The list range.
     */
    public final ListRange getListRange() {
        return listRange;
    }

    /**
     * The abstract validation method.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0122.html#usecases-validation">3.2 Validation Methods</a>
     */
    @XmlTransient
    public abstract static class ValidationMethod {

        @XmlValue
        final String value;

        private ValidationMethod() {
            this.value = null;
        }

        private ValidationMethod(String value) {
            this.value = value;
        }

        /**
         * Indicates, that the value(s) should simply match the field type and datatype constraints.
         */
        public static final class Basic extends ValidationMethod {
        }

        /**
         * For {@linkplain rocks.xmpp.extensions.data.model.DataForm.Field.Type#LIST_SINGLE "list-single"} or {@linkplain rocks.xmpp.extensions.data.model.DataForm.Field.Type#LIST_MULTI "list-multi"}, to indicate that the user may enter a custom value (matching the datatype constraints) or choose from the predefined values.
         * <p>
         * This validation method applies to {@linkplain rocks.xmpp.extensions.data.model.DataForm.Field.Type#TEXT_MULTI "text-multi"} differently; it hints that each value for a "text-multi" field shall be validated separately. This effectively turns "text-multi" fields into an open-ended "list-multi", with no options and all values automatically selected.
         * </p>
         */
        public static final class Open extends ValidationMethod {
        }

        /**
         * Indicates that the value should fall within a certain range.
         */
        public static final class Range extends ValidationMethod {

            @XmlAttribute
            private final String min;

            @XmlAttribute
            private final String max;

            private Range() {
                this.min = null;
                this.max = null;
            }

            /**
             * Creates a range validation.
             *
             * @param min The minimum value allowed. The value depends on the datatype in use.
             * @param max The maximum value allowed. The value depends on the datatype in use.
             */
            public Range(String min, String max) {
                this.min = min;
                this.max = max;
            }

            /**
             * Gets the minimum value allowed.
             *
             * @return The minimum.
             */
            public final String getMin() {
                return min;
            }

            /**
             * Gets the maximum value allowed.
             *
             * @return The maximum.
             */
            public final String getMax() {
                return max;
            }
        }

        /**
         * Indicates that the value should be restricted to a regular expression.
         */
        public static final class Regex extends ValidationMethod {

            private Regex() {
                super(null);
            }

            public Regex(String regex) {
                super(regex);
            }

            /**
             * Gets the regular expression.
             *
             * @return The regular expression.
             */
            public final String getRegex() {
                return value;
            }
        }
    }

    /**
     * For {@linkplain rocks.xmpp.extensions.data.model.DataForm.Field.Type#LIST_MULTI "list-multi"}, validation can indicate that a minimum and maximum number of options should be selected and/or entered. This selection range MAY be combined with the other methods to provide more flexibility.
     *
     * @see <a href="https://xmpp.org/extensions/xep-0122.html#usecases-ranges">3.3 Selection Ranges in "list-multi"</a>
     */
    public static final class ListRange {
        @XmlAttribute
        private final Integer min;

        @XmlAttribute
        private final Integer max;

        private ListRange() {
            this.min = null;
            this.max = null;
        }

        /**
         * Creates a list range for multi-text fields.
         *
         * @param min The minimum allowable number of selected/entered values.
         * @param max The maximum allowable number of selected/entered values.
         */
        public ListRange(int min, int max) {
            this.min = min;
            this.max = max;
        }

        /**
         * Gets the minimum allowable number of selected/entered values.
         *
         * @return The minimum allowable number of selected/entered values.
         */
        public final Integer getMin() {
            return min;
        }

        /**
         * Gets the maximum allowable number of selected/entered values.
         *
         * @return The maximum allowable number of selected/entered values.
         */
        public final Integer getMax() {
            return max;
        }
    }
}
