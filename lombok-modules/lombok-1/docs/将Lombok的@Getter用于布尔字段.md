## 1. 简介

[Project Lombok](https://www.baeldung.com/intro-to-project-lombok) 是一个用于减少Java样板文件的流行库。

在本快速教程中，我们将了解 Lombok 的[@Getter](https://projectlombok.org/features/GetterSetter) 注解如何在布尔字段上工作以消除创建其相应的 getter 方法的需要。

## 2.Maven依赖

让我们从将[Project Lombok](https://search.maven.org/classic/#search|ga|1|g%3A"org.projectlombok")添加到我们的pom.xml开始：

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.20</version>
</dependency>
```

## 3. 在布尔字段上使用@Getter

假设我们希望 Lombok 为我们的私有布尔字段生成访问器方法。

我们可以用 @Getter注解该字段：

```java
@Getter
private boolean running;
```

并且 Lombok 将使用其[注解处理器](https://www.baeldung.com/java-annotation-processing-builder)在类中生成一个 isRunning()方法。

现在，我们可以引用它，即使我们没有自己编写方法：

```java
@Test
public void whenBasicBooleanField_thenMethodNamePrefixedWithIsFollowedByFieldName() {
    LombokExamples lombokExamples = new LombokExamples();
    assertFalse(lombokExamples.isRunning());
}
```

### 3.1. 与其访问器同名的布尔字段

让我们添加另一行代码，使示例有点棘手：

```java
@Getter
private boolean isRunning = true;
```

如果 Lombok 创建一个名为isIsRunning的方法，那就有点麻烦了。

相反，Lombok 像以前一样创建 isRunning：

```java
@Test
public void whenBooleanFieldPrefixedWithIs_thenMethodNameIsSameAsFieldName() {
    LombokExamples lombokExamples = new LombokExamples();
    assertTrue(lombokExamples.isRunning());
}
```

### 3.2. 具有相同访问器名称的两个布尔字段

有时，可能会发生冲突。

假设我们需要在同一个类中包含以下几行：

```java
    @Getter
    public boolean running = true;

    @Getter
    public boolean isRunning = false;
```

我们应该避免像这样令人困惑的命名约定的原因有很多。其中之一是它为龙目岛制造了冲突。

使用 Lombok 的约定，这两个字段将具有相同的访问器方法名称： isRunning。但是在同一个类中有两个同名的方法会产生编译器错误。

Lombok 通过只创建一个访问器方法来解决这个问题，在这种情况下，根据字段声明顺序将其指向 运行 ：

```java
@Test
public void whenTwoBooleanFieldsCauseNamingConflict_thenLombokMapsToFirstDeclaredField() {
    LombokExamples lombokExamples = new LombokExamples();
    assertTrue(lombokExamples.isRunning() == lombokExamples.running);
    assertFalse(lombokExamples.isRunning() == lombokExamples.isRunning);
}
```

## 4. 在布尔字段上使用@Getter

现在，Lombok 对Boolean 类型的处理方式略有不同。

让我们最后一次尝试相同的运行示例，但使用 Boolean 而不是原始类型：

```java
@Getter
private Boolean running;
```

Lombok 将生成 getRunning而不是创建isRunning：

```java
@Test
public void whenFieldOfBooleanType_thenLombokPrefixesMethodWithGetInsteadOfIs() {
    LombokExamples lombokExamples = new LombokExamples();
    assertTrue(lombokExamples.getRunning());
}
```

## 5.总结

在本文中，我们探讨了如何将 Lombok 的 @Getter 注解用于布尔基元和布尔对象。