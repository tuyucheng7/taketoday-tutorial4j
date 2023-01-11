## 1. 概述

在本教程中，我们继续[Spring Data Querydsl Web支持]()的第二部分，我们重点介绍关联实体以及如何通过HTTP创建查询。

按照第一部分中使用的相同配置，我们将创建一个基于Maven的项目。请阅读原始文章以检查如何设置基础内容。

## 2. 实体

**首先，我们添加一个新实体(Address)，在用户和他的地址之间建立关系**，我们使用一对一关系来保持简单。

因此，我们有以下类：

```java
@Entity
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user")
    private Address addresses;

    // getters & setters 
}
```

```java
@Entity
public class Address {

    @Id
    @GeneratedValue
    private Long id;

    private String address;

    private String country;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // getters & setters
}
```

## 3. Spring Data Repository

此时，我们必须像往常一样为每个实体创建一个Spring Data Repository。请注意，这些Repository将包含Querydsl配置。

**让我们看看AddressRepository并解释框架配置的工作原理**：

```java
public interface AddressRepository extends JpaRepository<Address, Long>, QuerydslPredicateExecutor<Address>, QuerydslBinderCustomizer<QAddress> {

    @Override
    default void customize(QuerydslBindings bindings, QAddress root) {
        bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) StringExpression::eq);
    }
}
```

我们重写customize()方法来配置默认绑定。在本例中，我们为所有String属性将默认方法绑定自定义为equals。

一旦Repository设置完毕，我们只需添加一个@RestController来管理HTTP查询。

## 4. Query Rest Controller

在第一部分中，我们解释了对用户Repository的查询@RestController，在本文中，我们将重用它。

**另外，我们可能需要查询地址表；为此，我们添加一个类似的方法**：

```java
@GetMapping(value = "/addresses", produces = MediaType.APPLICATION_JSON_VALUE)
public Iterable<Address> queryOverAddress(@QuerydslPredicate(root = Address.class) Predicate predicate) {
    BooleanBuilder builder = new BooleanBuilder();
    return addressRepository.findAll(builder.and(predicate));
}
```

## 5. 集成测试

我们提供了一个测试来演示Querydsl的工作原理。为此，我们使用MockMvc框架来模拟HTTP查询用户使用新实体连接此实体Address。因此，我们现在可以查询过滤地址属性。

下面检索居住在中国的所有用户：

```http request
/users?addresses.country=China
```

```java
@Test
void givenRequest_whenQueryUserFilteringByCountrySpain_thenGetJohn() throws Exception {
    mockMvc.perform(get("/users?address.country=China"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(contentType))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("John")))
            .andExpect(jsonPath("$[0].address.address", is("Fake Street 1")))
            .andExpect(jsonPath("$[0].address.country", is("China")));
}
```

因此，Querydsl将映射通过HTTP发送的谓词并生成以下SQL脚本：

```sql
select user0_.id   as id1_1_,
       user0_.name as name2_1_
from user user0_
         cross join address address1_
where user0_.id = address1_.user_id 
  and address1_.country = 'Spain'
```

## 6. 总结

我们演示了Querydsl为Web客户端提供了一个非常简单的创建动态查询的替代方案；这个框架的另一个强大用途。

在[第一部分]()中，我们演示了如何从一个表中检索数据；因此，现在我们可以添加连接多个表的查询，为Web客户端提供更好的体验，直接过滤他们发出的HTTP请求。