## 一、简介

Kotlin 的一个特点是[与 Java](https://www.baeldung.com/kotlin-java-interoperability)库的互操作性，而 JPA 无疑是其中之一。

在本教程中，我们将探讨如何将 Kotlin 类用作 JPA 实体。

## 2.依赖关系

为了简单起见，我们将使用 Hibernate 作为我们的 JPA 实现；我们需要将以下依赖项添加到我们的 Maven 项目中：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.2.15.Final</version>
</dependency>
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-testing</artifactId>
    <version>5.2.15.Final</version>
    <scope>test</scope>
</dependency>
```

我们还将使用 H2 嵌入式数据库来运行我们的测试：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.196</version>
    <scope>test</scope>
</dependency>
```

对于 Kotlin，我们将使用以下内容：

```xml
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-stdlib-jdk8</artifactId>
    <version>1.2.30</version>
</dependency>
```

当然，可以在 Maven Central 中找到最新版本的[Hibernate](https://search.maven.org/classic/#search|gav|1|g%3A"org.hibernate" AND a%3A"hibernate-core")、[H2](https://search.maven.org/classic/#search|gav|1|g%3A"com.h2database" AND a%3A"h2")和[Kotlin 。](https://search.maven.org/classic/#search|gav|1|g%3A"org.jetbrains.kotlin" AND a%3A"kotlin-stdlib-jdk8")

## 3.编译器插件(jpa-plugin)

要使用 JPA，实体类需要一个不带参数的构造函数。

默认情况下，Kotlin 类没有它，要生成它们，我们需要使用[jpa-plugin](https://kotlinlang.org/docs/reference/compiler-plugins.html#jpa-support)：

```xml
<plugin>
    <artifactId>kotlin-maven-plugin</artifactId>
    <groupId>org.jetbrains.kotlin</groupId>
    <version>1.2.30</version>
    <configuration>
        <compilerPlugins>
        <plugin>jpa</plugin>
        </compilerPlugins>
        <jvmTarget>1.8</jvmTarget>
    </configuration>
    <dependencies>
        <dependency>
        <groupId>org.jetbrains.kotlin</groupId>
        <artifactId>kotlin-maven-noarg</artifactId>
        <version>1.2.30</version>
        </dependency>
    </dependencies>
    <!--...-->
</plugin>
```

## 4. 带有 Kotlin 类的 JPA

完成前面的设置后，我们就可以使用带有简单类的 JPA。

让我们开始创建一个具有两个属性的Person类—— name和id，如下所示：

```java
@Entity
class Person(
    @Column(nullable = false)
    val name: String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int?=null,
)
```

如我们所见，我们可以自由使用来自 JPA 的注解，如@Entity、@Column和@Id。

注意：确保将 id 属性放在最后，因为它是可选的并且是自动生成的。

要查看我们的实体的运行情况，我们将创建以下测试：

```java
@Test
fun givenPerson_whenSaved_thenFound() {
    doInHibernate(({ this.sessionFactory() }), { session ->
        val personToSave = Person("John")
        session.persist(personToSave)
        val personFound = session.find(Person::class.java, personToSave.id)
        session.refresh(personFound)

        assertTrue(personToSave.name == personFound.name)
    })
}
```

在启用日志记录的情况下运行测试后，我们可以看到以下结果：

```bash
Hibernate: insert into Person (id, name) values (null, ?)
Hibernate: select person0_.id as id1_0_0_, person0_.name as name2_0_0_ from Person person0_ where person0_.id=?
```

这表明一切进展顺利。

重要的是要注意，如果我们不在运行时使用jpa-plugin ，由于缺少默认构造函数，我们将得到一个InstantiationException ：

```bash
javax.persistence.PersistenceException: org.hibernate.InstantiationException: No default constructor for entity: : com.baeldung.entity.Person
```

现在，我们将再次使用空值进行测试。为此，让我们使用新属性电子邮件和@OneToMany关系扩展我们的Person实体：

```java
    //...
    @Column(nullable = true)
    val email: String? = null,

    @Column(nullable = true)
    @OneToMany(cascade = [CascadeType.ALL])
    val phoneNumbers: List<PhoneNumber>? = null
```

我们还可以看到email和phoneNumbers字段可以为空，因此用问号声明。

PhoneNumber实体有两个属性——name和id：

```java
@Entity
class PhoneNumber(   
    @Column(nullable = false)
    val number: String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int?=null,
)
```

让我们通过测试来验证这一点：

```java
@Test
fun givenPersonWithNullFields_whenSaved_thenFound() {
    doInHibernate(({ this.sessionFactory() }), { session ->
        val personToSave = Person("John", null, null)
        session.persist(personToSave)
        val personFound = session.find(Person::class.java, personToSave.id)
        session.refresh(personFound)

        assertTrue(personToSave.name == personFound.name)
    })
}
```

这一次，我们将得到一个插入语句：

```bash
Hibernate: insert into Person (id, email, name) values (null, ?, ?)
Hibernate: select person0_.id as id1_0_1_, person0_.email as email2_0_1_, person0_.name as name3_0_1_, phonenumbe1_.Person_id as Person_i1_1_3_, phonenumbe2_.id as phoneNum2_1_3_, phonenumbe2_.id as id1_2_0_, phonenumbe2_.number as number2_2_0_ from Person person0_ left outer join Person_PhoneNumber phonenumbe1_ on person0_.id=phonenumbe1_.Person_id left outer join PhoneNumber phonenumbe2_ on phonenumbe1_.phoneNumbers_id=phonenumbe2_.id where person0_.id=?
```

让我们再测试一次但没有空数据来验证输出：

```java
@Test
fun givenPersonWithFullData_whenSaved_thenFound() {
    doInHibernate(({ this.sessionFactory() }), { session ->
        val personToSave = Person(          
          "John", 
          "jhon@test.com", 
          Arrays.asList(PhoneNumber("202-555-0171"), PhoneNumber("202-555-0102")))
        session.persist(personToSave)
        val personFound = session.find(Person::class.java, personToSave.id)
        session.refresh(personFound)

        assertTrue(personToSave.name == personFound.name)
    })
}
```

而且，正如我们所见，现在我们得到了三个插入语句：

```bash
Hibernate: insert into Person (id, email, name) values (null, ?, ?)
Hibernate: insert into PhoneNumber (id, number) values (null, ?)
Hibernate: insert into PhoneNumber (id, number) values (null, ?)
```

## 5. 数据类

[Kotlin 数据类](https://www.baeldung.com/kotlin/data-classes)是具有额外功能的普通类，使它们适合作为数据持有者。在这些额外的函数中，有equals、hashCode和toString方法的默认实现。

自然地，我们可能会争辩说我们可以使用 Kotlin 数据类作为 JPA 实体。与这里自然而然的相反，通常不鼓励使用数据类作为 JPA 实体。这主要是因为 JPA 世界与 Kotlin 编译器为每个数据类提供的那些默认实现之间的复杂交互。

为了演示，我们将使用这个实体：

```kotlin
@Entity
data class Address(    
    val name: String,
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val phoneNumbers: List<PhoneNumber>,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,
)
```

### 5.1. equals和hashCode方法_

让我们从equals和hashCode实现开始。大多数 JPA 实体至少包含一个生成的值——例如，自动生成的标识符。这意味着某些属性只有在我们将它们持久化到数据库中后才会生成。

所以，持久化前后计算出的equals和hashCode是不一样的，因为equals和hashCode计算中用到的一些属性是在持久化之后产生的。因此，在将数据类 JPA 实体与基于散列的集合一起使用时，我们应该小心：

```kotlin
@Test
fun givenAddressWithDefaultEquals_whenAddedToSet_thenNotFound() {
    doInHibernate({ sessionFactory() }) { session ->
        val addresses = mutableSetOf<Address>()
        val address = Address(name = "Berlin", phones = listOf(PhoneNumber("42")))
        addresses.add(address)

        assertTrue(address in addresses)
        session.persist(address)
        assertFalse { address in addresses }
    }
 }
```

在上面的示例中，即使我们将地址添加到集合中，但是将其持久化到数据库中后，我们无法在集合中找到它。发生这种情况是因为在我们持久化地址后哈希码值发生了变化。

除此之外，简单的equals和hashCode实现[不足以在 JPA 实体中使用](https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/)。

### 5.2. 不需要的惰性关联获取

Kotlin 编译器根据数据类的所有属性生成默认方法实现。当我们为 JPA 实体使用数据类时，其中一些属性可能是与目标实体的惰性关联。

考虑到这一切，有时对toString、equals或hashCode的无害调用可能会发出更多查询来加载惰性关联。这可能会影响性能，尤其是当我们甚至不需要获取这些关联时：

```kotlin
@Test
fun givenAddress_whenLogging_thenFetchesLazyAssociations() {
    doInHibernate({ this.sessionFactory() }) { session ->
        val addressToSave = Address(name = "Berlin", phoneNumbers = listOf(PhoneNumber("42")))
        session.persist(addressToSave)
        session.clear()

        val addressFound = session.find(Address::class.java, addressToSave.id)
            
        assertFalse { Hibernate.isInitialized(addressFound.phoneNumbers) }
        logger.info("found the entity {}", addressFound) // initializes the lazy collection
        assertTrue(Hibernate.isInitialized(addressFound.phoneNumbers))
    }
}
```

在上面的例子中，一个看似无辜的日志语句触发了一个额外的查询：

```sql
Hibernate: select  from Address address0_ where address0_.id=?
Hibernate: select  from Address_PhoneNumber phonenumbe0_ inner join PhoneNumber phonenumbe1_ on phonenumbe0_.phoneNumbers_id=phonenumbe1_.id where phonenumbe0_.Address_id=?
```

## 六，总结

在这篇简短的文章中，我们看到了如何使用 jpa-plugin 和 Hibernate 将 Kotlin 类与 JPA 集成的示例。此外，我们还看到了为什么在将数据类用作 JPA 实体时应该谨慎。