## 1. 概述

[在本快速教程中，我们将展示使用Jackson](https://www.baeldung.com/jackson) JSON 处理库反序列化不可变Java对象的两种不同方法。

## 2. 为什么我们使用不可变对象？

不可[变对象](https://www.baeldung.com/java-immutable-object)是从创建之时起就保持其状态不变的对象。这意味着无论最终用户调用对象的哪些方法，对象的行为都是一样的。

当我们设计一个必须在多线程环境中工作的系统时，不可变对象就派上用场了，因为不可变性通常可以保证线程安全。

另一方面，当我们需要处理来自外部源的输入时，不可变对象很有用。例如，它可以是用户输入或存储中的一些数据。在这种情况下，保存接收到的数据并保护其免受意外或无意更改可能至关重要。

让我们看看如何反序列化一个不可变对象。

## 3.公共构造函数

让我们考虑一下Employee类结构。它有两个必填字段：id和name，因此我们定义了一个公共的全参数构造函数，它有一组与对象字段集匹配的参数：

```java
public class Employee {

    private final long id;
    private final String name;

    public Employee(long id, String name) {
        this.id = id;
        this.name = name;
    }

    // getters
}
```

这样，我们将在创建时初始化对象的所有字段。字段声明中的最终修饰符不允许我们在将来更改它们的值。为了使这个对象可反序列化，我们只需要向这个构造函数添加几个[注解：](https://www.baeldung.com/jackson-annotations)

```java
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
public Employee(@JsonProperty("id") long id, @JsonProperty("name") String name) {
    this.id = id;
    this.name = name;
}
```

让我们仔细看看我们刚刚添加的注解。

首先，@JsonCreator告诉 Jackson 反序列化器使用指定的构造函数进行反序列化。

有两种模式可以用作此注解的参数——PROPERTIES和DELEGATING。

当我们声明一个全参数构造函数时，PROPERTIES是最合适的，而DELEGATING可能对单参数构造函数有用。

之后，我们需要使用@JsonProperty对每个构造函数参数进行注解，并将相应属性的名称作为注解值。在这一步我们应该非常小心，因为所有属性名称都必须与我们在序列化期间使用的名称相匹配。

让我们看一下涵盖Employee对象反序列化的简单单元测试：

```java
String json = "{"name":"Frank","id":5000}";
Employee employee = new ObjectMapper().readValue(json, Employee.class);

assertEquals("Frank", employee.getName());
assertEquals(5000, employee.getId());
```

## 4. 私有构造函数和生成器

有时碰巧一个对象有一组可选字段。让我们考虑另一个类结构Person，它有一个可选的年龄字段：

```java
public class Person {
    private final String name;
    private final Integer age;

    // getters
}
```

当我们有大量这样的字段时，创建公共构造函数可能会变得很麻烦。换句话说，我们需要为构造函数声明很多参数，并用@JsonProperty注解对每个参数进行注解。结果，许多重复的声明会使我们的代码臃肿且难以阅读。

当经典的[Builder 模式](https://www.baeldung.com/creational-design-patterns)来拯救时就是这种情况。让我们看看我们如何在反序列化中利用它的力量。首先，让我们声明一个私有的全参数构造函数和一个Builder类：

```java
private Person(String name, Integer age) {
    this.name = name;
    this.age = age;
}

static class Builder {
    String name;
    Integer age;
    
    Builder withName(String name) {
        this.name = name;
        return this;
    }
    
    Builder withAge(Integer age) {
        this.age = age;
        return this;
    }
    
    public Person build() {
        return new Person(name, age);
    } 
}
```

为了让 Jackson 反序列化器使用这个Builder，我们只需要在我们的代码中添加两个注解。首先，我们需要用@JsonDeserialize注解来标记我们的类，传递一个带有构建器类的完全限定域名的构建器参数。

之后，我们需要将构建器类本身注解为@JsonPOJOBuilder：

```java
@JsonDeserialize(builder = Person.Builder.class)
public class Person {
    //...
    
    @JsonPOJOBuilder
    static class Builder {
        //...
    }
}
```

请注意，我们可以自定义构建期间使用的方法的名称。

参数buildMethodName默认为“ build”，代表构建器准备生成新对象时调用的方法名称。

另一个参数withPrefix代表我们添加到负责设置属性的构建器方法中的前缀。此参数的默认值为“with”。这就是为什么我们没有在示例中指定任何这些参数的原因。

让我们看一下涵盖Person对象反序列化的简单单元测试：

```java
String json = "{"name":"Frank","age":50}";
Person person = new ObjectMapper().readValue(json, Person.class);

assertEquals("Frank", person.getName());
assertEquals(50, person.getAge().intValue());
```

## 5.总结

在这篇简短的文章中，我们了解了如何使用 Jackson 库反序列化不可变对象。