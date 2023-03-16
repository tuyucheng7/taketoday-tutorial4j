## 1. 概述

在本教程中，我们将介绍如何将REST-Assured库与Groovy结合使用。

由于Rest-Assured在底层使用Groovy，我们实际上有机会使用原始Groovy语法来创建更强大的测试用例。这就是框架真正发挥作用的地方。

有关使用Rest-Assured所需的设置，请查看我们[之前的文章](https://www.baeldung.com/rest-assured-tutorial)。

## 2. Groovy的集合API

让我们从快速浏览一些基本的Groovy概念开始-通过一些简单的示例来准备我们所需要的东西。

### 2.1 findAll方法

在这个例子中，我们只关注方法、闭包和it隐式变量。让我们首先创建一个Groovy集合words：

```groovy
def words = ['ant', 'buffalo', 'cat', 'dinosaur']
```

现在让我们从上面创建另一个集合，其中包含长度超过四个字母的单词：

```groovy
def wordsWithSizeGreaterThanFour = words.findAll { it.length() > 4 }
```

在这里，findAll()是一个应用于集合的方法，并带有一个应用于该方法的闭包。该方法定义了应用于集合的逻辑，闭包为该方法提供了一个谓词来自定义逻辑。

我们告诉Groovy遍历集合并找到所有长度大于4的单词并将结果返回到新集合中。

### 2.2 it变量

隐式变量it保存循环中的当前单词，新集合wordsWithSizeGreaterThanFour将包含单词buffalo和dinosaur。

```groovy
['buffalo', 'dinosaur']
```

除了findAll()之外，还有其他Groovy方法。

### 2.3 collect迭代器

最后是collect，它调用集合中每个元素的闭包并返回一个包含每个元素结果的新集合。让我们根据words集合中每个元素的大小创建一个新集合：

```groovy
def sizes = words.collect{it.length()}
```

结果：

```groovy
[3,7,3,8]
```

顾名思义，我们使用sum将集合中的所有元素相加。我们可以像这样相加sizes集合中的元素：

```groovy
def charCount = sizes.sum()
```

结果将为21，即words集合中所有元素的字符数。

### 2.4 max/min运算符

max/min运算符被直观地命名以查找集合中的最大值或最小值：

```groovy
def maximum = sizes.max()
```

结果应该很明显为8。

### 2.5 find迭代器

我们使用find只搜索一个与闭包谓词匹配的集合值。

```groovy
def greaterThanSeven=sizes.find{it>7}
```

结果为8，即满足谓词的集合元素的第一个匹配项。

## 3. 使用Groovy验证JSON

如果我们在[http://localhost:8080/odds](http://localhost:8080/odds)有一个服务，它会返回我们最喜欢的足球比赛的赔率列表，如下所示：

```json
{
    "odds": [
        {
            "price": 1.30,
            "status": 0,
            "ck": 12.2,
            "name": "1"
        },
        {
            "price": 5.25,
            "status": 1,
            "ck": 13.1,
            "name": "X"
        },
        {
            "price": 2.70,
            "status": 0,
            "ck": 12.2,
            "name": "0"
        },
        {
            "price": 1.20,
            "status": 2,
            "ck": 13.1,
            "name": "2"
        }
    ]
}
```

如果我们想验证status大于1的赔率的价格为1.20和5.25，那么我们这样做：

```java
@Test
void givenUrl_whenVerifiesOddPricesAccuratelyByStatus_thenCorrect() {
    get("/odds").then()
        .body("odds.findAll { it.status > 0 }.price", hasItems(5.25f, 1.20f));
}
```

这里发生的事情是这样的；我们使用Groovy语法在键odds下加载JSON数组。由于它有多个元素，因此我们获得了一个Groovy集合。然后我们对该集合调用findAll方法。

闭包谓词告诉Groovy使用status大于0的JSON对象创建另一个集合。

我们以price结束我们的路径，它告诉Groovy在我们之前的JSON对象列表中创建另一个只有赔率价格的列表。然后我们将hasItems Hamcrest匹配器应用于这个列表。

## 4. 使用Groovy验证XML

假设我们在[http://localhost:8080/teachers](http://localhost:8080/teachers)有一个服务，它按id、系和教授的科目返回教师列表，如下所示：

```xml
<teachers>
    <teacher department="science" id=309>
        <subject>math</subject>
        <subject>physics</subject>
    </teacher>
    <teacher department="arts" id=310>
        <subject>political education</subject>
        <subject>english</subject>
    </teacher>
</teachers>
```

现在我们可以验证响应中返回的科学老师是否同时教授数学和物理：

```java
@Test
void givenUrl_whenVerifiesScienceTeacherFromXml_thenCorrect() {
    get("/teachers").then()
        .body("teachers.teacher.find { it.@department == 'science' }.subject", hasItems("math", "physics"));
}
```

我们已经使用XML路径teachers.teacher通过XML属性department获取教师列表。然后，我们在此列表上调用find方法。

我们的闭包谓词find确保我们最终只得到subjects系的教师。我们的XML路径终止于subject标签。

由于有多个主题，我们将获得一个列表，我们使用hasItems Hamcrest匹配器对其进行验证。

## 5. 总结

在本文中，我们了解了如何通过Groovy语言使用Rest-Assured库。