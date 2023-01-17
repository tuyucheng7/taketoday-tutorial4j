## 1. 简介

 Amazon Aurora 是为云构建的 MySQL 和 PostgreSQL 兼容 [关系数据库](https://aws.amazon.com/relational-database/)，它结合了高端商业数据库的性能和可用性与开源数据库的简单性和成本效益。

在本教程中，我们将介绍如何使用Java创建 Amazon RDS 实例并与之交互，我们还将在 Amazon RDS 上连接和执行 SQL 测试。

让我们从设置项目开始。

## 2.Maven依赖

让我们创建一个JavaMaven 项目并将 AWS SDK 添加到我们的项目中：

```xml
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk</artifactId>
    <version>1.11.377</version>
</dependency>
```

要查看最新版本，请检查 [Maven Central](https://search.maven.org/search?q=g:com.amazonaws AND a:aws-java-sdk&core=gav)。

## 3.先决条件

要使用 AWS SDK，我们需要进行一些设置：

-   AWS 账户
-   AWS 安全凭证
-   选择 AWS 区域

我们需要一个 Amazon Web Services 帐户。如果你还没有，请继续 [创建一个帐户](https://portal.aws.amazon.com/gp/aws/developer/registration/index.html)

AWS 安全凭证是允许我们以编程方式调用 AWS API 操作的访问密钥。我们可以通过两种方式获取这些凭证，一种是使用来自 [安全凭证页面的访问密钥部分的 AWS 根帐户凭证，另一种是使用来自](https://console.aws.amazon.com/iam/home?#security_credential)[IAM 控制台](https://console.aws.amazon.com/iam/home?#) 的 IAM 用户凭证 

我们必须选择要存储 Amazon RDS 的 AWS 区域。请记住，RDS 价格因地区而异。有关更多详细信息，请访问 [官方文档](https://aws.amazon.com/rds/aurora/pricing/)。

对于本教程，我们将使用亚太地区(悉尼)(区域ap-southeast-2)。

## 4. 连接到 AWS RDS Web 服务

首先，我们需要创建一个客户端连接来访问 Amazon RDS Web 服务。

为此，我们将使用 AmazonRDS 接口：

```java
AWSCredentials credentials = new BasicAWSCredentials(
  "<AWS accesskey>", 
  "<AWS secretkey>"
);

```

然后使用适当的区域和凭据配置RDS Builder ：

```java
AmazonRDSClientBuilder.standard().withCredentials(credentials)
  .withRegion(Regions.AP_SOUTHEAST_2)
  .build();

```

## 5. 亚马逊极光实例

现在让我们创建 Amazon Aurora RDS 实例。

### 5.1. 创建RDS实例

要创建 RDS 实例，我们需要实例化一个具有以下属性的CreateDBInstanceRequest ：

-   在 Amazon RDS 中的所有现有实例名称中唯一的数据库实例标识符
-   [DB 实例类从实例类表](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Concepts.DBInstanceClass.html)中指定 CPU、ECU、内存等的配置
-   数据库引擎。PostgreSQL 或 MySQL，我们将使用 PostgreSQL
-   数据库主/超级用户名
-   数据库主用户密码
-   DB name 用于创建具有指定名称的初始数据库
-   对于存储类型，指定Amazon EBS 卷类型。该列表可[在此处获得](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/EBSVolumeTypes.html)
-   以 GiB 为单位的存储分配

```java
CreateDBInstanceRequest request = new CreateDBInstanceRequest();
request.setDBInstanceIdentifier("baeldung");   
request.setDBInstanceClass("db.t2.micro");
request.setEngine("postgres");
request.setMultiAZ(false);
request.setMasterUsername("username");
request.setMasterUserPassword("password");
request.setDBName("mydb");       
request.setStorageType("gp2");   
request.setAllocatedStorage(10);

```

现在让我们通过调用createDBInstance()创建我们的第一个实例 ： 

```java
amazonRDS.createDBInstance(request);

```

RDS 实例将在几分钟内创建。

我们不会在响应中获得端点 URL，因为此调用是异步的。

### 5.2. 列出数据库实例

在本节中，我们将了解如何列出创建的数据库实例。

要列出 RDS 实例，我们需要使用 AmazonRDS接口 的 describeDBInstances ：

```java
DescribeDBInstancesResult result = amazonRDS.describeDBInstances();
List<DBInstance> instances = result.getDBInstances();
for (DBInstance instance : instances) {
    // Information about each RDS instance
    String identifier = instance.getDBInstanceIdentifier();
    String engine = instance.getEngine();
    String status = instance.getDBInstanceStatus();
    Endpoint endpoint = instance.getEndpoint();
}
```

端点 URL 是我们新数据库实例的连接 URL。在连接到数据库时，此 URL 将作为主机提供。

### 5.3. 运行 JDBC 测试

现在让我们连接我们的 RDS 实例并创建我们的第一个表。

让我们创建一个 db.properties 文件并添加数据库信息：

```plaintext
db_hostname=<Endpoint URL>
db_username=username
db_password=password
db_database=mydb

```

创建文件后，让我们连接到 RDS 实例并创建名为jdbc_test的表：

```java
Properties prop = new Properties();
InputStream input = AwsRdsDemo.class.getClassLoader().getResourceAsStream("db.properties");
prop.load(input);
String db_hostname = prop.getProperty("db_hostname");
String db_username = prop.getProperty("db_username");
String db_password = prop.getProperty("db_password");
String db_database = prop.getProperty("db_database");

Connection conn = DriverManager.getConnection(jdbc_url, db_username, db_password);
Statement statement = conn.createStatement();
String sql = "CREATE TABLE IF NOT EXISTS jdbc_test (id SERIAL PRIMARY KEY, content VARCHAR(80))";
statement.executeUpdate(sql);

```

之后，我们将从表中插入和检索数据：

```java
PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO jdbc_test (content) VALUES (?)");
String content = "" + UUID.randomUUID();
preparedStatement.setString(1, content);
preparedStatement.executeUpdate();

String sql = "SELECT  count() as count FROM jdbc_test";
ResultSet resultSet = statement.executeQuery(sql);
while (resultSet.next()) {
    String count = resultSet.getString("count");
    Logger.log("Total Records: " + count);
}

```

## 5.4. 删除实例

要删除数据库实例，我们需要生成 DeleteDBInstanceRequest。它需要数据库实例标识符和skipFinalSnapshot 参数。

skipFinalSanpshot是指定我们是否要在删除实例之前拍摄快照： 

```java
DeleteDBInstanceRequest request = new DeleteDBInstanceRequest();
request.setDBInstanceIdentifier(identifier);
request.setSkipFinalSnapshot(true);
DBInstance instance = amazonRDS.deleteDBInstance(request);
```

## 六. 总结

在本文中，我们重点介绍了通过 Amazon SDK 与 Amazon Aurora (PostgreSQL) RDS 交互的基础知识。本教程重点介绍 PostgreSQL，还有其他选项，包括 MySQL。

尽管 RDS 中的交互方式将保持不变。Aurora 是许多客户的首选，因为它比标准 MySQL 数据库快五倍，比标准 PostgreSQL 数据库快三倍。

有关更多信息，请访问 [亚马逊极光](https://aws.amazon.com/rds/aurora/)。