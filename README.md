[![Build Status](https://travis-ci.org/tklein/gradle-esb-plugin.png)](https://travis-ci.org/tklein/gradle-esb-plugin)

# Gradle ESB Plugin! 
ESB packaging similar to WAR or EAR

# Quick Start
Do ESB packaging in a [Gradle](http://gradle.org) build is easy! Just add this to your *build.gradle* file:

```groovy
apply plugin: 'esb'
apply plugin: 'eclipse'
apply plugin: 'maven'

group = 'eu.schnuckelig.test'
version = '0.1-SNAPSHOT'
esbContentDirName = 'esbcontent'

buildscript {
    repositories {
		maven {
            url uri('../repo')
        }
		mavenLocal()
		mavenCentral()
    }

	dependencies {
		classpath 'eu.schnuckelig.gradle:gradle-esb-plugin:0.1-SNAPSHOT'
	}
}

dependencies {
	providedCompile 'org.apache.activemq:activemq-all:5.6.0',
		'org.jboss.soa.bpel.dependencies.esb:jbossesb-rosetta:4.9',
		'jboss:jboss-common-logging-spi:2.0.4.GA'
	
	runtime 'log4j:log4j:1.2.17'
	
	testCompile 'junit:junit:4.10'
}
```

# See Also #
The [Gradle JAX-WS Plugin](https://github.com/tklein/gradle-jaxws-plugin)!
