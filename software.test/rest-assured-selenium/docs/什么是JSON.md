## 什么是 JSON？

***JSON***代表***JavaScript 对象表示法***。JSON 是一种人类和机器可读的格式，用于将数据表示为***结构化数据***。JSON 主要用于将数据从一台计算机传输到另一台计算机，甚至在同一台计算机上的不同程序之间传输。

为了可视化 JSON，假设我们想在 JSON 中表示一个具有以下详细信息的***人***

-   *名字 = Virender*
-   *姓氏 = 辛格*
-   *年龄 = 34*
-   *职业 = 工程师*

在 JSON 中，这将变成

```java
{
 "FirstName" : "Virender",
 "LastName"  : "Singh",
 "Age"       : 34,
 "Profession": "Engineer"
}
```

在这一点上，我们不知道*JSON*结构的细节，但我们仍然可以弄清楚*JSON*中呈现的是什么。现在让我们了解*JSON*中使用的不同元素。

***注意**：在名称中不带空格来表示键总是有益的*。

### ***键值对***

***JSON 中的键值***对用于表示对象的属性。在上面的例子中，我们试图表示一个***Person***。这个人有一些属性，比如

-   *名*
-   *姓*
-   *年龄*
-   *职业*

这些属性中的每一个都有一个与之关联的值。例如***First Name***的值为***Virender***。同样，***Age***的值为***34***。*要在JSON*中编写 Key-Value，我们必须遵循这些规则

-   *键值对由 :（冒号）分隔*
-   *键总是出现在**双引号“”中***
-   *值可以是任何值，具体取决于数据类型*

从上面的示例中，键值对是***FirstName***。尝试自己寻找其他键值对

```java
"FirstName" : "Virender"
```

值可以是以下数据类型

-   *布尔值：真或假*
-   *数字：数值*
-   *对象：键值对的关联数组*
-   *数组：仅值的关联数组*

### ***JSON 中的对象***

在 JSON 中，对象由键值对的集合表示。这个键值对的集合使用 { } （*打开和关闭花括号*} 进行分组。编写对象的规则是

-   *键值对应以 ,（逗号）分隔*
-   *每个对象都应该以一个**开头****{**（打开花括号）*
-   每个对象都应该以**结束*****}**结束（结束花括号）*

这里的一个例子是上面讨论的 Person 对象。Person 对象遵循用于表示对象的规则

```java
{
 "FirstName" : "Virender",
 "LastName"  : "Singh",
 "Age"       : 34,
 "Profession": "Engineer"
}
```

### ***JSON 格式的数组***

数组类似于您从任何其他编程语言中知道的数组。在 JSON 中，数组是由逗号分隔的值的集合。以下是编写数组的规则

-   ***数组以[**（括号）开头*  
-   *数组以结束**]**（方括号）结尾*
-   *数组中的值由**,**（逗号）分隔*

为了理解一个数组，让我们***再向 Person 对象***添加一个属性。让我们也添加爱好，一个人可以有多个爱好。这使得它适合将爱好表示为一个数组。如下面的*JSON*所示

```java
{
 "FirstName" : "Virender",
 "LastName"  : "Singh",
 "Age"       : 34,
 "Profession": "Engineer",
 "Hobbies"   : ["Videos games", "Computers", "Music"]
}
```

了解如何使用 Array 表示多个爱好。***数组以[ ]***开头和结尾，包含由 , 分隔的值

这些是*JSON*的不同组件，使用这些组件我们可以创建复杂的*JSON*。作为一个小练习，尝试解码下面的*JSON*并识别其中涉及的不同结构。

```java
{
  "Description": "Map containing Country, Capital, Currency, and some States of that Country",
  "Region": "Asia",
  "Countries": [
    {
      "Country": "India",
      "Data": {
        "Capital": "New Delhi",
        "minimum temp (Degree Celsius)": 6,
        "maximum temp (Degree Celsius)": 45,
        "Currency": "Rupee"
      }
    },
    {
      "Country": "Nepal",
      "Data": {
        "Capital": "Katmandu",
        "minimum temp (Degree Celsius)": 9,
        "maximum temp (Degree Celsius)": 23,
        "Currency": "Nepalese rupee"
      }
    }
  ]
}
```