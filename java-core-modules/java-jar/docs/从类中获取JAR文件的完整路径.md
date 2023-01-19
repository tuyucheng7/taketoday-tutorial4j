## 1. 概述

JAR 文件是Java档案。在构建Java应用程序时，我们可能会包含各种 JAR 文件作为库。

在本教程中，我们将探讨如何从给定类中查找 JAR 文件及其完整路径。

## 二、问题简介

假设我们在运行时有一个Class对象。我们的目标是找出该类属于哪个 JAR 文件。

一个例子可以帮助我们快速理解问题。假设我们有[Guava](https://www.baeldung.com/guava-guide)的Ascii类的类实例。我们想创建一个方法来找出包含Ascii类的 JAR 文件的完整路径。

我们将主要介绍两种不同的方法来获取 JAR 文件的完整路径。此外，我们将讨论它们的优缺点。

为简单起见，我们将通过单元测试断言来验证结果。

接下来，让我们看看它们的实际效果。

## 3. 使用getProtectionDomain()方法

Java的类对象提供了[getProtectionDomain()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Class.html#getProtectionDomain())方法来获取ProtectionDomain对象。然后，我们可以通过ProtectionDomain对象获取[CodeSource](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/CodeSource.html)。CodeSource实例将是我们要查找的JAR文件。此外，[CodeSource.getLocation()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/CodeSource.html#getLocation())方法为我们提供了JAR 文件的[URL对象。](https://www.baeldung.com/java-url-vs-uri)最后，我们可以使用[Paths](https://www.baeldung.com/java-nio-2-path)类来获取 JAR 文件的完整路径。

### 3.1. 实现byGetProtectionDomain()方法

如果我们将上面提到的所有步骤包装在一个方法中，那么几行代码就可以完成这项工作：

```java
public class JarFilePathResolver {
    String byGetProtectionDomain(Class clazz) throws URISyntaxException {
        URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
        return Paths.get(url.toURI()).toString();
    }
}

```

下面我们以Guava Ascii类为例，测试一下我们的方法是否如预期的那样工作：

```java
String jarPath = jarFilePathResolver.byGetProtectionDomain(Ascii.class);
assertThat(jarPath).endsWith(".jar").contains("guava");
assertThat(new File(jarPath)).exists();

```

如我们所见，我们已经通过两个断言验证了返回的jarPath ：

-   首先，路径应指向 Guava JAR 文件
-   如果jarPath是有效的完整路径，我们可以从jarPath创建一个File对象，并且该文件应该存在

如果我们运行测试，它就会通过。因此byGetProtectionDomain()方法按预期工作。

### 3.2. getProtectionDomain()方法的一些限制

如上面的代码所示，我们的byGetProtectionDomain()方法 非常简洁明了。但是，如果我们阅读getProtectionDomain()方法的 JavaDoc ，它说getProtectionDomain ()方法可能会抛出SecurityException。

我们已经编写了一个单元测试，并且测试通过了。这是因为我们正在本地开发环境中测试该方法。在我们的示例中，Guava JAR 位于我们本地的[Maven](https://www.baeldung.com/maven)存储库中。因此，没有引发SecurityException 。

但是有些平台，比如[Java/OpenWebStart](https://www.baeldung.com/java-web-start)和一些应用服务器，可能会禁止调用getProtectionDomain()方法获取ProtectionDomain对象 。因此，如果我们将应用程序部署到那些平台，我们的方法将失败并抛出SecurityException。

接下来，让我们看看另一种获取 JAR 文件完整路径的方法。

## 4. 使用getResource()方法

我们知道我们调用[Class.getResource](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Class.html#getResource(java.lang.String)) ()方法来获取类的资源的URL对象。那么我们就从这个方法入手，最终解析出对应JAR文件的完整路径。

### 4.1. 实现byGetResource()方法

让我们首先看一下实现，然后了解它是如何工作的：

```java
String byGetResource(Class clazz) {
    URL classResource = clazz.getResource(clazz.getSimpleName() + ".class");
    if (classResource == null) {
        throw new RuntimeException("class resource is null");
    }
    String url = classResource.toString();
    if (url.startsWith("jar:file:")) {
        // extract 'file:......jarName.jar' part from the url string
        String path = url.replaceAll("^jar:(file:.[.]jar)!/.", "$1");
        try {
            return Paths.get(new URL(path).toURI()).toString();
        } catch (Exception e) {
            throw new RuntimeException("Invalid Jar File URL String");
        }
    }
    throw new RuntimeException("Invalid Jar File URL String");
}

```

与byGetProtectionDomain方法相比，上面的方法看起来很复杂。但实际上，它也很容易理解。

接下来，让我们快速浏览一下该方法并了解其工作原理。为简单起见，我们针对各种异常情况抛出RuntimeException 。

### 4.2. 了解它是如何工作的

首先，我们调用Class.getResource(className)方法来获取给定类的 URL。

如果该类来自本地文件系统上的 JAR 文件，则 URL 字符串应采用以下格式：

```bash
jar:file:/FULL/PATH/TO/jarName.jar!/PACKAGE/HIERARCHY/TO/CLASS/className.class
```

例如，这是Linux 系统上Guava 的Ascii类的 URL 字符串：

```bash
jar:file:/home/kent/.m2/repository/com/google/guava/guava/31.0.1-jre/guava-31.0.1-jre.jar!/com/google/common/base/Ascii.class
```

如我们所见，JAR 文件的完整路径位于 URL 字符串的中间。

由于不同操作系统上的文件 URL 格式可能不同，我们将提取“ file:…..jar ”部分，将其转换回URL对象，并使用Paths类将路径作为String获取。

我们构建一个正则表达式并使用String的[replaceAll()](https://www.baeldung.com/string/replace-all)方法来提取我们需要的部分：String path = url.replaceAll(“^jar:(file:.[.]jar)!/.”, “$1” );

接下来，类似于byGetProtectionDomain()方法，我们使用Paths类获得最终结果。

现在，让我们创建一个测试来验证我们的方法是否适用于 Guava 的Ascii类：

```java
String jarPath = jarFilePathResolver.byGetResource(Ascii.class);
assertThat(jarPath).endsWith(".jar").contains("guava");
assertThat(new File(jarPath)).exists();

```

如果我们试一试，测试就会通过。

## 5.结合两种方法

到目前为止，我们已经看到了两种解决问题的方法。byGetProtectionDomain方法简单可靠，但由于安全限制，在某些平台上可能会失败。

另一方面，byGetResource方法没有安全问题。但是，我们需要做更多的手动操作，例如处理不同的异常情况以及使用正则表达式提取 JAR 文件的 URL 字符串。

### 5.1. 实现getJarFilePath()方法

我们可以结合这两种方法。首先，让我们尝试使用byGetProtectionDomain()解析 JAR 文件的路径。如果失败，我们调用byGetResource()方法作为回退：

```java
String getJarFilePath(Class clazz) {
    try {
        return byGetProtectionDomain(clazz);
    } catch (Exception e) {
        // cannot get jar file path using byGetProtectionDomain
        // Exception handling omitted
    }
    return byGetResource(clazz);
}

```

### 5.2. 测试getJarFilePath()方法

为了在我们的本地开发环境中模拟byGetProtectionDomain()抛出SecurityException，让我们添加[Mockito](https://www.baeldung.com/mockito-series)依赖项并 使用[@Spy](https://www.baeldung.com/mockito-spy)注解部分模拟JarFilePathResolver：

```java
@ExtendWith(MockitoExtension.class)
class JarFilePathResolverUnitTest {
    @Spy
    JarFilePathResolver jarFilePathResolver;
    ...
}
```

接下来，我们先测试一下getProtectionDomain()方法不抛出SecurityException 的场景：

```java
String jarPath = jarFilePathResolver.getJarFilePath(Ascii.class);
assertThat(jarPath).endsWith(".jar").contains("guava");
assertThat(new File(jarPath)).exists();
verify(jarFilePathResolver, times(1)).byGetProtectionDomain(Ascii.class);
verify(jarFilePathResolver, never()).byGetResource(Ascii.class);

```

如上面的代码所示，除了测试路径是否有效之外，我们还验证了如果我们可以通过 byGetProtectionDomain() 方法获取 JAR 文件的路径，则不应调用byGetResource()方法。

当然，如果byGetProtectionDomain()抛出SecurityException，这两个方法将被调用一次：

```java
when(jarFilePathResolver.byGetProtectionDomain(Ascii.class)).thenThrow(new SecurityException("not allowed"));
String jarPath = jarFilePathResolver.getJarFilePath(Ascii.class);
assertThat(jarPath).endsWith(".jar").contains("guava");
assertThat(new File(jarPath)).exists();
verify(jarFilePathResolver, times(1)).byGetProtectionDomain(Ascii.class);
verify(jarFilePathResolver, times(1)).byGetResource(Ascii.class);

```

如果我们执行测试，两个测试都会通过。

## 六，总结

在本文中，我们学习了如何从给定类中获取 JAR 文件的完整路径。