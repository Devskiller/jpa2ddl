[![Build Status](https://travis-ci.org/Devskiller/jpa2ddl.svg?branch=master)](https://travis-ci.org/Devskiller/jpa2ddl)   [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.devskiller.jpa2ddl/jpa2ddl-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.devskiller.jpa2ddl/jpa2ddl-maven-plugin)

# JPA Schema Generator Plugin

## Motivation

Why another tool to dump the JPA schema? All tools that we've found were related to legacy versions of Hibernate or were covering just simple cases, without options to configure dialect or naming strategy. Also all of the tools we've found are based on the `SchemaExport` class, which does not always correlate with the runtime schema - for example due to the lack of support for the `Integrator` services, used to register `UserType` classes like JodaTime or similar. We were also looking for a tool that is be able to handle further schema migrations, not just dump the current version. 

## Configuration parameters

- `packages` (required): list of packages containing JPA entities
-  `outputPath`: output file for the generated schema. By default:
  - for `UPDATE` action: `BUILD_OUTPUT_DIR/generated-resources/scripts/`
  - for other actions: `BUILD_OUTPUT_DIR/generated-resources/scripts/database.sql`
- `jpaProperties`: additional properties like dialect or naming strategies which should be used in generation task. By default `empty`
- `formatOutput`: should the output be formatted. By default `true`
- `skipSequences`: should the generator skip sequences creation. By default `false`
- `delimiter`: delimiter used to separate statements. By default `;`
- `action`: which statements should be generated. By default: `CREATE`. Possible values:
  - `DROP`
  - `CREATE`
  - `DROP_AND_CREATE`
  - `UPDATE`
- `generationMode`: schema generation mode. By default `DATABASE`. Possible values:
  - `DATABASE`: generation based on setting up embedded database and dumping the schema
  - `METADATA`: generation based on static metadata

## Maven Plugin

You can run this plugin directly or integrate it into the default build lifecycle.

### Simple configuration example

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.devskiller.jpa2ddl</groupId>
            <artifactId>jpa2ddl-maven-plugin</artifactId>
            <version>0.9.12</version>
            <extensions>true</extensions> <!-- required to run automatically -->
            <configuration>
                <packages>
                    <package>com.test.model</package>
                </packages>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Generate schema

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.devskiller.jpa2ddl</groupId>
            <artifactId>jpa2ddl-maven-plugin</artifactId>
            <version>0.9.12</version>
            <extensions>true</extensions> <!-- required to run automatically -->
            <configuration>
                <outputPath>${basedir}/src/main/resources/database.sql</outputPath>
                <packages>
                    <package>com.test.model</package>
                    <package>com.test.entities</package>
                </packages>
                <jpaProperties>
                    <property>
                        <name>hibernate.dialect</name>
                        <value>org.hibernate.dialect.MySQL57Dialect</value>
                    </property>
                    <property>
                        <name>hibernate.default_schema</name>
                        <value>prod</value>
                    </property>
                </jpaProperties>
                <formatOutput>true</formatOutput>
                <skipSequences>true</skipSequences>
                <delimiter>;</delimiter>
                <action>DROP_AND_CREATE</action>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Generate migrations

It's also possible to generate automated migrations scripts. JPA2DDL supports [Flyway naming patterns](https://flywaydb.org/documentation/migration/sql) for versioned migrations.

All subsequent migration scripts are saved in the `outputPath`, in the following layout:
```sh
src/main/resources/migrations/
 v1__jpa2ddl.sql
 v2__jpa2ddl.sql
 ... next
```

Please note that after generation you can change the name of the file to make it more descriptive following the filename pattern `v(N)__jpa2ddl(_custom_description).sql` - for example `v1__jpa2ddl_init.sql`

Sample configuration:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.devskiller.jpa2ddl</groupId>
            <artifactId>jpa2ddl-maven-plugin</artifactId>
            <version>0.9.12</version>
            <extensions>true</extensions> <!-- required to run automatically -->
            <configuration>
                <outputPath>${basedir}/src/main/resources/migrations/</outputPath>
                <packages>
                    <package>com.test.model</package>
                    <package>com.test.entities</package>
                </packages>
                <jpaProperties>
                    <property>
                        <name>hibernate.dialect</name>
                        <value>org.hibernate.dialect.MySQL57Dialect</value>
                    </property>
                </jpaProperties>
                <formatOutput>true</formatOutput>
                <delimiter>;</delimiter>
                <action>UPDATE</action>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Direct invocation

```
./mvnw com.devskiller.jpa2ddl:jpa2ddl-maven-plugin:0.9.12:generate
```

## Gradle Plugin

Below you can find a sample `build.gradle` script configuration:

```groovy
buildscript {
    repositories {
	    mavenCentral()
    }
    dependencies {
	    classpath "com.devskiller.jpa2ddl:jpa2ddl-gradle-plugin:0.9.12"
    }
}

apply plugin: 'com.devskiller.jpa2ddl'

jpa2ddl {
    packages = ['com.test.model']
}
```
