# FreeAtHome Binding
This binding integrates with [B+J FreeAtHome](https://www.busch-jaeger.de/produkte/systeme/busch-free-at-home/) smarthome system by connecting to the SysAP.

**Disclaimer:** This binding is a private contribution and is not an official B+J product.

**Issues and feature requests:** 
If you find any issue please submit new issues or feature requests on my private github repo [ruebox@github:issues](https://github.com/ruebox/openhab2-addons/issues) until the binding is merged to offical openhab distribution.
### Supported things
Type | Description
------------ | -------------
Bridge | Gateway that connects to the B+J SysAP. All commands from the things are routed through this bridge. The bridge has to added manually before starting discovery (fw version >= 2.2.4).
Raffstore switch | Switch to run blinds or raffstore shutters stepwise or completely.
Scenario selector | This thing can be used to provide user switches to configure different scenarios e.g. @Home. The status of these switches can be used by rules to setup different scenarios.
Scene | Switch that activates a scene that was generated within freeathome webui. The switch is autoresetted after a configurable timeout.
Switch | Binary switch or switch group.
Dimmer | Dimmer supporting switch on/off, fading, set percentage directly
Thermostat | Thermostat that can be switch on/off, eco mode on/off or to set the target temperature.
Weather station | Weather station providing wind speed, temperature, illumination.



### How to use the binding
#### Install via Eclipse marketplace: [FreeAtHome](https://marketplace.eclipse.org/content/freeathome)
1. Activate Eclipse Marketplace in openhab with Majurity Level: Alpha
![Activate Eclipse Marketplace in openhab with Majurity Level: Alpha](./doc/ActivateMarketPlace.png)
2. Install binding: Addons -> Bindings -> FreeAtHome -> Install
![Install FreeAtHome Binding](./doc/InstallViaMarketPlace.png)

#### Manual installation: 
1. Download latest release as jar file from [ruebox@github:releases](https://github.com/ruebox/openhab2-addons/releases) 
1. Copy org.openhab.binding.freeathome-SNAPSHOT.jar to your addons directory of your openhab distribution: openhab2/addons
2. Goto http://localhost:8080/paperui/index.html#/configuration/bindings
3. The B+J FreeAtHome Binding should be visible
![Binding overview with FreeAtHome Binding](./doc/BindingOverview.png)

#### Add bridge
Before you can discover any things, the FreeAtHome bridge has to be manually added.

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
