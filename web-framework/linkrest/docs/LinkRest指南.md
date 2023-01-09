## 1. 概述

[LinkRest](https://github.com/nhl/link-rest)是一个用于构建数据驱动的 REST Web 服务的开源框架。它建立在JAX-RS和Apache Cayenne ORM之上，并使用基于 HTTP/JSON 的消息协议。

基本上，这个框架旨在提供一种在网络上公开我们的数据存储的简单方法。

在以下部分中，我们将了解如何使用LinkRest构建 REST Web 服务来访问数据模型。

## 2.Maven依赖_

要开始使用该库，首先我们需要添加[link-rest](https://search.maven.org/classic/#search|ga|1|a%3A"link-rest")依赖项：

```xml
<dependency>
    <groupId>com.nhl.link.rest</groupId>
    <artifactId>link-rest</artifactId>
    <version>2.9</version>
</dependency>
```

这也带来了cayenne-server神器。

此外，我们将使用Jersey作为JAX-RS实现，因此我们需要添加[jersey-container-servlet](https://search.maven.org/classic/#search|ga|1|a%3A"jersey-container-servlet")依赖项，以及用于序列化 JSON 响应[的 jersey-media-moxy ：](https://search.maven.org/classic/#search|ga|1|a%3A"jersey-media-moxy")

```xml
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <artifactId>jersey-container-servlet</artifactId>
    <version>2.25.1</version>
</dependency>
<dependency>
    <groupId>org.glassfish.jersey.media</groupId>
    <artifactId>jersey-media-moxy</artifactId>
    <version>2.25.1</version>
</dependency>
```

对于我们的示例，我们将使用内存中的H2数据库，因为它更容易设置；因此，我们还将添加[h2](https://search.maven.org/classic/#search|ga|1|a%3A"h2" AND g%3A"com.h2database")：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.196</version>
</dependency>
```

## 3. Cayenne数据模型

我们将使用的数据模型包含一个Department和一个表示一对多关系的Employee实体：

[![桌子](https://www.baeldung.com/wp-content/uploads/2017/09/tables-300x119.png)](https://www.baeldung.com/wp-content/uploads/2017/09/tables.png)

如前所述，LinkRest可处理使用Apache Cayenne ORM生成的数据对象。使用Cayenne不是本文的主要主题，因此有关更多信息，请查看[Apache Cayenne文档](https://cayenne.apache.org/)。

我们将Cayenne项目保存在cayenne-linkrest-project.xml文件中。

运行cayenne-maven-plugin后，这将生成两个_Department和_Employee抽象类——它们将扩展CayenneDataObject类，以及从它们派生的两个具体类，Department和Employee。

后面这些类是我们可以自定义并与LinkRest一起使用的类。

## 4.LinkRest应用启动

在下一节中，我们将编写和测试 REST 端点，因此为了能够运行它们，我们需要设置运行时。

由于我们使用Jersey作为JAX-RS实现，让我们添加一个类来扩展ResourceConfig并指定将包含我们定义 REST 端点的类的包：

```java
@ApplicationPath("/linkrest")
public class LinkRestApplication extends ResourceConfig {

    public LinkRestApplication() {
        packages("com.baeldung.linkrest.apis");
        
        // load linkrest runtime
    }
}
```

在同一个构造函数中，我们需要构建LinkRestRuntime并将其注册到Jersey容器。此类基于首先加载CayenneRuntime：

```java
ServerRuntime cayenneRuntime = ServerRuntime.builder()
  .addConfig("cayenne-linkrest-project.xml")
  .build();
LinkRestRuntime lrRuntime = LinkRestBuilder.build(cayenneRuntime);
super.register(lrRuntime);
```

最后，我们需要将类添加到web.xml中：

```xml
<servlet>
    <servlet-name>linkrest</servlet-name>
    <servlet-class>org.glassfish.jersey.servlet.ServletContainer</servlet-class>
        <init-param>
            <param-name>javax.ws.rs.Application</param-name>
            <param-value>com.baeldung.LinkRestApplication</param-value>
        </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>linkrest</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

## 5. 休息资源

现在我们已经有了我们的模型类，我们可以开始编写 REST 资源了。

REST端点是使用标准JAX-RS注解创建的，而响应是使用LinkRest类构建的。

我们的示例将包括编写一系列使用不同 HTTP 方法访问/department URL 的 CRUD 端点。

首先，让我们创建映射到/department的DepartmentResource类：

```java
@Path("department")
@Produces(MediaType.APPLICATION_JSON)
public class DepartmentResource {

    @Context
    private Configuration config;
    
    // ...
}
```

LinkRest类需要JAX-RS Configuration类的一个实例，它使用Context注解注入，同样由JAX-RS提供。

接下来，让我们继续编写访问Department对象的每个端点。

### 5.1. 使用 POST 创建实体

为了创建实体，LinkRest类提供了create()方法，该方法返回一个UpdateBuilder对象：

```java
@POST
public SimpleResponse create(String data) {
    return LinkRest.create(Department.class, config).sync(data);
}
```

数据参数可以是表示部门的单个 JSON 对象，也可以是对象数组。使用sync()方法将此参数发送到UpdateBuilder以创建一个或多个对象并将记录插入数据库，之后该方法返回一个SimpleResponse。

该库定义了 3 种额外的响应格式：

-   DataResponse<T> – 表示T集合的响应
-   MetadataResponse<T> – 包含有关类型的元数据信息
-   SimpleResponse – 包含两个成功和消息属性的对象

接下来，让我们使用curl将部门记录添加到数据库中：

```plaintext
curl -i -X POST -H "Content-Type:application/json" 
  -d "{"name":"IT"}" http://localhost:8080/linkrest/department
```

结果，该命令返回状态201 Created和成功属性：

```plaintext
{"success":true}
```

我们还可以通过发送 JSON 数组来创建多个对象：

```plaintext
curl -i -X POST -H "Content-Type:application/json" 
  -d "[{"name":"HR"},{"name":"Marketing"}]" 
  http://localhost:8080/linkrest/department
```

### 5.2. 使用 GET 读取实体

查询对象的主要方法是LinkRest类中的select()方法。这将返回一个SelectBuilder对象，我们可以使用它来链接其他查询或过滤方法。

让我们在DepartmentResource类中创建一个端点，它返回数据库中的所有Department对象：

```java
@GET
public DataResponse<Department> getAll(@Context UriInfo uriInfo) {
    return LinkRest.select(Department.class, config).uri(uriInfo).get();
}
```

uri()调用设置SelectBuilder的请求信息，而 get() 返回包装为DataResponse<Department>对象的Departments集合。

让我们看一下我们在使用此端点之前添加的部门：

```plaintext
curl -i -X GET http://localhost:8080/linkrest/department
```

响应采用带有数据数组和总属性的 JSON 对象的形式：

```plaintext
{"data":[
  {"id":200,"name":"IT"},
  {"id":201,"name":"Marketing"},
  {"id":202,"name":"HR"}
], 
"total":3}
```

或者，要检索对象集合，我们也可以使用getOne()而不是get()来检索单个对象。

让我们添加一个映射到/department/{departmentId}的端点，它返回具有给定 ID 的对象。为此，我们将使用byId()方法过滤记录：

```java
@GET
@Path("{id}")
public DataResponse<Department> getOne(@PathParam("id") int id, 
  @Context UriInfo uriInfo) {
    return LinkRest.select(Department.class, config)
      .byId(id).uri(uriInfo).getOne();
}
```

然后，我们可以向这个 URL 发送一个 GET 请求：

```plaintext
curl -i -X GET http://localhost:8080/linkrest/department/200
```

结果是一个包含一个元素的数据数组：

```plaintext
{"data":[{"id":200,"name":"IT"}],"total":1}
```

### 5.3. 使用 PUT 更新实体

要更新记录，我们可以使用update()或createOrUpdate()方法。后者将更新记录(如果存在)或创建它们(如果不存在)：

```java
@PUT
public SimpleResponse createOrUpdate(String data) {
    return LinkRest.createOrUpdate(Department.class, config).sync(data);
}
```

与前面几节类似，数据参数可以是单个对象或对象数组。

让我们更新之前添加的部门之一：

```plaintext
curl -i -X PUT -H "Content-Type:application/json" 
  -d "{"id":202,"name":"Human Resources"}" 
  http://localhost:8080/linkrest/department
```

这将返回一个带有成功或错误消息的 JSON 对象。之后，我们可以验证 ID 为 202 的部门名称是否已更改：

```plaintext
curl -i -X GET http://localhost:8080/linkrest/department/202
```

果然，此命令返回具有新名称的对象：

```plaintext
{"data":[
  {"id":202,"name":"Human Resources"}
],
"total":1}
```

### 5.4. 使用DELETE删除实体

并且，要删除一个对象，我们可以调用创建DeleteBuilder的delete()方法，然后使用id()方法指定我们要删除的对象的主键：

```java
@DELETE
@Path("{id}")
public SimpleResponse delete(@PathParam("id") int id) {
    return LinkRest.delete(Department.class, config).id(id).delete();
}
```

然后我们可以使用curl调用这个端点：

```plaintext
curl -i -X DELETE http://localhost:8080/linkrest/department/202
```

### 5.5. 处理实体之间的关系

LinkRest还包含使处理对象之间的关系更容易的方法。

由于Department与Employee具有一对多关系，让我们添加一个/department/{departmentId}/employees端点来访问EmployeeSubResource类：

```java
@Path("{id}/employees")
public EmployeeSubResource getEmployees(
  @PathParam("id") int id, @Context UriInfo uriInfo) {
    return new EmployeeSubResource(id);
}
```

EmployeeSubResource类对应于一个部门，因此它将有一个设置部门 ID 的构造函数，以及Configuration实例：

```java
@Produces(MediaType.APPLICATION_JSON)
public class EmployeeSubResource {
    private Configuration config;

    private int departmentId;

    public EmployeeSubResource(int departmentId, Configuration configuration) {
        this.departmentId = departmentId;
        this.config = config;
    }

    public EmployeeSubResource() {
    }
}
```

请注意，要将对象序列化为 JSON 对象，默认构造函数是必需的。

接下来，让我们定义一个端点来检索部门中的所有员工：

```java
@GET
public DataResponse<Employee> getAll(@Context UriInfo uriInfo) {
    return LinkRest.select(Employee.class, config)
      .toManyParent(Department.class, departmentId, Department.EMPLOYEES)
      .uri(uriInfo).get();
}
```

在此示例中，我们使用了SelectBuilder的toManyParent()方法来仅查询具有给定父级的对象。

可以用类似的方式创建 POST、PUT、DELETE 方法的端点。

要将员工添加到部门，我们可以使用 POST 方法调用departments/{departmentId}/employees端点：

```plaintext
curl -i -X POST -H "Content-Type:application/json" 
  -d "{"name":"John"}" http://localhost:8080/linkrest/department/200/employees
```

然后，让我们发送一个 GET 请求来查看该部门的员工：

```plaintext
curl -i -X GET "http://localhost:8080/linkrest/department/200/employees
```

这将返回一个带有数据数组的 JSON 对象：

```plaintext
{"data":[{"id":200,"name":"John"}],"total":1}
```

## 6.使用请求参数自定义响应

LinkRest提供了一种通过向请求添加特定参数来自定义响应的简便方法。这些可用于过滤、排序、分页或限制结果集的属性集。

### 6.1. 过滤

我们可以使用cayenneExp参数根据属性值过滤结果。顾名思义，这遵循[Cayenne表达式](https://cayenne.apache.org/docs/4.0/cayenne-guide/expressions.html)的格式。

让我们发送一个只返回名称为“IT”的部门的请求：

```plaintext
curl -i -X GET http://localhost:8080/linkrest/department?cayenneExp=name='IT'
```

### 6.2. 排序

为对一组结果进行排序而添加的参数是sort和dir。其中第一个指定要排序的属性，第二个指定排序的方向。

让我们看看按名称排序的所有部门：

```plaintext
curl -i -X GET "http://localhost:8080/linkrest/department?sort=name&dir=ASC"
```

### 6.3. 分页

该库通过添加start和limit参数来支持分页：

```plaintext
curl -i -X GET "http://localhost:8080/linkrest/department?start=0&limit=2
```

### 6.4. 选择属性

使用include和exclude参数，我们可以控制在结果中返回哪些属性或关系。

例如，让我们发送一个只显示部门名称的请求：

```plaintext
curl -i -X GET "http://localhost:8080/linkrest/department?include=name
```

为了只用名字显示部门的名字和员工，我们可以使用include属性两次：

```plaintext
curl -i -X GET "http://localhost:8080/linkrest/department?include=name&include=employees.name
```

## 七. 总结

在本文中，我们展示了如何使用LinkRest框架通过 REST 端点快速公开数据模型。