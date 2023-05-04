##  1. 概述

在本文中，我们将了解如何将 SwaggerAPI导入Postman。

## 2. Swagger 和 OpenAPI

Swagger 是一组用于开发和描述REST API的开源规则、规范和工具。但是，2021 年后，OpenAPI 指的是行业标准规范，而 Swagger 指的是工具。

## 3.邮递员

Postman 是一个用于构建和使用API的API平台。Postman 简化了API生命周期的每个步骤并简化了协作。我们可以使用Postman 来 测试我们的API而无需编写任何代码。

我们可以使用独立应用程序或浏览器扩展。

## 4.申请

我们可以使用任何现有的应用程序，或者我们可以[从头开始创建一个公开REST API的简单应用程序](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)。

### 4.1.Maven依赖项

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
</dependency>
```

### 4.2.Java配置

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
}
```

当我们启动应用程序时，我们可以检查 Swagger-UI 并找到每个控制器的REST API描述：

[![招摇用户界面](https://www.baeldung.com/wp-content/uploads/2022/08/1_Swagger-UI.jpg)](https://www.baeldung.com/wp-content/uploads/2022/08/1_Swagger-UI.jpg)

 

我们还可以检查为我们的REST API生成的API文档：

[![Swagger-API_Docs](https://www.baeldung.com/wp-content/uploads/2022/08/Swagger-API_Docs.jpg)](https://www.baeldung.com/wp-content/uploads/2022/08/Swagger-API_Docs.jpg)

## 5.导入Postman

有多种方法可以将API导入Postman，但在大多数情况下，它要求Swagger 或 OpenAPI 定义以某种文本格式(例如JSON)提供。

我们可以打开Postman 并导航到左侧的APIs选项，然后单击Import以查看可用的不同选项：

[![邮递员API导入](https://www.baeldung.com/wp-content/uploads/2022/08/Postman_API_Import.jpg)](https://www.baeldung.com/wp-content/uploads/2022/08/Postman_API_Import.jpg)

### 5.1. 导入文件

如果我们有可用的 SwaggerJSON文件，我们可以通过Postman 中的文件选项导入它：

[![PostmanAPI导入文件](https://www.baeldung.com/wp-content/uploads/2022/08/Postman_API_Import_File.jpg)](https://www.baeldung.com/wp-content/uploads/2022/08/Postman_API_Import_File.jpg)

 

### 5.2. 导入链接

如果我们有 Swagger-UI 链接，我们可以直接使用该链接将API导入Postman。

从 Swagger-UI 复制API链接如下：

[![招摇复制链接](https://www.baeldung.com/wp-content/uploads/2022/08/2_Swagger_Copy_Link.jpg)](https://www.baeldung.com/wp-content/uploads/2022/08/2_Swagger_Copy_Link.jpg)

并通过Postman 的相同链接导入它：

[![PostmanAPI导入链接](https://www.baeldung.com/wp-content/uploads/2022/08/Postman_API_Import_Link.jpg)](https://www.baeldung.com/wp-content/uploads/2022/08/Postman_API_Import_Link.jpg)

 

### 5.3. 通过原始文本导入

我们也可以将JSON粘贴为原始文本来导入 API：

[![PostmanAPI导入原始文本](https://www.baeldung.com/wp-content/uploads/2022/08/Postman_API_Import_RawText.jpg)](https://www.baeldung.com/wp-content/uploads/2022/08/Postman_API_Import_RawText.jpg)

 

### 5.4. 通过代码库导入

要从存储库导入 API，我们需要登录到Postman。以从 GitHub 导入为例，让我们按照以下步骤操作：

1.  导航到“代码存储库”选项卡。
2.  单击GitHub。
3.  确认 GitHub 账号并授权postmanlabs访问仓库。完成后，返回Postman 应用程序以执行进一步的步骤。
4.  在Postman 上，选择organization、repository和branch，然后单击Continue。
5.  确认我们需要导入的API ，点击导入。

## 六，总结

在本文中，我们研究了将REST API导入Postman 的不同方法。