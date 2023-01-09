## 1. 概述

[CDI](https://www.baeldung.com/java-ee-cdi)(Contexts and Dependency Injection)是Jakarta EE平台的标准依赖注入框架。

在本教程中，我们将了解 [CDI 2.0](http://www.cdi-spec.org/news/2017/05/15/CDI_2_is_released/) 以及它如何通过添加改进的全功能事件通知模型来构建 CDI 1.x 强大的类型安全注入机制。

## 2. Maven 依赖

首先，我们将构建一个简单的 Maven 项目。

我们需要一个符合 CDI 2.0 的容器，而 CDI 的参考实现[Weld](http://weld.cdi-spec.org/)非常适合：

```xml
<dependencies>
    <dependency>
        <groupId>javax.enterprise</groupId>
        <artifactId>cdi-api</artifactId>
        <version>2.0.SP1</version>
    </dependency>
    <dependency>
        <groupId>org.jboss.weld.se</groupId>
        <artifactId>weld-se-core</artifactId>
        <version>3.0.5.Final</version>
    </dependency>
</dependencies>

```

像往常一样，我们可以从 Maven Central拉取最新版本的[cdi-api](https://search.maven.org/search?q=g:javax.enterprise AND a:cdi-api&core=gav)和[weld-se-core 。](https://search.maven.org/search?q=g:org.jboss.weld.se AND a:weld-se-core&core=gav)

## 3. 观察和处理自定义事件

简单地说，CDI 2.0 事件通知模型是[观察者模式](https://www.baeldung.com/java-observer-pattern)的经典实现，基于[@Observes](https://docs.jboss.org/cdi/api/1.0/javax/enterprise/event/Observes.html) 方法-参数注解。因此，它允许我们轻松定义观察者方法，这些方法可以自动调用以响应一个或多个事件。

例如，我们可以定义一个或多个 bean，它们将触发一个或多个特定事件，而其他 bean 将收到有关事件的通知并做出相应的反应。

为了更清楚地演示这是如何工作的，我们将构建一个简单的示例，包括一个基本服务类、一个自定义事件类和一个对自定义事件作出反应的观察者方法。

### 3.1. 基本服务类

让我们从创建一个简单的TextService类开始：

```java
public class TextService {

    public String parseText(String text) {
        return text.toUpperCase();
    }
}

```

### 3.2. 自定义事件类

接下来，让我们定义一个示例事件类，它在其构造函数中采用String参数：

```java
public class ExampleEvent {
    
    private final String eventMessage;

    public ExampleEvent(String eventMessage) {
        this.eventMessage = eventMessage;
    }
    
    // getter
}
```

### 3.3. 使用@Observes注解定义观察者方法

现在我们已经定义了我们的服务和事件类，让我们使用 @Observes注解为我们的ExampleEvent类创建一个观察者方法：

```java
public class ExampleEventObserver {
    
    public String onEvent(@Observes ExampleEvent event, TextService textService) {
        return textService.parseText(event.getEventMessage());
    } 
}
```

虽然乍一看，onEvent()方法的实现看起来很简单，但它实际上通过@Observes注解封装了很多功能。

正如我们所见，onEvent()方法是一个事件处理程序，它将ExampleEvent和TextService对象作为参数。

请记住，在@Observes注解之后指定的所有参数都是标准注入点。因此，CDI 将为我们创建完全初始化的实例并将它们注入到观察者方法中。

### 3.4. 初始化我们的 CDI 2.0 容器

此时，我们已经创建了我们的服务和事件类，并且我们已经定义了一个简单的观察者方法来对我们的事件做出反应。但是我们如何指示 CDI 在运行时注入这些实例呢？

这是事件通知模型充分展示其功能的地方。我们只需初始化新的[SeContainer](https://docs.jboss.org/cdi/api/2.0/javax/enterprise/inject/se/SeContainer.html)实现并通过fireEvent()方法触发一个或多个事件：

```java
SeContainerInitializer containerInitializer = SeContainerInitializer.newInstance(); 
try (SeContainer container = containerInitializer.initialize()) {
    container.getBeanManager().fireEvent(new ExampleEvent("Welcome to Baeldung!")); 
}
```

请注意，我们使用SeContainerInitializer和 SeContainer对象是因为我们在JavaSE 环境中使用 CDI，而不是在 Jakarta EE 中。

当通过传播事件本身触发ExampleEvent时，将通知所有附加的观察者方法。

由于在@Observes注解之后作为参数传递的所有对象都将被完全初始化，因此 CDI 将负责为我们连接整个TextService对象图，然后再将其注入onEvent()方法。

简而言之，我们拥有类型安全的 IoC 容器以及功能丰富的事件通知模型的优势。

## 4. ContainerInitialized事件

在前面的示例中，我们使用自定义事件将事件传递给观察者方法并获得完全初始化的TextService对象。

当然，当我们确实需要跨应用程序的多个点传播一个或多个事件时，这很有用。

有时，我们只需要获得一堆完全初始化的对象，这些对象就可以在我们的应用程序类中使用，而无需执行其他事件。

为此， CDI 2.0 提供了 [ContainerInitialized](http://javadox.com/org.jboss.weld.se/weld-se-core/2.2.6.Final/org/jboss/weld/environment/se/events/ContainerInitialized.html)事件类，它会在 Weld 容器初始化时自动触发。

让我们看一下如何使用ContainerInitialized事件将控制转移到ExampleEventObserver类：

```java
public class ExampleEventObserver {
    public String onEvent(@Observes ContainerInitialized event, TextService textService) {
        return textService.parseText(event.getEventMessage());
    }    
}

```

请记住，ContainerInitialized事件类是特定于焊接的。因此，如果我们使用不同的 CDI 实现，我们将需要重构我们的观察者方法。

## 5.条件观察者方法

在当前的实现中，我们的ExampleEventObserver类默认定义了一个无条件的观察者方法。这意味着无论当前上下文中是否存在该类的实例，观察者方法都将始终收到提供的事件的通知。

同样，我们可以通过将notifyObserver=IF_EXISTS指定为@Observes注解的参数来定义条件观察器方法：

```java
public String onEvent(@Observes(notifyObserver=IF_EXISTS) ExampleEvent event, TextService textService) { 
    return textService.parseText(event.getEventMessage());
}

```

当我们使用条件观察者方法时，只有在当前上下文中存在定义观察者方法的类的实例时，该方法才会收到匹配事件的通知。

## 6.事务观察者方法

我们还可以在事务中触发事件，例如数据库更新或删除操作。为此，我们可以通过将during 参数添加到@Observes注解来定义事务性观察者方法。

during参数的每个可能值对应于事务的特定阶段：

-   BEFORE_COMPLETION 完成前
-   完成后
-   成功后
-   失败后

如果我们在事务中触发ExampleEvent事件，我们需要相应地重构onEvent()方法以在所需阶段处理事件：

```java
public String onEvent(@Observes(during=AFTER_COMPLETION) ExampleEvent event, TextService textService) { 
    return textService.parseText(event.getEventMessage());
}
```

事务观察器方法将仅在给定事务的匹配阶段收到提供事件的通知。

## 7.观察者方法排序

CDI 2.0 的事件通知模型中包含的另一个不错的改进是能够为调用给定事件的观察者设置顺序或优先级。

[通过在@Observes 之后指定@Priority](https://docs.oracle.com/javaee/7/api/javax/annotation/Priority.html)注解，我们可以很容易地定义调用观察者方法的顺序。

为了理解这个特性是如何工作的，让我们定义另一个观察者方法，除了 ExampleEventObserver实现的方法：

```java
public class AnotherExampleEventObserver {
    
    public String onEvent(@Observes ExampleEvent event) {
        return event.getEventMessage();
    }   
}
```

在这种情况下，默认情况下两个观察者方法将具有相同的优先级。因此，CDI 调用它们的顺序是不可预测的。

我们可以通过@Priority注解为每个方法分配一个调用优先级来轻松解决这个问题：

```java
public String onEvent(@Observes @Priority(1) ExampleEvent event, TextService textService) {
    // ... implementation
}

public String onEvent(@Observes @Priority(2) ExampleEvent event) {
    // ... implementation
}
```

优先级遵循自然顺序。因此，CDI 将首先调用优先级为1的观察者方法 ，然后调用优先级为2的方法。

同样，如果我们在两个或多个方法中使用相同的优先级，顺序也是未定义的。

## 8.异步事件

到目前为止，在我们学习的所有示例中，我们都是同步触发事件的。但是，CDI 2.0 也允许我们轻松触发异步事件。然后异步观察者方法可以在不同的线程中处理这些异步事件。

我们可以使用fireAsync()方法异步触发事件：

```java
public class ExampleEventSource {
    
    @Inject
    Event<ExampleEvent> exampleEvent;
    
    public void fireEvent() {
        exampleEvent.fireAsync(new ExampleEvent("Welcome to Baeldung!"));
    }   
}
```

Beans 触发事件，这些事件是[Event](https://docs.jboss.org/cdi/api/2.0/javax/enterprise/event/Event.html)接口的实现。因此，我们可以像注入任何其他常规 bean 一样注入它们。

为了处理我们的异步事件，我们需要使用[@ObservesAsync](https://docs.jboss.org/cdi/api/2.0.EDR2/javax/enterprise/event/ObservesAsync.html)注解定义一个或多个异步观察者方法：

```java
public class AsynchronousExampleEventObserver {

    public void onEvent(@ObservesAsync ExampleEvent event) {
        // ... implementation
    }
}
```

## 9.总结

在本文中，我们了解了如何开始使用与 CDI 2.0 捆绑在一起的改进的事件通知模型。