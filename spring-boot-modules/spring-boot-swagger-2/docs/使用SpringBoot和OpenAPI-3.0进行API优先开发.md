## 1. 简介

软件工程行业越来越依赖于Web API。[云计算](https://www.baeldung.com/cs/cloud-computing-saas-vs-paas-vs-iaas)和[HTTP](https://www.baeldung.com/cs/http-versions)的日益普及或许可以解释这一点。

软件开发团队必须确保他们设计有用且用户友好的API。**传统开发方法的主要挑战是在设计API契约和实现新产品的业务逻辑的同时保持敏捷性**。

在本文中，我们将介绍使用Spring Boot和Open API规范3.0的API优先开发。**这种方法通过及时的API设计反馈、快速失败流程和并行工作来提高团队的沟通和敏捷性**。

## 2. 什么是Open API规范

[OpenAPI Specification](https://www.openapis.org/)(OAS)标准化了如何创建API设计文档。使用OAS的API优先方法的典型工作流程如下：

-   团队创建设计文档并与相关人员共享以获取反馈和迭代更改。
-   当团队和利益相关者就API设计达成一致时，开发人员使用文档生成器工具使用该文档生成服务器端框架代码。
-   最后，开发人员开始使用之前生成的代码处理API的业务逻辑。

[OAS 3.1](https://spec.openapis.org/oas/v3.1.0)允许指定HTTP资源、动词、响应代码、数据模型、媒体类型、安全模式和其他API组件。

## 3. 为什么使用API优先开发

[敏捷开发](https://www.baeldung.com/cs/agile-programming)是软件行业中最常用的方法之一。**敏捷意味着尽可能快地构建一些小的东西，要么快速失败并改变初始设计，要么继续前进，逐步添加小的更改**。

从敏捷团队的角度来看，API优先开发具有以下几个优势：

-   提供一种快速反馈API设计的方法
-   创建针对API合同协议的单一通信渠道
-   允许参与API设计的人员并行工作

为了充分理解API优先方法的优势，我们将比较两个敏捷团队的工作流场景。第一个团队使用传统方法，而第二个使用API优先开发：

<img src="../assets/img_9.png">

在传统方案中，开发人员被分配来构建新产品功能。通常，该开发人员通过首先实现业务逻辑然后将该逻辑连接到代码内API设计来创建新功能。因此，**仅当开发人员完成该功能的所有代码更改时，API才可供涉众审查。因此，造成API合同审查和协议的缓慢和错误传达**。

在API优先开发中，设计人员在业务逻辑开发阶段之前创建API协定文档。该文档在产品利益相关者之间提供了一种通用语言，用于评估构建工作、提供及时反馈、创建测试用例、记录API等。**因此，我们可以在浪费任何时间开发应用程序之前更改初始设计或继续使用它来变得更加敏捷**。

**使用API优先开发的另一个原因是，在创建文档后，多个人可以并行处理同一产品功能**，例如：

-   产品经理评估风险、创建新功能和管理时间。
-   QA分析师构建他们的测试场景。
-   技术编写者记录API。
-   开发人员实现业务逻辑代码。

## 4. 定义API契约

我们将使用银行演示REST API来说明API优先方法工作流。该API允许两种操作：获取余额和存款金额。为清楚起见，下表显示了这些操作的资源路径、HTTP谓词和响应代码(RC)：

|          | HTTP动词 |资源       | 失败RC      |成功RC          |
|--------|--------|----------|-----------|-----------------|
|获取余额| GET    |/account      | 404–未找到帐户 |200–获取余额信息|
|存款金额| POST   |/account/deposit| 404–未找到帐户 |204–存款操作完成|

现在，我们可以创建OAS API规范文件。我们将使用[Swagger在线编辑器](https://editor.swagger.io/)，这是一种将YAML解释为Swagger文档的在线解决方案。

### 4.1 API的顶级上下文

让我们从定义API的顶级上下文开始。为此，请转到[Swagger编辑器](https://editor.swagger.io/)并将编辑器左侧的内容替换为以下YAML代码：

```yaml
openapi: 3.0.3
info:
    title: Banking API Specification for account operations
    description: |-
        A simple banking API that allows two operations:
        - get account balance given account number
        - deposit amount to a account 
    version: 1.0.0
servers:
    -   url: https://testenvironment.org/api/v1
    -   url: https://prodenvironment.org/api/v1
tags:
    -   name: accounts
        description: Operations between bank accounts
```

注意：现在不要介意这些错误。它们不会阻碍我们的工作流程，并且会在我们进入下一节时逐渐消失。

让我们分别检查每个关键字：

-   openapi：使用的OAS版本
-   title：API的简称
-   description：API职责的描述
-   version：API的当前版本，例如1.0.0
-   servers：客户端可以访问API的可用机器
-   tags：一组唯一标签，用于对子集中的API操作进行分组

### 4.2 公开API路径

现在，让我们创建前面描述的GET /account和POST /account/depositAPI端点。为此，请在YAML编辑器的根级别添加以下内容：

```yaml
paths:
    /account:
        get:
            tags:
                - accounts
            summary: Get account information
            description: Get account information using account number
            operationId: getAccount
            responses:
                200:
                    description: Success
                    content:
                        application/json:
                            schema:
                                $ref: '#/components/schemas/Account'
                404:
                    description: Account not found
                    content:
                        application/json:
                            schema:
                                $ref: '#/components/schemas/AccountNotFoundError'
    /account/deposit:
        post:
            tags:
                - accounts
            summary: Deposit amount to account
            description: Initiates a deposit operation of a desired amount to the account specified
            operationId: depositToAccount
            requestBody:
                description: Account number and desired amount to deposit
                content:
                    application/json:
                        schema:
                            $ref: '#/components/schemas/DepositRequest'
                required: true
            responses:
                204:
                    description: Success
                404:
                    description: Account not found
                    content:
                        application/json:
                            schema:
                                $ref: '#/components/schemas/AccountNotFoundError'
```

注意：同样，YAML解释器将显示一些错误，我们将在第4.3节中解决。现在，我们可以忽略它们。

上面的文档中发生了很多事情。让我们通过单独查看每个关键字将其分解为多个部分：

-   paths：定义API资源/account和/account/deposit。在资源下，我们必须定义可用于它们的get和post动词。
-   tags：它所属的组。它应该与第4.1节中描述的标签名称相同。
-   summary：有关端点执行的操作的简要信息。
-   description：有关端点工作原理的更多详细信息。
-   operationId：描述的操作的唯一标识符。
-   requestBody：包含description、content和required关键字的请求有效负载。我们将在4.3节中定义内容模式。
-   responses：所有可用响应代码的列表。每个响应代码对象都包含description和content关键字。我们将在4.3节中定义该内容模式。
-   content：消息的HTTP Content-Type。

### 4.3 定义数据模型组件

最后，让我们创建API的数据模型对象：请求和响应主体以及错误消息。为此，请在YAML编辑器的根级别添加以下结构：

```yaml
components:
    schemas:
        Account:
            type: object
            properties:
                balance:
                    type: number
        AccountNotFoundError:
            type: object
            properties:
                message:
                    type: string
        DepositRequest:
            type: object
            properties:
                accountNumber:
                    type: string
                depositAmount:
                    type: number
```

添加以上内容后，编辑器中的所有解释错误应该都消失了。

在上面的YAML代码中，我们定义了4.2节中schema关键字中使用的相同组件。我们可以随心所欲地重用一个组件。例如，AccountNotFoundError对象用于两个端点的404响应代码。让我们分别检查代码中的关键字：

-   components：组件的根级关键字。
-   schemas：所有对象定义的列表。
-   type：字段的类型。如果我们使用object类型，我们还必须定义properties关键字。
-   properties：所有对象字段名称及其类型的列表。

### 4.4 查看API文档

此时，API已在线提供，供产品利益相关者审查。我们知道，API优先开发的主要优势是能够快速失败或成功，而不会在开发阶段浪费时间。因此，**确保每个人在进入开发阶段之前审查和协作提议的API资源、响应代码、数据模型以及API的描述和职责**。

一旦团队就设计达成一致，他们就可以并行处理API。

## 5. 导入到Spring Boot应用程序

本节介绍开发人员如何将YAML文档导入应用程序并自动生成API框架代码。

首先，我们必须在/src/main/resources文件夹中创建一个名为account_api_description.yaml的空YAML文件。然后，让我们将account_api_description.yaml的内容替换为Swagger在线编辑器中的完整YAML代码。最后，我们必须将openapi-generator-maven-plugin插件添加到Spring Boot应用程序pom.xml文件中的<plugins\>标签中：

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
                <inputSpec>./src/main/resources/account_api_description.yaml</inputSpec>
                <generatorName>spring</generatorName>
                <configOptions>
                    <openApiNullable>false</openApiNullable>
                    <interfaceOnly>true</interfaceOnly>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

你可以在[OpenAPI插件教程](https://www.baeldung.com/java-openapi-generator-server)中找到此处使用的代码。

现在，让我们通过运行以下命令从YAML文件生成服务器端代码：

```shell
mvn clean install
```

之后，我们可以在target文件夹中查看以下生成的代码：

<img src="../assets/img_10.png">

从那里，开发人员可以使用生成的类作为开发阶段的起点。例如，我们可以使用下面的AccountController类实现AccountApi接口：

```java
public class AccountController implements AccountApi {
    @Override
    public ResponseEntity<Void> depositToAccount(DepositRequest depositRequest) {
        // Execute the business logic through Service/Utils/Repository classes
        return AccountApi.super.depositToAccount(depositRequest);
    }

    @Override
    public ResponseEntity<Account> getAccount() {
        // Execute the business logic through Service/Utils/Repository classes
        return AccountApi.super.getAccount();
    }
}
```

被覆盖的方法depositToAccount和getAccount分别对应于POST /account/deposit和GET /account端点。

## 6. 总结

在本文中，我们了解了如何使用Spring Boot和OAS进行API优先开发。我们还研究了API优先的好处以及它如何帮助现代敏捷软件开发团队。使用API优先开发的最重要原因是在创建API时提高敏捷性和减少浪费的开发时间。使用这种方法，我们可以更快地起草、迭代更改并提供有关设计的反馈。

**使用API优先方法的另一个很好的理由是利益相关者不需要依赖开发人员完成代码来开始他们的工作**。QA、编写人员、经理和开发人员可以在创建初始API设计文档后并行工作。