# FreeAtHome Binding
This binding integrates with [B+J FreeAtHome](https://www.busch-jaeger.de/produkte/systeme/busch-free-at-home/) smarthome system by connecting to the SysAP.

**Disclaimer:** This binding is a private contribution and is not an official B+J product.

**Issues and feature requests:** 
If you find any issue please submit new issues or feature requests on my private github repo [ruebox@github:issues](https://github.com/ruebox/openhab2-addons/issues) until the binding is merge to offical openhab distribution.
### Supported things

Type | Description
------------ | -------------
Bridge | Gateway that connects to the B+J SysAP. All commands from the things are routed through this bridge. The bridge has to added manually before starting discovery.
Raffstore switch | Switch to run blinds or raffstore shutters stepwise or completely.
Scenario selector | This thing can be used to provide user switches to configure different scenarios e.g. @Home. The status of these switches can be used by rules to setup different scenarios.
Scene | Switch that activates a scene that was generated within freeathome webui. The switch is autoresetted after a configurable timeout.
Switch | Binary switch or switch group.
Thermostat | Thermostat that can be switch on/off, eco mode on/off or to set the target temperature. Feedback such as current room temperature is not supported (yet).

**Constraint:** When an action is triggered via openhab, the command is forwarded to freeathome. Till now, no feedback is reflected to openhab. I.e. if the thermostat is switched of in the device itself, the openhab item is not updated (to be realized in the future).

### How to use the binding
#### Install binding
1. Download latest release as jar file from [ruebox@github:releases](https://github.com/ruebox/openhab2-addons/releases) e.g. pre-release v1.0.1-alpha3.
1. Copy org.openhab.binding.freeathome-2.0.0-SNAPSHOT.jar to your addons directory of your openhab distribution: openhab2/addons
2. Goto http://localhost:8080/paperui/index.html#/configuration/bindings
3. The B+J FreeAtHome Binding should be visible
![Binding overview with FreeAtHome Binding](./doc/BindingOverview.png)

#### Add bridge
1. **Inbox** -> Go to the inbox
1. **+** -> B+J FreeAtHome Binding
1. **Add manually** -> FreeAtHome Bridge (Before bridge is not added, discovery will not find items)
![Add thing manually](./doc/AddBridge.png)
1. **Configure Bridge**
   - Login name: Login name from SysAP webfrontend e.g. Installer (take care for lower and upper letters)
   - Password: :-)
   - LAN gateway IP: IP adress of SysAP
-> Accept
 ![Bridge configuration](./doc/ConfigureBridge.png)
1. Configuration -> Things: Bridge shall appear online: ![Online bridge](./doc/BridgeOnline.png) 

#### Discover and add items
1. **Inbox** -> Goto inbox
1. **Seach for things**
1. **FreeAtHomeBinding**
1. *Be patient*
   -> Set of things are discovered

#### Use via control panel


