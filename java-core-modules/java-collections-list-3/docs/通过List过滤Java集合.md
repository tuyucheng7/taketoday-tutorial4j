## 1. 概述

通过列表过滤集合是一种常见的业务逻辑场景。有很多方法可以实现这一点。但是，如果处理不当，有些可能会导致性能不佳的解决方案。

在本教程中，我们将比较一些过滤实现并讨论它们的优点和缺点。

## 2. 使用For-Each循环

我们将从最经典的语法开始，即 for-each 循环。

对于本文中的这个示例和所有其他示例，我们将使用以下类：

```java
public class Employee {

    private Integer employeeNumber;
    private String name;
    private Integer departmentId;
    //Standard constructor, getters and setters.
}
```

为了简单起见，我们还将对所有示例使用以下方法：

```java
private List<Employee> buildEmployeeList() {
    return Arrays.asList(
      new Employee(1, "Mike", 1),
      new Employee(2, "John", 1),
      new Employee(3, "Mary", 1),
      new Employee(4, "Joe", 2),
      new Employee(5, "Nicole", 2),
      new Employee(6, "Alice", 2),
      new Employee(7, "Bob", 3),
      new Employee(8, "Scarlett", 3));
}

private List<String> employeeNameFilter() {
    return Arrays.asList("Alice", "Mike", "Bob");
}
```

对于我们的示例，我们将根据第二个具有员工姓名的列表过滤第一个员工列表，以仅查找具有这些特定名称的员工。

现在，让我们看看传统的方法—— 循环遍历两个列表以查找匹配项：

```java
@Test
public void givenEmployeeList_andNameFilterList_thenObtainFilteredEmployeeList_usingForEachLoop() {
    List<Employee> filteredList = new ArrayList<>();
    List<Employee> originalList = buildEmployeeList();
    List<String> nameFilter = employeeNameFilter();

    for (Employee employee : originalList) {
        for (String name : nameFilter) {
            if (employee.getName().equals(name)) {
                filteredList.add(employee);
                // break;
            }
        }
    }

    assertThat(filteredList.size(), is(nameFilter.size()));
}
```

这是一个简单的语法，但它非常冗长，实际上效率很低。简单地说，它迭代两个集合的笛卡尔积以获得我们的答案。

即使添加 中断以提早退出，在一般情况下仍将按照与笛卡尔积相同的顺序进行迭代。

如果我们称员工列表的大小为 n， 那么 nameFilter的顺序将一样大，给我们一个 O(n 2 ) 的分类。

## 3. 使用流和列表#contains

我们现在将通过使用 lambda 来简化语法和提高可读性来重构以前的方法。让我们也使用List#contains 方法作为[lambda 过滤器](https://www.baeldung.com/java-stream-filter-lambda)：

```java
@Test
public void givenEmployeeList_andNameFilterList_thenObtainFilteredEmployeeList_usingLambda() {
    List<Employee> filteredList;
    List<Employee> originalList = buildEmployeeList();
    List<String> nameFilter = employeeNameFilter();

    filteredList = originalList.stream()
      .filter(employee -> nameFilter.contains(employee.getName()))
      .collect(Collectors.toList());

    assertThat(filteredList.size(), is(nameFilter.size()));
}
```

通过使用[Stream API](https://www.baeldung.com/java-8-streams-introduction)，可读性得到了极大的提高，但是我们的代码仍然和我们以前的方法一样低效，因为它仍然在内部迭代笛卡尔积。因此，我们有相同的O(n 2 ) 分类。

## 4. 将流与HashSet结合使用

为了提高性能，我们必须使用HashSet#contains 方法。此方法与List#contains不同， 因为它执行哈希码查找，为我们提供恒定时间的操作数：

```java
@Test
public void givenEmployeeList_andNameFilterList_thenObtainFilteredEmployeeList_usingLambdaAndHashSet() {
    List<Employee> filteredList;
    List<Employee> originalList = buildEmployeeList();
    Set<String> nameFilterSet = employeeNameFilter().stream().collect(Collectors.toSet());

    filteredList = originalList.stream()
      .filter(employee -> nameFilterSet.contains(employee.getName()))
      .collect(Collectors.toList());

    assertThat(filteredList.size(), is(nameFilterSet.size()));
}
```

通过使用[HashSet](https://www.baeldung.com/java-hashset)，我们的代码效率大大提高，同时不影响可读性。由于 HashSet#contains在恒定时间内运行，我们已将分类改进为 O(n)。

## 5.总结

在本快速教程中，我们学习了如何通过值列表过滤集合，以及使用看似最直接的方法的缺点。

我们必须始终考虑效率，因为我们的代码最终可能会在庞大的数据集中运行，而性能问题可能会在此类环境中造成灾难性后果。