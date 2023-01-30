## 1. 概述

在本文中，我们将简要了解异常是什么，并深入讨论Java中的链式异常。

简单地说，异常是扰乱程序执行的正常流程的事件。现在让我们确切地看看我们如何链接异常以从中获得更好的语义。

## 2. 链式异常

链式异常有助于识别应用程序中一个异常导致另一个异常的情况。

**例如，考虑一个方法，该方法由于尝试除以零而抛出ArithmeticException，但异常的实际原因是导致除数为零的I/O错误**。该方法将向调用方抛出ArithmeticException，调用者不知道异常的实际原因，链式异常用于这种情况。

这个概念是在JDK 1.4中引入的。

让我们看看Java中如何支持链式异常。

## 3. Throwable类

Throwable类有一些构造函数和方法来支持链式异常。首先，让我们看一下构造函数。

-   **Throwable(Throwable cause)**：Throwable有一个参数，它指定异常的实际原因
-   **Throwable(String desc, Throwable cause)**：此构造函数接收异常描述以及异常的实际原因

接下来我们看一下这个类提供的方法：

-   **getCause()方法**：此方法返回与当前Exception关联的实际原因。
-   **initCause()方法**：它通过调用Exception来设置潜在原因。

## 4. 例子

现在，让我们看一下我们将设置自己的异常描述并抛出链式异常的示例：

```java
public class MyChainedException {

    public void main(String[] args) {
        try {
            throw new ArithmeticException("Top Level Exception.")
                  .initCause(new IOException("IO cause."));
        } catch(ArithmeticException ae) {
            System.out.println("Caught : " + ae);
            System.out.println("Actual cause: "+ ae.getCause());
        }
    }
}
```

正如猜测的那样，这将导致：

```shell
Caught: java.lang.ArithmeticException: Top Level Exception.
Actual cause: java.io.IOException: IO cause.
```

## 5. 为什么链式异常？

我们需要链接异常以使日志可读，让我们写两个例子。首先是不链接异常，其次是链接异常。稍后，我们将比较日志在这两种情况下的行为方式。

首先，我们将创建一系列异常：

```java
class NoLeaveGrantedException extends Exception {

    public NoLeaveGrantedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoLeaveGrantedException(String message) {
        super(message);
    }
}

class TeamLeadUpsetException extends Exception {
    // Both Constructors
}
```

现在，让我们开始在代码示例中使用上述异常。

### 5.1 不使用链式异常

让我们在不链接我们的自定义异常的情况下编写一个示例程序。

```java
public class MainClass {

    public void main(String[] args) throws Exception {
        getLeave();
    }

    void getLeave() throws NoLeaveGrantedException {
        try {
            howIsTeamLead();
        } catch (TeamLeadUpsetException e) {
            e.printStackTrace();
            throw new NoLeaveGrantedException("Leave not sanctioned.");
        }
    }

    void howIsTeamLead() throws TeamLeadUpsetException {
        throw new TeamLeadUpsetException("Team Lead Upset");
    }
}
```

在上面的示例中，日志将如下所示：

```shell
cn.tuyucheng.taketoday.chainedexception.exceptions.TeamLeadUpsetException: Team lead Upset
    at cn.tuyucheng.taketoday.chainedexception.exceptions.MainClass.howIsTeamLead(MainClass.java:46)
    at cn.tuyucheng.taketoday.chainedexception.exceptions.MainClass.getLeave(MainClass.java:34)
    at cn.tuyucheng.taketoday.chainedexception.exceptions.MainClass.main(MainClass.java:29)
Exception in thread "main" cn.tuyucheng.taketoday.chainedexception.exceptions.NoLeaveGrantedException: Leave not sanctioned.
    at cn.tuyucheng.taketoday.chainedexception.exceptions.MainClass.getLeave(MainClass.java:37)
    at cn.tuyucheng.taketoday.chainedexception.exceptions.MainClass.main(MainClass.java:29)
```

### 5.2 使用链式异常

接下来，让我们编写一个链接自定义异常的示例：

```java
public class MainClass {
    public void main(String[] args) throws Exception {
        getLeave();
    }

    public getLeave() throws NoLeaveGrantedException {
        try {
            howIsTeamLead();
        } catch (TeamLeadUpsetException e) {
            throw new NoLeaveGrantedException("Leave not sanctioned.", e);
        }
    }

    public void howIsTeamLead() throws TeamLeadUpsetException {
        throw new TeamLeadUpsetException("Team lead Upset.");
    }
}
```

最后我们看一下链式异常获取的日志：

```shell
Exception in thread "main" cn.tuyucheng.taketoday.chainedexception.exceptions.NoLeaveGrantedException: Leave not sanctioned. 
    at cn.tuyucheng.taketoday.chainedexception.exceptions.MainClass.getLeave(MainClass.java:36) 
    at cn.tuyucheng.taketoday.chainedexception.exceptions.MainClass.main(MainClass.java:29) 
Caused by: cn.tuyucheng.taketoday.chainedexception.exceptions.TeamLeadUpsetException: Team lead Upset.
    at cn.tuyucheng.taketoday.chainedexception.exceptions.MainClass.howIsTeamLead(MainClass.java:44) 
    at cn.tuyucheng.taketoday.chainedexception.exceptions.MainClass.getLeave(MainClass.java:34) 
    ... 1 more
```

我们可以很容易地比较显示的日志并得出总结，链式异常导致更清晰的日志。

## 6. 总结

在本文中，我们了解了链式异常的概念。