## 1. 简介

在本教程中，我们将探索[OData](https://www.odata.org/)，这是一种标准协议，允许使用 RESTFul API 轻松访问数据集。

## 2. 什么是OData？

OData 是 OASIS 和 ISO/IEC 标准，用于使用 RESTful API 访问数据。因此，它允许消费者使用标准 HTTP 调用来发现和浏览数据集。

例如，我们可以使用简单的curl one-liner访问其中一项[公开可用的 OData 服务](https://www.odata.org/odata-services/)：

```bash
curl -s https://services.odata.org/V2/Northwind/Northwind.svc/Regions
<?xml version="1.0" encoding="utf-8" standalone="yes"?>
<feed xml:base="https://services.odata.org/V2/Northwind/Northwind.svc/" 
  xmlns:d="http://schemas.microsoft.com/ado/2007/08/dataservices" 
  xmlns:m="http://schemas.microsoft.com/ado/2007/08/dataservices/metadata" 
  xmlns="http://www.w3.org/2005/Atom">
    <title type="text">Regions</title>
    <id>https://services.odata.org/V2/Northwind/Northwind.svc/Regions</id>
... rest of xml response omitted
```

在撰写本文时，OData 协议处于第 4 版——更准确地说是 4.01。OData V4在2014年就达到了OASIS标准的水平，但它的历史更久远。我们可以追溯到一个名为 Astoria 的 Microsoft 项目，该项目[在 2007 年更名为 ADO.Net Data Services](https://devblogs.microsoft.com/odata/ado-net-data-services-project-astoria/)。宣布该项目的[原始博客条目](https://devblogs.microsoft.com/odata/welcome/)仍然可以在 Microsoft 的 OData 博客上找到。

使用基于标准的协议来访问数据集会带来一些优于标准 API(例如 JDBC 或 ODBC)的优势。作为最终用户级别的消费者，我们可以使用流行的工具(例如 Excel)从任何兼容的提供者处检索数据。大量可用的 REST 客户端库也有助于编程。

作为提供者，采用 OData 也有好处：一旦我们创建了兼容的服务，我们就可以专注于提供有价值的数据集，最终用户可以使用他们选择的工具来使用这些数据集。由于它是基于 HTTP 的协议，我们还可以利用安全机制、监控和日志记录等方面。

这些特性使得 OData 成为政府机构在实施公共数据服务时的热门选择，我们可以通过[查看此目录](https://pragmatiqa.com/xodata/odatadir.html)来检查。

## 3. OData 概念

OData 协议的核心是实体数据模型(简称 EDM)的概念。EDM 通过包含许多元实体的元数据文档描述 OData 提供者公开的数据：

-   实体类型及其属性(例如Person、Customer、Order等)和键
-   实体之间的关系
-   用于描述嵌入到实体中的结构化类型的复杂类型(例如，作为客户类型一部分的地址类型)
-   实体集，聚合给定类型的实体

规范要求此元数据文档必须在用于访问服务的根 URL的标准位置$metadata中可用。例如，如果我们在http://example.org/odata.svc/有可用的 OData 服务，那么它的元数据文档将在http://example.org/odata.svc/$metadata可用。

返回的文档包含一堆描述此服务器支持的模式的 XML：

```xml
<?xml version="1.0"?>
<edmx:Edmx 
  xmlns:edmx="http://schemas.microsoft.com/ado/2007/06/edmx" 
  Version="1.0">
    <edmx:DataServices 
      xmlns:m="http://schemas.microsoft.com/ado/2007/08/dataservices/metadata" 
      m:DataServiceVersion="1.0">
    ... schema elements omitted
    </edmx:DataServices>
</edmx:Edmx>
```

让我们将这份文件拆成几个主要部分。

顶级元素<edmx:Edmx> 只能有一个子元素，即<edmx:DataServices>元素。 此处需要注意的重要事项是命名空间 URI，因为它允许我们识别服务器使用的 OData 版本。在这种情况下，命名空间表示我们有一个 OData V2 服务器，它使用 Microsoft 的标识符。

DataServices元素可以有一个 或多个 Schema 元素，每个元素描述一个可用的数据集。由于对Schema中可用元素的完整描述超出了本文的范围，我们将重点关注最重要的元素：EntityType、Associations和 EntitySets。

### 3.1. 实体类型元素

此元素定义给定实体的可用属性，包括其主键。它还可能包含有关与其他模式类型的关系的信息，并且通过查看示例( CarMaker)，我们将能够看到它与其他 ORM 技术(例如 JPA)中的描述没有太大区别：

```xml
<EntityType Name="CarMaker">
    <Key>
        <PropertyRef Name="Id"/>
    </Key>
    <Property Name="Id" Type="Edm.Int64" 
      Nullable="false"/>
    <Property Name="Name" Type="Edm.String" 
      Nullable="true" 
      MaxLength="255"/>
    <NavigationProperty Name="CarModelDetails" 
      Relationship="default.CarModel_CarMaker_Many_One0" 
      FromRole="CarMaker" 
      ToRole="CarModel"/>
</EntityType>
```

在这里，我们的 CarMaker只有两个属性 - Id 和 Name - 以及与另一个 EntityType的关联。Key 的子 元素将实体的主键定义为其 Id 属性，每个Property元素都包含有关实体属性的数据，例如实体的名称、类型或可空性。

NavigationProperty是一种特殊的属性，它描述了相关实体的“访问点”。

### 3.2. 关联 元素

Association 元素描述了两个实体之间的 关联，其中包括两端的多重性和可选的参照完整性约束：

```xml
<Association Name="CarModel_CarMaker_Many_One0">
    <End Type="default.CarModel" Multiplicity="" Role="CarModel"/>
    <End Type="default.CarMaker" Multiplicity="1" Role="CarMaker"/>
    <ReferentialConstraint>
        <Principal Role="CarMaker">
            <PropertyRef Name="Id"/>
        </Principal>
        <Dependent Role="CarModel">
            <PropertyRef Name="Maker"/>
        </Dependent>
    </ReferentialConstraint>
</Association>
```

此处，Association元素定义了CarModel和CarMaker实体之间的一对多关系，其中前者充当依赖方。

### 3.3. 实体集元素

我们将探索的最后一个模式概念是EntitySet元素，它表示给定类型的实体集合。虽然很容易将它们类比为表格——在许多情况下，它们就是这样——更好的类比是视图。这样做的原因是我们可以为同一个EntityType设置多个EntitySet元素，每个元素代表可用数据的不同子集。

EntityContainer元素是顶级架构元素，它对所有可用的EntitySet进行分组：

```xml
<EntityContainer Name="defaultContainer" 
  m:IsDefaultEntityContainer="true">
    <EntitySet Name="CarModels" 
      EntityType="default.CarModel"/>
    <EntitySet Name="CarMakers" 
      EntityType="default.CarMaker"/>
</EntityContainer>
```

在我们的简单示例中，我们只有两个EntitySet，但我们也可以添加其他视图，例如ForeignCarMakers或HistoricCarMakers。

## 4. OData URL 和方法

为了访问 OData 服务公开的数据，我们使用常规的 HTTP 动词：

-   GET 返回一个或多个实体
-   POST 将新实体添加到现有 实体集中
-   PUT 替换给定的实体
-   PATCH 替换给定实体的特定属性
-   DELETE 删除给定的实体

所有这些操作都需要一个资源路径来执行。资源路径可以定义实体集、实体甚至实体内的属性。

让我们看一下用于访问我们之前的 OData 服务的示例 URL：

```bash
http://example.org/odata/CarMakers

```

此 URL 的第一部分，从协议开始到odata/路径段，称为服务根 URL，并且对于此服务的所有资源路径都是相同的。 由于服务根始终相同，我们将在以下 URL 示例中将其替换为省略号(“…”)。

在这种情况下， CarMakers指的 是服务元数据中声明的EntitySets之一。我们可以使用常规浏览器访问此 URL，然后它应该返回包含该类型所有现有实体的文档：

```xml
<?xml version="1.0" encoding="utf-8"?>
<feed xmlns="http://www.w3.org/2005/Atom" 
  xmlns:m="http://schemas.microsoft.com/ado/2007/08/dataservices/metadata" 
  xmlns:d="http://schemas.microsoft.com/ado/2007/08/dataservices" 
  xml:base="http://localhost:8080/odata/">
    <id>http://localhost:8080/odata/CarMakers</id>
    <title type="text">CarMakers</title>
    <updated>2019-04-06T17:51:33.588-03:00</updated>
    <author>
        <name/>
    </author>
    <link href="CarMakers" rel="self" title="CarMakers"/>
    <entry>
      <id>http://localhost:8080/odata/CarMakers(1L)</id>
      <title type="text">CarMakers</title>
      <updated>2019-04-06T17:51:33.589-03:00</updated>
      <category term="default.CarMaker" 
        scheme="http://schemas.microsoft.com/ado/2007/08/dataservices/scheme"/>
      <link href="CarMakers(1L)" rel="edit" title="CarMaker"/>
      <link href="CarMakers(1L)/CarModelDetails" 
        rel="http://schemas.microsoft.com/ado/2007/08/dataservices/related/CarModelDetails" 
        title="CarModelDetails" 
        type="application/atom+xml;type=feed"/>
        <content type="application/xml">
            <m:properties>
                <d:Id>1</d:Id>
                <d:Name>Special Motors</d:Name>
            </m:properties>
        </content>
    </entry>  
  ... other entries omitted
</feed>
```

返回的文档包含 每个CarMaker实例的条目元素。

让我们仔细看看我们可以获得哪些信息：

-   id：指向此特定实体的链接
-   title/author/updated : 关于这个条目的元数据
-   链接元素：链接用于指向用于编辑实体(rel=”edit”)或相关实体的资源。在这种情况下，我们有一个链接，可以将我们带到 与此特定 CarMaker关联的CarModel实体集。
-   content : CarModel实体的属性值

这里要注意的一个重点是使用键值对来标识实体集中的特定实体。在我们的示例中，键是数字，因此像 CarMaker(1L)这样的资源路径指的是主键值等于 1 的实体——这里的“ L ”仅表示一个长值，可以省略。

## 5.查询选项

我们可以将查询选项传递给资源 URL，以便修改返回数据的许多方面，例如限制返回集的大小或其排序。OData 规范定义了一组丰富的选项，但在这里我们将重点关注最常见的选项。

作为一般规则，查询选项可以相互组合，从而使客户端可以轻松实现常见的功能，例如分页、过滤和排序结果列表。

### 5.1. $top和$skip

我们可以使用$top和$skip查询选项浏览大型数据集：

```bash
.../CarMakers?$top=10&$skip=10

```

$top告诉服务我们只需要 CarMakers实体集的前 10 条记录。在$top之前应用的$ skip告诉服务器跳过前 10 条记录。

了解给定实体集的大小通常很有用，为此，我们可以使用$count子资源：

```bash
.../CarMakers/$count

```

此资源生成包含相应集合大小的文本/纯文本文档。在这里，我们必须注意提供者支持的具体 OData 版本。OData V2 支持将 $count作为集合中的子资源，而 V4 允许将其用作查询参数。在这种情况下，$count是一个布尔值，因此我们需要相应地更改 URL：

```bash
.../CarMakers?$count=true

```

### 5.2. $过滤器

我们使用$filter查询选项将给定实体集中返回的实体限制为与给定条件匹配的实体。$filter的值 是一个逻辑表达式，支持基本运算符、分组和许多有用的函数。例如，让我们构建一个查询，返回其Name 属性以字母“B”开头的所有CarMaker实例：

```bash
.../CarMakers?$filter=startswith(Name,'B')

```

现在，让我们结合一些逻辑运算符来搜索特定年份和制造商的CarModels ：

```bash
.../CarModels?$filter=Year eq 2008 and CarMakerDetails/Name eq 'BWM'

```

在这里，我们使用了相等运算符eq 来指定属性的值。我们还可以看到如何在表达式中使用相关实体的属性。

### 5.3. $展开

默认情况下，OData 查询不返回相关实体的数据，这通常是可以的。我们可以使用$expand查询选项请求将来自给定相关实体的数据与主要内容内联包含在内。

使用我们的示例域，让我们构建一个从给定模型及其制造商返回数据的 URL ，从而避免额外的服务器往返：

```bash
.../CarModels(1L)?$expand=CarMakerDetails

```

返回的文档现在包含CarMaker数据作为相关实体的一部分：

```xml
<?xml version="1.0" encoding="utf-8"?>
<entry xmlns="http://www.w3.org/2005/Atom" 
  xmlns:m="http://schemas.microsoft.com/ado/2007/08/dataservices/metadata" 
  xmlns:d="http://schemas.microsoft.com/ado/2007/08/dataservices" 
  xml:base="http://localhost:8080/odata/">
    <id>http://example.org/odata/CarModels(1L)</id>
    <title type="text">CarModels</title>
    <updated>2019-04-07T11:33:38.467-03:00</updated>
    <category term="default.CarModel" 
      scheme="http://schemas.microsoft.com/ado/2007/08/dataservices/scheme"/>
    <link href="CarModels(1L)" rel="edit" title="CarModel"/>
    <link href="CarModels(1L)/CarMakerDetails" 
      rel="http://schemas.microsoft.com/ado/2007/08/dataservices/related/CarMakerDetails" 
      title="CarMakerDetails" 
      type="application/atom+xml;type=entry">
        <m:inline>
            <entry xml:base="http://localhost:8080/odata/">
                <id>http://example.org/odata/CarMakers(1L)</id>
                <title type="text">CarMakers</title>
                <updated>2019-04-07T11:33:38.492-03:00</updated>
                <category term="default.CarMaker" 
                  scheme="http://schemas.microsoft.com/ado/2007/08/dataservices/scheme"/>
                <link href="CarMakers(1L)" rel="edit" title="CarMaker"/>
                <link href="CarMakers(1L)/CarModelDetails" 
                  rel="http://schemas.microsoft.com/ado/2007/08/dataservices/related/CarModelDetails" 
                  title="CarModelDetails" 
                  type="application/atom+xml;type=feed"/>
                <content type="application/xml">
                    <m:properties>
                        <d:Id>1</d:Id>
                        <d:Name>Special Motors</d:Name>
                    </m:properties>
                </content>
            </entry>
        </m:inline>
    </link>
    <content type="application/xml">
        <m:properties>
            <d:Id>1</d:Id>
            <d:Maker>1</d:Maker>
            <d:Name>Muze</d:Name>
            <d:Sku>SM001</d:Sku>
            <d:Year>2018</d:Year>
        </m:properties>
    </content>
</entry>
```

### 5.4. $选择

我们使用 $select 查询选项来通知 OData 服务它应该只返回给定属性的值。这在我们的实体具有大量属性但我们只对其中一些属性感兴趣的情况下很有用。

让我们在仅返回Name和Sku 属性的查询中使用此选项：

```bash
.../CarModels(1L)?$select=Name,Sku

```

生成的文档现在只有请求的属性：

```xml
... xml omitted
    <content type="application/xml">
        <m:properties>
            <d:Name>Muze</d:Name>
            <d:Sku>SM001</d:Sku>
        </m:properties>
    </content>
... xml omitted
```

我们还可以看到，甚至连相关的实体都被省略了。为了包含它们，我们需要在$select 选项中包含关系的名称。

### 5.5. $orderBy

$orderBy选项与其对应的SQL 选项非常相似。我们用它来指定我们希望服务器返回一组给定实体的顺序。在其更简单的形式中，它的值只是来自所选实体的属性名称列表，可以选择通知顺序方向：

```bash
.../CarModels?$orderBy=Name asc,Sku desc

```

此查询将 生成按名称和 SKU 分别按升序和降序排列的CarModel 列表。

这里的一个重要细节是与给定属性的方向部分一起使用的大小写：虽然规范要求服务器必须支持关键字asc和 desc的大小写字母的任意组合，但它还要求客户端仅使用小写字母.

### 5.6. $格式

此选项定义服务器应使用的数据表示格式，它优先于任何 HTTP 内容协商标头，例如Accept。它的值必须是完整的 MIME 类型或特定于格式的短格式。

例如，我们可以使用json 作为application/json的缩写：

```bash
.../CarModels?$format=json

```

此 URL 指示我们的服务使用 JSON 格式返回数据，而不是我们之前看到的 XML。如果此选项不存在，服务器将使用Accept标头的值(如果存在)。当两者都不可用时，服务器可以自由选择任何表示形式——通常是 XML 或 JSON。

具体到 JSON，它基本上是无模式的。但是，OData 4.01也[为元数据端点定义了一个 JSON 模式](http://docs.oasis-open.org/odata/odata-csdl-json/v4.01/odata-csdl-json-v4.01.html)。这意味着我们现在可以编写可以完全摆脱 XML 处理的客户端(如果他们愿意的话)。

## 六. 总结

在对 OData 的简要介绍中，我们介绍了它的基本语义以及如何执行简单的数据集导航。我们的后续文章将从我们离开的地方继续，直接进入 Olingo 图书馆。然后我们将看到如何使用这个库来实现示例服务。