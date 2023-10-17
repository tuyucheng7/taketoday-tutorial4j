## 1. 概述

在这个简短的教程中，我们将研究“普通旧Java对象”或简称 POJO 的定义。

我们将了解 POJO 与 JavaBean 的比较，以及将我们的 POJO 转换为 JavaBean 有何帮助。

## 2. 普通的旧Java对象

### 2.1. 什么是POJO？

当我们谈论 POJO 时，我们所描述的是一种没有引用任何特定框架的简单类型。POJO 没有针对我们的属性和方法的命名约定。

让我们创建一个基本的员工 POJO。它将具有三个属性；名字、姓氏和开始日期：

```java
public class EmployeePojo {

    public String firstName;
    public String lastName;
    private LocalDate startDate;

    public EmployeePojo(String firstName, String lastName, LocalDate startDate) {
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

此类可由任何Java程序使用，因为它不依赖于任何框架。

但是，我们并没有遵循任何构造、访问或修改类状态的真正约定。

缺乏约定会导致两个问题：

首先，它增加了试图了解如何使用它的编码人员的学习曲线。

其次，它可能会限制框架支持约定优于配置、了解如何使用类以及增强其功能的能力。

为了探索第二点，让我们使用反射来处理EmployeePojo 。因此，我们将开始发现它的一些局限性。

### 2.2. 使用 POJO 进行反射

让我们将[commons -beanutils](https://search.maven.org/search?q=g:commons-beanutils AND a:commons-beanutils)[依赖](https://search.maven.org/search?q=g:commons-beanutils AND a:commons-beanutils)项添加 到我们的项目中：

```xml
<dependency>
    <groupId>commons-beanutils</groupId>
    <artifactId>commons-beanutils</artifactId>
    <version>1.9.4</version>
</dependency>
```

现在，让我们检查一下 POJO 的属性：

```java
List<String> propertyNames =
  PropertyUtils.getPropertyDescriptors(EmployeePojo.class).stream()
    .map(PropertyDescriptor::getDisplayName)
    .collect(Collectors.toList());
```

如果我们将propertyNames打印到控制台，我们只会看到：

```plaintext
[start]

```

在这里，我们看到我们只是作为类的一个属性开始 。PropertyUtils未能找到其他两个。

[如果我们使用Jackson](https://www.baeldung.com/jackson)等其他库来处理 EmployeePojo，我们会看到同样的结果。

理想情况下，我们会看到所有属性：firstName、lastName和startDate。好消息是许多Java库默认支持称为 JavaBean 命名约定的东西。

## 3.JavaBean

### 3.1. 什么是JavaBean？

一个 JavaBean 仍然是一个 POJO，但是引入了一组关于我们如何实现它的严格规则：

-   访问级别——我们的属性是私有的，我们公开 getter 和 setter
-   方法名称——我们的 getter 和 setter 遵循getX和setX约定(在布尔值的情况下，isX可用于 getter)
-   默认构造函数 - 必须存在无参数构造函数，以便可以在不提供参数的情况下创建实例，例如在反序列化期间
-   Serializable——实现Serializable接口允许我们存储状态

### 3.2. 作为 JavaBean 的EmployeePojo 

那么，让我们尝试将EmployeePojo转换为 JavaBean：

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

    //  additional getters/setters

}
```

### 3.3. 使用 JavaBean 进行反射

当我们用反射检查我们的 bean 时，现在我们得到了完整的属性列表：

```plaintext
[firstName, lastName, startDate]
```

## 4. 使用 JavaBean 时的权衡

因此，我们展示了 JavaBeans 的有用方式。请记住，每个设计选择都需要权衡取舍。

当我们使用 JavaBeans 时，我们还应该注意一些潜在的缺点：

-   可变性——我们的 JavaBeans 是可变的，因为它们的 setter 方法——这可能会导致并发或一致性问题
-   样板——我们必须为所有属性引入 getter 并为大多数属性引入 setter，其中大部分可能是不必要的
-   零参数构造函数——我们的构造函数中经常需要参数来确保对象在有效状态下被实例化，但 JavaBean 标准要求我们提供一个零参数构造函数

考虑到这些权衡，多年来框架也适应了其他 bean 约定。

## 5.总结

在本教程中，我们将 POJO 与 JavaBeans 进行了比较。

首先，我们了解到 POJO 是一个没有绑定到特定框架的Java对象，而 JavaBean 是一种特殊类型的 POJO，具有一组严格的约定。

然后，我们了解了一些框架和库如何利用 JavaBean 命名约定来发现类的属性。