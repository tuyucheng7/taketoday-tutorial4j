## JSONPath 和使用 JSONPath 查询 JSON

*JSON*最重要的优点之一是它是一种轻量级格式，可用于在计算机和进程之间交换数据。*JSON*与 XML 一样，是一种为数据提供结构的格式。*如果您对JSON*不熟悉，请先阅读[***JSON 简介的***](https://toolsqa.com/rest-assured/what-is-json/)本教程。

在本教程中，我们将了解有关***JSONPath 和使用 JSONPath 查询 JSON 的更多信息。***我们将讨论以下主题：

-   ***什么是 JSONPath？***
-   ***如何使用 JSONPath 查询 JSON？***
-   ***查询 JSON 对象和 JSON 数组的不同策略。***

### ***什么是 JSONPath？***

每个***JSON***对象都由固有的层次结构和结构组成。每个*JSON*最终都会创建一个节点树，其中每个节点都是一个***JSON 元素***。让我们在这里举个例子，下面是一个简单的*JSON*表示一个国家的集合

```java
{
  "Description": "Map containing Country, Capital, Currency, and some States of that Country",
  "Region": "Asia",
  "Countries": [
    {
      "Country": "India",
      "Data": {
        "Capital": "New Delhi",
        "mintemp": 6,
        "maxtemp": 45,
        "Currency": "Rupee"
      }
    },
    {
      "Country": "Nepal",
      "Data": {
        "Capital": "Katmandu",
        "mintemp": 9,
        "maxtemp": 23,
        "Currency": "Nepalese rupee"
      }
    }
  ]
}
```

在最顶层，我们有一个***Root***节点，它基本上是包含所有当前*JSON 的节点。*在这个根节点内，我们有以下节点

-   ***描述***
-   ***地区***
-   ***国家***

***描述***和***区域*** 是树中的简单叶节点。但是***Country***是一个非叶节点，它进一步包含更多的节点。这里***国家***节点包含两个国家的数组。***如果我们要简单地定义Root***节点和*JSON*中的任何节点之间的层次关系，我们可以执行如下所示的操作。

***注意：**让我们用**$表示****根节点，并用****>>**表示父子关系*

***描述***节点将由***$>> Description***表示。

***Region***节点将由***$>> Region***表示。

***同样，我们也可以定义Root节点与******国家***数组中第零项之间的关系。关系将是   ***$ >> countries[0]*** 其中**[ ]**是索引运算符，用于表示国家数组中 n 索引处的项目***。***

在某种程度上，*JSON*中的这种层次结构允许我们创建一个标准机制来遍历*JSON 的特定部分。*执行此操作的标准方法称为***JSONPath***。

### ***如何使用 JSONPath 查询 JSON？***

***JSONPath***创建统一的标准和语法来定义*JSON*文档的不同部分。*JSONPath*定义表达式以遍历*JSON*文档以到达 JSON 的子集*。*通过实际操作可以最好地理解该主题。我们创建了一个网页，可以帮助您评估*JSONPath。*使用此页面练习编写*JSONPath。*这是[***JSONPath 评估器的链接。***](https://jsonpath.com/)

#### ***在 JsonPath 中获取根节点运算符***

***JSON中的******根节点***运算符由***$***符号表示。**$** 将返回*JSON*文档中的所有节点。要尝试此操作，请打开[***JSONPath 评估器***](https://jsonpath.com/)页面并在*JSONPath*字段**中键入$ 。**如下图所示

![JSONPath 和使用 JSONPath 查询 JSON](https://toolsqa.com/gallery/Rest%20Assured/1.JSONPath%20and%20Query%20JSON%20using%20JSONPath.png)

#### ***在 JSONPath 中获取子运算符***

为了获取给定节点的子节点，我们可以使用***点 (.)***运算符或***['childname']***运算符。为了获得所有国家，我们可以将*JSONPath*作为

-   ***$.Countries***
-   ***$['国家']***

输出将是

```java
[
  [
    {
      "Country": "India",
      "Data": {
        "Capital": "New Delhi",
        "mintemp": 6,
        "maxtemp": 45,
        "Currency": "Rupee"
      }
    },
    {
      "Country": "Nepal",
      "Data": {
        "Capital": "Katmandu",
        "mintemp": 9,
        "maxtemp": 23,
        "Currency": "Nepalese rupee"
      }
    }
  ]
]
```

#### ***JSONPath 中的通配符运算符***

***JSONPath***中的通配符运算符是（*星号或星号*）符号。这实际上意味着该节点下的所有内容。例如，如果您想显示所有国家的数据节点，您可以简单地编写以下 *JSONPat*

-   ***$.Countries[\* ].Data** *
-   ***$['Countries'][\* ].Data** *

这将显示所有国家的数据节点

```java
[
  {
    "Capital": "New Delhi",
    "mintemp": 6,
    "maxtemp": 45,
    "Currency": "Rupee"
  },
  {
    "Capital": "Katmandu",
    "mintemp": 9,
    "maxtemp": 23,
    "Currency": "Nepalese rupee"
  }
]
```

#### ***JSONPath 中的数组索引运算符***

有时需要访问*JSON*数组中给定索引处的特定条目。我们可以使用数组索引***[i,j,k...]*** 来识别特定索引处的条目。在上面的示例中，让我们找出 Country 数组中的最后***一个***Country***条目***。

-   ***$.Countries[-1]***
-   ***$['国家'][-1]***

这里 -1 代表数组中的最后一项。您还可以通过为索引提供正值来引用最后一项。例如

-   ***$.Countries[1]***
-   ***$['国家'][1]***

输出将如下所示

```java
[
  {
    "Country": "Nepal",
    "Data": {
      "Capital": "Katmandu",
      "mintemp": 9,
      "maxtemp": 23,
      "Currency": "Nepalese rupee"
    }
  }
]
```

***注意：**数组索引从 0 开始。因此要引用数组中的第二项，我们必须使用 1 作为索引。*

数组索引不仅限于仅显示一项。我们可以从不同索引的数组中提取多个项目。这样做的语法是***[i,j,k..]。*** 例如，要提取前 2 个数组项，我们将*JSONPath*编写为

-   ***$.Countries[0,1]***
-   ***$['国家'][0,1]***

输出将是

```java
[
  {
    "Country": "India",
    "Data": {
      "Capital": "New Delhi",
      "mintemp": 6,
      "maxtemp": 45,
      "Currency": "Rupee"
    }
  },
  {
    "Country": "Nepal",
    "Data": {
      "Capital": "Katmandu",
      "mintemp": 9,
      "maxtemp": 23,
      "Currency": "Nepalese rupee"
    }
  }
]
```

对于下面显示的*JsonPath*，我们将更改*Json*以具有更多节点。让我们看一下代表书籍*收藏的**Json 。*这是*Json*，从现在开始在[***JsonPath Evaluator***](https://jsonpath.com/)*中使用这个Json*

```java
{
  "store": {
    "book": [
      {
        "category": "reference",
        "author": "Nigel Rees",
        "title": "Sayings of the Century",
        "price": 8.95
      },
      {
        "category": "fiction",
        "author": "Evelyn Waugh",
        "title": "Sword of Honour",
        "price": 12.99
      },
      {
        "category": "fiction",
        "author": "Herman Melville",
        "title": "Moby Dick",
        "isbn": "0-553-21311-3",
        "price": 8.99
      },
      {
        "category": "fiction",
        "author": "J. R. R. Tolkien",
        "title": "The Lord of the Rings",
        "isbn": "0-395-19395-8",
        "price": 22.99
      }
    ],
    "bicycle": {
      "color": "red",
      "price": 19.95
    }
  },
  "expensive": 10
}
```

#### ***JsonPath 中的数组切片运算符***

*数组切片运算符是从Json*中提取所选项目的绝妙运算符。以书籍为例，如果我们想检索*Json*中的所有替代书籍怎么办。为此，我们将需要 Array、Slice 运算符。Array Slice 运算符的语法是**[StartIndex : EndIndex : Steps]。**让我们找出书籍收藏中的奇数书籍是什么。JsonPath 看起来像

-   ***$..book[1,4,2]***
-   ***$..['书'][1,4,2]***

上述 JsonPath 的输出将是

```java
[
  {
    "category": "fiction",
    "author": "Evelyn Waugh",
    "title": "Sword of Honour",
    "price": 12.99
  },
  {
    "category": "fiction",
    "author": "Herman Melville",
    "title": "Moby Dick",
    "isbn": "0-553-21311-3",
    "price": 8.99
  }
]
```