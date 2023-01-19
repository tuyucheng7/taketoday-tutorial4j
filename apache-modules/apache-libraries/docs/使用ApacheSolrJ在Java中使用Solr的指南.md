## 1. 概述

[Apache Solr](https://lucene.apache.org/solr/)是一个构建在 Lucene 之上的开源搜索平台。Apache SolrJ 是一个基于Java的 Solr 客户端，它为搜索的主要功能(如索引、查询和删除文档)提供接口。

在本文中，我们将探索如何使用 SolrJ 与 Apache Solr 服务器进行交互。

## 2.设置

要在你的机器上安装 Solr 服务器，请参阅[Solr 快速入门指南](https://lucene.apache.org/solr/quickstart.html)。

安装过程很简单——只需下载 zip/tar 包，解压内容，然后从命令行启动服务器。对于本文，我们将创建一个 Solr 服务器，其核心名为“bigboxstore”：

```bash
bin/solr start
bin/solr create -c 'bigboxstore'
```

默认情况下，Solr 侦听端口 8983 以获取传入的 HTTP 查询。你可以通过在浏览器中打开http://localhost:8983/solr/#/bigboxstore URL 并观察 Solr 仪表板来验证它是否已成功启动。

## 3.Maven配置

现在我们已经启动并运行了 Solr 服务器，让我们直接跳转到 SolrJJava客户端。要在你的项目中使用 SolrJ，你需要在pom.xml文件中声明以下 Maven 依赖项：

```xml
<dependency>
    <groupId>org.apache.solr</groupId>
    <artifactId>solr-solrj</artifactId>
    <version>6.4.0</version>
</dependency>
```

你总能找到由[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.solr" a%3A"solr-solrj")托管的最新版本。

## 4.Apache SolrJJavaAPI

让我们通过连接到我们的 Solr 服务器来启动 SolrJ 客户端：

```java
String urlString = "http://localhost:8983/solr/bigboxstore";
HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
solr.setParser(new XMLResponseParser());
```

注意：SolrJ 使用二进制格式而不是 XML作为其默认响应格式。为了与 Solr 兼容，需要显式调用setParser()到 XML，如上所示。可以在[此处](https://cwiki.apache.org/confluence/display/solr/Using+SolrJ)找到更多详细信息。

### 4.1. 索引文件

让我们使用SolrInputDocument定义要索引的数据，并使用add()方法将其添加到我们的索引中：

```java
SolrInputDocument document = new SolrInputDocument();
document.addField("id", "123456");
document.addField("name", "Kenmore Dishwasher");
document.addField("price", "599.99");
solr.add(document);
solr.commit();
```

注意：任何修改 Solr 数据库的操作都需要在操作后跟上commit()。

### 4.2. 用 Bean 建立索引

你还可以使用 beans 索引 Solr 文档。让我们定义一个 ProductBean，其属性用 @Field注解：

```java
public class ProductBean {

    String id;
    String name;
    String price;

    @Field("id")
    protected void setId(String id) {
        this.id = id;
    }

    @Field("name")
    protected void setName(String name) {
        this.name = name;
    }

    @Field("price")
    protected void setPrice(String price) {
        this.price = price;
    }

    // getters and constructor omitted for space
}
```

然后，让我们将 bean 添加到我们的索引中：

```java
solrClient.addBean( new ProductBean("888", "Apple iPhone 6s", "299.99") );
solrClient.commit();
```

### 4.3. 按字段和 Id 查询索引文档

让我们通过使用SolrQuery查询我们的 Solr 服务器来验证我们的文档是否已添加。

来自服务器的QueryResponse将包含一个SolrDocument对象列表，该对象与格式为field:value的任何查询相匹配。在本例中，我们按价格查询：

```java
SolrQuery query = new SolrQuery();
query.set("q", "price:599.99");
QueryResponse response = solr.query(query);

SolrDocumentList docList = response.getResults();
assertEquals(docList.getNumFound(), 1);

for (SolrDocument doc : docList) {
     assertEquals((String) doc.getFieldValue("id"), "123456");
     assertEquals((Double) doc.getFieldValue("price"), (Double) 599.99);
}
```

一个更简单的选项是使用getById()按Id查询。如果找到匹配项，它将只返回一个文档：

```java
SolrDocument doc = solr.getById("123456");
assertEquals((String) doc.getFieldValue("name"), "Kenmore Dishwasher");
assertEquals((Double) doc.getFieldValue("price"), (Double) 599.99);
```

### 4.4. 删除文件

当我们想从索引中删除文档时，我们可以使用deleteById()并验证它是否已被删除：

```java
solr.deleteById("123456");
solr.commit();
SolrQuery query = new SolrQuery();
query.set("q", "id:123456");
QueryResponse response = solr.query(query);
SolrDocumentList docList = response.getResults();
assertEquals(docList.getNumFound(), 0);
```

我们也可以选择deleteByQuery()，所以让我们尝试删除任何具有特定名称的文档：

```java
solr.deleteByQuery("name:Kenmore Dishwasher");
solr.commit();
SolrQuery query = new SolrQuery();
query.set("q", "id:123456");
QueryResponse response = solr.query(query);
SolrDocumentList docList = response.getResults();
assertEquals(docList.getNumFound(), 0);
```

## 5.总结

在这篇简短的文章中，我们了解了如何使用 SolrJJavaAPI 执行与 Apache Solr 全文搜索引擎的一些常见交互。