## 1. 概述

当我们想要在Java中一个对象时，我们需要考虑两种可能性，[浅拷贝和深拷贝](https://www.baeldung.com/cs/deep-vs-shallow-copy)。

对于浅拷贝方法，我们只字段值，因此副本可能依赖于原始对象。在深度方法中，我们确保树中的所有对象都被深度，因此副本不依赖于任何可能发生变化的早期现有对象。

在本教程中，我们将比较这两种方法，并学习四种实现深拷贝的方法。

## 延伸阅读：

## [Java 构造函数](https://www.baeldung.com/java-copy-constructor)

以下是如何在Java中创建构造函数以及为什么实施 Cloneable 不是一个好主意。

[阅读更多](https://www.baeldung.com/java-copy-constructor)→

## [如何在Java中数组](https://www.baeldung.com/java-array-copy)

通过各种方法的示例了解如何在Java中数组。

[阅读更多](https://www.baeldung.com/java-array-copy)→

## [在Java中集合](https://www.baeldung.com/java-copy-sets)

了解如何在Java中 Set 的几种不同方式。

[阅读更多](https://www.baeldung.com/java-copy-sets)→

## 2.Maven 设置

我们将使用三个 Maven 依赖项，Gson、Jackson 和 Apache Commons Lang，来测试执行深度的不同方式。

让我们将这些依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.2</version>
</dependency>
<dependency>
    <groupId>commons-lang</groupId>
    <artifactId>commons-lang</artifactId>
    <version>2.6</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.0</version>
</dependency>
```

最新版本的[Gson](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.code.gson" AND a%3A"gson")、[Jackson](https://search.maven.org/classic/#search|gav|1|g%3A"com.fasterxml.jackson.core" AND a%3A"jackson-databind")和[Apache Commons Lang](https://search.maven.org/classic/#search|gav|1|g%3A"commons-lang" AND a%3A"commons-lang")可以在 Maven Central 上找到。

## 3.型号

为了比较Java对象的不同方法，我们需要处理两个类：

```java
class Address {

    private String street;
    private String city;
    private String country;

    // standard constructors, getters and setters
}
class User {

    private String firstName;
    private String lastName;
    private Address address;

    // standard constructors, getters and setters
}
```

## 4.浅拷贝

浅拷贝是指我们只将字段的值从一个对象到另一个对象：

```java
@Test
public void whenShallowCopying_thenObjectsShouldNotBeSame() {

    Address address = new Address("Downing St 10", "London", "England");
    User pm = new User("Prime", "Minister", address);
    
    User shallowCopy = new User(
      pm.getFirstName(), pm.getLastName(), pm.getAddress());

    assertThat(shallowCopy)
      .isNotSameAs(pm);
}
```

在这种情况下，pm != shallowCopy，这意味着它们是不同的对象；然而，问题是当我们更改任何原始地址的属性时，这也会影响shallowCopy的地址。

如果Address是不可变的，我们就不会为它烦恼，但它不是：

```java
@Test
public void whenModifyingOriginalObject_ThenCopyShouldChange() {
 
    Address address = new Address("Downing St 10", "London", "England");
    User pm = new User("Prime", "Minister", address);
    User shallowCopy = new User(
      pm.getFirstName(), pm.getLastName(), pm.getAddress());

    address.setCountry("Great Britain");
    assertThat(shallowCopy.getAddress().getCountry())
      .isEqualTo(pm.getAddress().getCountry());
}
```

## 5.深拷贝

深拷贝是解决这个问题的替代方案。它的优点是对象图中的每个可变对象都被递归。

由于副本不依赖于之前创建的任何可变对象，因此它不会像我们在浅拷贝中看到的那样被意外修改。

在接下来的部分中，我们将讨论几种深度实现并展示此优势。

### 5.1. 构造函数

我们将检查的第一个实现是基于构造函数的：

```java
public Address(Address that) {
    this(that.getStreet(), that.getCity(), that.getCountry());
}
public User(User that) {
    this(that.getFirstName(), that.getLastName(), new Address(that.getAddress()));
}
```

在上面的深拷贝实现中，我们没有在拷贝构造函数中创建新的String ，因为String是一个不可变类。

因此，它们不会被意外修改。让我们看看这是否有效：

```java
@Test
public void whenModifyingOriginalObject_thenCopyShouldNotChange() {
    Address address = new Address("Downing St 10", "London", "England");
    User pm = new User("Prime", "Minister", address);
    User deepCopy = new User(pm);

    address.setCountry("Great Britain");
    assertNotEquals(
      pm.getAddress().getCountry(), 
      deepCopy.getAddress().getCountry());
}
```

### 5.2. 可克隆接口

第二种实现基于继承自Object的 clone 方法。它受到保护，但我们需要将其重写为public。

我们还将向类添加一个标记接口Cloneable，以指示这些类实际上是可克隆的。

让我们将clone()方法添加到Address类：

```java
@Override
public Object clone() {
    try {
        return (Address) super.clone();
    } catch (CloneNotSupportedException e) {
        return new Address(this.street, this.getCity(), this.getCountry());
    }
}
```

现在让我们为User类实现clone() ：

```java
@Override
public Object clone() {
    User user = null;
    try {
        user = (User) super.clone();
    } catch (CloneNotSupportedException e) {
        user = new User(
          this.getFirstName(), this.getLastName(), this.getAddress());
    }
    user.address = (Address) this.address.clone();
    return user;
}
```

请注意，super.clone()调用返回对象的浅拷贝，但我们手动设置了可变字段的深拷贝，所以结果是正确的：

```java
@Test
public void whenModifyingOriginalObject_thenCloneCopyShouldNotChange() {
    Address address = new Address("Downing St 10", "London", "England");
    User pm = new User("Prime", "Minister", address);
    User deepCopy = (User) pm.clone();

    address.setCountry("Great Britain");

    assertThat(deepCopy.getAddress().getCountry())
      .isNotEqualTo(pm.getAddress().getCountry());
}
```

## 6. 外部图书馆

上面的例子看起来很简单，但有时当我们不能添加额外的构造函数或重写 clone 方法时，它们不能作为解决方案。

当我们不拥有代码时，或者当对象图非常复杂以致于如果我们专注于编写额外的构造函数或在对象图中的所有类上实现克隆方法，我们将无法按时完成项目时，可能会发生这种情况。

那我们能做什么呢？在这种情况下，我们可以使用外部库。要实现深拷贝，我们可以序列化一个对象，然后反序列化为一个新的对象。

让我们看几个例子。

### 6.1. Apache Commons 语言

Apache Commons Lang 有SerializationUtils#clone，当对象图中的所有类都实现了Serializable接口时，它会执行深拷贝。

如果该方法遇到一个不可序列化的类，它将失败并抛出一个未经检查的SerializationException。

因此，我们需要将Serializable接口添加到我们的类中：

```java
@Test
public void whenModifyingOriginalObject_thenCommonsCloneShouldNotChange() {
    Address address = new Address("Downing St 10", "London", "England");
    User pm = new User("Prime", "Minister", address);
    User deepCopy = (User) SerializationUtils.clone(pm);

    address.setCountry("Great Britain");

    assertThat(deepCopy.getAddress().getCountry())
      .isNotEqualTo(pm.getAddress().getCountry());
}
```

### 6.2. 使用 Gson 进行 JSON 序列化

另一种序列化方式是使用 JSON 序列化。Gson 是一个用于将对象转换为 JSON 的库，反之亦然。

与 Apache Commons Lang 不同，GSON 不需要Serializable接口来进行转换。

让我们快速看一个例子：

```java
@Test
public void whenModifyingOriginalObject_thenGsonCloneShouldNotChange() {
    Address address = new Address("Downing St 10", "London", "England");
    User pm = new User("Prime", "Minister", address);
    Gson gson = new Gson();
    User deepCopy = gson.fromJson(gson.toJson(pm), User.class);

    address.setCountry("Great Britain");

    assertThat(deepCopy.getAddress().getCountry())
      .isNotEqualTo(pm.getAddress().getCountry());
}
```

### 6.3. 使用 Jackson 进行 JSON 序列化

Jackson 是另一个支持 JSON 序列化的库。此实现与使用 Gson 的实现非常相似，但我们需要将默认构造函数添加到我们的类中。

让我们看一个例子：

```java
@Test
public void whenModifyingOriginalObject_thenJacksonCopyShouldNotChange() 
  throws IOException {
    Address address = new Address("Downing St 10", "London", "England");
    User pm = new User("Prime", "Minister", address);
    ObjectMapper objectMapper = new ObjectMapper();
    
    User deepCopy = objectMapper
      .readValue(objectMapper.writeValueAsString(pm), User.class);

    address.setCountry("Great Britain");

    assertThat(deepCopy.getAddress().getCountry())
      .isNotEqualTo(pm.getAddress().getCountry());
}
```

## 七、总结

进行深拷贝时我们应该使用哪种实现？最终决定通常取决于我们将的类，以及我们是否拥有对象图中的类。