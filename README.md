[![Build Status](https://travis-ci.org/mimarox/jetro.svg?branch=develop)](https://travis-ci.org/mimarox/jetro) [![javadoc](https://javadoc.io/badge2/net.sf.jetro/jetro-transform/javadoc.svg)](https://javadoc.io/doc/net.sf.jetro/jetro-transform)

# Jetro - JSON transformations - powerful, yet quick and easy

Jetro provides a JSON transformation engine and a comprehensive JSON tree API. It allows transforming any JSON source representation into any JSON target representation applying arbitrary changes while doing so. Additionally it implements the RFC6901 (JSON Pointer) and RFC6902 (JSON Patch) specifications in the jetro-patch module.


## Usage

Add needed dependencies:

```
<dependency>
    <groupId>net.sf.jetro</groupId>
    <artifactId>jetro-stream</artifactId>
    <version>2.0.0</version>
</dependency>
<dependency>
    <groupId>net.sf.jetro</groupId>
    <artifactId>jetro-object</artifactId>
    <version>2.1.1</version>
</dependency>
```

There is also a ```jetro-all``` package if all modules are needed.

Some code usage examples can be found in the Wiki: 
https://github.com/mimarox/jetro/wiki/Usage-Examples

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

- Merge any feature branches you want to include in the release into develop branch
- To prepare the release, execute on develop branch: `mvn release:prepare -P release`
- Performe the release with: `mvn release:perform -P release -DreleaseProfiles=release`
- Log into Sonatype Nexus and go to the staging repository
- Close and release the staging repository

### Release Plugin Docs
https://maven.apache.org/maven-release/maven-release-plugin/
