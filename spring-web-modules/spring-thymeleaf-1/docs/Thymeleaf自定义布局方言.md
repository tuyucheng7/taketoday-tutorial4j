## 1. 简介

[Thymeleaf](http://www.thymeleaf.org/)是一个Java模板引擎，用于处理和创建HTML、XML、JavaScript、CSS和纯文本。有关Thymeleaf和Spring的介绍，请查看[这篇](https://www.baeldung.com/thymeleaf-in-spring-mvc)文章。

在这篇文章中，我们将重点关注模板——大多数相当复杂的网站都需要以某种方式完成的事情。简而言之，页面需要共享常见的页面组件，例如页眉、页脚、菜单等等。

Thymeleaf使用布局方言解决了这个问题，你可以使用Thymeleaf标准布局系统或布局方言构建布局，它使用装饰器模式来处理布局文件。

在本文中，我们将讨论Thymeleaf布局方言的一些功能，可以在[此处](https://github.com/ultraq/thymeleaf-layout-dialect)找到。更具体地说，我们将讨论它的功能，如创建布局、自定义标题或头部元素合并。

## 2. Maven依赖

首先，让我们看看将Thymeleaf与Spring集成所需的配置。我们的依赖项中需要thymeleaf-spring库：

```xml
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf</artifactId>
    <version>3.0.11.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf-spring5</artifactId>
    <version>3.0.11.RELEASE</version>
</dependency>
```

请注意，对于Spring 4项目，必须使用thymeleaf-spring4库而不是thymeleaf-spring5。可以在[此处](https://search.maven.org/artifact/org.thymeleaf/thymeleaf-spring5)找到最新版本的依赖项。

我们还需要自定义布局方言的依赖项：

```xml
<dependency>
    <groupId>nz.net.ultraq.thymeleaf</groupId>
    <artifactId>thymeleaf-layout-dialect</artifactId>
    <version>2.4.1</version>
</dependency>
```

最新版本可以在[Maven Central Repository](https://search.maven.org/search?q=a:thymeleaf-layout-dialect)找到。

## 3. Thymeleaf布局方言配置

在本节中，我们将讨论如何配置Thymeleaf以使用布局方言。如果你想退一步看看如何在你的Web应用程序项目中配置Thymeleaf 3.0，请查看[本教程](https://www.baeldung.com/spring-thymeleaf-3)。

一旦我们将Maven依赖项添加为项目的一部分，我们就需要将布局方言添加到我们现有的Thymeleaf模板引擎中。我们可以使用Java配置来做到这一点：

```java
SpringTemplateEngine engine = new SpringTemplateEngine();
engine.addDialect(new LayoutDialect());
```

或者通过使用XML文件配置：

```xml
<bean id="templateEngine" class="org.thymeleaf.spring5.SpringTemplateEngine">
    <property name="additionalDialects">
        <set>
            <bean class="nz.net.ultraq.thymeleaf.LayoutDialect"/>
        </set>
    </property>
</bean>
```

在装饰内容和布局模板的部分时，默认行为是将所有内容元素放在布局元素之后。

有时我们需要更智能地合并元素，允许将相似的元素组合在一起(将js脚本组合在一起，将样式表组合在一起等)。

为此，我们需要将排序策略添加到我们的配置中——在我们的例子中，它将是分组策略：

```java
engine.addDialect(new LayoutDialect(new GroupingStrategy()));
```

或者在XML中：

```xml
<bean class="nz.net.ultraq.thymeleaf.LayoutDialect">
    <constructor-arg ref="groupingStrategy"/>
</bean>
```

默认策略是追加元素。通过上述，我们将对所有内容进行排序和分组。

如果两种策略都不适合我们的需要，我们可以实现自己的SortingStrategy并将其传递给上面的方言。

## 4. 命名空间和属性处理器的特点

一旦配置好，我们就可以开始使用布局命名空间和五个新的属性处理器：decorate、title-pattern、insert、replace和fragment。

为了创建我们要用于HTML文件的布局模板，我们创建了以下文件，名为template.html：

```html
<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
...
</html>
```

如我们所见，我们将命名空间从标准的xmlns:th="http://www.thymeleaf.org"更改为xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout" 。

现在我们可以开始在我们的HTML文件中使用属性处理器了。

### 4.1 layout:fragment

为了在我们的布局中创建自定义部分或可由共享相同名称的部分替换的可重用模板，我们在template.html主体中使用fragment属性：

```html
<body>
    <header>
        <h1>New dialect example</h1>
    </header>
    <section layout:fragment="custom-content">
        <p>Your page content goes here</p>
    </section>
    <footer>
        <p>My custom footer</p>
        <p layout:fragment="custom-footer">Your custom footer here</p>
    </footer>
</body>
```

请注意，有两个部分将被我们的自定义HTML替换，内容和页脚。

如果找不到任何片段，编写将要使用的默认HTML也很重要。

### 4.2 layout:decorate

我们需要做的下一步是创建一个HTML文件，它将被我们的布局装饰：

```html
<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{template.html}">
<head>
    <title>Layout Dialect Example</title>
</head>
<body>
<section layout:fragment="custom-content">
    <p>This is a custom content that you can provide</p>
</section>
<footer>
    <p layout:fragment="custom-footer">This is some footer content
        that you can change</p>
</footer>
</body>
</html>
```

如第3行所示，我们使用layout:decorate并指定装饰器源。布局文件中与内容文件中的片段匹配的所有片段都将被其自定义实现替换。

### 4.3 layout:title-pattern

鉴于布局方言会自动使用内容模板中的标题覆盖布局的标题，你可以保留布局中的部分标题。

例如，我们可以创建面包屑或在页面标题中保留网站名称。layout:title-pattern处理器在这种情况下会提供帮助。你需要在布局文件中指定的是：

```html
<title layout:title-pattern="$LAYOUT_TITLE - $CONTENT_TITLE">Tuyucheng</title>
```

因此，上一段中呈现的布局和内容文件的最终结果将如下所示：

```html
<title>Tuyucheng - Layout Dialect Example</title>
```

### 4.4 layout:insert/layout:replace

第一个处理器layout:insert类似于Thymeleaf的原始th:insert，但允许将整个HTML片段传递给插入的模板。如果你有一些要重用的HTML，但其内容太复杂而无法单独使用上下文变量来确定或构造，这将非常有用。

第二个layout:replace与第一个类似，但具有th:replace的行为，它实际上会用定义的片段代码替换主机标签。

## 5. 总结

在本文中，我们描述了在Thymeleaf中实现布局的几种方法。

请注意，这些示例使用的是Thymeleaf 3.0版；如果你想了解如何迁移你的项目，请遵循此[过程](https://ultraq.github.io/thymeleaf-layout-dialect/MigrationGuide.html)。

本教程的完整实现可以在[GitHub项目](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/spring-web-modules/spring-thymeleaf)中找到。

如何测试？我们的建议是先使用浏览器，然后再检查现有的JUnit测试。

请记住，你可以使用上述布局方言构建布局，也可以轻松创建自己的解决方案。希望本文能为你提供有关该主题的更多见解，并且你会根据自己的需要找到自己喜欢的方法。