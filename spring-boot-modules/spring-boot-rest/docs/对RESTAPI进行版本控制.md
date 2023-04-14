## 1.问题

发展REST API是一个难题——有很多选择。本文讨论了其中的一些选项。

## 延伸阅读：

## [Spring Boot教程——引导一个简单的应用程序](https://www.baeldung.com/spring-boot-start)

这就是您开始了解Spring Boot的方式。

[阅读更多](https://www.baeldung.com/spring-boot-start)→

## [探索Spring BootTestRestTemplate](https://www.baeldung.com/spring-boot-testresttemplate)

了解如何在Spring Boot中使用新的TestRestTemplate来测试简单的API。

[阅读更多](https://www.baeldung.com/spring-boot-testresttemplate)→

## [带有Jersey和Spring的REST API](https://www.baeldung.com/jersey-rest-api-with-spring)

使用Jersey2和Spring构建RestfulWeb服务。

[阅读更多](https://www.baeldung.com/jersey-rest-api-with-spring)→

## 2.合同内容是什么？

首先，我们需要回答一个简单的问题：API和客户端之间的契约是什么？

####2.1.URI是合约的一部分吗？

让我们首先考虑REST API的URI结构——那是契约的一部分吗？客户是否应该添加书签、硬编码并通常依赖API的URI？

如果是这种情况，那么客户端与REST服务的交互将不再由服务本身驱动，而是由[RoyFielding所说](http://roy.gbiv.com/untangled/2008/rest-apis-must-be-hypertext-driven)的带外信息驱动：

>   除了初始URI（书签）和一组适合目标受众的标准化媒体类型之外，应该在没有先验知识的情况下输入REST API……这里的失败意味着带外信息正在驱动交互而不是超文本。

很明显URI不是契约的一部分！客户端应该只知道一个URI——API的入口点。使用API时应发现所有其他URI。

####2.2.合同的媒体类型部分？

用于表示资源的媒体类型信息如何——这些是客户与服务之间合同的一部分吗？

为了成功使用API，客户端必须事先了解这些媒体类型。事实上，这些媒体类型的定义代表了整个契约。

因此，这是REST服务最应该关注的地方：

>   REST API应该花费几乎所有的描述性工作来定义用于表示资源和驱动应用程序状态的媒体类型，或者为现有标准媒体类型定义扩展关系名称和/或支持超文本的标记。

因此，媒体类型定义是合同的一部分，应该是使用API的客户端的先验知识。这就是标准化的用武之地。

我们现在对合同是什么有了一个很好的了解，让我们继续讨论如何实际解决版本控制问题。

## 3.高级选项

现在让我们讨论对REST API进行版本控制的高级方法：

-   URI版本控制——使用版本指示符对URI空间进行版本控制
-   媒体类型版本控制——对资源的表示进行版本控制

当我们在URI空间中引入版本时，资源的表示被认为是不可变的。所以当需要在API中引入更改时，需要创建一个新的URI空间。

例如，假设一个API发布了以下资源——用户和权限：

```bash
http://host/v1/users
http://host/v1/privileges
```

现在，让我们考虑一下用户API的重大变化需要引入第二个版本：

```bash
http://host/v2/users
http://host/v2/privileges
```

当我们对媒体类型进行版本控制并扩展语言时，[我们会](https://datatracker.ietf.org/doc/html/draft-ietf-httpbis-p3-payload-16#section-5.2)根据此标头进行内容协商。REST API将使用自定义[供应商MIME媒体类型](https://datatracker.ietf.org/doc/html/rfc4288#section-3.2)而不是通用媒体类型，例如application/json。我们将对这些媒体类型而不是URI进行版本控制。

例如：

```bash
===>
GET /users/3 HTTP/1.1
Accept: application/vnd.myname.v1+json
<===
HTTP/1.1 200 OK
Content-Type: application/vnd.myname.v1+json
{
    "user": {
        "name": "John Smith"
    }
}
```

我们可以查看这篇[“RestAPI的自定义媒体类型”文章](https://www.baeldung.com/spring-rest-custom-media-type)，以获取有关此主题的更多信息和示例。

此处需要理解的重要一点是，客户端不会对媒体类型中定义的响应结构做出任何假设。

这就是通用媒体类型不理想的原因。这些不提供足够的语义信息并迫使客户端使用需要额外的提示来处理资源的实际表示。

一个例外是使用一些其他方式来唯一标识内容的语义——例如XML模式。

## 4.优点和缺点

现在我们已经清楚地了解了客户端和服务之间契约的组成部分，以及对API版本化选项的高级概述，让我们讨论每种方法的优点和缺点。

首先，在URI中引入版本标识符会导致非常大的URI占用空间。这是因为任何已发布的API中的任何重大更改都会为整个API引入全新的表示树。随着时间的推移，这成为维护的负担以及客户的问题——现在有更多的选择可供选择。

URI中的版本标识符也非常不灵活。没有办法简单地改进单个资源的API或整个API的一小部分。

正如我们之前提到的，这是一种全有或全无的方法。如果API的一部分移动到新版本，那么整个API也必须随之移动。这也使得将客户端从v1升级到v2成为一项重大任务——这会导致旧版本的升级速度变慢和停用期更长。

在版本控制方面，HTTP缓存也是一个主要问题。

从中间的代理缓存来看，每种方式各有优缺点。如果URI是版本化的，那么缓存将需要为每个资源保留多个副本——每个版本的API都有一个副本。这会给缓存带来负担并降低缓存命中率，因为不同的客户端将使用不同的版本。

此外，一些缓存失效机制将不再起作用。如果媒体类型是版本化的，那么客户端和服务都需要支持[VaryHTTP标头](https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.44)以指示有多个版本被缓存。

然而，从客户端缓存的角度来看，对媒体类型进行版本化的解决方案比URI包含版本标识符的解决方案涉及的工作稍微多一些。这是因为当它的键是一个URL而不是媒体类型时，它更容易缓存一些东西。

让我们以定义一些目标来结束本节（直接来自[APIEvolution](https://www.mnot.net/blog/2012/12/04/api-evolution)）：

-   保留名称中的兼容更改
-   避免新的主要版本
-   使更改向后兼容
-   考虑向前兼容性

## 5.API可能发生的变化

接下来，让我们考虑一下对REST API的更改类型——这里介绍了这些：

-   表示格式更改
-   资源变化

### 5.1.添加到资源的表示

媒体类型的格式文档在设计时应考虑到向前兼容性。具体来说，客户端应该忽略它不理解的信息（JSON比XML做得更好）。

现在，如果正确实施，在资源表示中添加信息不会破坏现有客户端。

继续我们之前的示例，在用户表示中添加金额不会是重大更改：

```bash
{
    "user": {
        "name": "John Smith", 
        "amount": "300"
    }
}
```

### 5.2.删除或更改现有表示

在现有表示的设计中删除、重命名或一般重组信息对客户来说是一个重大变化。这是因为他们已经了解并依赖旧格式。

这就是内容协商的用武之地。对于此类更改，我们可以添加新的供应商MIME媒体类型。

让我们继续前面的例子。假设我们想将用户名分成firstname和lastname：

```bash
===>
GET /users/3 HTTP/1.1
Accept: application/vnd.myname.v2+json
<===
HTTP/1.1 200 OK
Content-Type: application/vnd.myname.v2+json
{
    "user": {
        "firstname": "John", 
        "lastname": "Smith", 
        "amount": "300"
    }
}
```

因此，这确实代表了客户端的不兼容更改——客户端将不得不请求新的表示并理解新的语义。但是，URI空间将保持稳定，不会受到影响。

### 5.3.主要语义变化

这些是资源含义的变化，它们之间的关系或后端映射到的内容。这种变化可能需要一种新的媒体类型，或者它们可能需要在旧资源旁边发布一个新的同级资源并使用链接指向它。

虽然这听起来像是在URI中再次使用版本标识符，但重要的区别在于新资源是独立于API中的任何其他资源发布的，并且不会在根目录下分叉整个API。

REST API应遵守HATEOAS约束。据此，大多数URI应该由客户端发现，而不是硬编码。更改此类URI不应被视为不兼容的更改。新的URI可以替换旧的URI，客户端将能够重新发现URI并且仍然可以正常工作。

然而，值得注意的是，虽然出于所有这些原因，在URI中使用版本标识符是有问题的，但它在任何方面都不是非RESTful的。

## 六，结论

本文试图提供一个关于发展REST服务的非常多样化和困难的问题的概述。我们讨论了两种常见的解决方案、每种解决方案的优点和缺点，以及在REST上下文中对这些方法进行推理的方法。

本文最后介绍了第二种解决方案——在检查对RESTfulAPI的可能更改的同时对媒体类型进行版本控制。

本教程的完整实现可以在[GitHub项目](https://github.com/eugenp/tutorials/tree/master/spring-boot-rest)中找到。

## 7.进一步阅读

通常，这些阅读资源在整篇文章中都有链接，但在这种情况下，好的阅读资源太多了：

-   -   [REST API必须是超文本驱动的](http://roy.gbiv.com/untangled/2008/rest-apis-must-be-hypertext-driven)
    -   [API演进](https://www.mnot.net/blog/2012/12/04/api-evolution)
    -   [链接HTTPAPI](https://www.mnot.net/blog/2013/06/23/linking_apis)
    -   [兼容性策略](https://www.w3.org/2001/tag/doc/versioning-compatibility-strategies#iddiv2125080648)