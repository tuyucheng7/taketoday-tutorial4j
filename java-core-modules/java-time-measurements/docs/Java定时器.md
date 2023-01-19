## 1. 定时器——基础知识

Timer和TimerTask是我们用来在后台线程中安排任务的 java util 类。基本上，[TimerTask](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/TimerTask.html)是要执行的任务，而[Timer](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Timer.html)是调度程序。

## 2.一次安排任务

### 2.1. 在给定的延迟之后

让我们首先在Timer的帮助下简单地运行一个任务：

```java
@Test
public void givenUsingTimer_whenSchedulingTaskOnce_thenCorrect() {
    TimerTask task = new TimerTask() {
        public void run() {
            System.out.println("Task performed on: " + new Date() + "n" +
              "Thread's name: " + Thread.currentThread().getName());
        }
    };
    Timer timer = new Timer("Timer");
    
    long delay = 1000L;
    timer.schedule(task, delay);
}
```

这会在一定延迟后执行任务，我们将其作为schedule()方法的第二个参数。在下一节中，我们将了解如何在给定的日期和时间安排任务。

请注意，如果我们将其作为 JUnit 测试运行，我们应该添加Thread.sleep(delay  2)调用以允许 Timer 的线程在 Junit 测试停止执行之前运行任务。

### 2.2. 在给定的日期和时间

现在让我们看一下Timer#schedule(TimerTask, Date)方法，它采用Date而不是long作为它的第二个参数。这使我们能够在某个时刻安排任务，而不是在延迟之后。

这一次，假设我们有一个旧的遗留数据库，我们希望将其数据迁移到具有更好模式的新数据库中。

我们可以创建一个DatabaseMigrationTask类来处理此迁移：

```java
public class DatabaseMigrationTask extends TimerTask {
    private List<String> oldDatabase;
    private List<String> newDatabase;

    public DatabaseMigrationTask(List<String> oldDatabase, List<String> newDatabase) {
        this.oldDatabase = oldDatabase;
        this.newDatabase = newDatabase;
    }

    @Override
    public void run() {
        newDatabase.addAll(oldDatabase);
    }
}
```

为简单起见，我们用 String 列表表示这 两个数据库。简而言之，我们的迁移包括将第一个列表中的数据放入第二个列表。

要在所需的时刻执行此迁移，我们必须使用 schedule ()方法的重载版本：

```java
List<String> oldDatabase = Arrays.asList("Harrison Ford", "Carrie Fisher", "Mark Hamill");
List<String> newDatabase = new ArrayList<>();

LocalDateTime twoSecondsLater = LocalDateTime.now().plusSeconds(2);
Date twoSecondsLaterAsDate = Date.from(twoSecondsLater.atZone(ZoneId.systemDefault()).toInstant());

new Timer().schedule(new DatabaseMigrationTask(oldDatabase, newDatabase), twoSecondsLaterAsDate);
```

如我们所见，我们将迁移任务以及执行日期提供给schedule()方法。

然后在twoSecondsLater指示的时间执行迁移：

```java
while (LocalDateTime.now().isBefore(twoSecondsLater)) {
    assertThat(newDatabase).isEmpty();
    Thread.sleep(500);
}
assertThat(newDatabase).containsExactlyElementsOf(oldDatabase);
```

在那一刻之前，不会发生迁移。

## 3.安排可重复的任务

现在我们已经介绍了如何安排任务的单次执行，让我们看看如何处理可重复的任务。

Timer类再次提供了多种可能性。我们可以设置重复以观察固定延迟或固定速率。

固定延迟意味着执行将在上次执行开始后的一段时间内开始，即使它被延迟(因此本身被延迟)。

假设我们想要每两秒安排一次任务，第一次执行需要一秒，第二次需要两秒，但会延迟一秒。然后第三次执行从第五秒开始：

```plaintext
0s     1s    2s     3s           5s
|--T1--|
|-----2s-----|--1s--|-----T2-----|
|-----2s-----|--1s--|-----2s-----|--T3--|
```

另一方面，固定速率意味着每次执行都将遵守初始计划，无论之前的执行是否已延迟。

让我们重用我们之前的例子。使用固定速率，第二个任务将在三秒后开始(因为延迟)，但第三个任务将在四秒后开始(尊重每两秒执行一次的初始计划)：

```plaintext
0s     1s    2s     3s    4s
|--T1--|       
|-----2s-----|--1s--|-----T2-----|
|-----2s-----|-----2s-----|--T3--|
```

现在我们已经介绍了这两个原则，让我们看看如何使用它们。

为了使用固定延迟调度，schedule()方法还有两个重载，每个重载一个额外的参数，以毫秒为单位说明周期性。

为什么有两个重载？因为仍然有可能在某个时刻或在某个延迟后开始任务。

至于固定速率调度，我们有两个 scheduleAtFixedRate()方法，它们也采用以毫秒为单位的周期性。同样，我们有一种方法可以在给定的日期和时间开始任务，另一种方法可以在给定的延迟后开始。

还值得一提的是，如果任务执行的时间超过周期，它会延迟整个执行链，无论我们使用固定延迟还是固定速率。

### 3.1. 固定延迟

现在让我们假设我们想要实现一个新闻通讯系统，每周向我们的关注者发送一封电子邮件。在这种情况下，重复性任务似乎是理想的。

因此，让我们每秒安排一次时事通讯，这基本上是垃圾邮件，但由于发送是假的，所以我们可以开始了。

首先，我们将设计一个NewsletterTask：

```java
public class NewsletterTask extends TimerTask {
    @Override
    public void run() {
        System.out.println("Email sent at: " 
          + LocalDateTime.ofInstant(Instant.ofEpochMilli(scheduledExecutionTime()), 
          ZoneId.systemDefault()));
    }
}
```

每次执行时，任务都会打印其预定时间，我们使用TimerTask#scheduledExecutionTime()方法收集该时间。

那么如果我们想在固定延迟模式下每秒调度这个任务呢？我们将不得不使用我们之前提到的schedule()的重载版本：

```java
new Timer().schedule(new NewsletterTask(), 0, 1000);

for (int i = 0; i < 3; i++) {
    Thread.sleep(1000);
}
```

当然，我们只对少数情况进行测试：

```plaintext
Email sent at: 2020-01-01T10:50:30.860
Email sent at: 2020-01-01T10:50:31.860
Email sent at: 2020-01-01T10:50:32.861
Email sent at: 2020-01-01T10:50:33.861
```

正如我们所见，每次执行之间至少间隔一秒，但有时会延迟一毫秒。这种现象是由于我们决定使用固定延迟重复。

### 3.2. 固定利率

现在，如果我们使用固定频率重复会怎样？然后我们将不得不使用scheduledAtFixedRate()方法：

```java
new Timer().scheduleAtFixedRate(new NewsletterTask(), 0, 1000);

for (int i = 0; i < 3; i++) {
    Thread.sleep(1000);
}
```

这一次，执行没有被之前的延迟：

```plaintext
Email sent at: 2020-01-01T10:55:03.805
Email sent at: 2020-01-01T10:55:04.805
Email sent at: 2020-01-01T10:55:05.805
Email sent at: 2020-01-01T10:55:06.805
```

### 3.3. 安排每日任务

接下来，让我们每天运行一次任务：

```java
@Test
public void givenUsingTimer_whenSchedulingDailyTask_thenCorrect() {
    TimerTask repeatedTask = new TimerTask() {
        public void run() {
            System.out.println("Task performed on " + new Date());
        }
    };
    Timer timer = new Timer("Timer");
    
    long delay = 1000L;
    long period = 1000L  60L  60L  24L;
    timer.scheduleAtFixedRate(repeatedTask, delay, period);
}
```

## 4.取消Timer和TimerTask

可以通过几种方式取消任务的执行。

### 4.1. 取消TimerTask Inside Run

第一个选项是在TimerTask本身的run()方法实现中调用TimerTask.cancel()方法：

```java
@Test
public void givenUsingTimer_whenCancelingTimerTask_thenCorrect()
  throws InterruptedException {
    TimerTask task = new TimerTask() {
        public void run() {
            System.out.println("Task performed on " + new Date());
            cancel();
        }
    };
    Timer timer = new Timer("Timer");
    
    timer.scheduleAtFixedRate(task, 1000L, 1000L);
    
    Thread.sleep(1000L  2);
}
```

### 4.2. 取消定时器

另一种选择是在Timer对象上调用Timer.cancel()方法：

```java
@Test
public void givenUsingTimer_whenCancelingTimer_thenCorrect() 
  throws InterruptedException {
    TimerTask task = new TimerTask() {
        public void run() {
            System.out.println("Task performed on " + new Date());
        }
    };
    Timer timer = new Timer("Timer");
    
    timer.scheduleAtFixedRate(task, 1000L, 1000L);
    
    Thread.sleep(1000L  2); 
    timer.cancel(); 
}
```

### 4.3. 停止TimerTask Inside Run的线程

我们还可以在任务的run方法中停止线程，从而取消整个任务：

```java
@Test
public void givenUsingTimer_whenStoppingThread_thenTimerTaskIsCancelled() 
  throws InterruptedException {
    TimerTask task = new TimerTask() {
        public void run() {
            System.out.println("Task performed on " + new Date());
            // TODO: stop the thread here
        }
    };
    Timer timer = new Timer("Timer");
    
    timer.scheduleAtFixedRate(task, 1000L, 1000L);
    
    Thread.sleep(1000L  2); 
}
```

注意运行实现中的 TODO 指令；为了运行这个简单的例子，我们实际上需要停止线程。

在现实世界的自定义线程实现中，应该支持停止线程，但在这种情况下，我们可以忽略弃用并在 Thread 类本身上使用简单的停止API。

## 5.定时器与ExecutorService

我们也可以利用一个 ExecutorService 来调度定时器任务，而不是使用定时器。

以下是如何以指定时间间隔运行重复任务的快速示例：

```java
@Test
public void givenUsingExecutorService_whenSchedulingRepeatedTask_thenCorrect() 
  throws InterruptedException {
    TimerTask repeatedTask = new TimerTask() {
        public void run() {
            System.out.println("Task performed on " + new Date());
        }
    };
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    long delay  = 1000L;
    long period = 1000L;
    executor.scheduleAtFixedRate(repeatedTask, delay, period, TimeUnit.MILLISECONDS);
    Thread.sleep(delay + period  3);
    executor.shutdown();
}
```

那么Timer和ExecutorService解决方案之间的主要区别是什么：

-   定时器可以对系统时钟的变化敏感；ScheduledThreadPoolExecutor不是。
-   Timer只有一个执行线程；ScheduledThreadPoolExecutor可以配置任意数量的线程。
-   TimerTask中抛出的运行时异常会杀死线程，因此以下计划任务将不会继续运行；使用ScheduledThreadExecutor，当前任务会被取消，但其余的会继续运行。

## 六，总结

在本文中，我们说明了使用Java 中内置的简单而灵活的Timer和TimerTask基础结构来快速安排任务的多种方法。当然，如果我们需要的话，在Java世界中还有更复杂和更完整的解决方案，例如[Quartz 库](http://quartz-scheduler.org/)，但这是一个很好的起点。