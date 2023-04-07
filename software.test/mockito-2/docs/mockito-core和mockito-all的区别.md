## 一、概述

Mockito 是一个流行的 Java 模拟框架。但是，在我们开始之前，我们有一些不同的工件可供选择。

*在本快速教程中，我们将探讨mockito-core*和*mockito-all*之间的区别。之后，我们将能够选择合适的。

## 2.*模拟核心*

**[mockito \*-core\*神器](https://search.maven.org/artifact/org.mockito/mockito-core)是 Mockito 的主要神器。**具体来说，它包含 API 和库的实现。

*我们可以通过将依赖项添加到我们的pom.xml*来获得工件：

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>3.3.3</version>
</dependency>复制
```

此时，我们已经可以开始使用[Mockito](https://www.baeldung.com/mockito-series)了。

## 3.*模拟所有*

当然， *mockito-core*有一些依赖项，例如 Maven 单独下载的*hamcrest* 和 *objenesis ，但是**mockito-all*是**一个过时的依赖项，它捆绑****了 Mockito 及其所需的依赖项**。

为了验证这一点，让我们查看*mockito-all.jar*内部以查看它包含的包：

```plaintext
mockito-all.jar
|-- org
|   |-- hamcrest
|   |-- mockito
|   |-- objenesis复制
```

[*mockito-all*](https://search.maven.org/artifact/org.mockito/mockito-all)的最新 GA 版本是 2014 年发布的 1.x 版本。**较新版本的 Mockito 不再发布\*mockito-\* all**。

维护者发布了这个依赖作为简化。如果开发人员没有带依赖管理的构建工具，他们应该使用它。

## 4。结论

正如我们上面探讨的那样，*mockito-core*是 Mockito 的主要工件。较新的版本不再发布*mockito-all*。**从今以后，我们应该只使用\*mockito-core\*。**