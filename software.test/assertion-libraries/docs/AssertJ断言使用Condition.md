## 1. 概述

在本教程中，我们介绍[AssertJ库](https://joel-costigliola.github.io/assertj/)如果定义和使用Contional来创建可读和可维护的测试。可以在[这里]()找到AssertJ的基础介绍。

## 2. 被测类

下面是我们将针对其编写测试用例的目标类：

```java
public class Member {
    private String name;
    private int age;

    // constructors and getters
}
```

## 3. 创造Condition

我们可以通过简单地使用适当的参数实例化Condition类来定义断言条件，**创建Condition最方便的方法是使用以Predicate作为参数的构造函数**。其他构造函数要求我们创建一个子类并重写matches方法，这不是很方便。

在构造Condition对象时，我们必须指定一个类型参数，它是用于评估条件的值的类型。

下面为Member类的age字段声明一个条件：

```java
Condition<Member> senior = new Condition<>(
    m -> m.getAge() >= 60, "senior");
```

senior变量现在引用了一个Condition实例，该实例根据Person的age测试是否是Senior。

构造函数的第二个字符串参数“senior”是一个简短的描述，如果条件失败，AssertJ将使用它来构建用户友好的错误消息。

下面是另一个条件，检查一个人的名字是否为“John”：

```java
Condition<Member> nameJohn = new Condition<>(
  m -> m.getName().equalsIgnoreCase("John"), 
  "name John"
);
```

## 4. 测试用例

### 4.1 断言标量值

当年龄值高于资历阈值时，以下测试应通过：

```java
Member member = new Member("John", 65);
assertThat(member).is(senior);
```

由于使用is方法的断言通过，因此使用isNot和相同参数的断言将失败：

```java
// assertion fails with an error message containing "not to be <senior>"
assertThat(member).isNot(senior);
```

使用nameJohn变量，我们可以编写两个类似的测试：

```java
Member member = new Member("Jane", 60);
assertThat(member).doesNotHave(nameJohn);

// assertion fails with an error message containing "to have:n <name John>"
assertThat(member).has(nameJohn);
```

**is和has方法，以及isNot和doesNotHave方法具有相同的语义**，我们使用哪个只是一个选择问题。尽管如此，还是建议选择能使我们的测试代码更具可读性的那种方法。

### 4.2 断言集合

条件不仅适用于标量值，还可以验证集合中元素的存在或不存在：

```java
List<Member> members = new ArrayList<>();
members.add(new Member("Alice", 50));
members.add(new Member("Bob", 60));

assertThat(members).haveExactly(1, senior);
assertThat(members).doNotHave(nameJohn);
```

haveExactly方法断言满足给定Condition的元素的确切数量，而doNotHave方法检查不包含。

方法haveExactly和doNotHave并不是唯一处理收集条件的方法，有关这些方法的完整列表，请参阅API文档中[的 AbstractIterableAssert类](https://joel-costigliola.github.io/assertj/core-8/api/org/assertj/core/api/AbstractIterableAssert.html)。

### 4.3 组合条件

我们可以使用Assertions类的三个静态方法来组合各种条件：

-   **not**：如果不满足指定条件，则创建满足条件
-   **allOf**：创建仅当满足所有指定条件时才满足的条件
-   **anyOf**：创建满足至少一个指定条件的条件

下面的例子演示如何使用not和allOf方法来组合条件：

```java
Member john = new Member("John", 60);
Member jane = new Member("Jane", 50);
        
assertThat(john).is(allOf(senior, nameJohn));
assertThat(jane).is(allOf(not(nameJohn), not(senior)));
```

同样，我们可以使用anyOf：

```java
Member john = new Member("John", 50);
Member jane = new Member("Jane", 60);
        
assertThat(john).is(anyOf(senior, nameJohn));
assertThat(jane).is(anyOf(nameJohn, senior));
```

## 5. 总结

本教程介绍AssertJ中的条件，以及如何使用它们在测试代码中创建非常易读的断言。
