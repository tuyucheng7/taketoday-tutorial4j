## 1. 概述

在本文中，我们将了解Quarkus和经典Jakarta EE环境中的bean发现之间的区别。我们将重点介绍如何确保Quarkus可以发现外部模块中的注解类。

## 2. 为什么Quarkus需要索引

[Quarkus](https://quarkus.io/)的主要优势之一是其极快的启动时间。为了实现这一点，Quarkus将类路径注解扫描等步骤从运行时向前移动到构建时。为此，我们需要在构建时声明所有依赖项。

因此，在运行时环境中通过类路径扩展来增强应用程序已不再可能。当在构建期间收集元数据时，索引参与进来。索引意味着将元数据存储在索引文件中。这允许应用程序在启动时或需要时快速读取它。

让我们使用一个简单的草图来检查差异：

<img src="../assets/img.png">

Quarkus使用[Jandex](https://github.com/wildfly/jandex)来创建和读取索引。

## 3. 创建索引

对于我们Quarkus项目中的类，我们不需要做任何特殊的事情-Quarkus Maven插件会自动生成索引。但是，我们需要注意依赖关系，项目内部模块和外部库。

### 3.1 Jandex Maven插件

对于我们自己的模块来说，实现这一点的最明显方法是使用[Jandex Maven插件](https://github.com/wildfly/jandex-maven-plugin)：

```xml
<build>
    <plugins>
        <plugin>
            <!-- https://github.com/wildfly/jandex-maven-plugin -->
            <groupId>org.jboss.jandex</groupId>
            <artifactId>jandex-maven-plugin</artifactId>
            <version>1.2.1</version>
            <executions>
                <execution>
                    <id>make-index</id>
                    <goals>
                        <!-- phase is 'process-classes by default' -->
                        <goal>jandex</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

这个插件创建一个打包到JAR中的“META-INF/jandex.idx”文件。当库在运行时提供时，Quarkus将读出该文件。因此，每个包含此类文件的库都会隐式增强索引。

对于Gradle构建，我们可以使用org.kordamp.gradle.jandex插件：

```groovy
plugins {
    id 'org.kordamp.gradle.jandex' version '0.11.0'
}
```

### 3.2 应用程序属性

如果我们不能修改依赖项(例如，在外部库的情况下)，我们需要在Quarkus项目的application.properties文件中明确指定它们：

```properties
quarkus.index-dependency.<name>.group-id=<groupId>
quarkus.index-dependency.<name>.artifact-id=<artifactId>
quarkus.index-dependency.<name>.classifier=(optional)
```

### 3.3 框架特定的含义

除了使用Jandex Maven插件，一个模块还可以包含一个META-INF/beans.xml文件。这实际上是[经过一些调整](https://quarkus.io/guides/cdi-reference)后被采用到Quarkus中的CDI技术的一部分，但我们并不局限于仅使用CDI管理的bean。例如，我们还可以声明JAX-RS资源，因为索引的范围是整个模块。

## 4. 总结

本文已确定Quarkus需要一个Jandex索引来在运行时检测带注解的类。索引是在构建时生成的，因此标准技术不会检测构建后添加到类路径中的带注解的类。