## 1. 简介

在本教程中，我们将探索Spring提供的支持系统，以促进Spring Boot升级。特别是，我们将研究[spring-boot-properties-migrator](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-properties-migrator)模块，它有助于迁移应用程序属性。

对于每个Spring Boot的版本升级，可能会有一些属性被标记为已弃用、已停止支持或新引入。Spring会发布每次升级的[综合变更日志](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.6.0-Configuration-Changelog)。但是，这些变更日志可能有点乏味，这就是spring-boot-properties-migrator模块派上用场的地方，它通过为我们的设置提供个性化信息来做到这一点。

## 2. Demo应用程序

让我们将Spring Boot应用程序从版本2.3.0升级到2.6.3。

### 2.1 属性

在我们的演示应用程序中，我们有两个属性文件。在默认属性文件application.properties中，让我们添加以下配置：

```properties
spring.resources.cache.period=31536000
spring.resources.chain.compressed=false
spring.resources.chain.html-application-cache=false
```

对于开发配置文件YAML文件application-dev.yaml：

```yaml
spring:
    resources:
        cache:
            period: 31536000
        chain:
            compressed: true
            html-application-cache: true
```

我们的属性文件包含几个属性，这些属性在Spring Boot 2.3.0和2.6.3之间已被替换或删除。此外，我们还有.properties 和.yaml文件，以便更好地演示。

### 2.2 添加依赖项

首先，让我们在模块中添加[spring-boot-properties-migrator](https://search.maven.org/artifact/org.springframework.boot/spring-boot-properties-migrator)作为依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-properties-migrator</artifactId>
    <scope>runtime</scope>
</dependency>
```

如果我们使用Gradle，我们可以添加：

```groovy
runtime("org.springframework.boot:spring-boot-properties-migrator")
```

依赖的范围应该是runtime。

## 3. 运行扫描

其次，让我们打包并运行我们的应用程序，我们将使用Maven来构建和打包：

```bash
mvn clean package
```

最后，让我们运行它：

```bash
java -jar target/spring-boot-properties-migrator-demo-1.0.0.jar
```

结果，spring-boot-properties-migrator模块会扫描我们的应用程序属性文件并发挥了它的魔力！稍后会详细介绍。

## 4. 了解扫描输出

让我们浏览日志以了解扫描的建议。

### 4.1 可替换属性

对于具有已知替换的属性，**我们可以看到来自PropertiesMigrationListener类的WARN日志**：

```shell
WARN 34777 --- [main] o.s.b.c.p.m.PropertiesMigrationListener  : 
The use of configuration keys that have been renamed was found in the environment:

Property source 'Config resource 'class path resource [application.properties]' via location 'optional:classpath:/'':
	Key: spring.resources.cache.period
		Line: 2
		Replacement: spring.web.resources.cache.period
	Key: spring.resources.chain.compressed
		Line: 3
		Replacement: spring.web.resources.chain.compressed

Property source 'Config resource 'class path resource [application-dev.yaml]' via location 'optional:classpath:/'':
	Key: spring.resources.cache.period
		Line: 5
		Replacement: spring.web.resources.cache.period
	Key: spring.resources.chain.compressed
		Line: 7
		Replacement: spring.web.resources.chain.compressed

Each configuration key has been temporarily mapped to its replacement for your convenience. To silence this warning, please update your configuration to use the new keys.
```

**我们在日志中看到了所有关键信息，例如每个条目属于哪个属性文件、键、行号和替换键**，这有助于我们轻松识别和替换所有此类属性。**此外，该模块在运行时用可用的替换项替换了这些属性**，使我们能够运行应用程序而无需进行任何更改。

### 4.2 不受支持的属性

对于没有已知替换的属性，**我们可以看到来自PropertiesMigrationListener类的ERROR日志**：

```java
ERROR 34777 --- [main] o.s.b.c.p.m.PropertiesMigrationListener  : 
The use of configuration keys that are no longer supported was found in the environment:

Property source 'Config resource 'class path resource [application.properties]' via location 'optional:classpath:/'':
	Key: spring.resources.chain.html-application-cache
		Line: 4
		Reason: none

Property source 'Config resource 'class path resource [application-dev.yaml]' via location 'optional:classpath:/'':
	Key: spring.resources.chain.html-application-cache
		Line: 8
		Reason: none
```

**与前面的场景一样， 我们看到有问题的属性文件、键、属性文件中的行号以及键被删除的原因**。但是，与前面的情况不同，应用程序的启动可能会失败，具体取决于所讨论的属性。我们还可能面临运行时问题，因为这些属性无法自动迁移。

## 5. 更新配置属性

现在，有了扫描提供给我们的重要信息，我们可以更好地升级属性。我们知道要转到的属性文件、行号以及要替换为建议的替换项或查阅发行说明以了解没有替换项的特定项的键。

让我们修复我们的属性文件，在默认属性文件application.properties中，让我们根据建议替换属性：

```properties
spring.web.resources.cache.period=31536000
spring.web.resources.chain.compressed=false
```

同样，让我们更新开发配置文件YAML文件application-dev.yaml：

```yaml
spring:
    web:
        resources:
            cache:
                period: 31536000
            chain:
                compressed: false
```

总而言之，我们将属性spring.resources.cache.period替换为spring.web.resources.cache.period，并将spring.resources.chain.compressed替换为spring.web.resources.chain.compressed。在新版本不再支持spring.resources.chain.html-application-cache键，因此，在这种情况下我们将其删除。

让我们再次运行扫描。首先，构建我们的应用程序：

```bash
mvn clean package
```

然后，让我们运行它：

```bash
java -jar target/spring-boot-properties-migrator-demo-1.0.0.jar
```

现在，我们之前从PropertiesMigrationListener类中看到的所有信息日志都消失了，说明我们的属性迁移成功了！

## 6. 这一切是如何运作的？

[Spring Boot模块JAR在META-INF文件夹中包含一个spring-configuration-metadata.json](https://docs.spring.io/spring-boot/docs/current/reference/html/configuration-metadata.html)文件，这些JSON文件是spring-boot-properties-migrator模块的信息来源，当它扫描我们的属性文件时，它会从这些JSON文件中提取相关属性的元数据信息以构建扫描报告。

该文件中的一个示例显示了我们在生成的报告中看到的各种信息：

在spring-autoconfigure:2.6.3.jar包中的META-INF/spring-configuration-metadata.json中，我们可以找到spring.resources.cache.period的条目：

```json
{
	"name": "spring.resources.cache.period",
	"type": "java.time.Duration",
	"deprecated": true,
	"deprecation": {
		"level": "error",
		"replacement": "spring.web.resources.cache.period"
	}
}
```

类似地，在spring-boot:2.0.0.RELEASE.jar包中的META-INF/spring-configuration-metadata.json中，我们可以找到banner.image.location的条目：

```json
{
	"defaultValue": "banner.gif",
	"deprecated": true,
	"name": "banner.image.location",
	"description": "Banner image file location (jpg/png can also be used).",
	"type": "org.springframework.core.io.Resource",
	"deprecation": {
		"level": "error",
		"replacement": "spring.banner.image.location"
	}
}
```

## 7. 注意事项

在结束本文之前，让我们回顾一下spring-boot-properties-migrator的一些注意事项。

### 7.1 不要在生产中保留这种依赖

此模块仅在开发环境中的升级期间使用，一旦我们确定了要更新或删除的属性，然后更正它们，我们就可以从我们的依赖项中删除该模块。最终，我们永远不应该在更高的环境中包含这个模块，由于与之相关的某些成本，[不推荐这样做](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.3-Release-Notes#configuration-properties)。

### 7.2 历史属性

我们不应该在升级期间跳转版本，因为模块可能无法检测到在更旧的版本中弃用的真正旧的属性。例如，让我们将banner.image.location添加到我们的application.properties文件中：

```properties
banner.image.location="myBanner.txt"
```

此属性[在Spring Boot 2.0中已弃用](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.0-Configuration-Changelog)，如果我们尝试直接使用Spring Boot 2.6.3版本运行我们的应用程序，我们将不会看到任何关于它的警告或错误消息，我们必须使用Spring Boot 2.0运行扫描才能检测到此属性：

```java
WARN 25015 --- [main] o.s.b.c.p.m.PropertiesMigrationListener  : 
The use of configuration keys that have been renamed was found in the environment:

Property source 'applicationConfig: [classpath:/application.properties]':
    Key: banner.image.location
	Line: 5
	Replacement: spring.banner.image.location


Each configuration key has been temporarily mapped to its replacement for your convenience. To silence this warning, please update your configuration to use the new keys.
```

## 8. 总结

在本文中，我们探讨了spring-boot-properties-migrator模块，这是一个方便的工具，可以扫描我们的属性文件并提供易于操作的扫描报告，我们还介绍了模块如何实现其壮举的高级视图。最后，为了确保此工具得到最佳利用，我们提出了一些注意事项。