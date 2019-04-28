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

package rocks.xmpp.extensions.rpc.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The value type, which is used by XML-RPC.
 *
 * @author Christian Schudt
 */
public final class Value {

    @XmlElements(value = {
            @XmlElement(name = "i4", type = Integer.class),
            @XmlElement(name = "int", type = Integer.class),
            @XmlElement(name = "string", type = String.class),
            @XmlElement(name = "double", type = Double.class),
            @XmlElement(name = "base64", type = byte[].class),
            @XmlElement(name = "boolean", type = NumericBoolean.class),
            @XmlElement(name = "dateTime.iso8601", type = XMLGregorianCalendar.class), // Using OffsetDateTime here does not work, not even with the Adapter
            @XmlElement(name = "array", type = ArrayType.class),
            @XmlElement(name = "struct", type = StructType.class)
    })
    private final Object value;

    private Value() {
        this.value = null;
    }

    /**
     * Creates an value.
     *
     * @param value The value.
     */
    private Value(Object value) {
        this.value = value;
    }

    /**
     * Creates a boolean value.
     *
     * @param b The boolean value.
     */
    private Value(boolean b) {
        this.value = new NumericBoolean(b);
    }

    /**
     * Creates a date value.
     *
     * @param date The date value.
     */
    private Value(OffsetDateTime date) {
        XMLGregorianCalendar xmlGregorianCalendar;
        try {
            xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
            xmlGregorianCalendar.setYear(date.getYear());
            xmlGregorianCalendar.setMonth(date.getMonth().getValue());
            xmlGregorianCalendar.setDay(date.getDayOfMonth());
            xmlGregorianCalendar.setTime(date.getHour(), date.getMinute(), date.getSecond()); // date.get(ChronoField.MILLI_OF_SECOND)
            xmlGregorianCalendar.setTimezone(date.getOffset().getTotalSeconds() / 60);
        } catch (DatatypeConfigurationException e) {
            xmlGregorianCalendar = null;
        }
        this.value = xmlGregorianCalendar;
    }

    /**
     * Creates an array type value.
     *
     * @param list The array type value.
     */
    private Value(Collection<Value> list) {
        if (list != null) {
            ArrayType arrayType = new ArrayType();
            arrayType.values.addAll(new ArrayList<>(list));
            this.value = arrayType;
        } else {
            this.value = null;
        }
    }

    /**
     * Creates a struct type value.
     *
     * @param map The struct type value.
     */
    Value(Map<String, Value> map) {
        if (map != null) {
            StructType structType = new StructType();
            structType.member.addAll(map.entrySet().stream().map(entry -> new StructType.MemberType(entry.getKey(), entry.getValue())).collect(Collectors.toList()));
            this.value = structType;
        } else {
            this.value = null;
        }
    }

    /**
     * Creates an integer value.
     *
     * @param integer The integer value.
     * @return The value element.
     */
    public static Value of(Integer integer) {
        return new Value(integer);
    }

    /**
     * Creates a string value.
     *
     * @param string The string value.
     * @return The value element.
     */
    public static Value of(String string) {
        return new Value(string);
    }

    /**
     * Creates a double value.
     *
     * @param d The double value.
     * @return The value element.
     */
    public static Value of(Double d) {
        return new Value(d);
    }

    /**
     * Creates a binary (base64) value.
     *
     * @param bytes The binary value.
     * @return The value element.
     */
    public static Value of(byte[] bytes) {
        return new Value(bytes);
    }

    /**
     * Creates a boolean value.
     *
     * @param b The boolean value.
     * @return The value element.
     */
    public static Value of(boolean b) {
        return new Value(b);
    }

    /**
     * Creates a date value.
     *
     * @param dateTime The date time value.
     * @return The value element.
     */
    public static Value of(OffsetDateTime dateTime) {
        return new Value(dateTime);
    }

    /**
     * Creates an array type value.
     *
     * @param list The array type value.
     * @return The value element.
     */
    public static Value of(Collection<Value> list) {
        return new Value(list);
    }

    /**
     * Creates a struct type value.
     *
     * @param map The struct type value.
     * @return The value element.
     */
    public static Value of(Map<String, Value> map) {
        return new Value(map);
    }

    /**
     * Gets the value as integer or null.
     *
     * @return The integer or null.
     */
    public final Integer getAsInteger() {
        return value instanceof Integer ? (Integer) value : null;
    }

    /**
     * Gets the value as double or null.
     *
     * @return The double or null.
     */
    public final Double getAsDouble() {
        return value instanceof Double ? (Double) value : null;
    }

    /**
     * Gets the value as string or null.
     *
     * @return The string or null.
     */
    public final String getAsString() {
        return value instanceof String ? (String) value : null;
    }

    /**
     * Gets the value as byte array or null.
     *
     * @return The byte array or null.
     */
    public final byte[] getAsByteArray() {
        return value instanceof byte[] ? (byte[]) value : null;
    }

    /**
     * Gets the value as boolean.
     *
     * @return The boolean.
     */
    public final boolean getAsBoolean() {
        return value instanceof NumericBoolean && ((NumericBoolean) value).getAsBoolean();
    }

    /**
     * Gets the value as date or null.
     *
     * @return The date or null.
     */
    public final OffsetDateTime getAsInstant() {
        if (value instanceof XMLGregorianCalendar) {
            XMLGregorianCalendar calendar = (XMLGregorianCalendar) value;
            return OffsetDateTime.of(calendar.getYear(), calendar.getMonth(), calendar.getDay(), calendar.getHour(), calendar.getMinute(), calendar.getSecond(), 0, ZoneOffset.ofTotalSeconds(calendar.getTimezone() * 60));
        }
        return null;
    }

    /**
     * Gets the value as array or null.
     *
     * @return The array or null.
     */
    public final List<Value> getAsArray() {
        if (value instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) value;
            return new ArrayList<>(arrayType.values);
        }
        return null;
    }

    /**
     * Gets the value as map or null.
     *
     * @return The map or null.
     */
    public final Map<String, Value> getAsMap() {
        if (value instanceof StructType) {
            StructType structType = (StructType) value;
            Map<String, Value> result = new HashMap<>();
            for (StructType.MemberType member : structType.member) {
                result.put(member.name, member.value);
            }
            return result;
        }
        return null;
    }

    @Override
    public final String toString() {
        return String.valueOf(value);
    }
}