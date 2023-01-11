## 1. 概述

本教程将重点介绍在 Spring Data MongoDB 中构建不同类型的查询。

我们将研究使用Query和Criteria类、自动生成的查询方法、JSON 查询和 QueryDSL 查询文档。

对于 Maven 设置，请查看我们的[介绍性文章](https://www.baeldung.com/spring-data-mongodb-tutorial)。

## 2. 单据查询

使用 Spring Data 查询 MongoDB 的一种更常见的方法是使用Query和Criteria类，它们非常接近本机运算符。

### 2.1. 是

这只是一个使用平等的标准。让我们看看它是如何工作的。

在以下示例中，我们将查找名为Eric的用户。

让我们看看我们的数据库：

```javascript
[
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581907"),
        "_class" : "org.baeldung.model.User",
        "name" : "Eric",
        "age" : 45
    },
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581908"),
        "_class" : "org.baeldung.model.User",
        "name" : "Antony",
        "age" : 55
    }
}
```

现在让我们看一下查询代码：

```java
Query query = new Query();
query.addCriteria(Criteria.where("name").is("Eric"));
List<User> users = mongoTemplate.find(query, User.class);

```

正如预期的那样，此逻辑返回：

```javascript
{
    "_id" : ObjectId("55c0e5e5511f0a164a581907"),
    "_class" : "org.baeldung.model.User",
    "name" : "Eric",
    "age" : 45
}
```

### 2.2. 正则表达式

一种更灵活、更强大的查询类型是正则表达式。这将使用 MongoDB $regex创建一个条件，该条件返回适合该字段的正则表达式的所有记录。

它的工作方式类似于startingWith和endingWith操作。

在此示例中，我们将查找名称以A开头的所有用户。

这是数据库的状态：

```javascript
[
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581907"),
        "_class" : "org.baeldung.model.User",
        "name" : "Eric",
        "age" : 45
    },
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581908"),
        "_class" : "org.baeldung.model.User",
        "name" : "Antony",
        "age" : 33
    },
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581909"),
        "_class" : "org.baeldung.model.User",
        "name" : "Alice",
        "age" : 35
    }
]
```

现在让我们创建查询：

```java
Query query = new Query();
query.addCriteria(Criteria.where("name").regex("^A"));
List<User> users = mongoTemplate.find(query,User.class);
```

这将运行并返回 2 条记录：

```javascript
[
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581908"),
        "_class" : "org.baeldung.model.User",
        "name" : "Antony",
        "age" : 33
    },
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581909"),
        "_class" : "org.baeldung.model.User",
        "name" : "Alice",
        "age" : 35
    }
]
```

这是另一个简单示例，这次查找名称以c结尾的所有用户：

```java
Query query = new Query();
query.addCriteria(Criteria.where("name").regex("c$"));
List<User> users = mongoTemplate.find(query, User.class);

```

所以结果将是：

```javascript
{
    "_id" : ObjectId("55c0e5e5511f0a164a581907"),
    "_class" : "org.baeldung.model.User",
    "name" : "Eric",
    "age" : 45
}
```

### 2.3. Lt和gt

这些运算符使用$lt(小于)和$gt(大于)运算符创建条件。

让我们举一个简单的例子，我们正在寻找 20 到 50 岁之间的所有用户。

数据库是：

```javascript
[
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581907"),
        "_class" : "org.baeldung.model.User",
        "name" : "Eric",
        "age" : 45
    },
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581908"),
        "_class" : "org.baeldung.model.User",
        "name" : "Antony",
        "age" : 55
    }
}
```

查询代码：

```java
Query query = new Query();
query.addCriteria(Criteria.where("age").lt(50).gt(20));
List<User> users = mongoTemplate.find(query,User.class);

```

以及年龄大于 20 且小于 50 的所有用户的结果：

```javascript
{
    "_id" : ObjectId("55c0e5e5511f0a164a581907"),
    "_class" : "org.baeldung.model.User",
    "name" : "Eric",
    "age" : 45
}
```

### 2.4. 种类

排序用于指定结果的排序顺序。

下面的示例返回按年龄升序排序的所有用户。

首先，这是现有数据：

```javascript
[
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581907"),
        "_class" : "org.baeldung.model.User",
        "name" : "Eric",
        "age" : 45
    },
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581908"),
        "_class" : "org.baeldung.model.User",
        "name" : "Antony",
        "age" : 33
    },
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581909"),
        "_class" : "org.baeldung.model.User",
        "name" : "Alice",
        "age" : 35
    }
]

```

执行排序后：

```java
Query query = new Query();
query.with(Sort.by(Sort.Direction.ASC, "age"));
List<User> users = mongoTemplate.find(query,User.class);

```

这是查询的结果，按年龄很好地排序：

```javascript
[
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581908"),
        "_class" : "org.baeldung.model.User",
        "name" : "Antony",
        "age" : 33
    },
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581909"),
        "_class" : "org.baeldung.model.User",
        "name" : "Alice",
        "age" : 35
    },
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581907"),
        "_class" : "org.baeldung.model.User",
        "name" : "Eric",
        "age" : 45
    }
]
```

### 2.5. 可分页

让我们看一个使用分页的简单示例。

这是数据库的状态：

```javascript
[
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581907"),
        "_class" : "org.baeldung.model.User",
        "name" : "Eric",
        "age" : 45
    },
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581908"),
        "_class" : "org.baeldung.model.User",
        "name" : "Antony",
        "age" : 33
    },
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581909"),
        "_class" : "org.baeldung.model.User",
        "name" : "Alice",
        "age" : 35
    }
]

```

现在是查询逻辑，简单地请求大小为 2 的页面：

```java
final Pageable pageableRequest = PageRequest.of(0, 2);
Query query = new Query();
query.with(pageableRequest);

```

结果，2 个文件，如预期的那样：

```javascript
[
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581907"),
        "_class" : "org.baeldung.model.User",
        "name" : "Eric",
        "age" : 45
    },
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581908"),
        "_class" : "org.baeldung.model.User",
        "name" : "Antony",
        "age" : 33
    }
]
```

## 3.生成的查询方法

现在让我们探索 Spring Data 通常提供的更常见的查询类型，即根据方法名称自动生成的查询。

要利用这些类型的查询，我们唯一需要做的就是在存储库接口上声明方法：

```java
public interface UserRepository 
  extends MongoRepository<User, String>, QueryDslPredicateExecutor<User> {
    ...
}
```

### 3.1. 查找方式

我们将从简单的开始，探索 findBy 类型的查询。在这种情况下，我们将使用按名称查找：


```java
List<User> findByName(String name);
```

就像在上一节 2.1 中一样，查询将有相同的结果，找到具有给定名称的所有用户：

```java
List<User> users = userRepository.findByName("Eric");

```

### 3.2. 开始和结束

在 2.2 节中，我们探讨了基于正则表达式的查询。开始和结束当然没有那么强大，但仍然非常有用，特别是如果我们不必实际实现它们。

这是操作的外观的简单示例：


```java
List<User> findByNameStartingWith(String regexp);
List<User> findByNameEndingWith(String regexp);
```

当然，实际使用它的例子非常简单：

```java
List<User> users = userRepository.findByNameStartingWith("A");

List<User> users = userRepository.findByNameEndingWith("c");
```

结果完全一样。

### 3.3. 之间

与 2.3 节类似，这将返回年龄在ageGT和ageLT 之间的所有用户：


```java
List<User> findByAgeBetween(int ageGT, int ageLT);
```

调用该方法将导致找到完全相同的文档：

```java
List<User> users = userRepository.findByAgeBetween(20, 50);

```

### 3.4. 点赞和订购

这次让我们看一个更高级的示例，为生成的查询组合两种类型的修饰符。

我们将寻找名称中包含字母A的所有用户，我们还将按年龄升序对结果进行排序：

```java
List<User> users = userRepository.findByNameLikeOrderByAgeAsc("A");

```

对于我们在 2.4 节中使用的数据库，结果将是：

```javascript
[
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581908"),
        "_class" : "org.baeldung.model.User",
        "name" : "Antony",
        "age" : 33
    },
    {
        "_id" : ObjectId("55c0e5e5511f0a164a581909"),
        "_class" : "org.baeldung.model.User",
        "name" : "Alice",
        "age" : 35
    }
]
```

## 4. JSON 查询方法

如果我们不能借助方法名称或标准来表示查询，我们可以做一些更底层的事情，使用@Query注解。

使用此注解，我们可以将原始查询指定为 Mongo JSON 查询字符串。

### 4.1. 查找依据 

让我们从简单开始，首先看看我们如何按方法类型表示查找：

```java
@Query("{ 'name' : ?0 }")
List<User> findUsersByName(String name);

```

此方法应按名称返回用户。占位符?0引用该方法的第一个参数。

```java
List<User> users = userRepository.findUsersByName("Eric");
```

### 4.2. $正则表达式

我们还可以查看正则表达式驱动的查询，它当然会产生与 2.2 和 3.2 节相同的结果：

```java
@Query("{ 'name' : { $regex: ?0 } }")
List<User> findUsersByRegexpName(String regexp);
```

用法也完全一样：

```java
List<User> users = userRepository.findUsersByRegexpName("^A");

List<User> users = userRepository.findUsersByRegexpName("c$");
```

### 4.3. $lt和$gt

现在让我们实现lt和gt查询：

```java
@Query("{ 'age' : { $gt: ?0, $lt: ?1 } }")
List<User> findUsersByAgeBetween(int ageGT, int ageLT);
```

现在该方法有 2 个参数，我们在原始查询中通过索引引用每个参数，?0和?1：

```java
List<User> users = userRepository.findUsersByAgeBetween(20, 50);
```

## 5.QueryDSL查询

MongoRepository对[QueryDSL](http://www.querydsl.com/)项目有很好的支持，所以我们也可以在这里利用那个漂亮的、类型安全的 API。

### 5.1. Maven 依赖项

首先，让我们确保在 pom 中定义了正确的 Maven 依赖项：

```xml
<dependency>
    <groupId>com.mysema.querydsl</groupId>
    <artifactId>querydsl-mongodb</artifactId>
    <version>4.3.1</version>
</dependency>
<dependency>
    <groupId>com.mysema.querydsl</groupId>
    <artifactId>querydsl-apt</artifactId>
    <version>4.3.1</version>
</dependency>
```

### 5.2. Q级

QueryDSL 使用 Q 类来创建查询，但由于我们真的不想手动创建这些，所以我们需要以某种方式生成它们。

我们将使用 apt-maven-plugin 来做到这一点：

```xml
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
                <outputDirectory>target/generated-sources/java</outputDirectory>
                <processor>
                  org.springframework.data.mongodb.repository.support.MongoAnnotationProcessor
                </processor>
            </configuration>
        </execution>
     </executions>
</plugin>
```

让我们看看User类，特别关注@QueryEntity注解：

```java
@QueryEntity 
@Document
public class User {
 
    @Id
    private String id;
    private String name;
    private Integer age;
 
    // standard getters and setters
}
```

在运行 Maven 生命周期的进程目标(或之后的任何其他目标)之后，apt 插件将在target/generated-sources/java/{your package structure}下生成新类：

```java
/
  QUser is a Querydsl query type for User
 /
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = ...;

    public static final QUser user = new QUser("user");

    public final NumberPath<Integer> age = createNumber("age", Integer.class);

    public final StringPath id = createString("id");

    public final StringPath name = createString("name");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata<?> metadata) {
        super(User.class, metadata);
    }
}
```

正是因为这个类，我们才不需要创建我们的查询。

作为旁注，如果我们使用 Eclipse，引入这个插件将在 pom 中生成以下警告：

>   需要使用 JDK 运行构建或在类路径中包含 tools.jar。如果在 eclipse 构建期间发生这种情况，请确保也在 JDK 下运行 eclipse (com.mysema.maven:apt-maven-plugin:1.1.3:process:default:generate-sources

Maven安装工作正常并且生成了QUser类，但是 pom.xml 中突出显示了一个插件。

快速解决方法是手动指向eclipse.ini中的 JDK ：

```plaintext
...
-vm
{path_to_jdk}jdk{your_version}binjavaw.exe
```

### 5.3. 新资料库 

现在我们需要在我们的存储库中实际启用 QueryDSL 支持，这通过简单地扩展QueryDslPredicateExecutor接口来完成：

```java
public interface UserRepository extends 
  MongoRepository<User, String>, QuerydslPredicateExecutor<User>
```

### 5.4. 方程式

启用支持后，现在让我们实现与之前说明的查询相同的查询。

我们将从简单的平等开始：

```java
QUser qUser = new QUser("user");
Predicate predicate = qUser.name.eq("Eric");
List<User> users = (List<User>) userRepository.findAll(predicate);
```

### 5.5. 开始和结束

同样，让我们实现前面的查询并查找名称以A开头的用户：

```java
QUser qUser = new QUser("user");
Predicate predicate = qUser.name.startsWith("A");
List<User> users = (List<User>) userRepository.findAll(predicate);

```

以及以c结尾：

```java
QUser qUser = new QUser("user");
Predicate predicate = qUser.name.endsWith("c");
List<User> users = (List<User>) userRepository.findAll(predicate);

```

结果与 2.2、3.2 和 4.2 节中的结果相同。

### 5.6. 之间

下一个查询将返回年龄在 20 到 50 之间的用户，类似于前面的部分：

```java
QUser qUser = new QUser("user");
Predicate predicate = qUser.age.between(20, 50);
List<User> users = (List<User>) userRepository.findAll(predicate);
```

## 六. 总结

在本文中，我们探索了使用 Spring Data MongoDB 进行查询的多种方式。

退后一步，看看我们查询 MongoDB 的所有强大方法，从有限的控制一直到使用原始查询的完全控制，都是很有趣的。