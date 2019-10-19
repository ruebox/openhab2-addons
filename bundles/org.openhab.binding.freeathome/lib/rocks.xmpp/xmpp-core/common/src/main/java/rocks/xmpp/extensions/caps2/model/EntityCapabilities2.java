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

package rocks.xmpp.extensions.caps2.model;

import rocks.xmpp.extensions.caps.model.EntityCapabilities;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.disco.model.info.Identity;
import rocks.xmpp.extensions.disco.model.info.InfoNode;
import rocks.xmpp.extensions.hashes.model.Hash;
import rocks.xmpp.extensions.hashes.model.Hashed;
import rocks.xmpp.util.Strings;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The implementation of the {@code <c/>} element in the {@code urn:xmpp:caps} namespace.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0390.html">XEP-0390: Entity Capabilities 2.0</a>
 */
@XmlRootElement(name = "c")
public final class EntityCapabilities2 implements EntityCapabilities {

    /**
     * urn:xmpp:caps
     */
    public static final String NAMESPACE = "urn:xmpp:caps";

    @XmlElementRef
    private final Set<Hash> hashes = new LinkedHashSet<>();

    private EntityCapabilities2() {
    }

    public EntityCapabilities2(final InfoNode infoNode, final MessageDigest... messageDigest) {
        if (messageDigest.length == 0) {
            throw new IllegalArgumentException("At least one hash function must be provided.");
        }
        final byte[] verificationString = createVerificationString(infoNode);
        for (MessageDigest digest : messageDigest) {
            hashes.add(new Hash(digest.digest(verificationString), digest.getAlgorithm()));
        }
    }

    @Override
    public final Set<Hashed> getCapabilityHashSet() {
        return Collections.unmodifiableSet(hashes);
    }

    @Override
    public final byte[] createVerificationString(final InfoNode infoNode) {

        final StringBuilder sb = new StringBuilder();

        for (String feature : infoNode.getFeatures()) {
            // 4.1. For each <feature/> element: Encode the character data of the 'var' attribute
            // and append an octet of value 0x1f (ASCII Unit Separator)
            // 4.2. Join the resulting octet strings together, ordered from lesser to greater.
            // 4.3. Append an octet of value 0x1c (ASCII File Separator).
            sb.append(feature).appendCodePoint('\u001F');
        }
        // 4.3. Append an octet of value 0x1c (ASCII File Separator).
        sb.appendCodePoint('\u001C');

        // 5.1. For each <identity/> node:
        for (Identity identity : infoNode.getIdentities()) {
            // 5.1.1. Encode the character data of the 'category', 'type', 'xml:lang' and 'name' attributes.
            if (identity.getCategory() != null) {
                sb.append(identity.getCategory());
            }
            sb.appendCodePoint('\u001F');
            if (identity.getType() != null) {
                sb.append(identity.getType());
            }
            sb.appendCodePoint('\u001F');
            if (identity.getLanguage() != null) {
                sb.append(identity.getLanguage().toLanguageTag());
            }
            sb.appendCodePoint('\u001F');
            if (identity.getName() != null) {
                sb.append(identity.getName());
            }
            // 5.1.2. Append an octet of value 0x1f (ASCII Unit Separator) to each resulting octet string.
            sb.appendCodePoint('\u001F');
            // 5.1.3. Join the resulting octet strings together, in the order of 'category', 'type', 'xml:lang' and 'name', resulting in a single octet string for the <identity/> node.
            // 5.1.4. Append an octet of value 0x1e (ASCII Record Separator).
            sb.appendCodePoint('\u001E');
        }
        // 5.2. Join the resulting octet strings together, ordered from lesser to greater.
        // 5.3. Append an octet of value 0x1c (ASCII File Separator).
        sb.appendCodePoint('\u001C');

        // 6.1. For each <x/> element
        for (DataForm dataForm : infoNode.getExtensions()) {
            List<DataForm.Field> fields = new ArrayList<>(dataForm.getFields());
            fields.sort(null);

            // 6.1.1. For each <field/> element
            for (DataForm.Field field : fields) {

                // 6.1.1.3. Encode the character data of the 'var' attribute
                if (field.getVar() != null) {
                    sb.append(field.getVar());
                }
                // and append an octet of value 0x1f (ASCII Unit Separator) and the result from the previous step.
                sb.appendCodePoint('\u001F');

                // 6.1.1.1 Encode the character data of each <value/> element
                if (field.getValue() != null) {
                    List<String> values = new ArrayList<>(field.getValues());
                    values.sort((s, t1) -> Strings.compareUnsignedBytes(s, t1, StandardCharsets.UTF_8));
                    for (String value : values) {
                        sb.append(value);
                        // and append an octet of value 0x1f (ASCII Unit Separator).
                        sb.appendCodePoint('\u001F');
                    }
                }

                // 6.1.1.4. Append an octet of value 0x1e (ASCII Record Separator).
                sb.appendCodePoint('\u001E');
            }
            // 6.1.3. Append an octet of value 0x1d (ASCII Group Separator).
            sb.appendCodePoint('\u001D');
        }

        // 6.3 Append an octet of value 0x1c (ASCII File Separator).
        sb.appendCodePoint('\u001C');
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public final String createCapabilityHashNode(final Hashed hashed) {
        // To the namespace prefix "urn:xmpp:caps#", append the name of the hash function as per Use of Cryptographic Hash Functions in XMPP (XEP-0300) [9].
        // Append a FULL STOP character (U+002E, ".").
        // Append the Base64 encoded (as specified in RFC 3548 [14]) hash value.
        return NAMESPACE + '#' + hashed.getHashAlgorithm() + '.' + Base64.getEncoder().encodeToString(hashed.getHashValue());
    }

    @Override
    public final String toString() {
        return "Caps 2.0: " + hashes;
    }
}
