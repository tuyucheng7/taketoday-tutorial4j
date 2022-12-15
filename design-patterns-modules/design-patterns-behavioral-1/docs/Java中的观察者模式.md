## 1. 概述

在本教程中，我们介绍观察者模式，并介绍一些Java实现替代方案。

## 2. 什么是观察者模式

观察者是一种行为设计模式，它指定对象之间的通信：observable(可观察对象)和observers(观察者)。**可观察对象是一个对象，它通知观察者其状态的变化**。

例如，新闻机构可以在收到新闻时通知频道，接收新闻是改变新闻机构状态的原因，它会导致频道得到通知。

让我们看看我们如何自己实现它。首先，我们定义NewsAgency类：

```java
public class NewsAgency {
    private String news;
    private List<Channel> channels = new ArrayList<>();

    public void addObserver(Channel channel) {
        this.channels.add(channel);
    }

    public void removeObserver(Channel channel) {
        this.channels.remove(channel);
    }

    public void setNews(String news) {
        this.news = news;
        for (Channel channel : this.channels) {
            channel.update(this.news);
        }
    }
}
```

NewsAgency是可观察的，当新闻更新时，NewsAgency的状态会发生变化。当变化发生时，NewsAgency通过调用他们的update()方法来通知观察者。

**为了能够做到这一点，可观察对象需要保留对观察者的引用。在我们的例子中，它是channels变量**。

现在让我们看看观察者(即Channel类)是什么样子，它应该有update()方法，当NewsAgency的状态发生变化时调用该方法：

```java
public class NewsChannel implements Channel {
    private String news;

    @Override
    public void update(Object news) {
        this.setNews((String) news);
    } 

    // standard getter and setter
}
```

Channel接口只有一个方法：

```java
public interface Channel {
    void update(Object o);
}
```

现在，如果我们将NewsChannel的实例添加到观察者的channels集合，并更改NewsAgency的状态，NewsChannel的实例将被更新：

```java
NewsAgency observable = new NewsAgency();
NewsChannel observer = new NewsChannel();

observable.addObserver(observer);
observable.setNews("news");
assertEquals(observer.getNews(), "news");
```

Java核心库中有一个预定义的Observer接口，这使得实现观察者模式变得更加简单。

## 3. 观察者实现

[java.util.Observer](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Observer.html)接口定义了update()方法，因此不需要像上一节那样我们自己定义它。

让我们看看如何在我们的实现中使用它：

```java
public class ONewsChannel implements Observer {

    private String news;

    @Override
    public void update(Observable o, Object news) {
        this.setNews((String) news);
    }

    // standard getter and setter
}
```

在这里，第二个参数来自Observable，我们将在下面看到。

要定义可观察对象，我们需要扩展Java的Observable类：

```java
public class ONewsAgency extends Observable {
    private String news;

    public void setNews(String news) {
        this.news = news;
        setChanged();
        notifyObservers(news);
    }
}
```

请注意，我们不需要直接调用观察者的update()方法。我们只需调用setChanged()和notifyObservers()，Observable类会为我们完成剩下的工作。

它还包含一个观察者集合，并公开维护该集合的方法，addObserver()和deleteObserver()。

为了测试结果，我们只需要将观察者添加到这个集合中并设置新闻：

```java
ONewsAgency observable = new ONewsAgency();
ONewsChannel observer = new ONewsChannel();

observable.addObserver(observer);
observable.setNews("news");
assertEquals(observer.getNews(), "news");
```

Observer接口并不完美，自Java 9以来已被弃用。其中一个缺点是Observable不是接口，它是一个类，这就是为什么子类不能用作可观察对象的原因。

此外，开发人员可以覆盖Observable的一些同步方法并破坏它们的线程安全。

现在让我们看一下[PropertyChangeListener](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/java/beans/PropertyChangeListener.html)接口，推荐使用它而不是Observer。

## 4. 使用PropertyChangeListener实现

**在此实现中，可观察对象必须保留对[PropertyChangeSupport](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/java/beans/PropertyChangeSupport.html)实例的引用**。当类的属性发生更改时，它有助于向观察者发送通知。

下面我们定义可观察对象：

```java
public class PCLNewsAgency {
    private String news;

    private PropertyChangeSupport support;

    public PCLNewsAgency() {
        support = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void setNews(String value) {
        support.firePropertyChange("news", this.news, value);
        this.news = value;
    }
}
```

使用这种支持，我们可以添加和删除观察者，并在可观察对象的状态发生变化时通知他们：

```java
support.firePropertyChange("news", this.news, value);
```

这里，第一个参数是观察到的属性的名称，因此，第二个和第三个参数是它的旧值和新值。

观察者应该实现[PropertyChangeListener](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/java/beans/PropertyChangeListener.html)：

```java
public class PCLNewsChannel implements PropertyChangeListener {

    private String news;

    public void propertyChange(PropertyChangeEvent evt) {
        this.setNews((String) evt.getNewValue());
    }

    // standard getter and setter
}
```

由于PropertyChangeSupport类为我们进行连接，我们可以从事件中恢复新的属性值。

下面我们测试一下实现以确保它也能正常工作：

```java
PCLNewsAgency observable = new PCLNewsAgency();
PCLNewsChannel observer = new PCLNewsChannel();

observable.addPropertyChangeListener(observer);
observable.setNews("news");

assertEquals(observer.getNews(), "news");
```

## 5. 总结

在本文中，我们介绍了两种在Java中实现观察者设计模式的方法，并提到了PropertyChangeListener方法是首选。