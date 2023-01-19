## 1. 概述

Java 平台曾经有一个整体架构，将所有包捆绑为一个单元。

在Java9 中，通过引入[Java 平台模块系统 (JPMS)](https://www.baeldung.com/java-9-modularity)或简称模块来简化这一过程。相关的包被归类到模块下，模块取代包成为复用的基本单位。

在本快速教程中，我们将讨论在将现有应用程序迁移到Java9时可能会遇到的一些与模块相关的问题。

## 2. 简单例子

让我们来看一个简单的Java8 应用程序，它包含四个方法，这四个方法在Java8 下有效，但在未来的版本中具有挑战性。我们将使用这些方法来了解迁移到Java9 的影响。

第一个方法获取应用程序中引用的JCE 提供者的名称：

```java
private static void getCrytpographyProviderName() {
    LOGGER.info("1. JCE Provider Name: {}n", new SunJCE().getName());
}
```

第二种方法在堆栈跟踪中列出类的名称：

```java
private static void getCallStackClassNames() {
    StringBuffer sbStack = new StringBuffer();
    int i = 0;
    Class<?> caller = Reflection.getCallerClass(i++);
    do {
        sbStack.append(i + ".").append(caller.getName())
            .append("n");
        caller = Reflection.getCallerClass(i++);
    } while (caller != null);
    LOGGER.info("2. Call Stack:n{}", sbStack);
}
```

第三种方法将Java对象转换为 XML：

```java
private static void getXmlFromObject(Book book) throws JAXBException {
    Marshaller marshallerObj = JAXBContext.newInstance(Book.class).createMarshaller();
    marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

    StringWriter sw = new StringWriter();
    marshallerObj.marshal(book, sw);
    LOGGER.info("3. Xml for Book object:n{}", sw);
}
```

最后一个方法使用来自 JDK 内部库的sun.misc.BASE64Encoder将字符串编码为 Base 64：

```java
private static void getBase64EncodedString(String inputString) {
    String encodedString = new BASE64Encoder().encode(inputString.getBytes());
    LOGGER.info("4. Base Encoded String: {}", encodedString);
}
```

让我们从 main 方法调用所有方法：

```java
public static void main(String[] args) throws Exception {
    getCrytpographyProviderName();
    getCallStackClassNames();
    getXmlFromObject(new Book(100, "Java Modules Architecture"));
    getBase64EncodedString("Java");
}
```

当我们在Java8 中运行这个应用程序时，我们得到以下信息：

```bash
> java -jar targetpre-jpms.jar
[INFO] 1. JCE Provider Name: SunJCE

[INFO] 2. Call Stack:
1.sun.reflect.Reflection
2.com.baeldung.prejpms.App
3.com.baeldung.prejpms.App

[INFO] 3. Xml for Book object:
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<book id="100">
    <title>Java Modules Architecture</title>
</book>

[INFO] 4. Base Encoded String: SmF2YQ==
```

通常，Java 版本保证向后兼容性，但 JPMS改变了其中的一些。

## 3.Java9 中的执行

现在，让我们在Java9 中运行这个应用程序：

```bash
>java -jar targetpre-jpms.jar
[INFO] 1. JCE Provider Name: SunJCE

[INFO] 2. Call Stack:
1.sun.reflect.Reflection
2.com.baeldung.prejpms.App
3.com.baeldung.prejpms.App

[ERROR] java.lang.NoClassDefFoundError: javax/xml/bind/JAXBContext
[ERROR] java.lang.NoClassDefFoundError: sun/misc/BASE64Encoder
```

我们可以看到前两种方法运行良好，而后两种方法失败了。让我们通过分析应用程序的依赖关系来调查失败的原因。我们将使用Java 9 附带的jdeps工具：

```bash
>jdeps targetpre-jpms.jar
   com.baeldung.prejpms            -> com.sun.crypto.provider               JDK internal API (java.base)
   com.baeldung.prejpms            -> java.io                               java.base
   com.baeldung.prejpms            -> java.lang                             java.base
   com.baeldung.prejpms            -> javax.xml.bind                        java.xml.bind
   com.baeldung.prejpms            -> javax.xml.bind.annotation             java.xml.bind
   com.baeldung.prejpms            -> org.slf4j                             not found
   com.baeldung.prejpms            -> sun.misc                              JDK internal API (JDK removed internal API)
   com.baeldung.prejpms            -> sun.reflect                           JDK internal API (jdk.unsupported)
```

命令的输出给出：

-   第一列中我们应用程序中所有包的列表
-   第二列中我们应用程序中所有依赖项的列表
-   依赖项在Java9 平台中的位置——这可以是模块名称，或内部 JDK API，或第三方库没有

## 4. 弃用的模块

现在让我们尝试解决第一个错误java.lang.NoClassDefFoundError: javax/xml/bind/JAXBContext。

根据依赖项列表，我们知道java.xml.bind包属于java.xml.bind 模块 ，这似乎是一个有效的模块。那么，让我们看看[这个模块的官方文档](https://docs.oracle.com/javase/9/docs/api/java.xml.bind-summary.html)吧。

官方文档说java.xml.bind模块已弃用，将在未来的版本中删除。因此，默认情况下该模块不会加载到类路径中。

但是，Java提供了一种通过使用–add-modules选项按需加载模块的方法。那么，让我们继续尝试一下：

```bash
>java --add-modules java.xml.bind -jar targetpre-jpms.jar
...
INFO 3. Xml for Book object:
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<book id="100">
    <title>Java Modules Architecture</title>
</book>
...
```

我们可以看到执行成功了。此解决方案快速简便，但不是最佳解决方案。

作为长期解决方案，我们应该使用 Maven 添加[依赖项](https://search.maven.org/search?q=g:javax.xml.bind AND a:jaxb-api&core=gav)作为第三方库：

```xml
<dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.3.1</version>
</dependency>
```

## 5. JDK 内部 API

现在让我们看看第二个错误java.lang.NoClassDefFoundError: sun/misc/BASE64Encoder。

从依赖列表可以看出sun.misc包是一个JDK内部API。

内部API，顾名思义，就是私有代码，在JDK内部使用。

在我们的示例中，内部 API 似乎已从 JDK 中删除。让我们使用–jdk-internals选项来检查替代 API 是什么：

```bash
>jdeps --jdk-internals targetpre-jpms.jar
...
JDK Internal API                         Suggested Replacement
----------------                         ---------------------
com.sun.crypto.provider.SunJCE           Use java.security.Security.getProvider(provider-name) @since 1.3
sun.misc.BASE64Encoder                   Use java.util.Base64 @since 1.8
sun.reflect.Reflection                   Use java.lang.StackWalker @since 9
```

我们可以看到Java9 推荐使用java.util.Base64而不是sun.misc.Base64Encoder。因此，我们的应用程序必须更改代码才能在Java9 中运行。


请注意，我们在应用程序中使用了另外两个内部 API，Java 平台建议对其进行替换，但我们没有收到任何错误：

-   一些内部 API，如sun.reflect.Reflection 被认为对平台至关重要，因此被添加到特定于 JDK 的 jdk.unsupported模块中。这个模块默认在Java9 的类路径中可用。
-   com.sun.crypto.provider.SunJCE等内部 API仅在某些Java实现中提供。只要使用它们的代码在同一个实现上运行，它就不会抛出任何错误。

在此示例中的所有情况下，我们都使用内部 API，这不是推荐的做法。因此，长期的解决方案是将它们替换为平台提供的合适的公共 API。

## 六，总结

在本文中，我们看到了Java 9中引入的模块系统如何导致一些使用已弃用 API 或内部 API 的旧应用程序出现迁移问题。

我们还看到了如何对这些错误应用短期和长期修复。