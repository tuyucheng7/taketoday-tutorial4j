## 1. 概述

Spring Data的CrudRepository#save无疑是简单的，但有一个功能可能是一个缺点：它会更新表中的每一列。这就是CRUD中U的语义，**但是如果我们想改为执行PATCH怎么办**？

在本教程中，我们将介绍执行部分更新而不是完整更新的技术和方法。

## 2. 问题

如前所述，save()将使用提供的数据覆盖任何匹配的实体，这意味着我们不能提供部分数据。这可能会变得不方便，特别是对于具有很多字段的较大对象。

如果我们查看ORM，就会发现一些补丁：

-   Hibernate的[@DynamicUpdate注解](https://www.baeldung.com/spring-data-jpa-dynamicupdate)，动态重写更新查询
-   JPA的@Column注解，因为我们可以使用updatable参数禁止对特定列进行更新

但是我们将以特定的意图来解决这个问题：**我们的目的是在不依赖ORM的情况下为save方法准备我们的实体**。

## 3. 案例构建

首先，让我们构建一个Customer实体：

```java
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    public String name;
    public String phone;
}
```

然后我们定义一个简单的CRUD Repository：

```java
@Repository 
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    Customer findById(long id);
}
```

最后，我们准备一个CustomerService：

```java
@Service
public class CustomerService {
    @Autowired
    CustomerRepository repo;

    public void addCustomer(String name) {
        Customer c = new Customer();
        c.name = name;
        repo.save(c);
    }
}
```

## 4. 加载和保存方法

**让我们首先来看一种可能很熟悉的方法：从数据库加载我们的实体，然后只更新我们需要的字段**。这是我们可以使用的最简单的方法。

让我们在服务中添加一个方法来更新Customer的phone数据。

```java
public void updateCustomerContacts(long id, String phone) {
    Customer myCustomer = repo.findById(id);
    myCustomer.phone = phone;
    repo.save(myCustomer);
}
```

我们将调用findById方法并检索匹配的实体。然后我们继续更新所需的字段并保存数据。

当要更新的字段数量相对较少且我们的实体相当简单时，这种基本技术非常有效。

更新数十个字段会发生什么情况？

### 4.1 映射策略

当我们的对象有大量具有不同[访问级别](https://www.baeldung.com/java-access-modifiers)的字段时，实现[DTO模式](https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application)是很常见的。

现在假设我们的对象中有一百多个phone字段。像我们之前所做的那样，编写一个将数据从DTO传输到我们的实体的方法可能很烦人，而且非常难以维护。

然后，我们可以使用映射策略来解决这个问题，特别是使用[MapStruct](https://www.baeldung.com/mapstruct)实现。

让我们创建一个CustomerDto：

```java
public class CustomerDto {
    private long id;
    public String name;
    public String phone;
    //...
    private String phone99;
}
```

我们还将创建一个CustomerMapper：

```java
@Mapper(componentModel = "spring")
public interface CustomerMapper {
    void updateCustomerFromDto(CustomerDto dto, @MappingTarget Customer entity);
}
```

@MappingTarget注解允许我们更新现有对象，从而省去了编写大量代码的痛苦。

MapStruct有一个@BeanMapping方法装饰器，它允许我们定义一个规则来在映射过程中跳过null值。

让我们将其添加到我们的updateCustomerFromDto接口方法上：

```java
@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
```

**有了这个，我们可以在调用JPA save方法之前加载存储的实体并将它们与DTO合并-事实上，我们将只更新修改后的值**。

因此，我们向Service添加一个方法，它将调用我们的映射器：

```java
public void updateCustomer(CustomerDto dto) {
    Customer myCustomer = repo.findById(dto.id);
    mapper.updateCustomerFromDto(dto, myCustomer);
    repo.save(myCustomer);
}
```

这种方法的缺点是我们不能在更新期间将null值传递给数据库。

### 4.2 更简单的实体

最后请记住，我们可以从应用程序的设计阶段开始解决这个问题。

**将我们的实体定义为尽可能小是很重要的**。

让我们来看看我们的Customer实体。

我们将对其进行一些结构化，将所有phone字段提取到ContactPhone实体中，并建立[一对多](https://www.baeldung.com/hibernate-one-to-many)关系：

```java
@Entity
public class CustomerStructured {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public String name;
    @OneToMany(fetch = FetchType.EAGER, targetEntity=ContactPhone.class, mappedBy="customerId")
    private List<ContactPhone> contactPhones;
}
```

代码很干净，更重要的是，我们取得了一些成就。现在我们可以更新我们的实体，而不必检索和填充所有phone数据。

**处理小型实体和有界实体允许我们只更新必要的字段**。

这种方法的唯一不便之处在于我们应该有意识地设计我们的实体，而不会陷入过度设计的陷阱。

## 5. 自定义查询

我们可以实现的另一种方法是为部分更新定义自定义查询。

事实上，JPA定义了两个注解[@Modifying](https://www.baeldung.com/spring-data-jpa-modifying-annotation)和[@Query](https://www.baeldung.com/spring-data-jpa-query)，允许我们显式地编写更新语句。

现在，我们可以告诉我们的应用程序在更新期间的行为方式，而不会给ORM留下负担。

让我们在Repository中添加自定义更新方法：

```java
@Modifying
@Query("update Customer u set u.phone = :phone where u.id = :id")
void updatePhone(@Param(value = "id") long id, @Param(value = "phone") String phone);
```

现在我们可以重写我们的更新方法：

```java
public void updateCustomerContacts(long id, String phone) {
    repo.updatePhone(id, phone);
}
```

我们现在可以执行部分更新。只需几行代码并且在不改变实体的情况下，我们就实现了我们的目标。

这种技术的缺点是我们必须为对象的每个可能的部分更新定义一个方法。

## 6. 总结

部分数据更新是一个非常基础的操作；虽然我们可以让ORM框架来处理它，但有时完全控制它是有利可图的。

正如我们所看到的，我们可以预加载数据，然后更新数据或定义我们的自定义语句，但请记住要意识到这些方法所隐含的缺点以及如何克服它们。