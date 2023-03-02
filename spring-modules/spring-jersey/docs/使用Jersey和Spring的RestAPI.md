## 1. 概述

[Jersey](https://jersey.java.net/)是一个用于开发RESTful Web服务的开源框架。它用作JAX-RS的参考实现。

在本文中，**我们将探索使用Jersey 2创建RESTful Web服务**。此外，我们将使用Spring的依赖注入(DI)和Java配置。

## 2. Maven依赖

让我们首先向pom.xml添加依赖项：

```xml
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <artifactId>jersey-container-servlet</artifactId>
    <version>2.26</version>
</dependency>
<dependency>
    <groupId>org.glassfish.jersey.media</groupId>
    <artifactId>jersey-media-json-jackson</artifactId>
    <version>2.26</version>
</dependency>
```

此外，对于Spring集成，我们必须添加jersey-spring4依赖项：

```xml
<dependency>
    <groupId>org.glassfish.jersey.ext</groupId>
    <artifactId>jersey-spring4</artifactId>
    <version>2.26</version>
</dependency>
```

这些依赖项的最新版本可以在[jersey-container-servlet](https://central.sonatype.com/artifact/org.glassfish.jersey.containers/jersey-container-servlet/3.1.1)、[jersey-media-json-jackson](https://central.sonatype.com/artifact/org.glassfish.jersey.media/jersey-media-json-jackson/3.1.1)和[jersey-spring4](https://central.sonatype.com/artifact/org.glassfish.jersey.ext/jersey-spring4/3.0.0-M6)找到。

## 3. Web配置

**接下来，我们需要搭建一个Web项目来做Servlet的配置**。为此，我们将使用Spring的WebApplicationInitializer：

```java
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();

        servletContext.addListener(new ContextLoaderListener(context));
        servletContext.setInitParameter("contextConfigLocation", "cn.tuyucheng.taketoday.server");
    }
}
```

在这里，我们添加了@Order(Ordered.HIGHEST_PRECEDENCE)注解以确保我们的初始化程序在Jersey-Spring默认初始化程序之前执行。

## 4. 使用Jersey JAX-RS的服务

### 4.1 资源表示类

让我们使用一个示例资源表示类：

```java
@XmlRootElement
public class Employee {
    private int id;
    private String firstName;

    // standard getters and setters
}
```

请注意，只有在需要XML支持(除了JSON之外)时，才需要像@XmlRootElement这样的JAXB注解。

### 4.2 服务实现

现在让我们看看如何使用JAX-RS注解来创建RESTful Web服务：

```java
@Path("/employees")
public class EmployeeResource {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GET
    @Path("/{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Employee getEmployee(@PathParam("id") int id) {
        return employeeRepository.getEmployee(id);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response addEmployee(Employee employee, @Context UriInfo uriInfo) {

        employeeRepository.addEmployee(new Employee(employee.getId(),
              employee.getFirstName(), employee.getLastName(),
              employee.getAge()));

        return Response.status(Response.Status.CREATED.getStatusCode())
              .header("Location",
                    String.format("%s/%s",uriInfo.getAbsolutePath().toString(), employee.getId())).build();
    }
}
```

**@Path注解提供服务的相对URI路径**。我们还可以在URI语法中嵌入变量，如{id}变量所示。然后，变量将在运行时被替换。要获取变量的值，我们可以使用@PathParam注解。

**@GET、@PUT、@POST、@DELETE和@HEAD定义了请求的HTTP方法**，这些方法将被标注的方法处理。

**@Produces注解定义端点的响应类型(MIME媒体类型)**。在我们的示例中，我们已将其配置为根据HTTP标头Accept的值(application/json或application/xml)返回JSON或XML。

**另一方面，@Consumes注解定义了服务可以使用的MIME媒体类型**。在我们的示例中，服务可以使用JSON或XML，具体取决于HTTP标头Content-Type(application/json或application/xml)。

@Context注解用于将信息注入到类字段、bean属性或方法参数中。在我们的示例中，我们使用它来注入UriInfo。我们还可以使用它来注入ServletConfig、ServletContext、HttpServletRequest和HttpServletResponse。

## 5. 使用ExceptionMapper

ExceptionMapper允许我们拦截异常并向客户端返回适当的HTTP响应代码。在以下示例中，如果抛出EmployeeNotFound异常，则返回HTTP响应代码404：

```java
@Provider
public class NotFoundExceptionHandler implements ExceptionMapper<EmployeeNotFound> {

    public Response toResponse(EmployeeNotFound ex) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
```

## 6. 管理资源类

最后，**让我们根据应用程序路径连接所有服务实现类和异常映射器**：

```java
@ApplicationPath("/resources")
public class RestConfig extends Application {
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(
              Arrays.asList(
                    EmployeeResource.class,
                    NotFoundExceptionHandler.class,
                    AlreadyExistsExceptionHandler.class));
    }
}
```

## 7. API测试

现在让我们通过一些实时测试来测试API：

```java
public class JerseyApiLiveTest {

    private static final String SERVICE_URL = "http://localhost:8082/spring-jersey/resources/employees";

    @Test
    public void givenGetAllEmployees_whenCorrectRequest_thenResponseCodeSuccess() throws ClientProtocolException, IOException {
        HttpUriRequest request = new HttpGet(SERVICE_URL);

        HttpResponse httpResponse = HttpClientBuilder
              .create()
              .build()
              .execute(request);

        assertEquals(httpResponse
              .getStatusLine()
              .getStatusCode(), HttpStatus.SC_OK);
    }
}
```

## 8. 总结

在本文中，我们介绍了Jersey框架并开发了一个简单的API。我们将Spring用于依赖注入功能。我们还看到了ExceptionMapper的使用。