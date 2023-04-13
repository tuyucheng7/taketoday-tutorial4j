## 1. 概述

Spring Boot使我们能够快速设置和运行服务。

为了进一步提升开发体验，Spring发布了spring-boot-devtools工具-作为Spring Boot 1.3的一部分。本文将尝试介绍使用新功能可以实现的好处。

我们将涵盖以下主题：

-   属性默认值
-   自动重启
-   实时重新加载
-   全局设置
-   远程应用程序

### 1.1 在项目中添加Spring-Boot-Devtools

在项目中添加spring-boot-devtools就像添加任何其他Spring Boot模块一样简单。在现有的Spring Boot项目中，添加以下依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
</dependency>
```

对项目进行干净的构建，你现在已与spring-boot-devtools集成。最新版本可以从[这里](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-devtools/3.0.5)获取，所有版本都可以在[这里](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-devtools/3.0.5)找到。

## 2. 属性默认值

Spring Boot做了很多自动配置，包括默认启用缓存以提高性能。一个这样的例子是缓存模板引擎使用的模板，例如Thymeleaf。但在开发过程中，更重要的是尽快看到变化。

可以使用application.properties文件中的属性spring.thymeleaf.cache=false为Thymeleaf禁用缓存的默认行为。我们不需要手动执行此操作，引入这个spring-boot-devtools会自动为我们执行此操作。

## 3. 自动重启

在典型的应用程序开发环境中，开发人员会进行一些更改、构建项目并部署/启动应用程序以使新更改生效，或者尝试利用JRebel等。

使用spring-boot-devtools，这个过程也是自动化的。每当类路径中的文件更改时，使用spring-boot-devtools的应用程序将导致应用程序重新启动。此功能的好处是大大减少了验证所做更改所需的时间：

```shell
19:45:44.804 ... - Included patterns for restart : []
19:45:44.809 ... - Excluded patterns for restart : [/spring-boot-starter/target/classes/, /spring-boot-autoconfigure/target/classes/, /spring-boot-starter-[\w-]+/, /spring-boot/target/classes/, /spring-boot-actuator/target/classes/, /spring-boot-devtools/target/classes/]
19:45:44.810 ... - Matching URLs for reloading : [file:/.../target/test-classes/, file:/.../target/classes/]

 :: Spring Boot ::        (v1.5.2.RELEASE)

2017-03-12 19:45:45.174  ...: Starting Application on machine with PID 7724 (<some path>\target\classes started by user in <project name>)
2017-03-12 19:45:45.175  ...: No active profile set, falling back to default profiles: default
2017-03-12 19:45:45.510  ...: Refreshing org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext@385c3ca3: startup date [Sun Mar 12 19:45:45 IST 2017]; root of context hierarchy
```

如日志中所示，生成应用程序的线程不是main线程，而是restartedMain线程。项目中所做的任何更改，无论是java文件更改，都将导致项目自动重启：

```shell
2017-03-12 19:53:46.204  ...: Closing org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext@385c3ca3: startup date [Sun Mar 12 19:45:45 IST 2017]; root of context hierarchy
2017-03-12 19:53:46.208  ...: Unregistering JMX-exposed beans on shutdown


 :: Spring Boot ::        (v1.5.2.RELEASE)

2017-03-12 19:53:46.587  ...: Starting Application on machine with PID 7724 (<project path>\target\classes started by user in <project name>)
2017-03-12 19:53:46.588  ...: No active profile set, falling back to default profiles: default
2017-03-12 19:53:46.591  ...: Refreshing org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext@acaf4a1: startup date [Sun Mar 12 19:53:46 IST 2017]; root of context hierarchy
```

## 4. 实时重新加载

spring-boot-devtools模块包含一个嵌入式LiveReload服务器，用于在资源更改时触发浏览器刷新。

为了在浏览器中发生这种情况，我们需要安装LiveReload插件，其中一个实现是用于Chrome的[Remote Live Reload](https://chrome.google.com/webstore/detail/remotelivereload/jlppknnillhjgiengoigajegdpieppei?hl=en-GB)。

## 5. 全局设置

spring-boot-devtools提供了一种配置不与任何应用程序耦合的全局设置的方法。该文件名为.spring-boot-devtools.properties，位于$HOME。

## 6. 远程应用程序

### 6.1 通过HTTP进行远程调试(远程调试隧道)

spring-boot-devtools通过HTTP提供开箱即用的远程调试功能，要拥有此功能，需要将spring-boot-devtools打包为应用程序的一部分。这可以通过在Maven的插件中禁用excludeDevtools配置来实现。

这是一个快速示例：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <excludeDevtools>false</excludeDevtools>
            </configuration>
        </plugin>
    </plugins>
</build>
```

现在，要通过HTTP进行远程调试，必须执行以下步骤：

1.  在服务器上部署和启动的应用程序应该在启用远程调试的情况下启动：

    ```shell
    -Xdebug -Xrunjdwp:server=y,transport=dt_socket,suspend=n
    ```

    可以看到，这里没有提到远程调试端口。因此，Java将选择一个随机端口

2.  对于同一个项目，打开Launch configurations，选择以下选项：
    选择主类：org.springframework.boot.devtools.RemoteSpringApplication
    在程序参数中，添加应用程序的URL，例如http://localhost:8080

3.  通过Spring Boot应用程序调试器的默认端口是8000，可以通过以下方式覆盖：

    ```properties
    spring.devtools.remote.debug.local-port=8010
    ```

4.  现在创建一个远程调试配置，将端口设置为通过属性配置的8010或8000(如果坚持默认设置)

日志如下所示：

```shell
  .   ____          _                                              __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _          ___               _      \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` |        | _ \___ _ __  ___| |_ ___ \ \ \ \
 \\/  ___)| |_)| | | | | || (_| []::::::[]   / -_) '  \/ _ \  _/ -_) ) ) ) )
  '  |____| .__|_| |_|_| |_\__, |        |_|_\___|_|_|_\___/\__\___|/ / / /
 =========|_|==============|___/===================================/_/_/_/
 :: Spring Boot Remote ::  (v1.5.2.RELEASE)

2017-03-12 22:24:11.089  ...: Starting RemoteSpringApplication v1.5.2.RELEASE on machine with PID 10476 (..\org\springframework\boot\spring-boot-devtools\1.5.2.RELEASE\spring-boot-devtools-1.5.2.RELEASE.jar started by user in project)
2017-03-12 22:24:11.097  ...: No active profile set, falling back to default profiles: default
2017-03-12 22:24:11.357  ...: Refreshing org.springframework.context.annotation.AnnotationConfigApplicationContext@11e21d0e: startup date [Sun Mar 12 22:24:11 IST 2017]; root of context hierarchy
2017-03-12 22:24:11.869  ...: The connection to http://localhost:8080 is insecure. You should use a URL starting with 'https://'.
2017-03-12 22:24:11.949  ...: LiveReload server is running on port 35729
2017-03-12 22:24:11.983  ...: Started RemoteSpringApplication in 1.24 seconds (JVM running for 1.802)
2017-03-12 22:24:34.324  ...: Remote debug connection opened
```

### 6.2 远程更新

远程客户端监视应用程序类路径的更改，就像远程重启功能所做的那样。类路径中的任何更改都会导致将更新后的资源推送到远程应用程序并触发重启。

当远程客户端启动并运行时，更改会被推送，因为只有在那时才可能监视更改的文件。

这是日志中的内容：

```shell
2017-03-12 22:33:11.613  INFO 1484 ...: Remote debug connection opened
2017-03-12 22:33:21.869  INFO 1484 ...: Uploaded 1 class resource
```

## 7. 总结

通过这篇简短的文章，我们演示了如何利用spring-boot-devtools模块通过自动化大量活动来改善开发人员体验并缩短开发时间。