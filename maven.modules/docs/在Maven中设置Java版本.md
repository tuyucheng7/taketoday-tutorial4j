## 1. 概述

在本快速教程中，我们将展示**如何在Maven中设置Java版本**。

在继续之前，我们可以**检查Maven的默认JDK版本**，运行mvn -v命令将显示运行Maven的Java版本。

## 2. 使用compiler插件

我们可以在[compiler插件](https://www.baeldung.com/maven-compiler-plugin)中指定需要的Java版本。

### 2.1 compiler插件

第一个选项是在compiler插件属性中设置版本：

```xml
<properties>
    <maven.compiler.target>1.8</maven.compiler.target>
    <maven.compiler.source>1.8</maven.compiler.source>
</properties>
```

Maven compiler接收带有–target和–source版本的命令，如果我们想使用Java 8的语言特性，-source应该设置为1.8。

此外，为了使编译的类与JVM 1.8兼容，–target值应为1.8。

它们的默认值都是1.6版本。

或者，我们可以直接配置compiler插件：

```xml
<plugins>
    <plugin>    
        <artifactId>maven-compiler-plugin</artifactId>
        <configuration>
            <source>1.8</source>
            <target>1.8</target>
        </configuration>
    </plugin>
</plugins>
```

maven-compiler-plugin还具有额外的配置属性，使我们能够在-source和-target版本之外更好地控制编译过程。

### 2.2 Java 9及更高版本

此外，**从JDK 9版本开始，我们可以使用新的-release命令行选项**，这个新参数将自动配置编译器以生成将链接到给定平台版本的实现的类文件。

**默认情况下，-source和-target选项不保证交叉编译**，这意味着我们不能在旧版本的平台上运行我们的应用程序。

此外，要编译和运行旧Java版本的程序，我们还需要指定-bootclasspath选项。

**为了正确地交叉编译，新的-release选项替换了三个标志：-source、-target和-bootclasspath**。

转换示例后，我们可以为插件属性声明以下内容：

```xml
<properties>
    <maven.compiler.release>7</maven.compiler.release>
</properties>
```

而对于3.6版本开始的maven-compiler-plugin，我们可以这么写：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.0</version>
    <configuration>
        <release>7</release>
    </configuration>
</plugin>
```

请注意，**我们可以在新的<release\>属性中添加Java版本**。在这个例子中，我们为Java 7编译我们的应用程序。

更重要的是，我们不需要在我们的机器上安装JDK 7，因为Java 9已经包含了将新语言特性与JDK 7联系起来的所有信息。

## 3. Spring Boot规范

Spring Boot应用程序在pom.xml文件的properties标签内指定JDK版本。

首先，我们需要将spring-boot-starter-parent作为父级添加到我们的项目中：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
</parent>
```

这个父POM允许我们配置默认插件和多个属性，包括Java版本，默认情况下，Java版本是1.8。

但是，我们可以通过指定java.version属性来覆盖父级的默认版本：

```xml
<properties>
    <java.version>9</java.version>
</properties>
```

**通过设置java.version属性，我们声明source和target Java版本都等于1.9**。

最重要的是，我们应该记住这个属性是一个Spring Boot规范。此外，**从Spring Boot 2.0开始，Java 8是最低版本**。

这意味着我们不能为较旧的JDK版本使用或配置Spring Boot。

## 4. 总结

这个快速教程演示了在我们的Maven项目中设置Java版本的可能方法。

以下是主要要点的摘要：

-   **只有在Spring Boot应用程序中才能使用<java.version\>**。
-   对于简单的情况，maven.compiler.source和maven.compiler.target属性应该是最合适的。
-   **最后，要更好地控制编译过程，请使用maven-compiler-plugin配置设置**。