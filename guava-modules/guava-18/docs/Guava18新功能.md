## 1. 概述

[Google Guava](https://github.com/google/guava)为库提供了可简化Java开发的实用程序。在本教程中，我们将了解[Guava 18 版本](https://github.com/google/guava/wiki/Release18)中引入的新功能。

## 2. MoreObjects实用类

Guava 18 添加了MoreObjects类，其中包含在java.util.Objects 中没有等效方法的方法。

从版本 18 开始，它仅包含toStringHelper方法的实现，可用于帮助你构建自己的toString方法。

-   toStringHelper(类 <?> clazz)
-   toStringHelper(对象自身)
-   toStringHelper(字符串类名)

通常，当你需要输出有关对象的一些信息时，会使用toString() 。通常它应该包含有关对象当前状态的详细信息。通过使用toStringHelper的实现之一，你可以轻松构建有用的toString()消息。

假设我们有一个User对象，其中包含一些需要在调用toString()时写入的字段。我们可以使用MoreObjects.toStringHelper(Object self)方法轻松完成此操作。

```java
public class User {

    private long id;
    private String name;

    public User(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("name", name)
            .toString();
    }
}

```

这里我们使用了toStringHelper(Object self)方法。通过此设置，我们可以创建一个示例用户来查看调用toString()时产生的输出。

```java
User user = new User(12L, "John Doe");
String userState = user.toString();
// userState: User{ id=12,name=John Doe }

```

如果配置类似，其他两个实现将返回相同的字符串：

toStringHelper(类 <?> clazz)

```java
@Override
public String toString() {
    return MoreObjects.toStringHelper(User.class)
        .add("id", id)
        .add("name", name)
        .toString();
}
```

toStringHelper(字符串类名)

```java
@Override
public String toString() {
    return MoreObjects.toStringHelper("User")
        .add("id", id)
        .add("name", name)
        .toString();
}
```

如果你在User类的扩展上调用toString() ，那么这些方法之间的区别就很明显了。例如，如果你有两种User：Administrator和Player，它们将产生不同的输出。


```java
public class Player extends User {
    public Player(long id, String name) {
        super(id, name);
    }
}

public class Administrator extends User {
    public Administrator(long id, String name) {
        super(id, name);
    }
}
```

如果你在User类中使用toStringHelper(Object self)那么你的Player.toString()将返回“ Player{id=12, name=John Doe} ”。但是，如果你使用toStringHelper(String className)或toStringHelper(Class<?> clazz)，Player.toString()将返回“ User{id=12, name=John Doe} ”。列出的类名将是父类而不是子类。

## 3. FluentIterable中的新方法

### 3.1. 概述

FluentIterable用于以链式方式对Iterable实例进行操作。让我们看看如何使用它。

假设你有上面示例中定义的用户对象列表，并且你希望过滤该列表以仅包含 18 岁或以上的用户。

```java
List<User> users = new ArrayList<>();
users.add(new User(1L, "John", 45));
users.add(new User(2L, "Michelle", 27));
users.add(new User(3L, "Max", 16));
users.add(new User(4L, "Sue", 10));
users.add(new User(5L, "Bill", 65));

Predicate<User> byAge = user -> user.getAge() >= 18;

List<String> results = FluentIterable.from(users)
                           .filter(byAge)
                           .transform(Functions.toStringFunction())
                           .toList();

```

结果列表将包含 John、Michelle 和 Bill 的信息。

### 3.2. FluentIterable.of(E[])

用这个方法。你可以从Object数组创建FluentIterable。

```java
User[] usersArray = { new User(1L, "John", 45), new User(2L, "Max", 15) } ;
FluentIterable<User> users = FluentIterable.of(usersArray);

```

你现在可以使用FluentIterable接口中提供的方法。

### 3.3. FluentIterable.append(E…)

你可以通过向现有FluentIterable添加更多元素来创建新的FluentIterable 。

```java
User[] usersArray = {new User(1L, "John", 45), new User(2L, "Max", 15)};

FluentIterable<User> users = FluentIterable.of(usersArray).append(
                                 new User(3L, "Sue", 23),
                                 new User(4L, "Bill", 17)
                             );

```

正如预期的那样，生成的FluentIterable的大小为 4。

### 3.4. FluentIterable.append(Iterable<? extends E>)

此方法的行为与前面的示例相同，但允许你将Iterable的任何现有实现的全部内容添加到FluentIterable中。

```java
User[] usersArray = { new User(1L, "John", 45), new User(2L, "Max", 15) };

List<User> usersList = new ArrayList<>();
usersList.add(new User(3L, "Diana", 32));

FluentIterable<User> users = FluentIterable.of(usersArray).append(usersList);

```

正如预期的那样，生成的FluentIterable的大小为 3。

### 3.5. FluentIterable.join(连接器)

FluentIterable.join (…)方法生成一个String表示FluentIterable的全部内容，由给定的String连接。

```java
User[] usersArray = { new User(1L, "John", 45), new User(2L, "Max", 15) };
FluentIterable<User> users = FluentIterable.of(usersArray);
String usersString = users.join(Joiner.on("; "));

```

usersString变量将包含对 FluentIterable 的每个元素调用toString ( ) 方法的输出，以“;”分隔。Joiner类提供了多个用于连接字符串的选项。

## 4.散列.crc32c

哈希函数是可用于将任意大小的数据映射到固定大小的数据的任何函数。它用于许多领域，例如密码学和检查传输数据中的错误。

Hashing.crc32c方法返回一个实现[CRC32C 算法](https://en.wikipedia.org/wiki/Cyclic_redundancy_check)的HashFunction。

```java
int receivedData = 123;
HashCode hashCode = Hashing.crc32c().hashInt(receivedData);
// hashCode: 495be649
```

## 5. InetAddresses.递减(InetAddress)

此方法返回一个新的InetAddress，它将比其输入“少一个”。

```java
InetAddress address = InetAddress.getByName("127.0.0.5");
InetAddress decrementedAddress = InetAddresses.decrement(address);
// decrementedAddress: 127.0.0.4
```

## 6. MoreExecutors中的新执行器 

### 6.1. 线程审查

在Java中，你可以使用多个线程来执行工作。为此，Java 具有Thread和Runnable类。

```java
ConcurrentHashMap<String, Boolean> threadExecutions = new ConcurrentHashMap<>();
Runnable logThreadRun = () -> threadExecutions.put(Thread.currentThread().getName(), true);

Thread t = new Thread(logThreadRun);
t.run();

Boolean isThreadExecuted = threadExecutions.get("main");
```

正如预期的那样，isThreadExecuted将为true。你还可以看到此Runnable将仅在主线程中运行。如果要使用多线程，可以针对不同的目的使用不同的Executor。

```java
ExecutorService executorService = Executors.newFixedThreadPool(2);
executorService.submit(logThreadRun);
executorService.submit(logThreadRun);
executorService.shutdown();

Boolean isThread1Executed = threadExecutions.get("pool-1-thread-1");
Boolean isThread2Executed = threadExecutions.get("pool-1-thread-2");
// isThread1Executed: true
// isThread2Executed: true
```

在此示例中，所有提交的工作都在ThreadPool线程中执行。

Guava 在其MoreExecutors类中提供了不同的方法。

### 6.2. 更多Executors.directExecutor()

这是一个轻量级的执行器，可以在调用execute方法的线程上运行任务。

```java
Executor executor = MoreExecutors.directExecutor();
executor.execute(logThreadRun);

Boolean isThreadExecuted = threadExecutions.get("main");
// isThreadExecuted: true
```

### 6.3. MoreExecutors.newDirectExecutorService()

此方法返回ListeningExecutorService的一个实例。它是Executor的较重实现，具有许多有用的方法。它类似于以前版本的 Guava 中已弃用的sameThreadExecutor()方法。

此ExecutorService将在调用execute()方法的线程上运行任务。

```java
ListeningExecutorService executor = MoreExecutors.newDirectExecutorService();
executor.execute(logThreadRun);

```

这个执行器有很多有用的方法，比如invokeAll、invokeAny、awaitTermination、submit、isShutdown、isTerminated、shutdown、shutdownNow。

## 七. 总结

Guava 18 对其不断增长的实用功能库进行了一些补充和改进。非常值得考虑在你的下一个项目中使用。