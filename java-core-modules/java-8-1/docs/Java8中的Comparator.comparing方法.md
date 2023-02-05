## 1. 概述

Java 8对Comparator接口进行了一些增强，包括一些静态方法，这些方法在为集合排序时非常有用。

Comparator接口还可以有效地利用Java 8 lambda。lambdas和Comparator的详细解释可以在[这里](https://www.baeldung.com/java-8-sort-lambda)找到，比较器和排序应用的编年史可以在[这里](https://www.baeldung.com/java-sorting)找到。

在本教程中，**我们将探讨Java 8中为Comparator接口引入的几个方法**。

## 2. 入门

### 2.1 示例Bean类

对于本教程中的示例，让我们创建一个Employee bean并使用它的字段进行比较和排序：

```java
public class Employee {
    String name;
    int age;
    double salary;
    long mobile;

    // constructors, getters & setters
}
```

### 2.2 测试数据

我们还将创建一个Employee数组，用于在整个教程的各种测试用例中存储我们类型的结果：

```java
employees = new Employee[] { ... };
```

Employee元素的初始排序为：

```java
[Employee(name=John, age=25, salary=3000.0, mobile=9922001), 
Employee(name=Ace, age=22, salary=2000.0, mobile=5924001), 
Employee(name=Keith, age=35, salary=4000.0, mobile=3924401)]
```

在整个教程中，我们将使用不同的方法对上述Employee数组进行排序。

对于测试断言，我们使用一组预先排序的数组，并与不同场景的排序结果(即employees数组)进行比较。

让我们声明其中的一些数组：

```java
@BeforeEach
void initData() {
    sortedEmployeesByName = new Employee[] {...};
    sortedEmployeesByNameDesc = new Employee[] {...};
    sortedEmployeesByAge = new Employee[] {...};
    
    // ...
}
```

## 3. 使用Comparator.comparing

在本节中，我们介绍Comparator.comparing静态方法的重载。

### 3.1 key选择器变体

Comparator.comparing静态方法接收排序key的Function作为参数，并返回包含排序key的类型的Comparator：

```java
static <T,U extends Comparable<? super U>> Comparator<T> comparing(Function<? super T,? extends U> keyExtractor)
```

要看到它的作用，我们将使用Employee中的name字段作为排序key，并将其方法引用作为Function类型的参数传递。从中返回的Comparator用于排序：

```java
@Test
void whenComparing_thenSortedByName() {
	Comparator<Employee> employeeNameComparator = Comparator.comparing(Employee::getName);
    
	Arrays.sort(employees, employeeNameComparator);
    
	assertArrayEquals(employees, sortedEmployeesByName);
}
```

作为排序的结果，employees数组按名称排序：

```shell
[Employee(name=Ace, age=22, salary=2000.0, mobile=5924001), 
Employee(name=John, age=25, salary=3000.0, mobile=9922001), 
Employee(name=Keith, age=35, salary=4000.0, mobile=3924401)]
```

### 3.2 key选择器和比较器变体

另一种方法可以通过提供一个为排序key创建自定义排序机制的Comparator来覆盖key的默认自然排序：

```java
static <T,U> Comparator<T> comparing(Function<? super T,? extends U> keyExtractor, Comparator<? super U> keyComparator)
```

因此，让我们修改上面的测试。我们将通过提供一个用于按降序对名称进行排序的Comparator作为Comparator.comparing的第二个参数来覆盖按名称字段排序的自然顺序：

```java
@Test
void whenComparingWithComparator_thenSortedByNameDesc() {
	Comparator<Employee> employeeNameComparator = Comparator.comparing(
        Employee::getName, (s1, s2) -> {
        	return s2.compareTo(s1);
        });
    
	Arrays.sort(employees, employeeNameComparator);
    
	assertArrayEquals(employees, sortedEmployeesByNameDesc);
}
```

如我们所见，结果按名称降序排列：

```shell
[Employee(name=Keith, age=35, salary=4000.0, mobile=3924401), 
Employee(name=John, age=25, salary=3000.0, mobile=9922001), 
Employee(name=Ace, age=22, salary=2000.0, mobile=5924001)]
```

### 3.3 使用Comparator.reversed

当在现有的Comparator上调用时，实例方法Comparator.reversed返回一个新的Comparator，它反转原始的排序顺序。

我们将使用Comparator按姓名对员工进行排序并将其反转，以便员工按姓名的降序排序：

```java
@Test
void whenReversed_thenSortedByNameDesc() {
	Comparator<Employee> employeeNameComparator = Comparator.comparing(Employee::getName);
	Comparator<Employee> employeeNameComparatorReversed = employeeNameComparator.reversed();
    
	Arrays.sort(employees, employeeNameComparatorReversed);
    
	assertArrayEquals(employees, sortedEmployeesByNameDesc);
}
```

现在结果按名称降序排列：

```shell
[Employee(name=Keith, age=35, salary=4000.0, mobile=3924401), 
Employee(name=John, age=25, salary=3000.0, mobile=9922001), 
Employee(name=Ace, age=22, salary=2000.0, mobile=5924001)]
```

### 3.4 使用Comparator.comparingInt

还有一个方法Comparator.comparingInt，它与Comparator.comparing做同样的事情，但它只接收int选择器。我们通过一个按年龄对员工进行排序的例子演示：

```java
@Test
void whenComparingInt_thenSortedByAge() {
	Comparator<Employee> employeeAgeComparator = Comparator.comparingInt(Employee::getAge);
    
	Arrays.sort(employees, employeeAgeComparator);
    
	assertArrayEquals(employees, sortedEmployeesByAge);
}
```

排序后，employees数组的顺序如下：

```shell
[Employee(name=Ace, age=22, salary=2000.0, mobile=5924001), 
Employee(name=John, age=25, salary=3000.0, mobile=9922001), 
Employee(name=Keith, age=35, salary=4000.0, mobile=3924401)]
```

### 3.5 使用Comparator.comparingLong

与int选择器类似，让我们看一个使用Comparator.comparingLong的示例，通过mobile字段对员工数组进行排序来演示long类型的排序key：

```java
@Test
void whenComparingLong_thenSortedByMobile() {
	Comparator<Employee> employeeMobileComparator = Comparator.comparingLong(Employee::getMobile);
    
	Arrays.sort(employees, employeeMobileComparator);
    
	assertArrayEquals(employees, sortedEmployeesByMobile);
}
```

排序后，employees数组的顺序如下，以mobile为key：

```shell
[Employee(name=Keith, age=35, salary=4000.0, mobile=3924401), 
Employee(name=Ace, age=22, salary=2000.0, mobile=5924001), 
Employee(name=John, age=25, salary=3000.0, mobile=9922001)]
```

### 3.6 使用Comparator.comparingDouble

同样，正如我们对int和long所做的那样，让我们看一个使用Comparator.comparingDouble的示例，通过按salary字段对员工数组进行排序来演示double类型的排序key：

```java
@Test
void whenComparingDouble_thenSortedBySalary() {
	Comparator<Employee> employeeSalaryComparator = Comparator.comparingDouble(Employee::getSalary);
    
	Arrays.sort(employees, employeeSalaryComparator);
    
	assertArrayEquals(employees, sortedEmployeesBySalary);
}
```

排序后，employees数组的顺序如下，以salary为排序key：

```shell
[Employee(name=Ace, age=22, salary=2000.0, mobile=5924001), 
Employee(name=John, age=25, salary=3000.0, mobile=9922001), 
Employee(name=Keith, age=35, salary=4000.0, mobile=3924401)]
```

## 4. 在比较器中考虑自然顺序

我们可以通过Comparable接口实现的行为来定义自然顺序。有关Comparator和Comparable接口使用之间的差异的更多信息，请参阅[本文](https://www.baeldung.com/java-sorting)。

让我们在Employee类中实现Comparable，以便我们可以使用Comparator接口的naturalOrder和reverseOrder方法：

```java
public class Employee implements Comparable<Employee>{
    // ...

    @Override
    public int compareTo(Employee argEmployee) {
        return name.compareTo(argEmployee.getName());
    }
}
```

### 4.1 使用自然顺序

naturalOrder方法返回签名中提到的返回类型的Comparator：

```java
static <T extends Comparable<? super T>> Comparator<T> naturalOrder()
```

鉴于上述基于姓名字段比较员工的逻辑，让我们使用此方法获取一个比较器，该比较器按自然顺序对员工数组进行排序：

```java
@Test
void whenNaturalOrder_thenSortedByName() {
	Comparator<Employee> employeeNameComparator = Comparator.naturalOrder();
    
	Arrays.sort(employees, employeeNameComparator);
    
	assertArrayEquals(employees, sortedEmployeesByName);
}
```

排序后，employees数组的顺序如下：

```java
[Employee(name=Ace, age=22, salary=2000.0, mobile=5924001), 
Employee(name=John, age=25, salary=3000.0, mobile=9922001), 
Employee(name=Keith, age=35, salary=4000.0, mobile=3924401)]
```

### 4.2 使用反向自然顺序

与我们使用naturalOrder的方式类似，通过reverseOrder方法生成一个Comparator，它将产生与naturalOrder示例中的相反的员工排序：

```java
@Test
void whenReverseOrder_thenSortedByNameDesc() {
	Comparator<Employee> employeeNameComparator = Comparator.reverseOrder();
    
	Arrays.sort(employees, employeeNameComparator);
    
	assertArrayEquals(employees, sortedEmployeesByNameDesc);
}
```

排序后，employees数组的顺序如下：

```shell
[Employee(name=Keith, age=35, salary=4000.0, mobile=3924401), 
Employee(name=John, age=25, salary=3000.0, mobile=9922001), 
Employee(name=Ace, age=22, salary=2000.0, mobile=5924001)]
```

## 5. 在比较器中考虑空值

在本节中，我们将介绍nullsFirst和nullsLast方法，它们在排序中会考虑空值，并将空值保留在排序序列的开头或结尾。

### 5.1 首先考虑空值

让我们在员工数组中随机插入空值：

```shell
[Employee(name=John, age=25, salary=3000.0, mobile=9922001), 
null, 
Employee(name=Ace, age=22, salary=2000.0, mobile=5924001), 
null, 
Employee(name=Keith, age=35, salary=4000.0, mobile=3924401)]
```

nullsFirst方法将返回一个Comparator，该比较器将所有空值保留在排序序列的开头：

```java
@Test
void whenNullsFirst_thenSortedByNameWithNullsFirst() {
	Comparator<Employee> employeeNameComparator = Comparator.comparing(Employee::getName);
    
	Comparator<Employee> employeeNameComparator_nullFirst = Comparator.nullsFirst(employeeNameComparator);
    
	Arrays.sort(employeesArrayWithNulls, employeeNameComparator_nullFirst);

	assertArrayEquals(employeesArrayWithNulls, sortedEmployeesArray_WithNullsFirst);
}
```

排序后，employees数组的顺序如下：

```shell
[null, 
null, 
Employee(name=Ace, age=22, salary=2000.0, mobile=5924001), 
Employee(name=John, age=25, salary=3000.0, mobile=9922001), 
Employee(name=Keith, age=35, salary=4000.0, mobile=3924401)]
```

### 5.2 最后考虑空值

nullsLast方法返回一个比较器，它将所有空值保留在排序序列的末尾：

```java
@Test
void whenNullsLast_thenSortedByNameWithNullsLast() {
	Comparator<Employee> employeeNameComparator = Comparator.comparing(Employee::getName);
    
	Comparator<Employee> employeeNameComparator_nullLast = Comparator.nullsLast(employeeNameComparator);
    
	Arrays.sort(employeesArrayWithNulls, employeeNameComparator_nullLast);

	assertArrayEquals(employeesArrayWithNulls, sortedEmployeesArray_WithNullsLast);
}
```

排序后，employees数组的顺序如下：

```shell
[Employee(name=Ace, age=22, salary=2000.0, mobile=5924001), 
Employee(name=John, age=25, salary=3000.0, mobile=9922001), 
Employee(name=Keith, age=35, salary=4000.0, mobile=3924401), 
null, 
null]
```

## 6. 使用Comparator.thenComparing

thenComparing方法允许我们通过按特定顺序提供多个排序key来设置值的字典顺序。

让我们看一下Employee类的另一个数组：

```java
someMoreEmployees = new Employee[] { ... };
```

我们希望对以上数组排序后的顺序如下：

```shell
[Employee(name=Jake, age=25, salary=3000.0, mobile=9922001), 
Employee(name=Jake, age=22, salary=2000.0, mobile=5924001), 
Employee(name=Ace, age=22, salary=3000.0, mobile=6423001), 
Employee(name=Keith, age=35, salary=4000.0, mobile=3924401)]
```

然后我们编写一个比较器序列，首先按年龄排序，然后按名称排序，并查看该数组的顺序：

```java
@Test
void whenThenComparing_thenSortedByAgeName() {
	Comparator<Employee> employee_Age_Name_Comparator = Comparator.comparing(Employee::getAge).thenComparing(Employee::getName);
    
	Arrays.sort(someMoreEmployees, employee_Age_Name_Comparator);
    
	assertArrayEquals(someMoreEmployees, sortedEmployeesByAgeName);
}
```

在这里，排序将按年龄完成，对于具有相同年龄的值，排序将按名称完成。我们可以在排序后得到的数组中看到这一点：

```shell
[Employee(name=Ace, age=22, salary=3000.0, mobile=6423001), 
Employee(name=Jake, age=22, salary=2000.0, mobile=5924001), 
Employee(name=Jake, age=25, salary=3000.0, mobile=9922001), 
Employee(name=Keith, age=35, salary=4000.0, mobile=3924401)]
```

我们也可以使用thenComparing的另一个版本thenComparingInt，方法是将字典顺序更改为name后跟age：

```java
@Test
void whenThenComparing_thenSortedByNameAge() {
	Comparator<Employee> employee_Name_Age_Comparator = Comparator.comparing(Employee::getName).thenComparingInt(Employee::getAge);
    
	Arrays.sort(someMoreEmployees, employee_Name_Age_Comparator);

	assertArrayEquals(someMoreEmployees, sortedEmployeesByNameAge);
}
```

排序后，employees数组的顺序如下：

```shell
[Employee(name=Ace, age=22, salary=3000.0, mobile=6423001), 
Employee(name=Jake, age=22, salary=2000.0, mobile=5924001), 
Employee(name=Jake, age=25, salary=3000.0, mobile=9922001), 
Employee(name=Keith, age=35, salary=4000.0, mobile=3924401)]
```

类似地，方法thenComparingLong和thenComparingDouble分别用于使用long和double排序key。

## 7. 总结

本文是Java 8中为Comparator接口引入的几个功能的指南。