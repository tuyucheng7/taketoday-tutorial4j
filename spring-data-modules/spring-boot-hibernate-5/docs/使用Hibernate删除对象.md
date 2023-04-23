## 1. 概述

Hibernate 作为一个全功能的 ORM 框架，负责持久化对象(实体)的生命周期管理，包括read、save、update和delete等 CRUD 操作。

在本文中，我们探讨了使用 Hibernate 从数据库中删除对象的各种方法，并解释了可能发生的常见问题和陷阱。

我们使用 JPA，并且只退后一步，将 Hibernate 本机 API 用于 JPA 中未标准化的那些功能。

## 2. 删除对象的不同方式

在以下情况下可能会删除对象：

-   通过使用EntityManager.remove
-   当删除从其他实体实例级联时
-   当应用orphanRemoval时
-   通过执行delete JPQL 语句
-   通过执行本机查询
-   通过应用软删除技术(通过@Where子句中的条件过滤软删除的实体)

在本文的其余部分，我们将详细研究这些要点。

## 3.使用实体管理器删除

使用EntityManager删除是删除实体实例的最直接方法：

```java
Foo foo = new Foo("foo");
entityManager.persist(foo);
flushAndClear();

foo = entityManager.find(Foo.class, foo.getId());
assertThat(foo, notNullValue());
entityManager.remove(foo);
flushAndClear();

assertThat(entityManager.find(Foo.class, foo.getId()), nullValue());


```

在本文的示例中，我们使用辅助方法在需要时刷新和清除持久化上下文：

```java
void flushAndClear() {
    entityManager.flush();
    entityManager.clear();
}
```

调用EntityManager.remove方法后，提供的实例转换为已删除状态，并且在下一次刷新时从数据库中删除关联的内容。

请注意，如果对其应用PERSIST操作，已删除的实例将重新保留。一个常见的错误是忽略PERSIST操作已应用于已删除的实例(通常是因为它在刷新时从另一个实例级联)，因为[JPA 规范](https://download.oracle.com/otndocs/jcp/persistence-2_1-fr-eval-spec/index.html)的第 3.2.2节要求此类实例是在这种情况下再次坚持。

我们通过定义一个从Foo到Bar的@ManyToOne关联来说明这一点：

```java
@Entity
public class Foo {
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Bar bar;

    // other mappings, getters and setters
}
```

当我们删除一个由Foo实例引用的Bar实例时，该实例也加载在持久化上下文中，Bar实例不会从数据库中删除：

```java
Bar bar = new Bar("bar");
Foo foo = new Foo("foo");
foo.setBar(bar);
entityManager.persist(foo);
flushAndClear();

foo = entityManager.find(Foo.class, foo.getId());
bar = entityManager.find(Bar.class, bar.getId());
entityManager.remove(bar);
flushAndClear();

bar = entityManager.find(Bar.class, bar.getId());
assertThat(bar, notNullValue());

foo = entityManager.find(Foo.class, foo.getId());
foo.setBar(null);
entityManager.remove(bar);
flushAndClear();

assertThat(entityManager.find(Bar.class, bar.getId()), nullValue());
```

如果删除的Bar被Foo引用，则PERSIST操作将从Foo级联到Bar，因为关联标记为cascade = CascadeType.ALL并且删除未计划。为了验证这是否正在发生，我们可以为org.hibernate包启用跟踪日志级别并搜索诸如un-scheduling entity deletion之类的条目。

## 4.级联删除

当父母被移除时，删除可以级联到子实体：

```java
Bar bar = new Bar("bar");
Foo foo = new Foo("foo");
foo.setBar(bar);
entityManager.persist(foo);
flushAndClear();

foo = entityManager.find(Foo.class, foo.getId());
entityManager.remove(foo);
flushAndClear();

assertThat(entityManager.find(Foo.class, foo.getId()), nullValue());
assertThat(entityManager.find(Bar.class, bar.getId()), nullValue());
```

这里bar被删除是因为删除是从foo级联的，因为关联被声明为将所有生命周期操作从Foo级联到Bar。

请注意，在@ManyToMany关联中级联REMOVE操作几乎总是一个错误，因为这会触发删除可能与其他父实例关联的子实例。这也适用于CascadeType.ALL，因为它意味着所有操作都将被级联，包括REMOVE操作。

## 5. 孤儿迁徙

orphanRemoval指令声明关联的实体实例在与父实体解除关联时将被删除，或者等效地在父实体被删除时删除。

我们通过定义从Bar到Baz 的关联来证明这一点：

```java
@Entity
public class Bar {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Baz> bazList = new ArrayList<>();

    // other mappings, getters and setters
}
```

然后，当Baz实例从父Bar实例的列表中删除时，它会自动删除：

```java
Bar bar = new Bar("bar");
Baz baz = new Baz("baz");
bar.getBazList().add(baz);
entityManager.persist(bar);
flushAndClear();

bar = entityManager.find(Bar.class, bar.getId());
baz = bar.getBazList().get(0);
bar.getBazList().remove(baz);
flushAndClear();

assertThat(entityManager.find(Baz.class, baz.getId()), nullValue());
```

orphanRemoval操作的语义与直接应用于受影响子实例的REMOVE操作完全相似，这意味着REMOVE操作进一步级联到嵌套子实例。因此，你必须确保没有其他实例引用已删除的实例(否则它们会重新保留)。

## 6.使用JPQL语句删除

Hibernate 支持 DML 风格的删除操作：

```java
Foo foo = new Foo("foo");
entityManager.persist(foo);
flushAndClear();

entityManager.createQuery("delete from Foo where id = :id")
  .setParameter("id", foo.getId())
  .executeUpdate();

assertThat(entityManager.find(Foo.class, foo.getId()), nullValue());
```

重要的是要注意DML 样式的 JPQL 语句既不影响已经加载到持久性上下文中的实体实例的状态也不影响生命周期，因此建议在加载受影响的实体之前执行它们。

## 7. 使用本机查询删除

有时我们需要回退到本地查询来实现 Hibernate 不支持或特定于数据库供应商的东西。我们还可以使用本机查询删除数据库中的数据：

```java
Foo foo = new Foo("foo");
entityManager.persist(foo);
flushAndClear();

entityManager.createNativeQuery("delete from FOO where ID = :id")
  .setParameter("id", foo.getId())
  .executeUpdate();

assertThat(entityManager.find(Foo.class, foo.getId()), nullValue());
```

与 JPA DML 样式语句一样，同样的建议适用于本机查询，即本机查询既不影响实体实例的状态，也不影响在执行查询之前加载到持久性上下文中的实体实例的生命周期。

## 8.软删除

出于审计目的和保留历史记录，通常不希望从数据库中删除数据。在这种情况下，我们可能会应用一种称为软删除的技术。基本上，我们只是将一行标记为已删除，然后在检索数据时将其过滤掉。

为了避免在所有读取软删除实体的查询中的where子句中出现大量冗余条件，Hibernate 提供了@Where注解，它可以放在实体上，它包含一个 SQL 片段，该片段会自动添加到为生成的 SQL 查询中那个实体。

为了演示这一点，我们将@Where注解和名为DELETED的列添加到Foo实体：

```java
@Entity
@Where(clause = "DELETED = 0")
public class Foo {
    // other mappings

    @Column(name = "DELETED")
    private Integer deleted = 0;
    
    // getters and setters

    public void setDeleted() {
        this.deleted = 1;
    }
}
```

以下测试确认一切都按预期工作：

```java
Foo foo = new Foo("foo");
entityManager.persist(foo);
flushAndClear();

foo = entityManager.find(Foo.class, foo.getId());
foo.setDeleted();
flushAndClear();

assertThat(entityManager.find(Foo.class, foo.getId()), nullValue());
```

## 9.总结

在本文中，我们研究了使用 Hibernate 删除数据的不同方法。我们解释了基本概念和一些最佳实践。我们还演示了如何使用 Hibernate 轻松实现软删除。