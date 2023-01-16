## 1. 简介

在本教程中，我们将看到如何解决一个常见的[Hibernate](https://www.baeldung.com/tag/hibernate/)错误——“No persistence provider for EntityManager”。简单的说，persistence provider就是指在我们的应用中使用的具体的JPA实现，用来将对象持久化到数据库中。

要了解有关 JPA 及其实现的更多信息，可以参考我们关于[JPA、Hibernate 和 EclipseLink 之间的区别的](https://www.baeldung.com/jpa-hibernate-difference)文章。

## 2. 错误原因

当应用程序不知道 应该使用哪个持久性提供者时，我们会看到错误。

当持久性提供程序既未在persistence.xml文件中提及也未在PersistenceUnitInfo实现类中配置时，会发生这种情况。

## 3.修复错误

要修复此错误，我们只需在persistence.xml文件中定义持久性提供程序：

```java
<provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
```

或者，如果我们使用的是Hibernate 4.2 或更早版本：

```java
<provider>org.hibernate.ejb.HibernatePersistence</provider>
```

如果我们在应用程序中实现了PersistenceUnitInfo接口，我们还必须重写
getPersistenceProviderClassName()方法：

```java
@Override
public String getPersistenceProviderClassName() {
    return HibernatePersistenceProvider.class.getName();
}
```

为确保所有必需的 Hibernate jar 都可用，在pom.xml文件中添加[hibernate-core](https://search.maven.org/artifact/org.hibernate/hibernate-core)依赖项很重要：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>${hibernate.version}</version>
</dependency>
```

## 4. 总结

总而言之，我们已经了解了[Hibernate](https://www.baeldung.com/tag/hibernate/)错误“No persistence provider for EntityManager”的可能原因以及解决它的各种方法。