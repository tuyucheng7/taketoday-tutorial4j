## 1. 概述

本教程重点介绍如何使用Jackson中的树模型节点，我们将使用JsonNode进行各种转换以及添加、修改和删除节点。

## 2. 创建节点

创建节点的第一步是使用默认构造函数实例化ObjectMapper对象：

```java
ObjectMapper mapper = new ObjectMapper();
```

由于创建ObjectMapper对象的成本很高，因此建议我们为多个操作重用同一个对象。接下来，一旦我们有了ObjectMapper ，我们就有三种不同的方法来创建树节点。

### 2.1 从头开始构建节点

这是创建节点的最常见方法：

```java
JsonNode node = mapper.createObjectNode();
```

或者，我们也可以通过JsonNodeFactory创建一个节点：

```java
JsonNode node = JsonNodeFactory.instance.objectNode();
```

### 2.2 从JSON源解析

这个方法在[Jackson – Marshall String to JsonNode](https://www.baeldung.com/jackson-json-to-jsonnode)文章中有很好的介绍，请参阅它以获取更多信息。

### 2.3 从对象转换

可以通过调用ObjectMapper上的valueToTree(Object fromValue)方法从Java对象转换节点：

```java
JsonNode node = mapper.valueToTree(fromValue);
```

convertValue API在这里也很有帮助：

```java
JsonNode node = mapper.convertValue(fromValue, JsonNode.class);
```

假设我们有一个名为NodeBean的类：

```java
public class NodeBean {
    private int id;
    private String name;

    public NodeBean() {
    }

    public NodeBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // standard getters and setters
}
```

让我们编写一个测试来确保转换正确执行：

```java
@Test
void givenAnObject_whenConvertingIntoNode_thenCorrect() {
    NodeBean fromValue = new NodeBean(2016, "tuyucheng.com");

    JsonNode node = mapper.valueToTree(fromValue);

    assertEquals(2016, node.get("id").intValue());
    assertEquals("tuyucheng.com", node.get("name").textValue());
}
```

## 3. 转换节点

### 3.1 写成JSON

这是将树节点转换为JSON字符串的基本方法，其中destination参数可以是File、OutputStream或Writer：

```java
mapper.writeValue(destination, node);
```

这里重用第2.3节中声明的NodeBean类，下面的测试确保此方法按预期工作：

```java
final String pathToTestFile = "node_to_json_test.json";

@Test
void givenANode_whenModifyingIt_thenCorrect() throws IOException {
    String newString = "{\"nick\": \"cowtowncoder\"}";
    JsonNode newNode = mapper.readTree(newString);

    JsonNode rootNode = ExampleStructure.getExampleRoot();
    ((ObjectNode) rootNode).set("name", newNode);

    assertFalse(rootNode.path("name").path("nick").isMissingNode());
    assertEquals("cowtowncoder", rootNode.path("name").path("nick").textValue());
}
```

### 3.2. 转换为对象

将JsonNode转换为Java对象最方便的方法是treeToValue API：

```java
NodeBean toValue = mapper.treeToValue(node, NodeBean.class);
```

这在功能上等同于以下方法：

```java
NodeBean toValue = mapper.convertValue(node, NodeBean.class)
```

我们也可以通过token流来做到这一点：

```java
JsonParser parser = mapper.treeAsTokens(node);
NodeBean toValue = mapper.readValue(parser, NodeBean.class);
```

最后，下面是一个验证转换过程的测试：

```java
@Test
void givenANode_whenConvertingIntoAnObject_thenCorrect() throws JsonProcessingException {
	final JsonNode node = mapper.createObjectNode();
	((ObjectNode) node).put("id", 2016);
	((ObjectNode) node).put("name", "tuyucheng.com");
    
	final NodeBean toValue = mapper.treeToValue(node, NodeBean.class);
    
	assertEquals(2016, toValue.getId());
	assertEquals("tuyucheng.com", toValue.getName());
}
```

## 4. 操作树节点

以下example.json的文件中的JSON元素作为例子：

```json
{
    "name": 
        {
            "first": "Tatu",
            "last": "Saloranta"
        },

    "title": "Jackson founder",
    "company": "FasterXML"
}
```

这个位于类路径上的JSON文件被解析为模型树：

```java
public class ExampleStructure {
	private static final ObjectMapper mapper = new ObjectMapper();

	static JsonNode getExampleRoot() throws IOException {
		InputStream exampleInput = ExampleStructure.class.getClassLoader().getResourceAsStream("example.json");
		return mapper.readTree(exampleInput);
	}
}
```

请注意，在以下小节中说明对节点的操作时，将使用树的根。

### 4.1 定位节点

在处理任何节点之前，我们需要做的第一件事就是定位并将其分配给一个变量。如果我们事先知道节点的路径，那很容易做到。

假设我们想要一个名为last的节点，它位于name节点下：

```java
JsonNode locatedNode = rootNode.path("name").path("last");
```

或者，也可以使用get或with API代替path。如果路径未知，搜索当然会变得更加复杂和迭代。

[我们可以在第5节 - 迭代](https://www.baeldung.com/jackson-json-node-tree-model#iterating)节点中看到迭代所有节点的示例。

### 4.2 添加新节点

一个节点可以添加为另一个节点的子节点：

```java
ObjectNode newNode = ((ObjectNode) locatedNode).put(fieldName, value);
```

put的许多重载变体可用于添加不同值类型的新节点。许多其他类似的方法也可用，包括putArray、putObject、PutPOJO、putRawValue和putNull。

最后，让我们看一个例子，将整个结构添加到树的根节点：

```json
"address":
{
    "city": "Seattle",
    "state": "Washington",
    "country": "United States"
}
```

这是完成所有这些操作并验证结果的完整测试：

```java
@Test
void givenANode_whenAddingIntoATree_thenCorrect() throws IOException {
	final JsonNode rootNode = ExampleStructure.getExampleRoot();
	final ObjectNode addedNode = ((ObjectNode) rootNode).putObject("address");
    
	addedNode.put("city", "Seattle")
			.put("state", "Washington")
			.put("country", "United States");
    
	assertFalse(rootNode.path("address")
			.isMissingNode());
	assertEquals("Seattle", rootNode.path("address")
			.path("city")
			.textValue());
	assertEquals("Washington", rootNode.path("address")
			.path("state")
			.textValue());
	assertEquals("United States", rootNode.path("address")
			.path("country")
			.textValue());
}
```

### 4.3 编辑节点

可以通过调用set(String fieldName, JsonNode value)方法修改ObjectNode实例：

```java
JsonNode locatedNode = locatedNode.set(fieldName, value);
```

通过对相同类型的对象使用replace或setAll方法可能会获得类似的结果。

为了验证该方法是否按预期工作，我们将在测试中将根节点下的字段名称的值从first和last对象更改为另一个仅包含nick字段的对象：

```java
@Test
void givenANode_whenModifyingIt_thenCorrect() throws IOException {
	final String newString = "{\"nick\": \"cowtowncoder\"}";
	final JsonNode newNode = mapper.readTree(newString);
    
	final JsonNode rootNode = ExampleStructure.getExampleRoot();
	((ObjectNode) rootNode).set("name", newNode);
    
	assertFalse(rootNode.path("name")
			.path("nick")
			.isMissingNode());
	assertEquals("cowtowncoder", rootNode.path("name")
			.path("nick")
			.textValue());
}
```

### 4.4 删除节点

可以通过在其父节点上调用remove(String fieldName) API来删除节点：

```java
JsonNode removedNode = locatedNode.remove(fieldName);
```

为了一次删除多个节点，我们可以调用一个带有Collection<>类型参数的重载方法，它返回父节点而不是要删除的那个：

```java
ObjectNode locatedNode = locatedNode.remove(fieldNames);
```

在极端情况下，当我们想要删除给定节点的所有子节点时， removeAll API会派上用场。

下面的测试将重点关注上面提到的第一种方法，这是最常见的场景：

```java
@Test
void givenANode_whenRemovingFromATree_thenCorrect() throws IOException {
	final JsonNode rootNode = ExampleStructure.getExampleRoot();
	((ObjectNode) rootNode).remove("company");
    
	assertTrue(rootNode.path("company").isMissingNode());
}
```

## 5. 迭代节点

让我们遍历JSON文档中的所有节点并将它们重新格式化为YAML。JSON具有三种类型的节点，分别是Value、Object和Array。

因此，让我们通过添加一个数组对象来确保我们的示例数据具有所有三种不同的类型：

```json
{
    "name": {
        "first": "Tatu",
        "last": "Saloranta"
    },
    "title": "Jackson founder",
    "company": "FasterXML",
    "pets": [
        {
            "type": "dog",
            "number": 1
        },
        {
            "type": "fish",
            "number": 50
        }
    ]
}
```

现在让我们看看我们想要生成的YAML：

```yaml
name:
    first: Tatu
    last: Saloranta
title: Jackson founder
company: FasterXML
pets:
    -   type: dog
        number: 1
    -   type: fish
        number: 50
```

我们知道JSON节点具有层次树结构。因此，遍历整个JSON文档的最简单方法是从顶部开始，向下遍历所有子节点。我们将根节点传递给递归方法。然后，该方法将使用所提供节点的每个子节点调用自身。

### 5.1 测试迭代

我们首先创建一个简单的测试来检查我们是否可以成功地将JSON转换为YAML。

我们的测试将JSON文档的根节点提供给我们的toYaml方法，并断言返回的值是我们所期望的：

```java
@Test
public void givenANodeTree_whenIteratingSubNodes_thenWeFindExpected() throws IOException {
	final JsonNode rootNode = ExampleStructure.getExampleRoot();
	String yaml = onTest.toYaml(rootNode);
    
	assertEquals(expectedYaml, yaml);
}

public String toYaml(JsonNode root) {
	StringBuilder yaml = new StringBuilder();
	processNode(root, yaml, 0);
	return yaml.toString();
}
```

### 5.2 处理不同的节点类型

我们需要稍微不同地处理不同类型的节点。我们将在processNode方法中执行此操作：

```java
private void processNode(JsonNode jsonNode, StringBuilder yaml, int depth) {
	if (jsonNode.isValueNode()) {
		yaml.append(jsonNode.asText());
	} else if (jsonNode.isArray()) {
		for (JsonNode arrayItem : jsonNode) {
			appendNodeToYaml(arrayItem, yaml, depth, true);
		}
	} else if (jsonNode.isObject()) {
		appendNodeToYaml(jsonNode, yaml, depth, false);
	}
}
```

首先，让我们考虑一个Value节点。我们只需调用节点的asText方法来获取值的String表示形式。

接下来，让我们看一个Array节点。Array节点中的每个项目本身都是一个JsonNode，因此我们遍历Array并将每个节点传递给appendNodeToYaml方法。我们还需要知道这些节点是数组的一部分。不幸的是，节点本身不包含任何告诉我们的信息，所以我们将一个标志传递给我们的appendNodeToYaml方法。

最后，我们要遍历每个Object节点的所有子节点，一种选择是使用JsonNode.elements。但是，我们无法从元素中确定字段名称，因为它只包含字段值：

```text
Object  {"first": "Tatu", "last": "Saloranta"}
Value  "Jackson Founder"
Value  "FasterXML"
Array  [{"type": "dog", "number": 1},{"type": "fish", "number": 50}]
```

相反，我们将使用JsonNode.fields因为这使我们可以访问字段名称和值：

```text
Key="name", Value=Object  {"first": "Tatu", "last": "Saloranta"}
Key="title", Value=Value  "Jackson Founder"
Key="company", Value=Value  "FasterXML"
Key="pets", Value=Array  [{"type": "dog", "number": 1},{"type": "fish", "number": 50}]
```

对于每个字段，我们将字段名称添加到输出中，然后将值作为子节点处理，方法是将其传递给processNode方法：

```java
private void appendNodeToYaml(JsonNode node, StringBuilder yaml, int depth, boolean isArrayItem) {
	Iterator<Entry<String, JsonNode>> fields = node.fields();
	boolean isFirst = true;
	while (fields.hasNext()) {
		Entry<String, JsonNode> jsonField = fields.next();
		addFieldNameToYaml(yaml, jsonField.getKey(), depth, isArrayItem && isFirst);
		processNode(jsonField.getValue(), yaml, depth + 1);
		isFirst = false;
	}
}
```

我们无法从节点中得知它有多少祖先。

因此，我们将一个名为depth的字段传递给processNode方法来跟踪这一点，并且每次获得子节点时我们都会递增该值，以便我们可以正确缩进YAML输出中的字段：

```java
private void addFieldNameToYaml(StringBuilder yaml, String fieldName, int depth, boolean isFirstInArray) {
	if (yaml.length() > 0) {
		yaml.append(NEW_LINE);
		int requiredDepth = (isFirstInArray) ? depth - 1 : depth;
		for (int i = 0; i < requiredDepth; i++) {
			yaml.append(YAML_PREFIX);
		}
		if (isFirstInArray) {
			yaml.append(ARRAY_PREFIX);
		}
	}
	yaml.append(fieldName);
	yaml.append(FIELD_DELIMITER);
}
```

现在我们已经准备好所有代码来遍历节点并创建YAML输出，可以运行我们的测试来证明它有效。

## 6. 总结

本文介绍了在Jackson中使用树模型时的常见API和场景。