## 1. 概述

Mockito是一个流行的Java mock框架。但是，在我们开始之前，我们有一些不同的工件可供选择。

在这个快速教程中，我们将探讨mockito-core和mockito-all之间的区别。之后，我们将能够选择合适的那个。

## 2. mockito-core

**[mockito-core](https://search.maven.org/artifact/org.mockito/mockito-core)工件是Mockito的主要依赖**。具体来说，它包含API和库的实现。

我们可以通过将依赖项添加到我们的pom.xml来获得工件：

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>3.3.3</version>
</dependency>
```

此时，我们已经可以开始使用[Mockito](https://www.baeldung.com/mockito-series)了。

## 3. mockito-all

当然，mockito-core有一些依赖项，例如Maven单独下载的hamcrest和objenesis，但是**mockito-all是一个过时的依赖项，它捆绑了Mockito及其所需的依赖项**。

为了验证这一点，让我们访问mockito-all.jar内部以查看它包含的包：

```powershell
mockito-all.jar
|-- org
|   |-- hamcrest
|   |-- mockito
|   |-- objenesis
```

[mockito-all](https://search.maven.org/artifact/org.mockito/mockito-all)的最新GA版本是2014年发布的1.x版本。**较新版本的Mockito不再发布mockito-all**。

维护者发布了这个依赖作为简化。如果开发人员没有具有依赖关系管理的构建工具，他们应该使用它。

## 4. 总结

正如我们上面探讨的那样，mockito-core是Mockito的主要工件。较新的版本不再发布mockito-all。**从今以后，我们应该只使用mockito-core**。