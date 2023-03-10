## 1. 概述

默认情况下，Spring Data使用Hibernate作为默认的JPA实现提供程序。

然而，Hibernate肯定不是我们唯一可用的JPA实现。

在本文中，我们将通过必要的步骤将[EclipseLink](http://www.eclipse.org/eclipselink/)设置为Spring Data JPA的实现提供程序。

## 2. Maven依赖

要在我们的Spring应用程序中使用它，我们只需要在项目的pom.xml中添加[org.eclipse.persistence.jpa](https://central.sonatype.com/artifact/org.eclipse.persistence/org.eclipse.persistence.jpa/4.0.1)依赖项：

```xml
<dependency>
    <groupId>org.eclipse.persistence</groupId>
    <artifactId>org.eclipse.persistence.jpa</artifactId>
    <version>3.0.0</version>
</dependency>
```

**默认情况下，spring-boot-starter-data-jpa依赖带有Hibernate实现**。

由于我们想使用EclipseLink作为JPA提供程序，因此我们不再需要它。

因此，我们可以通过排除其依赖项来将其从项目中删除：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>${spring-boot.version}</version>
    <exclusions>
        <exclusion>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-entitymanager</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-core</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

下一步是告诉Spring框架我们想要使用EclipseLink作为JPA实现。

## 3. Spring配置

**JpaBaseConfiguration是一个抽象类，它为Spring Boot中的JPA定义了bean**。要自定义它，我们必须实现一些方法，例如createJpaVendorAdapter()或getVendorProperties()。

Spring为Hibernate提供了一个开箱即用的配置实现，称为HibernateJpaAutoConfiguration。但是，对于EclipseLink，我们必须创建自定义配置。

首先，我们需要实现createJpaVendorAdapter()方法，该方法指定要使用的JPA实现。

**Spring为EclipseLink提供了一个名为EclipseLinkJpaVendorAdapter的AbstractJpaVendorAdapter实现**，我们将在我们的方法中使用它：

```java
@Configuration
public class JpaConfiguration extends JpaBaseConfiguration {

    @Override
    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
        return new EclipseLinkJpaVendorAdapter();
    }

    // ...
}
```

此外，我们必须**定义一些将由EclipseLink使用的特定于实现的属性**。

我们可以通过getVendorProperties()方法添加这些：

```java
@Override
protected Map<String, Object> getVendorProperties() {
    HashMap<String, Object> map = new HashMap<>();
    map.put(PersistenceUnitProperties.WEAVING, detectWeavingMode());
    map.put(PersistenceUnitProperties.DDL_GENERATION, "drop-and-create-tables");
    return map;
}
```

org.eclipse.persistence.config.PersistenceUnitProperties类包含我们可以为EclipseLink定义的属性。

在此示例中，我们已经指定我们要在应用程序运行时使用WEAVING并重新创建数据库模式。

**就是这样**！这是从默认的Hibernate JPA提供程序更改为EclipseLink所需的全部实现。

请注意，Spring Data使用JPA API，而不是任何实现特定的方法。因此，从理论上讲，从一个实现切换到另一个实现不应该出现任何问题。

## 4. 总结

在本教程中，我们介绍了如何更改Spring Data使用的默认JPA实现提供程序。