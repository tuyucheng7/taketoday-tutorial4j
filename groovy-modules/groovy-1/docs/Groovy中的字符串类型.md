## 一、概述

在本教程中，我们将仔细研究[Groovy](https://www.baeldung.com/groovy-language)中的几种字符串类型，包括单引号、双引号、三引号和斜线字符串。

我们还将探讨Groovy对特殊字符、多行、正则表达式、转义和变量插值的字符串支持。

## 2. 增强 java.lang.String

最好首先说明，由于Groovy基于 Java，它具有Java的所有 String 功能，如串联、String API，以及 String 常量池的固有优势。

让我们首先看看Groovy如何扩展其中的一些基础知识。

### 2.1. 字符串连接

字符串连接只是两个字符串的组合：

```groovy
def first = 'first'
def second = "second"        
def concatenation = first + second
assertEquals('firstsecond', concatenation)
```

Groovy 在此基础上构建了其他几种字符串类型，稍后我们将对其进行介绍。 请注意，我们可以互换地连接每种类型。

### 2.2. 字符串插值

现在，Java通过printf提供了一些非常基本的模板，但Groovy更深入，提供 字符串插值， 即使用变量对字符串进行模板化的过程：

```groovy
def name = "Kacper"
def result = "Hello ${name}!"
assertEquals("Hello Kacper!", result.toString())
```

虽然Groovy支持所有字符串类型的连接，但它只为某些类型提供插值。

### 2.3. G弦

但是隐藏在这个例子中的是一个小问题——我们为什么要调用toString()？

实际上， 结果 不是 String类型，即使它看起来像。

因为String 类是 final，所以支持插值的Groovy字符串类 GString不会将其子类化。换句话说，为了让Groovy提供这种增强功能，它有自己的字符串类 GString，它不能从 String 扩展。

简单地说，如果我们这样做：

```groovy
assertEquals("Hello Kacper!", result)
```

这会调用assertEquals(Object, Object)， 我们得到：

```plaintext
java.lang.AssertionError: expected: java.lang.String<Hello Kacper!>
  but was: org.codehaus.groovy.runtime.GStringImpl<Hello Kacper!>
Expected :java.lang.String<Hello Kacper!> 
Actual   :org.codehaus.groovy.runtime.GStringImpl<Hello Kacper!>
```

## 3. 单引号字符串

Groovy 中最简单的字符串可能是带有单引号的字符串：

```groovy
def example = 'Hello world'
```

在幕后，这些只是普通的旧Java字符串，当我们需要在字符串中包含引号时，它们会派上用场。

代替：

```groovy
def hardToRead = "Kacper loves "Lord of the Rings""
```

我们可以轻松地将一个字符串与另一个字符串连接起来：

```groovy
def easyToRead = 'Kacper loves "Lord of the Rings"'
```

因为我们可以像这样交换引号类型，所以它减少了转义引号的需要。

## 4. 三重单引号字符串

三重单引号字符串在定义多行内容的上下文中很有帮助。

例如，假设我们有一些 JSON表示为字符串：

```java
{
    "name": "John",
    "age": 20,
    "birthDate": null
}
```

我们不需要求助于连接和显式换行符来表示这一点。

相反，让我们使用三重单引号字符串：

```groovy
def jsonContent = '''
{
    "name": "John",
    "age": 20,
    "birthDate": null
}
'''
```

Groovy 将其存储为一个简单的JavaString 并为我们添加所需的连接和换行符。

不过，还有一项挑战需要克服。

通常为了代码的可读性，我们缩进我们的代码：

```groovy
def triple = '''
    firstline
    secondline
'''
```

但是三重单引号字符串保留了空格。这意味着上面的字符串实际上是：

```groovy
(newline)
    firstline(newline)
    secondline(newline)
```

不是：

```
  ``firstline(newline)``  ``secondline(newline)
```

也许就像我们打算的那样。

请继续关注我们如何摆脱它们。

### 4.1. 换行符

让我们确认我们之前的字符串以换行符开头：

```groovy
assertTrue(triple.startsWith("n"))
```

可以剥离该字符。为防止这种情况，我们需要将单个反斜杠作为第一个和最后一个字符：

```groovy
def triple = '''
    firstline
    secondline
'''
```

现在，我们至少有：

```
  ``firstline(newline)``  ``secondline(newline)
```

解决一个问题，再解决一个问题。

### 4.2. 去除代码缩进

接下来，让我们处理缩进。 我们希望保留我们的格式，但删除不必要的空白字符。

Groovy 字符串 API 来拯救！

要删除字符串每一行的前导空格，我们可以使用Groovy默认方法之一 String#stripIndent()：

```groovy
def triple = '''
    firstline
    secondline'''.stripIndent()
assertEquals("firstlinensecondline", triple)
```

请注意，通过将刻度向上移动一行，我们还删除了尾随的换行符。 

### 4.3. 相对缩进

我们应该记住stripIndent 不叫 stripWhitespace。

stripIndent确定字符串中缩短的非空白行的缩进量。

所以，让我们为我们的三重变量稍微改变一下缩进：

```groovy
class TripleSingleQuotedString {

    @Test
    void 'triple single quoted with multiline string with last line with only whitespaces'() {
        def triple = '''
            firstline
                secondline
        '''.stripIndent()

        // ... use triple
    }
}
```

打印triple会告诉我们：

```bash
firstline
    secondline
```

由于firstline 是缩进最少的非空白行，因此它变为零缩进，而 secondline仍然相对于它缩进。

另请注意，这一次，我们将使用斜杠删除尾随空格，就像我们之前看到的那样。

### 4.4. 用stripMargin()剥离

为了获得更多控制，我们可以使用 | 告诉Groovy从哪里开始该行。和stripMargin：

```groovy
def triple = '''
    |firstline
    |secondline'''.stripMargin()
```

哪个会显示：

```bash
firstline
secondline
```

管道说明字符串的那一行真正开始的地方。

此外，我们可以将Character或CharSequence作为参数传递给带有自定义分隔符的stripMargin 。

太棒了，我们去掉了所有不必要的空格，我们的字符串只包含我们想要的！

### 4.5. 转义特殊字符

由于三重单引号字符串的所有优点，自然会需要转义作为字符串一部分的单引号和反斜杠。 

为了表示特殊字符，我们还需要用反斜杠对它们进行转义。最常见的特殊字符是换行符 ( n ) 和制表符 ( t )。

例如：

```groovy
def specialCharacters = '''hello 'John'. This is backslash -  nSecond line starts here'''
```

将导致：

```bash
hello 'John'. This is backslash - 
Second line starts here
```

我们需要记住一些，即：

-    t——制表
-   n – 换行符
-    b——退格
-   r——回车
-    ——反斜杠
-   f – 换页
-   ' ——单引号

## 5. 双引号字符串

虽然双引号字符串也只是Java字符串，但它们的特殊功能是插值。当双引号字符串包含插值字符时，Groovy 将JavaString切换为GString。

### 5.1. GString和惰性求值

我们可以通过用${}或$包围表达式来插入双引号字符串。

不过，它的求值是惰性的——在传递给需要 String 的方法之前，它 不会被转换为String：

```groovy
def string = "example"
def stringWithExpression = "example${2}"
assertTrue(string instanceof String)
assertTrue(stringWithExpression instanceof GString)
assertTrue(stringWithExpression.toString() instanceof String)
```

### 5.2. 引用变量的占位符

我们可能想要对插值做的第一件事是向它发送一个变量引用：

```groovy
def name = "John"
def helloName = "Hello $name!"
assertEquals("Hello John!", helloName.toString())
```

### 5.2. 带有表达式的占位符

但是，我们也可以给它表达式：

```groovy
def result = "result is ${2  2}"    
assertEquals("result is 4", result.toString())
```

我们可以将 even 语句放入占位符中，但这被认为是不好的做法。

### 5.3. 带点运算符的占位符

我们甚至可以在字符串中遍历对象层次结构：

```groovy
def person = [name: 'John']
def myNameIs = "I'm $person.name, and you?"
assertEquals("I'm John, and you?", myNameIs.toString())
```

使用 getter，Groovy 通常可以推断属性名称。

但是如果我们直接调用一个方法，我们将需要使用${} 因为括号：

```groovy
def name = 'John'
def result = "Uppercase name: ${name.toUpperCase()}".toString()
assertEquals("Uppercase name: JOHN", result)
```

### 5.4. GString和String中的hashCode

与普通的java.util.String 相比，内插字符串无疑是天赐之物，但它们在一个重要方面有所不同。

看，Java字符串是不可变的，因此对给定字符串调用 hashCode总是返回相同的值。

但是，GString哈希码可能会有所不同 ，因为String 表示取决于内插值。

实际上，即使对于相同的结果字符串，它们也不会有相同的哈希码：

```groovy
def string = "2+2 is 4"
def gstring = "2+2 is ${4}"
assertTrue(string.hashCode() != gstring.hashCode())
```

因此，我们永远不应该使用GString作为Map中的键！

## 6. 三重双引号字符串

所以，我们已经看到了三重单引号字符串，并且我们看到了双引号字符串。

让我们结合两者的力量来获得两全其美——多行字符串插值：

```groovy
def name = "John"
def multiLine = """
    I'm $name.
    "This is quotation from 'War and Peace'"
"""
```

另外，请注意我们不必转义单引号或双引号！

## 7. 斜线

现在，假设我们正在用正则表达式做一些事情，因此我们在所有地方转义反斜杠：

```groovy
def pattern = "d{1,3}sw+sw+w+"
```

这显然是一团糟。

为了解决这个问题，Groovy 通过 slashy 字符串原生支持正则表达式：

```groovy
def pattern = /d{3}sw+sw+w+/
assertTrue("3 Blind MiceMen".matches(pattern))
```

Slashy 字符串可以是内插的和多行的：

```groovy
def name = 'John'
def example = /
    Dear ([A-Z]+),
    Love, $name
/
```

当然，我们必须转义正斜杠：

```groovy
def pattern = /.foobar./hello./

```

而且我们不能用Slashy String 表示空字符串，因为编译器将//理解为注解：

```groovy
// if ('' == //) {
//     println("I can't compile")
// }
```

## 8. Dollar-Slashy 字符串

Slashy strings 很棒，尽管不得不转义正斜杠是一件很糟糕的事情。 为了避免额外转义正斜杠，我们可以使用美元斜杠字符串。 

假设我们有一个正则表达式模式：[0-3]+/[0-3]+。它是美元斜线字符串的一个很好的候选者，因为在斜线字符串中，我们必须写：[0-3]+//[0-3]+。

Dollar-slashy字符串是多行 GString，以 $/ 开头并以 /$ 结尾。要转义美元或正斜杠，我们可以在它前面加上美元符号 ($)，但这不是必需的。

我们不需要在GString占位符中转义 $。

例如：

```groovy
def name = "John"

def dollarSlashy = $/
    Hello $name!,

    I can show you a $ sign or an escaped dollar sign: $$ 
    Both slashes work:  or /, but we can still escape it: $/
            
    We have to escape opening and closing delimiters:
    - $$$/  
    - $/$$
 /$

```

会输出：

```bash
Hello John!,

I can show you a $ sign or an escaped dollar sign: $ 
Both slashes work:  or /, but we can still escape it: /

We have to escape opening and closing delimiter:
- $/  
- /$
```

## 9.性格

熟悉Java的人已经想知道Groovy对字符做了什么，因为它对字符串使用单引号。

实际上，Groovy没有明确的字符文字。

可以通过三种方式使Groovy 字符串成为实际字符：

-   声明变量时显式使用“char”关键字
-   使用“as”运算符
-   通过转换为“char”

让我们来看看它们：

```groovy
char a = 'A'
char b = 'B' as char
char c = (char) 'C'
assertTrue(a instanceof Character)
assertTrue(b instanceof Character)
assertTrue(c instanceof Character)
```

当我们想将字符作为变量保留时，第一种方式非常方便。当我们想将字符作为参数传递给函数时，其他两种方法会更有趣。

## 10.总结

显然，这很多，所以让我们快速总结一些要点：

-   使用单引号 (') 创建的字符串不支持插值
-   斜线和三重双引号字符串可以是多行的
-   由于代码缩进，多行字符串包含空白字符
-   反斜杠()用于转义每种类型的特殊字符，除了美元斜线字符串，我们必须使用美元($)来转义

## 11.总结

在本文中，我们讨论了在Groovy中创建字符串的多种方法及其对多行、插值和正则表达式的支持。