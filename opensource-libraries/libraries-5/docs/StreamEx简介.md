## 1. 概述

[Stream API](https://www.baeldung.com/java-8-streams)是Java8 最令人兴奋的特性之一——简单地说，它是处理元素序列的强大工具。

[StreamEx](https://amaembo.github.io/streamex/javadoc/)是一个为标准 Stream API 提供附加功能以及性能改进的库。

以下是一些核心功能：

-   完成日常任务的更短、更方便的方法
-   与原始 JDK Streams 100% 兼容
-   并行处理的友好性：任何新功能都尽可能地利用并行流
-   性能和最小的开销。如果StreamEx允许使用与标准Stream相比更少的代码来解决任务，那么它应该不会比通常的方式慢很多(有时甚至更快)

在本教程中，我们将介绍StreamEx API 的一些功能。

## 2.设置示例

要使用StreamEx，我们需要将以下依赖项添加到pom.xml：

```xml
<dependency>
    <groupId>one.util</groupId>
    <artifactId>streamex</artifactId>
    <version>0.6.5</version>
</dependency>
```

可以在[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"one.util" AND a%3A"streamex")上找到该库的最新版本。

通过本教程，我们将使用一个简单的User类：

```java
public class User {
    int id;
    String name;
    Role role = new Role();

    // standard getters, setters, and constructors
}
```

还有一个简单的Role类：

```java
public class Role {
}
```

## 3.收藏家快捷方式

Streams最流行的终端操作之一是收集操作；这允许将Stream元素重新打包到我们选择的集合中。

问题是对于简单的场景，代码可能会变得不必要的冗长：

```java
users.stream()
  .map(User::getName)
  .collect(Collectors.toList());
```

### 3.1. 收集到一个集合

现在，有了 StreamEx，我们不需要提供Collector来指定我们需要List、Set 、 Map 、 InmutableList等：

```java
List<String> userNames = StreamEx.of(users)
  .map(User::getName)
  .toList();
```

如果我们想要执行比从Stream中获取元素并将它们放入集合中更复杂的操作，那么收集操作在 API 中仍然可用。

### 3.2. 高级收藏家

另一个速记是groupingBy：

```java
Map<Role, List<User>> role2users = StreamEx.of(users)
  .groupingBy(User::getRole);
```

这将生成一个具有方法引用中指定的键类型的Map，生成类似于 SQL 中的 group by 操作的内容。

使用普通的Stream API，我们需要编写：

```java
Map<Role, List<User>> role2users = users.stream()
  .collect(Collectors.groupingBy(User::getRole));
```

可以为Collectors.joining() 找到类似的简写形式：

```java
StreamEx.of(1, 2, 3)
  .joining("; "); // "1; 2; 3"
```

它采用Stream中的所有元素a 生成一个String连接所有这些元素。

## 4. 添加、删除和选择元素

在某些场景下，我们有一个不同类型的对象列表，我们需要按类型过滤它们：

```java
List usersAndRoles = Arrays.asList(new User(), new Role());
List<Role> roles = StreamEx.of(usersAndRoles)
  .select(Role.class)
  .toList();
```

我们可以通过以下方便的操作将元素添加到Stream的开头或结尾：

```java
List<String> appendedUsers = StreamEx.of(users)
  .map(User::getName)
  .prepend("(none)")
  .append("LAST")
  .toList();
```

我们可以使用nonNull()删除不需要的 null 元素，并将Stream用作Iterable：

```java
for (String line : StreamEx.of(users).map(User::getName).nonNull()) {
    System.out.println(line);
}
```

## 5. 数学运算和原始类型支持

StreamEx添加了对原始类型的支持，正如我们在这个不言自明的示例中所见：

```java
short[] src = {1,2,3};
char[] output = IntStreamEx.of(src)
  .map(x -> x  5)
  .toCharArray();
```

现在让我们以无序的方式获取一个double元素数组。我们想要创建一个由每对之间的差异组成的数组。

我们可以使用pairMap方法来执行这个操作：

```java
public double[] getDiffBetweenPairs(double... numbers) {
    return DoubleStreamEx.of(numbers)
      .pairMap((a, b) -> b - a)
      .toArray();
}
```

## 6.地图操作

### 6.1. 按键过滤

另一个有用的功能是能够从Map创建Stream并使用它们指向的值过滤元素。

在这种情况下，我们采用所有非空值：

```java
Map<String, Role> nameToRole = new HashMap<>();
nameToRole.put("first", new Role());
nameToRole.put("second", null);
Set<String> nonNullRoles = StreamEx.ofKeys(nameToRole, Objects::nonNull)
  .toSet();
```

### 6.2. 操作键值对

我们还可以通过创建一个EntryStream实例来操作键值对：

```java
public Map<User, List<Role>> transformMap( 
    Map<Role, List<User>> role2users) {
    Map<User, List<Role>> users2roles = EntryStream.of(role2users)
     .flatMapValues(List::stream)
     .invert()
     .grouping();
    return users2roles;
}
```

特殊操作EntryStream.of获取一个Map并将其转换为键值对象的Stream 。然后我们使用flatMapValues操作将我们的角色列表转换为单值流。

接下来，我们可以反转键值对，使User类成为键，Role类成为值。

最后，我们可以使用分组操作将我们的地图转换为接收到的地图的反转，所有这些只需四个操作。

### 6.3. 键值映射

我们还可以独立映射键和值：

```java
Map<String, String> mapToString = EntryStream.of(users2roles)
  .mapKeys(String::valueOf)
  .mapValues(String::valueOf)
  .toMap();
```

有了这个，我们可以快速将我们的键或值转换为另一种所需的类型。

## 七、文件操作

使用StreamEx，我们可以高效地读取文件，即无需一次加载完整文件。处理大文件时很方便：

```java
StreamEx.ofLines(reader)
  .remove(String::isEmpty)
  .forEach(System.out::println);
```

请注意，我们使用了remove()方法来过滤掉空行。

这里需要注意的是StreamEx不会自动关闭文件。因此，我们一定要记得在读写文件的时候手动执行关闭操作，以避免不必要的内存开销。

## 八. 总结

在本教程中，我们了解了StreamEx及其不同的实用程序。还有很多事情要做——他们在[这里](https://github.com/amaembo/streamex/blob/master/wiki/CHEATSHEET.md)有一个方便的备忘单。