## 1. 概述

[Apache Maven]()是一个广泛使用的项目依赖管理工具和项目构建工具。

在过去的几年里，[Spring Boot]()已经成为一个非常流行的应用程序构建框架，还有[Spring Boot Maven插件](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/)在Apache Maven中提供Spring Boot支持。

我们知道当我们想要使用Maven将我们的应用程序打包到JAR或WAR工件中时，我们可以使用mvn package。但是，Spring Boot Maven插件附带了一个repackage的目标，它也在mvn命令中被调用。

有时，这两个mvn命令令人困惑。在本教程中，我们将讨论mvn package和spring-boot:repackage之间的区别。

## 2. 一个Spring Boot应用示例

首先，我们将创建一个简单的Spring Boot应用程序作为示例：

```java
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

为了验证我们的应用程序是否已启动并正在运行，让我们创建一个简单的REST端点：

```java
@RestController
public class DemoRestController {
    @GetMapping(value = "/welcome")
    public ResponseEntity welcomeEndpoint() {
        return ResponseEntity.ok("Welcome to Tuyucheng Spring Boot Demo!");
    }
}
```

## 3. Maven的package目标

我们只需要[spring-boot-starter-web](https://search.maven.org/search?q=a:spring-boot-starter-web g:org.springframework.boot)依赖项来构建我们的Spring Boot应用程序：

```xml
<artifactId>spring-boot-artifacts-2</artifactId>
<packaging>jar</packaging>
...
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
...
```

**Maven的package目标将采用编译后的代码并将其打包为可分发格式**，在本例中为JAR格式：

```bash
$ mvn package
[INFO] Scanning for projects...
[INFO] ------< cn.tuyucheng.taketoday.spring-boot-modules:spring-boot-artifacts-2 >------
[INFO] Building spring-boot-artifacts-2 1.0.0
[INFO] --------------------------------[ jar ]---------------------------------
 ... 
[INFO] --- maven-jar-plugin:2.4:jar (default-jar) @ spring-boot-artifacts-2 ---
[INFO] Building jar: /home/kent ... /target/spring-boot-artifacts-2.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
 ...
```

执行mvn package命令后，我们可以在target目录下找到构建好的jar文件spring-boot-artifacts-2.jar，让我们[检查一下创建的JAR文件的内容]()：

```bash
$ jar tf target/spring-boot-artifacts-2.jar
META-INF/
META-INF/MANIFEST.MF
cn/
cn/tuyucheng/taketoday/
cn/tuyucheng/taketoday/demo/
application.yml
cn/tuyucheng/taketoday/demo/DemoApplication.class
cn/tuyucheng/taketoday/demo/DemoRestController.class
META-INF/maven/...
```

正如我们在上面的输出中看到的，**由mvn package命令创建的JAR文件仅包含来自我们项目源的资源和已编译的Java类**。

我们可以将这个JAR文件用作另一个项目中的依赖项。但是，即使是Spring Boot应用程序，我们也无法使用java -jar JAR_FILE执行JAR文件，这是因为运行时依赖项没有绑定。例如，我们没有一个Servlet容器来启动Web上下文。

要使用简单的java -jar命令启动我们的Spring Boot应用程序，我们需要构建一个[Fat JAR]()，Spring Boot Maven插件可以帮助我们做到这一点。

## 4. Spring Boot Maven Plugin的repackage目标

现在，让我们弄清楚spring-boot:repackage的作用。

### 4.1 添加Spring Boot Maven插件

要执行repackage目标，我们需要在我们的pom.xml中添加Spring Boot Maven插件：

```xml
<build>
    <finalName>${project.artifactId}</finalName>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

### 4.2 执行spring-boot:repackage目标

现在，我们清理之前构建的JAR文件并尝试使用spring-boot:repackage：

```bash
$ mvn clean spring-boot:repackage     
 ...
[INFO] --- spring-boot-maven-plugin:2.3.3.RELEASE:repackage (default-cli) @ spring-boot-artifacts-2 ---
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
...
[ERROR] Failed to execute goal org.springframework.boot:spring-boot-maven-plugin:2.3.3.RELEASE:repackage (default-cli) 
on project spring-boot-artifacts-2: Execution default-cli of goal 
org.springframework.boot:spring-boot-maven-plugin:2.3.3.RELEASE:repackage failed: Source file must not be null -> [Help 1]
...
```

意外的是，构建出现错误。**这是因为spring -boot:repackage目标将现有的JAR或WAR存档作为源，并将最终工件中的所有项目运行时依赖项与项目类一起重新打包。这样，重新打包的工件就可以使用命令行java -jar JAR_FILE.jar执行**。

因此，我们需要在执行spring-boot:repackage目标之前先构建JAR文件：

```bash
$ mvn clean package spring-boot:repackage
 ...
[INFO] Building spring-boot-artifacts-2 1.0.0
[INFO] --------------------------------[ jar ]---------------------------------
 ...
[INFO] --- maven-jar-plugin:2.4:jar (default-jar) @ spring-boot-artifacts-2 ---
[INFO] Building jar: /home/kent/.../target/spring-boot-artifacts-2.jar
[INFO] 
[INFO] --- spring-boot-maven-plugin:2.3.3.RELEASE:repackage (default-cli) @ spring-boot-artifacts-2 ---
[INFO] Replacing main artifact with repackaged archive
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
 ...
```

### 4.3 重新打包的JAR文件的内容

现在，如果我们检查target目录，我们将看到重新打包的JAR文件和原始JAR文件：

```bash
$ ls -1 target/jar
target/spring-boot-artifacts-2.jar
target/spring-boot-artifacts-2.jar.original
```

让我们检查一下重新打包的JAR文件的内容：

```bash
$ jar tf target/spring-boot-artifacts-2.jar 
META-INF/
META-INF/MANIFEST.MF
 ...
org/springframework/boot/loader/JarLauncher.class
 ...
BOOT-INF/classes/cn/tuyucheng/taketoday/demo/
BOOT-INF/classes/application.yml
BOOT-INF/classes/cn/tuyucheng/taketoday/demo/DemoApplication.class
BOOT-INF/classes/cn/tuyucheng/taketoday/demo/DemoRestController.class
META-INF/maven/cn.tuyucheng.taketoday.spring-boot-modules/spring-boot-artifacts-2/pom.xml
META-INF/maven/cn.tuyucheng.taketoday.spring-boot-modules/spring-boot-artifacts-2/pom.properties
BOOT-INF/lib/
BOOT-INF/lib/spring-boot-starter-web-2.3.3.RELEASE.jar
...
BOOT-INF/lib/spring-boot-starter-tomcat-2.3.3.RELEASE.jar
BOOT-INF/lib/tomcat-embed-core-9.0.37.jar
BOOT-INF/lib/jakarta.el-3.0.3.jar
BOOT-INF/lib/tomcat-embed-websocket-9.0.37.jar
BOOT-INF/lib/spring-web-5.2.8.RELEASE.jar
...
BOOT-INF/lib/httpclient-4.5.12.jar
...
```

如果我们检查上面的输出，它比mvn package命令构建的JAR文件要长得多。

在这里，**在重新打包的JAR文件中，我们不仅拥有项目中已编译的Java类，还拥有启动Spring Boot应用程序所需的所有运行时库**，比如一个内嵌的tomcat库被打包到BOOT-INF/lib目录下。

接下来，让我们启动我们的应用程序并检查它是否有效：

```bash
$ java -jar target/spring-boot-artifacts-2.jar 
  .   ____          _            __ _ _
 / / ___'_ __ _ _(_)_ __  __ _    
( ( )___ | '_ | '_| | '_ / _` |    
 /  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |___, | / / / /
 =========|_|==============|___/=/_/_/_/

2022-12-20 14:36:32.704  INFO 115154 [main] cn.tuyucheng.taketoday.demo.DemoApplication      : Starting DemoApplication on YK-Arch with PID 11515...
...
2022-12-20 14:36:34.070  INFO 115154 [main] o.s.b.w.embedded.tomcat.TomcatWebServer: Tomcat started on port(s): 8080 (http) ...
2022-12-20 14:36:34.078  INFO 115154 [main] cn.tuyucheng.taketoday.demo.DemoApplication      : Started DemoApplication in 1.766 seconds ...
```

我们的Spring Boot应用程序已启动并正在运行，现在通过调用我们的/welcome端点来验证它：

```bash
$ curl http://localhost:8080/welcome
Welcome to Tuyucheng Spring Boot Demo!
```

我们得到了预期的响应，表明我们的应用程序运行正常。

### 4.4 在Maven的package生命周期中执行spring-boot:repackage目标

我们可以在pom.xml中配置Spring Boot Maven插件，以便在Maven生命周期的打包阶段重新打包工件。也就是说，当我们执行mvn package的时候，spring-boot:repackage会自动执行。

配置非常简单，我们只需将repackage目标添加到<execution>标签中：

```xml
<build>
    <finalName>${project.artifactId}</finalName>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <executions>
                <execution>
                    <goals>
                        <goal>repackage</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

现在，让我们再次运行mvn clean package：

```bash
$ mvn clean package
 ...
[INFO] Building spring-boot-artifacts-2 1.0.0
[INFO] --------------------------------[ jar ]---------------------------------
...
[INFO] --- spring-boot-maven-plugin:2.3.3.RELEASE:repackage (default) @ spring-boot-artifacts-2 ---
[INFO] Replacing main artifact with repackaged archive
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
 ...
```

输出显示repackage目标已执行，如果我们检查文件系统，我们会发现重新打包的JAR文件已创建：

```bash
$ ls -lh target/jar
-rw-r--r-- 1 kent kent  29M Dec 22 14:56 target/spring-boot-artifacts-2.jar
-rw-r--r-- 1 kent kent 3.6K Dec 22 14:56 target/spring-boot-artifacts-2.jar.original

```

## 5. 总结

在本文中，我们讨论了mvn package和spring-boot:repackage之间的区别。此外，我们还学习了如何在Maven生命周期的打包阶段执行spring-boot:repackage。