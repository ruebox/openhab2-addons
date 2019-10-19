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

package rocks.xmpp.extensions.muc.model;

/**
 * A feature of a multi-user chat service.
 *
 * @author Christian Schudt
 */
public enum MucFeature {

    /**
     * Support for the muc#register FORM_TYPE
     */
    REGISTER("http://jabber.org/protocol/muc#register"),
    /**
     * Support for the muc#roomconfig FORM_TYPE
     */
    ROOMCONFIG("http://jabber.org/protocol/muc#roomconfig"),
    /**
     * Support for the muc#roominfo FORM_TYPE
     */
    ROOMINFO("http://jabber.org/protocol/muc#roominfo"),
    /**
     * Hidden room in Multi-User Chat (MUC)
     */
    HIDDEN("muc_hidden"),
    /**
     * Members-only room in Multi-User Chat (MUC)
     */
    MEMBERS_ONLY("muc_membersonly"),
    /**
     * Moderated room in Multi-User Chat (MUC)
     */
    MODERATED("muc_moderated"),
    /**
     * Non-anonymous room in Multi-User Chat (MUC)
     */
    NON_ANONYMOUS("muc_nonanonymous"),
    /**
     * Open room in Multi-User Chat (MUC)
     */
    OPEN("muc_open"),
    /**
     * Password-protected room in Multi-User Chat (MUC)
     */
    PASSWORD_PROTECTED("muc_passwordprotected"),
    /**
     * Persistent room in Multi-User Chat (MUC)
     */
    PERSISTENT("muc_persistent"),
    /**
     * Public room in Multi-User Chat (MUC)
     */
    PUBLIC("muc_public"),
    /**
     * List of MUC rooms (each as a separate item)
     */
    ROOMS("muc_rooms"),
    /**
     * Semi-anonymous room in Multi-User Chat (MUC)
     */
    SEMI_ANONYMOUS("muc_semianonymous"),
    /**
     * Temporary room in Multi-User Chat (MUC)
     */
    TEMPORARY("muc_temporary"),
    /**
     * Unmoderated room in Multi-User Chat (MUC)
     */
    UNMODERATED("muc_unmoderated"),
    /**
     * Unsecured room in Multi-User Chat (MUC)
     */
    UNSECURED("muc_unsecured");

    private final String feature;

    MucFeature(String feature) {
        this.feature = feature;
    }

    /**
     * Gets the name of the feature as it is used by service discovery.
     *
     * @return The feature name.
     */
    public String getServiceDiscoveryFeature() {
        return feature;
    }
}
