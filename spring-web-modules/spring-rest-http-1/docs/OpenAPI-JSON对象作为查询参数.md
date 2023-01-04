## 1. 概述

在本教程中，我们将学习如何使用OpenAPI将JSON对象作为查询参数。

## 2. OpenAPI 2中的查询参数

OpenAPI 2不支持对象作为查询参数；仅支持原始值和原始数组。

因此，我们希望将JSON参数定义为字符串。

为了实际看到这一点，让我们将一个名为params的参数定义为一个字符串，尽管我们将在后端将其解析为JSON：

```yaml
swagger: "2.0"
...
paths:
    /tickets:
        get:
            tags:
                - "tickets"
            summary: "Send an JSON Object as a query param"
            parameters:
                - name: "params"
                  in: "path"
                  description: "{\"type\":\"foo\",\"color\":\"green\"}"
                  required: true
                  type: "string"
```

因此，而不是：

```bash
GET http://localhost:8080/api/tickets?type=foo&color=green
```

干的好：

```bash
GET http://localhost:8080/api/tickets?params={"type":"foo","color":"green"}
```

## 3. OpenAPI 3中的查询参数

OpenAPI 3引入了对对象作为查询参数的支持。

要指定JSON参数，我们需要将内容部分添加到我们的定义中，其中包括MIME类型和模式：

```yaml
openapi: 3.0.1
...
paths:
    /tickets:
        get:
            tags:
                - tickets
            summary: Send an JSON Object as a query param
            parameters:
                -   name: params
                    in: query
                    description: '{"type":"foo","color":"green"}'
                    required: true
                    schema:
                        type: object
                        properties:
                            type:
                                type: "string"
                            color:
                                type: "string"
```

我们的请求现在看起来像：

```bash
GET http://localhost:8080/api/tickets?params[type]=foo¶ms[color]=green
```

而且，实际上，它仍然可以看起来像：

```bash
GET http://localhost:8080/api/tickets?params={"type":"foo","color":"green"}
```

第一个选项允许我们使用参数验证，这将让我们在发出请求之前知道是否有问题。

对于第二个选项，我们用它来换取对后端的更好控制以及OpenAPI 2兼容性。

## 4. URL编码

请务必注意，在做出将请求参数作为JSON对象传输的决定时，我们需要[对参数进行URL编码](https://www.baeldung.com/java-url-encoding-decoding)以确保安全传输。

因此，要发送以下URL：

```bash
GET /tickets?params={"type":"foo","color":"green"}
```

我们实际上会这样做：

```bash
GET /tickets?params=%7B%22type%22%3A%22foo%22%2C%22color%22%3A%22green%22%7D
```

## 5. 限制

另外，让我们记住将JSON对象作为一组查询参数传递的限制：

-   降低安全性
-   参数长度限制

例如，我们在查询参数中放置的数据越多，出现在服务器日志中的数据就越多，敏感数据暴露的可能性就越高。

此外，单个查询参数不能超过2048个字符。当然，我们都可以想象我们的JSON对象比这更大的场景。实际上，我们的JSON字符串的URL编码实际上会将我们的有效负载限制为大约1000个字符。

一种解决方法是在正文中发送更大的JSON对象。通过这种方式，我们解决了安全问题和JSON长度限制。

实际上，GET或POST都支持这个。通过GET发送正文的原因之一是维护我们API的RESTful语义。

当然，它有点不寻常并且没有得到普遍支持。例如，某些JavaScript HTTP库不允许GET请求具有请求主体。

简而言之，这种选择是语义和通用兼容性之间的权衡。

## 6. 总结

综上所述，在本文中我们学习了如何使用OpenAPI将JSON对象指定为查询参数。然后，我们观察了后端的一些影响。