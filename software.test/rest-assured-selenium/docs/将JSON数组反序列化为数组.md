## 将 JSON 数组反序列化为数组

本教程进一步建立在我们对将***JSON 响应******反序列***化为给定类型的对象的理解之上。请在下面的教程中了解***序列化和反序列化的基础知识***

-   [***将 JSON 数据反序列化为 POJO 类对象***](https://toolsqa.com/rest-assured/deserialize-json-response/)
-   [***将 JSON 数组反序列化为列表***](https://toolsqa.com/rest-assured/deserialize-json-array/)

我们将以*演示 REST API 端点*为例： [***https***](https://bookstore.demoqa.com/swagger/#/BookStore/BookStoreV1BooksGet) ://bookstore.demoqa.com/swagger/#/BookStore/BookStoreV1BooksGet如果您点击此端点，您将在 Json 响应中获得书籍**集合**。以下是回复：

```java
{
    "books": [
        {
            "isbn": "9781593275846",
            "title": "Eloquent JavaScript, Second Edition",
            "subtitle": "A Modern Introduction to Programming",
            "author": "Marijn Haverbeke",
            "published": "2014-12-14T00:00:00.000Z",
            "publisher": "No Starch Press",
            "pages": 472,
            "description": "JavaScript lies at the heart of almost every modern web application, from social apps to the newest browser-based games. Though simple for beginners to pick up and play with, JavaScript is a flexible, complex language that you can use to build full-scale applications.",
            "website": "http:\/\/eloquentjavascript.net\/"
        },
        {
            "isbn": "9781449331818",
            "title": "Learning JavaScript Design Patterns",
            "subtitle": "A JavaScript and jQuery Developer's Guide",
            "author": "Addy Osmani",
            "published": "2012-07-01T00:00:00.000Z",
            "publisher": "O'Reilly Media",
            "pages": 254,
            "description": "With Learning JavaScript Design Patterns, you'll learn how to write beautiful, structured, and maintainable JavaScript by applying classical and modern design patterns to the language. If you want to keep your code efficient, more manageable, and up-to-date with the latest best practices, this book is for you.",
            "website": "http:\/\/www.addyosmani.com\/resources\/essentialjsdesignpatterns\/book\/"
        },
        {
            "isbn": "9781449365035",
            "title": "Speaking JavaScript",
            "subtitle": "An In-Depth Guide for Programmers",
            "author": "Axel Rauschmayer",
            "published": "2014-02-01T00:00:00.000Z",
            "publisher": "O'Reilly Media",
            "pages": 460,
            "description": "Like it or not, JavaScript is everywhere these days-from browser to server to mobile-and now you, too, need to learn the language or dive deeper than you have. This concise book guides you into and through JavaScript, written by a veteran programmer who once found himself in the same position.",
            "website": "http:\/\/speakingjs.com\/"
        },
        {
            "isbn": "9781491950296",
            "title": "Programming JavaScript Applications",
            "subtitle": "Robust Web Architecture with Node, HTML5, and Modern JS Libraries",
            "author": "Eric Elliott",
            "published": "2014-07-01T00:00:00.000Z",
            "publisher": "O'Reilly Media",
            "pages": 254,
            "description": "Take advantage of JavaScript's power to build robust web-scale or enterprise applications that are easy to extend and maintain. By applying the design patterns outlined in this practical book, experienced JavaScript developers will learn how to write flexible and resilient code that's easier-yes, easier-to work with as your code base grows.",
            "website": "http:\/\/chimera.labs.oreilly.com\/books\/1234000000262\/index.html"
        },
        {
            "isbn": "9781593277574",
            "title": "Understanding ECMAScript 6",
            "subtitle": "The Definitive Guide for JavaScript Developers",
            "author": "Nicholas C. Zakas",
            "published": "2016-09-03T00:00:00.000Z",
            "publisher": "No Starch Press",
            "pages": 352,
            "description": "ECMAScript 6 represents the biggest update to the core of JavaScript in the history of the language. In Understanding ECMAScript 6, expert developer Nicholas C. Zakas provides a complete guide to the object types, syntax, and other exciting changes that ECMAScript 6 brings to JavaScript.",
            "website": "https:\/\/leanpub.com\/understandinges6\/read"
        },
        {
            "isbn": "9781491904244",
            "title": "You Don't Know JS",
            "subtitle": "ES6 & Beyond",
            "author": "Kyle Simpson",
            "published": "2015-12-27T00:00:00.000Z",
            "publisher": "O'Reilly Media",
            "pages": 278,
            "description": "No matter how much experience you have with JavaScript, odds are you don\u2019t fully understand the language. As part of the 'You Don\u2019t Know JS' series, this compact guide focuses on new features available in ECMAScript 6 (ES6), the latest version of the standard upon which JavaScript is built.",
            "website": "https:\/\/github.com\/getify\/You-Dont-Know-JS\/tree\/master\/es6%20&%20beyond"
        },
        {
            "isbn": "9781449325862",
            "title": "Git Pocket Guide",
            "subtitle": "A Working Introduction",
            "author": "Richard E. Silverman",
            "published": "2013-08-02T00:00:00.000Z",
            "publisher": "O'Reilly Media",
            "pages": 234,
            "description": "This pocket guide is the perfect on-the-job companion to Git, the distributed version control system. It provides a compact, readable introduction to Git for new users, as well as a reference to common commands and procedures for those of you with Git experience.",
            "website": "http:\/\/chimera.labs.oreilly.com\/books\/1230000000561\/index.html"
        },
        {
            "isbn": "9781449337711",
            "title": "Designing Evolvable Web APIs with ASP.NET",
            "subtitle": "Harnessing the Power of the Web",
            "author": "Glenn Block, et al.",
            "published": "2014-04-07T00:00:00.000Z",
            "publisher": "O'Reilly Media",
            "pages": 538,
            "description": "Design and build Web APIs for a broad range of clients\u2014including browsers and mobile devices\u2014that can adapt to change over time. This practical, hands-on guide takes you through the theory and tools you need to build evolvable HTTP services with Microsoft\u2019s ASP.NET Web API framework. In the process, you\u2019ll learn how design and implement a real-world Web API.",
            "website": "http:\/\/chimera.labs.oreilly.com\/books\/1234000001708\/index.html"
        }
    ]
}
```

此***JSON 响应***包含***Books 数组***。*JSON 数组*中的每一项都代表一本书，并具有诸如“ ***isbn*** ”、“ ***title”***、“ ***author*** ”等书籍的属性。在前面的教程中，我们学习了如何将***单节点的 JSON 响应反序列化为 Class 的实例***。然而，在将节点***集合反序列***化为数组时变得有点棘手。让我们看看 Rest Assured 如何帮助我们快速实现这一目标，而无需编写任何样板代码。

### ***使用 Rest Assured 的 JSONPath 将 JSON 数组反序列化为数组？***

同样，我们可以将***Json Array 转换为 Java Array。JsonPath***类具有称为***getObject***的方法。此方法可用于将响应直接转换为***Java Array of Book***。我们唯一需要做的就是将***Book[].class***作为第二个参数传递给该方法，以表示我们希望将 Json 反序列化为 Book 数组。这是执行此操作的代码

```java
@Test
public void JsonArrayToArray()
{

	RestAssured.baseURI = "https://restapi.demoqa.com/utilities/books/getallbooks";
	RequestSpecification request = RestAssured.given();

	Response response = request.get();
	System.out.println("Response Body -> " + response.body().asString());

	// We can convert the Json Response directly into a Java Array by using
	// JsonPath.getObject method. Here we have to specify that we want to
	// deserialize the Json into an Array of Book. This can be done by specifying
	// Book[].class as the second argument to the getObject method.
	Book[] books = response.jsonPath().getObject("books",Book[].class );

	for(Book book : books)
	{
		System.out.println("Book title " + book.title);
	}
}
```

以上两种技术对于编写***简洁***的 测试代码很重要。除了使用上述技术编写简洁的测试外，我们还充分利用了 Rest-Assured 提供给我们的功能。这有助于我们在 Rest Assured 之外减少依赖，也使我们能够编写可靠的测试。