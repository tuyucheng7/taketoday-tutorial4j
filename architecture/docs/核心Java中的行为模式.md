## 1. 概述

最近我们查看了[创建设计模式](https://www.baeldung.com/java-creational-design-patterns) 以及在 JVM 和其他核心库中的何处可以找到它们。现在我们来看看[行为设计模式](https://www.baeldung.com/design-patterns-series)。这些重点关注我们的对象如何相互交互或我们如何与它们交互。

## 2.责任链

[责任链](https://www.baeldung.com/chain-of-responsibility-pattern)模式允许对象实现一个公共接口，并且每个实现都可以在适当的情况下委托给下一个实现。然后，这允许我们构建一个实现链，其中每个实现在调用链中的下一个元素之前或之后执行一些操作：

```java
interface ChainOfResponsibility {
    void perform();
}复制
class LoggingChain {
    private ChainOfResponsibility delegate;

    public void perform() {
        System.out.println("Starting chain");
        delegate.perform();
        System.out.println("Ending chain");
    }
}复制
```

在这里我们可以看到一个示例，其中我们的实现在委托调用之前和之后打印出来。

我们不需要拜访代表。我们可以决定不应该这样做，而是提前终止链。例如，如果有一些输入参数，我们可以验证它们并在它们无效时提前终止。

### 2.1. JVM 中的示例

[Servlet 过滤器](https://www.oracle.com/java/technologies/filters.html)是 JEE 生态系统中以这种方式工作的示例。单个实例接收 servlet 请求和响应，FilterChain实例代表整个过滤器链。然后每个人都应该执行其工作，然后终止链或调用chain.doFilter()将控制权传递给下一个过滤器：

```java
public class AuthenticatingFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
      throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (!"MyAuthToken".equals(httpRequest.getHeader("X-Auth-Token")) {
             return;
        }
        chain.doFilter(request, response);
    }
}复制
```

## 3.命令

命令模式允许我们将一些具体的行为——或命令——封装在一个公共接口[后面](https://www.baeldung.com/java-command-pattern)，这样它们就可以在运行时被正确触发。

通常我们会有一个 Command 接口，一个接收命令实例的 Receiver 实例，以及一个负责调用正确命令实例的 Invoker。然后我们可以定义我们的 Command 接口的不同实例来对接收者执行不同的操作：

```java
interface DoorCommand {
    perform(Door door);
}复制
class OpenDoorCommand implements DoorCommand {
    public void perform(Door door) {
        door.setState("open");
    }
}复制
```

在这里，我们有一个命令实现，它将门作为接收者并使门“打开”。然后，我们的调用程序可以在它希望打开给定的门时调用此命令，并且该命令封装了如何执行此操作。

将来，我们可能需要更改我们的OpenDoorCommand以检查门是否先锁上。此更改将完全在命令中进行，接收器和调用程序类不需要进行任何更改。

### 3.1. JVM 中的示例

这种模式的一个非常常见的例子是Swing 中的Action类：

```java
Action saveAction = new SaveAction();
button = new JButton(saveAction)复制
```

在这里，SaveAction是命令，使用此类的Swing JButton组件是调用者， Action实现被调用，ActionEvent作为接收者。

## 4.迭代器

迭代器模式允许我们跨集合中的元素工作并依次与每个元素交互。我们用它来编写对某些元素采用任意迭代器的函数，而不考虑它们来自何处。源可以是有序列表、无序列集或无限流：

```java
void printAll<T>(Iterator<T> iter) {
    while (iter.hasNext()) {
        System.out.println(iter.next());
    }
}复制
```

### 4.1. JVM 中的示例

所有 JVM 标准集合都通过公开iterator()方法来实现[Iterator 模式](https://www.baeldung.com/java-iterator)，该方法返回集合中元素的Iterator<T> 。Streams 也实现了相同的方法，除了在这种情况下，它可能是一个无限流，因此迭代器可能永远不会终止。

## 5.纪念品

Memento模式允许我们编写能够更改状态的对象，然后恢复到它们之前的状态[。](https://www.baeldung.com/java-memento-design-pattern)本质上是对象状态的“撤消”功能。

这可以通过在任何时候调用 setter 时存储先前的状态来相对容易地实现：

```java
class Undoable {
    private String value;
    private String previous;

    public void setValue(String newValue) {
        this.previous = this.value;
        this.value = newValue;
    }

    public void restoreState() {
        if (this.previous != null) {
            this.value = this.previous;
            this.previous = null;
        }
    }
}复制
```

这提供了撤消对对象所做的最后更改的能力。

这通常是通过将整个对象状态包装在单个对象中来实现的，称为 Memento。这允许在单个操作中保存和恢复整个状态，而不必单独保存每个字段。

### 5.1. JVM 中的示例

[JavaServer Faces提供了一个名为](https://www.baeldung.com/spring-jsf)StateHolder 的接口，允许实现者保存和恢复它们的状态。有几个标准组件实现了这一点，包括单独的组件——例如， HtmlInputFile、 HtmlInputText或HtmlSelectManyCheckbox —— 以及复合组件，如HtmlForm。

## 6.观察者

观察者模式允许一个对象向其他人表明发生了变化[。](https://www.baeldung.com/java-observer-pattern)通常我们会有一个 Subject——对象发出事件，以及一系列 Observers——对象接收这些事件。观察者将向主题注册他们想要了解更改的信息。一旦发生这种情况，主题中发生的任何更改都会通知观察者：

```java
class Observable {
    private String state;
    private Set<Consumer<String>> listeners = new HashSet<>;

    public void addListener(Consumer<String> listener) {
        this.listeners.add(listener);
    }

    public void setState(String newState) {
        this.state = state;
        for (Consumer<String> listener : listeners) {
            listener.accept(newState);
        }
    }
}复制
```

这需要一组事件侦听器，并在每次状态随新状态值发生变化时调用每个事件侦听器。

### 6.1. JVM 中的示例

Java 有一对标准的类可以让我们做到这一点——java.beans.PropertyChangeSupport和java.beans.PropertyChangeListener。

PropertyChangeSupport充当一个类，可以添加和删除观察者，并可以通知他们所有的状态变化。PropertyChangeListener是一个接口，我们的代码可以实现该接口以接收已发生的任何更改：

```java
PropertyChangeSupport observable = new PropertyChangeSupport();

// Add some observers to be notified when the value changes
observable.addPropertyChangeListener(evt -> System.out.println("Value changed: " + evt));

// Indicate that the value has changed and notify observers of the new value
observable.firePropertyChange("field", "old value", "new value");复制
```

请注意，还有另外一对类似乎更合适——java.util.Observer和java.util.Observable。但是，由于不灵活且不可靠，这些在 Java 9 中已被弃用。

## 7.策略

策略模式允许我们编写通用代码，然后将特定策略插入其中，为我们提供具体案例所需的特定行为[。](https://www.baeldung.com/java-strategy-pattern)

这通常通过具有表示策略的接口来实现。然后，客户端代码能够根据具体情况的需要编写实现此接口的具体类。例如，我们可能有一个系统需要通知最终用户并将通知机制实现为可插入策略：

```java
interface NotificationStrategy {
    void notify(User user, Message message);
}复制
class EmailNotificationStrategy implements NotificationStrategy {
    ....
}复制
class SMSNotificationStrategy implements NotificationStrategy {
    ....
}复制
```

然后，我们可以在运行时准确决定实际使用这些策略中的哪一个来将此消息发送给该用户。我们还可以编写新的策略来使用，而对系统的其余部分的影响最小。

### 7.1. JVM 中的示例

标准 Java 库广泛使用这种模式，通常采用的方式起初看起来并不明显。例如， Java 8 中引入的[Streams API](https://www.baeldung.com/java-streams)就大量使用了这种模式。提供给map()、filter()和其他方法的lambda都是提供给通用方法的可插入策略。

不过，例子可以追溯到更远的地方。Java 1.2 中引入的Comparator接口是一种策略，可以根据需要对集合中的元素进行排序。我们可以提供Comparator的不同实例，以根据需要以不同方式对同一列表进行排序：

```java
// Sort by name
Collections.sort(users, new UsersNameComparator());

// Sort by ID
Collections.sort(users, new UsersIdComparator());复制
```

## 8.模板法

当我们想要协调多个不同的方法一起工作时，就会使用模板[方法模式。](https://www.baeldung.com/java-template-method-pattern)我们将使用模板方法和一组一个或多个抽象方法定义一个基类——未实现或以某些默认行为实现的。模板方法然后以固定模式调用这些抽象方法。我们的代码然后实现这个类的子类，并根据需要实现这些抽象方法：

```java
class Component {
    public void render() {
        doRender();
        addEventListeners();
        syncData();
    }

    protected abstract void doRender();

    protected void addEventListeners() {}

    protected void syncData() {}
}复制
```

在这里，我们有一些任意的 UI 组件。我们的子类将实现doRender()方法来实际渲染组件。我们还可以选择实现addEventListeners()和syncData()方法。当我们的 UI 框架渲染这个组件时，它将保证所有三个组件都以正确的顺序被调用。

### 8.1. JVM 中的示例

Java Collections 使用的 AbstractList 、 AbstractSet 和 AbstractMap 有很多这种模式的例子。例如，indexOf()和lastIndexOf()方法都根据listIterator()方法工作，后者具有默认实现，但在某些子类中会被覆盖。同样，add(T)和addAll(int, T)方法都按照add(int, T)方法工作，后者没有默认实现，需要由子类实现。

Java IO 还在InputStream、OutputStream、Reader和Writer中使用了这种模式。例如，InputStream类有几个方法在read(byte[], int, int)方面工作，需要子类来实现。

## 9.访客

访问者模式允许我们的代码以类型安全的方式处理各种子类，[而](https://www.baeldung.com/java-visitor-pattern)无需求助于instanceof检查。对于我们需要支持的每个具体子类，我们将拥有一个带有一个方法的访问者接口。我们的基类将有一个accept(Visitor)方法。子类将各自调用此访问者的适当方法，将其自身传入。这允许我们在每个方法中实现具体行为，每个方法都知道它将使用具体类型：

```java
interface UserVisitor<T> {
    T visitStandardUser(StandardUser user);
    T visitAdminUser(AdminUser user);
    T visitSuperuser(Superuser user);
}复制
class StandardUser {
    public <T> T accept(UserVisitor<T> visitor) {
        return visitor.visitStandardUser(this);
    }
}复制
```

这里我们有我们的UserVisitor接口，上面有三种不同的访问者方法。我们的示例StandardUser调用适当的方法，同样将在AdminUser和Superuser中完成。然后我们可以写我们的访问者根据需要使用这些：

```java
class AuthenticatingVisitor {
    public Boolean visitStandardUser(StandardUser user) {
        return false;
    }
    public Boolean visitAdminUser(AdminUser user) {
        return user.hasPermission("write");
    }
    public Boolean visitSuperuser(Superuser user) {
        return true;
    }
}复制
```

我们的StandardUser从来没有权限，我们的超级用户总是有权限，我们的AdminUser可能有权限，但这需要在用户本身中查找。

### 9.1. JVM 中的示例

Java NIO2 框架将此模式与[Files.walkFileTree()](https://www.baeldung.com/java-nio2-file-visitor)结合使用。这需要一个FileVisitor的实现，它具有处理遍历文件树的各个不同方面的方法。然后我们的代码可以使用它来搜索文件、打印出匹配的文件、处理目录中的许多文件，或者需要在目录中工作的许多其他事情：

```java
Files.walkFileTree(startingDir, new SimpleFileVisitor() {
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        System.out.println("Found file: " + file);
    }

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        System.out.println("Found directory: " + dir);
    }
});复制
```

## 10.总结

在本文中，我们了解了用于对象行为的各种设计模式。我们还查看了在核心 JVM 中使用的这些模式的示例，因此我们可以看到它们以许多应用程序已经从中受益的方式使用。