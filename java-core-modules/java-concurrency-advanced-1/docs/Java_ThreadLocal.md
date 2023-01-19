## 1. 概述

在本文中，我们介绍java.lang包中的ThreadLocal。它使我们能够为当前线程单独存储数据，并将其简单地包装在一种特殊类型的对象中。

## 2. ThreadLocal API

ThreadLocal允许我们存储只能由特定线程访问的数据。

假设我们希望有一个与特定线程绑定的Integer值：

```text
ThreadLocal<Integer> threadLocalValue = new ThreadLocal<>();
```

接下来，当我们想从线程中使用这个值时，我们只需要调用get()或set()方法。简单地说，我们可以认为ThreadLocal将数据存储在以线程为key的map中。

因此，当我们对threadLocalValue调用get()方法时，将为请求的线程返回一个Integer值：

```text
threadLocalValue.set(1);
Integer result = threadLocalValue.get();
```

我们可以通过使用withInitial()静态方法并向其传递Supplier参数来构造ThreadLocal的实例：

```text
ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 1);
```

要从ThreadLocal中删除该值，我们可以调用remove()方法：

为了了解如何正确使用ThreadLocal，我们首先来看一个不使用ThreadLocal的示例。

## 3. 在Map中存储数据

让我们考虑一个程序，它需要根据给定的用户id存储特定于用户的上下文数据：

```java
public class Context {
    private final String userName;

    Context(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "Context{" + "userNameSecret='" + userName + ''' + '}';
    }
}
```

我们希望每个用户id有一个线程。我们将创建一个SharedMapWithUserContext类来实现Runnable接口。
run()方法中的实现通过UserRepository类调用一些数据库，该类返回给定用户id的Context对象。

接下来，我们将该Context存储在由userId为key的ConcurrentHashMap中：

```java
public class SharedMapWithUserContext implements Runnable {
    final static Map<Integer, Context> userContextPerUserId = new ConcurrentHashMap<>();
    private final Integer userId;
    private final UserRepository userRepository = new UserRepository();

    SharedMapWithUserContext(Integer userId) {
        this.userId = userId;
    }

    @Override
    public void run() {
        String userName = userRepository.getUserNameForUserId(userId);
        userContextPerUserId.put(userId, new Context(userName));
    }
}

public class UserRepository {
    String getUserNameForUserId(Integer userId) {
        return UUID.randomUUID().toString();
    }
}
```

我们可以通过为两个不同的用户id创建并启动两个线程，并断言userContextPerUserId中有两个键值对：

```java
class ThreadLocalIntegrationTest {

    @Test
    void givenThreadThatStoresContextInAMap_whenStartThread_thenShouldSetContextForBothUsers() throws ExecutionException, InterruptedException {
        SharedMapWithUserContext firstUser = new SharedMapWithUserContext(1);
        SharedMapWithUserContext secondUser = new SharedMapWithUserContext(2);
        new Thread(firstUser).start();
        new Thread(secondUser).start();

        Thread.sleep(3000);
        assertEquals(SharedMapWithUserContext.userContextPerUserId.size(), 2);
    }
}
```

## 4. 在ThreadLocal中存储数据

我们可以重写上一个示例，使用ThreadLocal存储用户Context实例。每个线程都有自己的ThreadLocal实例。

在使用ThreadLocal时，我们需要非常小心，因为每个ThreadLocal实例都与一个特定的线程相关联。
在我们的示例中，我们为每个特定的userId设置了一个专用线程，并且该线程由我们创建，因此我们可以完全控制它。

run()方法将获取用户的Context，并使用set()方法将其保存到ThreadLocal变量中：

```java
public class ThreadLocalWithUserContext implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ThreadLocalWithUserContext.class);
    private static final ThreadLocal<Context> userContext = new ThreadLocal<>();
    private final Integer userId;
    private final UserRepository userRepository = new UserRepository();

    ThreadLocalWithUserContext(Integer userId) {
        this.userId = userId;
    }

    @Override
    public void run() {
        String userName = userRepository.getUserNameForUserId(userId);
        userContext.set(new Context(userName));
        LOG.debug("thread context for given userId: " + userId + " is: " + userContext.get());
    }
}
```

我们可以通过启动两个线程来测试它，这两个线程将对给定的用户id执行操作：

```java
class ThreadLocalIntegrationTest {

    @Test
    void givenThreadThatStoresContextInThreadLocal_whenStartThread_thenShouldStoreContextInThreadLocal() throws ExecutionException, InterruptedException {
        ThreadLocalWithUserContext firstUser = new ThreadLocalWithUserContext(1);
        ThreadLocalWithUserContext secondUser = new ThreadLocalWithUserContext(2);
        new Thread(firstUser).start();
        new Thread(secondUser).start();

        Thread.sleep(3000);
    }
}
```

运行此代码后，我们可以在控制台日志输出中看到为每个给定线程设置了ThreadLocal：

```text
thread context for given userId: 2 is: Context{userNameSecret='3a62760d-b736-47be-af6b-1dad7894a269'} 
thread context for given userId: 1 is: Context{userNameSecret='e3a26c28-a64f-4efe-aa00-96da78736b1b'}
```

## 5. ThreadLocal和ThreadPool

ThreadLocal提供了一个易于使用的API，将一些值限制在每个线程中。这是在Java中实现线程安全的合理方法。
然而，当我们同时使用ThreadLocal和线程池时，我们应该格外小心。

为了更好地理解这个可能的问题，让我们考虑以下场景：

1. 首先，应用程序从线程池中借用一个线程。
2. 然后将一些线程特定的值存储到当前线程的ThreadLocal中。
3. 当前线程执行完成后，应用程序将借用的线程返回池中。
4. 一段时间后，应用程序借用同一个线程来处理另一个请求。
5. 由于应用程序上次没有执行必要的清理，它可能会对新请求重新使用相同的ThreadLocal数据。

在高度并发的应用程序中，这可能会导致意外的后果。

解决此问题的一种方法是在使用完每个ThreadLocal后手动删除它。因为这种方法需要严格的代码审查，所以很容易出错。

### 5.1 扩展ThreadPoolExecutor

事实证明，可以继承ThreadPoolExecutor类，并为beforeExecute()和afterExecute()方法提供自定义钩子实现。
线程池将在使用借用的线程运行任何操作之前调用beforeExecute()方法。
另一方面，它将在执行我们的逻辑后调用afterExecute()方法。

因此，我们可以继承ThreadPoolExecutor类并在afterExecute()方法中删除ThreadLocal数据：

```java
public class ThreadLocalAwareThreadPool extends ThreadPoolExecutor {

    public ThreadLocalAwareThreadPool(int corePoolSize,
                                      int maximumPoolSize,
                                      long keepAliveTime,
                                      TimeUnit unit,
                                      BlockingQueue<Runnable> workQueue,
                                      ThreadFactory threadFactory,
                                      RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        // Call remove on each ThreadLocal
    }
}
```

如果我们向这个ExecutorService实现提交请求，那么我们可以确保使用ThreadLocal和线程池不会给我们的应用程序带来安全隐患。

## 6. 总结

在这篇简短的文章中，我们了解了ThreadLocal。实现了使用在线程之间共享的ConcurrentHashMap来存储与特定userId关联的上下文的逻辑。
然后我们重写了我们的示例以利用ThreadLocal来存储与特定userId和特定线程关联的数据。