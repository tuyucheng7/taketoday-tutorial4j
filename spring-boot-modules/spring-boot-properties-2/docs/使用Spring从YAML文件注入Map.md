## 1. 概述

在本快速教程中，我们将学习**如何在Spring Boot中从YAML文件注入Map**。

首先，我们将从对Spring框架中的YAML文件的一些了解开始，然后，我们将通过一个实际示例演示如何将YAML属性绑定到Map。

## 2. Spring框架中的YAML文件

使用[YAML](https://yaml.org/)文件来存储外部配置数据是Spring开发人员的普遍做法，基本上，[Spring支持YAML]()**文档作为属性properties文件的替代方案，并在底层使用**[SnakeYAML](https://bitbucket.org/asomov/snakeyaml/src)**来解析它们**。

事不宜迟，让我们看看典型的YAML文件是什么样的：

```yaml
server:
    port: 8090
    application:
        name: myapplication
        url: http://myapplication.com
```

正如我们所见，**YAML文件是不言自明的，更易于阅读。事实上，YAML提供了一种奇特而简洁的方式来存储分层配置数据**。

默认情况下，Spring Boot在应用程序启动时从application.properties或application.yml中读取配置属性。但是，我们可以使用[@PropertySource加载自定义YAML文件]()。

现在我们已经熟悉了什么是YAML文件，让我们看看如何在Spring Boot中将YAML属性作为Map注入。

## 3. 如何从YAML文件中注入Map

Spring Boot通过提供一个名为@ConfigurationProperties的方便注释，将数据外部化提升到了一个新的水平，引入此注解是为了**轻松地将配置文件中的外部属性直接注入到Java对象中**。

在本节中，我们将重点介绍如何使用@ConfigurationProperties注解将YAML属性绑定到bean类中。

首先，我们将在application.yml中定义一些键值属性：

```yaml
server:
    application:
        name: InjectMapFromYAML
        url: http://injectmapfromyaml.dev
        description: How To Inject a map from a YAML File in Spring Boot
    config:
        ips:
            - 10.10.10.10
            - 10.10.10.11
            - 10.10.10.12
            - 10.10.10.13
        filesystem:
            - /dev/root
            - /dev/md2
            - /dev/md4
    users:
        root:
            username: root
            password: rootpass
        guest:
            username: guest
            password: guestpass
```

在此示例中，我们尝试将application映射到一个简单的Map<String, String>中。同样，我们将以Map<String, List<String>>的形式注入config详细信息，以带有String键的Map形式注入users，以值形式注入属于用户定义类(Credential)的对象。

然后我们将创建一个bean类ServerProperties，以封装将我们的配置属性绑定到Map的逻辑：

```java
@Component
@ConfigurationProperties(prefix = "server")
public class ServerProperties {

	private Map<String, String> application;
	private Map<String, List<String>> config;
	private Map<String, Credential> users;

	// getters and setters

	public static class Credential {

		private String username;
		private String password;

		// getters and setters
	}
}
```

如我们所见，我们用@ConfigurationProperties修饰了ServerProperties类，**这样，我们告诉Spring将具有指定前缀的所有属性映射到ServerProperties的对象**。

回想一下，我们的应用程序也需要[为配置属性启用]()，尽管[这在大多数Spring Boot应用程序中是自动完成的](https://www.baeldung.com/spring-enable-config-properties#purpose)。

最后，我们将测试我们的YAML属性是否作为Map正确注入：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
class MapFromYamlIntegrationTest {

	@Autowired
	private ServerProperties serverProperties;

	@Test
	public void whenYamlFileProvidedThenInjectSimpleMap() {
		assertThat(serverProperties.getApplication())
			.containsOnlyKeys("name", "url", "description");

		assertThat(serverProperties.getApplication().get("name")).isEqualTo("InjectMapFromYAML");
	}

	@Test
	public void whenYamlFileProvidedThenInjectComplexMap() {
		assertThat(serverProperties.getConfig()).hasSize(2);

		assertThat(serverProperties.getConfig()
			.get("ips")
			.get(0)).isEqualTo("10.10.10.10");

		assertThat(serverProperties.getUsers()
			.get("root")
			.getUsername()).isEqualTo("root");
	}
}
```

## 4. @ConfigurationProperties与@Value

现在让我们快速比较一下@ConfigurationProperties和@Value。

尽管这两个注解都可以用于从配置文件中注入属性，但它们是完全不同的，这两个注解之间的主要区别在于每个注解都有不同的用途。

简而言之，[@Value]()**允许我们通过它的键直接注入一个特定的属性值**。但是，**@ConfigurationProperties注解将多个属性绑定到特定对象**，并通过映射对象提供对属性的访问。

一般来说，在注入配置数据时，Spring建议使用@ConfigurationProperties而不是@Value。@ConfigurationProperties提供了一种很好的方式来集中和分组结构化对象中的配置属性，我们可以稍后将其注入到其他bean中。

## 5. 总结

在这篇简短的文章中，我们讨论了如何在Spring Boot中从YAML文件注入Map，然后我们强调了@ConfigurationProperties和@Value注解之间的区别。