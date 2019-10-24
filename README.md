# Jetro - JSON transformations - powerful, yet quick and easy

Jetro provides a JSON transformation engine and a comprehensive JSON tree API. It allows transforming any JSON source representation into any JSON target representation applying arbitrary changes while doing so. Additionally it implements the RFC6901 (JSON Pointer) and RFC6902 (JSON Patch) specifications in the jetro-patch module.


## Usage

Add needed dependencies:

```
<dependency>
    <groupId>net.sf.jetro</groupId>
    <artifactId>jetro-stream</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>net.sf.jetro</groupId>
    <artifactId>jetro-object</artifactId>
    <version>1.0.0</version>
</dependency>
```

There is also a ```jetro-all``` package if all modules are needed.

Some code usage examples can be found in the Wiki: 
https://github.com/unic/jetro/wiki/Usage-Examples

## Build

Requirements:
- Java 8
- Maven 3.x

Command:
```
mvn clean install
```

## Release process

The Release process is currently not yet automated via a CI system. 
The release is done manually and locally. Publishing to Maven Central repository is done via Sonatype.

### Release steps

- Execute on develop branch: `mvn jgitflow:release-start -DreleaseVersion=NEW_VERSION -DdevelopmentVersion=X.Y.Z-SNAPSHOT`
- Finalize with: `mvn jgitflow:release-finish`
