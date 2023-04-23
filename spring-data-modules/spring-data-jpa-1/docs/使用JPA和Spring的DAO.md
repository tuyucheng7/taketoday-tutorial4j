## 1. 概述

本文将展示如何使用 Spring 和 JPA 实现 DAO。对于核心 JPA 配置，请参阅[有关](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)使用 Spring 的 JPA 的文章。

## 2. 不再有 Spring 模板

从 Spring 3.1 开始，JpaTemplate和相应的JpaDaoSupport 已被弃用，取而代之的是使用本机JavaPersistence API。

此外，这两个类仅与 JPA 1 相关(来自JpaTemplate javadoc)：

>   请注意，此类没有升级到 JPA 2.0，而且永远不会。

因此，现在最好的做法是直接使用JavaPersistence API而不是JpaTemplate。

### 2.1. 没有模板的异常翻译

JpaTemplate的职责之一是异常转换——将低级异常转换为更高级别的通用 Spring 异常。

在没有模板的情况下，对于所有使用 @Repository 注解的 DAO ，异常转换仍然启用并具有完整的功能。Spring 使用 bean 后处理器实现这一点，它将通知所有@Repository bean 以及在容器中找到的所有PersistenceExceptionTranslator 。

同样重要的是要注意异常翻译机制使用代理——为了让 Spring 能够围绕 DAO 类创建代理，这些不能声明为final。

## 3. The DAO

首先，我们将为所有 DAO 实现基础层——一个使用泛型并设计为可扩展的抽象类：

```java
public abstract class AbstractJpaDAO< T extends Serializable > {

   private Class< T > clazz;

   @PersistenceContext
   EntityManager entityManager;

   public final void setClazz( Class< T > clazzToSet ){
      this.clazz = clazzToSet;
   }

   public T findOne( long id ){
      return entityManager.find( clazz, id );
   }
   public List< T > findAll(){
      return entityManager.createQuery( "from " + clazz.getName() )
       .getResultList();
   }

   public void create( T entity ){
      entityManager.persist( entity );
   }

   public T update( T entity ){
      return entityManager.merge( entity );
   }

   public void delete( T entity ){
      entityManager.remove( entity );
   }
   public void deleteById( long entityId ){
      T entity = findOne( entityId );
      delete( entity );
   }
}
```

这里最有趣的方面是注入 EntityManager 的方式——使用标准的@PersistenceContext注解。在引擎盖下，这是由PersistenceAnnotationBeanPostProcessor处理的——它处理注解，从包含中检索 JPA 实体管理器并将其注入。

持久性后处理器可以通过在配置中定义它来显式创建，也可以通过在命名空间配置中定义context:annotation-config或context:component-scan来自动创建。

另请注意，实体类在构造函数中传递以用于泛型操作：

```java
@Repository
public class FooDAO extends AbstractJPADAO< Foo > implements IFooDAO{

   public FooDAO(){
      setClazz(Foo.class );
   }
}
```

## 4. 总结

本教程说明了如何使用基于 XML 和Java的配置，使用Spring 和 JPA 设置 DAO 层。我们还讨论了为什么不使用JpaTemplate以及如何用EntityManager替换它。最终结果是一个轻量级、干净的 DAO 实现，几乎没有编译时对 Spring 的依赖。