package rocks.xmpp.extensions.csi.model;

import rocks.xmpp.core.stream.model.StreamElement;
import rocks.xmpp.core.stream.model.StreamFeature;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a client state, i.e. active or inactive.
 * <p>
 * Because elements of Client State Indication are stateless, instances of this class are static singletons.
 * ```java
 * ClientState.ACTIVE;
 * ClientState.INACTIVE;
 * ClientState.FEATURE;
 * ```
 *
 * @author Christian Schudt
 * @see <a href="https://xmpp.org/extensions/xep-0352.html">XEP-0352: Client State Indication</a>
 * @see #ACTIVE
 * @see #INACTIVE
 * @see #FEATURE
 */
@XmlTransient
@XmlSeeAlso({ClientState.Active.class, ClientState.Inactive.class, ClientState.Csi.class})
public abstract class ClientState implements StreamElement {

    /**
     * The stream feature for servers to advertise support for client state indication.
     */
    public static final StreamFeature FEATURE = new Csi();

    /**
     * The active state.
     */
    public static final ClientState ACTIVE = new Active();

    /**
     * The inactive state.
     */
    public static final ClientState INACTIVE = new Inactive();

    private ClientState() {
    }

    /**
     * urn:xmpp:csi:0
     */
    public static final String NAMESPACE = "urn:xmpp:csi:0";

    @XmlRootElement
    @XmlType(factoryMethod = "create")
    static final class Csi extends StreamFeature {

        private Csi() {
        }

        private static Csi create() {
            return (Csi) ClientState.FEATURE;
        }
    }

    @XmlRootElement
    @XmlType(factoryMethod = "create")
    static final class Active extends ClientState {

        private Active() {
        }

        private static Active create() {
            return (Active) ClientState.ACTIVE;
        }
    }

    @XmlRootElement
    @XmlType(factoryMethod = "create")
    static final class Inactive extends ClientState {

        private Inactive() {
        }

        private static Inactive create() {
            return (Inactive) ClientState.INACTIVE;
        }
    }
}
