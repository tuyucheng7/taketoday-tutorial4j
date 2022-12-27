## 1. 概述

在本教程中，我们将学习如何使用[swagger-maven-plugin](https://search.maven.org/search?q=a:swagger-maven-plugin)插件在Swagger中记录枚举，并在Swagger编辑器中验证生成的JSON文档。

## 2. 什么是Swagger？

[Swagger](https://www.baeldung.com/spring-boot-rest-client-swagger-codegen)是一种用于定义基于Rest的API的开源工具，在当今世界，大多数组织都在转向微服务和API优先的方法，Swagger在设计和记录API方面非常方便。它还提供了各种工具，如SwaggerEditor、Swagger UI和SwaggerCodeGen来辅助API开发。

**此外，Swagger是OpenAPI规范或OAS的实现**，它定义了RestAPI开发的一套标准；因此，它可以帮助全球的组织标准化编写API的过程。

我们的应用程序生成的JSON文件也将遵循OpenAPI规范。

让我们试着理解[枚举](https://www.baeldung.com/a-guide-to-java-enums)在Swagger中的重要性。一些API需要用户坚持使用一组特定的预定义值，这些预定义的常量值称为枚举。同样，当Swagger公开API时，我们希望确保用户从这个预定义集中选择一个值而不是自由文本。**换句话说，我们需要在swagger.json文件中记录枚举，以便用户知道可能的值**。

## 3. 实现

让我们以REST API为例并跳转到实现，**我们将实现一个POST API来为组织雇用员工担任特定角色。但是，角色只能是以下角色之一：Engineer、Clerk、Driver或Janitor**。

我们将创建一个名为Role的枚举，其中包含员工角色的所有可能值，并创建一个将角色作为其属性之一的Employee类，让我们看一下UML图，以便更好地理解类及其关系：

<img src="../assets/img_6.png">

为了在Swagger中记录这一点，首先，我们将在我们的应用程序中导入并配置[swagger-maven-plugin](https://search.maven.org/search?q=a:swagger-maven-plugin)。其次，我们将在我们的代码中添加所需的注解，最后，我们将构建项目并在Swagger编辑器中验证生成的Swagger文档或swagger.json。

### 3.1 导入和配置插件

我们将使用[swagger-maven-plugin](https://search.maven.org/search?q=a:swagger-maven-plugin)插件，我们需要将其作为依赖项添加到我们应用程序的pom.xml中：

```xml
<dependency>
    <groupId>com.github.kongchen</groupId>
    <artifactId>swagger-maven-plugin</artifactId>
    <version>3.1.1</version>
</dependency>
```

此外，为了配置和启用这个插件，我们需要将它添加到pom.xml的<plugins\>部分：

-   locations：此标签指定包含@Api的包或类，以分号分隔
-   info：此标签提供API的元数据，Swagger-ui使用此数据显示信息
-   swaggerDirectory：这个标签定义了swagger.json文件的路径

```xml
<plugin>
    <groupId>com.github.kongchen</groupId>
    <artifactId>swagger-maven-plugin</artifactId>
    <version>3.1.1</version>
    <configuration>
        <apiSources>
            <apiSource>
                <springmvc>false</springmvc>
                <locations>cn.tuyucheng.taketoday.swaggerenums.controller</locations>
                <schemes>http,https</schemes>
                <host>tuyucheng.com</host>
                <basePath>/api</basePath>
                <info>
                    <title>Tuyucheng - Document Enum</title>
                    <version>v1</version>
                    <description>This is a Tuyucheng Document Enum Sample Code</description>
                    <contact>
                        <email>pmurria@tuyucheng.com</email>
                        <name>Test</name>
                    </contact>
                    <license>
                        <url>https://www.apache.org/licenses/LICENSE-2.0.html</url>
                        <name>Apache 2.0</name>
                    </license>
                </info>
                <swaggerDirectory>generated/swagger-ui</swaggerDirectory>
            </apiSource>
        </apiSources>
    </configuration>
    <executions>
        <execution>
            <phase>compile</phase>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### 3.2 记录枚举

为了在Swagger中记录枚举，**我们需要使用注解@ApiModel来声明模型**。

在此示例中，我们创建了一个具有四个可能值(Engineer、Clerk、Driver和Janitor)的枚举Role，由于我们需要记录这个枚举，因此我们将向枚举Role添加@ApiModel注解。换句话说，这会让Swagger知道模型的存在，在Employee类中，我们将使用@ApiModel注解标注Employee并使用@ApiModelProperty注解标注Role。

我们的Employee、Role和HireController将如下所示：

```java
@ApiModel
public class Employee {
    @ApiModelProperty
    public Role role;

    // standard setters and getters
}
```

```java
@ApiModel
public enum Role {
    Engineer, Clerk, Driver, Janitor
}
```

接下来，我们将创建一个带有@Path作为“/hire”的API，并将Employee模型用作hireEmployee方法的输入参数，我们必须将@Api添加到我们的HireController中，以便[swagger-maven-plugin](https://search.maven.org/search?q=a:swagger-maven-plugin)知道并且应该考虑将其用于记录：

```java
@Api
@Path(value="/hire")
@Produces({"application/json"})
public class HireController {

    @POST
    @ApiOperation(value = "This method is used to hire employee with a specific role")
    public String hireEmployee(@ApiParam(value = "role", required = true) Employee employee) {
        return String.format("Hired for role: %s", employee.role.name());
    }
}
```

### 3.3 生成Swagger文档

要构建我们的项目并生成Swagger文档，请运行以下命令：

```shell
mvn clean install
```

构建完成后，插件将在generated/swagger-ui或插件中配置的位置生成swagger.json文件：

<img src="../assets/img_7.png">

查看文件定义，我们将看到在员工属性中记录的枚举Role及其所有可能的值。

```json
{
    // ...
    "definitions" : {
        "Employee" : {
            "type" : "object",
            "properties" : {
                "role" : {
                    "type" : "string",
                    "enum" : [ "Engineer", "Clerk", "Driver", "Janitor" ]
                }
            }
        }
    }
}
```

现在，我们将使用[在线Swagger编辑器](https://editor.swagger.io/)可视化生成的JSON并查找枚举Role：

<img src="../assets/img_8.png">

## 4. 总结

在本教程中，我们讨论了什么是Swagger，并了解了OpenAPI规范及其在组织API开发中的重要性。此外，我们使用[swagger-maven-plugin](https://search.maven.org/search?q=a:swagger-maven-plugin)插件创建并记录了包含枚举的示例API。最后，为了验证输出，我们使用Swagger编辑器来可视化生成的JSON文档。