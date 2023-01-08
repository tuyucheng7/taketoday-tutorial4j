## 1. 概述

在我们的域对象中使用get和set方法是很典型的，但我们可能会发现其他方式更具表现力。

在本教程中，我们将了解[Project Lombok](https://www.baeldung.com/intro-to-project-lombok)的 [@Accessors注解](https://projectlombok.org/features/experimental/Accessors)及其对流畅、链接和自定义访问器的支持。

不过，在继续之前，我们的[IDE 需要安装 Lombok](https://www.baeldung.com/lombok-ide)。

## 2. 标准配件

在查看@Accessors注解之前，让我们先回顾一下Lombok默认情况下如何处理@Getter和@Setter注解。

首先，让我们创建我们的类：

```java
@Getter
@Setter
public class StandardAccount {
    private String name;
    private BigDecimal balance;
}
```

现在让我们创建一个测试用例。在我们的测试中可以看到，Lombok 添加了典型的 getter 和 setter 方法：

```java
@Test
public void givenStandardAccount_thenUseStandardAccessors() {
    StandardAccount account = new StandardAccount();
    account.setName("Basic Accessors");
    account.setBalance(BigDecimal.TEN);

    assertEquals("Basic Accessors", account.getName());
    assertEquals(BigDecimal.TEN, account.getBalance()); 
}
```

当我们查看@Accessor的选项时，我们将看到这个测试用例如何变化。

## 3. 流畅的访问器

让我们从 流利的选项开始：

```java
@Accessors(fluent = true)
```

fluent选项为我们提供了没有get或set前缀的访问器。

我们稍后会看一下chain选项，但由于它默认启用，现在让我们明确禁用它：

```java
@Accessors(fluent = true, chain = false)
@Getter
@Setter
public class FluentAccount {
    private String name;
    private BigDecimal balance;
}
```

现在，我们的测试仍然表现相同，但我们改变了访问和改变状态的方式：

```java
@Test
public void givenFluentAccount_thenUseFluentAccessors() {
    FluentAccount account = new FluentAccount();
    account.name("Fluent Account");
    account.balance(BigDecimal.TEN);

    assertEquals("Fluent Account", account.name()); 
    assertEquals(BigDecimal.TEN, account.balance());
}
```

注意 get 和 set前缀是如何消失的。

## 4.链式访问器

现在让我们看一下 链选项：

```java
@Accessors(chain = true)
```

chain选项为我们提供了返回this的setter 。 再次注意它默认为 true，但为了清楚起见，我们将明确设置它。

这意味着我们可以在一个语句中将多个 集合操作链接在一起。

让我们在流畅的访问器上构建并将链选项更改为true：

```java
@Accessors(fluent = true, chain = true)
@Getter 
@Setter 
public class ChainedFluentAccount { 
    private String name; 
    private BigDecimal balance;
}

```

如果我们省略chain并仅指定，我们会得到相同的效果：

```java
@Accessors(fluent = true)
```

现在让我们看看这如何影响我们的测试用例：

```java
@Test
public void givenChainedFluentAccount_thenUseChainedFluentAccessors() {
    ChainedFluentAccount account = new ChainedFluentAccount()
      .name("Fluent Account")
      .balance(BigDecimal.TEN);

    assertEquals("Fluent Account", account.name()); 
    assertEquals(BigDecimal.TEN, account.balance());
}
```

注意新语句如何随着setter链接在一起而变得更长，从而删除了一些样板。

当然，这就是[Lombok 的@Builder](https://www.baeldung.com/intro-to-project-lombok#builder) 使用chain ed fluent访问器的方式。

## 5.前缀访问器

最后，有时我们的字段可能具有与我们希望通过 getter 和 setter 公开的不同的命名约定。

让我们考虑以下为其字段使用匈牙利表示法的类：

```java
public class PrefixedAccount { 
    private String sName; 
    private BigDecimal bdBalance; 
}
```

如果我们用@Getter和@Setter 公开它，我们会得到像 getSName 这样的方法，它的可读性不太好。

prefix选项允许我们告诉 Lombok 忽略哪些前缀：

```java
@Accessors(prefix = {"s", "bd"})
@Getter
@Setter
public class PrefixedAccount {
    private String sName;
    private BigDecimal bdBalance;
}
```

那么，让我们看看这如何影响我们的测试用例：

```java
@Test
public void givenPrefixedAccount_thenRemovePrefixFromAccessors() {
    PrefixedAccount account = new PrefixedAccount();
    account.setName("Prefixed Fields");
    account.setBalance(BigDecimal.TEN);

    assertEquals("Prefixed Fields", account.getName()); 
    assertEquals(BigDecimal.TEN, account.getBalance());
}
```

请注意我们的sName字段 ( setName, getName ) 的访问器如何省略前导s以及bdBalance的访问器如何省略前导的bd。

但是，Lombok仅在前缀后跟小写字母以外的内容时才应用前缀。

这确保如果我们有一个不使用匈牙利表示法的字段，例如state，但以我们的前缀之一s开头，我们不会以getTate() 结束！

最后，假设我们想在我们的符号中使用下划线，但也想在它后面跟一个小写字母。

让我们添加一个前缀为s_的字段s_notes ：

```java
@Accessors(prefix = "s_")
private String s_notes;
```

遵循小写字母规则，我们将获得类似getS_Notes()的方法，因此当前缀本身以非字母结尾的内容时，Lombok 也会应用前缀。

## 6. 配置属性

我们可以通过将配置属性添加到lombok.config文件来为我们最喜欢的设置组合设置项目或目录范围的默认值：

```java
lombok.accessors.chain=true
lombok.accessors.fluent=true
```

有关详细信息，请参阅 Lombok[功能配置指南](https://projectlombok.org/features/configuration)。

## 七. 总结

在本文中，我们以各种组合方式使用了 Lombok 的@Accessors注解的fluent、chain和prefix选项，以查看它如何影响生成的代码。

要了解更多信息，请务必查看[Lombok 访问器 JavaDoc](https://projectlombok.org/api/lombok/experimental/Accessors.html)和[实验性功能指南](https://projectlombok.org/features/experimental/Accessors)。