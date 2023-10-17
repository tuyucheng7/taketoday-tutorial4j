## 一、概述

枚举是 Java 5 中引入的一种特殊类型的类[*，*](https://www.baeldung.com/a-guide-to-java-enums)用于帮助替换*int 枚举*模式。***虽然在技术上是合适的，但我们用于枚举\*****常量的名称通常不是我们希望在日志、数据库或应用程序面向客户的部分中显示的名称。**在本教程中，我们将了解在 Java 中对*enum s 实现**toString()*
的各种方法，以便我们可以提供替代名称或修饰名称。

## 2. 默认行为

所有*enum*都隐式扩展了*Enum*类，因此我们的*枚举*将从以下位置继承其默认的*toString()*行为：

```java
public String toString() {
    return name;
}复制
```

## 3.在*枚举*类型上覆盖*toString()*

由于我们无权访问*Enum类，因此我们可以控制**toString()*行为的下一个最高位置是在*枚举*类型声明中：

```java
enum FastFood {
    PIZZA,
    BURGER,
    TACO,
    CHICKEN,
    ;

    @Override
    public String toString() {
        // ...?
    }
}复制
```

*我们可以在toString()*方法内部访问的唯一变量是*this.name*和*this.ordinal*。我们已经知道默认行为是打印*枚举*常量名称，我们对打印序号不感兴趣。然而，我们可以将序数一对一映射到我们的装饰字符串：

```java
@Override
public String toString() {
    switch (this.ordinal()) {
        case 0:
            return "Pizza Pie";
        case 1:
            return "Cheese Burger";
        case 2:
            return "Crunchy Taco";
        case 3:
            return "Fried Chicken";
        default:
            return null;
    }
}复制
```

*虽然有效，但说0 = Pizza Pie*并不是很有表现力。然而，因为*这*本身就是一个*枚举*，我们可以简化上面的*switch*语句，使其更直接：

```java
@Override
public String toString() {
    switch (this) {
        case PIZZA:
            return "Pizza Pie";
        case BURGER:
            return "Cheese Burger";
        case TACO:
            return "Crunchy Taco";
        case CHICKEN:
            return "Fried Chicken";
        default:
            return null;
    }
}复制
```

## 4.在*枚举*常量上覆盖*toString()*

*我们可以覆盖枚举*类中定义的任何方法或每个单独枚举*常量的**枚举*类型声明。因为我们知道*toString()*是在*Enum*类中定义的，所以我们可以跳过在我们的*枚举*类型声明中实现它：

```java
enum FastFood {
    PIZZA {
        @Override
        public String toString() {
            return "Pizza Pie";
        }
    },
    BURGER {
        @Override
        public String toString() {
            return "Cheese Burger";
        }
    },
    TACO {
        @Override
        public String toString() {
            return "Crunchy Taco";
        }
    },
    CHICKEN {
        @Override
        public String toString() {
            return "Fried Chicken";
        }
    }
}复制
```

## 5. 别名字段

我们的另一个选择是在我们的*枚举*类型声明中包含[额外的字段](https://www.baeldung.com/java-enum-values)，并在创建每个*枚举*常量时分配将传递给构造函数的值：

```java
enum FastFood {
    PIZZA("Pizza Pie"),
    BURGER("Cheese Burger"),
    TACO("Crunchy Taco"),
    CHICKEN("Fried Chicken"),
    ;

    private final String prettyName;

    FastFood(String prettyName) {
        this.prettyName = prettyName;
    }
}复制
```

*然后，我们可以简单地在枚举类型声明的**toString()*方法中使用这些值：

```java
@Override
public String toString() {
    return prettyName;
}复制
```

## 6.从装饰*字符串*创建*枚举*

如果我们要完成所有这些工作以将我们的*enum* s 转换为装饰过的*String* s，那么以一种相反的方式构建相同的事情是有意义的。与其覆盖*Enum类的**valueOf()*方法（它将提供的*String*与*枚举*常量名称进行比较），不如创建我们自己的方法，以便我们可以在需要时使用任一行为：

```java
FastFood fromString(String prettyName) {
    for (FastFood f : values()) {
        if (f.prettyName.equals(prettyName)) {
            return f;
        }
    }
    return null;
}复制
```

## 七、结论

在本文中，我们学习了几种为*enum实现**toString()*的方法。[与往常一样，可以在 GitHub 上](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-lang-oop-types-2)找到本文中使用的完整源代码。