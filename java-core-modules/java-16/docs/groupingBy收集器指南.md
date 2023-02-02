## 1. 简介

在本教程中，**我们将使用各种示例了解groupingBy收集器的工作原理**。

为了理解本教程中涵盖的内容，我们需要对Java 8特性有基本的了解。我们可以看看[Java 8 Streams的介绍](https://www.baeldung.com/java-8-streams-introduction)和[Java 8的Collectors指南](https://www.baeldung.com/java-8-collectors)，了解这些基础知识。

## 2. groupingBy收集器

Java 8 Stream API允许我们以声明性方式处理数据集合。

静态工厂方法Collectors.groupingBy()和Collectors.groupingByConcurrent()为我们提供了类似于SQL语言中“GROUP BY”子句的功能，**我们使用它们按某些属性对对象进行分组并将结果存储在Map实例中**。

groupingBy的重载方法有：

-   首先，以分类器函数作为方法参数：

```java
static <T,K> Collector<T,?,Map<K,List<T>>>
    groupingBy(Function<? super T,? extends K> classifier)
```

-   其次，使用分类器函数和第二个收集器作为方法参数：

```java
static <T,K,A,D> Collector<T,?,Map<K,D>> groupingBy(
    Function<? super T,? extends K> classifier, 
    Collector<? super T,A,D> downstream)
```

-   最后，使用分类器函数、supplier方法(提供包含最终结果的Map实现)和第二个收集器作为方法参数：

```java
static <T,K,D,A,M extends Map<K,D>> Collector<T,?,M> groupingBy(
    Function<? super T,? extends K> classifier, 
    Supplier<M> mapFactory, 
    Collector<? super T,A,D> downstream)
```

### 2.1 示例代码设置

为了演示groupingBy()的用法，让我们定义一个博客文章类BlogPost(我们将使用博客文章对象流)：

```java
class BlogPost {
    String title;
    String author;
    BlogPostType type;
    int likes;
}
```

接下来，定义一个博客文章类型枚举：

```java
enum BlogPostType {
    NEWS,
    REVIEW,
    GUIDE
}
```

然后是博客文章对象集合：

```java
List<BlogPost> posts = Arrays.asList(...);
```

我们还定义一个Tuple类，该类用于根据type和author属性的组合对博客文章进行分组：

```java
class Tuple {
    BlogPostType type;
    String author;
}
```

### 2.2 按单列简单分组

让我们从最简单的groupingBy方法开始，它只接收一个分类器函数作为它的参数，分类器函数应用于流的每个元素。

我们使用函数返回的值作为从groupingBy收集器获得的Map的key。

下面按类型对博客文章集合中的博客文章进行分组：

```java
Map<BlogPostType, List<BlogPost>> postsPerType = posts.stream()
    .collect(groupingBy(BlogPost::getType));
```

### 2.3 具有复杂Map key类型的groupingBy

分类器函数不限于仅返回标量或字符串值，只要我们确保实现必要的equals和hashcode方法，生成的Map的key可以是任何对象。

**要使用两个字段作为key进行分组，我们可以使用javafx.util或org.apache.commons.lang3.tuple包中提供的Pair类**。

例如，按Apache Commons Pair实例中组合的类型和作者对集合中的博客文章进行分组：

```java
Map<Pair<BlogPostType, String>, List<BlogPost>> postsPerTypeAndAuthor = posts.stream()
    .collect(groupingBy(post -> new ImmutablePair<>(post.getType(), post.getAuthor())));
```

同样，我们可以使用之前定义的Tuple类，这个类可以很容易地泛化为根据需要包含更多字段。下面是使用Tuple类的例子：

```java
Map<Tuple, List<BlogPost>> postsPerTypeAndAuthor = posts.stream()
    .collect(groupingBy(post -> new Tuple(post.getType(), post.getAuthor())));
```

Java 16引入了[记录](https://www.baeldung.com/java-record-keyword)的概念，作为一种生成不可变Java类的新形式。

**记录功能为我们提供了一种比Tuple更简单、更清晰、更安全的分组方式**。例如，我们在BlogPost中定义了一个记录实例：

```java
public class BlogPost {
    private String title;
    private String author;
    private BlogPostType type;
    private int likes;

    record AuthPostTypesLikes(String author, BlogPostType type, int likes) {
    }

    // constructor, getters/setters
}
```

现在使用记录实例按类型、作者和喜好对集合中的BlotPost进行分组非常简单：

```java
Map<BlogPost.AuthPostTypesLikes, List<BlogPost>> postsPerTypeAndAuthor = posts.stream()
    .collect(groupingBy(post -> new BlogPost.AuthPostTypesLikes(post.getAuthor(), post.getType(), post.getLikes())));
```

### 2.4 修改返回的Map value类型

groupingBy的第二个重载接收额外的第二个收集器(downstream收集器)，该收集器应用于第一个收集器的结果。

当我们只指定分类器函数时，底层会使用toList()作为下游收集器。

让我们使用toSet()收集器作为下游收集器并以Set的方式获取博客文章(而不是List)：

```java
Map<BlogPostType, Set<BlogPost>> postsPerType = posts.stream()
    .collect(groupingBy(BlogPost::getType, toSet()));
```

### 2.5 按多个字段分组

下游收集器的另一个应用是对第一个分组依据的结果进行二次分组。

下面的例子首先按作者分组，然后按类型对BlogPost集合进行分组：

```java
Map<String, Map<BlogPostType, List>> map = posts.stream()
    .collect(groupingBy(BlogPost::getAuthor, groupingBy(BlogPost::getType)));
```

### 2.6 从分组结果中获取平均值

通过使用下游收集器，我们可以在分类器函数的结果中应用聚合函数。

例如，下面的例子找出每种博客文章类型的平均点赞数：

```java
Map<BlogPostType, Double> averageLikesPerType = posts.stream()
    .collect(groupingBy(BlogPost::getType, averagingInt(BlogPost::getLikes)));
```

### 2.7 从分组结果中获取总和

要计算每种博客类型的点赞总数，请执行以下操作：

```java
Map<BlogPostType, Integer> likesPerType = posts.stream()
    .collect(groupingBy(BlogPost::getType, summingInt(BlogPost::getLikes)));
```

### 2.8 从分组结果中获取最大值或最小值

我们可以执行的另一种聚合操作是获取点赞数最多的博客文章：

```java
Map<BlogPostType, Optional<BlogPost>> maxLikesPerPostType = posts.stream()
    .collect(groupingBy(BlogPost::getType,
    maxBy(comparingInt(BlogPost::getLikes))));
```

同样，我们可以应用minBy下游收集器来获取点赞数最少的博客文章。

请注意，maxBy和minBy收集器考虑了应用它们的集合可能为空的可能性，这就是为什么Map中的Value类型为Optional<BlogPost\>的原因。

### 2.9 获取分组结果属性的统计

Collectors API提供了一个summarizing收集器，我们可以在需要同时计算数字属性的计数、总和、最小值、最大值和平均值的情况下使用它。

下面的例子为每种不同类型的博客文章的likes属性计算一个摘要：

```java
Map<BlogPostType, IntSummaryStatistics> likeStatisticsPerType = posts.stream()
    .collect(groupingBy(BlogPost::getType,
    summarizingInt(BlogPost::getLikes)));
```

每种类型的IntSummaryStatistics对象包含likes属性的计数、总和、平均值、最小值和最大值。对于double和long值，可以使用对应的summarizingXxx方法。

### 2.10 聚合分组结果的多个属性

在前面的部分中，我们了解了如何一次聚合一个字段。**我们可以遵循一些技术来对多个字段进行聚合**。

**第一种方法是使用Collectors::collectingAndThen作为groupingBy的下游收集器**。对于collectingAndThen的第一个参数，我们使用Collectors::toList将流收集到一个集合中；第二个参数finisher应用完成转换，我们可以将它与任何支持聚合的Collectors类方法一起使用以获得我们想要的结果。

例如，让我们按作者分组，对于每个作者，我们统计标题的数量，列出标题，并提供点赞的汇总统计信息。为此，我们首先在BlogPost中添加一个新记录：

```java
public class BlogPost {
    // ...
    record PostCountTitlesLikesStats(long postCount, String titles, IntSummaryStatistics likesStats) {
    }
    // ...
}
```

groupingBy和collectingAndThen的实现将是：

```java
Map<String, BlogPost.PostCountTitlesLikesStats> postsPerAuthor = posts.stream()
    .collect(groupingBy(BlogPost::getAuthor, collectingAndThen(toList(), list -> {
        long count = list.stream()
            .map(BlogPost::getTitle)
            .collect(counting());
        String titles = list.stream()
            .map(BlogPost::getTitle)
            .collect(joining(" : "));
        IntSummaryStatistics summary = list.stream()
            .collect(summarizingInt(BlogPost::getLikes));
        return new BlogPost.PostCountTitlesLikesStats(count, titles, summary);
    })));
```

在collectingAndThen的第一个参数中，我们得到了BlogPost的集合，在完成转换中使用它作为lambda函数的输入来计算生成PostCountTitlesLikesStats的值。

要获取给定作者的信息非常简单：

```java
BlogPost.PostCountTitlesLikesStats result = postsPerAuthor.get("Author 1");
assertThat(result.postCount()).isEqualTo(3L);
assertThat(result.titles()).isEqualTo("News item 1 : Programming guide : Tech review 2");
assertThat(result.likesStats().getMax()).isEqualTo(20);
assertThat(result.likesStats().getMin()).isEqualTo(15);
assertThat(result.likesStats().getAverage()).isEqualTo(16.666d, offset(0.001d));
```

**如果我们使用Collectors::toMap来收集和聚合流的元素，我们还可以进行更复杂的聚合**。

让我们考虑一个简单的例子，我们希望按作者对BlogPost元素进行分组，并将标题与相似分数的上限总和连接起来。

首先，我们创建要封装聚合结果的记录：

```java
public class BlogPost {
    // ...
    record TitlesBoundedSumOfLikes(String titles, int boundedSumOfLikes) {
    }
    // ...
}
```

然后我们按照以下方式对流进行分组和累积：

```java
int maxValLikes = 17;
Map<String, BlogPost.TitlesBoundedSumOfLikes> postsPerAuthor = posts.stream()
    .collect(toMap(BlogPost::getAuthor, post -> {
        int likes = (post.getLikes() > maxValLikes) ? maxValLikes : post.getLikes();
        return new BlogPost.TitlesBoundedSumOfLikes(post.getTitle(), likes);
    }, (u1, u2) -> {
        int likes = (u2.boundedSumOfLikes() > maxValLikes) ? maxValLikes : u2.boundedSumOfLikes();
        return new BlogPost.TitlesBoundedSumOfLikes(u1.titles().toUpperCase() + " : " + u2.titles().toUpperCase(),      u1.boundedSumOfLikes() + likes);
    }));
```

toMap的第一个参数对应用BlogPost::getAuthor的key进行分组。

第二个参数使用lambda函数转换map的value，以将每个BlogPost转换为TitlesBoundedSumOfLikes记录。

toMap的第三个参数处理给定key的重复元素，这里我们使用另一个lambda函数连接标题并将喜欢的内容与maxValLikes中指定的最大允许值相加。

### 2.11 将分组结果映射到不同类型

通过将映射下游收集器应用于分类器函数的结果，我们可以实现更复杂的聚合。

下面对每种博客文章类型的文章标题进行串联：

```java
Map<BlogPostType, String> postsPerType = posts.stream()
    .collect(groupingBy(BlogPost::getType,
    mapping(BlogPost::getTitle, joining(", ", "Post titles: [", "]"))));
```

我们在这里所做的是将每个BlogPost实例映射到它的标题，然后将博客文章标题流缩减为一个拼接的String。在本例中，Map的value类型也不同于默认的List类型。

### 2.11 修改返回Map类型

使用groupingBy收集器时，我们不能对返回的Map的类型做出假设。如果我们想具体说明我们想从groupingBy中获取哪种类型的Map，那么我们可以使用groupingBy方法的第三种变体，该重载允许我们通过传递Map的supplier函数来更改返回Map的类型。

让我们通过将EnumMap supplier函数传递给groupingBy方法来检索EnumMap：

```java
EnumMap<BlogPostType, List<BlogPost>> postsPerType = posts.stream()
    .collect(groupingBy(BlogPost::getType,
    () -> new EnumMap<>(BlogPostType.class), toList()));
```

## 3. 并发分组收集器

与groupingBy类似的是groupingByConcurrent收集器，它充分利用了多核架构。这个收集器也有三个重载方法，与groupingBy收集器各自的重载方法具有完全相同的参数。但是，groupingByConcurrent收集器的返回类型必须是ConcurrentHashMap类或其子类的实例。

要并发执行分组操作，流也需要是并行的：

```java
ConcurrentMap<BlogPostType, List<BlogPost>> postsPerType = posts.parallelStream()
    .collect(groupingByConcurrent(BlogPost::getType));
```

如果我们选择将Map supplier函数传递给groupingByConcurrent收集器，那么我们需要确保该函数返回ConcurrentHashMap或其子类。

## 4. Java 9增强

Java 9引入了两个与groupingBy配合使用的新收集器；有关它们的更多信息可以在[这里](https://www.baeldung.com/java9-stream-collectors)找到。

## 5. 总结

在本文中，我们探讨了Java 8 Collectors API提供的groupingBy收集器的用法。

我们学习了如何使用groupingBy来根据元素的一个属性对元素流进行分类，以及如何进一步收集、变异并简化为最终容器的分类结果。