## 一、概述

REST-assured 库提供对测试 REST API 的支持，通常采用 JSON 格式。

有时可能需要在不详细分析响应的情况下先了解 JSON 正文是否符合某种 JSON 格式。

在本快速教程中，我们将了解如何**根据预定义的 JSON 模式验证 JSON 响应**。

## **2. 设置**

最初的 REST 保证设置与[我们之前的文章](https://www.baeldung.com/rest-assured-tutorial)相同。

此外，我们还需要在*pom.xml*文件中包含*json-schema-validator*模块：

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>json-schema-validator</artifactId>
    <version>3.3.0</version>
    <scope>test</scope>
</dependency>复制
```

为确保您拥有最新版本，请点击[此链接](https://search.maven.org/classic/#search|ga|1|a%3A"json-schema-validator")。

## **3. JSON 模式验证**

让我们看一个例子。

作为 JSON 模式，我们将使用保存在名为*event_0.json*的文件中的 JSON，该文件存在于类路径中：

```javascript
{
    "id": "390",
    "data": {
        "leagueId": 35,
        "homeTeam": "Norway",
        "visitingTeam": "England",
    },
    "odds": [{
        "price": "1.30",
        "name": "1"
    },
    {
        "price": "5.25",
        "name": "X"
    }]
}复制
```

然后假设这是我们的 REST API 返回的所有数据所遵循的通用格式，然后我们可以检查 JSON 响应的一致性，如下所示：

```java
@Test
public void givenUrl_whenJsonResponseConformsToSchema_thenCorrect() {
    get("/events?id=390").then().assertThat()
      .body(matchesJsonSchemaInClasspath("event_0.json"));
}复制
```

请注意，我们仍然会从io.restassured.module.jsv.JsonSchemaValidator 静态导入*matchesJsonSchemaInClasspath* *。*

## **4. JSON Schema 验证****设置**

### **4.1。验证响应**

REST-assured的*json-schema-validator*模块使我们能够通过定义我们自己的自定义配置规则来执行细粒度的验证。

假设我们希望我们的验证始终使用 JSON 模式版本 4：

```java
@Test
public void givenUrl_whenValidatesResponseWithInstanceSettings_thenCorrect() {
    JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder()
      .setValidationConfiguration(
        ValidationConfiguration.newBuilder()
          .setDefaultVersion(SchemaVersion.DRAFTV4).freeze())
            .freeze();
    get("/events?id=390").then().assertThat()
      .body(matchesJsonSchemaInClasspath("event_0.json")
        .using(jsonSchemaFactory));
}复制
```

我们将通过使用*JsonSchemaFactory*并指定版本 4 *SchemaVersion*并断言它在发出请求时正在使用该模式来做到这一点。

### **4.2. 检查验证**

默认情况下，*json-schema-validator*在 JSON 响应字符串上运行检查验证。这意味着如果架构将*赔率*定义为数组，如下面的 JSON 所示：

```javascript
{
    "odds": [{
        "price": "1.30",
        "name": "1"
    },
    {
        "price": "5.25",
        "name": "X"
    }]
}复制
```

那么验证器将始终期望一个数组作为*odds的值，因此，* *odds*为*String*的响应将无法通过验证。因此，如果我们希望对我们的响应不那么严格，我们可以在验证期间添加自定义规则，首先进行以下静态导入：

```java
io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;复制
```

然后在验证检查设置为*false*的情况下执行测试：

```java
@Test
public void givenUrl_whenValidatesResponseWithStaticSettings_thenCorrect() {
    get("/events?id=390").then().assertThat().body(matchesJsonSchemaInClasspath
      ("event_0.json").using(settings().with().checkedValidation(false)));
}复制
```

### **4.3. 全局验证配置**

这些定制非常灵活，但是对于大量的测试，我们必须为每个测试定义一个验证，这很麻烦并且不太易于维护。

为了避免这种情况，**我们可以自由地定义我们的配置一次并让它应用于所有测试**。

我们将验证配置为未选中并始终针对 JSON 模式版本 3 使用它：

```java
JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
  .setValidationConfiguration(
   ValidationConfiguration.newBuilder()
    .setDefaultVersion(SchemaVersion.DRAFTV3)
      .freeze()).freeze();
JsonSchemaValidator.settings = settings()
  .with().jsonSchemaFactory(factory)
      .and().with().checkedValidation(false);复制
```

然后要删除此配置，请调用 reset 方法：

```java
JsonSchemaValidator.reset();复制
```

## **5. 结论**

在本文中，我们展示了在使用 REST-assured 时如何针对模式验证 JSON 响应。