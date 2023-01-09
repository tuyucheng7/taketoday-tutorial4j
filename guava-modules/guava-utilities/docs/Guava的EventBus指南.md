## 1. 概述

Guava 库提供了允许组件之间发布-订阅通信的EventBus 。在本教程中，我们将了解如何使用EventBus的一些功能。

## 2.设置

首先，我们在pom.xml 中添加 Google Guava 库依赖项：

```java
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

最新版本可以在[这里](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")找到。

## 3. 使用事件总线

让我们从一个简单的例子开始。

### 3.1. 设置

我们首先查看EventBus对象。它可以注册监听器和发布事件。使用它就像实例化类一样简单：

```java
EventBus eventBus = new EventBus();
```

Guava 库使你能够以最适合你的开发需求的任何方式自由使用EventBus 。

### 3.2. 创建监听器

我们创建一个侦听器类，它具有用于接收特定事件的处理程序方法。我们用@Subscribe注解处理程序方法。该方法接受与正在发布的事件类型相同的对象作为参数：

```java
public class EventListener {

    private static int eventsHandled;

    @Subscribe
    public void stringEvent(String event) {
        eventsHandled++;
    }
}
```

### 3.3. 注册听众

我们可以通过在EventBus上注册我们的EventListener类来订阅事件：

```java
EventListener listener = new EventListener();
eventBus.register(listener);
```

### 3.4. 注销听众

如果出于任何原因我们想从EventBus中注销一个类，也可以轻松完成：

```java
eventBus.unregister(listener);
```

### 3.5. 发布事件

我们也可以使用EventBus发布事件：

```java
@Test
public void givenStringEvent_whenEventHandled_thenSuccess() {
    eventBus.post("String Event");
    assertEquals(1, listener.getEventsHandled());
}
```

### 3.6. 发布自定义事件

我们还可以指定自定义事件类并发布该事件。我们首先创建一个自定义事件：

```java
public class CustomEvent {
    private String action;

    // standard getters/setters and constructors
}
```

在EventListener类中为该事件添加一个处理程序方法：

```java
@Subscribe
public void someCustomEvent(CustomEvent customEvent) {
    eventsHandled++;
}
```

我们现在可以发布我们的自定义事件：

```java
@Test
public void givenCustomEvent_whenEventHandled_thenSuccess() {
    CustomEvent customEvent = new CustomEvent("Custom Event");
    eventBus.post(customEvent);

    assertEquals(1, listener.getEventsHandled());
}
```

### 3.7. 处理取消订阅的事件

我们提供了一个DeadEvent类，它允许我们处理任何没有侦听器的事件。我们可以添加一个方法来处理DeadEvent类：

```java
@Subscribe
public void handleDeadEvent(DeadEvent deadEvent) {
    eventsHandled++;
}
```

## 4. 总结

在本教程中，我们使用一个简单的示例来指导如何使用 Guava EventBus。