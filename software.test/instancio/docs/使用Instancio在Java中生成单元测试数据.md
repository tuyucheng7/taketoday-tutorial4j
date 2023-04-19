## 1. 概述

在单元测试中设置数据通常是一个涉及许多样板代码的手动过程。在测试包含许多字段、关系和集合的复杂类时尤其如此。更重要的是，值本身往往并不重要。相反，我们通常需要的是一个值的存在。这通常用person.setName("test name")之类的代码表示。

在本教程中，我们将了解Instancio如何通过创建完全填充的对象来帮助生成单元测试数据。我们将介绍如何创建、自定义和复制对象，以及在测试失败时重现。

## 2. 关于Instancio

[Instancio](https://github.com/instancio/instancio/)是一个测试数据生成器，用于在单元测试中自动设置数据。**它的目标是通过尽可能消除手动数据设置来使单元测试更加简洁和可维护**。简而言之，我们为Instancio提供一个类，它为我们提供了一个完全填充的对象，其中填充了可重现的、随机生成的数据。

## 3. Maven依赖

首先，让我们添加来自[Maven Central](https://central.sonatype.com/artifact/org.instancio/instancio-junit/2.14.0)的依赖项。由于我们将在示例中使用[JUnit 5](https://www.baeldung.com/junit-5)，因此我们将导入instancio-junit：

```xml
<dependency>
    <groupId>org.instancio</groupId>
    <artifactId>instancio-junit</artifactId>
    <version>2.9.0</version>
    <scope>test</scope>
</dependency>
```

或者，[instancio-core](https://central.sonatype.com/artifact/org.instancio/instancio-core/2.14.0)依赖项可用于单独使用Instancio、JUnit 4或其他测试框架：

```xml
<dependency>
    <groupId>org.instancio</groupId>
    <artifactId>instancio-core</artifactId>
    <version>2.6.0</version>
    <scope>test</scope>
</dependency>
```

## 4. 生成对象

使用Instancio，我们可以[创建不同类型的对象](https://www.instancio.org/user-guide/#creating-objects)，包括：

-   简单值，例如字符串、日期、数字
-   常规POJO，包括Java记录
-   集合、Map和流
-   使用类型标记的任意泛型类型

Instancio在填充对象时使用合理的默认值。生成的对象有：

-   非空值
-   非空字符串
-   正数
-   包含一些元素的非空集合

API的入口点是Instancio.create()和Instancio.of()方法。使用这些方法，我们可以创建POJO：

```java
Student student = Instancio.create(Student.class);
```

集合和流：

```java
List<Student> list = Instancio.ofList(Student.class).size(10).create();
Stream<Student> stream = Instancio.of(Student.class).stream().limit(10);
```

使用TypeToken的任意泛型类型：

```java
Pair<List<Foo>, List<Bar>> pairOfLists = Instancio.create(new TypeToken<Pair<List<Foo>, List<Bar>>>() {});
```

接下来，让我们看看如何自定义生成的数据。

## 5. 自定义对象

在编写单元测试时，我们经常需要创建各种状态的对象。状态通常取决于被测试的功能。例如，我们可能需要一个valid对象来验证快乐路径和一个invalid对象来验证校验错误。

使用Instancio，我们可以：

-   根据需要通过set()、supply()和generate()方法自定义生成的值
-   使用ignore()方法忽略某些字段和类
-   允许使用withNullable()方法生成空值
-   使用subtype()方法指定抽象类型的实现

### 5.1 选择器

Instancio使用选择器来指定要自定义的字段和类。上面列出的所有方法都接收一个选择器作为第一个参数。我们可以使用Select类提供的静态方法创建选择器。

例如，我们可以通过以下方法使用方法引用、字段名称或谓词来选择特定字段：

```java
Select.field(Address::getCity)
Select.field(Address.class, "city")
Select.fields().matching("c.*y").declaredIn(Address.class) // matches city, country
Select.fields(field -> field.getDeclaredAnnotations().length > 0)
```

我们还可以通过指定类或使用谓词来选择类型：

```java
Select.all(Collection.class)
Select.types().of(Collection.class)
Select.types(klass -> klass.getPackage().getName().startsWith("com.example"))
```

第一种方法all()基于严格的类相等性。换句话说，它将匹配Collection声明但不匹配List或Set。另一方面，第二种方法将匹配Collection及其子类型。

让我们看一些实际的例子。我们将使用选择器来自定义对象。为方便起见，我们假设静态导入Select.*。

### 5.2 使用set()

set()方法仅用于设置非随机(预期)值：

```java
Student student = Instancio.of(Student.class)
    .set(field(Phone::getCountryCode), "+49")
    .create();
```

一个常见的问题是为什么在创建对象后不使用常规setter，例如phone.setCountryCode("49")？与常规setter不同，set()方法(如上所示)将在所有生成的Phone实例上设置国家/地区代码。由于我们的Student包含一个List<Phone\>字段，因此使用常规setter需要我们遍历列表。

另一个原因是有时我们可能会使用不可变类，例如[Java记录](https://www.baeldung.com/java-record-keyword)。在这种情况下，无法在创建对象后对其进行修改。

### 5.3 使用supply()

supply()方法有两种变体：一种用于使用Supplier分配非随机值，另一种用于使用Generator生成随机值。

以下代码段说明了这两种变体：

```java
Student student = Instancio.of(Student.class)
    .supply(all(LocalDateTime.class), () -> LocalDateTime.now())
    .supply(field(Student::getDateOfBirth), random -> LocalDate.now().minusYears(18 + random.intRange(0, 60)))
    .create();
```

在此示例中，dateOfBirth由[Generator](https://www.instancio.org/user-guide/#custom-generators) lambda提供。Generator是一个函数接口，为我们提供了Random的种子实例。使用此实例可确保对象可以完整重现。

### 5.4 使用generate()

使用generate()方法，我们可以通过[内置数据生成器](https://www.instancio.org/user-guide/#using-generate)自定义值。Instancio为最常用的Java类型提供生成器。这包括字符串、数字类型、集合、数组、日期等。

在以下示例中，gen变量提供对可用生成器的访问。每个都提供了一个流式的API来自定义其值：

```java
Student student = Instancio.of(Student.class)
    .generate(field(Student::getEnrollmentYear), gen -> gen.temporal().year().past())
    .generate(field(ContactInfo::getEmail), gen -> gen.text().pattern("#a#a#a#a#a#a@example.com"))
    .create();
```

### 5.5 使用ignore()

如果我们不想填充某些字段或类，我们可以使用ignore()方法。假设我们要测试将Student的实例持久化到数据库中。在这种情况下，我们想要生成一个id为空的对象。

我们可以按如下方式完成此操作：

```java
Student student = Instancio.of(Student.class)
    .ignore(field(Student::getId))
    .create();
```

### 5.6 使用withNullable()

虽然Instancio通常生成完全填充的对象，但有时这是不可取的。例如，当某些可选字段为null时，我们可能希望验证我们的代码是否正常工作。我们可以使用withNullable()方法来完成此操作。顾名思义，Instancio随机生成实际值或空值。

```java
Student student = Instancio.of(Student.class)
    .withNullable(field(Student::getEmergencyContact))
    .withNullable(field(ContactInfo::getEmail))
    .create();
```

使用使用静态数据的传统方法，我们需要为空值和非空值创建单独的测试方法。或者，我们可以使用参数化测试。这可能会非常耗时，尤其是在有许多可选字段的情况下。如上所示，生成对象允许我们使用单一测试方法来验证输入的不同排列。

### 5.7 使用subtype()

subtype()方法允许我们为抽象类型指定实现或为具体类型指定子类。让我们看一下下面的示例，其中ContactInfo类声明了一个List<Phone\>字段：

```java
Student student = Instancio.of(Student.class)
    .subtype(field(ContactInfo::getPhones), LinkedList.class)
    .create();
```

如果不明确指定List类型，Instancio将使用ArrayList作为默认的List实现。我们可以通过指定子类型来覆盖此行为。

## 6. 使用Model

Instancio Model是通过API表达的对象模板。从模型创建的对象将具有模型的所有属性。可以通过调用toModel()方法来创建模型，如以下示例所示：

```java
Model<Student> studentModel = Instancio.of(Student.class)
    .generate(field(Student::getDateOfBirth), gen -> gen.temporal().localDate().past())
    .generate(field(Student::getEnrollmentYear), gen -> gen.temporal().year().past())
    .generate(field(ContactInfo::getEmail), gen -> gen.text().pattern("#a#a#a#a#a#a@example.com"))
    .generate(field(Phone::getCountryCode), gen -> gen.string().prefix("+").digits().maxLength(2))
    .toModel();
```

定义好模型后，我们现在可以在所有测试方法中使用它。每种测试方法都可以利用模板作为基础，并根据需要应用自定义。

假设我们正在测试一种方法，该方法要求学生已修读十门课程并且在所有课程中都获得A或B的成绩。我们可以使用上面定义的模型并自定义课程和成绩的数量：

```java
@Test
void whenGivenGoodGrades_thenCreatedStudentShouldHaveExpectedGrades() {
    final int numOfCourses = 10;
    Student student = Instancio.of(studentModel)
        .generate(all(Grade.class), gen -> gen.oneOf(Grade.A, Grade.B))
        .generate(field(Student::getCourseGrades), gen -> gen.map().size(numOfCourses))
        .create();

    Map<Course, Grade> courseGrades = student.getCourseGrades();

    assertThat(courseGrades.values()).hasSize(numOfCourses)
        .containsAnyOf(Grade.A, Grade.B)
        .doesNotContain(Grade.C, Grade.D, Grade.F);
    
    // Remaining data is defined by the model:
    assertThat(student.getEnrollmentYear()).isLessThan(Year.now());
    assertThat(student.getContactInfo().getEmail()).matches("^[a-zA-Z0-9]+@example.com$");
    // ...
```

我们可能还会有另一个测试，要求学生的课程不及格。为此，我们需要填充Student的Map<Course, Grade\>字段以包含成绩为F的课程。再一次，我们使用我们的学生模型作为基础，并覆盖我们感兴趣的属性：

```java
@InstancioSource
@ParameterizedTest
void whenGivenFailingGrade_thenStudentShouldHaveAFailedCourse(Course failedCourse) {
    Student student = Instancio.of(studentModel)
        .generate(field(Student::getCourseGrades), gen -> gen.map().with(failedCourse, Grade.F))
        .create();

    Map<Course, Grade> courseGrades = student.getCourseGrades();
    assertThat(courseGrades).containsEntry(failedCourse, Grade.F);
}
```

在此示例中，我们使用Map生成器的with(key, value)方法将预期的条目添加到生成的Map中。

请注意，此测试方法是@ParameterizedTest。当@InstancioSource与参数化测试一起使用时，Instancio会自动提供指定为方法参数的填充对象。我们可以根据需要指定任意数量的参数。

接下来，让我们看一下如何使用JUnit 5的Instancio扩展。

## 7. 使用Instancio JUnit 5扩展

使用随机数据的一个常见问题是测试可能会因生成的特定数据集而失败。失败可能是由于设置代码中的错误或生产代码中的错误。**无论根本原因如何，Instancio都会生成完全可重现的数据，并且使用InstancioExtension可以轻松重现失败的测试**。

### 7.1 重现测试失败

为了通过一个例子来说明这一点，我们将让我们的学生注册一门新课程。但是，如果学生至少有一门课程的成绩为F，我们的EnrollmentService将抛出异常。因此，以下测试可能通过或失败，具体取决于生成的成绩：

```java
@ExtendWith(InstancioExtension.class)
class ReproducingFailedTest {

    EnrollmentService enrollmentService = new EnrollmentService();

    @Test
    void whenGivenNoFailingGrades_thenShouldEnrollStudentInCourse() {
        Course course = Instancio.create(Course.class);
        Student student = Instancio.create(Student.class);

        boolean isEnrolled = enrollmentService.enrollStudent(student, course);

        assertThat(isEnrolled).isTrue();
    }
}
```

如果测试碰巧失败，它将生成一条错误消息，报告测试方法的名称和种子值，例如：

>   timestamp = 2023-01-24T13:50:12.436704221, Instancio = Test method ‘enrollStudent' failed with seed: 1234

使用报告的种子值，我们可以通过在测试方法上放置@Seed(1234)注解来重现失败。这样做会导致再次生成先前生成的数据，从而产生相同的结果：

```java
@Seed(1234)
@Test
void whenGivenNoFailingGrades_thenShouldEnrollStudentInCourse() {
    // test code unchanged
}
```

在此示例中，失败是由数据设置中的错误引起的。因此，我们可以简单地排除生成F成绩以修复我们的测试：

```java
Student student = Instancio.of(Student.class)
    .generate(all(Grade.class), gen -> gen.enumOf(Grade.class).excluding(Grade.F))
    .create();
```

**EnrollmentService现在针对所有有效成绩进行成功测试，我们使用单一测试方法实现了这一点**。

我们可以应用完全相同的工作流程来处理由生产代码导致的测试失败。

### 7.2 注入自定义设置

该扩展提供的另一个功能是使用@WithSettings注解进行Settings注入。例如，默认情况下，Instancio不会生成空集合。但是，我们可能有需要空集合的测试场景。我们可以使用自定义设置来覆盖默认行为，如下所示：

```java
@ExtendWith(InstancioExtension.class)
class CustomSettingsTest {

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.COLLECTION_MIN_SIZE, 0)
            .set(Keys.COLLECTION_MAX_SIZE, 3)
            .lock();

    @Test
    void whenGivenInjectedSettings_shouldUseCustomSettings() {
        ContactInfo info = Instancio.create(ContactInfo.class);

        List<Phone> phones = info.getPhones();
        assertThat(phones).hasSizeBetween(0, 3);
    }
}
```

注入的设置将应用于此测试类中创建的所有对象。虽然不是必需的，但我们也调用lock()方法使Settings实例不可变。这可确保测试方法不会无意中修改共享设置。

## 8. 总结

在本文中，我们探讨了通过使用Instancio自动生成数据来消除测试中的手动数据设置。我们还看到了如何在没有样板代码的情况下使用模型为单个测试方法创建自定义对象。最后，我们了解了JUnit 5的InstancioExtension如何帮助重现失败的测试。

有关详细信息，请查看[Instancio用户指南](https://www.instancio.org/user-guide/)和GitHub上的[项目主页](https://github.com/instancio/instancio/)。