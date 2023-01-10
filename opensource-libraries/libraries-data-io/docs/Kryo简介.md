## 1. 概述

[Kryo](https://github.com/EsotericSoftware/kryo)是一个Java序列化框架，专注于速度、效率和用户友好的 API。

在本文中，我们将探索 Kryo 框架的主要特性，并通过实施示例来展示其功能。

## 2.Maven依赖

我们需要做的第一件事是将kryo依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.esotericsoftware</groupId>
    <artifactId>kryo</artifactId>
    <version>4.0.1</version>
</dependency>
```

这个工件的最新版本可以在[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"com.esotericsoftware" AND a%3A"kryo")上找到。

## 3. 冷冻基础知识

让我们首先看看 Kryo 是如何工作的，以及我们如何使用它序列化和反序列化对象。

### 3.1. 介绍

该框架提供Kryo类作为其所有功能的主要入口点。

此类协调序列化过程并将类映射到Serializer实例，后者处理将对象的图形转换为字节表示的细节。

字节准备就绪后，将使用Output对象将它们写入流。通过这种方式，它们可以存储在文件、数据库中或通过网络传输。

稍后，当需要该对象时，使用Input实例读取这些字节并将它们解码为Java对象。

### 3.2. 序列化对象

在深入示例之前，让我们首先创建一个实用程序方法来初始化我们将用于本文中每个测试用例的一些变量：

```java
@Before
public void init() {
    kryo = new Kryo();
    output = new Output(new FileOutputStream("file.dat"));
    input = new Input(new FileInputStream("file.dat"));
}
```

现在，我们可以看看使用 Kryo 编写和读取对象是多么容易：

```java
@Test
public void givenObject_whenSerializing_thenReadCorrectly() {
    Object someObject = "Some string";

    kryo.writeClassAndObject(output, someObject);
    output.close();

    Object theObject = kryo.readClassAndObject(input);
    input.close();

    assertEquals(theObject, "Some string");
}
```

注意对close()方法的调用。这是必需的，因为Output和Input类分别继承自OutputStream和InputStream。

序列化多个对象同样简单：

```java
@Test
public void givenObjects_whenSerializing_thenReadCorrectly() {
    String someString = "Multiple Objects";
    Date someDate = new Date(915170400000L);

    kryo.writeObject(output, someString);
    kryo.writeObject(output, someDate);
    output.close();

    String readString = kryo.readObject(input, String.class);
    Date readDate = kryo.readObject(input, Date.class);
    input.close();

    assertEquals(readString, "Multiple Objects");
    assertEquals(readDate.getTime(), 915170400000L);
}
```

请注意，我们将适当的类传递给readObject()方法，这使我们的代码无需转换。

## 4.序列化器

在本节中，我们将展示哪些序列化程序已经可用，然后我们将创建我们自己的序列化程序。

### 4.1. 默认序列化程序

当 Kryo 序列化一个对象时，它会创建一个先前注册的Serializer类的实例来转换为字节。这些称为默认序列化程序，无需我们进行任何设置即可使用。

该库已经提供了几个这样的序列化器来处理基元、列表、映射、枚举等。如果没有为给定类找到序列化器，则使用FieldSerializer，它几乎可以处理任何类型的对象。

让我们看看这是什么样子的。首先，让我们创建一个Person类：

```java
public class Person {
    private String name = "John Doe";
    private int age = 18;
    private Date birthDate = new Date(933191282821L);

    // standard constructors, getters, and setters
}
```

现在，让我们从这个类中写一个对象，然后读回它：

```java
@Test
public void givenPerson_whenSerializing_thenReadCorrectly() {
    Person person = new Person();

    kryo.writeObject(output, person);
    output.close();

    Person readPerson = kryo.readObject(input, Person.class);
    input.close();

    assertEquals(readPerson.getName(), "John Doe");
}
```

请注意，我们不必指定任何内容来序列化Person对象，因为FieldSerializer是自动为我们创建的。

### 4.2. 自定义序列化程序

如果我们需要更多地控制序列化过程，我们有两个选择；我们可以编写自己的Serializer类并将其注册到 Kryo 或让该类自行处理序列化。

为了演示第一个选项，让我们创建一个扩展Serializer的类：

```java
public class PersonSerializer extends Serializer<Person> {

    public void write(Kryo kryo, Output output, Person object) {
        output.writeString(object.getName());
        output.writeLong(object.getBirthDate().getTime());
    }

    public Person read(Kryo kryo, Input input, Class<Person> type) {
        Person person = new Person();
        person.setName(input.readString());
        long birthDate = input.readLong();
        person.setBirthDate(new Date(birthDate));
        person.setAge(calculateAge(birthDate));
        return person;
    }

    private int calculateAge(long birthDate) {
        // Some custom logic
        return 18;
    }
}
```

现在，让我们来测试一下：

```java
@Test
public void givenPerson_whenUsingCustomSerializer_thenReadCorrectly() {
    Person person = new Person();
    person.setAge(0);
    
    kryo.register(Person.class, new PersonSerializer());
    kryo.writeObject(output, person);
    output.close();

    Person readPerson = kryo.readObject(input, Person.class);
    input.close();

    assertEquals(readPerson.getName(), "John Doe");
    assertEquals(readPerson.getAge(), 18);
}
```

请注意，age字段等于 18，即使我们之前将其设置为 0。

我们还可以使用@DefaultSerializer注解让 Kryo 知道我们想要在每次需要处理Person对象时使用PersonSerializer 。这有助于避免调用register()方法：

```java
@DefaultSerializer(PersonSerializer.class)
public class Person implements KryoSerializable {
    // ...
}
```

对于第二个选项，让我们修改我们的Person类以扩展KryoSerializable接口：

```java
public class Person implements KryoSerializable {
    // ...

    public void write(Kryo kryo, Output output) {
        output.writeString(name);
        // ...
    }

    public void read(Kryo kryo, Input input) {
        name = input.readString();
        // ...
    }
}
```

由于此选项的测试用例与前一个相同，因此此处不包括在内。但是，可以在本文的源代码中找到它。

### 4.3. Java序列化器

在零星的情况下，Kryo 将无法序列化一个类。如果发生这种情况，并且编写自定义序列化程序不是一个选项，我们可以使用标准的Java序列化机制，使用JavaSerializer。这要求该类像往常一样实现Serializable接口。

下面是一个使用上述序列化程序的示例：

```java
public class ComplexObject implements Serializable {
    private String name = "Bael";
    
    // standard getters and setters
}
@Test
public void givenJavaSerializable_whenSerializing_thenReadCorrectly() {
    ComplexClass complexObject = new ComplexClass();
    kryo.register(ComplexClass.class, new JavaSerializer());

    kryo.writeObject(output, complexObject);
    output.close();

    ComplexClass readComplexObject = kryo.readObject(input, ComplexClass.class);
    input.close();

    assertEquals(readComplexObject.getName(), "Bael");
}
```

## 5.总结

在本教程中，我们探索了 Kryo 库最显着的特性。

我们序列化了多个简单对象，并使用FieldSerializer类来处理自定义对象。我们还创建了一个自定义序列化程序，并演示了如何在需要时回退到标准Java序列化机制。