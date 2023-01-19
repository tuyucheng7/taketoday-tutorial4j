## 一、概述

在这个简短的教程中，我们将学习如何在[Groovy](https://www.baeldung.com/groovy-language)中将表示日期的String转换为真正的Date对象。

但是，我们应该记住，这种语言是Java的增强版。因此，除了新的Groovy方法之外，我们仍然可以使用所有普通的旧Java方法。

## 2.使用日期格式

首先，我们可以像往常一样使用JavaDateFormat将字符串解析为日期：

```groovy
def pattern = "yyyy-MM-dd"
def input = "2019-02-28"

def date = new SimpleDateFormat(pattern).parse(input)

```

然而，Groovy 允许我们更轻松地执行此操作。它在便捷的静态方法 [Date.parse(String format, String input)](http://docs.groovy-lang.org/2.5.6/html/api/org/apache/groovy/dateutil/extensions/DateUtilStaticExtensions.html#parse(java.util.Date,java.lang.String,java.lang.String))中封装了相同的行为：

```groovy
def date = Date.parse(pattern, input)

```

简而言之，该方法是java.util.Date对象的扩展，为了线程安全，它在每次调用时在内部实例化一个java.text.DateFormat 。

### 2.1. 兼容性问题

澄清一下，Date.parse(String format, String input)方法从Groovy的 1.5.7 版本开始可用。

版本 2.4.1 引入了一个变体，它接受指示时区的第三个参数：Date.parse(String format, String input, TimeZone zone)。

然而，从 2.5.0 开始，[发生了重大变化](http://groovy-lang.org/releasenotes/groovy-2.5.html#Groovy2.5releasenotes-Breakingchanges)，这些增强功能不再随[groovy-all](https://search.maven.org/search?q=a:groovy-all AND g:org.codehaus.groovy)一起提供。

因此，展望未来，它们需要作为一个单独的模块包含在内，名为[groovy-dateutil](https://search.maven.org/search?q=a:groovy-dateutil)：

```xml
<dependency>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy-dateutil</artifactId>
    <version>2.5.6</version>
</dependency>

```

还有 3.0.0 版，但目前处于 Alpha 阶段。

## 3. 使用 JSR-310 LocalDate

从版本 8 开始，Java 引入了一套全新的工具来处理日期：[Date/Time API](https://www.baeldung.com/java-8-date-time-intro)。

由于多种原因，这些 API 更好，并且应该优先于遗留API 。

让我们看看如何利用Groovy的java.time.LocalDate解析功能：

```groovy
def date = LocalDate.parse(input, pattern)

```

## 4。总结

我们已经了解了如何 在Groovy语言中将String转换为 Date，注意特定版本之间的特殊性。