## 1. 概述

[JSON-LD](https://json-ld.org/)是一种基于 JSON 的[RDF](https://www.w3.org/TR/rdf11-concepts/)格式，用于表示[关联数据](https://www.w3.org/DesignIssues/LinkedData.html)。它可以使用超媒体功能扩展现有的 JSON 对象；换句话说，以机器可读的方式包含链接的能力。

在本教程中，我们将研究几个基于 Jackson 的选项，以将 JSON-LD 格式直接序列化和反序列化为 POJO。我们还将介绍 JSON-LD 的基本概念，这将使我们能够理解示例。

## 2. 基本概念

第一次看到 JSON-LD 文档时，我们注意到一些成员名称以@字符开头。这些是 JSON-LD 关键字，它们的值帮助我们理解文档的其余部分。

要浏览 JSON-LD 的世界并理解本教程，我们需要了解四个关键字：

-   @context是 JSON 对象的描述，包含解释文档所需的一切的键值映射
-   @vocab是@context 中的一个可能的键，它引入了一个默认词汇表，使@context对象更短
-   @id是用于标识链接的关键字，可以作为资源属性来表示指向资源本身的直接链接，也可以作为@type值来将任何字段标记为链接
-   @type是在资源级别或@context中标识资源类型的关键字；例如，定义嵌入资源的类型

## 3. Java序列化

在我们继续之前，我们应该看看我们以前的教程来刷新我们对[Jackson ObjectMapper、](https://www.baeldung.com/jackson-object-mapper-tutorial) [Jackson Annotations](https://www.baeldung.com/jackson-annotations)和[自定义 Jackson Serializers](https://www.baeldung.com/jackson-custom-serialization)的记忆。

已经熟悉 Jackson，我们可能会意识到我们可以使用@JsonProperty注解轻松地将任何 POJO 中的两个自定义字段序列化为@id和@type 。但是，手动编写@context可能需要大量工作，而且容易出错。

因此，为了避免这种容易出错的方法，让我们仔细看看我们可以用于@context生成的两个库。不幸的是，它们都不能生成 JSON-LD 的所有特性，但我们稍后也会看看它们的缺点。

## 4. Jackson-Jsonld 连载

[Jackson-Jsonld](https://github.com/io-informatics/jackson-jsonld)是一个 Jackson 模块，可以方便地对 POJO 进行注解以生成 JSON-LD 文档。

### 4.1. Maven 依赖项

首先，让我们将[jackson-jsonld](https://search.maven.org/artifact/com.io-informatics.oss/jackson-jsonld)添加为pom.xml的依赖项：

```xml
<dependency>
    <groupId>com.io-informatics.oss</groupId>
    <artifactId>jackson-jsonld</artifactId>
    <version>0.1.1</version>
</dependency>
```

### 4.2. 例子

然后，让我们创建我们的示例 POJO 并为@context生成注解它：

```java
@JsonldResource
@JsonldNamespace(name = "s", uri = "http://schema.org/")
@JsonldType("s:Person")
@JsonldLink(rel = "s:knows", name = "knows", href = "http://example.com/person/2345")
public class Person {
    @JsonldId
    private String id;
    @JsonldProperty("s:name")
    private String name;

    // constructor, getters, setters
}
```

让我们解构这些步骤以了解我们所做的事情：

-   使用@JsonldResource我们将 POJO 标记为 JSON-LD 资源进行处理
-   在@JsonldNamespace中，我们为要使用的词汇定义了一个速记
-   我们在@JsonldType中指定的参数 将成为资源的@type
-   我们使用@JsonldLink注解来添加资源链接。处理时，名称参数将用作字段名称，并作为键添加到@context。 href将是字段值，rel将是@context中的映射值
-   我们用@JsonldId标记的字段将成为资源的@id
-   我们在@JsonldProperty中指定的参数将成为映射到@context中的字段名称的值

接下来，让我们生成 JSON-LD 文档。

首先，我们应该在ObjectMapper中注册JsonldModule。该模块包含一个自定义的序列化程序，Jackson 会将其用于标有@JsonldResource注解的 POJO 。

然后，我们将继续使用ObjectMapper生成 JSON-LD 文档：

```java
ObjectMapper objectMapper = new ObjectMapper();
objectMapper.registerModule(new JsonldModule());

Person person = new Person("http://example.com/person/1234", "Example Name");
String personJsonLd = objectMapper.writeValueAsString(person);
```

因此，personJsonLd变量现在应该包含：

```json
{
  "@type": "s:Person",
  "@context": {
    "s": "http://schema.org/",
    "name": "s:name",
    "knows": {
      "@id": "s:knows",
      "@type": "@id"
    }
  },
  "name": "Example Name",
  "@id": "http://example.com/person/1234",
  "knows": "http://example.com/person/2345"
}
```

### 4.3. 注意事项

在我们为项目选择这个库之前，我们应该考虑以下几点：

-   使用@vocab关键字是不可能的，因此我们必须使用@JsonldNamespace来提供解析字段名称的速记，或者每次都写出完整的国际化资源标识符(IRI)
-   我们只能在编译时定义链接，所以为了添加链接运行时，我们需要使用反射来更改注解中的那个参数

## 5. 使用 Hydra-Jsonld 序列化

Hydra-Jsonld 是[Hydra-Java](https://github.com/dschulten/hydra-java/)库的一个模块，主要用于为 Spring 应用程序创建方便的 JSON-LD 响应。它使用[Hydra Vocabulary](http://www.hydra-cg.com/spec/latest/core/)使 JSON-LD 文档更具表现力。

然而，Hydra-Jsonld 模块包含一个 Jackson Serializer和一些注解，我们可以使用它们在 Spring Framework 之外生成 JSON-LD 文档。

### 5.1. Maven 依赖项

首先，让我们将[hydra-jsonld](https://search.maven.org/artifact/de.escalon.hypermedia/hydra-jsonld)的依赖项添加到pom.xml中：

```xml
<dependency>
    <groupId>de.escalon.hypermedia</groupId>
    <artifactId>hydra-jsonld</artifactId>
    <version>0.4.2</version>
</dependency>
```

### 5.2. 例子

其次，让我们为@context生成注解我们的 POJO。

Hydra-Jsonld 自动生成一个默认的@context，不需要注解。如果我们对默认值感到满意，我们只需添加@id 即可获得有效的 JSON-LD 文档。

默认词汇表将是[schema.org](https://schema.org/)词汇表，@type 是 Java类名，POJO 的公共属性都将包含在生成的 JSON-LD 文档中。

在这个例子中，让我们用自定义值覆盖这些默认值：

```java
@Vocab("http://example.com/vocab/")
@Expose("person")
public class Person {
    private String id;
    private String name;

    // constructor

    @JsonProperty("@id")
    public String getId() {
        return id;
    }

    @Expose("fullName")
    public String getName() {
        return name;
    }
}
```

同样，让我们仔细看看所涉及的步骤：

-   与 Jackson-Jsonld 示例相比，由于 Hydra-Jsonld 在 Spring 框架之外的限制，我们从 POJO 中省略了knows字段
-   我们使用@Vocab注解设置我们的首选词汇表
-   通过在类上使用@Expose注解，我们设置了不同的资源@type
-   我们在属性上使用了相同的@Expose注解，将其映射设置为@context中的自定义值
-   为了从属性生成@id，我们使用了来自 Jackson的@JsonProperty注解

接下来，让我们配置一个 Jackson模块的实例，我们可以在ObjectMapper中注册它。我们将添加JacksonHydraSerializer作为BeanSerializerModifier，以便它可以应用于所有正在序列化的 POJO：

```java
SimpleModule getJacksonHydraSerializerModule() {
    return new SimpleModule() {
        @Override
        public void setupModule(SetupContext context) {
            super.setupModule(context);

            context.addBeanSerializerModifier(new BeanSerializerModifier() {
                @Override
                public JsonSerializer<?> modifySerializer(
                  SerializationConfig config, 
                  BeanDescription beanDesc, 
                  JsonSerializer<?> serializer) {
                    if (serializer instanceof BeanSerializerBase) {
                        return new JacksonHydraSerializer((BeanSerializerBase) serializer);
                    } else {
                        return serializer;
                    }
                }
            });
        }
    };
}
```

那我们就把Module注册到ObjectMapper中使用吧。我们还应该将ObjectMapper设置为仅包含非空值以生成有效的 JSON-LD 文档：

```java
ObjectMapper objectMapper = new ObjectMapper();
objectMapper.registerModule(getJacksonHydraSerializerModule());
objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

Person person = new Person("http://example.com/person/1234", "Example Name");

String personJsonLd = objectMapper.writeValueAsString(person);
```

现在，personJsonLd变量应该包含：

```json
{
  "@context": {
    "@vocab": "http://example.com/vocab/",
    "name": "fullName"
  },
  "@type": "person",
  "name": "Example Name",
  "@id": "http://example.com/person/1234"
}
```

### 5.3. 注意事项

尽管在 Spring 框架之外使用 Hydra-Jsonld 在技术上是可行的，但它最初是为与[Spring-HATEOAS](https://www.baeldung.com/spring-hateoas-tutorial)一起使用而设计的。因此，无法像我们在 Jackson-Jsonld 中看到的那样生成带有注解的链接。另一方面，它们是为某些特定于 Spring 的类自动生成的。

在我们为项目选择这个库之前，我们应该考虑以下几点：

-   将它与 Spring Framework 一起使用将启用其他功能
-   如果我们不使用 Spring 框架，就没有简单的方法来生成链接
-   我们不能禁用@vocab的使用，我们只能覆盖它

## 6. 使用 Jsonld-Java 和 Jackson 进行反序列化

[Jsonld-Java](https://github.com/jsonld-java/jsonld-java)是 JSON-LD 1.0 规范和 API 的Java实现，遗憾的是它不是最新版本。

对于 1.1 规范版本的实现，请查看[Titanium JSON-LD](https://github.com/filip26/titanium-json-ld)库。

要反序列化 JSON-LD 文档，让我们使用 JSON-LD API 功能(称为压缩)将其转换为我们可以使用ObjectMapper映射到 POJO 的格式。

### 6.1. Maven 依赖项

首先，让我们添加对[jsonld-java](https://search.maven.org/artifact/com.github.jsonld-java/jsonld-java)的依赖：

```xml
<dependency>
    <groupId>com.github.jsonld-java</groupId>
    <artifactId>jsonld-java</artifactId>
    <version>0.13.0</version>
</dependency>
```

### 6.2. 例子

让我们使用这个 JSON-LD 文档作为我们的输入：

```json
{
  "@context": {
    "@vocab": "http://schema.org/",
    "knows": {
      "@type": "@id"
    }
  },
  "@type": "Person",
  "@id": "http://example.com/person/1234",
  "name": "Example Name",
  "knows": "http://example.com/person/2345"
}
```

为了简单起见，假设我们在名为inputJsonLd的String变量中拥有文档的内容。

首先，让我们压缩它并将其转换回String：

```java
Object jsonObject = JsonUtils.fromString(inputJsonLd);
Object compact = JsonLdProcessor.compact(jsonObject, new HashMap<>(), new JsonLdOptions());
String compactContent = JsonUtils.toString(compact);
```

-   我们可以使用 JsonUtils 中的方法解析和编写 JSON-LD 对象，它是 Jsonld-Java 库的一部分
-   当使用compact方法时，我们可以使用一个空的Map作为第二个参数。这样，压缩算法将生成一个简单的 JSON 对象，其中的键被解析为它们的 IRI 形式

compactContent变量应包含：

```json
{
  "@id": "http://example.com/person/1234",
  "@type": "http://schema.org/Person",
  "http://schema.org/knows": {
    "@id": "http://example.com/person/2345"
  },
  "http://schema.org/name": "Example Name"
}
```

其次，让我们用 Jackson 注解定制我们的 POJO 以适应这样的文档结构：

```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class Person {
    @JsonProperty("@id")
    private String id;
    @JsonProperty("http://schema.org/name")
    private String name;
    @JsonProperty("http://schema.org/knows")
    private Link knows;

    // constructors, getters, setters

    public static class Link {
        @JsonProperty("@id")
        private String id;

        // constructors, getters, setters
    }
}
```

最后，让我们将 JSON-LD 映射到 POJO：

```java
ObjectMapper objectMapper = new ObjectMapper();
Person person = objectMapper.readValue(compactContent, Person.class);
```

## 七. 总结

在本文中，我们研究了两个基于 Jackson 的库，用于将 POJO 序列化为 JSON-LD 文档，以及一种将 JSON-LD 反序列化为 POJO 的方法。

正如我们所强调的，这两个序列化库都有缺点，我们在使用它们之前应该考虑这些缺点。如果我们需要使用比这些库所能提供的更多的 JSON-LD 特性，我们可以通过具有 JSON-LD 输出格式的 RDF 库来创建我们的文档。