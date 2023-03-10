## 1. 概述

在本文中，我们将了解为什么[JUnit](https://www.baeldung.com/junit)中会出现java.lang.NoClassDefFoundError以及如何修复它。这个问题主要与IDE的配置有关。因此，我们将专注于最流行的IDE来重现和解决此错误。

## 2. 什么是java.lang.NoClassDefFoundError

当Java运行时运行Java程序时，它不会一次性加载所有的类和依赖项。相反，它调用Java类加载器在需要时将类加载到内存中。**在加载类时，如果类加载器找不到类的定义，它会抛出NoClassDefFoundError**。

Java找不到类定义的几个原因有：

+ 缺少一些依赖的jar包，这是最常见的原因
+ 所有jar都作为依赖项添加，但路径错误
+ 依赖中的版本不匹配

## 3. IntelliJ IDEA

**运行JUnit 5测试用例需要jupiter-engine和jupiter-api这两个依赖**。jupiter-engine在内部依赖于jupiter-api，因此大多数时候，只需在pom.xml中添加jupiter-engine依赖项就足够了。但是，如果在我们的pom.xml中仅添加jupiter-api依赖项而缺少jupiter-engine依赖项就会导致NoClassDefFoundError。

pom.xml中不正确的设置是这样的：

```xml
<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-api</artifactId>
        <version>5.8.1</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

使用此设置运行一个简单的测试用例会产生以下堆栈跟踪：

```shell
Exception in thread "main" java.lang.NoClassDefFoundError: org/junit/platform/engine/TestDescriptor
	at java.base/java.lang.Class.forName0(Native Method)
	at java.base/java.lang.Class.forName(Class.java:375)
	at com.intellij.rt.junit.JUnitStarter.getAgentClass(JUnitStarter.java:230)
....
```

在IntelliJ中，要更正依赖关系，我们需要更正pom.xml如下所示：

```xml
<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-api</artifactId>
        <version>5.8.1</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
        <version>5.8.1</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

或者，我们可以简单的添加junit-jupiter-engine，因为添加它会自动将junit-jupiter-api jar添加到类路径中并解决错误。

## 4. 总结

在本文中，我们介绍了JUnit中出现java.lang.NoClassDefFoundError的不同原因，并演示了如何在Intellij IDEA中解决错误。