## 1. 概述

在本快速教程中，我们将学习如何使用[Java 8 Streams](https://www.baeldung.com/java-8-streams-introduction)根据另一个列表中的值从一个列表中查找项目。

## 2. 使用Java8 流

让我们从两个实体类开始 ——Employee 和 Department：

```java
class Employee {
    Integer employeeId;
    String employeeName;

    // getters and setters
}

class Department {
    Integer employeeId;
    String department;

    // getters and setters
}

```

这里的想法是根据Department对象列表过滤Employee 对象列表 。更具体地说，我们希望从列表中找到所有员工 ：

-   将“销售”作为他们的部门，并且
-   在Department的列表中有相应的employeeId

为了实现这一点，我们实际上会在另一个中过滤一个：

```java
@Test
public void givenDepartmentList_thenEmployeeListIsFilteredCorrectly() {
    Integer expectedId = 1002;

    populate(emplList, deptList);

    List<Employee> filteredList = emplList.stream()
      .filter(empl -> deptList.stream()
        .anyMatch(dept -> 
          dept.getDepartment().equals("sales") && 
          empl.getEmployeeId().equals(dept.getEmployeeId())))
        .collect(Collectors.toList());

    assertEquals(1, filteredList.size());
    assertEquals(expectedId, filteredList.get(0)
      .getEmployeeId());
}
```

填充完这两个列表后，我们只需将一个 Stream of Employee 对象传递给 Stream of Department 对象。

接下来，要根据我们的两个条件过滤记录，我们使用 anyMatch 谓词， 我们在其中组合了所有给定的条件。

最后，我们 将结果收集到 filteredList中。

## 3.总结

在本文中，我们学习了如何：

-   使用Collection#s tream 将一个列表的值流式传输到另一个列表中，并且
-   使用anyMatch() 谓词组合多个过滤条件