## 一、简介

[Groovy](https://www.baeldung.com/groovy-language)提供了大量专用于遍历和操作 XML 内容的方法。

在本教程中，我们将演示如何使用各种方法在 Groovy 中添加、编辑或删除 XML 中的元素。我们还将展示如何从头开始创建 XML 结构。

## 2. 定义模型

让我们在我们将在整个示例中使用的资源目录中定义一个 XML 结构：

```xml
<articles>
    <article>
        <title>First steps in Java</title>
        <author id="1">
            <firstname>Siena</firstname>
            <lastname>Kerr</lastname>
        </author>
        <release-date>2018-12-01</release-date>
    </article>
    <article>
        <title>Dockerize your SpringBoot application</title>
        <author id="2">
            <firstname>Jonas</firstname>
            <lastname>Lugo</lastname>
        </author>
        <release-date>2018-12-01</release-date>
    </article>
    <article>
        <title>SpringBoot tutorial</title>
        <author id="3">
            <firstname>Daniele</firstname>
            <lastname>Ferguson</lastname>
        </author>
        <release-date>2018-06-12</release-date>
    </article>
    <article>
        <title>Java 12 insights</title>
        <author id="1">
            <firstname>Siena</firstname>
            <lastname>Kerr</lastname>
        </author>
        <release-date>2018-07-22</release-date>
    </article>
</articles>
```

并将其读入InputStream变量：

```groovy
def xmlFile = getClass().getResourceAsStream("articles.xml")
```

## 3.XML解析器

让我们开始使用XmlParser类探索这个流。

### 3.1. 阅读

读取和解析 XML 文件可能是开发人员必须执行的最常见的 XML 操作。XmlParser提供了一个非常简单的接口，就是为了：

```groovy
def articles = new XmlParser().parse(xmlFile)
```

此时，我们可以使用 GPath 表达式访问 XML 结构的属性和值。 

[现在让我们使用Spock](https://www.baeldung.com/groovy-spock)实现一个简单的测试来检查我们的文章对象是否正确：

```groovy
def "Should read XML file properly"() {
    given: "XML file"

    when: "Using XmlParser to read file"
    def articles = new XmlParser().parse(xmlFile)

    then: "Xml is loaded properly"
    articles.''.size() == 4
    articles.article[0].author.firstname.text() == "Siena"
    articles.article[2].'release-date'.text() == "2018-06-12"
    articles.article[3].title.text() == "Java 12 insights"
    articles.article.find { it.author.'@id'.text() == "3" }.author.firstname.text() == "Daniele"
}
```

要了解如何访问 XML 值以及如何使用 GPath 表达式，让我们暂时关注XmlParser#parse 操作结果的内部结构。

articles对象是 groovy.util.Node 的一个实例。每个 节点都包含名称、属性映射、值和父节点(可以为空或另一个 节点)。

在我们的例子中，articles的值是一个groovy.util.NodeList 实例，它是Node集合的包装类。NodeList扩展了java.util.ArrayList类，后者提供按索引提取元素。要获取节点的字符串值， 我们使用groovy.util.Node#text()。

在上面的例子中，我们引入了几个GPath表达式：

-   articles.article[0].author.firstname — 获取第一篇文章作者的名字 - articles.article[n]将直接访问第n篇文章
-   ''  — 获取article的子节点列表——它等同于groovy.util.Node#children()
-   author.'@id' — 获取 author元素的id属性 – author.'@attributeName'通过其名称访问属性值(等效项是：author['@id']和author.@id)

### 3.2. 添加节点

与前面的示例类似，让我们先将 XML 内容读入变量。这将允许我们定义一个新节点并使用groovy.util.Node#append将其添加到我们的文章列表中。

现在让我们实施一个测试来证明我们的观点：

```groovy
def "Should add node to existing xml using NodeBuilder"() {
    given: "XML object"
    def articles = new XmlParser().parse(xmlFile)

    when: "Adding node to xml"
    def articleNode = new NodeBuilder().article(id: '5') {
        title('Traversing XML in the nutshell')
        author {
            firstname('Martin')
            lastname('Schmidt')
        }
        'release-date'('2019-05-18')
    }
    articles.append(articleNode)

    then: "Node is added to xml properly"
    articles.''.size() == 5
    articles.article[4].title.text() == "Traversing XML in the nutshell"
}
```

正如我们在上面的例子中看到的，这个过程非常简单。

我们还要注意，我们使用了groovy.util.NodeBuilder，这是对Node定义使用Node构造函数的巧妙替代方法。

### 3.3. 修改节点

我们还可以使用XmlParser修改节点的值。为此，让我们再次解析 XML 文件的内容。接下来，我们可以通过更改Node对象的值字段来编辑内容节点。

让我们记住，虽然 XmlParser使用 GPath 表达式，但我们总是检索NodeList的实例，因此要修改第一个(也是唯一的)元素，我们必须使用它的索引来访问它。

让我们通过编写一个快速测试来检查我们的假设：

```groovy
def "Should modify node"() {
    given: "XML object"
    def articles = new XmlParser().parse(xmlFile)

    when: "Changing value of one of the nodes"
    articles.article.each { it.'release-date'[0].value = "2019-05-18" }

    then: "XML is updated"
    articles.article.findAll { it.'release-date'.text() != "2019-05-18" }.isEmpty()
}
```

在上面的示例中，我们还使用了[Groovy Collections API](https://www.baeldung.com/groovy-collections-find-elements)来遍历NodeList。

### 3.4. 更换节点

接下来，让我们看看如何替换整个节点，而不是仅仅修改其中一个值。

与添加新元素类似，我们将使用NodeBuilder定义节点，然后使用groovy.util.Node#replaceNode替换其中的一个现有节点：

```groovy
def "Should replace node"() {
    given: "XML object"
    def articles = new XmlParser().parse(xmlFile)

    when: "Adding node to xml"
    def articleNode = new NodeBuilder().article(id: '5') {
        title('Traversing XML in the nutshell')
        author {
            firstname('Martin')
            lastname('Schmidt')
        }
        'release-date'('2019-05-18')
    }
    articles.article[0].replaceNode(articleNode)

    then: "Node is added to xml properly"
    articles.''.size() == 4
    articles.article[0].title.text() == "Traversing XML in the nutshell"
}
```

### 3.5. 删除节点

使用XmlParser删除节点非常棘手。尽管 Node类提供了remove(Node child)方法，但在大多数情况下，我们不会单独使用它。

相反，我们将展示如何删除其值满足给定条件的节点。

默认情况下，使用Node.NodeList引用链访问嵌套元素会返回相应子节点的副本。因此，我们不能直接在我们的文章集合上使用java.util.NodeList#removeAll方法。

要通过谓词删除节点，我们必须首先找到所有符合我们条件的节点，然后遍历它们并每次调用父节点上的java.util.Node#remove方法。

让我们实施一个测试，删除所有作者 ID 不是3的文章：

```groovy
def "Should remove article from xml"() {
    given: "XML object"
    def articles = new XmlParser().parse(xmlFile)

    when: "Removing all articles but the ones with id==3"
    articles.article
      .findAll { it.author.'@id'.text() != "3" }
      .each { articles.remove(it) }

    then: "There is only one article left"
    articles.children().size() == 1
    articles.article[0].author.'@id'.text() == "3"
}
```

正如我们所见，作为删除操作的结果，我们收到了一个只有一篇文章的 XML 结构，其 id 为3。

## 4. XMLSlurper

Groovy 还提供了另一个专门用于处理 XML 的类。在本节中，我们将展示如何使用XmlSlurper 读取和操作 XML 结构。

### 4.1. 阅读

与我们之前的示例一样，让我们从解析文件中的 XML 结构开始：

```groovy
def "Should read XML file properly"() {
    given: "XML file"

    when: "Using XmlSlurper to read file"
    def articles = new XmlSlurper().parse(xmlFile)

    then: "Xml is loaded properly"
    articles.''.size() == 4
    articles.article[0].author.firstname == "Siena"
    articles.article[2].'release-date' == "2018-06-12"
    articles.article[3].title == "Java 12 insights"
    articles.article.find { it.author.'@id' == "3" }.author.firstname == "Daniele"
}
```

如我们所见，该接口与XmlParser的接口相同。但是，输出结构使用groovy.util.slurpersupport.GPathResult，它是Node的包装类。GPathResult通过包装 Node#text()提供了方法的简化定义，例如：equals()和toString( )。因此，我们可以直接使用它们的名称来读取字段和参数。

### 4.2. 添加节点

添加 节点也与使用 XmlParser非常相似。然而，在这种情况下，groovy.util.slurpersupport。GPathResult#appendNode提供了一种将java.lang.Object 的实例作为参数的方法。因此，我们可以 按照Node Builder引入的相同约定来简化新的Node定义：

```groovy
def "Should add node to existing xml"() {
    given: "XML object"
    def articles = new XmlSlurper().parse(xmlFile)

    when: "Adding node to xml"
    articles.appendNode {
        article(id: '5') {
            title('Traversing XML in the nutshell')
            author {
                firstname('Martin')
                lastname('Schmidt')
            }
            'release-date'('2019-05-18')
        }
    }

    articles = new XmlSlurper().parseText(XmlUtil.serialize(articles))

    then: "Node is added to xml properly"
    articles.''.size() == 5
    articles.article[4].title == "Traversing XML in the nutshell"
}
```

如果我们需要使用XmlSlurper 修改 XML 的结构，我们必须重新初始化我们的文章对象以查看结果。我们可以通过结合使用groovy.util.XmlSlurper#parseText和groovy.xmlXmlUtil#serialize方法来实现。

### 4.3. 修改节点

正如我们之前提到的，GPathResult引入了一种简化的数据操作方法。也就是说，与XmlSlurper 相比，我们可以直接使用节点名称或参数名称修改值：

```groovy
def "Should modify node"() {
    given: "XML object"
    def articles = new XmlSlurper().parse(xmlFile)

    when: "Changing value of one of the nodes"
    articles.article.each { it.'release-date' = "2019-05-18" }

    then: "XML is updated"
    articles.article.findAll { it.'release-date' != "2019-05-18" }.isEmpty()
}
```

请注意，当我们只修改 XML 对象的值时，我们不必再次解析整个结构。

### 4.4. 更换节点

现在让我们开始替换整个节点。再次，GPathResult 来拯救。我们可以使用groovy.util.slurpersupport.NodeChild# replaceNode 轻松替换节点，它扩展了 GPathResult 并遵循使用对象值作为参数的相同约定：

```groovy
def "Should replace node"() {
    given: "XML object"
    def articles = new XmlSlurper().parse(xmlFile)

    when: "Replacing node"
    articles.article[0].replaceNode {
        article(id: '5') {
            title('Traversing XML in the nutshell')
            author {
                firstname('Martin')
                lastname('Schmidt')
            }
            'release-date'('2019-05-18')
        }
    }

    articles = new XmlSlurper().parseText(XmlUtil.serialize(articles))

    then: "Node is replaced properly"
    articles.''.size() == 4
    articles.article[0].title == "Traversing XML in the nutshell"
}
```

与添加节点时的情况一样，我们正在修改 XML 的结构，因此我们必须再次解析它。

### 4.5. 删除节点

要使用XmlSlurper 删除节点，我们可以通过提供一个空的 节点定义来重用groovy.util.slurpersupport.NodeChild#replaceNode方法：

```groovy
def "Should remove article from xml"() {
    given: "XML object"
    def articles = new XmlSlurper().parse(xmlFile)

    when: "Removing all articles but the ones with id==3"
    articles.article
      .findAll { it.author.'@id' != "3" }
      .replaceNode {}

    articles = new XmlSlurper().parseText(XmlUtil.serialize(articles))

    then: "There is only one article left"
    articles.children().size() == 1
    articles.article[0].author.'@id' == "3"
}
```

同样，修改 XML 结构需要重新初始化我们的文章对象。

## 5.XmlParser与XmlSlurper _

正如我们在示例中展示的那样，XmlParser 和XmlSlurper的用法非常相似。我们或多或少可以用两者达到相同的结果。但是，它们之间的某些差异可能会使天平偏向一个或另一个。

首先，XmlParser总是将整个文档解析为 DOM-ish 结构。因此，我们可以同时读取和写入它。我们不能对XmlSlurper做同样的事情，因为它更懒惰地评估路径。因此，XmlParser会消耗更多内存。 

另一方面，XmlSlurper使用更直接的定义，使其更易于使用。我们还需要记住，使用XmlSlurper对 XML 进行的任何结构更改都需要重新初始化，如果一个接一个地进行许多更改，这可能会对性能造成不可接受的影响。

应谨慎决定使用哪种工具，并且完全取决于用例。

## 6.标记生成器

除了读取和操作 XML 树之外，Groovy 还提供了从头开始创建 XML 文档的工具。现在让我们使用groovy.xml.MarkupBuilder创建一个包含我们第一个示例中的前两篇文章的文档：

```groovy
def "Should create XML properly"() {
    given: "Node structures"

    when: "Using MarkupBuilderTest to create xml structure"
    def writer = new StringWriter()
    new MarkupBuilder(writer).articles {
        article {
            title('First steps in Java')
            author(id: '1') {
                firstname('Siena')
                lastname('Kerr')
            }
            'release-date'('2018-12-01')
        }
        article {
            title('Dockerize your SpringBoot application')
            author(id: '2') {
                firstname('Jonas')
                lastname('Lugo')
            }
            'release-date'('2018-12-01')
        }
    }

    then: "Xml is created properly"
    XmlUtil.serialize(writer.toString()) == XmlUtil.serialize(xmlFile.text)
}
```

在上面的示例中，我们可以看到MarkupBuilder使用与我们之前在NodeBuilder和GPathResult中使用的节点定义完全相同的方法 。

为了将 MarkupBuilder的输出与预期的 XML 结构进行比较，我们使用了groovy.xml.XmlUtil#serialize方法。

## 七、总结

在本文中，我们探讨了使用 Groovy 操作 XML 结构的多种方法。

我们查看了使用 Groovy 提供的两个类来解析、添加、编辑、替换和删除节点的示例：XmlParser和 XmlSlurper。我们还讨论了它们之间的区别，并展示了如何使用MarkupBuilder从头开始构建 XML 树。