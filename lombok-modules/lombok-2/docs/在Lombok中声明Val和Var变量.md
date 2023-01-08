## 1. 简介

[Project Lombok](https://www.baeldung.com/intro-to-project-lombok)有助于减少Java在源代码中重复任务的冗长程度。在本教程中，我们将解释如何通过 在 Lombok 中声明局部val和var变量来推断类型。

## 2. 在 Lombok 中声明val和var变量

Lombok 提供了避免样板代码的智能功能。例如，它从领域模型对象中隐藏了[getter 和 setter 。](https://www.baeldung.com/intro-to-project-lombok#constructors)[Builder](https://www.baeldung.com/lombok-builder)注解是另一个有趣的特性，它有助于正确地实现 Builder[模式](https://www.baeldung.com/creational-design-patterns#builder)。

在接下来的部分中，我们将重点介绍Lombok 特性来定义局部变量而不指定类型。我们将使用 Lombok val和var类型来声明变量并避免源代码中出现额外的行。

[val](https://projectlombok.org/features/val) 是在 0.10 版本中引入的。使用val时，Lombok 将变量声明为final并在初始化后自动推断类型。因此，初始化表达式是强制性的。

[var](https://projectlombok.org/features/var)是在 1.16.20 版本中引入的。与 val一样，它也从初始化表达式中推断出类型，最大的不同是变量没有声明为final。因此，允许进一步赋值，但它们应符合声明变量时指定的类型。

## 3、在Lombok中实现 val和var的例子

### 3.1. 依赖关系

为了实现示例，我们只需将[Lombok](https://search.maven.org/classic/#search|ga|1|g%3A"org.projectlombok")依赖项添加到我们的 pom.xml中：

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.20</version>
    <scope>provided</scope>
</dependency>
```

[我们可以在此处](https://projectlombok.org/changelog)检查最新的可用版本。

### 3.2. val变量声明

首先，我们将从 Lombok导入val类型：

```java
import lombok.val;
```

其次，我们将使用val声明不同的局部变量。例如，我们可以从一个简单的String开始：

```java
public Class name() {
    val name = "name";
    System.out.println("Name: " + name);
    return name.getClass();
}
```

Lombok 自动生成以下原始 Java：

```java
final java.lang.String name = "name";
```

然后，让我们创建一个Integer：

```java
public Class age() {
    val age = Integer.valueOf(30);
    System.out.println("Age: " + age);
    return age.getClass();
}
```

正如我们所见，Lombok 生成了正确的类型：

```java
final java.lang.Integer age = Integer.valueOf(30);
```

我们还可以声明一个 List：

```java
public Class listOf() {
    val agenda = new ArrayList<String>();
    agenda.add("Day 1");
    System.out.println("Agenda: " + agenda);
    return agenda.getClass();
}
```

Lombok 不仅会推断List，还会推断其中的类型：

```java
final java.util.ArrayList<java.lang.String> agenda = new ArrayList<String>();
```

现在，让我们创建一个地图：

```java
public Class mapOf() {
    val books = new HashMap<Integer, String>();
    books.put(1, "Book 1");
    books.put(2, "Book 2");
    System.out.println("Books:");
    for (val entry : books.entrySet()) {
        System.out.printf("- %d. %sn", entry.getKey(), entry.getValue());
    }
    return books.getClass();
}
```

同样，推断出正确的类型：

```java
final java.util.HashMap<java.lang.Integer, java.lang.String> books = new HashMap<Integer, String>();
// ...
for (final java.util.Map.Entry<java.lang.Integer, java.lang.String> entry : books.entrySet()) {
   // ...
}
```

我们可以看到 Lombok 将适当的类型声明为final。因此，如果我们尝试修改名称，由于val的最终性质，构建将失败：

```java
name = "newName";

[12,9] cannot assign a value to final variable name
```

接下来，我们将运行一些测试来验证 Lombok 是否生成了正确的类型：

```java
ValExample val = new ValExample();
assertThat(val.name()).isEqualTo(String.class);
assertThat(val.age()).isEqualTo(Integer.class);
assertThat(val.listOf()).isEqualTo(ArrayList.class);
assertThat(val.mapOf()).isEqualTo(HashMap.class);
```

最后，我们可以在控制台输出特定类型的对象：

```bash
Name: name
Age: 30
Agenda: [Day 1]
Books:
- 1. Book 1
- 2. Book 2
```

### 3.3. var 变量声明

var声明与val非常相似，但变量不是final的特殊性：

```java
import lombok.var;

var name = "name";
name = "newName";

var age = Integer.valueOf(30);
age = 35;

var agenda = new ArrayList<String>();
agenda.add("Day 1");
agenda = new ArrayList<String>(Arrays.asList("Day 2"));

var books = new HashMap<Integer, String>();
books.put(1, "Book 1");
books.put(2, "Book 2");
books = new HashMap<Integer, String>();
books.put(3, "Book 3");
books.put(4, "Book 4");
```

让我们看一下生成的原始 Java：

```java
var name = "name";

var age = Integer.valueOf(30);

var agenda = new ArrayList<String>();

var books = new HashMap<Integer, String>();
```

这是因为Java10 支持var[声明](https://www.baeldung.com/java-10-local-variable-type-inference) 以使用初始化表达式推断局部变量的类型。但是，我们在使用它时需要考虑一些[限制。](https://www.baeldung.com/java-10-local-variable-type-inference#illegal-use-of-var)

由于声明的变量不是final，我们可以做进一步的赋值。尽管如此，对象必须符合从初始化表达式中推断出的适当类型。

如果我们尝试分配一个不同的类型，我们会在编译过程中得到一个错误：

```java
books = new ArrayList<String>();

[37,17] incompatible types: java.util.ArrayList<java.lang.String> cannot be converted to java.util.HashMap<java.lang.Integer,java.lang.String>
```

让我们稍微更改测试并检查新分配：

```java
VarExample varExample = new VarExample();
assertThat(varExample.name()).isEqualTo("newName");
assertThat(varExample.age()).isEqualTo(35);
assertThat("Day 2").isIn(varExample.listOf());
assertThat(varExample.mapOf()).containsValue("Book 3");
```

最后，控制台输出也与上一节不同：

```bash
Name: newName
Age: 35
Agenda: [Day 2]
Books:
- 3. Book 3
- 4. Book 4
```

## 4.复合类型

在某些情况下，我们需要使用复合类型作为初始化表达式：

```java
val compound = isArray ? new ArrayList<String>() : new HashSet<String>();
```

在上面的代码片段中，赋值取决于布尔值， 并推断出最常见的超类。

Lombok 将 AbstractCollection指定为原始代码所示的类型：

```java
final java.util.AbstractCollection<java.lang.String> compound = isArray ? new ArrayList<String>() : new HashSet<String>();
```

在不明确的情况下，例如空值，类Object被推断出来。

## 5.配置键

Lombok 允许在整个项目的一个文件中[配置功能。](https://projectlombok.org/features/configuration)因此，可以在一个地方包含项目的指令和设置。

有时，作为在我们的项目中强制执行开发标准的一部分，我们可能希望限制 Lombok 的var和val的使用。而且，如果有人无意中使用了它们，我们可能希望在编译期间生成警告。

对于这些情况， 我们可以通过在lombok.config文件中包含以下内容来将var或val的任何使用标记为警告或错误：

```bash
lombok.var.flagUsage = error
lombok.val.flagUsage = warning
```

我们将收到有关在整个项目中非法使用var的错误消息：

```bash
[12,13] Use of var is flagged according to lombok configuration.
```

同样，我们会收到有关使用val的警告消息：

```bash
ValExample.java:18: warning: Use of val is flagged according to lombok configuration.
val age = Integer.valueOf(30);

```

## 六. 总结

在本文中，我们展示了如何使用 Lombok 定义局部变量而不指定类型。此外，我们了解了声明val和var 变量的复杂性。

我们还演示了局部变量的泛型声明如何与复合类型一起工作。