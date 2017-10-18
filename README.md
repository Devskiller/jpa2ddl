[![Build Status](https://travis-ci.org/Devskiller/hbm2ddl-maven-plugin.svg?branch=master)](https://travis-ci.org/Devskiller/hbm2ddl-maven-plugin)   [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.devskiller.hbm2ddl-maven-plugin/hbm2ddl-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.devskiller.hbm2ddl-maven-plugin/hbm2ddl-maven-plugin)

# Hibernate Schema Generator Plugin

## Motivation

Why another tool to dump JPA schema? All tools that we've found were related to legacy versions of Hibernate or were covering just a simple cases, without option to configure dialect or naming strategy. Also all tools we found are based on the `SchemaExport` class, which does not always correlate with the runtime schema - usually due to lack of support for `Integrator` services, used to register `UserType` classes like JodaTime or similar.

## Usage

   

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.devskiller.hbm2ddl-maven-plugin</groupId>
            <artifactId>hbm2ddl-maven-plugin</artifactId>
            <version>0.9.3</version>
            <configuration>
                <packages>
                    <package>com.test.model</package>
                </packages>
            </configuration>
        </plugin>
    </plugins>
</build>
```