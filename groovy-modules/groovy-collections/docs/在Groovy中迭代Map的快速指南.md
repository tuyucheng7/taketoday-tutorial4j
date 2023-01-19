## 一、简介

在这个简短的教程中，我们将研究使用标准语言功能(如each、 eachWithIndex 和 for-in 循环)在 Groovy 中迭代地图的方法 。

## 2. each方法

假设我们有以下地图：

```groovy
def map = [
    'FF0000' : 'Red',
    '00FF00' : 'Lime',
    '0000FF' : 'Blue',
    'FFFF00' : 'Yellow'
]
```

我们可以通过为每个 方法提供一个简单的闭包来迭代地图 ：

```groovy
map.each { println "Hex Code: $it.key = Color Name: $it.value" }
```

我们还可以通过为入口变量命名来提高可读性：

```groovy
map.each { entry -> println "Hex Code: $entry.key = Color Name: $entry.value" }
```

或者，如果我们更愿意单独处理键和值，我们可以在闭包中单独列出它们：

```groovy
map.each { key, val ->
    println "Hex Code: $key = Color Name $val"
}
```

在 Groovy 中，使用文字符号创建的映射是有序的。 我们可以期望我们的输出与我们在原始地图中定义的顺序相同。

## 3. eachWithIndex方法

有时我们想在迭代时知道索引。

例如，假设我们想要缩进地图中的每一行。要在 Groovy 中做到这一点，我们将使用 带有入口和索引变量的eachWithIndex 方法：

```groovy
map.eachWithIndex { entry, index ->
    def indent = ((index == 0 || index % 2 == 0) ? "   " : "")
    println "$index Hex Code: $entry.key = Color Name: $entry.value"
}
```

与 each方法一样，我们可以选择在闭包中使用 键和值变量而不是entry：

```groovy
map.eachWithIndex { key, val, index ->
    def indent = ((index == 0 || index % 2 == 0) ? "   " : "")
    println "$index Hex Code: $key = Color Name: $val"
}
```

## 4. 使用For-in循环

另一方面，如果我们的用例更适合命令式编程，我们也可以使用for-in语句来迭代我们的地图：

```groovy
for (entry in map) {
    println "Hex Code: $entry.key = Color Name: $entry.value"
}
```

## 5.总结

在这个简短的教程中，我们学习了如何使用 Groovy 的each和eachWithIndex方法以及for-in循环迭代地图。