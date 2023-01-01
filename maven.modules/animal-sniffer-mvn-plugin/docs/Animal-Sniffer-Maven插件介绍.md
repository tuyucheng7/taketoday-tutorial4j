## 1. 简介

在使用Java时，有时我们需要同时使用多种语言版本。

通常需要我们的Java程序在编译时与一个Java版本(比如Java 6)兼容，但需要在我们的开发工具中使用不同的版本(比如Java 8)，并且可能需要使用不同的版本来运行应用程序。

在这篇快速文章中，我们将演示添加基于Java版本的不兼容保护措施有多么容易，以及如何使用Animal Sniffer插件在构建时通过检查我们的项目与先前生成的签名来标记这些问题。

## 2. 设置Java编译器的-source和-target

让我们从一个hello world Maven项目开始，我们在本地机器上使用Java 7，但我们想将该项目部署到仍在使用Java 6的生产环境中。

在这种情况下，我们可以使用指向Java 6的source和target字段来配置Maven编译器插件。

“source”字段用于指定与Java语言更改的兼容性，“target”字段用于指定与JVM更改的兼容性。

现在让我们看一下pom.xml的Maven编译器配置：

```xml
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.7.0</version>
        <configuration>
            <source>1.6</source>
            <target>1.6</target>
        </configuration>
    </plugin>
</plugins>
```

在我们的本地机器上使用Java 7并在控制台上打印“hello world”的Java代码，如果我们继续使用Maven构建这个项目，它将在运行Java 6的生产机器上构建并正常工作。

## 3. 引入API不兼容性

现在让我们看看不小心引入API不兼容是多么容易。

假设我们开始处理一些新需求，并且我们使用了Java 7的一些API特性，而这些特性在Java 6中是不存在的。

让我们看一下更新后的源代码：

```java
public static void main(String[] args) {
    System.out.println("Hello World!");
    System.out.println(StandardCharsets.UTF_8.name());
}
```

java.nio.charset.StandardCharsets是在Java 7中引入的。

如果我们现在继续执行Maven构建，它仍然会成功编译，但在运行时会失败，并在安装了Java 6的生产机器上出现链接错误。

**Maven[文档](https://maven.apache.org/plugins/maven-compiler-plugin/examples/set-compiler-source-and-target.html)提到了这个问题，并建议使用Animal Sniffer插件作为解决方案之一**。

## 4. 报告API兼容性

Animal Sniffer插件提供了两个核心功能：

1.  生成Java运行时的签名
2.  根据API签名检查项目

现在让我们修改pom.xml以包含该插件：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>animal-sniffer-maven-plugin</artifactId>
    <version>1.16</version>
    <configuration>
        <signature>
            <groupId>org.codehaus.mojo.signature</groupId>
            <artifactId>java16</artifactId>
            <version>1.0</version>
        </signature>
    </configuration>
    <executions>
        <execution>
            <id>animal-sniffer</id>
            <phase>verify</phase>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

这里，Animal Sniffer的<configuration\>部分引用了现有的Java 6运行时签名。此外，如果发现任何问题，<execution\>部分会根据给定的签名和标志检查并验证项目源代码。

如果我们继续构建Maven项目，构建将失败，插件将按预期报告签名验证错误：

```bash
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.codehaus.mojo:animal-sniffer-maven-plugin:1.16:check 
(animal-sniffer) on project example-animal-sniffer-mvn-plugin: Signature errors found.
Verify them and ignore them with the proper annotation if needed.
```

## 5. 总结

在本教程中，我们探讨了Maven Animal Sniffer插件以及如何在构建时使用它来报告与API相关的不兼容问题(如果有)。