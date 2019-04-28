# JPMS Support: Running Babbler on Java 9 and later
---

## Preconditions

JPMS (coll. "Java 9" or "Jigsaw") is supported since Babbler 0.8.0. **Older versions will not run on JPMS.**

## Usage

Applications compiled for Java 9 and later need to explicitly request access to Babbler modules in their `module-info.java` descriptor file. Babbler's module names are:

### Basics (Mandatory)

```
requires rocks.xmpp.addr;
requires rocks.xmpp.core;
requires rocks.xmpp.core.client;
requires rocks.xmpp.extensions;
requires rocks.xmpp.extensions.client;
```

### Debugging (Optional)

```
requires rocks.xmpp.debug;
```

### JavaFX (Optional)

```
requires rocks.xmpp.fx;
```

### WebSocket (Optional)
 
```
requires rocks.xmpp.websocket;
```

### Non-Blocking I/O (Optional)

```
requires rocks.xmpp.nio;
```
 
### Samples (Optional)

```
requires rocks.xmpp.sample;
```
