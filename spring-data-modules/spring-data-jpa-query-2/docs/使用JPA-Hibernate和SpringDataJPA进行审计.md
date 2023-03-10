## 1. 概述

在ORM的上下文中，数据库审计意味着跟踪和记录与持久实体相关的事件，或者只是实体版本控制。受SQL触发器的启发，事件是对实体的插入、更新和删除操作。数据库审计的好处类似于源代码版本控制提供的好处。

在本教程中，我们将演示三种将审计引入应用程序的方法。首先，我们将使用标准JPA来实现它。接下来，我们将了解两个提供自己审计功能的JPA扩展，一个由Hibernate提供，另一个由Spring Data提供。

下面是我们将在本例中使用的示例相关实体Bar和Foo：

<img src="../assets/img.png">

## 2. 使用JPA进行审计

JPA没有明确包含审计API，但我们可以通过使用实体生命周期事件来实现此功能。

### 2.1 @PrePersist、@PreUpdate和@PreRemove

在JPA实体类中，我们可以指定一个方法作为回调，我们可以在特定的实体生命周期事件期间调用该方法。由于我们对在相应的DML操作之前执行的回调感兴趣，@PrePersist、 @ PreUpdate和@PreRemove回调注解可用于我们的目的：

```java
@Entity
public class Bar {

    @PrePersist
    public void onPrePersist() {
        // ...
    }

    @PreUpdate
    public void onPreUpdate() {
        // ...
    }

    @PreRemove
    public void onPreRemove() {
        // ...
    }
}
```

内部回调方法应始终返回void，并且不带任何参数。他们可以使用任何名称和任何访问级别，但不应该是static。

请注意JPA中的@Version注解与我们的主题并不严格相关；与审计数据相比，它更多地与乐观锁定有关。

### 2.2 实现回调方法

但是这种方法有一个很大的限制。正如JPA 2规范(JSR317)中所述：

>   通常，可移植应用程序的生命周期方法不应调用EntityManager或Query操作、访问其他实体实例或修改同一持久性上下文中的关系。生命周期回调方法可以修改调用它的实体的非关系状态。

在没有审计框架的情况下，我们必须手动维护数据库模式和域模型。对于我们的简单用例，让我们向实体添加两个新属性，因为我们只能管理“实体的非关系状态”。operation属性将存储执行的操作的名称，timestamp属性用于操作的时间戳：

```java
@Entity
public class Bar {

    //...

    @Column(name = "operation")
    private String operation;

    @Column(name = "timestamp")
    private long timestamp;

    //...

    // standard setters and getters for the new properties

    //...

    @PrePersist
    public void onPrePersist() {
        audit("INSERT");
    }

    @PreUpdate
    public void onPreUpdate() {
        audit("UPDATE");
    }

    @PreRemove
    public void onPreRemove() {
        audit("DELETE");
    }

    private void audit(String operation) {
        setOperation(operation);
        setTimestamp((new Date()).getTime());
    }
}
```

如果我们需要为多个类添加这样的审计，我们可以使用@EntityListeners来集中管理代码：

```java
@EntityListeners(AuditListener.class)
@Entity
public class Bar { 
    // ...
}
```

```java
public class AuditListener {

    @PrePersist
    @PreUpdate
    @PreRemove
    private void beforeAnyOperation(Object object) { 
        // ...
    }
}
```

## 3. Hibernate Envers

使用Hibernate，我们可以利用拦截器和事件监听器以及数据库触发器来完成审计。但是ORM框架提供了Envers，这是一个实现持久类审计和版本控制的模块。

### 3.1 使用Envers

要设置Envers，我们需要将hibernate-envers依赖添加到我们的POM中：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-envers</artifactId>
    <version>${hibernate.version}</version>
</dependency>
```

然后我们在@Entity(审计整个实体)或特定的@Columns(如果我们只需要审计特定的属性)上添加@Audited注解：

```java
@Entity
@Audited
public class Bar { 
    // ...
}
```

请注意，Bar与Foo之间存在一对多关系。在这种情况下，我们也需要通过在Foo上添加@Audited来审计Foo，或者在Bar中的关系属性上设置@NotAudited：

```java
@OneToMany(mappedBy = "bar")
@NotAudited
private Set<Foo> fooSet;
```

### 3.2 创建审计日志表

有几种方法可以创建审计表：

-   将hibernate.hbm2ddl.auto设置为create、create-drop或update，以便Envers可以自动创建它们
-   使用org.hibernate.tool.EnversSchemaGenerator以编程方式导出完整的数据库模式
-   设置Ant任务以生成适当的DDL语句
-   使用Maven插件从我们的映射(例如Juplo)生成数据库模式以导出Envers模式(适用于Hibernate 4及更高版本)

我们将采用第一条路线，因为它是最直接的，但请注意，使用hibernate.hbm2ddl.auto在生产中并不安全。

在我们的例子中，应该自动生成bar_AUD和foo_AUD(如果我们也将Foo设置为@Audited)表。审计表从实体表中复制所有已审计字段，其中包含两个字段REVTYPE(值为：“0”表示添加，“1”表示更新，“2”表示删除实体)和REV。

除此之外，默认情况下还会生成一个名为REVINFO的额外表。它包括两个重要字段REV和REVTSTMP，记录了每次修订的时间戳。我们可以猜到，bar_AUD.REV和foo_AUD.REV实际上是REVINFO.REV的外键。

### 3.3 配置Envers

我们可以像配置任何其他Hibernate属性一样配置Envers属性。

例如，下面我们将审计表后缀(默认为“\_AUD”)更改为“_AUDIT_LOG”。以下是我们如何设置相应属性org.hibernate.envers.audit_table_suffix的值：

```java
Properties hibernateProperties = new Properties(); 
hibernateProperties.setProperty("org.hibernate.envers.audit_table_suffix", "_AUDIT_LOG"); 
sessionFactory.setHibernateProperties(hibernateProperties);
```

可以在[Envers文档](http://docs.jboss.org/envers/docs/#configuration)中找到可用属性的完整列表。

### 3.4 访问实体历史

我们可以以类似于通过Hibernate Criteria API查询数据的方式来查询历史数据。我们可以使用AuditReader接口访问实体的审计历史记录，我们可以通过AuditReaderFactory使用打开的EntityManager或Session获取该接口：

```java
AuditReader reader = AuditReaderFactory.get(session);
```

Envers提供AuditQueryCreator(由AuditReader.createQuery()返回)以创建特定于审计的查询。以下代码行将返回在修订版#2中修改的所有Bar实例(其中bar_AUDIT_LOG.REV = 2)：

```java
AuditQuery query = reader.createQuery().forEntitiesAtRevision(Bar.class, 2)
```

以下是我们如何查询Bar的修订版，这将导致获得所有状态下所有已审计Bar实例的列表：

```java
AuditQuery query = reader.createQuery().forRevisionsOfEntity(Bar.class, true, true);
```

如果第二个参数为false，则结果与REVINFO表连接。否则，只返回实体实例。最后一个参数指定是否返回已删除的Bar实例。

然后我们可以使用AuditEntity工厂类指定约束：

```java
query.addOrder(AuditEntity.revisionNumber().desc());
```

## 4. Spring Data JPA

Spring Data JPA是一个框架，它通过在JPA提供程序的顶部添加一个额外的抽象层来扩展JPA。该层支持通过扩展Spring JPA Repository接口来创建JPA Repository。

出于我们的目的，我们可以扩展CrudRepository<T, ID extends Serializable>，这是通用CRUD操作的接口。一旦我们创建了Repository并将其注入到另一个组件，Spring Data将自动提供实现，我们就可以添加审计功能了。

### 4.1 启用JPA审计

首先，我们要通过注解配置启用审计。为此，我们在@Configuration类上添加@EnableJpaAuditing：

```java
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories
@EnableJpaAuditing
public class PersistenceConfig {
    // ...
}
```

### 4.2 添加Spring的实体回调监听器

正如我们已经知道的，JPA提供了@EntityListeners注解来指定回调监听器类。Spring Data提供了自己的JPA实体监听器类AuditingEntityListener。因此，让我们为Bar实体指定监听器：

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Bar {
    // ...
}
```

现在我们可以在持久化和更新Bar实体时通过监听器捕获审计信息。

### 4.3 跟踪创建和最后修改日期

接下来，我们将添加两个新属性，用于将创建日期和上次修改日期存储到我们的Bar实体中。这些属性由@CreatedDate和@LastModifiedDate注解相应地进行标注，并且它们的值是自动设置的：

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Bar {

    //...

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    private long createdDate;

    @Column(name = "modified_date")
    @LastModifiedDate
    private long modifiedDate;

    //...
}
```

通常，我们会将属性移动到基类(由@MappedSuperClass标注)，我们所有审计的实体都将扩展该基类。在我们的示例中，为了简单起见，我们将它们直接添加到Bar中。

### 4.4 使用Spring Security审计更改的作者

如果我们的应用程序使用Spring Security，我们可以跟踪更改的时间和更改者：

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Bar {

    //...

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @Column(name = "modified_by")
    @LastModifiedBy
    private String modifiedBy;

    //...
}
```

用@CreatedBy和@LastModifiedBy标注的列填充有创建或上次修改实体的主体的名称。该信息来自SecurityContext的Authentication实例。如果我们想要自定义设置为带注解字段的值，我们可以实现AuditorAware<T\>接口：

```java
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public String getCurrentAuditor() {
        // your custom logic
    }
}
```

为了将应用程序配置为使用AuditorAwareImpl来查找当前主体，我们声明了一个AuditorAware类型的bean，使用AuditorAwareImpl实例初始化，并将bean的名称指定为@EnableJpaAuditing中的auditorAwareRef参数值：

```java
@EnableJpaAuditing(auditorAwareRef="auditorProvider")
public class PersistenceConfig {

    //...

    @Bean
    AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    //...
}
```

## 5. 总结

在本文中，我们考虑了三种实现审计功能的方法：

-   纯JPA方法是最基本的，包括使用生命周期回调。但是，我们只能修改实体的非关系状态。这使得@PreRemove回调对我们的目的毫无用处，因为我们在该方法中所做的任何设置都将与实体一起被删除。
-   Envers是Hibernate提供的一个成熟的审计模块。它是高度可配置的，并且没有纯JPA实现的缺陷。因此，它允许我们审计删除操作，因为它记录到实体表以外的表中。
-   Spring Data JPA方法抽象了JPA回调的使用，并为审计属性提供了方便的注解。它还可以与Spring Security集成。缺点是继承了JPA方式的相同缺陷，因此无法对删除操作进行审计。