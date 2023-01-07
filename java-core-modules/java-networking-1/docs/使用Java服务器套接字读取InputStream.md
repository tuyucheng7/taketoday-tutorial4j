## 1. 概述

为了通过网络发送和接收数据，我们经常使用套接字。套接字只不过是 IP 地址和端口号的组合，它可以唯一标识在任何给定机器上运行的程序。

在本教程中，我们将展示如何读取通过套接字发送给我们的数据。

## 2. 从套接字读取数据

假设我们已经对[套接字编程有了基本的了解](https://www.baeldung.com/a-guide-to-java-sockets)。

现在，我们将更深入地研究在我们的服务器正在侦听的端口上读取数据。

首先，我们需要声明并初始化ServerSocket、Socket和DataInputStream变量：

```java
ServerSocket server = new ServerSocket(port);
Socket socket = server.accept();
DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
```

请注意，我们已选择将套接字的InputStream包装在DataInputStream 中。这使我们能够以可移植的方式读取文本行和Java原始数据类型。

这很好，因为现在，如果我们知道我们将接收的数据类型，我们可以使用专门的方法，如readChar()、readInt()、readDouble()和readLine()。 

但是，如果事先不知道数据的类型和长度，这可能具有挑战性。

在这种情况下，我们将从套接字获取字节流，而不是使用较低级别的read()函数。但是，这种方法有一个小问题：我们如何知道我们将获得的数据的长度和类型？

让我们在下一节中探讨这种情况。

## 3. 从套接字中读取二进制数据

当以字节为单位读取数据时，我们需要定义自己的服务器和客户端之间的通信协议。我们可以定义的最简单的协议称为 TLV(类型长度值)。这意味着写入套接字的每条消息都采用类型长度值的形式。

因此，我们将发送的每条消息定义为：

-   表示数据类型的 1字节字符，如s 表示 String
-   指示数据长度的4字节整数
-   然后是实际数据，其长度刚刚指出

[![图1-1-1](https://www.baeldung.com/wp-content/uploads/2019/03/figure1-1-1.png)](https://www.baeldung.com/wp-content/uploads/2019/03/figure1-1-1.png)

一旦客户端和服务器建立连接，每条消息都将遵循这种格式。然后，我们可以编写代码来解析每条消息并读取特定类型的n 个字节的数据。

让我们看看如何使用带有String消息的简单示例来实现它。

首先，我们需要调用 readChar()函数，读取数据的类型，然后调用 readInt()函数读取数据的长度：

```java
char dataType = in.readChar();
int length = in.readInt();
```

之后，我们需要读取接收到的数据。此处需要注意的重要一点是read()函数可能无法在一次调用中读取所有数据。因此，我们需要在 while 循环中调用read() ：

```java
if(dataType == 's') {
    byte[] messageByte = new byte[length];
    boolean end = false;
    StringBuilder dataString = new StringBuilder(length);
    int totalBytesRead = 0;
    while(!end) {
        int currentBytesRead = in.read(messageByte);
        totalBytesRead = currentBytesRead + totalBytesRead;
        if(totalBytesRead <= length) {
            dataString
              .append(new String(messageByte, 0, currentBytesRead, StandardCharsets.UTF_8));
        } else {
            dataString
              .append(new String(messageByte, 0, length - totalBytesRead + currentBytesRead, 
              StandardCharsets.UTF_8));
        }
        if(dataString.length()>=length) {
            end = true;
        }
    }
}
```

## 4.发送数据的客户端代码

那么客户端代码呢？其实很简单：

```java
char type = 's'; // s for string
String data = "This is a string of length 29";
byte[] dataInBytes = data.getBytes(StandardCharsets.UTF_8);

out.writeChar(type);
out.writeInt(dataInBytes.length);
out.write(dataInBytes);
```

这就是我们的客户所做的一切！

## 5.总结

在本文中，我们讨论了如何从套接字读取数据。我们研究了帮助我们读取特定类型数据的不同函数。此外，我们还看到了如何读取二进制数据。