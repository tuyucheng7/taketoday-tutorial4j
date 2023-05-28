## 1. 概述

Dropwizard 是一个用于快速开发高性能 RESTful Web 服务的开源Java框架。它收集了一些流行的库来创建轻量级包。它使用的主要库是 Jetty、Jersey、Jackson、JUnit 和 Guava。此外，它使用自己的库[Metrics](https://www.baeldung.com/dropwizard-metrics)。

在本教程中，我们将学习如何配置和运行一个简单的 Dropwizard 应用程序。完成后，我们的应用程序将公开一个 RESTful API，使我们能够获取已存储品牌的列表。

## 2.Maven依赖

首先，[dropwizard-core](https://search.maven.org/search?q=g:io.dropwizard AND a:dropwizard-core)依赖项是我们创建服务所需要的。让我们将它添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>io.dropwizard</groupId>
    <artifactId>dropwizard-core</artifactId>
    <version>2.0.0</version>
</dependency>
```

## 三、配置

现在，我们将创建每个 Dropwizard 应用程序运行所需的必要类。

Dropwizard 应用程序将属性存储在 YML 文件中。因此，我们将在资源目录中创建introduction-config.yml文件：

```plaintext
defaultSize: 5
```

我们可以通过创建一个扩展io.dropwizard.Configuration的类来访问该文件中的值：

```java
public class BasicConfiguration extends Configuration {
    @NotNull private final int defaultSize;

    @JsonCreator
    public BasicConfiguration(@JsonProperty("defaultSize") int defaultSize) {
        this.defaultSize = defaultSize;
    }

    public int getDefaultSize() {
        return defaultSize;
    }
}
```

Dropwizard 使用[Jackson](https://www.baeldung.com/jackson)将配置文件反序列化到我们的类中。因此，我们使用了 Jackson 注解。

接下来，让我们创建主要的应用程序类，它负责准备我们的服务以供使用：

```java
public class IntroductionApplication extends Application<BasicConfiguration> {

    public static void main(String[] args) throws Exception {
        new IntroductionApplication().run("server", "introduction-config.yml");
    }

    @Override
    public void run(BasicConfiguration basicConfiguration, Environment environment) {
        //register classes
    }

    @Override
    public void initialize(Bootstrap<BasicConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(new ResourceConfigurationSourceProvider());
        super.initialize(bootstrap);
    }
}
```

首先，主要方法负责运行应用程序。我们可以将 args传递给run 方法，也可以自己填充它。

第一个参数可以是server或 check。检查选项验证配置，而服务器 选项运行应用程序。第二个参数是配置文件的位置。 

此外，初始化方法将配置提供程序设置为 ResourceConfigurationSourceProvider ，这允许应用程序在资源目录中找到给定的配置文件。不必重写此方法。

最后，run方法允许我们访问 Environment和BaseConfiguration ，我们将在本文后面使用它们。

## 4.资源

首先，让我们为我们的品牌创建一个领域类：

```java
public class Brand {
    private final Long id;
    private final String name;

    // all args constructor and getters
}
```

其次，让我们创建一个负责返回品牌的BrandRepository类：

```java
public class BrandRepository {
    private final List<Brand> brands;

    public BrandRepository(List<Brand> brands) {
        this.brands = ImmutableList.copyOf(brands);
    }

    public List<Brand> findAll(int size) {
        return brands.stream()
          .limit(size)
          .collect(Collectors.toList());
    }

    public Optional<Brand> findById(Long id) {
        return brands.stream()
          .filter(brand -> brand.getId().equals(id))
          .findFirst();
    }
}
```

此外，我们能够使用[Guava](https://www.baeldung.com/guava-collections)的 ImmutableList ，因为它是 Dropwizard 本身的一部分。

第三，我们将创建一个BrandResource类。Dropwizard 默认使用[JAX-RS](https://www.baeldung.com/jax-rs-spec-and-implementations)和 Jersey 作为实现。因此，我们将利用此规范中的注解来公开我们的 REST API 端点：

```java
@Path("/brands")
@Produces(MediaType.APPLICATION_JSON)
public class BrandResource {
    private final int defaultSize;
    private final BrandRepository brandRepository;

    public BrandResource(int defaultSize, BrandRepository brandRepository) {
        this.defaultSize = defaultSize;
        this.brandRepository = brandRepository;
    }

    @GET
    public List<Brand> getBrands(@QueryParam("size") Optional<Integer> size) {
        return brandRepository.findAll(size.orElse(defaultSize));
    }

    @GET
    @Path("/{id}")
    public Brand getById(@PathParam("id") Long id) {
        return brandRepository
          .findById(id)
          .orElseThrow(RuntimeException::new);
    }
}
```

此外，我们将size定义为Optional，以便在未提供参数时使用配置中的defaultSize 。

最后，我们将 在IntroductionApplicaton类中注册BrandResource 。为此，让我们实现run方法：

```java
@Override
public void run(BasicConfiguration basicConfiguration, Environment environment) {
    int defaultSize = basicConfiguration.getDefaultSize();
    BrandRepository brandRepository = new BrandRepository(initBrands());
    BrandResource brandResource = new BrandResource(defaultSize, brandRepository);

    environment
      .jersey()
      .register(brandResource);
}
```

所有创建的资源都应该在这个方法中注册。

## 5. 运行应用

在本节中，我们将学习如何从命令行运行应用程序。

[首先，我们将配置项目以使用maven-shade-plugin](https://search.maven.org/search?q=g:org.apache.maven.plugins AND a:maven-shade-plugin)构建 JAR 文件：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <configuration>
        <createDependencyReducedPom>true</createDependencyReducedPom>
        <filters>
            <filter>
                <artifact>:</artifact>
                <excludes>
                    <exclude>META-INF/.SF</exclude>
                    <exclude>META-INF/.DSA</exclude>
                    <exclude>META-INF/.RSA</exclude>
                </excludes>
            </filter>
        </filters>
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <transformers>
                    <transformer
                      implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <mainClass>com.baeldung.dropwizard.introduction.IntroductionApplication</mainClass>
                    </transformer>
                </transformers>
            </configuration>
        </execution>
    </executions>
</plugin>
```

这是插件的建议配置。此外，我们在<mainClass>元素中包含了主类的路径。

[最后，我们将使用Maven](https://www.baeldung.com/maven)构建应用程序。一旦我们有了 JAR 文件，我们就可以运行应用程序：

```bash
java -jar target/dropwizard-0.0.1-SNAPSHOT.jar
```

不需要传递参数，因为我们已经将它们包含在IntroductionApplication 类中。

之后，控制台日志应以以下内容结尾：

```plaintext
INFO  [2020-01-08 18:55:06,527] org.eclipse.jetty.server.Server: Started @1672ms
```

现在，应用程序正在侦听端口 8080，我们可以在http://localhost:8080/brands访问我们的品牌端点。

## 6.健康检查

启动应用程序时，我们被告知该应用程序没有任何健康检查。幸运的是，Dropwizard 提供了一个简单的解决方案来为我们的应用程序添加健康检查。

让我们从添加一个扩展com.codahale.metrics.health.HealthCheck的简单类开始：

```java
public class ApplicationHealthCheck extends HealthCheck {
    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
```

这个简单的方法将返回有关组件健康状况的信息。我们可以创建多个健康检查，其中一些可能会在某些情况下失败。例如，如果与数据库的连接失败，我们将返回Result.unhealthy() 。

最后，我们需要在IntroductionApplication类的run方法中注册我们的健康检查：

```java
environment
  .healthChecks()
  .register("application", new ApplicationHealthCheck());
```

运行应用程序后，我们可以在http://localhost:8081/healthcheck下查看健康检查响应：

```java
{
  "application": {
    "healthy": true,
    "duration": 0
  },
  "deadlocks": {
    "healthy": true,
    "duration": 0
  }
}
```

正如我们所看到的，我们的健康检查已经注册在application标签下。

## 七. 总结

在本文中，我们学习了如何使用 Maven 设置 Dropwizard 应用程序。

我们发现应用程序的基本设置非常简单快捷。此外，Dropwizard 包含我们运行高性能 RESTful Web 服务所需的每个库。