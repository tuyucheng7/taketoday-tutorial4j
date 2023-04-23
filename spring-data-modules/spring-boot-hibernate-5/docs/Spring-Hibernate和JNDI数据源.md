## 1. 概述

在本文中，我们将使用带有[JNDI](https://en.wikipedia.org/wiki/Java_Naming_and_Directory_Interface)数据源的 Hibernate/JPA 创建一个 Spring 应用程序。

如果你想重新了解 Spring 和 Hibernate 的基础知识，请查看[这篇文章](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)。

## 2.声明数据源

### 2.1. 系统

因为我们使用的是 JNDI 数据源，所以我们不会在我们的应用程序中定义它，我们将在我们的应用程序容器中定义它。

在这个例子中，我们将使用 8.5.x 版本的[Tomcat](https://tomcat.apache.org/)和 9.5.x 版本的[PostgreSQL](https://www.postgresql.org/)数据库。

你应该能够使用任何其他Java应用程序容器和你选择的数据库来相同的步骤(只要你有合适的 JDBC jar！)。

### 2.2. 在应用程序容器上声明数据源

我们将在<GlobalNamingResources>元素内的<tomcat_home>/conf/server.xml文件中声明我们的数据源。

假设数据库服务器与应用程序容器在同一台机器上运行，并且预期的数据库名为postgres，并且用户名是baeldung密码pass1234，资源将如下所示：

```xml
<Resource name="jdbc/BaeldungDatabase" 
  auth="Container"
  type="javax.sql.DataSource" 
  driverClassName="org.postgresql.Driver"
  url="jdbc:postgresql://localhost:5432/postgres"
  username="baeldung" 
  password="pass1234" 
  maxTotal="20" 
  maxIdle="10" 
  maxWaitMillis="-1"/>
```

请注意，我们已将资源命名为jdbc/BaeldungDatabase。这将是引用此数据源时要使用的名称。

我们还必须指定其类型和数据库驱动程序的类名。为了让它工作，你还必须将相应的 jar 放在<tomcat_home>/lib/中(在本例中，是 PostgreSQL 的 JDBC jar)。

其余配置参数为：

-   auth=”Container” – 表示容器将代表应用程序登录到资源管理器
-   maxTotal、maxIdle和maxWaitMillis – 是池连接的配置参数

我们还必须在<tomcat_home>/conf/context .xml中的<Context>元素内定义一个ResourceLink ，它看起来像：

```xml
<ResourceLink 
  name="jdbc/BaeldungDatabase" 
  global="jdbc/BaeldungDatabase" 
  type="javax.sql.DataSource"/>
```

请注意，我们使用的是我们在server.xml中的Resource中定义的名称。

## 3. 使用资源

### 3.1. 设置应用程序

我们现在将使用纯Java配置定义一个简单的 Spring + JPA + Hibernate 应用程序。

我们将从定义 Spring 上下文的配置开始(请记住，我们在这里关注 JNDI 并假设你已经了解 Spring 配置的基础知识)：

```java
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:persistence-jndi.properties")
@ComponentScan("com.baeldung.hibernate.cache")
@EnableJpaRepositories(basePackages = "com.baeldung.hibernate.cache.dao")
public class PersistenceJNDIConfig {

    @Autowired
    private Environment env;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() 
      throws NamingException {
        LocalContainerEntityManagerFactoryBean em 
          = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        
        // rest of entity manager configuration
        return em;
    }

    @Bean
    public DataSource dataSource() throws NamingException {
        return (DataSource) new JndiTemplate().lookup(env.getProperty("jdbc.url"));
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    // rest of persistence configuration
}
```

[请注意，我们在Spring 4 and JPA with Hibernate](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)一文中提供了配置的完整示例。

为了创建我们的数据源 bean，我们需要查找我们在应用程序容器中定义的 JNDI 资源。我们会将其存储在persistence-jndi.properties键(以及其他属性)中：

```diff
jdbc.url=java:comp/env/jdbc/BaeldungDatabase
```

请注意，在jdbc.url 属性中，我们定义了一个要查找的根名称：java:comp/env/ (这是默认值，对应于组件和环境)，然后是我们在server.xml中使用的相同名称：jdbc/ Baeldung 数据库。

### 3.2. JPA 配置——模型、DAO 和服务

我们将使用一个带有@Entity注解的简单模型以及生成的id和name：

```java
@Entity
public class Foo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
 
    @Column(name = "NAME")
    private String name;

    // default getters and setters
}
```

让我们定义一个简单的存储库：

```java
@Repository
public class FooDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Foo> findAll() {
        return entityManager
          .createQuery("from " + Foo.class.getName()).getResultList();
    }
}
```

最后，让我们创建一个简单的服务：

```java
@Service
@Transactional
public class FooService {

    @Autowired
    private FooDao dao;

    public List<Foo> findAll() {
        return dao.findAll();
    }
}
```

有了它，你就拥有了在 Spring 应用程序中使用 JNDI 数据源所需的一切。

## 4. 总结

在本文中，我们创建了一个示例 Spring 应用程序，该应用程序具有 JPA + Hibernate 设置并使用 JNDI 数据源。

请注意，最重要的部分是应用程序容器中资源的定义和配置中 JNDI 资源的查找。