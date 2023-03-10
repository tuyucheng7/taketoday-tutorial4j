## 1. 概述

[Apache Geode](https://www.baeldung.com/apache-geode)通过分布式云架构提供数据管理解决方案。通过[Spring Data](https://www.baeldung.com/spring-data) API对Apache Geode服务器进行数据访问是最理想的。

在本教程中，我们将探索用于配置和开发Apache Geode Java客户端应用程序的[Spring Data Geode](https://spring.io/projects/spring-data-geode)。

## 2. Spring Data Geode

Spring Data Geode库使Java应用程序能够通过XML和注解配置Apache Geode服务器。同时，该库还可以方便地创建Apache Geode缓存客户端-服务器应用程序。

Spring Data Geode库类似于[Spring Data Gemfire](https://www.baeldung.com/spring-data-gemfire)。除了[细微差别](https://spring.io/blog/2017/10/26/diff-q-spring-data-gemfire-spring-data-geode)之外，后者还提供与[Pivotal Gemfire](https://pivotal.io/pivotal-gemfire)的集成，Pivotal Gemfire是Apache Geode的商业版本。

在此过程中，我们将探索一些Spring Data Geode注解，以将Java应用程序配置到Apache Geode的缓存客户端中。

## 3. Maven依赖

让我们将最新的[spring-geode-starter](https://central.sonatype.com/artifact/org.springframework.geode/spring-geode-starter/1.7.5)依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.geode</groupId>
    <artifactId>spring-geode-starter</artifactId>
    <version>1.1.1.RELEASE</version>
</dependency>
```

## 4. Apache Geode的@ClientCacheApplication与Spring Boot

首先，让我们使用@SpringBootApplication创建一个Spring Boot主类ClientCacheApp：

```java
@SpringBootApplication 
public class ClientCacheApp {
    public static void main(String[] args) {
        SpringApplication.run(ClientCacheApp.class, args); 
    } 
}
```

然后，为了将ClientCacheApp类转换为Apache Geode缓存客户端，我们将添加Spring Data Geode提供的[@ClientCacheApplication](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/ClientCacheApplication.html)：

```java
@ClientCacheApplication
// existing annotations
public class ClientCacheApp {
    // ...
}
```

就是这样！缓存客户端应用程序已准备好运行。

然而，在启动我们的应用程序之前，我们需要启动Apache Geode服务器。

## 5. 启动Apache Geode服务器

假设[已经设置了Apache Geode](https://www.baeldung.com/apache-geode#installation-and-setup)和[gfsh](https://geode.apache.org/docs/guide/19/tools_modules/gfsh/chapter_overview.html)命令行界面，我们可以启动一个名为basicLocator的定位器，然后启动一个名为basicServer的服务器。

为此，让我们在gfsh CLI中运行以下命令：

```shell
gfsh>start locator --name="basicLocator"
```

```shell
gfsh>start server --name="basicServer"
```

服务器开始运行后，我们可以列出所有成员：

```shell
gfsh>list members
```

gfsh CLI输出应该列出定位器和服务器：

```shell
    Name     | Id
------------ | ------------------------------------------------------------------
basicLocator | 10.25.3.192(basicLocator:25461:locator)<ec><v0>:1024 [Coordinator]
basicServer  | 10.25.3.192(basicServer:25546)<v1>:1025
```

现在我们可以使用Maven命令运行我们的缓存客户端应用程序：

```shell
mvn spring-boot:run
```

## 6. 配置

让我们配置缓存客户端应用程序以通过Apache Geode服务器访问数据。

### 6.1 区域

首先，我们将创建一个名为Author的实体，然后将其定义为Apache Geode区域。Region类似于RDBMS中的表：

```java
@Region("Authors")
public class Author {
    @Id
    private Long id;

    private String firstName;
    private String lastName;
    private int age;
}
```

让我们回顾一下Author实体中声明的Spring Data Geode注解。

首先，[@Region](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/mapping/annotation/Region.html)将在Apache Geode服务器中创建Authors区域以保存Author对象。

然后，@Id会将属性标记为主键。

### 6.2 实体

我们可以通过添加[@EnableEntityDefinedRegions](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableEntityDefinedRegions.html)来启用Author实体。

此外，我们将添加[@EnableClusterConfiguration](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableClusterConfiguration.html)让应用程序在Apache Geode服务器中创建区域：

```java
@EnableEntityDefinedRegions(basePackageClasses = Author.class)
@EnableClusterConfiguration
// existing annotations
public class ClientCacheApp {
    // ...
}
```

因此，重新启动应用程序将自动创建区域：

```shell
gfsh>list regions

List of regions
---------------
Authors
```

### 6.3 Repository

接下来，我们将在Author实体上添加CRUD操作。

为此，让我们创建一个名为AuthorRepository的Repository，它扩展了Spring Data的[CrudRepository](https://www.baeldung.com/spring-data-repositories#crudrepository)：

```java
public interface AuthorRepository extends CrudRepository<Author, Long> {
}
```

然后，我们将通过添加[@EnableGemfireRepositories](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/repository/config/EnableGemfireRepositories.html)来启用AuthorRepository：

```java
@EnableGemfireRepositories(basePackageClasses = AuthorRepository.class)
// existing annotations
public class ClientCacheApp {
    // ...
}
```

现在，我们都准备好使用CrudRepository提供的save和findById等方法对Author实体执行CRUD操作。

### 6.4 索引

Spring Data Geode提供了一种在Apache Geode服务器中创建和启用索引的简单方法。

首先，我们将[@EnableIndexing](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableIndexing.html)添加到ClientCacheApp类：

```java
@EnableIndexing
// existing annotations
public class ClientCacheApp {
    // ...
}
```

然后，让我们将@Indexed添加到Author类中的属性：

```java
public class Author {
    @Id
    private Long id;

    @Indexed
    private int age;

    // existing data members
}
```

在这里，Spring Data Geode将根据Author实体中定义的注解自动实现索引。

因此，@Id将为id实现主键索引。类似地，@Indexed将实现age的哈希索引。

现在，让我们重新启动应用程序并确认在Apache Geode服务器中创建的索引：

```shell
gfsh> list indexes

Member Name | Region Path |       Name        | Type  | Indexed Expression | From Clause | Valid Index
----------- | ----------- | ----------------- | ----- | ------------------ | ----------- | -----------
basicServer | /Authors    | AuthorsAgeKeyIdx  | RANGE | age                | /Authors    | true
basicServer | /Authors    | AuthorsIdHashIdx  | RANGE | id                 | /Authors    | true
```

同样，我们可以使用@LuceneIndexed为String类型的属性创建Apache Geode Lucene索引。

### 6.5 连续查询

连续查询使应用程序能够在服务器中的数据发生更改时接收自动通知。它与查询匹配并依赖于订阅模型。

要添加该功能，我们将创建AuthorService并使用匹配查询添加[@ContinuousQuery](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/listener/annotation/ContinuousQuery.html)：

```java
@Service
public class AuthorService {
    @ContinuousQuery(query = "SELECT * FROM /Authors a WHERE a.id = 1")
    public void process(CqEvent event) {
        System.out.println("Author #" + event.getKey() + " updated to " + event.getNewValue());
    }
}
```

要使用连续查询，我们将启用服务器到客户端的订阅：

```java
@ClientCacheApplication(subscriptionEnabled = true)
// existing annotations
public class ClientCacheApp {
    // ...
}
```

因此，每当我们修改id等于1的Author对象时，我们的应用程序都会在process方法中收到自动通知。

## 7. 附加注解

让我们探索Spring Data Geode库中额外提供的一些方便的注解。

### 7.1 @PeerCacheApplication

到目前为止，我们已经检查了一个作为Apache Geode缓存客户端的Spring Boot应用程序。有时，我们可能需要我们的应用程序是Apache Geode对等缓存应用程序。

然后，我们应该用[@PeerCacheApplication](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/PeerCacheApplication.html)代替@CacheClientApplication来标注主类。

此外，@PeerCacheApplication将自动创建一个嵌入式对等缓存实例进行连接。

### 7.2 @CacheServerApplication

同样，要让我们的Spring Boot应用程序既是对等成员又是服务器，我们可以使用[@CacheServerApplication](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/CacheServerApplication.html)标注主类。

### 7.3 @EnableHttpService

我们可以为@PeerCacheApplication和@CacheServerApplication启用Apache Geode的嵌入式HTTP服务器。

为此，我们需要使用[@EnableHttpService](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableHttpService.html)标注主类。默认情况下，HTTP服务在端口7070上启动。

### 7.4 @EnableLogging

我们可以通过简单地将[@EnableLogging](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableLogging.html)添加到主类来启用日志记录。同时，我们可以使用logLevel和logFile属性来设置相应的属性。

### 7.5 @EnablePdx

此外，我们可以为所有域启用Apache Geode的PDX序列化技术，只需将[@EnablePdx](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/EnablePdx.html)添加到主类即可。

### 7.6 @EnableSsl和@EnableSecurity

我们可以使用[@EnableSsl](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableSsl.html)来打开Apache Geode的TCP/IP套接字SSL。同样，[@EnableSecurity](https://docs.spring.io/spring-data/geode/docs/current/api/org/springframework/data/gemfire/config/annotation/EnableSecurity.html)可用于启用Apache Geode的身份验证和授权安全性。

## 8. 总结

在本教程中，我们探索了Apache Geode的Spring Data。

首先，我们创建了一个Spring Boot应用程序作为Apache Geode缓存客户端应用程序。

同时，我们检查了Spring Data Geode提供的一些方便的注解来配置和启用Apache Geode功能。

最后，我们探索了一些额外的注解，例如@PeerCacheApplication和@CacheServerApplication，以将应用程序更改为集群配置中的对等点或服务器。