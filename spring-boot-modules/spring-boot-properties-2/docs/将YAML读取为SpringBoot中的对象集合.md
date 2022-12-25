## 1. 概述

在这个简短的教程中，我们介绍**如何将YAML集合映射到Spring Boot中的集合**。

我们将从一些有关如何在YAML中定义集合的背景知识开始，然后我们将更深入地了解如何将YAML集合绑定到对象的List。

## 2. 快速回顾YAML中的集合

简而言之，[YAML](https://yaml.org/spec/1.2/spec.html)是一种人类可读的数据序列化标准，它提供了一种简洁明了的方式来编写配置文件。**YAML的好处在于它支持多种数据类型，例如List、Map和标量类型**。

YAML集合中的元素是使用“-”字符定义的，并且它们都共享相同的缩进级别：

```yaml
yamlconfig:
    list:
        - item1
        - item2
        - item3
        - item4
```

作为比较，基于properties文件的等效项使用索引：

```properties
yamlconfig.list[0]=item1
yamlconfig.list[1]=item2
yamlconfig.list[2]=item3
yamlconfig.list[3]=item4
```

有关更多示例，请随时查看我们关于如何[使用YAML和属性文件定义集合和Map]()的文章。

事实上，与属性文件相比，**YAML的层次结构显著增强了可读性**。YAML的另一个有趣的特性是可以[为不同的Spring Profile定义不同的属性]()。从Boot版本2.4.0开始，属性文件也可以执行此操作。

值得一提的是，Spring Boot为YAML配置提供了开箱即用的支持。根据设计，Spring Boot在启动时从application.yml加载配置属性，无需任何额外的工作。

## 3. 将YAML集合绑定到简单的对象集合

Spring Boot提供了[@ConfigurationProperties]()注解来**简化将外部配置数据映射到对象模型的逻辑**。

在本节中，我们将使用@ConfigurationProperties将YAML集合绑定到List<Object\>中。

首先我们在application.yml中定义一个简单的集合：

```yaml
application:
    profiles:
        - dev
        - test
        - prod
        - 1
        - 2
```

然后我们将创建一个简单的ApplicationProps [POJO]()来保存将我们的YAML集合绑定到对象集合的逻辑：

```java
@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationProps {

	private List<Object> profiles;

	// getter and setter
}
```

**ApplicationProps类需要使用@ConfigurationProperties修饰，以表达将所有具有指定前缀的YAML属性映射到ApplicationProps对象的意图**。

要绑定profiles集合，我们只需要定义一个List类型的字段，@ConfigurationProperties注解将处理其余的事情。

请注意，我们使用@Component将ApplicationProps类注册为普通的Spring bean，**因此，我们可以像注入任何其他Spring bean一样将其注入到其他类中**。

最后，我们将ApplicationProps bean注入到测试类中，并验证我们的profiles YAML集合是否作为List<Object\>正确注入：

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = ApplicationProps.class)
class YamlSimpleListUnitTest {

	@Autowired
	private ApplicationProps applicationProps;

	@Test
	void whenYamlList_thenLoadSimpleList() {
		assertThat(applicationProps.getProfiles().get(0)).isEqualTo("dev");
		assertThat(applicationProps.getProfiles().get(4).getClass()).isEqualTo(Integer.class);
		assertThat(applicationProps.getProfiles().size()).isEqualTo(5);
	}
}
```

## 4. 将YAML集合绑定到复杂集合

现在让我们更深入地了解如何将嵌套的YAML集合注入到复杂的结构化集合中。

首先，让我们向application.yml添加一些嵌套集合：

```yaml
application:
    # ...
    props:
        -   name: YamlList
            url: http://yamllist.dev
            description: Mapping list in Yaml to list of objects in Spring Boot
        -   ip: 10.10.10.10
            port: 8091
        -   email: support@yamllist.dev
            contact: http://yamllist.dev/contact
    users:
        -   username: admin
            password: admin@10@
            roles:
                - READ
                - WRITE
                - VIEW
                - DELETE
        -   username: guest
            password: guest@01
            roles:
                - VIEW
```

在此示例中，我们将把props属性绑定到List<Map<String, Object>>。同样，我们将users映射到用户对象集合中。

由于props条目的每个元素都拥有不同的键，因此我们可以将其作为Map的集合注入，请务必查看我们关于如何[在Spring Boot中从YAML文件注入Map的文章](使用Spring从YAML文件注入Map.md)。

然而，在users的情况下，所有元素共享相同的键，**因此为了简化其映射，我们可能需要创建一个专用的User类来将键封装为字段**：

```java
public class ApplicationProps {

	// ...

	private List<Map<String, Object>> props;
	private List<User> users;

	// getters and setters

	public static class User {

		private String username;
		private String password;
		private List<String> roles;

		// getters and setters
	}
}
```

现在我们验证我们的嵌套YAML集合是否正确映射：

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = ApplicationProps.class)
class YamlComplexListsUnitTest {

	@Autowired
	private ApplicationProps applicationProps;

	@Test
	void whenYamlNestedLists_thenLoadComplexLists() {
		assertThat(applicationProps.getUsers().get(0).getPassword()).isEqualTo("admin@10@");
		assertThat(applicationProps.getProps().get(0).get("name")).isEqualTo("YamlList");
		assertThat(applicationProps.getProps().get(1).get("port").getClass()).isEqualTo(Integer.class);
	}
}
```

## 5. 总结

在本文中，我们学习了如何将YAML集合映射到Java List中，并介绍了如何将复杂集合绑定到自定义POJO。