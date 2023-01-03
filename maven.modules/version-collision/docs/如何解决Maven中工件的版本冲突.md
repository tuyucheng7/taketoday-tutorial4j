## 1. 概述

多模块Maven项目可以具有复杂的依赖关系图，这些可能会产生不寻常的结果，模块相互导入的越多。

在本教程中，我们将了解**如何解决Maven中工件的版本冲突**。

我们将从一个[多模块项目](https://www.baeldung.com/maven-multi-module)开始，在该项目中我们特意使用了同一工件的不同版本。然后，我们将了解如何通过排除或依赖管理来防止获取错误版本的工件。

最后，我们将尝试使用maven-enforcer-plugin通过禁止使用传递依赖来使事情更容易控制。

## 2. 工件的版本冲突

我们在项目中包含的每个依赖项都可能链接到其他工件，Maven可以自动引入这些工件，也称为传递依赖项。当多个依赖项链接到同一个工件但使用不同的版本时，就会发生版本冲突。

因此，我们的应用程序**在编译阶段和运行时都可能出现错误**。

### 2.1 项目结构

让我们定义一个多模块项目结构来进行试验，我们的项目由一个version-collision父模块和三个子模块组成：

```bash
version-collision
    project-a
    project-b
    project-collision
```

project-a和project-b的pom.xml几乎相同，唯一的区别是它们所依赖的com.google.guava工件的版本。特别是，project-a使用版本22.0：

```xml
<dependencies>
    <dependency>
        <groupId>com.google.guava</groupId>
        <artifactId>guava</artifactId>
        <version>22.0</version>
    </dependency>
</dependencies>
```

但是，project-b使用较新的版本29.0-jre：

```xml
<dependencies>
    <dependency>
        <groupId>com.google.guava</groupId>
        <artifactId>guava</artifactId>
        <version>29.0-jre</version>
    </dependency>
</dependencies>
```

第三个模块project-collision依赖于其他两个模块：

```xml
<dependencies>
    <dependency>
        <groupId>cn.tuyucheng.taketoday</groupId>
        <artifactId>project-a</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>cn.tuyucheng.taketoday</groupId>
        <artifactId>project-b</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

那么，哪个版本的guava将可用于project-collision？

### 2.2 使用特定依赖版本的功能

我们可以通过在project-collision模块中创建一个简单的测试来找出使用了哪个依赖项，该模块使用guava的Futures.immediateVoidFuture方法：

```java
@Test
public void whenVersionCollisionDoesNotExist_thenShouldCompile() {
    assertThat(Futures.immediateVoidFuture(), notNullValue());
}
```

此方法仅在29.0-jre版本中可用，我们从其他模块之一继承了它，但只有在从project-b获得传递依赖性时才能编译我们的代码。

### 2.3 版本冲突导致的编译错误

根据project-collision模块中的依赖顺序，在某些组合中Maven返回编译错误：

```bash
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.8.1:testCompile (default-testCompile) on project project-collision: Compilation failure
[ERROR] /taketoday-tutorial4j/maven.modules/version-collision/project-collision/src/test/java/cn/tuyucheng/taketoday/version/collision/VersionCollisionUnitTest.java:[12,27] cannot find symbol
[ERROR]   symbol:   method immediateVoidFuture()
[ERROR]   location: class com.google.common.util.concurrent.Futures
```

这是com.google.guava工件版本冲突的结果，默认情况下，对于依赖关系树中同一级别的依赖关系，Maven会选择它找到的第一个库。在我们的例子中，两个com.google.guava依赖项处于相同的高度，并且选择了旧版本。

### 2.4 使用maven-dependency-plugin

maven-dependency-plugin是一个非常有用的工具，可以显示所有依赖项及其版本：

```bash
% mvn dependency:tree -Dverbose

[INFO] --- maven-dependency-plugin:2.8:tree (default-cli) @ project-collision ---
[INFO] cn.tuyucheng.taketoday:project-collision:jar:1.0.0
[INFO] +- cn.tuyucheng.taketoday:project-a:jar:1.0.0:compile
[INFO] |  \- com.google.guava:guava:jar:22.0:compile
[INFO] \- cn.tuyucheng.taketoday:project-b:jar:1.0.0:compile
[INFO]    \- (com.google.guava:guava:jar:29.0-jre:compile - omitted for conflict with 22.0)
```

-Dverbose标志显示冲突的工件，事实上，我们有两个版本的com.google.guava依赖项：22.0和29.0-jre，后者是我们想在project-collision模块中使用的那个。

## 3. 从工件中排除传递依赖

解决版本冲突的一种方法是**从特定工件中删除冲突的传递依赖性**，在我们的示例中，我们不希望从project-a工件中传递地添加com.google.guava库。

因此，我们可以在project-collision pom中排除它：

```xml
<dependencies>
    <dependency>
        <groupId>cn.tuyucheng.taketoday</groupId>
        <artifactId>project-a</artifactId>
        <version>1.0.0</version>
        <exclusions>
            <exclusion>
                <groupId>com.google.guava</groupId>
                <artifactId>guava</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>cn.tuyucheng.taketoday</groupId>
        <artifactId>project-b</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

现在，当我们运行dependency:tree命令时，我们可以看到它不再存在了：

```bash
% mvn dependency:tree -Dverbose

[INFO] --- maven-dependency-plugin:2.8:tree (default-cli) @ project-collision ---
[INFO] cn.tuyucheng.taketoday:project-collision:jar:1.0.0
[INFO] \- cn.tuyucheng.taketoday:project-b:jar:1.0.0:compile
[INFO]    \- com.google.guava:guava:jar:29.0-jre:compile
```

因此，编译阶段没有错误地结束，我们可以使用版本29.0-jre中的类和方法。

## 4. 使用dependencyManagement标签

Maven的dependencyManagement标签是一种**集中依赖信息的机制**，它最有用的功能之一是控制用作传递依赖项的工件的版本。

考虑到这一点，让我们在父pom中创建一个dependencyManagement配置：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>29.0-jre</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

因此，Maven将确保在所有子模块中使用com.google.guava工件的29.0-jre版本：

```bash
% mvn dependency:tree -Dverbose

[INFO] --- maven-dependency-plugin:2.8:tree (default-cli) @ project-collision ---
[INFO] cn.tuyucheng.taketoday:project-collision:jar:1.0.0
[INFO] +- cn.tuyucheng.taketoday:project-a:jar:1.0.0:compile
[INFO] |  \- com.google.guava:guava:jar:29.0-jre:compile (version managed from 22.0)
[INFO] \- cn.tuyucheng.taketoday:project-b:jar:1.0.0:compile
[INFO]    \- (com.google.guava:guava:jar:29.0-jre:compile - version managed from 22.0; omitted for duplicate)
```

## 5. 防止意外的传递依赖

maven-enforcer-plugin提供了许多内置规则来**简化多模块项目的管理**，其中之一**禁止使用来自传递依赖的类和方法**。

显式依赖声明消除了工件版本冲突的可能性，让我们将带有该规则的maven-enforcer-plugin添加到我们的父pom中：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-enforcer-plugin</artifactId>
    <version>3.0.0-M3</version>
    <executions>
        <execution>
            <id>enforce-banned-dependencies</id>
            <goals>
                <goal>enforce</goal>
            </goals>
            <configuration>
                <rules>
                    <banTransitiveDependencies/>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

因此，如果我们想自己使用它，我们现在必须在我们的project-collision模块中显式声明com.google.guava工件，我们必须指定要使用的版本，或者在父pom.xml中设置dependencyManagement。**这使我们的项目更加防错，但要求我们在pom.xml文件中更加明确**。

## 6. 总结

在本文中，我们了解了如何解决Maven中工件的版本冲突。

首先，我们探讨了一个多模块项目中的版本冲突示例。

然后，我们展示了如何排除pom.xml中的传递依赖项，我们研究了如何使用父pom.xml中的dependencyManagement标签来控制依赖项版本。

最后，我们尝试使用maven-enforcer-plugin来禁止传递依赖项的使用，以强制每个模块自行控制。