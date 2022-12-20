## 1. 概述

Spring Boot提供了父POM，以便更轻松地创建Spring Boot应用程序。

然而，如果我们已经有一个父POM可以继承，那么使用父POM可能并不总是可取的。

在本快速教程中，我们介绍如何在没有父启动器的情况下仍然使用Boot。

## 2. 没有父POM的Spring Boot

父pom.xml负责依赖性和插件管理。出于这个原因，继承它可以在应用程序中提供有价值的支持，因此在创建Boot应用程序时，它通常是首选的操作过程。你可以在我们[之前的文章]()中找到有关如何基于父启动器构建应用程序的更多详细信息。

但在实践中，我们可能会受到设计规则或其他偏好的限制而使用不同的父级。

幸运的是，Spring Boot提供了一种从父启动器继承的替代方案，它仍然可以为我们提供它的一些优势。

如果我们不使用父POM，我们仍然可以通过添加带有scope=import的spring-boot-dependencies工件来受益于依赖管理：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>2.4.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

接下来，我们可以开始简单地开始添加Spring依赖项并使用Spring Boot功能：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

另一方面，如果没有父POM，我们就不再受益于插件管理，这意味着我们需要显式添加spring-boot-maven-plugin：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

## 3. 覆盖依赖版本

如果我们想对某个依赖使用不同于由Boot管理的依赖的版本，我们需要在dependencyManagement部分声明它，然后再声明spring-boot-dependencies：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
            <version>2.4.0</version>
        </dependency>
    </dependencies>
    // ...
</dependencyManagement>
```

相比之下，仅在dependencyManagement标签之外声明依赖项的版本将不再有效。

## 4. 总结

在本快速教程中，我们了解了如何在没有父pom.xml的情况下使用Spring Boot。