## 1. 概述

本快速教程提供了通过Maven和Gradle定义Spring Boot应用程序入口点的不同方法。

Spring Boot应用程序的主类是一个包含启动Spring ApplicationContext的public static void main()方法的类。**默认情况下，如果没有显式指定主类，Spring会在编译时在类路径中搜索一个，如果没有找到或找到多个则启动失败。**

与传统的Java应用程序不同，本教程中讨论的主类不会作为生成的JAR或WAR文件的META-INF/MANIFEST.MF中的Main-Class元数据属性出现。

Spring Boot期望将工件的Main-Class元数据属性设置为org.springframework.boot.loader.JarLauncher(或WarLauncher)，这意味着将我们的主类直接传递到java命令行不会正确启动我们的Spring Boot应用程序。

示例清单如下所示：

```manifest
Manifest-Version: 1.0
Start-Class: cn.tuyucheng.taketoday.DemoApplication
Main-Class: org.springframework.boot.loader.JarLauncher
```

相反，我们需要在Manifest文件中定义Start-Class属性，该属性由JarLauncher评估以启动应用程序。

让我们看看如何使用Maven和Gradle控制此属性。

## 2. Maven

主类可以定义为pom.xml的properties部分中的start-class元素：

```xml
<properties>
    <!-- The main class to start by executing "java -jar" -->
    <start-class>cn.tuyucheng.taketoday.DemoApplication</start-class>
</properties>
```

请注意，**仅当我们还在pom.xml中将[spring-boot-starter-parent](https://search.maven.org/search?q=a:spring-boot-starter-parent)添加为<parent\>时，才会评估此属性**。

或者，**可以将主类定义为pom.xml的plugin部分中的spring-boot-maven-plugin的mainClass元素**：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <mainClass>cn.tuyucheng.taketoday.DemoApplication</mainClass>
            </configuration>
        </plugin>
    </plugins>
</build>
```

可以在[GitHub上](https://github.com/eugenp/tutorials/tree/master/spring-boot-modules/spring-boot-basic-customization)找到此Maven配置的示例。

## 3. Gradle

如果我们使用的是**Spring Boot Gradle插件**，则有一些从org.springframework.boot继承的配置，我们可以在其中指定我们的主类。

在项目的Gradle文件中，mainClassName可以在**springBoot配置块**中定义。此处所做的更改由bootRun和bootJar任务获取：

```groovy
springBoot {
    mainClassName = 'cn.tuyucheng.taketoday.DemoApplication'
}
```

**或者，可以将主类定义为bootJar Gradle任务的mainClassName属性：**

```groovy
bootJar {
    mainClassName = 'cn.tuyucheng.taketoday.DemoApplication'
}
```

**或者作为bootJar任务的manifest属性：**

```xml
bootJar {
    manifest {
	attributes 'Start-Class': 'cn.tuyucheng.taketoday.DemoApplication'
    }
}
```

请注意，在bootJar配置块中指定的主类仅影响任务本身生成的JAR。此更改不会影响其他Spring Boot Gradle任务(例如bootRun)的行为。

作为奖励，如果将**Gradle application插件**应用于项目，则**可以将mainClassName定义为全局属性：**

```groovy
mainClassName = 'cn.tuyucheng.taketoday.DemoApplication'
```

我们可以在[GitHub上](https://github.com/eugenp/tutorials/tree/master/spring-boot-modules/spring-boot-gradle)找到这些Gradle配置的示例。

## 4. 使用CLI

我们也可以通过命令行界面指定一个主类。

Spring Boot的org.springframework.boot.loader.PropertiesLauncher带有一个JVM参数，可让你可以覆盖名为loader.main的逻辑主类：

```shell
java -cp bootApp.jar -Dloader.main=cn.tuyucheng.taketoday.DemoApplication org.springframework.boot.loader.PropertiesLauncher
```

## 5. 总结

有多种方法可以指定Spring Boot应用程序的入口点。重要的是要知道所有这些配置只是修改JAR或WAR文件清单的不同方式。