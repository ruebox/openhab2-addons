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

package rocks.xmpp.extensions.rpc;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.client.ClientIQ;
import rocks.xmpp.extensions.rpc.model.Rpc;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * @author Christian Schudt
 */
public class RpcMethodResponseTest extends XmlTest {

    protected RpcMethodResponseTest() throws JAXBException {
        super(ClientIQ.class, Rpc.class);
    }

    @Test
    public void unmarshalRpc() throws XMLStreamException, JAXBException {
        String xml = "<iq type='result' \n" +
                "    from='responder@company-a.com/jrpc-server' \n" +
                "    to='requester@company-b.com/jrpc-client' \n" +
                "    id='rpc1'>\n" +
                "  <query xmlns='jabber:iq:rpc'>\n" +
                "    <methodResponse>\n" +
                "      <params>\n" +
                "        <param>\n" +
                "          <value><string>Colorado</string></value>\n" +
                "        </param>\n" +
                "      </params>\n" +
                "    </methodResponse>\n" +
                "  </query>\n" +
                "</iq>";

        IQ iq = unmarshal(xml, IQ.class);
        Rpc rpc = iq.getExtension(Rpc.class);
        Assert.assertNotNull(rpc);
        Assert.assertEquals(rpc.getMethodResponse().getResponse().getAsString(), "Colorado");
    }

    @Test
    public void unmarshalFault() throws XMLStreamException, JAXBException {
        String xml = "<iq type='result' \n" +
                "    from='responder@company-a.com/jrpc-server' \n" +
                "    to='requester@company-b.com/jrpc-client' \n" +
                "    id='rpc1'>\n" +
                "  <query xmlns='jabber:iq:rpc'>\n" +
                "   <methodResponse>\n" +
                "  <fault>\n" +
                "    <value>\n" +
                "      <struct>\n" +
                "        <member>\n" +
                "          <name>faultCode</name>\n" +
                "          <value><int>4</int></value>\n" +
                "        </member>\n" +
                "        <member>\n" +
                "          <name>faultString</name>\n" +
                "          <value><string>Too many parameters.</string></value>\n" +
                "        </member>\n" +
                "      </struct>\n" +
                "    </value>\n" +
                "  </fault>\n" +
                "</methodResponse>" +
                "  </query>\n" +
                "</iq>";

        IQ iq = unmarshal(xml, IQ.class);
        Rpc rpc = iq.getExtension(Rpc.class);
        Assert.assertNotNull(rpc);
        Assert.assertNotNull(rpc.getMethodResponse().getFault());
        Assert.assertEquals(rpc.getMethodResponse().getFault().getFaultCode(), 4);
        Assert.assertEquals(rpc.getMethodResponse().getFault().getFaultString(), "Too many parameters.");
    }

    @Test
    public void unmarshal() throws JAXBException, XMLStreamException {
        String xml = "<query xmlns='jabber:iq:rpc'><methodResponse>\n" +
                "   <params>\n" +
                "      <param>\n" +
                "         <value>\n" +
                "            <struct>\n" +
                "               <member>\n" +
                "                  <name>username</name>\n" +
                "                  <value>\n" +
                "                     <string>xxx</string>\n" +
                "                  </value>\n" +
                "               </member>\n" +
                "               <member>\n" +
                "                  <name>branch_id</name>\n" +
                "                  <value>\n" +
                "                     <string>40277</string>\n" +
                "                  </value>\n" +
                "               </member>\n" +
                "               <member>\n" +
                "                  <name>first_name</name>\n" +
                "                  <value>\n" +
                "                     <string>xxx API</string>\n" +
                "                  </value>\n" +
                "               </member>\n" +
                "               <member>\n" +
                "                  <name>last_name</name>\n" +
                "                  <value>\n" +
                "                     <string>Test User</string>\n" +
                "                  </value>\n" +
                "               </member>\n" +
                "               <member>\n" +
                "                  <name>user_id</name>\n" +
                "                  <value>\n" +
                "                     <string>115524</string>\n" +
                "                  </value>\n" +
                "               </member>\n" +
                "               <member>\n" +
                "                  <name>xxx</name>\n" +
                "                  <value />\n" +
                "               </member>\n" +
                "               <member>\n" +
                "                  <name>reseller_id</name>\n" +
                "                  <value>\n" +
                "                     <string>43</string>\n" +
                "                  </value>\n" +
                "               </member>\n" +
                "               <member>\n" +
                "                  <name>role_id</name>\n" +
                "                  <value>\n" +
                "                     <string>1</string>\n" +
                "                  </value>\n" +
                "               </member>\n" +
                "               <member>\n" +
                "                  <name>organization_id</name>\n" +
                "                  <value>\n" +
                "                     <string>44316</string>\n" +
                "                  </value>\n" +
                "               </member>\n" +
                "               <member>\n" +
                "                  <name>call_center_agents</name>\n" +
                "                  <value>\n" +
                "                     <array>\n" +
                "                        <data>\n" +
                "                           <value>\n" +
                "                              <string>22873</string>\n" +
                "                           </value>\n" +
                "                        </data>\n" +
                "                     </array>\n" +
                "                  </value>\n" +
                "               </member>\n" +
                "               <member>\n" +
                "                  <name>extensions</name>\n" +
                "                  <value>\n" +
                "                     <array>\n" +
                "                        <data>\n" +
                "                           <value>\n" +
                "                              <string>496187</string>\n" +
                "                           </value>\n" +
                "                           <value>\n" +
                "                              <string>496528</string>\n" +
                "                           </value>\n" +
                "                        </data>\n" +
                "                     </array>\n" +
                "                  </value>\n" +
                "               </member>\n" +
                "               <member>\n" +
                "                  <name>primary_extension</name>\n" +
                "                  <value>\n" +
                "                     <string>496528</string>\n" +
                "                  </value>\n" +
                "               </member>\n" +
                "               <member>\n" +
                "                  <name>customer_id</name>\n" +
                "                  <value>\n" +
                "                     <string>45132</string>\n" +
                "                  </value>\n" +
                "               </member>\n" +
                "               <member>\n" +
                "                  <name>email</name>\n" +
                "                  <value>\n" +
                "                     <string>xxx@xxx</string>\n" +
                "                  </value>\n" +
                "               </member>\n" +
                "            </struct>\n" +
                "         </value>\n" +
                "      </param>\n" +
                "   </params>\n" +
                "</methodResponse></query>";

        Rpc rpc = unmarshal(xml, Rpc.class);

        Assert.assertNotNull(rpc);
        Assert.assertNotNull(rpc.getMethodResponse());
        Assert.assertNotNull(rpc.toString());
    }
}
