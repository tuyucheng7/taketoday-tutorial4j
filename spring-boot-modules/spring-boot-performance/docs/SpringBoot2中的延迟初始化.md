## 1. 概述

在本教程中，我们将了解如何从[Spring Boot 2.2]()开始，在应用程序级别配置延迟初始化。

## 2. 延迟初始化

默认情况下，在Spring中，所有已定义的bean及其依赖项都在创建应用程序上下文时创建的。

**相反，当我们使用惰性初始化配置一个bean时，该bean只会在需要时创建，并注入其依赖项**。

## 3. Maven依赖

为了在我们的应用程序中获得Spring Boot，我们需要将它包含在我们的类路径中。

使用[Maven](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter)，我们只需添加[spring-boot-starter](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter)依赖项：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
        <version>2.4.3</version>
    </dependency>
</dependencies>
```

## 4. 启用延迟初始化

Spring Boot 2引入了spring.main.lazy-initialization属性，使得在整个应用程序中配置惰性初始化变得更加容易。

**将属性值设置为true意味着应用程序中的所有bean都将使用惰性初始化**。

让我们在application.yml配置文件中配置属性：

```yaml
spring:
    main:
        lazy-initialization: true
```

或者，在我们的application.properties文件中：

```properties
spring.main.lazy-initialization=true
```

此配置会影响上下文中的所有bean，因此，如果我们想为特定的bean配置惰性初始化，我们可以通过[@Lazy]()方法来实现。

更重要的是，我们可以将新属性与设置为false的@Lazy注解结合使用。

或者换句话说，**所有定义的bean都将使用惰性初始化，除了那些我们明确配置为@Lazy(false)的**。

### 4.1 使用SpringApplicationBuilder

另一种配置延迟初始化的方法是使用SpringApplicationBuilder方法：

```java
SpringApplicationBuilder(Application.class)
      .lazyInitialization(true)
      .build(args)
      .run();
```

在上面的示例中，我们使用[lazyInitialization](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/builder/SpringApplicationBuilder.html#lazyInitialization-boolean-)方法来控制应用程序是否应该延迟初始化。

### 4.2 使用SpringApplication

或者，我们也可以使用SpringApplication类：

```java
SpringApplication app = new SpringApplication(Application.class);
app.setLazyInitialization(true);
app.run(args);
```

在这里，我们使用[setLazyInitialization](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/SpringApplication.html#setLazyInitialization-boolean-)方法将我们的应用程序配置为延迟初始化。

要记住的一个重要注意事项是，**应用程序属性文件中定义的属性优先于使用SpringApplication或SpringApplicationBuilder设置的标志**。

## 5. 运行

让我们创建一个简单的服务，使我们能够测试我们刚才描述的内容。

通过向构造函数添加一条println语句，我们可以确切地知道bean何时被创建。

```java
public class Writer {

    private final String writerId;

    public Writer(String writerId) {
        this.writerId = writerId;
        System.out.println(writerId + " initialized!!!");
    }

    public void write(String message) {
        System.out.println(writerId + ": " + message);
    }
}
```

此外，让我们创建SpringApplication并注入我们之前创建的服务。

```java
@SpringBootApplication
public class Application {

    @Bean("writer1")
    public Writer getWriter1() {
        return new Writer("Writer 1");
    }

    @Bean("writer2")
    public Writer getWriter2() {
        return new Writer("Writer 2");
    }

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        System.out.println("Application context initialized!!!");

        Writer writer1 = ctx.getBean("writer1", Writer.class);
        writer1.write("First message");

        Writer writer2 = ctx.getBean("writer2", Writer.class);
        writer2.write("Second message");
    }
}
```

让我们将spring.main.lazy-initialization属性值设置为false，然后运行我们的应用程序。

```shell
Writer 1 initialized!!!
Writer 2 initialized!!!
Application context initialized!!!
Writer 1: First message
Writer 2: Second message
```

如我们所见，bean是在应用程序上下文启动时创建的。

现在让我们将spring.main.lazy-initialization的值更改为true，然后再次运行我们的应用程序。

```shell
Application context initialized!!!
Writer 1 initialized!!!
Writer 1: First message
Writer 2 initialized!!!
Writer 2: Second message
```

**因此，应用程序不会在启动时创建bean，而只会在需要时创建它们**。

## 6. 延迟初始化的影响

在整个应用程序中启用延迟初始化可能会产生积极和消极的影响。

让我们来谈谈其中的一些，正如新功能的[官方公告](https://spring.io/blog/2019/03/14/lazy-initialization-in-spring-boot-2-2)中所描述的那样：

1.  延迟初始化可能会减少应用程序启动时创建的beans数量，因此，我们**可以缩短应用程序的启动时间**
2.  由于在需要之前不会创建任何bean，因此我们**可能会掩盖问题，让它们进入运行时，而不是在启动时给出异常**
3.  这些问题可能包括内存不足错误、配置错误或发现类定义的错误
4.  此外，当我们处于web上下文中时，**按需触发bean创建会增加HTTP请求的延迟**-bean创建只会影响第一个请求，但这**可能会对负载平衡和自动扩展产生负面影响**。

## 7. 总结

在本教程中，我们使用Spring Boot 2.2中引入的新属性spring.main.lazy-initialization配置了延迟初始化。