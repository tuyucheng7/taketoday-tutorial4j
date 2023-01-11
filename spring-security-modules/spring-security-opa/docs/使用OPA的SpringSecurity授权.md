## 1. 简介

在本教程中，我们将展示如何将 Spring Security 的授权决策外部化到 OPA——[开放策略代理](https://www.openpolicyagent.org/)。

## 2. 序言：外部授权案例

跨应用程序的一个共同要求是能够根据策略做出某些决定。当这个策略足够简单并且不太可能改变时，我们可以直接在代码中实现这个策略，这是最常见的场景。

但是，在其他情况下，我们需要更大的灵活性。访问控制决策是典型的：随着应用程序变得越来越复杂，授予对给定功能的访问权限可能不仅取决于是谁，还取决于请求的其他上下文方面。这些方面可能包括 IP 地址、时间和登录身份验证方法(例如：“记住我”、OTP)等。

此外，将上下文信息与用户身份相结合的规则应该易于更改，最好不会导致应用程序停机。这一要求自然会导致一个专用服务处理策略评估请求的架构。

[![bael 5584 spring opa 第1页](https://www.baeldung.com/wp-content/uploads/2022/05/bael-5584-spring-opa-Page-1.png)](https://www.baeldung.com/wp-content/uploads/2022/05/bael-5584-spring-opa-Page-1.png)

在这里，这种灵活性的权衡是增加的复杂性和调用外部服务所导致的性能损失。另一方面，我们可以在不影响应用的情况下，完全进化甚至替换授权服务。此外，我们可以与多个应用程序共享此服务，从而允许在它们之间使用一致的授权模型。

## 3.什么是OPA？

Open Policy Agent，简称 OPA，是一个用 Go 实现的开源策略评估引擎。它最初由[Styra](https://www.styra.com/)开发，现在是 CNCF 毕业的项目。以下是该工具的一些典型用途列表：

-   Envoy 授权过滤器
-   Kubernetes准入控制器
-   Terraform 计划评估

安装 OPA 非常简单：只需下载我们平台的二进制文件，将其放在操作系统路径中的文件夹中，我们就可以开始了。我们可以使用一个简单的命令来验证它是否正确安装：

```bash
$ opa version
Version: 0.39.0
Build Commit: cc965f6
Build Timestamp: 2022-03-31T12:34:56Z
Build Hostname: 5aba1d393f31
Go Version: go1.18
Platform: windows/amd64
WebAssembly: available
```

OPA 评估用 REGO 编写的策略，[REGO](https://www.openpolicyagent.org/docs/latest/policy-language/)是一种经过优化以在复杂对象结构上运行查询的声明性语言。然后，客户端应用程序根据特定用例使用这些查询的结果。在我们的例子中，对象结构是一个授权请求，我们将使用策略来查询结果以授予对给定功能的访问权限。

重要的是要注意 OPA 的政策是通用的，并且不以任何方式与表达授权决定相关联。事实上，我们可以在传统上由规则引擎(如 Drools 等)主导的其他场景中使用它。

## 4. 编写策略

这是用 REGO 编写的简单授权策略的样子：

```java
package baeldung.auth.account

# Not authorized by default
default authorized = false

authorized = true {
    count(deny) == 0
    count(allow) > 0
}

# Allow access to /public
allow["public"] {
    regex.match("^/public/.",input.uri)
}

# Account API requires authenticated user
deny["account_api_authenticated"] {
    regex.match("^/account/.",input.uri)
    regex.match("ANONYMOUS",input.principal)
}

# Authorize access to account
allow["account_api_authorized"] {
    regex.match("^/account/.+",input.uri)
    parts := split(input.uri,"/")
    account := parts[2]
    role := concat(":",[ "ROLE_account", "read", account] )
    role == input.authorities[i]
}


```

首先要注意的是包装声明。OPA 策略使用包来组织规则，它们在评估传入请求时也起着关键作用，我们将在后面展示。我们可以跨多个目录组织策略文件。

接下来，我们定义实际的策略规则：

-   确保我们始终以授权变量的值结束的默认规则
-   我们可以读作“授权为真，当没有拒绝访问的规则且至少有一条规则允许访问时”的主要聚合器规则
-   允许和拒绝规则，每个都表达一个条件，如果匹配，将分别向允许或拒绝数组添加一个条目

OPA 策略语言的完整描述超出了本文的范围，但规则本身并不难阅读。在查看它们时要记住以下几点：

-   a := b或a=b形式的语句是简单的赋值([虽然它们不一样](https://www.openpolicyagent.org/docs/latest/faq/#which-equality-operator-should-i-use))
-   a = b { ... conditions }或a { ...conditions }形式的语句表示“如果条件为真，则将b分配给a
-   保单文件中的订单外观无关紧要

除此之外，OPA 还附带了一个丰富的内置函数库，该函数库针对查询深度嵌套的数据结构进行了优化，以及更熟悉的功能，如字符串操作、集合等。

## 5. 评估政策

让我们使用上一节中定义的策略来评估授权请求。在我们的例子中，我们将使用一个 JSON 结构构建这个授权请求，其中包含来自传入请求的一些片段：

```json
{
    "input": {
        "principal": "user1",
        "authorities": ["ROLE_account:read:0001"],
        "uri": "/account/0001",
        "headers": {
            "WebTestClient-Request-Id": "1",
            "Accept": "application/json"
        }
    }
}

```

请注意，我们已将请求属性包装在单个 输入对象中。该对象在策略评估期间成为输入变量，我们可以使用类似 JavaScript 的语法访问其属性。

为了测试我们的策略是否按预期工作，让我们以服务器模式在本地运行 OPA 并手动提交一些测试请求：

```bash
$ opa run  -w -s src/test/rego
```

选项-s启用在服务器模式下运行，而-w启用自动规则文件重新加载。src/test/rego是包含我们示例代码中的策略文件的文件夹。运行后，OPA 将在本地端口 8181 上侦听 API 请求。如果需要，我们可以使用 -a选项更改默认端口。

现在，我们可以使用curl或其他一些工具来发送请求：

```bash
$ curl --location --request POST 'http://localhost:8181/v1/data/baeldung/auth/account' 
--header 'Content-Type: application/json' 
--data-raw '{
    "input": {
        "principal": "user1",
        "authorities": [],
        "uri": "/account/0001",
        "headers": {
            "WebTestClient-Request-Id": "1",
            "Accept": "application/json"
        }
    }
}'
```

注意 /v1/data 前缀后面的路径部分：它对应于策略的包名称，点替换为正斜杠。

响应将是一个 JSON 对象，其中包含通过针对输入数据评估策略产生的所有结果：

```json
{
  "result": {
    "allow": [],
    "authorized": false,
    "deny": []
  }
}

```

result属性是一个包含策略引擎生成的结果的对象。我们可以看到，在这种情况下，授权属性是false。我们还可以看到allow和deny是空数组。这意味着没有特定规则与输入匹配。结果，主要授权规则也不匹配。

## 6. Spring授权管理器集成

现在我们已经了解了 OPA 的工作方式，我们可以继续前进并将其集成到 Spring Authorization 框架中。在这里，我们将关注它的响应式 Web 变体，但总体思路也适用于常规的基于 MVC 的应用程序。

首先，我们需要实现使用 OPA 作为其后端的ReactiveAuthorizationManager bean：

```java
@Bean
public ReactiveAuthorizationManager<AuthorizationContext> opaAuthManager(WebClient opaWebClient) {
    
    return (auth, context) -> {
        return opaWebClient.post()
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .body(toAuthorizationPayload(auth,context), Map.class)
          .exchangeToMono(this::toDecision);
    };
}

```

这里，注入的WebClient来自另一个 bean，我们从@ConfigurationPropreties类预初始化它的属性。

处理管道委托toAuthorizationRequest方法从当前的Authentication和AuthorizationContext收集信息，然后构建授权请求有效负载。类似地，toAuthorizationDecision获取授权响应并将其映射到AuthorizationDecision。

现在，我们使用这个 bean 构建一个SecurityWebFilterChain：

```java
@Bean
public SecurityWebFilterChain accountAuthorization(ServerHttpSecurity http, @Qualifier("opaWebClient") WebClient opaWebClient) {
    return http
      .httpBasic()
      .and()
      .authorizeExchange(exchanges -> {
          exchanges
            .pathMatchers("/account/")
            .access(opaAuthManager(opaWebClient));
      })
      .build();
}

```

我们仅将自定义AuthorizationManager应用于/account API。这种方法背后的原因是我们可以轻松地扩展此逻辑以支持多个策略文档，从而使它们更易于维护。例如，我们可以有一个配置，它使用请求 URI 来选择适当的规则包，并使用此信息来构建授权请求。

在我们的例子中，/account API 本身只是一个简单的控制器/服务对，它返回一个填充了假余额的Account对象。

## 7. 测试

最后但同样重要的是，让我们构建一个集成测试来将所有内容放在一起。首先，让我们确保“幸福路径”有效。这意味着给定一个经过身份验证的用户，他们应该能够访问自己的帐户：

```java
@Test
@WithMockUser(username = "user1", roles = { "account:read:0001"} )
void testGivenValidUser_thenSuccess() {
    rest.get()
     .uri("/account/0001")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus()
      .is2xxSuccessful();
}

```

其次，我们还必须验证经过身份验证的用户应该只能访问自己的帐户：

```java
@Test
@WithMockUser(username = "user1", roles = { "account:read:0002"} )
void testGivenValidUser_thenUnauthorized() {
    rest.get()
     .uri("/account/0001")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus()
      .isForbidden();
}

```

最后，我们再测试一下认证用户没有权限的情况：

```java
@Test
@WithMockUser(username = "user1", roles = {} )
void testGivenNoAuthorities_thenForbidden() {
    rest.get()
      .uri("/account/0001")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus()
      .isForbidden();
}

```

我们可以从 IDE 或命令行运行这些测试。请注意，无论哪种情况，我们都必须首先启动指向包含我们的授权策略文件的文件夹的 OPA 服务器。

## 8. 总结

在本文中，我们展示了如何使用 OPA 将基于 Spring Security 的应用程序的授权决策外部化。像往常一样，完整的代码可以[在 GitHub 上找到](https://github.com/eugenp/tutorials/tree/master/spring-security-modules/spring-security-opa)。