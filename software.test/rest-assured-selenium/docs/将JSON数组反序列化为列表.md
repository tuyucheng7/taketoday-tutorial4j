## 将 JSON 数组反序列化为列表

之前我们看到了如何使用***JSONPath在******Response JSON***中进行确定性和准确的搜索。借助此功能，我们能够编写更好的测试验证。

在本章中，我们将重点介绍如何通过 将***JSON Array 反序列化为 List****在 Response JSON*中执行高级搜索。我们还将学习如何在各种 Java 数据结构中表示***Response的搜索部分。***本教程的结构基于***JSONPath*** 类中可用的不同方法。

### ***使用 JSONPath 将 JSON 数组反序列化为字符串列表***

***JsonPath***类有两个重载方法 ( *jsonPath.getList* ) 将*JSON* 节点提取为 Java 列表。以下是方法

![反序列化 JSON 数组](https://toolsqa.com/gallery/Rest%20Assured/1.DeSerialize%20JSON%20Array.png)

-   ***JsonPath.getList** (String) 方法让我们将搜索到的节点作为字符串列表获取*
-   ***JsonPath.getList** (String, Class T) 方法让我们将搜索到的节点作为 T 类型的 List*

为了理解，我们将对 [***https://bookstore.demoqa.com/swagger/#/BookStore/BookStoreV1BooksGet***](https://bookstore.demoqa.com/swagger/#/BookStore/BookStoreV1BooksGet) REST 服务进行 Get 调用。此服务返回一个*JSON* 响应，其中包含书籍集合（书籍*数组*）。集合中的每本书都是一个*JSON* 对象，其中包含描述该书的值。您可以直接在浏览器中打开 URL 来查看输出。

为了理解 ***JsonPath.getList*** ( *String* ) 方法，我们将尝试将所有书籍作为字符串***列表 (List `<String>`) 检索。*** 我们需要做的就是将***“书籍”***作为***JSONPath 提供***。

***下面是代码***

```java
@Test
public void JsonPathUsage() throws MalformedURLException
{
	RestAssured.baseURI = "https://restapi.demoqa.com/utilities/books/getallbooks";
	RequestSpecification httpRequest = RestAssured.given();
	Response response = httpRequest.get("");

	// First get the JsonPath object instance from the Response interface
	JsonPath jsonPathEvaluator = response.jsonPath();

	// Read all the books as a List of String. Each item in the list
	// represent a book node in the REST service Response
	List<String> allBooks = jsonPathEvaluator.getList("books.title");

	// Iterate over the list and print individual book item
	for(String book : allBooks)
	{
		System.out.println("Book: " + book);
	}
}
```

运行此命令后，输出将如下所示：

```java
Book: Eloquent JavaScript, Second Edition
Book: Learning JavaScript Design Patterns
Book: Speaking JavaScript
Book: Programming JavaScript Applications
Book: Understanding ECMAScript 6
Book: You Don't Know JS
Book: Git Pocket Guide
Book: Designing Evolvable Web APIs with ASP.NET
```

### ***使用 JSONPath 将 JSON 数组反序列化为类（T 型）对象列表***

我们都熟悉*Rest-Assured*中内置的***序列化***和***反序列化***支持。让我们尝试将搜索到的节点直接转换为对象表示。*在此示例中，让我们从JSON* 响应中检索所有书籍。我们将检索所有书籍作为书籍列表类。为此，我们将首先创建***Book 的类表示***。获取*JSON Book*实体的所有属性并使用这些成员变量创建一个类。一本书可以简单地用***POJO***类表示，如下面的代码所示。

```java
public class Book {

    String isbn;
    String title;
    String subtitle;
    String author;
    String published;
    String publisher;
    int pages;
    String description;
    String website;
}
```

现在要将响应转换为书籍列表，我们将简单地使用***JsonPath.getList*** ( *String, Class T* ) 方法。

-   *在这种情况下，第一个参数是***JSONPath（“books”）****
-   *第二个参数是我们希望将响应转换为的类型名称。在这种情况下**预订课程。***

下面是显示用法的完整代码

```java
@Test
public void JsonPathUsage() throws MalformedURLException
{
	RestAssured.baseURI = "https://restapi.demoqa.com/utilities/books/getallbooks";
	RequestSpecification httpRequest = RestAssured.given();
	Response response = httpRequest.get("");

	// First get the JsonPath object instance from the Response interface
	JsonPath jsonPathEvaluator = response.jsonPath();

	// Read all the books as a List of String. Each item in the list
	// represent a book node in the REST service Response
	List<Book> allBooks = jsonPathEvaluator.getList("books", Book.class);

	// Iterate over the list and print individual book item
	// Note that every book entry in the list will be complete Json object of book
	for(Book book : allBooks)
	{
		System.out.println("Book: " + book.title);
	}
}
```

这是 Rest-Assured 的一个非常好的特性，它使我们能够获得响应的目标部分。