## 一、概述

在本文中，我们将了解用于字符串模式匹配的 Groovy 语言特性。

我们将看到 Groovy 的内置电池方法如何为我们提供强大且符合人体工程学的语法来满足我们的基本模式匹配需求。

## 2. 模式算子

Groovy 语言引入了所谓的模式运算符~。这个运算符可以被认为是 Java 的java.util.regex.Pattern.compile(string)方法的语法糖快捷方式。

作为Spock测试的一部分，让我们在实践中检查一下：

```groovy
def "pattern operator example"() {
    given: "a pattern"
    def p = ~'foo'

    expect:
    p instanceof Pattern

    and: "you can use slashy strings to avoid escaping of blackslash"
    def digitPattern = ~/d/
    digitPattern.matcher('4711').matches()
}
```

这也很方便，但我们会看到这个运算符只是其他一些更有用的运算符的基线。

## 3.匹配运算符

大多数时候，尤其是在编写测试时，我们对创建Pattern对象并不真正感兴趣，而是想检查String是否与某个正则表达式(或Pattern)匹配。因此，Groovy 还包含匹配运算符==~。

它返回一个布尔值并对指定的正则表达式执行严格匹配。基本上，它是调用Pattern.matches(regex, string)的语法快捷方式。

同样，我们将在实践中将其作为Spock测试的一部分进行研究：

```groovy
def "match operator example"() {
    expect:
    'foobar' ==~ /.oba./

    and: "matching is strict"
    !('foobar' ==~ /foo/)
}
```

## 4.寻找运营商

模式匹配上下文中的最后一个 Groovy 运算符是查找运算符~=。在这种情况下，运算符将直接创建并返回一个java.util.regex.Matcher实例。

当然，我们可以通过访问其已知的 Java API 方法来对该Matcher实例进行操作。但除此之外，我们还可以使用多维数组访问匹配的组。

这还不是全部——如果用作谓词， Matcher实例将通过调用其find()方法自动强制转换为布尔类型。引用官方 Groovy 文档，这意味着“=~ 运算符与 Perl 的 =~ 运算符的简单使用一致”。

在这里，我们看到操作员在行动：

```groovy
def "find operator example"() {
    when: "using the find operator"
    def matcher = 'foo and bar, baz and buz' =~ /(w+) and (w+)/

    then: "will find groups"
    matcher.size() == 2

    and: "can access groups using array"
    matcher[0][0] == 'foo and bar'
    matcher[1][2] == 'buz'

    and: "you can use it as a predicate"
    'foobarbaz' =~ /bar/
}
```

## 5.总结

我们已经看到 Groovy 语言如何让我们以非常方便的方式访问关于正则表达式的内置 Java 特性。

[官方 Groovy 文档](http://groovy-lang.org/operators.html#_pattern_operator)还包含一些关于此主题的简明示例。如果你认为文档中的代码示例是作为文档构建的一部分执行的，那就特别酷了。