## 1. 概述

在本文中，我们将研究[HBase](https://hbase.apache.org/)数据库Java客户端库。HBase 是一个分布式数据库，它使用 Hadoop 文件系统来存储数据。

我们将创建一个Java示例客户端和一个表，我们将向其中添加一些简单的记录。

## 2. HBase数据结构

在 HBase 中，数据被分组到列族中。列族的所有列成员都具有相同的前缀。

例如，列family1:qualifier1和family1 :qualifier2都是family1列族的成员。所有列族成员都存储在文件系统中。

在列族内部，我们可以放置一个具有指定限定符的行。我们可以将限定符视为一种列名。

让我们看一个来自 Hbase 的示例记录：

```java
Family1:{  
   'Qualifier1':'row1:cell_data',
   'Qualifier2':'row2:cell_data',
   'Qualifier3':'row3:cell_data'
}
Family2:{  
   'Qualifier1':'row1:cell_data',
   'Qualifier2':'row2:cell_data',
   'Qualifier3':'row3:cell_data'
}
```

我们有两个列族，每个列族都有三个限定符，其中包含一些单元格数据。每行都有一个行键——它是一个唯一的行标识符。我们将使用行键来插入、检索和删除数据。

## 3. HBase客户端Maven依赖

在连接到 HBase 之前，我们需要添加[hbase-client ](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.hbase" AND a%3A"hbase-client")和[hbase](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.hbase" AND a%3A"hbase")依赖项：

```xml
<dependency>
    <groupId>org.apache.hbase</groupId>
    <artifactId>hbase-client</artifactId>
    <version>${hbase.version}</version>
</dependency>
<dependency>
     <groupId>org.apache.hbase</groupId>
     <artifactId>hbase</artifactId>
     <version>${hbase.version}</version>
</dependency>
```

## 4. HBase 设置

我们需要设置 HBase 以便能够从Java客户端库连接到它。安装超出了本文的范围，但你可以在线查看一些 HBase 安装[指南](https://www.tutorialspoint.com/hbase/hbase_installation.htm)。

接下来，我们需要通过执行以下命令在本地启动 HBase master：

```shell
hbase master start
```

## 5. 从Java连接到 HBase

要以编程方式从Java连接到 HBase，我们需要定义一个 XML 配置文件。我们在本地主机上启动了 HBase 实例，因此我们需要将其输入到配置文件中：

```xml
<configuration>
    <property>
        <name>hbase.zookeeper.quorum</name>
        <value>localhost</value>
    </property>
    <property>
        <name>hbase.zookeeper.property.clientPort</name>
        <value>2181</value>
    </property>
</configuration>

```

现在我们需要将 HBase 客户端指向该配置文件：

```java
Configuration config = HBaseConfiguration.create();

String path = this.getClass()
  .getClassLoader()
  .getResource("hbase-site.xml")
  .getPath();
config.addResource(new Path(path));

```

接下来，我们检查与 HBase 的连接是否成功——如果失败，将抛出MasterNotRunningException ：

```java
HBaseAdmin.checkHBaseAvailable(config);
```

## 6. 创建数据库结构

在我们开始向 HBase 添加数据之前，我们需要创建用于插入行的数据结构。我们将创建一个包含两个列族的表：

```java
private TableName table1 = TableName.valueOf("Table1");
private String family1 = "Family1";
private String family2 = "Family2";
```

首先，我们需要创建一个到数据库的连接并获取管理对象，我们将使用它来操作数据库结构：

```java
Connection connection = ConnectionFactory.createConnection(config)
Admin admin = connection.getAdmin();
```

然后，我们可以通过将HTableDescriptor类的实例传递给管理对象上的createTable()方法来创建表：

```java
HTableDescriptor desc = new HTableDescriptor(table1);
desc.addFamily(new HColumnDescriptor(family1));
desc.addFamily(new HColumnDescriptor(family2));
admin.createTable(desc);
```

## 7. 添加和检索元素

创建表后，我们可以通过创建Put对象并在Table对象上调用put()方法来向其中添加新数据：

```java
byte[] row1 = Bytes.toBytes("row1")
Put p = new Put(row1);
p.addImmutable(family1.getBytes(), qualifier1, Bytes.toBytes("cell_data"));
table1.put(p);
```

检索以前创建的行可以通过使用Get类来实现：

```java
Get g = new Get(row1);
Result r = table1.get(g);
byte[] value = r.getValue(family1.getBytes(), qualifier1);
```

row1是一个行标识符——我们可以用它来从数据库中检索特定的行。调用时：

```java
Bytes.bytesToString(value)
```

返回的结果将是之前插入的cell_data。

## 8. 扫描过滤

我们可以扫描表格，通过使用Scan对象检索给定限定符内的所有元素(注意ResultScanner扩展了Closable，所以一定要在完成后调用close())：

```java
Scan scan = new Scan();
scan.addColumn(family1.getBytes(), qualifier1);

ResultScanner scanner = table.getScanner(scan);
for (Result result : scanner) {
    System.out.println("Found row: " + result);
}

```

该操作将打印qualifier1内的所有行以及一些附加信息，例如时间戳：

```java
Found row: keyvalues={Row1/Family1:Qualifier1/1488202127489/Put/vlen=9/seqid=0}
```

我们可以使用过滤器检索特定记录。

首先，我们正在创建两个过滤器。filter1指定扫描查询将检索大于row1的元素，filter2指定我们只对限定符等于qualifier1的行感兴趣：

```java
Filter filter1 = new PrefixFilter(row1);
Filter filter2 = new QualifierFilter(
  CompareOp.GREATER_OR_EQUAL, 
  new BinaryComparator(qualifier1));
List<Filter> filters = Arrays.asList(filter1, filter2);
```

然后我们可以从Scan查询中获取结果集：

```java
Scan scan = new Scan();
scan.setFilter(new FilterList(Operator.MUST_PASS_ALL, filters));

try (ResultScanner scanner = table.getScanner(scan)) {
    for (Result result : scanner) {
        System.out.println("Found row: " + result);
    }
}
```

在创建FilterList时，我们传递了一个Operator.MUST_PASS_ALL——这意味着必须满足所有过滤器。如果只需要满足一个过滤器，我们可以选择Operation.MUST_PASS_ONE 。在结果集中，我们将只有与指定过滤器匹配的行。

## 9.删除行

最后，要删除一行，我们可以使用Delete类：

```java
Delete delete = new Delete(row1);
delete.addColumn(family1.getBytes(), qualifier1);
table.delete(delete);
```

我们正在删除位于family1内部的row1 。

## 10.总结

在本快速教程中，我们专注于与 HBase 数据库进行通信。我们看到了如何从Java客户端库连接到 HBase 以及如何运行各种基本操作。