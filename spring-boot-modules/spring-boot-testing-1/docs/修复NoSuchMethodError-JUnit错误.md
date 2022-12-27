## 1. 概述

在本文中，我们将学习**如何解决NoSuchMethodError和NoClassDefFoundError JUnit错误**。当我们的类路径中有两个不同的JUnit版本时，通常会出现此类问题。例如，当项目的JUnit版本与Maven或Gradle依赖项中使用的不同时，可能会发生这种情况。

## 2. Spring项目中的NoClassDefFoundError

假设我们有一个使用Spring Boot 2.1.2和spring-boot-starter-test依赖项的Maven项目，有了这样的依赖，我们就可以使用JUnit 5.3.2编写和运行自动化测试，这是spring-boot-test依赖的JUnit版本。

现在，假设我们将继续使用Spring Boot 2.1.2，但是，我们想使用JUnit 5.7.1。一种可能的方法是在我们的pom.xml文件中包含junit-jupiter-api、junit-jupiter-params、junit-jupiter-engine和junit-platform-launcher依赖项：

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.7.1</version>
    <scope>test</scope>
</dependency>
...
```

尽管如此，在这种情况下，当我们运行测试时，我们会得到一个NoClassDefFoundError：

```shell
[ERROR] java.lang.NoClassDefFoundError: org/junit/platform/commons/util/ClassNamePatternFilterUtils
```

如果我们切换到JUnit 5.4.0，将会发生NoSuchMethodError，而不是NoClassDefFoundError。

## 3. 理解并解决错误

如上一节所述，当我们尝试将JUnit版本从5.3.2迁移到5.7.1时，最终出现了NoClassDefFoundError。

发生此错误是**因为我们的类路径最终有两个不同版本的JUnit**，因此，我们的项目是使用较新版本的JUnit(5.7.1)编译，但在运行时发现了旧版本(5.3.2)。因此，JUnit Launcher尝试使用旧版本的JUnit中不可用的类。

接下来，我们将学习解决此错误的不同解决方案。

### 3.1 覆盖Spring的Junit版本

在我们的示例中解决错误的一种有效方法是覆盖Spring管理的JUnit版本：

```xml
<properties>
    <junit-jupiter.version>5.7.1</junit-jupiter.version>
</properties>
```

现在，我们还可以将我们的JUnit依赖项替换为以下依赖项：

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
</dependency>
```

这个单独的依赖包括junit-jupiter-api、junit-jupiter-params和junit-jupiter-engine，junit-platform-launcher依赖项通常仅在我们需要以编程方式运行JUnit测试时才是必需的。

同样，**我们也可以在Gradle项目中覆盖托管版本**：

```groovy
ext['junit-jupiter.version'] = '5.7.1'
```

### 3.2 任何项目的解决方案

在上一节中，我们学习了如何解决Spring项目中的NoSuchMethodError和NoClassDefFoundError JUnit错误，这是最常见的场景。但是，如果发生这些错误并且我们的项目没有使用Spring，我们可以尝试**修复Maven中的依赖项冲突**。

与我们在Spring示例中发生的情况类似，**由于传递依赖关系，一个项目可能有多个JUnit版本**。对于这种情况，我们有一个关于[如何在Maven中解决冲突](https://www.baeldung.com/maven-version-collision)的详细教程。

## 4. 总结

在本教程中，我们重现并学习了如何修复NoSuchMethodError和NoClassDefFoundError JUnit错误。与往常一样，代码片段可以在[GitHub]()上找到。请注意，在此源代码示例中，我们在父项目的pom.xml文件中覆盖了JUnit版本。