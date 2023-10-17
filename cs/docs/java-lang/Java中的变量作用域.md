## 1. 概述

在Java中，与在任何编程语言中一样，每个变量都有一个作用域。这是可以使用变量并且变量有效的程序段。

在本教程中，我们将介绍Java中的可用作用域并讨论它们之间的区别。

## 2. 类作用域

在类的括号({})内使用私有访问修饰符但在任何方法之外声明的每个变量都具有类作用域。因此，**这些变量可以在类中的任何地方使用，但不能在类外部使用**：

```java
public class ClassScopeExample {
    private Integer amount = 0;

    public void exampleMethod() {
        amount++;
    }

    public void anotherExampleMethod() {
        Integer anotherAmount = amount + 4;
    }
}
```

我们可以看到ClassScopeExample有一个可以在类的方法中访问的类变量amount。

如果我们不使用private，它将可以从整个包中访问。查看[访问修饰符](https://www.baeldung.com/java-access-modifiers)一文以获取更多信息。

## 3. 方法作用域

当在方法内部声明变量时，它具有方法作用域，并且**仅在同一方法内部有效**：

```java
public class MethodScopeExample {
    public void methodA() {
        Integer area = 2;
    }

    public void methodB() {
        // compiler error, area cannot be resolved to a variable
        area = area + 2;
    }
}
```

在methodA中，我们创建了一个名为area的方法变量。出于这个原因，我们可以在methodA中使用area，但我们不能在其他任何地方使用它。

## 4. 循环作用域

如果我们在循环内声明一个变量，它将有一个循环作用域并且**只能在循环内使用**：

```java
public class LoopScopeExample {
    List<String> listOfNames = Arrays.asList("Joe", "Susan", "Pattrick");

    public void iterationOfNames() {
        String allNames = "";
        for (String name : listOfNames) {
            allNames = allNames + " " + name;
        }
        // compiler error, name cannot be resolved to a variable
        String lastNameUsed = name;
    }
}
```

我们可以看到方法iterationOfNames有一个名为name的方法变量。该变量只能在循环内使用，在循环外无效。

## 5. 括号作用域

**我们可以使用括号({})在任何地方定义额外的作用域**：

```java
public class BracketScopeExample {
    public void mathOperationExample() {
        Integer sum = 0;
        {
            Integer number = 2;
            sum = sum + number;
        }
        // compiler error, number cannot be solved as a variable
        number++;
    }
}
```

变量number只在括号内有效。

## 6. 作用域和变量阴影

想象一下，我们有一个类变量，我们想声明一个同名的方法变量：

```java
public class NestedScopesExample {
    String title = "Tuyucheng";

    public void printTitle() {
        System.out.println(title);
        String title = "John Doe";
        System.out.println(title);
    }
}
```

我们第一次打印title时，它会打印“Tuyucheng”。之后，声明一个同名的方法变量并为其赋值“John Doe”。

title方法变量覆盖了再次访问类变量title的可能性。这就是为什么第二次，它会打印“John Doe”。

**令人困惑，对吧？这称为变量阴影**，不是一个好的做法。最好使用前缀this来访问title类变量，例如this.title。

## 7. 总结

我们了解了Java中存在的不同作用域。