## 1. 概述

简单地说，Spring Boot自动配置帮助我们根据类路径中存在的依赖项自动配置Spring应用程序。

通过消除定义自动配置类中包含的某些bean的需要，这可以使开发更快、更容易。

在下一节中，我们将介绍如何**创建自定义的Spring Boot自动配置**。

## 2. Maven依赖

让我们从依赖项开始：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>2.4.0</version>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.19</version>
</dependency>
```

可以从Maven Central下载最新版本的[spring-boot-starter-data-jpa](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa/3.0.5)和[mysql-connector-java](https://central.sonatype.com/artifact/mysql/mysql-connector-java/8.0.32)。

## 3. 创建自定义自动配置

**为了创建自定义自动配置，我们需要创建一个标注为@Configuration的类并注册它**。

让我们为MySQL数据源创建自定义配置：

```java
@Configuration
public class MySQLAutoconfiguration {
    // ...
}
```

接下来，我们需要将该类注册为自动配置候选者。

我们通过在resources/META-INF/spring.factories文件中添加键为org.springframework.boot.autoconfigure.EnableAutoConfiguration，值为类的名称来做到这一点：

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  cn.tuyucheng.taketoday.autoconfiguration.MySQLAutoconfiguration
```

如果我们希望我们的自动配置类优先于其他候选类，我们可以添加@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)注解。

我们使用标有@Conditional注解的类和bean来设计自动配置，以便我们可以替换自动配置或它的特定部分。

**请注意，仅当我们未在应用程序中定义自动配置的bean时，自动配置才会有效。如果我们定义我们的bean，它将覆盖默认的bean**。

### 3.1 类条件

如果使用@ConditionalOnClass注解指定的类存在，或者如果使用@ConditionalOnMissingClass注解指定的类不存在，类条件允许我们指定要包含配置bean。

让我们指定我们的MySQLConfiguration只有在类DataSource存在时才会加载，在这种情况下，我们可以假设应用程序将使用数据库：

```java
@Configuration
@ConditionalOnClass(DataSource.class)
public class MySQLAutoconfiguration {
    // ...
}
```

### 3.2 Bean条件

如果我们只想**在指定的bean存在或不存在时才包含bean**，我们可以使用@ConditionalOnBean和@ConditionalOnMissingBean注解。

为了了解这一点，让我们添加一个entityManagerFactory bean到我们的配置类中。

首先，我们将指定如果存在名为dataSource的bean并且尚未定义名为entityManagerFactory的bean时，我们才创建此bean：

```java
@Bean
@ConditionalOnBean(name = "dataSource")
@ConditionalOnMissingBean
public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource());
    em.setPackagesToScan("cn.tuyucheng.taketoday.autoconfiguration.example");
    em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
    if (additionalProperties() != null)
        em.setJpaProperties(additionalProperties());
    return em;
}
```

我们还配置一个transactionManager bean，只有当我们还没有定义JpaTransactionManager类型的bean时才会加载：

```java
@Bean
@ConditionalOnMissingBean(type = "JpaTransactionManager")
JpaTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
    final JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory);
    return transactionManager;
}
```

### 3.3 属性条件

我们使用@ConditionalOnProperty注解来**指定是否基于Spring Environment属性的存在和值加载配置**。

首先，让我们为我们的配置类添加一个属性源文件，该文件将确定从何处读取属性：

```java
@PropertySource("classpath:mysql.properties")
public class MySQLAutoconfiguration {
    // ...
}
```

我们可以配置用于创建与数据库的连接的主DataSource bean，以便仅在存在名为usemysql的属性时才加载它。

我们可以使用@ConditionalOnProperty的属性havingValue来指定必须匹配的usemysql属性的某些值。

现在让我们使用默认值定义dataSource bean，如果我们将usemysql属性设置为local，则连接到名为myDb的本地数据库：

```java
@Bean
@ConditionalOnMissingBean
@ConditionalOnProperty(name = "usemysql", havingValue = "local")
public DataSource dataSource() {
    final DriverManagerDataSource dataSource = new DriverManagerDataSource();
    
    dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dataSource.setUrl("jdbc:mysql://localhost:3306/myDb?createDatabaseIfNotExist=true&&serverTimezone=UTC");
    dataSource.setUsername("root");
    dataSource.setUsername("tu001118");
    
    return dataSource;
}
```

如果我们将usemysql属性设置为custom，我们将使用数据库URL、用户名和密码的自定义属性值来配置dataSource bean：

```java
@Bean(name = "dataSource")
@ConditionalOnProperty(name = "usemysql", havingValue = "custom")
@ConditionalOnMissingBean
public DataSource dataSource2() {
    final DriverManagerDataSource dataSource = new DriverManagerDataSource();
    
    dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dataSource.setUrl(environment.getProperty("mysql.url"));
    dataSource.setUsername(environment.getProperty("mysql.user") != null ? environment.getProperty("mysql.user") : "");
    dataSource.setPassword(environment.getProperty("mysql.pass") != null ? environment.getProperty("mysql.pass") : "");
    
    return dataSource;
}
```

mysql.properties文件将包含usemysql属性：

```properties
# mysql.properties
usemysql=local
```

使用MySQLAutoconfiguration的应用程序可能需要覆盖默认属性。在这种情况下，只需要为mysql.url、mysql.user和mysql.pass属性以及mysql.properties文件中的usemysql=custom添加不同的值。

### 3.4 资源条件

添加@ConditionalOnResource注解意味着**仅当存在指定的资源时，才会加载配置**。

让我们定义一个名为additionalProperties()的方法，该方法将返回一个Properties对象，其中包含要由entityManagerFactory bean使用的Hibernate特定属性，前提是资源文件mysql.properties存在：

```java
@ConditionalOnResource(resources = "classpath:mysql.properties")
@Conditional(HibernateCondition.class)
final Properties additionalProperties() {
    final Properties hibernateProperties = new Properties();
    
    hibernateProperties.setProperty("hibernate.hbm2ddl.auto", environment.getProperty("mysql-hibernate.hbm2ddl.auto"));
    hibernateProperties.setProperty("hibernate.dialect", environment.getProperty("mysql-hibernate.dialect"));
    hibernateProperties.setProperty("hibernate.show_sql", environment.getProperty("mysql-hibernate.show_sql") != null ? environment.getProperty("mysql-hibernate.show_sql") : "false");
      
    return hibernateProperties;
}
```

我们可以将特定于Hibernate的属性添加到mysql.properties文件中：

```properties
mysql-hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
mysql-hibernate.show_sql=true
mysql-hibernate.hbm2ddl.auto=create-drop
```

### 3.5 自定义条件

假设我们不想使用Spring Boot中可用的任何条件。

我们还可以**通过扩展SpringBootCondition类并重写getMatchOutcome()方法来自定义条件**。

让我们为additionalProperties()方法创建一个名为HibernateCondition的条件，该条件将验证HibernateEntityManager类是否存在于类路径中：

```java
static class HibernateCondition extends SpringBootCondition {

    private static final String[] CLASS_NAMES = {"org.hibernate.ejb.HibernateEntityManager", "org.hibernate.jpa.HibernateEntityManager"};

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConditionMessage.Builder message = ConditionMessage.forCondition("Hibernate");
        return Arrays.stream(CLASS_NAMES).filter(className -> ClassUtils.isPresent(className, context.getClassLoader()))
              .map(className -> ConditionOutcome.match(message.found("class").items(ConditionMessage.Style.NORMAL))).findAny()
              .orElseGet(() -> ConditionOutcome.noMatch(message.didNotFind("class", "classes").items(ConditionMessage.Style.NORMAL, Arrays.asList(CLASS_NAMES))));
    }
}
```

然后我们可以将条件添加到additionalProperties()方法上：

```java
@Conditional(HibernateCondition.class)
Properties additionalProperties() {
    // ...
}
```

### 3.6 ApplicationContext条件

我们还可以**指定配置只能在Web上下文内部/外部加载**。为此，我们可以添加@ConditionalOnWebApplication或@ConditionalOnNotWebApplication注解。

## 4. 测试自动配置

让我们创建一个非常简单的示例来测试我们的自动配置。

我们将使用Spring Data创建一个名为MyUser的实体类和一个MyUserRepository接口：

```java
@Entity
public class MyUser {
    @Id
    private String email;

    // standard constructor, getters, setters
}
```

```java
public interface MyUserRepository extends JpaRepository<MyUser, String> {
}
```

为了启用自动配置，我们可以使用@SpringBootApplication或@EnableAutoConfiguration注解：

```java
@SpringBootApplication
public class AutoconfigurationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoconfigurationApplication.class, args);
    }
}
```

接下来，让我们编写一个保存MyUser实体的JUnit测试：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AutoconfigurationApplication.class)
@EnableJpaRepositories(basePackages = {"cn.tuyucheng.taketoday.autoconfiguration.example"})
class AutoconfigurationLiveTest {

    @Autowired
    private MyUserRepository userRepository;

    @Test
    void whenSaveUser_thenOk() {
        MyUser user = new MyUser("user@email.com");
        userRepository.save(user);
    }
}
```

由于我们没有定义DataSource配置，因此应用程序将使用我们创建的自动配置连接到名为myDB的MySQL数据库。

**连接字符串包含createDatabaseIfNotExist=true属性，因此数据库不需要存在。但是，需要创建用户mysqluser或通过mysql.user属性指定的用户(如果存在)**。

我们可以检查应用程序日志来查看我们正在使用的是否是MySQL数据源：

```shell
web - 2017-04-12 00:01:33,956 [main] INFO  o.s.j.d.DriverManagerDataSource - Loaded JDBC driver: com.mysql.cj.jdbc.Driver
```

## 5. 禁用自动配置类

假设我们要从加载中排除自动配置。

我们可以将带有exclude或excludeName属性的@EnableAutoConfiguration注解添加到配置类上：

```java
@Configuration
@EnableAutoConfiguration(exclude = {MySQLAutoconfiguration.class})
public class AutoconfigurationApplication {
    // ...
}
```

我们还可以设置spring.autoconfigure.exclude属性：

```properties
spring.autoconfigure.exclude=cn.tuyucheng.taketoday.autoconfiguration.MySQLAutoconfiguration
```

## 6. 总结

在本文中，我们演示了如何创建自定义的Spring Boot自动配置。

该示例的完整源代码可以在[GitHub](https://github.com/tu-yucheng/spring-boot-examples/tree/master/spring-boot-autoconfiguration)上找到。

JUnit测试可以使用autoconfiguration Profile运行mvn clean install -Pautoconfiguration。