## 1. 概述

在本教程中，我们将在 Hibernate 的load()方法的上下文中了解什么是代理。

对于刚接触 Hibernate 的读者，请考虑先熟悉 [基础知识](https://www.baeldung.com/hibernate-4-spring)。

## 2.代理和load()方法简介

根据定义，[代理人](https://www.dictionary.com/browse/proxy)是“被授权充当代理人或替代他人的职能”。

当我们调用Session.load()来创建所谓的 所需实体类的未初始化代理 时，这适用于 Hibernate 。

简单地说，Hibernate 使用 CGLib库对我们的实体类进行子类化。除了 @Id方法之外，代理实现将所有其他属性方法委托给 Hibernate 会话来填充实例，有点像：

```java
public class HibernateProxy extends MyEntity {
    private MyEntity target;

    public String getFirstName() {
        if (target == null) {
            target = readFromDatabase();
        }
        return target.getFirstName();
    }
}
```

这个子类将被返回而不是直接查询数据库。

一旦其中一个实体方法被调用，实体就会被加载，并在此时成为一个 初始化的代理。

## 3.代理和延迟加载

### 3.1. 单一实体

让我们将 Employee 视为一个实体。首先，我们假设它与任何其他表都没有关系。

如果我们使用 Session.load()来实例化一个Employee：

```java
Employee albert = session.load(Employee.class, new Long(1));
```

然后 Hibernate 将创建一个未初始化的Employee代理。 它将包含我们给它的 ID，但除此之外没有其他值，因为我们还没有访问数据库。

但是，一旦我们在albert上调用方法 ：

```java
String firstName = albert.getFirstName();
```

然后 Hibernate 将查询员工数据库表以查找主键为 1 的实体，并 使用相应行中的属性填充albert 。

如果找不到行，则 Hibernate 会抛出 ObjectNotFoundException。

### 3.2. 一对多关系

现在，让我们 也 创建一个Company 实体，其中Company 有很多 Employees：

```java
public class Company {
    private String name;
    private Set<Employee> employees;
}
```

如果我们这次 对公司使用Session.load() ：

```java
Company bizco = session.load(Company.class, new Long(1));
String name = bizco.getName();
```

然后像以前一样填充公司的属性，只是员工组稍有不同。

 看，我们只查询了公司行，但是在我们根据获取策略调用getEmployees之前，代理会让员工保持独立 。

### 3.3. 多对一关系

反方向情况类似：

```java
public class Employee {
    private String firstName;
    private Company workplace;
}
```

如果我们再次使用 load()：

```java
Employee bob = session.load(Employee.class, new Long(2));
String firstName = bob.getFirstName();
```

bob 现在将被初始化，实际上， workplace现在将根据获取策略设置为未初始化的代理。

## 4. 延迟加载

现在， load() 不会总是给我们一个未初始化的代理。事实上，[Session java doc](https://docs.jboss.org/hibernate/orm/3.5/api/org/hibernate/Session.html#load(java.lang.Class, java.io.Serializable))提醒我们(强调)：

>   当访问非标识符方法时，此方法可能会返回按需初始化的代理实例。

何时会发生这种情况的一个简单示例是批量大小。

假设我们 在Employee 实体上 使用@BatchSize：

```java
@Entity
@BatchSize(size=5)
class Employee {
    // ...
}
```

这次我们有三名员工：

```java
Employee catherine = session.load(Employee.class, new Long(3));
Employee darrell = session.load(Employee.class, new Long(4));
Employee emma = session.load(Employee.class, new Long(5));
```

如果我们在 catherine上调用 getFirstName ：

```java
String cathy = catherine.getFirstName();
```

然后，实际上，Hibernate 可能会决定一次加载所有三个雇员，将所有三个都变成初始化代理。

然后，当我们要求 darrell的名字时：

```java
String darrell = darrell.getFirstName();
```

然后Hibernate 根本不会访问数据库。

## 5. 预加载

### 5.1. 使用get()

我们也可以完全绕过代理并要求 Hibernate 使用Session.get()加载真实的东西：

```java
Employee finnigan = session.get(Employee.class, new Long(6));
```

这将立即调用数据库，而不是返回代理。

实际上， 如果 finnigan 不存在，它将返回 null而不是ObjectNotFoundException 。

### 5.2. 性能影响

虽然get()很方便，但load()可以减轻数据库负担。

例如，假设杰拉德 要为一家新公司工作：

```java
Employee gerald = session.get(Employee.class, new Long(7));
Company worldco = (Company) session.load(Company.class, new Long(2));
employee.setCompany(worldco);        
session.save(employee);
```

因为我们知道在这种情况下我们只会更改员工 记录，所以为Company 调用 load()是明智的。

如果我们在 Company上调用 get()，那么我们会不必要地从数据库中加载它的所有数据。

## 六. 总结

在本文中，我们简要了解了Hibernate代理的工作原理，以及这如何影响 实体及其关系的加载方法。

此外，我们快速了解了load()与 get() 的不同之处。