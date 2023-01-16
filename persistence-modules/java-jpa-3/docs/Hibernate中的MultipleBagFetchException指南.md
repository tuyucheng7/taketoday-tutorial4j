## 1. 概述

在本教程中，我们将讨论 [MultipleBagFetchException](https://docs.jboss.org/hibernate/orm/5.2/javadocs/org/hibernate/loader/MultipleBagFetchException.html)。我们将从需要理解的必要术语开始，然后我们将探索一些解决方法，直到我们找到理想的解决方案。

我们将创建一个简单的音乐应用程序域来演示每个解决方案。

## 2. Hibernate 中的 Bag 是什么？

Bag 类似于 List，是一个可以包含重复元素的集合。然而，这是不对的。此外，Bag 是一个[Hibernate](https://www.baeldung.com/jpa-hibernate-difference)术语，不是JavaCollections Framework 的一部分。

根据前面的定义，值得强调的是List和 Bag 都使用java.util.List。虽然在Hibernate中，两者的处理方式不同。为了区分 Bag 和List，让我们看一下实际代码。

一个包：

```java
// @ any collection mapping annotation
private List<T> collection;
```

清单：_

```java
// @ any collection mapping annotation
@OrderColumn(name = "position")
private List<T> collection;
```

## 3.MultipleBagFetchException的原因

在实体上同时获取两个或更多 Bag可以形成笛卡尔积。由于 Bag 没有顺序，Hibernate 将无法将正确的列映射到正确的实体。因此，在这种情况下，它抛出MultipleBagFetchException。

让我们举一些导致MultipleBagFetchException 的具体示例。

对于第一个示例，让我们尝试创建一个简单的实体，它有 2 个袋子并且都具有 eager fetch 类型。艺术家可能是一个很好的例子。它可以有歌曲和优惠的集合。

鉴于此，让我们创建艺术家实体：

```java
@Entity
class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "artist", fetch = FetchType.EAGER)
    private List<Song> songs;

    @OneToMany(mappedBy = "artist", fetch = FetchType.EAGER)
    private List<Offer> offers;

    // constructor, equals, hashCode
}
```

如果我们尝试运行测试，我们将立即遇到 MultipleBagFetchException ，并且它将无法构建 Hibernate SessionFactory。话虽如此，我们不要这样做。

相反，让我们将一个或两个集合的获取类型转换为惰性：

```java
@OneToMany(mappedBy = "artist")
private List<Song> songs;

@OneToMany(mappedBy = "artist")
private List<Offer> offers;
```

现在，我们将能够创建和运行测试。虽然，如果我们尝试同时获取这两个包集合，它仍然会导致MultipleBagFetchException。

## 4.模拟一个MultipleBagFetchException

在上一节中，我们了解了MultipleBagFetchException 的原因。在这里，让我们通过创建集成测试来验证这些声明。

为简单起见，让我们使用之前创建的 艺术家 实体。

现在，让我们创建集成测试，并尝试 使用 JPQL 同时获取歌曲 和 报价：

```java
@Test
public void whenFetchingMoreThanOneBag_thenThrowAnException() {
    IllegalArgumentException exception =
      assertThrows(IllegalArgumentException.class, () -> {
        String jpql = "SELECT artist FROM Artist artist "
          + "JOIN FETCH artist.songs "
          + "JOIN FETCH artist.offers ";

        entityManager.createQuery(jpql);
    });

    final String expectedMessagePart = "MultipleBagFetchException";
    final String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessagePart));
}
```

从断言中，我们遇到了一个 IllegalArgumentException ，它的根本原因是MultipleBagFetchException。

## 5.领域模型

在继续寻找可能的解决方案之前，让我们先看看必要的领域模型，稍后我们将使用这些模型作为参考。

假设我们正在处理音乐应用程序的域。鉴于此，让我们将注意力集中在某些实体上：专辑、艺术家和用户。 

我们已经看到了Artist实体，所以让我们继续处理其他两个实体。

首先，让我们看一下Album实体：

```java
@Entity
class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "album")
    private List<Song> songs;

    @ManyToMany(mappedBy = "followingAlbums")
    private Set<Follower> followers;

    // constructor, equals, hashCode

}
```

一个Album有一组歌曲，同时可以有一组followers。 

接下来，这是用户实体：

```java
@Entity
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.PERSIST)
    private List<Playlist> playlists;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    @OrderColumn(name = "arrangement_index")
    private List<FavoriteSong> favoriteSongs;
    
    // constructor, equals, hashCode
}
```

一个用户可以创建多个播放列表。此外，用户有一个单独的收藏夹歌曲列表，其中它的顺序基于排列索引。

## 6. 解决方法：在单个 JPQL 查询中使用集合

首先，让我们强调 这种方法会生成笛卡尔积，这只是一种变通方法。这是因为我们将在单个 JPQL 查询中同时获取两个集合。 相反，使用 [Set](https://www.baeldung.com/java-set-operations)没有任何问题。如果我们不需要我们的集合有顺序或任何重复的元素，这是合适的选择。 

为了演示这种方法，让我们从我们的域模型中引用Album实体。 

一个 Album 实体有两个集合：songs和followers。歌曲集为 袋式。但是，对于 追随者， 我们使用的是 Set。 话虽如此，即使我们尝试同时获取两个集合，我们也不会遇到 MultipleBagFetchException 。

使用集成测试，让我们尝试通过其 id 检索相册，同时在单个 JPQL 查询中获取其两个集合：

```java
@Test
public void whenFetchingOneBagAndSet_thenRetrieveSuccess() {
    String jpql = "SELECT DISTINCT album FROM Album album "
      + "LEFT JOIN FETCH album.songs "
      + "LEFT JOIN FETCH album.followers "
      + "WHERE album.id = 1";

    Query query = entityManager.createQuery(jpql)
      .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false);

    assertEquals(1, query.getResultList().size());
}
```

如我们所见，我们已成功检索到Album。这是因为只有歌曲列表是 Bag。另一方面，关注者的集合是一个Set。

另外，值得强调的是我们正在使用 QueryHints.HINT_PASS_DISTINCT_THROUGH。 由于我们使用的是实体 JPQL 查询，因此它会阻止 DISTINCT 关键字包含在实际的 SQL 查询中。因此，我们也将对其余方法使用此查询提示。 

## 7. 解决方法：在单个 JPQL 查询中使用列表

与上一节类似， 这也会生成笛卡尔积，这可能会导致性能问题。同样，使用 [List](https://www.baeldung.com/java-arraylist)、 Set 或 Bag 作为数据类型也没有错。本节的目的是进一步演示 Hibernate 可以同时获取集合，前提是只有一个 Bag 类型。

对于这种方法，让我们使用域模型中的 用户 实体。

如前所述，一个 User 有两个集合：playlists和favoriteSongs。播放列表 没有明确的 顺序，使其成为一个包集。但是，对于 favoriteSongs 的 List ，其顺序取决于User的排列方式。如果我们仔细观察FavoriteSong实体， 就会发现arrangementIndex 属性使它成为可能。

同样，使用单个 JPQL 查询，让我们尝试验证我们是否能够在同时获取 播放列表 和 favoriteSongs集合的同时检索所有用户 。

为了演示，让我们创建一个集成测试：

```java
@Test
public void whenFetchingOneBagAndOneList_thenRetrieveSuccess() {
    String jpql = "SELECT DISTINCT user FROM User user "
      + "LEFT JOIN FETCH user.playlists "
      + "LEFT JOIN FETCH user.favoriteSongs ";

    List<User> users = entityManager.createQuery(jpql, User.class)
      .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
      .getResultList();

    assertEquals(3, users.size());
}
```

从断言中，我们可以看到我们已经成功检索到所有用户。此外，我们没有遇到 MultipleBagFetchException。这是因为即使我们同时获取两个集合，但只有 播放列表 是一个包集合。

## 8.理想的解决方案：使用多个查询

我们已经从之前的解决方法中看到使用单个 JPQL 查询来同时检索集合。不幸的是，它生成笛卡尔积。我们知道这并不理想。所以在这里，让我们在 不牺牲性能的情况下解决MultipleBagFetchException 。

假设我们正在处理一个拥有多个包集合的实体。在我们的例子中，它是 艺术家 实体。它有两个包包系列：歌曲和优惠。

在这种情况下，我们甚至无法使用单个 JPQL 查询同时获取两个集合。这样做会导致MultipleBagFetchException。相反，让我们将其拆分为两个 JPQL 查询。

通过这种方法，我们期望一次成功获取两个包集合。

同样，最后一次，让我们快速创建一个用于检索所有艺术家的集成测试：

```java
@Test
public void whenUsingMultipleQueries_thenRetrieveSuccess() {
    String jpql = "SELECT DISTINCT artist FROM Artist artist "
      + "LEFT JOIN FETCH artist.songs ";

    List<Artist> artists = entityManager.createQuery(jpql, Artist.class)
      .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
      .getResultList();

    jpql = "SELECT DISTINCT artist FROM Artist artist "
      + "LEFT JOIN FETCH artist.offers "
      + "WHERE artist IN :artists ";

    artists = entityManager.createQuery(jpql, Artist.class)
      .setParameter("artists", artists)
      .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
      .getResultList();

    assertEquals(2, artists.size());
}
```

从测试中，我们首先在获取其歌曲集的同时检索了所有艺术家。

然后，我们创建了另一个查询来获取艺术家的报价。

使用这种方法，我们避免了 MultipleBagFetchException 以及笛卡尔积的形成。

## 9.总结

在本文中，我们详细探讨了 MultipleBagFetchException 。我们讨论了必要的词汇和这个异常的原因。然后我们模拟了它。之后，我们讨论了一个简单的音乐应用程序域，为我们的每个变通办法和理想解决方案提供不同的场景。最后，我们设置了几个集成测试来验证每种方法。