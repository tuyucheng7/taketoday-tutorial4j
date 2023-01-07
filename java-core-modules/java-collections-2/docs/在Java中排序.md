## 1. 概述

本文将说明如何在Java7 和Java8 中对Array、List、Set和Map应用排序。

## 2.数组排序

让我们首先使用[Arrays.sort()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Arrays.html#sort(byte[])) 方法对整数数组进行排序。

我们将在@Before jUnit 方法中定义以下int数组：

```java
@Before
public void initVariables () {
    toSort = new int[] 
      { 5, 1, 89, 255, 7, 88, 200, 123, 66 }; 
    sortedInts = new int[] 
      {1, 5, 7, 66, 88, 89, 123, 200, 255};
    sortedRangeInts = new int[] 
      {5, 1, 89, 7, 88, 200, 255, 123, 66};
    ...
}
```

### 2.1. 排序完整数组

现在让我们使用简单的Array.sort() API：

```java
@Test
public void givenIntArray_whenUsingSort_thenSortedArray() {
    Arrays.sort(toSort);

    assertTrue(Arrays.equals(toSort, sortedInts));
}
```

未排序的数组现在已完全排序：

```shell
[1, 5, 7, 66, 88, 89, 123, 200, 255]
```

[如官方 JavaDoc 中所述](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Arrays.html#sort(int[]))，Arrays.sort在原语上使用双枢轴快速排序。它提供 O(n log(n)) 性能，通常比传统(单轴)快速排序实现更快。[但是，它为对象](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Arrays.html#sort(java.lang.Object[]))[数组](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Arrays.html#sort(java.lang.Object[]))使用稳定的、自适应的、迭代的[合并排序算法实现](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Arrays.html#sort(java.lang.Object[]))。

### 2.2. 对数组的一部分进行排序

Arrays.sort还有一种排序API——我们将在这里讨论：

```java
Arrays.sort(int[] a, int fromIndex, int toIndex)
```

这只会对两个索引之间的数组的一部分进行排序。

让我们看一个简单的例子：

```java
@Test
public void givenIntArray_whenUsingRangeSort_thenRangeSortedArray() {
    Arrays.sort(toSort, 3, 7);
 
    assertTrue(Arrays.equals(toSort, sortedRangeInts));
}
```

排序将仅在以下子数组元素上完成(toIndex将是排他的)：

```shell
[255, 7, 88, 200]
```

包含主数组的结果排序子数组将是：

```shell
[5, 1, 89, 7, 88, 200, 255, 123, 66]
```

### 2.3.Java8 Arrays.sort与Arrays.parallelSort

Java 8 带有一个新的 API – parallelSort – 具有与Arrays.sort() API类似的签名：

```java
@Test 
public void givenIntArray_whenUsingParallelSort_thenArraySorted() {
    Arrays.parallelSort(toSort);
 
    assertTrue(Arrays.equals(toSort, sortedInts));
}
```

在parallelSort()的幕后，它将数组分成不同的子数组(根据parallelSort算法中的粒度)。每个子数组都在不同的线程中使用Arrays.sort()进行排序，以便排序可以以并行方式执行，并最终合并为一个排序数组。

请注意，[ForJoin 公共池](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ForkJoinPool.html#commonPool())用于执行这些并行任务，然后合并结果。

Arrays.parallelSort的结果将与Array.sort相同，当然，这只是利用多线程的问题。

最后，在Arrays.parallelSort中也有类似的 API Arrays.sort变体：

```java
Arrays.parallelSort (int [] a, int fromIndex, int toIndex);
```

## 3. 排序列表 

现在让我们使用[java.utils.Collections 中的](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collections.html)[Collections.sort()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collections.html#sort(java.util.List)) API——对整数列表进行排序：

```java
@Test
public void givenList_whenUsingSort_thenSortedList() {
    List<Integer> toSortList = Ints.asList(toSort);
    Collections.sort(toSortList);

    assertTrue(Arrays.equals(toSortList.toArray(), 
    ArrayUtils.toObject(sortedInts)));
}
```

排序前的List将包含以下元素：

```shell
[5, 1, 89, 255, 7, 88, 200, 123, 66]
```

自然地，排序后：

```shell
[1, 5, 7, 66, 88, 89, 123, 200, 255]
```

[正如 Oracle JavaDoc](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collections.html#sort(java.util.List)) for Collections.Sort中所述，它使用修改后的合并排序并提供有保证的n log(n)性能。

## 4. 对集合进行排序

接下来，让我们使用[Collections.sort()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collections.html#sort(java.util.List))对LinkedHashSet进行排序。

我们使用LinkedHashSet是因为它维护插入顺序。

请注意，为了在集合中使用排序API，我们首先将集合包装在列表中：

```java
@Test
public void givenSet_whenUsingSort_thenSortedSet() {
    Set<Integer> integersSet = new LinkedHashSet<>(Ints.asList(toSort));
    Set<Integer> descSortedIntegersSet = new LinkedHashSet<>(
      Arrays.asList(new Integer[] 
        {255, 200, 123, 89, 88, 66, 7, 5, 1}));
        
    List<Integer> list = new ArrayList<Integer>(integersSet);
    Collections.sort(Comparator.reverseOrder());
    integersSet = new LinkedHashSet<>(list);
        
    assertTrue(Arrays.equals(
      integersSet.toArray(), descSortedIntegersSet.toArray()));
}
```

[Comparator.reverseOrder()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Comparator.html#reverseOrder()) 方法反转自然排序强加的排序。 

## 5.排序图

在本节中，我们将开始研究对 Map 进行排序——既可以按键也可以按值。

让我们首先定义要排序的地图：

```java
@Before
public void initVariables () {
    ....
    HashMap<Integer, String> map = new HashMap<>();
    map.put(55, "John");
    map.put(22, "Apple");
    map.put(66, "Earl");
    map.put(77, "Pearl");
    map.put(12, "George");
    map.put(6, "Rocky");
    ....
}
```

### 5.1. 按键排序地图

我们现在将从HashMap中提取键和值条目，并根据本示例中键的值对其进行排序：

```java
@Test
public void givenMap_whenSortingByKeys_thenSortedMap() {
    Integer[] sortedKeys = new Integer[] { 6, 12, 22, 55, 66, 77 };

    List<Map.Entry<Integer, String>> entries 
      = new ArrayList<>(map.entrySet());
    Collections.sort(entries, new Comparator<Entry<Integer, String>>() {
        @Override
        public int compare(
          Entry<Integer, String> o1, Entry<Integer, String> o2) {
            return o1.getKey().compareTo(o2.getKey());
        }
    });
    Map<Integer, String> sortedMap = new LinkedHashMap<>();
    for (Map.Entry<Integer, String> entry : entries) {
        sortedMap.put(entry.getKey(), entry.getValue());
    }
        
    assertTrue(Arrays.equals(sortedMap.keySet().toArray(), sortedKeys));
}
```

请注意我们在基于键的排序条目时如何使用LinkedHashMap (因为HashSet不保证键的顺序)。

排序前的地图：

```shell
[Key: 66 , Value: Earl] 
[Key: 22 , Value: Apple] 
[Key: 6 , Value: Rocky] 
[Key: 55 , Value: John] 
[Key: 12 , Value: George] 
[Key: 77 , Value: Pearl]
```

按键排序后的地图：

```shell
[Key: 6 , Value: Rocky] 
[Key: 12 , Value: George] 
[Key: 22 , Value: Apple] 
[Key: 55 , Value: John] 
[Key: 66 , Value: Earl] 
[Key: 77 , Value: Pearl]

```

### 5.2. 按值排序地图

在这里，我们将比较HashMap条目的值以根据HashMap的值进行排序：

```java
@Test
public void givenMap_whenSortingByValues_thenSortedMap() {
    String[] sortedValues = new String[] 
      { "Apple", "Earl", "George", "John", "Pearl", "Rocky" };

    List<Map.Entry<Integer, String>> entries 
      = new ArrayList<>(map.entrySet());
    Collections.sort(entries, new Comparator<Entry<Integer, String>>() {
        @Override
        public int compare(
          Entry<Integer, String> o1, Entry<Integer, String> o2) {
            return o1.getValue().compareTo(o2.getValue());
        }
    });
    Map<Integer, String> sortedMap = new LinkedHashMap<>();
    for (Map.Entry<Integer, String> entry : entries) {
        sortedMap.put(entry.getKey(), entry.getValue());
    }
        
    assertTrue(Arrays.equals(sortedMap.values().toArray(), sortedValues));
}
```

排序前的地图：

```shell
[Key: 66 , Value: Earl] 
[Key: 22 , Value: Apple] 
[Key: 6 , Value: Rocky] 
[Key: 55 , Value: John] 
[Key: 12 , Value: George] 
[Key: 77 , Value: Pearl]
```

按值排序后的地图：

```shell
[Key: 22 , Value: Apple] 
[Key: 66 , Value: Earl] 
[Key: 12 , Value: George] 
[Key: 55 , Value: John] 
[Key: 77 , Value: Pearl] 
[Key: 6 , Value: Rocky]
```

## 6. 对自定义对象进行排序

现在让我们使用自定义对象：

```java
public class Employee implements Comparable {
    private String name;
    private int age;
    private double salary;

    public Employee(String name, int age, double salary) {
        ...
    }

    // standard getters, setters and toString
}
```

我们将在以下部分中使用以下员工数组进行排序示例：

```java
@Before
public void initVariables () {
    ....    
    employees = new Employee[] { 
      new Employee("John", 23, 5000), new Employee("Steve", 26, 6000), 
      new Employee("Frank", 33, 7000), new Employee("Earl", 43, 10000), 
      new Employee("Jessica", 23, 4000), new Employee("Pearl", 33, 6000)};
    
    employeesSorted = new Employee[] {
      new Employee("Earl", 43, 10000), new Employee("Frank", 33, 70000),
      new Employee("Jessica", 23, 4000), new Employee("John", 23, 5000), 
      new Employee("Pearl", 33, 4000), new Employee("Steve", 26, 6000)};
    
    employeesSortedByAge = new Employee[] { 
      new Employee("John", 23, 5000), new Employee("Jessica", 23, 4000), 
      new Employee("Steve", 26, 6000), new Employee("Frank", 33, 70000), 
      new Employee("Pearl", 33, 4000), new Employee("Earl", 43, 10000)};
}
```

我们可以对自定义对象的数组或集合进行排序：

1.  按自然顺序(使用可比接口)或
2.  按照比较器 接口提供的顺序

### 6.1. 使用可比

[Java 中的自然顺序是](https://docs.oracle.com/javase/tutorial/collections/interfaces/order.html)指原语或对象在给定的数组或集合中应该有序排序的顺序。

[java.util.Arrays](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Arrays.html) 和[java.util.Collections ](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collections.html)都有一个sort()方法，强烈建议自然顺序应该与equals的语义一致。

在这个例子中，我们将同名员工视为平等：

```java
@Test
public void givenEmpArray_SortEmpArray_thenSortedArrayinNaturalOrder() {
    Arrays.sort(employees);

    assertTrue(Arrays.equals(employees, employeesSorted));
}
```

你可以通过实现Comparable接口来定义元素的自然顺序，该接口具有用于比较当前对象和作为参数传递的对象的compareTo()方法。

为了清楚地理解这一点，让我们看一个实现Comparable接口的示例Employee类：

```java
public class Employee implements Comparable {
    ...

    @Override
    public boolean equals(Object obj) {
        return ((Employee) obj).getName().equals(getName());
    }

    @Override
    public int compareTo(Object o) {
        Employee e = (Employee) o;
        return getName().compareTo(e.getName());
    }
}
```

通常，比较的逻辑会写在compareTo方法中。这里我们比较的是雇员字段的雇员顺序或姓名。如果两个员工具有相同的名字，他们将是平等的。

现在Arrays.sort(employees); 在上面的代码中被调用，我们现在知道根据年龄对员工进行排序的逻辑和顺序是什么：

```shell
[("Earl", 43, 10000),("Frank", 33, 70000), ("Jessica", 23, 4000),
 ("John", 23, 5000),("Pearl", 33, 4000), ("Steve", 26, 6000)]
```

我们可以看到数组是按员工姓名排序的——现在这成为员工类的自然顺序。

### 6.2. 使用比较器 

现在，让我们使用Comparator接口实现对元素进行排序——我们将匿名内部类即时传递给Arrays.sort() API：

```java
@Test
public void givenIntegerArray_whenUsingSort_thenSortedArray() {
    Integer [] integers = ArrayUtils.toObject(toSort);
    Arrays.sort(integers, new Comparator<Integer>() {
        @Override
        public int compare(Integer a, Integer b) {
            return Integer.compare(a, b);
        }
    });
 
    assertTrue(Arrays.equals(integers, ArrayUtils.toObject(sortedInts)));
}
```

现在让我们根据薪水对员工进行排序——并传入另一个比较器实现：

```java
Arrays.sort(employees, new Comparator<Employee>() {
    @Override
    public int compare(Employee o1, Employee o2) {
       return Double.compare(o1.getSalary(), o2.getSalary());
    }
 });
```

根据薪水排序的 Employees 数组将是：

```shell
[(Jessica,23,4000.0), (John,23,5000.0), (Pearl,33,6000.0), (Steve,26,6000.0), 
(Frank,33,7000.0), (Earl,43,10000.0)]

```

请注意，我们可以以类似的方式使用Collections.sort()以自然或自定义顺序对对象列表和集合进行排序，如上文所述的数组。

## 7. 使用 Lambdas 排序

从Java8 开始，我们可以使用 Lambdas 来实现Comparator Functional Interface。

你可以查看[Java 8](https://www.baeldung.com/java-streams#lambda)中的Lambdas writeup 以复习语法。

让我们替换旧的比较器：

```java
Comparator<Integer> c  = new Comparator<>() {
    @Override
    public int compare(Integer a, Integer b) {
        return Integer.compare(a, b);
    }
}
```

在等效的实现中，使用 Lambda 表达式：

```java
Comparator<Integer> c = (a, b) -> Integer.compare(a, b);
```

最后，让我们编写测试：

```java
@Test
public void givenArray_whenUsingSortWithLambdas_thenSortedArray() {
    Integer [] integersToSort = ArrayUtils.toObject(toSort);
    Arrays.sort(integersToSort, (a, b) -> {
        return Integer.compare(a, b);
    });
 
    assertTrue(Arrays.equals(integersToSort, 
      ArrayUtils.toObject(sortedInts)));
}
```

如你所见，这里的逻辑更加简洁明了。

## 8. 使用Comparator.comparing和Comparator.thenComparing

Java 8 带有两个对排序有用的新 API—— Comparator接口中的[comparing()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Comparator.html#comparing(java.util.function.Function)) 和[thenComparing() 。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Comparator.html#thenComparing(java.util.Comparator))

这些对于链接Comparator的多个条件非常方便。

让我们考虑一个场景，我们可能希望先按年龄比较Employee ，然后再按姓名进行比较：

```java
@Test
public void givenArrayObjects_whenUsingComparing_thenSortedArrayObjects() {
    List<Employee> employeesList = Arrays.asList(employees);
    employees.sort(Comparator.comparing(Employee::getAge));

    assertTrue(Arrays.toString(employees.toArray())
      .equals(sortedArrayString));
}
```

在此示例中，Employee::getAge是Comparator接口的排序键，实现了具有比较功能的功能接口。

这是排序后的 Employees 数组：

```shell
[(John,23,5000.0), (Jessica,23,4000.0), (Steve,26,6000.0), (Frank,33,7000.0), 
(Pearl,33,6000.0), (Earl,43,10000.0)]
```

这里的员工是根据年龄排序的。

我们可以看到John和Jessica年龄相同——这意味着订单逻辑现在应该考虑他们的名字——我们可以使用thenComparing()来实现：

```java
... 
employees.sort(Comparator.comparing(Employee::getAge)
  .thenComparing(Employee::getName)); 
...

```

使用上述代码片段排序后，employee 数组中的元素将排序为：

```shell
[(Jessica,23,4000.0), 
 (John,23,5000.0), 
 (Steve,26,6000.0), 
 (Frank,33,7000.0), 
 (Pearl,33,6000.0), 
 (Earl,43,10000.0)
]
```

因此comparing()和thenComparing()肯定会使更复杂的排序场景更容易实现。

## 9.总结

在本文中，我们了解了如何将排序应用于Array、List、Set和Map。

我们还看到了关于Java8 的特性如何在排序方面有用的简要介绍，例如 Lambdas、comparing()和thenComparing()以及parallelSort()的用法。