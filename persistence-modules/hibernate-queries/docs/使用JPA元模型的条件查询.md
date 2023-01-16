## 1. 概述

在本教程中，我们将讨论在 Hibernate 中编写条件查询时如何使用 JPA 静态元模型类。

我们需要对 Hibernate 中的条件查询 API 有基本的了解，因此如果需要，请查看我们的[条件查询](https://www.baeldung.com/hibernate-criteria-queries)教程以获取有关此主题的更多信息。

## 2. 为什么使用 JPA 元模型？

通常，当我们编写条件查询时，我们需要引用实体类及其属性。

现在，其中一种方法是将属性名称作为字符串提供。但是，这有几个缺点。

首先，我们必须查找实体属性的名称。而且，如果列名在项目生命周期的后期发生更改，我们必须重构使用该名称的每个查询。

社区引入了[JPA 元模型](https://docs.jboss.org/hibernate/orm/5.0/topical/html/metamodelgen/MetamodelGenerator.html#_canonical_metamodel)来避免这些缺点并提供对托管实体类的元数据的静态访问。

## 3.实体类

让我们考虑一个场景，我们正在为我们的一个客户构建一个学生门户管理系统，并且出现了根据学生毕业年份提供搜索功能的要求。

首先，让我们看看我们的 Student类：

```java
@Entity
@Table(name = "students")
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "grad_year")
    private int gradYear;

    // standard getters and setters
}
```

## 4. 生成 JPA 元模型类

接下来，我们需要生成元模型类，为此，我们将使用[JBoss](https://docs.jboss.org/hibernate/orm/5.0/topical/html/metamodelgen/MetamodelGenerator.html)提供的元模型生成器工具。JBoss 只是众多可用于生成元模型的工具之一。其他合适的工具包括 [EclipseLink](https://wiki.eclipse.org/UserGuide/JPA/Using_the_Canonical_Model_Generator_(ELUG))、 [OpenJPA](https://openjpa.apache.org/builds/2.4.1/apache-openjpa/docs/ch13s04.html)和 [DataNucleus](https://www.datanucleus.org/products/accessplatform_5_2/jpa/query.html#metamodel)。

要使用 JBoss 工具，我们需要在我们的 pom.xml文件中添加[最新的依赖](https://search.maven.org/search?q=g:org.hibernate AND a:hibernate-jpamodelgen)项，一旦我们触发 maven build 命令，该工具就会生成元模型类：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-jpamodelgen</artifactId>
    <version>5.3.7.Final</version>
</dependency>
```

请注意，我们需要将target/generated-classes 文件夹添加 到 IDE 的类路径中，因为默认情况下，类将仅在此文件夹中生成。

## 5. 静态 JPA 元模型类

根据 JPA 规范，生成的类将与相应的实体类驻留在同一个包中，并且具有相同的名称并在末尾添加“_”(下划线)。因此，为Student 类生成的元模型类 将是 Student_ 并且类似于：

```java
@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Student.class)
public abstract class Student_ {

    public static volatile SingularAttribute<Student, String> firstName;
    public static volatile SingularAttribute<Student, String> lastName;
    public static volatile SingularAttribute<Student, Integer> id;
    public static volatile SingularAttribute<Student, Integer> gradYear;

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String ID = "id";
    public static final String GRAD_YEAR = "gradYear";
}
```

## 6. 使用 JPA 元模型类

我们可以像使用字符串引用属性一样使用静态 元模型类。条件查询 API 提供了重载方法，这些方法接受String引用以及 Attribute 接口实现。

让我们看一下将获取 2015 年毕业的所有学生的条件查询：

```java
//session set-up code
CriteriaBuilder cb = session.getCriteriaBuilder();
CriteriaQuery<Student> criteriaQuery = cb.createQuery(Student.class);

Root<Student> root = criteriaQuery.from(Student.class);
criteriaQuery.select(root).where(cb.equal(root.get(Student_.gradYear), 2015));

Query<Student> query = session.createQuery(criteriaQuery);
List<Student> results = query.getResultList();
```

请注意我们如何使用 Student_.gradYear 引用而不是使用常规的grad_year 列名称。

## 七. 总结

在这篇简短的文章中，我们学习了如何使用静态元模型类，以及为什么它们比前面描述的使用String 引用的传统方式更受欢迎。