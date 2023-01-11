## 1. 概述

[GemFire](https://pivotal.io/pivotal-gemfire)是一个高性能的分布式数据管理基础设施，位于应用程序集群和后端数据源之间。

使用 GemFire，可以在内存中管理数据，从而加快访问速度。Spring Data 提供了一种从 Spring 应用程序轻松配置和访问 GemFire 的方法。

在本文中，我们将了解如何使用 GemFire 来满足应用程序的缓存要求。

 

重要更新：项目 spring-data-gemfire[不再维护](https://spring.io/projects/spring-data-gemfire)，不建议在新项目中使用。Spring 团队建议改用[Spring Data for Apache Geode](https://www.baeldung.com/spring-data-geode)。 

## 2.Maven依赖

要使用 Spring Data GemFire 支持，我们首先需要在pom.xml 中添加以下依赖项：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-gemfire</artifactId>
    <version>1.9.1.RELEASE</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.data" AND a%3A"spring-data-gemfire")找到此依赖项的最新版本。

## 3. GemFire 基本功能

### 3.1. 缓存

GemFire 中的缓存提供基本的数据管理服务以及管理与其他对等点的连接。

缓存配置 ( cache.xml ) 描述了数据将如何在不同节点之间分布：

```xml
<cache>
    <region name="region">
        <region-attributes>
            <cache-listener>
                <class-name>
                ...
                </class-name>
            </cache-listener>
        </region-attributes>
    </region>
    ...
</cache>
```

### 3.2. 地区

数据区域是缓存中单个数据集的逻辑分组。

简单的说，region可以让我们把数据存储在系统中的多个VM中，而不需要考虑数据存储在集群中的哪个节点上。

区域分为三大类：

-   区域

    保存每个节点上的完整数据集。它提供了很高的读取性能。写入操作较慢，因为需要将数据更新传播到每个节点：

    ```xml
    <region name="myRegion" refid="REPLICATE"/>
    ```

-   分区区域

    对数据进行分布，使得每个节点只存储一部分区域内容。数据的副本存储在其他节点之一上。它提供了良好的写入性能。

    ```xml
    <region name="myRegion" refid="PARTITION"/>
    ```

-   本地区域

    驻留在定义成员节点上。与集群中的其他节点没有连接。

    ```xml
    <region name="myRegion" refid="LOCAL"/>
    ```

### 3.3. 查询缓存

GemFire 提供了一种称为 OQL(对象查询语言)的查询语言，它允许我们引用存储在 GemFire 数据区域中的对象。这在语法上与 SQL 非常相似。让我们看看一个非常基本的查询是怎样的：

从示例区域中选择不同的 

GemFire 的QueryService提供了创建查询对象的方法。

### 3.4. 数据序列化

为了管理数据序列化-反序列化，GemFire 提供了除 Java 序列化之外的选项，这些选项提供了更高的性能，为数据存储和数据传输提供了更大的灵活性，还支持不同的语言。

考虑到这一点，GemFire 定义了便携式数据交换 (PDX) 数据格式。PDX 是一种跨语言数据格式，它通过将数据存储在命名字段中来提供更快的序列化和反序列化，无需完全反序列化对象即可直接访问。

### 3.5. 函数执行

在 GemFire 中，函数可以驻留在服务器上，并且可以从客户端应用程序或另一台服务器调用，而无需发送函数代码本身。

调用者可以指示依赖数据的函数对特定数据集进行操作，也可以引导独立数据函数对特定服务器、成员或成员组进行操作。

### 3.6. 连续查询

通过连续查询，客户端通过使用 SQL 类型的查询过滤来订阅服务器端事件。服务器发送所有修改查询结果的事件。连续查询事件传递使用客户端/服务器订阅框架。

连续查询的语法类似于用 OQL 编写的基本查询。例如，提供来自股票区域的最新股票数据的查询可以写成：

```plaintext
SELECT  from StockRegion s where s.stockStatus='active';
```

要从此查询中获取状态更新，需要将CQListener的实现附加到StockRegion：

```xml
<cache>
    <region name="StockRegion>
        <region-attributes refid="REPLICATE">
            ...
            <cache-listener>
                <class-name>...</class-name>
            </cache-listener>
        ...
        </region-attributes>
    </region>
</cache>
```

## 4. Spring Data GemFire 支持

### 4.1. Java配置

为了简化配置，Spring Data GemFire 提供了各种用于配置核心 GemFire 组件的注解：

```java
@Configuration
public class GemfireConfiguration {

    @Bean
    Properties gemfireProperties() {
        Properties gemfireProperties = new Properties();
        gemfireProperties.setProperty("name","SpringDataGemFireApplication");
        gemfireProperties.setProperty("mcast-port", "0");
        gemfireProperties.setProperty("log-level", "config");
        return gemfireProperties;
    }

    @Bean
    CacheFactoryBean gemfireCache() {
        CacheFactoryBean gemfireCache = new CacheFactoryBean();
        gemfireCache.setClose(true);
        gemfireCache.setProperties(gemfireProperties());
        return gemfireCache;
    }

    @Bean(name="employee")
    LocalRegionFactoryBean<String, Employee> getEmployee(final GemFireCache cache) {
        LocalRegionFactoryBean<String, Employee> employeeRegion = new LocalRegionFactoryBean();
        employeeRegion.setCache(cache);
        employeeRegion.setName("employee");
        // ...
        return employeeRegion;
    }
}
```

要设置 GemFire 缓存和区域，我们必须首先设置几个特定的属性。此处mcast-port设置为零，表示此 GemFire 节点已禁用多播发现和分发。然后将这些属性传递给CacheFactoryBean以创建GemFireCache实例。

使用GemFireCache bean，创建了一个LocalRegionFatcoryBean实例，它表示Employee实例缓存中的区域。

### 4.2. 实体映射

该库支持将映射对象存储在 GemFire 网格中。映射元数据是通过在域类中使用注解来定义的：

```java
@Region("employee")
public class Employee {

    @Id
    public String name;
    public double salary;

    @PersistenceConstructor
    public Employee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    // standard getters/setters
}
```

在上面的示例中，我们使用了以下注解：

-   @Region ，指定Employee类的region实例
-   @Id，注解应用作缓存键的属性
-   @PersistenceConstructor，这有助于标记将用于创建实体的一个构造函数，以防多个构造函数可用

### 4.3. GemFire 存储库

接下来，让我们看一下 Spring Data 中的一个核心组件——存储库：

```java
@Configuration
@EnableGemfireRepositories(basePackages
  = "com.baeldung.spring.data.gemfire.repository")
public class GemfireConfiguration {

    @Autowired
    EmployeeRepository employeeRepository;
    
    // ...
}
```

### 4.4. Oql 查询支持

存储库允许查询方法的定义，以针对托管实体映射到的区域有效地运行 OQL 查询：

```java
@Repository
public interface EmployeeRepository extends   
  CrudRepository<Employee, String> {

    Employee findByName(String name);

    Iterable<Employee> findBySalaryGreaterThan(double salary);

    Iterable<Employee> findBySalaryLessThan(double salary);

    Iterable<Employee> 
      findBySalaryGreaterThanAndSalaryLessThan(double salary1, double salary2);
}
```

### 4.5. 函数执行支持

我们还提供注解支持——以简化 GemFire 函数执行的工作。

当我们使用函数时，有两个问题需要解决，实现和执行。

让我们看看如何使用 Spring Data 注解将 POJO 公开为 GemFire 函数：

```java
@Component
public class FunctionImpl {

    @GemfireFunction
    public void greeting(String message){
        // some logic
    }
 
    // ...
}
```

我们需要显式激活注解处理，@GemfireFunction才能工作：

```java
@Configuration
@EnableGemfireFunctions
public class GemfireConfiguration {
    // ...
}
```

对于函数执行，调用远程函数的进程需要提供调用参数、函数id、执行目标(onServer、onRegion、onMember等)：

```java
@OnRegion(region="employee")
public interface FunctionExecution {
 
    @FunctionId("greeting")
    public void execute(String message);
    
    // ...
}
```

要启用函数执行注解处理，我们需要使用Spring的组件扫描功能添加激活它：

```java
@Configuration
@EnableGemfireFunctionExecutions(
  basePackages = "com.baeldung.spring.data.gemfire.function")
public class GemfireConfiguration {
    // ...
}
```

## 5.总结

在本文中，我们探讨了 GemFire 的基本功能，并研究了 Spring Data 提供的 API 如何使其易于使用。