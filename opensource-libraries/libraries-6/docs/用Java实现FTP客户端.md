## 1. 概述

在本教程中，我们将了解如何利用[Apache Commons Net](https://commons.apache.org/proper/commons-net/)库与外部 FTP 服务器进行交互。

## 2.设置

当使用用于与外部系统交互的库时，编写一些额外的集成测试通常是个好主意，以确保我们正确使用库。

如今，我们通常会使用 Docker 为我们的集成测试启动这些系统。然而，特别是在被动模式下使用时，如果我们想使用动态端口映射(这通常是能够在共享 CI 服务器上运行的测试所必需的)，那么 FTP 服务器并不是最容易在容器内透明运行的应用程序).

这就是我们将使用[MockFtpServer](http://mockftpserver.sourceforge.net/index.html)的原因，它是一个用Java编写的 Fake/Stub FTP 服务器，它提供了广泛的 API 以便在 JUnit 测试中轻松使用：

```xml
<dependency>
    <groupId>commons-net</groupId>
    <artifactId>commons-net</artifactId>
    <version>3.6</version>
</dependency>
<dependency> 
    <groupId>org.mockftpserver</groupId> 
    <artifactId>MockFtpServer</artifactId> 
    <version>2.7.1</version> 
    <scope>test</scope> 
</dependency>
```

建议始终使用最新版本。这些可以在 [这里](https://search.maven.org/classic/#search|ga|1|a%3A"commons-net")和[这里](https://search.maven.org/classic/#search|ga|1|a%3A"MockFtpServer")找到。

## 3. JDK 中的 FTP 支持

令人惊讶的是，某些 JDK 风格中已经以sun.net.www.protocol.ftp.FtpURLConnection的形式提供了对 FTP 的基本支持 。

但是，我们不应该直接使用这个类，而是可以使用 JDK 的 java.net。URL 类作为抽象。

这种 FTP 支持非常基础，但利用 java.nio.file.Files的便利 API，对于简单的用例来说就足够了：

```java
@Test
public void givenRemoteFile_whenDownloading_thenItIsOnTheLocalFilesystem() throws IOException {
    String ftpUrl = String.format(
      "ftp://user:password@localhost:%d/foobar.txt", fakeFtpServer.getServerControlPort());

    URLConnection urlConnection = new URL(ftpUrl).openConnection();
    InputStream inputStream = urlConnection.getInputStream();
    Files.copy(inputStream, new File("downloaded_buz.txt").toPath());
    inputStream.close();

    assertThat(new File("downloaded_buz.txt")).exists();

    new File("downloaded_buz.txt").delete(); // cleanup
}
```

由于此基本 FTP 支持已经缺少文件列表等基本功能，因此我们将在以下示例中使用 Apache Net Commons 库中的 FTP 支持。

## 4.连接

我们首先需要连接到 FTP 服务器。让我们从创建一个类FtpClient 开始。

它将作为实际 Apache Commons Net FTP 客户端的抽象 API：

```java
class FtpClient {

    private String server;
    private int port;
    private String user;
    private String password;
    private FTPClient ftp;

    // constructor

    void open() throws IOException {
        ftp = new FTPClient();

        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        ftp.connect(server, port);
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }

        ftp.login(user, password);
    }

    void close() throws IOException {
        ftp.disconnect();
    }
}
```

我们需要服务器地址和端口，以及用户名和密码。连接后有必要实际检查回复代码，以确保连接成功。我们还添加了一个 PrintCommandListener，以打印我们在使用命令行工具连接到 FTP 服务器时通常会看到的响应到标准输出。

因为我们的集成测试会有一些样板代码，比如启动/停止 MockFtpServer 和连接/断开我们的客户端，我们可以在@Before和@After方法中做这些事情：

```java
public class FtpClientIntegrationTest {

    private FakeFtpServer fakeFtpServer;

    private FtpClient ftpClient;

    @Before
    public void setup() throws IOException {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.addUserAccount(new UserAccount("user", "password", "/data"));

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/data"));
        fileSystem.add(new FileEntry("/data/foobar.txt", "abcdef 1234567890"));
        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.setServerControlPort(0);

        fakeFtpServer.start();

        ftpClient = new FtpClient("localhost", fakeFtpServer.getServerControlPort(), "user", "password");
        ftpClient.open();
    }

    @After
    public void teardown() throws IOException {
        ftpClient.close();
        fakeFtpServer.stop();
    }
}
```

通过将模拟服务器控制端口设置为值 0，我们将启动模拟服务器和一个免费的随机端口。

这就是为什么我们必须在服务器启动后 创建FtpClient时使用fakeFtpServer.getServerControlPort()检索实际端口。

## 5. 清单文件

第一个实际用例是列出文件。

让我们先从测试开始，TDD 风格：

```java
@Test
public void givenRemoteFile_whenListingRemoteFiles_thenItIsContainedInList() throws IOException {
    Collection<String> files = ftpClient.listFiles("");
    assertThat(files).contains("foobar.txt");
}
```

实现本身同样简单。为了使返回的数据结构在本示例中更简单一些，我们将返回的FTPFile数组转换为使用Java8 Streams的字符串列表：

```java
Collection<String> listFiles(String path) throws IOException {
    FTPFile[] files = ftp.listFiles(path);
    return Arrays.stream(files)
      .map(FTPFile::getName)
      .collect(Collectors.toList());
}
```

## 6.下载

为了从 FTP 服务器下载文件，我们定义了一个 API。

这里我们在本地文件系统上定义源文件和目标：

```java
@Test
public void givenRemoteFile_whenDownloading_thenItIsOnTheLocalFilesystem() throws IOException {
    ftpClient.downloadFile("/buz.txt", "downloaded_buz.txt");
    assertThat(new File("downloaded_buz.txt")).exists();
    new File("downloaded_buz.txt").delete(); // cleanup
}
```

Apache Net Commons FTP 客户端包含一个方便的 API，它将直接写入定义的OutputStream。 这意味着我们可以直接使用它：

```java
void downloadFile(String source, String destination) throws IOException {
    FileOutputStream out = new FileOutputStream(destination);
    ftp.retrieveFile(source, out);
}
```

## 7.上传

MockFtpServer 提供了一些有用的方法来访问其文件系统的内容。我们可以使用此功能为上传功能编写一个简单的集成测试：

```java
@Test
public void givenLocalFile_whenUploadingIt_thenItExistsOnRemoteLocation() 
  throws URISyntaxException, IOException {
  
    File file = new File(getClass().getClassLoader().getResource("baz.txt").toURI());
    ftpClient.putFileToPath(file, "/buz.txt");
    assertThat(fakeFtpServer.getFileSystem().exists("/buz.txt")).isTrue();
}
```

上传文件在 API 方面的工作方式与下载文件非常相似，但我们需要提供一个InputStream而不是使用OutputStream：

```java
void putFileToPath(File file, String path) throws IOException {
    ftp.storeFile(path, new FileInputStream(file));
}
```

## 八. 总结

我们已经看到，将Java与 Apache Net Commons 结合使用使我们能够轻松地与外部 FTP 服务器进行交互，以进行读取和写入访问。