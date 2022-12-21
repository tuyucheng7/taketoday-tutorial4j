## 1. 概述

在本快速教程中，我们将演示**如何使用Spring Boot创建多模块项目**。

首先，我们将构建一个本身不是应用程序的库jar，然后我们将构建一个使用我们的库的应用程序。

有关Spring Boot的介绍，请参阅[本文]()。

## 2. 设置

**为了设置我们的多模块项目，我们使用pom打包创建一个简单的模块，以在我们的Maven配置中聚合我们的库和应用程序模块**：

```xml
<groupId>cn.tuyucheng.taketoday</groupId>
<artifactId>parent-multi-module</artifactId>
<packaging>pom</packaging>
```

我们将在我们的项目中创建两个目录，将应用程序模块与库jar模块分开。

下面我们在pom.xml中声明我们的模块：

```xml
<modules>
    <module>library</module>
    <module>application</module>
</modules>
```

## 3. 库模块

对于我们的库模块，我们将使用jar打包：

```xml
<groupId>cn.tuyucheng.taketoday.example</groupId>
<artifactId>library</artifactId>
<packaging>jar</packaging>
```

由于我们想利用Spring Boot依赖管理，我们将使用spring-boot-starter-parent作为父项目，**注意将<relativePath/\>设置为空值，以便Maven从仓库解析父pom.xml**：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.2.6.RELEASE</version>
    <relativePath/>
</parent>
```

请注意，**如果我们有自己的父项目，我们可以在pom.xml的<dependencyManagement/\>部分中将依赖管理作为物料清单(BOM)导入**：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <type>pom</type>
            <version>2.4.0</version>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

最后，初始依赖关系将非常简单：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
	    <artifactId>spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

在这个模块中，**Spring Boot插件不是必需的，因为它的主要功能是创建一个可执行文件jar**，我们不想要也不需要库。

之后，我们准备开发一个由库模块提供的服务组件：

```java
@Service
public class EvenOddService {

    public String isEvenOrOdd(Integer number) {
        return number % 2 == 0 ? "Even" : "Odd";
    }
}
```

## 4. 应用程序模块

和我们的库模块一样，我们的应用模块也使用jar打包：

```xml
<groupId>cn.tuyucheng.taketoday.example</groupId>
<artifactId>application</artifactId>
<packaging>jar</packaging>
```

我们将像以前一样利用Spring Boot依赖管理：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.2.6.RELEASE</version>
    <relativePath/>
</parent>
```

除了Spring Boot启动器依赖项之外，我们还将**包含在上一节中创建的库jar**：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>cn.tuyucheng.taketoday.example</groupId>
        <artifactId>library</artifactId>
        <version>${project.version}</version>
    </dependency>
</dependencies>
```

最后，我们将使用Spring Boot插件：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

在这个地方使用上面提到的插件有几个方便的原因。

首先，它提供了一个内置的依赖解析器，可以设置版本号来匹配Spring Boot依赖关系。其次，它搜索要标记为可运行类的main方法。最后，也许也是最重要的，它收集了类路径上的所有jar并构建了一个可运行的jar。

现在一切准备就绪，可以编写我们的应用程序类并直奔主题，让我们在主应用程序类中实现一个控制器：

```java
@SpringBootApplication(scanBasePackages = "cn.tuyucheng.taketoday")
@RestController
public class EvenOddApplication {

    private EvenOddService evenOddService;

    // constructor

    @GetMapping("/validate/")
    public String isEvenOrOdd(
          @RequestParam("number") Integer number) {
        return evenOddService.isEvenOrOdd(number);
    }

    public static void main(String[] args) {
        SpringApplication.run(EvenOddApplication.class, args);
    }
}
```

## 5. 总结

在本文中，我们探讨了如何使用Spring Boot实现和配置多模块项目并自行构建库jar。