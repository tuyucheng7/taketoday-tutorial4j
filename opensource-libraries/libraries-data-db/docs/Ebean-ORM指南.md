## 1. 简介

[Ebean](https://ebean-orm.github.io/)是一种用Java编写的对象关系映射工具。

它支持用于声明实体的标准 JPA 注解。但是，它为持久化提供了一个更简单的 API。事实上，关于 Ebean 架构值得一提的一点是它是无会话的，这意味着它不完全管理实体。

除此之外，它还附带了一个查询 API，并支持使用原生 SQL 编写查询。Ebean 支持所有主要的数据库供应商，例如 Oracle、Postgres、MySql、H2 等。

在本教程中，我们将了解如何使用 Ebean 和 H2 创建、持久化和查询实体。

## 2.设置

首先，让我们获取依赖项以及一些基本配置。

### 2.1. Maven 依赖项

在我们开始之前，让我们导入所需的依赖项：

```xml
<dependency>
    <groupId>io.ebean</groupId>
    <artifactId>ebean</artifactId>
    <version>11.22.4</version>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.196</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.6</version>
</dependency>
```

最新版本的[Ebean](https://search.maven.org/search?q=g:io.ebean AND a:ebean&core=gav)、[H2](https://search.maven.org/search?q=g:com.h2database AND a:h2&core=gav)和[Logback](https://search.maven.org/search?q=g:ch.qos.logback AND a:logback-classic&core=gav)可以在 Maven Central 上找到。

### 2.2. 增强功能

Ebean 需要修改实体 bean，以便它们可以被服务器管理。因此，我们将添加一个 Maven 插件来完成这项工作：

```xml
<plugin>
    <groupId>io.ebean</groupId>
    <artifactId>ebean-maven-plugin</artifactId>
    <version>11.11.2</version>
    <executions>
        <execution>
            <id>main</id>
            <phase>process-classes</phase>
            <configuration>
                <transformArgs>debug=1</transformArgs>
            </configuration>
            <goals>
                <goal>enhance</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

我们还需要为 Maven 插件提供包含使用事务的实体和类的包的名称。为此，我们创建文件ebean.mf：

```plaintext
entity-packages: com.baeldung.ebean.model
transactional-packages: com.baeldung.ebean.app
```

### 2.3. 记录

我们还创建logback.xml并将某些包的日志记录级别设置为TRACE，以便我们可以看到正在执行的语句：

```plaintext
<logger name="io.ebean.DDL" level="TRACE"/>
<logger name="io.ebean.SQL" level="TRACE"/>
<logger name="io.ebean.TXN" level="TRACE"/>
```

## 3.配置服务器

我们需要创建一个EbeanServer实例来保存实体或在数据库上运行查询。我们可以通过两种方式创建服务器实例——使用默认属性文件或以编程方式创建。

### 3.1. 使用默认属性文件

默认属性文件可以是properties或yaml类型。Ebean 将在名称为application.properties、ebean.properties或application.yml的文件中搜索配置。

除了提供数据库连接细节外，我们还可以指示 Ebean 创建和运行 DDL 语句。

现在，让我们看一个示例配置：

```plaintext
ebean.db.ddl.generate=true
ebean.db.ddl.run=true

datasource.db.username=sa
datasource.db.password=
datasource.db.databaseUrl=jdbc:h2:mem:customer
datasource.db.databaseDriver=org.h2.Driver
```

### 3.2. 使用服务器配置

接下来，让我们看看如何使用EbeanServerFactory和ServerConfig以编程方式创建相同的服务器：

```java
ServerConfig cfg = new ServerConfig();

Properties properties = new Properties();
properties.put("ebean.db.ddl.generate", "true");
properties.put("ebean.db.ddl.run", "true");
properties.put("datasource.db.username", "sa");
properties.put("datasource.db.password", "");
properties.put("datasource.db.databaseUrl","jdbc:h2:mem:app2";
properties.put("datasource.db.databaseDriver", "org.h2.Driver");

cfg.loadFromProperties(properties);
EbeanServer server = EbeanServerFactory.create(cfg);
```

### 3.3. 默认服务器实例

单个EbeanServer实例映射到单个数据库。根据我们的要求，我们也可以创建多个EbeanServer实例。

如果只创建一个服务器实例，默认情况下，它被注册为默认服务器实例。可以使用Ebean类上的静态方法在应用程序的任何位置访问它：

```java
EbeanServer server = Ebean.getDefaultServer();
```

如果有多个数据库，可以将其中一个服务器实例注册为默认服务器实例：

```java
cfg.setDefaultServer(true);
```

## 4.创建实体

Ebean 提供对 JPA 注解的完全支持以及使用其自己的注解的附加功能。

让我们使用 JPA 和 Ebean 注解创建一些实体。首先，我们将创建一个BaseModel，其中包含跨实体通用的属性：

```java
@MappedSuperclass
public abstract class BaseModel {

    @Id
    protected long id;
    
    @Version
    protected long version;
    
    @WhenCreated
    protected Instant createdOn;
    
    @WhenModified
    protected Instant modifiedOn;

    // getters and setters
}
```

在这里，我们使用了MappedSuperClass JPA 注解来定义BaseModel。 还有两个 Ebean 注解io.ebean.annotation.WhenCreated和io.ebean.annotation.WhenModified用于审计目的。

接下来，我们将创建两个扩展BaseModel的实体Customer和Address：

```java
@Entity
public class Customer extends BaseModel {

    public Customer(String name, Address address) {
        super();
        this.name = name;
        this.address = address;
    }

    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    Address address;

    // getters and setters
}


@Entity
public class Address extends BaseModel{

    public Address(String addressLine1, String addressLine2, String city) {
        super();
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
    }
    
    private String addressLine1;
    private String addressLine2;
    private String city;

    // getters and setters
}
```

在Customer中，我们定义了与Address的一对一映射，并将 set cascade type 添加到ALL以便子实体也与父实体一起更新。

## 5.基本CRUD操作

之前我们已经了解了如何配置EbeanServer并创建了两个实体。现在，让我们对它们进行一些基本的 CRUD 操作。

我们将使用默认服务器实例来保存和访问数据。Ebean类还提供静态方法来持久化和访问将请求代理到默认服务器实例的数据：

```java
Address a1 = new Address("5, Wide Street", null, "New York");
Customer c1 = new Customer("John Wide", a1);

EbeanServer server = Ebean.getDefaultServer();
server.save(c1);

c1.setName("Jane Wide");
c1.setAddress(null);
server.save(c1);

Customer foundC1 = Ebean.find(Customer.class, c1.getId());

Ebean.delete(foundC1);
```

首先，我们创建一个Customer对象并使用默认服务器实例通过save()保存它。

接下来，我们更新客户详细信息并使用save()再次保存它。

最后，我们在Ebean上使用静态方法find()来获取客户并使用delete()删除它。

## 6.查询

查询 API 也可用于创建带有过滤器和谓词的对象图。我们可以使用Ebean或 EbeanServer 来创建和执行查询。

让我们看一下按城市查找客户并返回 仅填充了一些字段的客户和地址 对象的查询：

```java
Customer customer = Ebean.find(Customer.class)
            .select("name")
            .fetch("address", "city")
            .where()
            .eq("city", "San Jose")
            .findOne();
```

在这里，我们使用find()表示我们想要查找Customer类型的实体。接下来，我们使用 select()来指定要填充到 Customer对象中的属性。

稍后，我们使用 fetch()来指示我们要获取 属于Customer的Address对象以及我们要获取 city字段。

最后，我们添加一个谓词并将结果的大小限制为 1。

## 七、交易

Ebean 默认在新事务中执行每个语句或查询。

尽管在某些情况下这可能不是问题。有时我们可能希望在单个事务中执行一组语句。

在这种情况下，如果我们用 io.ebean.annotations.Transactional 注解方法，方法中的所有语句都将在同一个事务中执行：

```java
@Transactional
public static void insertAndDeleteInsideTransaction() {
    Customer c1 = getCustomer();
    EbeanServer server = Ebean.getDefaultServer();
    server.save(c1);
    Customer foundC1 = server.find(Customer.class, c1.getId());
    server.delete(foundC1);
}
```

## 8. 构建项目

最后，我们可以使用以下命令构建 Maven 项目：

```plaintext
compile io.ebean:ebean-maven-plugin:enhance
```

## 9.总结

总而言之，我们了解了 Ebean 的基本特性，这些特性可用于在关系数据库中持久化和查询实体。