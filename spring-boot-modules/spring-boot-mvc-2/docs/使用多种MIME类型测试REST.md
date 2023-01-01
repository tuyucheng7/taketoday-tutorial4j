## 1. 概述

**本文将重点测试具有多种媒体类型/表示的REST服务**。

我们将编写能够在API支持的多种表示类型之间切换的集成测试，目标是能够使用完全相同的服务URI运行完全相同的测试，只是要求不同的媒体类型。

## 2. 目标

任何REST API都需要使用一种或多种媒体类型将其资源公开为表示形式，**客户端将设置Accept标头以选择它向服务请求的表示类型**。

由于资源可以有多种表示形式，因此服务器必须实现一种负责选择正确表示形式的机制，这也称为内容协商。

因此，如果客户端请求application/xml，那么它应该得到资源的XML表示。如果它要求application/json，那么它应该得到JSON。

## 3. 测试基础设施

我们将从为编组器定义一个简单的接口开始，这将是允许测试在不同媒体类型之间切换的主要抽象：

```java
public interface IMarshaller {
    // ...
    String getMime();
}
```

然后我们需要一种方法来根据某种形式的外部配置来初始化正确的编组器。

**为此，我们将使用Spring FactoryBean来初始化编组器和一个简单的属性来确定使用哪个编组器**：

```java
@Component
@Profile("test")
public class TestMarshallerFactory implements FactoryBean<IMarshaller> {

	@Autowired
	private Environment env;

	public IMarshaller getObject() {
		String testMime = env.getProperty("test.mime");
		if (testMime != null) {
			return switch (testMime) {
				case "json" -> new JacksonMarshaller();
				case "xml" -> new XStreamMarshaller();
				default -> throw new IllegalStateException();
			};
		}

		return new JacksonMarshaller();
	}

	public Class<IMarshaller> getObjectType() {
		return IMarshaller.class;
	}

	public boolean isSingleton() {
		return true;
	}
}
```

让我们看看这个：

-   首先，这里使用了Spring 3.1中引入的新Environment抽象,有关更多信息，请查看有关[在Spring中使用属性的详细文章](https://www.baeldung.com/properties-with-spring)
-   我们从Environment中检索test.mime属性并使用它来确定要创建哪个编组器-一些Java 7 Switch在这里工作的字符串语法
-   接下来，默认编组器，以防根本未定义该属性，将成为支持JSON的Jackson编组器
-   最后，这个BeanFactory仅在测试场景中有效，因为我们使用了@Profile支持，它也在Spring 3.1中引入

就是这样，该机制能够根据test.mime属性的值在编组器之间切换。

## 4. JSON和XML编组器

继续，我们需要实际的编组器实现，每个支持的媒体类型都有一个。

对于JSON，我们将使用Jackson作为底层库：

```java
public class JacksonMarshaller implements IMarshaller {
    private ObjectMapper objectMapper;

    public JacksonMarshaller() {
        super();
        objectMapper = new ObjectMapper();
    }

    // ...

    @Override
    public String getMime() {
        return MediaType.APPLICATION_JSON.toString();
    }
}
```

对于XML支持，编组器使用XStream：

```java
public class XStreamMarshaller implements IMarshaller {
    private XStream xstream;

    public XStreamMarshaller() {
        super();
        xstream = new XStream();
    }

    // ...

    public String getMime() {
        return MediaType.APPLICATION_XML.toString();
    }
}
```

**请注意，这些编组器本身并不是Spring bean**，原因是它们将被TestMarshallerFactory引导到Spring上下文中；没有必要直接让它们成为组件。

## 5. 使用JSON和XML服务

此时，我们应该能够针对已部署的服务运行完整的集成测试。使用编组器很简单：我们将在测试中注入一个IMarshaller：

```java
@ActiveProfiles({ "test" })
public abstract class SomeRestLiveTest {

    @Autowired
    private IMarshaller marshaller;

    // tests
    // ...
}
```

**Spring将根据test.mime属性的值决定要注入的确切编组器**。

如果我们不为此属性提供值，TestMarshallerFactory将简单地退回到默认编组器-JSON编组器。

## 6. Maven和Jenkins

如果Maven设置为针对已部署的REST服务运行集成测试，那么我们可以使用以下方式运行它：

```shell
mvn test -Dtest.mime=xml
```

或者，如果此构建使用Maven生命周期的集成测试阶段：

```bash
mvn integration-test -Dtest.mime=xml
```

有关如何设置Maven构建以运行集成测试的更多详细信息，请参阅[使用Maven进行集成测试](https://www.baeldung.com/integration-testing-with-the-maven-cargo-plugin)一文。

使用Jenkins，我们必须配置作业：

```shell
This build is parametrized
```

并添加了String参数：test.mime=xml。


一个常见的Jenkins配置将不得不让作业针对已部署的服务运行同一组集成测试，一个使用XML，另一个使用JSON表示。

## 7. 结论

本文展示了如何测试使用多种表示形式的REST API，大多数API确实在多个表示下发布它们的资源，因此测试所有这些是至关重要的。事实上，我们可以对所有这些使用完全相同的测试，这真是太酷了。