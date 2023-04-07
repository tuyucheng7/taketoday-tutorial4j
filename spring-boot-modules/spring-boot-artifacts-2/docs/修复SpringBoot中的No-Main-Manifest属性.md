## 一、概述

每当我们在 Spring Boot 可执行文件 jar 中遇到*“无主清单属性”*消息时，那是因为我们缺少文件*MANIFEST.MF中*[*Main-Class*](https://www.baeldung.com/spring-boot-main-class)元数据属性的声明，该文件位于*META-INF*文件夹下。

在这个简短的教程中，我们将了解问题的原因以及解决方法。

## 2.出现问题时

*通常，如果我们从 Spring Initializr 中获取我们的pom*，我们不会有任何问题。[*但是，如果我们通过将spring-boot-starter-parent*](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-parent)添加到我们的*pom.xml*来手动构建我们的项目，我们可能会遇到这个问题。我们可以通过尝试干净地构建 jar 来复制它：

```bash
$ mvn clean package复制
```

运行 jar 时我们会遇到错误：

```bash
$ java -jar target\spring-boot-artifacts-2.jar复制
no main manifest attribute, in target\spring-boot-artifacts-2.jar复制
```

*在这个例子中， MANIFEST.MF*文件的内容是：

```xml
Manifest-Version: 1.0
Archiver-Version: Plexus Archiver
Created-By: Apache Maven 3.6.3
Built-By: Baeldung
Build-Jdk: 11.0.13复制
```

## 3. 使用 Maven 插件修复

### 3.1. 添加插件

在这种情况下，最常见的问题是我们错过了将*[spring-boot-maven-plugin](https://search.maven.org/search?q=a:spring-boot-maven-plugin)*声明添加到我们的*pom.xml*文件中。

让我们使用*plugins*标签下的*Main-Class声明将**插件*定义添加到我们的*pom.xml*中：

```xml
<plugins>
    <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <configuration>
            <mainClass>com.baeldung.demo.DemoApplication</mainClass>
            <layout>JAR</layout>
        </configuration>
    </plugin>
</plugins>复制
```

但是，**这可能不足以解决我们的问题**。重建并运行 jar 后，我们可能仍会收到*“无主要清单属性”消息。*

让我们看看我们有哪些额外的配置和替代方案来解决这个问题。

### 3.2. Maven 插件执行目标

让我们将*重新打包* 目标添加到*配置*标记之后的*spring-boot-maven-plugin*声明中：

```xml
<executions>
    <execution>
        <goals>
            <goal>repackage</goal>
        </goals>
    </execution>
</executions>复制
```

### 3.3. Maven 属性和内联命令执行目标

或者，**将属性\*start-class\*添加到我们的\*pom.xml\*文件的\*properties\*标记中，可以在构建过程中提供更大的灵活性**：

```xml
<properties>
    <start-class>com.baeldung.demo.DemoApplication</start-class>
</properties>复制
```

[*现在，我们必须使用 Maven 内联命令spring-boot:repackage*](https://www.baeldung.com/spring-boot-repackage-vs-mvn-package)执行目标来构建 jar ：

```xml
$ mvn package spring-boot:repackage复制
```

## 4.检查*MANIFEST.MF*文件内容

让我们应用我们的解决方案，构建 jar，然后检查*MANIFEST.MF*文件。

[*我们注意到Main-Class*和*Start-Class*](https://www.baeldung.com/spring-boot-main-class)属性的存在：

```xml
Manifest-Version: 1.0
Archiver-Version: Plexus Archiver
Created-By: Apache Maven 3.6.3
Built-By: Baeldung
Build-Jdk: 11.0.13
Main-Class: org.springframework.boot.loader.JarLauncher
Start-Class: com.baeldung.demo.DemoApplication
Spring-Boot-Version: 2.7.5
Spring-Boot-Classes: BOOT-INF/classes/
Spring-Boot-Lib: BOOT-INF/lib/
Spring-Boot-Classpath-Index: BOOT-INF/classpath.idx
Spring-Boot-Layers-Index: BOOT-INF/layers.idx复制
```

现在执行 jar，*“no main manifest attribute”*消息问题不再出现，应用程序运行。

## 5.结论

在本文中，我们了解了如何 在执行 Spring Boot 可执行 jar 时解决*“无主要清单属性”消息。*

我们看到了这是如何从手动创建的 *pom.xml*文件中产生的，以及如何添加和配置 Spring Maven 插件来修复它。