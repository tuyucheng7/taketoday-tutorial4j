## 1. 概述

在本教程中，我们将研究我们在REST架构中经常使用的两个重要的HTTP方法，PUT和POST。众所周知，开发人员有时在设计RESTful Web服务时难以在这两种方法之间做出选择。因此，我们将通过在Spring Boot中简单实现RESTful应用程序来解决这个问题。

## 2. PUT与POST困境

在典型的[REST架构](https://www.baeldung.com/cs/rest-architecture)中，客户端以HTTP方法的形式向服务器发送请求，以创建、检索、修改或销毁资源。虽然我们可以同时使用PUT和POST来创建资源，但它们在预期应用方面存在显着差异。

根据[RFC 2616](https://tools.ietf.org/html/rfc2616)标准，应使用POST方法请求服务器接受封闭实体作为Request-URI标识的现有资源的从属。这意味着POST方法调用将在资源集合下创建一个子资源。

相反，应该使用PUT方法来请求服务器在提供的Request-URI下存储封闭的实体。如果Request-URI指向服务器上的现有资源，则提供的实体将被视为现有资源的修改版本。因此，PUT方法调用将创建新资源或更新现有资源。

这些方法之间的另一个重要区别是PUT是一种幂等方法，而POST不是。例如，多次调用PUT方法将创建或更新相同的资源。相比之下，多个POST请求会导致多次创建同一个资源。

## 3. 示例应用

为了演示PUT和POST之间的区别，我们将使用[Spring Boot](https://www.baeldung.com/spring-boot)创建一个简单的RESTful Web应用程序。该应用程序将存储人员的姓名和地址。

### 3.1 Maven依赖项

首先，我们需要在pom.xml文件中包含Spring Web、Spring Data JPA和内存中的H2数据库的依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 3.2 域实体和Repository接口

让我们先从创建域对象开始。对于地址簿，我们将定义一个名为Address的实体类，我们将使用它来存储个人的地址信息。为了简单起见，我们将为地址实体使用三个字段，名称、城市和邮政编码：

```java
@Entity
public class Address {

    private @Id @GeneratedValue Long id;
    private String name;
    private String city;
    private String postalCode;

    // constructors, getters, and setters
}
```

下一步是访问数据库中的数据。为简单起见，我们将利用[Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)的JpaRepository，这将允许我们在不编写任何额外代码的情况下对数据执行CRUD功能：

```java
public interface AddressRepository extends JpaRepository<Address, Long> {
}
```

### 3.3  REST控制器

最后，我们需要为我们的应用程序定义API端点，我们将创建一个RestController，它将使用来自客户端的HTTP请求并发回适当的响应。

在这里，我们将定义一个@PostMapping用于创建新地址并将它们存储在数据库中，以及一个@PutMapping用于根据请求URI更新地址簿的内容。如果未找到URI，它将创建一个新地址并将其存储在数据库中：

```java
@RestController
public class AddressController {

    private final AddressRepository repository;

    AddressController(AddressRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/addresses")
    Address createNewAddress(@RequestBody Address newAddress) {
        return repository.save(newAddress);
    }

    @PutMapping("/addresses/{id}")
    Address replaceEmployee(@RequestBody Address newAddress, @PathVariable Long id) {

        return repository.findById(id)
              .map(address -> {
                  address.setCity(newAddress.getCity());
                  address.setPin(newAddress.getPostalCode());
                  return repository.save(address);
              })
              .orElseGet(() -> {
                  return repository.save(newAddress);
              });
    }
    // additional methods omitted
}
```

### 3.4 cURL请求

现在我们可以通过使用cURL向我们的服务器发送示例HTTP请求来测试我们开发的应用程序。

为了创建新地址，我们将以JSON格式封装数据并通过POST请求发送：

```rust
curl -X POST --header 'Content-Type: application/json' \
    -d '{ "name": "John Doe", "city": "Berlin", "postalCode": "10585" }' \ 
    http://localhost:8080/addresses
```

现在让我们更新我们创建的地址的内容。我们将使用URL中该地址的ID发送PUT请求。在此示例中，我们将更新刚创建的地址的城市和邮政编码部分。我们假设它是用id=1保存的：

```rust
curl -X PUT --header 'Content-Type: application/json' \
  -d '{ "name": "John Doe", "city": "Frankfurt", "postalCode": "60306" }' \ 
  http://localhost:8080/addresses/1
```

## 4. 总结

在本文中，我们讨论了HTTP方法PUT和POST之间的概念差异。此外，我们还了解了如何使用用于开发RESTful应用程序的Spring Boot框架来实现这些方法。

总之，我们应该使用POST方法创建新资源，使用PUT方法更新现有资源。