## 1. 简介

在本教程中，我们考虑了Spring MVC的一个新特性，它允许我们使用常用的Java接口指定Web请求。

## 2. 概述

通常，在Spring MVC中定义控制器时，我们会用各种注解来装饰它的方法，这些注解指定请求：端点的URL、HTTP请求方法、路径变量等。

例如，我们可以 在其他普通方法上使用所述注解来引入/save/{id}端点：

```java
@PostMapping("/save/{id}")
@ResponseBody
public Book save(@RequestBody Book book, @PathVariable int id) {
    // implementation
}
```

当然，当我们只有一个控制器来处理请求时，这根本不是问题。当我们有多个具有相同方法签名的控制器时，情况会有所改变。

例如，由于迁移或类似原因，我们可能有两个不同版本的控制器，它们具有相同的方法签名。在那种情况下，我们将有大量重复的注解伴随着方法定义。显然，这会违反DRY(不要重复自己)原则。

如果这种情况发生在纯Java类中，我们只需定义一个接口并让类实现这个接口。在控制器中，方法的主要负担不是方法签名，而是方法注解。

不过，Spring 5.1引入了一项[新功能](https://github.com/spring-projects/spring-framework/wiki/What's-New-in-Spring-Framework-5.x#general-web-revision-1)：

>   控制器参数注解也会在接口上被检测到：允许控制器接口中的完整映射契约。

让我们研究如何使用此功能。

## 3. 控制器接口

### 3.1 上下文设置

我们使用一个非常简单的管理书籍的REST应用程序示例来说明新功能，它将只包含一个控制器，其中包含允许我们检索和修改书籍的方法。

在本教程中，我们只关注与该功能相关的问题。该应用程序的所有实施问题都可以在我们的[GitHub仓库](https://github.com/eugenp/tutorials/tree/master/spring-web-modules/spring-5-mvc)中找到。

### 3.2 接口

让我们定义一个普通的Java接口，其中我们不仅定义了方法的签名，还定义了它们应该处理的Web请求的类型：

```java
@RequestMapping("/default")
public interface BookOperations {

    @GetMapping("/")
    List<Book> getAll();

    @GetMapping("/{id}")
    Optional<Book> getById(@PathVariable int id);

    @PostMapping("/save/{id}")
    void save(@RequestBody Book book, @PathVariable int id);
}
```

请注意，我们可能有类级别的注解以及方法级别的注解。现在，我们可以创建一个实现此接口的控制器：

```java
@RestController
@RequestMapping("/book")
public class BookController implements BookOperations {

    @Override
    public List<Book> getAll() {...}

    @Override
    public Optional<Book> getById(int id) {...}

    @Override
    public void save(Book book, int id) {...}
}
```

我们仍然应该将类级注解@RestController或@Controller添加到我们的控制器。以这种方式定义，控制器继承所有与映射Web请求相关的注解。

为了检查控制器现在是否按预期工作，让我们运行应用程序并通过发出相应的请求来调用getAll()方法：

```bash
curl http://localhost:8081/book/
```

即使控制器实现了接口，我们也可以通过添加Web请求注解来进一步微调它。我们可以采用与接口相同的方式来做到这一点：在类级别或方法级别。事实上，我们在定义控制器时已经使用了这种可能性：

```java
@RequestMapping("/book")
public class BookController implements BookOperations {...}
```

如果我们向控制器添加Web请求注解，它们将优先于接口的注解。换句话说，Spring以类似于Java处理继承的方式解释控制器接口。

我们在界面中定义了所有常见的Web请求属性，但在控制器中，我们可能总是对它们进行微调。

### 3.3 注意事项

当我们有一个接口和实现它的各种控制器时，我们可能会遇到这样一种情况：一个Web请求可能由多个方法处理。自然地，Spring会抛出一个异常：

```bash
Caused by: java.lang.IllegalStateException: Ambiguous mapping.
```

如果我们用@RequestMapping装饰控制器，我们可以降低模糊映射的风险。

## 4. 总结

在本教程中，我们考虑了Spring 5.1中引入的一项新功能。现在，当Spring MVC控制器实现一个接口时，它们不仅以标准的Java方式实现，而且还继承了接口中定义的所有与Web请求相关的功能。