## 1. 概述

[Apache ZooKeeper](https://zookeeper.apache.org/) 是一种分布式协调服务，可简化分布式应用程序的开发。它被 Apache Hadoop、HBase[等](https://cwiki.apache.org/confluence/display/ZOOKEEPER/PoweredBy)项目用于领导者选举、配置管理、节点协调、服务器租赁管理等不同用例。

ZooKeeper 集群中的节点将它们的数据存储在类似于标准文件系统或树数据结构的共享分层命名空间中。

在本文中，我们将探讨如何使用 Apache Zookeeper 的JavaAPI 来存储、更新和删除存储在 ZooKeeper 中的信息。

## 2.设置

可以在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.zookeeper" AND a%3A"zookeeper")找到最新版本的 Apache ZooKeeperJava库：

```xml
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.4.11</version>
</dependency>
```

## 3. ZooKeeper 数据模型——ZNode

ZooKeeper 有一个分层命名空间，很像一个分布式文件系统，它存储协调数据，如状态信息、协调信息、位置信息等。这些信息存储在不同的节点上。

ZooKeeper 树中的每个节点都称为 ZNode。

每个 ZNode 维护任何数据或 ACL 更改的版本号和时间戳。此外，这允许 ZooKeeper 验证缓存并协调更新。

## 四、安装

### 4.1. 安装

最新的 ZooKeeper 版本可以从[这里](https://www.apache.org/dyn/closer.cgi/zookeeper/)下载。在此之前，我们需要确保满足[此处](https://zookeeper.apache.org/doc/r3.3.5/zookeeperAdmin.html#sc_systemReq)描述的系统要求。

### 4.2. 独立模式

对于本文，我们将以独立模式运行 ZooKeeper，因为它需要最少的配置。按照[此处](https://zookeeper.apache.org/doc/r3.3.5/zookeeperStarted.html#sc_InstallingSingleMode)文档中描述的步骤进行操作。

注意：在独立模式下，没有，所以如果 ZooKeeper 进程失败，服务将会关闭。

## 5. ZooKeeper CLI 示例

我们现在将使用 ZooKeeper 命令行界面 (CLI) 与 ZooKeeper 交互：

```bash
bin/zkCli.sh -server 127.0.0.1:2181
```

上面的命令在本地启动一个独立实例。现在让我们看看如何在 ZooKeeper 中创建 ZNode 和存储信息：

```plaintext
[zk: localhost:2181(CONNECTED) 0] create /MyFirstZNode ZNodeVal
Created /FirstZnode
```

我们刚刚在ZooKeeper 分层命名空间的根目录下创建了一个 ZNode “MyFirstZNode” ，并向其写入了“ZNodeVal”。

由于我们没有传递任何标志，因此创建的 ZNode 将是持久的。

现在让我们发出一个“get”命令来获取数据以及与 ZNode 关联的元数据：

```plaintext
[zk: localhost:2181(CONNECTED) 1] get /FirstZnode

“Myfirstzookeeper-app”
cZxid = 0x7f
ctime = Sun Feb 18 16:15:47 IST 2018
mZxid = 0x7f
mtime = Sun Feb 18 16:15:47 IST 2018
pZxid = 0x7f
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 22
numChildren = 0
```

我们可以使用set操作更新现有ZNode的数据。

例如：

```bash
set /MyFirstZNode ZNodeValUpdated
```

这会将 MyFirstZNode 中的数据从 ZNodeVal更新为ZNodeValUpdated 。

## 6. ZooKeeperJavaAPI 示例

现在让我们看看 ZookeeperJavaAPI 并创建一个节点，更新节点并检索一些数据。

### 6.1.Java包

ZooKeeperJava绑定主要由两个Java包组成：

1.  org.apache.zookeeper ：它定义了 ZooKeeper 客户端库的主类以及 ZooKeeper 事件类型和状态的许多静态定义
2.  org.apache.zookeeper.data：定义与 ZNode 相关的特征，例如访问控制列表 (ACL)、ID、统计信息等

还有用于服务器实现的 ZooKeeperJavaAPI，例如org.apache.zookeeper.server、org.apache.zookeeper.server.quorum和org.apache.zookeeper.server.upgrade。

但是，它们超出了本文的范围。

### 6.2. 连接到 ZooKeeper 实例

现在让我们创建ZKConnection类，它将用于连接和断开与已经运行的 ZooKeeper 的连接：

```java
public class ZKConnection {
    private ZooKeeper zoo;
    CountDownLatch connectionLatch = new CountDownLatch(1);

    // ...

    public ZooKeeper connect(String host) 
      throws IOException, 
      InterruptedException {
        zoo = new ZooKeeper(host, 2000, new Watcher() {
            public void process(WatchedEvent we) {
                if (we.getState() == KeeperState.SyncConnected) {
                    connectionLatch.countDown();
                }
            }
        });

        connectionLatch.await();
        return zoo;
    }

    public void close() throws InterruptedException {
        zoo.close();
    }
}
```

要使用 ZooKeeper 服务，应用程序必须首先实例化ZooKeeper类的对象，该类是 ZooKeeper 客户端库的主类。

在connect方法中，我们正在实例化ZooKeeper类的实例。此外，我们已经注册了一个回调方法来处理来自 ZooKeeper 的WatchedEvent以接受连接，并相应地使用CountDownLatch的倒计时方法完成连接方法。

一旦建立了与服务器的连接，就会为客户端分配一个会话 ID。为了保持会话有效，客户端应该定期向服务器发送心跳。

只要其会话 ID 保持有效，客户端应用程序就可以调用 ZooKeeper API。

### 6.3. 客户端操作

我们现在将创建一个ZKManager接口，它公开不同的操作，如创建 ZNode 和保存一些数据，获取和更新 ZNode 数据：

```java
public interface ZKManager {
    public void create(String path, byte[] data)
      throws KeeperException, InterruptedException;
    public Object getZNodeData(String path, boolean watchFlag);
    public void update(String path, byte[] data) 
      throws KeeperException, InterruptedException;
}
```

我们现在看一下上面接口的实现：

```java
public class ZKManagerImpl implements ZKManager {
    private static ZooKeeper zkeeper;
    private static ZKConnection zkConnection;

    public ZKManagerImpl() {
        initialize();
    }

    private void initialize() {
        zkConnection = new ZKConnection();
        zkeeper = zkConnection.connect("localhost");
    }

    public void closeConnection() {
        zkConnection.close();
    }

    public void create(String path, byte[] data) 
      throws KeeperException, 
      InterruptedException {
 
        zkeeper.create(
          path, 
          data, 
          ZooDefs.Ids.OPEN_ACL_UNSAFE, 
          CreateMode.PERSISTENT);
    }

    public Object getZNodeData(String path, boolean watchFlag) 
      throws KeeperException, 
      InterruptedException {
 
        byte[] b = null;
        b = zkeeper.getData(path, null, null);
        return new String(b, "UTF-8");
    }

    public void update(String path, byte[] data) throws KeeperException, 
      InterruptedException {
        int version = zkeeper.exists(path, true).getVersion();
        zkeeper.setData(path, data, version);
    }
}
```

在上面的代码中，connect和disconnect调用被委托给之前创建的ZKConnection类。我们的create方法用于从字节数组数据的给定路径创建 ZNode。仅出于演示目的，我们将 ACL 保持完全打开状态。

一旦创建，ZNode 就会持久存在，并且在客户端断开连接时不会被删除。

在我们的getZNodeData方法中从 ZooKeeper 获取 ZNode 数据的逻辑非常简单。最后，使用update方法，我们检查给定路径上是否存在 ZNode 并在存在时获取它。

除此之外，为了更新数据，我们首先检查 ZNode 是否存在并获取当前版本。然后，我们以 ZNode 的路径、数据和当前版本作为参数调用setData方法。只有传递的版本与最新版本匹配时，ZooKeeper 才会更新数据。

## 七. 总结

在开发分布式应用程序时，Apache ZooKeeper 作为分布式协调服务起着至关重要的作用。专门用于存储共享配置、选举主节点等用例。

ZooKeeper 还提供了一个优雅的基于Java的 API，用于客户端应用程序代码，以便与 ZooKeeper ZNode 进行无缝通信。