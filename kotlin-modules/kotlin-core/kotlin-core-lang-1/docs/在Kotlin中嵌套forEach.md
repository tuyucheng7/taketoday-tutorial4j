## 1. 简介

**在这个简短的Kotlin教程中，我们将了解forEach循环的lambda中的参数作用域**。

首先，我们定义我们将在示例中使用的数据。其次，我们将了解如何使用forEach遍历列表。第三，我们将看看如何在嵌套循环中使用它。

## 2. 测试数据

我们将使用的数据是一个国家列表，每个国家包含一个城市列表，而城市列表又包含一个街道列表：

```kotlin
class Country(val name : String, val cities : List<City>)

class City(val name : String, val streets : List<String>)

class World {

    val streetsOfAmsterdam = listOf("Herengracht", "Prinsengracht")
    val streetsOfBerlin = listOf("Unter den Linden","Tiergarten")
    val streetsOfMaastricht = listOf("Grote Gracht", "Vrijthof")
    val countries = listOf(
        Country("Netherlands", listOf(City("Maastricht", streetsOfMaastricht),
            City("Amsterdam", streetsOfAmsterdam))),
        Country("Germany", listOf(City("Berlin", streetsOfBerlin))))
}
```

## 3. 简单的forEach

要打印列表中每个国家的名称，我们可以编写以下代码：

```kotlin
fun allCountriesExplicit() { 
    countries.forEach { c -> println(c.name) } 
}
```

上面的语法类似于Java。但是，在Kotlin中，如果lambda只接收一个参数，我们可以将其作为默认参数名称，而不需要显式命名它：

```kotlin
fun allCountriesIt() {
    countries.forEach { println(it.name) }
}
```

上面也等同于：

```kotlin
fun allCountriesItExplicit() {
    countries.forEach { it -> println(it.name) }
}
```

**值得注意的是，如果没有显式参数，我们只能将it用作隐式参数名称**。

例如，以下方法不起作用：

```kotlin
fun allCountriesExplicit() { 
    countries.forEach { c -> println(it.name) } 
}
```

我们会在编译时看到一个错误：

```bash
Error:(2, 38) Kotlin: Unresolved reference: it
```

## 4. 嵌套forEach

如果我们想遍历所有国家、城市和街道，我们可以编写一个嵌套循环：

```kotlin
fun allNested() {
    countries.forEach {
        println(it.name)
        it.cities.forEach {
            println(" ${it.name}")
            it.streets.forEach { println("  $it") }
        }
    }
}
```

在这里，第一个it表示国家，第二个it表示城市，第三个it表示街道。

但是，如果我们使用IntelliJ，我们会看到一条警告：

```bash
Implicit parameter 'it' of enclosing lambda is shadowed
```

这可能不是问题，但是，在第6行中，我们不能再引用国家或城市了。**如果我们想要这样，我们需要显式命名参数**：

```kotlin
fun allTable() {
    countries.forEach { c ->
        c.cities.forEach { p ->
            p.streets.forEach { println("${c.name} ${p.name} $it") }
        }
    }
}
```

## 5. 嵌套循环的替代方案

嵌套循环通常难以阅读，应尽可能避免使用，另一种选择是使用flatMap()：

```kotlin
fun allStreetsFlatMap() {
    countries.flatMap { it.cities}
        .flatMap { it.streets}
        .forEach { println(it) }
}
```

但是，如果我们不使用嵌套的flatMap，我们就无法在println语句中访问城市或街道名称。如果我们想获得与上述方法allTable()相同的输出并避免嵌套，我们可以添加两个扩展函数：

```kotlin
fun City.getStreetsWithCityName() : List<String> {
    return streets.map { "$name, $it" }
        .toList()
}

fun Country.getCitiesWithCountryName() : List<String> {
    return cities.flatMap { it.getStreetsWithCityName() }
        .map { "$name, $it" }
}
```

然后将这两种方法与单个flatMap一起使用：

```kotlin
fun allFlatMapTable() {
    countries.flatMap { it.getCitiesWithCountryName() }
        .forEach { println(it) }
}
```

## 6. 总结

在这篇简短的文章中，我们了解了如何在Kotlin中使用默认参数it以及如何从嵌套的forEach循环中访问外部forEach的参数。最后，我们还研究了如何使用flatMap和扩展函数来避免嵌套循环。