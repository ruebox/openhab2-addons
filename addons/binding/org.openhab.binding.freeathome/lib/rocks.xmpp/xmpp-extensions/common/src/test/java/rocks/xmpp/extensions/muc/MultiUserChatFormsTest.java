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

package rocks.xmpp.extensions.muc;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.muc.model.RequestVoice;
import rocks.xmpp.extensions.muc.model.Role;
import rocks.xmpp.extensions.muc.model.RoomConfiguration;
import rocks.xmpp.extensions.muc.model.RoomInfo;
import rocks.xmpp.extensions.muc.model.RoomRegistration;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;

/**
 * @author Christian Schudt
 */
public class MultiUserChatFormsTest extends XmlTest {
    protected MultiUserChatFormsTest() throws JAXBException {
        super(DataForm.class);
    }

    @Test
    public void testRequestVoiceForm() throws JAXBException, XMLStreamException {

        RequestVoice requestVoice = RequestVoice.builder()
                .jid(Jid.of("hag66@shakespeare.lit/pda"))
                .role(Role.MODERATOR)
                .roomNick("thirdwitch")
                .allowRequest(true)
                .build();

        String xml = marshal(requestVoice.getDataForm());
        Assert.assertEquals(xml, "<x xmlns=\"jabber:x:data\" type=\"submit\">" +
                "<field type=\"hidden\" var=\"FORM_TYPE\"><value>http://jabber.org/protocol/muc#request</value></field>" +
                "<field type=\"text-single\" var=\"muc#role\"><value>moderator</value></field>" +
                "<field type=\"jid-single\" var=\"muc#jid\"><value>hag66@shakespeare.lit/pda</value></field>" +
                "<field type=\"text-single\" var=\"muc#roomnick\"><value>thirdwitch</value></field>" +
                "<field type=\"boolean\" var=\"muc#request_allow\"><value>1</value></field>" +
                "</x>");

        DataForm dataForm = unmarshal(xml, DataForm.class);
        RequestVoice requestVoice1 = new RequestVoice(dataForm);

        Assert.assertEquals(requestVoice1.getRoomNick(), "thirdwitch");
        Assert.assertEquals(requestVoice1.getRole(), Role.MODERATOR);
        Assert.assertEquals(requestVoice1.getJid(), Jid.of("hag66@shakespeare.lit/pda"));
        Assert.assertTrue(requestVoice1.isRequestAllowed());
    }

    @Test
    public void testRoomConfigurationForm() throws JAXBException, XMLStreamException {

        RoomConfiguration roomConfiguration = RoomConfiguration.builder()
                .maxHistoryMessages(4)
                .rolesThatMaySendPrivateMessages(EnumSet.of(Role.MODERATOR, Role.PARTICIPANT, Role.VISITOR))
                .invitesAllowed(true)
                .changeSubjectAllowed(true)
                .loggingEnabled(true)
                .rolesThatMayRetrieveMemberList(EnumSet.of(Role.PARTICIPANT))
                .language(Locale.ENGLISH)
                .pubSubNode(URI.create("xmpp:pubsub.shakespeare.lit?;node=princely_musings"))
                .maxUsers(30)
                .membersOnly(true)
                .moderated(true)
                .passwordProtected(true)
                .persistent(true)
                .rolesForWhichPresenceIsBroadcast(EnumSet.of(Role.MODERATOR, Role.PARTICIPANT))
                .publicRoom(true)
                .administrators(Arrays.asList(Jid.of("admin1"), Jid.of("admin2")))
                .description("description")
                .name("name")
                .owners(Arrays.asList(Jid.of("owner1"), Jid.of("owner2")))
                .password("pass")
                .rolesThatMayDiscoverRealJids(EnumSet.of(Role.MODERATOR))
                .build();

        String xml = marshal(roomConfiguration.getDataForm());

        Assert.assertEquals(xml, "<x xmlns=\"jabber:x:data\" type=\"submit\">" +
                "<field type=\"hidden\" var=\"FORM_TYPE\"><value>http://jabber.org/protocol/muc#roomconfig</value></field>" +
                "<field type=\"text-single\" var=\"muc#maxhistoryfetch\"><value>4</value></field>" +
                "<field type=\"list-single\" var=\"muc#roomconfig_allowpm\"><value>anyone</value></field>" +
                "<field type=\"boolean\" var=\"muc#roomconfig_allowinvites\"><value>1</value></field>" +
                "<field type=\"boolean\" var=\"muc#roomconfig_changesubject\"><value>1</value></field>" +
                "<field type=\"boolean\" var=\"muc#roomconfig_enablelogging\"><value>1</value></field>" +
                "<field type=\"list-multi\" var=\"muc#roomconfig_getmemberlist\"><value>participant</value></field>" +
                "<field type=\"text-single\" var=\"muc#roomconfig_lang\"><value>en</value></field>" +
                "<field type=\"text-single\" var=\"muc#roomconfig_pubsub\"><value>xmpp:pubsub.shakespeare.lit?;node=princely_musings</value></field>" +
                "<field type=\"list-single\" var=\"muc#roomconfig_maxusers\"><value>30</value></field>" +
                "<field type=\"boolean\" var=\"muc#roomconfig_membersonly\"><value>1</value></field>" +
                "<field type=\"boolean\" var=\"muc#roomconfig_moderatedroom\"><value>1</value></field>" +
                "<field type=\"boolean\" var=\"muc#roomconfig_passwordprotectedroom\"><value>1</value></field>" +
                "<field type=\"boolean\" var=\"muc#roomconfig_persistentroom\"><value>1</value></field>" +
                "<field type=\"list-multi\" var=\"muc#roomconfig_presencebroadcast\"><value>moderator</value><value>participant</value></field>" +
                "<field type=\"boolean\" var=\"muc#roomconfig_publicroom\"><value>1</value></field>" +
                "<field type=\"jid-multi\" var=\"muc#roomconfig_roomadmins\"><value>admin1</value><value>admin2</value></field>" +
                "<field type=\"text-single\" var=\"muc#roomconfig_roomdesc\"><value>description</value></field>" +
                "<field type=\"text-single\" var=\"muc#roomconfig_roomname\"><value>name</value></field>" +
                "<field type=\"jid-multi\" var=\"muc#roomconfig_roomowners\"><value>owner1</value><value>owner2</value></field>" +
                "<field type=\"text-single\" var=\"muc#roomconfig_roomsecret\"><value>pass</value></field>" +
                "<field type=\"list-single\" var=\"muc#roomconfig_whois\"><value>moderators</value></field>" +
                "</x>");


        DataForm dataForm = unmarshal(xml, DataForm.class);

        RoomConfiguration roomConfiguration1 = new RoomConfiguration(dataForm);

        Assert.assertEquals(roomConfiguration1.getMaxHistoryMessages(), Integer.valueOf(4));
        Assert.assertEquals(roomConfiguration1.getRolesThatMaySendPrivateMessages(), EnumSet.of(Role.MODERATOR, Role.PARTICIPANT, Role.VISITOR));
        Assert.assertTrue(roomConfiguration1.isInvitesAllowed());
        Assert.assertTrue(roomConfiguration1.isChangeSubjectAllowed());
        Assert.assertTrue(roomConfiguration1.isLoggingEnabled());
        Assert.assertEquals(roomConfiguration1.getRolesThatMayRetrieveMemberList(), Collections.singleton(Role.PARTICIPANT));
        Assert.assertEquals(roomConfiguration1.getLanguage(), Locale.ENGLISH);
        Assert.assertEquals(roomConfiguration1.getPubSubNode(), URI.create("xmpp:pubsub.shakespeare.lit?;node=princely_musings"));
        Assert.assertEquals(roomConfiguration1.getMaxUsers(), Integer.valueOf(30));
        Assert.assertTrue(roomConfiguration1.isMembersOnly());
        Assert.assertTrue(roomConfiguration1.isModerated());
        Assert.assertTrue(roomConfiguration1.isPasswordProtected());
        Assert.assertTrue(roomConfiguration1.isPersistent());
        Assert.assertEquals(roomConfiguration1.getRolesForWhichPresenceIsBroadcast(), Arrays.asList(Role.MODERATOR, Role.PARTICIPANT));
        Assert.assertTrue(roomConfiguration1.isPublicRoom());
        Assert.assertEquals(roomConfiguration1.getAdministrators(), Arrays.asList(Jid.of("admin1"), Jid.of("admin2")));
        Assert.assertEquals(roomConfiguration1.getDescription(), "description");
        Assert.assertEquals(roomConfiguration1.getName(), "name");
        Assert.assertEquals(roomConfiguration1.getOwners(), Arrays.asList(Jid.of("owner1"), Jid.of("owner2")));
        Assert.assertEquals(roomConfiguration1.getPassword(), "pass");
        Assert.assertEquals(roomConfiguration1.getRolesThatMayDiscoverRealJids(), Collections.singleton(Role.MODERATOR));
    }

    @Test
    public void testRoomRegistrationForm() throws JAXBException, XMLStreamException, MalformedURLException {

        RoomRegistration roomRegistration = RoomRegistration.builder()
                .allowRegister(true)
                .email("hag66@witchesonline")
                .familyName("Entwhistle-Throckmorton")
                .givenName("Brunhilde")
                .faqEntry("Just another witch.")
                .nickname("thirdwitch")
                .webPage(new URL("http://witchesonline/~hag66/"))
                .build();

        String xml = marshal(roomRegistration.getDataForm());

        Assert.assertEquals(xml, "<x xmlns=\"jabber:x:data\" type=\"submit\">" +
                "<field type=\"hidden\" var=\"FORM_TYPE\"><value>http://jabber.org/protocol/muc#register</value></field>" +
                "<field type=\"boolean\" var=\"muc#register_allow\"><value>1</value></field>" +
                "<field type=\"text-single\" var=\"muc#register_email\"><value>hag66@witchesonline</value></field>" +
                "<field type=\"text-multi\" var=\"muc#register_faqentry\"><value>Just another witch.</value></field>" +
                "<field type=\"text-single\" var=\"muc#register_first\"><value>Brunhilde</value></field>" +
                "<field type=\"text-single\" var=\"muc#register_last\"><value>Entwhistle-Throckmorton</value></field>" +
                "<field type=\"text-single\" var=\"muc#register_roomnick\"><value>thirdwitch</value></field>" +
                "<field type=\"text-single\" var=\"muc#register_url\"><value>http://witchesonline/~hag66/</value></field>" +
                "</x>");

        DataForm dataForm = unmarshal(xml, DataForm.class);

        RoomRegistration roomRegistration2 = new RoomRegistration(dataForm);
        Assert.assertEquals(roomRegistration2.getGivenName(), "Brunhilde");
        Assert.assertEquals(roomRegistration2.getFamilyName(), "Entwhistle-Throckmorton");
        Assert.assertEquals(roomRegistration2.getRoomNick(), "thirdwitch");
        Assert.assertEquals(roomRegistration2.getWebPage().toString(), "http://witchesonline/~hag66/");
        Assert.assertEquals(roomRegistration2.getEmail(), "hag66@witchesonline");
        Assert.assertEquals(roomRegistration2.getFaqEntry(), "Just another witch.");
        Assert.assertTrue(roomRegistration2.isRegisterAllowed());
    }

    @Test
    public void testRoomInfoForm() throws JAXBException, XMLStreamException, MalformedURLException {

        RoomInfo roomInfo = RoomInfo.builder()
                .maxHistoryMessages(50)
                .contacts(Arrays.asList(Jid.of("contact1"), Jid.of("contact2")))
                .description("The place for all good witches!")
                .language(Locale.ENGLISH)
                .ldapGroup("cn=witches,dc=shakespeare,dc=lit")
                .logs(new URL("http://www.shakespeare.lit/chatlogs/coven/"))
                .currentNumberOfOccupants(45)
                .subject("Spells")
                .changeSubjectAllowed(true)
                .build();

        String xml = marshal(roomInfo.getDataForm());

        Assert.assertEquals(xml, "<x xmlns=\"jabber:x:data\" type=\"result\">" +
                "<field type=\"hidden\" var=\"FORM_TYPE\"><value>http://jabber.org/protocol/muc#roominfo</value></field>" +
                "<field type=\"text-single\" var=\"muc#maxhistoryfetch\"><value>50</value></field>" +
                "<field type=\"jid-multi\" var=\"muc#roominfo_contactjid\"><value>contact1</value><value>contact2</value></field>" +
                "<field type=\"text-single\" var=\"muc#roominfo_description\"><value>The place for all good witches!</value></field>" +
                "<field type=\"text-single\" var=\"muc#roominfo_lang\"><value>en</value></field>" +
                "<field type=\"text-single\" var=\"muc#roominfo_ldapgroup\"><value>cn=witches,dc=shakespeare,dc=lit</value></field>" +
                "<field type=\"text-single\" var=\"muc#roominfo_logs\"><value>http://www.shakespeare.lit/chatlogs/coven/</value></field>" +
                "<field type=\"text-single\" var=\"muc#roominfo_occupants\"><value>45</value></field>" +
                "<field type=\"text-single\" var=\"muc#roominfo_subject\"><value>Spells</value></field>" +
                "<field type=\"boolean\" var=\"muc#roominfo_subjectmod\"><value>1</value></field>" +
                "</x>");

        DataForm dataForm = unmarshal(xml, DataForm.class);

        RoomInfo roomInfo2 = new RoomInfo(dataForm);

        Assert.assertEquals(roomInfo2.getDescription(), "The place for all good witches!");
        Assert.assertTrue(roomInfo2.isChangeSubjectAllowed());
        Assert.assertEquals(roomInfo2.getContacts(), Arrays.asList(Jid.of("contact1"), Jid.of("contact2")));
        Assert.assertEquals(roomInfo2.getSubject(), "Spells");
        Assert.assertEquals(roomInfo2.getCurrentNumberOfOccupants(), Integer.valueOf(45));
        Assert.assertEquals(roomInfo2.getLdapGroup(), "cn=witches,dc=shakespeare,dc=lit");
        Assert.assertEquals(roomInfo2.getLanguage(), Locale.ENGLISH);
        Assert.assertEquals(roomInfo2.getLogs().toString(), "http://www.shakespeare.lit/chatlogs/coven/");
        Assert.assertEquals(roomInfo2.getMaxHistoryMessages(), Integer.valueOf(50));
    }
}
