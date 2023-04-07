## **一、简介**

对于任何尝试升级到 Java 9 的人来说，他们在编译以前在早期版本的 Java 中运行的代码时可能遇到过某种*NoClassDefFoundError* 。

在本文中，我们将研究一个常见的缺失类*JAXBException*以及解决它的不同方法。这里提供的解决方案通常适用于升级到 Java 9 时可能缺少的任何类。

## **2. 为什么 Java 9 找不到\*JAXBException\*？**

Java 9 中讨论最多的特性之一是模块系统。**Java 9 模块系统的目标是将核心 JVM 类和相关项目分解为独立的模块**。这有助于我们通过仅包含运行所需的最少类来创建占用空间更小的应用程序。

缺点是默认情况下许多类在类路径上不再可用。在这种情况下，类*JAXBException可以在名为**java.xml.bind*的新 Jakarta EE 模块之一中找到。由于核心 Java 运行时不需要此模块，因此默认情况下它在类路径中不可用。

尝试运行使用*JAXBException*的应用程序将导致：

```java
NoClassDefFoundError: javax/xml/bind/JAXBException复制
```

为了解决这个问题，**我们必须包含\*java.xml.bind\*模块**。正如我们将在下面看到的，有多种方法可以实现这一点。

## **3.短期解决方案**

确保 JAXB API 类对应用程序可用的最快方法是添加**使用\*–add-modules\* 命令行参数**：

```shell
--add-modules java.xml.bind复制
```

但是，由于几个原因，这可能不是一个好的解决方案。

首先，*–add-modules* 参数也是 Java 9 中新增的。对于需要在多个 Java 版本上运行的应用程序，这带来了一些挑战。我们必须维护多组构建文件，一个用于运行应用程序的每个 Java 版本。

为了解决这个问题，我们还可以为旧的 Java 编译器使用*-XX:+IgnoreUnrecognizedVMOptions*命令行参数。

但是，这意味着我们不会注意到任何拼写错误或拼写错误的参数。例如，如果我们尝试设置最小或最大堆大小并输入错误的参数名称，我们将不会收到警告。我们的应用程序仍将启动，但它将以与我们预期不同的配置运行。

其次，*–add-modules* 选项将在未来的 Java 版本中弃用。这意味着在我们升级到新版本的 Java 后的某个时候，我们将面临使用未知命令行参数的相同问题，并且必须再次解决该问题。

## **4. 长期解决方案**

有一种更好的方法可以跨不同版本的 Java 使用，并且不会与未来的版本中断。

解决方案是 **利用依赖管理工具，例如 Maven**。使用这种方法，我们将 JAXB API 库添加为依赖项，就像任何其他库一样：

```xml
<dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.3.0</version>
</dependency>复制
```

上面的库只包含 JAXB API 类，其中包括*JAXBException*。根据应用程序，我们可能需要包含其他模块。

还要记住，**Maven 工件名称可能与 Java 9 模块名称不同**，JAXB API 就是这种情况。[它可以在Maven Central](https://search.maven.org/classic/#artifactdetails|javax.xml.bind|jaxb-api-parent|2.3.0|pom)上找到。

## **5.结论**

Java 9 模块系统提供了许多好处，例如减小应用程序大小和提高性能。

但是，它也引入了一些意想不到的后果。升级到 Java 9 时，了解应用程序真正需要哪些模块并采取措施确保它们在类路径中可用非常重要。