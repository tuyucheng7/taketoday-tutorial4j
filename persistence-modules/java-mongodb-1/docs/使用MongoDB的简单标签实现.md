## 1. 概述

在本教程中，我们将了解使用Java和 MongoDB 的简单标记实现。

对于那些不熟悉这个概念的人来说，标签是用作“标签”的关键字，用于将文档分组到不同的类别中。这允许用户快速浏览相似的内容，在处理大量数据时尤其有用。

话虽如此，这种技术在博客中非常常用也就不足为奇了。在这种情况下，根据所涵盖的主题，每个帖子都有一个或多个标签。当用户阅读完后，他可以关注其中一个标签来查看与该主题相关的更多内容。

让我们看看如何实现这个场景。

## 2. 依赖

为了查询数据库，我们必须在我们的pom.xml中包含 MongoDB 驱动程序依赖项：

```xml
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongo-java-driver</artifactId>
    <version>3.6.3</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|ga|1|g%3A"org.mongodb" AND a%3A"mongo-java-driver")找到此依赖项的当前版本。

## 3. 数据模型

首先，让我们开始计划一个帖子文档应该是什么样子。

为简单起见，我们的数据模型将只有一个标题，我们还将其用作文档 ID、作者和一些标签。

我们会将标签存储在一个数组中，因为一篇文章可能不止一个：

```javascript
{
    "_id" : "Java 8 and MongoDB",
    "author" : "Donato Rimenti",
    "tags" : ["Java", "MongoDB", "Java 8", "Stream API"]
}
```

我们还将创建相应的Java模型类：

```java
public class Post {
    private String title;
    private String author;
    private List<String> tags;

    // getters and setters
}
```

## 4.更新标签

现在我们已经设置了数据库并插入了一些示例帖子，让我们看看如何更新它们。

我们的存储库类将包含两种方法来处理标签的添加和删除，方法是使用标题来查找标签。我们还将返回一个布尔值来指示查询是否更新了一个元素：

```java
public boolean addTags(String title, List<String> tags) {
    UpdateResult result = collection.updateOne(
      new BasicDBObject(DBCollection.ID_FIELD_NAME, title), 
      Updates.addEachToSet(TAGS_FIELD, tags));
    return result.getModifiedCount() == 1;
}

public boolean removeTags(String title, List<String> tags) {
    UpdateResult result = collection.updateOne(
      new BasicDBObject(DBCollection.ID_FIELD_NAME, title), 
      Updates.pullAll(TAGS_FIELD, tags));
    return result.getModifiedCount() == 1;
}
```

我们使用addEachToSet方法而不是push来添加，这样如果标签已经存在，我们就不会再次添加它们。

还要注意addToSet运算符也不起作用，因为它会将新标签添加为嵌套数组，这不是我们想要的。

执行更新的另一种方法是通过 Mongo shell。例如，让我们用Java更新 JUnit5 帖子。特别是，我们要添加标签Java和 J Unit5并删除标签Spring和REST：

```javascript
db.posts.updateOne(
    { _id : "JUnit 5 with Java" }, 
    { $addToSet : 
        { "tags" : 
            { $each : ["Java", "JUnit5"] }
        }
});

db.posts.updateOne(
    {_id : "JUnit 5 with Java" },
    { $pull : 
        { "tags" : { $in : ["Spring", "REST"] }
    }
});
```

## 5.查询

最后但并非最不重要的一点是，让我们来看看在使用标签时我们可能感兴趣的一些最常见的查询。为此，我们将特别利用三个数组运算符：

-   $in –返回字段包含指定数组的
-   $nin –返回字段不包含指定数组的
-   $all –返回一个字段包含指定数组的

我们将定义三种方法来查询与作为参数传递的标签集合相关的帖子。他们将返回匹配至少一个标签、所有标签和不匹配任何标签的帖子。我们还将使用Java8 的 Stream API 创建一个映射方法来处理文档和我们的模型之间的转换：

```java
public List<Post> postsWithAtLeastOneTag(String... tags) {
    FindIterable<Document> results = collection
      .find(Filters.in(TAGS_FIELD, tags));
    return StreamSupport.stream(results.spliterator(), false)
      .map(TagRepository::documentToPost)
      .collect(Collectors.toList());
}

public List<Post> postsWithAllTags(String... tags) {
    FindIterable<Document> results = collection
      .find(Filters.all(TAGS_FIELD, tags));
    return StreamSupport.stream(results.spliterator(), false)
      .map(TagRepository::documentToPost)
      .collect(Collectors.toList());
}

public List<Post> postsWithoutTags(String... tags) {
    FindIterable<Document> results = collection
      .find(Filters.nin(TAGS_FIELD, tags));
    return StreamSupport.stream(results.spliterator(), false)
      .map(TagRepository::documentToPost)
      .collect(Collectors.toList());
}

private static Post documentToPost(Document document) {
    Post post = new Post();
    post.setTitle(document.getString(DBCollection.ID_FIELD_NAME));
    post.setAuthor(document.getString("author"));
    post.setTags((List<String>) document.get(TAGS_FIELD));
    return post;
}
```

同样，让我们也看一下 shell 等效查询。我们将获取三个不同的帖子集合，分别用MongoDB或Stream API 标记，用Java 8和JUnit 5标记，而不用Groovy或Scala标记：

```javascript
db.posts.find({
    "tags" : { $in : ["MongoDB", "Stream API" ] } 
});

db.posts.find({
    "tags" : { $all : ["Java 8", "JUnit 5" ] } 
});

db.posts.find({
    "tags" : { $nin : ["Groovy", "Scala" ] } 
});
```

## 六. 总结

在本文中，我们展示了如何构建标记机制。当然，除了博客之外，我们还可以将同样的方法用于其他目的。

如果你有兴趣进一步学习 MongoDB，[我们鼓励你阅读这篇介绍性文章](https://www.baeldung.com/java-mongodb)。