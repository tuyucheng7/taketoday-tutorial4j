## 1. 概述

Kotlin语言引入了数据类的概念，其主要目的是简单地保存数据，而无需使用Java中创建POJO所需的样板代码。简单地说，Kotlin的解决方案使我们能够避免编写getters、setters、equals和hashCode方法，因此它使模型类更清晰、更具可读性。

在这篇简短的文章中，我们将了解Kotlin中的数据类，并将它们与Java中的数据类进行比较。

## 2. Kotlin设置

要开始设置Kotlin项目，请查看我们的[Kotlin语言简介](../../kotlin-core-1/docs/Kotlin语言简介.md)教程。

## 3. Java中的数据类

如果我们想在Java中创建一个Task条目，我们需要编写大量样板代码：

```java
public class Task {
    private int id;
    private String description;
    private int priority;

    public Task(int id, String description, int priority) {
        this.id = id;
        this.description = description;
        this.priority = priority;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public float getPriority() {
        return priority;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = Integer.hashCode(this.id) * prime;
        result = prime * result + Integer.hashCode(this.priority);
        result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object var1) {
        if (this != var1) {
            if (var1 instanceof Task) {
                Task var2 = (Task) var1;
                if (this.id == var2.id
                      && Intrinsics.areEqual(this.description, var2.description)
                      && this.priority == var2.priority) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return "Task [id=" + id + ", description=" + description + ", priority=" + priority + "]";
    }
}
```

69行代码！在一个简单的类中只存储三个字段的代码是如此夸张。

## 4. Kotlin数据类

现在，让我们将具有相同功能的同一个Task类重新定义为Kotlin数据类：

```kotlin
data class Task(
    var id: Int,
    var description: String,
    var priority: Int
)
```

正如我们所看到的，这要容易得多，也更干净。Kotlin生成了一个好的模型类必须重载的基本函数，为我们提供了良好的toString()、hashCode()和equals()函数。Kotlin还以copy()函数和各种componentN()函数的形式提供了一些额外的功能，这些功能对于变量解构很重要。

### 4.1 用法

我们以与其他类相同的方式实例化一个数据类：

```kotlin
val task = Task(1001, "Replace Fuel Tank Filler Caps", 5)
```

现在，属性和函数可用：

```kotlin
println(task.id) // 1001
println(task.description) // Replace Fuel Tank Filler Caps
println(task.priority) // 5

task.priority = 4

println(task.toString())
```

### 4.2 copy函数

copy()函数是为我们创建的，以防我们需要复制一个对象，更改它的一些属性但保持其余属性不变：

```kotlin
val copyTask = task.copy(priority = 4)
println(copyTask.toString())
```

Java没有提供一种清晰的原生方式来复制/克隆对象，我们可以使用Clonable接口、SerializationUtils.clone()或克隆构造函数。

### 4.3 解构声明

解构声明允许我们将对象的属性视为单独的值，对于我们数据类中的每个属性，都会生成一个componentN()函数：

```kotlin
task.component1()
task.component2()
task.component3()
```

我们还可以从对象或直接从函数创建多个变量，重要的是要记住使用括号：

```kotlin
val (id, description, priority) = task

fun getTask() = movie
val (idf, descriptionf, priorityf) = getTask()
```

### 4.4 数据类限制

为了创建数据类，我们必须考虑以下条件：

-   类声明必须以data开头
-   它必须至少有一个构造函数参数
-   所有构造函数参数必须是vals或vars
-   数据类不能是开放的、密封的、抽象的或内部类
-   编译器无法使用数据类的父类来定义生成的copy()函数

如果生成的类需要有一个无参数构造函数，则必须指定所有属性的默认值：

```kotlin
data class Task(var id: Int = 1000, var description: String = "", var priority: Int = 0)
```

## 5. Java记录兼容性

从[Kotlin 1.5](https://kotlinlang.org/docs/whatsnew15.html#jvm-records-support)开始，**我们可以将Kotlin数据类编译为**[Java 14+记录](https://www.baeldung.com/java-record-keyword)。要做到这一点，我们所要做的就是使用[@JvmRecord注解](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-record/)来标注数据类：

```kotlin
@JvmRecord
data class Person(val firstName: String, val lastName: String)
```

为了编译它，我们可以使用kotlinc：

```bash
>> kotlinc -jvm-target 15 -Xjvm-enable-preview Person.kt
```

如果我们的目标是Java 15或更早版本，我们必须通过-Xjvm-enable-preview标志启用预览JVM版本。但是，从Java 16开始，记录是稳定的Java特性。因此，如果我们的目标是Java 16或更新版本，则不需要启用预览功能：

```bash
>> kotlinc -jvm-target 16 Person.kt
```

现在，如果我们看一下生成的字节码，我们会看到Person类扩展了[java.lang.Record](https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/lang/Record.html)类：

```bash
>> javap -c -p cn.tuyucheng.taketoday.dataclass.Person
Compiled from "Person.kt"
public final class cn.tuyucheng.taketoday.dataclass.Person extends java.lang.Record {
    // omitted
}
```

**由于Java记录是不可变的，因此我们不能对用@JvmRecord注解标注的数据类使用var声明**：

```kotlin
@JvmRecord // won't compile
data class Person(val firstName: String, var lastName: String)
```

在这里，编译器将失败并显示以下错误消息：

```bash
Constructor parameter of @JvmRecord class should be a val
```

此外，**这种类型的数据类不能扩展其他类，因为它已经扩展了Record超类**。

## 6. 总结

我们已经了解了Kotlin中的数据类、它们的用法和要求、所需样板代码数量的减少以及与Java中相同代码的比较。

要了解有关Kotlin的更多信息，请查看[Kotlin Java互操作性](../../kotlin-core-1/docs/Kotlin与Java的互操作性.md)和已经提到的[Kotlin语言简介](../../kotlin-core-1/docs/Kotlin语言简介.md)等文章。