## 1. 概述

在本文中，**我们将以实用的方式探讨Spring Data Solr的基础知识**。

Apache Solr是一个开源的、可部署的企业全文搜索引擎。你可以在[官方网站](https://lucene.apache.org/solr/resources.html)上找到有关Solr功能的更多信息。

我们将展示如何进行简单的Solr配置，当然还有如何与服务器交互。

首先，我们需要启动一个Solr服务器并创建一个核心来存储数据(默认情况下，Solr将以schemaless模式创建)。

## 2. Spring Data

就像任何其他Spring Data项目一样，Spring Data Solr有一个明确的目标，即删除样板代码。

### 2.1 Maven依赖

让我们首先将Spring Data Solr依赖项添加到我们的pom.xml：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-solr</artifactId>
    <version>4.3.14</version>
</dependency>
```

你可以在[此处](https://central.sonatype.com/artifact/org.springframework.data/spring-data-solr/4.3.15)找到最新的依赖项。

### 2.2 定义文档

让我们定义一个名为Product的文档：

```java
@SolrDocument(solrCoreName = "product")
public class Product {

    @Id
    @Indexed(name = "id", type = "string")
    private String id;

    @Indexed(name = "name", type = "string")
    private String name;
}
```

@SolrDocument注解表示Product类是一个Solr文档并索引到product核心。用@Indexed标注的字段在Solr中被索引并且可以被搜索。

### 2.3 定义Repository接口

接下来，我们需要通过扩展Spring Data Solr提供的Repository来创建Repository接口。我们自然会使用Product和String作为我们的实体ID对其进行参数化：

```java
public interface ProductRepository extends SolrCrudRepository<Product, String> {

    List<Product> findByName(String name);

    @Query("id:*?0* OR name:*?0*")
    Page<Product> findByCustomQuery(String searchTerm, Pageable pageable);

    @Query(name = "Product.findByNamedQuery")
    Page<Product> findByNamedQuery(String searchTerm, Pageable pageable);
}
```

请注意我们如何在SolrCrudRepository提供的API之上定义三个方法。我们将在接下来的几节中讨论这些内容。

另请注意，Product.findByNamedQuery属性是在类路径文件夹中的Solr命名查询文件solr-named-queries.properties中定义的：

```java
Product.findByNamedQuery=id:*?0* OR name:*?0*
```

### 2.4 Java配置

现在我们将探讨Solr持久层的Spring配置：

```java
@Configuration
@EnableSolrRepositories(
      basePackages = "cn.tuyucheng.taketoday.spring.data.solr.repository",
      namedQueriesLocation = "classpath:solr-named-queries.properties")
@ComponentScan
public class SolrConfig {

    @Bean
    public SolrClient solrClient() {
        return new HttpSolrClient.Builder("http://localhost:8983/solr").build();
    }

    @Bean
    public SolrTemplate solrTemplate(SolrClient client) throws Exception {
        return new SolrTemplate(client);
    }
}
```

我们正在使用@EnableSolrRepositories来扫描Repository的包。请注意，我们指定了命名查询属性文件的位置并启用了多核支持。

如果未启用多核，则默认情况下Spring Data将假定Solr配置是针对单核的。我们在这里启用多核，仅供参考。

## 3. 使用Spring Boot的Spring Data Solr

在本节中，我们将看看Spring Boot应用程序中的设置。

让我们首先将Spring Boot Starter Data Solr依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-solr</artifactId>
    <version>2.4.12</version>
</dependency>
```

你可以在[此处](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-data-solr/3.0.3)找到最新版本的依赖项。

我们还必须使用Solr URL的值在application.properties文件中**定义属性spring.data.solr.host**：

```properties
spring.data.solr.host=http://localhost:8983/solr
```

确保Solr正在指定的URL上运行。

这是我们在Spring Boot应用程序中设置Spring Data Solr所需的所有配置，因为**将启动器依赖项放在类路径上将加载自动配置**。

## 4. 索引、更新和删除

为了在Solr中搜索文档，应该将文档索引到Solr Repository。

以下示例仅通过使用SolrCrudRepository的save方法对Solr存储中的产品文档进行索引：

```java
Product phone = new Product();
phone.setId("P0001");
phone.setName("Phone");
productRepository.save(phone);
```

现在让我们检索并更新文档：

```java
Product retrievedProduct = productRepository.findById("P0001").get();
retrievedProduct.setName("Smart Phone");
productRepository.save(retrievedProduct);
```

只需调用delete方法即可删除文档：

```java
productRepository.delete(retrievedProduct);
```

## 5. 查询

现在让我们探索Spring Data SolrAPI提供的不同查询技术。

### 5.1 方法名称查询生成

基于方法名称的查询是通过解析方法名称生成预期的查询来执行的：

```java
public List<Product> findByName(String name);
```

在我们的Repository接口中，我们有findByName方法，它根据方法名称生成查询：

```java
List<Product> retrievedProducts = productRepository.findByName("Phone");
```

### 5.2 使用@Query注解查询

可以通过在方法的@Query注解中包含查询来创建Solr搜索查询。在我们的示例中，findByCustomQuery是使用@Query注解进行标注的：

```java
@Query("id:*?0* OR name:*?0*")
public Page<Product> findByCustomQuery(String searchTerm, Pageable pageable);
```

让我们使用此方法来检索文档：

```java
Page<Product> result = productRepository.findByCustomQuery("Phone", PageRequest.of(0, 10));
```

通过调用findByCustomQuery("Phone", PageRequest.of(0, 10))，我们获得了产品文档的第一页，其中在其任何字段id或name中包含单词“Phone”。

### 5.3 命名查询

命名查询类似于带有@Query注解的查询，不同之处在于查询是在单独的属性文件中声明的：

```java
@Query(name = "Product.findByNamedQuery")
public Page<Product> findByNamedQuery(String searchTerm, Pageable pageable);
```

请注意，如果属性文件中查询的key(findByNamedQuery)与方法名称匹配，则不需要@Query注解。

让我们使用命名查询方法检索一些文档：

```java
Page<Product> result = productRepository.findByNamedQuery("one", PageRequest.of(0, 10));
```

## 6. 总结

本文是对Spring Data Solr的快速实用介绍，涵盖了基本配置、定义Repository和自然查询。