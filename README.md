# loxone-java [![Maven Central](https://maven-badges.herokuapp.com/maven-central/cz.smarteon/loxone-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cz-smarteon/loxone-java) [![codecov](https://codecov.io/gh/Smarteon/loxone-java/branch/master/graph/badge.svg)](https://codecov.io/gh/Smarteon/loxone-java)
Java implementation of the Loxone™ communication protocol (Web Socket).

* *Supported miniservers*: miniserver gen. 1, miniserver gen. 2, miniserver GO
* *Supported firmware*: **10.4.0.0** and ongoing

Most of the library is trying to behave according to 
[Loxone API documentation](https://www.loxone.com/enen/kb/api/) 
and [Loxone webservices](https://www.loxone.com/enen/kb/web-services/). 
However, there are also some reverse engineered parts, marked with `@LoxoneNotDocumented` annotation.  

*Disclaimer:*
The library is still far from complete coverage of Loxone behaviour, which means:
* public APIs are subject to change with every new version
* there can be serious bugs 

Therefore, any feedback or help is welcomed.
 
## Usage
### Maven
```xml
<dependency>
    <groupId>cz.smarteon</groupId>
    <artifactId>loxone-java</artifactId>
    <version><!-- desired version --></version>
</dependency>
```

### Gradle
```
compile group: 'cz.smarteon', name: 'loxone-java', version: 'desired version'
```
or
```kotlin
implementation("cz.smarteon", "loxone-java", "desired version")
```

### Quick start
The main entry point of the library is the [Loxone](src/main/java/cz/smarteon/loxone/Loxone.java) class.
```java
// obtain loxone instance
final Loxone loxone = new Loxone(new LoxoneEndpoint(address), user, password, uiPassword);

// start interaction and wait for LoxoneApp fetched
loxone.start();

// do the job
loxone.sendControlOn(loxone.app().getControl(SwitchControl.class));

// obtain http or websocket classes for more low-level work
final LoxoneHttp loxoneHttp = loxone.http();
final LoxoneWebSocket loxoneWebSocket = loxone.webSocket();

// stop the session correctly
loxone.stop();
```
Study [examples](examples) for detailed usage information.

#### Miniserver discovery
The library supports Loxone miniserver discovery, 
see [MiniserverDiscoverer](src/main/java/cz/smarteon/loxone/discovery/MiniserverDiscoverer.java).
```java
// find at least one miniserver, waiting for maximum 500 millis
Set<MiniserverDiscovery> discovered = new MiniserverDiscoverer().discover(1, 500);
```

#### Android specifics
On Android 6 and older, the `java.util.Base64` is not available. Please use `setBase64Codec` to use different implementation. 
```java
Codec.setBase64Codec(..., ...);
```
## Development & Contributions

_Note:_ Build currently requires at least JDK 11 & Gradle 7.2 to be locally installed

Start by generating gradle wrapper binaries (using local gradle installation)

```bash
gradle wrapper --gradle-version 7.2
```

After that you can use the gradlew binary to do builds & tests

```bash
./gradlew build
```

This first build should succeed, this means the project is correctly setup and you can start contributing to it.

_Note:_ When the MiniserverDiscovererTest fails this could be a firewall issue, this test opens up a random UDP port for 
the test

```bash
MiniserverDiscovererTest > should discover() FAILED
    strikt.internal.opentest4j.AssertionFailed at MiniserverDiscovererTest.kt:31
```

It is adviced to install the lombok plugin for your IDE of choice, this makes coding & debugging easier, you can find the 
instructions for installing the plugin for various IDE's on the following location https://projectlombok.org/setup/