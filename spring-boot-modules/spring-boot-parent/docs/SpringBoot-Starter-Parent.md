## 1. 简介

在本教程中，我们将了解spring-boot-starter-parent，我们将讨论如何从中受益，以实现更好的依赖管理、插件的默认配置以及快速构建我们的Spring Boot应用程序。

我们还将了解如何覆盖starter-parent提供的现有依赖项和属性的版本。

## 2. Spring Boot Starter Parent

spring-boot-starter-parent项目是一个特殊的starter项目，它为我们的应用程序提供了默认配置和完整的依赖树，用于快速构建我们的Spring Boot项目。它还为Maven插件提供默认配置，例如maven-failsafe-plugin、maven-jar-plugin、maven-surefire-plugin和maven-war-plugin。

除此之外，它还继承了spring-boot-dependencies的依赖管理，这是spring-boot-starter-parent的父级。

我们可以通过将它作为父级添加到项目的pom.xml中来开始在我们的项目中使用它：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.4.0</version>
</parent>
```

我们始终可以从Maven Central获取最新版本的[spring-boot-starter-parent](https://search.maven.org/classic/#search|ga|1|a%3A"spring-boot-starter-parent")。

## 3. 管理依赖

一旦在我们的项目中声明了父级starter，我们就可以通过在我们的dependencies标签中声明它来从父级中提取任何依赖项，我们也不需要定义依赖项的版本；Maven将根据parent标签中为starter parent定义的版本下载jar文件。

比如我们在构建一个web项目，可以直接添加spring-boot-starter-web即可，不需要指定版本：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

## 4. 依赖管理标签

要管理父级starter提供的不同版本的依赖项，我们可以在dependencyManagement部分中显式声明依赖项及其版本：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
            <version>2.4.0</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## 5. 属性

**要更改父级starter中定义的任何属性的值，我们可以在我们的属性部分重新声明它**。

spring-boot-starter-parent通过其父级spring-boot-dependencies使用属性来配置所有依赖项版本、Java版本和Maven插件版本，因此，只需更改相应的属性，我们就可以轻松控制这些配置。

如果我们想要更改我们想要从父级starter中提取的任何依赖项的版本，我们可以在依赖项标签中添加依赖项并直接配置其属性：

```xml
<properties>
    <junit.version>4.11</junit.version>
</properties>
```

## 6. 其他属性覆盖

我们还可以将属性用于其他配置，例如管理插件版本，甚至是一些基本配置，例如管理Java版本和源代码编码，我们只需要用新值重新声明该属性。

例如，要更改Java版本，我们可以在java.version属性中指明它：

```xml
<properties>
    <java.version>1.8</java.version>
</properties>
```

## 7. 不使用Starter Parent的Spring Boot项目

有时我们有一个自定义的Maven父级，或者我们更喜欢手动声明我们所有的Maven配置。

在这种情况下，我们可以选择不使用spring-boot-starter-parent项目，但是我们仍然可以通过在导入范围内的项目中添加依赖项spring-boot-dependencies来受益于它的依赖树。

让我们用一个简单的例子来说明这一点，在这个例子中，我们想要使用除父级starter之外的另一个父级：

```xml
<parent>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>spring-boot-parent</artifactId>
    <version>1.0.0</version>
</parent>
```

在这里我们使用了父模块，一个不同的项目，作为我们的父依赖。

现在，在这种情况下，我们仍然可以通过将其添加到import范围和pom类型中来获得与依赖管理的相同好处：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>2.2.6.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

此外，我们可以通过在依赖项中声明它来引入任何依赖项，就像我们在前面的示例中所做的那样，这些依赖项也不需要版本号。

## 8. 总结

在本文中，我们概述了spring-boot-starter-parent，以及在任何子项目中将其添加为父级的好处。

接下来，我们学习了如何管理依赖关系，我们可以在dependencyManagement中或通过属性覆盖依赖项。

本文中使用的代码片段的源代码可在[Github]()上找到，一个使用父级starter，另一个使用自定义父级。