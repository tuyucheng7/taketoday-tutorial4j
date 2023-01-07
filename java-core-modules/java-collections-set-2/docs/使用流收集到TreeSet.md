## 1. 概述

Java 8 中的一项重要新功能是[Stream API](https://www.baeldung.com/java-8-streams)。流允许我们方便地处理来自不同来源的元素，例如数组或集合。

此外，使用带有相应[Collectors的](https://www.baeldung.com/java-8-collectors)[Stream.collect()](https://www.baeldung.com/java-8-collectors#Collect)方法，我们可以将元素重新打包为不同的数据结构，如[Set](https://www.baeldung.com/java-hashset)、[Map](https://www.baeldung.com/java-hashmap)、[List](https://www.baeldung.com/java-arraylist)等。

在本教程中，我们将探讨如何将Stream 中的元素收集到[TreeSet](https://www.baeldung.com/java-tree-set)中。

## 2.以自然顺序收集成一个TreeSet

简单地说，TreeSet是一个有序的 Set。TreeSet中的元素使用它们的自然顺序或提供的[Comparator](https://www.baeldung.com/java-comparator-comparable)进行排序。

我们将首先了解如何使用自然顺序收集Stream元素。然后，让我们专注于使用自定义Comparator案例收集元素。

为简单起见，我们将使用单元测试断言来验证我们是否获得了预期的TreeSet结果。

### 2.1. 将字符串收集到TreeSet中

由于String 实现了Comparable接口，我们先以String为例，看看如何将它们收集到一个TreeSet中：

```java
String kotlin = "Kotlin";
String java = "Java";
String python = "Python";
String ruby = "Ruby";
TreeSet<String> myTreeSet = Stream.of(ruby, java, kotlin, python).collect(Collectors.toCollection(TreeSet::new));
assertThat(myTreeSet).containsExactly(java, kotlin, python, ruby);
```

如上面的测试所示，要将Stream元素收集到TreeSet中，我们只需将TreeSet的默认构造函数作为[方法引用](https://www.baeldung.com/java-method-references)或[lambda 表达式](https://www.baeldung.com/java-8-lambda-expressions-tips)传递给[Collectors.toCollection()](https://www.baeldung.com/java-8-collectors#3-collectorstocollection)方法。

如果我们执行这个测试，它就会通过。

接下来，让我们看一个带有自定义类的类似示例。

### 2.2. 按自然顺序收集玩家

首先，让我们看一下我们的Player类：

```java
public class Player implements Comparable<Player> {
    private String name;
    private int age;
    private int numberOfPlayed;
    private int numberOfWins;

    public Player(String name, int age, int numberOfPlayed, int numberOfWins) {
        this.name = name;
        this.age = age;
        this.numberOfPlayed = numberOfPlayed;
        this.numberOfWins = numberOfWins;
    }

    @Override
    public int compareTo(Player o) {
        return Integer.compare(age, o.age);
    }

    // getters are omitted
}

```

如上面的类所示，我们的Player类实现了Comparable接口。此外，我们在compareTo()方法中定义了它的自然顺序：玩家的年龄。

那么接下来，让我们创建一些Player实例：

```java
/                          name  |  age  | num of played | num of wins
                           --------------------------------------------- /
Player kai = new Player(   "Kai",     26,       28,            7);
Player eric = new Player(  "Eric",    28,       30,           11);
Player saajan = new Player("Saajan",  30,      100,           66);
Player kevin = new Player( "Kevin",   24,       50,           49);
```

由于我们稍后会用到这四个玩家对象进行其他演示，所以我们将代码放在一个类似表格的格式中，以便于查看每个玩家的属性值。

现在，让我们按照它们的自然顺序将它们收集到一个TreeSet中，并验证我们是否得到了预期的结果：

```java
TreeSet<Player> myTreeSet = Stream.of(saajan, eric, kai, kevin).collect(Collectors.toCollection(TreeSet::new));
assertThat(myTreeSet).containsExactly(kevin, kai, eric, saajan);
```

如我们所见，代码与将字符串收集到TreeSet中非常相似。由于Player的compareTo() 方法已将“age”属性指定为其自然顺序，因此我们使用按年龄升序排序的玩家来验证结果 ( myTreeSet )。

值得一提的是，我们使用[了 AssertJ](https://www.baeldung.com/introduction-to-assertj)的containsExactly()方法来验证TreeSet是否按顺序精确地包含给定的元素，而没有其他内容。

接下来，我们将看看如何使用自定义的Comparator将这些玩家收集到TreeSet中。

## 3.使用自定义比较器收集到TreeSet

我们已经看到Collectors.toCollection(TreeSet::new)允许我们按照自然顺序将Stream中的元素收集到TreeSet中。TreeSet提供了另一个接受Comparator对象作为参数的构造函数：

```java
public TreeSet(Comparator<? super E> comparator) { ... }
```

因此，如果我们希望TreeSet对元素应用不同的排序，我们可以创建一个Comparator对象并将其传递给上面提到的构造函数。

接下来，让我们根据获胜次数而不是年龄将这些玩家收集到TreeSet中：

```java
TreeSet<Player> myTreeSet = Stream.of(saajan, eric, kai, kevin)
  .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingInt(Player::getNumberOfWins))
));
assertThat(myTreeSet).containsExactly(kai, eric, kevin, saajan);
```

这一次，我们使用了 lambda 表达式来创建TreeSet实例。此外，我们已经使用Comparator.comparingInt()将我们自己的Comparator传递 给了TreeSet的构造函数。

Player::getNumberOfWins引用我们需要比较玩家的属性值。

当我们运行它时，测试通过了。

但是，所需的比较逻辑有时并不像示例所示那样简单，只是比较属性的值。例如，我们可能需要比较一些额外计算的结果。

所以最后，让我们再次将这些玩家收集到一个TreeSet中。但是这一次，我们希望他们按胜率(胜数/出场数)排序：

```java
TreeSet<Player> myTreeSet = Stream.of(saajan, eric, kai, kevin)
  .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(player -> BigDecimal.valueOf(player.getNumberOfWins())
    .divide(BigDecimal.valueOf(player.getNumberOfPlayed()), 2, RoundingMode.HALF_UP)))));
assertThat(myTreeSet).containsExactly(kai, eric, saajan, kevin);
```

如上面的测试所示，我们使用了Comparator.comparing(Function keyExtractor)方法来指定可比较的排序键。在此示例中，keyExtractor函数是一个 lambda 表达式，用于计算玩家的获胜率。

此外，如果我们运行测试，它就会通过。所以我们得到了预期的TreeSet。

## 4。总结

在本文中，我们通过示例讨论了如何通过自然顺序和自定义比较器将Stream 中的元素收集到TreeSet中。