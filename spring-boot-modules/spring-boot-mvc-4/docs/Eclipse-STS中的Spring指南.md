## 一、概述

本文阐述了[Eclipse Spring Tool Suite (STS)](https://spring.io/guides/gs/sts/) IDE 的一些有用特性，这些特性在开发[Spring 应用程序](https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/)时非常有用。

首先，我们展示了与使用 Eclipse 构建应用程序的传统方式相比，使用 STS 的优势。

此后，我们专注于如何引导应用程序、如何运行它以及如何添加其他依赖项。最后，我们通过添加应用程序参数来结束。

## 二、STS主要特点

STS是一个基于Eclipse的开发环境，专为Spring应用的开发而定制。

它提供了一个随时可用的环境来实施、调试、运行和部署你的应用程序。它还包括对 Pivotal tc Server、Pivotal Cloud Foundry、Git、Maven 和 AspectJ 的集成。STS 是作为对最新 Eclipse 版本的补充而构建的。

### 2.1. 项目配置

STS 了解几乎所有最常见的 Java 项目结构。[它解析配置文件，然后显示有关定义的bean](https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#beans-introduction)、依赖项、使用的命名空间的详细信息，此外还提取某些构造型的概述。

[![弹簧豆快照](https://www.baeldung.com/wp-content/uploads/2016/07/spring-bean-snapshot.png)](https://www.baeldung.com/wp-content/uploads/2016/07/spring-bean-snapshot.png)

### 2.2. STS 功能概述

Eclipse STS 验证你的项目并为你的应用程序提供快速修复。例如，在使用 Spring Data JPA 时，IDE 可用于验证查询方法名称(第 6 节对此有更多介绍)。

STS 还提供了所有 bean 方法及其相互关系的图形视图。你可能希望通过分别查看菜单window、show view和Spring下可用的视图来更仔细地了解 STS 附带的图形编辑器。

STS 还提供其他有用的功能，这些功能不仅限于 Spring 应用程序。建议读者查看可在[此处](https://spring.io/guides/gs/sts/)找到的完整功能列表。

## 3. Spring 应用程序的创建

让我们从引导一个简单的应用程序开始。如果没有 STS，通常使用[Spring Initializer](https://start.spring.io/)网站或[Spring Boot CLI](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#cli-init)创建 Spring 应用程序。这可以通过在 STS 的仪表板上单击“创建 Spring 入门项目”来简化。

在New Spring Starter Project屏幕中，使用默认值或进行你自己的调整，然后转到下一个屏幕。选择Web并单击完成。你的pom.xml现在应该类似于：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.4.0</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>

<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <java.version>1.8</java.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
		
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

你的 Spring Boot 版本可能不同，但总能在[此处](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web)找到最新版本。

## 4. 运行应用

可以通过右键单击项目并选择以Spring Boot App方式运行来启动上述应用程序。如果没有 STS，你很可能会使用以下命令从命令行运行应用程序：

```bash
$ mvn spring-boot:run
```

默认情况下，Spring 应用程序由运行在端口 8080 上的 Tomcat 启动。此时，应用程序在端口 8080 上启动，并且基本上什么都不做，因为我们还没有实现任何代码。第 8 节向你展示如何更改默认端口。

## 5. 日志记录和 ANSI 控制台

当你使用 run 命令从 IDE 运行项目时，你会注意到控制台输出了一些漂亮的[彩色编码](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html#boot-features-logging-color-coded-output)日志语句。如果你想关闭它，请转到运行配置...并禁用Spring Boot选项卡上的启用 ANSI 控制台输出复选框。或者，你也可以通过在application.properties文件中设置属性值来禁用它。

```plaintext
spring.output.ansi.enabled=NEVER
```

可以在[此处](https://docs.spring.io/spring-boot/docs/current/reference/html/howto-logging.html)找到有关应用程序日志配置的更多信息。

## 6. JPA 查询名称检查

有时实现数据访问层可能是一项繁琐的活动。可能需要编写大量样板代码来实现简单的查询和执行分页。[Spring Data JPA](https://spring.io/projects/spring-data-jpa) (JPA) 旨在显着促进数据访问层的此类实现。本节说明将 JPA 与 STS 结合使用的一些好处。

首先，将以下 JPA 依赖项添加到先前生成的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
```

你可能已经注意到上面的声明中没有指定版本。这是因为依赖项由spring-boot-starter-parent管理。

要使 JPA 工作，需要正确定义实体管理器和事务管理器。但是，Spring 会为你自动配置这些。留给开发人员的唯一事情就是创建实际的实体类。这些实体由实体管理器管理，实体管理器又由容器创建。例如，让我们创建一个实体类Foo，如下所示：

```java
@Entity
public class Foo implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;

    // Standard getters and setters
}
```

容器从配置包的根开始扫描所有用@Entity注解的类。接下来我们为Foo实体创建一个 JPA 存储库：

```java
public interface FooRepository extends JpaRepository<Foo, Integer> {
    public Foo findByNames(String name);
}
```

此时你可能已经注意到 IDE 现在将此查询方法标记为异常：

```bash
Invalid derived query! No property names found for type Foo!

```

这当然是由于我们不小心在 JPA 存储库的方法名称中写了一个 's'。要解决此问题，请像这样删除虚假的“s”：

```java
public Foo findByName(String name);
```

请注意，配置类上没有使用@EnableJpaRepositories。这是因为容器的AutoConfigration为项目预先注册了一个。

## 7.罐子类型搜索

“Jar 类型搜索”是[STS 3.5.0](https://docs.spring.io/sts/nan/v350/NewAndNoteworthy.html)中引入的一项功能。它在项目中为(尚未)在类路径上的类提供内容辅助建议。STS 可以帮助你将依赖项添加到 POM 文件，以防它们尚未在类路径中。

例如，让我们在Foo实体类中添加一行。为了让这个例子正常工作，请首先确保java.util.List的导入语句已经存在。现在我们可以添加 Google Guava 如下：

```java
private List<String> strings = Lists // ctrl + SPACE to get code completion
```

IDE 将建议将几个依赖项添加到类路径中。添加来自com.google.common.collect 的依赖项， 按回车键并添加来自Guava的依赖项。Guava jar 现在将自动添加到你的pom.xml文件中，如下所示：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>

```

从版本[STS 3.8.0开始，在 STS 对你的](https://docs.spring.io/sts/nan/v380/NewAndNoteworthy.html)pom.xml进行更改之前，你会看到一个确认对话框。

## 8. 添加应用参数

Spring 的其他强大功能之一是支持外部配置，这些配置可以通过多种方式传递给应用程序，例如作为命令行参数、在属性或 YAML 文件中指定或作为系统属性。在本节中，我们重点介绍使用 STS 添加配置选项作为应用程序启动参数。这通过将 Tomcat 配置为在不同的端口上启动来说明。

为了在默认端口以外的 Tomcat 端口上运行应用程序，你可以使用以下命令，其中将自定义端口指定为命令行参数：

```bash
mvn spring-boot:run -Drun.arguments="--server.port=7070"
```

使用 STS 时，你必须转到运行菜单。从 Run Configurations 对话框中选择run configurations ...，从左侧面板中选择Spring Boot App ，然后选择demo – DemoApplication(如果你没有选择默认项目，这将有所不同)。从(x)= Arguments选项卡在Program Arguments窗口中键入

```bash
--server.port=7070
```

并运行。你应该会在控制台中看到类似如下所示的输出：

```bash
.
.
2016-07-06 13:51:40.999  INFO 8724 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 7070 (http)
2016-07-06 13:51:41.006  INFO 8724 --- [           main] com.baeldung.boot.DemoApplication        : Started DemoApplication in 6.245 seconds (JVM running for 7.34)
```

## 9.总结

在本文中，我们展示了在 STS 中开发 Spring 项目的基础知识。我们展示的一些内容包括在 STS 中执行应用程序、在 Spring Data JPA 开发过程中提供支持以及命令行参数的使用。但是，由于 STS 提供了一组丰富的功能，因此在开发过程中可以使用更多有用的功能。

本文的完整实现可以在[github 项目](https://github.com/eugenp/tutorials/tree/master/spring-boot-modules/spring-boot-mvc-4)中找到——这是一个基于 Eclipse 的项目，因此应该很容易导入和运行。