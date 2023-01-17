## 1. 概述

在本教程中，我们将快速概述[ANTLR](http://www.antlr.org/)解析器生成器并展示一些实际应用程序。

## 2.ANTLR

ANTLR (ANother Tool for Language Recognition) 是一个处理结构化文本的工具。

它通过让我们访问语言处理原语(如词法分析器、语法和解析器)以及根据它们处理文本的运行时来实现这一点。

它通常用于构建工具和框架。例如，Hibernate 使用 ANTLR 来解析和处理 HQL 查询，Elasticsearch 使用它来实现 Painless。

Java 只是一种绑定。ANTLR 还为 C#、Python、JavaScript、Go、C++ 和 Swift 提供绑定。

## 三、配置

首先，让我们从添加 [antlr-runtime](https://search.maven.org/classic/#search|gav|1|g%3A"org.antlr" AND a%3A"antlr4-runtime") 到我们的pom.xml开始：

```xml
<dependency>
    <groupId>org.antlr</groupId>
    <artifactId>antlr4-runtime</artifactId>
    <version>4.7.1</version>
</dependency>
```

还有[antlr-maven-plugin](https://search.maven.org/classic/#search|ga|1|org.antlr antlr-maven-plugin)：

```xml
<plugin>
    <groupId>org.antlr</groupId>
    <artifactId>antlr4-maven-plugin</artifactId>
    <version>4.7.1</version>
    <executions>
        <execution>
            <goals>
                <goal>antlr4</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

从我们指定的语法生成代码是插件的工作。

## 4. 它是如何工作的？

基本上，当我们想要使用 [ANTLR Maven 插件](http://www.antlr.org/api/maven-plugin/latest/)创建解析器时，我们需要遵循三个简单的步骤：

-   准备语法文件
-   生成资源
-   创建监听器

那么，让我们看看这些步骤的实际效果。

## 5.使用现有语法

让我们首先使用 ANTLR 来分析带有错误外壳的方法的代码：

```java
public class SampleClass {
 
    public void DoSomethingElse() {
        //...
    }
}
```

简而言之，我们将验证代码中的所有方法名称是否以小写字母开头。

### 5.1. 准备语法文件

好的是已经有几个语法文件可以满足我们的目的。

让我们使用在[ANTLR 的 Github 语法库](https://github.com/antlr/grammars-v4/tree/master/java/java8)中找到[的 Java8.g4 语法文件](https://github.com/antlr/codebuff/blob/master/grammars/org/antlr/codebuff/Java8.g4)。

我们可以创建src/main/antlr4目录并在那里下载它。

### 5.2. 生成源

ANTLR 通过生成与我们提供的语法文件相对应的Java代码来工作，maven 插件使它变得简单：

```bash
mvn package
```

默认情况下，这会在 target/generated-sources/antlr4目录下生成几个文件：

-   Java8.interp
-   Java8Listener.java
-   Java8BaseListener.java
-   Java8Lexer.java
-   Java8Lexer.interp
-   Java8Parser.java
-   Java8.tokens
-   Java8Lexer.tokens

请注意，这些文件的名称是基于语法文件的名称。

稍后测试时，我们将需要 Java8Lexer 和 Java8Parser文件。不过现在，我们需要 Java8BaseListener来创建我们的MethodUppercaseListener。

### 5.3. 创建 方法UppercaseListener

基于我们使用的 Java8 语法，Java8BaseListener有几个我们可以覆盖的方法，每个方法对应于语法文件中的一个标题。

例如，语法定义方法名称、参数列表和 throws 子句如下：

```java
methodDeclarator
	:	Identifier '(' formalParameterList? ')' dims?
	;
```

因此，Java8BaseListener 有一个方法 enterMethodDeclarator ，每次遇到此模式时都会调用该方法。

因此，让我们覆盖enterMethodDeclarator，取出 Identifier，并执行我们的检查：

```java
public class UppercaseMethodListener extends Java8BaseListener {

    private List<String> errors = new ArrayList<>();

    // ... getter for errors
 
    @Override
    public void enterMethodDeclarator(Java8Parser.MethodDeclaratorContext ctx) {
        TerminalNode node = ctx.Identifier();
        String methodName = node.getText();

        if (Character.isUpperCase(methodName.charAt(0))) {
            String error = String.format("Method %s is uppercased!", methodName);
            errors.add(error);
        }
    }
}
```

### 5.4. 测试

现在，让我们做一些测试。首先，我们构造词法分析器：

```java
String javaClassContent = "public class SampleClass { void DoSomething(){} }";
Java8Lexer java8Lexer = new Java8Lexer(CharStreams.fromString(javaClassContent));
```

然后，我们实例化解析器：

```java
CommonTokenStream tokens = new CommonTokenStream(lexer);
Java8Parser parser = new Java8Parser(tokens);
ParseTree tree = parser.compilationUnit();
```

然后，步行者和听者：

```java
ParseTreeWalker walker = new ParseTreeWalker();
UppercaseMethodListener listener= new UppercaseMethodListener();
```

最后，我们告诉 ANTLR 遍历我们的示例类：

```java
walker.walk(listener, tree);

assertThat(listener.getErrors().size(), is(1));
assertThat(listener.getErrors().get(0),
  is("Method DoSomething is uppercased!"));
```

## 6. 建立我们的语法

现在，让我们尝试一些更复杂的事情，比如解析日志文件：

```bash
2018-May-05 14:20:18 INFO some error occurred
2018-May-05 14:20:19 INFO yet another error
2018-May-05 14:20:20 INFO some method started
2018-May-05 14:20:21 DEBUG another method started
2018-May-05 14:20:21 DEBUG entering awesome method
2018-May-05 14:20:24 ERROR Bad thing happened
```

因为我们有自定义日志格式，所以我们首先需要创建自己的语法。

### 6.1. 准备语法文件

首先，让我们看看是否可以创建文件中每个日志行的内容的心理地图。

<日期时间> <级别> <消息>

或者，如果我们再深入一层，我们可能会说：

<日期时间> := <年><短划线><月><短划线><日> …

等等。考虑这一点很重要，这样我们就可以决定我们想要解析文本的粒度级别。

语法文件基本上是一组词法分析器和解析器规则。简单地说，词法分析器规则描述语法的句法，而解析器规则描述语义。

让我们从定义片段开始，这些片段是 词法分析器规则的可重用构建块。

```bash
fragment DIGIT : [0-9];
fragment TWODIGIT : DIGIT DIGIT;
fragment LETTER : [A-Za-z];
```

接下来，让我们定义剩余词法分析器规则：

```bash
DATE : TWODIGIT TWODIGIT '-' LETTER LETTER LETTER '-' TWODIGIT;
TIME : TWODIGIT ':' TWODIGIT ':' TWODIGIT;
TEXT   : LETTER+ ;
CRLF : 'r'? 'n' | 'r';
```

有了这些构建块，我们就可以为基本结构构建解析器规则：

```bash
log : entry+;
entry : timestamp ' ' level ' ' message CRLF;
```

然后我们将添加 时间戳的详细信息：

```bash
timestamp : DATE ' ' TIME;
```

对于级别：

```bash
level : 'ERROR' | 'INFO' | 'DEBUG';
```

对于 消息：

```bash
message : (TEXT | ' ')+;
```

就是这样！我们的语法可以使用了。我们还是和之前一样放在 src/main/antlr4目录下。

### 6.2. 生成源

回想一下，这只是一个快速的 mvn package，这将 根据我们的语法名称创建几个文件，如LogBaseListener、 LogParser等。

### 6.3. 创建我们的日志监听器

现在，我们准备好实现我们的侦听器，我们最终将使用它来将日志文件解析为Java对象。

那么，让我们从一个简单的日志条目模型类开始：

```java
public class LogEntry {

    private LogLevel level;
    private String message;
    private LocalDateTime timestamp;
   
    // getters and setters
}
```

现在，我们需要像以前一样继承 LogBaseListener：

```java
public class LogListener extends LogBaseListener {

    private List<LogEntry> entries = new ArrayList<>();
    private LogEntry current;
```

current 将保留当前日志行，我们可以在每次输入logEntry 时重新初始化， 再次基于我们的语法：

```java
    @Override
    public void enterEntry(LogParser.EntryContext ctx) {
        this.current = new LogEntry();
    }
```

接下来，我们将使用 enterTimestamp、 enterLevel和 enterMessage 来设置适当的 LogEntry属性：

```java
    @Override
    public void enterTimestamp(LogParser.TimestampContext ctx) {
        this.current.setTimestamp(
          LocalDateTime.parse(ctx.getText(), DEFAULT_DATETIME_FORMATTER));
    }
    
    @Override
    public void enterMessage(LogParser.MessageContext ctx) {
        this.current.setMessage(ctx.getText());
    }

    @Override
    public void enterLevel(LogParser.LevelContext ctx) {
        this.current.setLevel(LogLevel.valueOf(ctx.getText()));
    }
```

最后，让我们使用exitEntry 方法来创建和添加我们的新LogEntry：

```java
    @Override
    public void exitLogEntry(LogParser.EntryContext ctx) {
        this.entries.add(this.current);
    }
```

请注意，顺便说一句，我们的 LogListener 不是线程安全的！

### 6.4. 测试

现在我们可以像上次一样再次测试：

```java
@Test
public void whenLogContainsOneErrorLogEntry_thenOneErrorIsReturned()
  throws Exception {
 
    String logLine ="2018-May-05 14:20:24 ERROR Bad thing happened";

    // instantiate the lexer, the parser, and the walker
    LogListener listener = new LogListener();
    walker.walk(listener, logParser.log());
    LogEntry entry = listener.getEntries().get(0);
 
    assertThat(entry.getLevel(), is(LogLevel.ERROR));
    assertThat(entry.getMessage(), is("Bad thing happened"));
    assertThat(entry.getTimestamp(), is(LocalDateTime.of(2018,5,5,14,20,24)));
}
```

## 七. 总结

在本文中，我们重点介绍了如何使用 ANTLR 为自己的语言创建自定义解析器。

我们还看到了如何使用现有的语法文件并将它们应用于代码检查等非常简单的任务。