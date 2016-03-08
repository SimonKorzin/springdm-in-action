# Introduction #

The [OSGi](http://www.osgi.org) Blueprint Service aims at instantiating, configuring and assembling the inner components of an OSGi bundle. It also allows interacting with the OSGi service registry (register/consume services) in a declarative way.

The Blueprint Service (a.k.a RFC-124) has been introduced in the 4.2 version of the OSGi specifiation and is a standardization of [Spring DM](http://www.springsource.org/osgi/). So, think of the Blueprint Service as a component model for the inner assembling of OSGi bundles and a tool to publish/consume OSGi services in a reliable way, without having to use the OSGi API at all.

This page is a tutorial to introduce the Reference Implementation of the Blueprint Service, Spring DM 2.0. You'll learn how to associate a Blueprint Container to an OSGi bundle. This container will contain a simple bean, which will be registered declaratively on the service registry.

Prerequisites:
  * basic understanding of OSGi
  * Maven 2 installed

# Creating the bundle #

In a working directory, create a Maven 2 project with the following command:

```
mvn archetype:generate
```

You'll be prompted for several choices:
  * archetype to use: choose the default (15, a basic Maven 2 project), just type `Enter`
  * the group of the project: `com.manning.sdmia`
  * the artifact id: `blueprint101`
  * the version: default, just type `Enter`
  * the defaut package: default, just type `Enter`
  * confirmation: type `Enter`

This creates a `blueprint101` directory, where lies a standard Maven 2 project. In the `src/main/java/com/manning/sdmia`, create a `BlueprintBean` class with the following content:

```
package com.manning.sdmia;

public class BlueprintBean {

  public BlueprintBean() {
    System.out.println("Blueprint Bean created!");
  }
	
}
```

It's now time to create the descriptor of the bundle's Blueprint container. This descriptor is a XML file and must be in the `OSGI-INF/blueprint` directory of the bundle. Create the `src/main/resources/OSGI-INF/blueprint` directory and a create a `bundle-context.xml` file in it. Here is the content of the descriptor:

```
<?xml version="1.0" encoding="UTF-8"?>
<blueprint xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="
    http://www.osgi.org/xmlns/blueprint/v1.0.0 
    http://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd">

  <!-- create the bean -->
  <bean id="blueprintBean" class="com.manning.sdmia.BlueprintBean" />      	
   	
  <!-- export the bean on the service registry -->
  <service ref="blueprintBean" interface="com.manning.sdmia.BlueprintBean" />   	
   	
</blueprint>
```

The next step is to package the project as an OSGi bundle.

# Packaging the bundle #

We'll use the [Apache Felix Bundle Plugin](http://felix.apache.org/site/apache-felix-maven-bundle-plugin-bnd.html) for packaging our Maven 2 project as an OSGi bundle. Here are the steps to do so:
  * change the packaging of the bundle from `jar` to `bundle`
  * add a plugin configuration entry in the `build` element of the POM file.

Here is how the `pom.xml` should look like once modified to use the Felix Bundle Plugin:

```
<project xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
    http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.manning.sdmia</groupId>
  <artifactId>blueprint101</artifactId>
  <packaging>bundle</packaging>
  <version>1.0-SNAPSHOT</version>
  <name>blueprint101</name>
	
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.felix</groupId>
        <artifactId>maven-bundle-plugin</artifactId>
        <version>2.0.0</version>
        <extensions>true</extensions>
      </plugin>
    </plugins>
  </build>
	
</project>
```

You can package the bundle by using the following Maven 2 command:

```
mvn clean package -Dmaven.test.skip
```

It will create the JAR file in the `target` directory.

# Deploying the Blueprint-powered bundle #

To deploy the bundle on a OSGi container, you need to provision the latter with the Blueprint API and an implementation. You can find a ready-to-use Equinox [here](http://springdm-in-action.googlecode.com/files/equinox-springdm-blueprint-20090904.zip) (it uses the 20090904 snapshot of the Uber Blueprint RI bundle, which embeds all the necessary bundles, including the Spring 3.0 ones).

Unzip the Equinox distribution in a working directory and copy the JAR file of the bundle in it (beside the Equinox JAR). Equinox is configured with a `config.ini` file, which lies in the `configuration` directory. Edit this file to add the bundle JAR in the installed bundles (last line):

```
eclipse.ignoreApp=true

osgi.bundles=\
reference:file:./system/org.eclipse.osgi.util_3.2.0.v20090520-1800.jar@1:start,\
reference:file:./system/org.eclipse.osgi.services_3.2.0.v20090520-1800.jar@1:start,\
reference:file:./rfc-124/uber-2.0.0-m1-SNAPSHOT.jar@start,\
reference:file:./blueprint101-1.0-SNAPSHOT.jar@start
```

You can now launch Equinox, with the following command line:

```
java -jar org.eclipse.osgi_3.5.0.v20090520.jar -console
```

Equinox fires up and you'll see that Spring DM's extender found the `bundle-context.xml` file:

```
Retrieved locations for location osgibundle:OSGI-INF/blueprint/*.xml = []
Retrieved locations for location osgibundle:OSGI-INF/blueprint/*.xml = []
Retrieved locations for location osgibundle:OSGI-INF/blueprint/*.xml = []
Retrieved locations for location osgibundle:OSGI-INF/blueprint/*.xml = [URL [bundleentry://4.fwk5184781/OSGI-INF/blueprint/bundle-context.xml]]
Retrieved locations for location osgibundle:OSGI-INF/blueprint/*.xml = [URL [bundleentry://4.fwk5184781/OSGI-INF/blueprint/bundle-context.xml]]
Retrieved locations for location osgibundle:OSGI-INF/blueprint/*.xml = []
```

You'll also see that the `BlueprintBean` has been created:

```
Blueprint Bean created!
```

The `ss` (Short Status) issues the installed bundle:

```
Framework is launched.

id      State       Bundle
0       ACTIVE      org.eclipse.osgi_3.5.0.v20090520
1       ACTIVE      org.eclipse.osgi.util_3.2.0.v20090520-1800
2       ACTIVE      org.eclipse.osgi.services_3.2.0.v20090520-1800
3       ACTIVE      org.springframework.osgi.blueprint.uber_2.0.0.m1
4       ACTIVE      com.manning.sdmia.blueprint101_1.0.0.SNAPSHOT
```

You can check that the `BlueprintBean` has been published as an OSGi service with the `bundle` command:

```
osgi> bundle 4
com.manning.sdmia.blueprint101_1.0.0.SNAPSHOT [4]
  Id=4, Status=ACTIVE      Data Root=D:\data\livres\springdm\rfc-124\container\configuration\org.eclipse.osgi\bundles\4\data
  Registered Services
    {com.manning.sdmia.BlueprintBean}={org.springframework.osgi.bean.name=blueprintBean, osgi.service.blueprint.compname=blueprintBean, Bundle-SymbolicName=com.manning.sdmia.blueprint101, Bundle-Version=1.0.0.SNAPSHOT, service.id=30}
    {org.osgi.service.blueprint.container.BlueprintContainer}={Bundle-SymbolicName=com.manning.sdmia.blueprint101, Bundle-Version=1.0.0.SNAPSHOT, osgi.blueprint.container.version=1.0.0.SNAPSHOT, osgi.blueprint.container.symbolicname=com.manning.sdmia.blueprint101, service.id=31}
    {org.springframework.osgi.context.DelegatedExecutionOsgiBundleApplicationContext, org.springframework.osgi.context.ConfigurableOsgiBundleApplicationContext, org.springframework.context.ConfigurableApplicationContext, org.springframework.context.ApplicationContext, org.springframework.context.Lifecycle, org.springframework.beans.factory.ListableBeanFactory, org.springframework.beans.factory.HierarchicalBeanFactory, org.springframework.context.MessageSource, org.springframework.context.ApplicationEventPublisher, org.springframework.core.io.support.ResourcePatternResolver, org.springframework.beans.factory.BeanFactory, org.springframework.core.io.ResourceLoader, org.springframework.beans.factory.DisposableBean}={org.springframework.context.service.name=com.manning.sdmia.blueprint101, Bundle-SymbolicName=com.manning.sdmia.blueprint101, Bundle-Version=1.0.0.SNAPSHOT, service.id=32}
  Services in use:
    {org.springframework.beans.factory.xml.NamespaceHandlerResolver}={service.id=26}
    {org.xml.sax.EntityResolver}={service.id=27}
  Exported packages
    com.manning.sdmia; version="0.0.0"[exported]
  No imported packages
  No fragment bundles
  Named class space
    com.manning.sdmia.blueprint101; bundle-version="1.0.0.SNAPSHOT"[provided]
  No required bundles

```

Congrats, this is your first Blueprint-powered bundle!

# Summary #

We created a very simple bundle, that contains an object and exports it as an OSGi service. The Blueprint service created and published the service on our behalf. This is particularly convenient, as we don't need to use the OSGi API (create an activator for the bundle, create the object in the activator and publish it, etc.). This is not really impressive, as we have only one bundle and one service, but imagine we had dozens of bundles and hundreds of services: configuring each bundle programmatically and checking if its dependencies are in the service registry would be a nightmare. That's where the Blueprint service becomes essential, as it's able to compute the dependency graph to create and configure the inner components of each bundle in a safe and reliable way.

Those used to Spring DM do not discover much, as the Blueprint Service is a standardization of Spring DM. Just think that now, most of the Spring lightweight container features are available as a _standard_ in OSGi and that you'll be able to _choose_ the Blueprint implementation that suits you best (as soon as other implementations will appear!).

# References #

  * [Spring Dynamic Modules](http://www.springsource.org/osgi/)
  * [Overview of the Blueprint Service](http://jbossosgi.blogspot.com/2009/04/osgi-blueprint-service-rfc-124.html) (somewhat outdated)
  * [OSGi](http://www.osgi.org)