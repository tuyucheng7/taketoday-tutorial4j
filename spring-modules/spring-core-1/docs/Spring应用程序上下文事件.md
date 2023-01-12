## 1. 概述

在本教程中，我们将介绍Spring框架提供的事件机制。首先介绍框架提供的各种内置事件，然后了解如何使用事件。

**Spring有一个围绕ApplicationContext构建的事件机制，它可以用来在不同的bean之间交换信息**。
我们可以通过监听事件和执行自定义代码来消费应用程序事件。

例如，一个场景是在ApplicationContext完全启动时执行自定义逻辑。

## 2. 标准上下文事件

事实上，Spring中有各种内置事件，**允许开发人员连接到应用程序的生命周期和上下文**，并执行一些自定义操作。

尽管我们很少在应用程序中手动使用这些事件，但框架自身在内部大量使用这些事件。

### 2.1 ContextRefreshedEvent

在**初始化或刷新ApplicationContext时**，Spring会引发ContextRefreshedEvent。通常，只要上下文尚未关闭，刷新就会被触发多次。

请注意，我们还可以通过调用ConfigurableApplicationContext接口中的refresh()方法手动触发该事件。

### 2.2 ContextStartedEvent

**通过调用ConfigurableApplicationContext的start()方法**，我们触发此事件并启动ApplicationContext。
事实上，该方法通常用于在显式停止ApplicationContext后重新启动bean。我们也可以使用该方法来处理没有配置自动启动的组件。

**这里需要注意的是，对start()的调用总是显式的**。

### 2.3 ContextStoppedEvent

通过调用ConfigurableApplicationContext上的stop()方法，ApplicationContext停止，并发布ContextStoppedEvent事件。
如前所述，我们可以使用start()方法重新启动已停止的事件。

### 2.4 ContextClosedEvent

使用ConfigurableApplicationContext中的close()方法，当ApplicationContext关闭时，发布此事件。

实际上，在关闭上下文后，我们无法重新启动它。

一个上下文在关闭时就到达了它的生命周期终点，因此我们不能像在ContextStoppedEvent中那样重新启动它。

## 3. @EventListener

接下来，让我们介绍如何消费已发布的事件。从Spring 4.2版本开始，Spring支持注解驱动的事件监听器 - @EventListener。

**特别是，我们可以利用这个注解根据方法的签名自动注册一个ApplicationListener**：

```java

@Component
public class ContextEventListener {

    @EventListener
    public void handleContextRefreshEvent(ContextStartedEvent ctxStartEvt) {
        System.out.println("Context Start Event received.");
    }
}
```

值得注意的是，**@EventListener是Spring中的一个核心注解，因此不需要任何额外的配置**。

用@EventListener标注的方法可以返回非void类型。如果返回的值不为null，事件机制将为其发布新事件。

### 3.1 监听多个事件

有时可能会出现需要监听多个事件的情况。此时，我们可以使用classes属性，它是一个数组类型：

```java

@Component
public class ContextEventListener {

    @Order(1)
    @EventListener(classes = {ContextStartedEvent.class, ContextStoppedEvent.class})
    public void handleMultipleEvents() {
        System.out.println("Multi-event listener invoked");
    }
}
```

## 4. Application Event Listener

如果我们使用的是Spring早期版本(<4.2)，那么我们必须引入一个自定义的ApplicationEventListener，
并重写onApplicationEvent()方法来监听事件。

## 5. 总结

在本文中，我们介绍了Spring中的各种内置事件。此外，我们还演示了各种监听已发布事件的方法。