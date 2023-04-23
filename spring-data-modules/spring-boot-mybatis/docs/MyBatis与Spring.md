## 1. 简介

MyBatis 是最常用的开源框架之一，用于在Java应用程序中实现 SQL 数据库访问。

在本快速教程中，我们将介绍如何将 MyBatis 与 Spring 和Spring Boot集成。

对于那些还不熟悉这个框架的人，一定要查看我们[关于使用 MyBatis 的文章](https://www.baeldung.com/mybatis)。

## 2. 定义模型

让我们从定义我们将在整篇文章中使用的简单 POJO 开始：

```java
public class Article {
    private Long id;
    private String title;
    private String author;

    // constructor, standard getters and setters
}
```

以及等效的 SQL schema.sql文件：

```sql
CREATE TABLE IF NOT EXISTS `ARTICLES`(
    `id`          INTEGER PRIMARY KEY,
    `title`       VARCHAR(100) NOT NULL,
    `author`      VARCHAR(100) NOT NULL
);
```

接下来，让我们创建一个 data.sql文件，它只是将一条记录插入到我们的articles表中：

```sql
INSERT INTO ARTICLES
VALUES (1, 'Working with MyBatis in Spring', 'Baeldung');
```

这两个 SQL 文件都必须包含在[类路径](https://www.baeldung.com/spring-classpath-file-access)中。

## 3.弹簧配置

要开始使用 MyBatis，我们必须包含两个主要依赖项[——MyBatis](https://search.maven.org/search?q=g:org.mybatis a:mybatis)和[MyBatis-Spring](https://search.maven.org/search?q=g:org.mybatis a: mybatis-spring)：

```xml
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.2</version>
</dependency>

<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-spring</artifactId>
    <version>2.0.2</version>
</dependency>
```

除此之外，我们还需要基本的[Spring 依赖项](https://search.maven.org/search?q=g:org.springframework)：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.3.8</version>
</dependency>

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-beans</artifactId>
    <version>5.3.8</version>
</dependency>
```

在我们的示例中，我们将使用[H2 嵌入式数据库](https://search.maven.org/search?q=g:com.h2database a:h2)来简化设置，并使用[spring-jdbc](https://search.maven.org/search?q=g:org.springframework a:spring-jdbc) 模块中的EmbeddedDatabaseBuilder 类 进行配置：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.199</version>
</dependency>

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>5.3.8</version>
</dependency>
```

### 3.1. 基于注解的配置

Spring 简化了 MyBatis 的配置。唯一需要的元素是 javax.sql.Datasource、org.apache.ibatis.session.SqlSessionFactory和至少一个映射器。

首先，让我们创建一个配置类：

```java
@Configuration
@MapperScan("com.baeldung.mybatis")
public class PersistenceConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
          .setType(EmbeddedDatabaseType.H2)
          .addScript("schema.sql")
          .addScript("data.sql")
          .build();
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource());
        return factoryBean.getObject();
    }
}
```

我们还应用了 来自 MyBatis-Spring 的@MapperScan注解，它扫描定义的包并使用任何映射器注解(例如@Select或 @Delete)自动选择接口。

使用@MapperScan还可以确保每个提供的映射器都自动注册为[Bean](https://www.baeldung.com/spring-bean)，并且稍后可以与[@Autowired](https://www.baeldung.com/spring-autowire)注解一起使用。

我们现在可以创建一个简单的 ArticleMapper接口：

```java
public interface ArticleMapper {
    @Select("SELECT  FROM ARTICLES WHERE id = #{id}")
    Article getArticle(@Param("id") Long id);
}
```

最后，测试我们的设置：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistenceConfig.class)
public class ArticleMapperIntegrationTest {

    @Autowired
    ArticleMapper articleMapper;

    @Test
    public void whenRecordsInDatabase_shouldReturnArticleWithGivenId() {
        Article article = articleMapper.getArticle(1L);

        assertThat(article).isNotNull();
        assertThat(article.getId()).isEqualTo(1L);
        assertThat(article.getAuthor()).isEqualTo("Baeldung");
        assertThat(article.getTitle()).isEqualTo("Working with MyBatis in Spring");
    }
}
```

在上面的例子中，我们使用 MyBatis 来检索我们之前插入到data.sql 文件中的唯一记录。

### 3.2. 基于 XML 的配置

如前所述，要将 MyBatis 与 Spring 一起使用，我们需要Datasource、SqlSessionFactory和至少一个映射器。

让我们在beans.xml 配置文件中创建所需的 bean 定义：

```xml
<jdbc:embedded-database id="dataSource" type="H2">
    <jdbc:script location="schema.sql"/>
    <jdbc:script location="data.sql"/>
</jdbc:embedded-database>
    
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
    <property name="dataSource" ref="dataSource" />
</bean>

<bean id="articleMapper" class="org.mybatis.spring.mapper.MapperFactoryBean">
    <property name="mapperInterface" value="com.baeldung.mybatis.ArticleMapper" />
    <property name="sqlSessionFactory" ref="sqlSessionFactory" />
</bean>
```

在这个例子中，我们还使用了spring-jdbc提供的自定义 XML 模式来配置我们的 H2 数据源。

为了测试这个配置，我们可以重用之前实现的测试类。但是，我们必须调整上下文配置，我们可以通过应用注解来完成：

```java
@ContextConfiguration(locations = "classpath:/beans.xml")
```

## 4. 弹簧靴

Spring Boot 提供的机制进一步简化了 MyBatis 与 Spring 的配置。

首先，让我们将[mybatis ](https://search.maven.org/search?q=g:org.mybatis.spring.boot a:mybatis-spring-boot-starter)[-spring-boot-starter](https://search.maven.org/search?q=g:org.mybatis.spring.boot a:mybatis-spring-boot-starter)[依赖](https://search.maven.org/search?q=g:org.mybatis.spring.boot a:mybatis-spring-boot-starter)添加到我们的 pom.xml中：

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.1.0</version>
</dependency>
```

默认情况下，如果我们使用自动配置功能，Spring Boot 会从类路径中检测 H2 依赖项，并为我们配置 Datasource和 SqlSessionFactory。此外，它还在启动时同时执行schema.sql 和 data.sql 。

如果我们不使用嵌入式数据库，我们可以通过application.yml或application.properties文件使用配置，或者定义一个指向我们数据库的数据源bean。

我们剩下要做的唯一一件事就是定义一个映射器接口，以与以前相同的方式，并使用MyBatis中的@Mapper注解对其进行注解。因此，Spring Boot 会扫描我们的项目，寻找该注解，并将我们的映射器注册为 beans。

之后，我们可以通过应用来自[spring-boot-starter-test 的](https://www.baeldung.com/spring-boot-testing)注解，使用之前定义的测试类来测试我们的配置：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
```

## 5.总结

在本文中，我们探讨了使用 Spring 配置 MyBatis 的多种方法。

我们查看了使用基于注解和 XML 配置的示例，并展示了 MyBatis 与Spring Boot的自动配置功能。