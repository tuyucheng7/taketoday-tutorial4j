## 一、概述

通常，存储库和 DAO 的实现被认为是可以互换的，尤其是在以数据为中心的应用程序中。这造成了对它们之间差异的混淆。

在本文中，我们将讨论 DAO 和存储库模式之间的差异。

## 2. DAO Pattern

数据访问对象模式，又名[DAO 模式](https://www.baeldung.com/java-dao-pattern)，是数据持久性的抽象，被认为更接近通常以表为中心的底层存储。

因此，在许多情况下，我们的 DAO 匹配数据库表，允许更直接的方式从存储发送/检索数据，隐藏丑陋的查询。

让我们检查一下 DAO 模式的简单实现。

### 2.1. 用户

首先，让我们创建一个基本的用户域类：

```java
public class User {
    private Long id;
    private String userName;
    private String firstName;
    private String email;

    // getters and setters
}
```

### 2.2. 用户道

然后，我们将创建UserDao接口，为User域提供简单的 CRUD 操作：

```java
public interface UserDao {
    void create(User user);
    User read(Long id);
    void update(User user);
    void delete(String userName);
}
```

### 2.3. UserDaoImpl

最后，我们将创建实现UserDao接口的UserDaoImpl类：

```java
public class UserDaoImpl implements UserDao {
    private final EntityManager entityManager;
    
    @Override
    public void create(User user) {
        entityManager.persist(user);
    }

    @Override
    public User read(long id) {
        return entityManager.find(User.class, id);
    }

    // ...
}
```

在这里，为简单起见，我们使用[JPA EntityManager接口](https://www.baeldung.com/hibernate-entitymanager)与底层存储进行交互，并为用户域提供数据访问机制。

## 3. 存储库模式

根据埃里克·埃文斯 (Eric Evans) 的《[领域驱动设计](https://www.pearson.com/store/p/domain-driven-design-tackling-complexity-in-the-heart-of-software/P100000775942/9780321125217)[》一书](https://www.pearson.com/store/p/domain-driven-design-tackling-complexity-in-the-heart-of-software/P100000775942/9780321125217)，“存储库是一种用于封装存储、检索和搜索行为的机制，它模拟了一组对象。”

同样，根据[企业应用程序架构模式](https://www.pearson.com/store/p/patterns-of-enterprise-application-architecture/P100001391761/9780321127426#)，它“使用类似集合的接口在域和数据映射层之间进行调解，以访问域对象。”

换句话说，存储库也处理数据并隐藏类似于 DAO 的查询。但是，它位于更高的层次，更接近应用程序的业务逻辑。

因此，存储库可以使用 DAO 从数据库中获取数据并填充域对象。或者，它可以从域对象准备数据并使用 DAO 将其发送到存储系统以实现持久性。

让我们检查用户域的存储库模式的简单实现。

### 3.1. 用户资料库

首先，让我们创建UserRepository接口：

```java
public interface UserRepository {
    User get(Long id);
    void add(User user);
    void update(User user);
    void remove(User user);
}
```

在这里，我们添加了一些常用方法，如get、add、update和remove来处理对象集合。

### 3.2. UserRepositoryImpl

然后，我们将创建UserRepositoryImpl类，提供UserRepository接口的实现：


```java
public class UserRepositoryImpl implements UserRepository {
    private UserDaoImpl userDaoImpl;
    
    @Override
    public User get(Long id) {
        User user = userDaoImpl.read(id);
        return user;
    }

    @Override
    public void add(User user) {
        userDaoImpl.create(user);
    }

    // ...
}
```

在这里，我们使用了UserDaoImpl从数据库发送/检索数据。

到目前为止，我们可以说 DAO 和存储库的实现看起来非常相似，因为User类是一个贫血域。而且，存储库只是数据访问层 (DAO) 之上的另一层。

然而，DAO 似乎是访问数据的完美候选者，而存储库是实现业务用例的理想方式。

## 4. 具有多个 DAO 的存储库模式

为了清楚地理解最后一句话，让我们增强我们的用户域来处理业务用例。

想象一下，我们想要通过聚合用户的 Twitter 推文、Facebook 帖子等来准备用户的社交媒体资料。

### 4.1. 鸣叫

首先，我们将创建Tweet类，其中包含一些保存推文信息的属性：

```java
public class Tweet {
    private String email;
    private String tweetText;    
    private Date dateCreated;

    // getters and setters
}
```

### 4.2. TweetDao和TweetDaoImpl

然后，类似于UserDao，我们将创建允许获取推文的TweetDao接口：

```java
public interface TweetDao {
    List<Tweet> fetchTweets(String email);    
}
```

同样，我们将创建提供fetchTweets方法实现的TweetDaoImpl类：

```java
public class TweetDaoImpl implements TweetDao {
    @Override
    public List<Tweet> fetchTweets(String email) {
        List<Tweet> tweets = new ArrayList<Tweet>();
        
        //call Twitter API and prepare Tweet object
        
        return tweets;
    }
}
```

在这里，我们将调用 Twitter API 来获取用户使用其电子邮件发送的所有推文。

因此，在这种情况下，DAO 提供了一种使用第三方 API 的数据访问机制。

### 4.3. 增强用户域

最后，让我们创建User类的UserSocialMedia子类来保存Tweet对象的列表：

```java
public class UserSocialMedia extends User {
    private List<Tweet> tweets;

    // getters and setters
}
```

在这里，我们的UserSocialMedia类是一个复杂的域，也包含User域的属性。

### 4.4. UserRepositoryImpl

现在，我们将升级我们的UserRepositoryImpl类以提供一个User域对象以及一个推文列表：


```java
public class UserRepositoryImpl implements UserRepository {
    private UserDaoImpl userDaoImpl;
    private TweetDaoImpl tweetDaoImpl;
    
    @Override
    public User get(Long id) {
        UserSocialMedia user = (UserSocialMedia) userDaoImpl.read(id);
        
        List<Tweet> tweets = tweetDaoImpl.fetchTweets(user.getEmail());
        user.setTweets(tweets);
        
        return user;
    }
}
```

在这里，UserRepositoryImpl 使用 UserDaoImpl提取用户数据，使用TweetDaoImpl提取用户的推文。

然后，它聚合两组信息并提供UserSocialMedia类的域对象，这对我们的业务用例很方便。因此，存储库依赖于 DAO 来访问来自各种来源的数据。

同样，我们可以增强我们的用户域以保留 Facebook 帖子列表。

## 5.比较两种模式

现在我们已经了解了 DAO 和 Repository 模式的细微差别，让我们总结一下它们的区别：

-   DAO 是数据持久化的抽象。但是，存储库是对象集合的抽象
-   DAO 是一个较低级别的概念，更接近于存储系统。然而，Repository 是一个更高层次的概念，更接近于 Domain 对象
-   DAO 用作数据映射/访问层，隐藏丑陋的查询。但是，存储库是域和数据访问层之间的层，隐藏了整理数据和准备域对象的复杂性
-   不能使用存储库实现 DAO。但是，存储库可以使用 DAO 来访问底层存储

此外，如果我们有一个贫血域，存储库将只是一个 DAO。

此外，存储库模式鼓励领域驱动设计，也为非技术团队成员提供了对数据结构的简单理解。

## 六，总结

在本文中，我们探讨了 DAO 和存储库模式之间的差异。

首先，我们检查了 DAO 模式的基本实现。然后，我们看到了使用 Repository 模式的类似实现。

最后，我们研究了利用多个 DAO 的存储库，增强了域解决业务用例的能力。

因此，我们可以得出总结，当应用程序从以数据为中心转变为以业务为导向时，存储库模式证明是一种更好的方法。