## 一、概述

如今，我们看到了一系列通过 Web 应用程序公开数据的方法。

通常，应用程序使用[SOAP](https://www.baeldung.com/spring-boot-soap-web-service)或[REST](https://www.baeldung.com/building-a-restful-web-service-with-spring-and-java-based-configuration) Web 服务来公开其 API。但是，还需要考虑像 RSS 和 Atom 这样的流媒体协议。

[在本快速教程中，我们将探讨几种在Groovy](https://www.baeldung.com/groovy-language)中为这些协议中的每一种协议使用 Web 服务的简便方法。

## 2.执行HTTP请求

首先，让我们使用[URL](https://www.baeldung.com/java-url)类执行一个简单的 HTTP GET 请求。我们将在探索期间使用[Postman Echo API。](https://docs.postman-echo.com/?version=latest)

首先，我们将调用URL类的openConnection方法，然后将requestMethod设置为 GET：

```groovy
def postmanGet = new URL('https://postman-echo.com/get')
def getConnection = postmanGet.openConnection()
getConnection.requestMethod = 'GET'
assert getConnection.responseCode == 200
```

同样，我们可以通过将requestMethod设置为 POST 来发出 POST 请求：

```groovy
def postmanPost = new URL('https://postman-echo.com/post')
def postConnection = postmanPost.openConnection()
postConnection.requestMethod = 'POST'
assert postConnection.responseCode == 200
```

[此外，我们可以使用outputStream.withWriter](https://www.baeldung.com/groovy-io#writing)将参数传递给 POST 请求：

```groovy
def form = "param1=This is request parameter."
postConnection.doOutput = true
def text
postConnection.with {
    outputStream.withWriter { outputStreamWriter ->
        outputStreamWriter << form
    }
    text = content.text
}
assert postConnection.responseCode == 200
```

在这里，Groovy 的with closure 看起来非常方便并且使代码更清晰。

让我们使用JsonSlurper将String响应解析为 JSON：

```groovy
JsonSlurper jsonSlurper = new JsonSlurper()
assert jsonSlurper.parseText(text)?.json.param1 == "This is request parameter."
```

## 3. RSS 和 Atom 提要

[RSS](https://en.wikipedia.org/wiki/RSS)和[Atom](https://en.wikipedia.org/wiki/Atom_(Web_standard))提要是在 Web 上公开新闻、博客和技术论坛等内容的常用方法。

此外，这两个提要都是 XML 格式的。因此，我们可以使用 Groovy 的[XMLParser](https://www.baeldung.com/groovy-xml#xml-parser)类来解析内容。

[让我们利用Google 新闻](https://news.google.com/)的 RSS 提要阅读一些头条新闻：

```groovy
def rssFeed = new XmlParser()
    .parse("https://news.google.com/rss?hl=en-US&gl=US&ceid=US:en")
def stories = []
(0..4).each {
    def item = rssFeed.channel.item.get(it)
    stories << item.title.text()
}
assert stories.size() == 5
```

同样，我们可以阅读 Atom 提要。但是，由于两种协议的规范不同，我们将以不同方式访问 Atom 提要中的内容：

```groovy
def atomFeed = new XmlParser()
    .parse("https://news.google.com/atom?hl=en-US&gl=US&ceid=US:en")
def stories = []
(0..4).each {
    def entry = atomFeed.entry.get(it)
    stories << entry.title.text()
}
assert stories.size() == 5
```

此外，我们了解 Groovy 支持所有 Java 库，并鼓励在 Groovy 中使用。因此，我们当然可以使用[Rome API](https://www.baeldung.com/rome-rss)来阅读 RSS 提要。

## 4. SOAP 请求和响应

SOAP 是应用程序用来在 Web 上公开其服务的最流行的 Web 服务协议之一。

我们将使用[groovy-wslite](https://github.com/jwagenleitner/groovy-wslite)库来使用 SOAP API。让我们将其最新的[依赖](https://mvnrepository.com/artifact/com.github.groovy-wslite/groovy-wslite)项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.github.groovy-wslite</groupId>
    <artifactId>groovy-wslite</artifactId>
    <version>1.1.3</version>
</dependency>
```

或者，我们可以使用 Gradle 添加最新的依赖项：

```shell
compile group: 'com.github.groovy-wslite', name: 'groovy-wslite', version: '1.1.3'
```

或者如果我们想写一个 Groovy 脚本。我们可以使用@Grab直接添加它：

```groovy
@Grab(group='com.github.groovy-wslite', module='groovy-wslite', version='1.1.3')
```

groovy-wslite 库提供了SOAPClient类来与 SOAP API 进行通信。同时，它具有创建请求消息的SOAPMessageBuilder类。

让我们使用SOAPClient使用[数字转换 SOAP 服务](http://www.dataaccess.com/webservicesserver/numberconversion.wso)：

```groovy
def url = "http://www.dataaccess.com/webservicesserver/numberconversion.wso"
def soapClient = new SOAPClient(url)
def message = new SOAPMessageBuilder().build({
    body {
        NumberToWords(xmlns: "http://www.dataaccess.com/webservicesserver/") {
            ubiNum(123)
        }
    }
})
def response = soapClient.send(message.toString());
def words = response.NumberToWordsResponse
assert words == "one hundred and twenty three "
```

## 5. REST 请求和响应

REST 是另一种用于创建 Web 服务的流行架构风格。此外，API 是基于 GET、POST、PUT 和 DELETE 等 HTTP 方法公开的。

### 5.1. 得到

我们将使用已经讨论过的 groovy-wslite 库来使用 REST API。它提供RESTClient类以实现无障碍通信。

让我们向已经讨论过的 Postman API 发出 GET 请求：

```groovy
RESTClient client = new RESTClient("https://postman-echo.com")
def path = "/get"
def response
try {
    response = client.get(path: path)
    assert response.statusCode = 200
    assert response.json?.headers?.host == "postman-echo.com"
} catch (RESTClientException e) {
    assert e?.response?.statusCode != 200
}
```

### 5.2. 邮政

现在，让我们向 Postman API 发出 POST 请求。同时，我们将表单参数作为 JSON 传递：

```groovy
client.defaultAcceptHeader = ContentType.JSON
def path = "/post"
def params = ["foo":1,"bar":2]
def response = client.post(path: path) {
    type ContentType.JSON
    json params
}
assert response.json?.data == params

```

在这里，我们将 JSON 设置为默认的接受标头。

## 6. Web服务认证

随着相互通信的 Web 服务和应用程序的数量不断增加，建议使用安全的 Web 服务。

因此，结合使用 HTTPS 和身份验证机制(如 Basic Auth 和 OAuth)非常重要。

因此，应用程序在使用 Web 服务 API 时必须对自身进行身份验证。

### 6.1. 基本授权

我们可以使用已经讨论过的RESTClient 类。让我们使用带有凭据的HTTPBasicAuthorization类来执行基本身份验证：

```groovy
def path = "/basic-auth"
client.authorization = new HTTPBasicAuthorization("postman", "password")
response = client.get(path: path)
assert response.statusCode == 200
assert response.json?.authenticated == true
```

或者，我们可以直接在headers参数中传递凭据(Base64 编码) ：

```groovy
def response = client
.get(path: path, headers: ["Authorization": "Basic cG9zdG1hbjpwYXNzd29yZA=="])
```

### 6.2. OAuth 1.0

同样，我们可以发出 OAuth 1.0 请求，传递消费者密钥和消费者机密等身份验证参数。

但是，由于我们没有像其他机制那样内置支持 OAuth 1.0，因此我们必须自己完成这项工作：

```groovy
def path = "/oauth1"
def params = [oauth_consumer_key: "RKCGzna7bv9YD57c", 
    oauth_signature_method: "HMAC-SHA1", 
    oauth_timestamp:1567089944, oauth_nonce: "URT7v4", oauth_version: 1.0, 
    oauth_signature: 'RGgR/ktDmclkM0ISWaFzebtlO0A=']
def response = new RESTClient("https://postman-echo.com")
    .get(path: path, query: params)
assert response.statusCode == 200
assert response.statusMessage == "OK"
assert response.json.status == "pass"
```

## 七、总结

在本教程中，我们探索了一些在 Groovy 中使用 Web 服务的简便方法。

同时，我们看到了一种阅读 RSS 或 Atom 提要的简单方法。