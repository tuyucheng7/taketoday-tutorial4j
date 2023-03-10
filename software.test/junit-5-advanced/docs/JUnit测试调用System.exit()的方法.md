## 1. 概述

在某些情况下，方法可能需要调用System.exit()并关闭应用程序。例如，这可能是应用程序应该只运行一次然后退出，或者发生致命错误(如丢失数据库连接)的情况。

如果方法调用[System.exit()](https://www.baeldung.com/java-system-exit)，则很难从单元测试中调用它并进行断言，因为这会导致单元测试退出。

在本教程中，我们将探讨如何在使用[JUnit](https://www.baeldung.com/junit)时测试调用System.exit()的方法。

## 2. 项目设置

让我们从创建一个Java项目开始。我们将创建一个将任务保存到数据库的服务。如果将任务保存到数据库引发异常，该服务将调用System.exit()。

### 2.1 JUnit和Mockito依赖项

让我们添加[JUnit](https://central.sonatype.com/artifact/org.junit.jupiter/junit-jupiter-api/5.9.2)和[Mockito](https://central.sonatype.com/artifact/org.mockito/mockito-core/5.1.1)依赖项：

```xml
<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-api</artifactId>
        <version>5.9.1</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>4.8.1</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 2.2 代码设置

我们将首先添加一个名为Task的实体类：

```java
public class Task {
    private String name;

    // getters, setters and constructor
}
```

接下来，让我们创建一个负责与数据库交互的[DAO](https://www.baeldung.com/java-dao-pattern)：

```java
public class TaskDAO {
    public void save(Task task) throws Exception {
        // save the task
    }
}
```

save()方法的实现对于本文的目的并不重要。

接下来，让我们创建一个调用DAO的TaskService：

```java
public class TaskService {

    private final TaskDAO taskDAO = new TaskDAO();

    public void saveTask(Task task) {
        try {
            taskDAO.save(task);
        } catch (Exception e) {
            System.exit(1);
        }
    }
}
```

我们应该注意，**如果save()方法抛出异常，应用程序将退出**。

### 2.3 单元测试

让我们尝试为上面的saveTask()方法编写单元测试：

```java
@Test
void givenDAOThrowsException_whenSaveTaskIsCalled_thenSystemExitIsCalled() throws Exception {
    Task task = new Task("test");
    TaskDAO taskDAO = mock(TaskDAO.class);
    TaskService service = new TaskService(taskDAO);
    doThrow(new NullPointerException()).when(taskDAO).save(task);
    service.saveTask(task);
}
```

我们Mock了TaskDAO在调用save()方法时抛出异常。这将导致执行saveTask()的catch块，它调用System.exit()。

**如果我们运行这个测试，我们会发现它在完成之前就退出了**：

<img src="../assets/img_3.png">

## 3. 使用安全管理器的解决方法(Java 17之前)

**我们可以提供我们的[安全管理器](https://www.baeldung.com/java-security-manager)来防止单元测试退出**。我们的安全管理器将阻止对System.exit()的调用并在调用发生时抛出异常。然后我们可以捕获抛出的异常来进行断言。默认情况下，Java不使用安全管理器，并且允许调用所有System方法。

**需要注意的是，SecurityManager在Java 17中已被弃用，如果与Java 17或更高版本一起使用，将会抛出异常**。

### 3.1 SecurityManager

我们看一下安全管理器的实现：

```java
class NoExitSecurityManager extends SecurityManager {
    @Override
    public void checkPermission(Permission perm) {
    }

    @Override
    public void checkExit(int status) {
        super.checkExit(status);
        throw new RuntimeException(String.valueOf(status));
    }
}
```

让我们谈谈这段代码的几个重要行为：

- **checkPermission()方法需要被重写，因为如果调用System.exit()，安全管理器的默认实现会抛出异常**
- 每当我们的代码调用System.exit()时，NoExitSecurityManager的checkExit()方法就会介入并抛出异常
- 只要它是非受检的异常，就可以抛出任何其他异常而不是RuntimeException

### 3.2 修改测试

下一步是修改测试以使用SecurityManager实现。我们将**添加setUp()和tearDown()方法来在测试运行时设置和删除安全管理器**：

```java
@BeforeEach
void setUp() {
    System.setSecurityManager(new NoExitSecurityManager());
}
```

最后，让我们**更改测试用例以捕获调用System.exit()时将抛出的RuntimeException**：

```java
@Test
void givenDAOThrowsException_whenSaveTaskIsCalled_thenSystemExitIsCalled() throws Exception {
    Task task = new Task("test");
    TaskDAO taskDAO = mock(TaskDAO.class);
    TaskService service = new TaskService(taskDAO);
    try {
        doThrow(new NullPointerException()).when(taskDAO).save(task);
        service.saveTask(task);
    } catch (RuntimeException e) {
         Assertions.assertEquals("1", e.getMessage());
    }
}
```

我们使用catch块来**验证退出消息的状态是否与DAO设置的退出代码相同**。

## 4. System Lambda库

编写测试的另一种方法是使用[System Lambda库](https://github.com/stefanbirkner/system-lambda)。该库有助于测试调用System类方法的代码，我们将探讨如何使用这个库来编写我们的测试。

### 4.1 依赖关系

让我们从添加[system-lambda](https://central.sonatype.com/artifact/com.github.stefanbirkner/system-lambda/1.2.1)依赖项开始：

```xml
<dependency>
    <groupId>com.github.stefanbirkner</groupId>
    <artifactId>system-lambda</artifactId>
    <version>1.2.1</version>
    <scope>test</scope>
</dependency>
```

### 4.2 修改测试用例

接下来，让我们修改测试用例。我们将**使用库的catchSystemExit()方法包装我们的原始测试代码**。此方法将阻止系统退出，而是返回退出代码。然后我们将断言退出代码：

```java
@Test
void givenDAOThrowsException_whenSaveTaskIsCalled_thenSystemExitIsCalled() throws Exception {
    int statusCode = catchSystemExit(() -> {
        Task task = new Task("test");
        TaskDAO taskDAO = mock(TaskDAO.class);
        TaskService service = new TaskService(taskDAO);
        doThrow(new NullPointerException()).when(taskDAO).save(task);
        service.saveTask(task);
    });
    Assertions.assertEquals(1, statusCode);
}
```

## 5. 使用JMockit

**[JMockit](https://www.baeldung.com/jmockit-101)库提供了一种Mock System类的方法。我们可以用它来改变System.exit()的行为并防止系统退出**。

### 5.1 依赖项

让我们添加[JMockit](https://central.sonatype.com/artifact/org.jmockit/jmockit/1.49)依赖项：

```xml
<dependency>
    <groupId>org.jmockit</groupId>
    <artifactId>jmockit</artifactId>
    <version>1.49</version>
    <scope>test</scope>
</dependency>
```

除此之外，我们需要为JMockit添加-javaagent JVM初始化参数。为此，我们可以使用[Maven Surefire](https://www.baeldung.com/maven-surefire-plugin)插件：

```xml
<plugins>
    <plugin>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>2.22.2</version>
        <configuration>
            <argLine>
                -javaagent:"${settings.localRepository}"/org/jmockit/jmockit/1.49/jmockit-1.49.jar
            </argLine>
        </configuration>
    </plugin>
</plugins>
```

这会导致JMockit在JUnit之前初始化。这样，所有的测试用例都通过JMockit运行。如果使用旧版本的JMockit，则不需要初始化参数。

### 5.2 修改测试

让我们修改测试以Mock System.exit()：

```java
@Test
public void givenDAOThrowsException_whenSaveTaskIsCalled_thenSystemExitIsCalled() throws Exception {
    new MockUp<System>() {
        @Mock
        public void exit(int value) {
            throw new RuntimeException(String.valueOf(value));
        }
    };

    Task task = new Task("test");
    TaskDAO taskDAO = mock(TaskDAO.class);
    TaskService service = new TaskService(taskDAO);
    try {
        doThrow(new NullPointerException()).when(taskDAO).save(task);
        service.saveTask(task);
    } catch (RuntimeException e) {
        Assertions.assertEquals("1", e.getMessage());
    }
}
```

这将抛出一个异常，我们可以像前面的安全管理器示例中那样捕获并断言该异常。

## 6. 总结

在本文中，我们了解了使用JUnit来测试调用System.exit()的代码是多么困难。然后，我们探索了一种通过添加安全管理器来解决它的方法。我们还研究了System Lambda和JMockit库，它们提供了解决问题的更简单方法。