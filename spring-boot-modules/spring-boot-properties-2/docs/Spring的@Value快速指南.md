## 1. 概述

在本快速教程中，我们将介绍**Spring中的@Value注解**。

此注解可用于将值注入到Spring管理的bean中的字段，并且可以在字段或构造函数/方法参数级别应用。

## 2. 设置应用程序

为了描述此注解的不同用法，我们需要配置一个简单的Spring应用程序配置类。

自然地，我们需要一个属性文件来定义我们想要使用@Value注解注入的值。因此，我们首先需要在我们的配置类中定义一个@PropertySource-使用属性文件名。

让我们定义属性文件：

```properties
value.from.file=Value got from the file
priority=high
listOfValues=A,B,C
```

## 3. 使用示例

作为一个基本且几乎无用的示例，我们只能将注解中的“string value”注入字段：

```java
@Value("string value")
private String stringValue;
```

使用@PropertySource注解允许我们使用带有@Value注解的属性文件中的值。

在下面的示例中，我们从分配给该字段的文件中获取值：

```java
@Value("${value.from.file}")
private String valueFromFile;
```

我们还可以使用相同的语法从系统属性中设置值。

假设我们已经定义了一个名为systemValue的系统属性：

```java
@Value("${systemValue}")
private String systemValue;
```

可以为可能未定义的属性提供默认值。在这里，将注入“some default”作为someDefault的值：

```java
@Value("${unknown.param:some default}")
private String someDefault;
```

如果相同的属性被定义为系统属性并在属性文件中，则系统属性将被应用。

假设我们将属性priority定义为系统属性，值为“System property”，并在属性文件中定义为其他内容，则该值将是”System property“：

```java
@Value("${priority}")
private String prioritySystemProperty;
```

有时，我们需要注入一堆值，将它们定义为属性文件中单个属性的逗号分隔值或系统属性并注入数组会很方便。

在第2节中，我们在属性文件的listOfValues中定义了逗号分隔值，因此valuesArray将是["A", "B", "C"]：

```java
@Value("${listOfValues}")
private String[] valuesArray;
```

## 4. SpEL高级示例

我们也可以使用SpEL表达式来获取值。

如果我们有一个名为priority的系统属性，那么它的值将应用于以下字段：

```java
@Value("#{systemProperties['priority']}")
private String spelValue;
```

如果我们没有定义系统属性，那么将分配空值。

为了防止这种情况，我们可以在SpEL表达式中提供一个默认值，如果未定义系统属性，我们会为该字段获取一些默认值：

```java
@Value("#{systemProperties['unknown'] ?: 'some default'}")
private String spelSomeDefault;
```

此外，我们可以使用来自其他bean的字段值，假设我们有一个名为someBean的bean，其字段someValue等于10。然后，将10分配给该字段：

```java
@Value("#{someBean.someValue}")
private Integer someBeanValue;
```

我们可以操作属性来获取值列表，这里是字符串值A、B和C的列表：

```java
@Value("#{'${listOfValues}'.split(',')}")
private List<String> valuesList;
```

## 5. 将@Value与Map一起使用

我们还可以使用@Value注解来注入一个Map属性。

首先，我们需要在属性文件中以{key: 'value'}形式定义属性：

```properties
valuesMap={key1: '1', key2: '2', key3: '3'}
```

**请注意，Map中的值必须用单引号引起来**。

现在我们可以从属性文件中将这个值作为Map注入：

```java
@Value("#{${valuesMap}}")
private Map<String, Integer> valuesMap;
```

如果我们需要**获取Map中特定键的值**，我们所要做的就是**在表达式中添加键的名称**：

```java
@Value("#{${valuesMap}.key1}")
private Integer valuesMapKey1;
```

**如果我们不确定Map是否包含某个键，我们应该选择一个更安全的表达式**，该表达式不会引发异常，但在找不到键时将值设置为null：

```java
@Value("#{${valuesMap}['unknownKey']}")
private Integer unknownMapKey;
```

我们还可以**为可能不存在的属性或键设置默认值**：

```java
@Value("#{${unknownMap : {key1: '1', key2: '2'}}}")
private Map<String, Integer> unknownMap;

@Value("#{${valuesMap}['unknownKey'] ?: 5}")
private Integer unknownMapKeyWithDefaultValue;
```

Map条目也可以在注入之前进行过滤，假设我们只需要获取值大于1的那些条目：

```html
@Value("#{${valuesMap}.?[value>'1']}")
private Map<String, Integer> valuesMapFiltered;
```

我们还可以使用@Value注解来**注入所有当前系统属性**：

```java
@Value("#{systemProperties}")
private Map<String, String> systemPropertiesMap;
```

## 6. 在构造函数注入中使用@Value

当我们使用@Value注解时，我们不限于字段注入，**我们也可以将它与构造函数注入一起使用**。

让我们在实践中看看这一点：

```java
@Component
@PropertySource("classpath:values.properties")
public class PriorityProvider {

	private String priority;

	@Autowired
	public PriorityProvider(@Value("${priority:normal}") String priority) {
		this.priority = priority;
	}

	// standard getter
}
```

在上面的示例中，我们将priority直接注入到PriorityProvider的构造函数中。

请注意，我们还提供了一个默认值，以防找不到该属性。

## 7. 将@Value与Setter注入一起使用

类似于构造函数注入，**我们也可以将@Value与setter注入一起使用**。

让我们来看看：

```java
@Component
@PropertySource("classpath:values.properties")
public class CollectionProvider {

	private List<String> values = new ArrayList<>();

	@Autowired
	public void setValues(@Value("#{'${listOfValues}'.split(',')}") List<String> values) {
		this.values.addAll(values);
	}

	// standard getter
}
```

我们使用SpEL表达式将values集合注入setValues方法。

## 8. 总结

在本文中，我们介绍了将@Value注解用于文件中定义的简单属性、系统属性以及使用SpEL表达式计算的属性的各种可能性。