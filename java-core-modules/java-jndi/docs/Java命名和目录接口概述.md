## 1. 概述

Java 命名和目录接口 (JNDI)将命名和/或目录服务作为JavaAPI 提供一致的使用。该接口可用于绑定对象、查找或查询对象，以及检测同一对象的变化。

虽然 JNDI 的使用包括各种[受支持的命名和目录服务](https://docs.oracle.com/javase/8/docs/technotes/guides/jndi/index.html)列表，但在本教程中，我们将在探索 JNDI 的 API 时重点介绍 JDBC。

## 2.JNDI说明

使用 JNDI 的任何工作都需要了解底层服务以及可访问的实现。例如，数据库连接服务调用特定的属性和异常处理。

然而，JNDI 的抽象将连接配置与应用程序分离。

让我们探索Name和Context，它们包含 JNDI 的核心功能。

### 2.1. 名称接口

```java
Name objectName = new CompositeName("java:comp/env/jdbc");
```

Name接口提供了管理组件名称和 JNDI 名称语法的能力。字符串的第一个标记代表全局上下文，之后添加的每个字符串代表下一个子上下文：

```java
Enumeration<String> elements = objectName.getAll();
while(elements.hasMoreElements()) {
  System.out.println(elements.nextElement());
}
```

我们的输出看起来像：

```plaintext
java:comp
env
jdbc
```

正如我们所见，/是Name 子上下文的分隔符。现在，让我们添加一个子上下文：

```java
objectName.add("example");
```

然后我们测试我们的添加：

```java
assertEquals("example", objectName.get(objectName.size() - 1));
```

### 2.2. 上下文接口

上下文包含命名和目录服务的属性。在这里，让我们使用 Spring 中的一些辅助代码来方便地构建Context：

```java
SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder(); 
builder.activate();
```

Spring 的SimpleNamingContextBuilder创建一个 JNDI 提供者，然后使用[NamingManager](https://docs.oracle.com/en/java/javase/11/docs/api/java.naming/javax/naming/spi/NamingManager.html)激活构建器：

```java
JndiTemplate jndiTemplate = new JndiTemplate();
ctx = (InitialContext) jndiTemplate.getContext();
```

最后，JndiTemplate 帮助我们访问InitialContext。

## 3. JNDI 对象绑定和查找

现在我们已经了解了如何使用Name和Context，让我们使用 JNDI 来存储 JDBC DataSource：

```java
ds = new DriverManagerDataSource("jdbc:h2:mem:mydb");
```

### 3.1. 绑定 JNDI 对象

因为我们有上下文，所以让我们将对象绑定到它：

```java
ctx.bind("java:comp/env/jdbc/datasource", ds);
```

通常，服务应该在目录上下文中存储对象引用、序列化数据或属性。这完全取决于应用程序的需求。

请注意，以这种方式使用 JNDI 并不常见。通常，JNDI 与在应用程序运行时外部管理的数据交互。

但是，如果应用程序已经可以创建或找到它的DataSource，那么使用 Spring 连接它可能会更容易。相反，如果应用程序之外的某些东西绑定了 JNDI 中的对象，那么应用程序可以使用它们。

### 3.2. 查找 JNDI 对象

让我们看看我们的数据源：

```java
DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/datasource");
```

然后让我们测试以确保DataSource符合预期：

```java
assertNotNull(ds.getConnection());
```

### 4. 常见的 JNDI 异常

使用 JNDI 有时可能会导致运行时异常。这里有一些常见的。

### 4.1. NameNotFoundException 异常

```java
ctx.lookup("badJndiName");
```

由于此名称未在此上下文中绑定，因此我们看到此堆栈跟踪：

```java
javax.naming.NameNotFoundException: Name [badJndiName] not bound; 0 bindings: []
  at org.springframework.mock.jndi.SimpleNamingContext.lookup(SimpleNamingContext.java:140)
  at java.naming/javax.naming.InitialContext.lookup(InitialContext.java:409)
```

我们应该注意到堆栈跟踪包含所有绑定的对象，这对于跟踪异常发生的原因很有用。

### 4.2. NoInitialContextException异常

与InitialContext 的任何交互都可能抛出NoInitialContextException：

```java
assertThrows(NoInitialContextException.class, () -> {
  JndiTemplate jndiTemplate = new JndiTemplate();
  InitialContext ctx = (InitialContext) jndiTemplate.getContext();
  ctx.lookup("java:comp/env/jdbc/datasource");
}).printStackTrace();
```

我们应该注意到 JNDI 的这种使用是有效的，因为我们之前使用过它。但是，这次没有 JNDI context provider，会抛出一个异常：

```java
javax.naming.NoInitialContextException: Need to specify class name in environment or system property, 
  or in an application resource file: java.naming.factory.initial
    at java.naming/javax.naming.spi.NamingManager.getInitialContext(NamingManager.java:685)
```

## 5. JNDI 在现代应用程序架构中的作用

虽然JNDI 在轻量级、容器化的Java应用程序(如 Spring Boot)中发挥的作用较小，但还有其他用途。仍然使用 JNDI 的三种Java技术是[JDBC](https://www.baeldung.com/java-jdbc)、[EJB](https://www.baeldung.com/ejb-intro)和[JMS](https://www.baeldung.com/spring-jms)。它们在Java企业应用程序中都有广泛的用途。

例如，一个单独的 DevOps 团队可以管理环境变量，例如所有环境中敏感数据库连接的用户名和密码。可以在 Web 应用程序容器中创建 JNDI 资源，将 JNDI 用作适用于所有环境的一致抽象层。

此设置允许开发人员创建和控制用于开发目的的本地定义，同时通过相同的 JNDI 名称连接到生产环境中的敏感资源。

## 六，总结

在本教程中，我们看到了使用Java命名和目录接口连接、绑定和查找对象。我们还研究了 JNDI 抛出的常见异常。

最后，我们了解了 JNDI 如何适应现代应用程序架构。