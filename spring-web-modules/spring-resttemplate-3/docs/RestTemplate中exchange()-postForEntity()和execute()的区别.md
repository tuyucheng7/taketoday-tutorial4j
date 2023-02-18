## 一、简介

在 Spring 生态系统的众多部分中，有一个名为[*RestTemplate*](https://www.baeldung.com/rest-template)的类。**此实用程序是用于发送 HTTP 消息和处理返回响应的高级类**。

在本教程中，我们将了解*RestTemplate类的**exchange()*、*postForEntity()*和*execute()*方法之间的区别。

## 2. 什么是*RestTemplate*？

如上所述，*RestTemplate*是 Spring Framework 中的一个实用类，它使发送 HTTP 消息和处理响应变得简单。RestTemplate类非常适合编写简单的 HTTP 客户端，因为它提供*了*许多功能：

-   支持所有标准 HTTP 动词（GET、POST 等）
-   能够使用所有标准 MIME 类型（JSON、XML、编码格式等）
-   允许我们使用 Java 类并避免复杂的序列化问题的高级 API
-   可使用*ClientHttpRequestInitializer*和*ClientHttpRequestInterceptor*接口进行自定义

### 2.1. 弃用警告

**从 Spring Framework 5 开始，\*RestTemplate\*类正在慢慢被弃用**。虽然它仍然存在于 Spring Framework 6 中，但维护人员明确表示未来不会对此类进行增强。

因为只接受小错误和安全修复，所以鼓励开发人员改用[*WebClient*](https://www.baeldung.com/spring-5-webclient)。此类具有更现代的 API，并支持同步、异步和流式用例。

## 3. *RestTemplate 的*基本使用

*RestTemplate***通过提供具有相应名称的公共方法，**使使用标准 HTTP 动词变得容易。

*例如，要发送 GET 请求，我们可以使用具有getFor*前缀的众多重载方法之一。其他 HTTP 动词也有类似的公共方法，包括 POST、PUT、DELETE、HEAD 和 PATCH。

所有这些方法的结构几乎相同。它们基本上只需要有关要发送的 URL 的信息，以及请求和响应主体的表示。标题等信息是自动为我们创建的。

虽然这些高级方法使编写 HTTP 客户端变得非常容易，但现实世界的行为并不总是像[HTTP 规范](https://www.baeldung.com/cs/rest-vs-http)那样。在某些情况下，**我们可能需要构造一个不完全适合特定动词方法之一的 HTTP 请求**。

这就是为什么*RestTemplate*提供了更通用的带有 grain 方法的方法，我们接下来会看。

## 4. 使用*exchange()*、*postForEntity()*和*execute()*方法

虽然使用顶级特定于动词的方法对于许多用例来说都很好，但有时我们可能需要更多地控制由*RestTemplate*生成的 HTTP 请求。这就是*exchange()*和*execute()*方法派上用场的地方。

让我们考虑一个示例 HTTP POST 请求，它允许我们在图书数据库中创建一个新条目。这是一个 Java 类，它封装了我们的请求主体所需的所有数据：

```java
class Book {
  String title;
  String author;
  int yearPublished;
}复制
```

下面我们将使用三种*RestTemplate*方法中的每一种来发送此请求。

### 4.1. 使用*postForEntity()*方法

发送 POST 请求的第一种也是最简单的方法是使用*postForEntity()*。**此方法只需要 URL 和请求主体并将响应主体解析为\*ResponseEntity\*对象**：

```java
Book book = new Book(
  "Cruising Along with Java",
  "Venkat Subramaniam",
  2023);

 ResponseEntity<Book> response = restTemplate.postForEntity(
  "https://api.bookstore.com", 
  book, 
  Book.class);复制
```

在这个例子中，我们创建了一个新的书籍对象，将它发送到服务器，并将响应解析回另一个书籍对象。值得注意的是，我们只需要提供远程 URL、请求对象和用于响应的类。其他一切，包括 HTTP 标头，都是由*RestTemplate*自动构建的。

### 4.2. 使用*exchange()*方法

发送请求的下一种方式是使用*exchange()*方法：

```java
Book book = new Book(
  "Effective Java",
  "Joshua Bloch",
  2001);

HttpHeaders headers = new HttpHeaders();
headers.setBasicAuth("username", "password");

ResponseEntity<Book> response = restTemplate.exchange(
  "https://api.bookstore.com",
  HttpMethod.POST,
  new HttpEntity<>(book, headers),
  Book.class);复制
```

这里的主要区别在于，我们没有将普通 Java 对象作为请求主体传递，而是使用*HttpEntity*对其进行包装。**这允许我们为请求显式设置额外的 HTTP 标头。**

另一个明显的区别是*exchange()*方法是通用的，这意味着它可以用于任何 HTTP 方法。因此，**URL 之后的第二个参数必须指示使用哪种方法进行请求。**

### 4.3. 使用*execute()*方法

发送 POST 请求的最后一种方法是使用*execute()*方法。这个方法是最通用的，实际上是***RestTemplate\*****中所有其他方法使用的底层方法**。

*以下是如何使用execute()*方法发送 POST 请求的高级示例：

```java
ResponseEntity<Book> response = restTemplate.execute(
    "https://api.bookstore.com",
    HttpMethod.POST,
    new RequestCallback() {
        @Override
        public void doWithRequest(ClientHttpRequest request) throws IOException {
            // manipulate request headers and body
        }
    },
    new ResponseExtractor<ResponseEntity<Book>>() {
        @Override
        public ResponseEntity<Book> extractData(ClientHttpResponse response) throws IOException {
            // manipulate response and return ResponseEntity
        }
    }
);复制
```

值得注意的是，虽然我们仍然必须提供 URL 和 HTTP 方法，但其他一切看起来都大不相同。**这是因为\*execute()\*方法不直接处理请求和响应**。

*相反，它使我们能够分别使用RequestCallback*和*ResponseExtractor*接口创建和修改它们。主要好处是**这使我们能够最大程度地控制请求和响应对象**。另一方面，**我们的代码不够简洁，我们失去了许多其他\*RestTemplate\*方法提供的**自动功能。

还值得注意的是，*RestTemplate确实提供了一个工厂方法来轻松创建**RequestCallback*的实例。该方法是*httpEntityCallback()并且有两种重载形式，可以帮助减少我们在使用**execute()*方法时编写的代码量 ：

```java
Book book = new Book(
  "Reactive Spring",
  "Josh Long",
  2020);
        
RequestCallback requestCallback1 = restTemplate.httpEntityCallback(book);
RequestCallback requestCallback2 = restTemplate.httpEntityCallback(book, Book.class);复制
```

同样，*RestTemplate提供了一个工厂方法来快速创建**ResponseExtractor*的实例：

```java
ResponseExtractor<ResponseEntity<Book>> responseExtractor = restTemplate.responseEntityExtractor(Book.class);复制
```

当然，同时使用这两种工厂方法会抵消使用*execute()*方法的任何好处。如果我们选择同时使用它们，我们最好只使用特定于动词的方法或*exchange()*方法。

## 5.结论

在本文中，我们研究了使用*RestTemplate*发送 HTTP POST 请求的三种不同方式。首先，我们看到了如何使用动词特定的*postForEntity()*方法来创建小而简洁的 HTTP 请求。然后我们研究了两种替代方法，*exchange()*和*execute()*来发送相同的请求。

虽然这三种方法都会产生相同的结果，但它们各有利弊。postForEntity *()*方法产生的代码更少，结果是我们对生成的 HTTP 请求的控制更少。exchange *()*和*execute()*方法为我们提供了对请求的更多控制，但代价是我们的代码更加冗长并且对 Spring 框架的自动功能的依赖程度更低。