## 1. 概述

本文将重点介绍通过为系统中的所有实体使用一个单一的、通用的数据访问对象来简化 DAO 层，这将导致优雅的数据访问，没有不必要的混乱或冗长。

[我们将在我们之前关于 Spring 和 Hibernate 的文章](https://www.baeldung.com/persistence-layer-with-spring-and-hibernate)中看到的抽象 DAO 类的基础上构建，并添加泛型支持。

## 延伸阅读：

## [Spring Data JPA 简介](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)

Spring Data JPA 与 Spring 4 简介 - Spring 配置、DAO、手动和生成的查询以及事务管理。

[阅读更多](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)→

## [使用 Spring 的 JPA 指南](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)

使用 Spring 设置 JPA - 如何设置 EntityManager 工厂和使用原始 JPA API。

[阅读更多](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)→

## [使用 JPA、Hibernate 和 Spring Data JPA 进行审计](https://www.baeldung.com/database-auditing-jpa)

本文演示了将审计引入应用程序的三种方法：JPA、Hibernate Envers 和 Spring Data JPA。

[阅读更多](https://www.baeldung.com/database-auditing-jpa)→



## 2. Hibernate 和 JPA DAO

大多数生产代码库都有某种 DAO 层。通常，实现范围从没有抽象基类的多个类到某种泛化类。然而，有一件事是一致的——总是不止一件。最有可能的是，DAO 和系统中的实体之间存在一对一的关系。

此外，根据所涉及的泛型级别，实际实现可能会有所不同，从大量重复代码到几乎空无一物，其中大部分逻辑都分组在一个基本抽象类中。

这些多个实现通常可以由单个参数化 DAO 代替。我们可以通过充分利用Java泛型提供的类型安全来实现这一点，从而不会丢失任何功能。

接下来我们将展示此概念的两个实现，一个用于以 Hibernate 为中心的持久层，另一个侧重于 JPA。这些实现绝不是完整的，但我们可以轻松地添加更多额外的数据访问方法。

### 2.1. 抽象 Hibernate DAO

让我们快速浏览一下AbstractHibernateDao类：

```java
public abstract class AbstractHibernateDao<T extends Serializable> {
    private Class<T> clazz;

    @Autowired
    protected SessionFactory sessionFactory;

    public void setClazz(final Class<T> clazzToSet) {
        clazz = Preconditions.checkNotNull(clazzToSet);
    }

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

这是一个具有多种数据访问方法的抽象类，它使用SessionFactory来操作实体。

我们在这里使用 Google Guava 的先决条件来确保使用有效的参数值调用方法或构造函数。如果先决条件失败，则会抛出定制的异常。

### 2.2. 通用 Hibernate DAO

现在我们有了抽象 DAO 类，我们可以只扩展它一次。通用 DAO 实现将成为我们唯一需要的实现：

```java
@Repository
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class GenericHibernateDao<T extends Serializable>
  extends AbstractHibernateDao<T> implements IGenericDao<T>{
   //
}
```

首先，请注意通用实现本身是参数化的，允许客户根据具体情况选择正确的参数。这将意味着客户无需为每个实体创建多个工件即可获得类型安全的所有好处。

其次，注意这个通用 DAO 实现的原型范围。使用此作用域意味着 Spring 容器将在每次请求时创建一个新的 DAO 实例(包括自动装配)。这将允许服务根据需要为不同的实体使用具有不同参数的多个 DAO。

这个作用域之所以如此重要，是因为 Spring 在容器中初始化 beans 的方式。让通用 DAO 没有范围意味着使用默认的单例范围，这将导致 DAO 的单个实例存在于容器中。对于任何一种更复杂的场景，这显然都是主要的限制。

IGenericDao只是所有 DAO 方法的一个接口，因此我们可以注入我们需要的实现：

```java
public interface IGenericDao<T extends Serializable> {
    void setClazz(Class< T > clazzToSet);

    T findOne(final long id);

    List<T> findAll();

    T create(final T entity);

    T update(final T entity);

    void delete(final T entity);

    void deleteById(final long entityId);
}

```

### 2.3. 抽象 JPA DAO

AbstractJpaDao与AbstractHibernateDao非常相似：

```java
public abstract class AbstractJpaDAO<T extends Serializable> {
    private Class<T> clazz;

    @PersistenceContext(unitName = "entityManagerFactory")
    private EntityManager entityManager;

    public final void setClazz(final Class<T> clazzToSet) {
        this.clazz = clazzToSet;
    }

    public T findOne(final long id) {
        return entityManager.find(clazz, id);
    }

    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        return entityManager.createQuery("from " + clazz.getName()).getResultList();
    }

    public T create(final T entity) {
        entityManager.persist(entity);
        return entity;
    }

    public T update(final T entity) {
        return entityManager.merge(entity);
    }

    public void delete(final T entity) {
        entityManager.remove(entity);
    }

    public void deleteById(final long entityId) {
        final T entity = findOne(entityId);
        delete(entity);
    }
}
```

与 Hibernate DAO 实现类似，我们直接使用JavaPersistence API，而不依赖于现已弃用的 Spring JpaTemplate。

### 2.4. 通用 JPA DAO

与 Hibernate 实现类似，JPA 数据访问对象也很简单：

```java
@Repository
@Scope( BeanDefinition.SCOPE_PROTOTYPE )
public class GenericJpaDao< T extends Serializable >
 extends AbstractJpaDao< T > implements IGenericDao< T >{
   //
}
```

## 3.注入这个DAO

我们现在有一个可以注入的 DAO 接口。 我们还需要指定类：

```java
@Service
class FooService implements IFooService{

   IGenericDao<Foo> dao;

   @Autowired
   public void setDao(IGenericDao<Foo> daoToSet) {
      dao = daoToSet;
      dao.setClazz(Foo.class);
   }

   // ...
}
```

Spring使用 setter 注入自动装配新的 DAO 实例，以便可以使用Class对象自定义实现。在此之后，DAO 已完全参数化并准备好供服务使用。

当然还有其他方法可以为 DAO 指定类——通过反射，甚至在 XML 中。我更喜欢这种更简单的解决方案，因为与使用反射相比，它提高了可读性和透明度。

## 4. 总结

本文讨论了通过提供通用 DAO 的单一、可重用实现来简化数据访问层。我们展示了在基于 Hibernate 和 JPA 的环境中的实现。结果是一个流线型的持久层，没有不必要的混乱。

有关使用基于Java的配置和项目的基本 Maven pom 设置 Spring 上下文的逐步介绍，请参阅[本文](https://www.baeldung.com/bootstraping-a-web-application-with-spring-and-java-based-configuration)。