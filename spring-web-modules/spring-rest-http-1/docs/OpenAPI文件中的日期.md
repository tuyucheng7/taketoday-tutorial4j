## 1. 简介

在本教程中，我们将了解如何在OpenAPI文件中声明日期，在本例中，使用[Swagger](https://www.baeldung.com/spring-rest-openapi-documentation)实现。这将使我们能够在调用外部API时以标准化方式管理输入和输出日期。

## 2. Swagger与OAS

Swagger是一组实现[OpenAPI规范](http://spec.openapis.org/oas/v3.0.3)(OAS)的工具，OAS是一种用于记录RESTful API的与语言无关的接口。这使我们能够在不访问源代码的情况下了解任何服务的功能。

为了实现这一点，我们将在我们的项目中有一个文件，通常是YAML或JSON，描述使用OAS的API。然后我们将使用Swagger工具来：

-   通过浏览器编辑我们的规范(Swagger Editor)
-   自动生成API客户端库 (Swagger Codegen)
-   显示自动生成的文档(Swagger UI)

OpenAPI[文件示例](https://swagger.io/docs/specification/basic-structure/)包含不同的部分，但我们将重点关注模型定义。

## 3. 定义日期

让我们使用OAS定义一个用户实体：

```yaml
components:
    User:
        type: "object"
        properties:
            id:
                type: integer
                format: int64
            createdAt:
                type: string
                format: date
                description: Creation date
                example: "2021-01-30"
            username:
                type: string
```

要定义日期，我们使用一个对象：

-   类型字段 等于字符串
-   指定日期形成方式的格式字段

在本例中，我们使用日期格式来描述createdAt日期。此格式使用[ISO-8601全日期](https://xml2rfc.tools.ietf.org/public/rfc/html/rfc3339.html#anchor14)格式描述日期。

## 4. 定义日期时间

此外，如果我们还想指定时间，我们将使用日期时间作为格式。让我们看一个例子：

```yaml
createdAt:
    type: string
    format: date-time
    description: Creation date and time
    example: "2021-01-30T08:30:00Z"
```

在本例中，我们使用[ISO-8601全时](https://xml2rfc.tools.ietf.org/public/rfc/html/rfc3339.html#anchor14)格式描述日期时间。

## 5. pattern字段

使用OAS，我们也可以用其他格式描述日期。为此，让我们使用模式字段：

```yaml
customDate:
    type: string
    pattern: '^\d{4}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])$'
    description: Custom date
    example: "20210130"
```

显然，这是可读性较差但功能最强大的方法。实际上，我们可以在此字段中使用任何正则表达式。

## 6. 总结

在本文中，我们了解了如何使用OpenAPI声明日期。我们可以使用OpenAPI提供的标准格式以及自定义模式来满足我们的需求。与往常一样，我们使用的示例的源代码可在[GitHub上](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/spring-web-modules/spring-rest-http)获得。