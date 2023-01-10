## 1. 简介

[SSH](https://www.baeldung.com/cs/ssh-intro)也称为 Secure Shell 或 Secure Socket Shell，是一种网络协议，它允许[一台计算机](https://www.baeldung.com/linux/secure-shell-ssh)通过不安全的网络安全地连接到另一台计算机。在本教程中，我们将展示如何使用 JSch 和 Apache MINA SSHD 库使用Java建立到远程 SSH 服务器的连接。

在我们的示例中，我们将首先打开 SSH 连接，然后执行一个命令，读取输出并将其写入控制台，最后关闭 SSH 连接。我们将使示例代码尽可能简单。

## 2. JSch

[JSch](http://www.jcraft.com/jsch/)是 SSH2 的Java实现，它允许我们连接到 SSH 服务器并使用端口转发、X11 转发和文件传输。此外，它是根据 BSD 样式许可证获得许可的，并为我们提供了一种使用Java建立 SSH 连接的简便方法。

首先，让我们将[JSch Maven 依赖](https://search.maven.org/search?q=g:com.jcraft AND a:jsch)项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>com.jcraft</groupId>
    <artifactId>jsch</artifactId>
    <version>0.1.55</version>
</dependency>
```

### 2.1. 执行

要使用 JSch 建立 SSH 连接，我们需要用户名、密码、主机 URL 和 SSH 端口。默认的 SSH 端口是 22，但我们可能会将服务器配置为使用其他端口进行 SSH 连接：

```java
public static void listFolderStructure(String username, String password, 
  String host, int port, String command) throws Exception {
    
    Session session = null;
    ChannelExec channel = null;
    
    try {
        session = new JSch().getSession(username, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        
        channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        channel.setOutputStream(responseStream);
        channel.connect();
        
        while (channel.isConnected()) {
            Thread.sleep(100);
        }
        
        String responseString = new String(responseStream.toByteArray());
        System.out.println(responseString);
    } finally {
        if (session != null) {
            session.disconnect();
        }
        if (channel != null) {
            channel.disconnect();
        }
    }
}
```

正如我们在代码中看到的那样，我们首先创建一个客户端会话并将其配置为连接到我们的 SSH 服务器。然后，我们创建一个客户端通道，用于与我们提供通道类型的 SSH 服务器通信——在本例中为exec，这意味着我们将向服务器传递 shell 命令。

此外，我们应该为将写入服务器响应的通道设置输出流。在我们使用 channel.connect()方法建立连接后，命令被传递，收到的响应被写在控制台上。

让我们看看如何使用 JSch 提供的不同配置参数：

-   StrictHostKeyChecking – 它指示应用程序是否将检查是否可以在已知主机中找到主机公钥。此外，可用的参数值为ask、yes和no，其中ask是默认值。如果我们将此属性设置为yes，JSch 将永远不会自动将主机密钥添加到known_hosts文件中，并且它将拒绝连接到主机密钥已更改的主机。这会强制用户手动添加所有新主机。如果我们将它设置为 no，JSch 会自动将一个新的主机密钥添加到已知主机列表中
-   compression.s2c – 指定是否对从服务器到客户端应用程序的数据流使用压缩。可用值为zlib和none，其中第二个是默认值
-   compression.c2s——指定是否对客户端-服务器方向的数据流使用压缩。可用值为zlib和none，其中第二个是默认值

与服务器通信结束后关闭会话和 SFTP 通道很重要，以避免内存泄漏。

## 3.Apache MINA SSHD

[Apache MINA SSHD](https://mina.apache.org/sshd-project/)为基于Java的应用程序提供 SSH 支持。这个库基于 Apache MINA，一个可扩展的高性能异步 IO 库。

让我们添加[Apache Mina SSHD Maven 依赖](https://search.maven.org/search?q=a:apache-sshd)项：

```xml
<dependency>
    <groupId>org.apache.sshd</groupId>
    <artifactId>sshd-core</artifactId>
    <version>2.5.1</version>
</dependency>
```

### 3.1. 执行

让我们看看使用 Apache MINA SSHD 连接到 SSH 服务器的代码示例：

```java
public static void listFolderStructure(String username, String password, 
  String host, int port, long defaultTimeoutSeconds, String command) throws IOException {
    
    SshClient client = SshClient.setUpDefaultClient();
    client.start();
    
    try (ClientSession session = client.connect(username, host, port)
      .verify(defaultTimeoutSeconds, TimeUnit.SECONDS).getSession()) {
        session.addPasswordIdentity(password);
        session.auth().verify(defaultTimeoutSeconds, TimeUnit.SECONDS);
        
        try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream(); 
          ClientChannel channel = session.createChannel(Channel.CHANNEL_SHELL)) {
            channel.setOut(responseStream);
            try {
                channel.open().verify(defaultTimeoutSeconds, TimeUnit.SECONDS);
                try (OutputStream pipedIn = channel.getInvertedIn()) {
                    pipedIn.write(command.getBytes());
                    pipedIn.flush();
                }
            
                channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), 
                TimeUnit.SECONDS.toMillis(defaultTimeoutSeconds));
                String responseString = new String(responseStream.toByteArray());
                System.out.println(responseString);
            } finally {
                channel.close(false);
            }
        }
    } finally {
        client.stop();
    }
}
```

在使用 Apache MINA SSHD 时，我们有一个与 JSch 非常相似的事件序列。首先，我们使用SshClient类实例建立到 SSH 服务器的连接。如果我们使用SshClient.setupDefaultClient() 对其进行初始化，我们将能够使用具有适用于大多数用例的默认配置的实例。这包括密码、压缩、MAC、密钥交换和签名。

之后，我们将创建ClientChannel并将ByteArrayOutputStream附加到它，以便我们将其用作响应流。正如我们所见，SSHD 需要为每个操作定义超时。它还允许我们定义在使用Channel.waitFor()方法传递命令后等待服务器响应的时间。

请务必注意，SSHD 会将完整的控制台输出写入响应流。JSch 只会用命令执行结果来做。

有关 Apache Mina SSHD 的完整文档可在[项目的官方 GitHub 存储库](https://github.com/apache/mina-sshd/tree/master/docs)中找到。

## 4. 总结

本文说明了如何使用两个可用的Java库(JSch 和 Apache Mina SSHD)与Java建立 SSH 连接。我们还展示了如何将命令传递到远程服务器并获取执行结果。