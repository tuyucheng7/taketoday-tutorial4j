## 1. 概述

本文将重点介绍如何使用Spring 设置 Hibernate 3——我们将了解如何使用 XML 和Java配置来设置 Spring 以及 Hibernate 3 和 MySQL。

更新：本文关注的是 Hibernate 3。如果你正在寻找当前版本的 Hibernate –[这篇文章关注的是它](https://www.baeldung.com/hibernate-5-spring)。

## 2. Hibernate 3 的Java Spring 配置

使用 Spring 和Java配置设置 Hibernate 3 非常简单：

```java
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.google.common.base.Preconditions;

@Configuration
@EnableTransactionManagement
@PropertySource({ "classpath:persistence-mysql.properties" })
@ComponentScan({ "com.baeldung.spring.persistence" })
public class PersistenceConfig {

   @Autowired
   private Environment env;

   @Bean
   public AnnotationSessionFactoryBean sessionFactory() {
      AnnotationSessionFactoryBean sessionFactory = new AnnotationSessionFactoryBean();
      sessionFactory.setDataSource(restDataSource());
      sessionFactory.setPackagesToScan(new String[] { "com.baeldung.spring.persistence.model" });
      sessionFactory.setHibernateProperties(hibernateProperties());

      return sessionFactory;
   }

   @Bean
   public DataSource restDataSource() {
      BasicDataSource dataSource = new BasicDataSource();
      dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
      dataSource.setUrl(env.getProperty("jdbc.url"));
      dataSource.setUsername(env.getProperty("jdbc.user"));
      dataSource.setPassword(env.getProperty("jdbc.pass"));

      return dataSource;
   }

   @Bean
   @Autowired
   public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
      HibernateTransactionManager txManager = new HibernateTransactionManager();
      txManager.setSessionFactory(sessionFactory);

      return txManager;
   }

   @Bean
   public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
      return new PersistenceExceptionTranslationPostProcessor();
   }

   Properties hibernateProperties() {
      return new Properties() {
         {
            setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
            setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
         }
      };
   }
}
```

与接下来描述的 XML 配置相比，配置中的一个 bean 访问另一个 bean 的方式略有不同。在 XML 中，指向 bean 或指向能够创建该 bean 的 bean 工厂之间没有区别。由于Java配置是类型安全的——直接指向 bean 工厂不再是一个选项——我们需要手动从 bean 工厂检索 bean：

```java
txManager.setSessionFactory(sessionFactory().getObject());
```

## 3. Hibernate 3 的 XML Spring 配置

同样，我们也可以使用 XML 配置来设置Hibernate 3 ：

```xml
<context:property-placeholder location="classpath:persistence-mysql.properties" />

<bean id="sessionFactory" 
  class="org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean">
    <property name="dataSource" ref="dataSource" />
    <property name="packagesToScan" value="com.baeldung.spring.persistence.model" />
    <property name="hibernateProperties">
        <props>
            <prop key="hibernate.hbm2ddl.auto">${hibernate.hbm2ddl.auto}</prop>
            <prop key="hibernate.dialect">${hibernate.dialect}</prop>
        </props>
    </property>
</bean>

<bean id="dataSource" 
  class="org.apache.tomcat.dbcp.dbcp.BasicDataSource">
    <property name="driverClassName" value="${jdbc.driverClassName}" />
    <property name="url" value="${jdbc.url}" />
    <property name="username" value="${jdbc.user}" />
    <property name="password" value="${jdbc.pass}" />
</bean>

<bean id="txManager" 
  class="org.springframework.orm.hibernate3.HibernateTransactionManager">
    <property name="sessionFactory" ref="sessionFactory" />
</bean>

<bean id="persistenceExceptionTranslationPostProcessor" 
  class="org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor"/>
```

然后，使用@Configuration类将该 XML 文件引导到 Spring 上下文中：

```java
@Configuration
@EnableTransactionManagement
@ImportResource({ "classpath:persistenceConfig.xml" })
public class PersistenceXmlConfig {
   //
}
```

对于这两种类型的配置，JDBC 和 Hibernate 特定的属性都存储在一个属性文件中：

```shell
# jdbc.X
jdbc.driverClassName=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/spring_hibernate_dev?createDatabaseIfNotExist=true
jdbc.user=tutorialuser
jdbc.pass=tutorialmy5ql
# hibernate.X
hibernate.dialect=org.hibernate.dialect.MySQL5Dialect
hibernate.show_sql=false
hibernate.hbm2ddl.auto=create-drop
```

## 4. Spring、Hibernate 和 MySQL

上面的示例使用 MySQL 5 作为配置了 Hibernate 的底层数据库——但是，Hibernate 支持多个底层[SQL 数据库](https://developer.jboss.org/docs/DOC-13921)。

### 4.1. 司机

Driver 类名称是通过提供给 DataSource 的jdbc.driverClassName属性配置的。

在上面的示例中，它被设置为来自我们在本文开头的 pom 中定义的mysql-connector-java依赖项的com.mysql.jdbc.Driver 。

### 4.2. 方言

方言是通过提供给 Hibernate SessionFactory 的hibernate.dialect 属性配置的。

在上面的示例中，这被设置为org.hibernate.dialect.MySQL5Dialect，因为我们使用 MySQL 5 作为基础数据库。还有其他几种支持 MySQL 的方言：

-   org.hibernate.dialect.MySQL5InnoDBDialect – 用于带有 InnoDB 存储引擎的 MySQL 5.x
-   org.hibernate.dialect.MySQLDialect – 适用于 5.x 之前的 MySQL
-   org.hibernate.dialect.MySQLInnoDBDialect – 适用于 5.x 之前的 MySQL 和 InnoDB 存储引擎
-   org.hibernate.dialect.MySQLMyISAMDialect – 适用于所有带有 ISAM 存储引擎的 MySQL 版本

Hibernate[支持](http://docs.jboss.org/hibernate/core/3.6/reference/en-US/html/session-configuration.html#configuration-optional-dialects)每个支持的数据库的 SQL 方言。

## 5.用法

此时，Hibernate 3 已完全配置了 Spring，我们可以在需要时直接注入原始 Hibernate SessionFactory ：

```java
public abstract class FooHibernateDAO{

   @Autowired
   SessionFactory sessionFactory;

   ...

   protected Session getCurrentSession(){
      return sessionFactory.getCurrentSession();
   }
}
```

## 6.专家

要将 Spring Persistence 依赖项添加到 pom，请参阅[Spring with Maven 示例](https://www.baeldung.com/spring-with-maven#persistence)——我们需要同时定义spring-context和spring-orm。

继续Hibernate 3，Maven依赖很简单：

```xml
<dependency>
   <groupId>org.hibernate</groupId>
   <artifactId>hibernate-core</artifactId>
   <version>3.6.10.Final</version>
</dependency>
```

然后，为了使 Hibernate 能够使用其代理模型，我们还需要javassist：

```xml
<dependency>
   <groupId>org.javassist</groupId>
   <artifactId>javassist</artifactId>
   <version>3.18.2-GA</version>
</dependency>
```

我们将使用 MySQL 作为本教程的数据库，因此我们还需要：

```xml
<dependency>
   <groupId>mysql</groupId>
   <artifactId>mysql-connector-java</artifactId>
   <version>5.1.32</version>
   <scope>runtime</scope>
</dependency>
```

最后，我们将不使用 Spring 数据源实现——DriverManagerDataSource；相反，我们将使用生产就绪的连接池解决方案——Tomcat JDBC 连接池：

```xml
<dependency>
    <groupId>org.apache.tomcat</groupId>
    <artifactId>tomcat-dbcp</artifactId>
    <version>7.0.55</version>
</dependency>
```

## 七. 总结

在此示例中，我们使用 Spring 配置了 Hibernate 3——同时使用了Java和 XML 配置。