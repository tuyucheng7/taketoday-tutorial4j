## 1. 概述

注解javax.persistence.JoinColumn将列标记为实体关联或元素集合的连接列。

在本快速教程中，我们将展示一些基本的@JoinColumn用法示例。

## 2. @OneToOne映射示例

@JoinColumn注解与 @OneToOne映射相结合表明所有者实体中的给定列引用引用实体中的主键：

```java
@Entity
public class Office {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "addressId")
    private Address address;
}
```

上面的代码示例将创建一个外键，将Office实体与Address实体的主键 链接起来。Office实体中外键列的名称由名称属性指定 。

## 3. @OneToMany映射示例

使用 @OneToMany映射时，我们可以使用mappedBy参数来指示给定列由另一个实体拥有：

```java
@Entity
public class Employee {
 
    @Id
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee")
    private List<Email> emails;
}

@Entity
public class Email {
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
```

在上面的示例中，Email(所有者实体)有一个连接列employee_id ，它存储 id 值并有一个指向 Employee实体的外键。

[![加入col1](https://www.baeldung.com/wp-content/uploads/2018/08/joincol1.png)](https://www.baeldung.com/wp-content/uploads/2018/08/joincol1.png)

## 4. @JoinColumns

在我们想要创建多个连接列的情况下，我们可以使用@JoinColumns注解：

```java
@Entity
public class Office {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name="ADDR_ID", referencedColumnName="ID"),
        @JoinColumn(name="ADDR_ZIP", referencedColumnName="ZIP")
    })
    private Address address;
}

```

上面的示例将创建两个指向地址实体中的ID和ZIP列 的外键：

[![加入col2](https://www.baeldung.com/wp-content/uploads/2018/08/joincol2.png)](https://www.baeldung.com/wp-content/uploads/2018/08/joincol2.png)

## 5.总结

在本文中，我们学习了如何使用@JoinColumn注解。我们研究了如何创建单个实体关联和元素集合。