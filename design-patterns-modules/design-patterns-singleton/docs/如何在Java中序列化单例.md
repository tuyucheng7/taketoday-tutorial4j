## 1. 概述

在本快速教程中，我们介绍如何在Java中创建可序列化的单例类。

## 2. 什么是序列化

**[序列化]()是将Java对象的状态转换为可以存储在文件或数据库中的字节流的过程**：

<img src="../assets/img.png">

**反序列化则相反，它从字节流创建对象**：

<img src="../assets/img_1.png">

## 3. Serializable接口

**[Serializable](https://docs.oracle.com/javase/7/docs/api/java/io/Serializable.html)接口是一个[标记接口]()，标记接口为编译器和JVM提供有关对象的运行时类型信息**。它内部没有任何字段、方法或常量，因此实现它的类不必实现任何方法。

**如果一个类实现了Serializable接口，它的实例就可以被序列化或反序列化**。

## 4. 什么是单例类？

在面向对象编程中，[单例]()类是一次只能有一个实例的类。在第一次实例化之后，如果我们尝试再次实例化单例类，它会为我们提供与第一次创建的实例相同的实例。下面是一个实现了Serializable接口的单例类：

```java
public class Singleton implements Serializable {

    private static Singleton INSTANCE;
    private String state = "State Zero";

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Singleton();
        }

        return INSTANCE;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
```

我们可以看到它有两个私有字段：INSTANCE和state，INSTANCE是单例类的唯一实例，state是一个保存类状态的字符串变量。

## 5. 创建可序列化的单例类

问题是，在实例化一个实现Serializable的单例类之后，然后对该实例进行序列化和反序列化，我们最终会得到单例类的两个实例，这违反了单例性：

```java
@Test
void givenSingleton_whenSerializedAndDeserialized_thenTwoInstances() {
	Singleton s1 = Singleton.getInstance();
    s1.setState("State One");
    
	try (
		FileOutputStream fos = new FileOutputStream("singleton_test.txt");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		FileInputStream fis = new FileInputStream("singleton_test.txt");
		ObjectInputStream ois = new ObjectInputStream(fis)) {
        
		// Serializing.
		oos.writeObject(s1);
        
		// Deserializing.
		Singleton s2 = (Singleton) ois.readObject();
        
		// Checking if s1 and s2 are not the same instance.
		assertNotEquals(s1, s2);
	} catch (Exception e) {
		// ...
	}
}
```

以上测试代码通过。因此，即使在序列化和反序列化期间保留了状态，新变量s2也不会指向与s1相同的实例，所以Singleton类有两个实例，这并不好。

要创建一个可序列化的单例类，我们应该使用枚举单例模式：

```java
public enum EnumSingleton {

    INSTANCE("State Zero");

    private String state;

    private EnumSingleton(String state) {
        this.state = state;
    }

    public EnumSingleton getInstance() {
        return INSTANCE;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
```

现在让我们看看如果我们序列化和反序列化它会发生什么：

```java
@Test
public void givenEnumSingleton_whenSerializedAndDeserialized_thenStatePreserved() {
	EnumSingleton es1 = EnumSingleton.getInstance();
	es1.setState("State One");

	try (
		FileOutputStream fos = new FileOutputStream("enum_singleton_test.txt");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		FileInputStream fis = new FileInputStream("enum_singleton_test.txt");
		ObjectInputStream ois = new ObjectInputStream(fis)) {

		// Serializing.
		oos.writeObject(es1);

		// Deserializing.
		EnumSingleton es2 = (EnumSingleton) ois.readObject();

		// Checking if the state is preserved.
		assertEquals(es1.getState(), es2.getState());

		// Checking if es1 and es2 are pointing to the same instance in memory.
		assertEquals(es1, es2);
	} catch (Exception e) {
		// ...
	}
}
```

以上测试代码通过。因此，在序列化和反序列化之后状态被保留，并且两个变量es1和es2指向最初创建的同一个实例。

## 6. 总结

在本教程中，我们学习了如何在Java中创建可序列化的单例类。