## 1. 概述

在本文中，我们将深入了解**java.util.concurrent.atomic包中AtomicMarkableReference类的细节**。

接下来，我们会演示该类的API方法，并了解如何在实践中使用AtomicMarkableReference类。

## 2. 目的

AtomicMarkableReference是一个泛型类，它封装了对Object的引用和布尔标志。**这两个字段耦合在一起，可以一起或单独地进行**[原子更新](https://www.baeldung.com/java-atomic-variables)。

AtomicMarkableReference也是**解决**[ABA问题](https://www.baeldung.com/cs/aba-concurrency)**的一种可能的方法**。

## 3. 实现

让我们更深入地了解一下AtomicMarkableReference类的实现：

```java
public class AtomicMarkableReference<V> {

    private static class Pair<T> {
        final T reference;
        final boolean mark;

        private Pair(T reference, boolean mark) {
            this.reference = reference;
            this.mark = mark;
        }

        static <T> Pair<T> of(T reference, boolean mark) {
            return new Pair<T>(reference, mark);
        }
    }

    private volatile Pair<V> pair;
    // ...
}
```

请注意，**AtomicMarkableReference有一个静态内部类Pair，其中包含reference和mark字段**。

此外，我们可以看到**这两个变量都使用final修饰**。因此，**每当我们想要修改这些变量时，就会创建Pair类的一个新实例，并替换旧实例**。

## 4. 方法

首先，要发现AtomicMarkableReference的有用性，让我们从创建一个员工POJO开始：

```java
class Employee {
	private int id;
	private String name;

	// constructor & getters & setters
}
```

现在，我们可以创建AtomicMarkableReference类的一个实例：

```java
AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee,true);
```

对于我们的示例，假设我们的AtomicMarkableReference实例代表组织结构图中的一个节点。它包含两个变量：对Employee类实例的引用和一个标记，该标记指示员工是否在职或者已离开公司。

AtomicMarkableReference提供了多种方法来更新或检索一个或两个字段，让我们一一看一下这些方法。

### 4.1 getReference()

我们使用getReference()方法返回引用变量的当前值：

```java
@Test
void whenUsingGetReferenceMethod_thenCurrentReferenceShouldBeReturned() {
    Employee employee = new Employee(123, "Mike");
    AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, true);

    assertEquals(employee, employeeNode.getReference());
}
```

### 4.2 isMarked()

要获取mark变量的值，我们应该调用isMarked()方法：

```java
@Test
void givenMarkValueAsTrue_whenUsingIsMarkedMethod_thenMarkValueShouldBeTrue() {
    Employee employee = new Employee(123, "Mike");
    AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, true);

    assertTrue(employeeNode.isMarked());
}
```

### 4.3 get()

接下来，当我们想要检索当前引用和当前标记时，我们使用get()方法。**为了获得标记，我们应该将一个大小至少为1的布尔数组作为参数传递，该数组将在索引0处存储布尔变量的当前值**。同时，该方法将返回引用的当前值：

```java
@Test
void whenUsingGetMethod_thenCurrentReferenceAndMarkShouldBeReturned() {
    Employee employee = new Employee(123, "Mike");
    AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, true);

    boolean[] markHolder = new boolean[1];
    Employee currentEmployee = employeeNode.get(markHolder);

    assertEquals(employee, currentEmployee);
    assertTrue(markHolder[0]);
}
```

这种获取引用和标记字段的方式有点奇怪，因为内部Pair类不向调用方公开。

Java的公共API中没有通用的Pair<T, U\>类，造成这种情况的主要原因是我们可能会过度使用它，而不是创建不同的类型。

### 4.4 set()

如果我们想要无条件地更新引用和标记字段，我们应该使用set()方法。如果作为参数传递的值中至少有一个不同，则引用和标记值将被更新：

```java
@Test
void givenNewReferenceAndMark_whenUsingSetMethod_thenCurrentReferenceAndMarkShouldBeUpdated() {
    Employee employee = new Employee(123, "Mike");
    AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, true);

    Employee newEmployee = new Employee(124, "John");
    employeeNode.set(newEmployee, false);

    assertEquals(newEmployee, employeeNode.getReference());
    assertFalse(employeeNode.isMarked());
}
```

### 4.5 compareAndSet()

接下来，**如果当前引用等于预期引用，并且当前标记等于预期标记，则compareAndSet()方法将引用和标记更新为给定的更新值**。

让我们看看如何使用compareAndSet()更新引用字段和标记字段：

```java
@Test
void givenCurrentReferenceAndCurrentMark_whenUsingCompareAndSet_thenReferenceAndMarkShouldBeUpdated() {
    Employee employee = new Employee(123, "Mike");
    AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, true);
    Employee newEmployee = new Employee(124, "John");

    assertTrue(employeeNode.compareAndSet(employee, newEmployee, true, false));
    assertEquals(newEmployee, employeeNode.getReference());
    assertFalse(employeeNode.isMarked());
}
```

此外，在调用compareAndSet()方法时，如果字段被更新，则返回true；如果更新失败，则为false。

### 4.6 weakCompareAndSet()

**weakCompareAndSet()方法应该是compareAndSet()方法的较弱版本**，也就是说，它不像compareAndSet()那样提供强大的内存排序保证。此外，它可能会错误地无法在硬件级别获得独占访问。

这是weakCompareAndSet()方法的规范。然而，**目前，weakCompareAndSet()只是**[在底层调用compareAndSet()方法](https://github.com/openjdk/jdk/blob/927a7287b70e8526435ff018d8f0504ebe9bbf94/src/java.base/share/classes/java/util/concurrent/atomic/AtomicMarkableReference.java#L126)。因此，它们具有相同的强大实现。

即使这两种方法现在具有相同的实现，我们也应该根据它们的规范来使用它们。因此，**我们应该把weakCompareAndSet()视为弱原子**。

在某些平台和某些情况下，弱原子的成本可能更低。例如，如果我们要在循环中执行compareAndSet，那么使用较弱的版本可能是一个更好的主意。在这种情况下，我们最终会在循环中更新状态，这样虚假故障就不会影响程序的正确性。

底线是，弱原子在某些特定的用例中是有用的，因此并不适用于所有可能的场景。如果有任何疑问，请选择更强的compareAndSet()。

### 4.7 attemptMark()

最后，对于attemptMark()方法，它检查当前引用是否等于作为参数传递的预期引用。如果它们匹配，它会自动将标记值原子设置为给定的更新值：

```java
@Test
void givenTheSameObjectReference_whenUsingAttemptMarkMethod_thenMarkShouldBeUpdated() {
    Employee employee = new Employee(123, "Mike");
    AtomicMarkableReference<Employee> employeeNode = new AtomicMarkableReference<>(employee, true);

    assertTrue(employeeNode.attemptMark(employee, false));
    assertFalse(employeeNode.isMarked());
}
```

**值得注意的是，即使预期引用值和当前引用值相等，此方法也可能会错误地失败。因此，我们应该注意方法执行返回的布尔值**。

如果标记更新成功，则结果为true，否则为false。但是，当当前引用等于预期引用时，重复调用将修改标记值。因此，**建议在while循环结构中使用此方法**。

此故障可能是由attemptMark()方法用于更新字段的compare-and-swap(CAS)算法造成的，如果我们有多个线程试图使用CAS更新同一个值，其中一个线程会设法更改该值，而另一个线程会收到更新失败的通知。

## 5. 总结

在本快速指南中，我们学习了AtomicMarkableReference类是如何实现的。此外，我们还说明了如何通过类的公共API方法来原子地更新其属性。