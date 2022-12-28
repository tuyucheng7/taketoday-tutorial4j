## 1. 简介

循环是任何编程语言的基本结构之一，它们允许重复执行一个或多个语句，直到满足条件。

在本教程中，我们将了解Kotlin支持的不同类型的循环：

-   repeat
-   for循环
-   while循环
-   do..while循环

## 2. repeat

repeat语句是所有Kotlin循环中最基本的，如果我们希望简单地重复一个操作n次，我们可以使用repeat。

例如，让我们打印两次Hello World：

```kotlin
repeat(2) {
    println("Hello World")
}
```

此外，我们还可以访问当前迭代的从零开始的索引：

```kotlin
repeat(2) { index ->
    println("Iteration ${index + 1}: Hello World")
}
```

## 3. for循环

for循环用于迭代一系列值，它为序列中的每个值执行一个语句块。

让我们从查看for循环的语法开始：

```kotlin
for (variableDeclaration 'in' expression) {
    // block of statements
}
```

**使用Kotlin的for循环，我们可以迭代任何Iterable，例如范围、数组或集合**。

### 3.1 迭代一系列值

范围是一系列值，包括开始、结束和步骤。在Kotlin中，我们可以结合使用for循环和[范围表达式](https://www.baeldung.com/kotlin-ranges)来迭代一个范围。

### 3.2 遍历数组

首先，让我们声明一个元音数组：

```kotlin
val vowels = arrayOf('a', 'e', 'i', 'o', 'u')
```

现在，我们可以使用for循环遍历此数组的元素：

```kotlin
for (vowel in vowels) {
    println(vowel)
}
```

接下来，让我们遍历数组索引，我们可以通过循环访问indices属性来做到这一点：

```kotlin
for (index in vowels.indices) {
    println(vowels[index])
}
```

indices属性仅返回数组索引作为我们可以迭代的IntRange，我们必须使用返回的索引分别获取数组元素。

让我们看一下同时为我们提供索引和元素的另一种方法：

```kotlin
for ((index, vowel) in vowels.withIndex()) {
    println("The vowel at index $index is: $vowel")
}
```

让我们进一步了解这个for循环的有趣语法：

```kotlin
for ((index, vowel) in vowels.withIndex()) { ... }
```

withIndex()方法返回一个IndexedValue实例，我们使用[解构声明](https://www.baeldung.com/kotlin-destructuring-declarations)并将IndexedValue的component1()和component2()方法返回的(index, value)属性分别捕获到index和vowel变量中。

### 3.3 遍历List

List是可以包含重复值的一般有序元素集合，我们可以使用for循环[遍历List的元素](https://www.baeldung.com/kotlin/lists#1-loops)，我们还可以通过其[元素的索引遍历List](https://www.baeldung.com/kotlin/iterating-collections-by-index)。

### 3.4 遍历Map

让我们首先声明一个以国家名称为key的首都城市名称Map：

```kotlin
val capitalCityByCountry = mapOf("Netherlands" to "Amsterdam",
  "Germany" to "Berlin", "USA" to "Washington, D.C.")
```

现在，我们可以遍历Map条目并访问每个条目的键和值：

```kotlin
for (entry in capitalCityByCountry) {
    println("The capital city of ${entry.key} is ${entry.value}")
}
```

我们可以使用keys属性遍历Map的键：

```kotlin
for (country in capitalCityByCountry.keys) {
    println(country)
}
```

在这里，我们遍历返回Map键的只读集合的keys属性。

同样，我们可以使用values属性迭代Map的值：

```kotlin
for (capitalCity in capitalCityByCountry.values) {
    println(capitalCity)
}
```

最后，与数组和List的情况一样，我们也可以在遍历Map时使用解构声明：

```kotlin
for ((country, capitalCity) in capitalCityByCountry) {
    println("The capital city of $country is $capitalCity")
}
```

当我们遍历Map时，解构声明使我们能够访问每个条目的键和值。

### 3.5 使用forEach的函数式风格迭代

到目前为止，我们了解了使用for循环迭代数组、集合和范围的传统风格。

要以函数式风格编写代码，我们可以改用[forEach](https://www.baeldung.com/kotlin-nested-foreach#simple-for-each)。

## 4. While循环

while循环在其控制布尔表达式为true时重复语句块。

让我们先看一下语法：

```kotlin
while (boolean-expression) {
    // statements here
}
```

接下来，我们来看一个例子：

```kotlin
var counter = 0
while (counter < 5) {
    println("while loop counter: " + counter++)
}
```

这将打印从0到4的counter值。

在while循环中，首先计算布尔表达式，这意味着如果布尔表达式在第一次迭代中的计算结果为false，则永远不会执行该语句块。

下面是一个while循环，其语句块永远不会被执行：

```kotlin
while (false) {
    println("This will never be printed")
}
```

这是一个无限的while循环：

```kotlin
while (true) {
    println("I am in an infinite loop")
}
```

## 5. Do..While循环

do..while循环是while循环的变体，布尔表达式在每次迭代后计算：

```kotlin
do {
    // statements here
} while (boolean-expression)
```

让我们看一个简单的例子：

```kotlin
var counter = 0
do {
    println("do..while counter: " + counter++)
} while (counter < 5)
```

这将打印从0到4的counter值。

因为布尔表达式是在每个循环结束时计算的，所以do..while循环至少执行一次。

这是一个do..while循环，其语句块将恰好执行一次：

```kotlin
do {
    println("This will be printed exactly once")
} while (false)
```

## 6. return、break、continue关键字

有时，我们可能希望终止循环或跳过循环的下一次迭代，在这种情况下，我们可以使用结构跳转表达式。

**Kotlin有三种结构跳转表达式**：[return、break和continue](https://www.baeldung.com/kotlin-return-break-continue)。 

然而，当一个循环包含多个break或continue语句时，它被认为是一种代码异味，必须避免。

## 7. 多变量循环

到目前为止，我们已经了解了使用单个变量进行循环的各种方法。在本节中，我们将把循环知识扩展到多个变量。

### 7.1 zip运算符

让我们想象一下，我们必须组织两支球队之间的多场双人比赛：

```kotlin
val teamA = listOf("A1", "A2", "A3")
val teamB = listOf("B1", "B2")
```

由于两支球队的球员人数不同，并非所有球员都可以同时上场。但是，**我们可以使用**[zip](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/zip.html)**运算符按照它们在集合中出现的顺序将它们配对**，并忽略来自teamA的第三个球员A3：

```kotlin
teamA zip teamB
```

让我们创建一个showMatches()函数，该函数将生成并显示任意两支球队之间的比赛：

```kotlin
fun showMatches(team1: List<String>, team2: List<String>) {
    for ((player1, player2) in team1 zip team2)
        println("$player1 vs $player2")
}
```

接下来，让我们验证调用showMatches(teamA, teamB)时将生成的匹配项：

```plaintext
A1 vs B1
A2 vs B2
```

或者，我们也可以将循环遍历多个变量(即player1和player2)的问题转换为循环遍历单个变量match：

```kotlin
fun showMatchLabels(team1: List<String>, team2: List<String>) {
    matches = team1.zip(team2) { player1, player2 -> "$player1 vs $player2" }
    for (match in matches)
        println(match)
}
```

从本质上讲，我们将这两个集合合并为一个集合，该集合现在包含匹配项的标签。

### 7.2 乘法表

让我们通过应用范围的概念和zip运算符来生成乘法表，从而加深我们对使用多个变量循环的理解，其中每一行以以下格式显示：

```kotlin
factor x multiplier = result(因子 x 乘数 = 结果)
```

由于该因子在所有行中都将保持不变，因此我们必须遍历两个乘数和结果变量。此外，我们的循环中需要两个范围。其中**第一个范围将保存对应于不同行的乘数列表**，另一方面，**第二个范围将保存结果值**。

首先，让我们继续编写printMultiplicationTable()函数：

```kotlin
fun printMultiplicationTable(factor: Int, start: Int, end: Int) {
    val multipliers = start..end
    val multiplicationResults = factor * start..factor * end step factor
    for ((multiplier, result) in multipliers zip multiplicationResults)
        println("$factor x $multiplier = $result")
}
```

接下来，我们一定要注意multiplicationResults实际上是一个阶梯值等于factor的[级数](https://kotlinlang.org/docs/ranges.html#progression)。

最后，让我们验证此函数以显示从3开始到7结束的行的27的乘法表：

```kotlin
printMultiplicationTable(27, 3, 7)
```

我们可以看到控制台中显示的输出符合预期：

```plaintext
27 x 3 = 81
27 x 4 = 108
27 x 5 = 135
27 x 6 = 162
27 x 7 = 189
```

## 8. 总结

在本教程中，我们了解了Kotlin支持的各种循环。首先，我们查看了repeat，这是最简单的循环语句。然后，我们研究了for循环以及如何使用它迭代范围、数组和集合。接下来，我们查看了while和do..while循环以及它们之间的细微差别。

此外，我们将理解扩展到使用多个变量进行循环。最后，我们查看了Kotlin的结构跳转表达式：return、break和continue。