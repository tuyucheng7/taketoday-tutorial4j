## 1. 概述

在本教程中，我们学习如何使用Spring多次从HttpServletRequest读取请求体。

HttpServletRequest是一个接口，它公开getInputStream()方法来读取请求正文。
默认情况下，来自此InputStream中的数据只能读取一次。

## 2. Maven依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.6.1</version>
</dependency>
```

## 3. Spring中的ContentCachingRequestWrapper

Spring提供了一个ContentCachingRequestWrapper类，此类包含了一个方法getContentAsByteArray()来多次读取请求正文。

但是，这个类有一个限制：我们**不能使用getInputStream()和getReader()方法多次读取请求正文**。

它通过使用InputStream来缓存请求正文，如果我们在其中一个过滤器中读取InputStream，那么过滤器链中的其他后续过滤器将无法再读取它。
由于这个限制，这个类并不适用于所有情况。

因此，让我们看一个更通用的解决方案。

## 4. 继承HttpServletRequest

**我们创建一个CachedBodyHttpServletRequest类，它继承了HttpServletRequestWrapper**。
这样，我们就不需要重写HttpServletRequest接口的所有抽象方法。

HttpServletRequestWrapper类有两个抽象方法getInputStream()和getReader()，我们将重写这两个方法并创建一个新的构造函数。

### 4.1 构造函数

首先，让我们创建一个构造函数，在它内部，我们从实际的InputStream中读取请求正文并将其存储在byte[]对象中：

```java
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private final byte[] cachedBody;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        InputStream requestInputStream = request.getInputStream();
        this.cachedBody = StreamUtils.copyToByteArray(requestInputStream);
    }
}
```

因此，我们能够通过cachedBody数组多次读取请求正文。

### 4.2 getInputStream()

接下来，我们重写getInputStream()方法，此方法用于读取原始请求体并将其转换为对象。

在这个方法中，我们**创建并返回一个新的CachedBodyServletInputStream类对象(ServletInputStream的一个实现)**：

```java
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CachedBodyServletInputStream(this.cachedBody);
    }
}
```

### 4.3 getReader()

然后，我们重写getReader()方法，此方法返回一个BufferedReader对象：

```java
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    @Override
    public BufferedReader getReader() throws IOException {
        // Create a reader from cachedContent and return it
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream));
    }
}
```

## 5. ServletInputStream实现

**我们创建一个类CachedBodyServletInputStream，它将实现ServletInputStream**。
在这个类中，我们创建一个新的构造函数并重写isFinished()、isReady()和read()方法。

### 5.1 构造函数

首先，我们创建一个接收字节数组作为参数的新构造函数。

**在它内部，使用该字节数组创建一个新的ByteArrayInputStream实例**。
之后，我们将其分配给全局变量cachedBodyInputStream：

```java
public class CachedBodyServletInputStream extends ServletInputStream {

    private final InputStream cachedBodyInputStream;

    public CachedBodyServletInputStream(byte[] cachedBody) {
        this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
    }
}
```

### 5.2 read()

然后重写read()方法，在这个方法中，我们将调用ByteArrayInputStream#read：

```java
public class CachedBodyServletInputStream extends ServletInputStream {

    @Override
    public int read() throws IOException {
        return cachedBodyInputStream.read();
    }
}
```

### 5.3 isFinished()

然后重写isFinished()方法，该方法指示InputStream是否有更多数据可供读取。
当有效数据的字节数为0时，它返回true：

```java
public class CachedBodyServletInputStream extends ServletInputStream {

    @Override
    public boolean isFinished() {
        try {
            return cachedBodyInputStream.available() == 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
```

### 5.4 isReady()

最后重写isReady()方法，该方法指示InputStream是否可以读取。

由于我们已经将InputStream复制到一个字节数组中，因此我们返回true，表示数据始终可用：

```java
public class CachedBodyServletInputStream extends ServletInputStream {

    @Override
    public boolean isReady() {
        return true;
    }
}
```

## 6. 过滤器

最后，我们创建一个新的过滤器来使用CachedBodyHttpServletRequest类。
在这里，我们继承Spring的OncePerRequestFilter类，这个类有一个抽象方法doFilterInternal()。

**在此方法中，我们从实际请求对象创建CachedBodyHttpServletRequest类的对象**：

```java
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
@WebFilter(filterName = "ContentCachingFilter", urlPatterns = "/*")
public class ContentCachingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("IN  ContentCachingFilter ");
        CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(request);
        filterChain.doFilter(cachedBodyHttpServletRequest, response);
    }
}
```

**然后，我们将这个新的请求包装器对象传递给过滤器链**。
因此，对getInputStream()方法的所有后续调用都将调用被重写的方法。

## 7. 总结

在本教程中，我们介绍了如何通过继承HttpServletRequest类来将原始请求对象封装为一个可多次读取请求体的请求对象。