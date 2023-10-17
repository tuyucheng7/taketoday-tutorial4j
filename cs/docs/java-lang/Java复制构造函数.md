## 1. 概述

Java类中的复制构造函数是**使用同一Java类的另一个对象创建对象的[构造函数](https://www.baeldung.com/java-constructors)**。

当我们想要复制具有多个字段的复杂对象时，或者当我们想要对现有对象进行[深度复制](https://www.baeldung.com/java-deep-copy)时，这很有用。

## 2. 如何创建复制构造函数

要创建复制构造函数，我们可以首先声明一个构造函数，该构造函数将相同类型的对象作为参数：

```java
public class Employee {
    private int id;
    private String name;

    public Employee(Employee employee) {
    }
}
```

然后，我们将输入对象的每个字段复制到新实例中：

```java
public class Employee {
    private int id;
    private String name;

    public Employee(Employee employee) {
        this.id = employee.id;
        this.name = employee.name;
    }
}
```

我们这里有一个浅拷贝，这很好，因为我们所有的字段(在本例中是一个int和一个String)要么是[原始类型](https://www.baeldung.com/java-primitives)，要么是[不可变类型](https://www.baeldung.com/java-immutable-object)。

如果Java类具有可变字段，那么我们可以改为在其复制构造函数中进行[深拷贝](https://www.baeldung.com/java-deep-copy)。使用深拷贝，新创建的对象独立于原始对象，因为我们为每个可变对象创建了一个不同的副本：

```java
public class Employee {
    private int id;
    private String name;
    private Date startDate;

    public Employee(Employee employee) {
        this.id = employee.id;
        this.name = employee.name;
        this.startDate = new Date(employee.startDate.getTime());
    }
}
```

## 3. 复制构造函数与克隆

在Java中，我们还可以使用[clone](https://www.baeldung.com/java-deep-copy)方法从现有对象创建对象。但是，复制构造函数与克隆方法相比具有一些优点：

1.  复制构造函数更容易实现。我们不需要实现Cloneable接口和处理CloneNotSupportedException
2.  clone方法返回一个通用的Object引用。因此，我们需要将其类型转换为适当的类型
3.  我们不能在clone方法中为final字段赋值。但是，我们可以在复制构造函数中这样做

## 4. 继承问题

Java中的复制构造函数不能被子类继承。因此，如果我们尝试从父类引用初始化子对象，我们将在使用复制构造函数克隆它时面临强制转换问题。

为了说明这个问题，让我们首先创建一个Employee的子类及其复制构造函数：

```java
public class Manager extends Employee {
    private List<Employee> directReports;
    // ... other constructors

    public Manager(Manager manager) {
        super(manager.id, manager.name, manager.startDate);
        this.directReports = directReports.stream()
              .collect(Collectors.toList());
    }
}
```

然后，我们声明一个Employee变量并使用Manager构造函数实例化它：

```java
Employee source = new Manager(1, "Tuyucheng Manager", startDate, directReports);
```

由于引用类型是Employee，我们必须将其强制转换为Manager类型，以便我们可以使用Manager类的复制构造函数：

```java
Employee clone = new Manager((Manager) source);
```

如果输入对象不是Manager类的实例，我们可能会在运行时得到ClassCastException。

避免在复制构造函数中强制转换的一种方法是为这两个类创建一个新的可继承方法：

```java
public class Employee {
    public Employee copy() {
        return new Employee(this);
    }
}

public class Manager extends Employee {
    @Override
    public Employee copy() {
        return new Manager(this);
    }
}
```

在每个类方法中，我们用这个对象的输入调用它的复制构造函数。这样，我们可以保证生成的对象等于调用者对象：

```java
Employee clone = source.copy();
```

## 5. 总结

在本教程中，我们展示了如何使用一些代码示例创建复制构造函数。此外，我们还讨论了应避免使用克隆方法的几个原因。

当我们使用它来克隆引用类型为父类的子类对象时，复制构造函数存在强制转换问题。我们为这个问题提供了一个解决方案。