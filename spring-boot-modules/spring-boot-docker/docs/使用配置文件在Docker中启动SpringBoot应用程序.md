## 1. 简介

我们都知道Docker现在的流行程度，作为Java开发人员，我们将Spring Boot应用程序容器化已成为一种趋势。然而，对于某些开发人员来说，如何在容器化的Spring Boot应用程序中设置Profile可能是一个问题。

在本教程中，我们介绍如何在Docker容器中使用Profile启动Spring Boot应用程序。

## 2. 基本的Dockerfile

通常，要对Spring Boot应用程序进行Docker化，我们只需提供一个Dockerfile。

下面是容器化一个Spring Boot应用程序所需的最小配置Dockerfile：

```dockerfile
FROM openjdk:17
COPY target/.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

然后，我们可以通过docker build命令构建我们的Docker镜像：

```shell
docker build --tag=docker-with-spring-profile:latest .
```

最后，我们可以从镜像docker-with-spring-profile运行我们的应用程序：

```shell
docker run docker-with-spring-profile:latest
```

正如我们所看到的，我们的Spring Boot应用程序以“default” Profile启动：

```shell
2022-11-15 10:33:50.227 INFO 1 --- [main] c.t.t.docker.spring.DemoApplication: Starting DemoApplication using Java 17.0.1 on 138d52328b16 with PID 1 (/app.jar started by root in /)
2022-11-15 10:36:50.635 INFO 1 --- [main] c.t.t.docker.spring.DemoApplication: No active profile set, falling back to 1 default profile: "default"
//...
```

## 3. 在Dockerfile中设置Profile

为我们的容器化应用程序设置Profile的一种方法是使用Spring Boot的命令行参数“-Dspring.profiles.active”。

因此，为了将Profile设置为“test”，我们在Dockerfile的ENTRYPOINT行中添加一个新参数“-Dspring.profiles.active=test”：

```dockerfile
//...
ENTRYPOINT ["java", "-Dspring.profiles.active=test", "-jar", "/app.jar"]
```

要看到Profile的实际改变，我们使用相同的命令再次运行我们的容器：

```shell
docker run docker-with-spring-profile:latest
```

这次，我们可以看到应用程序成功使用“test” Profile来运行：

```shell
2022-11-15 10:36:50.732 INFO 1 --- [main] c.t.t.docker.spring.DemoApplication: Starting DemoApplication using Java 17.0.1 on 227974fa84b2 with PID 1 (/app.jar started by root in /)
2022-11-15 10:36:50.899 INFO 1 --- [main] c.t.t.docker.spring.DemoApplication: The following 1 profile is active: "test"
//...
```

## 4. 使用环境变量设置Profile

有时，在我们的Dockerfile中使用硬编码的Profile并不方便，如果我们需要多个Profile，那么在运行容器时选择其中一个可能会很麻烦。

作为一种更好的方法，**在启动期间，Spring Boot会寻找一个特殊的环境变量SPRING_PROFILES_ACTIVE**。

因此，我们实际上可以将其与docker run命令一起使用，这样就可以在启动时设置Spring Profile：

```shell
docker run -e "SPRING_PROFILES_ACTIVE=test" docker-with-spring-profile:latest
```

此外，根据我们的用例，我们可以通过逗号分隔的字符串一次设置多个Profile：

```shell
docker run -e "SPRING_PROFILES_ACTIVE=test1,test2,test3" docker-with-spring-profile:latest
```

但是，我们应该注意Spring Boot在属性之间有特定的顺序，**命令行参数优先于环境变量**。出于这个原因，为了使SPRING_PROFILES_ACTIVE工作，我们需要恢复我们的Dockerfile。因此，我们需要从Dockerfile的ENTRYPOINT一行中删除“-Dspring.profiles.active=test”参数：

```dockerfile
//...
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

最后，我们可以看到通过SPRING_PROFILES_ACTIVE设置的Profile被用于启动应用程序：

```shell
2022-11-15 10:39:50.732 INFO 1 --- [main] c.t.t.docker.spring.DemoApplication: Starting DemoApplication using Java 17.0.1 on 138d52328b16 with PID 1 (/app.jar started by root in /)
2022-11-15 10:39:50.735 INFO 1 --- [main] c.t.t.docker.spring.DemoApplication: The following profiles are active: test
//..
```

## 5. 在Docker Compose文件中设置Profile

作为替代方法，**环境变量也可以在docker-compose文件中提供**。

此外，为了更好地利用docker run操作，我们可以为每个Profile创建一个docker-compose文件。

下面为“test” Profile创建一个docker-compose-test.yml文件：

```yaml
version: "3.5"
services:
    docker-with-spring-profile:
        image: docker-with-spring-profile:latest
        environment:
            - "SPRING_PROFILES_ACTIVE=test"
```

类似地，我们为“prod” Profile创建另一个文件docker-compose-prod.yml，唯一的区别是使用的Profile为“prod”：

```yaml
//...
environment:
    - "SPRING_PROFILES_ACTIVE=prod"
```

因此，我们可以通过两个不同的docker-compose文件来运行我们的容器：

```shell
# for the profile 'test'
docker-compose -f docker-compose-test.yml up

# for the profile 'prod'
docker-compose -f docker-compose-prod.yml up
```

## 6. 总结

在本教程中，我们描述了在容器化的Spring Boot应用程序中设置Profile的不同方法，并演示了一些使用Docker 和Docker Compose的示例。