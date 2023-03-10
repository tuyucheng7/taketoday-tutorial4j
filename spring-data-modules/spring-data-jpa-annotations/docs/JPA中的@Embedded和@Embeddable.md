## 1. 概述

在本教程中，我们将了解如何将包含嵌入属性的实体映射到单个数据库表。

为此，我们将使用[Java Persistence API(JPA)](https://www.baeldung.com/jpa-hibernate-difference)提供的@Embeddable和@Embedded注解。

## 2. 数据模型上下文

首先，我们定义一个名为company的表。

公司表会存放公司名称、地址、电话等基本信息以及联系人信息：

```java
public class Company {

    private Integer id;

    private String name;

    private String address;

    private String phone;

    private String contactFirstName;

    private String contactLastName;

    private String contactPhone;

    // standard getters, setters
}
```

**但是，跟联系人相关的三个字段似乎应该抽象到一个单独的类中**。

问题是**我们不想为这些细节创建一个单独的表**。

那么，让我们看看我们能做什么。

## 3. @Embeddable

**JPA提供了@Embeddable注解来声明一个类将被其他实体嵌入**。

让我们定义一个类来抽象出联系人详细信息：

```java
@Embeddable
public class ContactPerson {

    private String firstName;

    private String lastName;

    private String phone;

    // standard getters, setters
}
```

## 4. @Embedded

**JPA注解@Embedded用于将一个类型嵌入到另一个实体中**。

接下来，让我们修改Company类。

我们将添加JPA注解，并改为使用ContactPerson而不是单独的字段：

```java
@Entity
public class Company {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    private String address;

    private String phone;

    @Embedded
    private ContactPerson contactPerson;

    // standard getters, setters
}
```

因此，我们的Company实体嵌入联系人详细信息并映射到单个数据库表。

不过，我们还有一个问题，那就是**JPA如何将这些字段映射到数据库列**。

## 5. 属性覆盖

我们的字段在原来的Company类中被命名contactFirstName，现在在我们的ContactPerson类中被命名为firstName。因此，JPA会将它们分别映射到contact_first_name和first_name。

除了不太理想之外，它实际上还会破坏我们现在重复的phone列，因为此时Company和ContactPerson实体中都包含phone属性。

**因此，我们可以使用@AttributeOverrides和@AttributeOverride注解来覆盖我们嵌入类型的列属性**。

让我们将其添加到Company实体中的ContactPerson字段：

```java
@Embedded
@AttributeOverrides({
    @AttributeOverride( name = "firstName", column = @Column(name = "contact_first_name")), 
    @AttributeOverride( name = "lastName", column = @Column(name = "contact_last_name")), 
    @AttributeOverride( name = "phone", column = @Column(name = "contact_phone"))
})
private ContactPerson contactPerson;
```

请注意，由于这些注解使用在字段上，因此我们可以对每个封闭实体进行不同的覆盖。

## 6. 总结

在本文中，我们配置了一个具有一些嵌入属性的实体，并将它们映射到与封闭实体相同的数据库表。

为此，我们使用了Java Persistence API提供的@Embedded、@Embeddable、@AttributeOverrides和@AttributeOverride注解。