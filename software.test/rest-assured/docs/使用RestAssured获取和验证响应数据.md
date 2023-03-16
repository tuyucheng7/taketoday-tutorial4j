## 1. 概述

在本教程中，我们将讨论如何使用Rest-Assured测试REST服务，重点是**获取和验证来自REST API的响应数据**。

## 2. 测试类设置

在之前的教程中，我们从总体上探讨了[REST-Assured](RestAssured指南.md)，并且演示了如何操作[请求头、cookie和参数](https://www.baeldung.com/rest-assured-header-cookie-parameter)。

在此现有设置的基础上，我们添加了一个简单的REST控制器AppController，它在内部调用服务AppService。我们将在测试示例中使用这些类。

要创建我们的测试类，我们需要做更多的设置。由于我们添加了spring-boot-starter-test依赖项，因此可以很容易地利用Spring测试的工具类。

首先，让我们创建AppControllerIntegrationTest类的基本结构：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AppControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private String uri;

    @PostConstruct
    void init() {
        uri = "http://localhost:" + port;
    }

    @MockBean
    AppService appService;

    // test cases
}
```

在这个JUnit测试中，我们使用几个特定于Spring的注解来标注我们的类，这些注解在随机可用端口中本地启动应用程序。在@PostConstruct中，我们捕获了我们将在其上进行REST调用的完整URI。

我们还在AppService字段上使用了@MockBean，因为我们需要mock此类上的方法调用。

## 3. 验证JSON响应

JSON是REST API中用于交换数据的最常用格式。响应可以包含单个JSON对象或JSON对象数组。

### 3.1 单个JSON对象

假设我们需要测试/movie/{id}端点，如果存在给定id的Movie，它会返回一个Movie JSON对象。

我们将使用[Mockito](https://www.baeldung.com/mockito-series)框架mock AppService调用以返回一些模拟数据：

```java
@Test
void givenMovieId_whenMakingGetRequestToMovieEndpoint_thenReturnMovie() {
	Movie testMovie = new Movie(1, "movie1", "summary1");
	when(appService.findMovie(1)).thenReturn(testMovie);
    
	get(uri + "/movie/" + testMovie.getId()).then()
	    .assertThat()
	    .statusCode(HttpStatus.OK.value())
	    .body("id", equalTo(testMovie.getId()))
	    .body("name", equalTo(testMovie.getName()))
	    .body("synopsis", notNullValue());
}
```

上面，我们首先mock了appService.findMovie(1)调用来返回一个对象。然后，我们在Rest-Assured提供的get()方法中构建了我们的REST URL，用于发出GET请求。最后，我们指定了四个断言。

首先，**我们检查了响应状态码，然后检查响应正文**。这里使用[Hamcrest](https://www.baeldung.com/java-junit-hamcrest-guide)断言预期值。

另请注意，如果响应JSON是嵌套的，我们可以使用点运算符(如“key1.key2.key3”)来测试嵌套的键。

### 3.2 验证后提取JSON响应

在某些情况下，我们可能需要在验证后提取响应，以对其执行其他操作。

**我们可以使用extract()方法将JSON响应提取到类中**：

```java
Movie result = get(uri + "/movie/" + testMovie.getId()).then()
    .assertThat()
    .statusCode(HttpStatus.OK.value())
    .extract()
    .as(Movie.class);
assertThat(result).isEqualTo(testMovie);
```

在此示例中，我们指示Rest-Assured提取对Movie对象的JSON响应，然后对提取的对象进行断言。

**我们还可以使用extract().asString() API将整个响应提取到一个字符串中**：

```java
String responseString = get(uri + "/movie/" + testMovie.getId()).then()
    .assertThat()
    .statusCode(HttpStatus.OK.value())
    .extract()
    .asString();
assertThat(responseString).isNotEmpty();
```

最后，**我们也可以从响应JSON中提取特定字段**。

让我们看一下POST API的测试，它需要Movie JSON作为请求正文，如果插入成功，将返回与请求正文相同的内容：

```java
@Test
void givenMovie_whenMakingPostRequestToMovieEndpoint_thenCorrect() {
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
}
```

上面，我们首先创建了我们需要POST的请求对象。然后我们**使用path()方法从返回的JSON响应中提取id字段**。

### 3.3 JSON数组

我们还可以验证响应是否为JSON数组：

```java
@Test
void whenCallingMoviesEndpoint_thenReturnAllMovies() {
	Set<Movie> movieSet = new HashSet<>();
	movieSet.add(new Movie(1, "movie1", "summary1"));
	movieSet.add(new Movie(2, "movie2", "summary2"));
	when(appService.getAll()).thenReturn(movieSet);
    
	get(uri + "/movies").then()
	    .statusCode(HttpStatus.OK.value())
	    .assertThat()
	    .body("size()", is(2));
}
```

我们首先用一些数据mock appService.getAll()并向我们的端点发出请求。**然后我们断言响应状态码和响应数组的大小**。

这也可以通过extract来完成：

```java
Movie[] movies = get(uri + "/movies").then()
    .statusCode(200)
    .extract()
    .as(Movie[].class);
assertThat(movies.length).isEqualTo(2);
```

## 4. 验证Header和Cookie

我们可以使用同名方法验证响应的header或cookie：

```java
@Test
void whenCallingWelcomeEndpoint_thenCorrect() {
	get(uri + "/welcome").then()
	    .assertThat()
	    .header("sessionId", notNullValue())
	    .cookie("token", notNullValue());
}
```

我们还可以单独提取header和cookie：

```java
Response response = get(uri + "/welcome");

String headerName = response.getHeader("sessionId");
String cookieValue = response.getCookie("token");
assertThat(headerName).isNotBlank();
assertThat(cookieValue).isNotBlank();
```

## 5. 验证文件

如果我们的REST API返回一个文件，我们可以使用asByteArray()方法来提取响应：

```java
@Test
void givenId_whenCallingDownloadEndpoint_thenCorrect() throws IOException {
	File file = new ClassPathResource("test.txt").getFile();
	long fileSize = file.length();
	when(appService.getFile(1)).thenReturn(file);
    
	byte[] result = get(uri + "/download/1").asByteArray();
    
	assertThat(result.length).isEqualTo(fileSize);
}
```

在这里，我们首先mock了appService.getFile(1)以返回存在于我们的src/test/resources路径中的文本文件。然后我们调用端点并以byte[]的形式提取响应，最后我们断言该响应具有预期值。

## 6. 总结

在本教程中，我们研究了使用Rest-Assured从我们的REST API捕获和验证响应的不同方法。