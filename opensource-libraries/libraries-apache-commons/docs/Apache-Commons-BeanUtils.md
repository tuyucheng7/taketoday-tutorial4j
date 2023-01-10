## 1. 概述

Apache Commons BeansUtils 包含使用Javabean 所需的所有工具。

简单地说，bean 是一个简单的Java类，包含字段、getter/setter 和无参数构造函数。

Java 提供反射和内省功能来识别 getter-setter 方法并动态调用它们。然而，这些 API 可能难以学习，并且可能需要开发人员编写样板代码来执行最简单的操作。

## 2.Maven依赖

这是在使用之前需要将 Maven 依赖项包含在 POM 文件中：

```xml
<dependency>
    <groupId>commons-beanutils</groupId>
    <artifactId>commons-beanutils</artifactId>
    <version>1.9.4</version>
</dependency>
```

最新版本可以在[这里](https://search.maven.org/classic/#search|gav|1|g%3A"commons-beanutils" AND a%3A"commons-beanutils")找到。

## 3. 创建JavaBean

让我们使用典型的 getter 和 setter 方法创建两个 bean 类Course和Student 。

```java
public class Course {
    private String name;
    private List<String> codes;
    private Map<String, Student> enrolledStudent = new HashMap<>();

    //  standard getters/setters
}


public class Student {
    private String name;

    //  standard getters/setters
}
```

我们有一个Course类，其中包含课程名称、课程代码和多个注册学生。注册学生由唯一的注册 ID 标识。Course类在Map对象中维护注册学生，其中注册 ID 是键，学生对象将是值。

## 4. 财产准入

Bean 的属性可以分为三类。

### 4.1. 简单属性

单值属性也称为简单或标量。

它们的值可能是原始类型(如 int、float)或复杂类型对象。BeanUtils 有一个PropertyUtils类，它允许我们修改JavaBean 中的简单属性。

下面是设置属性的示例代码：

```java
Course course = new Course();
String name = "Computer Science";
List<String> codes = Arrays.asList("CS", "CS01");

PropertyUtils.setSimpleProperty(course, "name", name);
PropertyUtils.setSimpleProperty(course, "codes", codes);
```

### 4.2. 索引属性

索引属性有一个集合作为值，可以使用索引号单独访问。作为 JavaBean 的扩展，BeanUtils 也将java.util.List类型值视为索引。

我们可以使用PropertyUtils 的 setIndexedProperty方法修改索引属性的单个值。

这是修改索引属性的示例代码：

```java
PropertyUtils.setIndexedProperty(course, "codes[1]", "CS02");
```

### 4.3. 映射属性

任何具有java.util.Map作为基础类型的属性都称为映射属性。BeanUtils 允许我们使用字符串值键更新映射中的单个值。

以下是修改映射属性中的值的示例代码：

```java
Student student = new Student();
String studentName = "Joe";
student.setName(studentName);

PropertyUtils.setMappedProperty(course, "enrolledStudent(ST-1)", student);
```

## 5.嵌套属性访问

如果一个属性值是一个对象，而我们需要访问该对象内部的属性值——那就是访问一个嵌套的属性。PropertyUtils还允许我们访问和修改嵌套属性。

假设我们想通过Course对象访问Student类的 name 属性。我们可能会写：

```java
String name = course.getEnrolledStudent("ST-1").getName();
```

我们可以使用getNestedProperty访问嵌套属性值，并使用 PropertyUtils 中的setNestedProperty方法修改嵌套属性。这是代码：

```java
Student student = new Student();
String studentName = "Joe";
student.setName(studentName);

String nameValue 
  = (String) PropertyUtils.getNestedProperty(
  course, "enrolledStudent(ST-1).name");
```

## 6.  Bean 属性

将一个对象的属性到另一个对象对于开发人员来说通常是乏味且容易出错的。BeanUtils类提供了一个copyProperties方法，该方法将源对象的属性到目标对象，其中两个对象的属性名称相同。

让我们创建另一个 bean 类作为我们在上面创建的具有相同属性的Course，除了它没有enrolledStudent属性，属性名称将是students。让我们将该类命名为CourseEntity。该类看起来像：

```java
public class CourseEntity {
    private String name;
    private List<String> codes;
    private Map<String, Student> students = new HashMap<>();

    //  standard getters/setters
}
```

现在我们将Course对象的属性到CourseEntity对象：

```java
Course course = new Course();
course.setName("Computer Science");
course.setCodes(Arrays.asList("CS"));
course.setEnrolledStudent("ST-1", new Student());

CourseEntity courseEntity = new CourseEntity();
BeanUtils.copyProperties(courseEntity, course);
```

请记住，这将仅具有相同名称的属性。因此，它不会Course类中的enrolledStudent属性，因为CourseEntity类中没有同名的属性。

## 七. 总结

在这篇简短的文章中，我们回顾了BeanUtils提供的实用程序类。我们还研究了不同类型的属性以及我们如何访问和修改它们的值。

最后，我们研究了访问嵌套属性值以及将一个对象的属性到另一个对象。

当然，Java SDK 中的反射和内省功能也允许我们动态访问属性，但它可能很难学习并且需要一些样板代码。BeanUtils允许我们通过单个方法调用来访问和修改这些值。