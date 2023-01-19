## 1. 概述

[Apache Geode](https://geode.apache.org/)是一个分布式内存数据网格，支持缓存和数据计算。

在本教程中，我们将介绍 Geode 的关键概念并使用其Java客户端运行一些代码示例。

## 2.设置

首先，我们需要下载并安装 Apache Geode 并设置gfsh 环境。为此，我们可以按照[Geode 官方指南](https://geode.apache.org/docs/guide/16/getting_started/15_minute_quickstart_gfsh.html)中的说明进行操作。

其次，本教程将创建一些文件系统工件。因此，我们可以通过创建一个临时目录并从那里启动东西来隔离它们。

### 2.1. 安装和配置

从我们的临时目录，我们需要启动一个Locator实例：

```plaintext
gfsh> start locator --name=locator --bind-address=localhost
```

定位器 负责 Geode Cluster的不同成员之间的协调，我们可以通过 JMX 进一步管理它。

接下来，让我们启动一个Server实例来托管一个或多个数据Region：

```bash
gfsh> start server --name=server1 --server-port=0
```

我们将–server-port选项设置为 0，以便 Geode 将选择任何可用端口。尽管如果我们将其省略，服务器将使用默认端口 40404。服务器是集群的可配置成员，作为长期进程运行并负责管理数据区域。

最后，我们需要一个 区域：

```plaintext
gfsh> create region --name=baeldung --type=REPLICATE
```

Region最终是我们存储数据的地方。

### 2.2. 确认

在我们继续之前，让我们确保一切正常。

首先，让我们检查我们是否有我们的 服务器 和我们的 定位器：

```bash
gfsh> list members
 Name   | Id
------- | ----------------------------------------------------------
server1 | 192.168.0.105(server1:6119)<v1>:1024
locator | 127.0.0.1(locator:5996:locator)<ec><v0>:1024 [Coordinator]
```

接下来，我们有我们的 区域：

```plaintext
gfsh> describe region --name=baeldung
..........................................................
Name            : baeldung
Data Policy     : replicate
Hosting Members : server1

Non-Default Attributes Shared By Hosting Members  

 Type  |    Name     | Value
------ | ----------- | ---------------
Region | data-policy | REPLICATE
       | size        | 0
       | scope       | distributed-ack
```

此外，我们应该在临时目录下的文件系统上有一些目录，称为“locator”和“server1”。

有了这个输出，我们知道我们已经准备好继续前进了。

## 3.Maven依赖

现在我们有一个正在运行的 Geode，让我们开始查看客户端代码。

要在我们的Java代码中使用 Geode，我们需要将 [Apache GeodeJava客户端](https://search.maven.org/search?q=a:geode-core)库添加到我们的 pom中：

```plaintext
<dependency>
     <groupId>org.apache.geode</groupId>
     <artifactId>geode-core</artifactId>
     <version>1.6.0</version>
</dependency>
```

让我们从简单地在几个区域中存储和检索一些数据开始。

## 4. 简单的存储和检索

让我们演示如何存储单个值、成批值以及自定义对象。

要开始在我们的“baeldung”区域中存储数据，让我们使用定位器连接到它：

```java
@Before
public void connect() {
    this.cache = new ClientCacheFactory()
      .addPoolLocator("localhost", 10334)
        .create();
    this.region = cache.<String, String> 
      createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY)
        .create("baeldung");
}
```

### 4.1. 保存单个值

现在，我们可以简单地在我们的区域中存储和检索数据：

```java
@Test
public void whenSendMessageToRegion_thenMessageSavedSuccessfully() {

    this.region.put("A", "Hello");
    this.region.put("B", "Baeldung");

    assertEquals("Hello", region.get("A"));
    assertEquals("Baeldung", region.get("B"));
}
```

### 4.2. 一次保存多个值

我们还可以一次保存多个值，比如在尝试减少网络延迟时：

```java
@Test
public void whenPutMultipleValuesAtOnce_thenValuesSavedSuccessfully() {

    Supplier<Stream<String>> keys = () -> Stream.of("A", "B", "C", "D", "E");
    Map<String, String> values = keys.get()
        .collect(Collectors.toMap(Function.identity(), String::toLowerCase));

    this.region.putAll(values);

    keys.get()
        .forEach(k -> assertEquals(k.toLowerCase(), this.region.get(k)));
}
```

### 4.3. 保存自定义对象

字符串很有用，但迟早我们需要存储自定义对象。

假设我们有一条要使用以下键类型存储的客户记录：

```java
public class CustomerKey implements Serializable {
    private long id;
    private String country;
    
    // getters and setters
    // equals and hashcode
}
```

以及以下值类型：

```java
public class Customer implements Serializable {
    private CustomerKey key;
    private String firstName;
    private String lastName;
    private Integer age;
    
    // getters and setters 
}
```

有几个额外的步骤可以存储这些：

首先，他们应该实现 Serializable。 虽然这不是一个严格的要求，但通过使它们可序列化，[Geode 可以更稳健地存储它们](https://geode.apache.org/docs/guide/16/developing/data_serialization/data_serialization_options.html)。

其次，它们需要在我们应用程序的类路径以及我们的 Geode Server的类路径上。

为了让它们进入服务器的类路径，让我们打包它们，比如使用 mvn clean package。

然后我们可以在新的启动服务器命令中引用生成的 jar ：

```plaintext
gfsh> stop server --name=server1
gfsh> start server --name=server1 --classpath=../lib/apache-geode-1.0-SNAPSHOT.jar --server-port=0
```

同样，我们必须从临时目录运行这些命令。

最后，让我们使用与创建“baeldung”区域相同的命令在服务器上创建一个名为“baeldung-customers”的新区域：

```bash
gfsh> create region --name=baeldung-customers --type=REPLICATE
```

在代码中，我们将像以前一样访问定位器，指定自定义类型：

```java
@Before
public void connect() {
    // ... connect through the locator
    this.customerRegion = this.cache.<CustomerKey, Customer> 
      createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY)
        .create("baeldung-customers");
}
```

然后，我们可以像以前一样存储我们的客户：

```java
@Test
public void whenPutCustomKey_thenValuesSavedSuccessfully() {
    CustomerKey key = new CustomerKey(123);
    Customer customer = new Customer(key, "William", "Russell", 35);

    this.customerRegion.put(key, customer);

    Customer storedCustomer = this.customerRegion.get(key);
    assertEquals("William", storedCustomer.getFirstName());
    assertEquals("Russell", storedCustomer.getLastName());
}
```

## 5. 区域类型

对于大多数环境，根据我们的读写吞吐量要求，我们的区域会有多个副本或多个分区。

到目前为止，我们已经使用了内存中的区域。让我们仔细看看。

### 5.1. 区域

顾名思义，区域在多个服务器上维护其数据的副本。 让我们测试一下。

从工作目录中的gfsh 控制台，让我们向集群添加一个名为server2的服务器：

```plaintext
gfsh> start server --name=server2 --classpath=../lib/apache-geode-1.0-SNAPSHOT.jar --server-port=0
```

请记住，当我们制作“baeldung”时，我们使用了–type=REPLICATE。正因为如此，Geode 会自动将我们的数据到新服务器。

让我们通过停止server1 来验证这一点：

```plaintext
gfsh> stop server --name=server1
```

然后，让我们对“baeldung”区域执行快速查询。

如果数据成功，我们将返回结果：

```plaintext
gfsh> query --query='select e.key from /baeldung.entries e'
Result : true
Limit  : 100
Rows   : 5

Result
------
C
B
A 
E
D
```

所以，看起来成功了！

向我们的区域添加副本可提高数据可用性。而且，因为不止一台服务器可以响应查询，我们也将获得更高的读取吞吐量。

但是，如果他们都崩溃了怎么办？由于这些是内存区域，因此数据将丢失。 为此，我们可以改用 –type=REPLICATE_PERSISTENT，它在时也将数据存储在磁盘上。

### 5.2. 分区区域

对于更大的数据集，我们可以通过配置 Geode 将一个区域分割成单独的分区或桶来更好地扩展系统。

让我们创建一个名为“baeldung-partitioned”的分区区域：

```plaintext
gfsh> create region --name=baeldung-partitioned --type=PARTITION
```

添加一些数据：

```plaintext
gfsh> put --region=baeldung-partitioned --key="1" --value="one"
gfsh> put --region=baeldung-partitioned --key="2" --value="two"
gfsh> put --region=baeldung-partitioned --key="3" --value="three"
```

并快速验证：

```plaintext
gfsh> query --query='select e.key, e.value from /baeldung-partitioned.entries e'
Result : true
Limit  : 100
Rows   : 3

key | value
--- | -----
2   | two
1   | one
3   | three
```

然后，为了验证数据是否已分区，让我们再次停止server1并重新查询：

```plaintext
gfsh> stop server --name=server1
gfsh> query --query='select e.key, e.value from /baeldung-partitioned.entries e'
Result : true
Limit  : 100
Rows   : 1

key | value
--- | -----
2   | two
```

这次我们只取回了一些数据条目，因为该服务器只有一个数据分区，所以当 server1掉线时，它的数据丢失了。

但是，如果我们既需要分区又需要冗余怎么办？Geode 还支持[许多其他类型](https://geode.apache.org/docs/guide/11/reference/topics/region_shortcuts_reference.html)。以下三个很方便：

-   PARTITION_REDUNDANT分区 并在集群的不同成员之间我们的数据
-   PARTITION_PERSISTENT像PARTITION 一样对数据进行分区，但分区到磁盘，并且
-   PARTITION_REDUNDANT_PERSISTENT 为我们提供了所有三种行为。

## 6.对象查询语言

Geode 还支持对象查询语言或 OQL，它比简单的键查找更强大。这有点像 SQL。

对于此示例，让我们使用之前构建的“baeldung-customer”区域。

如果我们再添加几个客户：

```java
Map<CustomerKey, Customer> data = new HashMap<>();
data.put(new CustomerKey(1), new Customer("Gheorge", "Manuc", 36));
data.put(new CustomerKey(2), new Customer("Allan", "McDowell", 43));
this.customerRegion.putAll(data);
```

然后我们可以使用 QueryService来查找名字为“Allan”的客户：

```java
QueryService queryService = this.cache.getQueryService();
String query = 
  "select  from /baeldung-customers c where c.firstName = 'Allan'";
SelectResults<Customer> results =
  (SelectResults<Customer>) queryService.newQuery(query).execute();
assertEquals(1, results.size());
```

## 七、功能

内存数据网格的一个更强大的概念是“将计算带到数据中”的想法。

简而言之，由于 Geode 是纯 Java，因此我们不仅可以轻松地发送数据，还可以轻松地执行对该数据执行的逻辑。

这可能会让我们想起 PL-SQL 或 Transact-SQL 等 SQL 扩展的想法。

### 7.1. 定义函数

为了定义 Geode 要做的工作单元， 我们实现了 Geode 的 Function接口。

例如，假设我们需要将所有客户的姓名更改为大写。

无需查询数据并让我们的应用程序完成工作，我们只需实现Function即可：

```java
public class UpperCaseNames implements Function<Boolean> {
    @Override
    public void execute(FunctionContext<Boolean> context) {
        RegionFunctionContext regionContext = (RegionFunctionContext) context;
        Region<CustomerKey, Customer> region = regionContext.getDataSet();

        for ( Map.Entry<CustomerKey, Customer> entry : region.entrySet() ) {
            Customer customer = entry.getValue();
            customer.setFirstName(customer.getFirstName().toUpperCase());
        }
        context.getResultSender().lastResult(true);   
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
```

请注意， getId 必须返回一个唯一值，因此类名通常是一个不错的选择。

FunctionContext包含我们所有的区域数据，因此我们可以从中进行更复杂的查询，或者像我们在这里所做的那样，改变它。

而Function的功能远不止于此，因此请查看[官方手册](https://gemfire.docs.pivotal.io/95/geode/developing/function_exec/function_execution.html)，尤其[是getResultSender方法](https://geode.apache.org/releases/latest/javadoc/org/apache/geode/cache/execute/FunctionContext.html#getResultSender--)。

### 7.2. 部署函数

我们需要让 Geode 知道我们的功能才能运行它。就像我们对自定义数据类型所做的那样，我们将打包 jar。

但是这一次，我们可以只使用 deploy命令：

```plaintext
gfsh> deploy --jar=./lib/apache-geode-1.0-SNAPSHOT.jar
```

### 7.3. 执行函数

现在，我们可以使用FunctionService 从应用程序执行函数：

```java
@Test
public void whenExecuteUppercaseNames_thenCustomerNamesAreUppercased() {
    Execution execution = FunctionService.onRegion(this.customerRegion);
    execution.execute(UpperCaseNames.class.getName());
    Customer customer = this.customerRegion.get(new CustomerKey(1));
    assertEquals("GHEORGE", customer.getFirstName());
}
```

## 八. 总结

在本文中，我们了解了Apache Geode生态系统的基本概念。我们研究了使用标准和自定义类型、和分区区域以及 oql 和函数支持的简单获取和放置。