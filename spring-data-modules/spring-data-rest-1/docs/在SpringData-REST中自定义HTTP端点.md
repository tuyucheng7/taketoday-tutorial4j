## 1. 简介

Spring Data REST可以消除许多对REST服务来说很自然的样板代码。在本教程中，我们将探讨如何**自定义一些Spring Data REST的HTTP绑定默认值**。

## 2. Spring Data REST Repository基础

首先，我们**创建一个扩展CrudRepository接口的空接口**，指定实体的类型及其主键的类型：

```java
public interface UserRepository extends CrudRepository<WebsiteUser, Long> {}
```

默认情况下，**Spring生成所有需要的映射**，将每个资源配置为可通过适当的HTTP方法访问，并返回适当的状态码。

如果我们不需要CrudRepository中定义的所有资源，我们可以扩展基本的Repository接口并只定义我们需要的资源：

```java
public interface UserRepository extends Repository<WebsiteUser, Long> {
    void deleteById(Long aLong);
}
```

收到请求后，**Spring读取使用的HTTP方法，并根据资源类型调用接口中定义的适当方法(如果存在)**，否则返回HTTP状态码405(Method Not Allowed)。

参考上面的代码，当Spring收到DELETE请求时，它会执行我们的deleteById方法。

## 3. 限制公开哪些HTTP方法

假设我们有一个用户管理系统，那么我们可能会有一个UserRepository。而且，由于我们使用的是Spring Data REST，因此通过扩展CrudRepository可以获得很多功能：

```java
@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends CrudRepository<WebsiteUser, Long> {}
```

**我们的所有资源都使用默认的CRUD模式公开**，因此发出以下请求：

```shell
curl -v -X DELETE http://localhost:8080/users/<existing_user_id>
```

这将返回HTTP状态码204(No Content returned)以确认删除。

现在，假设我们想对第三方隐藏delete方法，同时又能在内部使用它。我们可以首先将deleteById方法签名添加到我们的接口中，该签名向Spring Data REST发出信号，表示我们将对其进行配置。**然后，我们可以使用注解@RestResource(exported = false)，它将配置Spring在触发HTTP方法公开时跳过此方法**：

```java
@Override
@RestResource(exported = false)
void deleteById(Long aLong);
```

现在，如果我们再次运行上面相同的cUrl命令，得到的HTTP状态码将是405(Method Not Allowed)。

## 4. 自定义支持的HTTP方法

@RestResource注解还允许我们能够自定义映射到Repository方法的URL路径以及[HATEOAS]()资源发现返回的JSON中的链接ID 。

为此，我们使用注解的几个可选参数：

-   **path**：URL路径
-   **rel**：链接ID

因此，我们向UserRepository添加一个简单的findByEmail方法：

```java
WebsiteUser findByEmail(@Param("email") String email);
```

通过访问http://localhost:8080/users/search/，我们现在可以看到与其他资源一起列出的新方法：

```json
{
    "_links": {
        "findByEmail": {
            "href": "http://localhost:8080/users/search/findByEmail{?email}"
        },
        "self": {
            "href": "http://localhost:8080/users/search/"
        }
    }
}
```

**如果我们不喜欢默认路径，而不是更改Repository方法，我们可以简单地添加@RestResource注解**：

```java
@RestResource(path = "byEmail", rel = "customFindMethod")
WebsiteUser findByEmail(@Param("email") String email);
```

如果我们再次访问http://localhost:8080/users/search/，可以看到返回JSON中的更改：

```json
{
    "_links": {
        "customFindMethod": {
            "href": "http://localhost:8080/users/search/byEmail{?email}",
            "templated": true
        },
        "self": {
            "href": "http://localhost:8080/users/search/"
        }
    }
}
```

## 5. 编程配置

有时我们需要更细粒度的配置来公开或限制对我们的HTTP方法的访问。例如，对集合资源的POST，以及对元素资源的PUT和PATCH，都使用相同的保存方法。

**从Spring Data REST 3.1和Spring Boot 2.1开始，我们可以通过ExposureConfiguration类更改特定HTTP方法的公开**。这个特定的配置类公开了一个基于lambda的API来定义全局规则和基于类型的规则。

例如，我们可以使用ExposureConfiguration来限制针对UserRepository的PATCH请求：

```java
public class RestConfig implements RepositoryRestConfigurer {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration restConfig, CorsRegistry cors) {
        ExposureConfiguration config = restConfig.getExposureConfiguration();
        config.forDomainType(WebsiteUser.class)
                .withItemExposure((metadata, httpMethods) -> httpMethods.disable(HttpMethod.PATCH));
    }
}
```

## 6. 总结

在本文中，我们探讨了如何配置Spring Data REST来自定义资源中默认支持的HTTP方法。