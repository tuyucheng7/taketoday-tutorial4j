## 一、概述

Java [Records](https://www.baeldung.com/java-record-vs-final-class)是一种在 Java 14 中定义不可变数据容器的简洁方法。

在本文中，我们将探索 Java Records 中的自定义构造函数如何通过允许数据验证和错误处理在对象初始化期间为我们提供更大的控制。

## 2. 了解 Java 记录

记录提供简洁、可读的语法，强制不变性，并**生成常用方法（**如*toString()*、*hashCode()*和*equals() ）*的标准实现。这些实现基于记录的组件并由编译器自动生成。

记录是使用*record*关键字定义的，后跟记录的名称及其组件：

```java
record StudentRecord(String name, int rollNo, int marks) {}复制
```

*此记录定义创建一个名为StudentRecord*的新记录类，包含三个组件：*name*、*rollNo*和*marks*。

这些组件是浅层不变的实例变量，这意味着**一旦创建了记录实例，它们就无法更改**。但是，可以更改记录中包含的可变对象。

默认情况下，记录组件是私有的，只能通过访问器方法访问。可以将自定义方法和行为添加到记录中，但组件必须保持*私有*并且只能通过访问器方法访问：

```java
@Test
public void givenStudentRecordData_whenCreated_thenStudentPropertiesMatch() {
     StudentRecord s1 = new StudentRecord("John", 1, 90);
     StudentRecord s2 = new StudentRecord("Jane", 2, 80);

     assertEquals("John", s1.name());
     assertEquals(1, s1.rollNo());
     assertEquals(90, s1.marks());
     assertEquals("Jane", s2.name());
     assertEquals(2, s2.rollNo());
     assertEquals(80, s2.marks());
}复制
```

在此示例中，我们使用生成的访问器方法来检查记录组件的值。

## 3. 如何为 Java 记录创建自定义构造函数

自定义构造函数在 Java 记录中至关重要，因为它们提供了添加更多逻辑和控制记录对象创建的能力。

与 Java 编译器提供的标准实现相比，自定义构造函数为记录提供了更多功能。

为了保证数据完整性并能够按名称对*StudentRecord* 进行排序，我们可以为输入验证和字段初始化创建自定义构造函数：

```java
record Student(String name, int rollNo, int marks) {
    public Student {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
    }
}复制
```

在这里，自定义构造函数检查*名称*组件是否为*null*。如果它是*null*，它会抛出一个*IllegalArgumentException*。这使我们能够验证输入数据并确保以有效状态创建记录对象。

### 3.1 对StudentRecord*对象*列表*进行排序*

现在我们已经了解了如何为记录创建自定义构造函数，让我们在示例中使用此自定义构造函数按名称对*StudentRecord*对象列表进行排序：

```java
@Test
public void givenStudentRecordsList_whenSortingDataWithName_thenStudentsSorted(){
    List<StudentRecord> studentRecords = List.of(
        new StudentRecord("Dana", 1, 85),
        new StudentRecord("Jim", 2, 90),
        new StudentRecord("Jane", 3, 80)
        );

    List<StudentRecord> mutableStudentRecords = new ArrayList<>(studentRecords);
    mutableStudentRecords.sort(Comparator.comparing(StudentRecord::name));
    List<StudentRecord> sortedStudentRecords = 
      List.copyOf(mutableStudentRecords);

    assertEquals("Jane", sortedStudentRecords.get(1).name());
}复制
```

*在此示例中，我们创建了一个StudentRecord*对象列表 ，我们可以按 *名称*对其进行排序。我们不需要 [在排序时处理空值，](https://www.baeldung.com/java-8-comparator-comparing#considering-null-values-in-comparator) 因为 *name* 永远不会为*null*。

总而言之，Java 记录中的自定义构造函数提供了添加额外逻辑和控制记录对象创建的灵活性。虽然标准实现很简单，但自定义构造函数使记录更加通用和有用。

## 4. Java 记录中自定义构造函数的优点和局限性

与任何语言特性一样，Java Records 中的自定义构造函数有其自身的优点和局限性。下面，我们将更详细地探讨这些。

### 4.1. 好处

自定义构造函数可以为 Java Records 带来多种好处。它们可用于**为传入的数据提供额外的验证**，例如，通过检查值是否在特定范围内或是否满足特定条件。

例如，假设我们要确保*StudentRecord中的**marks*字段始终介于 0 和 100 之间。我们可以创建一个自定义构造函数来检查*marks*字段是否在范围内。如果超出范围，我们可以抛出异常或将其设置为默认值，如 0：

```java
public StudentRecord {
    if (marks < 0 || marks > 100) {
        throw new IllegalArgumentException("Marks should be between 0 and 100.");
    }
}复制
```

Java 记录中的自定义构造函数也可用于**将相关数据提取和聚合到较少数量的组件中**，从而更轻松地处理记录中的数据。

例如，假设我们要根据学生的分数计算他的总成绩。我们可以将*成绩*字段添加到*StudentRecord*并创建一个自定义构造函数来根据*标记*字段计算成绩。因此，我们可以轻松访问学生的成绩，而无需每次都手动计算：

```java
record StudentRecordV2(String name, int rollNo, int marks, char grade) {
    public StudentRecordV2(String name, int rollNo, int marks) {
        this(name, rollNo, marks, calculateGrade(marks));
    }

    private static char calculateGrade(int marks) {
        if (marks >= 90) {
            return 'A';
        } else if (marks >= 80) {
            return 'B';
        } else if (marks >= 70) {
            return 'C';
        } else if (marks >= 60) {
            return 'D';
        } else {
            return 'F';
        }
    }
}复制
```

此外，自定义构造函数可以**为其参数设置默认值（如果未提供）**。这在我们想要为某些字段提供默认值或自动生成值的情况下很有用，例如生成新的 ID 或 UUID：

```java
record StudentRecordV3(String name, int rollNo, int marks, String id) {
    
    public StudentRecordV3(String name, int rollNo, int marks) {
        this(name, rollNo, marks, UUID.randomUUID().toString());
    }
}复制
```

### 4.2. 限制

尽管自定义构造函数可以为 Java Records 带来许多好处，但它们也有一定的局限性。

与普通的 Java 构造函数不同，**记录的自定义构造函数不能用不同的参数集重载**。这意味着记录必须具有在所有构造函数中初始化的相同属性集。但是，通过使用默认值以及向自定义构造函数提供不属于记录标头的参数，记录仍然可以以不同的方式进行初始化。

例如，自定义构造函数可以采用不属于记录标题的其他参数，并使用它们来计算记录组件的值。同样，自定义构造函数可以使用默认值来初始化记录的部分或全部组件。

此外，**记录在创建后无法修改**。它们仅用于表示一旦创建就不应更改的数据。

## 5.结论

在本文中，我们介绍了 Java Records 中的自定义构造函数，以及它们的优点和局限性。

总而言之，Java 记录和自定义构造函数简化了代码并增强了可读性和可维护性。它们有很多好处，但它们的使用有一些限制，可能不适用于所有用例。