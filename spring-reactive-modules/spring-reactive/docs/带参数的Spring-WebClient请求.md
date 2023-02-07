## 1. 概述

许多框架和项目正在引入**响应式编程和异步请求处理**。因此，[Spring 5](https://baeldung.com/spring-5)引入了一个响应式[WebClient](https://www.baeldung.com/spring-5-webclient)实现作为[WebFlux](https://www.baeldung.com/spring-webflux)框架的一部分。

在本教程中，我们将学习如何**通过WebClient以响应方式使用REST API端点**。

## 2. REST API端点

首先，让我们使用以下GET端点定义一个示例REST API：

+ /products：获取所有产品
+ /products/{id}：通过id获取产品
+ /products/{id}/attributes/{attributeId}：通过id获取产品属性
+ /products/?name={name}&deliveryDate={deliveryDate}&color={color}：查找产品
+ /products/?tag[]={tag1}&tag[]={tag2}：通过标签获取产品
+ /products/?category={category1}&category={category2}：通过类别获取产品

在这里，我们定义了几个不同的URI。稍后，我们将了解如何使用WebClient构建和发送每种类型的URI。

请注意，按标签和类别获取产品的URI包含数组作为查询参数；但是，**语法有所不同，因为没有严格定义数组在URI中的表示方式**。这主要取决于服务器端的实现。因此，我们将涵盖这两种情况。

## 3. WebClient构建

首先，我们需要创建一个WebClient实例。在本文中，我们将使用[Mock对象](https://www.baeldung.com/mockito-series)来验证是否请求了有效的URI。

让我们定义客户端和相关的Mock对象：

```java
@WebFluxTest
public class WebClientRequestsWithParametersUnitTest {
    public static final String BASE_URL = "https://example.com";
    private WebClient webClient;
    @Captor
    private ArgumentCaptor<ClientRequest> argumentCaptor;
    @Mock
    private ExchangeFunction exchangeFunction;

    @BeforeEach
    void setUp() {
        ClientResponse mockResponse = mock(ClientResponse.class);
        when(mockResponse.bodyToMono(String.class))
              .thenReturn(Mono.just("test"));
        when(exchangeFunction.exchange(argumentCaptor.capture()))
              .thenReturn(Mono.just(mockResponse));

        webClient = WebClient
              .builder()
              .baseUrl(BASE_URL)
              .exchangeFunction(exchangeFunction)
              .build();
    }
}
```

我们还将传递一个基本URL，该URL将附加到客户端发出的所有请求之前。

最后，为了验证特定的URI是否已传递给底层的ExchangeFunction实例，我们将使用以下工具方法：

```java
private void verifyCalledUrl(String relativeUrl) {
    ClientRequest request = argumentCaptor.capture();
    assertEquals(String.format("%s%s", BASE_URL, relativeUrl), request.url().toString());

    verify(this.exchangeFunction).exchange(request);
    verifyNoMoreInteractions(this.exchangeFunction);
}
```

WebClientBuilder类具有uri()方法，该方法提供UriBuilder实例作为参数。通常，我们通过以下方式进行API调用：

```java
webClient.get()
    .uri(uriBuilder -> uriBuilder
        //... building a URI
        .build())
    .retrieve()
    .bodyToMono(String.class)
    .block();
```

我们将在本指南中广泛使用UriBuilder来构建URI。值得注意的是，我们可以使用其他方法构建URI，然后将生成的URI作为字符串传递。

## 4. URI路径组件

**路径组件由一系列斜杠(/)分隔的路径段组成**。首先，我们将从一个简单的例子开始，其中URI没有任何变量段，/products：

```java
@Test
void whenCallSimpleURI_thenURIMatched() {
    webClient.get()
        .uri("/products")
        .retrieve()
        .bodyToMono(String.class)
        .block();

    verifyCalledUrl("/products");
}
```

对于这种情况，我们可以只传递一个字符串作为参数。

接下来，我们将使用/products/{id}端点并构建相应的URI：

```java
@Test
void whenCallSinglePathSegmentUri_thenURIMatched() {
    webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/products/{id}")
            .build(2))
        .retrieve()
        .bodyToMono(String.class)
        .block();

    verifyCalledUrl("/products/2");
}
```

从上面的代码中，我们可以看到实际的路径参数值被传递给了build()方法。

以类似的方式，我们可以为/products/{id}/attributes/{attributeId}端点创建一个包含多个路径段的URI：

```java
@Test
void whenCallMultiplePathSegmentsUri_thenURIMatched() {
	webClient.get()
		.uri(uriBuilder -> uriBuilder
			.path("/products/{id}/attributes/{attributeId}")
			.build(2, 13))
		.retrieve()
		.bodyToMono(String.class)
		.block();
	verifyCalledUrl("/products/2/attributes/13");
}
```

一个URI可以根据需要具有任意数量的路径段，但最终的URI长度不得超过限制。最后，我们需要记住保持传递给build()方法的实际段值的正确顺序。

## 5. URI查询参数

通常，查询参数是一个简单的键值对，例如title=Tuyucheng，让我们看看如何构建这样的URI。

### 5.1 单值参数

我们将从单值参数开始，并采用/products/?name={name}&deliveryDate={deliveryDate}&color={color}端点。要设置查询参数，我们将调用UriBuilder接口的queryParam()方法：

```java
@Test
void whenCallSingleQueryParams_thenURIMatched() {
    webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/products/")
            .queryParam("name", "AndroidPhone")
            .queryParam("color", "black")
            .queryParam("deliveryDate", "13/04/2019")
            .build())
        .retrieve()
        .bodyToMono(String.class)
        .block();

    verifyCalledUrl("/products/?name=AndroidPhone&color=black&deliveryDate=13/04/2019");
}
```

这里我们添加了三个查询参数并立即分配了实际值。同样，也可以使用占位符而不是精确值：

```java
@Test
void whenCallSingleQueryParamsPlaceholders_thenURIMatched() {
    webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/products/")
            .queryParam("name", "{title}")
            .queryParam("color", "{authorId}")
            .queryParam("deliveryDate", "{date}")
            .build("AndroidPhone", "black", "13/04/2019"))
        .retrieve()
        .bodyToMono(String.class)
        .block();

    verifyCalledUrl("/products/?name=AndroidPhone&color=black&deliveryDate=13%2F04%2F2019");
}
```

当在链中进一步传递构建器对象时，这可能特别有用。

请注意，**上面的两个代码片段之间有一个重要区别**。注意预期的URI，我们可以看到它们的**编码方式不同**。特别是，斜线字符(/)在后面一个示例中被转义。

一般来说，[RFC3986](https://www.ietf.org/rfc/rfc3986.txt)不要求在查询中对斜杠进行编码；但是，某些服务器端应用程序可能需要这种转换。因此，我们将在本指南的后面部分了解如何更改此行为。

### 5.2 数组参数

我们可能需要传递一个值数组，并且在查询字符串中传递数组没有严格的规则。因此，**查询字符串中的数组表示因项目而异，通常取决于底层框架**。我们将在本文中介绍最广泛使用的格式。

让我们从/products/?tag[]={tag1}&tag[]={tag2}端点开始：

```java
@Test
void whenCallArrayQueryParamsBrackets_thenURIMatched() {
    webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/products/")
            .queryParam("tag[]", "Snapdragon", "NFC")
            .build())
        .retrieve()
        .bodyToMono(String.class)
        .block();

    verifyCalledUrl("/products/?tag%5B%5D=Snapdragon&tag%5B%5D=NFC");
}
```

正如我们所见，最终的URI包含多个tag参数，后面是编码的方括号。queryParam()方法接收可变参数作为值，因此无需多次调用该方法。

或者，**我们可以省略方括号，只传递具有相同键但值不同的多个查询参数**，/products/?category={category1}&category={category2}：

```java
@Test
void whenCallArrayQueryParams_thenURIMatched() {
    webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/products/")
            .queryParam("category", "Phones", "Tablets")
            .build())
        .retrieve()
        .bodyToMono(String.class)
        .block();

    verifyCalledUrl("/products/?category=Phones&category=Tablets");
}
```

最后，还有一种更广泛使用的方法来对数组进行编码，即传递逗号分隔值。让我们将前面的示例转换为逗号分隔的值：

```java
@Test
void whenCallArrayQueryParamsComma_thenURIMatched() {
    webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/products/")
            .queryParam("category", String.join(",", "Phones", "Tablets"))
            .build())
        .retrieve()
        .bodyToMono(String.class)
        .block();

    verifyCalledUrl("/products/?category=Phones,Tablets");
}
```

我们只是使用String类的join()方法来创建一个逗号分隔的字符串。我们还可以使用应用程序支持的任何其他分隔符。

## 6. 编码方式

还记得我们之前提到的URL编码吗？

如果默认行为不符合我们的要求，我们可以更改它。我们需要在构建WebClient实例时提供一个UriBuilderFactory实现。 在这种情况下，我们将使用DefaultUriBuilderFactory类。要设置编码，我们将调用setEncodingMode()方法。可以使用以下模式：

+ **TEMPLATE_AND_VALUES**：对URI模板进行预编码，并在展开时对URI变量进行严格编码
+ **VALUES_ONLY**：不对URI模板进行编码，而是将URI变量展开成模板后严格编码
+ **URI_COMPONENTS**：在扩展URI变量后编码URI组件值
+ **NONE**：不应用任何编码

默认值为**TEMPLATE_AND_VALUES**。让我们将模式设置为**URI_COMPONENTS**：

```java
DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT);
webClient = WebClient
    .builder()
    .uriBuilderFactory(factory)
    .baseUrl(BASE_URL)
    .exchangeFunction(exchangeFunction)
    .build();
```

因此，以下断言将成功：

```java
webClient.get()
    .uri(uriBuilder -> uriBuilder
        .path("/products/")
        .queryParam("name", "AndroidPhone")
        .queryParam("color", "black")
        .queryParam("deliveryDate", "13/04/2019")
        .build())
    .retrieve()
    .bodyToMono(String.class)
    .block();
verifyCalledUrl("/products/?name=AndroidPhone&color=black&deliveryDate=13/04/2019");
```

当然，我们可以提供一个完全自定义的UriBuilderFactory实现来手动处理URI创建。

## 7. 总结

在本文中，我们学习了如何使用WebClient和DefaultUriBuilder构建不同类型的URI。

在此过程中，我们介绍了各种类型和格式的查询参数。最后，我们通过更改URL构建器的默认编码模式来结束。