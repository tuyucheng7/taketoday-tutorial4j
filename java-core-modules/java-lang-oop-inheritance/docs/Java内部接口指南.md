## 1. 概述

在这个简短的教程中，我们将研究Java中的内部接口。它们主要用于：

-   解决接口具有通用名称时的命名空间问题
-   增加封装
-   通过将相关接口分组在一个地方来提高可读性

一个众所周知的例子是在Map接口内部声明的Entry接口。以这种方式定义，该接口不在全局范围内，并且它被引用为Map.Entry以区别于其他Entry接口并使其与Map的关系显而易见。

## 2. 内部接口

根据定义，内部接口的声明发生在另一个接口或类的主体中。

当在另一个接口中声明时(类似于顶级接口中的字段声明)，它们是隐式公共和静态的以及它们的字段，并且它们可以在任何地方实现：

```java
public interface Customer {
    // ...
    interface List {
        // ...
    }
}
```

**在另一个类中声明的内部接口也是静态的**，但它们可以有访问修饰符，可以限制它们的实现位置：

```java
public class Customer {
    public interface List {
        void add(Customer customer);

        String getCustomerNames();
    }
    // ...
}
```

在上面的示例中，我们有一个List接口，它将用于声明对客户列表的一些操作，例如添加新客户、获取String表示形式等。

List是一个流行的名称，为了与定义此接口的其他库一起工作，我们需要将我们的声明分开，即命名空间。

如果我们不想使用像CustomerList这样的新名称，这就是我们使用内部接口的地方。

我们还将两个相关的接口放在一起，以提高封装性。

最后，我们可以继续实现它：

```java
public class CommaSeparatedCustomers implements Customer.List {
    // ...
}
```

## 3. 总结

我们快速浏览了Java中的内部接口。