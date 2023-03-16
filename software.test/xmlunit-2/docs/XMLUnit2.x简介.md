## 1. 概述

[XMLUnit 2.x](http://www.xmlunit.org/)是一个功能强大的库，可以帮助我们测试和验证XML内容，当我们确切地知道XML应该包含什么时，它会派上用场。

因此我们将主要在单元测试中使用XMLUnit来验证我们所拥有的是有效的XML，它是否包含某些信息或符合某种样式的文档。

此外，使用XMLUnit，我们可以控制哪种差异对我们很重要，以及样式参考的哪一部分与您比较XML的哪一部分进行比较。

由于我们关注的是XMLUnit2.x而不是XMLUnit1.x，所以无论何时我们使用XMLUnit这个词，我们都严格指的是2.x。

最后，我们还将使用Hamcrest匹配器进行断言，因此最好复习一下[Hamcrest](https://www.baeldung.com/java-junit-hamcrest-guide)，以防您不熟悉它。

## 2.XMLUnitMaven设置

要在我们的maven项目中使用该库，我们需要在pom.xml中具有以下依赖项：

```xml
<dependency>
    <groupId>org.xmlunit</groupId>
    <artifactId>xmlunit-core</artifactId>
    <version>2.2.1</version>
</dependency>
```

最新版本的xmlunit-core可以通过[这个链接](https://search.maven.org/classic/#search|ga|1|a%3A"xmlunit-core")找到。和：

```xml
<dependency>
    <groupId>org.xmlunit</groupId>
    <artifactId>xmlunit-matchers</artifactId>
    <version>2.2.1</version>
</dependency>
```

[此链接](https://search.maven.org/classic/#search|ga|1|a%3A"xmlunit-matchers")提供了最新版本的xmlunit-matchers。

## 3.比较XML

### 3.1。简单差异示例

假设我们有两段XML。当文档中节点的内容和顺序完全相同时，就认为它们是相同的，所以下面的测试会通过：

```java
@Test
public void given2XMLS_whenIdentical_thenCorrect() {
    String controlXml = "<struct><int>3</int><boolean>false</boolean></struct>";
    String testXml = "<struct><int>3</int><boolean>false</boolean></struct>";
    assertThat(testXml, CompareMatcher.isIdenticalTo(controlXml));
}
```

下一个测试失败，因为这两条XML相似但不相同，因为它们的节点以不同的顺序出现：

```java
@Test
public void given2XMLSWithSimilarNodesButDifferentSequence_whenNotIdentical_thenCorrect() {
    String controlXml = "<struct><int>3</int><boolean>false</boolean></struct>";
    String testXml = "<struct><boolean>false</boolean><int>3</int></struct>";
    assertThat(testXml, assertThat(testXml, not(isIdenticalTo(controlXml)));
}
```

### 3.2.详细差异示例

上述两个XML文档之间的差异由差异引擎检测。

默认情况下，出于效率原因，一旦发现第一个差异，它就会停止比较过程。

为了获得两段XML之间的所有差异，我们使用Diff类的实例，如下所示：

```java
@Test
public void given2XMLS_whenGeneratesDifferences_thenCorrect(){
    String controlXml = "<struct><int>3</int><boolean>false</boolean></struct>";
    String testXml = "<struct><boolean>false</boolean><int>3</int></struct>";
    Diff myDiff = DiffBuilder.compare(controlXml).withTest(testXml).build();
    
    Iterator<Difference> iter = myDiff.getDifferences().iterator();
    int size = 0;
    while (iter.hasNext()) {
        iter.next().toString();
        size++;
    }
    assertThat(size, greaterThan(1));
}
```

如果我们打印while循环中返回的值，结果如下：

```diff
Expected element tag name 'int' but was 'boolean' - 
  comparing <int...> at /struct[1]/int[1] to <boolean...> 
    at /struct[1]/boolean[1] (DIFFERENT)
Expected text value '3' but was 'false' - 
  comparing <int ...>3</int> at /struct[1]/int[1]/text()[1] to 
    <boolean ...>false</boolean> at /struct[1]/boolean[1]/text()[1] (DIFFERENT)
Expected element tag name 'boolean' but was 'int' - 
  comparing <boolean...> at /struct[1]/boolean[1] 
    to <int...> at /struct[1]/int[1] (DIFFERENT)
Expected text value 'false' but was '3' - 
  comparing <boolean ...>false</boolean> at /struct[1]/boolean[1]/text()[1] 
    to <int ...>3</int> at /struct[1]/int[1]/text()[1] (DIFFERENT)
```

每个实例都描述了在控制节点和测试节点之间发现的差异类型以及这些节点的详细信息（包括每个节点的XPath位置）。

如果我们想在找到第一个差异后强制差异引擎停止并且不继续枚举更多差异-我们需要提供一个ComparisonController：

```java
@Test
public void given2XMLS_whenGeneratesOneDifference_thenCorrect(){
    String myControlXML = "<struct><int>3</int><boolean>false</boolean></struct>";
    String myTestXML = "<struct><boolean>false</boolean><int>3</int></struct>";
    
    Diff myDiff = DiffBuilder
      .compare(myControlXML)
      .withTest(myTestXML)
      .withComparisonController(ComparisonControllers.StopWhenDifferent)
       .build();
    
    Iterator<Difference> iter = myDiff.getDifferences().iterator();
    int size = 0;
    while (iter.hasNext()) {
        iter.next().toString();
        size++;
    }
    assertThat(size, equalTo(1));
}
```

差异消息更简单：

```diff
Expected element tag name 'int' but was 'boolean' - 
  comparing <int...> at /struct[1]/int[1] 
    to <boolean...> at /struct[1]/boolean[1] (DIFFERENT)
```

## 4.输入源

使用XMLUnit，我们可以从各种可能方便满足应用程序需求的来源中挑选XML数据。在这种情况下，我们使用Input类及其静态方法数组。

要从位于项目根目录中的XML文件中选择输入，我们执行以下操作：

```java
@Test
public void givenFileSource_whenAbleToInput_thenCorrect() {
    ClassLoader classLoader = getClass().getClassLoader();
    String testPath = classLoader.getResource("test.xml").getPath();
    String controlPath = classLoader.getResource("control.xml").getPath();
    
    assertThat(
      Input.fromFile(testPath), isSimilarTo(Input.fromFile(controlPath)));
}
```

要从XML字符串中选择输入源，如下所示：

```java
@Test
public void givenStringSource_whenAbleToInput_thenCorrect() {
    String controlXml = "<struct><int>3</int><boolean>false</boolean></struct>";
    String testXml = "<struct><int>3</int><boolean>false</boolean></struct>";
    
    assertThat(
      Input.fromString(testXml),isSimilarTo(Input.fromString(controlXml)));
}
```

现在让我们使用流作为输入：

```java
@Test
public void givenStreamAsSource_whenAbleToInput_thenCorrect() {
    assertThat(Input.fromStream(XMLUnitTests.class
      .getResourceAsStream("/test.xml")),
        isSimilarTo(
          Input.fromStream(XMLUnitTests.class
            .getResourceAsStream("/control.xml"))));
}
```

我们还可以使用Input.from(Object)传入任何由XMLUnit解析的有效源。

例如，我们可以传入一个文件：

```java
@Test
public void givenFileSourceAsObject_whenAbleToInput_thenCorrect() {
    ClassLoader classLoader = getClass().getClassLoader();
    
    assertThat(
      Input.from(new File(classLoader.getResource("test.xml").getFile())), 
      isSimilarTo(Input.from(new File(classLoader.getResource("control.xml").getFile()))));
}
```

或字符串：

```java
@Test
public void givenStringSourceAsObject_whenAbleToInput_thenCorrect() {
    assertThat(
      Input.from("<struct><int>3</int><boolean>false</boolean></struct>"),
      isSimilarTo(Input.from("<struct><int>3</int><boolean>false</boolean></struct>")));
}
```

或流：

```java
@Test
public void givenStreamAsObject_whenAbleToInput_thenCorrect() {
    assertThat(
      Input.from(XMLUnitTest.class.getResourceAsStream("/test.xml")), 
      isSimilarTo(Input.from(XMLUnitTest.class.getResourceAsStream("/control.xml"))));
}
```

他们都将得到解决。

## 5.比较特定节点

在上面的第2节中，我们只查看了相同的XML，因为类似的XML需要使用xmlunit-core库中的功能进行一些定制：

```java
@Test
public void given2XMLS_whenSimilar_thenCorrect() {
    String controlXml = "<struct><int>3</int><boolean>false</boolean></struct>";
    String testXml = "<struct><boolean>false</boolean><int>3</int></struct>";
    
    assertThat(testXml, isSimilarTo(controlXml));
}
```

上述测试应该通过，因为XML具有相似的节点，但是它失败了。这是因为XMLUnit在相对于根节点的相同深度比较控制节点和测试节点。

因此，isSimilarTo条件比isIdenticalTo条件测试起来更有趣。controlXml中的节点<int>3</int>将与testXml中的<boolean>false</boolean>进行比较，自动给出失败消息：

```diff
java.lang.AssertionError: 
Expected: Expected element tag name 'int' but was 'boolean' - 
  comparing <int...> at /struct[1]/int[1] to <boolean...> at /struct[1]/boolean[1]:
<int>3</int>
   but: result was: 
<boolean>false</boolean>
```

这是XMLUnit的DefaultNodeMatcher和ElementSelector类派上用场的地方

当XMLUnit在controlXml的节点上循环时，会在比较阶段咨询DefaultNodeMatcher类，以确定testXml中的哪个XML节点与它在controlXml中遇到的当前XML节点进行比较。

在此之前，DefaultNodeMatcher已经咨询过ElementSelector来决定如何匹配节点。

我们的测试失败了，因为在默认状态下，XMLUnit将使用深度优先的方法来遍历XML，并根据文档顺序来匹配节点，因此<int>与<boolean>匹配。

让我们调整我们的测试以使其通过：

```java
@Test
public void given2XMLS_whenSimilar_thenCorrect() {
    String controlXml = "<struct><int>3</int><boolean>false</boolean></struct>";
    String testXml = "<struct><boolean>false</boolean><int>3</int></struct>";
    
    assertThat(testXml, 
      isSimilarTo(controlXml).withNodeMatcher(
      new DefaultNodeMatcher(ElementSelectors.byName)));
}
```

在这种情况下，我们告诉DefaultNodeMatcher，当XMLUnit要求比较节点时，您应该已经按照节点的元素名称对节点进行排序和匹配。

最初失败的示例类似于将ElementSelectors.Default传递给DefaultNodeMatcher。

或者，我们可以使用xmlunit-core的Diff而不是使用xmlunit-matchers：

```java
@Test
public void given2XMLs_whenSimilarWithDiff_thenCorrect() throws Exception {
    String myControlXML = "<struct><int>3</int><boolean>false</boolean></struct>";
    String myTestXML = "<struct><boolean>false</boolean><int>3</int></struct>";
    Diff myDiffSimilar = DiffBuilder.compare(myControlXML).withTest(myTestXML)
      .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
      .checkForSimilar().build();
    
    assertFalse("XML similar " + myDiffSimilar.toString(),
      myDiffSimilar.hasDifferences());
}
```

## 6.自定义DifferenceEvaluator

DifferenceEvaluator确定比较的结果。它的作用仅限于确定比较结果的严重性。

它是决定两个XML片段是否相同、相似或不同的类。

考虑以下XML片段：

```xml
<a>
    <b attr="abc">
    </b>
</a>
```

和：

```xml
<a>
    <b attr="xyz">
    </b>
</a>
```

在默认状态下，它们在技术上被评估为不同，因为它们的attr属性具有不同的值。我们来看一个测试：

```java
@Test
public void given2XMLsWithDifferences_whenTestsDifferentWithoutDifferenceEvaluator_thenCorrect(){
    final String control = "<a><b attr="abc"></b></a>";
    final String test = "<a><b attr="xyz"></b></a>";
    Diff myDiff = DiffBuilder.compare(control).withTest(test)
      .checkForSimilar().build();
    assertFalse(myDiff.toString(), myDiff.hasDifferences());
}
```

失败信息：

```diff
java.lang.AssertionError: Expected attribute value 'abc' but was 'xyz' - 
  comparing <b attr="abc"...> at /a[1]/b[1]/@attr 
  to <b attr="xyz"...> at /a[1]/b[1]/@attr
```

如果我们并不真正关心该属性，我们可以更改DifferenceEvaluator的行为以忽略它。我们通过创建自己的来做到这一点：

```java
public class IgnoreAttributeDifferenceEvaluator implements DifferenceEvaluator {
    private String attributeName;
    public IgnoreAttributeDifferenceEvaluator(String attributeName) {
        this.attributeName = attributeName;
    }
    
    @Override
    public ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome) {
        if (outcome == ComparisonResult.EQUAL)
            return outcome;
        final Node controlNode = comparison.getControlDetails().getTarget();
        if (controlNode instanceof Attr) {
            Attr attr = (Attr) controlNode;
            if (attr.getName().equals(attributeName)) {
                return ComparisonResult.SIMILAR;
            }
        }
        return outcome;
    }
}
```

然后我们重写我们最初失败的测试并提供我们自己的DifferenceEvaluator实例，如下所示：

```java
@Test
public void given2XMLsWithDifferences_whenTestsSimilarWithDifferenceEvaluator_thenCorrect() {
    final String control = "<a><b attr="abc"></b></a>";
    final String test = "<a><b attr="xyz"></b></a>";
    Diff myDiff = DiffBuilder.compare(control).withTest(test)
      .withDifferenceEvaluator(new IgnoreAttributeDifferenceEvaluator("attr"))
      .checkForSimilar().build();
    
    assertFalse(myDiff.toString(), myDiff.hasDifferences());
}
```

这次过去了。

## 7.验证

XMLUnit使用Validator类执行XML验证。您使用forLanguage工厂方法创建它的一个实例，同时传入要在验证中使用的模式。

模式作为通向其位置的URI传入，XMLUnit将其在Languages类中支持的模式位置抽象为常量。

我们通常像这样创建一个Validator类的实例：

```java
Validator v = Validator.forLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
```

在这一步之后，如果我们有自己的XSD文件来验证我们的XML，我们只需指定它的源，然后使用我们的XML文件源调用Validator的validateInstance方法。

以我们的students.xsd为例：

```xml
<?xml version = "1.0"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
    <xs:element name='class'>
        <xs:complexType>
            <xs:sequence>
                <xs:element name='student' type='StudentObject'
                   minOccurs='0' maxOccurs='unbounded' />
            </xs:sequence>
        </xs:complexType>
    </xs:element>
    <xs:complexType name="StudentObject">
        <xs:sequence>
            <xs:element name="name" type="xs:string" />
            <xs:element name="age" type="xs:positiveInteger" />
        </xs:sequence>
        <xs:attribute name='id' type='xs:positiveInteger' />
    </xs:complexType>
</xs:schema>
```

和students.xml：

```xml
<?xml version = "1.0"?>
<class>
    <student id="393">
        <name>Rajiv</name>
        <age>18</age>
    </student>
    <student id="493">
        <name>Candie</name>
        <age>19</age>
    </student>
</class>
```

然后让我们运行一个测试：

```java
@Test
public void givenXml_whenValidatesAgainstXsd_thenCorrect() {
    Validator v = Validator.forLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
    v.setSchemaSource(Input.fromStream(
      XMLUnitTests.class.getResourceAsStream("/students.xsd")).build());
    ValidationResult r = v.validateInstance(Input.fromStream(
      XMLUnitTests.class.getResourceAsStream("/students.xml")).build());
    Iterator<ValidationProblem> probs = r.getProblems().iterator();
    while (probs.hasNext()) {
        probs.next().toString();
    }
    assertTrue(r.isValid());
}
```

验证的结果是ValidationResult的一个实例，其中包含一个布尔标志，指示文档是否已成功验证。

ValidationResult还包含一个带有ValidationProblem的Iterable，以防出现故障。让我们创建一个名为students_with_error.xml的带有错误的新XML。而不是<student>，我们的起始标签都是</studet>：

```xml
<?xml version = "1.0"?>
<class>
    <studet id="393">
        <name>Rajiv</name>
        <age>18</age>
    </student>
    <studet id="493">
        <name>Candie</name>
        <age>19</age>
    </student>
</class>
```

然后对它运行这个测试：

```java
@Test
public void givenXmlWithErrors_whenReturnsValidationProblems_thenCorrect() {
    Validator v = Validator.forLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
    v.setSchemaSource(Input.fromStream(
       XMLUnitTests.class.getResourceAsStream("/students.xsd")).build());
    ValidationResult r = v.validateInstance(Input.fromStream(
      XMLUnitTests.class.getResourceAsStream("/students_with_error.xml")).build());
    Iterator<ValidationProblem> probs = r.getProblems().iterator();
    int count = 0;
    while (probs.hasNext()) {
        count++;
        probs.next().toString();
    }
    assertTrue(count > 0);
}
```

如果我们在while循环中打印错误，它们看起来像：

```diff
ValidationProblem { line=3, column=19, type=ERROR,message='cvc-complex-type.2.4.a: 
  Invalid content was found starting with element 'studet'. 
    One of '{student}' is expected.' }
ValidationProblem { line=6, column=4, type=ERROR, message='The element type "studet" 
  must be terminated by the matching end-tag "</studet>".' }
ValidationProblem { line=6, column=4, type=ERROR, message='The element type "studet" 
  must be terminated by the matching end-tag "</studet>".' }
```

## 8.XPath

当针对一段XML评估XPath表达式时，会创建一个包含匹配节点的NodeList。

考虑一下保存在名为teacher.xml的文件中的这段XML：

```xml
<teachers>
    <teacher department="science" id='309'>
        <subject>math</subject>
        <subject>physics</subject>
    </teacher>
    <teacher department="arts" id='310'>
        <subject>political education</subject>
        <subject>english</subject>
    </teacher>
</teachers>
```

XMLUnit提供了许多与XPath相关的断言方法，如下所示。

我们可以检索所有名为teacher的节点并分别对它们执行断言：

```java
@Test
public void givenXPath_whenAbleToRetrieveNodes_thenCorrect() {
    Iterable<Node> i = new JAXPXPathEngine()
      .selectNodes("//teacher", Input.fromFile(new File("teachers.xml")).build());
    assertNotNull(i);
    int count = 0;
    for (Iterator<Node> it = i.iterator(); it.hasNext();) {
        count++;
        Node node = it.next();
        assertEquals("teacher", node.getNodeName());
        
        NamedNodeMap map = node.getAttributes();
        assertEquals("department", map.item(0).getNodeName());
        assertEquals("id", map.item(1).getNodeName());
        assertEquals("teacher", node.getNodeName());
    }
    assertEquals(2, count);
}
```

请注意我们如何验证子节点的数量、每个节点的名称以及每个节点中的属性。检索Node后，还有更多选项可用。

要验证路径是否存在，我们可以执行以下操作：

```java
@Test
public void givenXmlSource_whenAbleToValidateExistingXPath_thenCorrect() {
    assertThat(Input.fromFile(new File("teachers.xml")), hasXPath("//teachers"));
    assertThat(Input.fromFile(new File("teachers.xml")), hasXPath("//teacher"));
    assertThat(Input.fromFile(new File("teachers.xml")), hasXPath("//subject"));
    assertThat(Input.fromFile(new File("teachers.xml")), hasXPath("//@department"));
}
```

要验证路径不存在，我们可以这样做：

```java
@Test
public void givenXmlSource_whenFailsToValidateInExistentXPath_thenCorrect() {
    assertThat(Input.fromFile(new File("teachers.xml")), not(hasXPath("//sujet")));
}
```

XPath在文档主要由已知的、不变的内容和系统创建的少量变化内容组成的情况下特别有用。

## 9.结论

在本教程中，我们介绍了XMLUnit2.x的大部分基本特性以及如何使用它们在我们的应用程序中验证XML文档。