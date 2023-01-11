## 一、简介

Spring Data Key Value 框架使得编写使用键值存储的 Spring 应用程序变得容易。

它最大限度地减少了与商店交互所需的冗余任务和样板代码。该框架适用于 Redis 和 Riak 等键值存储。

在本教程中，**我们将介绍如何将 Spring Data Key Value 与默认 的基于\*java.util.Map\*的实现一起使用。**

## 二、要求

Spring Data Key Value 1.x 二进制文件需要 JDK 级别 6.0 或更高版本，以及 Spring Framework 3.0.x 或更高版本。

## 3.Maven依赖

要使用 Spring Data Key Value，我们需要添加以下依赖项：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-keyvalue</artifactId>
    <version>2.0.6.RELEASE</version>
</dependency>
复制
```

最新版本可以在[这里](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.data" AND a%3A"spring-data-keyvalue")找到。

## 4.创建实体

让我们创建一个*Employee*实体：

```java
@KeySpace("employees")
public class Employee {

    @Id
    private Integer id;

    private String name;

    private String department;

    private String salary;

    // constructors/ standard getters and setters

}复制
```

***键空间\*定义实体应该保存在数据结构的哪一部分。**这个概念非常类似于 MongoDB 和 Elasticsearch 中的集合、Solr 中的核心和 JPA 中的表。

默认情况下，实体的键空间是从其类型中提取的。

## 5.存储库

与其他 Spring Data 框架类似，我们**需要使用 \*@EnableMapRepositories\*注释**激活 Spring Data 存储库。

默认情况下，存储库将使用 基于*ConcurrentHashMap*的实现：

```java
@SpringBootApplication
@EnableMapRepositories
public class SpringDataKeyValueApplication {
}复制
```

**可以更改默认的\*ConcurrentHashMap\*实现并使用其他一些\*java.util.Map\* 实现：**

```java
@EnableMapRepositories(mapType = WeakHashMap.class)
复制
```

使用 Spring Data Key Value 创建存储库的工作方式与其他 Spring Data 框架相同：

```java
@Repository
public interface EmployeeRepository
  extends CrudRepository<Employee, Integer> {
}复制
```

要了解有关 Spring Data 存储库的更多信息，我们可以查看[这篇文章](https://www.baeldung.com/spring-data-repositories)。

## 6. 使用存储库

通过扩展*EmployeeRepository*中的 *CrudRepository*，我们获得了一组完整的执行 CRUD 功能的持久化方法。

现在，我们将了解如何使用一些可用的持久化方法。

### 6.1. 保存对象

让我们使用存储库将一个新的*Employee*对象保存到数据存储中：

```java
Employee employee = new Employee(1, "Mike", "IT", "5000");
employeeRepository.save(employee);复制
```

### 6.2. 检索现有对象

我们可以通过获取员工来验证上一节中员工的正确保存：

```java
Optional<Employee> savedEmployee = employeeRepository.findById(1);
复制
```

### 6.3. 更新现有对象

*CrudRepository*不提供用于更新对象的专用方法。

相反，我们可以使用*save()*方法：

```java
employee.setName("Jack");
employeeRepository.save(employee);复制
```

### 6.4. 删除现有对象

我们可以使用存储库删除插入的对象：

```java
employeeRepository.deleteById(1);
复制
```

### 6.5. 获取所有对象

我们可以获取所有保存的对象：

```java
Iterable<Employee> employees = employeeRepository.findAll();复制
```

## 7.*键值模板*

对数据结构执行操作的另一种方法是使用*KeyValueTemplate*。

用非常基本的术语来说，*KeyValueTemplate*使用一个*MapAdapter*包装一个*java.util.Map* 实现来执行查询和排序：

```java
@Bean
public KeyValueOperations keyValueTemplate() {
    return new KeyValueTemplate(keyValueAdapter());
}

@Bean
public KeyValueAdapter keyValueAdapter() {
    return new MapKeyValueAdapter(WeakHashMap.class);
}
复制
```

**请注意，如果我们使用\*了 @EnableMapRepositories\*，则不需要指定 \*KeyValueTemplate。\* 它将由框架本身创建。**

## 8. 使用*键值模板*

使用 *KeyValueTemplate*，我们可以执行与存储库相同的操作。

### 8.1. 保存对象

让我们看看如何使用模板将新的*Employee对象保存到数据存储中：*

```java
Employee employee = new Employee(1, "Mile", "IT", "5000");
keyValueTemplate.insert(employee);
复制
```

### 8.2. 检索现有对象

我们可以通过使用模板从结构中获取对象来验证对象的插入：

```java
Optional<Employee> savedEmployee = keyValueTemplate
  .findById(id, Employee.class);
复制
```

### 8.3. 更新现有对象

与 *CrudRepository*不同，模板提供了一个专门的方法来更新对象：

```java
employee.setName("Jacek");
keyValueTemplate.update(employee);复制
```

### 8.4. 删除现有对象

我们可以使用模板删除对象：

```java
keyValueTemplate.delete(id, Employee.class);复制
```

### 8.5. 获取所有对象

我们可以使用模板获取所有保存的对象：

```java
Iterable<Employee> employees = keyValueTemplate
  .findAll(Employee.class);复制
```

### 8.6. 排序对象

除了基本功能外，**该模板还支持用于编写自定义查询的\*KeyValueQuery 。\***

例如，我们可以使用查询来获取根据薪水排序的*员工列表：*

```java
KeyValueQuery<Employee> query = new KeyValueQuery<Employee>();
query.setSort(new Sort(Sort.Direction.DESC, "salary"));
Iterable<Employee> employees 
  = keyValueTemplate.find(query, Employee.class);复制
```

## 9.结论

本文展示了我们如何将 Spring Data KeyValue 框架与使用 *Repository*或*KeyValueTemplate*的默认 Map 实现结合使用。

还有更多的 Spring Data Frameworks，例如 Spring Data Redis，它们是在 Spring Data Key Value 之上编写的。Spring Data Redis 的介绍可以参考 [这篇文章](https://www.baeldung.com/spring-data-redis-tutorial)。