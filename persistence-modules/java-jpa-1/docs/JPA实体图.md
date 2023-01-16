## 1. 概述

JPA 2.1 引入了实体图功能作为处理性能加载的更复杂的方法。

它允许通过对我们想要检索的相关持久性字段进行分组来定义模板，并让我们在运行时选择图形类型。

在本教程中，我们将更详细地解释如何创建和使用此功能。

## 2.实体图试图解决什么

在 JPA 2.0 之前，要加载实体关联，我们通常使用 FetchType。LAZY和 FetchType。EAGER 作为抓取策略。 这指示 JPA 提供程序是否另外获取相关关联。 不幸的是，这个元配置是静态的，不允许在运行时在这两种策略之间切换。

JPA Entity Graph 的主要目标是在加载实体的相关关联和基本字段时提高运行时性能。

简而言之，JPA 提供程序在一个选择查询中加载所有图形，然后避免获取与更多 SELECT 查询的关联。这被认为是提高应用程序性能的好方法。

## 3. 定义模型

在我们开始探索实体图之前，我们需要定义我们正在使用的模型实体。假设我们要创建一个博客站点，用户可以在其中评论和分享帖子。

因此，首先我们将有一个User实体：

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    //...
}
```

用户可以分享各种帖子，所以我们还需要一个Post实体：

```java
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String subject;
    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User user;
    
    //...
}
```

用户还可以对共享的帖子发表评论，因此，最后，我们将添加一个Comment实体：

```java
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User user;
    
    //...
}
```

如我们所见，Post实体与Comment和User实体有关联。Comment实体与Post和User实体有关联。

目标是使用各种方式加载以下图表：

```plaintext
Post  ->  user:User
      ->  comments:List<Comment>
            comments[0]:Comment -> user:User
            comments[1]:Comment -> user:User
```

## 4.使用FetchType策略加载相关实体 

FetchType 方法定义了两种从数据库中获取数据的策略：

-   FetchType.EAGER：持久性提供者必须加载相关的注解字段或属性。这是@Basic、 @ManyToOne和@OneToOne 注解字段的默认行为
-   FetchType.LAZY：持久性提供程序应在首次访问数据时加载数据，但可以预先加载。这是@OneToMany、@ ManyToMany和@ElementCollection-注解字段的默认行为。

例如，当我们加载一个Post实体时，相关的Comment实体不会作为默认的 FetchType 加载，因为@OneToMany 是LAZY 。我们可以通过将FetchType更改 为EAGER来覆盖此行为：

```java
@OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
private List<Comment> comments = new ArrayList<>();
```

相比之下，当我们加载一个Comment实体时，他的Post父实体被加载为@ManyToOne 的默认模式， 即EAGER。我们还可以 通过将此注解更改为LAZY来选择不加载Post实体：

```java
@ManyToOne(fetch = FetchType.LAZY) 
@JoinColumn(name = "post_id") 
private Post post;
```

请注意，由于LAZY不是必需的，持久性提供程序仍然可以根据需要急切地加载Post实体。所以为了正确使用这个策略，我们应该回到相应的持久化提供者的官方文档。

现在，因为我们已经使用注解来描述我们的抓取策略，所以我们的定义是静态的，并且无法在运行时在LAZY和EAGER之间切换。

这就是实体图发挥作用的地方，我们将在下一节中看到。

## 5.定义实体图

要定义实体图，我们可以使用实体上的注解，也可以使用 JPA API 以编程方式进行。

### 5.1. 使用注解定义实体图

@NamedEntityGraph 注解允许在我们想要加载实体和相关关联时指定要包含的属性。

因此，让我们首先定义一个加载 Post及其相关实体 User和Comment的实体图：

```java
@NamedEntityGraph(
  name = "post-entity-graph",
  attributeNodes = {
    @NamedAttributeNode("subject"),
    @NamedAttributeNode("user"),
    @NamedAttributeNode("comments"),
  }
)
@Entity
public class Post {

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();
    
    //...
}
```

在此示例中，我们使用@NamedAttributeNode来定义加载根实体时要加载的相关实体。

现在让我们定义一个更复杂的实体图，我们还想在其中加载与Comment相关的User。

为此，我们将使用@NamedAttributeNode 子图属性。这允许引用通过@NamedSubgraph注解定义的命名子图：

```java
@NamedEntityGraph(
  name = "post-entity-graph-with-comment-users",
  attributeNodes = {
    @NamedAttributeNode("subject"),
    @NamedAttributeNode("user"),
    @NamedAttributeNode(value = "comments", subgraph = "comments-subgraph"),
  },
  subgraphs = {
    @NamedSubgraph(
      name = "comments-subgraph",
      attributeNodes = {
        @NamedAttributeNode("user")
      }
    )
  }
)
@Entity
public class Post {

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();
    //...
}
```

@NamedSubgraph注解的定义 类似于@NamedEntityGraph ，允许指定关联关联的属性。这样做，我们就可以构建一个完整的图。

在上面的示例中，使用定义的“ post-entity-graph-with-comment-users” 图，我们可以加载帖子、相关用户、评论 和与 评论相关的用户。

最后，请注意，我们可以选择使用orm.xml部署描述符添加实体图的定义：

```xml
<entity-mappings>
  <entity class="com.baeldung.jpa.entitygraph.Post" name="Post">
    ...
    <named-entity-graph name="post-entity-graph">
            <named-attribute-node name="comments" />
    </named-entity-graph>
  </entity>
  ...
</entity-mappings>
```

### 5.2. 使用 JPA API 定义实体图

我们还可以通过EntityManager API 调用createEntityGraph()方法来定义实体图：

```java
EntityGraph<Post> entityGraph = entityManager.createEntityGraph(Post.class);
```

要指定根实体的属性，我们使用addAttributeNodes()方法。

```java
entityGraph.addAttributeNodes("subject");
entityGraph.addAttributeNodes("user");
```

类似地，为了包含相关实体的属性，我们使用addSubgraph()构造一个嵌入式实体图，然后 像上面那样使用addAttributeNodes() 。

```java
entityGraph.addSubgraph("comments")
  .addAttributeNodes("user");
```

现在我们已经了解了如何创建实体图，我们将在下一节中探讨如何使用它。

## 6.使用实体图

### 6.1. 实体图的类型

JPA 定义了两个属性或提示，持久性提供程序可以通过它们来选择以便在运行时加载或获取实体图：

-   javax.persistence.fetchgraph – 仅从数据库中检索指定的属性。由于我们在本教程中使用 Hibernate，我们可以注意到，与 JPA 规范相比，静态配置为EAGER的属性也会被加载。
-   javax.persistence.loadgraph – 除了指定的属性外， 还检索静态配置为EAGER的属性。

在任何一种情况下，始终加载主键和版本(如果有)。

### 6.2. 加载实体图

我们可以使用各种方式检索实体图。

让我们从使用 EntityManager.find () 方法开始。 正如我们已经展示的，默认模式基于静态元策略FetchType.EAGER和FetchType.LAZY。

因此，让我们调用find()方法并检查日志：

```java
Post post = entityManager.find(Post.class, 1L);
```

这是 Hibernate 实现提供的日志：

```sql
select
    post0_.id as id1_1_0_,
    post0_.subject as subject2_1_0_,
    post0_.user_id as user_id3_1_0_ 
from
    Post post0_ 
where
    post0_.id=?
```

正如我们从日志中看到的，User和Comment实体没有被加载。

我们可以通过调用接受提示作为Map的重载find()方法来覆盖此默认行为。然后我们可以提供我们想要加载的图形类型：

```java
EntityGraph entityGraph = entityManager.getEntityGraph("post-entity-graph");
Map<String, Object> properties = new HashMap<>();
properties.put("javax.persistence.fetchgraph", entityGraph);
Post post = entityManager.find(Post.class, id, properties);
```

如果我们再次查看日志，我们可以看到这些实体现在已加载并且仅在一个选择查询中：

```sql
select
    post0_.id as id1_1_0_,
    post0_.subject as subject2_1_0_,
    post0_.user_id as user_id3_1_0_,
    comments1_.post_id as post_id3_0_1_,
    comments1_.id as id1_0_1_,
    comments1_.id as id1_0_2_,
    comments1_.post_id as post_id3_0_2_,
    comments1_.reply as reply2_0_2_,
    comments1_.user_id as user_id4_0_2_,
    user2_.id as id1_2_3_,
    user2_.email as email2_2_3_,
    user2_.name as name3_2_3_ 
from
    Post post0_ 
left outer join
    Comment comments1_ 
        on post0_.id=comments1_.post_id 
left outer join
    User user2_ 
        on post0_.user_id=user2_.id 
where
    post0_.id=?
```

让我们看看如何使用 JPQL 实现相同的目的：

```java
EntityGraph entityGraph = entityManager.getEntityGraph("post-entity-graph-with-comment-users");
Post post = entityManager.createQuery("select p from Post p where p.id = :id", Post.class)
  .setParameter("id", id)
  .setHint("javax.persistence.fetchgraph", entityGraph)
  .getSingleResult();
```

最后，让我们看一下Criteria API 示例：

```java
EntityGraph entityGraph = entityManager.getEntityGraph("post-entity-graph-with-comment-users");
CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
CriteriaQuery<Post> criteriaQuery = criteriaBuilder.createQuery(Post.class);
Root<Post> root = criteriaQuery.from(Post.class);
criteriaQuery.where(criteriaBuilder.equal(root.<Long>get("id"), id));
TypedQuery<Post> typedQuery = entityManager.createQuery(criteriaQuery);
typedQuery.setHint("javax.persistence.loadgraph", entityGraph);
Post post = typedQuery.getSingleResult();
```

在每一个中，图形类型都作为提示给出。在第一个示例中，我们使用了Map，而在后面的两个示例中，我们使用了setHint()方法。

## 七. 总结

在本文中，我们探索了使用 JPA 实体图来动态获取实体及其关联。

决定是在运行时做出的，我们选择加载或不加载相关关联。

性能显然是设计 JPA 实体时要考虑的关键因素。JPA 文档建议尽可能使用FetchType.LAZY 策略，并在我们需要加载关联时使用实体图。