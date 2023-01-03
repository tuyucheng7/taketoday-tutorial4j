## 1. 概述

这个简短的教程将描述Maven的<optional\>标签，以及我们如何使用它来减小Maven项目工件(例如WAR、EAR或JAR)的大小和范围。

有关Maven的复习，请查看我们的[综合指南](https://www.baeldung.com/maven)。

## 2. 什么是<optional\>

有时我们会创建一个Maven项目作为其他Maven项目的依赖，在处理此类项目时，可能需要包含一个或多个仅对该项目的一部分功能有用的依赖项。

如果最终用户不使用该功能子集，项目仍会传递拉入这些依赖项，**这会不必要地增加用户的项目大小，甚至可能会引入与其他项目依赖项冲突的依赖项版本**。

理想情况下，我们应该将项目的功能子集拆分到它自己的模块中，因此不会污染项目的其余部分。然而，这并不总是实用的。

为了从主项目中排除这些特殊的依赖，我们可以给它们应用Maven的<optional\>标签，**这会强制任何想要使用这些依赖项的用户显式声明它们**。但是，它不会将这些依赖项强制到不需要它们的项目中。

## 3. 如何使用<optional\>

正如我们将要看到的，我们可以包含值为true的<optional\>元素，以使任何Maven依赖项成为可选的。

假设我们有以下项目pom：

```xml
<project>
    ...
    <artifactId>project-with-optionals</artifactId>
    ...
    <dependencies>
        <dependency>
            <groupId>cn.tuyucheng.taketoday</groupId>
            <artifactId>optional-project</artifactId>
            <version>1.0.0</version>
            <optional>true</optional>
        </dependency>
    </dependencies>
</project>
```

在此示例中，尽管optional-project被标记为可选，但它仍然作为project-with-optionals的可用依赖项，就好像<optional\>标签从未存在过一样。

为了查看<optional\>标签的效果，我们需要创建一个依赖于project-with-optionals的新项目：

```xml
<project>
    ...
    <artifactId>main-project</artifactId>
    ...
    <dependencies>
        <dependency>
            <groupId>cn.tuyucheng.taketoday</groupId>
            <artifactId>project-with-optionals</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
</project>
```

现在，如果我们尝试从main-project中引用optional-project，我们会看到optional-project不存在，这是因为<optional\>标签会阻止它被传递包含。

如果我们发现我们的main-project中需要optional-project，我们只需要将其声明为依赖项。

## 4. 总结

在本文中，我们研究了Maven的<optional\>标签，使用该标签的主要好处是它可以减小项目的大小并有助于防止版本冲突，我们还看到该标签不会影响使用它的项目。