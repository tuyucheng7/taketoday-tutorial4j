## 1. 概述

在本教程中，我们将演示如何将Git仓库信息注入到Maven构建的基于Spring Boot的应用程序中。

为此，我们将使用[maven-git-commit-id-plugin](https://github.com/ktoso/maven-git-commit-id-plugin)-一个专为此目的而创建的便捷工具。

## 2. Maven依赖

在项目的pom.xml文件的<plugins\>部分添加一个插件：

```xml
<plugin>
    <groupId>pl.project13.maven</groupId>
    <artifactId>git-commit-id-plugin</artifactId>
    <version>2.2.1</version>
</plugin>
```

你可以在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"pl.project13.maven" AND a%3A"git-commit-id-plugin")找到最新版本。请记住，**此插件至少需要3.1.1版本的Maven**。

请注意，此插件在不同的仓库坐标处有一个更高版本的重新定位版本(5.x或更新版本)，但是，该版本需要Java 11。由于我们将使用具有Java 8基线的Spring Boot 2.x开发示例应用程序，因此我们需要使用旧版本的插件。这使我们能够保持Spring Boot和git-commit-id-plugin之间的兼容性。

## 3. 配置

该插件有许多方便的标志和属性，可以扩展其功能。在本节中，我们将简要介绍其中的一些，如果你想了解所有这些，请访问maven-git-commit-id-plugin的[页面](https://github.com/ktoso/maven-git-commit-id-plugin)，如果你想直接看示例，请转到第4节。

以下代码片段包含插件属性的示例；根据你的需要在<configuration\></configuration\>部分中指定它们。

### 3.1 缺少仓库

如果未找到Git仓库，你可以将其配置为忽略错误：

```xml
<failOnNoGitDirectory>false</failOnNoGitDirectory>
```

### 3.2 Git仓库位置

如果要指定自定义.git仓库位置，请使用dotGitDirectory属性：

```xml
<dotGitDirectory>${project.basedir}/submodule_directory/.git</dotGitDirectory>
```

### 3.3 输出文件

为了生成具有自定义名称和/或目录的属性文件，请使用以下部分：

```xml
<generateGitPropertiesFilename>
    ${project.build.outputDirectory}/filename.properties
</generateGitPropertiesFilename>
```

### 3.4 详细程度

对于更详细的日志记录使用：

```xml
<verbose>true</verbose>
```

### 3.5 属性文件生成

你可以关闭git.properties文件的创建：

```xml
<generateGitPropertiesFile>false</generateGitPropertiesFile>
```

### 3.6 属性前缀

如果要指定自定义属性前缀，请使用：

```xml
<prefix>git</prefix>
```

### 3.7 仅适用于父仓库

使用带有子模块的项目时，设置此标志可确保该插件仅适用于父仓库：

```xml
<runOnlyOnce>true</runOnlyOnce>
```

### 3.8 属性排除

你可能希望排除一些敏感数据，例如仓库用户信息：

```xml
<excludeProperties>
    <excludeProperty>git.user.</excludeProperty>
</excludeProperties>
```

### 3.9 属性包含

也可以只包含指定的数据：

```xml
<includeOnlyProperties>    
    <includeOnlyProperty>git.commit.id</includeOnlyProperty>
</includeOnlyProperties>
```

## 4. 示例应用

让我们创建一个示例REST控制器，它将返回有关我们项目的基本信息。

我们将使用Spring Boot创建示例应用程序，如果你不知道如何设置Spring Boot应用程序，请参阅介绍文章：[配置Spring Boot Web应用程序]()。

我们的应用程序将包含2个类：CommitIdApplication和CommitIdController

### 4.1 CommitIdApplication

CommitIdApplication将作为我们应用程序的主类：

```java
@SpringBootApplication(scanBasePackages = {"cn.tuyucheng.taketoday.git"})
public class CommitIdApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommitIdApplication.class, args);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propsConfig = new PropertySourcesPlaceholderConfigurer();
        propsConfig.setLocation(new ClassPathResource("git.properties"));
        propsConfig.setIgnoreResourceNotFound(true);
        propsConfig.setIgnoreUnresolvablePlaceholders(true);
        return propsConfig;
    }
}
```

除了配置应用程序的主方法之外，我们还创建了PropertyPlaceHolderConfigurer bean，以便我们能够访问插件生成的属性文件。

我们还设置了一些标志，这样即使Spring无法解析git.properties文件，应用程序也能顺利运行。

### 4.2 CommitIdController

```java
@RestController
public class CommitInfoController {

    @Value("${git.commit.message.short}")
    private String commitMessage;

    @Value("${git.branch}")
    private String branch;

    @Value("${git.commit.id}")
    private String commitId;

    @RequestMapping("/commitId")
    public Map<String, String> getCommitId() {
        Map<String, String> result = new HashMap<>();
        result.put("Commit message",commitMessage);
        result.put("Commit branch", branch);
        result.put("Commit id", commitId);
        return result;
    }
}
```

如你所见，我们将Git属性注入到类字段中。

要查看所有可用属性，请参阅git.properties文件或作者的Github[页面](https://github.com/ktoso/maven-git-commit-id-plugin)。我们还创建了一个简单的端点，在HTTP GET请求时，它将使用包含注入值的JSON进行响应。

### 4.3 Maven入口

我们首先设置插件要执行的执行步骤，以及我们认为有用的任何其他配置属性：

```xml
<plugin>
    <groupId>pl.project13.maven</groupId>
    <artifactId>git-commit-id-plugin</artifactId>
    <version>2.2.1</version>
    <executions>
        <execution>
            <id>get-the-git-infos</id>
            <goals>
                <goal>revision</goal>
            </goals>
        </execution>
        <execution>
            <id>validate-the-git-infos</id>
            <goals>
                <goal>validateRevision</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <!-- ... -->
    </configuration>
</plugin>
```

为了使我们的代码正常工作，我们需要在类路径中添加一个git.properties文件，为实现这一目标，我们有两种选择。

第一个是将其留给插件来生成文件，我们可以通过将generateGitPropertiesFile配置属性设置为true来指定这一点：

```xml
<configuration>
    <generateGitPropertiesFile>true</generateGitPropertiesFile>
</configuration>
```

第二种选择是我们自己在resources文件夹中包含一个git.properties文件，我们可以只包含我们将在项目中使用的条目：

```properties
# git.properties
git.tags=${git.tags}
git.branch=${git.branch}
git.dirty=${git.dirty}
git.remote.origin.url=${git.remote.origin.url}
git.commit.id=${git.commit.id}
git.commit.id.abbrev=${git.commit.id.abbrev}
git.commit.id.describe=${git.commit.id.describe}
git.commit.id.describe-short=${git.commit.id.describe-short}
git.commit.user.name=${git.commit.user.name}
git.commit.user.email=${git.commit.user.email}
git.commit.message.full=${git.commit.message.full}
git.commit.message.short=${git.commit.message.short}
git.commit.time=${git.commit.time}
git.closest.tag.name=${git.closest.tag.name}
git.closest.tag.commit.count=${git.closest.tag.commit.count}
git.build.user.name=${git.build.user.name}
git.build.user.email=${git.build.user.email}
git.build.time=${git.build.time}
git.build.host=${git.build.host}
git.build.version=${git.build.version}
```

Maven会将占位符替换为适当的值。

注意：一些IDE不能很好地与这个插件配合使用，并且当我们像上面那样定义属性时，可能会在引导程序上抛出一个“循环占位符引用”错误。

启动并请求localhost:8080/commitId后，你可以看到一个结构类似于以下内容的JSON文件：

```json
{
    "Commit id": "1a509ef1dce3effa7fbd87446308be279c6a5e6f",
    "Commit branch": "master",
    "Commit message": "✨ 使用Maven运行Spring Boot应用程序 vs 可执行的War/Jar"
}
```

## 5. 与Spring Boot Actuator集成

你还可以简单地将插件与[Spring Actuator]()一起使用。

正如你在[文档](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html)中看到的那样，GitInfoContributor会选择可用的git.properties文件。因此，使用默认插件配置，调用/info端点时将返回Git信息：

```json
{
    "git": {
        "branch": "master",
        "commit": {
            "id": "7adb64f",
            "time": "2022-12-20T14:30:34+0200"
        }
    }
}
```

## 6. 总结

在本教程中，我们演示了使用[maven-git-commit-id-plugin](https://github.com/ktoso/maven-git-commit-id-plugin)的基础知识，并创建了一个简单的Spring Boot应用程序，该程序使用了插件生成的属性。提供的配置并未涵盖所有可用的标志和属性，但它涵盖了开始使用此插件所需的所有基础知识。