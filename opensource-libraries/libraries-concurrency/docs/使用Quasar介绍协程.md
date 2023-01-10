## 1. 简介

协程是Java线程的替代方案，因为它们提供了一种在非常高的并发级别上执行可中断任务的方法，但在 Loom 项目完成之前，我们必须寻求库支持才能获得它。

在本教程中，我们将了解 Quasar，这是一个提供协程支持的库。

## 2.设置

我们将使用需要Java11 或更高版本的最新版本的 Quasar。但是，该示例应用程序也可以与兼容Java7 和 8 的 Quasar 的早期版本一起使用。

Quasar 提供了我们需要在构建中包含的三个依赖项：

```java
<dependency>
    <groupId>co.paralleluniverse</groupId>
    <artifactId>quasar-core</artifactId>
    <version>0.8.0</version>
</dependency>
<dependency>
    <groupId>co.paralleluniverse</groupId>
    <artifactId>quasar-actors</artifactId>
    <version>0.8.0</version>
</dependency>
<dependency>
    <groupId>co.paralleluniverse</groupId>
    <artifactId>quasar-reactive-streams</artifactId>
    <version>0.8.0</version>
</dependency>

```

Quasar 的实现依赖于字节码检测才能正常工作。要执行字节码检测，我们有两个选择：

-   在编译时，或
-   在运行时使用Java代理

使用Java代理是首选方式，因为它没有任何特殊的构建要求并且适用于任何设置。

### 2.1. 使用 Maven 指定Java代理

要使用 Maven 运行Java代理，我们需要包含[maven-dependency-plugin](https://maven.apache.org/plugins/maven-dependency-plugin/)以始终运行属性目标：

```java
<plugin>
    <artifactId>maven-dependency-plugin</artifactId>
    <version>3.1.1</version>
    <executions>
        <execution>
            <id>getClasspathFilenames</id>
            <goals>
               <goal>properties</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

properties目标将生成一个指向类路径上quasar-core.jar位置的属性。

为了执行我们的应用程序，我们将使用[exec-maven-plugin](https://www.mojohaus.org/exec-maven-plugin/)：

```java
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.0.0</version>
    <configuration>
        <workingDirectory>target/classes</workingDirectory>
        <executable>echo</executable>
        <arguments>
            <argument>-javaagent:${co.paralleluniverse:quasar-core:jar}</argument>
            <argument>-classpath</argument> <classpath/>
            <argument>com.baeldung.quasar.QuasarHelloWorldKt</argument>
        </arguments>
    </configuration>
</plugin>
```

要使用该插件并启动我们的应用程序，我们将运行 Maven：

```java
mvn compile dependency:properties exec:exec
```

## 3. 实现协程

为了实现协程，我们将使用Quasar 库中的Fibers。纤程提供将由 JVM 而不是操作系统管理的轻量级线程。因为它们需要很少的 RAM 并且给 CPU 带来的负担要小得多，所以我们可以在我们的应用程序中拥有数百万个它们而不必担心性能。

要启动纤程，我们创建Fiber<T>类的实例，它将包装我们要执行的代码并调用start方法：

```java
new Fiber<Void>(() -> {
    System.out.println("Inside fiber coroutine...");
}).start();
```

## 4. 总结

在本文中，我们介绍了如何使用 Quasar 库实现协程。我们在这里看到的只是一个最小的工作示例，而 Quasar 库能够做更多的事情。