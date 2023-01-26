## 1. 概述

Instancio是一个用于生成测试对象的Java库，它的主要目标是减少单元测试中的手动数据设置。它的API被设计为尽可能简洁和非侵入性，同时提供足够的灵活性来自定义生成的对象。Instancio不需要更改生产代码，并且可以开箱即用，零配置。

## 2. 项目目标

有一些现有的库可用于生成真实的测试数据，例如地址、名字和姓氏等。虽然Instancio也支持这个用例，但这不是它的目标。该项目背后的想法是大多数单元测试不关心实际值是什么，它们只需要
*存在一个值*。因此，Instancio的主要目标只是简单地生成具有随机数据的完全填充的对象，包括数组、集合、嵌套集合、泛型类型等。它的目标是使用尽可能少的代码来做到这一点，以保持测试简洁。

Instancio的另一个目标是使测试更加动态，由于每次测试运行都是针对随机值的，因此测试将变为活动状态。它们涵盖了更广泛的输入范围，这可能有助于发现静态数据可能未被注意到的错误。在许多情况下，数据的随机性也消除了对参数化测试方法的需要。

最后，Instancio旨在提供可重现的数据。它为它生成的每个对象图使用一致的种子值。因此，如果测试针对一组给定的输入失败，Instancio支持重新生成相同的数据集以重现失败的测试。

## 3. API实例

本节概述了用于创建和自定义对象的API。

### 3.1 创建对象

[Instancio](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/Instancio.html)
类是API的入口点，它提供了以下用于创建对象的速记方法。当默认值足够并且不需要自定义生成的值时，可以使用这些。

```java
// 速记方法
Instancio.create(Class<T> type)
Instancio.create(TypeTokenSupplier<T> supplier)
Instancio.create(Model<T> model)
```

以下构建器方法允许链接其他方法调用以自定义生成的值、忽略某些字段、提供自定义设置等。

```java
// 构建器API
1. Instancio.of(Class<T> type).create()
2. Instancio.of(TypeTokenSupplier<T> supplier).create()
3. Instancio.of(Model<T> model).create()
```

这些方法接收的三个参数可以用于不同的目的。

1. 通过指定类创建实例；在大多数情况下，此方法应该足够了。
2. 此方法用于通过提供类型标记来创建泛型类型的实例。
3. 使用Instancio [Model](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/Model.html)
   创建一个实例，它充当创建对象的模板(请参阅[使用Models](https://www.instancio.org/user-guide/#using-models))。

create()方法的示例：

```java
// Create by specifying the class
Person person = Instancio.create(Person.class);

// Using type tokens
Pair<String, Long> pair = Instancio.create(new TypeToken<Pair<String, Long>>() {});

Map<Integer, List<String>> map = Instancio.create(new TypeToken<Map<Integer, List<String>>>() {});

// Create from a model
Model<Person> personModel = Instancio.of(Person.class)
    .ignore(field("age"))
    .toModel();

Person personWithoutAgeAndAddress = Instancio.of(personModel)
    .ignore(field("address"))
    .create();
```

应该注意的是，也可以使用Instancio.of(Class)类型参数并将类型参数指定为withTypeParameters()方法的参数来创建泛型类型：

```java
Pair<String, Long> pair = Instancio.of(Pair.class)
    .withTypeParameters(String.class, Long.class)
    .create();
```

但是，这种方法有几个缺点：它不支持嵌套泛型，并且使用它会产生“unchecked assignment”警告。

### 3.2 创建集合

构建器API还支持使用以下方法创建集合：

```java
// 集合API
Instancio.ofList(Class<T> elementType).create()
Instancio.ofSet(Class<T> elementType).create()
Instancio.ofMap(Class<K> keyType, Class<V> valueType).create()
```

**例子：**

```java
List<Person> list = Instancio.ofList(Person.class).size(10).create();

Map<UUID, Address> map = Instancio.ofMap(UUID.class, Address.class).size(3)
    .set(field(Address.class, "city"), "Vancouver")
    .create();
```

指定集合的大小是可选的，如果未指定大小，将生成随机大小(介于2和6之间)的集合。

### 3.3 创建Record和Sealed类

Instancio版本1.5.0引入了对Java 17的支持：

- 在Java 16+上运行时的record类
- 在Java 17+上运行时的sealed类

这使用与上述相同的API来创建常规类。

### 3.4 创建对象流

Instancio还提供了用于创建Stream对象的方法，这些stream()方法返回一个由不同的完全填充实例组成的无限流。与create()
方法类似，如果不需要自定义，这些方法具有速记形式：

```java
// 速记方法
Instancio.stream(Class<T> type)
Instancio.stream(TypeTokenSupplier<T> supplier)
```

以及允许自定义生成值的构建器API：

```java
// 流构建器API
Instancio.of(Class<T> type).stream()
Instancio.of(TypeTokenSupplier<T> supplier).stream()
```

以下是使用流的几个示例。请注意对limit()的调用，这是避免无限循环所必需的。

```java
List<Person> personList = Instancio.stream(Person.class)
    .limit(3)
    .collect(Collectors.toList());

Map<UUID, Person> personMap = Instancio.of(new TypeToken<Person>() {})
    .ignore(all(field("age"), field("address")))
    .stream()
    .limit(3)
    .collect(Collectors.toMap(Person::getUuid, Function.identity()));
```

> 注意：由于返回的流是无限的，因此**必须调用limit()**以避免无限循环。

## 4. 选择器

选择器用于定位字段和类，例如为了自定义生成的值。Instancio支持不同类型的选择器，所有这些选择器都实现了[TargetSelector](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/TargetSelector.html)
接口。这些类型是：

- 常规选择器
- 方法引用选择器
- 谓词选择器
- 选择器组
- 便利选择器

以上所有内容都可以使用[Select](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/Select.html)
类中的静态方法创建。

### 4.1 常规选择器

常规选择器用于精确匹配：它们只能匹配单个字段或单个类型。

```java
1. Select.field(String fieldName)
2. Select.field(Class<?> declaringClass, String fieldName)
3. Select.all(Class<?> type)
```

1. 按名称选择字段，在正在创建的类中声明。
2. 按名称选择字段，在指定的类中声明。
3. 选择指定的类，包括该类型的字段和集合元素。

**例子：**

```java
Select.field(Person.class, "name") // Person.name
Select.all(Set.class)
```

Select.field()根据确切的字段名称进行匹配，如果具有指定名称的字段不存在，则会抛出错误。Select.all()使用Class相等性匹配，因此匹配不包括子类型。

### 4.2 方法引用选择器

该选择器使用方法引用来匹配字段。

```java
Select.field(GetMethodSelector<T, R> methodReference)
```

**例子：**

```java
Select.field(Person::getName)
```

在内部，方法引用被转换为常规字段选择器，相当于Select.field(Class<?> declaringClass, String fieldName)
，这是通过将方法名称映射到相应的字段名称来完成的。映射逻辑支持以下命名约定：

- Java Bean：其中getter方法以get为前缀；如果是布尔值，则为is
- Java Record：其中方法名称与字段名称完全匹配

例如，支持以下所有字段和方法名称的组合：

| 方法名称       | 字段名称     | 例子                                                         |
|:-----------|:---------|:-----------------------------------------------------------|
| getName()  | name     | field(Person::getName) -> field(Person.class, "name")      |
| name()     | name     | field(Person::name) -> field(Person.class, "name")         |
| isActive() | active   | field(Person::isActive) -> field(Person.class, "active")   |
| isActive() | isActive | field(Person::isActive) -> field(Person.class, "isActive") |

对于遵循其他命名约定的方法，或者没有可用的方法的情况，可以改用常规字段选择器。

> 常规选择器定义：从这里开始，*常规选择器*的定义也包括方法引用选择器。

### 4.3 谓词选择器

谓词选择器允许在匹配字段和类时具有更大的灵活性，它们使用复数命名约定：fields()和types()。

```java
1. Select.fields(Predicate<Field> fieldPredicate)
2. Select.types(Predicate<Class<?>> classPredicate)
```

1. 选择与谓词匹配的所有字段
2. 选择与谓词匹配的所有类型

**例子：**

```java
Select.fields(field -> field.getName().contains("date"))
Select.types(klass -> Collection.class.isAssignableFrom(klass))
```

与常规选择器不同，这些选择器可以匹配多个字段或类型。例如，它们可用于匹配类声明的所有字段，或包中的所有类。它们也可用于匹配特定类型，包括其子类型。

### 4.4 便利选择器

便利选择器提供构建在常规和谓词选择器之上的语法糖。

```java
1. Select.all(GroupableSelector... selectors)
2. Select.allStrings()
3. Select.allInts()
4. Select.fields()
5. Select.types()
6. Select.root()
```

1. 用于组合多个常规选择器
2. 相当于all(String.class)
3. 相当于all(all(int.class), all(Integer.class))
4. 构建Predicate<Field\>选择器的构建器
5. 构建Predicate<Class<?>\>选择器的构建器
6. 选择根，即正在创建的对象

> allXxx()方法(例如allInts())适用于所有核心类型。

+ **Select.all(GroupableSelector... selectors)**

此方法可用于对多个选择器进行分组，从而允许更简洁的代码，如下所示。但是，只有常规选择器可以使用all()进行分组，谓词选择器不可分组。

```java
all(field(Address::getCity),
    field(Address.class, "postalCode"),
    all(Phone.class))
```

+ **Select.fields()**和**Select.types()**

这些选择器提供用于构建谓词选择器的构建器API。例如，以下谓词匹配用@Id标注的Long字段：

```java
Select.fields(f -> f.getType() == Long.class && f.getDeclaredAnnotation(Id.class) != null)
```

也可以使用fields()谓词构建器来表达：

```java
Select.fields().ofType(Long.class).annotated(Id.class)
```

+ **Select.root()**

此方法选择根对象。以下代码片段创建嵌套列表，其中外部列表和内部列表具有不同的大小：

```java
List<List<String>> result = Instancio.of(new TypeToken<List<List<String>>>() {})
    .generate(root(), gen -> gen.collection().size(outerListSize))
    .generate(all(List.class), gen -> gen.collection().size(innerListSize))
    .create();
```

在这种情况下，all(List.class)匹配除外部列表之外的所有列表，因为root()选择器的优先级高于其他选择器。

### 4.5 选择器优先级

当多个选择器匹配一个字段或类时，选择器优先级规则适用：

+ 常规选择器的优先级高于谓词选择器
+ 字段选择器的优先级高于类型选择器

考虑以下示例：

```java
Address address = Instancio.of(Address.class)
    .set(allStrings(), "foo")
    .set(field("city"), "bar")
    .create();
```

这将生成一个所有字符串都设置为“foo”的Address对象。但是，由于字段选择器具有更高的优先级，city将被设置为“bar”。

在下面的示例中，city也将设置为“bar”，因为谓词fields()选择器的优先级低于常规field()选择器：

```java
Address address = Instancio.of(Address.class)
    .set(fields().named("city"), "foo")
    .set(field("city"), "bar")
    .create();
```

#### 4.5.1 多个匹配选择器

当多个选择器与给定目标匹配时，最后一个选择器获胜，此规则适用于常规选择器和谓词选择器。

在常规选择器的情况下，如果两者相同，则最后一个选择器简单地替换第一个选择器(在内部，常规选择器存储为Map键)。

```java
Address address = Instancio.of(Address.class)
    .set(field(Address.class, "city"), "foo")
    .set(field(Address.class, "city"), "bar") // wins!
    .create();
```

另一方面，谓词选择器存储为List并从最后一个条目开始按顺序求值。

```java
Address address = Instancio.of(Address.class)
    .set(fields().named("city"), "foo")
    .set(fields().named("city"), "bar") // wins!
    .lenient()
    .create();
```

在此特定示例中，第一个条目保持未使用状态，因此必须启用lenient()模式以防止未使用的选择器错误(
请参阅[选择器严格性](https://www.instancio.org/user-guide/#selector-strictness))。

### 4.6 选择器范围

常规选择器提供within(Scope... scopes)方法，用于微调目标选择器。Instancio支持两种类型的范围：

- 类级范围：将选择器缩小到指定的类
- 字段级范围：将选择器缩小到目标类的指定字段

为了说明范围是如何工作的，我们将假设Person类具有以下结构(省略了getter和setter)。

```java
class Person {
    String name;
    Address homeAddress;
    Address workAddress;
}

class Address {
    String street;
    String city;
    List<Phone> phoneNumbers;
}

class Phone {
    String areaCode;
    String number;
}
```

请注意，该Person类有两个Address字段。选择器field(Address::getCity)
将针对这两个Address，homeAddress和workAddress。如果没有范围，就不可能将两个City设置为不同的值，使用范围可以解决此问题：

```java
Scope homeAddress = field(Person::getHomeAddress).toScope();
Scope workAddress = field(Person::getWorkAddress).toScope();

Person person = Instancio.of(Person.class)
    .set(field(Address::getCity).within(homeAddress), "foo")
    .set(field(Address::getCity).within(workAddress), "bar")
    .create();
```

对于更复杂的类结构，可以使用within(Scope...)方法指定多个范围。指定多个范围时，顺序很重要：从最外层到最内层范围。下面将提供额外的示例。

#### 4.6.1 创建范围

可以使用以下方式创建范围：

- [Select](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/Select.html)类中的Select.scope()静态方法
- 常规选择器提供的Selector.toScope()方法

第一种方法需要指定目标类，对于字段级范围，还需要指定字段名称：

```java
Select.scope(Class<?> targetClass)
Select.scope(Class<?> targetClass, String field)
```

**例子：**

```java
Select.scope(Phone.class);
Select.scope(Person.class, "homeAddress");
```

第二种方法是使用toScope()方法从选择器创建范围，此方法仅适用于常规(非谓词)选择器。

```java
Select.all(Class<T> targetClass).toScope()
Select.field(Class<T> targetClass, String field).toScope()
Select.field(GetMethodSelector<T, R> methodReference).toScope()
```

**例子：**

```java
Select.all(Phone.class).toScope();
Select.field(Person.class, "homeAddress").toScope();
Select.field(Person::getHomeAddress).toScope();
```

#### 4.6.2 使用范围的示例

首先，在不使用范围的情况下，我们可以将所有字符串设置为相同的值。例如，以下代码片段会将每个类的每个字符串字段设置为“foo”。

```java
// 将所有字符串设置为"Foo"
Person person = Instancio.of(Person.class)
    .set(allStrings(), "foo")
    .create();
```

使用within()我们可以缩小allStrings()选择器的范围。为简洁起见，该Instancio.of(Person.class)行将被省略。

```java
// 设置所有Address实例中的所有字符串；这包括Phone实例，因为它们包含在Address中
set(allStrings().within(scope(Address.class)), "foo")
```

```java
// 设置列表中包含的所有字符串(匹配我们示例中的所有Phone实例)
set(allStrings().within(scope(List.class)), "foo")
```

```java
// 设置Person.homeAddress地址对象中的所有字符串
set(allStrings().within(scope(Person.class, "homeAddress")), "foo")
```

使用within()还允许指定多个范围。范围必须自上而下指定，从最外层到最内层。

```java
// 设置Person.workAddress字段中包含的所有Phone实例的所有字符串
set(allStrings().within(scope(Person.class, "workAddress"), scope(Phone.class)), "foo")
```

该Person.workAddress对象包含一个Phone列表，因此Person.workAddress是最外层的范围并且首先被指定。Phone类是最里面的范围，最后指定。

最后的示例演示了从常规选择器创建范围对象，以下所有内容都是等价的。

```java
// 基于字段创建范围的等效方法
set(allStrings().within(scope(Person.class, "homeAddress")), "foo")

set(allStrings().within(field(Person.class, "homeAddress").toScope()), "foo")

set(allStrings().within(field(Person::getHomeAddress).toScope()), "foo")
```

```java
// 基于类创建范围的等效方法
set(allStrings().within(scope(Person.class)), "foo")

set(allStrings().within(all(Person.class).toScope()), "foo")
```

## 5. 选择器严格性

### 5.1 严格模式

Instancio支持两种模式：严格模式和宽松模式，这一想法受到Mockito非常有用的严格存根功能的启发。

在严格模式下，未使用的选择器将触发错误。在宽松模式下，未使用的选择器将被忽略。默认情况下，Instancio在严格模式下运行，这样做的原因如下：

- 消除数据设置中的错误
- 重构后简化修复测试
- 保持测试代码清洁和可维护

#### 5.1.1 消除数据设置中的错误

未使用的选择器可能表示数据设置中存在错误。例如，考虑填充以下数据的POJO：

```java
class SamplePojo {
    SortedSet<String> values;
}

SamplePojo pojo = Instancio.of(SamplePojo.class)
    .generate(all(Set.class), gen -> gen.collection().size(10))
    .create();
```

乍一看，我们可能会期望Set生成大小为10的。但是，由于该字段被声明为SortedSet并且类选择器targets Set，因此generate()不会应用该方法。

由于all(Set.class)与任何目标都不匹配，Instancio会产生一个错误：

```shell
org.instancio.exception.UnusedSelectorException:

 -> Unused selectors in generate(), set(), or supply():
 1: all(Set)
```

如果不注意这个细节，即使使用像上面这样的简单类，也很容易犯这种错误并面临意想不到的结果，在生成更复杂的类时会变得更加棘手。严格模式有助于减少此类错误。

#### 5.1.2 重构后简化修复测试

重构总是会导致测试在某种程度上中断。随着类和字段的重新组织和重命名，测试需要更新以反映变化。假设存在使用Instancio的现有测试，在严格模式下运行测试将快速突出显示重构引起的数据设置中的任何问题。

#### 5.1.3 保持测试代码清洁和可维护

最后但同样重要的是，保持测试的清洁和可维护性非常重要。应像对待生产代码一样谨慎对待测试代码，保持测试简洁明了使它们更易于维护。

### 5.2 宽松模式

虽然强烈建议使用严格模式，但可以根据选择切换到宽松模式。宽松模式可以使用以下lenient()方法来启用：

```java
// 使用构建器API设置宽松模式
Person person = Instancio.of(Person.class)
    // snip...
    .lenient()
    .create();
```

也可以通过Settings的以下方式启用宽松模式：

```java
// 使用Settings设置宽松模式
Settings settings = Settings.create()
    .set(Keys.MODE, Mode.LENIENT);

Person person = Instancio.of(Person.class)
    .withSettings(settings)
    // snip...
    .create();
```

也可以使用全局[instancio.properties](https://www.instancio.org/user-guide/#overriding-settings-using-a-properties-file)
文件启用宽松模式：

```properties
# 使用属性文件设置宽松模式
mode=LENIENT
```

## 6. 总结

在本文中，我们介绍了Instanico的基本使用，包括如何使用Instancio生成测试数据(例如对象、集合、流)，以及选择器的相关分类。