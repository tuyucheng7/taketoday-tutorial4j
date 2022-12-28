## 1. 概述

在这个快速教程中，我们将阐明Kotlin编程语言中类和[对象](https://www.baeldung.com/kotlin-objects)之间的区别。

在此过程中，我们将深入挖掘并了解每个抽象是如何在字节码级别表示的。

## 2. 类表示

**Kotlin中的类在概念上类似于Java中的类**：它们是创建实例的蓝图：

```kotlin
class Person {
    // omitted
}
```

在上面的例子中，Kotlin编译器会在字节码级别生成一个典型的类定义：

```bash
>> kotlinc Person.kt
>> javap -c -p -v cn.tuyucheng.taketoday.classobject.Person
public final class cn.tuyucheng.taketoday.classobject.Person
  // omitted
  flags: (0x0031) ACC_PUBLIC, ACC_FINAL, ACC_SUPER
  this_class: #2        // cn/tuyucheng/taketoday/classobject/Person
  super_class: #4       // java/lang/Object
  interfaces: 0, fields: 0, methods: 1, attributes: 2
```

如上所示，Kotlin类的编译方式与Java类一样，但存在一些细微差别，例如[默认情况下是final](../../kotlin-core-2/docs/Kotlin中的open关键字.md)。

与Java类似，可以从具体(非抽象)的Kotlin类创建实例或对象：

```kotlin
val p = Person()
```

此外，我们可以从普通类继承，只要它对扩展开放：

```kotlin
open class Person {
    // omitted
}

class User : Person() {
    // omitted
}
```

## 3. 单例对象

在某些用例中，只有某些抽象的一个实例(也称为单例)可能会派上用场，**在Kotlin中，我们可以使用object声明来创建这样的单例**：

```kotlin
object FileSystem {
    
    fun createTempFile() {
        // omitted
    }
}
```

由于FileSystem在这里是一个单例，我们可以将其成员视为Java中的静态成员。简而言之，**我们不需要实例化FileSystem来访问它的成员**：

```kotlin
fun main() {
    FileSystem.createTempFile()
}
```

与简单的类相反，我们不需要创建单例实例。事实上，我们甚至不能创建这样的实例：

```kotlin
val f = FileSystem()
```

在这里，Kotlin编译器阻止我们从单例对象创建实例。

**尽管单例对象本身可以扩展其他类或接口，但它们也不能被继承**。更具体地说，我们可以这样写：

```kotlin
object FileSystem : Serializable {
    // omitted
}
```

但是，以下内容是不可能的，并且无法编译：

```kotlin
class NTFS : FileSystem
```

### 3.1 字节码表示

在幕后，**Kotlin编译器将FileSystem单例对象转换为具有私有构造函数的最终类**：

```bash
>> kotlinc FileSystem.kt 
>> javap -c -p cn.tuyucheng.taketoday.classobject.FileSystem
public final class cn.tuyucheng.taketoday.classobject.FileSystem {
  private cn.tuyucheng.taketoday.classobject.FileSystem();
    Code:
       0: aload_0
       1: invokespecial #11    // Method java/lang/Object."<init>":()V
       4: return
}
```

因此，没有其他人可以实例化它，也没有人可以继承它。

此外，它使用静态变量来保存单例实例，类似于[Java中的传统方法](https://www.baeldung.com/java-singleton)：

```bash
public final class cn.tuyucheng.taketoday.classobject.FileSystem {
  public static final cn.tuyucheng.taketoday.classobject.FileSystem INSTANCE;

  // omitted
}
```

为了初始化这个变量，Kotlin编译器省略了一个静态初始化器：

```bash
static {};
    Code:
       0: new           #2    // class cn/tuyucheng/taketoday/classobject/FileSystem
       3: dup
       4: invokespecial #26   // Method "<init>":()V
       7: astore_0
       8: aload_0
       9: putstatic     #28   // Field INSTANCE:Lcn/tuyucheng/taketoday/classobject/FileSystem;
      12: return
```

如上所示，它首先创建一个FileSystem实例，然后将其分配给静态单例持有者。

## 4. 总结

在这个简短的教程中，我们看到了Kotlin中类和对象在语言和字节码级别的不同之处，总结一下：

-   我们可以从具体类实例化，而单例对象不能
-   我们可以从开放类继承，而单例对象不能