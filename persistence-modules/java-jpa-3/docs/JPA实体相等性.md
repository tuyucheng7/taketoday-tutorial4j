## 1. 概述

在本教程中，我们将了解如何使用 JPA 实体对象处理相等性。

## 2.注意事项

一般来说，相等只是意味着两个对象相同。[但是，在Java中，我们可以通过覆盖Object.equals()和Object.hashCode()](https://www.baeldung.com/java-equals-hashcode-contracts)方法来更改相等性的定义。最终，Java 允许我们定义平等的含义。但首先，我们需要考虑几件事。

### 2.1. 收藏品

Java 集合将对象组合在一起。分组逻辑使用称为散列码的特殊值来确定对象的组。

如果hashCode()方法返回的值对于所有实体都相同，这可能会导致意外行为。假设我们的实体对象有一个定义为id的主键，但我们将hashCode()方法定义为：

```java
@Override
public int hashCode() {
    return 12345;
}

```

集合在比较它们时将无法区分不同的对象，因为它们将共享相同的哈希码。幸运的是，解决这个问题就像在生成哈希码时使用唯一密钥一样简单。例如，我们可以使用我们的id定义hashCode()方法 ：

```java
@Override
public int hashCode() {
    return id  12345;
}

```

在这种情况下，我们使用实体的ID来定义哈希码。现在，集合可以比较、排序和存储我们的实体。

### 2.2. 瞬态实体

[与持久性上下文](https://www.baeldung.com/jpa-hibernate-persistence-context)没有关联的新创建的 JPA 实体对象被认为处于瞬态状态。这些对象通常没有填充它们的@Id成员。因此，如果equals()或hashCode()在它们的计算中使用id，这意味着所有瞬态对象都将相等，因为它们的id都将为null。在很多情况下，这是可取的。

### 2.3. 子类

在定义平等时，子类也是一个问题。在equals()方法中比较类是很常见的。因此，包含getClass()方法将有助于在比较对象是否相等时过滤掉子类。

让我们定义一个equals()方法，该方法仅在对象属于同一类且具有相同id时才有效：

```java
@Override
public boolean equals(Object o) {
    if (o == null || this.getClass() != o.getClass()) {
        return false;
    }
    return o.id.equals(this.id);
}

```

## 3. 定义平等

鉴于这些考虑，我们在处理相等性时有一些选择。因此，我们采取的方法取决于我们计划如何使用我们的对象的细节。让我们看看我们的选择。

### 3.1. 无覆盖

默认情况下，Java凭借从Object类派生的所有对象提供equals()和hashCode()方法。因此，我们能做的最简单的事情就是什么都不做。不幸的是，这意味着在比较对象时，为了被认为是相等的，它们必须是相同的实例，而不是代表同一对象的两个单独的实例。

### 3.2. 使用数据库键

在大多数情况下，我们处理的是存储在数据库中的 JPA 实体。通常，这些实体有一个唯一值的主键。因此，具有相同主键值的该实体的任何实例都是相等的。因此，我们可以 像上面对子类所做的那样覆盖equals() ，也可以仅使用主键覆盖hashCode() 。

### 3.3. 使用业务密钥

或者，我们可以使用业务键来比较 JPA 实体。在这种情况下，对象的键由除主键之外的实体成员组成。此键应使 JPA 实体唯一。在不需要主键或数据库生成的键的情况下比较实体时，使用业务键可以为我们提供相同的预期结果。

假设我们知道电子邮件地址始终是唯一的，即使它不是@Id字段。我们可以在hashCode()和equals()方法中包含电子邮件字段：

```java
public class EqualByBusinessKey {

    private String email;

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(email);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof EqualByBusinessKey) {
            if (((EqualByBusinessKey) obj).getEmail().equals(getEmail())) {
                return true;
            }
        }

        return false;
    }
}

```

## 4. 总结

在本教程中，我们讨论了在编写 JPA 实体对象时可以处理相等性的各种方法。我们还描述了在选择方法时应考虑的因素。