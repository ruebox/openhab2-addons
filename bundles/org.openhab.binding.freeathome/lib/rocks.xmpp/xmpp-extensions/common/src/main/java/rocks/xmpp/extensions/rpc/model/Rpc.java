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

package rocks.xmpp.extensions.rpc.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The implementation of the {@code <query/>} element in the {@code jabber:iq:rpc} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0009.html">XEP-0009: Jabber-RPC</a>
 * @see <a href="https://xmpp.org/extensions/xep-0009.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "query")
public final class Rpc {

    /**
     * jabber:iq:rpc
     */
    public static final String NAMESPACE = "jabber:iq:rpc";

    private MethodCall methodCall;

    private MethodResponse methodResponse;

    private Rpc() {
    }

    /**
     * Creates a method call with a list of parameters.
     *
     * @param methodName The method name.
     * @param parameters The parameters.
     */
    private Rpc(String methodName, Value... parameters) {
        this.methodCall = new MethodCall(methodName, parameters);
    }

    /**
     * Creates a method response.
     *
     * @param value The return value.
     */
    private Rpc(Value value) {
        this.methodResponse = new MethodResponse(value);
    }

    /**
     * Creates a method response with a fault.
     *
     * @param fault The fault.
     */
    private Rpc(MethodResponse.Fault fault) {
        this.methodResponse = new MethodResponse(fault);
    }

    /**
     * Creates a method call with a list of parameters.
     *
     * @param methodName The method name.
     * @param parameters The parameters.
     * @return The RPC element.
     */
    public static Rpc ofMethodCall(String methodName, Value... parameters) {
        return new Rpc(methodName, parameters);
    }

    /**
     * Creates a method response.
     *
     * @param value The return value.
     * @return The RPC element.
     */
    public static Rpc ofMethodResponse(Value value) {
        return new Rpc(value);
    }

    /**
     * Creates a method response with a fault.
     *
     * @param faultCode   The fault code.
     * @param faultString The fault string.
     * @return The RPC element.
     */
    public static Rpc ofFaultResponse(int faultCode, String faultString) {
        return new Rpc(new MethodResponse.Fault(faultCode, faultString));
    }

    /**
     * Gets the method call.
     *
     * @return The method call.
     */
    public final MethodCall getMethodCall() {
        return methodCall;
    }

    /**
     * Gets the method response.
     *
     * @return The method response.
     */
    public final MethodResponse getMethodResponse() {
        return methodResponse;
    }

    @Override
    public final String toString() {
        if (this.methodCall != null) {
            return methodCall.toString();
        }
        if (this.methodResponse != null) {
            return methodResponse.toString();
        }
        return super.toString();
    }

    /**
     * The implementation of a RPC method call.
     */
    @XmlType(propOrder = {"methodName", "parameters"})
    public static final class MethodCall {

        @XmlElementWrapper(name = "params")
        @XmlElement(name = "param")
        private final List<Parameter> parameters = new ArrayList<>();

        private final String methodName;

        private MethodCall() {
            this.methodName = null;
        }

        MethodCall(String methodName, Value... parameters) {
            this.methodName = Objects.requireNonNull(methodName);
            for (Value value : parameters) {
                this.parameters.add(new Parameter(value));
            }
        }

        /**
         * Gets the method name.
         *
         * @return The method name.
         */
        public final String getMethodName() {
            return methodName;
        }

        /**
         * Gets the parameters.
         *
         * @return The parameters.
         */
        public final List<Value> getParameters() {
            List<Value> values = parameters.stream().map(Parameter::getValue).collect(Collectors.toList());
            return Collections.unmodifiableList(values);
        }

        @Override
        public final String toString() {
            return methodName + '(' + String.join(", ", parameters.stream().map(Object::toString).collect(Collectors.toList())) + ')';
        }
    }

    /**
     * The implementation of a method response.
     */
    public static final class MethodResponse {
        @XmlElementWrapper(name = "params")
        @XmlElement(name = "param")
        private final List<Parameter> parameters = new ArrayList<>();

        private final Fault fault;

        private MethodResponse() {
            this.fault = null;
        }

        private MethodResponse(Value value) {
            this.parameters.add(new Parameter(value));
            this.fault = null;
        }

        private MethodResponse(Fault fault) {
            this.fault = fault;
        }

        /**
         * Gets the response value.
         *
         * @return The response value.
         */
        public final Value getResponse() {
            if (!parameters.isEmpty()) {
                return parameters.get(0).getValue();
            }
            return null;
        }

        /**
         * Gets the fault.
         *
         * @return The fault.
         */
        public final Fault getFault() {
            return fault;
        }

        @Override
        public final String toString() {
            if (fault != null) {
                return fault.toString();
            }
            return "Response: " + parameters.get(0);
        }

        /**
         * The implementation of a RPC fault.
         */
        public static final class Fault {

            private Value value;

            private Fault() {
            }

            /**
             * @param faultCode   The fault code.
             * @param faultString The fault string.
             */
            private Fault(int faultCode, String faultString) {
                Map<String, Value> faultMap = new LinkedHashMap<>();
                faultMap.put("faultCode", Value.of(faultCode));
                faultMap.put("faultString", Value.of(faultString));
                this.value = new Value(faultMap);
            }

            /**
             * Gets the fault code.
             *
             * @return The fault code.
             */
            public final int getFaultCode() {
                if (value != null) {
                    Map<String, Value> map = value.getAsMap();
                    if (map != null) {
                        Value faultCode = map.get("faultCode");
                        if (faultCode != null) {
                            Integer value = faultCode.getAsInteger();
                            if (value != null) {
                                return value;
                            }
                        }
                    }
                }
                return 0;
            }

            /**
             * Gets the fault string.
             *
             * @return The fault string.
             */
            public final String getFaultString() {
                if (value != null) {
                    Map<String, Value> map = value.getAsMap();
                    if (map != null) {
                        Value faultCode = map.get("faultString");
                        if (faultCode != null) {
                            return faultCode.getAsString();
                        }
                    }
                }
                return null;
            }

            @Override
            public final String toString() {
                return "FaultCode: " + getFaultCode() + "; FaultString: " + getFaultString();
            }
        }
    }
}
