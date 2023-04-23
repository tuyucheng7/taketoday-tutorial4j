## 1. 概述

本文将展示如何使用 Spring 和 Hibernate 实现 DAO。对于核心 Hibernate 配置，请查看之前的[Hibernate 5 with Spring](https://www.baeldung.com/hibernate-5-spring)文章。

## 2. 不再有 Spring 模板

从 Spring 3.0 和 Hibernate 3.0.1 开始，不再需要Spring HibernateTemplate来管理 Hibernate Session。现在可以使用[上下文会话](http://docs.jboss.org/hibernate/core/3.6/reference/en-US/html/architecture.html#architecture-current-session)——由 Hibernate 直接管理并在整个事务范围内处于活动状态的会话。

因此，现在最好的做法是直接使用 Hibernate API 而不是HibernateTemplate。这将有效地将 DAO 层实现与 Spring 完全分离。

### 2.1. 没有HibernateTemplate的异常转换 

异常转换是HibernateTemplate的职责之一——将低级别的 Hibernate 异常转换为更高级别的通用 Spring 异常。

在没有模板的情况下，对于所有使用 @Repository 注解进行注解的 DAO ，此机制仍然启用并处于活动状态 。在引擎盖下，这使用了一个 Spring bean 后处理器，它将使用在 Spring 上下文中找到的所有PersistenceExceptionTranslator通知所有@Repository bean 。

要记住的一件事是异常转换使用代理。为了让 Spring 能够围绕 DAO 类创建代理，这些不能声明为final。

### 2.2. 没有模板的 Hibernate 会话管理

当 Hibernate 对上下文会话的支持出现时，HibernateTemplate基本上已经过时了。事实上，该类的 Javadoc 现在突出显示了这一方面(粗体来自原文)：

>   注意：从 Hibernate 3.0.1 开始，事务性 Hibernate 访问代码也可以以普通 Hibernate 样式进行编码。因此，对于新开始的项目，考虑采用标准的 Hibernate3 风格来编码数据访问对象，基于 {@link org.hibernate.SessionFactory#getCurrentSession()}。

## 3. The DAO

我们将从基础 DAO 开始——一个抽象的、参数化的 DAO，它支持常见的通用操作，我们可以为每个实体扩展它：

```java
public abstract class AbstractHibernateDao<T extends Serializable> {
    private Class<T> clazz;

    @Autowired
    protected SessionFactory sessionFactory;

    public final void setClazz(final Class<T> clazzToSet) {
        clazz = Preconditions.checkNotNull(clazzToSet);
    }

    // API
    public T findOne(final long id) {
        return (T) getCurrentSession().get(clazz, id);
    }

    public List<T> findAll() {
        return getCurrentSession().createQuery("from " + clazz.getName()).list();
    }

    public T create(final T entity) {
        Preconditions.checkNotNull(entity);
        getCurrentSession().saveOrUpdate(entity);
        return entity;
    }

    public T update(final T entity) {
        Preconditions.checkNotNull(entity);
        return (T) getCurrentSession().merge(entity);
    }

    public void delete(final T entity) {
        Preconditions.checkNotNull(entity);
        getCurrentSession().delete(entity);
    }

    public void deleteById(final long entityId) {
        final T entity = findOne(entityId);
        Preconditions.checkState(entity != null);
        delete(entity);
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}
```

这里有几个方面很有趣——正如所讨论的，抽象 DAO 不扩展任何 Spring 模板(例如HibernateTemplate)。相反，Hibernate SessionFactory直接注入到 DAO 中，并将通过它公开的上下文Session发挥主要 Hibernate API 的作用：

这个.sessionFactory.getCurrentSession();

另请注意，构造函数接收实体的类作为要在通用操作中使用的参数。

现在，让我们看一下这个 DAO 的示例实现，用于Foo实体：

```java
@Repository
public class FooDAO extends AbstractHibernateDAO< Foo > implements IFooDAO{

   public FooDAO(){
      setClazz(Foo.class );
   }
}
```

## 4. 总结

本文介绍了使用 Hibernate 和 Spring 的持久层的配置和实现。

讨论了停止依赖 DAO 层模板的原因，以及配置 Spring 来管理事务和 Hibernate Session 的可能陷阱。最终结果是一个轻量级、干净的 DAO 实现，几乎没有编译时对 Spring 的依赖。