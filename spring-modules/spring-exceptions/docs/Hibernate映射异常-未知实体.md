## 1. 问题

本文将讨论org.hibernate.MappingException：未知实体问题和解决方案，既适用于Hibernate也适用于Spring和Hibernate环境。

### 延伸阅读

### [使用Spring Boot Hibernate 5](https://www.baeldung.com/hibernate-5-spring)

将Hibernate 5与Spring集成的快速实用指南。

[阅读更多](https://www.baeldung.com/hibernate-5-spring)→

### [Hibernate中的@Immutable](https://www.baeldung.com/hibernate-immutable)

Hibernate中@Immutable注解的快速实用指南

[阅读更多](https://www.baeldung.com/hibernate-immutable)→

### [HibernateException：没有Hibernate会话绑定到Hibernate3中的线程](https://www.baeldung.com/no-hibernate-session-bound-to-thread-exception)

了解何时抛出“No Hibernate Session Bound to Thread”异常以及如何处理它。

[阅读更多](https://www.baeldung.com/no-hibernate-session-bound-to-thread-exception)→

## 2. @Entity注解缺失或无效

映射异常的最常见原因仅仅是实体类缺少@Entity注解：

```java
public class Foo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public Foo() {
        super();
    }

    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
}
```

另一种可能性是它可能有错误类型的@Entity注解：

```java
import org.hibernate.annotations.Entity;

@Entity
public class Foo implements Serializable {
    // ...
}
```

已弃用的org.hibernate.annotations.Entity是错误的实体类型-我们需要的是javax.persistence.Entity：

```java
import javax.persistence.Entity;

@Entity
public class Foo implements Serializable {
    // ...
}
```

## 3. Spring的MappingException

[Spring中Hibernate](https://www.baeldung.com/hibernate-4-spring)的配置涉及通过LocalSessionFactoryBean从注解扫描引导SessionFactory：

```java
@Bean
public LocalSessionFactoryBean sessionFactory() {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    sessionFactory.setDataSource(restDataSource());
    // ...
    return sessionFactory;
}
```

SessionFactoryBean的这个简单配置缺少一个关键成分，尝试使用SessionFactory的测试将失败：

```java
...
@Autowired
private SessionFactory sessionFactory;

@Test(expected = MappingException.class)
@Transactional
public void givenEntityIsPersisted_thenException() {
    sessionFactory.getCurrentSession().saveOrUpdate(new Foo());
}
```

正如预期的那样，异常是MappingException:Unknown entity：

```bash
org.hibernate.MappingException: Unknown entity: 
cn.tuyucheng.taketoday.ex.mappingexception.persistence.model.Foo
    at o.h.i.SessionFactoryImpl.getEntityPersister(SessionFactoryImpl.java:1141)
```

现在，这个问题有两种解决方案——两种告诉LocalSessionFactoryBean关于Foo实体类的方法。

我们可以指定在类路径中搜索哪些包的实体类：

```java
sessionFactory.setPackagesToScan(new String[] { "cn.tuyucheng.taketoday.ex.mappingexception.persistence.model" });
```

或者我们可以简单地将实体类直接注册到会话工厂中：

```java
sessionFactory.setAnnotatedClasses(new Class[] { Foo.class });
```

使用这些额外的配置行中的任何一个，测试现在将正确运行并通过。

## 4. Hibernate的MappingException

现在让我们看看仅使用Hibernate时的错误：

```java
public class Cause4MappingExceptionIntegrationTest {

    @Test
    public void givenEntityIsPersisted_thenException() throws IOException {
        SessionFactory sessionFactory = configureSessionFactory();

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.saveOrUpdate(new Foo());
        session.getTransaction().commit();
    }

    private SessionFactory configureSessionFactory() throws IOException {
        Configuration configuration = new Configuration();
        InputStream inputStream = this.getClass().getClassLoader().
              getResourceAsStream("hibernate-mysql.properties");
        Properties hibernateProperties = new Properties();
        hibernateProperties.load(inputStream);
        configuration.setProperties(hibernateProperties);

        // configuration.addAnnotatedClass(Foo.class);

        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().
              applySettings(configuration.getProperties()).buildServiceRegistry();
        SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        return sessionFactory;
    }
}
```

hibernate-mysql.properties文件包含Hibernate配置属性：

```properties
hibernate.connection.username=tutorialuser
hibernate.connection.password=tutorialmy5ql
hibernate.connection.driver_class=com.mysql.jdbc.Driver
hibernate.dialect=org.hibernate.dialect.MySQL5Dialect
hibernate.connection.url=jdbc:mysql://localhost:3306/spring_hibernate4_exceptions
hibernate.show_sql=false
hibernate.hbm2ddl.auto=create
```

运行此测试将导致相同的映射异常：

```bash
org.hibernate.MappingException: 
  Unknown entity: cn.tuyucheng.taketoday.ex.mappingexception.persistence.model.Foo
    at o.h.i.SessionFactoryImpl.getEntityPersister(SessionFactoryImpl.java:1141)
```

从上面的例子中可能已经很清楚了，配置中缺少的是将实体类的元数据Foo添加到配置中：

```java
configuration.addAnnotatedClass(Foo.class);
```

这修复了测试-现在能够持久化Foo实体。

## 5. 总结

本文说明了为什么会出现未知实体映射异常，以及当它出现时如何解决问题，首先是在实体级别，然后是Spring和Hibernate，最后是单独使用Hibernate。