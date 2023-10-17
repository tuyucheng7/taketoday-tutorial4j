## 1. 概述

在Java中进行比较非常容易，除非它们不是。

当使用自定义类型，或尝试比较不能直接比较的对象时，我们需要使用比较策略。我们可以简单地通过使用Comparator或Comparable接口来构建一个。

## 2. 设置示例

让我们举一个足球队的例子，我们想按排名排列球员。

我们将从创建一个简单的Player类开始：

```java
public class Player {
    private int ranking;
    private String name;
    private int age;
    
    // constructor, getters, setters  
}
```

接下来，我们将创建一个PlayerSorter类来创建我们的集合，并尝试使用Collections.sort对其进行排序：

```java
public static void main(String[] args) {
    List<Player> footballTeam = new ArrayList<>();
    Player player1 = new Player(59, "John", 20);
    Player player2 = new Player(67, "Roger", 22);
    Player player3 = new Player(45, "Steven", 24);
    footballTeam.add(player1);
    footballTeam.add(player2);
    footballTeam.add(player3);

    System.out.println("Before Sorting : " + footballTeam);
    Collections.sort(footballTeam);
    System.out.println("After Sorting : " + footballTeam);
}

```

正如预期的那样，这会导致编译时错误：

```java
The method sort(List<T>) in the type Collections 
  is not applicable for the arguments (ArrayList<Player>)
```

现在让我们试着理解我们在这里做错了什么。

##3、可比性

顾名思义，Comparable是一个接口，定义了将一个对象与其他同类对象进行比较的策略。这被称为类的“自然排序”。

为了能够排序，我们必须通过实现Comparable接口将我们的Player对象定义为可比较的：

```java
public class Player implements Comparable<Player> {

    // same as before

    @Override
    public int compareTo(Player otherPlayer) {
        return Integer.compare(getRanking(), otherPlayer.getRanking());
    }

}

```

排序顺序由compareTo()方法的返回值决定。如果x小于y，[Integer.compare(x,y)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Integer.html#compare(int,int))返回-1，如果它们相等则返回0，否则返回1。

该方法返回一个数字，指示被比较的对象是小于、等于还是大于作为参数传递的对象。

现在当我们运行我们的PlayerSorter时，我们可以看到我们的玩家按照他们的排名排序：

```java
Before Sorting : [John, Roger, Steven]
After Sorting : [Steven, John, Roger]
```

现在我们已经清楚地了解了Comparable的自然排序，让我们看看如何以比直接实现接口更灵活的方式使用其他类型的排序。

##4.比较器

Comparator接口定义了一个compare(arg1,arg2)方法，它有两个参数，代表被比较的对象，其工作方式类似于Comparable.compareTo()方法。

###4.1.创建比较器

要创建一个Comparator，我们必须实现Comparator接口。

对于我们的第一个示例，我们将创建一个Comparator以使用Player的ranking属性对玩家进行排序：

```java
public class PlayerRankingComparator implements Comparator<Player> {

    @Override
    public int compare(Player firstPlayer, Player secondPlayer) {
       return Integer.compare(firstPlayer.getRanking(), secondPlayer.getRanking());
    }

}
```

同样，我们可以创建一个Comparator来使用Player的age属性对玩家进行排序：

```java
public class PlayerAgeComparator implements Comparator<Player> {

    @Override
    public int compare(Player firstPlayer, Player secondPlayer) {
       return Integer.compare(firstPlayer.getAge(), secondPlayer.getAge());
    }

}
```

###4.2.比较器在行动

为了演示这个概念，让我们通过向Collections.sort方法引入第二个参数来修改我们的PlayerSorter，这实际上是我们要使用的Comparator的实例。

使用这种方法，我们可以覆盖自然顺序：

```java
PlayerRankingComparator playerComparator = new PlayerRankingComparator();
Collections.sort(footballTeam, playerComparator);

```

现在让我们运行我们的PlayerRankingSorter来查看结果：

```java
Before Sorting : [John, Roger, Steven]
After Sorting by ranking : [Steven, John, Roger]
```

如果我们想要不同的排序顺序，我们只需要更改我们正在使用的比较器：

```java
PlayerAgeComparator playerComparator = new PlayerAgeComparator();
Collections.sort(footballTeam, playerComparator);
```

现在，当我们运行PlayerAgeSorter时，我们可以看到按年龄排序的不同顺序：

```java
Before Sorting : [John, Roger, Steven]
After Sorting by age : [Roger, John, Steven]
```

###4.3.Java8比较器

Java8提供了使用lambda表达式和comparing()静态工厂方法定义比较器的新方法。

让我们看一个如何使用lambda表达式创建Comparator的快速示例：

```java
Comparator byRanking = 
  (Player player1, Player player2) -> Integer.compare(player1.getRanking(), player2.getRanking());
```

Comparator.comparing方法采用一种方法计算将用于比较项目的属性，并返回匹配的Comparator实例：

```java
Comparator<Player> byRanking = Comparator
  .comparing(Player::getRanking);
Comparator<Player> byAge = Comparator
  .comparing(Player::getAge);
```

要深入探索Java8功能，请查看我们的[Java8Comparator.comparing](https://www.baeldung.com/java-8-comparator-comparing)指南。

##5.比较器与可比较

Comparable接口是用于定义默认排序的不错选择，或者换句话说，如果它是比较对象的主要方式。

那么，如果我们已经有了Comparable，为什么还要使用Comparator呢？

有几个原因：

-有时我们无法修改要对其对象进行排序的类的源代码，从而无法使用Comparable
-使用比较器可以让我们避免向领域类中添加额外的代码
-我们可以定义多个不同的比较策略，这在使用Comparable时是不可能的

##6.避免减法技巧

在本教程中，我们使用了Integer.compare()方法来比较两个整数。然而，有人可能会争辩说我们应该改用这个聪明的单行代码：

```java
Comparator<Player> comparator = (p1, p2) -> p1.getRanking() - p2.getRanking();
```

虽然它比其他解决方案简洁得多，但它可能成为Java中整数溢出的受害者：

```java
Player player1 = new Player(59, "John", Integer.MAX_VALUE);
Player player2 = new Player(67, "Roger", -1);

List<Player> players = Arrays.asList(player1, player2);
players.sort(comparator);
```

由于-1远小于Integer.MAX_VALUE，“Roger”应该在排序集合中排在“John”之前。但是，由于整数溢出，“Integer.MAX_VALUE–(-1)”将小于零。所以根据Comparator/Comparable契约，Integer.MAX_VALUE小于-1，这显然是不正确的。

因此，尽管如我们所料，“John”在排序集合中排在“Roger”之前：

```java
assertEquals("John", players.get(0).getName());
assertEquals("Roger", players.get(1).getName());
```

##七、总结

在本文中，我们探讨了Comparable和Comparator接口，并讨论了它们之间的区别。

要了解更高级的排序主题，请查看我们的其他文章，例如[Java8Comparator](https://www.baeldung.com/java-8-comparator-comparing)和[Java8ComparisonwithLambdas](https://www.baeldung.com/java-8-sort-lambda)。