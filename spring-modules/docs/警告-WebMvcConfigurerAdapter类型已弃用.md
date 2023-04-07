## 一、简介

在本快速教程中，我们将了解在使用 Spring 5.xx 版本时可能会看到的警告之一，即引用已弃用的*WebMvcConfigurerAdapter*类的警告。

我们将了解为什么会出现此警告以及如何处理它。

## **2. 为什么出现警告**

**如果我们使用的是 Spring 版本 5（或 Spring Boot 2）**，则在升级现有应用程序或使用旧 API 构建新应用程序时会出现此警告。

让我们简单回顾一下它背后的历史。

在 Spring 的早期版本中，直到并包括版本 4，如果我们想要配置 Web 应用程序，我们可以使用 WebMvcConfigurerAdapter*类*：

```java
@Configuration
public WebConfig extends WebMvcConfigurerAdapter {
    
    // ...
}复制
```

这是一个实现*WebMvcConfigurer*接口的抽象类，并包含所有继承方法的空实现。

通过子类化它，我们可以覆盖它的方法，这些方法提供了各种 MVC 配置元素的挂钩，例如视图解析器、拦截器等。

但是，Java 8 在接口中添加了默认方法的概念。自然地，Spring 团队更新了框架以充分利用新的 Java 语言特性。

## 3.解决方案

如前所述，从 Spring 5 开始，*WebMvcConfigurer接口包含其所有方法的默认实现。*结果，抽象适配器类被标记为已弃用。

让我们看看如何**直接开始使用界面并摆脱警告**：

```java
@Configuration
public WebConfig implements WebMvcConfigurer {
    // ...
}复制
```

就这样！更改应该相当容易进行。

如果有任何对重写方法的*super()*调用，我们也应该删除它们。否则，我们可以像往常一样覆盖任何配置回调。

虽然删除警告不是强制性的，但建议这样做，因为新的 API 更方便，并且在未来的版本中可能会删除已弃用的类。

## 4。结论

*在这篇简短的文章中，我们了解了如何修复有关弃用WebMvcConfigurerAdapter*类的警告。