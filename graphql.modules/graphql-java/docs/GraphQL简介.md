## 1. 概述

[GraphQL](https://graphql.github.io/)是一种查询语言，由Facebook创建，目的是基于直观灵活的语法构建客户端应用程序，用于描述他们的数据需求和交互。

传统REST调用的主要挑战之一是客户端无法请求自定义(有限或扩展)的数据集。在大多数情况下，一旦客户端向服务器请求信息，它要么获取所有字段，要么不获取任何字段。

另一个困难是工作和维护多个端点，随着平台的发展，数量也会随之增加。因此，客户端经常需要从不同的端点请求数据。

在构建GraphQL服务器时，只需要一个URL即可用于所有数据获取和更改。因此，客户端可以通过向服务器发送描述他们所需内容的查询字符串来请求一组数据。

## 2. 基本的GraphQL命名法

让我们来看看GraphQL的基本术语。

-   **Query**：是向GraphQL服务器请求的只读操作
-   **Mutation**：是向GraphQL服务器请求的读写操作
-   **Resolver**：在GraphQL中，Resolver负责映射操作和运行在负责处理请求的后端的代码，它类似于RESTFul应用程序中的MVC后端
-   **Type**：类型定义了可以从GraphQL服务器返回的响应数据的形状，包括作为其他类型的边缘的字段
-   **Input**：类似于类型，但定义了发送到GraphQL服务器的输入数据的形状
-   **Scalar**：是原始类型，例如String、Int、Boolean、Float等
-   **Interface**：Interface会存储字段的名称和它们的参数，因此GraphQL对象可以继承它，从而确保使用特定字段
-   **Schema**：在GraphQL中，Schema管理查询和突变，定义允许在GraphQL服务器中执行的内容

### 2.1 模式加载

有两种方法可以将模式加载到GraphQL服务器中：

1.  通过使用GraphQL的接口定义语言(IDL)
2.  通过使用一种受支持的编程语言

让我们演示一个使用IDL的示例：

```graphql
type User {
    firstName: String
}
```

现在，下面是一个使用Java代码的模式定义示例：

```java
GraphQLObjectType userType = newObject()
      .name("User")
      .field(newFieldDefinition()
          .name("firstName")
          .type(GraphQLString))
      .build();
```

## 3. 接口定义语言

接口定义语言(IDL)或模式定义语言(SDL)是指定GraphQL模式的最简洁方式，该语法定义明确，将在官方GraphQL规范中采用。

例如，让我们为用户/电子邮件创建一个GraphQL模式，可以像这样指定：

```graphql
schema {
    query: QueryType
}

enum Gender {
    MALE
    FEMALE
}

type User {
    id: String!
    firstName: String!
    lastName: String!
    createdAt: DateTime!
    age: Int! @default(value: 0)
    gender: [Gender]!
    emails: [Email!]! @relation(name: "Emails")
}

type Email {
    id: String!
    email: String!
    default: Int! @default(value: 0)
    user: User @relation(name: "Emails")
}
```

## 4. GraphQL-java

GraphQL-java是基于规范和[JavaScript参考实现](https://github.com/graphql/graphql-js)的实现。请注意，它至少需要Java 8才能正常运行。

### 4.1 GraphQL-java注解

GraphQL还可以使用[Java注解](https://github.com/graphql-java/graphql-java-annotations)来生成其模式定义，而无需使用传统IDL方法创建的所有样板代码。

### 4.2 依赖关系

为了创建我们的示例，让我们首先开始导入依赖于[graphql-java-annotations](https://search.maven.org/classic/#search|ga|1|g%3A"com.graphql-java" AND a%3A"graphql-java-annotations")模块的所需依赖项：

```xml
<dependency>
    <groupId>com.graphql-java</groupId>
    <artifactId>graphql-java-annotations</artifactId>
    <version>3.0.3</version>
</dependency>
```

我们还实现了一个HTTP库来简化我们应用程序的设置，我们将使用[Ratpack](https://search.maven.org/classic/#search|ga|1|g%3A"io.ratpack" AND a%3A"ratpack-core")(尽管它也可以通过Vert.x、Spark、Dropwizard、Spring Boot等实现)。

让我们也导入Ratpack依赖项：

```xml
<dependency>
    <groupId>io.ratpack</groupId>
    <artifactId>ratpack-core</artifactId>
    <version>1.4.6</version>
</dependency>
```

### 4.3 实现

接下来创建我们的示例：一个简单的API，它为用户提供“CRUDL”(创建、检索、更新、删除和列表)。首先，创建我们的User POJO类：

```java
@GraphQLName("user")
public class User {

    @GraphQLField
    private Long id;

    @GraphQLField
    private String name;

    @GraphQLField
    private String email;

    // getters, setters, constructors, and helper methods omitted
}
```

在这个POJO中，我们可以看到@GraphQLName("user")注解，表明这个类由GraphQL映射，每个字段都用@GraphQLField注解标注。

接下来，我们将创建UserHandler类，这个类从所选的HTTP连接器库(在我们的示例中为Ratpack)继承一个handle方法，该方法将管理和调用GraphQL的解析器功能。因此，将请求(JSON有效负载)重定向到正确的查询或变异操作：

```java
@Override
public void handle(Context context) {
	context.parse(Map.class)
		.then(payload -> {
			Map<String, Object> parameters = (Map<String, Object>) payload.get("parameters");
			ExecutionResult executionResult = graphql.execute(payload.get(SchemaUtils.QUERY)
				.toString(), null, this, parameters);
            
			Map<String, Object> result = new LinkedHashMap<>();
			if (executionResult.getErrors().isEmpty()) {
				result.put(SchemaUtils.DATA, executionResult.getData());
			} else {
				result.put(SchemaUtils.ERRORS, executionResult.getErrors());
				LOGGER.warning("Errors: " + executionResult.getErrors());
			}
			context.render(json(result));
		});
}
```

现在，将支持查询操作的类，即UserQuery。如前所述，从服务器到客户端检索数据的所有方法都由此类管理：

```java
@GraphQLName("query")
public class UserQuery {

    @GraphQLField
    public static User retrieveUser(DataFetchingEnvironment env, @NotNull @GraphQLName("id") String id) {
        // return user
    }

    @GraphQLField
    public static List<User> listUsers(DataFetchingEnvironment env) {
        // return list of users
    }
}
```

与UserQuery类似，现在我们创建UserMutation，它将管理所有旨在更改存储在服务器端的某些给定数据的操作：

```java
@GraphQLName("mutation")
public class UserMutation {

    @GraphQLField
    public static User createUser(
          DataFetchingEnvironment env,
          @NotNull @GraphQLName("name") String name,
          @NotNull @GraphQLName("email") String email) {
        // create user information
    }
}
```

值得注意的是UserQuery和UserMutation类中的注解：@GraphQLName("query")和@GraphQLName("mutation")，这些注解分别用于定义查询和变异操作。

通过GraphQL-java服务器能够运行查询和变异操作，我们可以使用以下JSON负载来测试客户端对服务器的请求：

-   对于创建操作：

```json
{
    "query": "mutation($name: String! $email: String!){
       createUser (name: $name email: $email) { id name email age } }",
    "parameters": {
        "name": "John",
        "email": "john@email.com"
     }
}

```

作为服务器对此操作的响应：

```json
{
    "data": {
        "createUser": {
            "id": 1,
            "name": "John",
            "email": "john@email.com"
        }
    } 
}
```

-   对于检索操作：

```json
{
    "query": "query($id: String!){ retrieveUser (id: $id) {name email} }",
    "parameters": {
        "id": 1
    }
}
```

作为服务器对此操作的响应：

```json
{
    "data": {
        "retrieveUser": {
            "name": "John",
            "email": "john@email.com"
        }
    }
}
```

GraphQL提供了客户端可以自定义响应的功能。因此，在用作示例的最后一个RETRIEVE操作中，我们可以不返回姓名和电子邮件，例如，只返回电子邮件：

```json
{
    "query": "query($id: String!){ retrieveUser (id: $id) {email} }",
    "parameters": {
        "id": 1
    }
}
```

因此，GraphQL服务器的返回信息只会返回请求的数据：

```json
{
    "data": {
        "retrieveUser": {
            "email": "john@email.com"
        }
    }
}
```

## 5. 总结

作为REST API的替代方法，GraphQL是一种将客户端/服务器之间的复杂性降至最低的简单且极具吸引力的方法。