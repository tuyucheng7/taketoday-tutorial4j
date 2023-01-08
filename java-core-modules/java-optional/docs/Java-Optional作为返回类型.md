## 1. 概述

Optional类型是在Java8 中引入的。它提供了一种清晰明确的方式来传达可能没有值的消息，而无需[使用](https://www.baeldung.com/java-optional)null。

当获取Optional返回类型时，我们可能会检查该值是否丢失，从而导致应用程序中的NullPointerException更少。然而，Optional类型并不适用于所有地方。

尽管我们可以在任何我们认为合适的地方使用它，但在本教程中，我们将重点介绍使用Optional作为返回类型的一些最佳实践。

## 2.可选的返回类型

Optional类型可以是大多数方法的返回类型，但本教程稍后讨论的某些场景除外。

大多数时候，返回一个Optional就可以了：

```java
public static Optional<User> findUserByName(String name) {
    User user = usersByName.get(name);
    Optional<User> opt = Optional.ofNullable(user);
    return opt;
}
```

这很方便，因为我们可以在调用方法中使用Optional API：

```java
public static void changeUserName(String oldFirstName, String newFirstName) {
    findUserByFirstName(oldFirstName).ifPresent(user -> user.setFirstName(newFirstName));
}
```

静态方法或实用程序方法返回Optional值也是合适的。但是，在很多情况下我们不应该返回Optional类型。

## 3. 何时不返回可选

因为Optional是一个包装器和[基于值的](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/doc-files/ValueBased.html)类，所以有些操作不能对Optional对象进行。很多时候，返回实际类型比返回Optional类型更好。

一般来说，对于POJO中的getter，返回实际类型更合适，而不是Optional类型。特别是，实体 Bean、数据模型和 DTO 具有传统的 getter 非常重要。

我们将在下面检查一些重要的用例。

### 3.1. 连载

假设我们有一个简单的实体：

```java
public class Sock implements Serializable {
    Integer size;
    Optional<Sock> pair;

    // ... getters and setters
}
```

这实际上根本行不通。如果我们尝试序列化它，我们会得到一个NotSerializableException：

```java
new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(new Sock());

```

实际上，虽然[序列化 Optional可以与其他库一起使用](https://www.baeldung.com/jackson-optional)，但它肯定会增加不必要的复杂性。

让我们看一下同样序列化不匹配的另一个应用程序，这次是使用 JSON。

### 3.2. JSON

现代应用程序始终将Java对象转换为 JSON。如果 getter 返回Optional类型，我们很可能会在最终的 JSON 中看到一些意想不到的数据结构。

假设我们有一个带有可选属性的 bean：

```java
private String firstName;

public Optional<String> getFirstName() {
    return Optional.ofNullable(firstName);
}

public void setFirstName(String firstName) {
    this.firstName = firstName;
}
```

因此，如果我们使用 Jackson 来序列化Optional的一个实例，我们将得到：

```plaintext
{"firstName":{"present":true}}

```

但是，我们真正想要的是：

```plaintext
{"firstName":"Baeldung"}
```

因此， Optional 对于序列化用例来说是一种痛苦。接下来，让我们看看序列化的表亲：将数据写入数据库。

### 3.3. JPA

在 JPA 中，getter、setter 和字段应该具有名称和类型协议。例如，String 类型的 firstName 字段应与名为getFirstName的 getter 配对 ，后者也返回 String。

遵循这个约定可以让很多事情变得更简单，包括像 Hibernate 这样的库使用反射，为我们提供强大的对象-关系映射支持。

让我们看一下POJO 中可选名字的相同用例。

不过这一次，它将是一个 JPA 实体：

```java
@Entity
public class UserOptionalField implements Serializable {
    @Id
    private long userId;

    private Optional<String> firstName;

    // ... getters and setters
}
```

让我们继续努力坚持下去：

```java
UserOptionalField user = new UserOptionalField();
user.setUserId(1l);
user.setFirstName(Optional.of("Baeldung"));
entityManager.persist(user);
```

可悲的是，我们遇到了一个错误：

```plaintext
Caused by: javax.persistence.PersistenceException: [PersistenceUnit: com.baeldung.optionalReturnType] Unable to build Hibernate SessionFactory
	at org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl.persistenceException(EntityManagerFactoryBuilderImpl.java:1015)
	at org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl.build(EntityManagerFactoryBuilderImpl.java:941)
	at org.hibernate.jpa.HibernatePersistenceProvider.createEntityManagerFactory(HibernatePersistenceProvider.java:56)
	at javax.persistence.Persistence.createEntityManagerFactory(Persistence.java:79)
	at javax.persistence.Persistence.createEntityManagerFactory(Persistence.java:54)
	at com.baeldung.optionalReturnType.PersistOptionalTypeExample.<clinit>(PersistOptionalTypeExample.java:11)
Caused by: org.hibernate.MappingException: Could not determine type for: java.util.Optional, at table: UserOptionalField, for columns: [org.hibernate.mapping.Column(firstName)]
```

我们可以尝试偏离这个标准。 例如，我们可以将属性保留为 String，但更改 getter：

```java
@Column(nullable = true) 
private String firstName; 

public Optional<String> getFirstName() { 
    return Optional.ofNullable(firstName); 
}
```

看起来我们可以有两种方法：为 getter提供一个Optional返回类型和一个可持久化的字段firstName。

但是，既然我们与我们的 getter、setter 和字段不一致，那么利用 JPA 默认值和 IDE 源代码工具将更加困难。

在 JPA 优雅地支持Optional类型之前，我们应该坚持使用传统代码。它更简单更好：

```java
private String firstName;

// ... traditional getter and setter
```

最后让我们来看看这对前端有何影响——看看我们遇到的问题是否听起来很熟悉。

### 3.4. 表达语言

为前端准备 DTO 存在类似的困难。

例如，假设我们正在使用 JSP 模板从请求中读取我们的UserOptional DTO 的 名字：

```java
<c:out value="${requestScope.user.firstName}" />

```

因为它是可选的，所以我们不会看到“ Baeldung ”。相反，我们将看到Optional类型的String表示：

```plaintext
Optional[Baeldung]

```

这不仅仅是 JSP 的问题。任何模板语言，无论是 Velocity、Freemarker 还是其他语言，都需要添加对此的支持。在那之前，让我们继续保持我们的 DTO 简单。

## 4。总结

在本教程中，我们学习了如何返回一个Optional对象，以及如何处理这种返回值。

另一方面，我们还了解到，在很多情况下，我们最好不要对getter使用Optional返回类型。虽然我们可以使用Optional类型作为可能没有非空值的提示，但我们应该注意不要过度使用Optional返回类型，尤其是在实体 bean 或 DTO 的 getter 中。