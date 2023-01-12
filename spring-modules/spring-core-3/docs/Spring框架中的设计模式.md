## 1. 概述

设计模式是软件开发的重要组成部分。这些解决方案不仅可以解决重复出现的问题，还可以帮助开发人员通过识别常见模式来理解框架的设计。

在本文中，我们将介绍Spring框架中使用的四种最常见的设计模式：

1. 单例模式
2. 工厂方法模式
3. 代理模式
4. 模板模式

我们还将介绍Spring如何使用这些模式来减轻开发人员的负担并帮助用户快速执行繁琐的任务。

## 2. 单例模式

**单例模式是一种确保每个类只存在一个对象实例的机制**。这种模式在管理共享资源或提供横切服务(例如日志记录)时很有用。

### 2.1 单例Bean

一般来说，单例对于应用程序来说是全局唯一的，但在Spring中，这个约束被放宽了。
相反，**Spring将单例限制为每个Spring IoC容器一个对象**。
实际上，这意味着Spring只会为每个应用程序上下文的每种类型创建一个bean。

Spring的单例不同于对单例的严格定义，因为一个应用程序可以有多个Spring容器。
因此，**如果我们有多个容器，则同一类的多个对象可以存在于单个应用程序中**。

<img src="../assets/spring-core-3-1.png">

**默认情况下，Spring将所有bean创建为单例**。

### 2.2 自动注入单例

例如，我们可以在单个应用程序上下文中创建两个控制器，并在每个控制器中注入一个相同类型的bean。

首先，我们创建一个BookRepository来管理我们的Book域对象。

```java
public class Book {

}

@Repository
public class BookRepository {

    public long count() {
        return 1;
    }

    public Optional<Book> findById(long id) {
        return Optional.of(new Book());
    }
}
```

接下来，我们创建LibraryController，它使用BookRepository返回图书馆中的书籍数量：

```java

@RestController
public class LibraryController {

    @Autowired
    private BookRepository repository;

    @GetMapping("/count")
    public Long findCount() {
        System.out.println(repository);
        return repository.count();
    }
}
```

最后，我们创建一个BookController，它专注特定于Book的操作，例如通过ID查找一本书：

```java

@RestController
public class BookController {

    @Autowired
    private BookRepository repository;

    @GetMapping("/book/{id}")
    public Book findById(@PathVariable long id) {
        System.out.println(repository);
        return repository.findById(id).get();
    }
}
```

然后我们启动这个应用程序并使用GET请求调用/count和/book/1：

```
curl -X GET http://localhost:8080/count
curl -X GET http://localhost:8080/book/1
```

在控制台的输出中，我们可以看到两个BookRepository对象具有相同的对象ID：

```
cn.tuyucheng.taketoday.spring.patterns.singleton.BookRepository@184b0785
cn.tuyucheng.taketoday.spring.patterns.singleton.BookRepository@184b0785
```

LibraryController和BookController中的BookRepository对象ID相同，证明Spring将相同的bean注入到两个控制器中。

**我们可以通过使用@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)注解将bean作用域从单例更改为原型来创建BookRepository
bean的单独实例**。

这样做会指示Spring为其创建的每个BookRepository bean创建单独的对象。
因此，如果我们再次检查每个控制器中BookRepository的对象ID，我们会发现它们不再相同。

## 3. 工厂方法模式

**工厂方法模式需要一个带有抽象方法的工厂类来创建所需的对象**。

通常，我们希望根据特定的上下文创建不同的对象。

例如，我们的应用程序可能需要一个汽车对象。在航海环境中，我们想要制造船只，但在航空环境中，我们想要制造飞机：

<img src="../assets/spring-core-3-2.png">

为此，我们可以为每个所需对象创建一个工厂实现，并从具体工厂方法返回所需对象。

### 3.1 Application Context

Spring在其依赖注入(DI)框架的基础上使用了这种技术。

从根本上说，**Spring将bean容器视为生产bean的工厂**。

因此，Spring将BeanFactory接口定义为bean容器的抽象：

```java
public interface BeanFactory {

    getBean(Class<T> requiredType);

    getBean(Class<T> requiredType, Object... args);

    getBean(String name);
    // ...
}
```

**每个getBean()方法都被认为是一个工厂方法**，它返回一个与提供给该方法的条件匹配的bean，例如bean的类型和名称。

然后Spring使用ApplicationContext接口扩展BeanFactory，该接口引入了额外的应用程序配置。
Spring使用此配置基于一些外部配置(例如XML文件或Java注解)启动bean容器。

使用像AnnotationConfigApplicationContext这样的ApplicationContext实现类，
我们可以通过从BeanFactory接口继承的各种工厂方法来创建bean。

首先，我们创建一个简单的应用程序配置类：

```java

@Configuration
@ComponentScan(basePackageClasses = ApplicationConfig.class)
public class ApplicationConfig {

}
```

接下来，我们创建一个不带有构造函数参数的简单类Foo：

```java

@Component
public class Foo {

}
```

然后创建另一个接收单个构造函数参数的类Bar：

```java

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Bar {
    private final String name;

    public Bar(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
```

最后，我们通过ApplicationContext的AnnotationConfigApplicationContext实现创建我们的bean：

```java
class AnnotationConfigApplicationContextUnitTest {

    @Test
    void whenGetSimpleBean_thenReturnConstructedBean() {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        Foo foo = context.getBean(Foo.class);
        assertNotNull(foo);
    }

    @Test
    void whenGetPrototypeBean_thenReturnConstructedBean() {
        String expectedName = "Some name";
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        Bar bar = context.getBean(Bar.class, expectedName);
        assertNotNull(bar);
        assertThat(bar.getName(), is(expectedName));
    }
}
```

使用getBean()工厂方法，我们可以只使用class类型和(在Bar的情况下)构造函数参数来创建配置的bean。

### 3.2 外部配置

这种模式是通用的，因为**我们可以根据外部配置完全改变应用程序的行为**。

如果我们希望更改应用程序中自动装配对象的实现，我们可以调整我们使用的ApplicationContext实现。

<img src="../assets/spring-core-3-3.png">

例如，我们可以将AnnotationConfigApplicationContext更改为基于XML的配置类，例如ClassPathXmlApplicationContext：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">

    <bean id="foo" class="cn.tuyucheng.taketoday.spring.patterns.factory.Foo"/>
    <bean id="bar" scope="prototype" class="cn.tuyucheng.taketoday.spring.patterns.factory.Bar"/>
</beans>
```

```java
class ClassPathXmlApplicationContextUnitTest {

    @Test
    void givenXmlConfiguration_whenGetSimpleBean_thenReturnConstructedBean() {
        ApplicationContext context = new ClassPathXmlApplicationContext("patterns-context.xml");
        Foo foo = context.getBean(Foo.class);
        assertNotNull(foo);
    }

    @Test
    void givenXmlConfiguration_whenGetPrototypeBean_thenReturnConstructedBean() {
        String expectedName = "Some name";
        ApplicationContext context = new ClassPathXmlApplicationContext("patterns-context.xml");
        Bar bar = context.getBean(Bar.class, expectedName);
        assertNotNull(bar);
        assertThat(bar.getName(), is(expectedName));
    }
}
```

## 4. 代理模式

代理在我们的数字世界中是一个方便的工具，我们经常在软件之外使用它们(例如网络代理)。
在代码中，**代理模式是一种允许一个对象(代理)控制对另一个对象(主体或服务)的访问的技术**。

<img src="../assets/spring-core-3-4.png">

### 4.1 事务

要创建代理，我们创建一个对象，该对象实现与主题相同的接口，并包含对主题的引用。

然后我们可以使用代理代替主题。

在Spring中，bean被代理来控制对底层bean的访问。我们在使用事务时可以看到这种模式的实现：

```java
public class Book {
    private String author;
    // constructors, getter and setter
}

@Repository
public class BookRepository {

    public Book create(String author) {
        return new Book(author);
    }
}

@Service
public class BookManager {

    @Autowired
    private BookRepository repository;

    @Transactional
    public Book create(String author) {
        System.out.println(repository.getClass().getName());
        return repository.create(author);
    }
}
```

在我们的BookManager类中，我们使用@Transactional注解来标注create()方法。
这个注解指示Spring以原子方式执行我们的create()方法。如果没有代理，Spring将无法控制对BookRepository bean的访问并确保其事务一致性。

### 4.2 CGLib代理

相反，**Spring创建了一个代理来包装我们的BookRepository bean**，并指示bean以原子方式执行创建create()方法。

当我们调用BookManager#create方法时，我们可以看到如下输出：

```
cn.tuyucheng.taketoday.spring.patterns.proxy.BookRepository$$EnhancerBySpringCGLIB$$bd1a5425
```

通常，我们希望看到一个标准的BookRepository对象ID；但是，我们看到的是EnhancerBySpringCGLIB对象ID。

在幕后，**Spring将我们的BookRepository对象包装为EnhancerBySpringCGLIB对象**。
因此，Spring控制对BookRepository对象的访问(确保事务一致性)。

<img src="../assets/spring-core-3-5.png">

通常，Spring使用两种类型的代理：

1. CGLib代理 – 代理类时使用。
2. JDK动态代理 – 代理接口时使用。

虽然在这里只是介绍事务中代理的使用，**但Spring会在必须控制对bean访问的任何场景中使用代理**。

## 5. 模板方法模式

在许多框架中，代码的很大一部分是样板代码。

例如，在对数据库执行查询时，必须完成相同的一系列步骤：

1. 建立连接
2. 执行查询
3. 执行清理
4. 关闭连接

这些步骤是模板方法模式的理想场景。

### 5.1 模板和回调

**模板方法模式是一种技术，它定义了某些操作所需的步骤，实现了样板步骤，并将可定制化的步骤保留为抽象**。
然后子类可以实现这个抽象类，并为缺少的步骤提供一个具体的实现。

我们可以在为数据库查询的情况下创建一个模板：

```java
public abstract class DatabaseQuery {

    public void execute() {
        Connection connection = createConnection();
        executeQuery(connection);
        closeConnection(connection);
    }

    protected Connection createConnection() {
        // Connect to database ...
    }

    protected void closeConnection(Connection connection) {
        // Close connection ...
    }

    protected abstract void executeQuery(Connection connection);
}
```

或者，我们可以通过提供回调方法来提供缺少的步骤。

**回调方法是一种允许主体向客户端发出信号表明某些所需操作已完成的方法**。

在某些情况下，主体可以使用此回调来执行操作，例如结果集映射。

<img src="../assets/spring-core-3-6.png">

例如，我们可以不使用executeQuery方法，而是为execute方法提供一个查询字符串和一个回调方法来处理结果。

首先，我们创建回调方法，该方法接收一个Results对象并将其映射到T类型的对象：

```java
public interface ResultsMapper<T> {
    T map(Results results);
}
```

然后更改我们的DatabaseQuery类来利用这个回调：

```java
public abstract class DatabaseQuery {

    public <T> T execute(String query, ResultsMapper<T> mapper) {
        Connection connection = createConnection();
        Results results = executeQuery(connection, query);
        closeConnection(connection);
        return mapper.map(results);
    }

    protected Results executeQuery(Connection connection, String query) {
        // Perform query ...
    }
}
```

这种回调机制正是Spring中JdbcTemplate类使用的方法。

### 5.2 JdbcTemplate

JdbcTemplate类提供了query方法，它接收一个查询字符串和ResultSetExtractor对象：

```java
public class JdbcTemplate {

    public <T> T query(final String sql, final ResultSetExtractor<T> rse) throws DataAccessException {
        // Execute query ...
    }

    // Other methods ...
}
```

ResultSetExtractor将ResultSet对象(表示查询结果)转换为T类型的实体对象：

```java

@FunctionalInterface
public interface ResultSetExtractor<T> {
    T extractData(ResultSet rs) throws SQLException, DataAccessException;
}
```

**Spring通过创建更具体的回调接口进一步减少了样板代码**。

例如，RowMapper接口用于将单行SQL数据转换为T类型的域对象。

```java

@FunctionalInterface
public interface RowMapper<T> {
    T mapRow(ResultSet rs, int rowNum) throws SQLException;
}
```

为了使RowMapper接口适配预期的ResultSetExtractor，Spring提供了RowMapperResultSetExtractor类：

```java
public class JdbcTemplate {

    public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        return result(query(sql, new RowMapperResultSetExtractor<>(rowMapper)));
    }
    // Other methods ...
}
```

我们可以提供如何转换单行数据的逻辑，而不是提供转换整个ResultSet对象的逻辑，包括对行的迭代：

```java
public class BookRowMapper implements RowMapper<Book> {

    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        return book;
    }
}
```

使用这个转换器，我们可以使用JdbcTemplate查询数据库并映射每个结果行：

```
JdbcTemplate template = // create template ...
template.query("select * from books", new BookRowMapper());
```

除了JDBC数据库管理之外，Spring大量使用了这种模板模式：

+ Java Message Service(JMS)
+ Java Persistence API(JPA)
+ Hibernate
+ Transactions

## 6. 总结

在本文中，我们讲解了Spring框架中应用的四种最常见的设计模式。

我们还探讨了Spring如何利用这些模式来提供丰富的功能，同时减轻开发人员的负担。