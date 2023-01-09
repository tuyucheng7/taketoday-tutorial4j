## 1. 概述

在本文中，我们将了解如何借助称为[Apache Thrift](https://thrift.apache.org/)的 RPC 框架开发跨平台的客户端-服务器应用程序。

我们将涵盖：

-   使用 IDL 定义数据类型和服务接口
-   安装库并生成不同语言的源代码
-   用特定语言实现定义的接口
-   实施客户端/服务器软件

如果你想直接看示例，请直接进入第 5 节。

## 2.阿帕奇节俭

Apache Thrift 最初由 Facebook 开发团队开发，目前由 Apache 维护。

与管理跨平台对象序列化/反序列化过程的[Protocol Buffers](https://www.baeldung.com/spring-rest-api-with-protocol-buffers)相比， Thrift 主要关注系统组件之间的通信层。

Thrift 使用一种特殊的接口描述语言 (IDL) 来定义数据类型和服务接口，这些数据类型和服务接口存储为.thrift文件，稍后用作编译器的输入，用于生成通过不同编程语言进行通信的客户端和服务器软件的源代码。

要在你的项目中使用 Apache Thrift，请添加此 Maven 依赖项：

```xml
<dependency>
    <groupId>org.apache.thrift</groupId>
    <artifactId>libthrift</artifactId>
    <version>0.10.0</version>
</dependency>
```

[你可以在Maven 存储库](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.thrift" AND a%3A"libthrift")中找到最新版本。

## 3.接口描述语言

如前所述，[IDL](https://thrift.apache.org/docs/idl)允许以中性语言定义通信接口。你将在下方找到当前支持的类型。

### 3.1. 基本类型

-   bool——一个布尔值(真或假)
-   byte——一个 8 位有符号整数
-   i16——一个 16 位有符号整数
-   i32——一个 32 位有符号整数
-   i64——一个 64 位有符号整数
-   double – 一个 64 位浮点数
-   string – 使用 UTF-8 编码的文本字符串

### 3.2. 特殊类型

-   二进制——未编码的字节序列
-   可选——Java 8 的可选类型

### 3.3. 结构体

Thrift结构等同于 OOP 语言中的类，但没有继承。一个结构有一组强类型字段，每个字段都有一个唯一的名称作为标识符。字段可能有各种注解(数字字段 ID、可选的默认值等)。

### 3.4. 集装箱

Thrift 容器是强类型容器：

-   列表——有序的元素列表
-   set——一组无序的唯一元素
-   map<type1,type2> – 严格唯一键到值的映射

容器元素可以是任何有效的 Thrift 类型。

### 3.5. 例外情况

异常在功能上等同于结构，只是它们继承自本机异常。

### 3.6. 服务

服务实际上是使用 Thrift 类型定义的通信接口。它们由一组命名函数组成，每个函数都有一个参数列表和一个返回类型。

## 4. 源代码生成

### 4.1. 语言支持

当前支持的语言列表很长：

-   C++
-   C＃
-   去
-   哈斯克尔
-   爪哇
-   Javascript
-   节点.js
-   Perl
-   PHP
-   Python
-   红宝石

[你可以在此处](https://thrift.apache.org/lib/)查看完整列表。

### 4.2. 使用库的可执行文件

只需下载[最新版本](https://thrift.apache.org/download)，必要时构建并安装它，并使用以下语法：

```bash
cd path/to/thrift
thrift -r --gen [LANGUAGE] [FILENAME]
```

在上面的命令集中，[LANGUAGE]是支持的语言之一，[FILENAME ] 是具有 IDL 定义的文件。

注意-r标志。一旦它注意到给定的.thrift文件中包含，它就会告诉 Thrift 递归地生成代码。

### 4.3. 使用 Maven 插件

在pom.xml文件中添加插件：

```xml
<plugin>
   <groupId>org.apache.thrift.tools</groupId>
   <artifactId>maven-thrift-plugin</artifactId>
   <version>0.1.11</version>
   <configuration>
      <thriftExecutable>path/to/thrift</thriftExecutable>
   </configuration>
   <executions>
      <execution>
         <id>thrift-sources</id>
         <phase>generate-sources</phase>
         <goals>
            <goal>compile</goal>
         </goals>
      </execution>
   </executions>
</plugin>
```

之后只需执行以下命令：

```bash
mvn clean install
```

请注意，此插件将不再有任何进一步的维护。请访问[此页面](https://github.com/dtrott/maven-thrift-plugin)以获取更多信息。

## 5. 客户端-服务器应用程序示例

### 5.1. 定义节俭文件

让我们编写一些带有异常和结构的简单服务：

```plaintext
namespace cpp com.baeldung.thrift.impl
namespace java com.baeldung.thrift.impl

exception InvalidOperationException {
    1: i32 code,
    2: string description
}

struct CrossPlatformResource {
    1: i32 id,
    2: string name,
    3: optional string salutation
}

service CrossPlatformService {

    CrossPlatformResource get(1:i32 id) throws (1:InvalidOperationException e),

    void save(1:CrossPlatformResource resource) throws (1:InvalidOperationException e),

    list <CrossPlatformResource> getList() throws (1:InvalidOperationException e),

    bool ping() throws (1:InvalidOperationException e)
}
```

如你所见，语法非常简单且不言自明。我们定义了一组命名空间(每个实现语言)、一个异常类型、一个结构，最后是一个将在不同组件之间共享的服务接口。

然后将其存储为service.thrift文件。

### 5.2. 编译和生成代码

现在是时候运行一个编译器来为我们生成代码了：

```bash
thrift -r -out generated --gen java /path/to/service.thrift
```

如你所见，我们添加了一个特殊标志-out来指定生成文件的输出目录。如果你没有收到任何错误，生成的目录将包含 3 个文件：

-   CrossPlatformResource.java
-   CrossPlatformService.java
-   InvalidOperationException.java

让我们通过运行生成服务的 C++ 版本：

```bash
thrift -r -out generated --gen cpp /path/to/service.thrift
```

现在我们得到了同一个服务接口的 2 个不同的有效实现(Java 和 C++)。

### 5.3. 添加服务实现

虽然 Thrift 已经为我们完成了大部分工作，但我们仍然需要编写自己的CrossPlatformService实现。为此，我们只需要实现一个CrossPlatformService.Iface接口：

```java
public class CrossPlatformServiceImpl implements CrossPlatformService.Iface {

    @Override
    public CrossPlatformResource get(int id) 
      throws InvalidOperationException, TException {
        return new CrossPlatformResource();
    }

    @Override
    public void save(CrossPlatformResource resource) 
      throws InvalidOperationException, TException {
        saveResource();
    }

    @Override
    public List<CrossPlatformResource> getList() 
      throws InvalidOperationException, TException {
        return Collections.emptyList();
    }

    @Override
    public boolean ping() throws InvalidOperationException, TException {
        return true;
    }
}
```

### 5.4. 编写服务器

正如我们所说，我们想要构建一个跨平台的客户端-服务器应用程序，因此我们需要一个服务器。Apache Thrift 的伟大之处在于它有自己的客户端-服务器通信框架，这使得通信变得轻而易举：

```java
public class CrossPlatformServiceServer {
    public void start() throws TTransportException {
        TServerTransport serverTransport = new TServerSocket(9090);
        server = new TSimpleServer(new TServer.Args(serverTransport)
          .processor(new CrossPlatformService.Processor<>(new CrossPlatformServiceImpl())));

        System.out.print("Starting the server... ");

        server.serve();

        System.out.println("done.");
    }

    public void stop() {
        if (server != null && server.isServing()) {
            System.out.print("Stopping the server... ");

            server.stop();

            System.out.println("done.");
        }
    }
}

```

首先是定义一个传输层，实现TServerTransport接口(或抽象类，更准确地说)。由于我们在谈论服务器，因此我们需要提供一个端口来监听。然后我们需要定义一个TServer实例并选择一个可用的实现：

-   TSimpleServer – 用于简单服务器
-   TThreadPoolServer——用于多线程服务器
-   TNonblockingServer – 用于非阻塞多线程服务器

最后，为已由 Thrift 为我们生成的所选服务器提供处理器实现，即CrossPlatofformService.Processor类。

### 5.5. 编写客户端

这是客户端的实现：

```java
TTransport transport = new TSocket("localhost", 9090);
transport.open();

TProtocol protocol = new TBinaryProtocol(transport);
CrossPlatformService.Client client = new CrossPlatformService.Client(protocol);

boolean result = client.ping();

transport.close();
```

从客户的角度来看，这些操作非常相似。

首先，定义传输并将其指向我们的服务器实例，然后选择合适的协议。唯一的不同是这里我们初始化了已经由 Thrift 生成的客户端实例，即CrossPlatformService.Client类。

由于它基于.thrift文件定义，我们可以直接调用那里描述的方法。在此特定示例中，client.ping()将对服务器进行远程调用，服务器将以true响应。

## 六. 总结

在本文中，我们向你展示了使用 Apache Thrift 的基本概念和步骤，并且展示了如何创建一个利用 Thrift 库的工作示例。