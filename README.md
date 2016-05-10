# Jetro - JSON transformations - powerful, yet quick and easy

Jetro provides a JSON transformation engine and a comprehensive JSON tree API. It allows transforming any JSON source representation into any JSON target representation applying arbitrary changes while doing so.


## Usage

Add needed dependencies:

```
<dependency>
    <groupId>net.sf.jetro</groupId>
    <artifactId>jetro-stream</artifactId>
    <version>0.2-b1</version>
</dependency>
<dependency>
    <groupId>net.sf.jetro</groupId>
    <artifactId>jetro-object</artifactId>
    <version>0.2-b1</version>
</dependency>
```

There is also a ```jetro-all``` package if all modules are needed.

Some code usage examples can be found in the Wiki: 
https://github.com/unic/jetro/wiki/Usage-Examples

## Build

Requirements:
- Java 7
- Maven 3.x

Command:
```
mvn install
```

## Release process

The Release process is currently not yet automated via a CI system and are not published in the Maven Central repository. 

The release is done manually and locally.

### Release steps

- Execute on develop branch: `mvn jgitflow:release-start -DreleaseVersion=NEW_VERSION -DdevelopmentVersion=X.Y.Z-SNAPSHOT`
- Finalize with: `mvn jgitflow:release-finish -DnoDeploy=true`
- Upload manually to your Artifact repository, for example with:
  - First checkout a proper released tag
  - Then execute: `mvn deploy -DaltDeploymentRepository=MY_SERVER_ID::default::https://mynexus.com/repo/foo`
- Finally create a Github release page and attach the jar binaries
