## 1. 概述

在编写单元测试时，我们有时会提供输出的多个断言。当这些断言中的第一个失败时，测试将停止。这意味着我们不知道是否有任何后续断言会通过或失败，这可能会增加调试时间。

我们可以通过将多个断言包装到一个操作中来解决这个问题。

在这个简短的教程中，我们将学习如何使用[JUnit 5]()中引入的assertAll()方法，并了解它与使用多个断言有何不同。

## 2. 模型类

我们将使用User类来帮助我们的示例：

```java
public class User {
    String username;
    String email;
    boolean activated;
    // constructors
    // getters and setters
}
```

## 3. 使用多个断言

让我们从一个所有断言都会失败的例子开始：

```java
User user = new User("tuyucheng", "support@tuyucheng.com", false);
assertEquals("admin", user.getUsername(), "Username should be admin");
assertEquals("admin@tuyucheng.com", user.getEmail(), "Email should be admin@tuyucheng.com");
assertTrue(user.getActivated(), "User should be activated");
```

运行测试后，只有第一个断言失败：

```shell
org.opentest4j.AssertionFailedError: Username should be admin ==> 
Expected :admin
Actual   :tuyucheng
```

假设我们修复了失败的代码或测试并重新运行测试，然后我们会遇到第二次失败，依此类推。**在这种情况下，最好将所有这些断言分组到单个通过/失败中**。

## 4. 使用assertAll()方法

我们可以使用assertAll()对JUnit 5的断言进行分组。

### 4.1 理解assertAll()

assertAll()断言函数接收多个Executable对象的集合：

```java
assertAll(
	"Grouped Assertions of User",
	() -> assertEquals("tuyucheng", user.getUsername(), "Username should be tuyucheng"),
	// more assertions
	// ...
);
```

因此，我们可以使用lambdas来提供我们的每个断言，然后lambda将被调用以在assertAll()提供的分组内运行断言。

在这里，在assertAll()的第一个参数中，我们还提供了一个描述来解释整个断言组的含义。

### 4.2 使用assertAll()对断言进行分组

让我们看看完整的例子：

```java
User user = new User("tuyucheng", "support@tuyucheng.com", false);

assertAll("Grouped Assertions of User",
	() -> assertEquals("admin", user.getUsername(), "Username should be admin"),
	() -> assertEquals("admin@tuyucheng.com", user.getEmail(), "Email should be admin@tuyucheng.com"),
	() -> assertTrue(user.getActivated(), "User should be activated")
);
```

现在，让我们看看运行测试时会发生什么：

```shell
org.opentest4j.MultipleFailuresError: Grouped Assertions of User (3 failures)
org.opentest4j.AssertionFailedError: Username should be admin ==> expected: <admin> but was: <tuyucheng>
org.opentest4j.AssertionFailedError: Email should be admin@tuyucheng.com ==> expected: <admin@tuyucheng.com> but was: <support@tuyucheng.com>
org.opentest4j.AssertionFailedError: User should be activated ==> expected: <true> but was: <false>
```

与多个断言发生的情况相反，**这次执行了所有断言，并且它们的失败在MultipleFailuresError消息中报告**。

我们应该注意到assertAll()只处理AssertionError。**如果任何断言以异常结束，而不是通常的AssertionError，则执行将立即停止并且错误输出将与异常相关，而不是与MultipleFailuresError相关**。

## 5. 总结

在本文中，我们学习了在JUnit 5中使用assertAll()并了解了它与使用多个单独的断言有何不同。