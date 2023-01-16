## 1. 概述

Hibernate 5 为与 Hibernate 实体一起使用提供了两种不同的命名策略：隐式命名策略和物理命名策略。

在本教程中，我们将了解如何配置这些命名策略以将实体映射到自定义的表名和列名。

对于 Hibernate 的新读者，请务必在此处查看我们的[介绍文章](https://www.baeldung.com/hibernate-save-persist-update-merge-saveorupdate)。

## 2.依赖关系

我们将在本教程中使用[基本的 Hibernate Core 依赖项：](https://search.maven.org/search?q=g:org.hibernate AND a:hibernate-core)

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.3.6.Final</version>
</dependency>
```

## 3.隐式命名策略

Hibernate 使用逻辑名将实体名或属性名映射到表名或列名。可以通过两种方式自定义此名称：可以使用 ImplicitNamingStrategy自动派生，也可以使用注解显式定义。

ImplicitNamingStrategy控制 Hibernate 如何从我们的Java类和属性名称中 派生出一个逻辑名称。我们可以从四种内置策略中进行选择，也可以创建自己的策略。

对于此示例，我们将使用默认策略 ImplicitNamingStrategyJpaCompliantImpl。 使用此策略，逻辑名称将与我们的Java类和属性名称相同。

如果我们想要针对特定实体偏离此策略，我们可以使用注解来进行这些自定义。我们可以使用@Table注解来自定义@Entity的名称。对于属性，我们可以使用 @Column注解：

```java
@Entity
@Table(name = "Customers")
public class Customer {

    @Id
    @GeneratedValue
    private Long id;

    private String firstName;

    private String lastName;

    @Column(name = "email")
    private String emailAddress;
    
    // getters and setters
    
}
```

使用此配置，客户实体及其属性的逻辑名称将是：

```plaintext
Customer -> Customers
firstName -> firstName
lastName -> lastName
emailAddress -> email
```

## 4.物理命名策略

现在我们配置了我们的逻辑名称，让我们来看看我们的物理名称。

Hibernate 使用物理命名策略将我们的逻辑名称映射到 SQL 表及其列。

 默认情况下，物理名称将与我们在上一节中指定的逻辑名称相同。如果我们想要自定义物理名称，我们可以创建一个自定义的 PhysicalNamingStrategy类。

例如，我们可能希望在我们的Java代码中使用驼峰命名，但我们希望使用下划线分隔的名称作为我们在数据库中的实际表名和列名。

现在，我们可以结合使用注解和自定义ImplicitNamingStrategy来正确映射这些名称，但是 Hibernate 5 提供了PhysicalNamingStrategy作为简化此过程的方法。它采用我们上一节中的逻辑名称，并允许我们在一个地方自定义它们。

让我们看看这是如何完成的。

首先，我们将创建一个策略，将驼峰命名法转换为使用更标准的 SQL 格式：

```java
public class CustomPhysicalNamingStrategy implements PhysicalNamingStrategy {

    @Override
    public Identifier toPhysicalCatalogName(final Identifier identifier, final JdbcEnvironment jdbcEnv) {
        return convertToSnakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalColumnName(final Identifier identifier, final JdbcEnvironment jdbcEnv) {
        return convertToSnakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalSchemaName(final Identifier identifier, final JdbcEnvironment jdbcEnv) {
        return convertToSnakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalSequenceName(final Identifier identifier, final JdbcEnvironment jdbcEnv) {
        return convertToSnakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalTableName(final Identifier identifier, final JdbcEnvironment jdbcEnv) {
        return convertToSnakeCase(identifier);
    }

    private Identifier convertToSnakeCase(final Identifier identifier) {
        final String regex = "([a-z])([A-Z])";
        final String replacement = "$1_$2";
        final String newName = identifier.getText()
          .replaceAll(regex, replacement)
          .toLowerCase();
        return Identifier.toIdentifier(newName);
    }
}
```

最后，我们可以告诉 Hibernate 使用我们的新策略：

```plaintext
hibernate.physical_naming_strategy=com.baeldung.hibernate.namingstrategy.CustomPhysicalNamingStrategy
```

使用我们针对客户实体的新策略，物理名称将是：

```plaintext
Customer -> customers
firstName -> first_name
lastName -> last_name
emailAddress -> email
```

## 5. 引用标识符

因为 SQL 是一种声明性语言，构成该语言语法的关键字保留供内部使用，在定义数据库标识符(例如，目录、模式、表、列名)时不能使用它们。

### 5.1. 使用双引号手动转义

转义数据库标识符的第一个选项是使用双引号将表名或列名括起来：

```java
@Entity(name = "Table")
@Table(name = ""Table"")
public class Table {
 
    @Id
    @GeneratedValue
    private Long id;
 
    @Column(name = ""catalog"")
    private String catalog;
 
    @Column(name = ""schema"")
    private String schema;
 
    private String name;
 
    //Getters and setters 
} 
```

### 5.2. 使用特定于 Hibernate 的反引号字符手动转义

或者，我们也可以使用反引号字符转义给定的数据库标识符：

```java
@Entity(name = "Table")
@Table(name = "`Table`")
public class Table {
 
    @Id
    @GeneratedValue
    private Long id;
 
    @Column(name = "`catalog`")
    private String catalog;
 
    @Column(name = "`schema`")
    private String schema;
 
    @Column(name = "`name`")
    private String name;
 
    //Getters and setters 
}

```

### 5.3. 使用 Hibernate 配置进行全局转义

另一种选择是将hibernate.globally_quoted_identifiers属性设置为true。这样，Hibernate 将转义所有数据库标识符。因此，我们不必手动引用它们。

为了在CustomPhysicalNamingStrategy 中使用带引号的标识符，我们需要在创建新的标识符对象时显式使用isQuoted()方法：

```java
Identifier.toIdentifier(newName, identifier.isQuoted());
```

## 六. 总结

在这篇简短的文章中，我们了解了隐式命名策略和物理命名策略之间的关系。

我们还看到了如何自定义实体及其属性的隐式名称和物理名称。