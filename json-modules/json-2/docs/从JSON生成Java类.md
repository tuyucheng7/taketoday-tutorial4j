## 1. 概述

在某些情况下，我们需要使用 JSON 文件创建Java类，也称为[POJO 。](https://www.baeldung.com/java-pojo-class)[这是可能的，无需使用方便的jsonschema2pojo](https://github.com/joelittlejohn/jsonschema2pojo) 库从头开始编写整个类。

在本教程中，我们将了解如何使用此库从 JSON 对象创建Java类。

## 2.设置

[我们可以使用jsonschema2pojo-core](https://search.maven.org/search?q=g: org.jsonschema2pojo a: jsonschema2pojo-core)依赖项将 JSON 对象转换为Java类：

```xml
<dependency>
    <groupId>org.jsonschema2pojo</groupId>
    <artifactId>jsonschema2pojo-core</artifactId>
    <version>1.1.1</version>
</dependency>
```

## 3. JSON 到Java类的转换

让我们看看如何使用jsonschema2pojo库编写程序，它将 JSON 文件转换为Java类。

首先，我们将创建一个方法convertJsonToJavaClass将 JSON 文件转换为 POJO 类并接受四个参数：

-   一个inputJson文件 URL
-   将生成 POJO的outputJavaClassDirectory
-   POJO 类所属的packageName和
-   输出 POJO className。

然后，我们将定义此方法中的步骤：

-   我们将从创建JCodeModel类的对象开始，它将生成Java类
-   然后，我们将定义jsonschema2pojo的配置，让程序识别输入源文件是 JSON(getSourceType方法)
-   此外，我们会将此配置传递给[RuleFactory](https://github.com/joelittlejohn/jsonschema2pojo/blob/888884421d8357cb8d5537b3d9ffb27cca278edc/jsonschema2pojo-core/src/main/java/org/jsonschema2pojo/rules/RuleFactory.java)，它将用于为此映射创建类型生成规则
-   我们将使用这个工厂和SchemaGenerator对象创建一个[SchemaMapper](https://github.com/joelittlejohn/jsonschema2pojo/blob/master/jsonschema2pojo-core/src/main/java/org/jsonschema2pojo/SchemaMapper.java)，它从提供的 JSON 生成Java类型
-   最后，我们将调用JCodeModel的构建方法来创建输出类

让我们看看实现：

```java
public void convertJsonToJavaClass(URL inputJsonUrl, File outputJavaClassDirectory, String packageName, String javaClassName) 
  throws IOException {
    JCodeModel jcodeModel = new JCodeModel();

    GenerationConfig config = new DefaultGenerationConfig() {
        @Override
        public boolean isGenerateBuilders() {
            return true;
        }

        @Override
        public SourceType getSourceType() {
            return SourceType.JSON;
        }
    };

    SchemaMapper mapper = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(config), new SchemaStore()), new SchemaGenerator());
    mapper.generate(jcodeModel, javaClassName, packageName, inputJsonUrl);

    jcodeModel.build(outputJavaClassDirectory);
}
```

## 4. 输入输出

让我们使用这个示例 JSON 来执行程序：

```json
{
  "name": "Baeldung",
  "area": "tech blogs",
  "author": "Eugen",
  "id": 32134,
  "topics": [
    "java",
    "kotlin",
    "cs",
    "linux"
  ],
  "address": {
    "city": "Bucharest",
    "country": "Romania"
  }
}
```

执行程序后，它会在给定目录中创建以下Java类：

```java
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"name", "area", "author", "id", "topics", "address"})
@Generated("jsonschema2pojo")
public class Input {

    @JsonProperty("name")
    private String name;
    @JsonProperty("area")
    private String area;
    @JsonProperty("author")
    private String author;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("topics")
    private List<String> topics = new ArrayList<String>();
    @JsonProperty("address")
    private Address address;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    // getters & setters
    // hashCode & equals
    // toString
}
```

请注意，它因此也为嵌套的 JSON 对象创建了一个新的地址类：

```java
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"city", "country"})
@Generated("jsonschema2pojo")
public class Address {

    @JsonProperty("city")
    private String city;
    @JsonProperty("country")
    private String country;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    // getters & setters
    // hashCode & equals
    // toString
}
```

我们也可以通过简单地访问[jsonschema2pojo.org](https://www.jsonschema2pojo.org/)来实现所有这些。jsonschema2pojo工具采用JSON(或 YAML)模式文档并生成 DTO 样式的Java类。它提供了许多你可以选择包含在Java类中的选项，包括构造函数以及hashCode、equals和toString方法。

## 5.总结

在本教程中，我们介绍了如何使用jsonschema2pojo库通过示例从 JSON 创建Java类。