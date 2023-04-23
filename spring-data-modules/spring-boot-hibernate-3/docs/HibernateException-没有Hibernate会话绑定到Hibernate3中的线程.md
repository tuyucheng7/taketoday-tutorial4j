## 1. 简介

在这个简短的教程中，我们将阐明何时抛出“No Hibernate Session Bound to Thread”异常以及如何解决它。

我们将在这里关注两种不同的场景：

1.  使用LocalSessionFactoryBean
2.  使用AnnotationSessionFactoryBean

## 2. 原因

在版本 3 中，Hibernate 引入了上下文会话的概念，并将getCurrentSession()方法添加到SessionFactory类中。可以在[此处](https://docs.jboss.org/hibernate/stable/core.old/reference/en/html/architecture-current-session.html)找到有关上下文会话的更多信息。

Spring 有自己的org.hibernate.context.CurrentSessionContext接口实现——org.springframework.orm.hibernate3.SpringSessionContext(在 Spring Hibernate 3 的情况下)。此实现要求会话绑定到事务。

自然地，调用getCurrentSession()方法的类应该在类级别或方法级别使用@Transactional注解。如果不是，将抛出org.hibernate.HibernateException: No Hibernate Session Bound to Thread 。

让我们快速看一个例子。

## 3.本地工厂会话Bean

他是我们将在本文中看到的第一个场景。

我们将使用LocalSessionFactoryBean定义一个JavaSpring 配置类：

```java
@Configuration
@EnableTransactionManagement
@PropertySource(
  { "classpath:persistence-h2.properties" }
)
@ComponentScan(
  { "com.baeldung.persistence.dao", "com.baeldung.persistence.service" }
)
public class PersistenceConfigHibernate3 {   
    // ...    
    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory 
          = new LocalSessionFactoryBean();
        Resource config = new ClassPathResource("exceptionDemo.cfg.xml");
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setConfigLocation(config);
        sessionFactory.setHibernateProperties(hibernateProperties());

        return sessionFactory;
    }    
    // ...
}
```

请注意，我们在这里使用 Hibernate 配置文件 ( exceptionDemo.cfg.xml ) 来映射模型类。这是因为org.springframework.orm.hibernate3.LocalSessionFactoryBean没有提供属性packagesToScan来映射模型类。

这是我们的简单服务：

```java
@Service
@Transactional
public class EventService {
    
    @Autowired
    private IEventDao dao;
    
    public void create(Event entity) {
        dao.create(entity);
    }
}
@Entity
@Table(name = "EVENTS")
public class Event implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    private String description;
    
    // ...
 }
```

正如我们在下面的代码片段中看到的，SessionFactory类的getCurrentSession()方法用于获取 Hibernate 会话：

```java
public abstract class AbstractHibernateDao<T extends Serializable> 
  implements IOperations<T> {
    private Class<T> clazz;
    @Autowired
    private SessionFactory sessionFactory;
    // ...
    
    @Override
    public void create(T entity) {
        Preconditions.checkNotNull(entity);
        getCurrentSession().persist(entity);
    }
    
    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}
```

下面的测试通过了，演示了当包含服务方法的类EventService未使用@Transactional注解进行注解时将如何抛出异常：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
  classes = { PersistenceConfigHibernate3.class }, 
  loader = AnnotationConfigContextLoader.class
)
public class HibernateExceptionScen1MainIntegrationTest {
    @Autowired
    EventService service;
    
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
        
    @Test
    public void whenNoTransBoundToSession_thenException() {
        expectedEx.expectCause(
          IsInstanceOf.<Throwable>instanceOf(HibernateException.class));
        expectedEx.expectMessage("No Hibernate Session bound to thread, "
          + "and configuration does not allow creation "
          + "of non-transactional one here");
        service.create(new Event("from LocalSessionFactoryBean"));
    }
}
```

这个测试展示了当EventService类被@Transactional注解注解时服务方法如何成功执行：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
  classes = { PersistenceConfigHibernate3.class }, 
  loader = AnnotationConfigContextLoader.class
)
public class HibernateExceptionScen1MainIntegrationTest {
    @Autowired
    EventService service;
    
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    
    @Test
    public void whenEntityIsCreated_thenNoExceptions() {
        service.create(new Event("from LocalSessionFactoryBean"));
        List<Event> events = service.findAll();
    }
}
```

## 4.注解会话工厂Bean

当我们使用org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean在我们的 Spring 应用程序中创建SessionFactory时，也会发生此异常。

让我们看一些演示这一点的示例代码。为此，我们使用AnnotationSessionFactoryBean定义了一个JavaSpring 配置类：

```java
@Configuration
@EnableTransactionManagement
@PropertySource(
  { "classpath:persistence-h2.properties" }
)
@ComponentScan(
  { "com.baeldung.persistence.dao", "com.baeldung.persistence.service" }
)
public class PersistenceConfig {
    //...
    @Bean
    public AnnotationSessionFactoryBean sessionFactory() {
        AnnotationSessionFactoryBean sessionFactory 
          = new AnnotationSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(
          new String[] { "com.baeldung.persistence.model" });
        sessionFactory.setHibernateProperties(hibernateProperties());

        return sessionFactory;
    }
    // ...
}
```

使用上一节中的同一组 DAO、服务和模型类，我们遇到了上述异常：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
  classes = { PersistenceConfig.class }, 
  loader = AnnotationConfigContextLoader.class
)
public class HibernateExceptionScen2MainIntegrationTest {
    @Autowired
    EventService service;
    
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
         
    @Test
    public void whenNoTransBoundToSession_thenException() {
        expectedEx.expectCause(
          IsInstanceOf.<Throwable>instanceOf(HibernateException.class));
        expectedEx.expectMessage("No Hibernate Session bound to thread, "
          + "and configuration does not allow creation "
          + "of non-transactional one here");
        service.create(new Event("from AnnotationSessionFactoryBean"));
    }
}
```

如果我们使用@Transactional注解来注解服务类，服务方法将按预期工作并且下面显示的测试通过：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
  classes = { PersistenceConfig.class }, 
  loader = AnnotationConfigContextLoader.class
)
public class HibernateExceptionScen2MainIntegrationTest {
    @Autowired
    EventService service;
    
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    
    @Test
    public void whenEntityIsCreated_thenNoExceptions() {
        service.create(new Event("from AnnotationSessionFactoryBean"));
        List<Event> events = service.findAll();
    }
}
```

## 5.解决方案

很明显，需要从打开的事务中调用从 Spring 获得的SessionFactory的getCurrentSession()方法。因此，解决方案是确保我们的 DAO/Service 方法/类使用@Transactional注解正确注解。

需要注意的是，在 Hibernate 4 及以后的版本中，由于同样的原因抛出的异常的消息是不同的措辞。我们得到的不是“没有绑定到线程的 Hibernate 会话”，而是“无法为当前线程获取事务同步会话”。

还有一点要说明。与org.hibernate.context.CurrentSessionContext接口一起，Hibernate 引入了一个属性hibernate.current_session_context_class，它可以设置为实现当前会话上下文的类。

如前所述，Spring 自带此接口的实现：SpringSessionContext 。默认情况下，它将hibernate.current_session_context_class属性设置为等于此类。

因此，如果我们明确地将此属性设置为其他内容，它会破坏 Spring 管理 Hibernate 会话和事务的能力。这也会导致异常，但与正在考虑的异常不同。

总而言之，重要的是要记住，当我们使用 Spring 来管理 Hibernate 会话时，我们不应该显式地设置hibernate.current_session_context_class。

## 六. 总结

在本文中，我们查看了为什么在 Hibernate 3 中会抛出异常org.hibernate.HibernateException: No Hibernate Session Bound to Thread以及一些示例代码，以及我们如何轻松解决它。