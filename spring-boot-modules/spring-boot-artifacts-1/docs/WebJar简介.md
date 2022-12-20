## 1. 概述

本教程介绍WebJars以及如何在Java应用程序中使用它们。

简单地说，WebJars是打包到JAR存档文件中的客户端依赖项，它们适用于大多数JVM容器和Web框架。

这里有一些流行的WebJars：Twitter Bootstrap、jQuery、Angular JS、Chart.js等；[官方网站](http://www.webjars.org/)上提供了完整列表。

## 2. 为什么要使用WebJar？

这个问题的答案非常简单-因为它很容易。

**手动添加和管理客户端依赖项通常会导致难以维护代码库**。

此外，大多数Java开发人员更喜欢使用Maven和Gradle作为构建和依赖管理工具。

WebJars解决的主要问题是使客户端依赖项在Maven Central上可用，并且可以在任何标准的Maven项目中使用。

以下是WebJars的一些主要优点：

1.  我们可以明确且轻松地管理基于JVM的Web应用程序中的客户端依赖项
2.  我们可以将它们与任何常用的构建工具一起使用，例如：Maven、Gradle等
3.  WebJars的行为与任何其他Maven依赖项一样-这意味着我们也获得了传递依赖项

## 3. Maven依赖

接下来将Twitter Bootstrap和jQuery添加到pom.xml中：


```xml
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>bootstrap</artifactId>
    <version>3.3.7-1</version>
</dependency>
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>jquery</artifactId>
    <version>3.1.1</version>
</dependency>

```

现在Twitter Bootstrap和jQuery在项目类路径中可用；我们可以简单地引用它们并在我们的应用程序中使用它们。

注意：你可以在Maven Central上查看最新版本的[Twitter Bootstrap](https://mvnrepository.com/artifact/org.webjars/bootstrap)和[jQuery](https://mvnrepository.com/artifact/org.webjars/jquery)依赖项。

## 4. 简单的应用程序

定义了这两个WebJar依赖项后，现在让我们设置一个简单的Spring MVC项目，以便能够使用客户端依赖项。

然而，在我们开始之前，重要的是要了解**WebJars与Spring无关**，我们在这里只使用Spring，因为它是设置MVC项目的一种非常快速和简单的方法。

并且，通过简单的项目设置，我们将为新的客户端依赖项定义一些映射：

```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
              .addResourceHandler("/webjars/**")
              .addResourceLocations("/webjars/");
    }
}
```

我们当然也可以通过XML来做到这一点：

```xml
<mvc:resources mapping="/webjars/**" location="/webjars/"/>
```

## 5. 与版本无关的依赖关系

当使用Spring Framework 4.2或更高版本时，它会自动检测类路径上的webjars-locator库，并使用它来自动解析任何WebJars资产的版本。

为了启用此功能，我们将添加webjars-locator库作为应用程序的依赖项：

```html
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>webjars-locator</artifactId>
    <version>0.30</version>
</dependency>
```

在这种情况下，我们可以在不使用版本的情况下引用WebJars资产；有关几个实际示例，请参见下一节。

## 6. 客户端上的WebJars

让我们为我们的应用程序添加一个简单的纯HTML欢迎页面(这是index.html)：

```html
<html>
<head>
    <title>WebJars Demo</title>
</head>
<body>
</body>
</html>
```

现在我们可以在项目中使用Twitter Bootstrap和jQuery-让我们在欢迎页面中同时使用它们，从Bootstrap开始：

```html
<script src="/webjars/bootstrap/3.3.7-1/js/bootstrap.min.js"></script>
```

对于与版本无关的方法：

```html
<script src="/webjars/bootstrap/js/bootstrap.min.js"></script>
```

添加jQuery：

```html
<script src="/webjars/jquery/3.1.1/jquery.min.js"></script>
```

以及与版本无关的方法：

```html
<script src="/webjars/jquery/jquery.min.js"></script>
```

## 7. 测试

现在我们已经在我们的HTML页面中添加了Twitter Bootstrap和jQuery，让我们来测试它们。

我们将在我们的页面中添加引导警报：

```html
<div class="container"><br/>
    <div class="alert alert-success">
        <strong>Success!</strong> It is working as we expected.
    </div>
</div>
```

请注意，这里假设对Twitter Bootstrap有一些基本的了解；这是官方的[入门指南](https://getbootstrap.com/components/)。

这将显示如下所示的警报，这意味着我们已成功将Twitter Bootstrap添加到我们的类路径中。

现在让我们使用jQuery，我们将为此警报添加一个关闭按钮：

```html
<a href="#" class="close" data-dismiss="alert" aria-label="close">×</a>
```

现在我们需要为关闭按钮功能添加jQuery和bootstrap.min.js，所以将它们添加到index.html的body标签中，如下所示：

```html
<script src="/webjars/jquery/3.1.1/jquery.min.js"></script>
<script src="/webjars/bootstrap/3.3.7-1/js/bootstrap.min.js"></script>
```

注意：如果你使用的是与版本无关的方法，请确保仅从路径中删除版本，否则，相对导入可能不起作用：

```html
<script src="/webjars/jquery/jquery.min.js"></script>
<script src="/webjars/bootstrap/js/bootstrap.min.js"></script>
```

这就是我们最终的index页面的样子：

```html
<html>
    <head>
        <script src="/webjars/jquery/3.1.1/jquery.min.js"></script>
        <script src="/webjars/bootstrap/3.3.7-1/js/bootstrap.min.js"></script>
        <title>WebJars Demo</title>
        <link rel="stylesheet" 
          href="/webjars/bootstrap/3.3.7-1/css/bootstrap.min.css" />
    </head>
    <body>
        <div class="container"><br/>
            <div class="alert alert-success">
                <a href="#" class="close" data-dismiss="alert" 
                  aria-label="close">×</a>
                <strong>Success!</strong> It is working as we expected.
            </div>
        </div>
    </body>
</html>
```

这是index页面的外观(点击关闭按钮时警报应该消失)。

<img src="../assets/img.png">

## 8. 总结

在这篇快速文章中，我们重点介绍了在基于JVM的项目中使用WebJars的基础知识，这使得开发和维护变得更加容易。

我们实现了一个Spring Boot支持的项目，并在我们使用WebJars的项目中使用了Twitter Bootstrap和jQuery。