## 1. 简介

在本快速教程中，我们将探讨 Hibernate 提供的查询计划缓存及其对性能的影响。

## 2.查询计划缓存

每个 JPQL 查询或 Criteria 查询在执行之前都被解析为抽象语法树 (AST)，以便 Hibernate 可以生成 SQL 语句。由于查询编译需要时间，Hibernate 提供了一个[QueryPlanCache](https://docs.jboss.org/hibernate/orm/5.0/javadocs/org/hibernate/engine/query/spi/QueryPlanCache.html) 以获得更好的性能。

对于本机查询，Hibernate 提取有关命名参数和查询返回类型的信息并将其存储在 ParameterMetadata [中](https://docs.jboss.org/hibernate/orm/5.0/javadocs/org/hibernate/engine/query/spi/ParameterMetadata.html)。

对于每次执行，Hibernate 首先检查计划缓存，只有在没有计划可用时，它才会生成一个新计划并将执行计划存储在缓存中以备将来参考。

## 三、配置

查询计划缓存配置由以下属性控制：

-   hibernate.query.plan_cache_max_size – 控制计划缓存中的最大条目数(默认为 2048)
-   hibernate.query.plan_parameter_metadata_max_size – 管理缓存中ParameterMetadata实例 的数量(默认为 128)

因此，如果我们的应用程序执行的查询多于查询计划缓存的大小，Hibernate 将不得不花费额外的时间来编译查询。因此，整体查询执行时间将增加。

## 4. 设置测试用例

正如业内所说的那样，当谈到性能时，永远不要相信声称。因此，让我们测试查询编译时间如何随着我们更改缓存设置而变化。

### 4.1. 参与测试的实体类

让我们首先看一下我们将在示例中使用的实体DeptEmployee和Department：

```java
@Entity
public class DeptEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String employeeNumber;

    private String title;

    private String name;

    @ManyToOne
    private Department department;

   // standard getters and setters
}
@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String name;

    @OneToMany(mappedBy="department")
    private List<DeptEmployee> employees;

    // standard getters and setters
}

```

### 4.2. 测试中涉及的 Hibernate 查询

我们只对测量整体查询编译时间感兴趣，因此，我们可以为我们的测试选择有效 HQL 查询的任意组合。

出于本文的目的，我们将使用以下三个查询：

-    按部门名称查找员工

```java
session.createQuery("SELECT e FROM DeptEmployee e " +
  "JOIN e.department WHERE e.department.name = :deptName")
  .setMaxResults(30)
  .setHint(QueryHints.HINT_FETCH_SIZE, 30);
```

-   按指定查找员工

```java
session.createQuery("SELECT e FROM DeptEmployee e " +
  "WHERE e.title = :designation")
  .setHint(QueryHints.SPEC_HINT_TIMEOUT, 1000);
```

-   找到员工部门

```java
session.createQuery("SELECT e.department FROM DeptEmployee e " +
  "JOIN e.department WHERE e.employeeNumber = :empId");
```

## 5. 衡量绩效影响

### 5.1. 基准代码设置

我们将缓存大小从 1 变为 3—— 之后，我们所有的三个查询都已经在缓存中了。因此，没有必要进一步增加它：

```java
@State(Scope.Thread)
public static class QueryPlanCacheBenchMarkState {
    @Param({"1", "2", "3"})
    public int planCacheSize;
    
    public Session session;

    @Setup
    public void stateSetup() throws IOException {
       session = initSession(planCacheSize);
    }

    private Session initSession(int planCacheSize) throws IOException {
        Properties properties = HibernateUtil.getProperties();
        properties.put("hibernate.query.plan_cache_max_size", planCacheSize);
        properties.put("hibernate.query.plan_parameter_metadata_max_size", planCacheSize);
        SessionFactory sessionFactory = HibernateUtil.getSessionFactoryByProperties(properties);
        return sessionFactory.openSession();
    }
    //teardown...
}
```

### 5.2. 被测代码

接下来，让我们看一下用于测量 Hibernate 在编译查询时所花费的平均时间的基准代码：

```java
@Benchmark
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 5)
public void givenQueryPlanCacheSize_thenCompileQueries(
  QueryPlanCacheBenchMarkState state, Blackhole blackhole) {

    Query query1 = findEmployeesByDepartmentNameQuery(state.session);
    Query query2 = findEmployeesByDesignationQuery(state.session);
    Query query3 = findDepartmentOfAnEmployeeQuery(state.session);

    blackhole.consume(query1);
    blackhole.consume(query2);
    blackhole.consume(query3);
}
```

请注意，我们使用[JMH](https://www.baeldung.com/java-microbenchmark-harness)来编写我们的基准测试。

### 5.3. 基准测试结果

现在，让我们可视化我们通过运行上述基准测试准备的编译时间与缓存大小的关系图：

[![缓存映射](https://www.baeldung.com/wp-content/uploads/2019/02/plan-cache.png)](https://www.baeldung.com/wp-content/uploads/2019/02/plan-cache.png)

正如我们在图中可以清楚地看到的那样，增加允许 Hibernate 缓存的查询数量会减少编译时间。

对于 1 的缓存大小，平均编译时间最高，为 709 微秒，然后对于 2 的缓存大小减少到 409 微秒，对于 3 的缓存大小一直减少到 0.637 微秒。

## 6. 使用 Hibernate 统计

为了监控查询计划缓存的有效性，Hibernate 通过 [Statistics](http://docs.jboss.org/hibernate/orm/5.0/javadocs/org/hibernate/stat/Statistics.html)接口公开了以下方法：

-   getQueryPlanCacheHitCount
-   getQueryPlanCacheMissCount

因此，如果命中数很高而未命中数很低，那么大部分查询都是从缓存本身提供的，而不是一遍又一遍地编译。

## 七. 总结

在本文中，我们了解了 Hibernate 中的查询计划缓存是什么以及它如何影响应用程序的整体性能。总的来说，我们应该尽量根据应用程序中运行的查询数量来保持查询计划缓存大小。