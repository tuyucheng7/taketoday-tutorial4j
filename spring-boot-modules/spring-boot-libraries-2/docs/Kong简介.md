## 1. 简介

[Kong](https://getkong.org/)是一个开源的API网关和微服务管理层。

基于Nginx和[lua-nginx-module](https://github.com/openresty/lua-nginx-module)(特别是[OpenResty](https://openresty.org/))，Kong的可插拔架构使其灵活而强大。

## 2. 关键概念

在我们深入研究代码示例之前，让我们看一下Kong中的关键概念：

-   **API对象**：包装完成特定任务或提供某些服务的任何HTTP(s)端点的属性，配置包括HTTP方法、端点URI、指向我们API服务器的上游URL，将用于代理请求、最大停用、速率限制、超时等。
-   **消费者对象**：包装任何使用我们的API端点的人的属性，它将用于跟踪、访问控制等。
-   **上游对象**：描述传入请求将如何被代理或负载平衡，由虚拟主机名表示。
-   **目标对象**：表示实现和提供服务的服务，由主机名(或IP地址)和端口标识。请注意，只能添加或禁用每个上游的目标，目标更改的历史记录由上游维护。
-   **插件对象**：可插拔功能，用于在请求和响应生命周期中丰富我们应用程序的功能。例如，可以通过启用相关插件来添加API身份验证和限速功能，Kong在其[插件库](https://docs.konghq.com/hub/)中提供了非常强大的插件。
-   **Admin API**：用于管理Kong配置、端点、消费者、插件等的RESTful API端点

下图描述了Kong与传统架构的不同之处，这可以帮助我们理解它引入这些概念的原因：

<img src="../assets/img_3.png">

## 3. 设置

官方文档提供了各种环境的[详细说明](https://konghq.com/install/)。

## 4. API管理

在本地设置Kong之后，让我们通过代理我们简单的股票查询端点来体验一下Kong的强大功能：

```java
@RestController
@RequestMapping("/stock")
public class QueryController {

    @GetMapping("/{code}")
    public String getStockPrice(@PathVariable String code){
        return "BTC".equalsIgnoreCase(code) ? "10000" : "0";
    }
}
```

### 4.1 添加API

接下来，让我们将查询API添加到Kong中。

管理API可通过http://localhost:8001访问，因此我们所有的API管理操作都将使用此基本URI完成：

```java
APIObject stockAPI = new APIObject("stock-api", "stock.api", "http://localhost:8080", "/");
HttpEntity<APIObject> apiEntity = new HttpEntity<>(stockAPI);
ResponseEntity<String> addAPIResp = restTemplate.postForEntity("http://localhost:8001/apis", apiEntity, String.class);

assertEquals(HttpStatus.CREATED, addAPIResp.getStatusCode());
```

在这里，我们添加了一个具有以下配置的API：

```json
{
    "name": "stock-api",
    "hosts": "stock.api",
    "upstream_url": "http://localhost:8080",
    "uris": "/"
}
```

-   “name”是API的标识符，在操纵其行为时使用
-   “hosts”将用于通过匹配“Host”标头将传入请求路由到给定的“upstream_url”
-   相对路径将与配置的“uris”相匹配

如果我们想弃用一个API或者配置错误，我们可以简单地删除它：

```java
restTemplate.delete("http://localhost:8001/apis/stock-api");
```

添加API后，可以通过http://localhost:8000使用它们：

```java
String apiListResp = restTemplate.getForObject("http://localhost:8001/apis/", String.class);
 
assertTrue(apiListResp.contains("stock-api"));

HttpHeaders headers = new HttpHeaders();
headers.set("Host", "stock.api");
RequestEntity<String> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, new URI("http://localhost:8000/stock/btc"));
ResponseEntity<String> stockPriceResp = restTemplate.exchange(requestEntity, String.class);

assertEquals("10000", stockPriceResp.getBody());
```

在上面的代码示例中，我们尝试通过刚刚添加到Kong的API查询股票价格。

通过请求http://localhost:8000/stock/btc，我们获得了与直接从http://localhost:8080/stock/btc查询相同的服务。

### 4.2 添加API消费者

现在让我们谈谈安全性-更具体地说，是对访问我们API的用户的身份验证。

让我们向我们的股票查询API添加一个消费者，以便我们稍后可以启用身份验证功能。

为API添加消费者与添加API一样简单，消费者的名称(或ID)是所有消费者属性的唯一必填字段：

```java
ConsumerObject consumer = new ConsumerObject("tuyucheng");
HttpEntity<ConsumerObject> addConsumerEntity = new HttpEntity<>(consumer);
ResponseEntity<String> addConsumerResp = restTemplate.postForEntity("http://localhost:8001/consumers/", addConsumerEntity, String.class);
 
assertEquals(HttpStatus.CREATED, addConsumerResp.getStatusCode());
```

在这里，我们添加了“tuyucheng”作为新消费者：

```json
{
    "username": "tuyucheng"
}
```

### 4.3 启用身份验证

这是Kong最强大的功能，插件。

现在我们要将auth插件应用于我们的代理股票查询API：

```java
PluginObject authPlugin = new PluginObject("key-auth");
ResponseEntity<String> enableAuthResp = restTemplate.postForEntity("http://localhost:8001/apis/stock-api/plugins", new HttpEntity<>(authPlugin), String.class);
assertEquals(HttpStatus.CREATED, enableAuthResp.getStatusCode());
```

如果我们尝试通过代理URI查询股票价格，请求将被拒绝：

```java
HttpHeaders headers = new HttpHeaders();
headers.set("Host", "stock.api");
RequestEntity<String> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, new URI("http://localhost:8000/stock/btc"));
ResponseEntity<String> stockPriceResp = restTemplate
      .exchange(requestEntity, String.class);
 
assertEquals(HttpStatus.UNAUTHORIZED, stockPriceResp.getStatusCode());
```

请记住，tuyucheng是我们的API消费者之一，因此我们应该通过添加身份验证密钥来允许他使用此API：

```java
String consumerKey = "tuyucheng.pass";
KeyAuthObject keyAuth = new KeyAuthObject(consumerKey);
ResponseEntity<String> keyAuthResp = restTemplate.postForEntity("http://localhost:8001/consumers/eugenp/key-auth", new HttpEntity<>(keyAuth), String.class); 
assertTrue(HttpStatus.CREATED == keyAuthResp.getStatusCode());
```

然后tuyucheng可以像以前一样使用这个API：

```java
HttpHeaders headers = new HttpHeaders();
headers.set("Host", "stock.api");
headers.set("apikey", consumerKey);
RequestEntity<String> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, new URI("http://localhost:8000/stock/btc"));
ResponseEntity<String> stockPriceResp = restTemplate
      .exchange(requestEntity, String.class);
 
assertEquals("10000", stockPriceResp.getBody());
```

## 5. 高级功能

除了基本的API代理和管理之外，Kong还支持API负载均衡、集群、健康检查和监控等。

在本节中，我们将了解如何使用Kong对请求进行负载均衡，以及如何保护管理API。

### 5.1 负载均衡

Kong为后端服务提供了两种负载均衡请求策略：动态环形均衡器和直接的基于DNS的方法。为了简单起见，**我们将使用ring-balancer**。

正如我们之前提到的，上游用于负载均衡，每个上游可以有多个目标。

Kong支持加权循环法和基于散列的平衡算法。默认情况下，使用加权循环方案-其中请求根据其权重传递到每个目标。

首先，让我们准备上游：

```java
UpstreamObject upstream = new UpstreamObject("stock.api.service");
ResponseEntity<String> addUpstreamResp = restTemplate.postForEntity("http://localhost:8001/upstreams", new HttpEntity<>(upstream), String.class);
 
assertEquals(HttpStatus.CREATED, addUpstreamResp.getStatusCode());
```

然后，为上游添加两个目标，一个weight=10的测试版本和一个weight=40的发布版本：

```java
TargetObject testTarget = new TargetObject("localhost:8080", 10);
ResponseEntity<String> addTargetResp = restTemplate.postForEntity("http://localhost:8001/upstreams/stock.api.service/targets", new HttpEntity<>(testTarget), String.class);
 
assertEquals(HttpStatus.CREATED, ddTargetResp.getStatusCode());

TargetObject releaseTarget = new TargetObject("localhost:9090",40);
addTargetResp = restTemplate.postForEntity("http://localhost:8001/upstreams/stock.api.service/targets", new HttpEntity<>(releaseTarget), String.class);
 
assertEquals(HttpStatus.CREATED, addTargetResp.getStatusCode());
```

通过上面的配置，我们可以假设1/5的请求将进入测试版本，4/5将进入发布版本：

```java
APIObject stockAPI = new APIObject(
      "balanced-stock-api", 
      "balanced.stock.api", 
      "http://stock.api.service", 
      "/");
HttpEntity<APIObject> apiEntity = new HttpEntity<>(stockAPI);
ResponseEntity<String> addAPIResp = restTemplate.postForEntity("http://localhost:8001/apis", apiEntity, String.class);
 
assertEquals(HttpStatus.CREATED, addAPIResp.getStatusCode());

HttpHeaders headers = new HttpHeaders();
headers.set("Host", "balanced.stock.api");
for(int i = 0; i < 1000; i++) {
    RequestEntity<String> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, new URI("http://localhost:8000/stock/btc"));
    ResponseEntity<String> stockPriceResp = restTemplate.exchange(requestEntity, String.class);
 
    assertEquals("10000", stockPriceResp.getBody());
}
 
int releaseCount = restTemplate.getForObject("http://localhost:9090/stock/reqcount", Integer.class);
int testCount = restTemplate.getForObject("http://localhost:8080/stock/reqcount", Integer.class);

assertTrue(Math.round(releaseCount * 1.0 / testCount) == 4);
```

请注意，weighted-round-robin(加权轮询)方案将对后端服务的请求大致平衡到权重比率，因此只能验证该比率的近似值，反映在上述代码的最后一行中。

### 5.2 保护管理API

默认情况下，Kong只接受来自本地界面的管理请求，这在大多数情况下是一个足够好的限制。但是如果我们想通过其他网络接口来管理它，我们可以更改kong.conf中的admin_listen值，并配置防火墙规则。

或者，我们可以让Kong充当Admin API本身的代理，假设我们要管理路径为“/admin-api”的API，我们可以添加这样的API：

```java
APIObject stockAPI = new APIObject(
      "admin-api", 
      "admin.api", 
      "http://localhost:8001", 
      "/admin-api");
HttpEntity<APIObject> apiEntity = new HttpEntity<>(stockAPI);
ResponseEntity<String> addAPIResp = restTemplate.postForEntity(
      "http://localhost:8001/apis",
      apiEntity,
      String.class);
 
assertEquals(HttpStatus.CREATED, addAPIResp.getStatusCode());
```

现在我们可以使用代理管理API来管理API：

```java
HttpHeaders headers = new HttpHeaders();
headers.set("Host", "admin.api");
APIObject baeldungAPI = new APIObject(
      "tuyucheng-api", 
      "tuyucheng.com", 
      "http://ww.tuyucheng.com", 
      "/");
RequestEntity<APIObject> requestEntity = new RequestEntity<>(
      baeldungAPI, 
      headers, 
      HttpMethod.POST, 
      new URI("http://localhost:8000/admin-api/apis"));
ResponseEntity<String> addAPIResp = restTemplate
      .exchange(requestEntity, String.class);

assertEquals(HttpStatus.CREATED, addAPIResp.getStatusCode());
```

当然，我们希望代理API是安全的，这可以通过为代理管理API启用身份验证插件轻松实现。

## 6. 总结

在本文中，我们介绍了Kong，一个用于微服务API网关的平台，并重点介绍了它的核心功能-管理API和将请求路由到上游服务器，以及一些更高级的功能，例如负载平衡。

然而，还有更多可靠的功能等待我们探索，如果需要，我们可以开发自己的插件-你可以继续探索这里的[官方文档](https://getkong.org/docs/)。