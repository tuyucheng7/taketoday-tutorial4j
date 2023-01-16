## 1. 概述

本教程展示了如何使用 Hibernate 作为持久性提供程序来设置带有 JPA 的 Spring 。

有关使用基于Java的配置和项目的基本 Maven pom 设置 Spring 上下文的分步介绍，请参阅[本文](https://www.baeldung.com/bootstraping-a-web-application-with-spring-and-java-based-configuration)。

我们将从在Spring Boot项目中设置 JPA 开始。如果我们有一个标准的 Spring 项目，那么我们将研究我们需要的完整配置。

## 延伸阅读：

## [定义 JPA 实体](https://www.baeldung.com/jpa-entities)

了解如何使用JavaPersistence API 定义实体和自定义它们。

[阅读更多](https://www.baeldung.com/jpa-entities)→

## [带休眠功能的 Spring Boot](https://www.baeldung.com/spring-boot-hibernate)

集成Spring Boot和 Hibernate/JPA 的快速、实用的介绍。

[阅读更多](https://www.baeldung.com/spring-boot-hibernate)→

这是一个关于使用 Spring 4 设置 Hibernate 4 的视频(我们建议以完整的 1080p 观看)：



## 2.Spring Boot中的 JPA

Spring Boot 项目旨在使创建 Spring 应用程序变得更快、更容易。这是通过使用启动器和自动配置各种 Spring 功能(其中包括 JPA)来完成的。

### 2.1. Maven 依赖项

要在Spring Boot应用程序中启用 JPA，我们需要[spring-boot-starter](https://search.maven.org/classic/#search|ga|1|a%3A"spring-boot-starter" AND g%3A"org.springframework.boot")和[spring-boot-starter-data-jpa](https://search.maven.org/classic/#search|ga|1|a%3A"spring-boot-starter-data-jpa")依赖项：

```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>
```

spring-boot-starter包含 Spring JPA 必要的自动配置。此外，spring-boot-starter-jpa项目引用了所有必要的依赖项，例如hibernate-core。

### 2.2. 配置

Spring Boot 将Hibernate配置为默认的 JPA 提供程序，因此不再需要定义entityManagerFactory bean，除非我们想自定义它。

Spring Boot 还可以根据我们使用的数据库自动配置数据源 bean。对于H2、HSQLDB 和Apache Derby类型的内存数据库，如果类路径上存在相应的数据库依赖项，Boot 会自动配置DataSource 。

例如，如果我们想在Spring BootJPA 应用程序中使用内存中的H2数据库，我们只需要在pom.xml文件中添加[h2](https://search.maven.org/classic/#search|ga|1|a%3A"h2" AND g%3A"com.h2database")依赖：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.200</version>
</dependency>
```

这样，我们不需要定义dataSource bean，但如果我们想自定义它，我们可以。

如果我们想将 JPA 与MySQL数据库一起使用，我们需要mysql-connector-java依赖项。我们还需要定义数据源配置。

我们可以在@Configuration类中或使用标准的Spring Boot属性来执行此操作。

Java 配置看起来与标准 Spring 项目中的配置相同：

```java
@Bean
public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();

    dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dataSource.setUsername("mysqluser");
    dataSource.setPassword("mysqlpass");
    dataSource.setUrl(
      "jdbc:mysql://localhost:3306/myDb?createDatabaseIfNotExist=true"); 
    
    return dataSource;
}
```

要使用属性文件配置数据源，我们必须设置以spring.datasource为前缀的属性：

```plaintext
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=mysqluser
spring.datasource.password=mysqlpass
spring.datasource.url=
  jdbc:mysql://localhost:3306/myDb?createDatabaseIfNotExist=true
```

Spring Boot 会根据这些属性自动配置一个数据源。

同样在Spring Boot1 中，默认连接池是Tomcat，但在Spring Boot2 中已更改为HikariCP。

我们在[GitHub 项目](https://github.com/eugenp/tutorials/tree/master/persistence-modules/spring-jpa)中有更多在Spring Boot中配置 JPA 的示例。

如我们所见，如果我们使用 Spring Boot，基本的 JPA 配置相当简单。

然而，如果我们有一个标准的 Spring 项目，我们需要更明确的配置，使用Java或 XML。这就是我们将在下一节中关注的内容。

## 3. 在非引导项目中使用Java的 JPA Spring 配置

要在 Spring 项目中使用 JPA，我们需要设置EntityManager。

这是配置的主要部分，我们可以通过 Spring 工厂 bean 来完成。这可以是更简单的LocalEntityManagerFactoryBean或更灵活的LocalContainerEntityManagerFactoryBean。

让我们看看如何使用后一个选项：

```java
@Configuration
@EnableTransactionManagement
public class PersistenceJPAConfig{

   @Bean
   public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
      LocalContainerEntityManagerFactoryBean em 
        = new LocalContainerEntityManagerFactoryBean();
      em.setDataSource(dataSource());
      em.setPackagesToScan(new String[] { "com.baeldung.persistence.model" });

      JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
      em.setJpaVendorAdapter(vendorAdapter);
      em.setJpaProperties(additionalProperties());

      return em;
   }
   
   // ...

}
```

我们还需要显式定义我们在上面使用的DataSource bean ：

```java
@Bean
public DataSource dataSource(){
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dataSource.setUrl("jdbc:mysql://localhost:3306/spring_jpa");
    dataSource.setUsername( "tutorialuser" );
    dataSource.setPassword( "tutorialmy5ql" );
    return dataSource;
}
```

配置的最后一部分是额外的 Hibernate 属性以及TransactionManager和exceptionTranslation beans：

```java
@Bean
public PlatformTransactionManager transactionManager() {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

    return transactionManager;
}

@Bean
public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
    return new PersistenceExceptionTranslationPostProcessor();
}

Properties additionalProperties() {
    Properties properties = new Properties();
    properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
    properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
       
    return properties;
}
```

## 4. 使用 XML 的 JPA Spring 配置

接下来，让我们看看使用 XML 的相同 Spring 配置：

```xml
<bean id="myEmf" 
  class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
    <property name="dataSource" ref="dataSource" />
    <property name="packagesToScan" value="com.baeldung.persistence.model" />
    <property name="jpaVendorAdapter">
        <bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter" />
    </property>
    <property name="jpaProperties">
        <props>
            <prop key="hibernate.hbm2ddl.auto">create-drop</prop>
            <prop key="hibernate.dialect">org.hibernate.dialect.MySQL5Dialect</prop>
        </props>
    </property>
</bean>

<bean id="dataSource" 
  class="org.springframework.jdbc.datasource.DriverManagerDataSource">
    <property name="driverClassName" value="com.mysql.cj.jdbc.Driver" />
    <property name="url" value="jdbc:mysql://localhost:3306/spring_jpa" />
    <property name="username" value="tutorialuser" />
    <property name="password" value="tutorialmy5ql" />
</bean>

<bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
    <property name="entityManagerFactory" ref="myEmf" />
</bean>
<tx:annotation-driven />

<bean id="persistenceExceptionTranslationPostProcessor" class=
  "org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor" />
```

XML 和新的基于Java的配置之间存在相对较小的差异。即，在 XML 中，对另一个 bean 的引用可以指向该 bean 或该 bean 的 bean 工厂。

但在Java中，由于类型不同，编译器不允许，因此首先从其 bean 工厂中检索EntityManagerFactory ，然后传递给事务管理器：

```java
transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
```

## 5. 完全无 XML

通常，JPA 通过META-INF/persistence.xml文件定义一个持久化单元。从 Spring 3.1 开始，不再需要persistence.xml 。LocalContainerEntityManagerFactoryBean现在支持packagesToScan属性，可以在其中指定要扫描@Entity类的包。

该文件是我们需要删除的最后一段 XML。我们现在可以在没有 XML 的情况下完全设置 JPA。

我们通常会在persistence.xml文件中指定 JPA 属性。

或者，我们可以将属性直接添加到实体管理器工厂 bean：

```java
factoryBean.setJpaProperties(this.additionalProperties());
```

作为旁注，如果 Hibernate 是持久性提供者，这也将是指定 Hibernate 特定属性的方式。

## 6.Maven配置

除了 Spring Core 和持久性依赖项——在[Spring with Maven 教程](https://www.baeldung.com/spring-with-maven)中有详细介绍——我们还需要在项目中定义 JPA 和 Hibernate 以及 MySQL 连接器：

```xml
<dependency>
   <groupId>org.hibernate</groupId>
   <artifactId>hibernate-core</artifactId>
   <version>5.2.17.Final</version>
   <scope>runtime</scope>
</dependency>

<dependency>
   <groupId>mysql</groupId>
   <artifactId>mysql-connector-java</artifactId>
   <version>8.0.19</version>
   <scope>runtime</scope>
</dependency>
```

请注意，此处包含 MySQL 依赖项作为示例。我们需要一个驱动程序来配置数据源，但是任何支持 Hibernate 的数据库都可以。

## 七. 总结

本教程说明了如何在Spring Boot 和标准 Spring 应用程序中使用 Spring 中的Hibernate 配置 JPA 。