## 1. 概述

在本教程中，我们将了解如何使用OpenAPI映射日期。我们将学习如何处理各种日期格式。

两个不同的Maven插件允许从OpenAPI规范生成代码：swagger-codegen和openapi-generator。我们将讨论如何同时使用它们。

## 2. 示例设置

首先，让我们设置一个示例。我们将编写我们的初始YAML文件和Maven插件的基本配置。

### 2.1 基础YAML文件

**我们将使用YAML文件来描述我们的API**。请注意，我们将使用OpenAPI规范的第三个版本。

我们需要为文件添加标题和版本以符合规范。此外，我们会将paths部分留空。但是，在components部分，我们将定义一个Event对象，它目前只有一个属性，即它的组织者：

```yaml
openapi: 3.0.0
info:
    title: an example api with dates
    version: 0.1.0
paths:
components:
    schemas:
        Event:
            type: object
            properties:
                organizer:
                    type: string
```

### 2.2 swagger-codegen插件配置

可以在[Maven Central仓库](https://mvnrepository.com/artifact/io.swagger/swagger-codegen-maven-plugin)中找到最新版本的swagger-codegen插件。**让我们从插件的基本配置开始**：

```xml
<plugin>
    <groupId>io.swagger.codegen.v3</groupId>
    <artifactId>swagger-codegen-maven-plugin</artifactId>
    <version>3.0.34</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>${project.basedir}/src/main/resources/static/event.yaml</inputSpec>
                <language>spring</language>
                <configOptions>
                    <java8>true</java8>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

我们现在可以执行插件：

```shell
mvn clean compile
```

Event类是根据OpenAPI规范生成的，具有构造函数、getter和setter。它的[equals()](https://www.baeldung.com/java-equals-hashcode-contracts#equals)、[hashcode()](https://www.baeldung.com/java-equals-hashcode-contracts#hashcode)和[toString()](https://www.baeldung.com/java-tostring)方法也被覆盖。

### 2.3 openapi-generator插件配置

同样，最新版本的openapi-generator插件在[Maven Central仓库](https://mvnrepository.com/artifact/org.openapitools/openapi-generator-maven-plugin)中可用。**现在让我们为它做基本配置**：

```xml
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>6.2.1</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <skipValidateSpec>true</skipValidateSpec>
                <inputSpec>${project.basedir}/src/main/resources/static/event.yaml</inputSpec>
                <generatorName>spring</generatorName>
                <configOptions>
                    <java8>true</java8>
                    <openApiNullable>false</openApiNullable>
                    <interfaceOnly>true</interfaceOnly>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

根据OpenAPI规范，我们的YAML文件中有一个空paths部分是可以的。但是，openapi-generator默认会拒绝它。因此，我们将skipValidateSpec标志设置为true。

我们还在选项列表中将openApiNullable属性设置为false，否则插件会要求我们向jackson-databing-nullable添加一个我们不需要的依赖项。

我们还将interfaceOnly设置为true，主要是为了避免生成不必要的[Spring Boot集成测试](https://www.baeldung.com/spring-boot-testing)。

在这种情况下，运行compile [Maven阶段](https://www.baeldung.com/maven-goals-phases)也会生成具有所有方法的Event类。

## 3. OpenAPI标准日期映射

OpenAPI定义了几种基本数据类型：字符串就是其中之一。**在字符串数据类型中，OpenAPI定义了两种默认格式来处理日期：date和date-time**。

### 3.1 data

**date格式是指[RFC 3339第5.6节](https://www.rfc-editor.org/rfc/rfc3339#section-5.6)定义的完整日期表示法**。例如，2023-02-08就是这样一个日期。

现在让我们将date格式的startDate属性添加到我们的Event定义中：

```yaml
startDate:
    type: string
    format: date
```

我们不需要更新Maven插件的配置。让我们再次生成Event类。我们可以看到生成的文件中出现了一个新的属性：

```java
@JsonProperty("startDate")
@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
private LocalDate startDate;
```

这两个插件之间的主要区别在于swagger-codegen不使用[@DateTimeFormat](https://www.baeldung.com/spring-date-parameters#convert-date-parameters-on-request-level)标注startDate属性。这两个插件还以相同的方式创建关联的getter、setter和构造函数。

正如我们所见，生成器的默认行为是使用[LocalDate](https://www.baeldung.com/java-8-date-time-intro#1-working-with-localdate)类作为date格式。

### 3.2 date-time

**date-time格式是指RFC 3339第5.6节定义的日期时间表示法**。例如，2023-02-08T18:04:28Z与此格式匹配。

现在让我们将date-time格式的endDate属性添加到我们的Event中：

```yaml
endDate:
    type: string
    format: date-time
```

再一次，我们不需要修改任何插件的配置。当我们再次生成Event类时，会出现一个新属性：

```java
@JsonProperty("endDate")
@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
private OffsetDateTime endDate;
```

我们对date格式所做的评论仍然有效：与openapi-generator相反，swagger-codegen不会使用@DateTimeFormat标注该属性。此外，插件创建关联的getter、setter和构造函数。

我们可以看到生成器默认使用[OffsetDateTime](https://www.baeldung.com/java-convert-date-to-offsetdatetime)类来表示date-time格式。

## 4. 使用其他标准日期类

我们现在将强制插件为每种格式使用特定的类，而不是生成默认类。

让我们编辑swagger-codegen Maven插件配置：

```xml
<configuration>
    <inputSpec>${project.basedir}/src/main/resources/static/event.yaml</inputSpec>
    <language>spring</language>
    <configOptions>
        <java8>true</java8>
        <dateLibrary>custom</dateLibrary>
    </configOptions>
    <typeMappings>
        <typeMapping>DateTime=Instant</typeMapping>
        <typeMapping>Date=Date</typeMapping>
    </typeMappings>
    <importMappings>
        <importMapping>Instant=java.time.Instant</importMapping>
        <importMapping>Date=java.util.Date</importMapping>
    </importMappings>
</configuration>
```

让我们仔细看看新行：

-   我们使用带有custom值的dateLibrary选项：这意味着我们将定义我们自己的日期类而不是使用标准类
-   在importMappings部分，我们告诉插件导入Instant和Date类并告诉它在哪里查找它们
-   **typeMappings部分是所有魔法发生的地方：我们告诉插件使用Instant来处理date-time格式，并使用Date来处理date格式**

对于openapi-generator，我们需要在完全相同的位置添加完全相同的行。结果略有不同，因为我们定义了更多选项：

```xml
<configuration>
    <skipValidateSpec>true</skipValidateSpec>
    <inputSpec>${project.basedir}/src/main/resources/static/event.yaml</inputSpec>
    <generatorName>spring</generatorName>
    <configOptions>
        <java8>true</java8>
        <dateLibrary>custom</dateLibrary>
        <openApiNullable>false</openApiNullable>
        <interfaceOnly>true</interfaceOnly>
    </configOptions>
    <typeMappings>
        <typeMapping>DateTime=Instant</typeMapping>
        <typeMapping>Date=Date</typeMapping>
    </typeMappings>
    <importMappings>
        <importMapping>Instant=java.time.Instant</importMapping>
        <importMapping>Date=java.util.Date</importMapping>
    </importMappings>
</configuration>
```

现在让我们生成文件并查看它们：

```java
import java.time.Instant;
import java.util.Date;

(...)
    @JsonProperty("startDate")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date startDate;

    @JsonProperty("endDate")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant endDate;
```

该插件确实用Date对象替换了date格式，用Instant替换了date-time格式。和以前一样，这两个插件之间的唯一区别是swagger-codegen不使用@DateTimeFormat标注属性。

最后但同样重要的是，请注意插件没有任何验证。例如，我们可以使用[java.lang.Math](https://www.baeldung.com/java-lang-math)类来模拟日期格式，代码仍然可以成功生成。

## 5. 使用自定义日期模式

我们现在将讨论最后一种可能性。如果出于某种原因，我们真的不能依赖任何标准的日期API，我们总是可以使用String来处理我们的日期。**在这种情况下，我们需要定义我们希望字符串遵循的验证模式**。

例如，让我们将ticketSales日期添加到我们的Event对象规范中。此ticketSales将被格式化为DD-MM-YYYY，例如18-07-2024：

```yaml
ticketSales:
    type: string
    description: Beginning of the ticket sales
    example: "01-01-2023"
    pattern: "[0-9]{2}-[0-9]{2}-[0-9]{4}"
```

如我们所见，我们定义了ticketSales必须匹配的[正则表达式](https://www.baeldung.com/regular-expressions-java)。请注意，此模式无法区分DD-MM-YYYY和MM-DD-YYYY。此外，我们还为此字段添加了描述和示例：由于我们不以标准方式处理日期，因此洞察力似乎很有帮助。

我们不需要对插件的配置进行任何更改。让我们使用openapi-generator生成Event类：

```java
@JsonProperty("ticketSales")
private String ticketSales;

(...)

/**
 * Beginning of the ticket sales
 * @return ticketSales
*/
@Pattern(regexp = "[0-9]{2}-[0-9]{2}-[0-9]{4}") 
@Schema(name = "ticketSales", example = "01-01-2023", description = "Beginning of the ticket sales", required = false)
public String getTicketSales() {
    return ticketSales;
}
```

正如我们所看到的，getter是用定义的@Pattern标注的。因此，我们需要向[javax.validation](https://www.baeldung.com/javax-validation#1-validation-api)添加[依赖项](https://mvnrepository.com/artifact/javax.validation/validation-api)以使其工作：

```xml
<dependency>
    <groupId>javax.validation</groupId>
    <artifactId>validation-api</artifactId>
    <version>2.0.1.Final</version>
</dependency>
```

swagger-codegen插件生成非常相似的代码。

## 6. 总结

在本文中，我们已经看到swagger-codegen和openapi-generator Maven插件都提供了用于日期和日期时间处理的内置格式。如果我们更喜欢使用其他标准的Java日期API，我们可以覆盖插件的配置。当我们真的不能使用任何日期API时，我们总是可以将日期存储为String并手动指定验证模式。