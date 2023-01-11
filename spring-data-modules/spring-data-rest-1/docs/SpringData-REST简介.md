## 1. 概述

本文解释[Spring Data REST](https://spring.io/projects/spring-data-rest)的基础知识，并展开介绍如何使用它来构建简单的REST API。

一般来说，Spring Data REST建立在Spring Data项目之上，它可以很容易地构建连接到Spring Data Repository的超媒体驱动的REST Web服务-所有这些都使用HAL作为驱动超媒体类型。

它省去了通常与此类任务相关的大量手动工作，并使为Web应用程序实现基本的CRUD功能变得非常简单。

## 2. Maven依赖

我们的应用程序需要以下Maven依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-rest</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
```

我们在本例中使用Spring Boot，但普通的Spring也可以正常工作。我们还选择使用H2嵌入式数据库以避免任何额外设置，但该示例也可以应用于任何数据库。

## 3. 编写应用程序

首先，我们定义一个表示网站用户的实体：

```java
@Entity
public class WebsiteUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String email;

    // standard getters and setters
}
```

每个用户都有一个名字和一个电子邮件，以及一个自动生成的ID。现在我们编写一个简单的Repository：

```java
@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends PagingAndSortingRepository<WebsiteUser, Long> {
    List<WebsiteUser> findByName(@Param("name") String name);
}
```

这是一个允许你对WebsiteUser对象执行各种操作的接口。我们还定义了一个自定义查询，它根据给定名称返回用户集合。

@RepositoryRestResource注解是可选的，用于自定义REST端点。如果我们省略它，Spring会自动在“/websiteUsers”处创建一个端点，而不是我们在上面指定的“/users”。

最后，我们添加一个标准的Spring Boot主类来初始化应用程序：

```java
@SpringBootApplication
public class SpringDataRestApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringDataRestApplication.class, args);
    }
}
```

就这样，我们现在有一个功能齐全的REST API，让我们来看看它的实际效果。

## 4. 访问REST API

如果我们运行应用程序并在浏览器中访问http://localhost:8080/，返回的JSON如下：

```json
{
    "_links": {
        "users": {
            "href": "http://localhost:8080/users{?page,size,sort}",
            "templated": true
        },
        "profile": {
            "href": "http://localhost:8080/profile"
        }
    }
}
```

正如你所看到的，有一个“/users”端点可用，并且它已经有“?page”、“?size”和“?sort”参数。

此外，还有一个标准的“/profile”端点，它提供应用程序元数据。请务必注意，响应的结构遵循REST架构风格的约束。具体来说，它提供统一的界面和自描述消息。这意味着每条消息都包含足够的信息来描述如何处理消息。

我们的应用程序中还没有用户，所以如果访问http://localhost:8080/users只会显示一个空的用户列表。下面我们使用curl添加用户。

```shell
$ curl -i -X POST -H "Content-Type:application/json" -d '{"name": "Test", "email": "test@test.com"}' http://localhost:8080/users
{
    "name": "test",
    "email": "test@test.com",
    "_links": {
        "self": {
            "href": "http://localhost:8080/users/1"
        },
        "websiteUser": {
            "href": "http://localhost:8080/users/1"
        }
    }
}
```

下面是返回的响应头：

```shell
HTTP/1.1 201 Created
Server: Apache-Coyote/1.1
Location: http://localhost:8080/users/1
Content-Type: application/hal+json;charset=UTF-8
Transfer-Encoding: chunked
```

你会注意到返回的内容类型是“application/hal+json”。[HAL](http://stateless.co/hal_specification.html)是一种简单的格式，它为API中的资源之间的超链接提供了一种一致且简单的方法。该响应还自动包含Location头，这是我们可以用来访问新创建的用户的地址。

现在，我们可以在[http://localhost:8080/users/1](http://localhost:8080/users/1)访问这个用户：

```json
{
    "name": "test",
    "email": "test@test.com",
    "_links": {
        "self": {
            "href": "http://localhost:8080/users/1"
        },
        "websiteUser": {
            "href": "http://localhost:8080/users/1"
        }
    }
}
```

你还可以使用curl或任何其他REST客户端发出PUT、PATCH和DELETE请求。同样重要的是要注意Spring Data REST自动遵循HATEOAS的原则。[HATEOAS](https://spring.io/projects/spring-hateoas)是REST架构风格的约束之一，这意味着应该使用超文本来找到你访问API的方式。

最后，让我们尝试访问我们之前编写的自定义查询并查找所有名为“test”的用户。这是通过访问[http://localhost:8080/users/search/findByName?name=test](http://localhost:8080/users/search/findByName?name=test)来完成的：

```json
{
    "_embedded": {
        "users": [
            {
                "name": "test",
                "email": "test@test.com",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/users/1"
                    },
                    "websiteUser": {
                        "href": "http://localhost:8080/users/1"
                    }
                }
            }
        ]
    },
    "_links": {
        "self": {
            "href": "http://localhost:8080/users/search/findByName?name=test"
        }
    }
}
```

## 5. 总结

本教程演示了使用Spring Data REST创建简单REST API的基础知识。