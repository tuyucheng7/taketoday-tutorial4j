## 1. 概述

大多数 JPA 驱动的应用程序大量使用“persistence.xml”文件来获取 JPA 实现，例如[Hibernate](http://hibernate.org/)或[OpenJPA](https://openjpa.apache.org/)。

我们这里的方法提供了一种集中机制来配置一个或多个[持久性单元](https://docs.oracle.com/cd/E19798-01/821-1841/bnbrj/index.html) 和相关的[持久性上下文](https://docs.jboss.org/hibernate/orm/4.0/devguide/en-US/html/ch03.html)。

虽然这种方法本身并没有错，但它不适用于需要单独测试使用不同持久性单元的应用程序组件的用例。

从好的方面来说， 可以引导 JPA 实现而无需借助“persistence.xml”文件，只需使用纯Java即可。

在本教程中，我们将了解如何使用 Hibernate 完成此操作。

## 2.实现PersistenceUnitInfo接口

在典型的“基于 xml 的”JPA 配置中，JPA 实现自动负责实现[PersistenceUnitInfo](https://docs.oracle.com/javaee/7/api/javax/persistence/spi/PersistenceUnitInfo.html)接口。

使用通过解析“persistence.xml”文件收集的所有数据，持久性提供者使用此实现来创建实体管理器工厂。从这个工厂，我们可以获得一个实体管理器。

由于我们不依赖于“persistence.xml”文件，我们需要做的第一件事就是提供我们自己的PersistenceUnitInfo实现。 我们将使用 Hibernate 作为我们的持久性提供者：

```java
public class HibernatePersistenceUnitInfo implements PersistenceUnitInfo {
    
    public static String JPA_VERSION = "2.1";
    private String persistenceUnitName;
    private PersistenceUnitTransactionType transactionType
      = PersistenceUnitTransactionType.RESOURCE_LOCAL;
    private List<String> managedClassNames;
    private List<String> mappingFileNames = new ArrayList<>();
    private Properties properties;
    private DataSource jtaDataSource;
    private DataSource nonjtaDataSource;
    private List<ClassTransformer> transformers = new ArrayList<>();
    
    public HibernatePersistenceUnitInfo(
      String persistenceUnitName, List<String> managedClassNames, Properties properties) {
        this.persistenceUnitName = persistenceUnitName;
        this.managedClassNames = managedClassNames;
        this.properties = properties;
    }

    // standard setters / getters   
}
```

简而言之，HibernatePersistenceUnitInfo类只是一个普通的数据容器，它存储绑定到特定持久性单元的配置参数。这包括持久性单元名称、托管实体类的名称、事务类型、JTA 和非 JTA 数据源等等。

## 3. 使用 Hibernate 的EntityManagerFactoryBuilderImpl类创建实体管理器工厂

现在我们已经有了一个自定义的PersistenceUnitInfo实现，我们需要做的最后一件事就是获得一个实体管理器工厂。

[Hibernate 使用它的EntityManagerFactoryBuilderImpl](https://docs.jboss.org/hibernate/orm/5.0/javadocs/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.html)类(构建器模式的简洁实现)使这个过程变得轻而易举[ ](https://docs.jboss.org/hibernate/orm/5.0/javadocs/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.html)。

为了提供更高级别的抽象，让我们创建一个包装 EntityManagerFactoryBuilderImpl 功能的类。

首先，让我们使用 Hibernate 的 EntityManagerFactoryBuilderImpl 类和我们的 HibernatePersistenceUnitInfo类展示负责创建实体管理器工厂和实体管理器的方法：

```java
public class JpaEntityManagerFactory {
    private String DB_URL = "jdbc:mysql://databaseurl";
    private String DB_USER_NAME = "username";
    private String DB_PASSWORD = "password";
    private Class[] entityClasses;
    
    public JpaEntityManagerFactory(Class[] entityClasses) {
        this.entityClasses = entityClasses;
    }
    
    public EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }
    
    protected EntityManagerFactory getEntityManagerFactory() {
        PersistenceUnitInfo persistenceUnitInfo = getPersistenceUnitInfo(
          getClass().getSimpleName());
        Map<String, Object> configuration = new HashMap<>();
        return new EntityManagerFactoryBuilderImpl(
          new PersistenceUnitInfoDescriptor(persistenceUnitInfo), configuration)
          .build();
    }
    
    protected HibernatePersistenceUnitInfo getPersistenceUnitInfo(String name) {
        return new HibernatePersistenceUnitInfo(name, getEntityClassNames(), getProperties());
    }

    // additional methods
}

```

接下来我们看一下提供 EntityManagerFactoryBuilderImpl和HibernatePersistenceUnitInfo所需参数的方法。

这些参数包括托管实体类、实体类的名称、Hibernate 的配置属性和MysqlDataSource对象：

```java
public class JpaEntityManagerFactory {
    //...
    
    protected List<String> getEntityClassNames() {
        return Arrays.asList(getEntities())
          .stream()
          .map(Class::getName)
          .collect(Collectors.toList());
    }
    
    protected Properties getProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.id.new_generator_mappings", false);
        properties.put("hibernate.connection.datasource", getMysqlDataSource());
        return properties;
    }
    
    protected Class[] getEntities() {
        return entityClasses;
    }
    
    protected DataSource getMysqlDataSource() {
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setURL(DB_URL);
        mysqlDataSource.setUser(DB_USER_NAME);
        mysqlDataSource.setPassword(DB_PASSWORD);
        return mysqlDataSource;
    }
}

```

为了简单起见，我们在JpaEntityManagerFactory类中对数据库连接参数进行了硬编码。但是，在生产中，我们应该将它们存储在一个单独的属性文件中。

此外，getMysqlDataSource()方法返回一个完全初始化的MysqlDataSource对象。

我们这样做只是为了让事情更容易理解。在更现实、松散耦合的设计中，我们将使用EntityManagerFactoryBuilderImpl的[withDataSource()](https://docs.jboss.org/hibernate/orm/5.0/javadocs/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.html#withDataSource-javax.sql.DataSource-)方法注入DataSource对象 ，而不是在类中创建它。

## 4. 使用实体管理器执行 CRUD 操作

最后，让我们看看如何使用JpaEntityManagerFactory实例来获取 JPA 实体管理器并执行 CRUD 操作。(请注意，为简洁起见，我们省略了User类)：

```java
public static void main(String[] args) {
    EntityManager entityManager = getJpaEntityManager();
    User user = entityManager.find(User.class, 1);
    
    entityManager.getTransaction().begin();
    user.setName("John");
    user.setEmail("john@domain.com");
    entityManager.merge(user);
    entityManager.getTransaction().commit();
    
    entityManager.getTransaction().begin();
    entityManager.persist(new User("Monica", "monica@domain.com"));
    entityManager.getTransaction().commit();
 
    // additional CRUD operations
}

private static class EntityManagerHolder {
    private static final EntityManager ENTITY_MANAGER = new JpaEntityManagerFactory(
      new Class[]{User.class})
      .getEntityManager();
}

public static EntityManager getJpaEntityManager() {
    return EntityManagerHolder.ENTITY_MANAGER;
}
```

## 5.总结

在本文中，我们展示了如何使用 JPA 的PersistenceUnitInfo接口和 Hibernate 的 EntityManagerFactoryBuilderImpl类的自定义实现以编程方式引导 JPA 实体管理器，而不必依赖传统的“persistence.xml”文件。