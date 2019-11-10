# loxone-java [![Maven Central](https://maven-badges.herokuapp.com/maven-central/cz.smarteon/loxone-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cz-smarteon/loxone-java) [![codecov](https://codecov.io/gh/Smarteon/loxone-java/branch/master/graph/badge.svg)](https://codecov.io/gh/Smarteon/loxone-java)
Java implementation of the Loxone™ communication protocol (Web Socket)

*Disclaimer:*
This library is in early stage of development - public APIs are subject to change. Any feedback or help is welcomed.
 
## Usage
### Maven
```xml
<dependency>
    <groupId>cz.smarteon</groupId>
    <artifactId>loxone-java</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Gradle
```
compile group: 'cz.smarteon', name: 'loxone-java', version: '0.1.0'
```

### Quick start
```java
LoxoneHttp loxoneHttp = new LoxoneHttp(address);
LoxoneWebSocket loxoneWebSocket = new LoxoneWebSocket(address, new LoxoneAuth(loxoneHttp, user, password, uiPassword));

loxoneWebSocket.sendCommand(...);
...
```
Study [examples](examples) for detailed usage information.

## Development & Contributions
Start by generating gradle wrapper binaries (using local gradle installation)
```bash
gradle wrapper
```

_Note:_ Build currently requires JDK 8