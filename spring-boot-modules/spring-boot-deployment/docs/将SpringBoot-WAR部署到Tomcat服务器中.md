## 1. 简介

[Spring Boot](https://projects.spring.io/spring-boot/)是一种[约定优于配置](https://en.wikipedia.org/wiki/Convention_over_configuration)框架，它允许我们创建Spring项目的生产就绪设置，而[Tomcat](https://tomcat.apache.org/)是最流行的Java Servlet容器之一。

默认情况下，Spring Boot构建一个独立的Java应用程序，该应用程序可以作为桌面应用程序运行或配置为系统服务，但在某些环境中我们无法安装新服务或手动运行应用程序。

与独立应用程序不同，Tomcat作为服务安装，可以在同一个应用程序进程中管理多个应用程序，从而避免了为每个应用程序进行特定设置的需要。

在本教程中，我们将创建一个简单的Spring Boot应用程序并使其适应在Tomcat中工作。

## 2. 设置Spring Boot应用程序

我们使用可用的Starter模板之一设置一个简单的Spring Boot Web应用程序：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId> 
    <version>2.4.0</version> 
    <relativePath/> 
</parent> 
<dependencies>
    <dependency> 
        <groupId>org.springframework.boot</groupId> 
        <artifactId>spring-boot-starter-web</artifactId> 
    </dependency> 
</dependencies>
```

**除了标准的@SpringBootApplication之外，不需要额外的配置，因为Spring Boot会处理默认设置**。

然后我们将添加一个简单的REST端点来为我们返回一些有效的内容：

```java
@RestController
public class TomcatController {

    @GetMapping("/hello")
    public Collection<String> sayHello() {
        return IntStream.range(0, 10)
              .mapToObj(i -> "Hello number " + i)
              .collect(Collectors.toList());
    }
}
```

最后，我们将使用mvn spring-boot:run执行应用程序，并在浏览器中访问http://localhost:8080/hello检查结果。

## 3. 创建Spring Boot WAR

Servlet容器期望应用程序满足要部署的某些协定，对于Tomcat，该协定是[Servlet API 3.0](https://tomcat.apache.org/tomcat-8.0-doc/servletapi/index.html)。

为了让我们的应用程序满足这个契约，我们必须对源代码进行一些小的修改。

首先，我们需要打包一个WAR应用程序而不是JAR，为此，我们将更改pom.xml的以下内容：

```xml
<packaging>war</packaging>
```

接下来，我们将修改最终的WAR文件名以避免包含版本号：

```xml
<build>
    <finalName>${artifactId}</finalName>
    ... 
</build>
```

然后我们将添加Tomcat依赖项：

```xml
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-tomcat</artifactId>
   <scope>provided</scope>
</dependency>
```

最后，我们通过实现SpringBootServletInitializer接口来初始化Tomcat所需的Servlet上下文：

```java
@SpringBootApplication
public class SpringBootTomcatApplication extends SpringBootServletInitializer {
}
```

为了构建我们的Tomcat可部署WAR应用程序，我们将执行mvn clean package。之后，我们的WAR文件在target/spring-boot-deployment.war生成(假设Maven artifactId是“spring-boot-deployment”)。

我们应该考虑到这个新设置使我们的Spring Boot应用程序成为一个非独立应用程序(如果我们想让它再次以独立模式工作，我们可以从tomcat依赖项中删除provided范围)。

## 4. 部署WAR到Tomcat

要在Tomcat中部署和运行我们的WAR文件，我们需要完成以下步骤：

1.  [下载Apache Tomcat](https://tomcat.apache.org/download-90.cgi)并解压到tomcat文件夹中

2.  将我们的WAR文件从target/spring-boot-deployment.war复制到tomcat/webapps/文件夹

3.  从终端导航到tomcat/bin文件夹并执行

    1.  catalina.bat run(在Windows上)
    2.  catalina.sh run(在基于Unix的系统上)

4.  转到http://localhost:8080/spring-boot-deployment/hello

这是一个快速的Tomcat设置，所以请查看[Tomcat安装]()指南以获得完整的设置指南。还有其他方法可以[将WAR文件部署到Tomcat]()。

##  5. 总结

在这篇简短的文章中，我们创建了一个简单的Spring Boot应用程序，并将其转换为可部署在Tomcat服务器上的有效WAR应用程序。