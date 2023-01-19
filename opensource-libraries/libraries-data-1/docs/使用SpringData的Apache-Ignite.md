## 1. 概述

在本快速指南中，我们将重点关注如何将 Spring Data API 与 Apache Ignite 平台集成。

要了解 Apache Ignite，请查看[我们之前的指南](https://www.baeldung.com/apache-ignite)。

## 2.Maven 设置

除了现有的依赖项之外，我们还必须启用 Spring Data 支持：

```xml
<dependency>
    <groupId>org.apache.ignite</groupId>
    <artifactId>ignite-spring-data</artifactId>
    <version>${ignite.version}</version>
</dependency>
```

ignite-spring-data工件可以从[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.ignite" AND a%3A"ignite-spring-data")下载。

## 3.模型和存储库

为了演示集成，我们将构建一个应用程序，使用 Spring Data API将员工存储到 Ignite 的缓存中。

EmployeeDTO的 POJO将如下所示：

```java
public class EmployeeDTO implements Serializable {
 
    @QuerySqlField(index = true)
    private Integer id;
    
    @QuerySqlField(index = true)
    private String name;
    
    @QuerySqlField(index = true)
    private boolean isEmployed;

    // getters, setters
}
```

在这里，@QuerySqlField注解允许使用 SQL 查询字段。

接下来，我们将创建存储库来保存 Employee对象：

```java
@RepositoryConfig(cacheName = "baeldungCache")
public interface EmployeeRepository 
  extends IgniteRepository<EmployeeDTO, Integer> {
    EmployeeDTO getEmployeeDTOById(Integer id);
}
```

Apache Ignite 使用自己的IgniteRepository，它是从 Spring Data 的CrudRepository扩展而来的。它还支持从 Spring Data 访问 SQL 网格。 

这支持标准的 CRUD 方法，除了一些不需要 id 的方法。我们将在测试部分更详细地了解原因。

@RepositoryConfig注解将EmployeeRepository映射到 Ignite 的baeldungCache。

## 4.弹簧配置

现在让我们创建我们的 Spring 配置类。

我们将使用 @EnableIgniteRepositories 注解来添加对 Ignite 存储库的支持：

```java
@Configuration
@EnableIgniteRepositories
public class SpringDataConfig {

    @Bean
    public Ignite igniteInstance() {
        IgniteConfiguration config = new IgniteConfiguration();

        CacheConfiguration cache = new CacheConfiguration("baeldungCache");
        cache.setIndexedTypes(Integer.class, EmployeeDTO.class);

        config.setCacheConfiguration(cache);
        return Ignition.start(config);
    }
}
```

在这里，igniteInstance() 方法创建Ignite实例并将其传递给IgniteRepositoryFactoryBean，以便访问 Apache Ignite 集群。

我们还定义并设置了baeldungCache配置。setIndexedTypes()方法为缓存设置 SQL 模式。 

## 5. 测试存储库

为了测试应用程序，让我们在应用程序上下文中注册SpringDataConfiguration并从中获取EmployeeRepository：

```java
AnnotationConfigApplicationContext context
 = new AnnotationConfigApplicationContext();
context.register(SpringDataConfig.class);
context.refresh();

EmployeeRepository repository = context.getBean(EmployeeRepository.class);
```

然后，我们要创建EmployeeDTO实例并将其保存在缓存中：

```java
EmployeeDTO employeeDTO = new EmployeeDTO();
employeeDTO.setId(1);
employeeDTO.setName("John");
employeeDTO.setEmployed(true);

repository.save(employeeDTO.getId(), employeeDTO);
```

这里我们使用了IgniteRepository的save (key, value)方法。原因是标准的CrudRepository save(entity), save(entities), delete(entity)操作还不被支持。

这背后的问题是CrudRepository.save()方法生成的 ID 在集群中不是唯一的。

相反，我们必须使用 save (key, value)、save(Map<ID, Entity> values)、deleteAll(Iterable<ID> ids)方法。

之后，我们可以使用 Spring Data 的getEmployeeDTOById()方法从缓存中获取员工对象：

```java
EmployeeDTO employee = repository.getEmployeeDTOById(employeeDTO.getId());
System.out.println(employee);
```

输出显示我们成功获取了初始对象：

```plaintext
EmployeeDTO{id=1, name='John', isEmployed=true}
```

或者，我们可以使用IgniteCache API 检索相同的对象：

```java
IgniteCache<Integer, EmployeeDTO> cache = ignite.cache("baeldungCache");
EmployeeDTO employeeDTO = cache.get(employeeId);
```

或者使用标准 SQL：

```java
SqlFieldsQuery sql = new SqlFieldsQuery(
  "select  from EmployeeDTO where isEmployed = 'true'");
```

## 6.总结

这个简短的教程展示了如何将 Spring Data Framework 与 Apache Ignite 项目集成。借助实际示例，我们学习了如何使用 Spring Data API 来处理 Apache Ignite 缓存。