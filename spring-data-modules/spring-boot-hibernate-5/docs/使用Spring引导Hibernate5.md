## 1. 概述

在本文中，我们将讨论如何使用Java和 XML 配置通过 Spring 引导 Hibernate 5。

本文重点介绍 Spring MVC。我们的文章 [Spring Boot with Hibernate](https://www.baeldung.com/spring-boot-hibernate) 描述了如何在Spring Boot中使用 Hibernate。

## 2. 弹簧集成

使用本机 Hibernate API引导SessionFactory有点复杂，需要我们编写相当多的代码行(如果你确实需要这样做，请查看[官方文档)。](http://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#bootstrap-native)

幸运的是，Spring支持引导SessionFactory——因此我们只需要几行Java代码或 XML 配置。

## 3.Maven依赖

让我们首先向我们的pom.xml添加必要的依赖项：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.4.2.Final</version>
</dependency>
```

[spring-orm 模块](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework" AND a%3A"spring-orm")提供了 Spring 与 Hibernate 的集成：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-orm</artifactId>
    <version>5.1.6.RELEASE</version>
</dependency>
```

为了简单起见，我们将使用[H2](https://search.maven.org/classic/#search|gav|1|g%3A"com.h2database" AND a%3A"h2")作为我们的数据库：

```xml
<dependency>
    <groupId>com.h2database</groupId> 
    <artifactId>h2</artifactId>
    <version>1.4.197</version>
</dependency>
```

最后，我们将使用[Tomcat JDBC 连接池](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.tomcat" AND a%3A"tomcat-dbcp")，它比Spring 提供的DriverManagerDataSource更适合生产目的：

```xml
<dependency>
    <groupId>org.apache.tomcat</groupId>
    <artifactId>tomcat-dbcp</artifactId>
    <version>9.0.1</version>
</dependency>
```

## 4.配置

如前所述，Spring 支持我们引导 Hibernate SessionFactory。

我们所要做的就是定义一些 bean 以及一些参数。

使用 Spring，对于这些配置，我们有两种选择，一种是基于Java的方式，一种是基于 XML 的方式。

### 4.1. 使用Java配置

对于将 Hibernate 5 与 Spring 一起使用，自[Hibernate 4](https://www.baeldung.com/hibernate-4-spring)以来几乎没有变化：我们必须使用包org.springframework.orm.hibernate5中的LocalSessionFactoryBean而不是org.springframework.orm.hibernate4。

与之前的 Hibernate 4 一样，我们必须为LocalSessionFactoryBean、DataSource和PlatformTransactionManager定义 bean ，以及一些特定于 Hibernate 的属性。

让我们创建HibernateConfig类来使用 Spring 配置 Hibernate 5：

```java
@Configuration
@EnableTransactionManagement
public class HibernateConf {

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(
          {"com.baeldung.hibernate.bootstrap.model" });
        sessionFactory.setHibernateProperties(hibernateProperties());

        return sessionFactory;
    }

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager hibernateTransactionManager() {
        HibernateTransactionManager transactionManager
          = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }

    private final Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty(
          "hibernate.hbm2ddl.auto", "create-drop");
        hibernateProperties.setProperty(
          "hibernate.dialect", "org.hibernate.dialect.H2Dialect");

        return hibernateProperties;
    }
}
```

### 4.2. 使用 XML 配置

作为次要选项，我们还可以使用基于 XML 的配置来配置 Hibernate 5：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="...">

    <bean id="sessionFactory" 
      class="org.springframework.orm.hibernate5.LocalSessionFactoryBean">
        <property name="dataSource" 
          ref="dataSource"/>
        <property name="packagesToScan" 
          value="com.baeldung.hibernate.bootstrap.model"/>
        <property name="hibernateProperties">
            <props>
                <prop key="hibernate.hbm2ddl.auto">
                    create-drop
                </prop>
                <prop key="hibernate.dialect">
                    org.hibernate.dialect.H2Dialect
                </prop>
            </props>
        </property>
    </bean>

    <bean id="dataSource" 
      class="org.apache.tomcat.dbcp.dbcp2.BasicDataSource">
        <property name="driverClassName" value="org.h2.Driver"/>
        <property name="url" value="jdbc:h2:mem:db;DB_CLOSE_DELAY=-1"/>
        <property name="username" value="sa"/>
        <property name="password" value="sa"/>
    </bean>

    <bean id="txManager" 
      class="org.springframework.orm.hibernate5.HibernateTransactionManager">
        <property name="sessionFactory" ref="sessionFactory"/>
    </bean>
</beans>
```

正如我们可以很容易地看到的，我们正在定义与之前基于Java的配置中完全相同的 bean 和参数。

要将 XML 引导到 Spring 上下文中，如果应用程序配置有Java配置，我们可以使用一个简单的Java配置文件：

```java
@Configuration
@EnableTransactionManagement
@ImportResource({"classpath:hibernate5Configuration.xml"})
public class HibernateXMLConf {
    //
}
```

或者，如果整体配置是纯 XML，我们可以简单地将 XML 文件提供给 Spring 上下文。

## 5.用法

此时，Hibernate 5 已完全配置了 Spring，我们可以在需要时直接注入原始 Hibernate SessionFactory ：

```java
public abstract class BarHibernateDAO {

    @Autowired
    private SessionFactory sessionFactory;

    // ...
}
```

## 6. 支持的数据库

遗憾的是，Hibernate 项目并未准确提供受支持数据库的官方列表。

也就是说，很容易看出是否支持特定的数据库类型，我们可以查看[支持的方言列表](http://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#database-dialect)。

## 七. 总结

在本快速教程中，我们使用 Hibernate 5 配置了 Spring——同时使用了Java和 XML 配置。