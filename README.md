ISO8583 AppDynamics Agent Plugin
==================================

## Purpose ##

ISO8583 https://en.wikipedia.org/wiki/ISO_8583 message support for java apm agents. This plugin creates a service endpoint, and business transaction if not active, for inbound messages, and exit calls for out bound messages. It injects the correlation header into the outbound message and looks for one on the inbound message.

Please let us know in the Issues list if anything more is requested or something isn't working as expected.

## Required
- Agent version 21.02+
- Java 8


## Deployment steps
- Copy *AgentPlugin.jar file under <agent-install-dir>/ver.x.x.x.x/sdk-plugins

## Change Log

- V1.0 supports BT, SE, Exitcalls with correlation
