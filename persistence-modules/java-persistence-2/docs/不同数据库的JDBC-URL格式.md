## 1. 概述

当我们在Java中使用数据库时，通常我们使用[JDBC](https://www.baeldung.com/java-jdbc)连接到数据库。

JDBC URL 是在我们的Java应用程序和数据库之间建立连接的重要参数。但是，不同的数据库系统的 JDBC URL 格式可能不同。

在本教程中，我们将仔细研究几种广泛使用的数据库的 JDBC URL 格式：[Oracle](https://www.oracle.com/database/technologies/)、[MySQL](https://www.mysql.com/)、[Microsoft SQL Server](https://www.microsoft.com/en-us/sql-server/sql-server-2019)和[PostgreSQL](https://www.postgresql.org/)。

## 2. Oracle 的 JDBC URL 格式

Oracle 数据库系统广泛用于企业Java应用程序。在我们查看连接 Oracle 数据库的 JDBC URL 格式之前，我们应该首先确保 Oracle Thin 数据库驱动程序在我们的类路径中。

例如，如果我们的项目由 Maven 管理，我们需要在我们的pom.xml中添加[ojdbc8依赖](https://search.maven.org/search?q=g:com.oracle.database.jdbc AND a:ojdbc8)：

```xml
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc8</artifactId>
    <version>21.1.0.0</version>
</dependency>

```

瘦驱动程序提供多种 JDBC URL 格式：

-   连接到[SID](https://docs.oracle.com/cd/E11882_01/network.112/e41945/glossary.htm#BGBFBBAI)
-   连接到 Oracle[服务名称](https://docs.oracle.com/cd/E11882_01/network.112/e41945/glossary.htm#BGBGIHFG)
-   [带有tnsnames.ora](https://docs.oracle.com/database/121/NETRF/tnsnames.htm#NETRF007)条目的 URL

接下来，我们将逐一介绍这些格式。

### 2.1. 连接到 Oracle 数据库 SID

在一些旧版本的 Oracle 数据库中，数据库被定义为一个 SID。让我们看看用于连接到 SID 的 JDBC URL 格式：

```bash
jdbc:oracle:thin:[<user>/<password>]@<host>[:<port>]:<SID>

```

例如，假设我们有一个 Oracle 数据库服务器主机“ myoracle.db.server:1521 ”，SID 的名称是“ my_sid ”，我们可以按照上面的格式构建连接 URL 并连接到数据库：

```java
@Test
public void givenOracleSID_thenCreateConnectionObject() {
    String oracleJdbcUrl = "jdbc:oracle:thin:@myoracle.db.server:1521:my_sid";
    String username = "dbUser";
    String password = "1234567";
    try (Connection conn = DriverManager.getConnection(oracleJdbcUrl, username, password)) {
        assertNotNull(conn);
    } catch (SQLException e) {
        System.err.format("SQL State: %sn%s", e.getSQLState(), e.getMessage());
    }
}
```

### 2.2. 连接到 Oracle 数据库服务名称

通过服务名称连接 Oracle 数据库的 JDBC URL 的格式与我们通过 SID 连接的格式非常相似：

```bash
jdbc:oracle:thin:[<user>/<password>]@//<host>[:<port>]/<service>
```

我们可以连接到Oracle 数据库服务器“ myoracle.db.server:1521 ”上的服务“ my_servicename ”：

```java
@Test
public void givenOracleServiceName_thenCreateConnectionObject() {
    String oracleJdbcUrl = "jdbc:oracle:thin:@//myoracle.db.server:1521/my_servicename";
    ...
    try (Connection conn = DriverManager.getConnection(oracleJdbcUrl, username, password)) {
        assertNotNull(conn);
        ...
    }
    ...
}

```

### 2.3. 使用tnsnames.ora条目连接到 Oracle 数据库

我们还可以在 JDBC URL 中包含tnsnames.ora条目以连接到 Oracle 数据库：

```bash
jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=<host>)(PORT=<port>))(CONNECT_DATA=(SERVICE_NAME=<service>)))
```

让我们看看如何使用tnsnames.ora文件中的条目连接到我们的“ my_servicename ”服务：

```java
@Test
public void givenOracleTnsnames_thenCreateConnectionObject() {
    String oracleJdbcUrl = "jdbc:oracle:thin:@" +
      "(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)" +
      "(HOST=myoracle.db.server)(PORT=1521))" +
      "(CONNECT_DATA=(SERVICE_NAME=my_servicename)))";
    ...
    try (Connection conn = DriverManager.getConnection(oracleJdbcUrl, username, password)) {
        assertNotNull(conn);
        ...
    }
    ...
}
```

## 3. MySQL 的 JDBC URL 格式

在本节中，让我们讨论如何编写连接 MySQL 数据库的 JDBC URL。

要从我们的Java应用程序连接到 MySQL 数据库，让我们首先在我们的pom.xml中添加 JDBC 驱动程序[mysql-connector-java依赖](https://search.maven.org/search?q=a:mysql-connector-java g:mysql)项：

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.22</version>
</dependency>

```

接下来我们看一下MySQL JDBC驱动支持的连接URL的通用格式：

```bash
protocol//[hosts][/database][?properties]
```

让我们看一个连接到主机“ mysql.db.server ”上的 MySQL 数据库“ my_database ”的示例：

```java
@Test
public void givenMysqlDb_thenCreateConnectionObject() {
    String jdbcUrl = "jdbc:mysql://mysql.db.server:3306/my_database?useSSL=false&serverTimezone=UTC";    
    String username = "dbUser";
    String password = "1234567";
    try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
        assertNotNull(conn);
    } catch (SQLException e) {
        System.err.format("SQL State: %sn%s", e.getSQLState(), e.getMessage());
    }
}

```

上面示例中的 JDBC URL 看起来很简单。它有四个构建块：

-   协议——jdbc :mysql:
-   主机– mysql.db.server:3306
-   数据库——我的数据库
-   属性– useSSL=false&serverTimezone=UTC

然而，有时，我们可能会面临更复杂的情况，例如不同类型的连接或多个 MySQL 主机等。

接下来，我们将仔细研究每个构建块。

### 3.1. 协议

除了普通的“ jdbc:mysql: ”协议外，connector-java JDBC驱动还支持一些特殊连接的协议：

-   [负载平衡 JDBC 连接](https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-usagenotes-j2ee-concepts-managing-load-balanced-connections.html)– jdbc:mysql:loadbalance:
-   [JDBC 连接](https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-source-replica-replication-connection.html)– jdbc:mysql:replication: 

当我们谈论负载平衡和JDBC 时，我们可能会意识到应该有多个MySQL 主机。

接下来，让我们检查连接 URL 的另一部分的详细信息 — hosts。

### 3.2. 主持

我们已经在上一节中看到了定义单个主机的 JDBC URL 示例——例如，mysql.db.server:3306。

但是，如果我们需要处理多个主机，我们可以在逗号分隔列表中列出主机：host1, host2,…,hostN。

我们还可以用方括号将逗号分隔的主机列表括起来：[host1, host2,…,hostN]。

让我们看几个连接到多个 MySQL 服务器的 JDBC URL 示例：

-   jdbc:mysql://myhost1:3306,myhost2:3307/db_name
-   jdbc:mysql://[myhost1:3306,myhost2:3307]/db_name
-   jdbc:mysql:loadbalance://myhost1:3306,myhost2:3307/db_name?user=dbUser&password=1234567&loadBalanceConnectionGroup=group_name&ha.enableJMX=true

如果我们仔细查看上面的最后一个示例，我们会看到在数据库名称之后，有一些属性和用户凭据的定义。接下来我们将看看这些。

### 3.3. 属性和用户凭据

有效的全局属性将应用于所有主机。属性前面有一个问号“ ？”并写成由“ & ”符号分隔的键=值对：

```bash
jdbc:mysql://myhost1:3306/db_name?prop1=value1&prop2=value2
```

我们也可以将用户凭据放在属性列表中：

```bash
jdbc:mysql://myhost1:3306/db_name?user=root&password=mypass
```

此外，我们可以在每个主机前加上用户凭据，格式为“ user: password@host ”：

```bash
jdbc:mysql://root:mypass@myhost1:3306/db_name
```

此外，如果我们的 JDBC URL 包含一个主机列表并且所有主机都使用相同的用户凭据，我们可以为主机列表添加前缀：

```bash
jdbc:mysql://root:mypass[myhost1:3306,myhost2:3307]/db_name
```

毕竟，也可以在 JDBC URL 之外提供用户凭据。

[我们可以在调用DriverManager.getConnection(String url, String user, String password)](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/DriverManager.html#getConnection(java.lang.String,java.lang.String,java.lang.String))方法获取连接时将用户名和密码传递给该方法。

## 4. Microsoft SQL Server 的 JDBC URL 格式

Microsoft SQL Server 是另一种流行的数据库系统。要从Java应用程序连接 MS SQL Server 数据库，我们需要将[mssql-jdbc依赖](https://search.maven.org/search?q=g:com.microsoft.sqlserver a:mssql-jdbc)项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>8.4.1.jre11</version>
</dependency>
```

接下来，让我们看看如何构建 JDBC URL 来获取到 MS SQL Server 的连接。

连接 MS SQL Server 数据库的 JDBC URL 的一般格式为：

```bash
jdbc:sqlserver://[serverName[instanceName][:portNumber]][;property=value[;property=value]]
```

让我们仔细看看格式的每个部分。

-   serverName——我们要连接的服务器的地址；这可能是指向服务器的域名或 IP 地址
-   instanceName –要连接到serverName的实例；它是一个可选字段，如果未指定该字段，将选择默认实例
-   portNumber – 这是连接到serverName的端口(默认端口是1433)
-   properties – 可以包含一个或多个可选的连接属性，必须用分号分隔，并且不允许重复的属性名称

现在，假设我们有一个在主机“ mssql.db.server ”上运行的 MS SQL Server 数据库，服务器上的实例名称是“ mssql_instance ”，我们要连接的数据库的名称是“ my_database ”。

让我们尝试获取与该数据库的连接：

```java
@Test
public void givenMssqlDb_thenCreateConnectionObject() {
    String jdbcUrl = "jdbc:sqlserver://mssql.db.servermssql_instance;databaseName=my_database";
    String username = "dbUser";
    String password = "1234567";
    try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
        assertNotNull(conn);
    } catch (SQLException e) {
        System.err.format("SQL State: %sn%s", e.getSQLState(), e.getMessage());
    }
}

```

## 5. PostgreSQL 的 JDBC URL 格式

PostgreSQL 是一种流行的开源数据库系统。要使用 PostgreSQL，JDBC 驱动程序[postgresql](https://search.maven.org/search?q=g:org.postgresql AND a:postgresql)应该作为依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.2.18</version>
</dependency>
```

连接到 PostgreSQL 的 JDBC URL 的一般形式是：

```bash
jdbc:postgresql://host:port/database?properties
```

现在，让我们看看上述 JDBC URL 格式中的每个部分。

host参数为数据库服务器的域名或IP地址。

如果我们要指定一个IPv6地址，host参数必须用方括号括起来，例如jdbc:postgresql://[::1]:5740/ my_database.mysql

port参数指定 PostgreSQL 正在侦听的 端口号。端口参数是可选的，默认端口号是5432。

顾名思义，数据库参数定义了我们要连接的数据库的名称。

properties参数可以包含一组由“ & ”符号分隔的 键=值对。

了解了 JDBC URL 格式的参数后，我们来看一个如何获取 PostgreSQL 数据库连接的例子：

```java
@Test
public void givenPostgreSqlDb_thenCreateConnectionObject() {
    String jdbcUrl = "jdbc:postgresql://postgresql.db.server:5430/my_database?ssl=true&loglevel=2";
    String username = "dbUser";
    String password = "1234567";
    try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
        assertNotNull(conn);
    } catch (SQLException e) {
        System.err.format("SQL State: %sn%s", e.getSQLState(), e.getMessage());
    }
}

```

在上面的示例中，我们连接到 PostgreSQL 数据库：

-   主机：端口 – postgresql.db.server:5430
-   数据库——我的数据库
-   属性 – ssl=true&loglevel=2

## 六. 总结

本文讨论了四种广泛使用的数据库系统的 JDBC URL 格式：Oracle、MySQL、Microsoft SQL Server 和 PostgreSQL。

我们还看到了构建 JDBC URL 字符串以获取与这些数据库的连接的不同示例。