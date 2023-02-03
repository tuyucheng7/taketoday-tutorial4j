## 1. 概述

[反射](https://www.baeldung.com/java-reflection)是计算机软件在运行时检查其结构的能力。在Java中，我们通过使用Java Reflection API来实现这一点。它允许我们在运行时检查类的元素，例如字段、方法甚至内部类。

本教程将重点介绍如何检索Java类的字段，包括私有字段和继承字段。

## 2. 从类中检索字段

让我们首先看一下如何检索类的字段，而不考虑它们的可见性。稍后，我们还将了解如何获取继承的字段。

让我们从一个具有两个字符串字段的Person类的示例开始：lastName和firstName。前者是受保护的(稍后会有用)，而后者是私有的：

```java
public class Person {
    protected String lastName;
    private String firstName;
}
```

我们想使用反射获取lastName和firstName字段。**我们将通过使用Class::getDeclaredFields方法来实现这一点。顾名思义，这将以Field数组的形式返回类的所有声明字段**：

```java
public class PersonAndEmployeeReflectionUnitTest {

    /* ... constants ... */

    @Test
    public void givenPersonClass_whenGetDeclaredFields_thenTwoFields() {
        Field[] allFields = Person.class.getDeclaredFields();

        assertEquals(2, allFields.length);

        assertTrue(Arrays.stream(allFields).anyMatch(field ->
              field.getName().equals(LAST_NAME_FIELD) && field.getType().equals(String.class))
        );
        assertTrue(Arrays.stream(allFields).anyMatch(field ->
              field.getName().equals(FIRST_NAME_FIELD) && field.getType().equals(String.class))
        );
    }
}
```

正如我们所看到的，我们得到了Person类的两个字段。我们检查它们的名称和类型是否与Person类中的字段定义相匹配。

## 3. 检索继承字段

现在让我们看看如何获取Java类的继承字段。

为了说明这一点，让我们创建一个名为Employee的第二个类，它扩展了Person，其中包含一个自己的字段：

```java
public class Employee extends Person {
    public int employeeId;
}
```

### 3.1 检索简单类层次结构上的继承字段

**使用Employee.class.getDeclaredFields()只会返回employeeId字段**，因为此方法不会返回在超类中声明的字段。为了获得继承的字段，我们还必须获得Person超类的字段。

当然，我们可以对Person和Employee类都使用getDeclaredFields()方法，并将它们的结果合并到一个数组中。但是，如果我们不想显式指定超类怎么办？

在这种情况下，**我们可以使用Java Reflection API的另一种方法：Class::getSuperclass**。这给了我们另一个类的超类，而我们不需要知道那个超类是什么。

让我们收集Employee.class和Employee.class.getSuperclass()上的getDeclaredFields()的结果，并将它们合并到一个数组中：

```java
@Test
public void givenEmployeeClass_whenGetDeclaredFieldsOnBothClasses_thenThreeFields() {
    Field[] personFields = Employee.class.getSuperclass().getDeclaredFields();
    Field[] employeeFields = Employee.class.getDeclaredFields();
    Field[] allFields = new Field[employeeFields.length + personFields.length];
    Arrays.setAll(allFields, i -> 
        (i < personFields.length ? personFields[i] : employeeFields[i - personFields.length]));

    assertEquals(3, allFields.length);

    Field lastNameField = allFields[0];
    assertEquals(LAST_NAME_FIELD, lastNameField.getName());
    assertEquals(String.class, lastNameField.getType());

    Field firstNameField = allFields[1];
    assertEquals(FIRST_NAME_FIELD, firstNameField.getName());
    assertEquals(String.class, firstNameField.getType());

    Field employeeIdField = allFields[2];
    assertEquals(EMPLOYEE_ID_FIELD, employeeIdField.getName());
    assertEquals(int.class, employeeIdField.getType());
}
```

我们可以在这里看到，我们已经收集了Person的两个字段以及Employee的单个字段。

但是，Person的私有字段真的是继承字段吗？没那么多。这对于包私有字段来说是一样的。**只有公共和受保护的字段被认为是继承的**。

### 3.2 过滤public和protected字段

不幸的是，Java API中没有任何方法允许我们从类及其超类中收集公共和受保护的字段。Class::getFields方法接近我们的目标，因为它返回类及其超类的所有公共字段，但不返回受保护的字段。

我们必须只获取继承字段的唯一方法是使用getDeclaredFields()方法，就像我们刚才所做的那样，并使用Field::getModifiers方法过滤其结果。**这个返回一个int表示当前字段的修饰符。每个可能的修饰符都被分配了2^0和2^7之间的2的幂**。

例如，public是2^0，static是2^3。因此，在公共和静态字段上调用getModifiers()方法将返回9。

然后，可以在该值和特定修饰符的值之间执行按位和运算，以查看该字段是否具有该修饰符。如果操作返回0以外的值，则应用修饰符，否则没有应用。

我们很幸运，因为Java为我们提供了一个工具类来检查getModifiers()返回的值中是否存在修饰符。**在我们的示例中，让我们使用isPublic()和isProtected()方法仅收集继承的字段**：

```java
List<Field> personFields = Arrays.stream(Employee.class.getSuperclass().getDeclaredFields())
    .filter(f -> Modifier.isPublic(f.getModifiers()) || Modifier.isProtected(f.getModifiers()))
    .collect(Collectors.toList());

assertEquals(1, personFields.size());

assertTrue(personFields.stream().anyMatch(field ->
    field.getName().equals(LAST_NAME_FIELD)
        && field.getType().equals(String.class))
);
```

如我们所见，结果不再带有私有字段。

### 3.3 检索深层类层次结构上的继承字段

在上面的示例中，我们处理了单个类层次结构。如果我们有更深层次的类层次并且想要收集所有继承的字段，我们现在该怎么办？

假设我们有一个Employee的子类或Person的超类-那么获取整个层次结构的字段将需要检查所有超类。

我们可以通过创建一个贯穿层次结构的工具方法来实现这一点，为我们构建完整的结果：

```java
List<Field> getAllFields(Class clazz) {
    if (clazz == null) {
        return Collections.emptyList();
    }

    List<Field> result = new ArrayList<>(getAllFields(clazz.getSuperclass()));
    List<Field> filteredFields = Arrays.stream(clazz.getDeclaredFields())
        .filter(f -> Modifier.isPublic(f.getModifiers()) || Modifier.isProtected(f.getModifiers()))
        .collect(Collectors.toList());
    result.addAll(filteredFields);
    return result;
}
```

**此递归方法将通过类层次结构搜索公共和受保护字段，并返回在List中找到的所有字段**。

让我们用一个新的MonthEmployee类(扩展Employee类)上的一个小测试来说明它：

```java
public class MonthEmployee extends Employee {
    protected double reward;
}
```

这个类定义了一个新的字段reward。**给定所有层次结构类，我们的方法应该为我们提供以下字段定义**： Person::lastName、Employee::employeeId和MonthEmployee::reward。

让我们在MonthEmployee上调用getAllFields()方法：

```java
@Test
public void givenMonthEmployeeClass_whenGetAllFields_thenThreeFields() {
    List<Field> allFields = getAllFields(MonthEmployee.class);

    assertEquals(3, allFields.size());

    assertTrue(allFields.stream().anyMatch(field ->
        field.getName().equals(LAST_NAME_FIELD)
            && field.getType().equals(String.class))
    );
    assertTrue(allFields.stream().anyMatch(field ->
        field.getName().equals(EMPLOYEE_ID_FIELD)
            && field.getType().equals(int.class))
    );
    assertTrue(allFields.stream().anyMatch(field ->
        field.getName().equals(MONTH_EMPLOYEE_REWARD_FIELD)
            && field.getType().equals(double.class))
    );
}
```

正如预期的那样，我们收集了所有公共和受保护的字段。

## 4. 总结

在本文中，我们了解了如何使用Java反射API检索Java类的字段。

我们首先学习了如何检索类的声明字段。之后，我们还看到了如何检索它的超类字段。然后，我们学会了过滤掉非公共和非保护字段。

最后，我们看到了如何应用所有这些来收集多类层次结构的继承字段。