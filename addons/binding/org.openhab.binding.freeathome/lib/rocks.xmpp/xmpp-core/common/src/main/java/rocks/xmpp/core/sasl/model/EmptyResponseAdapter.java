package rocks.xmpp.core.sasl.model;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Converts a zero-length byte array to a single equals sign ("=") instead to an empty string (as the default Base64 would treat it).
 * <p>
 * An equals sign is converted to a zero-length byte array.
 * <p>
 * This is a special behavior for SASL authentication as per the XMPP specification:
 * <blockquote>
 * <p>If the initiating entity needs to send a zero-length initial response, it MUST transmit the response as a single equals sign character ("="), which indicates that the response is present but contains no data.</p>
 * <p>If the receiving entity needs to send additional data of zero length, it MUST transmit the data as a single equals sign character ("=").</p>
 * </blockquote>
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/rfcs/rfc6120.html#sasl-process-neg-initiate">6.4.2.  Initiation</a>
 * @see <a href="https://xmpp.org/rfcs/rfc6120.html#sasl-process-neg-success">6.4.6.  SASL Success</a>
 */
final class EmptyResponseAdapter extends XmlAdapter<String, byte[]> {

    @Override
    public final byte[] unmarshal(final String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        return DatatypeConverter.parseBase64Binary(s.equals("=") ? "" : s);
    }

    @Override
    public final String marshal(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        if (bytes.length == 0) {
            return "=";
        }
        return DatatypeConverter.printBase64Binary(bytes);
    }
}
