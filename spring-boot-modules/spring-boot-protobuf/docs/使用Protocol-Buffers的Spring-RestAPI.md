## 1. 概述

[Protocol Buffers](https://developers.google.com/protocol-buffers/)是一种用于结构化数据序列化和反序列化的语言和平台中立机制，它的创建者Google宣称它比其他类型的有效负载(例如XML和JSON)更快、更小和更简单。

本教程将指导你设置REST API以利用这种基于二进制的消息结构。

## 2. 协议缓冲区

本节提供有关Protocol Buffers的一些基本信息以及它们如何在Java生态系统中应用。

### 2.1 协议缓冲区简介

为了使用Protocol Buffers，我们需要在.proto文件中定义消息结构。每个文件都是对可能从一个节点传输到另一个节点或存储在数据源中的数据的描述。下面是一个.proto文件的示例，它名为tuyucheng.proto并且位于src/main/resources目录中。本教程后面会用到这个文件：

```protobuf
syntax = "proto3";
package tuyucheng;
option java_package = "cn.tuyucheng.taketoday.protobuf";
option java_outer_classname = "TuyuchengTraining";

message Course {
    int32 id = 1;
    string course_name = 2;
    repeated Student student = 3;
}
message Student {
    int32 id = 1;
    string first_name = 2;
    string last_name = 3;
    string email = 4;
    repeated PhoneNumber phone = 5;
    message PhoneNumber {
        string number = 1;
        PhoneType type = 2;
    }
    enum PhoneType {
        MOBILE = 0;
        LANDLINE = 1;
    }
}
```

在本教程中，**我们使用协议缓冲区编译器和协议缓冲区语言的版本3**，因此.proto文件必须以syntax = "proto3"声明开头。如果正在使用编译器版本2，则将省略此声明。接下来是package声明，这是这个消息结构的命名空间，避免与其他项目命名冲突。

以下两个声明仅用于Java：java_package选项指定我们生成的类所在的包，java_outer_classname选项指示包含此.proto文件中定义的所有类型的类的名称。

下面的2.3小节将描述剩余的元素以及如何将这些元素编译成Java代码。

### 2.2 使用Java的协议缓冲区

在定义了一个消息结构之后，我们需要一个编译器来将这种语言中立的内容转换成Java代码。你可以按照[Protocol Buffers仓库](https://github.com/google/protobuf)中的说明获取合适的编译器版本。或者，你可以通过搜索[com.google.protobuf:protoc](https://central.sonatype.com/artifact/com.google.protobuf/protoc/21.0-rc-1)工件从Maven中央仓库下载预构建的二进制编译器，然后为你的平台选择合适的版本。

接下来，将编译器复制到你项目的src/main目录下，并在命令行中执行以下命令：

```shell
protoc --java_out=java resources/tuyucheng.proto
```

这应该在cn.tuyucheng.taketoday.protobuf包中为TuyuchengTraining类生成一个源文件，如tuyucheng.proto文件的option声明中指定的那样。

除了编译器之外，还需要Protocol Buffers运行时。这可以通过将以下依赖项添加到Maven POM文件来实现：

```xml
<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java</artifactId>
    <version>3.0.0-beta-3</version>
</dependency>
```

我们可以使用另一个版本的运行时，前提是它与编译器的版本相同。对于最新的，请查看[此链接](https://central.sonatype.com/artifact/com.google.protobuf/protobuf-java/4.0.0-rc-2)。

### 2.3 编译消息描述

通过使用编译器，.proto文件中的消息被编译成静态嵌套的Java类。在上面的示例中，Course和Student消息分别转换为Course和Student Java类。同时，消息的字段被编译成那些生成类型中的JavaBeans风格的getter和setter。每个字段声明末尾的标记由等号和数字组成，是用于以二进制形式对相关字段进行编码的唯一标记。

我们将遍历消息的类型化字段，看看这些字段是如何转换为访问器方法的。

让我们从Course消息开始。它有两个简单的字段，包括id和course_name。它们的协议缓冲区类型int32和string被翻译成Java int和String类型。以下是编译后的相关getter(为简洁起见省略了实现)：

```java
public int getId();
public java.lang.String getCourseName();
```

请注意，类型化字段的名称应采用蛇形大小写(各个单词之间用下划线字符分隔)以保持与其他语言的配合。编译器会根据Java约定将这些名称转换为驼峰命名法。

Course消息的最后一个字段student是Student复杂类型，下面会介绍。该字段以repeated关键字为前缀，这意味着它可以重复任意次数。编译器生成一些与student字段关联的方法如下(没有实现)：

```java
public java.util.List<cn.tuyucheng.taketoday.protobuf.TuyuchengTraining.Student> getStudentList();
public int getStudentCount();
public cn.tuyucheng.taketoday.protobuf.TuyuchengTraining.Student getStudent(int index);
```

现在我们将继续讨论Student消息，它用作Course消息的student字段的复杂类型。它的简单字段，包括id、first_name、last_name和email用于创建Java访问器方法：

```java
public int getId();
public java.lang.String getFirstName();
public java.lang.String getLastName();
public java.lang.String.getEmail();
```

最后一个字段phone是PhoneNumber复杂类型。和Course消息的student字段类似，这个字段是重复的，有几个关联的方法：

```java
public java.util.List<cn.tuyucheng.taketoday.protobuf.TuyuchengTraining.Student.PhoneNumber> getPhoneList();
public int getPhoneCount();
public cn.tuyucheng.taketoday.protobuf.TuyuchengTraining.Student.PhoneNumber getPhone(int index);
```

PhoneNumber消息被编译成TuyuchengTraining.Student.PhoneNumber嵌套类，有两个getter对应于消息的字段：

```java
public java.lang.String getNumber();
public cn.tuyucheng.taketoday.protobuf.TuyuchengTraining.Student.PhoneType getType();
```

PhoneNumber消息的type字段的复杂类型PhoneType是一个枚举类型，它将被转换为嵌套在TuyuchengTraining.Student类中的Java枚举类型：

```java
public enum PhoneType implements com.google.protobuf.ProtocolMessageEnum {
    MOBILE(0),
    LANDLINE(1),
    UNRECOGNIZED(-1),
    ;
    // Other declarations
}
```

## 3. Spring REST API中的Protobuf

本节将指导你使用Spring Boot设置REST服务。

### 3.1 Bean声明

让我们从主@SpringBootApplication类的定义开始：

```java
@SpringBootApplication
public class Application {
    @Bean
    ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        return new ProtobufHttpMessageConverter();
    }

    @Bean
    public CourseRepository createTestCourses() {
        Map<Integer, Course> courses = new HashMap<>();
        Course course1 = Course.newBuilder()
              .setId(1)
              .setCourseName("REST with Spring")
              .addAllStudent(createTestStudents())
              .build();
        Course course2 = Course.newBuilder()
              .setId(2)
              .setCourseName("Learn Spring Security")
              .addAllStudent(new ArrayList<Student>())
              .build();
        courses.put(course1.getId(), course1);
        courses.put(course2.getId(), course2);
        return new CourseRepository(courses);
    }

    // Other declarations
}
```

ProtobufHttpMessageConverter bean用于将@RequestMapping注解方法返回的响应转换为协议缓冲区消息。

另一个bean CourseRepository包含我们API的一些测试数据。

这里重要的是我们正在**使用Protocol Buffer特定数据进行操作-而不是使用标准POJO**。

这是CourseRepository的简单实现：

```java
public class CourseRepository {
    Map<Integer, Course> courses;

    public CourseRepository(Map<Integer, Course> courses) {
        this.courses = courses;
    }

    public Course getCourse(int id) {
        return courses.get(id);
    }
}
```

### 3.2 控制器配置

我们可以为测试URL定义@Controller类，如下所示：

```java
@RestController
public class CourseController {
    @Autowired
    CourseRepository courseRepo;

    @RequestMapping("/courses/{id}")
    Course customer(@PathVariable Integer id) {
        return courseRepo.getCourse(id);
    }
}
```

再说一遍-这里重要的是我们从控制器层返回的Course DTO不是标准的POJO。这将触发它在传输回客户端之前转换为协议缓冲区消息。

## 4. REST客户端和测试

我们已经了解了简单的API实现-现在让我们使用两种方法来说明**客户端协议缓冲区消息的反序列化**。

第一个利用RestTemplate API和预配置的ProtobufHttpMessageConverter bean来自动转换消息。

第二种是使用protobuf-java-format手动将协议缓冲区响应转换为JSON文档。

首先，我们需要为集成测试设置上下文，并通过声明一个测试类来指示Spring Boot在Application类中查找配置信息，如下所示：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class ApplicationTest {
    // Other declarations
}
```

本节中的所有代码片段都将放在ApplicationTest类中。

### 4.1 预期响应

访问REST服务的第一步是确定请求URL：

```java
private static final String COURSE1_URL = "http://localhost:8080/courses/1";
```

此COURSE1_URL将用于从我们之前创建的REST服务中获取第一个测试双门课程。将GET请求发送到上述URL后，使用以下断言验证相应的响应：

```java
private void assertResponse(String response) {
    assertThat(response, containsString("id"));
    assertThat(response, containsString("course_name"));
    assertThat(response, containsString("REST with Spring"));
    assertThat(response, containsString("student"));
    assertThat(response, containsString("first_name"));
    assertThat(response, containsString("last_name"));
    assertThat(response, containsString("email"));
    assertThat(response, containsString("john.doe@tuyucheng.com"));
    assertThat(response, containsString("richard.roe@tuyucheng.com"));
    assertThat(response, containsString("jane.doe@tuyucheng.com"));
    assertThat(response, containsString("phone"));
    assertThat(response, containsString("number"));
    assertThat(response, containsString("type"));
}
```

我们将在后续小节中涵盖的两个测试用例中使用此辅助方法。

### 4.2 使用RestTemplate进行测试

以下是我们如何创建客户端，向指定目的地发送GET请求，以协议缓冲区消息的形式接收响应并使用RestTemplate API对其进行验证：

```java
@Autowired
private RestTemplate restTemplate;

@Test
public void whenUsingRestTemplate_thenSucceed() {
    ResponseEntity<Course> course = restTemplate.getForEntity(COURSE1_URL, Course.class);
    assertResponse(course.toString());
}
```

为了使这个测试用例正常工作，我们需要在配置类中注册一个RestTemplate类型的bean：

```java
@Bean
RestTemplate restTemplate(ProtobufHttpMessageConverter hmc) {
    return new RestTemplate(Arrays.asList(hmc));
}
```

还需要ProtobufHttpMessageConverter类型的另一个bean来自动转换接收到的协议缓冲区消息。此bean与3.1小节中定义的相同。由于客户端和服务器在本教程中共享相同的应用程序上下文，因此我们可以在Application类中声明RestTemplate bean并重用ProtobufHttpMessageConverter bean。

### 4.3 使用HttpClient进行测试

使用HttpClient API并手动转换协议缓冲区消息的第一步是将以下两个依赖项添加到Maven POM文件：

```xml
<dependency>
    <groupId>com.googlecode.protobuf-java-format</groupId>
    <artifactId>protobuf-java-format</artifactId>
    <version>1.4</version>
</dependency>
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.2</version>
</dependency>
```

有关这些依赖项的最新版本，请查看Maven中央仓库中的[protobuf-java-format](https://central.sonatype.com/artifact/com.googlecode.protobuf-java-format/protobuf-java-format/1.4)和[httpclient](https://central.sonatype.com/artifact/org.apache.httpcomponents/httpclient/4.5.14)工件。

让我们继续创建一个客户端，执行GET请求并使用给定的URL将关联的响应转换为InputStream实例：

```java
private InputStream executeHttpRequest(String url) throws IOException {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpGet request = new HttpGet(url);
    HttpResponse httpResponse = httpClient.execute(request);
    return httpResponse.getEntity().getContent();
}
```

现在，我们将InputStream对象形式的协议缓冲区消息转换为JSON文档：

```java
private String convertProtobufMessageStreamToJsonString(InputStream protobufStream) throws IOException {
    JsonFormat jsonFormat = new JsonFormat();
    Course course = Course.parseFrom(protobufStream);
    return jsonFormat.printToString(course);
}
```

下面是测试用例如何使用上面声明的私有辅助方法并验证响应：

```java
@Test
public void whenUsingHttpClient_thenSucceed() throws IOException {
    InputStream responseStream = executeHttpRequest(COURSE1_URL);
    String jsonOutput = convertProtobufMessageStreamToJsonString(responseStream);
    assertResponse(jsonOutput);
}
```

### 4.4 JSON格式的响应

为了清楚起见，我们在前面小节中描述的测试中收到的响应的JSON格式包含在此处：

```javascript
id: 1
course_name: "REST with Spring"
student {
    id: 1
    first_name: "John"
    last_name: "Doe"
    email: "john.doe@tuyucheng.com"
    phone {
        number: "123456"
    }
}
student {
    id: 2
    first_name: "Richard"
    last_name: "Roe"
    email: "richard.roe@tuyucheng.com"
    phone {
        number: "234567"
        type: LANDLINE
    }
}
student {
    id: 3
    first_name: "Jane"
    last_name: "Doe"
    email: "jane.doe@tuyucheng.com"
    phone {
        number: "345678"
    }
    phone {
        number: "456789"
        type: LANDLINE
    }
}
```

## 5. 总结

本教程快速介绍了Protocol Buffers，并说明了如何使用Spring的格式设置REST API。然后我们转向客户端支持和序列化-反序列化机制。