## 1. 概述

当使用Maven管理我们的项目依赖项时，我们可能会忘记在我们的应用程序中使用了哪些依赖项。

**在这个简短的教程中，我们将介绍如何使用**[Maven dependency插件](https://mvnrepository.com/artifact/org.apache.maven.plugins/maven-dependency-plugin)，该插件可以帮助我们找到项目中未使用的依赖项。

## 2. 项目设置

让我们首先添加一些依赖项，[slf4j-api](https://mvnrepository.com/artifact/org.slf4j/slf4j-api)(我们将使用的)和[common-collections](https://mvnrepository.com/artifact/commons-collections/commons-collections)(我们不会使用的)：

```xml
<dependencies>
    <dependency>
        <groupId>commons-collections</groupId>
        <artifactId>commons-collections</artifactId>
        <version>3.2.2</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.7.25</version>
    </dependency>
</dependencies>
```

我们可以访问[Maven dependency插件](https://search.maven.org/artifact/org.apache.maven.plugins/maven-dependency-plugin)，而无需在我们的pom.xml中指定它。在任何情况下，我们都可以使用pom.xml定义来指定版本和一些属性(如果需要)：

```xml
<build>
    <plugins>
        <plugin>
            <artifactId>maven-dependency-plugin</artifactId>
            <version>3.1.2</version>
        </plugin>
    </plugins>
</build>
```

## 3. 代码示例

现在我们已经设置了我们的项目，**让我们编写一行代码，我们在其中使用我们之前定义的依赖项之一**：

```java
public Logger getLogger() {
    return LoggerFactory.getLogger(UnusedDependenciesExample.class);
}
```

Slf4J库中的LoggerFactory创建的Logger对象在一个方法中返回，**但没有使用commons-collections库，使其成为删除的候选对象**。

## 4. 查找未使用的依赖项

使用Maven dependency插件，我们可以找到我们项目中没有使用的依赖项。为此，我们调用插件的analyze目标：

```bash
$ mvn dependency:analyze

[INFO] --- maven-dependency-plugin:3.1.1:analyze (default-cli) @ maven-unused-dependencies ---
[WARNING] Unused declared dependencies found:
[WARNING]    commons-collections:commons-collections:jar:3.2.2:compile
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 1.225 s
[INFO] Finished at: 2020-04-01T16:10:25-04:00
[INFO] ------------------------------------------------------------------------
```

对于我们项目中未使用的每个依赖项，Maven都会在分析报告中发出警告。

## 5. 指定使用的依赖关系

根据项目的性质，有时我们可能需要在运行时加载类，例如在面向插件的项目中。

由于在编译时未使用依赖项，因此maven-dependency-plugin会发出警告，指出依赖项未被使用，而实际上它已被使用。为此，我们可以强制并指示插件正在使用一个库。

我们通过在usedDependencies属性中列出依赖项来做到这一点：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <configuration>
        <usedDependencies>
            <dependency>commons-collections:commons-collections</dependency>
        </usedDependencies>
    </configuration>
</plugin>
```

再次运行analyze目标，我们看到报告中不再考虑未使用的依赖项。

## 6. 总结

在这个简短的教程中，我们学习了如何查找未使用的Maven依赖项。定期检查未使用的依赖项是一种很好的做法，因为它可以提高可维护性并减少项目的库大小。