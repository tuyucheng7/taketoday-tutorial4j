## 一、概述

在本教程中，我们将了解如何在 Groovy 中使用 REST-assured 库。

由于 REST 保证在底层使用 Groovy，我们实际上有机会使用原始 Groovy 语法来创建更强大的测试用例。这就是框架真正发挥作用的地方。

有关使用 REST-assured 所需的设置，请查看我们[之前的文章](https://www.baeldung.com/rest-assured-tutorial)。

## **2. Groovy 的集合 API**

让我们从快速浏览一些基本的 Groovy 概念开始——通过一些简单的示例来为我们提供所需的知识。

### **2.1。*findAll*方法_**

在这个例子中，我们只关注*方法*、*闭包*和*它*的隐式变量。让我们首先创建一个 Groovy 单词集合：

```groovy
def words = ['ant', 'buffalo', 'cat', 'dinosaur']复制
```

现在让我们从上面创建另一个集合，其中包含长度超过四个字母的单词：

```groovy
def wordsWithSizeGreaterThanFour = words.findAll { it.length() > 4 }复制
```

在这里，*findAll()*是一个应用于集合的方法，其中一个*闭包*应用于该方法。该*方法*定义了应用于集合的逻辑，*闭包*为该方法提供了一个谓词来自定义逻辑。

我们告诉 Groovy 循环遍历集合并查找长度大于 4 的所有单词并将结果返回到新集合中。

### **2.2. *它*变量_**

*它*在循环中保存当前单词的隐式变量。新的集合*wordsWithSizeGreaterThanFour*将包含单词*buffalo*和*恐龙*。

```groovy
['buffalo', 'dinosaur']复制
```

除了*findAll()*之外，还有其他 Groovy 方法。

### **2.3. 收集*迭代*器**

最后是*collect*，它对集合中的每个项目调用闭包，并返回一个包含每个项目结果的新集合。*让我们根据words*集合中每个项目的大小创建一个新集合：

```groovy
def sizes = words.collect{it.length()}
复制
```

结果：

```groovy
[3,7,3,8]复制
```

顾名思义，我们使用*sum来将集合中的所有元素相加。*我们可以像这样总结*size*集合中的项目：

```groovy
def charCount = sizes.sum()复制
```

结果将是 21，即*words*集合中所有项目的字符数。

### **2.4. 最大/*最小*运算符**

*max/min*运算符的命名很直观，用于查找集合中的最大或最小数：

```groovy
def maximum = sizes.max()复制
```

结果应该很明显，8。

### **2.5. *查找*迭代器**

我们使用*find*只搜索一个与闭包谓词匹配的集合值。

```groovy
def greaterThanSeven=sizes.find{it>7}复制
```

结果，8，满足谓词的集合项的第一次出现。

## **3. 使用 Groovy 验证 JSON**

如果我们在*http://localhost:8080/odds*有一个服务，它会返回我们最喜欢的足球比赛的赔率列表，如下所示：

```javascript
{
    "odds": [{
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
    }]
}复制
```

如果我们想验证状态大于 1 的赔率的价格为*1.20*和 5 *.25*，那么我们这样做：

```java
@Test
public void givenUrl_whenVerifiesOddPricesAccuratelyByStatus_thenCorrect() {
    get("/odds").then().body("odds.findAll { it.status > 0 }.price",
      hasItems(5.25f, 1.20f));
}复制
```

这里发生的事情是这样的；我们使用 Groovy 语法来加载关键*赔率*下的 JSON 数组。由于它有多个项目，因此我们获得了一个 Groovy 集合。然后我们在这个集合上调用*findAll方法。*

闭包谓词告诉 Groovy 使用状态大于零的 JSON 对象创建另一个集合。

*我们以price*结束我们的路径，它告诉 groovy 在我们之前的 JSON 对象列表中创建另一个仅包含赔率价格的列表。然后我们将*hasItems* Hamcrest 匹配器应用到这个列表。

## **4. 使用 Groovy 验证 XML**

假设我们在*http://localhost:8080/teachers有一个服务，它通过他们的**id*、*部门*和教授的*科目*返回一个教师列表，如下所示：

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
</teachers>复制
```

现在我们可以验证回复中返回的科学老师是否同时教数学和物理：

```java
@Test
public void givenUrl_whenVerifiesScienceTeacherFromXml_thenCorrect() {
    get("/teachers").then().body(
      "teachers.teacher.find { it.@department == 'science' }.subject",
        hasItems("math", "physics"));
}复制
```

我们使用 XML 路径teacher.teacher通过 XML 属性 Department 获取*教师**列表*。然后我们在这个列表中调用*find方法。*

我们的闭包谓词*find*确保我们最终只得到*科学*系的教师。*我们的 XML 路径在主题*标记处终止。

由于有多个主题，我们将获得一个列表，我们使用*hasItems* Hamcrest 匹配器验证该列表。

## 5. 结论

在本文中，我们了解了如何将 REST-assured 库与 Groovy 语言一起使用。