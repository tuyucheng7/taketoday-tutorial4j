## 1. 概述

在本教程中，我们介绍如何在Java中使用泛型实现工厂模式。

## 2. 什么是工厂模式？

在面向对象编程中，工厂模式是一种[创建型设计模式]()，它负责在调用时创建对象。

**工厂是一个类，它通过方法调用创建原型类(也称为接口)的对象**：

<img src="../assets/img_3.png">

当我们想要创建公共接口的对象同时向用户隐藏创建逻辑时，工厂模式非常有用。

## 3. 如何实现？

现在我们介绍如何实现它，首先我们看一下类图：

<img src="../assets/img_4.png">

接下来我们实现类图中的每个类。

### 3.1 实现Notifier接口

Notifier接口是一个原型，其他通知器类实现它：

```java
public interface Notifier<T> {
    void notify(T obj);
}
```

正如我们所见，Notifier是一个泛型接口，它有一个名为notify的方法。

### 3.2 Notifier接口的实现类

现在我们实现Notifier接口的两个实现类：

```java
public class StringNotifier implements Notifier<String> {

    @Override
    public void notify(String str) {
        System.out.println("Notifying: " + str);
    }
}

public class DateNotifier implements Notifier<Date> {

    @Override
    public void notify(Date date) {
        System.out.println("Notifying: " + date);
    }
}
```

其中一个输出简单的文本，另一个输出日期对象。

### 3.3 工厂实现

每次调用工厂类唯一的方法getNotifier()时，都会生成一个Notifier实例：

```java
public class NotifierFactory {

    public <T> Notifier<T> getNotifier(Class<T> c) {
        if (c == String.class) {
            return Record.STRING.make();
        }
        if (c == Date.class) {
            return Record.DATE.make();
        }
        return null;
    }
}
```

在上面的代码中，Record是一个枚举，其中包含两个名为STRING和DATE的常量。

### 3.4 Record实现

**Record枚举保留有效通知器类的记录，并在工厂类每次调用它时创建一个实例**：

```java
public enum Record {
    STRING {
        @Override
        public Notifier<String> make() {
            return new StringNotifier();
        }
    },
    DATE {
        @Override
        public Notifier<Date> make() {
            return new DateNotifier();
        }
    };

    public abstract <T> Notifier<T> make();
}
```

至此，我们成功地实现了工厂模式的所有组成部分。

## 4. 使用工厂

这里通过一个简单的单元测试使用工厂类：

```java
public static void main(String[] args) {
    NotifierFactory factory = new NotifierFactory();
    Notifier<String> stringNotifier = factory.getNotifier(String.class);
    Notifier<Date> dateNotifier = factory.getNotifier(Date.class);

    stringNotifier.notify("Hello world!");
    dateNotifier.notify(new Date());
}
```

运行我们的测试，生成如下结果：

```shell
Notifying: Hello world!
Notifying: Wed Dec 14 15:56:53 CST 2022
```

如我们所见，工厂已成功创建了两个适当类型的Notifier实例。

## 5. 总结

在本文中，我们学习了如何在Java中使用泛型实现工厂模式。