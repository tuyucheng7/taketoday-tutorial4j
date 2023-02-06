## 1. 概述

HTTPS是HTTP的扩展，它允许计算机网络中两个实体之间的安全通信。HTTPS使用[TLS](https://en.wikipedia.org/wiki/Transport_Layer_Security)(传输层安全)协议来实现安全连接。

**TLS可以通过单向或双向证书验证来实现**。在单向中，服务器共享其公共证书，以便客户端可以验证它是受信任的服务器。另一种方法是双向验证。**客户端和服务器共享他们的公共证书来验证彼此的身份**。

**本文将重点介绍双向证书验证，其中服务器也会检查客户端的证书**。

## 2. Java和TLS版本

**TLS 1.3是该协议的最新版本。这个版本更高效、更安全**。它具有更高效的握手协议并使用现代密码算法。

Java在Java 11中开始支持这个版本的协议。我们将使用这个版本来生成证书并实现一个简单的客户端-服务器对，它使用TLS来相互验证。

## 3. 用Java生成证书

由于我们正在进行双向[TLS身份验证](https://baeldung.com/spring-tls-setup)，因此我们需要为客户端和服务器生成证书。

在生产环境中，建议从证书颁发机构购买证书。但是，出于测试或演示目的，使用[自签名证书](https://www.baeldung.com/spring-boot-https-self-signed-certificate)就足够了。对于本文，我们将使用Java的keytool来生成自签名证书。

### 3.1 服务器证书

首先， 我们生成服务器密钥库：

```shell
keytool -genkey -alias serverkey -keyalg RSA -keysize 2048 -sigalg SHA256withRSA -keystore serverkeystore.p12 -storepass password -ext san=ip:127.0.0.1,dns:localhost
```

**我们使用keytool -ext选项设置主题备用名称(SAN)以定义标识服务器的本地主机名/IP地址**。通常，我们可以使用此选项指定多个地址。但是，客户端将被限制使用这些地址之一连接到服务器。

接下来，我们将证书导出到文件server-certificate.pem：

```shell
keytool -exportcert -keystore serverkeystore.p12 -alias serverkey -storepass password -rfc -file server-certificate.pem
```

最后，**我们将服务器证书添加到客户端的信任库中**：

```shell
keytool -import -trustcacerts -file server-certificate.pem -keypass password -storepass password -keystore clienttruststore.jks
```

### 3.2 客户端证书

同样，我们生成客户端密钥存储并导出其证书：

```shell
keytool -genkey -alias clientkey -keyalg RSA -keysize 2048 -sigalg SHA256withRSA -keystore clientkeystore.p12 -storepass password -ext san=ip:127.0.0.1,dns:localhost

keytool -exportcert -keystore clientkeystore.p12 -alias clientkey -storepass password -rfc -file client-certificate.pem

keytool -import -trustcacerts -file client-certificate.pem -keypass password -storepass password -keystore servertruststore.jks
```

**在最后一个命令中，我们将客户端的证书添加到服务器信任库中**。

## 4. 服务器Java实现

使用Java套接字服务器实现是微不足道的。SSLSocketEchoServer类获取SSLServerSocket以轻松支持TLS身份验证。我们只需要指定密码和协议，其余的只是一个标准的回显服务器，它回复客户端发送的相同消息：

```java
public class SSLSocketEchoServer {

    static void startServer(int port) throws IOException {

        ServerSocketFactory factory = SSLServerSocketFactory.getDefault();
        try (SSLServerSocket listener = (SSLServerSocket) factory.createServerSocket(port)) {
            listener.setNeedClientAuth(true);
            listener.setEnabledCipherSuites(new String[]{"TLS_AES_128_GCM_SHA256"});
            listener.setEnabledProtocols(new String[]{"TLSv1.3"});
            System.out.println("listening for messages...");
            try (Socket socket = listener.accept()) {

                InputStream is = new BufferedInputStream(socket.getInputStream());
                byte[] data = new byte[2048];
                int len = is.read(data);

                String message = new String(data, 0, len);
                OutputStream os = new BufferedOutputStream(socket.getOutputStream());
                System.out.printf("server received %d bytes: %s%n", len, message);
                String response = message + " processed by server";
                os.write(response.getBytes(), 0, response.getBytes().length);
                os.flush();
            }
        }
    }
}
```

服务器侦听客户端连接。**listener.setNeedClientAuth(true)的调用要求客户端与服务器共享其证书**。在后台，SSLServerSocket实现使用TLS协议对客户端进行身份验证。

**在我们的例子中，自签名客户端证书在服务器信任库中，因此套接字将接受连接**。服务器继续使用InputStream读取消息。然后，它使用OutputStream回显附加确认的传入消息。

## 5. 客户端Java实现

与我们对服务器所做的方式相同，客户端实现是一个简单的SSLSocketClient类：

```java
public class SSLSocketClient {

    static void startClient(String host, int port) throws IOException {

        SocketFactory factory = SSLSocketFactory.getDefault();
        try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port)) {

            socket.setEnabledCipherSuites(new String[]{"TLS_AES_128_GCM_SHA256"});
            socket.setEnabledProtocols(new String[]{"TLSv1.3"});

            String message = "Hello World Message";
            System.out.println("sending message: " + message);
            OutputStream os = new BufferedOutputStream(socket.getOutputStream());
            os.write(message.getBytes());
            os.flush();

            InputStream is = new BufferedInputStream(socket.getInputStream());
            byte[] data = new byte[2048];
            int len = is.read(data);
            System.out.printf("client received %d bytes: %s%n", len, new String(data, 0, len));
        }
    }
}
```

首先，我们创建一个与服务器建立连接的SSLSocket。在后台，套接字将设置TLS连接建立握手。**作为此握手的一部分，客户端将验证服务器的证书并检查它是否在客户端truststore中**。

成功建立连接后，客户端使用输出流向服务器发送消息。然后它使用输入流读取服务器的响应。

## 6. 运行应用程序

要运行服务器，请打开命令窗口并运行：

```shell
java -Djavax.net.ssl.keyStore=/path/to/serverkeystore.p12 \ 
  -Djavax.net.ssl.keyStorePassword=password \
  -Djavax.net.ssl.trustStore=/path/to/servertruststore.jks \ 
  -Djavax.net.ssl.trustStorePassword=password \
  cn.tuyucheng.taketoday.httpsclientauthentication.SSLSocketEchoServer
```

我们为javax.net.ssl指定系统属性。密钥库和javax.net.ssl。trustStore指向我们之前使用keytool创建的serverkeystore.p12和servertruststore.jks文件。

要运行客户端，我们打开另一个命令窗口并运行：

```shell
java -Djavax.net.ssl.keyStore=/path/to/clientkeystore.p12 \ 
  -Djavax.net.ssl.keyStorePassword=password \ 
  -Djavax.net.ssl.trustStore=/path/to/clienttruststore.jks \ 
  -Djavax.net.ssl.trustStorePassword=password \ 
  cn.tuyucheng.taketoday.httpsclientauthentication.SSLScocketClient	
```

同样，我们设置javax.net.ssl.keyStore和javax.net.ssl。trustStore系统属性指向我们之前使用keytool生成的clientkeystore.p12和clienttruststore.jks文件。

## 7. 总结

**我们编写了一个简单的客户端-服务器Java实现，它使用服务器和客户端证书进行双向TLS身份验证**。

我们使用keytool来生成自签名证书。