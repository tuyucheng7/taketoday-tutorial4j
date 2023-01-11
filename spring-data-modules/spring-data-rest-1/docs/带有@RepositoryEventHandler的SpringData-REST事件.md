## 1. 简介

在处理实体时，REST导出器处理创建、保存和删除事件的操作。**我们可以使用ApplicationListener来监听这些事件并在执行特定操作时执行函数**。或者，我们可以使用带注解的处理程序，它根据域类型过滤事件。

## 2. 编写带注解的处理程序

ApplicationListener不区分实体类型；**但是使用带注解的处理程序**，我们可以根据域类型过滤事件。

我们可以通过在POJO上添加@RepositoryEventHandler注解来声明基于注解的事件处理程序。因此，这会通知BeanPostProcessor需要检查POJO的处理程序方法。

在下面的示例中，我们使用对应于Author实体的RepositoryEventHandler注解类，并在AuthorEventHandler类中声明与Author实体对应的不同前后事件的方法：

```java
@RepositoryEventHandler(Author.class) 
public class AuthorEventHandler {
    Logger logger = Logger.getLogger("Class AuthorEventHandler");
    
    @HandleBeforeCreate
    public void handleAuthorBeforeCreate(Author author){
        logger.info("Inside Author Before Create....");
        String name = author.getName();
    }

    @HandleAfterCreate
    public void handleAuthorAfterCreate(Author author){
        logger.info("Inside Author After Create ....");
        String name = author.getName();
    }

    @HandleBeforeDelete
    public void handleAuthorBeforeDelete(Author author){
        logger.info("Inside Author Before Delete ....");
        String name = author.getName();
    }

    @HandleAfterDelete
    public void handleAuthorAfterDelete(Author author){
        logger.info("Inside Author After Delete ....");
        String name = author.getName();
    }
}
```

在这里，将根据对Author实体执行的操作调用AuthorEventHandler类的不同方法。在找到带有@RepositoryEventHandler注解的类时，Spring遍历类中的方法以查找与下面提到的Before和After事件对应的注解：

**Before事件注解**：与事件调用前调用的before注解相关联。

-   BeforeCreateEvent
-   BeforeDeleteEvent
-   BeforeSaveEvent
-   BeforeLinkSaveEvent

**After事件注解**：与事件调用后调用的after注解相关联。

-   AfterLinkSaveEvent
-   AfterSaveEvent
-   AfterCreateEvent
-   AfterDeleteEvent

我们也可以在一个类中声明同一个事件类型对应的不同实体类型的方法：

```java
@RepositoryEventHandler
public class BookEventHandler {

    @HandleBeforeCreate
    public void handleBookBeforeCreate(Book book){
        // code for before create book event
    }

    @HandleBeforeCreate
    public void handleAuthorBeforeCreate(Author author){
        // code for before create author event
    }
}
```

在这里，BookEventHandler类处理多个实体。在找到带有@RepositoryEventHandler注解的类时，它会遍历方法并在相应的创建事件之前调用相应的实体。

此外，我们需要在@Configuration类中声明事件处理程序，该类将检查处理程序的bean并将它们与正确的事件匹配：

```java
@Configuration
public class RepositoryConfiguration{
    
    public RepositoryConfiguration(){
        super();
    }

    @Bean
    AuthorEventHandler authorEventHandler() {
        return new AuthorEventHandler();
    }

    @Bean
    BookEventHandler bookEventHandler(){
        return new BookEventHandler();
    }
}
```

## 3. 总结

本文是对@RepositoryEventHandler注解的快速介绍，我们学习了如何实现@RepositoryEventHandler注解来处理实体类型对应的各种事件。