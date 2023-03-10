## 1. 简介

Spring Data KeyValue框架使得编写使用键值存储的Spring应用程序变得容易。

它最大限度地减少了与存储交互所需的冗余任务和样板代码。该框架适用于Redis和Riak等键值存储。

在本教程中，**我们将介绍如何将Spring Data KeyValue与默认的基于java.util.Map的实现一起使用**。

## 2. 需求

Spring Data KeyValue 1.x二进制文件需要JDK 6.0或更高版本，以及Spring Framework 3.0.x或更高版本。

## 3. Maven依赖

要使用Spring Data KeyValue，我们需要添加以下依赖项：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-keyvalue</artifactId>
    <version>2.0.6.RELEASE</version>
</dependency>
```

最新版本可以在[这里](https://central.sonatype.com/artifact/org.springframework.data/spring-data-keyvalue/3.0.3)找到。

## 4. 创建实体

让我们创建一个Employee实体：

```java
@KeySpace("employees")
public class Employee {

    @Id
    private Integer id;

    private String name;

    private String department;

    private String salary;

    // constructors/ standard getters and setters
}
```

**@Keyspace定义实体应该保存在数据结构的哪一部分**。这个概念非常类似于MongoDB和Elasticsearch中的集合、Solr中的内核和JPA中的表。

默认情况下，实体的键空间是从其类型中提取的。

## 5. Repository

与其他Spring Data框架类似，**我们需要使用@EnableMapRepositories注解激活Spring Data Repository**。

默认情况下，Repository将使用基于ConcurrentHashMap的实现：

```java
@SpringBootApplication
@EnableMapRepositories
public class SpringDataKeyValueApplication {
}
```

**可以更改默认的ConcurrentHashMap实现并使用其他一些java.util.Map实现**：

```java
@EnableMapRepositories(mapType = WeakHashMap.class)
```

使用Spring Data KeyValue创建Repository的工作方式与其他Spring Data框架相同：

```java
@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Integer> {
}
```

要了解有关Spring Data Repository的更多信息，我们可以查看[这篇文章](https://www.baeldung.com/spring-data-repositories)。

## 6. 使用Repository

通过在EmployeeRepository中扩展CrudRepository，我们得到了一组完整的执行CRUD功能的持久化方法。

现在，我们将了解如何使用一些可用的持久化方法。

### 6.1 保存对象

让我们使用Repository将一个新的Employee对象保存到数据存储中：

```java
Employee employee = new Employee(1, "Mike", "IT", "5000");
employeeRepository.save(employee);
```

### 6.2 检索现有对象

我们可以通过获取员工来验证上一节中员工的正确保存：

```java
Optional<Employee> savedEmployee = employeeRepository.findById(1);
```

### 6.3 更新现有对象

CrudRepository不提供用于更新对象的专用方法。

相反，我们可以使用save()方法：

```java
employee.setName("Jack");
employeeRepository.save(employee);
```

### 6.4 删除现有对象

我们可以使用Repository删除插入的对象：

```java
employeeRepository.deleteById(1);
```

### 6.5 获取所有对象

我们可以获取所有保存的对象：

```java
Iterable<Employee> employees = employeeRepository.findAll();
```

## 7. KeyValueTemplate

对数据结构执行操作的另一种方法是使用KeyValueTemplate。

用非常基本的术语来说，KeyValueTemplate使用包装java.util.Map实现的MapAdapter来执行查询和排序：

```java
@Bean
public KeyValueOperations keyValueTemplate() {
    return new KeyValueTemplate(keyValueAdapter());
}

@Bean
public KeyValueAdapter keyValueAdapter() {
    return new MapKeyValueAdapter(WeakHashMap.class);
}
```

**请注意，如果我们使用了@EnableMapRepositories，则不需要指定KeyValueTemplate。它将由框架本身创建**。

## 8. 使用KeyValueTemplate

使用KeyValueTemplate，我们可以执行与Repository相同的操作。

### 8.1 保存对象

让我们看看如何使用模板将新的Employee对象保存到数据存储中：

```java
Employee employee = new Employee(1, "Mile", "IT", "5000");
keyValueTemplate.insert(employee);
```

### 8.2 检索现有对象

我们可以通过使用模板从结构中获取对象来验证对象的插入：

```java
Optional<Employee> savedEmployee = keyValueTemplate
    .findById(id, Employee.class);
```

### 8.3 更新现有对象

与CrudRepository不同，该模板提供了一个专门的方法来更新对象：

```java
employee.setName("Jacek");
keyValueTemplate.update(employee);
```

### 8.4 删除现有对象

我们可以使用模板删除对象：

```java
keyValueTemplate.delete(id, Employee.class);
```

### 8.5 获取所有对象

我们可以使用模板获取所有保存的对象：

```java
Iterable<Employee> employees = keyValueTemplate
  .findAll(Employee.class);
```

### 8.6 排序对象

除了基本功能外，**该模板还支持用于编写自定义查询的KeyValueQuery**。

例如，我们可以使用查询来获取根据薪水排序的员工列表：

```java
KeyValueQuery<Employee> query = new KeyValueQuery<Employee>();
query.setSort(new Sort(Sort.Direction.DESC, "salary"));
Iterable<Employee> employees = keyValueTemplate.find(query, Employee.class);
```

## 9. 总结

本文展示了我们如何将Spring Data KeyValue框架与使用Repository或KeyValueTemplate的默认Map实现结合使用。

还有更多的Spring Data Frameworks，例如Spring Data Redis，它们是在Spring Data KeyValue之上编写的。Spring Data Redis的介绍可以参考[这篇文章](https://www.baeldung.com/spring-data-redis-tutorial)。