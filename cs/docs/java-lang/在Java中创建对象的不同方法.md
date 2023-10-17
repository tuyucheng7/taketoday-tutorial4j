## 一、简介

Java 是一种面向对象编程 (OOP) 语言。这意味着**[Java 使用通常组织在类中的对象](https://www.baeldung.com/java-classes-objects)来对状态和行为进行建模。**

在本教程中，我们将了解一些创建对象的不同方法。

在我们的大多数示例中，我们将使用一个非常简单的*Rabbit*对象：

```java
public class Rabbit {

    String name = "";
        
    public Rabbit() {
    }
    
    // getters/setters 
}复制
```

我们的*Rabbit*不一定有*name*，尽管我们可以根据需要设置一个*名称。*

现在，让我们创建一些*Rabbit*对象！

## 2. 如何使用*new*运算符创建对象

**使用\*new\*关键字可能是创建对象最常用的方法：**

```java
Rabbit rabbit = new Rabbit();复制
```

在上面的示例中，我们将*Rabbit*的一个新实例分配给一个名为*rabbit*的变量。

new*关键字*表示我们想要一个对象的新实例。它通过使用该对象中的[构造函数类](https://www.baeldung.com/java-constructors)来实现这一点。

请注意，如果类中不存在构造函数，则将使用内部默认构造函数。

## *3. 如何使用Class.newInstance()*方法创建对象

由于 Java 是一种基于对象的语言，因此将 Java 的关键概念存储为对象是有意义的。

一个例子是*Class*对象，其中存储了有关 Java 类的所有信息。

**要访问\*Rabbit\*类对象，我们使用\*Class.forName()\*和限定的类名**（包含该类所在的包的名称）。

***一旦我们为我们的Rabbit\*获得了相应的类对象，我们将调用\*newInstance()\*方法，它创建***Rabbit*对象的一个新实例：

```java
Rabbit rabbit = (Rabbit) Class
  .forName("com.baeldung.objectcreation.objects.Rabbit")
  .newInstance();复制
```

请注意，我们必须将新对象实例转换为*Rabbit。*

一个稍微不同的版本使用*class*关键字而不是*Class*对象，后者更简洁：

```java
Rabbit rabbit = Rabbit.class.newInstance();复制
```

让我们也使用*Constructor*类来生成类似的替代方案：

```java
Rabbit rabbit = Rabbit.class.getConstructor().newInstance();复制
```

在所有这些情况下，我们都使用可用于任何对象的内置*newInstance()方法。*

**newInstance \*()\*方法依赖于可见的构造函数。**

例如，如果*Rabbit*只有私有构造函数，而我们尝试使用上面的*newInstance*方法之一，我们会看到 IllegalAccessException 的堆栈跟踪*：*

```xml
java.lang.IllegalAccessException: Class com.baeldung.objectcreation.CreateRabbits can not access 
  a member of class com.baeldung.objectcreation.objects.Rabbit with modifiers "private"
  at sun.reflect.Reflection.ensureMemberAccess(Reflection.java:102)
  at java.lang.Class.newInstance(Class.java:436)复制
```

## *4. 如何使用 c lone()*方法创建对象

现在让我们看看如何克隆一个对象。

**clone \*()\* 方法接受一个对象并生成它的精确副本并分配给新内存。**

但是，并非所有类都可以克隆。

**只有实现接口\*Clonable 的\* 类才能被克隆。**

该类还必须包含*clone()*方法的实现，所以让我们创建一个名为*CloneableRabbit 的类， 它与**Rabbit*相同，但也实现了*clone()*方法：

```java
public Object clone() throws CloneNotSupportedException {
    return super.clone();
}复制
```

然后，我们克隆 CloneableRabbit 的代码*是* ：

```java
ClonableRabbit clonedRabbit = (ClonableRabbit) originalRabbit.clone();复制
```

如果我们正在考虑使用*clone()* 方法，我们可能希望改用[Java 复制构造函数](https://www.baeldung.com/java-copy-constructor)。

## 5. 如何使用反序列化创建对象

我们已经涵盖了明显的例子，所以让我们开始跳出框框思考。

**我们可以通过反序列化**（读取外部数据，然后从中创建对象）来创建对象。

为了演示这一点，首先，我们需要一个可序列化的类。

**我们可以通过复制 \*Rabbit\*并实现\*Serializable\*接口来使我们的类可序列化：**

```java
public class SerializableRabbit implements Serializable {
    //class contents
}复制
```

然后我们要将一个名为 Peter 的*Rabbit*写入测试目录中的文件：

```java
SerializableRabbit originalRabbit = new SerializableRabbit();
originalRabbit.setName("Peter");

File resourcesFolder = new File("src/test/resources");
resourcesFolder.mkdirs(); //creates the directory in case it doesn't exist
        
File file = new File(resourcesFolder, "rabbit.ser");
        
try (FileOutputStream fileOutputStream = new FileOutputStream(file);
  ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);) {
    objectOutputStream.writeObject(originalRabbit);
}复制
```

最后，我们将再次读回它：

```java
try (FileInputStream fis = new FileInputStream(file);
  ObjectInputStream ois = new ObjectInputStream(fis);) {   
    return (SerializableRabbit) ois.readObject();
}复制
```

如果我们检查名称，我们会看到这个新创建的*Rabbit*对象是 Peter。

这整个概念本身就是一个大话题，称为[反序列化](https://www.baeldung.com/java-serialization)。

## 6. 其他一些创建对象的方法

如果我们深入一点，就会发现我们可以做很多事情来本质上创建对象。

下面是一些我们经常使用和从未使用过的熟悉的类。

### 6.1. 功能接口

我们可以使用[功能接口](https://www.baeldung.com/java-8-functional-interfaces)创建一个对象：

```java
Supplier<Rabbit> rabbitSupplier = Rabbit::new;
Rabbit rabbit = rabbitSupplier.get();复制
```

此代码使用[*Supplier*](https://www.baeldung.com/java-callable-vs-supplier#supplier)功能接口来提供*Rabbit*对象。

我们使用方法引用运算符，即*Rabbit::new*中的[双冒号运算符来实现这一点。](https://www.baeldung.com/java-8-double-colon-operator#1-create-a-new-instance)

双冒号运算符文章包含更多示例，例如如何处理构造函数中的参数。

### 6.2. *不安全的.AllocateInstance*

让我们快速提一下我们不建议在我们的代码中创建对象的方法。

**sun.misc.Unsafe类是核心 Java 类中使用的低级类，**这意味着它不是为在我们的代码中使用而设计的**[\*。\*](https://www.baeldung.com/java-unsafe)**

但是，它确实包含一个名为*allocateInstance 的方法，* 该方法[可以](https://www.baeldung.com/java-unsafe#instantiating-a-class-using-unsafe)在不调用构造函数的情况下创建对象。

由于**不建议在核心库之外使用*****Unsafe\***，因此我们未在此处包含示例。

### 6.3. 数组

在 Java 中创建对象的另一种方法是通过[初始化数组](https://www.baeldung.com/java-initialize-array)。

代码结构看起来类似于前面使用*new*关键字的示例：

```java
Rabbit[] rabbitArray = new Rabbit[10];复制
```

但是，在运行代码时，我们发现**它没有显式使用构造方法**。因此，虽然在外部看起来使用相同的代码风格，但幕后的内部机制是不同的。

### 6.4. 枚举

让我们再快速看一下另一个常见对象，一个[enum](https://www.baeldung.com/a-guide-to-java-enums)。

枚举只是一种特殊类型的类，我们以面向对象的方式来考虑它们。

所以如果我们有一个枚举：

```java
public enum RabbitType {
    PET,
    TAME,
    WILD
}复制
```

**每次我们初始化枚举时，代码都会创建对象**，这与上面在运行时创建对象的示例不同。

## 七、结论

在本文中，我们已经看到可以使用关键字（如*new* 或 *class）* 来创建对象。

我们已经了解到其他操作（例如克隆或反序列化）可以创建对象。

此外，我们已经看到 Java 拥有成熟的创建对象的方法，其中许多方法我们可能已经在使用，但从未考虑过。