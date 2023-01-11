## 1. 概述

[Okta](https://developer.okta.com/)为Web、移动或API服务提供身份验证、授权和社交登录等功能。此外，它还对Spring框架提供了强大的支持，使集成变得非常简单。

既然[Stormpath](https://stormpath.com/)与Okta联手为开发人员提供更好的身份API，它现在是一种在Web应用程序中启用身份验证的流行方式。

在本教程中，我们介绍Spring Security与Okta以及Okta开发人员帐户的简约设置。

## 2. 设置Okta

### 2.1 开发者帐户注册

首先，我们将[注册一个免费的Okta开发者帐户](https://developer.okta.com/signup/)，该帐户可为每月最多1k的活跃用户提供访问权限。如果我们已经有一个账号，我们可以跳过这一部分：

[![img](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-04-30-at-11.52.36-AM-1024x664.png)](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-04-30-at-11.52.36-AM.png)

### 2.2 仪表板

登录到Okta开发人员帐户后，我们进入仪表板屏幕，向我们简要介绍用户数量、身份验证和登录失败。

此外，它还显示了系统的详细日志条目：

[![img](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-04-30-at-3.48.49-PM-1024x664.png)](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-04-30-at-3.48.49-PM.png)

 

此外，我们将注意到仪表板右上角的Org URL，这是我们稍后将创建的Spring Boot应用程序中的Okta设置所必需的。

### 2.3. 创建新应用程序

然后，让我们使用Applications菜单创建一个新应用程序，为Spring Boot创建OpenID Connect(OIDC)应用程序。

此外，我们将从Native、Single-Page App和Service等可用选项中选择Web平台：

[![img](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-04-30-at-11.58.42-AM-1024x715.png)](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-04-30-at-11.58.42-AM.png)

### 2.4 应用程序设置

接下来，让我们配置一些应用程序设置，例如指向我们应用程序的基本URI和登录重定向URI：

[![img](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-04-30-at-12.08.56-PM-735x1024.png)](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-04-30-at-12.08.56-PM.png)

此外，请确保将授权码标记为允许的授权类型，这是为Web应用程序启用OAuth2身份验证所必需的。

### 2.5 客户凭证

然后，我们将获取与应用关联的客户端ID和客户端密码的值：

[![img](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-04-30-at-12.10.59-PM-1024x450.png)](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-04-30-at-12.10.59-PM.png)

请随身携带这些凭据，因为它们是Okta设置所必需的。

## 3. Spring Boot应用程序设置

现在我们的Okta开发人员帐户已准备好基本配置，我们准备将Okta安全支持集成到Spring Boot应用程序中。

### 3.1 Maven

首先，让我们将okta-spring-boot-starter Maven依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.okta.spring</groupId>
    <artifactId>okta-spring-boot-starter</artifactId>
    <version>1.4.0</version>
</dependency>
```

### 3.2 Gradle

同样，在使用Gradle时，我们可以在build.gradle中添加okta-spring-boot-starter依赖：

```groovy
implementation 'com.okta.spring:okta-spring-boot-starter:1.4.0'
```

### 3.3 application.properties

然后，我们将在application.properties中配置Okta oauth2属性：

```properties
okta.oauth2.issuer=https://dev-44431010.okta.com/oauth2/default
okta.oauth2.client-id=0oa77uolz3FiSdVSy5d7
okta.oauth2.client-secret=FQF3INkq9e2tPYDoPG-mELqxobnQro4ysi42HYts
okta.oauth2.redirect-uri=/authorization-code/callback
```

在这里，我们可以为指向[{orgURL}/oauth2/default](https://{youroktadomain}/oauth2/default)的颁发者URL使用默认授权服务器(如果没有可用)。

此外，我们还可以使用API菜单在Okta开发者帐户中创建一个新的授权服务器：

[![img](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-05-02-at-8.21.07-PM-1024x406.png)](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-05-02-at-8.21.07-PM.png)

然后，我们将添加上一节中生成的Okta应用程序的客户端ID和客户端密码。

最后，我们配置了在应用程序设置中设置的相同重定向uri。

## 4. HomeController

之后，让我们创建HomeController类：

```java
@RestController
public class HomeController {

	@GetMapping("/")
	public String home(@AuthenticationPrincipal OidcUser user) {
		return "Welcome, " + user.getFullName() + "!";
	}
}
```

在这里，我们在应用程序设置中添加了带有Base Uri(/)映射的home方法。

另外，home方法的参数是Spring Security提供的用于访问用户信息的[OidcUser](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/oauth2/core/oidc/user/OidcUser.html)类的实例。

就这样，我们的Spring Boot应用程序已准备好Okta安全支持。让我们使用Maven命令运行我们的应用程序：

```shell
mvn spring-boot:run
```

在[localhost:8080](http://localhost:8080/)访问应用程序时，我们将看到Okta提供的默认登录页面：

[![img](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-05-01-at-6.54.18-PM-947x1024.png)](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-05-01-at-6.54.18-PM.png)

使用注册用户的凭据登录后，将显示带有用户全名的欢迎消息：

[![img](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-05-03-at-9.00.15-AM-1024x85.png)](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-05-03-at-9.00.15-AM.png)

此外，我们将在默认登录屏幕的底部找到一个“注册”链接，用于自行注册。

## 5. 注册

### 5.1 自行注册

第一次，我们可以使用“注册”链接创建一个Okta帐户，然后提供电子邮件、名字和姓氏等信息：

[![img](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-05-03-at-12.06.36-PM-849x1024.png)](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-05-03-at-12.06.36-PM.png)

### 5.2 创建用户

或者，我们可以从Okta开发者帐户的用户菜单中创建一个新用户：

[![img](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-05-03-at-12.04.33-PM-1024x834.png)](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-05-03-at-12.04.33-PM.png)

### 5.3. 自助注册设置

此外，可以从Okta开发人员帐户的用户菜单中配置注册和注册设置：

[![img](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-05-03-at-12.05.47-PM-1024x808.png)](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-05-03-at-12.05.47-PM.png)

## 6. Okta Spring SDK

现在我们已经看到了Spring Boot应用程序中的Okta安全集成，让我们在同一个应用程序中与Okta管理API进行交互。

首先，我们应该使用Okta开发者帐户中的API菜单创建一个Token ：

[![img](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-05-01-at-6.23.53-PM-1024x642.png)](https://www.baeldung.com/wp-content/uploads/2020/05/Screen-Shot-2020-05-01-at-6.23.53-PM.png)

确保记下令牌，因为它在生成后仅显示一次。然后，它将被存储为哈希值以供我们保护。

### 6.1 设置

然后，让我们将okta-spring-sdk Maven依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.okta.spring</groupId>
    <artifactId>okta-spring-sdk</artifactId>
    <version>1.4.0</version>
</dependency>
```

### 6.2 application.properties

接下来，我们将添加一些基本的 Okta 客户端属性：

```properties
okta.client.orgUrl=https://dev-44431010.okta.com
okta.client.token=00Q5ws_gSGLitC1e4mgkCiusHvMHl0k1SG-Or6wobQ
```

在这里，我们添加了上一节中提到的令牌。

### 6.3 管理员控制器

最后，让我们创建注入[Client](https://developer.okta.com/okta-sdk-java/apidocs/index.html?com/okta/sdk/client/Client.html)实例的AdminController ：

```java
@RestController
public class AdminController {
    @Autowired
    public Client client;
}
```

而已！我们已准备好调用客户端实例上的方法来向Okta API发出请求。

### 6.4 列出用户

让我们创建getUsers方法来获取组织中所有用户的列表，使用返回[UserList](https://developer.okta.com/okta-sdk-java/apidocs/com/okta/sdk/resource/user/UserList.html)对象的listUsers方法：

```java
public class AdminController {
    // ...

    @GetMapping("/users") 
    public UserList getUsers() { 
        return client.listUsers(); 
    }
}
```

之后，我们可以访问[localhost:8080/users](http://localhost:8080/users)以接收包含所有用户的JSON响应：

```javascript
{
    "dirty":false,
    "propertyDescriptors":{
        "items":{
            "name":"items",
            "type":"com.okta.sdk.resource.user.User"
        }
    },
    "resourceHref":"/api/v1/users",
    "currentPage":{
        "items":[
            {
                "id":"00uanxiv7naevaEL14x6",
                "profile":{
                    "firstName":"Anshul",
                    "lastName":"Bansal",
                    "email":"anshul@bansal.com",
                    // ...
                },
                // ...
            },
            { 
                "id":"00uag6vugXMeBmXky4x6", 
                "profile":{ 
                    "firstName":"Ansh", 
                    "lastName":"Bans", 
                    "email":"ansh@bans.com",
                    // ... 
                }, 
                // ... 
            }
        ]
    },
    "empty":false,
    // ...
}
```

### 6.5 搜索用户

同样，我们可以使用firstName、lastName或email作为查询参数来过滤用户：

```java
@GetMapping("/user")
public UserList searchUserByEmail(@RequestParam String query) {
    return client.listUsers(query, null, null, null, null);
}
```

让我们使用localhost:8080/user?query=ansh@bans.com通过电子邮件搜索用户：

```javascript
{
    "dirty":false,
    "propertyDescriptors":{
        "items":{
            "name":"items",
            "type":"com.okta.sdk.resource.user.User"
        }
    },
    "resourceHref":"/api/v1/users?q=ansh%40bans.com",
    "currentPage":{
        "items":[
            {
                "id":"00uag6vugXMeBmXky4x6",
                "profile":{
                    "firstName":"Ansh",
                    "lastName":"Bans",
                    "email":"ansh@bans.com",
                    // ...
                },
                // ...
            }
        ]
    },
    // ...
}
```

### 6.6 创建用户

[另外，我们可以使用UserBuilder](https://developer.okta.com/okta-sdk-java/apidocs/com/okta/sdk/resource/user/UserBuilder.html)接口的实例方法创建一个新用户：

```java
@GetMapping("/createUser")
public User createUser() {
    char[] tempPassword = {'P','a','$','$','w','0','r','d'};
    User user = UserBuilder.instance()
        .setEmail("normal.lewis@email.com")
        .setFirstName("Norman")
        .setLastName("Lewis")
        .setPassword(tempPassword)
        .setActive(true)
        .buildAndCreate(client);
    return user;
}
```

因此，让我们访问[localhost:8080/createUser](http://localhost:8080/createUser)并验证新用户的详细信息：

```javascript
{
    "id": "00uauveccPIYxQKUf4x6",   
    "profile": {
        "firstName": "Norman",
        "lastName": "Lewis",
        "email": "norman.lewis@email.com"
    },
    "credentials": {
        "password": {},
        "emails": [
            {
                "value": "norman.lewis@email.com",
                "status": "VERIFIED",
                "type": "PRIMARY"
            }
        ],
        // ...
    },
    "_links": {
        "resetPassword": {
            "href": "https://dev-example123.okta.com/api/v1/users/00uauveccPIYxQKUf4x6/lifecycle/reset_password",
            "method": "POST"
        },
        // ...
    }
}
```

同样，我们可以执行一系列操作，例如列出所有应用程序、创建应用程序、列出所有组和创建组。

## 7. 总结

在这个快速教程中，我们使用 Okta 探索了 Spring Security。

首先，我们使用基本配置设置 Okta 开发者帐户。然后，我们创建了一个Spring Boot应用程序并配置了application.properties以用于 Spring Security 与 Okta 的集成。

接下来，我们集成了 Okta Spring SDK 来管理 Okta API。最后，我们研究了列出所有用户、搜索用户和创建用户等功能。