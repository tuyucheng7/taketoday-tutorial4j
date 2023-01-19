## 1. 简介

在本文中，我们将展示如何使用[Immutables](https://immutables.github.io/)库。

该库由注解和注解处理器组成，用于生成和处理可序列化和可定制的不可变对象。

## 2.Maven依赖

为了在的项目中使用不可变，需要将以下依赖项添加到pom.xml文件的依赖项部分：

```xml
<dependency>
    <groupId>org.immutables</groupId>
    <artifactId>value</artifactId>
    <version>2.2.10</version>
    <scope>provided</scope>
</dependency>
```

由于在运行时不需要此工件，因此建议指定提供的范围。

可以在[此处](https://search.maven.org/classic/#search|ga|1|a%3A"value" org.immutables)找到该库的最新版本。

## 3.不可变的

该库从抽象类型生成不可变对象：Interface、Class、Annotation。

实现这一目标的关键是正确使用@Value.Immutable注解。它生成带注解类型的不可变版本，并在其名称前加上Immutable关键字作为前缀。

如果我们尝试生成一个名为“ X ”的类的不可变版本，它将生成一个名为“ImmutableX”的类。生成的类不是递归不可变的，所以最好记住这一点。

快速说明 - 因为 Immutables 使用注解处理，所以需要记住在的 IDE 中启用注解处理。

### 3.1. 将@Value.Immutable与抽象类和接口一起使用

让我们创建一个简单的抽象类Person，它由两个表示要生成的字段的抽象访问器方法组成，然后用@Value.Immutable注解对该类进行注解：

```java
@Value.Immutable
public abstract class Person {

    abstract String getName();
    abstract Integer getAge();

}
```

注解处理完成后，我们可以在target/generated-sources目录中找到一个随时可用的新生成的ImmutablePerson类：

```java
@Generated({"Immutables.generator", "Person"})
public final class ImmutablePerson extends Person {

    private final String name;
    private final Integer age;

    private ImmutablePerson(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    @Override
    String getName() {
        return name;
    }

    @Override
    Integer getAge() {
        return age;
    }

    // toString, hashcode, equals, copyOf and Builder omitted

}
```

生成的类带有已实现的toString、hashcode、equals方法和一个 stepbuilder ImmutablePerson.Builder。请注意，生成的构造函数具有私有访问权限。

为了构造ImmutablePerson类的实例，我们需要使用构建器或静态方法ImmutablePerson.copyOf，它可以从Person对象创建一个ImmutablePerson副本。

如果我们想使用构建器构造一个实例，我们可以简单地编写代码：

```java
ImmutablePerson john = ImmutablePerson.builder()
  .age(42)
  .name("John")
  .build();
```

生成的类是不可变的，这意味着它们不能被修改。如果你想修改一个已经存在的对象，你可以使用其中一个“ withX ”方法，它不会修改原始对象，而是创建一个带有修改字段的新实例。

让我们更新john 的年龄并创建一个新的john43对象：

```java
ImmutablePerson john43 = john.withAge(43);

```

在这种情况下，以下断言将为真：

```java
assertThat(john).isNotSameAs(john43);
assertThat(john.getAge()).isEqualTo(42);
```

## 4. 附加实用程序

如果不能自定义，这样的类生成就不会很有用。Immutables 库带有一组附加注解，可用于自定义@Value.Immutable的输出。要查看所有这些，请参阅 Immutables 的[文档](https://immutables.github.io/immutable.html)。

### 4.1. @ Value.Parameter注解

@Value.Parameter注解可用于指定应为其生成构造方法的字段。

如果你这样注解你的类：

```java
@Value.Immutable
public abstract class Person {

    @Value.Parameter
    abstract String getName();

    @Value.Parameter
    abstract Integer getAge();
}
```

可以通过以下方式实例化它：

```java
ImmutablePerson.of("John", 42);
```

### 4.2. @ Value.Default注解

@Value.Default注解允许指定一个默认值，当未提供初始值时应使用该默认值。为此，需要创建一个返回固定值的非抽象访问器方法，并使用@Value.Default对其进行注解：

```java
@Value.Immutable
public abstract class Person {

    abstract String getName();

    @Value.Default
    Integer getAge() {
        return 42;
    }
}
```

以下断言将为真：

```java
ImmutablePerson john = ImmutablePerson.builder()
  .name("John")
  .build();

assertThat(john.getAge()).isEqualTo(42);
```

### 4.3. @ Value.Auxiliary注解

@Value.Auxiliary注解可用于注解将存储在对象实例中的属性，但将被 equals、hashCode和toString实现忽略。

如果你这样注解你的类：

```java
@Value.Immutable
public abstract class Person {

    abstract String getName();
    abstract Integer getAge();

    @Value.Auxiliary
    abstract String getAuxiliaryField();

}
```

使用辅助字段时，以下断言将为真：

```java
ImmutablePerson john1 = ImmutablePerson.builder()
  .name("John")
  .age(42)
  .auxiliaryField("Value1")
  .build();

ImmutablePerson john2 = ImmutablePerson.builder()
  .name("John")
  .age(42)
  .auxiliaryField("Value2")
  .build();


assertThat(john1.equals(john2)).isTrue();
assertThat(john1.toString()).isEqualTo(john2.toString());

assertThat(john1.hashCode()).isEqualTo(john2.hashCode());
```

### 4.4. @Value.Immutable(Prehash = True )注解

由于我们生成的类是不可变的并且永远不会被修改，因此hashCode结果将始终保持不变并且只能在对象实例化期间计算一次。

如果你这样注解你的类：

```java
@Value.Immutable(prehash = true)
public abstract class Person {

    abstract String getName();
    abstract Integer getAge();

}
```

检查生成的类时，可以看到哈希码值现在已预先计算并存储在一个字段中：

```java
@Generated({"Immutables.generator", "Person"})
public final class ImmutablePerson extends Person {

    private final String name;
    private final Integer age;
    private final int hashCode;

    private ImmutablePerson(String name, Integer age) {
        this.name = name;
        this.age = age;
        this.hashCode = computeHashCode();
    }

    // generated methods
 
    @Override
    public int hashCode() {
        return hashCode;
    }
}

```

hashCode()方法返回在构造对象时生成的预先计算的哈希码。

## 5.总结

在本快速教程中，我们展示了[Immutables](https://immutables.github.io/)库的基本工作原理。