package rocks.xmpp.extensions.hashes.model;

/**
 * Represents a hashed object. It consists a hash algorithm and a corresponding hash value.
 *
 * @author Christian Schudt
 */
public interface Hashed {

    /**
     * Gets the hash algorithm.
     *
     * @return The hash algorithm.
     */
    String getHashAlgorithm();

    /**
     * Gets the hash value.
     *
     * @return The hash value.
     */
    byte[] getHashValue();
}
