## 1. 概述

作为Java开发人员，我们经常需要对集合中组合在一起的元素进行排序。Java 允许我们对任何类型的数据实现各种排序算法。

例如，我们可以按字母顺序、逆字母顺序或基于长度对字符串进行排序。

在本教程中，我们将探索Comparable接口及其启用排序的compareTo方法。我们将查看对包含来自核心类和自定义类的对象的集合进行排序。

我们还将提及正确实施compareTo的规则，以及需要避免的损坏模式。

## 2.比较界面

[Comparable接口](https://www.baeldung.com/java-comparator-comparable)对实现它的每个类的对象进行排序。

compareTo是Comparable接口定义的唯一方法。它通常被称为自然比较法。

### 2.1. 实施比较

compareTo方法将当前对象与作为参数发送的对象进行比较。

在实现它时，我们需要确保该方法返回：

-   一个正整数，如果当前对象大于参数对象
-   一个负整数，如果当前对象小于参数对象
-   零，如果当前对象等于参数对象

在数学中，我们称之为符号或符号函数：

[![信号功能](https://www.baeldung.com/wp-content/uploads/2021/02/2021-01-24-10_27_03-notation-What-does-sgn-mean_-Mathematics-Stack-Exchange.png)](https://www.baeldung.com/wp-content/uploads/2021/02/2021-01-24-10_27_03-notation-What-does-sgn-mean_-Mathematics-Stack-Exchange.png)

### 2.2. 示例实现

我们来看看核心Integer类中的compareTo方法是如何实现的：

```java
@Override
public int compareTo(Integer anotherInteger) {
    return compare(this.value, anotherInteger.value);
}

public static int compare (int x, int y) {
    return (x < y) ? -1 : ((x == y) ? 0 : 1);
}
```

### 2.3. 破碎的减法模式

有人可能会争辩说我们可以使用一种巧妙的单行减法：

```java
@Override
public int compareTo(BankAccount anotherAccount) {
    return this.balance - anotherAccount.balance;
}
```

让我们考虑一个示例，其中我们期望正帐户余额大于负帐户余额：

```java
BankAccount accountOne = new BankAccount(1900000000);
BankAccount accountTwo = new BankAccount(-2000000000);
int comparison = accountOne.compareTo(accountTwo);
assertThat(comparison).isNegative();
```

但是，整数不足以存储差异，因此给我们错误的结果。当然，这种模式由于可能的整数溢出而被打破，需要避免。

正确的解决办法是用比较代替减法。我们也可以重用核心Integer类的正确实现：

```java
@Override
public int compareTo(BankAccount anotherAccount) {
    return Integer.compare(this.balance, anotherAccount.balance);
}
```

### 2.4. 实施细则

为了正确实现compareTo方法，我们需要遵守以下数学规则：

-   sgn(x.compareTo(y)) == -sgn(y.compareTo(x))
-   (x.compareTo(y) > 0 && y.compareTo(z) > 0)意味着 x.compareTo(z) > 0
-   x.compareTo(y) == 0 意味着 sgn(x.compareTo(z)) == sgn(y.compareTo(z))

强烈建议(虽然不是必需的) 使compareTo实现与[equals](https://www.baeldung.com/java-comparing-objects#:~:text=Object%23equals Method&text=This method is defined in,equality means for our objects.)方法实现保持一致：

-   x.compareTo(e2) == 0应该与x.equals(y)具有相同的布尔值

这将确保我们可以安全地使用有序集合和有序映射中的对象。

### 2.5. 与平等的一致性

让我们看看当compareTo和equals实现不一致时会发生什么。

在我们的示例中，compareTo方法检查进球数，而equals方法检查球员姓名：

```java
@Override
public int compareTo(FootballPlayer anotherPlayer) {
    return this.goalsScored - anotherPlayer.goalsScored;
}

@Override
public boolean equals(Object object) {
    if (this == object)
        return true;
    if (object == null || getClass() != object.getClass())
        return false;
    FootballPlayer player = (FootballPlayer) object;
    return name.equals(player.name);
}
```

在排序集或排序映射中使用此类时，这可能会导致意外行为：

```java
FootballPlayer messi = new FootballPlayer("Messi", 800);
FootballPlayer ronaldo = new FootballPlayer("Ronaldo", 800);

TreeSet<FootballPlayer> set = new TreeSet<>();
set.add(messi);
set.add(ronaldo);

assertThat(set).hasSize(1);
assertThat(set).doesNotContain(ronaldo);
```

排序集使用compareTo而不是equals方法执行所有元素比较。因此，从它的角度来看，这两个玩家似乎是等价的，它不会添加第二个玩家。

## 3.分类收藏

Comparable接口的主要目的是使分组在集合或数组中的元素能够自然排序。

我们可以使用Java实用方法[Collections.sort](https://www.baeldung.com/java-sorting)[或](https://www.baeldung.com/java-sorting)[Arrays.sort](https://www.baeldung.com/java-sorting)对实现Comparable的所有对象进行排序。

### 3.1. 核心Java类

大多数核心Java类，如String、Integer或Double，已经实现了Comparable接口。

因此，对它们进行排序非常简单，因为我们可以重用它们现有的自然排序实现。

按自然顺序对数字进行排序将导致升序：

```java
int[] numbers = new int[] {5, 3, 9, 11, 1, 7};
Arrays.sort(numbers);
assertThat(numbers).containsExactly(1, 3, 5, 7, 9, 11);
```

另一方面，字符串的自然排序将导致字母顺序：

```java
String[] players = new String[] {"ronaldo",  "modric", "ramos", "messi"};
Arrays.sort(players);
assertThat(players).containsExactly("messi", "modric", "ramos", "ronaldo");
```

### 3.2. 自定义类

相反，对于任何可排序的自定义类，我们需要手动实现Comparable接口。

如果我们尝试对未实现Comparable的对象集合进行排序，Java 编译器将抛出错误。

如果我们对数组进行同样的尝试，它不会在编译期间失败。但是，它会导致类转换运行时异常：

```java
HandballPlayer duvnjak = new HandballPlayer("Duvnjak", 197);
HandballPlayer hansen = new HandballPlayer("Hansen", 196);
HandballPlayer[] players = new HandballPlayer[] {duvnjak, hansen};
assertThatExceptionOfType(ClassCastException.class).isThrownBy(() -> Arrays.sort(players));
```

### 3.3. TreeMap和TreeSet

[TreeMap](https://www.baeldung.com/java-treemap)和[TreeSet](https://www.baeldung.com/java-tree-set)是JavaCollections Framework 的两个实现，它们帮助我们对其元素进行自动排序。

我们可以在有序映射中使用实现Comparable接口的对象，或将其用作有序集合中的元素。

让我们看一个自定义类的示例，该类根据玩家的进球数来比较玩家：

```java
@Override
public int compareTo(FootballPlayer anotherPlayer) {
    return Integer.compare(this.goalsScored, anotherPlayer.goalsScored);
}
```

在我们的示例中，键会根据compareTo实现中定义的标准自动排序：

```java
FootballPlayer ronaldo = new FootballPlayer("Ronaldo", 900);
FootballPlayer messi = new FootballPlayer("Messi", 800);
FootballPlayer modric = new FootballPlayer("modric", 100);

Map<FootballPlayer, String> players = new TreeMap<>();
players.put(ronaldo, "forward");
players.put(messi, "forward");
players.put(modric, "midfielder");

assertThat(players.keySet()).containsExactly(modric, messi, ronaldo);
```

## 4.比较器替代方案

除了自然排序，Java 还允许我们以灵活的方式定义特定的排序逻辑。

[Comparator接口](https://www.baeldung.com/java-comparator-comparable)允许从我们正在排序的对象中分离出多种不同的比较策略：

```java
FootballPlayer ronaldo = new FootballPlayer("Ronaldo", 900);
FootballPlayer messi = new FootballPlayer("Messi", 800);
FootballPlayer modric = new FootballPlayer("Modric", 100);

List<FootballPlayer> players = Arrays.asList(ronaldo, messi, modric);
Comparator<FootballPlayer> nameComparator = Comparator.comparing(FootballPlayer::getName);
Collections.sort(players, nameComparator);

assertThat(players).containsExactly(messi, modric, ronaldo);
```

当我们不想或不能修改我们要排序的对象的源代码时，它通常也是一个不错的选择。

## 5.总结

在本文中，我们研究了如何使用Comparable接口为我们的Java类定义自然排序算法。我们查看了常见的损坏模式并定义了如何正确实施compareTo方法。

我们还探索了对包含核心类和自定义类的集合进行排序。接下来，我们考虑了在排序集和排序映射中使用的类中compareTo方法的实现。

最后，我们研究了一些应该使用Comparator接口的用例。