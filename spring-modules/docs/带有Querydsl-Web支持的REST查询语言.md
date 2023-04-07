## **一、概述**

在本快速教程中，我们将讨论 Spring Data Querydsl Web 支持。

[对于我们在主要 REST 查询语言系列](https://www.baeldung.com/spring-rest-api-query-search-language-tutorial)中关注的所有其他方法，这绝对是一个有趣的替代方法。

## **2. Maven 配置**

首先，让我们从我们的 maven 配置开始：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.3.0.RELEASE</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.data</groupId>
        <artifactId>spring-data-commons</artifactId>
    </dependency>
    <dependency>
        <groupId>com.mysema.querydsl</groupId>
        <artifactId>querydsl-apt</artifactId>
        <version>${querydsl.version}</version>
    </dependency>
    <dependency>
        <groupId>com.mysema.querydsl</groupId>
        <artifactId>querydsl-jpa</artifactId>
        <version>${querydsl.version}</version>
    </dependency>
...复制
```

请注意，从**1.11开始，Querydsl Web 支持在****spring-data-commons**中可用

## **3.用户存储库**

接下来，让我们看看我们的存储库：

```java
public interface UserRepository extends 
  JpaRepository<User, Long>, QueryDslPredicateExecutor<User>, QuerydslBinderCustomizer<QUser> {
    @Override
    default public void customize(QuerydslBindings bindings, QUser root) {
        bindings.bind(String.class).first(
          (StringPath path, String value) -> path.containsIgnoreCase(value));
        bindings.excluding(root.email);
    }
}复制
```

注意：

-   我们正在覆盖*QuerydslBinderCustomizer* *customize()*以自定义默认绑定
-   我们正在自定义默认的*equals*绑定以忽略所有*String*属性的大小写
-   我们还将用户的电子邮件从*谓词*解析中排除

[在此处](http://docs.spring.io/spring-data/jpa/docs/1.9.0.RELEASE/reference/html/#core.web.type-safe)查看完整文档。

## **4. 用户控制器**

现在，让我们看一下控制器：

```java
@RequestMapping(method = RequestMethod.GET, value = "/users")
@ResponseBody
public Iterable<User> findAllByWebQuerydsl(
  @QuerydslPredicate(root = User.class) Predicate predicate) {
    return userRepository.findAll(predicate);
}复制
```

这是有趣的部分——请注意我们是如何使用*@QuerydslPredicate注释***直接从 HttpRequest 获取 Predicate 的**。

具有此类查询的 URL 如下所示：

```bash
http://localhost:8080/users?firstName=john复制
```

以下是潜在响应的结构：

```bash
[
   {
      "id":1,
      "firstName":"john",
      "lastName":"doe",
      "email":"john@test.com",
      "age":11
   }
]复制
```

## **5.现场测试**

最后，让我们测试一下新的 Querydsl Web 支持：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class UserLiveTest {

    private ObjectMapper mapper = new ObjectMapper();
    private User userJohn = new User("john", "doe", "john@test.com");
    private User userTom = new User("tom", "doe", "tom@test.com");

    private static boolean setupDataCreated = false;

    @Before
    public void setupData() throws JsonProcessingException {
        if (!setupDataCreated) {
            givenAuth().contentType(MediaType.APPLICATION_JSON_VALUE)
                       .body(mapper.writeValueAsString(userJohn))
                       .post("http://localhost:8080/users");
 
            givenAuth().contentType(MediaType.APPLICATION_JSON_VALUE)
                       .body(mapper.writeValueAsString(userTom))
                       .post("http://localhost:8080/users");
            setupDataCreated = true;
        }
    }

    private RequestSpecification givenAuth() {
        return RestAssured.given().auth().preemptive().basic("user1", "user1Pass");
    }
}复制
```

首先，让我们获取系统中的所有用户：

```java
@Test
public void whenGettingListOfUsers_thenCorrect() {
    Response response = givenAuth().get("http://localhost:8080/users");
    User[] result = response.as(User[].class);
    assertEquals(result.length, 2);
}复制
```

接下来，让我们按**名字**查找用户：

```java
@Test
public void givenFirstName_whenGettingListOfUsers_thenCorrect() {
    Response response = givenAuth().get("http://localhost:8080/users?firstName=john");
    User[] result = response.as(User[].class);
    assertEquals(result.length, 1);
    assertEquals(result[0].getEmail(), userJohn.getEmail());
}复制
```

**接下来，以免通过部分姓氏**找到用户：

```java
@Test
public void givenPartialLastName_whenGettingListOfUsers_thenCorrect() {
    Response response = givenAuth().get("http://localhost:8080/users?lastName=do");
    User[] result = response.as(User[].class);
    assertEquals(result.length, 2);
}复制
```

**现在，让我们尝试通过电子邮件**查找用户：

```java
@Test
public void givenEmail_whenGettingListOfUsers_thenIgnored() {
    Response response = givenAuth().get("http://localhost:8080/users?email=john");
    User[] result = response.as(User[].class);
    assertEquals(result.length, 2);
}复制
```

注意：当我们尝试通过电子邮件查找用户时——查询被忽略了，因为我们从*谓词*解析中排除了用户的电子邮件。

## **六，结论**

在本文中，我们快速介绍了 Spring Data Querydsl Web 支持以及一种很酷、简单的方法来直接从 HTTP 请求中获取*谓词并使用它来检索数据。*