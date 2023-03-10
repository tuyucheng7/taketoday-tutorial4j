## 1. 概述

在本教程中，我们将了解[GraphQL](https://www.baeldung.com/graphql)中的错误处理选项。我们将看看GraphQL规范对错误响应的描述。因此，我们将开发一个使用Spring Boot进行GraphQL错误处理的示例。

## 2. 根据GraphQL规范的响应

根据GraphQL规范，收到的每个请求都必须返回格式正确的响应。此格式良好的响应包含来自相应成功或不成功请求操作的数据或错误的映射。此外，响应可能包含部分成功的结果数据和字段错误。

响应映射的关键组件是错误、数据和扩展。

响应中的错误 部分描述了请求操作期间的任何失败。如果没有错误发生，则错误 组件不得出现在响应中。在下一节中，我们将研究规范中描述的不同类型的错误。

数据 部分描述了请求操作成功执行的结果。如果操作是查询，则此组件是查询根操作类型的对象。另一方面，如果操作是突变，则此组件是突变根操作类型的对象。

如果由于缺少信息、验证错误或语法错误，请求的操作甚至在执行之前就失败了，则数据 组件不得出现在响应中。如果在操作执行过程中操作失败且结果不成功，则数据组件必须为null。

响应映射可能包含一个名为extensions的附加组件，它是一个映射对象。该组件有助于实施者在响应中提供他们认为合适的其他自定义内容。因此，对其内容格式没有额外的限制。

如果数据组件不存在于响应中，则错误组件必须存在并且必须包含至少一个错误。此外，它应该指出失败的原因。

这是GraphQL错误的示例：

```graphql
mutation {
     addVehicle(vin: "NDXT155NDFTV59834", year: 2021, make: "Toyota", model: "Camry", trim: "XLE",
             location: {zipcode: "75024", city: "Dallas", state: "TX"}) {
        vin
        year
        make
        model
        trim
    }
}
```

违反唯一约束时的错误响应如下所示：

```json
{
  "data": null,
  "errors": [
    {
      "errorType": "DataFetchingException",
      "locations": [
        {
          "line": 2,
          "column": 5,
          "sourceName": null
        }
      ],
      "message": "Failed to add vehicle. Vehicle with vin NDXT155NDFTV59834 already present.",
      "path": [
        "addVehicle"
      ],
      "extensions": {
        "vin": "NDXT155NDFTV59834"
      }
    }
  ]
}
```

## 3. 根据GraphQL规范的错误响应组件

响应中的错误部分是一个非空的错误列表，每个错误都是一个映射。

### 3.1. 请求错误

顾名思义，如果请求本身有任何问题，请求错误可能会在操作执行之前发生。可能是请求数据解析失败、请求文档验证、不支持的操作或请求值无效。

当发生请求错误时，这表明执行尚未开始，这意味着响应中的数据部分不能出现在响应中。换句话说，响应仅包含错误部分。

让我们看一个演示无效输入语法的例子：

```json
query {
  searchByVin(vin: "error) {
    vin
    year
    make
    model
    trim
  }
}
```

这是语法错误的请求错误响应，在本例中是缺少引号：

```json
{
  "data": null,
  "errors": [
    {
      "message": "Invalid Syntax",
      "locations": [
        {
          "line": 5,
          "column": 8,
          "sourceName": null
        }
      ],
      "errorType": "InvalidSyntax",
      "path": null,
      "extensions": null
    }
  ]
}
```

### 3.2. 字段错误

字段错误，顾名思义，可能是由于未能将值强制转换为预期类型或在特定字段的值解析过程中出现内部错误。这意味着在执行请求的操作期间发生了字段错误。

在字段错误的情况下，所请求操作的执行将继续并返回部分结果，这意味着响应的数据 部分必须与错误 部分中的所有字段错误一起出现。

让我们看另一个例子：

```json
query {
  searchAll {
    vin
    year
    make
    model
    trim
  }
}
```

这一次，我们包含了 vehicle trim字段，根据我们的GraphQL架构，该字段应该是不可空的。

然而，其中一辆车的信息有一个空的trim值，所以我们只取回部分数据——trim 值不为空的车辆——以及错误：

```json
{
  "data": {
    "searchAll": [
      null,
      {
        "vin": "JTKKU4B41C1023346",
        "year": 2012,
        "make": "Toyota",
        "model": "Scion",
        "trim": "Xd"
      },
      {
        "vin": "1G1JC1444PZ215071",
        "year": 2000,
        "make": "Chevrolet",
        "model": "CAVALIER VL",
        "trim": "RS"
      }
    ]
  },
  "errors": [
    {
      "message": "Cannot return null for non-nullable type: 'String' within parent 'Vehicle' (/searchAll[0]/trim)",
      "path": [
        "searchAll",
        0,
        "trim"
      ],
      "errorType": "DataFetchingException",
      "locations": null,
      "extensions": null
    }
  ]
}
```

### 3.3. 错误响应格式

正如我们之前看到的，响应中的错误是一个或多个错误的集合。而且，每个错误都必须包含一个描述失败原因的消息 键，以便客户端开发人员可以进行必要的更正以避免错误。

每个错误还可能包含一个名为locations的键，它是指向请求的GraphQL文档中与错误关联的行的位置列表。每个位置都是一个带有键的映射：行和列，分别提供关联元素的行号和开始列号。

另一个可能是错误的一部分的键称为path。它提供从根元素跟踪到具有错误的响应的特定元素的值列表。如果字段值是列表，则路径值可以是表示错误元素的字段名称或索引的字符串。如果错误与具有别名的字段相关，则路径中的值应该是别名。

### 3.4. 处理字段错误

无论是在可空字段还是不可空字段上引发字段错误，我们都应该将其视为字段返回null并且必须将错误添加到错误列表中。

在可空字段的情况下，响应中字段的值将为空，但错误必须包含此字段错误，描述失败原因和其他信息，如前一节所示。

另一方面，父字段处理非空字段错误。如果父字段不可为空，则传播错误处理，直到我们到达可为空的父字段或根元素。

类似地，如果列表字段包含不可为 null 的类型并且一个或多个列表元素返回null，则整个列表解析为null。此外，如果包含列表字段的父字段不可为空，则错误处理将传播，直到我们到达可为空的父元素或根元素。

无论出于何种原因，如果在解析过程中针对同一个字段引发了多个错误，那么对于该字段，我们必须只将一个字段错误添加到errors 中。

## 4. Spring Boot GraphQL库

我们的Spring Boot应用程序示例使用[spring-boot-starter-graphql](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-graphql/3.0.3) 模块，它引入了所需的GraphQL依赖项。

我们还使用[spring-graphql-test](https://central.sonatype.com/artifact/org.springframework.graphql/spring-graphql-test/1.1.2)模块进行相关测试：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.graphql</groupId>
    <artifactId>spring-graphql-test</artifactId>
    <scope>test</scope>
</dependency>
```

## 5.Spring Boot GraphQL错误处理

在本节中，我们将主要介绍Spring Boot应用程序本身中的GraphQL错误处理。我们不会涵盖[GraphQL Java](https://www.baeldung.com/java-call-graphql-service)和[GraphQL Spring Boot](https://www.baeldung.com/spring-graphql)应用程序开发。

在我们的Spring Boot应用程序示例中，我们将根据位置或 VIN(车辆识别码)改变或查询车辆。我们将看到使用此示例实现错误处理的不同方法。

在以下小节中，我们将了解Spring Boot模块如何处理异常或错误。

### 5.1. 具有标准异常的GraphQL响应

通常，在REST应用程序中，我们通过扩展RuntimeException或Throwable来创建自定义运行时异常类：

```java
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
```

通过这种方法，我们可以看到GraphQL引擎返回以下响应：

```json
{
    "errors": [
        {
            "message": "INTERNAL_ERROR for 2c69042a-e7e6-c0c7-03cf-6026b1bbe559",
            "locations": [
                {
                    "line": 2,
                    "column": 5
                }
            ],
            "path": [
                "searchByLocation"
            ],
            "extensions": {
                "classification": "INTERNAL_ERROR"
            }
        }
    ],
    "data": null
}
```

在上面的错误响应中，我们可以看到它不包含任何错误的详细信息。

默认情况下，请求处理期间的任何异常都由ExceptionResolversExceptionHandler 类处理，该类从GraphQL API实现DataFetcherExceptionHandler接口。它允许应用程序注册一个或多个DataFetcherExceptionResolver组件。

这些解析器被顺序调用，直到其中一个能够处理异常并将其解析为GraphQLError。如果没有解析器能够处理异常，则异常被归类为INTERNAL_ERROR。 它还包含执行 ID 和一般错误消息，如上所示。

### 5.2. 具有已处理异常的GraphQL响应

现在让我们看看如果我们实现自定义异常处理，响应会是什么样子。

首先，我们有另一个自定义异常：

```java
public class VehicleNotFoundException extends RuntimeException {
    public VehicleNotFoundException(String message) {
        super(message);
    }
}
```

DataFetcherExceptionResolver提供了一个异步契约。但是，在大多数情况下，扩展DataFetcherExceptionResolverAdapter并覆盖其同步解决异常的resolveToSingleError或resolveToMultipleErrors方法之一就足够了。

现在，让我们实现这个组件，我们可以返回一个 NOT_FOUND 分类以及异常消息而不是一般错误：

```java
@Component
public class CustomExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof VehicleNotFoundException) {
            return GraphqlErrorBuilder.newError()
              .errorType(ErrorType.NOT_FOUND)
              .message(ex.getMessage())
              .path(env.getExecutionStepInfo().getPath())
              .location(env.getField().getSourceLocation())
              .build();
        } else {
            return null;
        }
    }
}
```

在这里，我们创建了一个具有适当分类和其他错误详细信息的GraphQLError ，以便在 JSON 响应的错误部分创建更有用的响应：

```json
{
    "errors": [
        {
            "message": "Vehicle with vin: 123 not found.",
            "locations": [
                {
                    "line": 2,
                    "column": 5
                }
            ],
            "path": [
                "searchByVin"
            ],
            "extensions": {
                "classification": "NOT_FOUND"
            }
        }
    ],
    "data": {
        "searchByVin": null
    }
}
```

此错误处理机制的一个重要细节是未解决的异常与发送到客户端的错误相关的executionId一起记录在 ERROR 级别。如上所示，任何已解决的异常都记录在日志中的 DEBUG 级别。

## 六，总结

在本教程中，我们了解了不同类型的GraphQL错误。我们还研究了如何根据规范格式化GraphQL错误。后来我们在Spring Boot应用程序中实现了错误处理。