# XEP-0045: Multi-User Chat
---

For all MUC-related operations, you will need the `MultiUserChatManager`, which you can get like this:

```java
MultiUserChatManager multiUserChatManager = xmppClient.getManager(MultiUserChatManager.class);
```

## Chat Services

If you don't have an idea, which chat services your XMPP domain hosts, you can easily discover them:

```java
Collection<ChatService> chatServices = multiUserChatManager.discoverChatServices().getResult();
```

If you already know the address of your chat service, you can also create an instance of the `ChatService` directly:

```java
ChatService chatService = multiUserChatManager.createChatService(Jid.of("conference.yourxmppdomain"));
```

A chat service allows you to do two things:

First, discovering public chat rooms hosted by this service:

```java
List<ChatRoom> publicRooms = chatService.discoverRooms().getResult();
```

or second - if you know an existing room or want to create a new one - creating a new chat room directly:

```java
ChatRoom chatRoom = chatService.createRoom("myroom");
```

whose room address would basically be `myroom@conference.yourxmppdomain`.

This can also be used, if you want to create instant chat rooms (e.g. with an UUID).

Note, that no room is created on the server! All you have now is a local `ChatRoom` instance, you can work with.


## Chat Rooms

Once you have a `ChatRoom` instance, you can now do multiple things with it:

* Discovering occupants in the room and room information.
* Entering (aka joining) and exiting (aka leaving) the room.
* Configuring a room.
* Sending messages.
* Listening to messages, subject changes, occupant events ("leaves" and "joins"), ...

### Discovering Occupants and Room Info

You can discover the occupants, which are currently in the room (nicknames only) with:

```java
List<String> occupants = chatRoom.discoverOccupants().getResult();
```

And you get additional room info (e.g. the current subject, the max history messages, the description and room features) with:

```java
RoomInformation roomInfo = chatRoom.getRoomInformation().getResult();
```

### Occupant Use Cases

These are the use cases, when you are *in* the room (or want to enter the room).

#### Entering a Room

Before entering a room, you should add listeners to it, if you want to listen for occupants "joins" and "leaves", subject changes or messages being sent by the room, then enter the room with your desired nickname:

```java
chatRoom.addOccupantListener(e -> {
    if (!e.getOccupant().isSelf()) {
        switch (e.getType()) {
            case ENTERED:
                System.out.println(e.getOccupant().getNick() + " has entered the room.");
                break;
            case EXITED:
                System.out.println(e.getOccupant().getNick() + " has exited the room.");
                break;
            case KICKED:
                System.out.println(e.getOccupant().getNick() + " has been kicked out of the room.");
                break;
        }
    }
});

chatRoom.addInboundMessageListener(e -> {
    Message message = e.getMessage();
    System.out.println(message.getFrom().getResource() + ": " + message.getBody());
});

chatRoom.addSubjectChangeListener(e -> {
    System.out.println(e.getNickname() + " changed the subject to '" + e.getSubject() + "'.");
});

chatRoom.enter("nickname");
```

You can also request history when entering, e.g.:

```java
chatRoom.enter("nickname", History.forMaxMessages(20));
```

#### Sending Messages

```java
chatRoom.sendMessage("Hi all!");
```

#### Changing Nickname

```java
chatRoom.changeNickname("newNickname");
```

#### Inviting Other Users

```java
chatRoom.invite(Jid.of("romeo@example.net"), "Hey, please join the room");
```

#### Requesting Voice

If you are a mere visitor to a moderated room, you can request voice:

```java
chatRoom.requestVoice();
```

#### Exiting a Room

In order to exit the room, simply call:

```java
chatRoom.exit();
```

Optionally you can also provide a presence status exit message:

```java
chatRoom.exit("Bye!");
```

## Admin, Owner and Moderator Use Cases

As admin, owner or moderator you have elevated privileges in the room, including banning users, kicking occupants, change room subject, change room configuration, etc.

### Modifying the Room Subject

```java
chatRoom.changeSubject("New subject");
```

### Changing Roles and Affiliations

If you have enough privileges in the room, you can ban users, kick occupants, grant moderator status, revoke voice, etc...

These use cases are all covered by two methods: `changeRole` and `changeAffiliation`. Here are some examples:

#### Granting Moderator Status

```java
chatRoom.changeRole(Role.MODERATOR, "nick", "You are now a moderator!");
```

#### Banning a User

If you are allowed (you need to be admin or owner), you can ban a user based on his JID:

```java
chatRoom.changeAffiliation(Affiliation.OUTCAST, Jid.of("juliet@example.net"), "You are banned!");
```

#### Kicking an Occupant

If you are a moderator in the room, you can kick other occupants (based on their nickname):

```java
chatRoom.changeRole(Role.NONE, "nick", "You are kicked!");
```

### Destroying a Room

If you are owner, you can destroy the room.

```java
chatRoom.destroy("Macbeth doth come.")
```

## Managing Room Invitations

### Listening to Room Invitations

`MultiUserChatManager` listens for both kinds of invitations: [mediated][Mediated] and [direct][Direct].
You can listen for them in the following way:

```java
multiUserChatManager.addInvitationListener(e -> {
    // e.getInviter() has invited you to the MUC room e.getRoomAddress()
});
```

### Accepting an Invitation

You get the MUC with ```e.getRoomAddress()``` of the event object. A room is in the form ```room@chatservice```, so we have to create the service and then the room at that service:

```java
ChatService chatService = multiUserChatManager.createChatService(new Jid(e.getRoomAddress().getDomain()));
ChatRoom chatRoom = chatService.createRoom(e.getRoomAddress().getLocal());
```

When you've created the room, you should add listeners to it and then enter it.

### Declining an Invitation

```java
e.decline("I don't have time right now...");
```

#### Listening for Invitation Declines

```java
chatRoom.addInvitationDeclineListener(e -> {
    // e.getInvitee() declined your invitation.
});
```

[MUC]: https://xmpp.org/extensions/xep-0045.html "XEP-0045: Multi-User Chat"
[Mediated]: https://xmpp.org/extensions/xep-0045.html#invite-mediated
[Direct]: https://xmpp.org/extensions/xep-0249.html
