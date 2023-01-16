## 1. 概述

Querydsl 是一个广泛的Java框架，它有助于以类似于 SQL 的领域特定语言创建和运行类型安全的查询。

在本文中，我们将使用JavaPersistence API 探索 Querydsl。

这里需要注意的是 HQL for Hibernate 是 Querydsl 的第一个目标语言，但现在它支持 JPA、JDO、JDBC、Lucene、Hibernate Search、MongoDB、Collections 和 RDFBean 作为后端。

## 二、准备工作

让我们首先将必要的依赖项添加到我们的 Maven 项目中：

```xml
<properties>
    <querydsl.version>2.5.0</querydsl.version>
</properties>

<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-apt</artifactId>
    <version>${querydsl.version}</version>
    <scope>provided</scope>
</dependency>

<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-jpa</artifactId>
    <version>${querydsl.version}</version>
</dependency>

<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-log4j12</artifactId>
    <version>1.6.1</version>
</dependency>
```

现在让我们配置 Maven APT 插件：

```xml
<project>
    <build>
    <plugins>
    ...
    <plugin>
        <groupId>com.mysema.maven</groupId>
        <artifactId>apt-maven-plugin</artifactId>
        <version>1.1.3</version>
        <executions>
        <execution>
            <goals>
                <goal>process</goal>
            </goals>
            <configuration>
                <outputDirectory>target/generated-sources</outputDirectory>
                <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
            </configuration>
        </execution>
        </executions>
    </plugin>
    ...
    </plugins>
    </build>
</project>
```

JPAAnnotationProcessor将找到用javax.persistence.Entity注解注解的域类型并为它们生成查询类型。

## 3. 使用 Querydsl 查询

查询是根据生成的反映域类型属性的查询类型构建的。函数/方法调用也是以完全类型安全的方式构建的。

查询路径和操作在所有实现中都是相同的，而且查询接口有一个公共的基础接口。

### 3.1. 实体和 Querydsl 查询类型

让我们首先定义一个简单的实体，我们将在通过示例时使用它：

```java
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String firstname;

    @Column
    private String surname;
    
    Person() {
    }

    public Person(String firstname, String surname) {
        this.firstname = firstname;
        this.surname = surname;
    }

    // standard getters and setters

}
```

Querydsl 将在与Person相同的包中生成一个简单名称为QPerson的查询类型。QPerson可以用作 Querydsl 查询中的静态类型变量，作为Person类型的代表。

首先——QPerson有一个默认实例变量，可以作为静态字段访问：

```java
QPerson person = QPerson.person;
```

或者，你可以像这样定义自己的Person变量：

```java
QPerson person = new QPerson("Erich", "Gamma");
```

### 3.2. 使用JPAQuery构建查询

我们现在可以使用JPAQuery实例进行查询：

```java
JPAQuery query = new JPAQuery(entityManager);
```

请注意，entityManager是 JPA EntityManager。

现在让我们检索所有名字为“ Kent ”的人作为一个简单的例子：

```java
QPerson person = QPerson.person;
List<Person> persons = query.from(person).where(person.firstName.eq("Kent")).list(person);
```

from调用定义查询源和投影，where部分定义过滤器，list告诉 Querydsl 返回所有匹配的元素。

我们还可以使用多个过滤器：

```java
query.from(person).where(person.firstName.eq("Kent"), person.surname.eq("Beck"));
```

或者：

```java
query.from(person).where(person.firstName.eq("Kent").and(person.surname.eq("Beck")));
```

在本机 JPQL 形式中，查询将像这样编写：

```sql
select person from Person as person where person.firstName = "Kent" and person.surname = "Beck"
```

如果你想通过“或”组合过滤器，请使用以下模式：

```java
query.from(person).where(person.firstName.eq("Kent").or(person.surname.eq("Beck")));
```

## 4. Querydsl中的排序和聚合

现在让我们看一下 Querydsl 库中的排序和聚合是如何工作的。

### 4.1. 订购

我们将从按姓氏字段降序排列我们的结果开始：

```java
QPerson person = QPerson.person;
List<Person> persons = query.from(person)
    .where(person.firstname.eq(firstname))
    .orderBy(person.surname.desc())
    .list(person);
```

### 4.2. 聚合

现在让我们使用一个简单的聚合，因为我们确实有一些可用的聚合(Sum、Avg、Max、Min)：

```java
QPerson person = QPerson.person;    
int maxAge = query.from(person).list(person.age.max()).get(0);
```

### 4.3. 使用GroupBy聚合 

com.mysema.query.group.GroupBy类提供聚合功能，我们可以使用它来聚合内存中的查询结果。

这是一个简单的示例，其中结果作为Map返回，其中firstname作为键，max age作为值：

```java
QPerson person = QPerson.person;   
Map<String, Integer> results = 
  query.from(person).transform(
      GroupBy.groupBy(person.firstname).as(GroupBy.max(person.age)));
```

## 5. 使用 Querydsl 进行测试

现在，让我们使用 Querydsl 定义一个 DAO 实现——并定义以下搜索操作：

```java
public List<Person> findPersonsByFirstnameQuerydsl(String firstname) {
    JPAQuery query = new JPAQuery(em);
    QPerson person = QPerson.person;
    return query.from(person).where(person.firstname.eq(firstname)).list(person);
}
```

现在让我们使用这个新的 DAO 构建一些测试，让我们使用 Querydsl 来搜索新创建的Person对象(在PersonDao类中实现)，并在另一个使用GroupBy类的测试聚合中进行测试：

```java
@Autowired
private PersonDao personDao;

@Test
public void givenExistingPersons_whenFindingPersonByFirstName_thenFound() {
    personDao.save(new Person("Erich", "Gamma"));
    Person person = new Person("Kent", "Beck");
    personDao.save(person);
    personDao.save(new Person("Ralph", "Johnson"));

    Person personFromDb =  personDao.findPersonsByFirstnameQuerydsl("Kent").get(0);
    Assert.assertEquals(person.getId(), personFromDb.getId());
}

@Test
public void givenExistingPersons_whenFindingMaxAgeByName_thenFound() {
    personDao.save(new Person("Kent", "Gamma", 20));
    personDao.save(new Person("Ralph", "Johnson", 35));
    personDao.save(new Person("Kent", "Zivago", 30));

    Map<String, Integer> maxAge = personDao.findMaxAgeByName();
    Assert.assertTrue(maxAge.size() == 2);
    Assert.assertSame(35, maxAge.get("Ralph"));
    Assert.assertSame(30, maxAge.get("Kent"));
}
```

## 六. 总结

本教程说明了如何使用 Querydsl 构建 JPA 项目。