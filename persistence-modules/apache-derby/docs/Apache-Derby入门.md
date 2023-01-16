## 1. 概述

在本教程中，我们将了解 Apache Derby 数据库引擎，这是一个基于Java的关系数据库引擎，由 Apache Software Foundation 作为开源项目开发。

我们将从安装和配置它开始，然后查看它提供的用于与之交互的工具。创建示例数据库后，我们将学习如何使用 Derby 的命令行工具执行 SQL 命令。最后，我们将了解如何使用普通 JDBC 以及通过Spring Boot应用程序以编程方式连接到数据库。

## 2.部署方式

Apache Derby 有两个基本的部署选项：一个简单的嵌入式选项和一个客户端/服务器选项。

在嵌入式模式下，Derby 作为一个简单的单用户Java应用程序在同一个Java虚拟机 (JVM) 中运行。由于其自动启动和关闭，它通常对最终用户不可见，不需要管理员干预。在这种模式下，我们可以建立一个临时数据库，而无需对其进行管理。

在客户端/服务器模式下，Derby 在托管服务器的Java虚拟机 (JVM) 中运行，当应用程序启动时，该应用程序提供跨网络的多用户连接。

## 三、安装配置

让我们看看如何安装 Apache Derby。

### 3.1. 安装

[首先，我们可以从这里](https://db.apache.org/derby/derby_downloads.html)下载最新版本的 Apache Derby 。之后，我们提取下载的文件。提取的安装包含几个子目录：

```bash
$ ls 
bin  demo  docs  index.html  javadoc  KEYS  lib  LICENSE  NOTICE  RELEASE-NOTES.html  test

```

-   bin包含用于执行实用程序和设置环境的脚本
-   demo包含示例程序
-   javadoc包含 API 文档
-   docs包含 Apache Derby 文档
-   lib包含 Apache Derby .jar 文件
-   测试包含 Apache Derby 的回归测试

### 3.2. 配置

在我们启动数据库引擎之前，我们需要配置一些东西。

首先，我们将DERBY_HOME环境变量设置为我们提取 Apache Derby bin 的位置。例如，如果 Apache Derby 安装在/opt/derby-db目录中，我们可以使用以下命令：

```bash
export DERBY_HOME=/opt/derby-db

```

然后，我们将DERBY_HOME/bin目录添加到 PATH 环境变量，以便我们可以从任何目录运行 Derby 脚本：

```bash
export PATH="$DERBY_HOME/bin:$PATH"
```

### 3.3. 德比图书馆

lib目录中有 Apache Derby 提供的各种库：

```bash
$ ls
derbyclient.jar     derbynet.jar            derbyshared.jar
derby.jar           derbyoptionaltools.jar  derbytools.jar
derbyrun.jar        derby.war               derbyLocale_XX.jar ...

```

每个库如下所述：

-   derby.jar：嵌入式环境需要。对于客户端/服务器环境，我们只需要服务器上的这个库。
-   derbyshared.jar：所有配置都需要，无论我们运行的是嵌入式引擎、网络服务器、远程客户端还是数据库工具。
-   derbytools.jar： 运行所有 Apache Derby 工具(IJ、DBLook 和导入/导出)所必需的。如果我们正在运行网络服务器或者如果我们的应用程序直接引用 JDBC 驱动程序，则也需要。
-   derbyrun.jar：用于启动 Apache Derby 工具
-   derbynet.jar： 需要启动 Apache Derby 网络服务器
-   derbyclient.jar：需要使用 Apache Derby 网络客户端驱动程序
-   derbyLocale_XX.jar： 需要本地化 Apache Derby 的消息

## 4.工具

Apache Derby 为不同的应用程序提供了许多工具。我们可以使用缩短的名称通过derbyrun.jar运行 Apache Derby 工具和实用程序，并且不需要设置 java CLASSPATH 环境变量。derbyrun.jar文件必须与其他 Derby JAR 文件位于同一文件夹中。

### 4.1. IJ

IJ 是一个基于 JDBC 的Java命令行应用程序。它的主要目的是允许以交互方式或通过脚本执行 Derby SQL 语句。

首先，让我们运行 IJ 工具：

```bash
$ $DERBY_HOME/bin/ij
ij version 10.13
ij> 

```

我们也可以使用下面的命令来执行它：

```bash
$ java -jar $DERBY_HOME/lib/derbyrun.jar ij
ij version 10.13
ij> 
```

请注意，所有命令都以分号结尾。如果我们执行一个没有分号的命令，命令行将移动到下一行。

在使用 SQL 语句的部分中，我们将看到如何使用 IJ 执行多个命令。

此外，IJ 可以使用属性来执行命令。这可以帮助我们 通过 使用 IJ工具支持的 属性来节省一些重复性工作。    

我们可以通过以下方式设置 IJ 属性：

-   通过在命令行上使用-p property-file选项指定属性文件
-   通过在命令行上使用-D选项

我们可以创建一个属性文件，添加所有需要的属性，然后运行以下命令：

```bash
$ java -jar derbyrun.jar ij -p file-name.properties
```

如果当前目录中不存在file-name.properties文件，我们将得到[java.io.FileNotFoundException。](https://www.baeldung.com/java-filenotfound-exception)

例如，假设我们要创建到具有特定名称的特定数据库的连接。我们可以使用ij.database属性来做到这一点：

```bash
$ java -jar -Dij.protocol=jdbc:derby: -Dij.database=baeldung derbyrun.jar ij
ij version 10.13
CONNECTION0 - 	jdbc:derby:baeldung
 = current connection

```

### 4.2. DBLook

dblook工具提供了数据库的DDL(数据定义语言)。比如我们可以将baeldung数据库的DDL写入控制台：

```bash
$ $DERBY_HOME/bin/dblook -d jdbc:derby:baeldung
-- Timestamp: 2021-08-23 01:29:48.529
-- Source database is: baeldung
-- Connection URL is: jdbc:derby:baeldung
-- appendLogs: false

-- ----------------------------------------------
-- DDL Statements for schemas
-- ----------------------------------------------

CREATE SCHEMA "basic_users";

-- ----------------------------------------------
-- DDL Statements for tables
-- ----------------------------------------------

CREATE TABLE "APP"."authors" ("id" INTEGER NOT NULL, "first_name" VARCHAR(255) , "last_name" VARCHAR(255));

-- ----------------------------------------------
-- DDL Statements for keys
-- ----------------------------------------------

-- PRIMARY/UNIQUE
ALTER TABLE "APP"."authors" ADD CONSTRAINT "SQL0000000000-582f8014-017b-6e26-ada1-00000644e000" PRIMARY KEY ("id");

```

dblook具有下面描述的其他选项。

我们可以使用-o将 DDL 写入类似baeldung.sql的文件：

```bash
$ sudo $DERBY_HOME/bin/dblook -d jdbc:derby:baeldung -o baeldung.sql

```

我们还可以使用-z指定模式，使用-t指定表：

```bash
$ sudo $DERBY_HOME/bin/dblook -d jdbc:derby:baeldung -o baeldung.sql -z SCHEMA_NAME -t "TABLE_NAME"
```

### 4.3. 系统信息

Apache Derby sysinfo工具显示有关我们的Java环境和 Derby 版本的信息。此外，sysinfo工具在控制台上显示系统信息：

```bash
$ java -jar $DERBY_HOME/lib/derbyrun.jar sysinfo

------------------JavaInformation ------------------
Java Version:    11.0.11
Java Vendor:     Ubuntu
Java home:       /usr/lib/jvm/java-11-openjdk-amd64
Java classpath:  /opt/derby-db/lib/derbyrun.jar
OS name:         Linux
OS architecture: amd64
OS version:      5.11.0-27-generic
Java user name:  arash
Java user home:  /home/arash
Java user dir:   /opt/derby-db
java.specification.name:JavaPlatform API Specification
java.specification.version: 11
java.runtime.version: 11.0.11+9-Ubuntu-0ubuntu2.20.04
--------- Derby Information --------
[/opt/derby-db/lib/derby.jar] 10.13.1.1 - (1873585)
[/opt/derby-db/lib/derbytools.jar] 10.13.1.1 - (1873585)
[/opt/derby-db/lib/derbynet.jar] 10.13.1.1 - (1873585)
[/opt/derby-db/lib/derbyclient.jar] 10.13.1.1 - (1873585)
[/opt/derby-db/lib/derbyshared.jar] 10.13.1.1 - (1873585)
[/opt/derby-db/lib/derbyoptionaltools.jar] 10.13.1.1 - (1873585)
```

## 5. Apache Derby 中的 SQL 语句

在这里，我们将 检查Apache Derby 提供的一些基本 SQL 语句。我们将通过示例查看每个语句的语法。 

### 5.1. 创建数据库

我们可以使用连接命令和连接字符串中的create=true属性创建一个新数据库：

```rust
ij> connect 'jdbc:derby:databaseName;create=true';
```

### 5.2. 连接到数据库

当我们与 Derby 数据库交互时，IJ 会根据 URL 语法自动加载适当的驱动程序：

```bash
ij> connect 'jdbc:derby:baeldung' user 'user1' password 'pass123';
```

### 5.3. 创建模式

CREATE SCHEMA语句定义一个模式，这是一种为一组对象标识特定命名空间的方法：

```sql
CREATE SCHEMA schema_name AUTHORIZATION userName;
```

模式名称不能超过 128 个字符，并且在数据库中也必须是唯一的。此外，模式名称不能以前缀 SYS 开头。

以下是名为baeldung_authors的架构示例：

```sql
ij> CREATE SCHEMA baeldung_authors;
```

此外，我们可以为baeldung_authors创建一个模式，只有ID 为arash的特定用户才能访问：

```sql
ij> CREATE SCHEMA baeldung_authors AUTHORIZATION arash;
```

### 5.4. 删除模式

我们可以 使用 DROP SCHEMA语句删除 模式 ，而且目标模式必须为空才能使DROP SCHEMA成功。我们不能删除APP模式(默认用户模式)或 SYS 模式： 

```sql
DROP SCHEMA schema_name RESTRICT;
```

通过使用 RESTRICT关键字，我们可以强制执行 规则，即 指定 模式中不能定义任何对象，以便从数据库中删除该 模式 。   

让我们看一个删除架构的示例：

```sql
ij> DROP SCHEMA baeldung_authors RESTRICT;
```

### 5.5. 创建表

我们可以使用CREATE TABLE语句创建一个包含列和约束的表： 

```sql
CREATE TABLE table_name (
   column_name1 column_data_type1 constraint (optional),
   column_name2 column_data_type2 constraint (optional),
);
```

让我们看一个例子：

```sql
ij> CREATE TABLE posts(post_id INT NOT NULL, publish_date DATE NOT NULL,
    view_number INT DEFAULT 0, PRIMARY KEY (post_id));
```

如果我们不指定默认 值，NULL将 作为 默认值 插入 列中。    

用于 CRUD 操作的其他 SQL 语句(如 INSERT、UPDATE、DELETE 和 SELECT)与标准 SQL 类似。

## 6. 使用 Apache Derby 进行 JDBC 编程

在这里，我们将学习如何创建使用 Apache Derby 作为数据库引擎的Java应用程序。

### 6.1. Maven 依赖项

[Maven](https://www.baeldung.com/maven-guide)中有两个 Derby驱动程序：derby和derbynet。前者用于嵌入式应用，后者用于 客户端/服务器应用。  

[让我们为derby](https://mvnrepository.com/artifact/org.apache.derby/derby)添加 Maven 依赖项：

```xml
<dependency>
    <groupId>org.apache.derby</groupId>
    <artifactId>derby</artifactId>
    <version>10.13.1.1</version>
</dependency>

```

[此外，我们为derbyclient](https://mvnrepository.com/artifact/org.apache.derby/derbyclient)添加 Maven 依赖项：

```xml
<dependency>
    <groupId>org.apache.derby</groupId>
    <artifactId>derbyclient</artifactId>
    <version>10.13.1.1</version>
</dependency>

```

### 6.2. JDBC URL 连接

我们可以为 客户端/服务器和嵌入式应用程序使用 不同 的连接字符串参数 连接到 数据库。

在下面的语法中，我们可以将它用于嵌入模式：

```makefile
jdbc:derby:[subsubprotocol:][databaseName][;attribute=value]
```

而且，我们可以将以下语法用于客户端/服务器模式：

```bash
jdbc:derby://server[:port]/databaseName[;attribute=value]
```

数据库 URL 在嵌入式模式下不包含主机名和端口号。例如：

```java
String urlConnection = "jdbc:derby:baeldung;create=true";
```

### 6.3. 在嵌入式模式下使用 Apache Derby

让我们以嵌入式模式连接到 Apache Derby 数据库，如果它不存在则在当前目录中创建它，创建一个表，并使用 SQL 语句将行插入表中：

```java
String urlConnection = "jdbc:derby:baeldung;create=true";
Connection con = DriverManager.getConnection(urlConnection);
Statement statement = con.createStatement();
String sql = "CREATE TABLE authors (id INT PRIMARY KEY,first_name VARCHAR(255),last_name VARCHAR(255))";
statement.execute(sql);
sql = "INSERT INTO authors VALUES (1, 'arash','ariani')";
statement.execute(sql);
```

### 6.4. 在客户端/服务器模式下使用 Apache Derby

首先，我们运行以下命令以客户端/服务器模式启动 Apache Derby：

```bash
$ java -jar $DERBY_HOME/lib/derbyrun.jar server start 
Sat Aug 28 20:47:58 IRDT 2021 : Security manager installed using the Basic server security policy.
Sat Aug 28 20:47:58 IRDT 2021 : Apache Derby Network Server - 10.13.1.1 -
(1873585) started and ready to accept connections on port 1527
```

我们使用 JDBC API 连接到本地主机上的 Apache Derby 服务器，并从baeldung数据库的authors表中选择所有条目：

```java
String urlConnection = "jdbc:derby://localhost:1527/baeldung";
   try (Connection con = DriverManager.getConnection(urlConnection)) {
       Statement statement = con.createStatement();
       String sql = "SELECT  FROM authors";
       ResultSet result = statement.executeQuery(sql);
         while (result.next()) {
           // We can print or use ResultSet here
         }
   } catch (SQLException ex) {
       ex.printStackTrace();
 }
```

## 7. Apache Derby 与 Spring Boot

本节不会详细介绍如何创建基于[Spring Boot](https://www.baeldung.com/spring-boot)的应用程序，仅介绍与Apache Derby数据库交互相关的设置。

### 7.1. Apache Derby 的依赖项

我们只需将Spring Boot中的[Spring Data](https://www.baeldung.com/spring-data)和 Apache Derby 依赖项添加到项目中，即可将 Apache Derby 合并到其中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.derby</groupId>
    <artifactId>derbyclient</artifactId>
    <version>10.13.1.1</version>
</dependency>
```

### 7.2. 春季启动配置

通过添加这些应用程序属性，我们可以使用 Derby 作为我们的持久数据库：

```properties
spring.datasource.url=jdbc:derby://localhost:1527/baeldung 
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.DerbyTenSevenDialect 
spring.jpa.hibernate.ddl-auto=update
spring.datasource.driver-class-name=org.apache.derby.jdbc.ClientDriver
```

## 八. 总结

在本教程中，我们研究了安装和配置 Apache Derby。之后，我们对工具和一些最重要的 SQL 语句进行了概述。我们用Java中的片段代码介绍了 JDBC 编程，并最终学习了如何配置Spring Boot以将 Apache Derby 用作持久数据库。