## 1. 概述

在本教程中，我们将为**具有多个数据库的Spring Data JPA系统**实现一个简单的Spring配置。

## 延伸阅读

### [Spring Data JPA-派生的删除方法](https://www.baeldung.com/spring-data-jpa-deleteby)

了解如何定义Spring Data deleteBy和removeBy方法

[阅读更多](https://www.baeldung.com/spring-data-jpa-deleteby)→

### [在Spring Boot中以编程方式配置数据源](https://www.baeldung.com/spring-boot-configure-data-source-programmatic)

了解如何以编程方式配置Spring Boot数据源，从而避开Spring Boot的自动数据源配置算法。

[阅读更多](https://www.baeldung.com/spring-boot-configure-data-source-programmatic)→

## 2. 实体

首先，让我们创建两个简单的实体，每个都存在于一个单独的数据库中。

这是第一个用户实体：

```java
package cn.tuyucheng.taketoday.multipledb.model.user;

@Entity
@Table(schema = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private int age;
}
```

这是第二个产品实体：

```java
package cn.tuyucheng.taketoday.multipledb.model.product;

@Entity
@Table(schema = "products")
public class Product {

    @Id
    private int id;

    private String name;

    private double price;
}
```

我们可以看到**这两个实体也被放在了独立的包中**。当我们进入配置时，这将很重要。

## 3. JPA Repository

接下来，让我们看一下我们的两个JPA Repository，UserRepository：

```java
package cn.tuyucheng.taketoday.multipledb.dao.user;

public interface UserRepository extends JpaRepository<User, Integer> {
}
```

和ProductRepository：

```java
package cn.tuyucheng.taketoday.multipledb.dao.product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
```

再次注意，这两个Repository也是放在不同的包中。

## 4. 使用Java配置JPA

现在我们将进入实际的Spring配置。**我们需要首先设置两个配置类-一个用于User，另一个用于Product**。

在每个配置类中，我们需要为User定义以下接口：

-   DataSource
-   EntityManagerFactory(userEntityManager)
-   TransactionManager(userTransactionManager)

让我们先看一下用户配置：

```java
@Configuration
@PropertySource({"classpath:persistence-multiple-db.properties"})
@EnableJpaRepositories(
      basePackages = "cn.tuyucheng.taketoday.multipledb.dao.user",
      entityManagerFactoryRef = "userEntityManager",
      transactionManagerRef = "userTransactionManager"
)
@Profile("!tc")
public class PersistenceUserConfiguration {
    @Autowired
    private Environment env;

    public PersistenceUserConfiguration() {
        super();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean userEntityManager() {
        System.out.println("loading config");
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(userDataSource());
        em.setPackagesToScan("cn.tuyucheng.taketoday.multipledb.model.user");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        final HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Primary
    @Bean
    public DataSource userDataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Preconditions.checkNotNull(env.getProperty("jdbc.driverClassName")));
        dataSource.setUrl(Preconditions.checkNotNull(env.getProperty("user.jdbc.url")));
        dataSource.setUsername(Preconditions.checkNotNull(env.getProperty("jdbc.user")));
        dataSource.setPassword(Preconditions.checkNotNull(env.getProperty("jdbc.pass")));

        return dataSource;
    }

    @Primary
    @Bean
    public PlatformTransactionManager userTransactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(userEntityManager().getObject());
        return transactionManager;
    }
}
```

注意我们如何通过使用@Primary标注bean定义来使用userTransactionManager作为我们的**主TransactionManager**。每当我们要隐式或显式地注入事务管理器而不指定名称时，这都会很有帮助。

接下来，让我们讨论PersistenceProductConfiguration，我们在其中定义了类似的bean：

```java
@Configuration
@PropertySource({"classpath:persistence-multiple-db.properties"})
@EnableJpaRepositories(
      basePackages = "cn.tuyucheng.taketoday.multipledb.dao.product",
      entityManagerFactoryRef = "productEntityManager",
      transactionManagerRef = "productTransactionManager"
)
@Profile("!tc")
public class PersistenceProductConfiguration {
    @Autowired
    private Environment env;

    public PersistenceProductConfiguration() {
        super();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean productEntityManager() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(productDataSource());
        em.setPackagesToScan("cn.tuyucheng.taketoday.multipledb.model.product");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        final HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public DataSource productDataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Preconditions.checkNotNull(env.getProperty("jdbc.driverClassName")));
        dataSource.setUrl(Preconditions.checkNotNull(env.getProperty("product.jdbc.url")));
        dataSource.setUsername(Preconditions.checkNotNull(env.getProperty("jdbc.user")));
        dataSource.setPassword(Preconditions.checkNotNull(env.getProperty("jdbc.pass")));

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager productTransactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(productEntityManager().getObject());
        return transactionManager;
    }
}
```

## 5. 简单测试

最后，让我们测试一下我们的配置。

为此，我们将为每个实体创建一个实例并确保它已创建：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnableTransactionManagement
class JpaMultipleDBIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Transactional("userTransactionManager")
    void whenCreatingUser_thenCreated() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@test.com");
        user.setAge(20);
        user = userRepository.save(user);

        assertNotNull(userRepository.findOne(user.getId()));
    }

    @Test
    @Transactional("userTransactionManager")
    void whenCreatingUsersWithSameEmail_thenRollback() {
        User user1 = new User();
        user1.setName("John");
        user1.setEmail("john@test.com");
        user1.setAge(20);
        user1 = userRepository.save(user1);
        assertNotNull(userRepository.findOne(user1.getId()));

        User user2 = new User();
        user2.setName("Tom");
        user2.setEmail("john@test.com");
        user2.setAge(10);
        try {
            user2 = userRepository.save(user2);
        } catch (DataIntegrityViolationException ignored) {
        }

        assertNull(userRepository.findOne(user2.getId()));
    }

    @Test
    @Transactional("productTransactionManager")
    void whenCreatingProduct_thenCreated() {
        Product product = new Product();
        product.setName("Book");
        product.setId(2);
        product.setPrice(20);
        product = productRepository.save(product);

        assertNotNull(productRepository.findOne(product.getId()));
    }
}
```

## 6. Spring Boot中的多个数据库

对于Spring Boot来说，可以简化上面的配置。

默认情况下，**Spring Boot将使用以spring.datasource.\*为前缀的配置属性实例化其默认DataSource**：

```properties
spring.datasource.jdbcUrl=[url]
spring.datasource.username=[username]
spring.datasource.password=[password]
```

我们现在希望继续使用相同的方式来**配置第二个DataSource，但使用不同的属性命名空间**：

```properties
spring.second-datasource.jdbcUrl=[url]
spring.second-datasource.username=[username]
spring.second-datasource.password=[password]
```

因为我们希望Spring Boot自动配置获取这些不同的属性(并实例化两个不同的DataSources)，所以我们将定义两个类似于前面部分的配置类：

```java
@Configuration
@PropertySource({"classpath:persistence-multiple-db-boot.properties"})
@EnableJpaRepositories(
      basePackages = "cn.tuyucheng.taketoday.multipledb.dao.user",
      entityManagerFactoryRef = "userEntityManager",
      transactionManagerRef = "userTransactionManager"
)
public class PersistenceUserAutoConfiguration {

    @Primary
    @Bean
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource userDataSource() {
        return DataSourceBuilder.create().build();
    }
    // userEntityManager bean 

    // userTransactionManager bean
}
```

```java
@Configuration
@PropertySource({"classpath:persistence-multiple-db-boot.properties"})
@EnableJpaRepositories(
      basePackages = "cn.tuyucheng.taketoday.multipledb.dao.product",
      entityManagerFactoryRef = "productEntityManager",
      transactionManagerRef = "productTransactionManager"
)
public class PersistenceProductAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix="spring.second-datasource")
    public DataSource productDataSource() {
        return DataSourceBuilder.create().build();
    }

    // productEntityManager bean 

    // productTransactionManager bean
}
```

现在我们已经根据Spring Boot自动配置约定在persistence-multiple-db-boot.properties中定义了数据源属性。

有趣的部分是**使用@ConfigurationProperties标注数据源bean创建方法**，我们只需要指定相应的配置前缀即可。在此方法中，我们使用了DataSourceBuilder，Spring Boot将自动处理其余部分。

但是如何将配置的属性注入到DataSource配置中呢？

在DataSourceBuilder上调用build()方法时，它将调用其私有的bind()方法：

```java
public T build() {
    Class<? extends DataSource> type = getType();
    DataSource result = BeanUtils.instantiateClass(type);
    maybeGetDriverClassName();
    bind(result);
    return (T) result;
}
```

这个私有方法执行了很多自动配置魔法，将解析的配置绑定到实际的DataSource实例：

```java
private void bind(DataSource result) {
    ConfigurationPropertySource source = new MapConfigurationPropertySource(this.properties);
    ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases();
    aliases.addAliases("url", "jdbc-url");
    aliases.addAliases("username", "user");
    Binder binder = new Binder(source.withAliases(aliases));
    binder.bind(ConfigurationPropertyName.EMPTY, Bindable.ofInstance(result));
}
```

尽管我们自己不必接触任何这些代码，但了解Spring Boot自动配置的幕后情况仍然很有用。

除此之外，事务管理器和实体管理器bean配置与标准Spring应用程序相同。

## 7. 总结

本文是关于如何配置我们的Spring Data JPA项目以使用多个数据库的实用概述。