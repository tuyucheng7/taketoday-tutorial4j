## 使用 Rest Assured 读取 JSON 响应正文

让我们继续前面教程中使用的 Weather Web 服务示例。当我们请求特定城市的天气详细信息时，服务器通过将城市的天气详细信息作为响应主体发送来进行响应。Response 接口包含两种获取 Response Body 的方法

-   ***Response.body() ：返回 ResponseBody***
-   ***Response.getBody() ：返回 ResponseBody***

![使用 Rest-Assured 读取 Json 响应正文](https://toolsqa.com/gallery/Rest%20Assured/1.Read%20Json%20Response%20Body%20using%20Rest-Assured.png)

使用这些方法，我们可以获得一个***io.restassured.response.ResponseBody***类型的对象。此类表示接收到的响应的正文。使用此类，您可以获取和验证完整或部分响应正文。在下面的代码中，我们将 使用***Response.getBody()简单地读取完整的******响应正文*** ，并将其打印在控制台窗口上。

```java
@Test
public void WeatherMessageBody()
{
	RestAssured.baseURI = "https://restapi.demoqa.com/utilities/weather/city";
	RequestSpecification httpRequest = RestAssured.given();
	Response response = httpRequest.get("/Hyderabad");

	// Retrieve the body of the Response
	ResponseBody body = response.getBody();

	// By using the ResponseBody.asString() method, we can convert the  body
	// into the string representation.
	System.out.println("Response Body is: " + body.asString());
}
```

***ResponseBody***接口还有一个名为***.asString()***的方法，如上面的代码中所用，它将***ResponseBody***转换为其字符串表示形式。如果您运行此测试，输出将如下所示：

![使用 Rest Assured 读取 Json 响应正文](https://toolsqa.com/gallery/Rest%20Assured/2.Read%20Json%20Response%20Body%20using%20Rest%20Assured.png)

***注意： Response.body()**方法的作用完全相同。所以你甚至可以在上面的代码中使用**.body()***方法。

### ***如何验证响应正文包含一些字符串？***

***ResponseBody***可以返回字符串格式的响应正文。我们可以使用简单的字符串方法来验证响应中某些基本级别的值。例如，我们可以使用***String.contains()***方法来查看响应中是否包含“ ***Hyderabad*** ”。下面的代码显示了如何检查子字符串是否存在。

```java
@Test
public void WeatherMessageBody()
{
	RestAssured.baseURI = "https://restapi.demoqa.com/utilities/weather/city";
	RequestSpecification httpRequest = RestAssured.given();
	Response response = httpRequest.get("/Hyderabad");

	// Retrieve the body of the Response
	ResponseBody body = response.getBody();

	// To check for sub string presence get the Response body as a String.
	// Do a String.contains
	String bodyAsString = body.asString();
	Assert.assertEquals(bodyAsString.contains("Hyderabad") /*Expected value*/, true /*Actual Value*/, "Response body contains Hyderabad");
}
```

***通过忽略字母大小写检查字符串是否存在***

我们也可以使用 String 内部方法忽略大小写。为此，我们将 Response 转换为小写，然后将其与我们的小写字符串值进行比较。下面的代码演示了这一点。

```java
@Test
public void WeatherMessageBody()
{
	RestAssured.baseURI = "https://restapi.demoqa.com/utilities/weather/city";
	RequestSpecification httpRequest = RestAssured.given();
	Response response = httpRequest.get("/Hyderabad");

	// Retrieve the body of the Response
	ResponseBody body = response.getBody();

	// To check for sub string presence get the Response body as a String.
	// Do a String.contains
	String bodyAsString = body.asString();

	// convert the body into lower case and then do a comparison to ignore casing.
	Assert.assertEquals(bodyAsString.toLowerCase().contains("hyderabad") /*Expected value*/, true /*Actual Value*/, "Response body contains Hyderabad");
}
```

上述两种方法都有一个经典问题，如果字符串“ ***Hyderabad*** ”出现在错误的节点中，或者可能是同一字符串的多个实例，该怎么办。***这不是在Response***中测试特定节点的万无一失的方法。还有更好的方法，***响应***接口为您提供了一种基于给定***JsonPath***提取节点的机制。有一个名为***Response.JsonPath()***的方法，它返回一个***io.restassured.path.json.JsonPath*** 对象。该对象可用于进一步查询***Response Json***的特定部分。

*如果您不了解 JsonPath，请阅读这些教程*

-   [***什么是 JsonPath***](https://toolsqa.com/rest-assured/jsonpath-and-query-json-using-jsonpath/)
-   [***JsonPath 表达式***](https://toolsqa.com/rest-assured/expressions-in-jsonpath/)

### ***如何使用 JsonPath 从响应中提取节点文本？***

让我们继续上面的示例并从***Response中检索******City***。为此，我们只需从 Response 接口获取*JsonPath*对象，然后查询特定节点。为了清楚起见，让我们再看一下 Weather API 响应。

```java
{
    "City": "Hyderabad",
    "Temperature": "25.51 Degree celsius",
    "Humidity": "94 Percent",
    "Weather Description": "mist",
    "Wind Speed": "1 Km per hour",
    "Wind Direction degree": " Degree"
}
```

在这个响应中，如果我们想去 City 节点，我们所要做的就是拥有以下***JsonPath： $.City***。在[***JsonPath Evaluator***](https://jsonpath.com/)上试用以验证输出。

现在让我们看一下代码，特别注意代码中的注释。

***注意**：在 Java **JsonPath 中**，您不需要将 $ 作为根节点。你可以完全跳过它。*

```java
@Test
public void VerifyCityInJsonResponse()
{
	RestAssured.baseURI = "https://restapi.demoqa.com/utilities/weather/city";
	RequestSpecification httpRequest = RestAssured.given();
	Response response = httpRequest.get("/Hyderabad");

	// First get the JsonPath object instance from the Response interface
	JsonPath jsonPathEvaluator = response.jsonPath();

	// Then simply query the JsonPath object to get a String value of the node
	// specified by JsonPath: City (Note: You should not put $. in the Java code)
	String city = jsonPathEvaluator.get("City");

	// Let us print the city variable to see what we got
	System.out.println("City received from Response " + city);

	// Validate the response
	Assert.assertEquals(city, "Hyderabad", "Correct city name received in the Response");

}
```

代码的输出传递了断言，它还打印了从响应中检索到的***城市名称。***如下图所示

![城市提取](https://toolsqa.com/gallery/Rest%20Assured/3.CityExtracted.png)

***在类似的行中，您可以使用Rest-Assured的******JsonPath***实现来提取**Json**响应的任何部分。这是编写测试的非常方便、紧凑和简单的方法。

### ***从 Weather API Response 读取所有节点的示例代码***

现在我们知道如何使用 JsonPath 读取节点，下面是一小段代码，它读取所有节点并将它们打印到控制台。

```java
@Test
public void DisplayAllNodesInWeatherAPI()
{
	RestAssured.baseURI = "https://restapi.demoqa.com/utilities/weather/city";
	RequestSpecification httpRequest = RestAssured.given();
	Response response = httpRequest.get("/Hyderabad");

	// First get the JsonPath object instance from the Response interface
	JsonPath jsonPathEvaluator = response.jsonPath();

	// Let us print the city variable to see what we got
	System.out.println("City received from Response " + jsonPathEvaluator.get("City"));

	// Print the temperature node
	System.out.println("Temperature received from Response " + jsonPathEvaluator.get("Temperature"));

	// Print the humidity node
	System.out.println("Humidity received from Response " + jsonPathEvaluator.get("Humidity"));

	// Print weather description
	System.out.println("Weather description received from Response " + jsonPathEvaluator.get("Weather"));

	// Print Wind Speed
	System.out.println("City received from Response " + jsonPathEvaluator.get("WindSpeed"));

	// Print Wind Direction Degree
	System.out.println("City received from Response " + jsonPathEvaluator.get("WindDirectionDegree"));
}
```

在下一章中，我们将研究 XML 响应中 XmlPath 的使用。