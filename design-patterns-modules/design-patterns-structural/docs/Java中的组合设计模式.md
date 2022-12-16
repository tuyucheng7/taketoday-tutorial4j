## 1. 简介

在本快速教程中，我们介绍Java中的组合设计模式，主要描述该模式的结构及其使用目的。

## 2. 结构

**组合模式旨在允许以相同的方式处理单个对象和对象的组合，或“复合”**。 

它可以被视为由继承基类型的类型组成的树结构，它可以表示对象的单个部分或整个层次结构。

我们可以将模式分解为：

-   component：是组合中所有对象的基础接口，它应该是一个接口或一个抽象类，具有管理子组合的通用方法。
-   leaf：实现基本组件的默认行为，它不包含对其他对象的引用。
-   composite：具有叶元素，它实现了基本组件方法并定义了与子组件相关的操作。
-   client：可以使用基本组件对象访问组合元素。

## 3. 实例

现在，让我们实现该模式。假**设我们想在公司中建立一个部门的层次结构**。

### 3.1 基本组件

作为组件对象，我们将定义一个简单的 Department接口：

```java
public interface Department {
    void printDepartmentName();
}
```

### 3.2 叶子

对于叶组件，我们为财务和销售部门定义类：

```java
public class FinancialDepartment implements Department {

    private Integer id;
    private String name;

    public void printDepartmentName() {
        System.out.println(getClass().getSimpleName());
    }

    // standard constructor, getters, setters
}
```

第二个叶组件类SalesDepartment类似：

```java
public class SalesDepartment implements Department {

    private Integer id;
    private String name;

    public void printDepartmentName() {
        System.out.println(getClass().getSimpleName());
    }

    // standard constructor, getters, setters
}
```

这两个类都实现了基础组件的printDepartmentName()方法，它们在其中打印每个类的类名。此外，由于它们是叶子类，因此它们不包含其他Department对象。

接下来，让我们看看复合类。

### 3.3 复合元素

作为复合类，我们创建一个HeadDepartment类：

```java
public class HeadDepartment implements Department {
    private Integer id;
    private String name;

    private List<Department> childDepartments;

    public HeadDepartment(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.childDepartments = new ArrayList<>();
    }

    public void printDepartmentName() {
        childDepartments.forEach(Department::printDepartmentName);
    }

    public void addDepartment(Department department) {
        childDepartments.add(department);
    }

    public void removeDepartment(Department department) {
        childDepartments.remove(department);
    }
}
```

**这是一个复合类，因为它包含Department组件的集合**，以及用于在集合中添加和删除元素的方法。

复合printDepartmentName()方法是通过遍历叶元素集合并为每个元素调用适当的方法来实现的。

## 4. 测试

出于测试目的，让我们看一下CompositeDemo类：

```java
public class CompositeDemo {
    public static void main(String[] args) {
        Department salesDepartment = new SalesDepartment(1, "Sales department");
        Department financialDepartment = new FinancialDepartment(2, "Financial department");

        HeadDepartment headDepartment = new HeadDepartment(3, "Head department");

        headDepartment.addDepartment(salesDepartment);
        headDepartment.addDepartment(financialDepartment);

        headDepartment.printDepartmentName();
    }
}
```

首先，我们为财务和销售部门创建两个实例，之后，我们实例化总部门并将之前创建的实例添加到其中。

最后，我们可以测试printDepartmentName()组合方法，正如我们所期望的，**输出包含每个叶组件的类名**：

```shell
SalesDepartment
FinancialDepartment
```

## 5. 总结

在本文中，我们了解了组合设计模式，简单了解了其模式的主要结构，并通过实际示例演示了用法。