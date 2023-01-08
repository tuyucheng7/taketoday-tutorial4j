## 1. 概述

Lombok 库提供了一种无需编写任何样板代码即可实现[构建器模式的好方法： ](https://en.wikipedia.org/wiki/Builder_pattern#Java)[@Builder](https://projectlombok.org/features/Builder)注解。

在这个简短的教程中，我们将专门学习如何在涉及继承时处理@Builder注解。 我们将演示两种技术。一个依赖于标准的 Lombok 特性。另一个使用 Lombok 1.18 中引入的实验性功能。

有关 Builder 注解的更广泛概述，请参阅[使用 Lombok 的@Builder注解](https://www.baeldung.com/lombok-builder)。

[Project Lombok简介](https://www.baeldung.com/intro-to-project-lombok)中也提供了对 Project Lombok 库的详细了解。

## 2. Lombok @Builder和继承

### 2.1. 定义问题

假设我们的Child类扩展了一个Parent类：

```java
@Getter
@AllArgsConstructor
public class Parent {
    private final String parentName;
    private final int parentAge;
}

@Getter
@Builder
public class Child extends Parent {
    private final String childName;
    private final int childAge;
}

```

当在一个扩展了另一个类的类上使用@Builder时，我们将在注解上得到以下编译错误：

>   隐式超级构造函数 Parent() 未定义。必须显式调用另一个构造函数

这是因为 Lombok 不考虑超类的字段，而只考虑当前类的字段。

### 2.2. 解决问题

对我们来说幸运的是，有一个简单的解决方法。我们可以生成(使用我们的 IDE 甚至手动)基于字段的构造函数。这也包括来自超类的字段。

我们用@Builder来注解它，而不是类：

```java
@Getter
@AllArgsConstructor
public class Parent {
    private final String parentName;
    private final int parentAge;
}

@Getter
public class Child extends Parent {
    private final String childName;
    private final int childAge;

    @Builder
    public Child(String parentName, int parentAge, String childName, int childAge) {
        super(parentName, parentAge);
        this.childName = childName;
        this.childAge = childAge;
    }
}

```

这样，我们将能够从Child类访问一个方便的构建器，这将允许我们还指定Parent类字段：

```java
Child child = Child.builder()
  .parentName("Andrea")
  .parentAge(38)
  .childName("Emma")
  .childAge(6)
  .build();

assertThat(child.getParentName()).isEqualTo("Andrea");
assertThat(child.getParentAge()).isEqualTo(38);
assertThat(child.getChildName()).isEqualTo("Emma");
assertThat(child.getChildAge()).isEqualTo(6);
```

### 2.3. 使多个@Builder共存

如果超类本身用@Builder注解，我们在注解Child类构造函数时会收到以下错误：

>   返回类型与 Parent.builder() 不兼容

这是因为Child类试图公开两个具有相同名称的Builder 。

我们可以通过为至少一个构建器方法分配一个唯一的名称来解决这个问题：

```java
@Getter
public class Child extends Parent {
    private final String childName;
    private final int childAge;
    
    @Builder(builderMethodName = "childBuilder")
    public Child(String parentName, int parentAge, String childName, int childAge) {
        super(parentName, parentAge);
        this.childName = childName;
        this.childAge = childAge;
    }
}

```

然后，我们将能够通过 Child.builder() 获得一个ParentBuilder ，通过Child.childBuilder()获得一个ChildBuilder。

### 2.4. 支持更大的继承层次结构

在某些情况下，我们可能需要支持更深的继承层次结构。我们可以使用与之前相同的模式。

让我们创建一个Child的子类：

```java
@Getter
public class Student extends Child {

    private final String schoolName;

    @Builder(builderMethodName = "studentBuilder")
    public Student(String parentName, int parentAge, String childName, int childAge, String schoolName) {
        super(parentName, parentAge, childName, childAge);
        this.schoolName = schoolName;
    }
}
```

和以前一样，我们需要手动添加一个构造函数。这需要接受来自所有父类和子类的所有属性作为参数。然后我们像以前一样添加@Builder注解。

通过在注解中提供另一个唯一的方法名称，我们可以获得Parent、Child或Student的构建器：

```java
Student student = Student.studentBuilder()
  .parentName("Andrea")
  .parentAge(38)
  .childName("Emma")
  .childAge(6)
  .schoolName("Baeldung High School")
  .build();

assertThat(student.getChildName()).isEqualTo("Emma");
assertThat(student.getChildAge()).isEqualTo(6);
assertThat(student.getParentName()).isEqualTo("Andrea");
assertThat(student.getParentAge()).isEqualTo(38);
assertThat(student.getSchoolName()).isEqualTo("Baeldung High School");
```

然后我们可以扩展这个模式来处理任何深度的继承。我们需要创建的构造函数可能会变得非常大，但我们的 IDE 可以帮助我们。

## 3. Lombok @SuperBuilder和继承

正如我们之前提到的，Lombok 1.18 版本引入了 [@SuperBuilder](https://projectlombok.org/features/experimental/SuperBuilder)注解。我们可以使用它以更简单的方式解决我们的问题。

### 3.1. 应用注解

我们可以创建一个可以查看其祖先属性的构建器。

为此，我们使用@SuperBuilder注解来注解我们的类及其祖先。

让我们在这里演示我们的三层层次结构。

请注意，简单父子继承的原理是相同的：

```java
@Getter
@SuperBuilder
public class Parent {
    // same as before...

@Getter
@SuperBuilder
public class Child extends Parent {
   // same as before...

@Getter
@SuperBuilder
public class Student extends Child {
   // same as before...
```

当所有的类都以这种方式注解时，我们得到了一个子类的构建器，它也公开了父类的属性。

请注意，我们必须注解所有类。@SuperBuilder不能与@Builder在同一类层次结构中混合使用。这样做会导致编译错误。

### 3.2. 使用生成器

这一次，我们不需要定义任何特殊的构造函数。

由@SuperBuilder生成的构建器类的行为就像我们使用主 Lombok @Builder生成的那样：

```java
Student student = Student.builder()
  .parentName("Andrea")
  .parentAge(38)
  .childName("Emma")
  .childAge(6)
  .schoolName("Baeldung High School")
  .build();

assertThat(student.getChildName()).isEqualTo("Emma");
assertThat(student.getChildAge()).isEqualTo(6);
assertThat(student.getParentName()).isEqualTo("Andrea");
assertThat(student.getParentAge()).isEqualTo(38);
assertThat(student.getSchoolName()).isEqualTo("Baeldung High School");
```

## 4. 总结

我们已经了解了如何处理在使用继承的类中使用@Builder注解的常见陷阱。

如果我们使用主要的 Lombok @Builder注解，我们还有一些额外的步骤来让它工作。但如果我们愿意使用实验性功能，@SuperBuilder可以简化事情。