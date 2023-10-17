## 1. 概述

在本教程中，我们将了解什么是数据传输对象 (DTO)、值对象 (VO)、普通旧Java对象 (POJO) 和 JavaBeans。我们将查看它们之间的区别，并了解使用哪种类型以及何时使用。

## 2. 普通的旧Java对象

[POJO](https://www.baeldung.com/java-pojo-class)，也称为 Plain OldJavaObject，是一个普通的Java对象，没有对任何特定框架的引用。它是一个用来指代简单、轻量级Java对象的术语。

POJO 不对属性和方法使用任何命名约定。

让我们定义一个具有三个属性的基本EmployeePOJO对象：

```java
public class EmployeePOJO {

    private String firstName;
    private String lastName;
    private LocalDate startDate;

    public EmployeePOJO(String firstName, String lastName, LocalDate startDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.startDate = startDate;
    }

    public String name() {
        return this.firstName + " " + this.lastName;
    }

    public LocalDate getStart() {
        return this.startDate;
    }
}
```

正如我们所看到的，上面的Java对象定义了表示员工的结构，并且不依赖于任何框架。

## 3.JavaBean

### 3.1. 什么是 JavaBean？

[JavaBean](https://www.baeldung.com/java-pojo-class#what-is-a-javabean)很像 POJO，有一些关于如何实现它的严格规则。 

规则指定它应该是可序列化的，具有空构造函数，并允许使用遵循getX()和setX()约定的方法访问变量。

### 3.2. 作为 JavaBean 的 POJO

由于 JavaBean 本质上是一个 POJO，让我们通过实现必要的 bean 规则将EmployeePOJO转换为 JavaBean：

```java
public class EmployeeBean implements Serializable {

    private static final long serialVersionUID = -3760445487636086034L;
    private String firstName;
    private String lastName;
    private LocalDate startDate;

    public EmployeeBean() {
    }

    public EmployeeBean(String firstName, String lastName, LocalDate startDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.startDate = startDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // additional getters and setters
}
```

在这里，为了将 POJO 转换为 JavaBean，我们实现了Serializable接口，将属性标记为私有，并使用 getter/setter 方法访问属性。

## 4.DTO

### 4.1. DTO模式

DTO，也称为[数据传输对象](https://www.baeldung.com/java-dto-pattern)，封装值以在进程或网络之间传输数据。

这有助于减少调用的方法的数量。通过在单个调用中包含多个参数或值，我们减少了远程操作中的网络开销。

这种模式的另一个优点是序列化逻辑的封装。它允许程序以特定格式存储和传输数据。

DTO 没有任何明确的行为。它基本上有助于通过将域模型与表示层解耦来使代码松散耦合。

### 4.2. 如何使用DTO？

DTO 具有扁平结构，没有任何业务逻辑。它们使用与 POJO 相同的格式。DTO 仅包含与序列化或解析相关的存储、访问器和方法。

DTO 基本上映射到域模型，从而将数据发送到方法或服务器。

让我们创建EmployeeDTO，它将创建员工所需的所有详细信息分组。我们将在优化与 API 交互的单个请求中将此数据发送到服务器：

```java
public class EmployeeDTO {

    private String firstName;
    private String lastName;
    private LocalDate startDate;

    // standard getters and setters
}
```

上面的 DTO 与不同的服务交互并处理数据流。这种 DTO 模式可以在任何服务中使用，没有任何框架限制。

## 5. 画外音

VO，也称为值对象，是一种特殊类型的对象，可以保存java.lang.Integer和java.lang.Long等值。

VO 应该总是覆盖equals()和hashCode()方法。VO 一般会封装数字、日期、字符串等小对象。它们遵循值语义，即它们直接更改对象的值并传递副本而不是引用。

使值对象不可变是一种很好的做法。值的更改仅通过创建新对象发生，而不是通过更新旧对象本身的值发生。这有助于理解两个创建相等的值对象应该保持相等的隐式契约。

让我们定义EmployeeVO并覆盖equals()和hashCode()方法：

```java
public class EmployeeVO {

    private String firstName;
    private String lastName;
    private LocalDate startDate;

    public EmployeeVO(String firstName, String lastName, LocalDate startDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.startDate = startDate;
    }
    // Getters

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        EmployeeVO emp = (EmployeeVO) obj;

        return Objects.equals(firstName, emp.firstName)
          && Objects.equals(lastName, emp.lastName)
          && Objects.equals(startDate, emp.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, startDate);
    }
}
```

## 六，总结

在本文中，我们看到了 POJO、JavaBeans、DTO 和值对象的定义。我们还了解了一些框架和库如何利用 JavaBean 命名约定以及如何将 POJO 转换为 JavaBean。我们还了解了 DTO 模式和值对象以及它们在不同场景中的用法。