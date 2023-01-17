## 1. 概述

在[我们之前的文章中](https://www.baeldung.com/aws-lambda-api-gateway)，我们已经在 AWS 上实现了一个全栈无服务器应用程序，使用 API 网关作为 REST 端点，AWS Lambda 用于业务逻辑，以及 DynamoDB 作为数据库。

但是，部署包含许多手动步骤，随着复杂性和环境数量的增加，这些步骤可能变得不便。

现在在本教程中，我们将讨论如何使用AWS 无服务器应用程序模型 (SAM)，它支持基于模板的描述和 AWS 上无服务器应用程序的自动部署。

我们将详细了解以下主题：

-   无服务器应用程序模型 (SAM) 以及底层 CloudFormation 的基础知识
-   使用 SAM 模板语法定义无服务器应用程序
-   使用 CloudFormation CLI 自动部署应用程序

## 2. 基础知识

如前所述，AWS 使我们能够通过使用 API 网关、Lambda 函数和 DynamoDB 实施完全无服务器的应用程序[。](https://www.baeldung.com/using-aws-lambda-with-api-gateway)毫无疑问，这已经为性能、成本和可扩展性提供了许多优势。

然而，缺点是，我们目前需要在 AWS 控制台中进行大量手动步骤，例如创建每个函数、上传代码、创建 DynamoDB 表、创建 IAM 角色、创建 API 和 API 结构等。

对于复杂的应用程序和多个环境(如测试、暂存和生产)，这种努力会迅速成倍增加。

这就是通常用于 AWS 上应用程序的 CloudFormation 和专门用于无服务器应用程序的无服务器应用程序模型 (SAM) 发挥作用的地方。

### 2.1. AWS CloudFormation

CloudFormation 是一项用于自动配置 AWS 基础设施资源的 AWS 服务。用户在蓝图(称为模板)中定义所有必需的资源，AWS 负责供应和配置。

以下术语和概念对于理解 CloudFormation 和 SAM 至关重要：

模板是对应用程序的描述，它应该如何在运行时构建。我们可以定义一组所需的资源，以及如何配置这些资源。CloudFormation 提供了一种用于定义模板的通用语言，支持 JSON 和 YAML 作为格式。

资源是 CloudFormation 中的构建块。资源可以是任何东西，例如 RestApi、RestApi 的阶段、批处理作业、DynamoDB 表、EC2 实例、网络接口、IAM 角色等等。[官方文档](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-template-resource-type-ref.html)目前为 CloudFormation 列出了大约 300 种资源类型。

堆栈是模板的实例化。CloudFormation 负责供应和配置堆栈。

### 2.2. 无服务器应用程序模型 (SAM)

通常情况下，强大工具的使用会变得非常复杂和不方便，CloudFormation 也是如此。

这就是 Amazon 引入无服务器应用程序模型 (SAM) 的原因。SAM 一开始就声称为定义无服务器应用程序提供了一种简洁明了的语法。目前，它只有三种资源类型，分别是 Lambda 函数、DynamoDB 表和 API。

SAM 基于 CloudFormation 模板语法，因此我们可以使用简单的 SAM 语法定义我们的模板，CloudFormation 将进一步处理该模板。

更多详细信息可[在官方 GitHub 存储库](https://github.com/awslabs/serverless-application-model) 以及[AWS 文档](https://docs.aws.amazon.com/lambda/latest/dg/serverless_app.html)中找到。

## 3.先决条件

对于以下教程，我们需要一个 AWS 账户。[免费套餐帐户](https://aws.amazon.com/free/)应该足够了。

除此之外，我们还需要[安装](https://docs.aws.amazon.com/cli/latest/userguide/installing.html)AWS CLI 。

最后，我们需要在我们的区域中有一个 S3 存储桶，它可以通过 AWS CLI 使用以下命令创建：

```bash
$>aws s3 mb s3://baeldung-sam-bucket
```

虽然本教程在下文中使用了 baeldung-sam-bucket ，但请注意存储桶名称必须是唯一的，因此你必须选择自己的名称。

作为演示应用程序，我们将使用将 [AWS Lambda 与 API 网关结合](https://www.baeldung.com/using-aws-lambda-with-api-gateway)使用中的代码。

## 4. 创建模板

在本节中，我们将创建 SAM 模板。

在定义单个资源之前，我们将首先查看总体结构。

### 4.1. 模板的结构

首先，让我们看一下模板的整体结构：

```xml
AWSTemplateFormatVersion: '2010-09-09'
Transform: 'AWS::Serverless-2016-10-31'
Description: Baeldung Serverless Application Model example
 
Resources:
  PersonTable:
    Type: AWS::Serverless::SimpleTable
    Properties:
      # Define table properties here
  StorePersonFunction:
    Type: AWS::Serverless::Function
    Properties:
      # Define function properties here
  GetPersonByHTTPParamFunction:
    Type: AWS::Serverless::Function
    Properties:
      # Define function properties here
  MyApi:
    Type: AWS::Serverless::Api
    Properties:
      # Define API properties here
```

正如我们所见，模板由标题和主体组成：

标头指定 CloudFormation 模板 ( AWSTemplateFormatVersion ) 的版本以及我们的 SAM 模板 ( Transform ) 的版本。我们还可以指定一个Description。

正文由一组资源组成：每个资源都有一个名称、一个资源 类型和一组属性。

SAM 规范目前支持三种类型： AWS::Serverless::Api、 AWS::Serverless::Function以及AWS::Serverless::SimpleTable。

由于我们要部署 [示例应用程序](https://www.baeldung.com/using-aws-lambda-with-api-gateway)，因此我们必须在模板主体中定义一个SimpleTable、两个 Functions以及一个Api 。

### 4.2. DynamoDB 表定义

现在让我们定义我们的 DynamoDB 表：

```xml
AWSTemplateFormatVersion: '2010-09-09'
Transform: 'AWS::Serverless-2016-10-31'
Description: Baeldung Serverless Application Model example
 
Resources:
  PersonTable:
    Type: AWS::Serverless::SimpleTable
    Properties:
      PrimaryKey:
          Name: id
          Type: Number
      TableName: Person
```

我们只需要为我们的SimpleTable定义两个属性：表名，以及一个主键，在我们的例子中称为 id并且类型为Number 。

可以[在官方规范中找到受支持的](https://github.com/awslabs/serverless-application-model/blob/master/versions/2016-10-31.md#awsserverlesssimpletable)SimpleTable属性的完整列表。

注意：由于我们只想使用主键访问表，AWS::Serverless::SimpleTable对我们来说就足够了。对于更复杂的要求， 可以改用原生 CloudFormation 类型AWS::DynamoDB::Table 。

### 4.3. Lambda 函数的定义

接下来，让我们定义我们的两个函数：

```xml
AWSTemplateFormatVersion: '2010-09-09'
Transform: 'AWS::Serverless-2016-10-31'
Description: Baeldung Serverless Application Model example
 
Resources:
  StorePersonFunction:
    Type: AWS::Serverless::Function
    Properties:
      Handler: com.baeldung.lambda.apigateway.APIDemoHandler::handleRequest
      Runtime: java8
      Timeout: 15
      MemorySize: 512
      CodeUri: ../target/aws-lambda-0.1.0-SNAPSHOT.jar
      Policies: DynamoDBCrudPolicy
      Environment:
        Variables:
          TABLE_NAME: !Ref PersonTable
      Events:
        StoreApi:
          Type: Api
            Properties:
              Path: /persons
              Method: PUT
              RestApiId:
                Ref: MyApi
  GetPersonByHTTPParamFunction:
    Type: AWS::Serverless::Function
    Properties:
      Handler: com.baeldung.lambda.apigateway.APIDemoHandler::handleGetByParam
      Runtime: java8
      Timeout: 15
      MemorySize: 512
      CodeUri: ../target/aws-lambda-0.1.0-SNAPSHOT.jar
      Policies: DynamoDBReadPolicy
      Environment:
        Variables:
          TABLE_NAME: !Ref PersonTable
      Events:
        GetByPathApi:
          Type: Api
            Properties:
              Path: /persons/{id}
              Method: GET
              RestApiId:
                Ref: MyApi
        GetByQueryApi:
          Type: Api
            Properties:
              Path: /persons
              Method: GET
              RestApiId:
                Ref: MyApi
```

正如我们所见，每个函数都具有相同的属性：

Handler定义函数的逻辑。由于我们使用的是Java，它是包含包的类名，与方法名相关联。

运行时定义函数是如何实现的，在我们的例子中是Java8。

超时定义了在 AWS 终止执行之前代码的执行最多可能需要多长时间。

MemorySize 定义分配内存的大小(以 MB 为单位)。重要的是要知道，AWS 将 CPU 资源按比例分配给MemorySize。因此，在 CPU 密集型函数的情况下，可能需要增加MemorySize，即使该函数不需要那么多内存。

CodeUri 定义功能代码的位置。它当前引用我们本地工作区中的目标文件夹。当我们稍后使用 CloudFormation 上传我们的函数时，我们将获得一个包含对 S3 对象的引用的更新文件。

策略 可以包含一组 AWS 管理的 IAM 策略或特定于 SAM 的策略模板。我们对StorePersonFunction使用特定于 SAM 的策略 DynamoDBCrudPolicy ，对 GetPersonByPathParamFunction 和GetPersonByQueryParamFunction 使用DynamoDBReadPolicy。

Environment 在运行时定义环境属性。我们使用一个环境变量来保存 DynamoDB 表的名称。

事件 可以包含一组 AWS 事件，这些事件应该能够触发该功能。在我们的例子中，我们定义了一个Api类型的事件。path、HTTP Method和 RestApiId的独特组合 将函数链接到我们将在下一节中定义的 API 方法。

可以[在官方规范中找到受支持的](https://github.com/awslabs/serverless-application-model/blob/master/versions/2016-10-31.md#awsserverlessfunction)Function属性的完整列表。

### 4.4. API 定义为 Swagger 文件

在定义 DynamoDB 表和函数之后，我们现在可以定义 API。

第一种可能性是使用 Swagger 格式内联定义我们的 API：

```xml
AWSTemplateFormatVersion: '2010-09-09'
Transform: 'AWS::Serverless-2016-10-31'
Description: Baeldung Serverless Application Model example
 
Resources:
  MyApi:
    Type: AWS::Serverless::Api
    Properties:
      StageName: test
      EndpointConfiguration: REGIONAL
      DefinitionBody:
        swagger: "2.0"
        info:
          title: "TestAPI"
        paths:
          /persons:
            get:
              parameters:
              - name: "id"
                in: "query"
                required: true
                type: "string"
              x-amazon-apigateway-request-validator: "Validate query string parameters and
                 headers"
              x-amazon-apigateway-integration:
                uri:
                  Fn::Sub: arn:aws:apigateway:${AWS::Region}:lambda:path/2015-03-31/functions/${GetPersonByHTTPParamFunction.Arn}/invocations
                responses: {}
                httpMethod: "POST"
                type: "aws_proxy"
            put:
              x-amazon-apigateway-integration:
                uri:
                  Fn::Sub: arn:aws:apigateway:${AWS::Region}:lambda:path/2015-03-31/functions/${StorePersonFunction.Arn}/invocations
                responses: {}
                httpMethod: "POST"
                type: "aws_proxy"
          /persons/{id}:
            get:
              parameters:
              - name: "id"
                in: "path"
                required: true
                type: "string"
              responses: {}
              x-amazon-apigateway-integration:
                uri:
                  Fn::Sub: arn:aws:apigateway:${AWS::Region}:lambda:path/2015-03-31/functions/${GetPersonByHTTPParamFunction.Arn}/invocations
                responses: {}
                httpMethod: "POST"
                type: "aws_proxy"
        x-amazon-apigateway-request-validators:
          Validate query string parameters and headers:
            validateRequestParameters: true
            validateRequestBody: false
```

我们的Api 具有三个属性： StageName定义 API 的阶段， EndpointConfiguration 定义 API 是区域优化还是边缘优化， DefinitionBody 包含 API 的实际结构。

在 DefinitionBody中，我们定义了三个参数：swagger 版本为“2.0”，info:title:为“TestAPI”，以及一组路径。

正如我们所见，路径代表了我们之前必须手动定义的 API[结构](https://www.baeldung.com/using-aws-lambda-with-api-gateway#create-api)。Swagger 中的路径等同于 AWS 控制台中的资源。就这样，每条路径都可以有一个或多个HTTP动词，相当于AWS控制台中的方法。

每个方法都可以有一个或多个参数以及一个请求验证器。

最令人兴奋的部分是属性 x-amazon-apigateway-integration，它是 AWS 特定的 Swagger 扩展：

uri 指定应调用哪个 Lambda 函数。

responses 指定如何转换函数返回的响应的规则。由于我们正在使用 Lambda 代理集成，因此我们不需要任何特定规则。

type定义我们想要使用 Lambda 代理集成，因此我们必须将httpMethod设置为“POST”，因为这是 Lambda 函数所期望的。

可以[在官方规范中找到受支持的](https://github.com/awslabs/serverless-application-model/blob/master/versions/2016-10-31.md#awsserverlessapi)Api属性的完整列表。

### 4.5. 隐式 API 定义

第二种选择是在函数资源中隐式定义 API：

```xml
AWSTemplateFormatVersion: '2010-09-09'
Transform: 'AWS::Serverless-2016-10-31'
Description: Baeldung Serverless Application Model Example with Implicit API Definition
 
Globals:
  Api:
    EndpointConfiguration: REGIONAL
    Name: "TestAPI"
 
Resources:
  StorePersonFunction:
    Type: AWS::Serverless::Function
    Properties:
      Handler: com.baeldung.lambda.apigateway.APIDemoHandler::handleRequest
      Runtime: java8
      Timeout: 15
      MemorySize: 512
      CodeUri: ../target/aws-lambda-0.1.0-SNAPSHOT.jar
      Policies:
        - DynamoDBCrudPolicy:
            TableName: !Ref PersonTable
      Environment:
        Variables:
          TABLE_NAME: !Ref PersonTable
      Events:
        StoreApi:
          Type: Api
          Properties:
            Path: /persons
            Method: PUT
  GetPersonByHTTPParamFunction:
    Type: AWS::Serverless::Function
    Properties:
      Handler: com.baeldung.lambda.apigateway.APIDemoHandler::handleGetByParam
      Runtime: java8
      Timeout: 15
      MemorySize: 512
      CodeUri: ../target/aws-lambda-0.1.0-SNAPSHOT.jar
      Policies:
        - DynamoDBReadPolicy:
            TableName: !Ref PersonTable
      Environment:
        Variables:
          TABLE_NAME: !Ref PersonTable
      Events:
        GetByPathApi:
          Type: Api
          Properties:
            Path: /persons/{id}
            Method: GET
        GetByQueryApi:
          Type: Api
          Properties:
            Path: /persons
            Method: GET
```

正如我们所见，我们的模板现在略有不同：不再有 AWS::Serverless::Api 资源。

但是，CloudFormation 将Api类型的事件 属性作为隐式定义并创建 API。一旦我们测试我们的应用程序，我们就会发现它的行为与使用 Swagger 显式定义 API 时的行为相同。

此外，还有一个Globals部分，我们可以在其中定义 API 的名称，以及我们的端点应该是区域性的。

只出现一个限制：当隐式定义 API 时，我们无法设置阶段名称。这就是为什么 AWS在任何情况下都会创建一个名为Prod的阶段。

## 5. 部署与测试

创建模板后，我们现在可以继续进行部署和测试。

为此，我们将在触发实际部署之前将我们的函数代码上传到 S3。

最后，我们可以使用任何 HTTP 客户端测试我们的应用程序。

### 5.1. 代码上传到 S3

第一步，我们必须将函数代码上传到 S3。

我们可以通过 AWS CLI 调用 CloudFormation 来做到这一点：

```shell
$> aws cloudformation package --template-file ./sam-templates/template.yml --s3-bucket baeldung-sam-bucket --output-template-file ./sam-templates/packaged-template.yml
```

使用此命令，我们触发CloudFormation 获取 CodeUri:中指定的函数代码并将其上传到 S3。CloudFormation 将创建一个 packaged-template.yml文件，其内容相同，只是CodeUri: 现在指向 S3 对象。

让我们看一下 CLI 输出：

```shell
Uploading to 4b445c195c24d05d8a9eee4cd07f34d0 92702076 / 92702076.0 (100.00%)
Successfully packaged artifacts and wrote output template to file packaged-template.yml.
Execute the following command to deploy the packaged template
aws cloudformation deploy --template-file c:zz_workspacetutorialsaws-lambdasam-templatespackaged-template.yml --stack-name <YOUR STACK NAME>
```

### 5.2. 部署

现在，我们可以触发实际部署：

```shell
$> aws cloudformation deploy --template-file ./sam-templates/packaged-template.yml --stack-name baeldung-sam-stack  --capabilities CAPABILITY_IAM
```

由于我们的堆栈还需要 IAM 角色(如访问 DynamoDB 表的函数角色)，我们必须通过指定–capabilities 参数明确承认这一点。

CLI 输出应如下所示：

```shell
Waiting for changeset to be created..
Waiting for stack create/update to complete
Successfully created/updated stack - baeldung-sam-stack
```

### 5.3. 部署审查

部署完成后，我们可以查看结果：

```shell
$> aws cloudformation describe-stack-resources --stack-name baeldung-sam-stack
```

CloudFormation 将列出所有资源，这些资源是我们堆栈的一部分。

### 5.4. 测试

最后，我们可以使用任何 HTTP 客户端测试我们的应用程序。

 让我们看看可用于这些测试的一些示例 cURL命令。

StorePerson函数：

```powershell
$> curl -X PUT 'https://0skaqfgdw4.execute-api.eu-central-1.amazonaws.com/test/persons' 
   -H 'content-type: application/json' 
   -d '{"id": 1, "name": "John Doe"}'
```

GetPersonByPathParamFunction :

```powershell
$> curl -X GET 'https://0skaqfgdw4.execute-api.eu-central-1.amazonaws.com/test/persons/1' 
   -H 'content-type: application/json'
```

GetPersonByQueryParamFunction：

```powershell
$> curl -X GET 'https://0skaqfgdw4.execute-api.eu-central-1.amazonaws.com/test/persons?id=1' 
   -H 'content-type: application/json'
```

### 5.5. 清理

最后，我们可以通过删除堆栈和所有包含的资源来清理：

```shell
aws cloudformation delete-stack --stack-name baeldung-sam-stack
```

## 六. 总结

在本文中，我们了解了 AWS 无服务器应用程序模型 (SAM)，它支持基于模板的描述和 AWS 上无服务器应用程序的自动部署。

我们详细讨论了以下主题：

-   无服务器应用程序模型 (SAM) 以及底层 CloudFormation 的基础知识
-   使用 SAM 模板语法定义无服务器应用程序
-   使用 CloudFormation CLI 自动部署应用程序