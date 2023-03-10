## 1. 概述

存储过程是**驻留在数据库**中的一组已编译的SQL语句。它们用于封装逻辑并与其他程序共享逻辑，并受益于特定于数据库的功能，如索引提示或特定关键字。

本文演示如何使用**Hibernate**调用**MySQL数据库中的存储过程**。

## 2. MySQL中的存储过程

在我们讨论如何从Hibernate调用存储过程之前，我们需要创建它。

对于这个快速的MySQL示例，我们将[创建一个存储过程](https://dev.mysql.com/doc/refman/5.7/en/create-procedure.html)以从**foo**表中获取所有记录。

要创建存储过程，我们使用CREATE PROCEDURE语句：

```sql
DELIMITER
//
CREATE PROCEDURE GetAllFoos()
    LANGUAGE SQL DETERMINISTIC SQL SECURITY DEFINER
BEGIN
SELECT * FROM foo;
END
//
DELIMITER;
```

在BEGIN语句之前，我们可以定义可选语句。你可以通过[官方MySQL文档](https://dev.mysql.com/doc/refman/5.7/en/create-procedure.html)深入了解这些语句的详细信息。

我们可以使用[CALL](https://dev.mysql.com/doc/refman/5.7/en/call.html)语句来确保我们的过程以期望的方式运行：

```sql
CALL GetAllFoos();
```

现在我们已经启动并运行了存储过程，让我们直接跳到如何从Hibernate调用它。

## 3. 使用Hibernate调用存储过程

从Hibernate 3开始，我们可以使用包含存储过程的原始SQL语句来查询数据库。

在本节中，我们将通过一个看似基本的示例来说明如何使用Hibernate调用**GetAllFoos()**过程。

### 3.1 配置

在我们开始编写可以运行的代码之前，我们需要在我们的项目中配置好Hibernate。

当然，对于所有这些Maven依赖项、MySQL配置、Hibernate配置和**SessionFactory**实例化-你可以查看[Hibernate文章](https://www.baeldung.com/hibernate-4-spring)。

### 3.2 使用CreateNativeSQL方法调用存储过程

Hibernate允许直接以**原生SQL**格式表达查询。因此，我们可以直接创建原生SQL查询，并使用CALL语句调用getAllFoos()存储过程：

```java
Query query = session.createSQLQuery("CALL GetAllFoos()").addEntity(Foo.class);
List<Foo> allFoos = query.list();
```

上面的查询返回一个列表，其中每个元素都是一个Foo对象。

我们使用**addEntity()**方法从原生**SQL**查询中获取实体对象，否则，只要存储过程返回非原始值，就会抛出**ClassCastException**。

### 3.3 使用@NamedNativeQueries调用存储过程

调用存储过程的另一种方法是使用**@NamedNativeQueries**注解。

**@NamedNativeQueries**用于指定作用域为持久性单元的原生**SQL**命名查询：

```java
@NamedNativeQueries({
      @NamedNativeQuery(
            name = "callGetAllFoos",
            query = "CALL GetAllFoos()",
            resultClass = Foo.class)
})
@Entity
public class Foo implements Serializable {
    // Model definition
}
```

每个命名查询显然都有一个**name**属性、实际的**SQL查询**和引用**Foo**映射实体的**resultClass**。

```java
Query query = session.getNamedQuery("callGetAllFoos");
List<Foo> allFoos = query.list();
```

**resultClass**属性与我们前面示例中的**addEntity()**方法起着相同的作用。

这两种方法可以互换使用，因为在性能或生产力方面，两者之间没有真正的区别。

### 3.4 使用@NamedStoredProcedureQuery调用存储过程

如果你使用的是**JPA 2.1**以及**EntityManagerFactory**和**EntityManager**的**Hibernate**实现。

**@NamedStoredProcedureQuery**注解可用于声明存储过程：

```java
@NamedStoredProcedureQuery(
      name="GetAllFoos",
      procedureName="GetAllFoos",
      resultClasses = { Foo.class }
)
@Entity
public class Foo implements Serializable {
    // Model Definition 
}
```

要调用我们的命名存储过程查询，我们需要实例化一个**EntityManager**，然后调用**createNamedStoredProcedureQuery()**方法来创建过程：

```java
StoredProcedureQuery spQuery = entityManager.createNamedStoredProcedureQuery("getAllFoos");
```

我们可以通过调用**StoredProcedureQuery**对象的**execute()**方法直接获取Foo实体的集合。

## 4. 带参数的存储过程

几乎我们所有的存储过程都需要参数。在本节中，我们将展示如何从Hibernate调用带有参数的存储过程。

让我们在MySQL中创建一个getFoosByName()存储过程。

此过程返回name属性与fooName参数匹配的Foo对象列表：

```sql
DELIMITER //
    CREATE PROCEDURE GetFoosByName(IN fooName VARCHAR(255))
        LANGUAGE SQL
        DETERMINISTIC
        SQL SECURITY DEFINER
        BEGIN
            SELECT * FROM foo WHERE name = fooName;
        END //
DELIMITER;
```

要调用**GetFoosByName()**过程，我们将使用命名参数：

```java
Query query = session.createSQLQuery("CALL GetFoosByName(:fooName)")
    .addEntity(Foo.class)
    .setParameter("fooName","New Foo");
```

同样，命名参数**:fooName**可以与**@NamedNativeQuery**注解一起使用：

```java
@NamedNativeQuery(
    name = "callGetFoosByName", 
    query = "CALL GetFoosByName(:fooName)", 
    resultClass = Foo.class
)
```

命名查询将按如下方式调用：

```java
Query query = session.getNamedQuery("callGetFoosByName")
    .setParameter("fooName","New Foo");
```

使用**@NamedStoredProcedureQuery**注解时，我们可以使用**@StoredProcedureParameter**注解指定参数：

```java
@NamedStoredProcedureQuery(
    name="GetFoosByName",
    procedureName="GetFoosByName",
    resultClasses = { Foo.class },
    parameters={
        @StoredProcedureParameter(name="fooName", type=String.class, mode=ParameterMode.IN)
    }
)
```

我们可以使用**setParameter()**方法调用带有fooName参数的存储过程：

```java
StoredProcedureQuery spQuery = entityManager.createNamedStoredProcedureQuery("GetFoosByName")
    .setParameter("fooName", "NewFooName");
```

## 5. 总结

本文演示了如何使用Hibernate以不同的方式调用MySQL数据库中的存储过程。

值得一提的是，并不是所有的RDBMS都支持存储过程。