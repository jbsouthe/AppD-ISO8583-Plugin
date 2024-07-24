ISO8583 AppDynamics Agent Plugin
==================================

## Purpose ##

ISO8583 https://en.wikipedia.org/wiki/ISO_8583 message support for java apm agents. This plugin creates a service endpoint, and business transaction if not active, for inbound messages, and exit calls for out bound messages. It injects the correlation header into the outbound message and looks for one on the inbound message.

![Flowmap Image](doc_images%2FISO8583-transaction-flowmap.png)

Please let us know in the Issues list if anything more is requested or something isn't working as expected.

## Configuration

This plugin can be configured to support correlation. By default, the configuration has correlation disabled because correlation requires using an available field number to store the correlation string, and this could be writing over a field already in use by a customer for other data. The default field number for correlation is "127", this is reserved for private use https://en.wikipedia.org/wiki/ISO_8583#ISO-defined_data_elements_(ver_1987), but may already be in use, other fields can be used, please try to stay between 105 and 127.

To modify the configuration, load the plugin by installing it, as per notes in deployment steps, and once loaded the plugin will generate a properties file named "ISO8583.properties", it contains several lines, the two that can be modified to affect this behavior are:

    iso8583-correlation-enabled=false
    iso8583-correlation-field=127

When modifying these properties, use the command "touch <jarfile.jar>" to force a reload of the plugin and a re-evaluation of the properties file

## Required

- Agent version 21.02+
- Java 8

## Deployment steps

- Copy *AgentPlugin.jar file under <agent-install-dir>/ver.x.x.x.x/sdk-plugins

## Change Log

- V1.0 supports BT, SE, Exitcalls with correlation
