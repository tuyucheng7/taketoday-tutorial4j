## 1. 概述

Java 和[Kotlin](https://www.baeldung.com/kotlin)都是[为 JVM](https://www.baeldung.com/jvm-languages)设计的语言。但是，如果我们尝试在 Jakarta EE 容器中使用 Kotlin，我们会立即遇到一些惯用的挑战。

在本教程中，我们将了解这些挑战以及如何有效地应对它们。

## 2. 挑战

Java 和 Kotlin 是有些不同的语言。语法不同，这是显而易见的，但这不是真正的问题。它的语言设计和范式不同，这会在 Jakarta EE 容器中的使用中产生一些问题。要使用 Kotlin 构建企业应用程序，我们需要满足这些差异。

例如，Kotlin 类默认是最终的。为了使它们可扩展，我们需要明确地打开它们。我们还需要提供无参数构造函数，以便与[JPA](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)或[Jackson](https://www.baeldung.com/jackson)等框架一起使用。在 Java 中，它们默认可用，但在 Kotlin 中，我们需要做一些额外的工作。由于初始化，容器大量使用的注入也会有点棘手，与[Arquillian](https://www.baeldung.com/arquillian)的集成测试也是如此。

让我们来应对这些挑战。我们的示例将演示如何在 Kotlin 中构建一个简单的 CRUD 应用程序并在 Jakarta EE 容器中运行它。我们将从一个简单的数据类开始，然后我们将着手编写服务和集成测试。

## 3.依赖关系

所以，首先，让我们添加[javaee-api](https://search.maven.org/search?q=g:javax AND a:javaee-api)依赖项：

```xml
<dependency>
    <groupId>javax</groupId>
    <artifactId>javaee-api</artifactId>
    <version>8.0.1</version>
    <scope>provided</scope>
</dependency>
```

请注意，我们按照提供的方式设置此依赖项，因为 我们只需要它进行编译。

## 4.JPA实体

现在，让我们编写一个简单的Student数据类，我们将同时将其用作[实体](https://www.baeldung.com/jpa-entities)和[DTO](https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application)。

我们需要学生的 ID、名字和姓氏：

```java
@Entity
data class Student constructor (

    @SequenceGenerator(name = "student_id_seq", sequenceName = "student_id_seq",
      allocationSize = 1)
    @GeneratedValue(generator = "student_id_seq", strategy = GenerationType.SEQUENCE)
    @Id
    var id: Long?,

    var firstName: String,
    var lastName: String

) {
    constructor() : this(null, "", "")
    constructor(firstName: String, lastName: String) : this(null, firstName, lastName)
}

```

我们将Student定义为数据类，这是 Kotlin 中用于保存数据的一种特殊类。 在这些类中，编译器自动从主构造函数中声明的所有属性派生常用函数，例如equals()、hashCode()和toString() 。

自动生成函数使得数据类非常方便易用。但是，数据类也必须满足一些规则。例如，它们必须有一个主构造函数，其中至少有一个参数标记为val或var。

在我们的数据类中，我们在主构造函数中定义了所有成员。

我们还定义了两个辅助构造函数：

-   首先，我们有一个无参数的构造函数，容器和通用框架(如 JPA 或 Jackson)需要它来实例化类并使用 setter 填充数据。
-   其次，我们有一个方便的构造函数，当我们想要实例化一个没有 id 的新对象时使用它，当我们想要将新实体保存到数据库时通常使用它。

此外，我们使用标准 JPA 注解来定义 JPA 实体，就像我们在标准 Jakarta EE 中所做的那样。

## 三、商务服务

我们需要一个业务服务来处理[EntityManager](https://www.baeldung.com/hibernate-entitymanager)的 CRUD 操作。它很简单，与 Java 实现非常相似，但有一些显着差异：

```java
@Stateless
open class StudentService {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    open fun create(student: Student) = entityManager.persist(student)

    open fun read(id: Long): Student? = entityManager.find(Student::class.java, id)

    open fun update(student: Student) = entityManager.merge(student)

    open fun delete(id: Long) = entityManager.remove(read(id))
}

```

由于 Kotlin 中的所有类都是最终类，我们需要显式打开我们的类以启用扩展。Jakarta EE 容器需要扩展，因为它将从我们的服务类创建一个代理并将其注入到需要的地方。公共方法也必须打开，否则无法创建代理。因此，我们 在类及其所有公共方法上使用open关键字。

EntityManager的使用方式与 Java 中的 @PersistenceContext注解类似。我们将其定义为带有附加[lateinit关键字](https://www.baeldung.com/kotlin-lazy-initialization)的私有成员。这个关键字告诉编译器这个变量一开始是空的，但它会在第一次使用之前被初始化。这消除了不必要的空检查并与容器注入完美对齐。每次我们使用 @Inject注解时都会用到它。

## 4.休息服务

最后，我们需要为我们的应用程序定义一个 REST 服务端点。为此，我们需要注册我们的资源类：

```java
@ApplicationPath("/")
class ApplicationConfig : Application() {
    override fun getClasses() = setOf(StudentResource::class.java)
}

```

现在，我们定义我们的资源类。它将非常接近我们在 Java 中所做的，并进行了一些 Kotlin 特定的修改：

```java
@Path("/student")
open class StudentResource {

    @Inject
    private lateinit var service: StudentService

    @POST
    open fun create(student: Student): Response {
        service.create(student)
        return Response.ok().build()
    }

    @GET
    @Path("/{id}")
    open fun read(@PathParam("id") id: Long): Response {
        val student  = service.read(id)
        return Response.ok(student, MediaType.APPLICATION_JSON_TYPE).build()
    }

    @PUT
    @Path("/{id}")
    open fun update(@PathParam("id") id: Long, student: Student): Response {
        service.update(student)
        return Response.ok(student, MediaType.APPLICATION_JSON_TYPE).build()
    }

    @DELETE
    @Path("/{id}")
    open fun delete(@PathParam("id") id: Long): Response {
        service.delete(id)
        return Response.noContent().build()
    }

}

```

我们明确地打开了我们的资源类和所有公共方法，就像前面的业务服务示例一样。我们还再次使用了lateinit关键字，这次是为了将我们的业务服务注入到资源类中。

## 5. 使用 Arquillian 进行测试

我们现在将使用 Arquillian 和 Shrinkwrap 对我们的应用程序进行集成测试。

要使用 Arquillian 测试我们的应用程序，我们需要使用 ShrinkWrap 设置打包和部署到测试容器：

```java
@RunWith(Arquillian.class)
public class StudentResourceIntegrationTest {

    @Deployment
    public static WebArchive createDeployment() {
        JavaArchive[] kotlinRuntime = Maven.configureResolver()
          .workOffline()
          .withMavenCentralRepo(true)
          .withClassPathResolution(true)
          .loadPomFromFile("pom.xml")
          .resolve("org.jetbrains.kotlin:kotlin-stdlib")
          .withTransitivity()
          .as(JavaArchive.class);

        return ShrinkWrap.create(WebArchive.class, "kotlin.war")
          .addPackages(true, Filters.exclude(".Test"), "com.baeldung.jeekotlin")
          .addAsLibraries(kotlinRuntime)
          .addAsResource("META-INF/persistence.xml")
          .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }
    // more code
}

```

部署类似于用于集成测试的典型 Java 部署，但它包含一些额外的配置。在这里，我们使用 Shrinkwrap Maven Resolver从 Maven 存储库中检索kotlin-stdlib 。然后我们将它作为一个库归档到我们的 WAR 中。

之后，我们使用 HttpClient对我们的 REST API 运行 CRUD 请求：

```java
@Test
@RunAsClient
public void when_post__then_return_ok(@ArquillianResource URL url)
  throws URISyntaxException, JsonProcessingException {
    String student = new ObjectMapper()
      .writeValueAsString(new Student("firstName", "lastName"));
      WebTarget webTarget = ClientBuilder.newClient().target(url.toURI());

    Response response = webTarget
      .path("/student")
      .request(MediaType.APPLICATION_JSON)
      .post(Entity.json(student));

    assertEquals(200, response.getStatus());
}

```

在此示例中，我们使用@ArquillianResource为 API 提供 URL，然后我们序列化Student对象并将其 POST 到 API。如果一切正常并且对象已在数据库中创建，则响应状态为 200 OK，我们在测试结束时断言。

## 六，总结

在本文中，我们演示了如何在 Kotlin 中构建 CRUD REST JPA 应用程序、如何部署它、如何在 Jakarta EE 容器中运行它以及如何使用 Arquillian 对其进行测试。如你所见，Kotlin 和 Java 可以很好地协同工作，但我们必须做一些额外的工作才能实现。