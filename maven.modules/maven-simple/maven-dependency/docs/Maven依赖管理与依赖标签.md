## 1. 概述

在本教程中，我们将回顾两个重要的[Maven](https://www.baeldung.com/maven-guide)标签：dependencyManagement和dependencies。

**这些功能对于多模块项目特别有用**。

我们将回顾这两个标签的相同点和不同点，我们还将研究开发人员在使用它们时常犯的一些可能导致混淆的错误。

## 2. 用法

通常，当我们在dependencies标签中定义我们的依赖时，我们使用dependencyManagement标签来避免重复版本和scope标签，通过这种方式，所需的依赖项在中央POM文件中声明。

### 2.1 dependencyManagement

这个标签由一个dependencies标签组成，它本身可能包含多个dependency标签。每个dependency应该至少有三个主要标签：groupId、artifactId和version，让我们看一个例子：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.12.0</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

上面的代码只是声明了新的工件commons-lang3，但它并没有真正将其添加到项目依赖资源列表中。

### 2.2 dependencies

此标签包含dependency标签列表，每个dependency应该至少有两个主要标签，即groupId和artifactId。

让我们看一个简单的例子：

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.12.0</version>
    </dependency>
</dependencies>
```

**如果我们之前在POM文件中使用过dependencyManagement标签，则可以隐式继承version和scope标签**：

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
    </dependency>
</dependencies>
```

## 3. 相似之处

这两个标签都旨在声明一些第三方或子模块依赖性，它们相辅相成。

事实上，我们通常在dependencies标签之前定义一次dependencyManagement标签，**这用于在POM文件中声明依赖关系。这个声明只是一个公告，它并没有真正向项目添加依赖项**。

让我们看一个添加JUnit库依赖项的示例：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

正如我们在上面的代码中看到的，有一个dependencyManagement标签本身包含另一个dependencies标签。

现在，让我们看看代码的另一面，它将实际的依赖项添加到项目中：

```xml
<dependencies>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
    </dependency>
</dependencies>
```

因此，当前标签与前一个标签非常相似，它们都将定义一个依赖项列表。当然，我们很快就会介绍一些小的差异。

两个代码片段中重复了相同的groupId和artifactId标签，并且它们之间存在有意义的相关性：它们都指向同一个工件。

正如我们所看到的，我们后面的dependency标签中没有任何version标签，令人惊讶的是，它是有效的语法，并且可以毫无问题地进行解析和编译。原因很容易猜到：它会使用dependencyManagement声明的版本。

## 4. 差异

### 4.1 结构差异

**正如我们之前介绍的，这两个标签之间的主要结构差异是继承逻辑**，我们在dependencyManagement标签中定义版本，然后我们可以使用提到的版本而无需在下一个dependencies标签中指定它。

### 4.2 行为差异

**dependencyManagement只是一个声明，它并没有真正添加依赖关系**，此部分中声明的依赖项必须稍后由dependencies标签使用。导致真正依赖发生的只是dependencies标签，在上面的示例中，dependencyManagement标记不会将junit库添加到任何范围，它只是未来dependencies标签的声明。

## 5. 真实世界的例子

几乎所有基于Maven的开源项目都使用这种机制。

让我们看一个[Maven项目](https://github.com/apache/maven/blob/master/pom.xml)本身的例子。我们看到了hamcrest-core依赖，它存在于Maven项目中，它首先在dependencyManagement标签中声明，然后由主dependencies标签导入：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.hamcrest</groupId>
            <artifactId>hamcrest-core</artifactId>
            <version>2.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>org.hamcrest</groupId>
        <artifactId>hamcrest-core</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 6. 常见用例

此功能的一个非常常见的用例是多模块项目。

想象一下，我们有一个由不同模块组成的大项目，每个模块都有自己的依赖项，每个开发人员可能会为使用的依赖项使用不同的版本，然后它可能导致不同工件版本的网格，这也可能导致难以解决的冲突。

**这个问题的简单解决方案肯定是在根POM文件(通常称为父模块)中使用dependencyManagement标签，然后在子POM文件(子模块)甚至父模块本身(如果适用)中使用dependencies标签**。

如果我们只有一个模块，使用这个功能是否有意义？尽管这在多模块环境中非常有用，但即使在单模块项目中，也可以将其作为最佳实践遵守。这有助于提高项目的可读性，并使其准备好扩展到多模块项目。

## 7. 常见错误

一个常见的错误是仅在dependencyManagement部分中定义依赖项，而不将其包含在dependencies标签中。在这种情况下，我们会遇到编译或运行时错误，具体取决于提到的范围。

让我们看一个例子：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.12.0</version>
        </dependency>
        ...
    </dependencies>
</dependencyManagement>
```

想象一下上面的POM代码片段，然后假设我们要在子模块源文件中使用这个库：

```java
import org.apache.commons.lang3.StringUtils;

public class Main {

    public static void main(String[] args) {
        StringUtils.isBlank(" ");
    }
}
```

由于缺少库，此代码将无法编译，编译器报错：

```bash
[ERROR] Failed to execute goal compile (default-compile) on project sample-module: Compilation failure
[ERROR] ~/sample-module/src/main/java/cn/tuyucheng/taketoday/Main.java:[3,32] package org.apache.commons.lang3 does not exist
```

若要避免此错误，将以下dependencies标签添加到子模块POM文件中就足够了：

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
    </dependency>
</dependencies>

```

## 8. 总结

在本教程中，我们比较了Maven的dependencyManagement和dependencies标签，然后，我们回顾了它们的异同，并了解了它们如何协同工作。