***在 JsonPath 中使用表达式***是一个非常好的特性，可以让*JsonPath 简洁而复杂。JsonPath*中的表达式基本上是评估为布尔值的代码片段。根据结果，仅选择符合标准的节点。让我们进一步了解它，但在此之前，请确保您已完成以下有关*Json*和*JsonPath基础知识的教程*

-   [***json***](https://toolsqa.com/rest-assured/what-is-json/)
-   [***jsonpath***](https://toolsqa.com/rest-assured/jsonpath-and-query-json-using-jsonpath/)

在本教程中，我们将使用一个示例 Json，它在 Array 中有一些项目。[***请在我们的JsonPath Evaluator***](https://jsonpath.com/)中复制以下 Json

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
      "website": "https://eloquentjavascript.net/"
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
      "website": "https://www.addyosmani.com/resources/essentialjsdesignpatterns/book/"
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
      "website": "https://speakingjs.com/"
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
      "website": "https://chimera.labs.oreilly.com/books/1234000000262/index.html"
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
      "website": "https://leanpub.com/understandinges6/read"
    },
    {
      "isbn": "9781491904244",
      "title": "You Don't Know JS",
      "subtitle": "ES6 & Beyond",
      "author": "Kyle Simpson",
      "published": "2015-12-27T00:00:00.000Z",
      "publisher": "O'Reilly Media",
      "pages": 278,
      "description": "No matter how much experience you have with JavaScript, odds are you don’t fully understand the language. As part of the 'You Don’t Know JS' series, this compact guide focuses on new features available in ECMAScript 6 (ES6), the latest version of the standard upon which JavaScript is built.",
      "website": "https://github.com/getify/You-Dont-Know-JS/tree/master/es6%20&%20beyond"
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
      "website": "https://chimera.labs.oreilly.com/books/1230000000561/index.html"
    },
    {
      "isbn": "9781449337711",
      "title": "Designing Evolvable Web APIs with ASP.NET",
      "subtitle": "Harnessing the Power of the Web",
      "author": "Glenn Block, et al.",
      "published": "2014-04-07T00:00:00.000Z",
      "publisher": "O'Reilly Media",
      "pages": 538,
      "description": "Design and build Web APIs for a broad range of clients—including browsers and mobile devices—that can adapt to change over time. This practical, hands-on guide takes you through the theory and tools you need to build evolvable HTTP services with Microsoft’s ASP.NET Web API framework. In the process, you’ll learn how design and implement a real-world Web API.",
      "website": "https://chimera.labs.oreilly.com/books/1234000001708/index.html"
    }
  ]
}
```

## JsonPath 中的表达式

***JsonPath 中的表达式是JsonPath***最强大的功能之一。请注意，表达式在***Xpath***和***CSS 选择器***中也可用。表达式可帮助您创建评估为真或假的条件。***在JsonPath***中创建表达式之前，您必须了解两个重要符号。

-   *? : 问号，表示表达式的开始。使用的语法**[? （表达）]***
-   *@ ：在符号处，表示当前正在处理的节点。使用的语法**$.books[?(@.price > 100)]***

现在让我们从上面的 Json 中执行一个简单的任务。

-   ***找出所有页数大于 460 的书***

要创建一个 JsonPath 可以获取所有页面大于 460 的书籍，我们必须将问题分为两部分

1.  ***创建一个 JsonPath 来检索所有书籍***
2.  ***附加表达式以过滤所有页面大于 460 的书籍***

要获取所有书籍，我们可以创建一个简单的***JsonPath：$*** .books 。现在我们必须在数组books 中添加一个表达式。为此，我们将简单地以 ***?*** 符号开始一个表达式，然后在当前节点***@***上添加一个过滤器 。一个简单的表达式看起来像这样***?(@.pages > 460)***。

如果我们将 JsonPath 与表达式结合起来，我们将得到： ***$.books[?(@.pages > 460)]***

在[***JsonPath Evaluator***](https://jsonpath.com/)中，只需输入此表达式并查看结果。如下图所示

![JsonPath_0.png 中的表达式](https://toolsqa.com/gallery/Rest%20Assured/1.Expressions%20in%20JsonPath_0.png)

结果将是所有页码大于 460 的书。这是结果 Json

```java
[
  {
    "isbn": "9781593275846",
    "title": "Eloquent JavaScript, Second Edition",
    "subtitle": "A Modern Introduction to Programming",
    "author": "Marijn Haverbeke",
    "published": "2014-12-14T00:00:00.000Z",
    "publisher": "No Starch Press",
    "pages": 472,
    "description": "JavaScript lies at the heart of almost every modern web application, from social apps to the newest browser-based games. Though simple for beginners to pick up and play with, JavaScript is a flexible, complex language that you can use to build full-scale applications.",
    "website": "https://eloquentjavascript.net/"
  },
  {
    "isbn": "9781449337711",
    "title": "Designing Evolvable Web APIs with ASP.NET",
    "subtitle": "Harnessing the Power of the Web",
    "author": "Glenn Block, et al.",
    "published": "2014-04-07T00:00:00.000Z",
    "publisher": "O'Reilly Media",
    "pages": 538,
    "description": "Design and build Web APIs for a broad range of clients—including browsers and mobile devices—that can adapt to change over time. This practical, hands-on guide takes you through the theory and tools you need to build evolvable HTTP services with Microsoft’s ASP.NET Web API framework. In the process, you’ll learn how design and implement a real-world Web API.",
    "website": "https://chimera.labs.oreilly.com/books/1234000001708/index.html"
  }
]
```

### ***JsonPath 中的逻辑运算符***

就像任何编程语言一样，***JsonPath***支持所有逻辑运算符。下面是我们可以用来创建表达式的逻辑运算符列表。下面详细讨论每个逻辑运算符。

| ***操作员*** | ***描述***                               |
| ------------ | ---------------------------------------- |
| *==*         | *left 等于 right（注意 1 不等于 '1'）。* |
| *!=*         | *左不等于右。*                           |
| *<*          | *左小于右。*                             |
| *<=*         | *左小于或等于右。*                       |
| *>*          | *左大于右。*                             |
| *>=*         | *left 大于或等于 right。*                |

尝试以上所有示例，并尝试根据您的需要创建更多表达式。通过这种方式，您将了解有关***JsonPath***表达式的更多信息。

### ***等于 JsonPath 中的 (==) 运算符***

顾名思义，操作员检查左侧是否等于右侧。让我们找出所有有 352 页的书。这是 JsonPath

***JsonPath: $.books[?(@.pages == 352)]***

***结果将是：***

```java
[
  {
    "isbn": "9781593277574",
    "title": "Understanding ECMAScript 6",
    "subtitle": "The Definitive Guide for JavaScript Developers",
    "author": "Nicholas C. Zakas",
    "published": "2016-09-03T00:00:00.000Z",
    "publisher": "No Starch Press",
    "pages": 352,
    "description": "ECMAScript 6 represents the biggest update to the core of JavaScript in the history of the language. In Understanding ECMAScript 6, expert developer Nicholas C. Zakas provides a complete guide to the object types, syntax, and other exciting changes that ECMAScript 6 brings to JavaScript.",
    "website": "https://leanpub.com/understandinges6/read"
  }
]
```

### ***不等于 JsonPath 中的 (!=) 运算符***

当我们想根据条件***排除***一组特定的值时，我们使用不等于运算符。让我们把上面的例子倒过来，找出所有***页码不等于 352***的书。

***JsonPath: $.books[?(@.pages != 352)]***

***结果将是：***

```java
[
  {
    "isbn": "9781593275846",
    "title": "Eloquent JavaScript, Second Edition",
    "subtitle": "A Modern Introduction to Programming",
    "author": "Marijn Haverbeke",
    "published": "2014-12-14T00:00:00.000Z",
    "publisher": "No Starch Press",
    "pages": 472,
    "description": "JavaScript lies at the heart of almost every modern web application, from social apps to the newest browser-based games. Though simple for beginners to pick up and play with, JavaScript is a flexible, complex language that you can use to build full-scale applications.",
    "website": "https://eloquentjavascript.net/"
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
    "website": "https://www.addyosmani.com/resources/essentialjsdesignpatterns/book/"
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
    "website": "https://speakingjs.com/"
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
    "website": "https://chimera.labs.oreilly.com/books/1234000000262/index.html"
  },
  {
    "isbn": "9781491904244",
    "title": "You Don't Know JS",
    "subtitle": "ES6 & Beyond",
    "author": "Kyle Simpson",
    "published": "2015-12-27T00:00:00.000Z",
    "publisher": "O'Reilly Media",
    "pages": 278,
    "description": "No matter how much experience you have with JavaScript, odds are you don’t fully understand the language. As part of the 'You Don’t Know JS' series, this compact guide focuses on new features available in ECMAScript 6 (ES6), the latest version of the standard upon which JavaScript is built.",
    "website": "https://github.com/getify/You-Dont-Know-JS/tree/master/es6+&+beyond"
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
    "website": "https://chimera.labs.oreilly.com/books/1230000000561/index.html"
  },
  {
    "isbn": "9781449337711",
    "title": "Designing Evolvable Web APIs with ASP.NET",
    "subtitle": "Harnessing the Power of the Web",
    "author": "Glenn Block, et al.",
    "published": "2014-04-07T00:00:00.000Z",
    "publisher": "O'Reilly Media",
    "pages": 538,
    "description": "Design and build Web APIs for a broad range of clients—including browsers and mobile devices—that can adapt to change over time. This practical, hands-on guide takes you through the theory and tools you need to build evolvable HTTP services with Microsoft’s ASP.NET Web API framework. In the process, you’ll learn how design and implement a real-world Web API.",
    "website": "https://chimera.labs.oreilly.com/books/1234000001708/index.html"
  }
]
```

### ***JsonPath 中的小于 ( < ) 运算符***

小于运算符，顾名思义，将返回所有小于右边给定值的值。让我们找出所有小于 352 页的书。

***JsonPath: $.books[?(@.pages < 352)]***

***结果将是：***

```java
[
  {
    "isbn": "9781449331818",
    "title": "Learning JavaScript Design Patterns",
    "subtitle": "A JavaScript and jQuery Developer's Guide",
    "author": "Addy Osmani",
    "published": "2012-07-01T00:00:00.000Z",
    "publisher": "O'Reilly Media",
    "pages": 254,
    "description": "With Learning JavaScript Design Patterns, you'll learn how to write beautiful, structured, and maintainable JavaScript by applying classical and modern design patterns to the language. If you want to keep your code efficient, more manageable, and up-to-date with the latest best practices, this book is for you.",
    "website": "https://www.addyosmani.com/resources/essentialjsdesignpatterns/book/"
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
    "website": "https://chimera.labs.oreilly.com/books/1234000000262/index.html"
  },
  {
    "isbn": "9781491904244",
    "title": "You Don't Know JS",
    "subtitle": "ES6 & Beyond",
    "author": "Kyle Simpson",
    "published": "2015-12-27T00:00:00.000Z",
    "publisher": "O'Reilly Media",
    "pages": 278,
    "description": "No matter how much experience you have with JavaScript, odds are you don’t fully understand the language. As part of the 'You Don’t Know JS' series, this compact guide focuses on new features available in ECMAScript 6 (ES6), the latest version of the standard upon which JavaScript is built.",
    "website": "https://github.com/getify/You-Dont-Know-JS/tree/master/es6+&+beyond"
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
    "website": "https://chimera.labs.oreilly.com/books/1230000000561/index.html"
  }
]
```

### ***JsonPath 中的小于等于 ( <= ) 运算符***

该运算符将让您获得小于或等于给定值的所有值。让我们找出所有页数小于或等于 352 的书。

***JsonPath: $.books[?(@.pages < 352)]***

***结果将是：***

```java
[
  {
    "isbn": "9781449331818",
    "title": "Learning JavaScript Design Patterns",
    "subtitle": "A JavaScript and jQuery Developer's Guide",
    "author": "Addy Osmani",
    "published": "2012-07-01T00:00:00.000Z",
    "publisher": "O'Reilly Media",
    "pages": 254,
    "description": "With Learning JavaScript Design Patterns, you'll learn how to write beautiful, structured, and maintainable JavaScript by applying classical and modern design patterns to the language. If you want to keep your code efficient, more manageable, and up-to-date with the latest best practices, this book is for you.",
    "website": "https://www.addyosmani.com/resources/essentialjsdesignpatterns/book/"
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
    "website": "https://chimera.labs.oreilly.com/books/1234000000262/index.html"
  },
  {
    "isbn": "9781491904244",
    "title": "You Don't Know JS",
    "subtitle": "ES6 & Beyond",
    "author": "Kyle Simpson",
    "published": "2015-12-27T00:00:00.000Z",
    "publisher": "O'Reilly Media",
    "pages": 278,
    "description": "No matter how much experience you have with JavaScript, odds are you don’t fully understand the language. As part of the 'You Don’t Know JS' series, this compact guide focuses on new features available in ECMAScript 6 (ES6), the latest version of the standard upon which JavaScript is built.",
    "website": "https://github.com/getify/You-Dont-Know-JS/tree/master/es6+&+beyond"
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
    "website": "https://chimera.labs.oreilly.com/books/1230000000561/index.html"
  }
]
```

### ***JsonPath 中的大于 ( > ) 运算符***

大于运算符将让您获得所有大于左侧值的值。让我们找出所有超过 460 页的书。

***JsonPath: $.books[?(@.pages > 460)]***

***结果将是：***

```java
[
  {
    "isbn": "9781593275846",
    "title": "Eloquent JavaScript, Second Edition",
    "subtitle": "A Modern Introduction to Programming",
    "author": "Marijn Haverbeke",
    "published": "2014-12-14T00:00:00.000Z",
    "publisher": "No Starch Press",
    "pages": 472,
    "description": "JavaScript lies at the heart of almost every modern web application, from social apps to the newest browser-based games. Though simple for beginners to pick up and play with, JavaScript is a flexible, complex language that you can use to build full-scale applications.",
    "website": "https://eloquentjavascript.net/"
  },
  {
    "isbn": "9781449337711",
    "title": "Designing Evolvable Web APIs with ASP.NET",
    "subtitle": "Harnessing the Power of the Web",
    "author": "Glenn Block, et al.",
    "published": "2014-04-07T00:00:00.000Z",
    "publisher": "O'Reilly Media",
    "pages": 538,
    "description": "Design and build Web APIs for a broad range of clients—including browsers and mobile devices—that can adapt to change over time. This practical, hands-on guide takes you through the theory and tools you need to build evolvable HTTP services with Microsoft’s ASP.NET Web API framework. In the process, you’ll learn how design and implement a real-world Web API.",
    "website": "https://chimera.labs.oreilly.com/books/1234000001708/index.html"
  }
]
```

### ***JsonPath 中的大于等于 ( >= ) 运算符***

大于等于将让您获得所有等于或大于右侧值的值。让我们获取所有页面大于或等于 460 的书

***JsonPath: $.books[?(@.pages >= 460)]***

***结果将是：***

```java
[
   {
      "isbn" : "9781593275846",
      "title" : "Eloquent JavaScript, Second Edition",
      "subtitle" : "A Modern Introduction to Programming",
      "author" : "Marijn Haverbeke",
      "published" : "2014-12-14T00:00:00.000Z",
      "publisher" : "No Starch Press",
      "pages" : 472,
      "description" : "JavaScript lies at the heart of almost every modern web application, from social apps to the newest browser-based games. Though simple for beginners to pick up and play with, JavaScript is a flexible, complex language that you can use to build full-scale applications.",
      "website" : "https://eloquentjavascript.net/"
   },
   {
      "isbn" : "9781449365035",
      "title" : "Speaking JavaScript",
      "subtitle" : "An In-Depth Guide for Programmers",
      "author" : "Axel Rauschmayer",
      "published" : "2014-02-01T00:00:00.000Z",
      "publisher" : "O'Reilly Media",
      "pages" : 460,
      "description" : "Like it or not, JavaScript is everywhere these days-from browser to server to mobile-and now you, too, need to learn the language or dive deeper than you have. This concise book guides you into and through JavaScript, written by a veteran programmer who once found himself in the same position.",
      "website" : "https://speakingjs.com/"
   },
   {
      "isbn" : "9781449337711",
      "title" : "Designing Evolvable Web APIs with ASP.NET",
      "subtitle" : "Harnessing the Power of the Web",
      "author" : "Glenn Block, et al.",
      "published" : "2014-04-07T00:00:00.000Z",
      "publisher" : "O'Reilly Media",
      "pages" : 538,
      "description" : "Design and build Web APIs for a broad range of clients\u2014including browsers and mobile devices\u2014that can adapt to change over time. This practical, hands-on guide takes you through the theory and tools you need to build evolvable HTTP services with Microsoft\u2019s ASP.NET Web API framework. In the process, you\u2019ll learn how design and implement a real-world Web API.",
      "website" : "https://chimera.labs.oreilly.com/books/1234000001708/index.html"
   }
]
```

在接下来的章节中，我们将在***Rest-Assured中使用******JsonPath*** ，看看我们如何编写有效的验证。