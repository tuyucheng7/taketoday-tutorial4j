## 1. 概述

在本教程中，我们将学习检查与[MongoDB](https://www.mongodb.com/)的连接。

重要的是要连接到单个 MongoDB 实例，我们需要指定 MongoDB 实例的 URI。

## 2. 使用 Mongo Shell 检查连接

在本节中，我们将使用 mongo shell 命令连接到 MongoDB 服务器。我们将探讨连接到 MongoDB 的不同情况。

### 2.1. 检查默认端口上的连接

默认情况下，MongoDB 在端口27017 上运行，但我们也可以在其他端口上运行它。 我们可以使用简单的 mongo 命令连接到 MongoDB 服务器：

```bash
$ mongo
MongoDB shell version v4.4.2
connecting to: mongodb://localhost:27017/?compressors=disabled&gssapiServiceName=mongodb
Implicit session: session { "id" : UUID("b7f80a0c-c7b9-4aea-b34c-605b85e601dd") }
MongoDB server version: 4.0.1-rc0-2-g54f1582fc6
```

在上面的命令中，默认情况下，MongoDB 假定端口为27017。如果 MongoDB 服务器关闭，我们会收到以下错误：

```bash
$ mongo --host localhost --port 27017 admin
MongoDB shell version v4.4.2
connecting to: mongodb://localhost:27017/admin?compressors=disabled&gssapiServiceName=mongodb
Error: couldn't connect to server localhost:27017, connection attempt failed:
  SocketException: Error connecting to localhost:27017 :: caused by :: Connection refused :
connect@src/mongo/shell/mongo.js:374:17
@(connect):2:6
exception: connect failed
exiting with code 1
```

在这种情况下，我们收到错误是因为我们无法连接到服务器。

### 2.2. 检查安全 MongoDB 数据库的连接

MongoDB 可以通过身份验证进行保护。在这种情况下，我们需要在命令中传递用户名和密码：

```bash
$ mongo mongodb://baeldung:baeldung@localhost:27017
```

在这里，我们使用用户名“baeldung”和密码“baeldung”连接到在本地主机上运行的 MongoDB。

### 2.3. 检查自定义端口上的连接

我们还可以在自定义端口上运行 MongoDB。我们需要做的就是在mongod.conf文件中进行更改。如果 MongoDB 在其他端口上运行，我们需要在命令中提供该端口：

```bash
$ mongo mongodb://localhost:27001
```

在这里，在 mongo shell 中，我们还可以检查数据库服务器的当前活动连接。

```bash
var status = db.serverStatus();
status.connections
{
    "current" : 21,
    "available" : 15979
}
```

serverStatus返回一个文档，该文档概述了数据库进程的当前状态。定期运行serverStatus 命令将收集有关 MongoDB 实例的统计信息。

## 3. 使用Java驱动程序代码检查连接

到目前为止，我们已经学会了使用 shell 检查与 MongoDB 的连接。现在让我们使用Java驱动程序代码查看相同的内容：

```java
MongoClientOptions.Builder builder = MongoClientOptions.builder();
// builder settings
ServerAddress ServerAddress = new ServerAddress("localhost", 27017);
MongoClient mongoClient = new MongoClient(ServerAddress, builder.build());

try {
    System.out.println("MongoDB Server is Up:- "+mongoClient.getAddress());
    System.out.println(mongoClient.getConnectPoint());
    System.out.println(db.getStats());
} catch (Exception e) {
    System.out.println("MongoDB Server is Down");
} finally{
    mongoClient.close();
}
```

在上面的代码中，我们首先创建了MongoClientOption构建器来自定义MongoClient连接的配置，然后使用服务器地址创建MongoClient连接。假设 MongoDB 服务器运行在本地主机的27017端口上。否则，MongoClient 将抛出错误。

## 4. 总结

在本教程中，我们学习了在不同的实时案例中检查 MongoDB 服务器的连接情况。

首先，我们使用 mongo default 命令检查连接，然后使用 authenticated 命令并连接到运行在自定义端口上的 MongoDB 服务器。接下来，我们检查了 MongoDB shell 和Java驱动程序代码的连接。