## 1. 概述

通常，将许多Java类文件捆绑到一个归档文件中会很方便。

在本教程中，我们将介绍在Java中 使用 jar(或J ava AR chive)文件的来龙去脉。

具体来说，我们将采用一个简单的应用程序并探索将其打包并作为 jar 运行的不同方法。我们还将回答一些好奇心，例如如何轻松读取 jar 的清单文件 。

## 2.Java程序设置

在我们可以创建一个可运行的 jar 文件之前，我们的应用程序需要有一个带有[main方法](https://www.baeldung.com/java-main-method)的类。这个类提供了我们进入应用程序的入口点：

```java
public static void main(String[] args) {
    System.out.println("Hello Baeldung Reader!");
}
```

## 3.罐子命令

现在我们都设置好了，让我们编译我们的代码并创建我们的 jar 文件。

我们可以 从命令行使用[javac来做到这一点：](https://www.baeldung.com/javac)

```shell
javac com/baeldung/jar/.java
```

javac命令 在com/baeldung/jar目录中创建JarExample.class。我们现在可以将它打包成一个 jar 文件。

### 3.1. 使用默认值

要创建 jar 文件，我们将使用jar命令。

要使用jar命令创建 jar 文件，我们需要使用 c选项来指示我们正在创建文件，并使用 f选项来指定文件：

```shell
jar cf JarExample.jar com/baeldung/jar/.class
```

### 3.2. 设置主类

包含主类的 jar 文件清单很有帮助。

清单是位于META-INF目录并名为MANIFEST.MF 的jar 中的特殊文件。 清单文件包含有关 jar 文件中文件的特殊元信息。

我们可以使用清单文件的一些示例包括设置入口点、设置版本信息和配置类路径。

通过使用e选项，我们可以指定入口点， jar命令会将其添加到生成的清单文件中。

让我们运行 带有指定入口点的jar ：

```shell
jar cfe JarExample.jar jar.cn.tuyucheng.taketoday.JarExample com/baeldung/jar/.class
```

### 3.3. 更新内容

假设我们对其中一个类进行了更改并重新编译了它。现在，我们需要更新我们的 jar 文件。

让我们使用带有u 选项的jar命令来更新它的内容：

```shell
jar uf JarExample.jar com/baeldung/jar/JarExample.class
```

### 3.4. 设置清单文件

在某些情况下，我们可能需要更多地控制清单文件中的内容。jar命令提供了提供我们自己的清单信息的功能。

让我们将名为example_manifest.txt的部分清单文件添加到我们的应用程序以设置我们的入口点：

```plaintext
Main-Class: jar.cn.tuyucheng.taketoday.JarExample
```

我们提供的清单信息将添加到 jar 命令生成的内容中，因此它是我们在文件中唯一需要的行。

以换行符结束我们的清单文件很重要。如果没有换行符，我们的清单文件将被忽略。

通过该设置，让我们使用我们的清单信息和m 选项再次创建我们的 jar：

```shell
jar cfm JarExample.jar com/baeldung/jar/example_manifest.txt com/baeldung/jar/.class
```

### 3.5. 详细输出

如果我们想从jar命令中获得更多信息，我们可以简单地添加 v选项来表示详细信息。

让我们使用v选项运行我们的jar命令：

```shell
jar cvfm JarExample.jar com/baeldung/jar/example_manifest.txt com/baeldung/jar/.class
added manifest
adding: com/baeldung/jar/JarExample.class(in = 453) (out= 312)(deflated 31%)
```

## 4. 使用 Maven

### 4.1. 默认配置

我们也可以使用 Maven 来创建我们的 jar。由于 Maven 倾向于约定而不是配置，我们可以只运行 package 来创建我们的 jar 文件。

```shell
mvn package
```

默认情况下，我们的 jar 文件将添加到项目中的目标文件夹中。

### 4.2. 指示主类

我们还可以配置 Maven 来指定主类并创建一个 [可执行的 jar 文件](https://www.baeldung.com/executable-jar-with-maven)。

```xml

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <version>${maven-jar-plugin.version}</version>
    <configuration>
        <archive>
            <manifest>
                <mainClass>jar.cn.tuyucheng.taketoday.JarExamplecn.tuyucheng.taketoday.jar.JarExample</mainClass>
            </manifest>
        </archive>
    </configuration>
</plugin>
```

## 5. 使用 Spring Boot

### 5.1. 使用 Maven 和默认值

如果我们将 Spring Boot 与 Maven 一起使用，我们应该首先确认我们的打包设置在我们的pom.xml文件中设置为jar而不是war。

```xml
<modelVersion>4.0.0</modelVersion>
<artifactId>spring-boot</artifactId>
<packaging>jar</packaging>
<name>spring-boot</name>
```

一旦我们知道已经配置好了，我们就可以运行包目标：

```shell
mvn package
```

### 5.2. 设置入口点

设置我们的主类是我们发现使用常规Java应用程序创建 jar 和[为 Spring Boot 应用程序创建 fat jar](https://www.baeldung.com/deployable-fat-jar-spring-boot)之间的区别的地方。在 Spring Boot 应用程序中，[主类](https://www.baeldung.com/spring-boot-main-class)实际上是org.springframework.boot.loader.JarLauncher。

虽然我们的示例不是 Spring Boot 应用程序，但我们可以轻松地将其设置为[Spring Boot 控制台应用程序](https://www.baeldung.com/spring-boot-console-app)。

我们的主类应该指定为起始类：

```xml

<properties>
    <start-class>jar.cn.tuyucheng.taketoday.JarExamplecn.tuyucheng.taketoday.jar.JarExample</start-class>
    <!-- Other properties -->
</properties>
```

我们还可以使用[Gradle 创建一个 Spring Boot fat jar](https://www.baeldung.com/gradle-fat-jar)。

## 6.运行罐子

现在我们已经有了我们的 jar 文件，我们可以运行它了。 我们使用java命令运行 jar 文件。

### 6.1. 推断主类

由于我们已经继续并确保我们的主类在清单中指定，我们可以使用java命令的-jar选项来运行我们的应用程序而不指定主类：

```shell
java -jar JarExample.jar
```

### 6.2. 指定主类

我们还可以在运行应用程序时指定主类。 我们可以使用-cp选项来确保我们的 jar 文件在类路径中，然后以package.className格式提供我们的主类：

```shell
java -cp JarExample.jar jar.cn.tuyucheng.taketoday.JarExample
```

使用路径分隔符而不是包格式也可以：

```shell
java -cp JarExample.jar com/baeldung/jar/JarExample
```

### 6.3. 列出 Jar 的内容

我们可以使用jar命令列出我们的 jar 文件的内容：

```shell
jar tf JarExample.jar
META-INF/
META-INF/MANIFEST.MF
com/baeldung/jar/JarExample.class
```

### 6.4. 查看清单文件

由于了解我们的MANIFEST.MF文件中的内容可能很重要，因此让我们看一下无需离开命令行即可查看内容的快速简便的方法。

让我们使用带有 -p 选项的解压缩命令：

```shell
unzip -p JarExample.jar META-INF/MANIFEST.MF
Manifest-Version: 1.0
Created-By: 1.8.0_31 (Oracle Corporation)
Main-Class: jar.cn.tuyucheng.taketoday.JarExample
```

## 七、总结

在本教程中，我们设置了一个带有主类的简单Java应用程序。

然后我们研究了创建 jar 文件的三种方法：使用jar命令、使用 Maven 和使用 Maven Spring Boot 应用程序。

创建 jar 文件后，我们返回命令行并使用推断的和指定的主类运行它们。

我们还学习了如何显示文件的内容以及如何显示 jar 中单个文件的内容。