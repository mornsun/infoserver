# InfoServer
InfoServer is a terminal asynchronous netty server (without downstreams) framework, which has some fancy and general functionalities for a high-throughput(20k+) server like complete timeout management, simple &amp; comprehensive configuration, auto reload configuration &amp; data source, and so forth.
Currently it provides some essences for an infomation server in many web application:
* IP location and ISP recognition
* AB Testing framework based on the mobile internet

## Install

1. git clone git://github.com/mornsun/infoserver.git
2. Configure maven in eclipse
 * Import a maven project: if you come across dependence error, right click the project in project explorer and select **Maven - Update Projects...**, then select the root directory of this project and refresh it.
 * Protobuf: InfoProtocol is just an example, you can use any protocol as you wish.
4. Revise configurations and data source files
 * config/infoserver.yaml: server related configuration
 * config/exp.yaml: AB Testing experiment definitions, which take analogue rules in some large companies and support complicated flow matching
 * config/log4j.properties: log related configuration
 * config/ss-geoip.utf8: geoip data source
4. Debug / Run
 * Open **Debug configuration - vm arguments**, type **-Dinfoserver-config=config**.
 * Launch the server with **org.mornsun.info.server.InfoServerLuncher**

## Example Usage

Please see also **[BDClient](https://github.com/mornsun/bdclient)** with a sample protocol and interaction

