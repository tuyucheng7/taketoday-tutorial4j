## 1. 概述

在本文中，我们将研究一种不同于标准try-catch块的错误处理功能方法。

我们将使用Vavr库中的Try类，它允许我们通过将错误处理嵌入到正常程序处理流程中来创建更加流畅和有意识的API。

如果你想获得有关Vavr的更多信息，请查看[这篇文章](https://www.baeldung.com/vavr)。

## 2. 异常处理的标准方式

假设我们有一个带有方法call()的简单接口，该方法返回Response或抛出ClientException，这是在失败情况下检查的异常：

```java
public interface HttpClient {
    Response call() throws ClientException;
}
```

Response是一个简单的类，只有一个id字段：

```java
public class Response {
    public final String id;

    public Response(String id) {
        this.id = id;
    }
}
```

假设我们有一个调用HttpClient的服务，那么我们需要在标准的try-catch块中处理已检查的异常：

```java
public Response getResponse() {
    try {
        return httpClient.call();
    } catch (ClientException e) {
        return null;
    }
}
```

当我们想要创建流畅且按功能编写的API时，每个抛出已检查异常的方法都会扰乱程序流程，并且我们的程序代码包含许多try-catch块，因此很难阅读。

理想情况下，我们希望有一个封装结果状态(成功或失败)的特殊类，然后我们可以根据该结果链接操作。

## 3. 用try处理异常

Vavr库为我们提供了一个特殊的容器，它表示可能导致异常或成功完成的计算。

在Try对象中包含操作给了我们一个成功或失败的结果。然后我们可以根据该类型执行进一步的操作。

让我们看看与前面示例中相同的方法getResponse()使用Try时的效果：

```java
public class VavrTry {
    private HttpClient httpClient;

    public Try<Response> getResponse() {
        return Try.of(httpClient::call);
    }

    // standard constructors
}
```

需要注意的重要一点是返回类型Try<Response\>。当一个方法返回这样的结果类型时，我们需要正确处理它并记住，该结果类型可以是Success或Failure，所以我们需要在编译时明确地处理它。

### 3.1 处理成功

让我们编写一个测试用例，在httpClient返回成功结果的情况下使用我们的Vavr类。方法getResponse()返回Try<Resposne\>对象。因此我们可以在其上调用map()方法，仅当Try为Success类型时才会对Response执行操作：

```java
@Test
public void givenHttpClient_whenMakeACall_shouldReturnSuccess() {
    // given
    Integer defaultChainedResult = 1;
    String id = "a";
    HttpClient httpClient = () -> new Response(id);

    // when
    Try<Response> response = new VavrTry(httpClient).getResponse();
    Integer chainedResult = response
      	.map(this::actionThatTakesResponse)
      	.getOrElse(defaultChainedResult);
    Stream<String> stream = response.toStream().map(it -> it.id);

    // then
    assertTrue(!stream.isEmpty());
    assertTrue(response.isSuccess());
    response.onSuccess(r -> assertEquals(id, r.id));
    response.andThen(r -> assertEquals(id, r.id)); 
 
    assertNotEquals(defaultChainedResult, chainedResult);
}
```

函数actionThatTakesResponse()简单地将Response作为参数并返回id字段的hashCode：

```java
public int actionThatTakesResponse(Response response) {
    return response.id.hashCode();
}
```

一旦我们使用actionThatTakesResponse()函数映射我们的值，我们就执行方法getOrElse()。

如果Try中有Success，它返回Try的值，否则，它返回defaultChainedResult。我们的httpClient执行成功，因此isSuccess方法返回true。然后我们可以执行onSuccess()方法，该方法对Response对象执行操作。Try也有一个方法andThen，当该值是Success时，该方法采用Consumer来消费Try的值。

我们可以将Try响应视为流。为此，我们需要使用toStream()方法将其转换为Stream，然后可以使用Stream类中可用的所有操作对该结果进行操作。

如果我们想对Try类型执行操作，我们可以使用transform()方法，该方法将Try作为参数并对其执行操作而无需展开封闭值：

```java
public int actionThatTakesTryResponse(Try<Response> response, int defaultTransformation){
    return response.transform(responses -> response.map(it -> it.id.hashCode())
      	.getOrElse(defaultTransformation));
}
```

### 3.2 处理失败

让我们写一个例子，当我们的HttpClient在执行时会抛出ClientException。

与前面的示例相比，我们的getOrElse方法将返回defaultChainedResult，因为Try将是Failure类型：

```java
@Test
public void givenHttpClientFailure_whenMakeACall_shouldReturnFailure() {
    // given
    Integer defaultChainedResult = 1;
    HttpClient httpClient = () -> {
        throw new ClientException("problem");
    };

    // when
    Try<Response> response = new VavrTry(httpClient).getResponse();
    Integer chainedResult = response
        .map(this::actionThatTakesResponse)
        .getOrElse(defaultChainedResult);
	Option<Response> optionalResponse = response.toOption();

    // then
    assertTrue(optionalResponse.isEmpty());
    assertTrue(response.isFailure());
    response.onFailure(ex -> assertTrue(ex instanceof ClientException));
    assertEquals(defaultChainedResult, chainedResult);
}
```

getResponse()方法返回Failure，因此isFailure方法返回true。

我们可以在返回的响应上执行onFailure()回调，并看到异常是ClientException类型的。Try类型的对象可以使用toOption()方法映射到Option类型。

当我们不想在所有代码库中携带我们的Try结果时，它很有用，但我们有使用Option类型处理显式缺失值的方法。当我们将Failure映射到Option时，方法isEmpty()将返回true。当Try对象是Success类型时调用toOption将使Option被定义，因此方法isDefined()将返回true。

### 3.3 利用模式匹配

当我们的httpClient返回异常时，我们可以对该异常的类型进行模式匹配。然后根据recover()方法中该异常的类型，我们可以决定是否要从该异常中恢复并将Failure变为Success，或者是否要将计算结果保留为Failure：

```java
@Test
public void givenHttpClientThatFailure_whenMakeACall_shouldReturnFailureAndNotRecover() {
    // given
    Response defaultResponse = new Response("b");
    HttpClient httpClient = () -> {
        throw new RuntimeException("critical problem");
    };

    // when
    Try<Response> recovered = new VavrTry(httpClient).getResponse()
      	.recover(r -> Match(r).of(
          	Case(instanceOf(ClientException.class), defaultResponse)
      	));

    // then
    assertTrue(recovered.isFailure());
```

仅当Exception的类型是ClientException时，recover()方法内部的模式匹配才会将Failure变为Success。否则，它会将其保留为Failure()。我们看到我们的httpClient正在抛出RuntimeException，因此我们的恢复方法不会处理这种情况，因此isFailure()返回true。

如果我们想从恢复的对象中获取结果，但在严重失败的情况下重新抛出该异常，我们可以使用getOrElseThrow()方法来完成：

```java
recovered.getOrElseThrow(throwable -> {
    throw new RuntimeException(throwable);
});
```

有些错误很严重，当它们发生时，我们希望通过在调用堆栈中更高的位置抛出异常来明确发出信号，让调用者决定进一步的异常处理。在这种情况下，像上面的例子一样重新抛出异常是非常有用的。

当我们的客户端抛出一个非关键异常时，我们在recover()方法中的模式匹配会将我们的失败变成成功。我们正在从两种类型的异常ClientException和IllegalArgumentException中恢复：

```java
@Test
public void givenHttpClientThatFailure_whenMakeACall_shouldReturnFailureAndRecover() {
    // given
    Response defaultResponse = new Response("b");
    HttpClient httpClient = () -> {
        throw new ClientException("non critical problem");
    };

    // when
    Try<Response> recovered = new VavrTry(httpClient).getResponse()
      	.recover(r -> Match(r).of(
        	Case(instanceOf(ClientException.class), defaultResponse),
        	Case(instanceOf(IllegalArgumentException.class), defaultResponse)
       	));
    
    // then
    assertTrue(recovered.isSuccess());
}
```

我们看到isSuccess()返回true，因此我们的恢复处理代码成功运行。

## 4. 总结

本文展示了Vavr库中Try容器的实际使用。我们通过以更实用的方式处理故障来查看使用该构造的实际示例。使用Try将使我们能够创建更多功能和可读性更强的API。