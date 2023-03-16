## 1. 概述

在本教程中，我们将介绍如何使用[EasyRandom](https://github.com/j-easy/easy-random)库生成Java对象。

## 2. EasyRandom

在某些情况下，我们需要一组用于测试目的的模型对象。或者，我们想用一些我们将要使用的数据来填充我们的测试数据库。然后，也许我们想要收集一些虚拟DTO来返回给我们的客户端。

如果它们不复杂，设置一个、两个或几个这样的对象可能很容易。然而，在某些情况下，我们可能会需要数百个，这就变得很难以管理。

这就是[EasyRandom](https://github.com/j-easy/easy-random)的作用。**EasyRandom是一个易于使用的库，几乎不需要任何设置，只需绕过类类型，它将为我们实例化整个对象图**。

## 3. Maven依赖

首先，让我们将[easy-random-core](https://central.sonatype.com/artifact/org.jeasy/easy-random-core/5.0.0) Maven依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.jeasy</groupId>
    <artifactId>easy-random-core</artifactId>
    <version>4.0.0</version>
</dependency>
```

## 4. 对象生成

该库中最重要的两个类是：

-   用于生成对象的EasyRandom
-   以及允许我们配置生成过程并使其更可预测的EasyRandomParameters

### 4.1 单个对象

我们的第一个示例生成一个简单的随机Person对象，该对象没有嵌套对象属性、集合属性，只有一个Integer和两个Strings属性。

让我们**使用nextObject(Class<T\> t)生成Person对象的一个实例**：

```java
@Test
void givenDefaultConfiguration_thenGenerateSingleObject() {
    EasyRandom generator = new EasyRandom();
    Person person = generator.nextObject(Person.class);

    assertNotNull(person.getAge());
    assertNotNull(person.getFirstName());
    assertNotNull(person.getLastName());
}
```

下面是生成的对象的输出：

```shell
Person[firstName='eOMtThyhVNLWUZNRcBaQKxI', lastName='yedUsFwdkelQbxeTeQOvaScfqIOOmaa', age=-1188957731]
```

正如我们所看到的，生成的字符串可能有点太长，而且年龄是负数。我们将在后续部分中展示如何调整这一点。

### 4.2 对象集合

现在，假设我们需要一个Person对象的集合。另一种方法objects(Class<T\> t, int size)允许我们实现这一点。

有一个好处是，它返回对象流，因此最终，**我们可以根据需要向其添加中间操作或分组**。

以下是一个生成五个Person实例的例子：

```java
@Test
void givenDefaultConfiguration_thenGenerateObjectsList() {
    EasyRandom generator = new EasyRandom();
    List<Person> persons = generator.objects(Person.class, 5)
        .toList();
    
    assertEquals(5, persons.size());
}
```

### 4.3 复杂对象生成

让我们来看看我们的Employee类：

```java
public class Employee {
    private long id;
    private String firstName;
    private String lastName;
    private Department department;
    private Collection<Employee> coworkers;
    private Map<YearQuarter, Grade> quarterGrades;
}
```

这个类相对复杂，它有一个嵌套对象、一个集合和一个Map。

现在，默认情况下，**集合生成范围为1到100**，因此我们的Collection<Employee\>大小将介于两者之间。

一个好处是，**对象将被缓存和重用**，因此不一定都是唯一的。不过，我们可能不需要这么多。

我们很快就会了解如何调整集合的范围，但首先，让我们看看我们可能会遇到的另一个问题。

在我们的域类中，我们有一个YearQuarter，它代表一年的四分之一。

将endDate设置为正好指向startDate后3个月有一些逻辑：

```java
public class YearQuarter {

    private LocalDate startDate;
    private LocalDate endDate;

    public YearQuarter(LocalDate startDate) {
        this.startDate = startDate;
        autoAdjustEndDate();
    }

    private void autoAdjustEndDate() {
        endDate = startDate.plusMonths(3L);
    }
}
```

我们必须注意，**EasyRandom使用反射来构建我们的对象**，因此通过库生成此对象将导致数据很可能对我们没有用，因为我们的3个月限制根本不会被保留。

让我们来看看如何解决这个问题。

### 4.4 生成配置

在下面的配置中，我们**通过EasyRandomParameters提供我们的自定义配置**。

首先，我们明确说明我们想要的字符串长度和集合大小。接下来，我们从生成中排除一些字段，假设我们有理由只包含空值。

在这里，我们使用了方便的FieldPredicates工具类来链接排除谓词。

之后，我们通过另一个方便的TypePredicates工具类从“not.existing.pkg” Java包中排除所有内容。

最后，正如承诺的那样，**我们通过应用我们的自定义YearQuarterRandomizer来解决YearQuarter类的startDate和endDate生成问题**：

```java
@Test
void givenCustomConfiguration_thenGenerateSingleEmployee() {
    EasyRandomParameters parameters = new EasyRandomParameters();
    parameters.stringLengthRange(3, 3);
    parameters.collectionSizeRange(5, 5);
    parameters.excludeField(FieldPredicates.named("lastName").and(FieldPredicates.inClass(Employee.class)));
    parameters.excludeType(TypePredicates.inPackage("not.existing.pkg"));
    parameters.randomize(YearQuarter.class, new YearQuarterRandomizer());

    EasyRandom generator = new EasyRandom(parameters);
    Employee employee = generator.nextObject(Employee.class);

    assertEquals(3, employee.getFirstName().length());
    assertEquals(5, employee.getCoworkers().size());
    assertEquals(5, employee.getQuarterGrades().size());
    assertNotNull(employee.getDepartment());

    assertNull(employee.getLastName());

    for (YearQuarter key : employee.getQuarterGrades().keySet()) {
        assertEquals(key.getStartDate(), key.getEndDate().minusMonths(3L));
    }
}
```

## 5. 总结

手动设置模型、DTO或实体对象可能很麻烦，并导致代码的可读性降低和重复。EasyRandom是一个很好的工具，可以节省时间并提供帮助。

正如我们所看到的，该库不会生成有意义的String对象，但是还有另一个名为[Java Faker](https://github.com/DiUS/java-faker)的工具，我们可以使用它为字段创建自定义随机化器以对其进行排序。

此外，为了更深入地了解该库并了解它的配置方式，我们可以访问它的[Github Wiki页面](https://github.com/j-easy/easy-random/wiki)。