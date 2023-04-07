## 一、概述

我们可以通过多种方式从 Java 连接到 MySQL 数据库，在本教程中，我们将探讨几个选项以了解如何实现这一点。

我们将从使用 JDBC 和 Hibernate 查看可以说是最流行的选项开始。

**然后，我们还将查看一些外部库，包括 MyBatis、Apache Cayenne 和 Spring Data**。在此过程中，我们将提供许多实际示例。

## **2.前提条件**

我们假设我们已经在本地主机（默认端口 3306）上安装并运行了一个 MySQL 服务器，并且我们有一个包含以下人员表的测试模式：

```sql
CREATE TABLE person 
( 
    ID         INT, 
    FIRST_NAME VARCHAR(100), 
    LAST_NAME  VARCHAR(100)  
);复制
```

我们还需要*mysql-connector-java*工件，它始终可以从[Maven Central](https://search.maven.org/classic/#search|ga|1|a%3A"mysql-connector-java" AND g%3A"mysql")获得：

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.19</version>
</dependency>复制
```

## **3.** **使用JDBC连接**

[JDBC](https://www.baeldung.com/java-jdbc)（Java 数据库连接）是一种用于连接数据库并执行查询的 API。

### 3.1. 公共属性

**在本文的过程中，我们通常会使用几个常见的 JDBC 属性**：

-   连接 URL – JDBC 驱动程序用于连接到数据库的字符串。它可以包含诸如在哪里搜索数据库、要连接的数据库名称和其他配置属性等信息：

    ```plaintext
    jdbc:mysql://[host][,failoverhost...]
        [:port]/[database]
        [?propertyName1][=propertyValue1]
        [&propertyName2][=propertyValue2]...复制
    ```

    我们将像这样设置此属性：*jdbc:mysql://localhost:3306/test?serverTimezone=UTC*

-   驱动程序类——要使用的[驱动程序](https://www.baeldung.com/java-jdbc#jdbc-drivers)的完全限定类名。在我们的例子中，我们将使用 MySQL 驱动程序：*com.mysql.cj.jdbc.Driver*

-   用户名和密码——MySQL 帐户的凭据

### 3.2. JDBC 连接示例

[让我们看看如何连接到我们的数据库并通过try-with-multiple-resources](https://www.baeldung.com/java-try-with-resources#resources)执行简单的全选：

```java
String sqlSelectAllPersons = "SELECT * FROM person";
String connectionUrl = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC";

try (Connection conn = DriverManager.getConnection(connectionUrl, "username", "password"); 
        PreparedStatement ps = conn.prepareStatement(sqlSelectAllPersons); 
        ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            long id = rs.getLong("ID");
            String name = rs.getString("FIRST_NAME");
            String lastName = rs.getString("LAST_NAME");

            // do something with the extracted data...
        }
} catch (SQLException e) {
    // handle the exception
}复制
```

正如我们所见，在*try*体内，我们遍历结果集并从 person 表中提取值。

## **4.** **使用 ORM 连接**

**更典型的是，我们将使用对象关系映射 (ORM) 框架连接到我们的 MySQL 数据库**。因此，让我们看一些使用这些框架中更受欢迎的连接示例。

### 4.1. 本机 Hibernate API

在本节中，我们将了解如何使用[Hibernate](https://hibernate.org/)来管理与数据库的 JDBC 连接。

首先，我们需要添加*[hibernate-core](https://search.maven.org/classic/#search|ga|1|g%3A"org.hibernate" AND a%3A"hibernate-core")* Maven 依赖：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.4.10.Final</version>
</dependency>复制
```

Hibernate 要求必须为每个表创建一个实体类。让我们继续定义*Person*类：

```java
@Entity
@Table(name = "Person")
public class Person {
    @Id
    Long id;
    @Column(name = "FIRST_NAME")
    String firstName;

    @Column(name = "LAST_NAME")
    String lastName;
    
    // getters & setters
}
复制
```

**另一个重要方面是创建 Hibernate 资源文件，通常名为\*hibernate.cfg.xml\***，我们将在其中定义配置信息：

```xml
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">

<hibernate-configuration>
    <session-factory>
        <!-- Database connection settings -->
        <property name="connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <property name="connection.url">jdbc:mysql://localhost:3306/test?serverTimezone=UTC</property>
        <property name="connection.username">username</property>
        <property name="connection.password">password</property>

        <!-- SQL dialect -->
        <property name="dialect">org.hibernate.dialect.MySQL5Dialect</property>

        <!-- Validate the database schema on startup -->
        <property name="hbm2ddl.auto">validate</property>

        <!-- Names the annotated entity class -->
        <mapping class="Person"/>
    </session-factory>
</hibernate-configuration>复制
```

Hibernate 有许多[配置属性](https://docs.jboss.org/hibernate/orm/3.3/reference/en/html/session-configuration.html)。除了标准连接属性外，值得一提的是方言属性，它允许我们为数据库指定 SQL 方言的名称。

框架使用此属性将[Hibernate 查询语言](https://docs.jboss.org/hibernate/core/3.3/reference/en/html/queryhql.html)(HQL) 语句正确转换为适合我们给定数据库的 SQL。Hibernate 附带了 40 多种[SQL 方言](https://docs.jboss.org/hibernate/orm/3.5/javadocs/org/hibernate/dialect/package-summary.html#package_description)。**由于我们在本文中关注 MySQL，因此我们将坚持使用\*MySQL5Dialect\*方言。**

最后，Hibernate 还需要通过映射标记知道实体类的完全限定名称。完成配置后，我们将使用*SessionFactory*类，该类负责创建和汇集 JDBC 连接。

通常，这只需要为一个应用程序设置一次：

```java
SessionFactory sessionFactory;
// configures settings from hibernate.cfg.xml 
StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build(); 
try {
    sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory(); 
} catch (Exception e) {
    // handle the exception
}复制
```

现在我们已经建立了连接，我们可以运行一个查询来从我们的人员表中选择所有人：

```java
Session session = sessionFactory.openSession();
session.beginTransaction();

List<Person> result = session.createQuery("from Person", Person.class).list();
        
result.forEach(person -> {
    //do something with Person instance...   
});
        
session.getTransaction().commit();
session.close();复制
```

### 4.2. MyBatis

**[MyBatis](https://github.com/mybatis/mybatis-3)于 2010 年推出，是一个以简单为强项的 SQL 映射器框架**。在另一个教程中，我们谈到了[如何将 MyBatis 与 Spring 和 Spring Boot 集成](https://www.baeldung.com/spring-mybatis)。在这里，我们将重点介绍如何直接配置 MyBatis。

要使用它，我们需要添加[*mybatis*](https://search.maven.org/classic/#artifactdetails|org.mybatis|mybatis|3.5.3|jar)依赖：

```xml
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.3</version>
</dependency>复制
```

假设我们在没有注释的情况下重用上面的*Person*类，我们可以继续创建一个*PersonMapper*接口：

```java
public interface PersonMapper {
    String selectAll = "SELECT * FROM Person"; 
    
    @Select(selectAll)
    @Results(value = {
       @Result(property = "id", column = "ID"),
       @Result(property = "firstName", column = "FIRST_NAME"),
       @Result(property = "lastName", column = "LAST_NAME")
    })
    List<Person> selectAll();
}复制
```

下一步是关于 MyBatis 配置的：

```java
Configuration initMybatis() throws SQLException {
    DataSource dataSource = getDataSource();
    TransactionFactory trxFactory = new JdbcTransactionFactory();
    
    Environment env = new Environment("dev", trxFactory, dataSource);
    Configuration config = new Configuration(env);
    TypeAliasRegistry aliases = config.getTypeAliasRegistry();
    aliases.registerAlias("person", Person.class);

    config.addMapper(PersonMapper.class);
    return config;
}

DataSource getDataSource() throws SQLException {
    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setDatabaseName("test");
    dataSource.setServerName("localhost");
    dataSource.setPort(3306);
    dataSource.setUser("username");
    dataSource.setPassword("password");
    dataSource.setServerTimezone("UTC");
    
    return dataSource;
}复制
```

**配置包括创建一个\*Configuration对象，它是\******Environment*****等设置的容器***。*它还包含数据源设置。

然后我们可以使用*Configuration*对象，它通常为应用程序设置一次来创建*SqlSessionFactory*：

```java
Configuration configuration = initMybatis();
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
try (SqlSession session = sqlSessionFactory.openSession()) {
    PersonMapper mapper = session.getMapper(PersonMapper.class);
    List<Person> persons = mapper.selectAll();
    
    // do something with persons list ...
}复制
```

### 4.3. 阿帕奇卡宴

[Apache Cayenne](https://cayenne.apache.org/)是一个持久性框架，其第一个版本可以追溯到 2002 年。要了解更多信息，我们建议阅读我们[对 Apache Cayenne 的介绍](https://www.baeldung.com/apache-cayenne-orm)。

像往常一样，让我们添加*[cayenne-server](https://search.maven.org/classic/#artifactdetails|org.apache.cayenne|cayenne-server|4.0.2|jar)* Maven 依赖项：

```xml
<dependency>
    <groupId>org.apache.cayenne</groupId>
    <artifactId>cayenne-server</artifactId>
    <version>4.0.2</version>
</dependency>复制
```

**我们将特别关注 MySQL 连接设置。在这种情况下，我们将配置\*cayenne-project.xml\***：

```xml
<?xml version="1.0" encoding="utf-8"?>
<domain project-version="9"> 
    <map name="datamap"/> 
	<node name="datanode" 
	    factory="org.apache.cayenne.configuration.server.XMLPoolingDataSourceFactory" 
		schema-update-strategy="org.apache.cayenne.access.dbsync.CreateIfNoSchemaStrategy"> 
	    <map-ref name="datamap"/> 
		<data-source>
		    <driver value="com.mysql.cj.jdbc.Driver"/> 
			<url value="jdbc:mysql://localhost:3306/test?serverTimezone=UTC"/> 
			<connectionPool min="1" max="1"/> 
			<login userName="username" password="password"/> 
		</data-source> 
	</node> 
</domain>复制
```

在以*[CayenneDataObject](https://cayenne.apache.org/docs/4.0/api/org/apache/cayenne/CayenneDataObject.html)*的形式自动生成*datamap.map.xml*和*Person*类之后，我们可以执行一些查询。

例如，我们将像以前一样继续全选：

```java
ServerRuntime cayenneRuntime = ServerRuntime.builder()
    .addConfig("cayenne-project.xml")
    .build();

ObjectContext context = cayenneRuntime.newContext();
List<Person> persons = ObjectSelect.query(Person.class).select(context);

// do something with persons list...复制
```

## **5.** **使用 Spring Data 连接**

[Spring Data](https://spring.io/projects/spring-data)是一种基于 Spring 的数据访问编程模型。从技术上讲，Spring Data 是一个伞式项目，其中包含许多特定于给定数据库的子项目。

让我们看看如何使用其中两个项目连接到 MySQL 数据库。

### 5.1. 弹簧数据/JPA

**Spring Data JPA 是一个健壮的框架，有助于减少样板代码，并提供一种机制，通过几个预定义的存储库接口之一实现基本的 CRUD 操作**。除此之外，它还有许多其他有用的功能。

请务必查看我们[对 Spring Data JPA 的介绍](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)以了解更多信息。

[可以在Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.data" AND a%3A"spring-data-jpa")上找到spring *-data-jpa*工件：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-jpa</artifactId>
    <version>2.2.4.RELEASE</version>
</dependency>复制
```

我们将继续使用*Person*类。下一步是使用注解配置 JPA：

```java
@Configuration
@EnableJpaRepositories("packages.to.scan")
public class JpaConfiguration {
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/test?serverTimezone=UTC");
        dataSource.setUsername( "username" );
        dataSource.setPassword( "password" );
        return dataSource;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
      return new JpaTransactionManager(emf);
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
      HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
      jpaVendorAdapter.setDatabase(Database.MYSQL);
      jpaVendorAdapter.setGenerateDdl(true);
      return jpaVendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
      LocalContainerEntityManagerFactoryBean lemfb = new LocalContainerEntityManagerFactoryBean();
      lemfb.setDataSource(dataSource());
      lemfb.setJpaVendorAdapter(jpaVendorAdapter());
      lemfb.setPackagesToScan("packages.containing.entity.classes");
      return lemfb;
    }
}复制
```

为了让 Spring Data 实现 CRUD 操作，我们必须创建一个扩展[*CrudRepository*](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html)接口的接口：

```java
@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

}复制
```

最后，让我们看一个使用 Spring Data 进行全选的示例：

```java
personRepository.findAll().forEach(person -> {
    // do something with the extracted person
});复制
```

### 5.2. 弹簧数据/JDBC

**Spring Data JDBC 是 Spring Data 系列的有限实现，其主要目标是允许对关系数据库进行简单访问**。

因此，它不提供诸如缓存、脏跟踪、延迟加载和许多其他 JPA 功能之类的功能。

这次我们需要的 Maven 依赖是*[spring-data-jdbc](https://search.maven.org/classic/#artifactdetails|org.springframework.data|spring-data-jdbc|1.1.4.RELEASE|jar)*：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-jdbc</artifactId>
    <version>1.1.4.RELEASE</version>
</dependency>复制
```

与我们在上一节中用于 Spring Data JPA 的配置相比，配置更轻：

```java
@Configuration
@EnableJdbcRepositories("packages.to.scan")
public class JdbcConfiguration extends AbstractJdbcConfiguration {
    // NamedParameterJdbcOperations is used internally to submit SQL statements to the database
    @Bean
    NamedParameterJdbcOperations operations() {
        return new NamedParameterJdbcTemplate(dataSource());
    }

    @Bean
    PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/test?serverTimezone=UTC");
        dataSource.setUsername("username");
        dataSource.setPassword("password");
        return dataSource;
    }
}复制
```

**在 Spring Data JDBC 的情况下，我们必须定义一个新的\*Person\*类或修改现有的类以添加一些 Spring 特定的注释**。

这是因为 Spring Data JDBC 将直接处理实体映射而不是 Hibernate：

```java
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(value = "Person")
public class Person {
    @Id
    Long id;

    @Column(value = "FIRST_NAME")
    String firstName;

    @Column(value = "LAST_NAME")
    String lastName;

    // getters and setters
}复制
```

通过 Spring Data JDBC，我们还可以使用*CrudRepository*接口。因此声明将与我们在上面的 Spring Data JPA 示例中编写的声明相同。同样，这同样适用于全选示例。

## 六，结论

**在本教程中，我们看到了从 Java 连接到 MySQL 数据库的几种不同方法**。我们从基本的 JDBC 连接开始。然后我们研究了常用的 ORM，如 Hibernate、Mybatis 和 Apache Cayenne。最后，我们了解了 Spring Data JPA 和 Spring Data JDBC。

使用 JDBC 或 Hibernate API 意味着更多样板代码。使用健壮的框架，如 Spring Data 或 Mybatis，需要更多配置，但具有显着优势，因为它们提供默认实现和功能，如缓存和延迟加载。