## 1. 概述

多年来，我们一直在采取多种策略，从 Elvis 运算符到[Optional](https://www.baeldung.com/java-optional)，以帮助从我们的应用程序中删除NullPointerException 。在本教程中，我们将了解 Uber 对对话的贡献[NullAway](https://github.com/uber/NullAway)以及如何使用它。

NullAway 是一个构建工具，可以帮助我们消除Java 代码中的NullPointerException (NPE)。

此工具执行一系列基于类型的本地检查，以确保代码中取消引用的任何指针都不能为null。它的构建时间开销很低，可以配置为在每次构建代码时运行。

## 2. 安装

我们来看看如何安装 NullAway 及其依赖。在此示例中，我们将使用 Gradle 配置 NullAway。

NullAway 依赖于 [Error Prone](http://errorprone.info/)。因此，我们将添加容易出错的插件：

```groovy
plugins {
  id "net.ltgt.errorprone" version "1.1.1"
}
```

我们还将在不同范围内添加四个依赖项：annotationProcessor、compileOnly、errorprone和errorproneJavac：

```groovy
dependencies {
  annotationProcessor "com.uber.nullaway:nullaway:0.7.9"
  compileOnly "com.google.code.findbugs:jsr305:3.0.2"
  errorprone "com.google.errorprone:error_prone_core:2.3.4"
  errorproneJavac "com.google.errorprone:javac:9+181-r4173-1"
}

```

最后，我们将添加配置 NullAway 在编译期间如何工作的 Gradle 任务：

```groovy
import net.ltgt.gradle.errorprone.CheckSeverity

tasks.withType(JavaCompile) {
    options.errorprone {
        check("NullAway", CheckSeverity.ERROR)
        option("NullAway:AnnotatedPackages", "com.baeldung")
    }
}
```

上述任务将 NullAway 严重性设置为错误级别，这意味着我们可以配置 NullAway 以在出现错误时停止构建。默认情况下，NullAway 只会在编译时警告用户。

此外，该任务将包设置为检查空取消引用。

就是这样，我们现在可以在我们的Java代码中使用该工具了。

同样，我们可以使用[其他构建系统](https://github.com/uber/NullAway/wiki/Configuration#other-build-systems)，Maven 或 Bazel来集成该工具。

## 3.用法

假设我们有一个包含年龄属性的Person类。此外，我们还有一个将Person实例作为参数的getAge方法：

```java
Integer getAge(Person person) {
    return person.getAge();
}
```

此时，我们可以看到如果person为null getAge将抛出NullPointerException。

NullAway 假定每个方法参数、返回值和字段都是非空的。因此，它期望person实例是非null的。

并且我们还要说我们代码中的某个地方确实将空引用传递给getAge：

```java
Integer yearsToRetirement() {
    Person p = null;
    // ... p never gets set correctly...
    return 65 - getAge(p);
}
```

然后，运行构建将产生以下错误：

```java
error: [NullAway] passing @Nullable parameter 'null' where @NonNull is required
    getAge(p);
```

我们可以通过向参数添加@Nullable注解来修复此错误：

```java
Integer getAge(@Nullable Person person) { 
    // ... same as earlier
}
```

现在，当我们运行构建时，我们会看到一个新错误：

```java
error: [NullAway] dereferenced expression person is @Nullable
    return person.getAge();
            ^
```

这告诉我们person实例有可能为null。我们可以通过添加标准的 null 检查来解决这个问题：

```java
Integer getAge(@Nullable Person person) {
    if (person != null) {
        return person.getAge();
    } else {
        return 0;
    }
}
```

## 4. 总结

在本教程中，我们了解了如何使用 NullAway 来限制遇到NullPointerException的可能性。