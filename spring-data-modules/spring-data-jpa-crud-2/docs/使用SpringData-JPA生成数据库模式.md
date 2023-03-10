## 1. 概述

创建持久层时，我们需要将SQL数据库模式与我们在代码中创建的对象模型相匹配。这可能需要手动完成很多工作。

在本教程中，**我们将学习如何根据代码中的实体模型生成和导出数据库模式**。

首先，我们将介绍用于模式生成的JPA配置属性。然后我们将探讨如何在Spring Data JPA中使用这些属性。

最后，我们将讨论使用Hibernate的原生API生成DDL的替代方法。

## 2. JPA模式生成

**JPA 2.1引入了数据库模式生成标准**。因此，从这个版本开始，我们可以通过一组预定义的配置属性来控制如何生成和导出我们的数据库模式。

### 2.1 Script Action

**为了控制我们将生成哪些DDL命令**，JPA引入了script action配置项：

```properties
javax.persistence.schema-generation.scripts.action
```

我们可以从四种不同的选项中进行选择：

+ none：不生成任何DDL命令
+ create：仅生成数据库创建命令
+ drop：仅生成数据库删除命令
+ drop-and-create：生成数据库删除命令，然后生成创建命令

### 2.2 Script Target

对于每个指定的script action，我们需要定义相应的target配置：

```properties
javax.persistence.schema-generation.scripts.create-target
javax.persistence.schema-generation.scripts.drop-target
```

本质上，**script target定义了包含模式创建或删除命令的文件的位置**。因此，如果我们选择drop-and-create作为script action，则我们需要指定两个target。

### 2.3 模式源

最后，要从我们的实体模型生成模式DDL命令，我们应该包括模式source配置并选择metadata选项：

```properties
javax.persistence.schema-generation.create-source=metadata
javax.persistence.schema-generation.drop-source=metadata
```

在下一节中，我们将了解如何使用Spring Data JPA自动生成具有标准JPA属性的数据库模式。

## 3. 使用Spring Data JPA生成模式

### 3.1 模型类

假设我们正在使用一个名为Account的[实体](https://www.baeldung.com/jpa-entities)来实现一个用户帐户系统：

```java
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "email_address")
    private String emailAddress;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<AccountSettings> accountSettings = new ArrayList<>();

    // getters and setters
}
```

每个帐户可以有多个帐户设置，因此这里我们将有一个[一对多](https://www.baeldung.com/hibernate-one-to-many)映射：

```java
@Entity
@Table(name = "account_settings")
public class AccountSetting {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name", nullable = false)
    private String settingName;

    @Column(name = "value", nullable = false)
    private String settingValue;

    @ManyToOne
    @JoinColumn(name ="account_id", nullable = false)
    private Account account;

    // getters and setters
}
```

### 3.2 Spring Data JPA配置

要生成数据库模式，我们需要将schema-generation属性传递给正在使用的持久层提供程序。为此，我们将在配置文件中的spring.jpa.properties前缀下设置原生JPA属性：

```properties
spring.jpa.properties.javax.persistence.schema-generation.scripts.action=create
spring.jpa.properties.javax.persistence.schema-generation.scripts.create-target=create.sql
spring.jpa.properties.javax.persistence.schema-generation.scripts.create-source=metadata
```

**然后Spring Data JPA在创建EntityManagerFactory bean时将这些属性传递给持久层提供程序**。

### 3.3 create.sql文件

因此，在应用程序启动时，上述配置将根据实体映射元数据生成数据库创建命令。此外，DDL命令被导出到create.sql文件中，该文件在我们的主项目文件夹中创建：

```sql
create table account_settings
(
    id         bigint       not null,
    name       varchar(255) not null,
    value      varchar(255) not null,
    account_id bigint       not null,
    primary key (id)
)

create table accounts
(
    id            bigint       not null,
    email_address varchar(255),
    name          varchar(100) not null,
    primary key (id)
)

alter table account_settings
    add constraint FK54uo82jnot7ye32pyc8dcj2eh
    foreign key (account_id)
    references accounts (id)
```

## 4. Hibernate API生成模式

**如果我们使用Hibernate，我们可以使用它的原生API SchemaExport来生成我们的模式DDL命令**。同样，Hibernate API使用我们的应用程序实体模型来生成和导出数据库模式。

使用Hibernate的SchemaExport，我们可以显式地使用drop、createOnly和create方法：

```java
MetadataSources metadataSources = new MetadataSources(serviceRegistry);
metadataSources.addAnnotatedClass(Account.class);
metadataSources.addAnnotatedClass(AccountSettings.class);
Metadata metadata = metadataSources.buildMetadata();

SchemaExport schemaExport = new SchemaExport();
schemaExport.setFormat(true);
schemaExport.setOutputFile("create.sql");
schemaExport.createOnly(EnumSet.of(TargetType.SCRIPT), metadata);
```

当我们运行这段代码时，我们的数据库创建命令被导出到主项目文件夹中的create.sql文件中。

SchemaExport是[Hibernate Bootstrapping API](https://www.baeldung.com/hibernate-5-bootstrapping-api)的一部分。

## 5. 模式生成方式

尽管模式生成可以在开发过程中节省我们的时间，但我们应该只将它用于基本场景。

例如，我们可以使用它来快速启动开发或测试数据库。

相比之下，对于数据库迁移等更复杂的场景，我们应该使用更精细的工具，比如[Liquibase](https://www.baeldung.com/liquibase-refactor-schema-of-java-app)或[Flyway](https://www.baeldung.com/database-migrations-with-flyway)。

## 6. 总结

在本文中，我们学习了如何在JPA schema-generation属性的帮助下生成和导出数据库模式。然后我们讨论了如何使用Hibernate的原生API SchemaExport来实现相同的效果。