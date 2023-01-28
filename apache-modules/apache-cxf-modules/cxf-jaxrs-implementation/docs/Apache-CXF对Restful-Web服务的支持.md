## 1. 概述

本教程介绍[Apache CXF](https://cxf.apache.org/)作为符合JAX-RS标准的框架，该标准定义了Java生态系统对REpresentational State Transfer(REST)架构模式的支持。

具体来说，它逐步描述了如何构建和发布RESTful Web服务，以及如何编写单元测试来验证服务。

这是Apache CXF系列中的第三篇；[第一个](https://www.baeldung.com/introduction-to-apache-cxf)侧重于将CXF用作JAX-WS完全兼容的实现。[第二篇文章](https://www.baeldung.com/apache-cxf-with-spring)提供了有关如何将CXF与Spring结合使用的指南。

## 2. Maven依赖

第一个必需的依赖项是org.apache.cxf:cxf-rt-frontend-jaxrs，此工件提供JAX-RS API以及CXF实现：

```xml
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-rt-frontend-jaxrs</artifactId>
    <version>3.1.7</version>
</dependency>
```

在本教程中，我们使用CXF创建一个服务器端点来发布Web服务，而不是使用Servlet容器。因此，需要在Maven POM文件中包含如下依赖：

```xml
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-rt-transports-http-jetty</artifactId>
    <version>3.1.7</version>
</dependency>
```

最后，让我们添加HttpClient库以方便单元测试：

```xml
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.2</version>
</dependency>
```

在[这里](https://search.maven.org/artifact/org.apache.cxf/cxf-rt-frontend-jaxrs)你可以找到最新版本的cxf-rt-frontend-jaxrs依赖项。你可能还想参考[此链接](https://search.maven.org/artifact/org.apache.cxf/cxf-rt-transports-http-jetty)以获取最新版本的org.apache.cxf:cxf-rt-transports-http-jetty工件。最后，最新版本的httpclient可以在[这里](https://search.maven.org/search?q=a:httpclient)找到。

## 3. 资源类和请求映射

让我们开始实现一个简单的例子；我们将使用两个资源Course和Student来设置我们的REST API。

我们将从简单开始，然后逐渐转向更复杂的示例。

### 3.1 资源

下面是Student资源类的定义：

```java
@XmlRootElement(name = "Student")
public class Student {
    private int id;
    private String name;

    // standard getters and setters
    // standard equals and hashCode implementations
}
```

请注意，我们正在使用@XmlRootElement注解来告诉JAXB此类的实例应该被编组为XML。

接下来是Course资源类的定义：

```java
@XmlRootElement(name = "Course")
public class Course {
    private int id;
    private String name;
    private List<Student> students = new ArrayList<>();

    private Student findById(int id) {
        for (Student student : students) {
            if (student.getId() == id) {
                return student;
            }
        }
        return null;
    }
    // standard getters and setters
    // standard equals and hasCode implementations
}
```

最后，让我们实现CourseRepository-它是根资源并作为Web服务资源的入口点：

```java
@Path("course")
@Produces("text/xml")
public class CourseRepository {
    private Map<Integer, Course> courses = new HashMap<>();

    // request handling methods

    private Course findById(int id) {
        for (Map.Entry<Integer, Course> course : courses.entrySet()) {
            if (course.getKey() == id) {
                return course.getValue();
            }
        }
        return null;
    }
}
```

注意带有@Path注解的映射，CourseRepository是这里的根资源，因此它被映射为处理以course开头的所有URL。

@Produces注解的值用于告诉服务器在将此类中的方法返回的对象发送给客户端之前将其转换为XML文档。我们在这里默认使用JAXB，因为没有指定其他绑定机制。

### 3.2 简单的数据设置

因为这是一个简单的示例实现，所以我们使用内存中的数据而不是成熟的持久性解决方案。

考虑到这一点，让我们实现一些简单的设置逻辑来将一些数据填充到系统中：

```java
{
    Student student1 = new Student();
    Student student2 = new Student();
    student1.setId(1);
    student1.setName("Student A");
    student2.setId(2);
    student2.setName("Student B");

    List<Student> course1Students = new ArrayList<>();
    course1Students.add(student1);
    course1Students.add(student2);

    Course course1 = new Course();
    Course course2 = new Course();
    course1.setId(1);
    course1.setName("REST with Spring");
    course1.setStudents(course1Students);
    course2.setId(2);
    course2.setName("LearnSpringSecurity");

    courses.put(1, course1);
    courses.put(2, course2);
}
```

此类中处理HTTP请求的方法将在下一小节中介绍。

### 3.3 API-请求映射方法

现在，让我们来看看实际的REST API的实现。

我们将开始在资源POJO中添加API操作-使用@Path注解。

重要的是要理解这与典型Spring项目中的方法有显着差异-在典型的Spring项目中，API操作将在控制器中定义，而不是在POJO本身上定义。

让我们从Course类中定义的映射方法开始：

```java
@GET
@Path("{studentId}")
public Student getStudent(@PathParam("studentId")int studentId) {
    return findById(studentId);
}
```

简单地说，该方法在处理GET请求时被调用，由@GET注解表示。

注意从HTTP请求映射studentId路径参数的简单语法。

然后我们简单地使用findById辅助方法来返回相应的Student实例。

以下方法通过将接收到的Student对象添加到学生列表来处理由@POST注解指示的POST请求：

```java
@POST
@Path("")
public Response createStudent(Student student) {
    for (Student element : students) {
        if (element.getId() == student.getId() {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }
    students.add(student);
    return Response.ok(student).build();
}
```

如果创建操作成功，则返回200 OK响应，如果具有提交ID的对象已经存在，则返回409 Conflict。

另请注意，我们可以跳过@Path注解，因为它的值为空字符串。

最后一个方法负责处理DELETE请求。它从学生列表中删除一个元素，其id是接收到的路径参数，并返回一个状态为OK(200)的响应。如果没有与指定id关联的元素，这意味着没有要删除的内容，此方法将返回Not Found(404)状态的响应：

```java
@DELETE
@Path("{studentId}")
public Response deleteStudent(@PathParam("studentId") int studentId) {
    Student student = findById(studentId);
    if (student == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    students.remove(student);
    return Response.ok().build();
}
```

让我们继续请求CourseRepository类的映射方法。

以下getCourse方法返回一个Course对象，该对象是课程Map中条目的值，其键是接收到的GET请求的courseId路径参数。在内部，该方法将路径参数分派给findById辅助方法以完成其工作。

```java
@GET
@Path("courses/{courseId}")
public Course getCourse(@PathParam("courseId") int courseId) {
    return findById(courseId);
}
```

以下方法更新课程映射的现有条目，其中接收到的PUT请求的主体是条目值，courseId参数是关联的键：

```java
@PUT
@Path("courses/{courseId}")
public Response updateCourse(@PathParam("courseId") int courseId, Course course) {
    Course existingCourse = findById(courseId);        
    if (existingCourse == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    if (existingCourse.equals(course)) {
        return Response.notModified().build();    
    }
    courses.put(courseId, course);
    return Response.ok().build();
}
```

如果更新成功，此updateCourse方法将返回带有OK(200)状态的响应，如果现有对象和上传的对象具有相同的字段值，则不会更改任何内容并返回未修改(304)响应。如果在课程Map中找不到具有给定id的课程实例，该方法将返回带有未找到(404)状态的响应。

此根资源类的第三个方法不直接处理任何HTTP请求。相反，它将请求委托给Course类，其中请求由匹配方法处理：

```java
@Path("courses/{courseId}/students")
public Course pathToStudent(@PathParam("courseId") int courseId) {
    return findById(courseId);
}
```

我们已经在Course类中展示了处理委托请求的方法。

## 4. 服务器端点

本节重点介绍CXF服务器的搭建，用于发布RESTful web服务，其资源如上节所述。第一步是实例化一个JAXRSServerFactoryBean对象并设置根资源类：

```java
JAXRSServerFactoryBean factoryBean = new JAXRSServerFactoryBean();
factoryBean.setResourceClasses(CourseRepository.class);
```

然后需要在工厂bean上设置资源提供者来管理根资源类的生命周期。我们使用默认的单例资源提供者，它为每个请求返回相同的资源实例：

```java
factoryBean.setResourceProvider(new SingletonResourceProvider(new CourseRepository()));
```

我们还设置了一个地址来表示发布web服务的URL：

```java
factoryBean.setAddress("http://localhost:8080/");
```

现在可以使用factoryBean来创建一个新的服务器，该服务器将开始监听传入的连接：

```java
Server server = factoryBean.create();
```

本节上面的所有代码都应该包含在main方法中：

```java
public class RestfulServer {
    public static void main(String[] args) throws Exception {
        // code snippets shown above
    }
}
```

这个主要方法的调用在第6节中介绍。

## 5. 测试用例

本节描述用于验证我们之前创建的Web服务的测试用例。这些测试在响应四种最常用方法(即GET、POST、PUT和DELETE)的HTTP请求后验证服务的资源状态。

### 5.1 准备

首先，在测试类中声明了两个静态字段，名为RestfulTest：

```java
private static String BASE_URL = "http://localhost:8080/tuyucheng/courses/";
private static CloseableHttpClient client;
```

在运行测试之前，我们创建一个客户端对象，用于与服务器通信并在之后销毁它：

```java
@BeforeAll
static void createClient() {
    client = HttpClients.createDefault();
}
    
@AfterAll
static void closeClient() throws IOException {
    client.close();
}
```

客户端实例现在已准备好供测试用例使用。

### 5.2 GET请求

在测试类中，我们定义了两个方法来向运行Web服务的服务器发送GET请求。

第一种方法是根据资源中的ID获取Course实例：

```java
private Course getCourse(int courseOrder) throws IOException {
    URL url = new URL(BASE_URL + courseOrder);
    InputStream input = url.openStream();
    Course course = JAXB.unmarshal(new InputStreamReader(input), Course.class);
    return course;
}
```

第二种是在给定资源中课程和学生的id的情况下获取Student实例：

```java
private Student getStudent(int courseOrder, int studentOrder) throws IOException {
    URL url = new URL(BASE_URL + courseOrder + "/students/" + studentOrder);
    InputStream input = url.openStream();
    Student student = JAXB.unmarshal(new InputStreamReader(input), Student.class);
    return student;
}
```

这些方法将HTTP GET请求发送到服务资源，然后将XML响应解组到相应类的实例。两者都用于在执行POST、PUT和DELETE请求后验证服务资源状态。

### 5.3 POST请求

本小节包含两个POST请求测试用例，说明当上传的Student实例导致冲突以及成功创建时Web服务的操作。

在第一个测试中，我们使用从conflict_student.xml文件解组的Student对象，该文件位于类路径中，内容如下：

```xml
<Student>
    <id>2</id>
    <name>Student B</name>
</Student>
```

这是将内容转换为POST请求正文的方式：

```java
HttpPost httpPost = new HttpPost(BASE_URL + "1/students");
InputStream resourceStream = this.getClass().getClassLoader()
    .getResourceAsStream("conflict_student.xml");
httpPost.setEntity(new InputStreamEntity(resourceStream));
```

Content-Type标头设置为告诉服务器请求的内容类型是XML：

```java
httpPost.setHeader("Content-Type", "text/xml");
```

由于上传的Student对象已经存在于第一个Course实例中，我们预计创建失败并返回Conflict(409)状态的响应。以下代码片段验证了预期：

```java
HttpResponse response = client.execute(httpPost);
assertEquals(409, response.getStatusLine().getStatusCode());
```

在下一个测试中，我们从名为created_student.xml的文件中提取HTTP请求的主体，该文件也在类路径中。这是文件的内容：

```xml
<Student>
    <id>3</id>
    <name>Student C</name>
</Student>
```

与前面的测试用例类似，我们构建并执行一个请求，然后验证是否成功创建了一个新实例：

```java
HttpPost httpPost = new HttpPost(BASE_URL + "2/students");
InputStream resourceStream = this.getClass().getClassLoader()
    .getResourceAsStream("created_student.xml");
httpPost.setEntity(new InputStreamEntity(resourceStream));
httpPost.setHeader("Content-Type", "text/xml");
        
HttpResponse response = client.execute(httpPost);
assertEquals(200, response.getStatusLine().getStatusCode());
```

我们可以确认Web服务资源的新状态：

```java
Student student = getStudent(2, 3);
assertEquals(3, student.getId());
assertEquals("Student C", student.getName());
```

这是对新Student对象请求的XML响应：

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Student>
    <id>3</id>
    <name>Student C</name>
</Student>
```

### 5.4 Put请求

让我们从一个无效的更新请求开始，其中正在更新的Course对象不存在。以下是用于替换Web服务资源中不存在的Course对象的实例的内容：

```xml
<Course>
    <id>3</id>
    <name>ApacheCXFSupport for RESTful</name>
</Course>
```

该内容存储在类路径上名为non_existent_course.xml的文件中。它被提取出来，然后用于通过以下代码填充PUT请求的主体：

```java
HttpPut httpPut = new HttpPut(BASE_URL + "3");
InputStream resourceStream = this.getClass().getClassLoader()
    .getResourceAsStream("non_existent_course.xml");
httpPut.setEntity(new InputStreamEntity(resourceStream));
```

Content-Type标头设置为告诉服务器请求的内容类型是XML：

```java
httpPut.setHeader("Content-Type", "text/xml");
```

由于我们故意发送无效请求来更新不存在的对象，因此预计会收到Not Found(404)响应。响应已验证：

```java
HttpResponse response = client.execute(httpPut);
assertEquals(404, response.getStatusLine().getStatusCode());
```

在PUT请求的第二个测试用例中，我们提交了一个具有相同字段值的Course对象。由于在这种情况下没有任何更改，我们希望返回状态为未修改(304)的响应。整个过程如图：

```java
HttpPut httpPut = new HttpPut(BASE_URL + "1");
InputStream resourceStream = this.getClass().getClassLoader()
    .getResourceAsStream("unchanged_course.xml");
httpPut.setEntity(new InputStreamEntity(resourceStream));
httpPut.setHeader("Content-Type", "text/xml");
        
HttpResponse response = client.execute(httpPut);
assertEquals(304, response.getStatusLine().getStatusCode());
```

其中unchanged_course.xml是类路径上保存用于更新的信息的文件。这是它的内容：

```xml
<Course>
    <id>1</id>
    <name>REST with Spring</name>
</Course>
```

在PUT请求的最后一个演示中，我们执行了一个有效的更新。以下是changed_course.xml文件的内容，其内容用于更新Web服务资源中的Course实例：

```xml
<Course>
    <id>2</id>
    <name>ApacheCXFSupport for RESTful</name>
</Course>
```

这是构建和执行请求的方式：

```java
HttpPut httpPut = new HttpPut(BASE_URL + "2");
InputStream resourceStream = this.getClass().getClassLoader()
    .getResourceAsStream("changed_course.xml");
httpPut.setEntity(new InputStreamEntity(resourceStream));
httpPut.setHeader("Content-Type", "text/xml");
```

让我们验证对服务器的PUT请求并验证上传是否成功：

```java
HttpResponse response = client.execute(httpPut);
assertEquals(200, response.getStatusLine().getStatusCode());
```

让我们验证Web服务资源的新状态：

```java
Course course = getCourse(2);
assertEquals(2, course.getId());
assertEquals("ApacheCXFSupport for RESTful", course.getName());
```

以下代码片段显示了在发送先前上传的Course对象的GET请求时XML响应的内容：

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Course>
    <id>2</id>
    <name>ApacheCXFSupport for RESTful</name>
</Course>
```

### 5.5 DELETE请求

首先，让我们尝试删除一个不存在的Student实例。该操作应该会失败，并且会出现相应的Not Found(404)状态响应：

```java
HttpDelete httpDelete = new HttpDelete(BASE_URL + "1/students/3");
HttpResponse response = client.execute(httpDelete);
assertEquals(404, response.getStatusLine().getStatusCode());
```

在DELETE请求的第二个测试用例中，我们创建、执行并验证一个请求：

```java
HttpDelete httpDelete = new HttpDelete(BASE_URL + "1/students/1");
HttpResponse response = client.execute(httpDelete);
assertEquals(200, response.getStatusLine().getStatusCode());
```

我们使用以下代码片段验证Web服务资源的新状态：

```java
Course course = getCourse(1);
assertEquals(1, course.getStudents().size());
assertEquals(2, course.getStudents().get(0).getId());
assertEquals("Student B", course.getStudents().get(0).getName());
```

接下来，我们列出在请求Web服务资源中的第一个Course对象后收到的XML响应：

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Course>
    <id>1</id>
    <name>REST with Spring</name>
    <students>
        <id>2</id>
        <name>Student B</name>
    </students>
</Course>
```

很明显，第一个Student已经成功移除。

## 6. 测试执行

第4节描述了如何在RestfulServer类的main方法中创建和销毁Server实例。

使服务器启动并运行的最后一步是调用该main方法。为了实现这一点，在Maven POM文件中包含并配置了Exec Maven插件：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.0.0</version>
    <configuration>
        <mainClass>
            cn.tuyucheng.taketoday.cxf.jaxrs.implementation.RestfulServer
        </mainClass>
    </configuration>
</plugin>
```

可以通过[此链接](https://search.maven.org/search?q=a:exec-maven-plugin)找到此插件的最新版本。

在编译和打包本教程中演示的工件的过程中，Maven Surefire插件会自动执行包含在名称以Test开头或结尾的类中的所有测试。如果是这种情况，插件应该配置为排除这些测试：

```xml
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
    <configuration>
        <excludes>
            <exclude>/ServiceTest</exclude>
        </excludes>
    </configuration>
</plugin>
```

通过上述配置，ServiceTest被排除在外，因为它是测试类的名称。你可以为该类选择任何名称，前提是其中包含的测试在服务器准备好连接之前不会由Maven Surefire插件运行。

最新版本的Maven Surefire插件，请查看[这里](https://search.maven.org/classic/#search|ga|1|a%3A"maven-surefire-plugin")。

现在你可以执行exec:java目标来启动RESTful Web服务服务器，然后使用IDE运行上述测试。同样，你可以通过在终端中执行命令mvn -Dtest=ServiceTest test来启动测试。

## 7. 总结

本教程说明了如何使用Apache CXF作为JAX-RS实现，它演示了如何使用该框架为RESTful Web服务定义资源以及创建用于发布该服务的服务器。