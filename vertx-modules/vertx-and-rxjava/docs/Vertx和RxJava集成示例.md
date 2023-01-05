## 1. 概述

[RxJava](https://github.com/ReactiveX/RxJava)是一个流行的用于创建异步和基于事件的程序的库，它的灵感来自[Reactive Extensions](http://reactivex.io/)计划提出的主要思想。

[Vert.x](http://vertx.io/)是Eclipse旗下的一个项目，它提供了几个从头开始设计的组件，以充分利用响应式范例。

一起使用，它们可以证明是任何需要响应式的Java程序的有效基础。

在本文中，我们将加载一个包含城市名称列表的文件，并为每个城市打印出一天有多长，从日出到日落。

我们将使用从公共[www.metaweather.com](https://www.metaweather.com/api/) REST API发布的数据-计算日光的长度，并使用带有Vert.x的RxJava以纯响应的方式进行计算。

## 2. Maven依赖

让我们从导入vertx-rx-java2开始：

```xml
<dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-rx-java2</artifactId>
    <version>3.5.0-Beta1</version>
</dependency>
```

在撰写本文时，Vert.x和较新的RxJava 2之间的集成仅作为测试版提供，但是对于我们正在构建的程序来说已经足够稳定了。

请注意，io.vertx:vertx-rx-java2依赖于io.reactivex.rxjava2:rxjava，因此无需显式导入任何RxJava相关包。

可以在[Maven Central](https://search.maven.org/search?q=vertx-rx-java2)上找到最新版本的Vert.x与RxJava的集成。

## 3. 设置

正如在每个使用Vert.x的应用程序中一样，我们将开始创建vertx对象，它是所有Vert.x功能的主要入口点：

```java
Vertx vertx = io.vertx.reactivex.core.Vertx.vertx();
```

vertx-rx-java2库提供了两个类：io.vertx.core.Vertx和io.vertx.reactivex.core.Vertx。第一个是唯一基于Vert.x的应用程序的常用入口点，而后者是我们必须用来与RxJava集成的入口点。

我们继续定义稍后将使用的对象：

```java
FileSystem fileSystem = vertx.fileSystem();
HttpClient httpClient = vertx.createHttpClient();
```

Vert.x的FileSystem以响应方式提供对文件系统的访问，而Vert.x的HttpClient对HTTP执行相同的操作。

## 4. 响应链

在响应式上下文中很容易连接几个更简单的响应式运算符以获得有意义的计算。

让我们为我们的例子这样做：

```java
fileSystem
    .rxReadFile("cities.txt").toFlowable()
    .flatMap(buffer -> Flowable.fromArray(buffer.toString().split("\\r?\\n")))
    .flatMap(city -> searchByCityName(httpClient, city))
    .flatMap(HttpClientResponse::toFlowable)
    .map(extractingWoeid())
    .flatMap(cityId -> getDataByPlaceId(httpClient, cityId))
    .flatMap(toBufferFlowable())
    .map(Buffer::toJsonObject)
    .map(toCityAndDayLength())
    .subscribe(System.out::println, Throwable::printStackTrace);
```

现在让我们探讨每个逻辑代码块是如何工作的。

## 5. 城市名称

第一步是读取包含城市名称列表的文件，每行一个名称：

```java
fileSystem
    .rxReadFile("cities.txt").toFlowable()
    .flatMap(buffer -> Flowable.fromArray(buffer.toString().split("\\r?\\n")))
```

rxReadFile()方法响应式地读取一个文件并返回一个RxJava的Single<Buffer\>。所以我们得到了我们正在寻找的集成：来自RxJava的数据结构中Vert.x的异步性。

只有一个文件，因此我们将获得包含文件全部内容的缓冲区的单次发射。我们将该输入转换为RxJava的Flowable并将文件的行平面映射为具有一个Flowable，该Flowable改为为每个城市名称发出一个事件。

## 6. JSON城市描述符

有了城市名称，下一步是使用Metaweather REST API获取该城市的标识符代码。然后，此标识符将用于获取城市的日出和日落时间。让我们继续调用链：

让我们继续调用链：

```java
.flatMap(city -> searchByCityName(httpClient, city))
.flatMap(HttpClientResponse::toFlowable)
```

searchByCityName()方法使用我们在第一步中创建的HttpClient来调用提供城市标识符的REST服务。然后使用第二个flatMap()，我们得到包含响应的缓冲区。

让我们完成编写searchByCityName()主体的这一步：

```java
Flowable<HttpClientResponse> searchByCityName(HttpClient httpClient, String cityName) {
    HttpClientRequest req = httpClient.get(
        new RequestOptions()
          	.setHost("www.metaweather.com")
            .setPort(443)
            .setSsl(true)
            .setURI(format("/api/location/search/?query=%s", cityName)));
    return req
        .toFlowable()
        .doOnSubscribe(subscription -> req.end());
}
```

Vert.x的HttpClient返回一个RxJava的Flowable，它发出响应式HTTP响应。这又会在Buffers中发出响应的主体。

我们为正确的URL创建了一个新的响应式请求，但我们注意到Vert.x需要调用HttpClientRequest.end()方法来发出请求可以发送的信号，并且它还需要至少一个订阅才能发送end()被成功调用。

实现这一点的解决方案是使用RxJava的[doOnSubscribe()](http://reactivex.io/documentation/operators/do.html)在消费者订阅后立即调用end()。

## 7. 城市标识符

我们现在只需要获取返回的JSON对象的woeid属性的值，它通过自定义方法来唯一标识城市：

```java
.map(extractingWoeid())
```

extractingWoeid()方法返回一个函数，该函数从REST服务响应中包含的JSON中提取城市标识符：

```java
private static Function<Buffer, Long> extractingWoeid() {
    return cityBuffer -> cityBuffer
        .toJsonArray()
        .getJsonObject(0)
        .getLong("woeid");
}
```

请注意，我们可以使用Buffer提供的方便的toJson...()方法来快速访问我们需要的属性。

## 8. 城市详情

让我们继续响应链以从REST API检索我们需要的详细信息：

```java
.flatMap(cityId -> getDataByPlaceId(httpClient, cityId))
.flatMap(toBufferFlowable())
```

让我们详细介绍getDataByPlaceId()方法：

```java
static Flowable<HttpClientResponse> getDataByPlaceId(HttpClient httpClient, long placeId) {
    return autoPerformingReq(httpClient, format("/api/location/%s/", placeId));
}
```

在这里，我们使用了与上一步相同的方法。getDataByPlaceId()返回一个Flowable<HttpClientResponse\>。如果HttpClientResponse的长度超过几个字节，则HttpClientResponse将以块的形式发出API响应。

使用toBufferFlowable()方法，我们将响应块缩减为一个，以便访问完整的JSON对象：

```java
static Function<HttpClientResponse, Publisher<? extends Buffer>>
  toBufferFlowable() {
    	return response -> response
      		.toObservable()
      		.reduce(
        		Buffer.buffer(),
        		Buffer::appendBuffer).toFlowable();
}
```

## 9. 日落和日出时间

让我们继续添加到响应链，从JSON对象中检索我们感兴趣的信息：

```java
.map(toCityAndDayLength())
```

让我们编写toCityAndDayLength()方法：

```java
static Function<JsonObject, CityAndDayLength> toCityAndDayLength() {
    return json -> {
        ZonedDateTime sunRise = ZonedDateTime.parse(json.getString("sun_rise"));
        ZonedDateTime sunSet = ZonedDateTime.parse(json.getString("sun_set"));
        String cityName = json.getString("title");
        return new CityAndDayLength(cityName, sunSet.toEpochSecond() - sunRise.toEpochSecond());
    };
}
```

它返回一个函数，该函数映射JSON中包含的信息以创建一个POJO，该POJO仅计算日出和日落之间的时间(以小时为单位)。

## 10. 订阅

响应链完成。我们现在可以使用打印出CityAndDayLength发出的实例的处理程序订阅生成的Flowable，或者在出现错误时打印堆栈跟踪：

```java
.subscribe(
  	System.out::println, 
  	Throwable::printStackTrace)
```

当我们运行应用程序时，我们可以看到这样的结果，具体取决于列表中包含的城市和应用程序运行的日期：

```bash
In Chicago there are 13.3 hours of light.
In Milan there are 13.5 hours of light.
In Cairo there are 12.9 hours of light.
In Moscow there are 14.1 hours of light.
In Santiago there are 11.3 hours of light.
In Auckland there are 11.2 hours of light.
```

城市的显示顺序可能与文件中指定的顺序不同，因为对HTTP API的所有请求都是异步执行的。

## 11. 总结

在本文中，我们看到了将Vert.x响应式模块与RxJava提供的运算符和逻辑结构混合是多么容易。

我们构建的响应链虽然很长，但展示了它如何使复杂的场景变得相当容易编写。