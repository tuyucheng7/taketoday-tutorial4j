## 1. 概述

数据库抽象层(例如 ORM(对象关系映射)框架)的优点之一是它们能够透明地缓存从底层存储检索的数据。这有助于消除频繁访问的数据的数据库访问成本。

如果缓存内容的读/写比率很高，性能提升可能会很显着。对于由大型对象图组成的实体尤其如此。

在本教程中，我们将探索 Hibernate 二级缓存。我们将解释一些基本概念，并用简单的例子来说明一切。我们将使用 JPA，并且仅针对 JPA 中未标准化的那些功能回退到 Hibernate 本机 API。

## 2. 什么是二级缓存？

与大多数其他功能齐全的 ORM 框架一样，Hibernate 具有一级缓存的概念。它是一个会话范围的缓存，确保每个实体实例在持久上下文中只加载一次。

一旦会话关闭，一级缓存也将终止。这实际上是可取的，因为它允许并发会话与彼此隔离的实体实例一起工作。

相反，二级缓存是SessionFactory范围的，这意味着它由使用同一会话工厂创建的所有会话共享。当通过 id 查找实体实例时(通过应用程序逻辑或内部 Hibernate，例如，当它从其他实体加载到该实体的关联时)，并为该实体启用二级缓存时，会发生以下情况：

-   如果一个实例已经存在于一级缓存中，它会从那里返回。
-   如果在一级缓存中没有找到实例，而对应的实例状态缓存在二级缓存中，则从二级缓存中获取数据并组装一个实例并返回。
-   否则，从数据库加载必要的数据并组装并返回一个实例。

一旦实例存储在持久性上下文(一级缓存)中，它就会在同一会话中的所有后续调用中从那里返回，直到会话关闭，或者该实例被手动从持久性上下文中逐出。如果加载的实例状态不存在，它也会存储在 L2 缓存中。

## 3.区域工厂

Hibernate 二级缓存被设计为不知道实际使用的缓存提供程序。Hibernate 只需要提供 org.hibernate.cache.spi.RegionFactory接口的实现，它封装了所有特定于实际缓存提供者的细节。基本上，它充当 Hibernate 和缓存提供程序之间的桥梁。

在本文中，我们将使用 Ehcache，一种成熟且广泛使用的缓存作为我们的缓存提供程序。我们可以选择任何其他提供者，只要有RegionFactory的实现即可。

我们将使用以下 Maven 依赖项将 Ehcache 区域工厂实现添加到类路径中：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-ehcache</artifactId>
    <version>5.2.2.Final</version>
</dependency>
```

我们可以[在这里查看](https://search.maven.org/classic/#search|ga|1|g%3A"org.hibernate" AND a%3A"hibernate-ehcache")最新版本的hibernate-ehcache。但是，我们需要确保hibernate-ehcache版本等于我们在项目中使用的 Hibernate 版本(例如，如果我们使用hibernate-ehcache 5.2.2.Final，就像在这个例子中，那么 Hibernate 的版本也应该是5.2.2.Final)。

hibernate-ehcache工件依赖于 Ehcache 实现本身，它也可传递地包含在类路径中。

## 4.启用二级缓存

使用以下两个属性，我们将告诉 Hibernate L2 缓存已启用，并为其指定区域工厂类的名称：

```plaintext
hibernate.cache.use_second_level_cache=true
hibernate.cache.region.factory_class=org.hibernate.cache.ehcache.EhCacheRegionFactory

```

例如，在persistence.xml 中，它看起来像：

```xml
<properties>
    ...
    <property name="hibernate.cache.use_second_level_cache" value="true"/>
    <property name="hibernate.cache.region.factory_class" 
      value="org.hibernate.cache.ehcache.EhCacheRegionFactory"/>
    ...
</properties>
```

要禁用二级缓存(比如出于调试目的)，我们只需将hibernate.cache.use_second_level_cache属性设置为 false。

## 5. 使实体可缓存

为了使实体符合二级缓存的条件，我们将使用 Hibernate 特定的@org.hibernate.annotations.Cache注解对其进行注解，并指定[缓存并发策略](https://www.baeldung.com/hibernate-second-level-cache#cacheConcurrencyStrategy)。

一些开发人员认为添加标准@javax.persistence.Cacheable注解也是一个很好的约定(尽管 Hibernate 不需要)，因此实体类实现可能如下所示：

```java
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Foo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private long id;

    @Column(name = "NAME")
    private String name;
    
    // getters and setters
}
```

对于每个实体类，Hibernate 将使用单独的缓存区域来存储该类的实例状态。区域名称是完全限定的类名。

例如，Foo实例存储在 Ehcache 中名为com.baeldung.hibernate.cache.model.Foo的缓存中。

为了验证缓存是否正常工作，我们可以编写一个快速测试：

```java
Foo foo = new Foo();
fooService.create(foo);
fooService.findOne(foo.getId());
int size = CacheManager.ALL_CACHE_MANAGERS.get(0)
  .getCache("com.baeldung.hibernate.cache.model.Foo").getSize();
assertThat(size, greaterThan(0));
```

这里我们直接使用 Ehcache API 来验证加载Foo实例后com.baeldung.hibernate.cache.model.Foo缓存是否为空。

我们还可以启用 Hibernate 生成的 SQL 的日志记录，并在测试中多次调用fooService.findOne(foo.getId())以验证用于加载Foo的select语句仅打印一次(第一次)，这意味着在随后的调用中，实体实例从缓存中获取。

## 6.缓存并发策略

根据用例，我们可以自由选择以下缓存并发策略之一：

-   READ_ONLY：仅用于永不更改的实体(如果尝试更新此类实体，则会抛出异常)。它非常简单和高效。适用于不会变化的静态引用数据。
-   NONSTRICT_READ_WRITE：在提交更改受影响数据的事务后更新缓存。因此，不能保证强一致性，并且有一个小的时间窗口可以从缓存中获取陈旧数据。这种策略适用于可以容忍最终一致性的用例。
-   READ_WRITE：此策略保证强一致性，它通过使用“软”锁实现。当缓存的实体被更新时，软锁也会存储在该实体的缓存中，并在事务提交后释放。所有访问软锁条目的并发事务都将直接从数据库中获取相应的数据。
-   TRANSACTIONAL：缓存更改在分布式 XA 事务中完成。缓存实体中的更改在同一 XA 事务中的数据库和缓存中提交或回滚。

## 7.缓存管理

如果未定义过期和逐出策略，缓存可能会无限增长，并最终耗尽所有可用内存。在大多数情况下，Hibernate 将此类缓存管理职责留给缓存提供者，因为它们确实特定于每个缓存实现。

例如，我们可以定义以下 Ehcache 配置以将缓存的Foo实例的最大数量限制为 1000：

```xml
<ehcache>
    <cache name="com.baeldung.persistence.model.Foo" maxElementsInMemory="1000" />
</ehcache>
```

## 8. 集合缓存

默认情况下不缓存集合，我们需要将它们显式标记为可缓存：

```java
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Foo {

    ...

    @Cacheable
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany
    private Collection<Bar> bars;

    // getters and setters
}
```

## 9.缓存状态的内部表示

实体不作为Java实例存储在二级缓存中，而是以它们的反汇编(水化)状态存储：

-   不存储 ID(主键)(它作为缓存键的一部分存储)
-   不存储瞬态属性
-   不存储集合(有关更多详细信息，请参见下文)
-   非关联属性值以其原始形式存储
-   ToOne关联仅存储 id(外键)

这描述了一般的 Hibernate 二级缓存设计，其中缓存模型反映了底层关系模型，这是空间高效的，并且很容易保持两者同步。

### 9.1. 缓存集合的内部表示

我们已经提到，我们必须明确指示集合(OneToMany或ManyToMany关联)是可缓存的，否则不会被缓存。

Hibernate 实际上将集合存储在单独的缓存区域中，每个集合一个。区域名称是一个完全限定的类名，加上一个集合属性的名称(例如，com.baeldung.hibernate.cache.model.Foo.bars)。这使我们能够灵活地为集合定义单独的缓存参数，例如逐出/过期策略。

同样重要的是要提到，只有集合中包含的实体的 ID 才会为每个集合条目缓存。这意味着在大多数情况下，让包含的实体也可缓存是个好主意。

## 10. HQL DML 样式查询和本机查询的缓存失效

当谈到 DML 风格的 HQL(插入、更新和删除HQL 语句)时，Hibernate 能够确定哪些实体受到此类操作的影响：

```java
entityManager.createQuery("update Foo set … where …").executeUpdate();
```

在这种情况下，所有 Foo 实例都从 L2 缓存中逐出，而其他缓存内容保持不变。

但是，当涉及到原生 SQL DML 语句时，Hibernate 无法猜测正在更新的内容，因此它使整个二级缓存失效：

```java
session.createNativeQuery("update FOO set … where …").executeUpdate();
```

这可能不是我们想要的。解决方案是告诉 Hibernate 哪些实体受到原生 DML 语句的影响，这样它就可以只驱逐与Foo实体相关的条目：

```java
Query nativeQuery = entityManager.createNativeQuery("update FOO set ... where ...");
nativeQuery.unwrap(org.hibernate.SQLQuery.class).addSynchronizedEntityClass(Foo.class);
nativeQuery.executeUpdate();
```

我们必须回退到 Hibernate 本机SQLQuery API，因为 JPA 中尚未定义此功能。

请注意，以上仅适用于 DML 语句(插入、更新、删除和本机函数/过程调用)。本机选择查询不会使缓存无效。

## 11.查询缓存

我们还可以缓存 HQL 查询的结果。如果我们经常对很少更改的实体执行查询，这将很有用。

要启用查询缓存，我们将hibernate.cache.use_query_cache属性的值设置为true：

```plaintext
hibernate.cache.use_query_cache=true
```

对于每个查询，我们必须明确指出该查询是可缓存的(通过org.hibernate.cacheable查询提示)：

```java
entityManager.createQuery("select f from Foo f")
  .setHint("org.hibernate.cacheable", true)
  .getResultList();
```

### 11.1. 查询缓存最佳实践

以下是与查询缓存相关的一些指南和最佳实践：

-   与集合的情况一样，只有作为可缓存查询结果返回的实体的 ID 会被缓存。因此，我们强烈建议为此类实体启用二级缓存。
-   每个查询的每个查询参数值(绑定变量)组合都有一个缓存条目，因此我们期望参数值的许多不同组合的查询不是缓存的好候选者。
-   涉及数据库中频繁更改的实体类的查询也不是缓存的好候选者，因为只要有与参与查询的任何实体类相关的更改，无论更改的实例是否被缓存，它们都会失效是否作为查询结果的一部分。
-   默认情况下，所有查询缓存结果都存储在org.hibernate.cache.internal.StandardQueryCache区域中。与实体/集合缓存一样，我们可以为该区域自定义缓存参数，以根据需要定义逐出和过期策略。对于每个查询，我们还可以指定一个自定义区域名称，以便为不同的查询提供不同的设置。
-   对于作为可缓存查询的一部分进行查询的所有表，Hibernate 将最后更新时间戳保存在名为org.hibernate.cache.spi.UpdateTimestampsCache的单独区域中。如果我们使用查询缓存，了解这个区域非常重要，因为 Hibernate 使用它来验证缓存的查询结果是否过时。只要在查询结果区域中存在对应表的缓存查询结果，就不能将此缓存中的条目逐出/过期。最好关闭此缓存区域的自动逐出和过期，因为它不会消耗大量内存。

## 12.总结

在本文中，我们学习了如何设置 Hibernate 二级缓存。Hibernate 相当容易配置和使用，使二级缓存的使用对应用程序业务逻辑透明。