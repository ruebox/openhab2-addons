# Free@Home Binding
This binding integrates with [Busch-Jaeger Free@Home](https://www.busch-jaeger.de/produkte/systeme/busch-free-at-home/) smarthome system by connecting to the SysAP.

**Disclaimer:** This binding is a private contribution and is not an official B+J product.

**Issues and feature requests:** 

### Supported things
Type | Description
------------ | -------------
Bridge | Gateway that connects to the Busch-Jaeger SysAP. All commands from the things are routed through this bridge. The bridge has to added manually before starting discovery.
Raffstore switch | Switch to run blinds or raffstore shutters stepwise or completely.
Scenario selector | This thing can be used to provide user switches to configure different scenarios e.g. @Home. The status of these switches can be used by rules to setup different scenarios.
Scene | Switch that activates a scene that was generated within Free@Hqome webui. The switch is autoresetted after a configurable timeout.
Switch | Binary switch or switch group.
Dimmer | Dimmer supporting switch on/off, fading, set percentage directly
Thermostat | Thermostat that can be switch on/off, eco mode on/off or to set the target temperature.
Weather station | Weather station providing wind speed, temperature, illumination.



### How to use the binding
#### Install via Eclipse marketplace: [Free@Home](https://marketplace.eclipse.org/content/freeathome)
1. Activate Eclipse Marketplace in openhab with Majurity Level: Alpha
![Activate Eclipse Marketplace in openhab with Majurity Level: Alpha](./doc/ActivateMarketPlace.png)
2. Install binding: Addons -> Bindings -> Free@Home -> Install
![Install Free@Home Binding](./doc/InstallViaMarketPlace.png)

#### Manual installation: 
1. Download latest release as jar file from [ruebox@github:releases](https://github.com/ruebox/openhab2-addons/releases) 
1. Copy org.openhab.binding.freeathome-SNAPSHOT.jar to your addons directory of your openhab distribution: openhab2/addons
2. Goto http://localhost:8080/paperui/index.html#/configuration/bindings
3. The Busch-Jaeger Free@Home Binding should be visible
![Binding overview with Free@Home Binding](./doc/BindingOverview.png)

#### Add bridge
Before you can discover any things, the Free@Home bridge has to be manually added.

1. **Inbox** -> Go to the inbox
1. **+** -> Busch-Jaeger Free@Home Binding
1. **Add manually** -> Free@Home Bridge (Before bridge is not added, discovery will not find items)
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
1. **Free@HomeBinding**
1. *Be patient*
   -> Set of things are discovered
