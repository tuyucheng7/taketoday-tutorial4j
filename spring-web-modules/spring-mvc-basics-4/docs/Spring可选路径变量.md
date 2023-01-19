## 1. 概述

在本教程中，我们将学习如何在 Spring 中使路径变量成为可选的。首先，我们将描述[Spring 如何在处理程序方法中绑定@PathVariable](https://www.baeldung.com/spring-requestmapping)参数。然后，我们将展示在不同的 Spring 版本中使路径变量可选的不同方法。

有关路径变量的快速概述，请阅读[我们的Spring MVC文章](https://www.baeldung.com/spring-mvc-interview-questions#q5describe-apathvariable)。

## 二、Spring如何绑定@PathVariable参数

默认情况下，Spring 将尝试将处理程序方法中所有用@PathVariable注解的参数与URI 模板中的相应变量绑定。如果 Spring 失败，它不会将我们的请求传递给该处理程序方法。

例如，考虑以下尝试(未成功)使id路径变量可选的getArticle方法：

```java
@RequestMapping(value = {"/article", "/article/{id}"})
public Article getArticle(@PathVariable(name = "id") Integer articleId) {
    if (articleId != null) {
        //...
    } else {
        //...
    }
}
```

在这里，getArticle方法应该为对/article和/article/{id}的请求提供服务。Spring 将尝试将articleId参数绑定到id路径变量(如果存在)。

例如，向/article/123发送请求会将articleId 的值设置为 123。

另一方面，如果我们向/article发送请求，由于以下异常，Spring 返回状态码 500：

```java
org.springframework.web.bind.MissingPathVariableException:
  Missing URI template variable 'id' for method parameter of type Integer
```

这是因为缺少id，Spring 无法为articleId参数设置值。

因此，我们需要一些方法来告诉 Spring 在没有相应路径变量的情况下忽略绑定特定的@PathVariable参数，正如我们将在以下部分中看到的那样。

## 3. 使路径变量可选

### 3.1. 使用@PathVariable的必需属性

从 Spring 4.3.3 开始，@PathVariable注解定义了我们需要的布尔属性，以指示路径变量是否对处理程序方法是必需的。

例如，以下版本的getArticle使用required属性：

```java
@RequestMapping(value = {"/article", "/article/{id}"})
public Article getArticle(@PathVariable(required = false) Integer articleId) {
   if (articleId != null) {
       //...
   } else {
       //...
   }
}
```

由于required属性是false ，如果请求中没有发送id路径变量，Spring 不会抱怨。也就是说，如果发送了，Spring 会将articleId设置为id ，否则设置为null。

另一方面，如果required为true ，Spring 会在id丢失的情况下抛出异常。

### 3.2. 使用可选参数类型

以下实现展示了 Spring 4.1 以及[JDK 8 的Optional类](https://www.baeldung.com/java-optional)如何提供另一种方式使articleId成为可选的：

```java
@RequestMapping(value = {"/article", "/article/{id}"}")
public Article getArticle(@PathVariable Optional<Integer> optionalArticleId) {
    if (optionalArticleId.isPresent()) {
        Integer articleId = optionalArticleId.get();
        //...
    } else {
        //...
    }
}
```

在这里，Spring 创建了Optional<Integer>实例optionalArticleId来保存id的值。如果id存在，optionalArticleId将包装它的值，否则，optionalArticleId将包装一个空值。然后，我们可以使用Optional的isPresent()、 get() 或 orElse()方法来处理该值。

### 3.3. 使用地图参数类型

另一种定义自 Spring 3.2 以来可用的可选路径变量的方法是使用M ap用于@PathVariable参数：

```java
@RequestMapping(value = {"/article", "/article/{id}"})
public Article getArticle(@PathVariable Map<String, String> pathVarsMap) {
    String articleId = pathVarsMap.get("id");
    if (articleId != null) {
        Integer articleIdAsInt = Integer.valueOf(articleId);
        //...
    } else {
        //...
    }
}
```

在此示例中，Map<String, String> pathVarsMap参数将 URI 中的所有路径变量收集为键/值对。然后，我们可以使用get()方法获取特定路径变量。

请注意，因为 Spring 将路径变量的值提取为String，所以我们使用Integer.valueOf()方法将其转换为Integer。

### 3.4. 使用两种处理程序方法

如果我们使用旧的 Spring 版本，我们可以将getArticle处理程序方法拆分为两个方法。

第一个方法将处理对/article/{id}的请求：

```java
@RequestMapping(value = "/article/{id}")
public Article getArticle(@PathVariable(name = "id") Integer articleId) {
    //...        
}

```

第二种方法将处理对/article的请求：

```java
@RequestMapping(value = "/article")
public Article getDefaultArticle() {
    //...
}
```

## 4. 总结

总而言之，我们已经讨论了如何在不同的 Spring 版本中使路径变量成为可选的。