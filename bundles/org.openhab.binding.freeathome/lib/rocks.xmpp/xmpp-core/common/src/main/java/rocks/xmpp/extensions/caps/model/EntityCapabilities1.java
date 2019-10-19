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

package rocks.xmpp.extensions.caps.model;

import rocks.xmpp.core.stream.model.StreamFeature;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.disco.model.info.Identity;
import rocks.xmpp.extensions.disco.model.info.InfoNode;
import rocks.xmpp.extensions.hashes.model.Hashed;
import rocks.xmpp.util.Strings;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The implementation of the {@code <c/>} element in the {@code http://jabber.org/protocol/caps}.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0115.html">XEP-0115: Entity Capabilities</a>
 * @see <a href="https://xmpp.org/extensions/xep-0115.html#schema">XML Schema</a>
 */
@XmlRootElement(name = "c")
public final class EntityCapabilities1 extends StreamFeature implements EntityCapabilities, Hashed {

    /**
     * http://jabber.org/protocol/caps
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/caps";

    /**
     * The hashing algorithm used to generate the verification string.
     */
    @XmlAttribute
    private final String hash;

    @XmlAttribute
    private final String node;

    /**
     * The 'ver' attribute is a specially-constructed string (called a "verification string") that represents the entity's service discovery identity.
     */
    @XmlAttribute
    private final byte[] ver;

    private EntityCapabilities1() {
        this.node = null;
        this.hash = null;
        this.ver = null;
    }

    /**
     * Creates an entity caps from an info node and hash function.
     *
     * @param node          The node.
     * @param infoNode      The info node.
     * @param messageDigest The hash function.
     */
    public EntityCapabilities1(final String node, final InfoNode infoNode, final MessageDigest messageDigest) {
        this.node = Objects.requireNonNull(node);
        this.hash = messageDigest.getAlgorithm();
        this.ver = messageDigest.digest(createVerificationString(infoNode));
    }

    @Override
    public final String getHashAlgorithm() {
        return hash;
    }

    @Override
    public final byte[] getHashValue() {
        return ver.clone();
    }

    /**
     * Gets the node.
     * <blockquote>
     * <p>A URI that uniquely identifies a software application, typically a URL at the website of the project or company that produces the software.</p>
     * <p>It is RECOMMENDED for the value of the 'node' attribute to be an HTTP URL at which a user could find further information about the software product, such as "http://psi-im.org" for the Psi client; this enables a processing application to also determine a unique string for the generating application, which it could maintain in a list of known software implementations (e.g., associating the name received via the disco#info reply with the URL found in the caps data).</p>
     * </blockquote>
     *
     * @return The node.
     */
    public final String getNode() {
        return node;
    }

    /**
     * Gets the verification string that is used to verify the identity and supported features of the entity.
     *
     * @return The verification string.
     */
    public final String getVerificationString() {
        return Base64.getEncoder().encodeToString(ver);
    }

    @Override
    public final Set<Hashed> getCapabilityHashSet() {
        return Collections.singleton(this);
    }

    @Override
    public final byte[] createVerificationString(final InfoNode infoNode) {

        final Set<Identity> identities = infoNode.getIdentities();
        final Set<String> features = infoNode.getFeatures();
        final List<DataForm> dataForms = new ArrayList<>(infoNode.getExtensions());

        // 1. Initialize an empty string S.
        final StringBuilder sb = new StringBuilder();

        // 2. Sort the service discovery identities [15] by category and then by type and then by xml:lang (if it exists), formatted as CATEGORY '/' [TYPE] '/' [LANG] '/' [NAME]. [16] Note that each slash is included even if the LANG or NAME is not included (in accordance with XEP-0030, the category and type MUST be included.
        // This is done by TreeSet.

        // 3. For each identity, append the 'category/type/lang/name' to S, followed by the '<' character.
        for (Identity identity : identities) {
            if (identity.getCategory() != null) {
                sb.append(identity.getCategory());
            }
            sb.append('/');
            if (identity.getType() != null) {
                sb.append(identity.getType());
            }
            sb.append('/');
            if (identity.getLanguage() != null) {
                sb.append(identity.getLanguage());
            }
            sb.append('/');
            if (identity.getName() != null) {
                sb.append(identity.getName());
            }
            sb.append('<');
        }

        // 4. Sort the supported service discovery features.
        // This is done by TreeSet.

        // 5. For each feature, append the feature to S, followed by the '<' character.
        for (String feature : features) {
            if (feature != null) {
                sb.append(feature);
            }
            sb.append('<');
        }

        // 6. If the service discovery information response includes XEP-0128 data forms, sort the forms by the FORM_TYPE (i.e., by the XML character data of the <value/> element).
        dataForms.sort(null);

        // 7. For each extended service discovery information form:
        for (DataForm dataForm : dataForms) {
            final List<DataForm.Field> fields = new ArrayList<>(dataForm.getFields());
            // 7.2. Sort the fields by the value of the "var" attribute.
            // This makes sure, that FORM_TYPE fields are always on zero position.
            fields.sort(null);

            if (!fields.isEmpty()) {

                // Also make sure, that we don't send an ill-formed verification string.
                // 3.6 If the response includes an extended service discovery information form where the FORM_TYPE field is not of type "hidden" or the form does not include a FORM_TYPE field, ignore the form but continue processing.
                if (!DataForm.FORM_TYPE.equals(fields.get(0).getVar()) || fields.get(0).getType() != DataForm.Field.Type.HIDDEN) {
                    // => Don't include this form in the verification string.
                    continue;
                }

                for (DataForm.Field field : fields) {
                    List<String> values = new ArrayList<>(field.getValues());
                    // 7.3. For each field other than FORM_TYPE:
                    if (!DataForm.FORM_TYPE.equals(field.getVar())) {
                        // 7.3.1. Append the value of the "var" attribute, followed by the '<' character.
                        sb.append(field.getVar()).append('<');

                        // 7.3.2. Sort values by the XML character data of the <value/> element.
                        values.sort((s, t1) -> Strings.compareUnsignedBytes(s, t1, StandardCharsets.UTF_8));
                    }
                    // 7.1. Append the XML character data of the FORM_TYPE field's <value/> element, followed by the '<' character.
                    // 7.3.3. For each <value/> element, append the XML character data, followed by the '<' character.
                    for (String value : values) {
                        sb.append(value).append('<');
                    }
                }
            }
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public final String createCapabilityHashNode(final Hashed hashed) {
        // The value of the 'node' attribute SHOULD be generated by concatenating the value of the caps 'node' attribute
        // (e.g., "http://code.google.com/p/exodus") as provided by the generating entity, the "#" character, and the value of the caps 'ver' attribute (e.g., "QgayPKawpkPSDYmwT/WM94uAlu0=") as provided by the generating entity.
        return node + '#' + Base64.getEncoder().encodeToString(hashed.getHashValue());
    }

    @Override
    public final String toString() {
        return "Caps: " + getVerificationString();
    }
}
