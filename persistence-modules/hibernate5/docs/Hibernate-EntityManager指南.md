## 1. 简介

EntityManager是JavaPersistence API 的一部分。主要是，它实现了 JPA 2.0 规范定义的编程接口和生命周期规则。

此外，我们可以使用EntityManager中的 API 访问 Persistence Context 。

在本教程中，我们将了解EntityManager的配置、类型和各种 API 。

## 2.Maven依赖

首先，我们需要包含 Hibernate 的依赖项：

```java
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.4.0.Final</version>
</dependency>

```

根据我们使用的数据库，我们还必须包含驱动程序依赖项：

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.13</version>
</dependency>
```

Maven Central 上提供了hibernate [-core](https://search.maven.org/search?q=a:hibernate-core AND g:org.hibernate)和[mysql-connector-java依赖项。](https://search.maven.org/search?q=a:mysql-connector-java AND g:mysql)

## 三、配置

现在让我们 通过使用对应于数据库中的 MOVIE 表的Movie实体来演示EntityManager 。

在本文中，我们将使用EntityManager API 来处理数据库中的Movie对象。

### 3.1. 定义实体

让我们首先使用@Entity注解创建对应于 MOVIE 表的实体：

```java
@Entity
@Table(name = "MOVIE")
public class Movie {
    
    @Id
    private Long id;

    private String movieName;

    private Integer releaseYear;

    private String language;

    // standard constructor, getters, setters
}
```

### 3.2. persistence.xml文件_

创建EntityManagerFactory时，持久性实现会在类路径中搜索META-INF/persistence.xml文件。

此文件包含EntityManager的配置：

```xml
<persistence-unit name="com.baeldung.movie_catalog">
    <description>Hibernate EntityManager Demo</description>
    <class>com.baeldung.hibernate.pojo.Movie</class> 
    <exclude-unlisted-classes>true</exclude-unlisted-classes>
    <properties>
        <property name="hibernate.dialect" value="org.hibernate.dialect.MySQL5Dialect"/>
        <property name="hibernate.hbm2ddl.auto" value="update"/>
        <property name="javax.persistence.jdbc.driver" value="com.mysql.jdbc.Driver"/>
        <property name="javax.persistence.jdbc.url" value="jdbc:mysql://127.0.0.1:3306/moviecatalog"/>
        <property name="javax.persistence.jdbc.user" value="root"/>
        <property name="javax.persistence.jdbc.password" value="root"/>
    </properties>
</persistence-unit>
```

如我们所见，我们定义了指定由EntityManager管理的底层数据存储的持久性单元。

此外，我们定义了底层数据存储的方言和其他 JDBC 属性。Hibernate 与数据库无关。基于这些属性，Hibernate 连接底层数据库。

## 4.容器和应用管理的EntityManager

基本上，有两种类型的EntityManager：容器管理的和应用程序管理的。

让我们仔细看看每种类型。

### 4.1. 容器管理的实体管理器

在这里，容器将EntityManager注入到我们的企业组件中。

换句话说，容器为我们从EntityManagerFactory创建了EntityManager ：

```java
@PersistenceContext
EntityManager entityManager;

```

这也意味着 容器负责开始事务，以及提交或回滚事务。

同样，容器负责关闭 EntityManager， 因此 无需手动清理即可安全使用。即使我们尝试关闭容器管理的EntityManager，它也应该抛出 IllegalStateException。

### 4.2. 应用程序管理的实体管理器

相反，EntityManager的生命周期由应用程序管理。

事实上，我们将手动创建EntityManager并管理它的生命周期。

首先，让我们创建EntityManagerFactory：

```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.baeldung.movie_catalog");
```

为了创建EntityManager，我们必须在EntityManagerFactory中显式调用createEntityManager()：

```java
public static EntityManager getEntityManager() {
    return emf.createEntityManager();
}
```

由于我们负责创建EntityManager 实例，因此关闭它们也是我们的责任。 因此，我们应该 在使用完每个EntityManager后关闭 它们。

### 4.3. 线程安全

EntityManagerFactory 实例以及 Hibernate 的[SessionFactory](https://github.com/hibernate/hibernate-orm/blob/716a8bac2080fdcce349550e1817b14a100f3b74/hibernate-core/src/main/java/org/hibernate/SessionFactory.java#L32) 实例都是线程安全的。所以在并发上下文中写是完全安全的：

```java
EntityManagerFactory emf = // fetched from somewhere
EntityManager em = emf.createEntityManager();
```

另一方面，EntityManager 实例不是线程安全的，旨在用于线程受限环境中。这意味着每个线程都应该获取它的实例，使用它，并在最后关闭它。

使用应用程序管理的 EntityManager时，很容易创建线程受限实例：

```java
EntityManagerFactory emf = // fetched from somewhere 
EntityManager em = emf.createEntityManager();
// use it in the current thread
```

然而，当使用容器管理的EntityManager时，事情变得有悖常理：

```java
@Service
public class MovieService {

    @PersistenceContext // or even @Autowired
    private EntityManager entityManager;
    
    // omitted
}
```

似乎应该为所有操作共享一个EntityManager 实例。然而，容器(JakartaEE 或 Spring)在这里注入了一个特殊的代理而不是简单的EntityManager 。例如，Spring 注入了一个类型为[SharedEntityManagerCreator](https://github.com/spring-projects/spring-framework/blob/master/spring-orm/src/main/java/org/springframework/orm/jpa/SharedEntityManagerCreator.java#L71)的代理。 

每次我们使用注入的EntityManager 时， 这个代理将重用现有的EntityManager 或创建一个新的。[当我们在 View 中启用诸如 Open Session/EntityManager](https://www.baeldung.com/spring-open-session-in-view)之类的东西时，通常会发生重用。 

无论哪种方式，容器都会确保每个 EntityManager 都被限制在一个线程中。

## 5. Hibernate实体操作

EntityManager API 提供了一组方法。我们可以通过使用这些方法与数据库进行交互。

### 5.1. 持久实体

为了让一个对象与 EntityManager 相关联，我们可以使用persist()方法：

```java
public void saveMovie() {
    EntityManager em = getEntityManager();
    
    em.getTransaction().begin();
    
    Movie movie = new Movie();
    movie.setId(1L);
    movie.setMovieName("The Godfather");
    movie.setReleaseYear(1972);
    movie.setLanguage("English");

    em.persist(movie);
    em.getTransaction().commit();
}
```

一旦对象被保存在数据库中，它就处于持久状态。

### 5.2. 加载实体

为了从数据库中检索对象，我们可以使用find()方法。

在这里，该方法按主键搜索。事实上，该方法需要实体类类型和主键：

```java
public Movie getMovie(Long movieId) {
    EntityManager em = getEntityManager();
    Movie movie = em.find(Movie.class, new Long(movieId));
    em.detach(movie);
    return movie;
}
```

但是，如果我们只需要对实体的引用，我们可以改用getReference()方法。实际上，它返回实体的代理：

```java
Movie movieRef = em.getReference(Movie.class, new Long(movieId));
```

### 5.3. 分离实体

如果我们需要从持久化上下文中分离一个实体，我们可以使用detach()方法。我们将要分离的对象作为参数传递给方法：

```java
em.detach(movie);
```

一旦实体从持久性上下文中分离出来，它将处于分离状态。

### 5.4. 合并实体

实际上，许多应用程序需要跨多个事务修改实体。例如，我们可能希望在一个事务中检索一个实体以呈现给 UI。然后另一个事务将带来在 UI 中所做的更改。

对于这种情况，我们可以使用merge()方法。merge 方法有助于将对分离实体所做的任何修改带入托管实体： 

```java
public void mergeMovie() {
    EntityManager em = getEntityManager();
    Movie movie = getMovie(1L);
    em.detach(movie);
    movie.setLanguage("Italian");
    em.getTransaction().begin();
    em.merge(movie);
    em.getTransaction().commit();
}
```

### 5.5. 查询实体

此外，我们可以使用 JPQL 来查询实体。我们将调用getResultList()来执行它们。

当然， 如果查询只返回一个对象，我们可以使用getSingleResult() ：

```java
public List<?> queryForMovies() {
    EntityManager em = getEntityManager();
    List<?> movies = em.createQuery("SELECT movie from Movie movie where movie.language = ?1")
      .setParameter(1, "English")
      .getResultList();
    return movies;
}
```

### 5.6. 删除实体

此外，我们可以使用remove()方法从数据库中删除实体。重要的是要注意该对象不是分离的，而是被删除的。

在这里，实体的状态从持久变为新：

```java
public void removeMovie() {
    EntityManager em = HibernateOperations.getEntityManager();
    em.getTransaction().begin();
    Movie movie = em.find(Movie.class, new Long(1L));
    em.remove(movie);
    em.getTransaction().commit();
}
```

## 六. 总结

在本文中，我们探讨了Hibernate 中的EntityManager。我们查看了类型和配置，并了解了 API 中用于处理 Persistence Context 的各种可用方法。