## 1. 概述

在[介绍Spring Data Couchbase](https://www.baeldung.com/spring-data-couchbase)之后，在第二篇教程中，我们重点介绍了对Couchbase文档数据库的实体验证(JSR-303)、乐观锁定和不同级别的查询一致性的支持。

## 2. 实体验证

Spring Data Couchbase提供对JSR-303实体验证注解的支持。为了利用此功能，首先我们将JSR-303库添加到Maven项目的依赖项部分：

```xml
<dependency>
    <groupId>javax.validation</groupId>
    <artifactId>validation-api</artifactId>
    <version>1.1.0.Final</version>
</dependency>
```

然后我们添加一个JSR-303的实现。我们将使用Hibernate实现：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>5.2.4.Final</version>
</dependency>
```

最后，我们将验证器工厂bean和相应的Couchbase事件监听器添加到我们的Couchbase配置中：

```java
@Bean
public LocalValidatorFactoryBean localValidatorFactoryBean() {
    return new LocalValidatorFactoryBean();
}

@Bean
public ValidatingCouchbaseEventListener validatingCouchbaseEventListener() {
    return new ValidatingCouchbaseEventListener(localValidatorFactoryBean());
}
```

等效的XML配置如下所示：

```xml
<bean id="validator" class="org.springframework.validation.beanvalidation.LocalValidatorFactoryBean"/>

<bean id="validatingEventListener" class="org.springframework.data.couchbase.core.mapping.event.ValidatingCouchbaseEventListener"/>
```

现在我们将JSR-303注解添加到我们的实体类中。当在持久化操作期间遇到约束冲突时，操作将失败，并抛出ConstraintViolationException。

以下是我们可以强制执行的涉及学生实体的约束示例：

```java
@Field
@NotNull
@Size(min=1, max=20)
@Pattern(regexp="^[a-zA-Z .'-]+$")
private String firstName;

// ...
@Field
@Past
private DateTime dateOfBirth;
```

## 3. 乐观锁

Spring Data Couchbase不支持类似于你可以在其他Spring Data模块(例如Spring Data JPA)中实现的多文档事务(通过@Transactional注解)，也不提供回滚功能。

但是，它通过使用@Version注解以与其他Spring Data模块大致相同的方式支持乐观锁定：

```java
@Version
private long version;
```

在幕后，Couchbase使用所谓的“比较和交换”(CAS)机制在数据存储级别实现乐观锁定。

Couchbase中的每个文档都有一个关联的CAS值，每当文档的元数据或内容发生更改时，该值就会自动修改。每当从Couchbase检索文档时，在字段上使用@Version注解会导致该字段填充当前CAS值。

当你尝试将文档保存回Couchbase时，会根据Couchbase中的当前CAS值检查此字段。如果值不匹配，持久化操作将失败并抛出OptimisticLockingException。

**请务必注意，永远不要尝试在代码中访问或修改此字段**。

## 4. 查询一致性

在Couchbase上实现持久层时，你必须考虑过时读写的可能性。这是因为当插入、更新或删除文档时，可能需要一些时间才能更新后备视图和索引以反映这些更改。

如果你有一个由Couchbase节点集群支持的大型数据集，这可能会成为一个严重的问题，尤其是对于OLTP系统。

Spring Data为某些Repository和模板操作提供了强大的一致性级别，以及一些选项，可让你确定应用程序可接受的读写一致性级别。

### 4.1 一致性级别

Spring Data允许你通过org.springframework.data.couchbase.core.query包中的Consistency枚举为你的应用程序指定各种级别的查询一致性和过期性。

此枚举定义了以下级别的查询一致性和过期性，从最低到最严格：

- EVENTUALLY_CONSISTENT
  - 允许过时读取
  - 索引根据Couchbase标准算法更新
- UPDATE_AFTER
  - 允许过时读取
  - 索引在每次请求后更新
- DEFAULT_CONSISTENCY(与READ_YOUR_OWN_WRITES相同)
- READ_YOUR_OWN_WRITES
  - 不允许过时读取
  - 索引在每次请求后更新
- STRONGLY_CONSISTENT
  - 不允许过时读取
  - 索引在每个语句后更新

### 4.2 默认行为

考虑这样一种情况，你的文档已从Couchbase中删除，并且后备视图和索引尚未完全更新。

CouchbaseRepository内置方法deleteAll()安全地忽略后备视图找到但视图尚未反映其删除的文档。

同样，CouchbaseTemplate内置方法findByView和findBySpatialView通过不返回最初由后备视图找到但后来被删除的文档来提供类似级别的一致性。

对于所有其他模板方法、内置Repository方法和派生Repository查询方法，根据撰写本文时的官方Spring Data Couchbase 2.1.x文档，Spring Data使用默认的一致性级别Consistency.READ_YOUR_OWN_WRITES。

值得注意的是，该库的早期版本使用默认值Consistency.UPDATE_AFTER。

无论你使用哪个版本，如果你对盲目接受所提供的默认一致性级别有任何保留，Spring提供了两种方法，你可以通过它们以声明方式控制所使用的一致性级别，如以下小节所述。

### 4.3 全局一致性设置

如果你正在使用Couchbase Repository并且你的应用程序需要更高级别的一致性，或者如果它可以容忍更弱的级别，那么你可以通过覆盖Couchbase配置中的getDefaultConsistency()方法来覆盖所有Repository的默认一致性设置。

以下是如何在Couchbase配置类中覆盖全局一致性级别：

```java
@Override
public Consistency getDefaultConsistency() {
    return Consistency.STRONGLY_CONSISTENT;
}
```

这是等效的XML配置：

```xml
<couchbase:template consistency="STRONGLY_CONSISTENT"/>
```

请注意，更严格的一致性级别的代价是查询时的延迟增加，因此请务必根据你的应用程序的需要定制此设置。

例如，通常仅批量附加或更新数据的数据仓库或报告应用程序将是EVENTUALLY_CONSISTENT的良好候选者，而OLTP应用程序可能应该倾向于更严格的级别，例如READ_YOUR_OWN_WRITES或STRONGLY_CONSISTENT。

### 4.4 自定义一致性实现

如果你需要更精细调整的一致性设置，你可以通过为你想要独立控制其一致性级别的任何查询提供你自己的Repository实现并使用queryView和/或CouchbaseTemplate提供的queryN1QL方法。

让我们为我们不想允许过时读取的学生实体实现一个名为findByFirstNameStartsWith的自定义Repository方法。

首先，创建一个包含自定义方法声明的接口：

```java
public interface CustomStudentRepository {
    List<Student> findByFirstNameStartsWith(String s);
}
```

接下来，实现接口，将底层Couchbase Java SDK中的Stale设置设置为所需级别：

```java
public class CustomStudentRepositoryImpl implements CustomStudentRepository {

    @Autowired
    private CouchbaseTemplate template;

    public List<Student> findByFirstNameStartsWith(String s) {
        return template.findByView(ViewQuery.from("student", "byFirstName")
                    .startKey(s)
                    .stale(Stale.FALSE),
              Student.class);
    }
}
```

最后，通过让你的标准Repository接口扩展通用CrudRepository接口和你的自定义Repository接口，客户端将可以访问你的标准Repository接口的所有内置和派生方法，以及你在自定义Repository类中实现的任何自定义方法:

```java
public interface StudentRepository extends CrudRepository<Student, String>, CustomStudentRepository {
    // ...
}
```

## 5. 总结

在本教程中，我们展示了如何在使用Spring Data Couchbase社区项目时实现JSR-303实体验证并实现乐观锁定功能。

我们还讨论了理解Couchbase中查询一致性的必要性，并介绍了Spring Data Couchbase提供的不同级别的一致性。

最后，我们解释了Spring Data Couchbase在全局和一些特定方法中使用的默认一致性级别，我们演示了覆盖全局默认一致性设置的方法以及如何通过提供逐个查询来覆盖一致性设置你自己的自定义Repository实现。

要了解有关Spring Data Couchbase的更多信息，请访问官方[Spring Data Couchbase](https://projects.spring.io/spring-data-couchbase/)项目站点。