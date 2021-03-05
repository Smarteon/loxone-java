# loxone-java [![Maven Central](https://maven-badges.herokuapp.com/maven-central/cz.smarteon/loxone-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cz-smarteon/loxone-java) [![codecov](https://codecov.io/gh/Smarteon/loxone-java/branch/master/graph/badge.svg)](https://codecov.io/gh/Smarteon/loxone-java)
Java implementation of the Loxone™ communication protocol (Web Socket).

Most of the library is trying to behave according to 
[Loxone API documentation](https://www.loxone.com/enen/kb/api/) 
and [Loxone webservices](https://www.loxone.com/enen/kb/web-services/). 
However, there are also some reverse engineered parts, marked with `@LoxoneNotDocumented` annotation.  

*Disclaimer:*
This library is in early stage of development - public APIs are subject to change. Any feedback or help is welcomed.
 
## Usage
### Maven
```xml
<dependency>
    <groupId>cz.smarteon</groupId>
    <artifactId>loxone-java</artifactId>
    <version><!-- desired version -->></version>
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

## Development & Contributions
Start by generating gradle wrapper binaries (using local gradle installation)
```bash
gradle wrapper
```

_Note:_ Build currently requires JDK 8