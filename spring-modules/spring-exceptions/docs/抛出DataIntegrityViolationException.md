## 1. 概述

在本文中，我们将讨论Spring org.springframework.dao.DataIntegrityViolationException–这是一个通用数据异常，通常由Spring异常转换机制在处理较低级别的持久性异常时抛出。本文将讨论此异常的最常见原因以及每个原因的解决方案。

### 延伸阅读

### [Spring Data Java 8支持](https://www.baeldung.com/spring-data-java-8)

Spring Data中Java 8支持的快速实用指南。

[阅读更多](https://www.baeldung.com/spring-data-java-8)→

### [Spring Data注解](https://www.baeldung.com/spring-data-annotations)

了解我们使用Spring Data项目处理持久性所需的最重要的注解

[阅读更多](https://www.baeldung.com/spring-data-annotations)→

### [在Spring Data REST中处理关系](https://www.baeldung.com/spring-data-rest-relationships)

在Spring Data REST中处理实体关系的实用指南。

[阅读更多](https://www.baeldung.com/spring-data-rest-relationships)→

## 2. DataIntegrityViolationException与Spring异常翻译

Spring异常转换机制可以透明地应用于所有用@Repository注解的bean-通过在Context中定义一个异常转换bean后处理器bean：

```xml
<bean id="persistenceExceptionTranslationPostProcessor" 
   class="org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor" />
```

或者在Java中：

```java
@Configuration
public class PersistenceHibernateConfig{
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
        return new PersistenceExceptionTranslationPostProcessor();
    }
}
```

异常转换机制也默认在Spring中可用的旧持久性模板上启用——HibernateTemplate、JpaTemplate等。

## 3. DataIntegrityViolationException在什么地方抛出

### 3.1 Hibernate的DataIntegrityViolationException

Spring配置Hibernate时，在Spring提供的异常转换层-SessionFactoryUtils-convertHibernateAccessException中抛出异常。

存在三种可能导致抛出DataIntegrityViolationException的Hibernate异常：

-   org.hibernate.exception.ConstraintViolationException异常
-   org.hibernate.PropertyValueException异常
-   org.hibernate.exception.DataException异常

### 3.2 JPA的DataIntegrityViolationException

当Spring配置为使用JPA作为其持久性提供程序时，类似于Hibernate，在异常转换层中抛出DataIntegrityViolationException-即在EntityManagerFactoryUtils-convertJpaAccessExceptionIfPossible中。

有一个JPA异常可能触发抛出DataIntegrityViolationException–javax.persistence.EntityExistsException。

## 4. 原因：org.hibernate.exception.ConstraintViolationException

这是迄今为止引发DataIntegrityViolationException的最常见原因——HibernateConstraintViolationException表示该操作违反了数据库完整性约束。

考虑以下示例——对于通过父实体和子实体之间的显式外键列的一对一映射——以下操作应该失败：

```java
@Test(expected = DataIntegrityViolationException.class)
public void whenChildIsDeletedWhileParentStillHasForeignKeyToIt_thenDataException() {
    Child childEntity = new Child();
    childService.create(childEntity);

    Parent parentEntity = new Parent(childEntity);
    service.create(parentEntity);

    childService.delete(childEntity);
}
```

Parent实体有一个指向Child实体的外键，因此删除子实体会破坏Parent上的外键约束这会导致ConstraintViolationException-由Spring包装在DataIntegrityViolationException中：

```bash
org.springframework.dao.DataIntegrityViolationException: 
could not execute statement; SQL [n/a]; constraint [null]; 
nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement
    at o.s.orm.h.SessionFactoryUtils.convertHibernateAccessException(SessionFactoryUtils.java:138)
Caused by: org.hibernate.exception.ConstraintViolationException: could not execute statement
```

要解决此问题，应先删除Parent：

```java
@Test
public void whenChildIsDeletedAfterTheParent_thenNoExceptions() {
    Child childEntity = new Child();
    childService.create(childEntity);

    Parent parentEntity = new Parent(childEntity);
    service.create(parentEntity);

    service.delete(parentEntity);
    childService.delete(childEntity);
}
```

## 5. 原因：org.hibernate.PropertyValueException

这是DataIntegrityViolationException的更常见原因之一-在Hibernate中，这将归结为实体被持久化时出现问题。要么实体有一个用非空约束定义的空属性，要么实体的关联可能引用一个未保存的瞬态实例。

例如，以下实体具有非空名称属性

```java
@Entity
public class Foo {
    // ...

    @Column(nullable = false)
    private String name;

    // ...
}
```

如果以下测试尝试使用name的空值来持久化实体：

```java
@Test(expected = DataIntegrityViolationException.class)
public void whenInvalidEntityIsCreated_thenDataException() {
   fooService.create(new Foo());
}
```

违反了数据库完整性约束，因此抛出DataIntegrityViolationException：

```bash
org.springframework.dao.DataIntegrityViolationException: 
not-null property references a null or transient value: 
org.baeldung.spring.persistence.model.Foo.name; 
nested exception is org.hibernate.PropertyValueException: 
not-null property references a null or transient value: 
org.baeldung.spring.persistence.model.Foo.name
	at o.s.orm.h.SessionFactoryUtils.convertHibernateAccessException(SessionFactoryUtils.java:160)
...
Caused by: org.hibernate.PropertyValueException: 
not-null property references a null or transient value: 
org.baeldung.spring.persistence.model.Foo.name
	at o.h.e.i.Nullability.checkNullability(Nullability.java:103)
...
```

## 6. 原因：org.hibernate.exception.DataException

HibernateDataException表示无效的SQL语句-在该特定上下文中语句或数据有问题。例如，使用之前的orFoo实体，以下将触发此异常：

```java
@Test(expected = DataIntegrityViolationException.class)
public final void whenEntityWithLongNameIsCreated_thenDataException() {
   service.create(new Foo(randomAlphabetic(2048)));
}
```

持久化具有长名称值的对象的实际异常是：

```bash
org.springframework.dao.DataIntegrityViolationException: 
could not execute statement; SQL [n/a]; 
nested exception is org.hibernate.exception.DataException: could not execute statement
   at o.s.o.h.SessionFactoryUtils.convertHibernateAccessException(SessionFactoryUtils.java:143)
...
Caused by: org.hibernate.exception.DataException: could not execute statement
	at o.h.e.i.SQLExceptionTypeDelegate.convert(SQLExceptionTypeDelegate.java:71)
```

在此特定示例中，解决方案是指定名称的最大长度：

```java
@Column(nullable = false, length = 4096)
```

## 7. 原因：javax.persistence.EntityExistsException

与Hibernate类似，EntityExistsExceptionJPA异常也将由SpringExceptionTranslation包装成DataIntegrityViolationException。唯一的区别是JPA本身已经是高级别的，这使得这个JPA异常成为数据完整性违规的唯一潜在原因。

## 8. 潜在的DataIntegrityViolationException

在某些可能预期会出现DataIntegrityViolationException的情况下，可能会抛出另一个异常-一种情况是类路径上存在JSR-303验证程序，例如hibernate-validator4或5。

在这种情况下，如果以下实体以name的空值持久化，它将不再因持久层触发的数据完整性违规而失败：

```java
@Entity
public class Foo {
    ...
    @Column(nullable = false)
    @NotNull
    private String name;

    ...
}
```

这是因为执行不会到达持久层-它会在此之前失败并出现javax.validation.ConstraintViolationException：

```bash
javax.validation.ConstraintViolationException: 
Validation failed for classes [org.baeldung.spring.persistence.model.Foo] 
during persist time for groups [javax.validation.groups.Default, ]
List of constraint violations:[ ConstraintViolationImpl{
    interpolatedMessage='may not be null', propertyPath=name, 
    rootBeanClass=class org.baeldung.spring.persistence.model.Foo, 
    messageTemplate='{javax.validation.constraints.NotNull.message}'}
]
    at o.h.c.b.BeanValidationEventListener.validate(BeanValidationEventListener.java:159)
    at o.h.c.b.BeanValidationEventListener.onPreInsert(BeanValidationEventListener.java:94)
```

## 9. 总结

在本文的最后，我们应该有一个清晰的地图来导航可能导致Spring中的DataIntegrityViolationException的各种原因和问题，以及如何解决所有这些问题。