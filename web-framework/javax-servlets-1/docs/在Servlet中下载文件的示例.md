##  1. 概述

Web 应用程序的一个共同特征是能够下载文件。

在本教程中，我们将介绍一个创建可下载文件并从JavaServlet 应用程序提供该文件的简单示例。

我们使用的文件将来自 webapp 资源。

## 2.Maven依赖

如果使用 Jakarta EE，那么我们不需要添加任何依赖项。但是，如果我们使用JavaSE，我们将需要 javax.servlet-api 依赖项：

```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>4.0.1</version>
    <scope>provided</scope>
</dependency>

```

[可以在此处](https://search.maven.org/classic/#search|gav|1|g%3A"javax.servlet" AND a%3A"javax.servlet-api")找到最新版本的依赖项。

## 3.服务小程序

让我们先看一下代码，然后看看是怎么回事：

```java
@WebServlet("/download")
public class DownloadServlet extends HttpServlet {
    private final int ARBITARY_SIZE = 1048;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
      throws ServletException, IOException {
    
        resp.setContentType("text/plain");
        resp.setHeader("Content-disposition", "attachment; filename=sample.txt");

        try(InputStream in = req.getServletContext().getResourceAsStream("/WEB-INF/sample.txt");
          OutputStream out = resp.getOutputStream()) {

            byte[] buffer = new byte[ARBITARY_SIZE];
        
            int numBytesRead;
            while ((numBytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, numBytesRead);
            }
        }
    }
}
```

### 3.1. 请求端点

@WebServlet(“/download”)注解标记DownloadServlet类以服务于指向“/download” 端点的请求。

或者，我们可以通过在 web.xml 文件中描述映射来做到这一点。

### 3.2. 响应内容类型

HttpServletResponse对象有一个名为setContentType的 方法，我们可以使用它来设置 HTTP 响应的Content-Type标头。

Content-Type是标头属性的历史名称。另一个名称是 MIME 类型(多用途 Internet 邮件扩展)。我们现在简单地将值称为媒体类型。

该值可以是“application/pdf”、“text/plain”、“text/html”、“image/jpg”等，官方列表由 Internet Assigned Numbers Authority (IANA) 维护，可以[在此处](https://www.iana.org/assignments/media-types/media-types.xhtml#application)找到.

对于我们的示例，我们使用一个简单的文本文件。文本文件的Content-Type是“text/plain”。

### 3.3. 响应内容-处置

在响应对象中设置 Content-Disposition 标头告诉浏览器如何处理它正在访问的文件。

浏览器将Content-Disposition的使用理解为一种约定，但它实际上并不是 HTTP 标准的一部分。W3 有一份关于使用Content-Disposition的备忘录，可[在此处](https://www.ietf.org/rfc/rfc1806.txt)阅读。

响应主体的 Content-Disposition 值将是“inline”(对于要呈现的网页内容)或“attachment”(对于可下载文件)。

如果未指定，则默认Content-Disposition为“inline”。

使用可选的标头参数，我们可以指定文件名“sample.txt”。

一些浏览器会立即使用给定的文件名下载文件，而其他浏览器会显示一个包含我们预定义值的下载对话框。

采取的确切操作将取决于浏览器。

### 3.4. 读取文件并写入输出流

在剩余的代码行中，我们从请求中获取ServletContext ，并使用它来获取位于“/WEB-INF/sample.txt”的文件。

使用HttpServletResponse #getOutputStream ()，然后我们从资源的输入流中读取并写入响应的OutputStream。

我们使用的字节数组的大小是任意的。我们可以根据合理分配的内存量来决定大小，以便将数据从InputStream传递到OutputStream；nuber越小，循环越多；数字越大，内存使用率越高。

这个循环一直持续到numByteRead为 0，因为这表示文件结束。

### 3.5. 关闭并冲洗

Stream实例在使用后必须关闭以释放它当前持有的任何资源。Writer实例也必须被刷新以将任何剩余的缓冲字节写入它的目的地。

使用try-with-resources语句，应用程序将自动关闭定义为try语句一部分的任何AutoCloseable实例。[在此处](https://www.baeldung.com/java-try-with-resources)阅读有关 try-with-resources 的更多信息。

我们使用这两种方法来释放内存，确保我们准备好的数据从我们的应用程序中发送出去。

### 3.6. 下载文件

一切就绪后，我们现在就可以运行我们的 Servlet 了。

现在，当我们访问相对端点“/download”时，我们的浏览器将尝试将文件下载为“simple.txt”。

## 4. 总结

从 Servlet 下载文件成为一个简单的过程。使用流允许我们以字节形式传递数据，媒体类型通知客户端浏览器期望的数据类型。

由浏览器决定如何处理响应，但是，我们可以为Content-Disposition标头提供一些指导。