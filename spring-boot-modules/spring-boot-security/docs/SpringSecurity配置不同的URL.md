## 1. 概述

在本教程中，我们将了解如何配置Spring Security以针对不同的URL模式使用不同的安全配置。

当应用程序的某些操作需要更高的安全性而所有用户都允许其他操作时，这非常有用。

## 2. 设置

我们需要[Web](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web)和[Security](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-security)依赖项来创建此服务，因此首先我们将以下依赖项添加到`pom.xml`文件中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-security</artifactId> 
</dependency> 
```

## 3. 创建API

我们将创建一个包含两个API的RESTful Web服务：一个products API和一个customers API，为此，我们将设置两个控制器。

### 3.1 产品API

让我们创建ProductController，它包含一个getProducts方法，该方法返回一个产品列表：

```java
@RestController("/products")
public class ProductController {

	@GetMapping
	public List<Product> getProducts() {
		return new ArrayList<>(Arrays.asList(
			new Product("Product 1", "Description 1", 1.0),
			new Product("Product 2", "Description 2", 2.0)
		));
	}
}
```

### 3.2 客户API

同样，让我们定义CustomerController： 

```java
@RestController("/customers")
public class CustomerController {

	@GetMapping("/{id}")
	public Customer getCustomerById(@PathVariable("id") String id) {
		return new Customer("Customer 1", "Address 1", "Phone 1");
	}
}
```

在典型的Web应用程序中，包括来宾用户在内的所有用户都可以获得产品列表。

然而，通过客户的ID获取客户的详细信息似乎只有管理员才能做到。因此，我们将以一种可以实现这一点的方式定义我们的安全配置。

## 4. 设置安全配置

当我们将Spring Security添加到项目中时，默认情况下它将禁用对所有API的访问，因此我们需要配置Spring Security以允许访问API。

让我们创建SecurityConfiguration类：

```java
@Configuration
public class SecurityConfiguration {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/products/**")
			.permitAll()
			.and()
			.authorizeRequests()
			.antMatchers("/customers/**")
			.hasRole("ADMIN")
			.anyRequest()
			.authenticated()
			.and()
			.httpBasic();
		return http.build();
	}
}
```

在这里，我们创建了一个SecurityFilterChain bean来配置应用程序的安全性。此外，为了准备基本身份验证，我们需要[为我们的应用程序配置用户](https://www.baeldung.com/java-config-spring-security)。

下面我们仔细剖析代码的每一部分以更好地理解它。

### 4.1 允许对产品API的请求

-   authorizeRequests()：此方法告诉Spring在授权请求时使用以下规则。
-   antMatchers("/products/**")：这指定应用安全配置的URL模式，我们将它与permitAll()操作链接起来，如果请求的路径中包含“/products”，则允许它转到控制器。
-   我们可以使用and()方法向我们的配置添加更多规则。

这标志着一个规则链的结束，随后的其他规则也将适用于请求，所以我们需要确保我们的规则不会相互冲突。**一个好的做法是在上面定义通用规则，而在下面定义更具体的规则**。

### 4.2 仅允许管理员访问客户API

现在让我们看一下配置的第二部分：

-   要启动一个新规则，我们可以再次使用authorizeRequests()方法。
-   antMatchers("/customers/**").hasRole("ADMIN")：如果URL的路径中包含“/customers”，我们会检查发出请求的用户是否具有ADMIN角色。

如果用户未通过身份验证，这将导致“401 Unauthorized”错误。如果用户没有正确的角色，这将导致“403 Forbidden”错误。

### 4.3 默认规则

我们添加了匹配项以匹配某些请求，现在我们需要为其余请求定义一些默认行为。

anyRequest().authenticated()：**anyRequest()为任何与先前规则不匹配的请求定义规则链**。在我们的例子中，只要经过身份验证，此类请求就会通过。

请注意，**配置中只能有一个默认规则，并且需要位于末尾**。如果我们尝试在添加默认规则后继续添加规则，则会得到一个错误-“Can't configure antMatchers after anyRequest”。

## 5. 测试

让我们使用cURL测试这两个API。

### 5.1 测试产品API

```shell
$ curl -i http://localhost:8080/products
[
  	{
    	"name": "Product 1",
    	"description": "Description 1",
    	"price": 1.0
  	},
  	{
   		"name": "Product 2",
    	"description": "Description 2",
    	"price": 2.0
  	}
]
```

正如预期的那样，我们得到了包含2个产品的响应。

### 5.2 测试客户API

```bash
$ curl -i http://localhost:8080/customers/1
```

响应主体为空。

**如果我们检查标头，我们会看到“401 Unauthorized”状态码，这是因为仅允许具有ADMIN角色的经过身份验证的用户访问客户API**。

现在让我们在请求中添加身份验证信息后重试该请求：

```bash
$ curl -u admin:password -i http://localhost:8080/customers/1 
{
  	"name": "Customer 1",
  	"address": "Address 1",
  	"phone": "Phone 1"
}
```

因此，我们现在可以访问客户API。

## 6. 总结

在本教程中，我们学习了如何在Spring Boot应用程序中设置Spring Security，并介绍了使用antMatchers()方法配置特定于URL模式的访问。