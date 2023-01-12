## 1. 概述

在本文中，我们将详细说明如何**使用Spring中的事件机制**。

事件是框架中被忽视的功能之一，但也是更有用的功能之一。和Spring中的许多其他功能一样，事件发布是ApplicationContext提供的功能之一。

事件的简单使用可以遵循步骤：

+ 如果我们使用Spring 4.2之前的版本，事件类应该继承ApplicationEvent。从4.2 版本开始，事件类不再需要继承ApplicationEvent类。
+ 发布者应注入ApplicationEventPublisher对象。
+ 监听器应该实现ApplicationListener接口。

## 2. 自定义事件

Spring允许我们创建和发布**默认同步**的自定义事件。这有一些优点，例如监听器能够参与发布者的事务上下文。

### 2.1 一个简单的应用程序事件

让我们创建**一个简单的事件类** - 只是一个用于存储事件数据的占位符。

在这种情况下，事件类包含一个message属性：

```java
public class CustomSpringEvent extends ApplicationEvent {

    private final String message;

    public CustomSpringEvent(final Object source, final String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
```

### 2.2 Publisher

现在让我们创建**该事件的发布者**。发布者构造事件对象并将其发布给正在监听的任何人。

要发布事件，发布者可以简单地注入ApplicationEventPublisher并使用publishEvent()API：

```java

@Component
public class CustomSpringEventPublisher {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishCustomEvent(final String message) {
        System.out.println("Publishing custom event. ");
        final CustomSpringEvent customSpringEvent = new CustomSpringEvent(this, message);
        applicationEventPublisher.publishEvent(customSpringEvent);
    }
}
```

或者，发布者类可以实现ApplicationEventPublisherAware接口，这也将在应用程序启动时注入ApplicationEventPublisher。
通常，使用@Autowire注入ApplicationEventPublisher会更简单。

从Spring 4.2开始，ApplicationEventPublisher接口为publishEvent(Object event)方法提供了一个新的重载，
该方法接收任何对象作为事件。**因此，Spring事件不再需要继承ApplicationEvent类**。

### 2.3 Listener

最后，让我们创建监听器。

监听器的唯一要求是成为一个bean并实现ApplicationListener接口：

```java

@Component
public class CustomSpringEventListener implements ApplicationListener<CustomSpringEvent> {

    @Override
    public void onApplicationEvent(final CustomSpringEvent event) {
        System.out.println("Received spring custom event - " + event.getMessage());
    }
}
```

请注意我们的自定义监听器是如何使用CustomSpringEvent作为泛型参数的，这使得onApplicationEvent()方法类型安全。
也避免了必须检查对象是否是特定事件类的实例并强制转换它。

而且，正如前面所说的(默认情况下，**Spring事件是同步的**)，doStuffAndPublishAnEvent()方法会阻塞，直到所有监听器完成对事件的处理。

## 3. 创建异步事件

在某些情况下，同步发布事件并不是我们真正想要的，**我们可能需要对事件进行异步处理**。

我们可以通过创建一个带有executor的ApplicationEventMulticaster bean在配置中启用对异步事件的支持。

出于我们的目的，SimpleAsyncTaskExecutor是个不错的选择：

```java

@Configuration
@ComponentScan("cn.tuyucheng.taketoday.springevents.synchronous")
public class AsynchronousSpringEventsConfig {

    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        final SimpleApplicationEventMulticaster simpleApplicationEventMulticaster = new SimpleApplicationEventMulticaster();
        simpleApplicationEventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return simpleApplicationEventMulticaster;
    }
}
```

事件、发布者和监听器的实现与之前相同，**但现在监听器将在单独的线程中异步处理事件**。

## 4. 框架现有事件

Spring本身发布了各种开箱即用的事件。例如，ApplicationContext将触发各种框架事件：ContextRefreshedEvent、
ContextStartedEvent、RequestHandledEvent等。

这些事件为应用程序开发人员提供了一个选项，可以连接到应用程序和上下文的生命周期中，并在需要时添加自己的自定义逻辑。

下面是一个监听上下文刷新的监听器的快速示例：

```java

@Component
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent cse) {
        System.out.println("Handling context re-fresh event. ");
    }
}
```

要了解有关现有框架事件的更多信息，请阅读[本文](../../spring-modules/spring-core-1/docs/ApplicationContext_Event.md)。

## 5. 注解驱动的事件监听器

从Spring 4.2开始，事件监听器不需要是实现ApplicationListener接口的bean，它可以通过@EventListener注解注册到托管bean的任何公共方法上：

```java

@Component
public class AnnotationDrivenEventListener {

    @EventListener
    public void handleContextStart(final ContextStartedEvent cse) {
        System.out.println("Handling context started event.");
        hitContextStartedHandler = true;
    }
}
```

与前面一样，方法签名声明它消费的事件类型。

默认情况下，监听器是同步调用的。但是，我们可以通过添加@Async注解轻松使其异步化。我们只需要记住在应用程序中使用@EnableAsync启用异步支持。

## 6. 泛型支持

还可以使用事件类型中的泛型信息来调度事件。

### 6.1 泛型事件

**让我们创建一个泛型类型事件**。

在我们的示例中，事件类包含一个T类型和boolean类型的变量：

```java
public class GenericSpringEvent<T> {
    protected boolean success;
    private T what;

    public GenericSpringEvent(T what, boolean success) {
        this.what = what;
        this.success = success;
    }
    // standard getters
}
```

注意GenericSpringEvent和CustomSpringEvent之间的区别。我们现在可以灵活地发布任意事件，并且不再需要从ApplicationEvent继承。

### 6.2 监听器

现在，让我们创建**该事件的监听器**。

我们可以像以前一样通过实现ApplicationListener接口来定义监听器：

```java

@Component
public class GenericSpringEventListener implements ApplicationListener<GenericSpringEvent<String>> {

    @Override
    public void onApplicationEvent(@NonNull final GenericSpringEvent<String> event) {
        System.out.println("Received spring generic event - " + event.getWhat());
    }
}
```

但不幸的是，这个定义要求我们的GenericSpringEvent继承ApplicationEvent。因此，对于本教程，让我们使用前面讨论的注解驱动的事件监听器。

也可以通过在@EventListener注解上定义布尔SpEL表达式来使事件监听器有条件的运行。

在这种情况下，事件处理程序只会在GenericSpringEvent的success属性为true时被调用：

```java

@Component
public class AnnotationDrivenEventListener {

    @EventListener(condition = "#event.success")
    public void handleSuccessful(final GenericSpringEvent<String> event) {
        System.out.println("Handling generic event (conditional): " + event.getWhat());
    }
}
```

### 6.3 Publisher

事件发布者与上面描述的类似。但是由于类型擦除，我们需要发布一个事件来解析我们将过滤的泛型参数，
例如，class GenericStringSpringEvent extends GenericSpringEvent<String\>。

此外，还有另一种发布事件的方式。如果我们从带有@EventListener注解的方法返回一个非空值作为结果，
Spring将把该结果作为一个新事件发送给我们。此外，我们可以通过将它们作为事件处理的结果返回到集合中来发布多个新事件。

## 7. 事务绑定事件

本节是关于使用@TransactionalEventListener注解的。

从Spring 4.2开始，框架提供了一个新的@TransactionalEventListener注解，
它是@EventListener的扩展，允许将事件的监听器绑定到事务的以下某个阶段：

+ AFTER_COMMIT(default)用于在事务**成功完成**时触发事件。
+ AFTER_ROLLBACK - 如果事务已**回滚**。
+ AFTER_COMPLETION - 如果事务已完成(AFTER_COMMIT和AFTER_ROLLBACK的别名)。
+ BEFORE_COMMIT用于在事务**提交之前**触发事件。

下面是一个事务事件监听器的快速示例：

```java

@Component
public class AnnotationDrivenEventListener {

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleCustom(final CustomSpringEvent event) {
        System.out.println("Handling event inside a transaction BEFORE COMMIT.");
    }
}
```

只有在事件生产者正在运行的事务即将提交时，才会调用此监听器。

如果没有事务正在运行，则根本不会发送事件，除非我们通过将fallbackExecution属性设置为true来覆盖它。

## 8. 总结

在本文中，我们介绍了在Spring中处理事件的基本知识，包括创建一个简单的自定义事件，发布它，然后在监听器中处理它。

我们还简要介绍了如何在配置中启用事件的异步处理。

然后我们介绍了Spring 4.2中引入的改进，例如注解驱动的监听器、更好的泛型支持以及与事务阶段的事件绑定。