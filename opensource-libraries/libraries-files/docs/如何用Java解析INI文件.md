## 1. 概述

INI 文件是 Windows 或 MS-DOS 的初始化或配置文件。它们具有纯文本内容，其中包含 sections 中的键值对。虽然我们可能更喜欢使用Java的本机 .properties 文件或其他格式来配置我们的应用程序，但有时我们可能需要使用现有 INI 文件中的数据。

在本教程中，我们将了解几个可以帮助我们的库。我们还将了解一种使用 INI 文件中的数据填充[POJO的方法。](https://www.baeldung.com/java-pojo-class)

## 2. 创建示例 INI 文件

让我们从示例 INI 文件sample.ini 开始：

```plaintext
; for 16-bit app support
[fonts]
letter=bold
text-size=28

[background]
color=white

[RequestResult]
RequestCode=1

[ResponseResult]
ResultCode=0
```

该文件有四个部分，混合使用小写、kebab 和大驼峰命名。它具有字符串或数字值。

## 3. 使用ini4j解析 INI 文件

[ini4j](http://ini4j.sourceforge.net/index.html)是一个用于从 INI 文件中读取配置的轻量级库。自 2015 年以来就没有更新过。

### 3.1. 安装ini4j

为了能够使用ini4j库，首先，我们应该在我们的pom.xml中添加它的[依赖](https://search.maven.org/artifact/org.ini4j/ini4j)项：

```xml
<dependency>
    <groupId>org.ini4j</groupId>
    <artifactId>ini4j</artifactId>
    <version>0.5.4</version>
</dependency>
```

### 3.2. 在ini4j中打开一个 INI 文件

我们可以通过构造一个 Ini对象在ini4j 中打开一个 INI 文件：

```java
File fileToParse = new File("sample.ini");
Ini ini = new Ini(fileToParse);
```

该对象现在包含部分和键。

### 3.3. 阅读章节重点

我们可以使用Ini类上的get()函数 从我们的 INI 文件中读取一个部分中的键：

```java
assertThat(ini.get("fonts", "letter"))
  .isEqualTo("bold");
```

### 3.4. 转换为地图

让我们看看将整个 INI 文件转换为Map<String, Map<String, String>>是多么容易 ，它是一种表示 INI 文件层次结构的Java本机数据结构：

```java
public static Map<String, Map<String, String>> parseIniFile(File fileToParse)
  throws IOException {
    Ini ini = new Ini(fileToParse);
    return ini.entrySet().stream()
      .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
}
```

在这里，Ini 对象的 entrySet 本质上是String和 Map<String, String>的键值对 。Ini的内部表示 几乎是一个 Map ，因此可以使用 stream()和 toMap()收集器轻松将其转换为普通 Map 。

我们现在可以使用get()从这张地图中读取部分：

```java
assertThat(result.get("fonts").get("letter"))
  .isEqualTo("bold");
```

Ini类开箱即用，非常容易使用，并且可能不需要转换为 Map ，尽管我们稍后会找到它的用途。

但是， ini4j是一个旧库，看起来维护得不好。让我们考虑另一种选择。

## 4. 使用 Apache Commons 解析 INI 文件

Apache Commons 有一个更复杂的工具来处理 INI 文件。这能够对整个文件进行读写建模，尽管我们将只关注它的解析功能。

### 4.1. 安装公共配置

让我们首先在pom.xml中添加所需的[依赖](https://search.maven.org/artifact/org.apache.commons/commons-configuration2)项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-configuration2</artifactId>
    <version>2.8.0</version>
</dependency>
```

2.8.0 版本于 2022 年更新，比ini4j 更新。

### 4.2. 打开 INI 文件

我们可以通过声明INIConfiguration对象并将其传递给 Reader来打开 INI 文件 ：

```java
INIConfiguration iniConfiguration = new INIConfiguration();
try (FileReader fileReader = new FileReader(fileToParse)) {
    iniConfiguration.read(fileReader);
}
```

在这里，我们使用[try-with-resources](https://www.baeldung.com/java-try-with-resources)模式打开一个 FileReader ，然后要求INIConfiguration对象使用 read函数读取它。

### 4.3. 阅读章节重点

INIConfiguration 类有一个 getSection ()函数来读取一个部分和一个 返回对象上的getProperty()函数来读取一个键：

```java
String value = iniConfiguration.getSection("fonts")
  .getProperty("letter")
  .toString();
assertThat(value)
  .isEqualTo("bold");
```

我们应该注意 getProperty()返回 Object而不是 String，因此需要转换为 String。

### 4.4. 转换为地图

我们还可以像以前一样将 INIConfiguration转换为 Map。这比 ini4j复杂一点：

```java
Map<String, Map<String, String>> iniFileContents = new HashMap<>();

for (String section : iniConfiguration.getSections()) {
    Map<String, String> subSectionMap = new HashMap<>();
    SubnodeConfiguration confSection = iniConfiguration.getSection(section);
    Iterator<String> keyIterator = confSection.getKeys();
    while (keyIterator.hasNext()) {
        String key = keyIterator.next();
        String value = confSection.getProperty(key).toString();
        subSectionMap.put(key, value);
    }
    iniFileContents.put(section, subSectionMap);
}
```

要获取所有部分，我们需要使用 getSections()来查找它们的名称。然后 getSection()可以给我们每个部分。

然后我们可以使用为该部分提供所有键的迭代器，并使用getProperty()为每个键值对获取键值对。

虽然这里很难生成Map ，但更简单的数据结构的优点是我们可以隐藏系统其他部分对 INI 文件的解析。或者，我们可以将配置转换为 POJO。

## 5.INI文件转POJO

我们可以使用[Jackson](https://www.baeldung.com/category/json/jackson/)将我们的Map结构转换为 POJO。我们可以用反序列化注解修饰我们的 POJO，以帮助 Jackson 理解原始 INI 文件中的各种命名约定。POJO 比我们迄今为止看到的任何数据结构都更易于使用。

### 5.1. 进口杰克逊

我们需要将[Jackson](https://search.maven.org/artifact/com.fasterxml.jackson.core/jackson-databind)添加到我们的 pom.xml中：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-annotations</artifactId>
    <version>2.13.1</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-core</artifactId>
    <version>2.13.1</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.1</version>
</dependency>
```

### 5.2. 定义一些 POJO

我们示例文件的 字体部分使用 kebab-case 作为其属性。让我们定义一个类来表示该部分：

```java
@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public static class Fonts {
    private String letter;
    private int textSize;

    // getters and setters
}
```

这里我们使用JsonNaming注解来描述属性中使用的大小写。

类似地，RequestResult部分具有使用大驼峰式大小写的属性：

```java
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public static class RequestResult {
    private int requestCode;

    // getters and setters
}
```

部分名称本身有多种情况，因此我们可以在父对象中声明每个部分，使用JsonProperty注解显示与默认小驼峰命名的偏差：

```java
public class MyConfiguration {
    private Fonts fonts;
    private Background background;

    @JsonProperty("RequestResult")
    private RequestResult requestResult;

    @JsonProperty("ResponseResult")
    private ResponseResult responseResult;

    // getters and setters
}
```

### 5.3. 从 Map 转换为 POJO

现在我们可以使用我们的任何一个库将 INI 文件作为 Map读取 ，并且可以将文件的内容建模为 POJO，我们可以使用 Jackson ObjectMapper来执行转换：

```java
ObjectMapper objectMapper = new ObjectMapper();
Map<String, Map<String, String>> iniKeys = parseIniFile(TEST_FILE);
MyConfiguration config = objectMapper.convertValue(iniKeys, MyConfiguration.class);
```

让我们检查整个文件是否正确加载：

```java
assertThat(config.getFonts().getLetter()).isEqualTo("bold");
assertThat(config.getFonts().getTextSize()).isEqualTo(28);
assertThat(config.getBackground().getColor()).isEqualTo("white");
assertThat(config.getRequestResult().getRequestCode()).isEqualTo(1);
assertThat(config.getResponseResult().getResultCode()).isZero();
```

我们应该注意到数字属性，例如textSize和requestCode已经作为数字加载到我们的 POJO 中。

## 6. 库和方法的比较

ini4j库使用 起来非常简单，本质上是一个简单的Map- like 结构。但是，这是一个没有定期更新的旧库。

Apache Commons 解决方案功能更全面，并且定期更新，但需要做更多的工作才能使用。

## 七. 总结

在本文中，我们看到了如何使用几个开源库读取 INI 文件。我们看到了如何读取单个键以及如何遍历整个文件以生成Map。

然后我们看到了如何使用 Jackson 从 Map转换为 POJO。