# Free@Home Binding
This binding integrates with [Busch-Jaeger Free@Home](https://www.busch-jaeger.de/produkte/systeme/busch-free-at-home/) smarthome system by connecting to the SysAP.

NOTE: Only suitable for SysAp version 2.2.4 and higher

**Disclaimer:** This binding is a private contribution and is not an official Busch-Jaeger product.

**Issues and feature requests:** 

### Supported things
Type | Description
------------ | -------------
Bridge | Gateway that connects to the Busch-Jaeger SysAP. All commands from the things are routed through this bridge. The bridge has to added manually before starting discovery.
Raffstore switch | Switch to run blinds or raffstore shutters stepwise or completely.
Scenario selector | This thing can be used to provide user switches to configure different scenarios e.g. @Home. The status of these switches can be used by rules to setup different scenarios.
Scene | Switch that activates a scene that was generated within Free@Home webui. The switch is autoreset after a configurable timeout.
Switch | Binary switch or switch group.
Dimmer | Dimmer supporting switch on/off, fading, set percentage directly
Thermostat | Thermostat that can be switched on/off, eco mode on/off or to set the target temperature.
4.3" Touch | Integrated thermostat that can be switched on/off, eco mode on/off or to set the target temperature.
Weather station | Weather station providing wind speed, temperature, illumination.
