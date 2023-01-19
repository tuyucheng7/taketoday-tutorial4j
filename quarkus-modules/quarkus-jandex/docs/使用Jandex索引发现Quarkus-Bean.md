## 1. 概述

在本文中，我们将了解 Quarkus 和经典 Jakarta EE 环境中的 bean 发现之间的区别。我们将重点关注如何确保 Quarkus 可以发现外部模块中的注解类。

## 2. 为什么 Quarkus 需要索引

[Quarkus](https://quarkus.io/)的主要优势之一是其极快的启动时间。为实现这一点，Quarkus 将类路径注解扫描等步骤从运行时向前移动到构建时。为此，我们需要在构建时声明所有依赖项。

因此，不再可能在运行时环境中通过类路径扩展来增强应用程序。当在构建期间收集元数据时，索引加入游戏。索引意味着将元数据存储在索引文件中。这允许应用程序在启动时或需要时快速读取它。

让我们使用一个简单的草图来检查差异：

 

[![q1](https://www.baeldung.com/wp-content/uploads/2021/11/q1.svg)](https://www.baeldung.com/wp-content/uploads/2021/11/q1.svg)

Quarkus 使用[Jandex](https://github.com/wildfly/jandex)来创建和读取索引。

## 3. 创建索引

对于我们 Quarkus 项目中的类，我们不需要做任何特殊的事情——Quarkus Maven 插件会自动生成索引。但是，我们需要注意依赖关系——项目内部模块和外部库。

### 3.1. Jandex Maven 插件

为我们自己的模块实现这一点的最明显方法是使用[Jandex Maven 插件](https://github.com/wildfly/jandex-maven-plugin)：

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

这个插件创建一个“META-INF/jandex.idx”文件打包到 JAR 中。当库在运行时提供时，Quarkus 将读出该文件。因此，每个包含此类文件的库都会隐式增强索引。

对于 Gradle 构建，我们可以使用org.kordamp.gradle.jandex插件：

```groovy
plugins {
    id 'org.kordamp.gradle.jandex' version '0.11.0'
}
```

### 3.2. 应用程序属性

如果我们不能修改依赖项(例如，在外部库的情况下)，我们需要在 Quarkus 项目的application.properties文件中明确指定它们：

```
quarkus.index-dependency.<name>.group-id=<groupId>quarkus.index-dependency.<name>.artifact-id=<artifactId>quarkus.index-dependency.<name>.classifier=(optional)
```

### 3.3. 框架特定的含义

除了使用 Jandex Maven 插件，一个模块还可以包含一个META-INF/beans.xml文件。这实际上是[经过一些调整后被采用到 Quarkus](https://quarkus.io/guides/cdi-reference)中的 CDI 技术的一部分，但我们并不局限于仅使用 CDI 管理的 bean。例如，我们还可以声明 JAX-RS 资源，因为索引的范围是整个模块。

## 4. 总结

本文已确定 Quarkus 需要一个 Jandex 索引来在运行时检测带注解的类。索引是在构建时生成的，因此标准技术不会检测构建后添加到类路径中的带注解的类。

一如既往，所有代码都可以[在 GitHub 上找到](https://github.com/eugenp/tutorials/tree/master/quarkus-modules/quarkus-jandex)。有一个多模块项目，其中包含一个 Quarkus 应用程序和一些提供 CDI 管理的 bean 的依赖项。