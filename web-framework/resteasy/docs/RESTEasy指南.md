## 1. 简介

JAX-RS(用于 RESTful Web 服务的JavaAPI)是一组JavaAPI，为创建REST API提供支持。并且框架很好地利用了注解来简化这些API的开发和部署。

在本教程中，我们将使用 JBoss 提供的 JAX-RS 规范的可移植实现 RESTEasy，以创建简单的 RESTful Web 服务。

## 2.项目设置

我们将考虑两种可能的情况：

-   独立设置 - 用于在每个应用程序服务器上工作
-   JBoss AS 设置——仅考虑在 JBoss AS 中部署

### 2.1. 独立设置

让我们从使用独立设置的JBoss WildFly 10开始。

JBoss WildFly 10 附带 RESTEasy 版本 3.0.11，但如你所见，我们将使用新的 3.0.14 版本配置pom.xml 。

多亏了resteasy-servlet-initializer，RESTEasy通过ServletContainerInitializer集成接口提供了与独立Servlet 3.0容器的集成。

让我们看看pom.xml：


```xml
<properties>
    <resteasy.version>3.0.14.Final</resteasy.version>
</properties>
<dependencies>
    <dependency>
        <groupId>org.jboss.resteasy</groupId>
        <artifactId>resteasy-servlet-initializer</artifactId>
        <version>${resteasy.version}</version>
    </dependency>
    <dependency>
        <groupId>org.jboss.resteasy</groupId>
        <artifactId>resteasy-client</artifactId>
        <version>${resteasy.version}</version>
    </dependency>
</dependencies>

```

jboss-deployment-structure.xml

在 JBoss 中，所有部署为 WAR、JAR 或 EAR 的东西都是一个模块。这些模块称为动态模块。

除此之外， $JBOSS_HOME/modules中还有一些静态模块。由于 JBoss 具有 RESTEasy静态模块——对于独立部署，jboss-deployment-structure.xml是必需的，以便排除其中的一些模块。

这样，我们的WAR中包含的所有类和JAR文件都将被加载：

```xml
<jboss-deployment-structure>
    <deployment>
        <exclude-subsystems>
            <subsystem name="resteasy" />
        </exclude-subsystems>
        <exclusions>
            <module name="javaee.api" />
            <module name="javax.ws.rs.api"/>
            <module name="org.jboss.resteasy.resteasy-jaxrs" />
        </exclusions>
        <local-last value="true" />
    </deployment>
</jboss-deployment-structure>
```

### 2.2. JBoss 作为设置

如果你要使用 JBoss 版本 6 或更高版本运行 RESTEasy，你可以选择采用应用服务器中已经捆绑的库，从而简化 pom：

```xml
<dependencies>
    <dependency>
        <groupId>org.jboss.resteasy</groupId>
        <artifactId>resteasy-jaxrs</artifactId>
        <version>${resteasy.version}</version>
    </dependency>
<dependencies>

```

请注意，不再需要jboss-deployment-structure.xml 。

## 3.服务器端代码

### 3.1. Servlet 版本 3 web.xml

现在让我们快速浏览一下我们简单项目的 web.xml：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="3.0" xmlns="http://java.sun.com/xml/ns/javaee"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xsi:schemaLocation="http://java.sun.com/xml/ns/javaee 
     http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd">

   <display-name>RestEasy Example</display-name>

   <context-param>
      <param-name>resteasy.servlet.mapping.prefix</param-name>
      <param-value>/rest</param-value>
   </context-param>

</web-app>
```

resteasy.servlet.mapping.prefix仅当你想要在 API 应用程序的前面添加相对路径时才需要。

在这一点上，非常重要的是要注意我们没有在web.xml中声明任何Servlet，因为resteasy servlet-initializer已作为依赖项添加到pom.xml中。原因是——RESTEasy 提供了org.jboss.resteasy.plugins.servlet.ResteasyServletInitializer类，它实现了javax.server.ServletContainerInitializer。

ServletContainerInitializer是一个初始化程序，它在任何 servlet 上下文准备就绪之前执行——你可以使用此初始化程序为你的应用程序定义 servlet、过滤器或侦听器。

### 3.2. 应用类

javax.ws.rs.core.Application类是一个标准的 JAX-RS 类，你可以实现它以提供有关你的部署的信息：

```java
@ApplicationPath("/rest")
public class RestEasyServices extends Application {

    private Set<Object> singletons = new HashSet<Object>();

    public RestEasyServices() {
        singletons.add(new MovieCrudService());
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
```

如你所见——这只是一个列出所有 JAX-RS 根资源和提供程序的类，并使用@ApplicationPath注解对其进行了注解。

如果你为类和单例返回任何空集，则将扫描 WAR 以查找 JAX-RS 注解资源和提供程序类。

### 3.3. 服务实现类

最后，让我们在这里看一个实际的 API 定义：

```java
@Path("/movies")
public class MovieCrudService {

    private Map<String, Movie> inventory = new HashMap<String, Movie>();

    @GET
    @Path("/getinfo")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Movie movieByImdbId(@QueryParam("imdbId") String imdbId) {
        if (inventory.containsKey(imdbId)) {
            return inventory.get(imdbId);
        } else {
            return null;
        }
    }

    @POST
    @Path("/addmovie")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response addMovie(Movie movie) {
        if (null != inventory.get(movie.getImdbId())) {
            return Response
              .status(Response.Status.NOT_MODIFIED)
              .entity("Movie is Already in the database.").build();
        }

        inventory.put(movie.getImdbId(), movie);
        return Response.status(Response.Status.CREATED).build();
    }
}
```

## 4. 总结

在本快速教程中，我们介绍了 RESTEasy 并使用它构建了一个超级简单的 API。