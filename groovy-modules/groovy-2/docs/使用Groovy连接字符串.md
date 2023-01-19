## 一、概述

在本教程中，我们将了解使用 Groovy连接String的几种方法。请注意，[Groovy 在线解释器](https://groovyconsole.appspot.com/)在这里派上用场。

我们将从定义一个numOfWonder变量开始，我们将在整个示例中使用它：

```groovy
def numOfWonder = 'seven'
```

## 2. 串联运算符

很简单，我们可以使用[+ 运算符](https://www.baeldung.com/groovy-strings#string-concatenation)来连接String：

```groovy
'The ' + numOfWonder + ' wonders of the world'

```

同样，Groovy 也支持左移 << 运算符：

```groovy
'The ' << numOfWonder << ' wonders of ' << 'the world'
```

## 3.字符串插值

下一步，我们将尝试[在字符串文字中使用 Groovy 表达式](https://www.baeldung.com/groovy-strings#string-interpolation)来提高代码的可读性：

```groovy
"The $numOfWonder wonders of the worldn"
```

这也可以使用花括号来实现：

```groovy
"The ${numOfWonder} wonders of the worldn"

```

## 4. 多行字符串

假设我们想要打印世界上所有的奇迹，那么我们可以使用[三重双引号](https://www.baeldung.com/groovy-strings#triple-quoted-string)来定义多行String，仍然包括我们的numOfWonder变量：

```groovy
"""
There are $numOfWonder wonders of the world.
Can you name them all? 
1. The Great Pyramid of Giza
2. Hanging Gardens of Babylon
3. Colossus of Rhode
4. Lighthouse of Alexendra
5. Temple of Artemis
6. Status of Zeus at Olympia
7. Mausoleum at Halicarnassus
"""
```

## 5.连接方法

作为最后一个选项，我们将查看 String的 concat方法：

```groovy
'The '.concat(numOfWonder).concat(' wonders of the world')
```

对于非常长的文本，我们建议改用[StringBuilder](https://www.baeldung.com/java-string-builder-string-buffer)或 [StringBuffer](https://www.baeldung.com/java-string-builder-string-buffer) ：

```groovy
new StringBuilder().append('The ').append(numOfWonder).append(' wonders of the world')
new StringBuffer().append('The ').append(numOfWonder).append(' wonders of the world')
```

## 六，总结

在本文中，我们快速了解了如何 使用 Groovy连接String 。