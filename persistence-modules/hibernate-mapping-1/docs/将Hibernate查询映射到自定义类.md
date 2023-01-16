## 1. 概述

当我们使用 Hibernate 从数据库中检索数据时，默认情况下，它使用检索到的数据为请求的对象构造整个对象图。但有时我们可能只想检索部分数据，最好是在平面结构中。

在本快速教程中，我们将了解如何使用自定义类在 Hibernate 中实现这一点。

## 2.实体

首先，让我们看看我们将用来检索数据的实体：

```java
@Entity
public class DeptEmployee {
 
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String employeeNumber;

    private String designation;

    private String name;

    @ManyToOne
    private Department department;

    // constructor, getters and setters 
} 

@Entity
public class Department {
 
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String name;

    @OneToMany(mappedBy="department")
    private List<DeptEmployee> employees;

    public Department(String name) {
        this.name = name;
    }
    
    // getters and setters 
}
```

在这里，我们有两个实体——DeptEmployee和Department。为简单起见，我们假设DeptEmployee只能属于一个部门。

但是，一个部门可以有多个DeptEmployees。

## 3.自定义查询结果类

假设我们要打印所有员工的列表，其中只包含他们的姓名和部门名称。

通常，我们会使用如下查询来检索此数据：

```java
Query<DeptEmployee> query = session.createQuery("from com.baeldung.hibernate.entities.DeptEmployee");
List<DeptEmployee> deptEmployees = query.list();
```

这将检索所有员工、他们的所有属性、关联的部门及其所有属性。

但是，在这种特殊情况下，这可能有点昂贵，因为我们只需要员工的姓名和部门的名称。

仅检索我们需要的信息的一种方法是在 select 子句中指定属性。

但是，当我们这样做时，Hibernate 返回一个数组列表而不是一个对象列表：

```java
Query query = session.createQuery("select m.name, m.department.name from com.baeldung.hibernate.entities.DeptEmployee m");
List managers = query.list();
Object[] manager = (Object[]) managers.get(0);
assertEquals("John Smith", manager[0]);
assertEquals("Sales", manager[1]);
```

正如我们所见，返回的数据处理起来有点麻烦。但是，幸运的是，我们可以让 Hibernate 将这些数据填充到一个类中。

让我们看看我们将用来将检索到的数据填充到的Result类：

```java
public class Result {
    private String employeeName;
    
    private String departmentName;
    
    public Result(String employeeName, String departmentName) {
        this.employeeName = employeeName;
        this.departmentName = departmentName;
    }

    public Result() {
    }

    // getters and setters 
}
```

请注意，该类不是实体，而只是一个 POJO。但是，我们也可以使用一个实体，只要它有一个构造函数，该构造函数将我们想要填充的所有属性作为参数。

我们将在下一节中了解为什么构造函数很重要。

## 4. 在 HQL 中使用构造函数

现在，让我们看看使用这个类的 HQL：

```java
Query<Result> query = session.createQuery("select new com.baeldung.hibernate.pojo.Result(m.name, m.department.name)" 
  + " from com.baeldung.hibernate.entities.DeptEmployee m");
List<Result> results = query.list();
Result result = results.get(0);
assertEquals("John Smith", result.getEmployeeName());
assertEquals("Sales", result.getDepartmentName());
```

在这里，我们使用我们在Result 类中定义的构造函数以及我们要检索的属性。这将返回一个Result对象列表，其中包含从列中填充的数据。

如我们所见，返回的列表比使用对象数组列表更容易处理。

请务必注意，我们必须在查询中使用类的完全限定名称。

## 5. 使用 ResultTransformer

在 HQL 查询中使用构造函数的替代方法是使用ResultTransformer：

```java
Query query = session.createQuery("select m.name as employeeName, m.department.name as departmentName" 
  + " from com.baeldung.hibernate.entities.DeptEmployee m");
query.setResultTransformer(Transformers.aliasToBean(Result.class));
List<Result> results = query.list();
Result result = results.get(0);
assertEquals("John Smith", result.getEmployeeName());
assertEquals("Sales", result.getDepartmentName());
```

我们使用 变形金刚。aliasToBean() 方法使用检索到的数据填充 Result 对象。

因此，我们必须确保 select 语句中的列名或它们的别名与 Result 类的属性相匹配。

请注意，[Query.setResultTransformer( ResultTransformer ](https://docs.jboss.org/hibernate/orm/5.2/javadocs/org/hibernate/Query.html#setResultTransformer-org.hibernate.transform.ResultTransformer-)) 自 Hibernate 5.2 以来已被弃用。

## 六. 总结

在本文中，我们了解了如何使用自定义类以易于阅读的形式检索数据。