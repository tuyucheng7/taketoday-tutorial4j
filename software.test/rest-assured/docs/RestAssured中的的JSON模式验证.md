## 1. 概述

Rest-Assured库提供对测试REST API的支持，通常采用JSON格式。

有时可能需要在不详细分析响应的情况下首先了解JSON主体是否符合某种JSON格式。

在本快速教程中，**我们将了解如何根据预定义的JSON模式验证JSON响应**。

## 2. 设置

初始的Rest-Assured设置与我们[之前的文章](https://www.baeldung.com/rest-assured-tutorial)相同。

另外，我们还需要在pom.xml文件中包含json-schema-validator模块：

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>json-schema-validator</artifactId>
    <version>3.3.0</version>
    <scope>test</scope>
</dependency>
```

为确保你拥有最新版本，请点击[此链接](https://central.sonatype.com/artifact/io.rest-assured/json-schema-validator/5.3.0)。

## 3. JSON模式验证

作为JSON模式，我们将使用保存在名为event_0.json的文件中的JSON，该文件存在于类路径中：

```json
{
    "id": "390",
    "data": {
        "leagueId": 35,
        "homeTeam": "Norway",
        "visitingTeam": "England"
    },
    "odds": [
        {
            "price": "1.30",
            "name": "1"
        },
        {
            "price": "5.25",
            "name": "X"
        }
    ]
}
```

然后假设这是我们的REST API返回的所有数据后跟的通用格式，然后我们可以检查JSON响应的一致性，如下所示：

```java
@Test
void givenUrl_whenJsonResponseConformsToSchema_thenCorrect() {
	get("/events?id=390")
	    .then()
	    .assertThat()
	    .body(matchesJsonSchemaInClasspath("event_0.json"));
}
```

请注意，我们仍将从io.restassured.module.jsv.JsonSchemaValidator静态导入matchesJsonSchemaInClasspath。

## 4. JSON Schema验证设置

### 4.1 验证响应

Rest-Assured的json-schema-validator模块使我们能够通过定义自己的自定义配置规则来执行细粒度验证。

假设我们希望我们的验证始终使用JSON模式版本4：

```java
@Test
void givenUrl_whenValidatesResponseWithInstanceSettings_thenCorrect() {
	JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory
	    .newBuilder()
	    .setValidationConfiguration(
	        ValidationConfiguration.newBuilder()
	            .setDefaultVersion(SchemaVersion.DRAFTV4)
	            .freeze()).freeze();
	get("/events?id=390")
	    .then()
	    .assertThat()
	    .body(matchesJsonSchemaInClasspath("event_0.json")
	        .using(jsonSchemaFactory));
}
```

我们将通过使用JsonSchemaFactory并指定版本4 SchemaVersion并断言它在发出请求时使用该模式来执行此操作。

### 4.2 检查验证

默认情况下，json-schema-validator对JSON响应字符串运行检查验证。这意味着如果模式将odds定义为数组，如以下JSON所示：

```json
{
    "odds": [
        {
            "price": "1.30",
            "name": "1"
        },
        {
            "price": "5.25",
            "name": "X"
        }
    ]
}
```

那么验证器将始终期望一个数组作为odds的值，因此odds是一个字符串的响应将无法通过验证。因此，如果我们希望对我们的响应不那么严格，我们可以在验证期间通过首先进行以下静态导入来添加自定义规则：

```java
io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;
```

然后在验证检查设置为false的情况下执行测试：

```java
@Test
void givenUrl_whenValidatesResponseWithStaticSettings_thenCorrect() {
	get("/events?id=390")
	    .then()
	    .assertThat()
	    .body(matchesJsonSchemaInClasspath("event_0.json")
	        .using(settings()
	            .with()
	            .checkedValidation(false)));
}
```

### 4.3 全局验证配置

这些自定义非常灵活，但是对于大量的测试，我们必须为每个测试定义一个验证，这很麻烦而且不太容易维护。

为了避免这种情况，**我们可以自由地只定义一次配置并将其应用于所有测试**。

我们将验证配置为未选中并始终针对JSON模式版本3使用它：

```java
JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
    .setValidationConfiguration(ValidationConfiguration.newBuilder()
                                .setDefaultVersion(SchemaVersion.DRAFTV3)
                                .freeze())
    .freeze();
JsonSchemaValidator.settings = settings()
    .with().jsonSchemaFactory(factory)
    .and()
    .with().checkedValidation(false);
```

然后，要删除此配置，请调用reset方法：

```java
JsonSchemaValidator.reset();
```

## 5. 总结

在本文中，我们展示了如何在使用Rest-Assured时根据模式验证JSON响应。