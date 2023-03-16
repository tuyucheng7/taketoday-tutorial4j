## 1. 概述

在本快速教程中，我们将了解[@DirtiesContext](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/annotation/DirtiesContext.html)注解的作用。我们还将展示使用该注解进行测试的标准方法。

## 2. @DirtiesContext

@DirtiesContext是一个**Spring测试注解**。它指示关联的测试或类修改ApplicationContext。它告诉测试框架关闭并重新创建上下文以供以后的测试使用。

我们可以使用该注解标注一个测试方法或整个类。**通过设置MethodMode或ClassMode，我们可以控制Spring何时将上下文标记为关闭**。

如果我们将@DirtiesContext放在一个类上，则注解将应用于具有给定ClassMode的类中的每个方法。

## 3. 在不清除Spring上下文的情况下进行测试

假设我们有一个User类：

```java
public class User {
    String firstName;
    String lastName;
}
```

我们还有一个非常简单的UserCache：

```java
@Component
public class UserCache {

    @Getter
    private final Set<String> userList = new HashSet<>();

    public void addUser(String user) {
        userList.add(user);
    }

    public boolean removeUser(String user) {
        return userList.remove(user);
    }

    public void printUserList(String message) {
        System.out.println(message + ": " + userList);
    }
}
```

我们创建一个集成测试来加载和测试整个应用程序：

```java
@TestMethodOrder(OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringDataRestApplication.class)
class DirtiesContextIntegrationTest {

    @Autowired
    protected UserCache userCache;

    // ...
}
```

第一个测试方法addJaneDoeAndPrintCache()向缓存添加一个数据：

```java
@Test
@Order(1)
void addJaneDoeAndPrintCache() {
    userCache.addUser("Jane Doe");
    userCache.printUserList("addJaneDoeAndPrintCache");
}
```

将用户添加到缓存后，它会打印缓存的内容：

```shell
addJaneDoeAndPrintCache: [Jane Doe]
```

接下来，测试方法printCache()再次打印userCache：

```java
@Test
@Order(2)
void printCache() {
    userCache.printUserList("printCache");
}
```

它包含在上一个测试方法中添加的数据：

```shell
printCache: [Jane Doe]
```

假设第二个测试方法依赖于空缓存来进行某些断言，那么第一个测试方法中插入的数据可能会导致不期望的行为。

## 4. 使用@DirtiesContext注解

现在，我们介绍@DirtiesContext默认的[MethodMode.AFTER_METHOD](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/annotation/DirtiesContext.MethodMode.html#AFTER_METHOD)。这意味着Spring将在相应的测试方法完成后将上下文标记为关闭。

为了隔离对测试的更改，我们添加了@DirtiesContext。让我们看看它是如何工作的。

addJohnDoeAndPrintCache()测试方法将“John Doe”添加到缓存中。我们还添加了@DirtiesContext注解，它表示上下文应该在测试方法结束时关闭：

```java
@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
@Test
@Order(3)
void addJohnDoeAndPrintCache() {
    userCache.addUser("John Doe");
    userCache.printUserList("addJohnDoeAndPrintCache");
}
```

现在的输出是：

```shell
addJohnDoeAndPrintCache: [John Doe, Jane Doe]
```

最后， printCacheAgain()再次打印缓存：

```java
@Test
@Order(4)
void printCacheAgain() {
    userCache.printUserList("printCacheAgain");
}
```

在运行完整的测试类时，我们可以看到**Spring上下文在addJohnDoeAndPrintCache和printCacheAgain这两个测试方法之间重新加载**。因此缓存重新初始化，输出为空：

```shell
printCacheAgain: []
```

## 5. 其他支持的测试阶段

上面的示例显示了**当前测试方法之后**的阶段，让我们快速总结一下这些阶段：

### 5.1 类级别

**测试类的ClassMode属性定义何时重置上下文**：

+ BEFORE_CLASS：在当前测试类之前
+ BEFORE_EACH_TEST_METHOD：在当前测试类中的每个测试方法之前
+ AFTER_EACH_TEST_METHOD：在当前测试类中的每个测试方法之后
+ AFTER_CLASS：在当前测试类之后

### 5.2 方法级别

**单个方法的MethodMode属性定义何时重置上下文**：

+ BEFORE_METHOD：在当前测试方法之前
+ AFTER_METHOD：在当前测试方法之后

## 6. 总结

在本文中，我们介绍了@DirtiesContext测试注解的使用。