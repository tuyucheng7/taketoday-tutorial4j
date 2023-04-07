##  一、概述

在本文中，我们将了解如何将 Swagger API 导入 Postman。

## 2. Swagger 和 OpenAPI

Swagger 是一组用于开发和描述 REST API 的开源规则、规范和工具。但是，2021 年后，**OpenAPI 指的是行业标准规范**，而 Swagger 指的是工具。

## 3.邮递员

Postman 是一个用于构建和使用 API 的 API 平台。Postman 简化了 API 生命周期的每个步骤并简化了协作。**我们可以使用 Postman 来** **测试我们的 API 而无需编写任何代码**。

我们可以使用独立应用程序或浏览器扩展。

## 4.申请

我们可以使用任何现有的应用程序，或者我们可以[从头开始创建一个公开 REST API 的简单应用程序](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)。

### 4.1. Maven 依赖项

我们需要添加一些依赖项，以便将 Swagger 与 Swagger-UI 结合使用：

```java
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>3.0.0</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>3.0.0</version>
</dependency>复制
```

### 4.2. Java配置

Swagger 可以很容易地配置为：

```java
@Configuration
public class SpringFoxConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
          .select()
          .apis(RequestHandlerSelectors.any())
          .paths(PathSelectors.any())
          .build();
    }
}复制
```

当我们启动应用程序时，我们可以**检查 Swagger-UI 并找到每个控制器的 REST API 描述**：

[![招摇用户界面](https://www.baeldung.com/wp-content/uploads/2022/08/1_Swagger-UI.jpg)](https://www.baeldung.com/wp-content/uploads/2022/08/1_Swagger-UI.jpg)

 

我们还可以检查**为我们的 REST API 生成的 API 文档**：

[![Swagger-API_Docs](https://www.baeldung.com/wp-content/uploads/2022/08/Swagger-API_Docs.jpg)](https://www.baeldung.com/wp-content/uploads/2022/08/Swagger-API_Docs.jpg)

## 5.导入Postman

有多种方法可以将 API 导入 Postman，但在大多数情况下，它要求**Swagger 或 OpenAPI 定义以某种文本格式**（例如 JSON）提供。

我们可以打开 Postman 并导航到左侧的*APIs选项，然后单击**Import*以查看可用的不同选项：

[![邮递员 API 导入](https://www.baeldung.com/wp-content/uploads/2022/08/Postman_API_Import.jpg)](https://www.baeldung.com/wp-content/uploads/2022/08/Postman_API_Import.jpg)

### 5.1. 导入文件

**如果我们有可用的 Swagger JSON 文件**，我们可以通过 Postman 中的文件选项导入它：

[![Postman API 导入文件](https://www.baeldung.com/wp-content/uploads/2022/08/Postman_API_Import_File.jpg)](https://www.baeldung.com/wp-content/uploads/2022/08/Postman_API_Import_File.jpg)

 

### 5.2. 导入链接

如果我们有 Swagger-UI 链接，我们可以**直接使用该链接将 API 导入**Postman。

从 Swagger-UI 复制 API 链接如下：

[![招摇复制链接](https://www.baeldung.com/wp-content/uploads/2022/08/2_Swagger_Copy_Link.jpg)](https://www.baeldung.com/wp-content/uploads/2022/08/2_Swagger_Copy_Link.jpg)

并通过 Postman 的相同链接导入它：

[![Postman API 导入链接](https://www.baeldung.com/wp-content/uploads/2022/08/Postman_API_Import_Link.jpg)](https://www.baeldung.com/wp-content/uploads/2022/08/Postman_API_Import_Link.jpg)

 

### 5.3. 通过原始文本导入

我们也可以将**JSON 粘贴为原始文本**来导入 API：

[![Postman API 导入原始文本](https://www.baeldung.com/wp-content/uploads/2022/08/Postman_API_Import_RawText.jpg)](https://www.baeldung.com/wp-content/uploads/2022/08/Postman_API_Import_RawText.jpg)

 

### 5.4. 通过代码库导入

要从存储库导入 API，我们**需要登录到 Postman**。以从 GitHub 导入为例，让我们按照以下步骤操作：

1.  导航到*“代码存储库”*选项卡。
2.  单击*GitHub*。
3.  确认 GitHub 账号并**授权\*postmanlabs\*访问仓库**。完成后，返回 Postman 应用程序以执行进一步的步骤。
4.  在 Postman 上，选择***organization***、***repository***和***branch***，然后单击*Continue*。
5.  确认我们需要导入的**API ，点击***导入*。

## 六，结论

在本文中，我们研究了将 REST API 导入 Postman 的不同方法。