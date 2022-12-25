## 1. 概述

配置Spring应用程序的方法之一是使用YAML配置文件。

在本快速教程中，我们将使用YAML为简单的Spring Boot应用程序配置不同的配置文件。

## 2. Spring YAML文件

Spring Profile有助于使Spring应用程序为不同的环境定义不同的属性。

让我们看一下包含两个Profile的简单YAML文件，**分隔两个Profile的三个破折号表示新文档的开始，因此可以在同一个YAML文件中描述所有Profile**。

application.yml文件的相对路径为/myApplication/src/main/resources/application.yml：

```yaml
spring:
    config:
        activate:
            on-profile: test
name: test-YAML
environment: testing
enabled: false
servers:
    - www.abc.test.com
    - www.xyz.test.com

---
spring:
    config:
        activate:
            on-profile: prod
name: prod-YAML
environment: production
enabled: true
servers:
    - www.abc.com
    - www.xyz.com
```

请注意，此设置并不意味着在我们启动应用程序时这些Profile中的任何一个都将处于激活状态，除非我们明确指出，否则不会加载特定于Profile的文档中定义的属性；默认情况下，唯一激活的Profile将是'default'。

## 3. 将YAML绑定到配置类

要从属性文件加载一组相关属性，我们将创建一个bean类：

```java
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class YAMLConfig {

	private String name;
	private String environment;
	private boolean enabled;
	private List<String> servers = new ArrayList<>();

	// standard getters and setters
}
```

这里使用的注解包括：

-   **@Configuration**：这将类标记为bean定义的来源
-   **@ConfigurationProperties**：这将外部配置绑定并验证到配置类
-   **@EnableConfigurationProperties**：这个注解用于在Spring应用中启用@ConfigurationProperties注解的bean

## 4. 访问YAML属性

要访问YAML属性，我们将创建一个YAMLConfig类的对象，并使用该对象访问属性。

在属性文件中，我们将spring.profiles.active环境变量设置为prod，如果我们不定义此属性，则只有“default” Profile将处于激活状态。

属性文件的相对路径是/myApplication/src/main/resources/application.properties：

```properties
spring.profiles.active=prod
```

在此示例中，我们将使用CommandLineRunner显示属性：

```java
@SpringBootApplication
public class MyApplication implements CommandLineRunner {

	@Autowired
	private YAMLConfig myConfig;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(MyApplication.class);
		app.run();
	}

	public void run(String... args) throws Exception {
		System.out.println("using environment: " + myConfig.getEnvironment());
		System.out.println("name: " + myConfig.getName());
		System.out.println("enabled:" + myConfig.isEnabled());
		System.out.println("servers: " + myConfig.getServers());
	}
}
```

命令行上的输出为：

```bash
using environment: production
name: prod-YAML
enabled: true
servers: [www.abc.com, www.xyz.com]
```

## 5. YAML属性覆盖

在Spring Boot中，YAML文件可以被其他YAML属性文件覆盖。

在版本2.4.0之前，YAML属性被以下位置的属性文件覆盖，按优先级最高的顺序排列：

-   配置文件的属性放在打包的jar之外
-   配置文件的属性打包在打包的jar中
-   应用程序属性放在打包的jar之外
-   应用程序属性打包在打包的jar中

从Spring Boot 2.4开始，外部文件总是覆盖打包的文件，无论它们是否特定于Profile。

## 6. 总结

在这篇简短的文章中，我们学习了如何使用YAML在Spring Boot应用程序中配置属性，并讨论了Spring Boot对YAML文件的属性覆盖规则。