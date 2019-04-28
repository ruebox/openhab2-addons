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
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.extensions.muc.model.Affiliation;
import rocks.xmpp.extensions.muc.model.Role;
import rocks.xmpp.extensions.muc.model.user.MucUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Christian Schudt
 */
public class OccupantTest {

    @Test
    public void testComparable() {
        Presence presence1 = new Presence();
        presence1.setFrom(Jid.of("room@conference/firstwitch"));
        presence1.addExtension(MucUser.withItem(Affiliation.OWNER, Role.MODERATOR));
        Occupant occupant1 = new Occupant(presence1, true);

        Presence presence2 = new Presence();
        presence2.setFrom(Jid.of("room@conference/secondwitch"));
        presence2.addExtension(MucUser.withItem(Affiliation.OWNER, Role.PARTICIPANT));
        Occupant occupant2 = new Occupant(presence2, true);

        Presence presence2a = new Presence(Presence.Show.AWAY);
        presence2a.setFrom(Jid.of("room@conference/thirdwitch"));
        presence2a.addExtension(MucUser.withItem(Affiliation.OWNER, Role.VISITOR));
        Occupant occupant2a = new Occupant(presence2a, true);

        Presence presence2b = new Presence();
        presence2b.setFrom(Jid.of("room@conference/zzz"));
        presence2b.addExtension(MucUser.withItem(Affiliation.OWNER, Role.VISITOR));
        Occupant occupant2b = new Occupant(presence2b, true);

        Presence presence3 = new Presence();
        presence3.setFrom(Jid.of("room@conference/aaa"));
        presence3.addExtension(MucUser.withItem(Affiliation.ADMIN, Role.MODERATOR));
        Occupant occupant3 = new Occupant(presence3, true);

        Presence presence4 = new Presence();
        presence4.setFrom(Jid.of("room@conference/bbb"));
        presence4.addExtension(MucUser.withItem(Affiliation.MEMBER, Role.PARTICIPANT));
        Occupant occupant4 = new Occupant(presence4, true);

        Presence presence5 = new Presence();
        presence5.setFrom(Jid.of("room@conference/bbb"));
        presence5.addExtension(MucUser.withItem(Affiliation.OUTCAST, Role.PARTICIPANT));
        Occupant occupant5 = new Occupant(presence5, true);

        Presence presence5a = new Presence();
        presence5a.setFrom(Jid.of("room@conference/ccc"));
        presence5a.addExtension(MucUser.withItem(Affiliation.OUTCAST, Role.PARTICIPANT));
        Occupant occupant5a = new Occupant(presence5a, true);

        Presence presence6 = new Presence();
        presence6.setFrom(Jid.of("room@conference/bbb"));
        presence6.addExtension(MucUser.withItem(Affiliation.NONE, Role.PARTICIPANT));
        Occupant occupant6 = new Occupant(presence6, true);

        List<Occupant> occupants = new ArrayList<>();
        occupants.add(occupant1);
        occupants.add(occupant2);
        occupants.add(occupant2a);
        occupants.add(occupant2b);
        occupants.add(occupant3);
        occupants.add(occupant4);
        occupants.add(occupant5);
        occupants.add(occupant5a);
        occupants.add(occupant6);

        Collections.shuffle(occupants);
        occupants.sort(null);

        Iterator<Occupant> iterator = occupants.iterator();

        Assert.assertEquals(iterator.next(), occupant1);
        Assert.assertEquals(iterator.next(), occupant2);
        Assert.assertEquals(iterator.next(), occupant2b);
        Assert.assertEquals(iterator.next(), occupant2a);
        Assert.assertEquals(iterator.next(), occupant3);
        Assert.assertEquals(iterator.next(), occupant4);
        Assert.assertEquals(iterator.next(), occupant6);
        Assert.assertEquals(iterator.next(), occupant5);
        Assert.assertEquals(iterator.next(), occupant5a);

    }
}
