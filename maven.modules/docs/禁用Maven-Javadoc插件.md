## 1. 概述

[Apache Maven Javadoc插件](https://maven.apache.org/plugins/maven-javadoc-plugin/)允许我们在Maven构建期间为指定的项目生成Javadoc，此外，该插件非常方便，因为它使用标准的[javadoc工具](https://docs.oracle.com/en/java/javase/11/tools/javadoc.html)自动生成Javadoc。

在这个快速教程中，我们将了解如何在Maven构建中临时禁用Javadoc生成。

## 2. 问题简介

我们可以在我们的pom.xml中配置Maven Javadoc插件来生成Javadocs并将它们附加到构建的jar文件中，例如：

```xml
...
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-javadoc-plugin</artifactId>
    <executions>
        <execution>
            <id>attach-javadocs</id>
            <goals>
                <goal>jar</goal>
            </goals>
        </execution>
    </executions>
</plugin>
...
```

这很方便。然而，有时，当我们发布时，我们不想将Javadocs附加到jar文件。但是，我们也不想删除Maven Javadoc插件。

因此，我们需要一种方法来跳过构建中的Javadoc生成。接下来，让我们看看如何实现它。

## 3. maven.javadoc.skip选项

Maven Javadoc插件提供了一个[maven.javadoc.skip](https://maven.apache.org/plugins/maven-javadoc-plugin/javadoc-mojo.html#skip)选项来跳过Javadoc生成。

如果我们在构建项目时传递值为true的此选项，我们的Maven构建将不会生成Javadoc：

```bash
mvn clean install -Dmaven.javadoc.skip=true
```

## 4. 使用Maven Release插件跳过Javadoc生成

[Maven Release插件](https://maven.apache.org/maven-release/maven-release-plugin/)广泛用于自动发布管理。

假设我们已经在我们的项目中同时配置了Maven Release插件和Javadoc插件。

现在，我们希望Maven Javadoc插件为常规构建生成Javadoc，但只为发布构建跳过Javadoc生成。

我们可以**使用两种方法**来实现这一目标。

第一种方法是在我们开始发布构建时将参数传递给mvn命令行：

```bash
mvn release:perform -Darguments="-Dmaven.javadoc.skip=true"
```

或者，我们可以在Maven Release插件配置中添加maven.javadoc.skip=true参数：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-release-plugin</artifactId>
    <configuration>
        <arguments>-Dmaven.javadoc.skip=true</arguments>
    </configuration>
</plugin>
```

这样，所有使用Maven Release插件的构建都将跳过Javadoc生成。

## 5. 总结

在这篇快速文章中，我们讨论了如何在pom.xml中配置Maven Javadoc插件时跳过Javadoc生成。