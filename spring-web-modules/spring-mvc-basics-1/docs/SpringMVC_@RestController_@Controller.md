## 1. 概述

在这个简短的教程中，我们将讨论Spring MVC中@Controller和@RestController注解之间的区别。

Spring 4.0引入了@RestController注解，以简化Restful Web服务的创建。
这是一个方便的注解，结合了@Controller和@ResponseBody，这样就不需要用@ResponseBody注解来标注控制器类的每个请求处理方法。

## 2. @Controller

我们可以使用@Controller注解来标注简单控制器。这只是@Component注解的一个特化，它允许我们通过类路径扫描自动检测实现类。

我们通常将@Controller与@RequestMapping注解结合使用，用于请求处理方法。

让我们看一个Spring MVC控制器的快速示例：

```java

@Controller
@RequestMapping("books")
public class SimpleBookController {

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody Book getBook(@PathVariable int id) {
        return findBookById(id);
    }

    private Book findBookById(int id) {
        Book book = null;
        if (id == 42) {
            book = new Book();
            book.setId(id);
            book.setAuthor("Douglas Adamas");
            book.setTitle("Hitchhiker's guide to the galaxy");
        }
        return book;
    }
}
```

我们用@ResponseBody标注了请求处理方法。此注解支持将返回对象自动序列化为HttpResponse。

## 3. @RestController

@RestController是控制器的专用版本。它包括@Controller和@ResponseBody注解，因此简化了控制器的实现：

```java

@RestController
@RequestMapping("books-rest")
public class SimpleBookRestController {

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public Book getBook(@PathVariable int id) {
        return findBookById(id);
    }

    private Book findBookById(int id) {
        Book book = null;
        if (id == 42) {
            book = new Book();
            book.setId(id);
            book.setAuthor("Douglas Adamas");
            book.setTitle("Hitchhiker's guide to the galaxy");
        }
        return book;
    }
}
```

**控制器使用@RestController注解进行标注；因此，不需要额外添加@ResponseBody**。

控制器类的每个请求处理方法都会自动将返回对象序列化为HttpResponse。

## 4. 总结

在本文中，我们介绍了Spring中@RestController注解的用法。