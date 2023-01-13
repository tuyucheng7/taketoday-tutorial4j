## 1. 概述

[FastJson](https://github.com/alibaba/fastjson)是一个轻量级的Java库，用于有效地将 JSON 字符串转换为Java对象，反之亦然。

在本文中，我们将深入探讨 FastJson 库的几个具体和实际应用。

## 2.Maven配置

为了开始使用 FastJson，我们首先需要将它添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.13</version>
</dependency>

```

快速说明一下——[这是 Maven Central 上最新版本](https://search.maven.org/classic/#search|gav|1|g%3A"com.alibaba" AND a%3A"fastjson")的库。

## 3.将Java对象转换为JSON格式

让我们定义以下PersonJavabean：

```java
public class Person {
    
    @JSONField(name = "AGE")
    private int age;

    @JSONField(name = "FULL NAME")
    private String fullName;

    @JSONField(name = "DATE OF BIRTH")
    private Date dateOfBirth;

    public Person(int age, String fullName, Date dateOfBirth) {
        super();
        this.age = age;
        this.fullName= fullName;
        this.dateOfBirth = dateOfBirth;
    }

    // standard getters & setters
}
```

我们可以使用JSON.toJSONString()将Java对象转换为 JSON 字符串：

```java
private List<Person> listOfPersons = new ArrayList<Person>();

@Before
public void setUp() {
    listOfPersons.add(new Person(15, "John Doe", new Date()));
    listOfPersons.add(new Person(20, "Janette Doe", new Date()));
}

@Test
public void whenJavaList_thanConvertToJsonCorrect() {
    String jsonOutput= JSON.toJSONString(listOfPersons);
}
```

结果如下：

```javascript
[  
    {  
        "AGE":15,
        "DATE OF BIRTH":1468962431394,
        "FULL NAME":"John Doe"
    },
    {  
        "AGE":20,
        "DATE OF BIRTH":1468962431394,
        "FULL NAME":"Janette Doe"
    }
]
```

我们还可以更进一步，开始自定义输出并控制排序、日期格式或序列化标志等内容。

例如——让我们更新 bean 并添加更多字段：

```java
@JSONField(name="AGE", serialize=false)
private int age;

@JSONField(name="LAST NAME", ordinal = 2)
private String lastName;

@JSONField(name="FIRST NAME", ordinal = 1)
private String firstName;

@JSONField(name="DATE OF BIRTH", format="dd/MM/yyyy", ordinal = 3)
private Date dateOfBirth;
```

这是我们可以与@JSONField注解一起使用的最基本参数的列表，以便自定义转换过程：

-   参数格式 用于正确格式化日期属性
-   默认情况下，FastJson 库会完全序列化Javabean，但我们可以利用参数serialize 来忽略特定字段的序列化
-   参数ordinal 用于指定字段顺序

这是新的输出：

```javascript
[
    {
        "FIRST NAME":"Doe",
        "LAST NAME":"Jhon",
        "DATE OF BIRTH":"19/07/2016"
    },
    {
        "FIRST NAME":"Doe",
        "LAST NAME":"Janette",
        "DATE OF BIRTH":"19/07/2016"
    }
]
```

FastJson 还支持一个非常有趣的BeanToArray序列化特性：

```java
String jsonOutput= JSON.toJSONString(listOfPersons, SerializerFeature.BeanToArray);
```

在这种情况下，输出如下所示：

```javascript
[
    [
        15,
        1469003271063,
        "John Doe"
    ],
    [
        20,
        1469003271063,
        "Janette Doe"
    ]
]
```

## 4. 创建 JSON 对象

与[其他 JSON 库](https://www.baeldung.com/java-json)一样，从头开始创建 JSON 对象非常简单，只需组合JSONObject 和JSONArray 对象即可：

```java
@Test
public void whenGenerateJson_thanGenerationCorrect() throws ParseException {
    JSONArray jsonArray = new JSONArray();
    for (int i = 0; i < 2; i++) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("AGE", 10);
        jsonObject.put("FULL NAME", "Doe " + i);
        jsonObject.put("DATE OF BIRTH", "2016/12/12 12:12:12");
        jsonArray.add(jsonObject);
    }
    String jsonOutput = jsonArray.toJSONString();
}
```

下面是这里的输出：

```javascript
[
   {
      "AGE":"10",
      "DATE OF BIRTH":"2016/12/12 12:12:12",
      "FULL NAME":"Doe 0"
   },
   {
      "AGE":"10",
      "DATE OF BIRTH":"2016/12/12 12:12:12",
      "FULL NAME":"Doe 1"
   }
]
```

## 5. 将 JSON 字符串解析为Java对象

现在我们知道如何从头开始创建 JSON 对象，以及如何将Java对象转换为其 JSON 表示，让我们将重点放在如何解析 JSON 表示上：

```java
@Test
public void whenJson_thanConvertToObjectCorrect() {
    Person person = new Person(20, "John", "Doe", new Date());
    String jsonObject = JSON.toJSONString(person);
    Person newPerson = JSON.parseObject(jsonObject, Person.class);
    
    assertEquals(newPerson.getAge(), 0); // if we set serialize to false
    assertEquals(newPerson.getFullName(), listOfPersons.get(0).getFullName());
}
```

我们可以使用JSON.parseObject()从 JSON 字符串中获取Java对象。

请注意，如果你已经声明了自己的参数化构造函数，则必须定义一个无参数或默认构造函数，否则将抛出com.alibaba.fastjson.JSONException 。

这是这个简单测试的输出：

```java
Person [age=20, fullName=John Doe, dateOfBirth=Wed Jul 20 08:51:12 WEST 2016]
```

通过在@JSONField注解中使用反序列化选项，我们可以忽略特定字段的反序列化，在这种情况下，默认值将自动应用于被忽略的字段：

```java
@JSONField(name = "DATE OF BIRTH", deserialize=false)
private Date dateOfBirth;
```

这是新创建的对象：

```plaintext
Person [age=20, fullName=John Doe, dateOfBirth=null]
```

## 6.使用ContextValueFilter配置JSON转换

在某些场景下，我们可能需要对从Java对象到 JSON 格式的转换过程有更多的控制。

在这种情况下，我们可以利用ContextValueFilter对象对转换流应用额外的过滤和自定义处理：

```java
@Test
public void givenContextFilter_whenJavaObject_thanJsonCorrect() {
    ContextValueFilter valueFilter = new ContextValueFilter () {
        public Object process(
          BeanContext context, Object object, String name, Object value) {
            if (name.equals("DATE OF BIRTH")) {
                return "NOT TO DISCLOSE";
            }
            if (value.equals("John")) {
                return ((String) value).toUpperCase();
            } else {
                return null;
            }
        }
    };
    String jsonOutput = JSON.toJSONString(listOfPersons, valueFilter);
}
```

在这个例子中，我们隐藏了DATE OF BIRTH字段，通过强制一个常量值，我们也忽略了所有不是John或Doe 的字段：


```javascript
[
    {
        "FULL NAME":"JOHN DOE",
        "DATE OF BIRTH":"NOT TO DISCLOSE"
    }
]
```

如你所见，这是一个非常基本的示例，但你当然也可以将相同的概念用于更复杂的场景——将 FastJson 提供的这些强大而轻量级的工具集结合到一个真实世界的项目中。

## 7. 使用NameFilter和SerializeConfig

FastJson 提供了一组工具来在处理任意对象时自定义你的 JSON 操作——我们没有源代码的对象。

假设我们有一个编译版本的PersonJavabean，最初在本文中声明，我们需要对字段命名和基本格式进行一些改进：

```java
@Test
public void givenSerializeConfig_whenJavaObject_thanJsonCorrect() {
    NameFilter formatName = new NameFilter() {
        public String process(Object object, String name, Object value) {
            return name.toLowerCase().replace(" ", "_");
        }
    };
    
    SerializeConfig.getGlobalInstance().addFilter(Person.class,  formatName);
    String jsonOutput = 
      JSON.toJSONStringWithDateFormat(listOfPersons, "yyyy-MM-dd");
}
```

我们已经使用NameFilter匿名类声明了formatName过滤器来处理字段名称。新创建的过滤器关联到Person类，然后添加到一个全局实例——这基本上是SerializeConfig类中的一个静态属性。

现在我们可以轻松地将我们的对象转换为 JSON 格式，如本文前面所示。

请注意，我们使用toJSONStringWithDateFormat()而不是toJSONString()来快速对日期字段应用相同的格式规则。

这是输出：

```javascript
[  
    {  
        "full_name":"John Doe",
        "date_of_birth":"2016-07-21"
    },
    {  
        "full_name":"Janette Doe",
        "date_of_birth":"2016-07-21"
    }
]
```

如你所见——字段名称已更改，日期值的格式也正确。

结合使用SerializeFilter和ContextValueFilter 可以完全控制任意复杂Java对象的转换过程。

## 八. 总结

在本文中，我们展示了如何使用 FastJson 将Javabean 转换为 JSON 字符串，以及如何进行相反的转换。我们还展示了如何使用FastJson的一些核心功能来自定义 JSON 输出。

如你看到的，the library offers a relatively simple to use but still very powerful API. JSON.toJSONString and JSON.parseObject are all you need to use in order to meet most of your needs – if not all.