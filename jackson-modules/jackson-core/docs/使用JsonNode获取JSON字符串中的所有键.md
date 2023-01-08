## 1. 概述

在本文中，我们将探索使用JsonNode从JSON中提取所有嵌套key的不同方法。我们的目标是遍历一个JSON字符串并收集一个列表中的key名称。

## 2. 简介

[Jackson](https://www.baeldung.com/jackson)库使用树模型来表示[JSON](https://www.baeldung.com/java-json)数据，树模型为我们提供了一种与分层数据交互的有效方式。JSON对象在树模型中表示为节点，这使得对JSON内容执行CRUD操作变得更加容易。

### 2.1 对象映射器

我们使用ObjectMapper类方法来读取JSON内容。ObjectMapper.readTree()方法反序列化JSON并构建JsonNode实例树。它将JSON源作为输入并返回创建的树模型的根节点。随后，我们可以使用根节点遍历整个JSON树。 

树模型不仅限于读取常规Java对象，JSON字段和树模型之间存在一对一的映射。因此，每个对象，无论是否POJO，都可以表示为一个节点。因此，我们喜欢将JSON内容表示为通用节点的灵活方法。

要了解更多信息，请参阅我们关于[Jackson ObjectMapper的文章。](https://www.baeldung.com/jackson-object-mapper-tutorial)

### 2.2 JsonNode

JsonNode类表示JSON树模型中的一个节点。它可以用以下数据类型表示JSON数据：Array、Binary、Boolean、Missing、Null、Number、Object、POJO、String。 这些数据类型在JsonNodeType枚举中定义。

## 3. 从JSON中获取key

我们在本文中使用以下JSON作为输入：

```json
{
    "Name": "Craig",
    "Age": 10,
    "BookInterests": [
        {
            "Book": "The Kite Runner",
            "Author": "Khaled Hosseini"
        },
        {
            "Book": "Harry Potter",
            "Author": "J. K. Rowling"
        }
    ],
    "FoodInterests": {
        "Breakfast": [
            {
                "Bread": "Whole wheat",
                "Beverage": "Fruit juice"
            },
            {
                "Sandwich": "Vegetable Sandwich",
                "Beverage": "Coffee"
            }
        ]
    }
}
```

在这里，我们使用String对象作为输入，但我们可以从不同的源读取JSON内容，例如File、byte[]、URL、InputStream、JsonParser等。

现在，让我们讨论从JSON中获取key的不同方法。

### 3.1 使用字段名称

我们可以在JsonNode实例上使用fieldNames()方法来获取嵌套的字段名称，它只返回直接嵌套字段的名称。

让我们用一个简单的例子来试试：

```java
public List<String> getKeysInJsonUsingJsonNodeFieldNames(String json, ObjectMapper mapper) throws JsonMappingException, JsonProcessingException {
	List<String> keys = new ArrayList<>();
	JsonNode jsonNode = mapper.readTree(json);
	Iterator<String> iterator = jsonNode.fieldNames();
	iterator.forEachRemaining(keys::add);
    
	return keys;
}
```

我们得到以下key：

```java
[Name, Age, BookInterests, FoodInterests]
```

为了获取所有内部嵌套节点，我们需要在每个级别的节点上调用fieldNames()方法：

```java
public List<String> getAllKeysInJsonUsingJsonNodeFieldNames(String json, ObjectMapper mapper) throws JsonMappingException, JsonProcessingException {
	List<String> keys = new ArrayList<>();
	JsonNode jsonNode = mapper.readTree(json);
	getAllKeysUsingJsonNodeFieldNames(jsonNode, keys);
    
	return keys;
}

private void getAllKeysUsingJsonNodeFields(JsonNode jsonNode, List<String> keys) {
	if (jsonNode.isObject()) {
		Iterator<Entry<String, JsonNode>> fields = jsonNode.fields();
		fields.forEachRemaining(field -> {
			keys.add(field.getKey());
			getAllKeysUsingJsonNodeFieldNames(field.getValue(), keys);
		});
	} else if (jsonNode.isArray()) {
		ArrayNode arrayField = (ArrayNode) jsonNode;
		arrayField.forEach(node -> getAllKeysUsingJsonNodeFieldNames(node, keys));
	}
}
```

首先，我们检查JSON值是对象还是数组。如果是，我们也遍历值对象以获取内部节点。结果，我们得到了JSON中存在的所有key名称：

```json
[Name, Age, BookInterests, Book, Author,
  Book, Author, FoodInterests, Breakfast, Bread, Beverage, Sandwich, Beverage]
```

在上面的示例中，我们还可以使用JsonNode类的fields()方法来获取字段对象，而不仅仅是字段名称：

```java
public List<String> getAllKeysInJsonUsingJsonNodeFields(String json, ObjectMapper mapper) throws JsonMappingException, JsonProcessingException {
	List<String> keys = new ArrayList<>();
	JsonNode jsonNode = mapper.readTree(json);
	getAllKeysUsingJsonNodeFields(jsonNode, keys);
    
	return keys;
}

private void getAllKeysUsingJsonNodeFieldNames(JsonNode jsonNode, List<String> keys) {
	if (jsonNode.isObject()) {
		Iterator<String> fieldNames = jsonNode.fieldNames();
		fieldNames.forEachRemaining(fieldName -> {
			keys.add(fieldName);
			getAllKeysUsingJsonNodeFieldNames(jsonNode.get(fieldName), keys);
		});
	} else if (jsonNode.isArray()) {
		ArrayNode arrayField = (ArrayNode) jsonNode;
		arrayField.forEach(node -> getAllKeysUsingJsonNodeFieldNames(node, keys));
	}
}
```

### 3.2 使用JsonParser

我们还可以使用JsonParser类进行低级JSON解析，JsonParser从给定的JSON内容创建一系列可迭代的标记。令牌类型在JsonToken类中指定为枚举，如下所示：

-   NOT_AVAILABLE
-   START_OBJECT
-   END_OBJECT
-   START_ARRAY
-   FIELD_NAME
-   VALUE_EMBEDDED_OBJECT
-   VALUE_STRING
-   VALUE_NUMBER_INT
-   VALUE_NUMBER_FLOAT
-   VALUE_TRUE
-   VALUE_FALSE
-   VALUE_NULL

在使用JsonParser进行迭代时，我们可以检查令牌类型并执行所需的操作。让我们获取示例JSON字符串的所有字段名称：

```java
public List<String> getKeysInJsonUsingJsonParser(String json, ObjectMapper mapper) throws IOException {
	List<String> keys = new ArrayList<>();
	JsonNode jsonNode = mapper.readTree(json);
	JsonParser jsonParser = jsonNode.traverse();
	while (!jsonParser.isClosed()) {
		if (jsonParser.nextToken() == JsonToken.FIELD_NAME) {
			keys.add((jsonParser.getCurrentName()));
		}
	}
    
	return keys;
}
```

在这里，我们使用了JsonNode类的traverse()方法来获取JsonParser对象。同样，我们也可以使用JsonFactory创建一个JsonParser对象：

```java
public List<String> getKeysInJsonUsingJsonParser(String json) throws JsonParseException, IOException {
	List<String> keys = new ArrayList<>();
	JsonFactory factory = new JsonFactory();
	JsonParser jsonParser = factory.createParser(json);
	while (!jsonParser.isClosed()) {
		if (jsonParser.nextToken() == JsonToken.FIELD_NAME) {
			keys.add((jsonParser.getCurrentName()));
		}
	}
    
	return keys;
}
```

结果，我们得到了从示例JSON内容中提取的所有key名称：

```json
[Name, Age, BookInterests, Book, Author,
  Book, Author, FoodInterests, Breakfast, Bread, Beverage, Sandwich, Beverage]
```

请注意，如果我们将其与本教程中介绍的其他方法进行比较，代码是多么简洁。

###  3.3 使用Map

我们可以使用ObjectMapper类的readValue()方法将JSON内容反序列化为Map。因此，我们可以在迭代Map对象时提取JSON元素。让我们尝试使用这种方法从我们的示例JSON中获取所有key名称：

```java
public List<String> getKeysInJsonUsingMaps(String json, ObjectMapper mapper) throws JsonMappingException, JsonProcessingException {
	List<String> keys = new ArrayList<>();
	Map<String, Object> jsonElements = mapper.readValue(json, new TypeReference<>() {
	});
	getAllKeys(jsonElements, keys);
    
	return keys;
}

private void getAllKeys(Map<String, Object> jsonElements, List<String> keys) {
	jsonElements.entrySet()
			.forEach(entry -> {
				keys.add(entry.getKey());
				if (entry.getValue() instanceof Map) {
					Map<String, Object> map = (Map<String, Object>) entry.getValue();
					getAllKeys(map, keys);
				} else if (entry.getValue() instanceof List) {
					List<?> list = (List<?>) entry.getValue();
					list.forEach(listEntry -> {
						if (listEntry instanceof Map) {
							Map<String, Object> map = (Map<String, Object>) listEntry;
							getAllKeys(map, keys);
						}
					});
				}
			});
}
```

在这种情况下，在获得顶级节点之后，我们遍历具有对象(Map)或数组值的JSON对象以获取嵌套节点。

## 4. 总结

我们介绍看到了从JSON内容中读取key名称的不同方法。此后，我们可以扩展本文中讨论的遍历逻辑，以根据需要对JSON元素执行其他操作。
