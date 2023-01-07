## 1. 概述

私有构造函数允许我们限制类的实例化。简而言之，它们阻止在类本身以外的任何地方创建类实例。

公共和私有构造函数一起使用，允许控制我们希望如何实例化我们的类——这被称为构造函数委托。

## 2. 典型用法

限制显式类实例化有多种模式和好处，我们将在本教程中介绍最常见的模式和好处：

-   单[例模式](https://www.baeldung.com/java-singleton)
-   委派构造函数
-   不可实例化类
-   建造者[模式](https://www.baeldung.com/java-builder-pattern-freebuilder)

让我们看看如何定义私有构造函数：

```java
public class PrivateConstructorClass {
    
    private PrivateConstructorClass() {
        // in the private constructor
    }
}
```

我们以类似于公共构造函数的方式定义私有构造函数；我们只是将public 关键字更改为 private。

## 3. 在单例模式中使用私有构造器

单例模式是我们最常遇到的使用私有构造函数的地方之一。私有构造函数允许我们将类实例化限制为单个对象实例：

```java
public final class SingletonClass {
    
    private static SingletonClass INSTANCE;
    private String info = "Initial info class";

    private SingletonClass() {
    }

    public static SingletonClass getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SingletonClass();
        }

        return INSTANCE;
    }

    // getters and setters
}
```

我们可以通过调用SingletonClass.getInstance()创建一个实例——这要么返回一个现有实例，要么创建一个实例(如果这是第一个实例化)。我们只能使用getInstance() 静态方法来实例化这个类。

## 4.使用私有构造函数委托构造函数

私有构造函数的另一个常见用例是提供构造函数委托的方法。构造函数委托允许我们通过几个不同的构造函数传递参数，同时将初始化限制在特定的地方。

在此示例中，ValueTypeClass允许使用值和类型进行初始化——但我们只想允许它用于类型的子集。通用构造函数必须是私有的，以确保只使用允许的类型：

```java
public class ValueTypeClass {
    
    private final String value;
    private final String type;

    public ValueTypeClass(int x) {
        this(Integer.toString(x), "int");
    }

    public ValueTypeClass(boolean x) {
        this(Boolean.toString(x), "boolean");
    }

    private ValueTypeClass(String value, String type) {
        this.value = value;
        this.type = type;
    }

    // getters and setters
}
```

我们可以通过两个不同的公共构造函数初始化 ValueType类 ：一个接受int，另一个接受boolean。然后这些构造函数中的每一个都调用一个公共的私有构造函数来完成对象的初始化。

## 5. 使用私有构造函数创建不可实例化的类

不可实例化类是我们无法实例化的类。在这个例子中，我们将创建一个只包含静态方法集合的类：

```java
public class StringUtils {
    
    private StringUtils() {
        // this class cannot be instantiated
    }

    public static String toUpperCase(String s) {
        return s.toUpperCase();
    }

    public static String toLowerCase(String s) {
        return s.toLowerCase();
    }
}
```

StringUtils类包含几个静态实用程序方法，并且由于私有构造函数而 无法实例化。

实际上，不需要允许对象实例化，因为静态方法不需要使用对象实例。

## 6. 在建造者模式中使用私有构造器

建造者模式允许我们一步一步地构造复杂的对象，而不是让多个构造函数提供不同的方式来创建对象。私有构造函数限制初始化，允许构建器管理对象创建。

在此示例中，我们创建了一个 Employee类，其中包含员工的姓名、年龄和部门：

```java
public class Employee {

    private final String name;
    private final int age;
    private final String department;

    private Employee(String name, int age, String department) {
        this.name = name;
        this.age = age;
        this.department = department;
    }
}
```

如我们所见，我们已将 Employee构造函数设为私有 - 因此，我们无法显式实例化该类。

我们现在将向 Employee类添加一个内部Builder类：

```java
public static class Builder {

    private String name;
    private int age;
    private String department;

    public Builder setName(String name) {
        this.name = name;
        return this;
    }

    public Builder setAge(int age) {
        this.age = age;
        return this;
    }

    public Builder setDepartment(String department) {
        this.department = department;
        return this;
    }

    public Employee build() {
        return new Employee(name, age, department);
    }
}
```

构建器现在可以使用name、age或department创建不同的员工——我们必须提供多少字段没有限制：

```java
Employee.Builder emplBuilder = new Employee.Builder();

Employee employee = emplBuilder
  .setName("baeldung")
  .setDepartment("Builder Pattern")
  .build();
```

我们创建了 一个名为“ baeldung ”的员工和一个“ Builder Pattern ”的部门。未提供年龄，因此将使用默认原始int值 0。

## 7. 使用私有构造函数来防止子类化

私有构造函数的另一个可能用途是防止类的子类化。如果我们试图创建这样的子类，它将无法调用超级构造函数。然而，重要的是要注意，我们通常会将类设置为[final](https://www.baeldung.com/java-final)以防止子类化，而不是使用私有构造函数。

## 八、总结

私有构造函数的主要用途是限制类的实例化。当我们要限制类的外部创建时，私有构造函数特别有用。

单例、工厂和静态方法对象是限制对象实例化如何有助于强制执行特定模式的示例。

常量类和静态方法类也规定一个类不应该是可实例化的。重要的是要记住，我们还可以将私有构造函数与公共构造函数结合起来，以允许在不同的公共构造函数定义中共享代码。