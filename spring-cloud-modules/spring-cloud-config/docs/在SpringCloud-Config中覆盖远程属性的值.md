## 1. 概述

[Spring Cloud Config](https://www.baeldung.com/spring-cloud-configuration)是Spring Cloud伞式项目的一部分。**它通过集中式服务管理应用程序配置数据**，因此它与你部署的微服务明显分开。Spring Cloud Config有自己的属性管理仓库，但也与Git、Consul和Eureka等开源项目集成。

在本文中，我们将看到在Spring Cloud Config中覆盖远程属性值的不同方法、Spring从2.4版本开始施加的限制以及3.0版本带来的变化。对于本教程，我们将使用Spring Boot版本2.7.2。

## 2. 创建Spring配置服务器

要外部化配置文件，让我们使用Spring Cloud Config创建我们的边缘服务。**它被定位为配置文件的分发服务器**。在本文中，我们将使用文件系统仓库。

### 2.1 创建配置文件

application.properties文件中定义的配置与所有客户端应用程序共享。还可以为应用程序或给定Profile定义特定配置。

让我们从创建包含服务于我们的客户端应用程序的属性的配置文件开始。我们将我们的客户端应用程序命名为“tuyucheng”。在/resources/config文件夹中，让我们创建一个tuyucheng.properties文件。

### 2.2 添加属性

让我们将一些属性添加到我们的tuyucheng.properties文件中，然后我们将在我们的客户端应用程序中使用它们：

```properties
hello=Hello Jane Doe!
welcome=Welcome Jane Doe!
```

我们还可以在resources/config/application.properties文件中添加一个共享属性，Spring将在所有客户端之间共享该属性：

```properties
shared-property=This property is shared accross all client applications
```

### 2.3 Spring Boot配置服务器应用程序

现在，让我们创建将为我们的配置提供服务的Spring应用程序。我们还需要[spring-cloud-config-server](https://search.maven.org/search?q=spring-cloud-config-server)依赖项：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

安装完成后，让我们创建应用程序并启用配置服务器：

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServer {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServer.class, args);
    }
}
```

让我们在应用程序的application.properties文件中添加以下属性，以告诉它在端口8081上启动并加载先前定义的配置：

```properties
server.port=8081
spring.cloud.config.server.native.searchLocations=classpath:/config
```

现在，让我们启动我们的服务器应用程序，激活native Profile并允许我们将文件系统用作配置仓库：

```shell
mvn spring-boot:run -Drun.profiles=native
```

我们的服务器现在正在运行并为我们的配置提供服务。让我们验证一下我们的共享属性是否可以访问：

```shell
$ curl localhost:8081/unknownclient/default
{
    "name": "unknownclient",
    "profiles": [
        "default"
    ],
    "label": null,
    "version": null,
    "state": null,
    "propertySources": [
        {
            "name": "classpath:/config/application.properties",
            "source": {
                "shared-property": "This property is shared accross all client applications"
            }
        }
    ]
}
```

以及特定于我们的应用程序的属性：

```shell
$ curl localhost:8081/tuyucheng/default
{
    "name": "tuyucheng",
    "profiles": [
        "default"
    ],
    "label": null,
    "version": null,
    "state": null,
    "propertySources": [
        {
            "name": "classpath:/config/tuyucheng.properties",
            "source": {
                "hello": "Hello Jane Doe!",
                "welcome": "Welcome Jane Doe!"
            }
        },
        {
            "name": "classpath:/config/application.properties",
            "source": {
                "shared-property": "This property is shared accross all client applications"
            }
        }
    ]
}
```

我们向服务器指示我们的应用程序的名称以及使用的profile default。

我们没有禁用spring.cloud.config.server.accept-empty属性，它默认为true。如果应用程序是未知的(unknownclient)，则配置服务器无论如何都会返回共享属性。

## 3. 客户端应用程序

现在让我们创建一个客户端应用程序，在启动时加载由我们的服务器提供的配置。

### 3.1 项目设置和依赖

让我们添加[spring-cloud-starter-config](https://search.maven.org/search?q=spring-cloud-starter-config)依赖项来加载配置和[spring-boot-starter-web](https://search.maven.org/search?q=spring-boot-starter-web)以在我们的pom.xml中创建控制器：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### 3.2 创建客户端应用程序

接下来，让我们创建将从我们的Spring Cloud配置服务器读取配置的客户端应用程序：

```java
@SpringBootApplication
public class Client {
    public static void main(String[] args) {
        SpringApplication.run(Client.class, args);
    }
}
```

### 3.3 获取配置

让我们修改我们的application.properties文件来告诉Spring Boot从我们的服务器获取它的配置：

```properties
spring.cloud.config.name=tuyucheng
spring.config.import=optional:configserver:http://localhost:8081
```

我们还指定我们的应用程序的名称是“tuyucheng”以利用专用属性。

### 3.4 添加一个简单的控制器

现在让我们创建一个控制器，负责显示我们配置的特定属性，以及共享属性：

```java
@RestController
public class HelloController {

    @Value("${hello}")
    private String hello;

    @Value("${welcome}")
    private String welcome;

    @Value("${shared-property}")
    private String shared;

    @GetMapping("hello")
    public String hello() {
        return this.hello;
    }

    @GetMapping("welcome")
    public String welcome() {
        return this.welcome;
    }

    @GetMapping("shared")
    public String shared() {
        return this.shared;
    }
}
```

现在我们可以导航到这三个URL来验证我们的配置是否被考虑在内：

```shell
$ curl http://localhost:8080/hello
Hello Jane Doe!
$ curl http://localhost:8080/welcome
Welcome Jane Doe!
$ curl http://localhost:8080/shared
This property is shared accross all client applications
```

## 4. 覆盖服务器端的属性

可以通过修改服务器配置来覆盖为给定应用程序定义的属性。

让我们编辑我们服务器的resources/application.properties文件来覆盖hello属性：

```properties
spring.cloud.config.server.overrides.hello=Hello Jane Doe – application.properties!
```

让我们再次测试对/hello控制器的调用，以验证是否考虑了覆盖：

```shell
$ curl http://localhost:8080/hello
Hello Jane Doe - application.properties!
```

可以在resources/config/application.properties文件中的共享配置级别添加此重载。**在这种情况下，它将优先于上面定义的那个**。

## 5. 覆盖客户端的属性

从Spring Boot 2.4版本开始，不再可能通过客户端应用程序的application.properties文件覆盖属性。

### 5.1 使用Spring Profile

但是，我们可以使用[Spring Profile](https://www.baeldung.com/spring-profiles)。**在Profile中本地定义的属性比在服务器级别为应用程序定义的优先级具有更高的优先级**。

让我们向我们的客户端应用程序添加一个覆盖了hello属性的application-development.properties配置文件：

```properties
hello=Hello local property!
```

现在，让我们通过激活development Profile来启动我们的客户端：

```shell
mvn spring-boot:run -Drun.profiles=development
```

我们现在可以再次测试我们的控制器/hello以验证重载是否正常工作：

```shell
$ curl http://localhost:8080/hello
Hello local property!
```

### 5.2 使用占位符

我们可以使用占位符来评估属性。**因此，服务器将提供一个默认值，该值可以被客户端定义的属性覆盖**。

让我们从服务器的resources/application.properties文件中删除hello属性的重载，并修改config/tuyucheng.properties中的那个以合并占位符的使用：

```properties
hello=${app.hello:Hello Jane Doe!}
```

因此，服务器提供了一个默认值，如果客户端声明名为app.hello的属性，则可以覆盖该值。

让我们编辑我们客户端的resources/application.properties文件来添加属性：

```properties
app.hello=Hello, overriden local property!
```

让我们再次测试我们的hello端点以验证是否正确考虑了重载：

```shell
$ curl http://localhost:8080/hello
Hello, overriden local property!
```

请注意，如果在Profile配置中也定义了hello属性，则后者优先。

## 6. 遗留配置

**从Spring Boot 2.4开始可以使用“遗留配置”。这允许我们在Spring Boot 2.4版本所做的更改之前使用旧的属性管理系统**。

### 6.1 加载外部配置

在2.4版本之前，外部配置的管理由bootstrap确保。我们需要[spring-cloud-starter-bootstrap](https://search.maven.org/search?q=spring-cloud-starter-bootstrap)依赖项。让我们将其添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
</dependency>
```

接下来，我们将在资源文件夹中创建一个bootstrap.properties文件并配置对我们服务器的访问URL：

```properties
spring.cloud.config.name=tuyucheng
spring.cloud.config.uri=http://localhost:8081
```

让我们在application.properties中启用遗留配置：

```properties
spring.config.use-legacy-processing=true
```

### 6.2 启用覆盖功能

在服务器端，我们需要指出属性重载是可能的。让我们以这种方式修改我们的tuyucheng.properties文件：

```properties
spring.cloud.config.overrideNone=true
```

因此，外部属性不会优先于应用程序JAR中定义的那些。

### 6.3 覆盖服务器的属性

我们现在可以通过application.properties文件覆盖客户端应用程序中的hello属性：

```properties
hello=localproperty
```

让我们测试对控制器的调用：

```shell
$ curl http://localhost:8080/hello
localproperty
```

### 6.4 遗留配置过时

**自Spring Boot版本3.0以来，不再可能启用旧版配置**。在这种情况下，我们应该使用上面提出的其他方法。

## 7. 总结

在本文中，**我们看到了在Spring Cloud Config中覆盖远程属性值的不同方法**。

可以从服务器覆盖为给定应用程序定义的那些属性。也可以使用Profile或占位符在客户端级别覆盖属性。我们还研究了如何激活旧配置以返回到旧的属性管理系统。