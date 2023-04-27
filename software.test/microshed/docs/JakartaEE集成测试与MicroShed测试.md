## 1. 概述

Jakarta EE应用程序的集成测试至关重要，在完整设置中测试应用程序将确保你的所有组件都能协同工作。

Testcontainers项目提供了一种使用Docker容器进行此类测试的好方法。通过[MicroShed](https://github.com/MicroShed/microshed-testing)测试，你可以方便地使用Testcontainers为你的Jakarta EE应用程序编写集成测试。对于Java EE或独立的MicroProfile应用程序也是如此。

在这篇文章中，我们将了解如何使用此框架来编写对于Jakarta EE应用程序的集成测试。

## 2. MicroShed测试的项目设置

为了创建这个项目，我们使用以下[Maven Archetype](https://rieckpil.de/bootstrap-a-jakarta-ee-8-maven-project-with-java-11-in-seconds/)：

```shell
mvn archetype:generate \     
  -DarchetypeGroupId=cn.tuyucheng.taketoday \
  -DarchetypeArtifactId=jakartaee8 \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=cn.tuyucheng.taketoday \
  -DartifactId=microshed \
  -DinteractiveMode=false
```

除了Jakarta EE和Eclipse MicroProfile的基本依赖项之外，我们还需要一些测试依赖项。

由于**MicroShed测试在幕后使用[Testcontainers](https://rieckpil.de/howto-write-spring-boot-integration-tests-with-a-real-database/)**，我们可以添加[官方Testcontainers](https://www.testcontainers.org/)依赖项来启动PostgreSQL和MockServer容器。

此外，我们需要JUnit 5和MicroShed Testing Open Liberty依赖项本身。Log4 1.2 SLF4J绑定依赖项是可选的，可以改进我们测试的日志记录输出。

因此，完整的pom.xml如下所示：

```xml
<dependencies>
    <dependency>
        <groupId>jakarta.platform</groupId>
        <artifactId>jakarta.jakartaee-api</artifactId>
        <version>${jakarta.jakartaee-api.version}</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.eclipse.microprofile</groupId>
        <artifactId>microprofile</artifactId>
        <version>${microprofile.version}</version>
        <type>pom</type>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.microshed</groupId>
        <artifactId>microshed-testing-liberty</artifactId>
        <version>${microshed-testing.version}</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>${junit-jupiter.version}</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-log4j12</artifactId>
        <version>1.7.29</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <version>${testcontainers.version}</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>mockserver</artifactId>
        <version>${testcontainers.version}</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mock-server</groupId>
        <artifactId>mockserver-client-java</artifactId>
        <version>5.5.4</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <finalName>microshed</finalName>
    <plugins>
        <plugin>
            <groupId>io.openliberty.tools</groupId>
            <artifactId>liberty-maven-plugin</artifactId>
            <version>3.1</version>
        </plugin>
    </plugins>
</build>

<properties>
    <failOnMissingWebXml>false</failOnMissingWebXml>
    <jakarta.jakartaee-api.version>8.0.0</jakarta.jakartaee-api.version>
    <microprofile.version>3.3</microprofile.version>
    <microshed-testing.version>0.9</microshed-testing.version>
    <testcontainers.version>1.14.2</testcontainers.version>
</properties>
```

使用MicroShed测试，你可以提供自己的Dockerfile，或者在OpenLiberty的情况下使用扩展来完成几乎为零的设置任务。

MicroShed测试在项目的根文件夹或src/main/docker中搜索Dockerfile。

因此，我们可以在这两个位置的任意一处创建一个自定义的Dockerfile：

```dockerfile
FROM openliberty/open-liberty:20.0.0.5-kernel-java11-openj9-ubi

COPY --chown=1001:0  src/main/liberty/config/postgres /config/postgres
COPY --chown=1001:0  src/main/liberty/config/server.xml /config
COPY --chown=1001:0  target/microshed.war /config/dropins/

RUN configure.sh
```

对于此应用程序，我们使用来自MicroShed Testing的官方OpenLiberty扩展，因此只需要在src/main/liberty中配置Open Liberty服务器。此文件夹包含自定义server.xml和JDBC驱动程序：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<server description="new server">

    <featureManager>
        <feature>cdi-2.0</feature>
        <feature>jpa-2.2</feature>
        <feature>jaxrs-2.1</feature>
        <feature>mpConfig-1.4</feature>
        <feature>mpHealth-2.2</feature>
        <feature>mpRestClient-1.4</feature>
    </featureManager>

    <httpEndpoint id="defaultHttpEndpoint" httpPort="9080" httpsPort="9443"/>

    <dataSource id="DefaultDataSource">
        <jdbcDriver libraryRef="postgresql-library"/>
        <properties.postgresql serverName="${POSTGRES_HOSTNAME}"
                               portNumber="${POSTGRES_PORT}"
                               databaseName="users"
                               user="${POSTGRES_USERNAME}"
                               password="${POSTGRES_PASSWORD}"/>
    </dataSource>

    <library id="postgresql-library">
        <fileset dir="${server.config.dir}/postgres"/>
    </library>
</server>
```

## 3. Jakarta EE应用程序

由于不想为此示例提供一个简单的Hello World应用程序，因此本文使用了一个具有JPA和PostgreSQL以及外部REST API作为依赖项的应用程序。这应该反映了80%的基本应用程序。

该应用程序包含两个JAX-RS资源：SampleResource和PersonResource

首先，你可以从当天的报价中检索[MicroProfile Config](https://rieckpil.de/whatis-eclipse-microprofile-config/)属性。今天的报价是使用[MicroProfile RestClient](https://rieckpil.de/whatis-eclipse-microprofile-rest-client/)从公共REST API中获取的：

```java
@RegisterRestClient(baseUri = "https://quotes.rest")
public interface QuoteRestClient {

    @GET
    @Path("/qod")
    @Consumes(MediaType.APPLICATION_JSON)
    JsonObject getQuoteOfTheDay();
}
```

使用此公共REST API，稍后我们将演示如何在Jakarta EE集成测试中mock此调用。完整的资源如下所示：

```java
@Path("sample")
@Produces(MediaType.TEXT_PLAIN)
public class SampleResource {

    @Inject
    @ConfigProperty(name = "message")
    private String message;

    @Inject
    @RestClient
    private QuoteRestClient quoteRestClient;

    @GET
    @Path("/message")
    public Response getMessage() {
        return Response.ok(message).build();
    }

    @GET
    @Path("/quotes")
    public Response getQuotes() {
        var quoteOfTheDayPointer = Json.createPointer("/contents/quotes/0/quote");
        var quoteOfTheDay = quoteOfTheDayPointer.getValue(quoteRestClient.getQuoteOfTheDay()).toString();
        return Response.ok(quoteOfTheDay).build();
    }
}
```

其次，PersonResource允许客户端创建和读取Person资源。我们将使用JPA将Person存储在PostgreSQL数据库中：

```java
@Path("/persons")
@ApplicationScoped
@Transactional(TxType.REQUIRED)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {

    @PersistenceContext
    private EntityManager entityManager;

    @GET
    public List<Person> getAllPersons() {
        return entityManager.createQuery("SELECT p FROM Person p", Person.class).getResultList();
    }

    @GET
    @Path("/{id}")
    public Person getPersonById(@PathParam("id") Long id) {
        var personById = entityManager.find(Person.class, id);
        if (personById == null) {
            throw new NotFoundException();
        }
        return personById;
    }

    @POST
    public Response createNewPerson(@Context UriInfo uriInfo, @RequestBody Person personToStore) {
        entityManager.persist(personToStore);
        var headerLocation = uriInfo.getAbsolutePathBuilder()
                .path(personToStore.getId().toString())
                .build();
        return Response.created(headerLocation).build();
    }
}
```

## 4. MicroShed集成测试设置

接下来，我们可以为我们的Jakarta EE(或Java EE或独立MicroProfile)应用程序设置集成测试。

**通过MicroShed测试，我们可以创建一个SharedContainerConfiguration来在集成测试之间共享Docker容器**。

由于我们的系统需要一个正在运行的应用程序、一个PostgreSQL数据库和一个远程系统，因此这里创建了三个容器：

```java
public class SampleApplicationConfig implements SharedContainerConfiguration {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>()
            .withNetworkAliases("mypostgres")
            .withExposedPorts(5432)
            .withUsername("duke")
            .withPassword("duke42")
            .withDatabaseName("users");

    @Container
    public static MockServerContainer mockServer = new MockServerContainer()
            .withNetworkAliases("mockserver");

    @Container
    public static ApplicationContainer app = new ApplicationContainer()
            .withEnv("POSTGRES_HOSTNAME", "mypostgres")
            .withEnv("POSTGRES_PORT", "5432")
            .withEnv("POSTGRES_USERNAME", "duke")
            .withEnv("POSTGRES_PASSWORD", "duke42")
            .withEnv("message", "Hello World from MicroShed Testing")
            .withAppContextRoot("/")
            .withReadinessPath("/health/ready")
            .withMpRestClient(QuoteRestClient.class, "http://mockserver:" + MockServerContainer.PORT);
}
```

PostgreSQLContainer和MockServerContainer是Testcontainer依赖项的一部分。ApplicationContainer现在是我们使用的第一个MicroShed测试类，你可以将此包装器用于普通Jakarta EE或Java EE应用程序。

我们可以使用任何环境变量设置ApplicationContainer，我们希望以这种方式提供MicroProfile Config属性。接下来，你可以定义应用程序的就绪路径。为此，我们可以使用[MicroProfile Health](https://rieckpil.de/whatis-eclipse-microprofile-health/)并指定就绪路径/health/ready。

此外，MicroShed测试能够覆盖MicroProfile RestClient的基本URL，这允许我们使用MockServer进行集成测试。

使用网络别名，我们的应用程序能够与Docker网络中的MockServer和PostgreSQL数据库进行通信。

## 5. 为Jakarta EE应用程序编写集成测试

有了这个设置，就可以开始为我们的Jakarta EE应用程序编写集成测试。可以使用@MicroShedTest为JUnit 5测试启用MicroShed测试。使用@SharedContainerConfig，你可以引用通用系统设置。

为了测试SampleResource，我们在SharedContainerConfiguration中配置MicroProfile Config属性。此外，quotes端点应正确返回当天的报价：

```java
@MicroShedTest
@SharedContainerConfig(SampleApplicationConfig.class)
public class SampleResourceIT {

    @RESTClient
    public static SampleResource sampleEndpoint;

    @Test
    public void shouldReturnSampleMessage() {
        assertEquals("Hello World from MicroShed Testing",
                sampleEndpoint.getMessage());
    }

    @Test
    public void shouldReturnQuoteOfTheDay() {
        var resultQuote = Json.createObjectBuilder()
                .add("contents",
                        Json.createObjectBuilder().add("quotes",
                                Json.createArrayBuilder().add(Json.createObjectBuilder()
                                        .add("quote", "Do not worry if you have built your castles in the air. " +
                                                      "They are where they should be. Now put the foundations under them."))))
                .build();

        new MockServerClient(mockServer.getContainerIpAddress(), mockServer.getServerPort())
                .when(request("/qod"))
                .respond(response().withBody(resultQuote.toString(), com.google.common.net.MediaType.JSON_UTF_8));

        var result = sampleEndpoint.getQuotes();

        System.out.println("Quote of the day: " + result);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
```

在集成测试中注入具有@RESTClient的JAX-RS资源允许我们像使用JAX-RS客户端一样进行HTTP调用。

PersonResource的集成测试更高级，也就是测试创建一个Person并查询他：

```java
@MicroShedTest
@SharedContainerConfig(SampleApplicationConfig.class)
public class PersonResourceIT {

    @RESTClient
    public static PersonResource personsEndpoint;

    @Test
    public void shouldCreatePerson() {
        Person duke = new Person();
        duke.setFirstName("duke");
        duke.setLastName("jakarta");

        Response result = personsEndpoint.createNewPerson(null, duke);

        assertEquals(Response.Status.CREATED.getStatusCode(), result.getStatus());
        var createdUrl = result.getHeaderString("Location");
        assertNotNull(createdUrl);

        var id = Long.valueOf(createdUrl.substring(createdUrl.lastIndexOf('/') + 1));
        assertTrue(id > 0, "Generated ID should be greater than 0 but was: " + id);

        var newPerson = personsEndpoint.getPersonById(id);
        assertNotNull(newPerson);
        assertEquals("duke", newPerson.getFirstName());
        assertEquals("jakarta", newPerson.getLastName());
    }
}
```

## 6. 总结

尽管该项目还处于早期阶段，但它为使用Testcontainers编写Jakarta EE集成测试提供了出色的支持。

还有可用于Open Liberty和Payara的[专用依赖项](https://microshed.org/microshed-testing/features/01_SupportedRuntimes.html)。通过这些依赖项，MicroShed测试的使用可以变得更加简单。