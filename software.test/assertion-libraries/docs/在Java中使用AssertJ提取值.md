## 一、概述

[AssertJ](https://assertj.github.io/doc/)是 Java 的断言库，它使我们能够流畅地编写断言，并使其更具可读性。

**在本教程中，我们将探索 AssertJ 的提取方法，以便在不中断测试断言流程的情况下流畅地进行检查。**

## 2.实施

让我们从一个*Person*示例类开始：

```java
class Person {
    private String firstName;
    private String lastName;
    private Address address;

    Person(String firstName, String lastName, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    // getters and setter omitted
}复制
```

每个*人都将与一些**地址*相关联：

```java
class Address {
    private String street;
    private String city;
    private ZipCode zipCode;

    Address(String street, String city, ZipCode zipCode) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
    }

    // getters and setter omitted
}复制
```

每个*地址*都将包含一个*邮政编码*作为一个类：

```java
class ZipCode {
    private long zipcode;

    ZipCode(long zipcode) {
        this.zipcode = zipcode;
    }

    // getters and setter omitted
}复制
```

现在假设在创建一个*Person*对象后，我们需要测试以下情况：

-   地址不*为**空*
-   该*地址*不在受限地址列表中
-   *ZipCode*对象 不为*空*
-   ZipCode值介于 1000 和 100000*之间*

## 3.使用AssertJ的常见断言

给定以下*Person*对象：

```java
Person person = new Person("aName", "aLastName", new Address("aStreet", "aCity", new ZipCode(90210)));复制
```

我们可以提取*地址*对象：

```java
Address address = person.getAddress();复制
```

然后我们可以断言*Adress*不为空：

```java
assertThat(address).isNotNull();复制
```

我们还可以检查该*地址*是否不在受限地址列表中：

```java
assertThat(address).isNotIn(RESTRICTED_ADDRESSES);复制
```

下一步是检查*邮政编码*：

```java
ZipCode zipCode = address.getZipCode();复制
```

并断言它不为空：

```java
assertThat(zipCode).isNotNull();复制
```

最后，我们可以提取*ZipCode*值并断言它在 1000 到 100000 之间：

```java
assertThat(zipCode.getZipcode()).isBetween(1000L, 100_000L);复制
```

上面的代码很简单，但我们需要帮助才能流畅地阅读它，因为它需要多行处理。我们还需要分配变量以便稍后能够对它们进行断言，这不是[一种干净的代码](https://www.baeldung.com/cs/clean-code-formatting)体验。

## 4.使用AssertJ的提取方法

现在让我们看看提取方法如何帮助我们：

```java
assertThat(person)
  .extracting(Person::getAddress)
    .isNotNull()
    .isNotIn(RESTRICTED_ADDRESSES)
  .extracting(Address::getZipCode)
    .isNotNull()
  .extracting(ZipCode::getZipcode, as(InstanceOfAssertFactories.LONG))
    .isBetween(1_000L, 100_000L);复制
```

正如我们所看到的，代码并没有太大的不同，但它很流畅，也更容易阅读。

## 5.结论

在这篇文章中，我们能够看到两种提取对象值以断言的方法：

-   提取到稍后断言的变量中
-   使用AssertJ的提取方法以流畅的方式提取