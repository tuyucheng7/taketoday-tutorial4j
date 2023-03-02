## 1. 概述

在本教程中，我们将实现[Spring CredHub](https://spring.io/projects/spring-credhub)(CredHub的Spring抽象)来存储具有将凭证资源映射到用户和操作的访问控制规则的机密。请注意，在运行代码之前，我们需要确保我们的应用程序在安装了CredHub的[Cloud Foundry](https://www.baeldung.com/cloud-foundry-uaa)平台上运行。

## 2. Maven依赖

首先，我们需要安装[spring-credhub-starter](https://search.maven.org/search?q=a:spring-credhub-starter)依赖项：

```xml
<dependency>
    <groupId>org.springframework.credhub</groupId>
    <artifactId>spring-credhub-starter</artifactId>
    <version>2.2.0</version>
</dependency>
```

## 3. 为什么凭证管理很重要？

**凭证管理是对凭证整个生命周期进行安全、集中处理的过程，主要包括生成、创建、轮换和撤销**。尽管每家公司的应用程序和信息技术环境彼此截然不同，但有一点是一致的：凭证，每个应用程序都需要凭证才能访问其他应用程序、数据库或工具。

**凭据管理对于数据安全至关重要，因为它允许用户和应用程序访问敏感信息**。因此，确保它们在运输途中和静止时的安全非常重要。最好的策略之一是用API调用CredHub等专用凭证管理工具来以编程方式检索凭证，从而取代硬编码凭证及其手动管理。

## 4. CredHub API

### 4.1 验证

**有两种验证CredHub API的方法：[UAA(OAuth2)](https://github.com/cloudfoundry/uaa)和双向[TLS](https://github.com/cloudfoundry-incubator/credhub/blob/master/docs/mutual-tls.md)**。

默认情况下，CredHub提供集成的双向TLS身份验证。要使用此方案，请在应用程序属性中指定CredHub服务器的URL：

```yaml
spring:
    credhub:
        url: <CredHub URL>
```

验证API的另一种方法是通过UAA，它需要客户端凭据授予令牌才能获取访问令牌：

```yaml
spring:
    credhub:
        url: <CredHub URL>
        oauth2:
            registration-id: <credhub-client>
    security:
        oauth2:
            client:
                registration:
                    credhub-client:
                        provider: uaa
                        client-id: <OAuth2 client ID>
                        client-secret: <OAuth2 client secret>
                        authorization-grant-type: <client_credentials>
                provider:
                    uaa:
                        token-uri: <UAA token server endpoint>
```

然后可以将访问令牌传递给授权标头。

### 4.2 Credentials API

**通过CredHubCredentialOperations接口，我们可以调用CredHub API来创建、更新、检索和删除凭据**。CredHub支持的凭证类型有：

-   value：单个配置的字符串
-   json：用于静态配置的JSON对象
-   user：3个字符串–用户名、密码和密码哈希
-   password：用于密码和其他字符串凭据的字符串
-   certificate：包含根CA、证书和私钥的对象
-   rsa：包含公钥和私钥的对象
-   ssh：包含SSH格式公钥和私钥的对象

### 4.2 Permissions API

写入凭证时会提供权限，以控制用户可以访问、更新或检索的内容。**Spring CredHub提供了CredHubPermissionV2Operations接口来创建、更新、检索和删除权限**。允许用户对凭据执行的操作是：读取、写入和删除。

## 5. CredHub整合

我们现在将实现一个返回订单详细信息的Spring Boot应用程序，并将演示一些示例来解释凭证管理的完整生命周期。

### 5.1 CredHubOperations接口

**CredHubOperations接口位于org.springframework.credhub.core包中。它是Spring的CredHub的核心类，支持与CredHub交互的丰富功能集**。它提供对模拟整个CredHub API的接口以及域对象和CredHub数据之间的映射的访问：

```java
public class CredentialService {
    private final CredHubCredentialOperations credentialOperations;
    private final CredHubPermissionV2Operations permissionOperations;

    public CredentialService(CredHubOperations credHubOperations) {
        this.credentialOperations = credHubOperations.credentials();
        this.permissionOperations = credHubOperations.permissionsV2();
    }
}
```

### 5.2 凭据创建

让我们从创建一个新的password类型的凭证开始，它是使用PasswordCredentialRequest构造的：

```java
SimpleCredentialName credentialName = new SimpleCredentialName(credential.getName());
PasswordCredential passwordCredential = new PasswordCredential((String) value.get("password"));
PasswordCredentialRequest request = PasswordCredentialRequest.builder()
    .name(credentialName)
    .value(passwordCredential)
    .build();
credentialOperations.write(request);
```

同样，CredentialRequest的其他实现可用于构建不同的凭证类型，例如用于值凭证的ValueCredentialRequest、用于rsa凭证的RsaCredentialRequest等：

```java
ValueCredential valueCredential = new ValueCredential((String) value.get("value"));
request = ValueCredentialRequest.builder()
    .name(credentialName)
    .value(valueCredential)
    .build();
```

```java
RsaCredential rsaCredential = new RsaCredential((String) value.get("public_key"), (String) value.get("private_key"));
request = RsaCredentialRequest.builder()
    .name(credentialName)
    .value(rsaCredential)
    .build();
```

### 5.3 凭据生成

Spring CredHub还提供了一个动态生成凭据的选项，以避免在应用程序端管理它们所涉及的工作。这 反过来又增强了数据安全性。让我们看看如何实现此功能： 

```java
SimpleCredentialName credentialName = new SimpleCredentialName("api_key");
PasswordParameters parameters = PasswordParameters.builder()
    .length(24)
    .excludeUpper(false)
    .excludeLower(false)
    .includeSpecial(true)
    .excludeNumber(false)
    .build();

CredentialDetails<PasswordCredential> generatedCred = credentialOperations.generate(PasswordParametersRequest.builder()
    .name(credentialName)
    .parameters(parameters)
    .build());

String password = generatedCred.getValue().getPassword();
```

### 5.4 凭证轮换和撤销

凭证管理的另一个重要阶段是轮换凭证。下面的代码演示了我们如何使用密码凭证类型实现这一点：

```java
CredentialDetails<PasswordCredential> newPassword = credentialOperations.regenerate(credentialName, PasswordCredential.class);
```

CredHub还允许证书凭据类型同时具有多个活动版本，以便它们可以在不停机的情况下轮换。凭证管理的最后也是最关键的阶段是撤销凭证，可以按如下方式实现： 

```java
credentialOperations.deleteByName(credentialName);
```

### 5.5 凭证检索

现在我们已经了解了完整的凭证管理生命周期。我们将了解如何检索用于Orders API身份验证的最新版本的密码凭证：

```java
public ResponseEntity<Collection<Order>> getAllOrders() {
    try {
        String apiKey = credentialService.getPassword("api_key");
        return new ResponseEntity<>(getOrderList(apiKey), HttpStatus.OK);
    } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}

public String getPassword(String name) {
    SimpleCredentialName credentialName = new SimpleCredentialName(name);
    return credentialOperations.getByName(credentialName, PasswordCredential.class)
        .getValue()
        .getPassword();
}
```

### 5.6 控制对凭据的访问

**可以将权限附加到凭证以限制访问**。例如，授予单个用户访问凭据并使该用户能够对凭据执行所有操作的权限。可以附加另一个权限，允许所有用户仅对凭证执行READ操作。

让我们看一个向凭证添加权限以允许用户u101执行READ和WRITE操作的示例：

```java
Permission permission = Permission.builder()
    .app(UUID.randomUUID().toString())
    .operations(Operation.READ, Operation.WRITE)
    .user("u101")
    .build();
permissionOperations.addPermissions(name, permission);
```

除了READ和WRITE之外，Spring CredHub还提供对其他几种操作的支持，这允许用户：

-   READ：通过id和name获取凭证
-   WRITE：按名称设置、生成和重新生成凭证
-   DELETE：按名称删除凭证
-   READ_ACL：通过凭据名称获取ACL(访问控制列表)
-   WRITE_ACL：添加和删除凭证ACL的条目

## 6. 总结

在本教程中，我们展示了如何使用Spring CredHub库将CredHub与Spring Boot集成。**我们集中管理订单应用程序的凭据，涵盖两个关键方面：凭据生命周期和权限**。