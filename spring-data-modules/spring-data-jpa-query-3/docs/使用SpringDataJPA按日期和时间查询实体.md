## 1. 概述

在本快速教程中，我们将了解如何使用Spring Data JPA按日期查询实体。

首先，我们将回顾一下如何使用JPA映射日期和时间。然后我们将创建一个包含日期和时间字段的实体，以及一个用于查询这些实体的Spring Data Repository。

## 2. 使用JPA映射日期和时间

首先，**我们将回顾一些关于使用JPA映射日期的理论**。要知道的是，我们需要决定是否要表示：

+ 仅日期
+ 仅时间
+ 两者都有

除了@Column注解(可选)之外，我们还需要添加@Temporal注解来指定字段表示的内容。

此注解接收一个参数，该参数是TemporalType枚举的值：

+ TemporalType.DATE
+ TemporalType.TIME
+ TemporalType.TIMESTAMP

## 3. 实践

在实践中，一旦正确设置了我们的实体，使用Spring Data JPA查询它们就不需要做太多工作了。我们只需要使用查询方法或@Query注解。

**每个Spring Data JPA机制都可以正常工作**。

让我们看几个使用Spring Data JPA按日期和时间查询实体的示例。

### 3.1 设置实体

例如，假设我们有一个包含publicationDate、publicationTime和creationDateTime字段的Article实体：

```java
@Entity
public class Article {

    @Id
    @GeneratedValue
    private Integer id;

    @Temporal(TemporalType.DATE)
    private Date publicationDate;

    @Temporal(TemporalType.TIME)
    private Date publicationTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDateTime;
    // ...
}
```

出于演示目的，我们将发布日期和时间分成两个字段；这样我们就可以演示三种时间类型。

### 3.2 查询实体

现在我们的实体已经全部设置好了，让我们创建一个Spring Data Repository来查询这些文章。

我们将使用Spring Data JPA功能创建三个方法：

```java
public interface ArticleRepository extends JpaRepository<Article, Integer> {

    List<Article> findAllByPublicationDate(Date publicationDate);

    List<Article> findAllByPublicationTimeBetween(Date publicationTimeStart, Date publicationTimeEnd);

    @Query("select a from Article a where a.creationDateTime <= :creationDateTime")
    List<Article> findAllWithCreationDateTimeBefore(@Param("creationDateTime") Date creationDateTime);
}
```

+ findAllByPublicationDate：检索在给定日期发表的文章
+ findAllByPublicationTimeBetween：检索在两个给定时间之间发布的文章
+ findAllWithCreationDateTimeBefore：检索在给定日期和时间之前发布的文章

前两个方法依赖于Spring Data的查询方法机制，最后一个依赖于@Query注解。

最后，这不会改变处理日期的方式。**第一个方法只会考虑参数的日期部分**。

第二个只会考虑参数的时间。最后一个将同时使用日期和时间。

### 3.3 测试查询

我们要做的最后一件事是设置一些测试来检查这些查询是否按预期工作。

我们首先将数据导入我们的数据库，然后创建测试类来检查Repository的每个方法：

```java
@ExtendWith(SpringExtension.class)
@DataJpaTest(properties = "spring.sql.init.data-locations=classpath:import_entities.sql", showSql = false)
class ArticleRepositoryIntegrationTest {

    @Autowired
    private ArticleRepository repository;

    @Test
    void givenImportedArticlesWhenFindAllByPublicationDateThenArticles1And2Returned() throws Exception {
        List<Article> result = repository.findAllByPublicationDate(
              new SimpleDateFormat("yyyy-MM-dd").parse("2018-01-01")
        );

        assertEquals(2, result.size());
        assertTrue(result.stream()
              .map(Article::getId)
              .allMatch(id -> Arrays.asList(1, 2).contains(id))
        );
    }

    @Test
    void givenImportedArticlesWhenFindAllByPublicationTimeBetweenThenArticles2And3Returned() throws Exception {
        List<Article> result = repository.findAllByPublicationTimeBetween(
              new SimpleDateFormat("HH:mm").parse("15:15"),
              new SimpleDateFormat("HH:mm").parse("16:30")
        );

        assertEquals(2, result.size());
        assertTrue(result.stream()
              .map(Article::getId)
              .allMatch(id -> Arrays.asList(2, 3).contains(id))
        );
    }

    @Test
    void givenImportedArticlesWhenFindAllWithCreationDateTimeBeforeThenArticles2And3Returned() throws Exception {
        List<Article> result = repository.findAllWithCreationDateTimeBefore(
              new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2017-12-15 10:00")
        );

        assertEquals(2, result.size());
        assertTrue(result.stream()
              .map(Article::getId)
              .allMatch(id -> Arrays.asList(2, 3).contains(id))
        );
    }
}
```

以下是测试中使用到的SQL脚本：

```sql
insert into Article(id, publication_date, publication_time, creation_date_time)
values (1, PARSEDATETIME('01/01/2018', 'dd/MM/yyyy'), PARSEDATETIME('15:00', 'HH:mm'),
        PARSEDATETIME('31/12/2017 07:30', 'dd/MM/yyyy HH:mm'));
insert into Article(id, publication_date, publication_time, creation_date_time)
values (2, PARSEDATETIME('01/01/2018', 'dd/MM/yyyy'), PARSEDATETIME('15:30', 'HH:mm'),
        PARSEDATETIME('15/12/2017 08:00', 'dd/MM/yyyy HH:mm'));
insert into Article(id, publication_date, publication_time, creation_date_time)
values (3, PARSEDATETIME('15/12/2017', 'dd/MM/yyyy'), PARSEDATETIME('16:00', 'HH:mm'),
        PARSEDATETIME('01/12/2017 13:45', 'dd/MM/yyyy HH:mm'));
```

每个测试都会验证是否只检索到符合条件的文章。

## 4. 总结

在这篇简短的文章中，我们学习了如何使用Spring Data JPA按日期和时间字段查询实体。