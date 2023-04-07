## 一、概述

[在本文中，我们将探讨Java 泛型](https://www.baeldung.com/java-generics)在生成和使用集合时的用法。

我们还将讨论*extends*和*super*关键字，我们将查看 PECS（Producer Extends Consumer Supers）规则的几个示例以确定如何正确使用这些关键字。

## 2.生产者延伸

对于本文中的代码示例，我们将使用一个简单的数据模型，其中包含一个*User*基类和两个扩展它的类：*Operator*和*Customer*。

**了解必须从馆藏的角度应用 PECS 规则很重要。**换句话说，如果我们遍历一个*List*并处理它的元素，这个列表将充当我们逻辑的生产者：

```java
public void sendEmails(List<User> users) {
    for (User user : users) {
        System.out.println("sending email to " + user);
    }
}复制
```

*现在，*假设我们要对Operator*列表使用**sendEmail*方法。Operator类扩展了*User ，因此我们可能希望它**是*一个简单、直接的方法调用。但是，不幸的是，我们会得到一个编译错误：

[![制作人](https://www.baeldung.com/wp-content/uploads/2022/12/producer_extends_error.png)](https://www.baeldung.com/wp-content/uploads/2022/12/producer_extends_error.png)

为了解决这个问题，我们可以按照 PECS 规则更新*sendEmail方法。*因为用户列表是我们逻辑的*生产者，所以我们将使用**extends*关键字：

```java
public void sendEmailsFixed(List<? extends User> users) {
    for (User user : users) {
        System.out.println("sending email to " + user);
    }
}复制
```

因此，我们现在可以轻松调用任何泛型列表的方法，只要它们继承自*User*类：

```java
List<Operator> operators = Arrays.asList(new Operator("sam"), new Operator("daniel"));
List<Customer> customers = Arrays.asList(new Customer("john"), new Customer("arys"));

sendEmailsFixed(operators);
sendEmailsFixed(customers);
复制
```

## 3.消费者超级

当我们向集合添加元素时，我们成为生产者，而列表将充当消费者。*让我们编写一个方法来接收Operator*列表并向其添加另外两个元素：

```java
private void addUsersFromMarketingDepartment(List<Operator> users) {
    users.add(new Operator("john doe"));
    users.add(new Operator("jane doe"));
}
复制
```

*如果我们传递一个Operator*列表，这将完美地工作。*但是，如果我们想用它来将两个运算符添加到Users*列表中，我们将再次遇到编译错误：[![消费者超级错误](https://www.baeldung.com/wp-content/uploads/2022/12/consumer_supers_error.png)](https://www.baeldung.com/wp-content/uploads/2022/12/consumer_supers_error.png)

*因此，我们需要更新该方法并使用super关键字使其接受**Operator*或它的前身的集合：

```java
private void addUsersFromMarketingDepartmentFixed(List<? super Operator> users) {
    users.add(new Operator("john doe"));
    users.add(new Operator("jane doe"));
}复制
```

## 4. 生产与消费

可能存在我们的逻辑需要读取和写入集合的情况。在这种情况下，*Collection*将同时是生产者和消费者。

处理这些情况的唯一方法是使用基类，不带任何关键字：

```java
private void addUsersAndSendEmails(List<User> users) {
    users.add(new Operator("john doe"));
    for (User user : users) {
        System.out.println("sending email to: " + user);
    }
}复制
```

**另一方面，使用同一个集合进行读取和写入将违反命令-查询分离原则，应该避免。**

## 5.结论

在本文中，我们讨论了*Produce Extends Consumer Supers*规则，并学习了如何在处理 Java 集合时应用它。

我们探索了集合作为我们逻辑的生产者或消费者的各种用法。在那之后，我们了解到，如果一个集合同时执行这两种操作，这可能表明我们的设计中存在代码味道。