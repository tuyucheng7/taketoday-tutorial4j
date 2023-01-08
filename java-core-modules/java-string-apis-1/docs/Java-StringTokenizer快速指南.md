## 1. 概述

在这篇简短的文章中，我们将探索Java中的一个基本类—— [StringTokenizer](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/StringTokenizer.html)。

## 2.StringTokenizer _ 

StringTokenizer类帮助我们将字符串拆分为多个标记。

[StreamTokenizer](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/StreamTokenizer.html)提供类似的功能，但StringTokenizer的标记化方法比[StreamTokenizer](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/StreamTokenizer.html)类使用的方法简单得多StringTokenizer的方法不区分标识符、数字和带引号的字符串，也不识别和跳过注解。

分隔符集(分隔标记的字符)可以在创建时指定，也可以在每个标记的基础上指定。

## 3. 使用StringTokenizer

使用StringTokenizer的最简单示例是根据指定的分隔符拆分String 。

在这个快速示例中，我们将拆分参数 String 并将标记添加到列表中：


```java
public List<String> getTokens(String str) {
    List<String> tokens = new ArrayList<>();
    StringTokenizer tokenizer = new StringTokenizer(str, ",");
    while (tokenizer.hasMoreElements()) {
        tokens.add(tokenizer.nextToken());
    }
    return tokens;
}

```

请注意我们是如何根据定界符 ' , ' 将字符串分解为标记列表的。然后在循环中，使用tokens.add()方法；我们将每个标记添加到ArrayList 中。

例如，如果用户输入“ Welcome,to,baeldung.com ”，此方法应返回包含三个词片段的列表，如“ Welcome ”、“ to ”和“ baeldung.com ”。

### 3.1.Java8 方法

由于StringTokenizer实现了Enumeration<Object>接口，我们可以将它与 J ava的Collections接口一起使用。

如果我们考虑前面的示例，我们可以使用Collections.list()方法和Stream API检索同一组标记：

```java
public List<String> getTokensWithCollection(String str) {
    return Collections.list(new StringTokenizer(str, ",")).stream()
      .map(token -> (String) token)
      .collect(Collectors.toList());
}
```

在这里，我们将StringTokenizer本身作为参数传递给Collections.list()方法。

这里要注意的是，由于Enumeration是一个Object类型，我们需要将标记类型转换为String类型(即取决于实现；如果我们使用Integer/Float列表，那么我们需要类型转换与整数/浮点数)。

### 3.2. StringTokenizer的变体 

StringTokenizer在默认构造函数旁边带有两个重载构造函数：StringTokenizer(String str)和StringTokenizer(String str, String delim, boolean returnDelims)：

StringTokenizer(String str, String delim, boolean returnDelims)需要一个额外的布尔输入。如果布尔值为true，则StringTokenizer会将分隔符本身视为标记并将其添加到其内部标记池中。

StringTokenizer(String str)是前面例子的快捷方式；它在内部调用另一个构造函数，硬编码定界符为“tnrf”，布尔值为false。

### 3.3. 代币定制

StringTokenizer还带有一个重载的nextToken()方法，该方法将字符串片段作为输入。这个String片段充当一组额外的分隔符；基于哪些令牌再次重新组织。

例如，如果我们可以在nextToken()方法中传递 ' e ' 以根据分隔符 ' e '进一步拆分字符串：

```java
tokens.add(tokenizer.nextToken("e"));
```

因此，对于给定的“ Hello,baeldung.com ”字符串，我们将生成以下标记：

```plaintext
H
llo
ba
ldung.com
```

### 3.4. 令牌长度

要计算可用的令牌数量，我们可以使用StringTokenizer的countTokens方法：

```java
int tokenLength = tokens.countTokens();
```

### 3.5. 从 CSV 文件读取

现在，让我们尝试在实际用例中使用StringTokenizer 。

在某些情况下，我们会尝试从 CSV 文件中读取数据并根据用户给定的分隔符解析数据。

使用StringTokenizer，我们可以轻松实现：

```java
public List<String> getTokensFromFile( String path , String delim ) {
    List<String> tokens = new ArrayList<>();
    String currLine = "";
    StringTokenizer tokenizer;
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(Application.class.getResourceAsStream( 
          "/" + path )))) {
        while (( currLine = br.readLine()) != null ) {
            tokenizer = new StringTokenizer( currLine , delim );
            while (tokenizer.hasMoreElements()) {
                tokens.add(tokenizer.nextToken());
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return tokens;
}
```

在这里，该函数有两个参数；一个作为 CSV 文件名(即从资源[src -> main -> resources]文件夹中读取)，另一个作为分隔符。

基于这两个参数，逐行读取 CSV 数据，并使用StringTokenizer对每一行进行标记化。

例如，我们在 CSV 中放入了以下内容：

```plaintext
1|IND|India
2|MY|Malaysia
3|AU|Australia
```

因此，应生成以下令牌：

```plaintext
1
IND
India
2
MY
Malaysia
3
AU
Australia
```

### 3.6. 测试

现在，让我们创建一个快速测试用例：

```java
public class TokenizerTest {

    private MyTokenizer myTokenizer = new MyTokenizer();
    private List<String> expectedTokensForString = Arrays.asList(
      "Welcome" , "to" , "baeldung.com" );
    private List<String> expectedTokensForFile = Arrays.asList(
      "1" , "IND" , "India" , 
      "2" , "MY" , "Malaysia" , 
      "3", "AU" , "Australia" );

    @Test
    public void givenString_thenGetListOfString() {
        String str = "Welcome,to,baeldung.com";
        List<String> actualTokens = myTokenizer.getTokens( str );
 
        assertEquals( expectedTokensForString, actualTokens );
    }

    @Test
    public void givenFile_thenGetListOfString() {
        List<String> actualTokens = myTokenizer.getTokensFromFile( 
          "data.csv", "|" );
 
        assertEquals( expectedTokensForFile , actualTokens );
    }
}
```

## 4。总结

在本快速教程中，我们查看了一些使用核心JavaStringTokenizer的实际示例。