## 1. 概述

在本快速教程中，我们将介绍在[Spring JPA Repository](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)中创建LIKE查询的不同方法。

我们将首先查看在创建查询方法时可以使用的各种关键字。然后我们将介绍使用命名和索引参数的@Query注解。

## 2. 项目构建

对于我们的示例，我们将查询movie表。

让我们定义我们的Movie实体：

```java
@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String title;
    private String director;
    private String rating;
    private int duration;

    // standard getters and setters
}
```

定义了Movie实体后，让我们添加一些初始数据：

```sql
INSERT INTO movie(id, title, director, rating, duration)
VALUES (1, 'Godzilla: King of the Monsters', ' Michael Dougherty', 'PG-13', 132);
INSERT INTO movie(id, title, director, rating, duration)
VALUES (2, 'Avengers: Endgame', 'Anthony Russo', 'PG-13', 181);
INSERT INTO movie(id, title, director, rating, duration)
VALUES (3, 'Captain Marvel', 'Anna Boden', 'PG-13', 123);
INSERT INTO movie(id, title, director, rating, duration)
VALUES (4, 'Dumbo', 'Tim Burton', 'PG', 112);
INSERT INTO movie(id, title, director, rating, duration)
VALUES (5, 'Booksmart', 'Olivia Wilde', 'R', 102);
INSERT INTO movie(id, title, director, rating, duration)
VALUES (6, 'Aladdin', 'Guy Ritchie', 'PG', 128);
INSERT INTO movie(id, title, director, rating, duration)
VALUES (7, 'The Sun Is Also a Star', 'Ry Russo-Young', 'PG-13', 100);
```

## 3. LIKE查询方法

**对于许多简单的LIKE查询场景，我们可以利用各种关键字在我们的Repository中创建查询方法**。

### 3.1 Containing、Contains、IsContaining和Like

让我们看看如何使用查询方法执行以下LIKE查询：

```sql
SELECT * FROM movie WHERE title LIKE '%in%';
```

首先，让我们使用Containing、Contains和IsContaining定义查询方法：

```java
List<Movie> findByTitleContaining(String title);
List<Movie> findByTitleContains(String title);
List<Movie> findByTitleIsContaining(String title);
```

让我们用部分标题调用我们的查询方法：

```java
List<Movie> results = movieRepository.findByTitleContaining("in");
assertEquals(3, results.size());

results = movieRepository.findByTitleIsContaining("in");
assertEquals(3, results.size());

results = movieRepository.findByTitleContains("in");
assertEquals(3, results.size());
```

我们可以期望这三种方法中的每一种都返回相同的结果。

**Spring还为我们提供了一个Like关键字，但它的行为略有不同，因为我们需要为搜索参数提供通配符**。

让我们定义一个LIKE查询方法：

```java
List<Movie> findByTitleLike(String title);
```

现在我们将使用之前传递的相同值调用我们的findByTitleLike方法，但包括通配符：

```java
results = movieRepository.findByTitleLike("%in%");
assertEquals(3, results.size());
```

### 3.2 StartsWith

让我们看看下面的查询：

```sql
SELECT * FROM Movie WHERE Rating LIKE 'PG%';
```

我们将使用StartsWith关键字来创建查询方法：

```java
List<Movie> findByRatingStartsWith(String rating);
```

定义了我们的方法后，通过参数PG调用它：

```java
List<Movie> results = movieRepository.findByRatingStartsWith("PG");
assertEquals(6, results.size());
```

### 3.3 EndsWith

**Spring通过EndsWith关键字为我们提供了相反的功能**。

让我们考虑这个查询：

```sql
SELECT * FROM Movie WHERE director LIKE '%Burton';
```

现在我们将定义一个EndsWith查询方法：

```java
List<Movie> findByDirectorEndsWith(String director);
```

然后使用Burton参数调用它：

```java
List<Movie> results = movieRepository.findByDirectorEndsWith("Burton");
assertEquals(1, results.size());
```

### 3.4 不区分大小写

我们有时想不区分大小写地找出包含某个字符串的所有记录。在SQL中，我们可以通过强制列为全部大写或小写字母并为我们查询的值提供相同的值来实现这一点。

**使用Spring JPA，我们可以将[IgnoreCase](https://www.baeldung.com/spring-data-case-insensitive-queries)关键字与其他关键字之一结合使用**：

```java
List<Movie> findByTitleContainingIgnoreCase(String title);
```

现在我们可以调用该方法，并期望得到包含小写和大写结果的结果：

```java
List<Movie> results = movieRepository.findByTitleContainingIgnoreCase("the");
assertEquals(2, results.size());
```

### 3.5 Not

有时我们想找到所有不包含特定字符串的记录。**我们可以使用NotContains、NotContaining和NotLike关键字来做到这一点**。

让我们定义一个查询，使用NotContains来查找分级不包含PG的电影：

```java
List<Movie> findByRatingNotContaining(String rating);
```

现在让我们调用新定义的方法：

```java
List<Movie> results = movieRepository.findByRatingNotContaining("PG");
assertEquals(1, results.size());
```

为了实现查找导演姓名不以特定字符串开头的记录的功能，我们将使用NotLike关键字来保留对通配符位置的控制：

```java
List<Movie> findByDirectorNotLike(String director);
```

最后，让我们调用该方法来查找导演姓名不是以An开头的所有电影：

```java
List<Movie> results = movieRepository.findByDirectorNotLike("An%");
assertEquals(5, results.size());
```

我们可以以类似的方式使用NotLike来完成Not与EndsWith类功能的结合。

## 4. 使用@Query

有时我们需要创建对于查询方法来说过于复杂的查询，或者会导致方法名称过长。在这些情况下，**我们可以使用[@Query注解](https://www.baeldung.com/spring-data-jpa-query)来查询我们的数据库**。

### 4.1 命名参数

为了进行比较，我们将创建一个等效于我们之前定义的findByTitleContaining方法的查询：

```java
@Query("SELECT m FROM Movie m WHERE m.titleLIKE%:title%")
List<Movie> searchByTitleLike(@Param("title") String title);
```

我们在提供的查询中包含我们的通配符。@Param注解在这里很重要，因为我们使用的是命名参数。

### 4.2 索引参数

除了命名参数之外，我们还可以在查询中使用索引参数：

```java
@Query("SELECT m FROM Movie m WHERE m.ratingLIKE?1%")
List<Movie> searchByRatingStartsWith(String rating);
```

我们可以控制通配符，因此该查询等效于findByRatingStartsWith查询方法。

让我们找出所有评级以PG开头的电影：

```java
List<Movie> results = movieRepository.searchByRatingStartsWith("PG");
assertEquals(6, results.size());
```

当我们在包含不可信数据的LIKE查询中使用索引参数时，我们应该转义传入的搜索值。

如果我们使用Spring Boot 2.4.1或更高版本，我们可以使用[SpEL](https://www.baeldung.com/spring-expression-language)转义方法：

```java
@Query("SELECT m FROM Movie m WHERE m.directorLIKE%?#{escape([0])} escape ?#{escapeCharacter()}")
List<Movie> searchByDirectorEndsWith(String director);
```

现在使用Burton调用我们的方法：

```java
List<Movie> results = movieRepository.searchByDirectorEndsWith("Burton");
assertEquals(1, results.size());
```

## 5. 总结

在这篇简短的文章中，我们学习了如何在Spring JPA Repository中创建LIKE查询。

首先，我们学习了如何使用提供的关键字来创建查询方法。

然后我们学习了如何使用带有命名和索引参数的@Query参数来实现相同的目的。