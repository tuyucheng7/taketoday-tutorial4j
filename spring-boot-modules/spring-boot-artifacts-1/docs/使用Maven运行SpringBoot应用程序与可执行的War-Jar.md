## 1. 简介

在本教程中，我们将探讨通过mvn spring-boot:run命令启动Spring Boot Web应用程序与通过java -jar命令编译成jar/war包后运行它之间的区别。

出于本教程的目的，我们假设你熟悉Spring Boot repackage目标的配置。有关此主题的更多详细信息，请阅读[使用Spring Boot创建Fat Jar应用程序](使用SpringBoot创建一个FatJar应用程序.md)。

## 2. Spring Boot Maven插件

**在编写Spring Boot应用程序时，建议使用[Spring Boot Maven插件](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/)来构建、测试和打包我们的代码**。

该插件附带了许多方便的功能，例如：

-   它为我们解析了正确的依赖版本
-   它可以将我们所有的依赖项(如果需要，包括嵌入式应用程序服务器)打包到一个可运行的fat jar/war中，并且还可以：
    -   为我们管理类路径配置，因此我们可以跳过java -jar命令中的长-cp参数
    -   实现一个自定义的ClassLoader来定位和加载现在嵌套在包中的所有外部jar库
    -   自动找到main()方法并在Manifest中配置它，因此我们不必在java -jar命令中指定主类

## 3. 使用分解形式的Maven运行代码

当我们在开发Web应用程序时，我们可以利用Spring Boot Maven插件的另一个非常有趣的功能：在嵌入式应用程序服务器中自动部署我们的Web应用程序的能力。

我们只需要一个依赖项就可以让插件知道我们要使用Tomcat来运行我们的代码：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId> 
</dependency>
```

现在，当我们在项目根文件夹中执行mvn spring-boot:run命令时，插件会读取pom配置并了解我们需要一个Web应用程序容器。

**执行mvn spring-boot:run命令会触发Apache Tomcat的下载并初始化Tomcat的启动**：

```bash
$ mvn spring-boot:run
...
...
[INFO] --------------------< cn.tuyucheng.taketoday:spring-boot-ops >--------------------
[INFO] Building spring-boot-ops 1.0.0
[INFO] --------------------------------[ war ]---------------------------------
[INFO]
[INFO] >>> spring-boot-maven-plugin:2.1.3.RELEASE:run (default-cli) > test-compile @ spring-boot-ops >>>
Downloading from central: https://repo.maven.apache.org/maven2/org/apache/tomcat/embed/tomcat-embed-core/9.0.16/tomcat-embed-core-9.0.16.pom
Downloaded from central: https://repo.maven.apache.org/maven2/org/apache/tomcat/embed/tomcat-embed-core/9.0.16/tomcat-embed-core-9.0.16.pom (1.8 kB at 2.8 kB/s)
...
...
[INFO] --- spring-boot-maven-plugin:2.1.3.RELEASE:run (default-cli) @ spring-boot-ops ---
...
...
11:33:36.648 [main] INFO  o.a.catalina.core.StandardService - Starting service [Tomcat]
11:33:36.649 [main] INFO  o.a.catalina.core.StandardEngine - Starting Servlet engine: [Apache Tomcat/9.0.16]
...
...
11:33:36.952 [main] INFO  o.a.c.c.C.[Tomcat].[localhost].[/] - Initializing Spring embedded WebApplicationContext
...
...
11:33:48.223 [main] INFO  o.a.coyote.http11.Http11NioProtocol - Starting ProtocolHandler ["http-nio-8080"]
11:33:48.289 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat started on port(s): 8080 (http) with context path ''
11:33:48.292 [main] INFO  cn.tuyucheng.taketoday.boot.Application - Started Application in 22.454 seconds (JVM running for 37.692)
```

当日志显示包含“Started Application”的行时，我们的Web应用程序已准备好通过地址为 http://localhost:8080/ 的浏览器进行查询

## 4. 将代码作为独立的打包应用程序运行

**一旦我们通过了开发阶段并朝着将我们的应用程序投入生产的方向前进，我们就需要打包我们的应用程序**。

不幸的是，如果我们使用jar包，则基本的Maven package目标不包括任何外部依赖项。这意味着我们只能将其用作更大项目中的库。

**为了规避这个限制，我们需要利用Maven Spring Boot插件repackage目标来将我们的jar/war作为独立应用程序运行**。

### 4.1 配置

通常，我们只需要配置构建插件：

```xml
<build>
    <plugins>
        ...
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
        ...
    </plugins>
</build>
```

由于我们的示例项目包含多个主类，因此我们必须通过配置插件来告诉Java要运行哪个类：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <executions>
        <execution>
            <configuration>
                <mainClass>cn.tuyucheng.taketoday.webjar.WebjarsdemoApplication</mainClass>
            </configuration>
        </execution>
    </executions>
</plugin>
```

或者设置start-class属性：

```xml

<properties>
    <start-class>cn.tuyucheng.taketoday.webjar.WebjarsdemoApplication</start-class>
</properties>
```

### 4.2 运行应用程序

现在我们可以使用两个简单的命令来运行示例war：

```bash
$ mvn clean package spring-boot:repackage
$ java -jar target/spring-boot-ops.war
```

有关如何运行jar文件的更多详细信息，请参阅我们的文章[使用命令行参数运行JAR应用程序]()。

### 4.3 War文件内部

**为了更好地理解上述命令如何运行完整的服务器应用程序，我们可以查看我们的spring-boot-ops.war**。

如果我们解压缩它并查看内部，我们会发现常见的嫌疑人：

-   META-INF，带有自动生成的MANIFEST.MF
-   WEB-INF/classes，包含我们编译的类
-   WEB-INF/lib，其中包含我们的war依赖项和嵌入式Tomcat jar文件

这还不是全部，因为有一些特定于我们的胖包配置的文件夹：

-   WEB-INF/lib-provided，包含嵌入式运行时需要的外部库，但部署时不需要
-   org/springframework/boot/loader，其中包含Spring Boot自定义类加载器，该库负责加载我们的外部依赖项并使它们在运行时可访问。

### 4.4 War Manifest内部

如前所述，Maven Spring Boot插件会找到主类并生成运行java命令所需的配置。

生成的MANIFEST.MF有一些额外的行：

```manifest
Start-Class: cn.tuyucheng.taketoday.webjar.WebjarsdemoApplication
Main-Class: org.springframework.boot.loader.WarLauncher
```

特别是，我们可以观察到Main-Class指定了要使用的Spring Boot类加载器启动器。

### 4.5 Jar文件内部

由于默认的打包策略，我们的war打包场景并没有太大区别，无论我们是否使用Spring Boot Maven Plugin。

为了更好的体会插件的优势，我们可以尝试将pom打包配置改成jar，再运行mvn clean package。

我们现在可以观察到我们的fat jar的组织方式与我们之前的war文件略有不同：

-   我们所有的类和资源文件夹现在都位于BOOT-INF/classes下
-   BOOT-INF/lib包含所有外部库

**如果没有插件，lib文件夹将不存在，并且BOOT-INF/classes的所有内容将位于包的根目录中**。

### 4.6 Jar Manifest内部

MANIFEST.MF也发生了变化，具有以下附加行：

```manifest
Spring-Boot-Classes: BOOT-INF/classes/
Spring-Boot-Lib: BOOT-INF/lib/
Spring-Boot-Version: 2.1.3.RELEASE
Main-Class: org.springframework.boot.loader.JarLauncher
```

Spring-Boot-Classes和Spring-Boot-Lib特别有趣，因为它们告诉我们类加载器将在哪里找到类和外部库。

## 5. 如何选择

**在分析工具时，我们必须考虑创建这些工具的目的**。我们是要简化开发，还是要确保顺利部署和可移植性？让我们来看看受此选择影响最大的阶段。

### 5.1 开发

作为开发人员，我们经常将大部分时间花在编码上，而不需要花费大量时间来设置我们的环境以在本地运行代码。在简单的应用程序中，这通常不是问题。但对于更复杂的项目，我们可能需要设置环境变量、启动服务器和填充数据库。

**每次我们想要运行应用程序时都配置正确的环境是非常不切实际的**，尤其是在必须同时运行多个服务的情况下。

这就是使用Maven运行代码对我们有帮助的地方。我们已经在本地检查了整个代码库，因此我们可以利用pom配置和资源文件。我们可以设置环境变量，生成内存数据库，甚至可以下载正确的服务器版本并使用一个命令部署我们的应用程序。

即使在多模块代码库中，每个模块都需要不同的变量和服务器版本，我们也可以通过Maven Profile轻松运行正确的环境。

### 5.2 生产

我们越是转向生产，话题就越转向稳定性和安全性，这就是为什么我们不能将用于我们的开发机器的过程应用到有真实客户的服务器上。

**出于多种原因，在此阶段通过Maven运行代码是不好的做法**：

-   首先，我们需要安装Maven。
-   然后，仅仅因为我们需要编译代码，我们就需要完整的Java Development Kit(JDK)。
-   接下来，我们必须将代码库复制到我们的服务器，以纯文本形式保留我们所有的专有代码。
-   mvn命令必须执行生命周期的所有阶段(查找源、编译和运行)。
-   由于前一点，我们还会浪费CPU，如果是云服务器，还会浪费金钱。
-   Maven生成多个Java进程，每个进程使用内存(默认情况下，每个进程使用与父进程相同的内存量)。
-   最后，如果我们要部署多台服务器，则在每台服务器上重复上述所有操作。

这些只是为什么将应用程序作为一个包来发送对生产更实用的几个原因。

## 6. 总结

在本文中，我们探讨了通过Maven和通过java -jar命令运行代码之间的区别，并快速介绍了一些实际案例场景。