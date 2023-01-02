## 1. 概述

在本教程中，我们将了解Maven工件分类器的作用。之后，我们将看看它们可能有用的各种场景。在结束之前，我们还将简要讨论一下Maven工件类型。

## 2. 什么是Maven工件分类器？

**Maven工件分类器是一个可选的任意字符串**，它被附加到生成的工件名称的版本号之后。它区分了从相同POM构建但内容不同的工件。

让我们考虑工件定义：

```xml
<groupId>cn.tuyucheng.taketoday</groupId>
<artifactId>maven-classifier-example-provider</artifactId>
<version>1.0.0</version>
```

为此，Maven jar插件生成maven-classifier-example-provider-1.0.0.jar。

### 2.1 使用分类器生成工件 

让我们在jar插件中添加一个分类器配置：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jar-plugin</artifactId>
            <version>3.2.2</version>
            <executions>
                <execution>
                    <id>Arbitrary</id>
                    <goals>
                        <goal>jar</goal>
                    </goals>
                    <configuration>
                        <classifier>arbitrary</classifier>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

结果，生成了maven-classifier-example-provider-1.0.0-arbitrary.jar和maven-classifier-example-provider-1.0.0.jar。

### 2.2 使用分类器使用工件

我们现在可以使用分类器生成带有自定义后缀的工件，让我们看看我们如何使用分类器来使用它。

为了使用默认工件，我们不需要做任何特别的事情：

```xml
<dependency>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>maven-classifier-example-provider</artifactId>
    <version>1.0.0</version>
</dependency>
```

但是，要使用带有自定义后缀“arbitrary”的工件，我们需要在依赖元素中使用<classifier\>标签：

```xml
<dependency>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>maven-classifier-example-provider</artifactId>
    <version>1.0.0</version>
    <classifier>arbitrary</classifier>
</dependency>
```

毕竟，一个很好的问题是为什么我们要使用分类器？因此，让我们看一下分类器有用的一些实际用例。

## 3. 使用源分类器

我们可能已经注意到，每个Maven工件通常都伴随着一个“-sources.jar”工件。它包含源代码，即主要工件的.java文件，它对于调试目的很有用。

### 3.1 生成源工件

首先，让我们为maven-classifier-example-provider模块生成一个sources jar，我们将使用maven-source-plugin来做到这一点：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-source-plugin</artifactId>
    <version>3.2.1</version>
    <executions>
        <execution>
            <id>attach-sources</id>
            <phase>verify</phase>
            <goals>
                <goal>jar-no-fork</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

现在，让我们运行：

```bash
mvn clean install
```

结果，生成了maven-classifier-example-provider-1.0.0-sources.jar工件。

### 3.2 使用源工件

现在，要使用源工件，[有几种方法](https://www.baeldung.com/maven-download-sources-javadoc)，让我们来看看其中的一个。我们可以使用依赖项定义中的<classifier\>标签为选择性依赖项获取源jar：

```xml
<dependency>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>maven-classifier-example-producer</artifactId>
    <version>1.0.0</version>
    <classifier>sources</classifier>
</dependency>
```

现在，让我们运行：

```bash
mvn clean install
```

结果，获取了此特定依赖项的源jar。通常，我们不会这样做，因为它会不必要地污染POM。因此，我们通常更喜欢使用IDE的功能来按需附加源jar。或者，我们也可以通过mvn CLI命令有选择地获取它们：

```bash
mvn dependency:sources -DincludeArtifactIds=maven-classifier-example-provider
```

**简而言之，这里的关键要点是源jar是通过使用源分类器引用的**。

## 4. 使用Javadoc分类器

与sources jar用例一样，我们有Javadoc，它包含主要jar工件的文档。

### 4.1 生成Javadoc工件

让我们使用maven-javadoc-plugin来生成Javadoc工件：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-javadoc-plugin</artifactId>
    <version>3.3.2</version>
    <executions>
        <execution>
            <id>attach-javadocs</id>
            <goals>
                <goal>jar</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

定义好插件后，让我们运行：

```bash
mvn clean install
```

结果，生成了maven-classifier-example-provider-1.0.0-javadoc.jar工件。

### 4.2 使用Javadoc工件

现在让我们从消费者的角度通过mvn下载生成的Javadoc：

```bash
mvn dependency:resolve -Dclassifier=javadoc -DincludeArtifactIds=maven-classifier-example-provider
```

**我们可以注意到-Dclassifier=javadoc选项，它将分类器指定为javadoc**。

## 5. 使用测试分类器

现在，让我们看一下分类器可以提供的不同用例。我们可能出于各种原因想要获取模块的测试jar，首先，我们可能希望重用某些测试存根，让我们看看如何通过分类器将测试独立于主要jar工件。

### 5.1 生成测试Jar

首先，让我们为maven-classifier-example-provider模块生成测试jar，为此，我们将通过指定test-jar目标来使用Maven jar插件：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <version>3.2.2</version>
    <executions>
        <execution>
            <id>Test Jar</id>
            <goals>
                <goal>test-jar</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

现在，让我们运行：

```bash
mvn clean install
```

结果，生成并安装了测试jar maven-classifier-example-provider-1.0.0-tests.jar。

### 5.2 使用测试罐

现在，让我们将测试jar依赖项放入我们的消费者模块maven-classifier-example-consumer中，我们使用测试分类器来做到这一点：

```xml
<dependency>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>maven-classifier-example-provider</artifactId>
    <version>1.0.0</version>
    <classifier>tests</classifier>
</dependency>
```

有了这个，我们应该能够访问maven-classifier-example-provider的测试包中可用的类。 

## 6. 使用分类器支持多个Java版本

早些时候，我们使用了arbitrary分类器为我们的maven-classifier-example-provider模块构建了第二个jar，现在让我们把它用于更实际的用途。

Java现在以6个月的更快节奏发布更新版本，因此，它要求模块开发人员能够支持多个版本的Java。为使用不同Java版本编译的模块构建多个jar包听起来像是一项具有挑战性的任务，这就是Maven分类器派上用场的地方，我们可以使用单个POM构建使用不同Java版本编译的多个jar。

### 6.1 使用多个Java版本进行编译

首先，我们将配置Maven编译器插件以使用JDK 8和JDK 11生成编译后的类文件：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.10.0</version>
    <executions>
        <execution>
            <id>JDK 8</id>
            <phase>compile</phase>
            <goals>
                <goal>compile</goal>
            </goals>
            <configuration>
                <source>8</source>
                <target>8</target>
                <fork>true</fork>
            </configuration>
        </execution>
        <execution>
            <id>JDK 11</id>
            <phase>compile</phase>
            <goals>
                <goal>compile</goal>
            </goals>
            <configuration>
                <outputDirectory>${project.build.outputDirectory}_jdk11</outputDirectory>
                <executable>${jdk.11.executable.path}</executable>
                <source>8</source>
                <target>11</target>
                <fork>true</fork>
            </configuration>
        </execution>
    </executions>
</plugin>
```

我们正在设置两个<execution\>块，一个用于Java 8，另一个用于Java 11。

在此之后，我们为Java 11配置了不同的<outputDirectory\>。另一方面，Java 8编译将使用默认的<outputDirectory\>。

接下来是源和目标Java版本配置，源代码是使用Java 8编写的，因此我们将源版本保留为8，并修改Java 11 <execution>块的目标版本为11。

我们还明确提供了Java 11的编译器javac的可执行路径。对于Java 8，Maven将使用系统上配置的默认javac，在本例中是Java 8的javac：

```bash
mvn clean compile
```

**因此，将在target文件夹中生成两个文件夹：classes和classes_jdk11**。

### 6.2 为多个Java版本生成Jar工件

其次，我们可以继续使用Maven jar插件将模块打包到两个单独的jar中：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <version>3.2.2</version>
    <executions>
        <execution>
            <id>default-package-jdk11</id>
            <phase>package</phase>
            <goals>
                <goal>jar</goal>
            </goals>
            <configuration>
                <classesDirectory>${project.build.outputDirectory}_jdk11</classesDirectory>
                <classifier>jdk11</classifier>
            </configuration>
        </execution>
    </executions>
</plugin>
```

**我们可以注意到分类器设置为jdk11**。

现在，让我们运行：

```bash
mvn clean install
```

结果，生成了两个jar：maven-classifier-example-provider-1.0.0-jdk11.jar和maven-classifier-example-provider-1.0.0.jar。第一个是使用Java 11编译器编译的，第二个是使用Java 8编译器编译的。

### 6.3 使用特定Java版本的Jar工件

最后，我们模块的使用者可以根据他们使用的Java版本选择与他们相关的jar工件，让我们看看如何从消费者的角度使用这些jar。

如果我们正在处理Java 8项目，则无需执行任何特殊操作，我们将只添加一个正常依赖：

```xml
<dependency>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>maven-classifier-example-provider</artifactId>
    <version>1.0.0</version>
</dependency>
```

但是，如果我们正在处理Java 11项目，我们必须通过<classifier\>明确要求使用Java 11编译的jar：

```xml
<dependency>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>maven-classifier-example-provider</artifactId>
    <version>1.0.0</version>
    <classifier>jdk11</classifier>
</dependency>
```

## 7. 工件分类器与工件类型

在结束本文之前，让我们简要了解一下工件类型，虽然工件类型和分类器密切相关，但它们不可互换。工件类型定义工件的打包格式，例如POM或jar，在大多数情况下，它还会转换为文件扩展名，它也可能有一个相应的分类器。

**换句话说，**[工件类型](https://maven.apache.org/ref/3.8.4/maven-core/artifact-handlers.html)**处理工件的打包，并且可以使用分类器来修改生成的工件的名称**。

我们之前在Java源和Javadoc中看到的两个用例是使用sources和javadoc作为分类器的工件类型的示例。

## 8. 总结

在本文中，我们了解了分类器如何提供一种从单个POM文件生成多个工件的方法，使用分类器，使用者可以根据需要从特定模块的各种工件中挑选。