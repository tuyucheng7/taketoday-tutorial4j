## 1. 概述

随着微服务架构和云原生应用程序开发的流行，对快速、轻量级应用服务器的需求越来越大。

在本介绍性教程中，我们将探索[Open Liberty框架](https://openliberty.io/)来创建和使用RESTful Web服务。我们还将研究它提供的一些基本功能。

## 2. Open Liberty

**Open Liberty是Java生态系统的开放框架，允许使用[Eclipse MicroProfile](https://www.baeldung.com/eclipse-microprofile)和[Jakarta EE](https://www.baeldung.com/kotlin/java-ee-kotlin-app)平台的功能开发微服务**。

它是一种灵活、快速且轻量级的Java运行时，对于云原生微服务开发似乎很有前途。

**该框架允许我们仅配置我们的应用程序需要的功能，从而在启动期间占用更少的内存**。此外，它可以部署在任何使用[Docker](https://www.baeldung.com/docker-compose)和[Kubernetes](https://www.baeldung.com/kubernetes)等容器的云平台上。

它通过实时重新加载代码以实现快速迭代来支持快速开发。

## 3. 构建并运行

首先，我们将创建一个名为open-liberty的简单的基于Maven的项目，然后将最新的[liberty-maven-plugin](https://central.sonatype.com/artifact/io.openliberty.tools/liberty-maven-plugin/3.7.1)插件添加到pom.xml中：

```xml
<plugin>
    <groupId>io.openliberty.tools</groupId>
    <artifactId>liberty-maven-plugin</artifactId>
    <version>3.3-M3</version>
</plugin>
```

或者，我们可以添加最新的[openliberty-runtime](https://central.sonatype.com/artifact/io.openliberty/openliberty-runtime/23.0.0.2) Maven依赖项作为liberty-maven-plugin的替代方案：

```xml
<dependency>
    <groupId>io.openliberty</groupId>
    <artifactId>openliberty-runtime</artifactId>
    <version>20.0.0.1</version>
    <type>zip</type>
</dependency>
```

同样，我们可以将最新的Gradle依赖项添加到build.gradle中：

```groovy
dependencies {
    libertyRuntime group: 'io.openliberty', name: 'openliberty-runtime', version: '20.0.0.1'
}
```

然后，我们将添加最新的[jakarta.jakartaee-web-api](https://central.sonatype.com/artifact/jakarta.platform/jakarta.jakartaee-web-api/10.0.0)和[microprofile](https://central.sonatype.com/artifact/org.eclipse.microprofile/microprofile/6.0) Maven依赖项：

```xml
<dependency>
    <groupId>jakarta.platform</groupId>
    <artifactId>jakarta.jakartaee-web-api</artifactId>
    <version>8.0.0</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.eclipse.microprofile</groupId>
    <artifactId>microprofile</artifactId>
    <version>3.2</version>
    <type>pom</type>
    <scope>provided</scope>
</dependency>
```

然后，让我们将默认的HTTP端口属性添加到pom.xml中：

```xml
<properties>
    <liberty.var.default.http.port>9080</liberty.var.default.http.port>
    <liberty.var.default.https.port>9443</liberty.var.default.https.port>
</properties>
```

接下来，我们将在src/main/liberty/config目录中创建server.xml文件：

```xml
<server description="Baeldung Open Liberty server">
    <featureManager>
        <feature>mpHealth-2.0</feature>
    </featureManager>
    <webApplication location="open-liberty.war" contextRoot="/"/>
    <httpEndpoint host="*" httpPort="${default.http.port}"
                  httpsPort="${default.https.port}" id="defaultHttpEndpoint"/>
</server>
```

在这里，我们添加了mpHealth-2.0功能来检查应用程序的运行状况。

这就是所有基本设置。让我们运行Maven命令来首次编译文件：

```shell
mvn clean package
```

最后，让我们使用Liberty提供的Maven命令运行服务器：

```shell
mvn liberty:dev
```

现在我们的应用程序已启动，可以在[localhost:9080](http://localhost:9080)访问：

<img src="../assets/img.png">

此外，我们可以在localhost:9080/health访问应用程序的健康状况：

```json
{
    "checks": [],
    "status": "UP"
}
```

**liberty:dev命令以开发模式启动Open Liberty服务器**，它会热重载对代码或配置所做的任何更改，而无需重新启动服务器。

同样，liberty:run命令可用于在生产模式下启动服务器。

另外，**我们可以使用liberty:start-server和liberty:stop-server在后台启动/停止服务器**。

## 4. Servlet

要在应用程序中使用Servlet，我们将servlet-4.0功能添加到server.xml：

```xml
<featureManager>
    <!--...-->
    <feature>servlet-4.0</feature>
</featureManager>
```

如果在pom.xml中使用openliberty-runtime Maven依赖项，请添加最新的[servlet-4.0](https://central.sonatype.com/artifact/io.openliberty.features/servlet-4.0/23.0.0.2) Maven依赖项：

```xml
<dependency>
    <groupId>io.openliberty.features</groupId>
    <artifactId>servlet-4.0</artifactId>
    <version>20.0.0.1</version>
    <type>esa</type>
</dependency>
```

但是，如果我们使用liberty-maven-plugin插件，则没有必要这样做。

然后，我们将创建扩展HttpServlet类的AppServlet类：

```java
@WebServlet(urlPatterns="/app")
public class AppServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String htmlOutput = "<html><h2>Hello! Welcome to Open Liberty</h2></html>";
        response.getWriter().append(htmlOutput);
    }
}
```

在这里，我们添加了[@WebServlet](https://jakarta.ee/specifications/platform/8/apidocs/javax/servlet/annotation/WebServlet.html)注解，这将使AppServlet在指定的URL模式下可用。

让我们在[localhost:9080/app](http://localhost:9080/app)访问Servlet：

<img src="../assets/img_1.png">

## 5. 创建RESTful Web服务

首先，让我们将[jaxrs-2.1](https://search.maven.org/search?q=g:io.openliberty.featuresa:jaxrs-2.1)功能添加到server.xml中：

```xml
<featureManager>
    <!--...-->
    <feature>jaxrs-2.1</feature>
</featureManager>
```

然后，我们将创建ApiApplication类，它为RESTful Web服务提供端点：

```java
@ApplicationPath("/api")
public class ApiApplication extends Application {
}
```

在这里，我们为URL路径使用了[@ApplicationPath](https://jakarta.ee/specifications/platform/8/apidocs/javax/ws/rs/ApplicationPath.html)注解。

然后，让我们创建服务模型的Person类：

```java
public class Person {
    private String username;
    private String email;

    // getters, setters and constructors
}
```

接下来，我们将创建PersonResource类来定义HTTP映射：

```java
@RequestScoped
@Path("persons")
public class PersonResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Person> getAllPersons() {
        return Arrays.asList(new Person(1, "normanlewis", "normanlewis@email.com"));
    }
}
```

在这里，我们添加了getAllPersons方法，用于将GET映射到/api/people端点。因此，我们已准备好使用RESTful Web服务，并且liberty:dev命令将即时加载更改。

让我们使用curl GET请求访问/api/persons RESTful Web服务：

```shell
curl --request GET --url http://localhost:9080/api/persons
```

然后，我们将得到一个JSON数组作为响应：

```json
[
    {
        "id": 1,
        "username": "normanlewis",
        "email": "normanlewis@email.com"
    }
]
```

同样，我们可以通过创建addPerson方法来添加POST映射：

```java
@POST
@Consumes(MediaType.APPLICATION_JSON)
public Response addPerson(Person person) {
    String respMessage = "Person " + person.getUsername() + " received successfully.";
    return Response.status(Response.Status.CREATED).entity(respMessage).build();
}
```

现在，我们可以使用curl POST请求调用端点：

```shell
curl --request POST --url http://localhost:9080/api/persons \
  --header 'content-type: application/json' \
  --data '{"username": "normanlewis", "email": "normanlewis@email.com"}'
```

响应将如下所示：

```shell
Person normanlewis received successfully.
```

## 6. 持久层

### 6.1 配置

让我们为RESTful Web服务添加持久性支持。

首先，我们将[derby](https://central.sonatype.com/artifact/org.apache.derby/derby/10.15.2.0) Maven依赖项添加到pom.xml中：

```xml
<dependency>
    <groupId>org.apache.derby</groupId>
    <artifactId>derby</artifactId>
    <version>10.14.2.0</version>
</dependency>
```

然后，我们将向server.xml添加一些功能，例如jpa-2.2、jsonp-1.1和cdi-2.0：

```xml
<featureManager>
    <!--...-->
    <feature>jpa-2.2</feature> 
    <feature>jsonp-1.1</feature>
    <feature>cdi-2.0</feature>
</featureManager>
```

在这里，[jsonp-1.1](https://search.maven.org/search?q=g:io.openliberty.featuresa:jsonp-1.1)功能提供了用于JSON处理的Java API，而[cdi-2.0](https://search.maven.org/search?q=g:io.openliberty.featuresa:cdi-2.0)功能处理作用域和依赖注入。

接下来，我们将在src/main/resources/META-INF目录中创建persistence.xml：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.2"
             xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
                        http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd">
    <persistence-unit name="jpa-unit" transaction-type="JTA">
        <jta-data-source>jdbc/jpadatasource</jta-data-source>
        <properties>
            <property name="eclipselink.ddl-generation" value="create-tables"/>
            <property name="eclipselink.ddl-generation.output-mode" value="both"/>
        </properties>
    </persistence-unit>
</persistence>
```

在这里，我们使用了[EclipseLink ddl-generation](https://www.eclipse.org/eclipselink/documentation/2.5/jpa/extensions/p_ddl_generation.htm)来自动创建我们的数据库模式。我们还可以使用其他替代方案，例如Hibernate。

然后，让我们将dataSource配置添加到server.xml：

```xml
<library id="derbyJDBCLib">
    <fileset dir="${shared.resource.dir}" includes="derby*.jar"/> 
</library>
<dataSource id="jpadatasource" jndiName="jdbc/jpadatasource">
    <jdbcDriver libraryRef="derbyJDBCLib" />
    <properties.derby.embedded databaseName="libertyDB" createDatabase="create" />
</dataSource>
```

请注意，jndiName具有对persistence.xml中的jta-data-source标签的相同引用。

### 6.2 实体和DAO

然后，我们将[@Entity](https://www.baeldung.com/jpa-entities)和相关注解添加到Person类：

```java
@Entity
public class Person {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private int id;

    private String username;
    private String email;

    // getters and setters
}
```

接下来，让我们创建将使用[EntityManager](https://www.baeldung.com/hibernate-entitymanager)实例与数据库交互的PersonDao类：

```java
@RequestScoped
public class PersonDao {
    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;

    public Person createPerson(Person person) {
        em.persist(person);
        return person;
    }

    public Person readPerson(int personId) {
        return em.find(Person.class, personId);
    }
}
```

请注意，[@PersistenceContext](https://jakarta.ee/specifications/platform/8/apidocs/javax/persistence/PersistenceContext.html)定义了对persistence.xml中persistence-unit标签的相同引用

现在，我们将在PersonResource类中注入PersonDao依赖：

```java
@RequestScoped
@Path("person")
public class PersonResource {
    @Inject
    private PersonDao personDao;

    // ...
}
```

在这里，我们使用了CDI功能提供的[@Inject](https://jakarta.ee/specifications/platform/8/apidocs/javax/inject/Inject.html)注解。

最后，我们将更新PersonResource类的addPerson方法来持久化Person对象：

```java
@POST
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public Response addPerson(Person person) {
    personDao.createPerson(person);
    String respMessage = "Person #" + person.getId() + " created successfully.";
    return Response.status(Response.Status.CREATED).entity(respMessage).build();
}
```

此处，addPerson方法使用[@Transactional](https://jakarta.ee/specifications/platform/8/apidocs/javax/transaction/Transactional.html)注解进行标注，以控制CDI托管bean上的事务。

让我们使用已经讨论过的curl POST请求调用端点：

```shell
curl --request POST --url http://localhost:9080/api/persons \
  --header 'content-type: application/json' \
  --data '{"username": "normanlewis", "email": "normanlewis@email.com"}'
```

然后，我们将收到一条文本回复：

```text
Person #1 created successfully.
```

同样，让我们添加带有GET映射的getPerson方法来获取Person对象：

```java
@GET
@Path("{id}")
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public Person getPerson(@PathParam("id") int id) {
    Person person = personDao.readPerson(id);
    return person;
}
```

让我们使用curl GET请求调用端点：

```shell
curl --request GET --url http://localhost:9080/api/persons/1
```

然后，我们将获取Person对象作为JSON响应：

```json
{
    "email": "normanlewis@email.com",
    "id": 1,
    "username": "normanlewis"
}
```

## 7. 使用JSON-B使用RESTful Web服务

首先，我们将通过向server.xml添加[jsonb-1.0](https://search.maven.org/search?q=g:io.openliberty.featuresa:jsonb-1.0)功能来启用直接序列化和反序列化模型的能力：

```xml
<featureManager>
    <!--...-->
    <feature>jsonb-1.0</feature>
</featureManager>
```

然后，让我们使用consumeWithJsonb方法创建RestConsumer类：

```java
public class RestConsumer {
    public static String consumeWithJsonb(String targetUrl) {
        Client client = ClientBuilder.newClient();
        Response response = client.target(targetUrl).request().get();
        String result = response.readEntity(String.class);
        response.close();
        client.close();
        return result;
    }
}
```

在这里，我们使用了[ClientBuilder](https://jakarta.ee/specifications/platform/8/apidocs/javax/ws/rs/client/ClientBuilder.html)类来请求RESTful Web服务端点。

最后，让我们编写一个单元测试来使用/api/person RESTful Web服务并验证响应：

```java
@Test
public void whenConsumeWithJsonb_thenGetPerson() {
    String url = "http://localhost:9080/api/persons/1";
    String result = RestConsumer.consumeWithJsonb(url);        
    
    Person person = JsonbBuilder.create().fromJson(result, Person.class);
    assertEquals(1, person.getId());
    assertEquals("normanlewis", person.getUsername());
    assertEquals("normanlewis@email.com", person.getEmail());
}
```

在这里，我们使用[JsonbBuilder](https://jakarta.ee/specifications/platform/8/apidocs/javax/json/bind/JsonbBuilder.html)类将String响应解析为Person对象。

此外，**我们可以通过添加[mpRestClient-1.3](https://search.maven.org/search?q=g:io.openliberty.featuresa:mpRestClient-1.3)功能来使用MicroProfile RestClient来使用RESTful Web服务**。它提供[RestClientBuilder](https://download.eclipse.org/microprofile/microprofile-rest-client-1.3/apidocs/org/eclipse/microprofile/rest/client/RestClientBuilder.html)接口来请求RESTful Web服务端点。

## 8. 总结

在本文中，我们探索了Open Liberty框架-一个快速且轻量级的Java运行时，它提供了Eclipse MicroProfile和Jakarta EE平台的全部功能。

首先，我们使用JAX-RS创建了一个RESTful Web服务。然后，我们使用JPA和CDI等功能启用持久性。

最后，我们使用JSON-B使用RESTful Web服务。

