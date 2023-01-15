## 一、概述

在本教程中，我们将讨论如何使用 REST-assured 测试 REST 服务，重点是**捕获和验证来自 REST API 的响应数据**。

## 2. 测试类的设置

在之前的教程中，我们[一般探索了 REST-assured](https://www.baeldung.com/rest-assured-tutorial)，并且我们展示了如何操作请求[标头、cookie 和参数](https://www.baeldung.com/rest-assured-header-cookie-parameter)。

在此现有设置的基础上，我们添加了一个简单的 REST 控制器*AppController*，它在内部调用服务*AppService*。我们将在测试示例中使用这些类。

要创建我们的测试类，我们需要做更多的设置。由于我们的类路径中有*spring-boot-starter-test*，我们可以轻松地利用 Spring 测试实用程序。

首先，让我们创建*AppControllerIntegrationTest*类的框架：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AppControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private String uri;

    @PostConstruct
    public void init() {
        uri = "http://localhost:" + port;
    }

    @MockBean
    AppService appService;

     //test cases
}复制
```

在这个 JUnit 测试中，我们使用一些 Spring 特定的注释来注释我们的类，这些注释在随机可用端口中本地启动应用程序。在*@PostConstruct*中，我们捕获了我们将在其上进行 REST 调用的完整 URI。

我们还在*AppService*上 使用*了@MockBean*，因为我们需要模拟这个类的方法调用。

## 3. 验证 JSON 响应

JSON 是 REST API 中用于交换数据的最常用格式。响应可以由单个 JSON 对象或 JSON 对象数组组成。我们将在本节中查看两者。

### 3.1。单个 JSON 对象

假设我们需要测试*/movie/{id}*端点，如果找到*id ，它将返回一个**Movie* JSON 对象。

我们将使用[Mockito](https://www.baeldung.com/mockito-series)框架模拟*AppService*调用以返回一些模拟数据：

```java
@Test
public void givenMovieId_whenMakingGetRequestToMovieEndpoint_thenReturnMovie() {

    Movie testMovie = new Movie(1, "movie1", "summary1");
    when(appService.findMovie(1)).thenReturn(testMovie);

    get(uri + "/movie/" + testMovie.getId()).then()
      .assertThat()
      .statusCode(HttpStatus.OK.value())
      .body("id", equalTo(testMovie.getId()))
      .body("name", equalTo(testMovie.getName()))
      .body("synopsis", notNullValue());
}复制
```

上面，我们首先模拟了*appService.findMovie(1)*调用以返回一个对象。*然后，我们在 REST-assured 提供的get()*方法中构造了我们的 REST URL，用于发出 GET 请求。最后，我们做了四个断言。

首先，**我们检查了响应状态码，然后是*正文*元素**。我们使用[Hamcrest](https://www.baeldung.com/java-junit-hamcrest-guide)来断言期望值。

另请注意，如果响应 JSON 是嵌套的，我们可以使用*点*运算符（如*“key1.key2.key3”）*来测试嵌套键。

### 3.2. 验证后提取 JSON 响应

在某些情况下，我们可能需要在验证后提取响应，以对其执行额外的操作。

***我们可以使用extract()*方法将 JSON 响应提取到一个类：**

```java
Movie result = get(uri + "/movie/" + testMovie.getId()).then()
  .assertThat()
  .statusCode(HttpStatus.OK.value())
  .extract()
  .as(Movie.class);
assertThat(result).isEqualTo(testMovie);复制
```

在此示例中，我们指示 REST-assured 将 JSON 响应提取到*Movie*对象，然后对提取的对象进行断言。

**我们还可以使用*extract().asString()* API将整个响应提取到*字符串：***

```java
String responseString = get(uri + "/movie/" + testMovie.getId()).then()
  .assertThat()
  .statusCode(HttpStatus.OK.value())
  .extract()
  .asString();
assertThat(responseString).isNotEmpty();复制
```

最后，**我们也可以从响应 JSON 中提取特定字段**。

让我们看一个 POST API 的测试，该 API 需要一个*Movie* JSON 主体，如果插入成功，将返回相同的内容：

```java
@Test
public void givenMovie_whenMakingPostRequestToMovieEndpoint_thenCorrect() {
    Map<String, String> request = new HashMap<>();
    request.put("id", "11");
    request.put("name", "movie1");
    request.put("synopsis", "summary1");

    int movieId = given().contentType("application/json")
      .body(request)
      .when()
      .post(uri + "/movie")
      .then()
      .assertThat()
      .statusCode(HttpStatus.CREATED.value())
      .extract()
      .path("id");
    assertThat(movieId).isEqualTo(11);
}复制
```

上面，我们首先制作了我们需要 POST 的请求对象。***然后，我们使用path()*****方法****从返回的 JSON 响应****中提取*id字段。***

### 3.3. JSON 数组

我们还可以验证响应是否为 JSON 数组：

```java
@Test
public void whenCallingMoviesEndpoint_thenReturnAllMovies() {

Set<Movie> movieSet = new HashSet<>();
movieSet.add(new Movie(1, "movie1", "summary1"));
movieSet.add(new Movie(2, "movie2", "summary2"));
when(appService.getAll()).thenReturn(movieSet);

get(uri + "/movies").then()
    .statusCode(HttpStatus.OK.value())
    .assertThat()
    .body("size()", is(2));
}复制
```

我们再次首先 用一些数据*模拟 appService.getAll()*并向我们的端点发出请求。**然后我们断言响应数组的*statusCode*和*大小*。**

这又可以通过提取来完成：

```java
Movie[] movies = get(uri + "/movies").then()
  .statusCode(200)
  .extract()
  .as(Movie[].class);
assertThat(movies.length).isEqualTo(2);复制
```

## 4. 验证标头和 Cookie

我们可以使用具有相同名称的方法来验证响应的标头或 cookie：

```java
@Test
public void whenCallingWelcomeEndpoint_thenCorrect() {
    get(uri + "/welcome").then()
        .assertThat()
        .header("sessionId", notNullValue())
        .cookie("token", notNullValue());
}复制
```

我们还可以单独提取标头和 cookie：

```java
Response response = get(uri + "/welcome");

String headerName = response.getHeader("sessionId");
String cookieValue = response.getCookie("token");
assertThat(headerName).isNotBlank();
assertThat(cookieValue).isNotBlank();复制
```

## 5. 验证文件

如果我们的 REST API 返回一个文件，我们可以使用*asByteArray()*方法来提取响应：

```java
File file = new ClassPathResource("test.txt").getFile();
long fileSize = file.length();
when(appService.getFile(1)).thenReturn(file);

byte[] result = get(uri + "/download/1").asByteArray();

assertThat(result.length).isEqualTo(fileSize);复制
```

在这里，我们首先 *模拟 appService.getFile(1)*以返回一个文本文件，该文件存在于我们的*src/test/resources*路径中。然后我们调用我们的端点并在*byte[]*中提取响应，然后我们断言它具有预期值。

## 六，结论

在本教程中，我们研究了使用 REST-assured 来捕获和验证来自 REST API 的响应的不同方法。