## 1. 概述

在本教程中，我们将讨论如何在Java中使用 SFTP 从远程服务器上传和下载文件。

我们将使用三个不同的库：JSch、SSHJ 和 Apache Commons VFS。

## 2.使用JSch

首先，让我们看看如何使用 JSch 库从远程服务器上传和下载文件。

### 2.1. Maven 配置

我们需要将jsch依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.jcraft</groupId>
    <artifactId>jsch</artifactId>
    <version>0.1.55</version>
</dependency>
```

最新版本的jsch可以在[Maven Central](https://search.maven.org/search?q=g:com.jcraft AND a:jsch)上找到。

### 2.2. 设置 JSch

现在我们将设置 JSch。

JSch 使我们能够使用密码身份验证或公钥身份验证来访问远程服务器。在此示例中，我们将使用密码身份验证：

```java
private ChannelSftp setupJsch() throws JSchException {
    JSch jsch = new JSch();
    jsch.setKnownHosts("/Users/john/.ssh/known_hosts");
    Session jschSession = jsch.getSession(username, remoteHost);
    jschSession.setPassword(password);
    jschSession.connect();
    return (ChannelSftp) jschSession.openChannel("sftp");
}
```

在上面的示例中，remoteHost表示远程服务器的名称或 IP 地址(即example.com)。我们可以将测试中使用的变量定义为：

```java
private String remoteHost = "HOST_NAME_HERE";
private String username = "USERNAME_HERE";
private String password = "PASSWORD_HERE";
```

我们还可以使用以下命令生成known_hosts文件：

```bash
ssh-keyscan -H -t rsa REMOTE_HOSTNAME >> known_hosts
```

### 2.3. 使用 JSch 上传文件

要将文件上传到远程服务器，我们将使用ChannelSftp.put()方法：

```java
@Test
public void whenUploadFileUsingJsch_thenSuccess() throws JSchException, SftpException {
    ChannelSftp channelSftp = setupJsch();
    channelSftp.connect();
 
    String localFile = "src/main/resources/sample.txt";
    String remoteDir = "remote_sftp_test/";
 
    channelSftp.put(localFile, remoteDir + "jschFile.txt");
 
    channelSftp.exit();
}
```

在这个例子中，方法的第一个参数表示要传输的本地文件src/main/resources/sample.txt，而remoteDir是目标目录在远程服务器的路径。

### 2.4. 使用 JSch 下载文件

我们还可以使用ChannelSftp.get()从远程服务器下载文件：

```java
@Test
public void whenDownloadFileUsingJsch_thenSuccess() throws JSchException, SftpException {
    ChannelSftp channelSftp = setupJsch();
    channelSftp.connect();
 
    String remoteFile = "welcome.txt";
    String localDir = "src/main/resources/";
 
    channelSftp.get(remoteFile, localDir + "jschFile.txt");
 
    channelSftp.exit();
}
```

remoteFile是要下载的文件路径， localDir 是目标本地目录的路径。

## 3.使用SSHJ

接下来，我们将使用 SSHJ 库从远程服务器上传和下载文件。

### 3.1. Maven 配置

首先，我们将依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.hierynomus</groupId>
    <artifactId>sshj</artifactId>
    <version>0.27.0</version>
</dependency>
```

可以在[Maven Central](https://search.maven.org/search?q=g:com.hierynomus AND a:sshj)上找到最新版本的sshj。

### 3.2. 设置 SSHJ

然后我们将设置SSHClient。

SSHJ 还允许我们使用密码或公钥身份验证来访问远程服务器。

我们将在示例中使用密码身份验证：

```java
private SSHClient setupSshj() throws IOException {
    SSHClient client = new SSHClient();
    client.addHostKeyVerifier(new PromiscuousVerifier());
    client.connect(remoteHost);
    client.authPassword(username, password);
    return client;
}
```

### 3.3. 使用 SSHJ 上传文件

与 JSch 类似，我们将使用SFTPClient.put()方法将文件上传到远程服务器：

```java
@Test
public void whenUploadFileUsingSshj_thenSuccess() throws IOException {
    SSHClient sshClient = setupSshj();
    SFTPClient sftpClient = sshClient.newSFTPClient();
 
    sftpClient.put(localFile, remoteDir + "sshjFile.txt");
 
    sftpClient.close();
    sshClient.disconnect();
}
```

我们在这里定义了两个新变量：

```java
private String localFile = "src/main/resources/input.txt";
private String remoteDir = "remote_sftp_test/";
```

### 3.4. 使用 SSHJ 下载文件

从远程服务器下载文件也是如此；我们将使用SFTPClient.get()：

```java
@Test
public void whenDownloadFileUsingSshj_thenSuccess() throws IOException {
    SSHClient sshClient = setupSshj();
    SFTPClient sftpClient = sshClient.newSFTPClient();
 
    sftpClient.get(remoteFile, localDir + "sshjFile.txt");
 
    sftpClient.close();
    sshClient.disconnect();
}
```

我们将添加上面使用的两个变量：

```java
private String remoteFile = "welcome.txt";
private String localDir = "src/main/resources/";
```

## 4. 使用 Apache Commons VFS

最后，我们将使用 Apache Commons VFS 将文件传输到远程服务器。

事实上，Apache Commons VFS 内部使用了 JSch 库。

### 4.1. Maven 配置

我们需要将commons-vfs2依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-vfs2</artifactId>
    <version>2.4</version>
</dependency>
```

最新版本的commons-vfs2可以在[Maven Central](https://search.maven.org/search?q=g:org.apache.commons AND a:commons-vfs2)上找到。

### 4.2. 使用 Apache Commons VFS 上传文件

Apache Commons VFS 有点不同。

我们将使用FileSystemManager从我们的目标文件创建FileObject ，然后使用FileObject来传输我们的文件。

在此示例中，我们将使用FileObject.copyFrom()方法上传文件：

```java
@Test
public void whenUploadFileUsingVfs_thenSuccess() throws IOException {
    FileSystemManager manager = VFS.getManager();
 
    FileObject local = manager.resolveFile(
      System.getProperty("user.dir") + "/" + localFile);
    FileObject remote = manager.resolveFile(
      "sftp://" + username + ":" + password + "@" + remoteHost + "/" + remoteDir + "vfsFile.txt");
 
    remote.copyFrom(local, Selectors.SELECT_SELF);
 
    local.close();
    remote.close();
}
```

注意本地文件路径要绝对，远程文件路径以sftp://username: password@remoteHost开头。

### 4.3. 使用 Apache Commons VFS 下载文件

从远程服务器下载文件非常相似；我们还将使用FileObject.copyFrom()从remoteFilelocalFile：

```java
@Test
public void whenDownloadFileUsingVfs_thenSuccess() throws IOException {
    FileSystemManager manager = VFS.getManager();
 
    FileObject local = manager.resolveFile(
      System.getProperty("user.dir") + "/" + localDir + "vfsFile.txt");
    FileObject remote = manager.resolveFile(
      "sftp://" + username + ":" + password + "@" + remoteHost + "/" + remoteFile);
 
    local.copyFrom(remote, Selectors.SELECT_SELF);
 
    local.close();
    remote.close();
}
```

## 5.总结

在本文中，我们学习了如何使用Java从远程 SFTP 服务器上传和下载文件。为此，我们使用了多个库：JSch、SSHJ 和 Apache Commons VFS。