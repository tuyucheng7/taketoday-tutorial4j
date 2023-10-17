## 1. 概述

方法签名只是Java中整个方法定义的一个子集。因此，签名的确切结构可能会造成混淆。

在本教程中，我们将学习方法签名的元素及其在Java编程中的含义。

## 2. 方法签名

[Java中的方法](https://www.baeldung.com/java-methods)支持重载，这意味着可以在同一个类或类层次结构中定义多个同名方法。因此，编译器必须能够静态绑定客户端代码引用的方法。为此，**方法签名唯一标识每个方法**。

根据[Oracle](https://docs.oracle.com/javase/tutorial/java/javaOO/methods.html)的说法，**方法签名由名称和参数类型组成**。因此，方法声明的所有其他元素，如修饰符、返回类型、参数名称、异常列表和主体都不是签名的一部分。

让我们仔细看看[方法重载](https://www.baeldung.com/java-method-overload-override)及其与方法签名的关系。

## 3. 重载错误

让我们考虑以下代码：

```java
public void print() {
    System.out.println("Signature is: print()");
}

public void print(int parameter) {
    System.out.println("Signature is: print(int)");
}
```

如我们所见，代码编译为方法具有不同的参数类型列表。实际上，编译器可以确定性地将任何调用绑定到其中一个。

现在让我们通过添加以下方法来测试重载是否合法：

```java
public int print() { 
    System.out.println("Signature is: print()"); 
    return 0; 
}
```

当我们编译时，我们得到一个“method is already defined in class”的错误。这证明**方法返回类型不是方法签名的一部分**。

让我们尝试使用修饰符进行相同的操作：

```java
private final void print() { 
    System.out.println("Signature is: print()");
}
```

我们仍然看到相同的“method is already defined in class”错误。因此，**方法签名不依赖于修饰符**。

通过更改抛出的异常来测试重载可以通过添加以下内容来测试：

```java
public void print() throws IllegalStateException { 
    System.out.println("Signature is: print()");
    throw new IllegalStateException();
}
```

我们再次看到“method is already defined in class”错误，表明**throws声明不能成为签名的一部分**。

我们可以测试的最后一件事是更改参数名称是否允许重载。让我们添加以下方法：

```java
public void print(int anotherParameter) { 
    System.out.println("Signature is: print(int)");
}
```

正如预期的那样，我们得到了相同的编译错误。这意味着**参数名称不影响方法签名**。

## 4. 泛型和类型擦除

使用泛型参数时，**[类型擦除](https://www.baeldung.com/java-type-erasure)会更改有效签名**。实际上，它可能会导致与另一个使用泛型类型上限而不是泛型标记的方法发生冲突。

让我们考虑以下代码：

```java
public class OverloadingErrors<T extends Serializable> {

    public void printElement(T t) {
        System.out.println("Signature is: printElement(T)");
    }

    public void printElement(Serializable o) {
        System.out.println("Signature is: printElement(Serializable)");
    }
}
```

即使签名看起来不同，编译器也无法在类型擦除后静态绑定正确的方法。

由于类型擦除，我们可以看到编译器将T替换为上限Serializable。因此，它与显式使用Serializable的方法冲突。

当泛型类型没有边界时，我们会看到基类型Object的相同结果。

## 5. 参数列表和多态性

方法签名考虑了确切的类型。这意味着我们可以重载参数类型为子类或超类的方法。

但是，我们必须特别注意，因为**静态绑定会尝试使用多态性、自动装箱和类型提升进行匹配**。

让我们看一下下面的代码：

```java
public Number sum(Integer term1, Integer term2) {
    System.out.println("Adding integers");
    return term1 + term2;
}

public Number sum(Number term1, Number term2) {
    System.out.println("Adding numbers");
    return term1.doubleValue() + term2.doubleValue();
}

public Number sum(Object term1, Object term2) {
    System.out.println("Adding objects");
    return term1.hashCode() + term2.hashCode();
}
```

上面的代码是完全合法的，可以编译。但是调用这些方法时可能会出现混淆，因为我们不仅需要知道我们正在调用的确切方法签名，还需要知道Java如何基于实际值进行静态绑定。

让我们探索一些最终绑定到sum(Integer, Integer)的方法调用：

```java
StaticBinding obj = new StaticBinding(); 
obj.sum(Integer.valueOf(2), Integer.valueOf(3)); 
obj.sum(2, 3); 
obj.sum(2, 0x1);
```

对于第一次调用，我们有确切的参数类型Integer、Integer。在第二次调用时，Java会自动将int装箱为Integer。最后，Java会将字节值0x1通过类型提升的方式转换为int，然后将其自动装箱为Integer。 

类似地，我们有以下绑定到sum(Number, Number)的调用：

```java
obj.sum(2.0d, 3.0d);
obj.sum(Float.valueOf(2), Float.valueOf(3));
```

在第一次调用时，我们有自动装箱为Double的double值。然后，通过多态性，Double匹配Number。同样，Float与Number匹配第二次调用。

让我们观察一下Float和Double都继承自Number和Object的事实。但是，默认绑定是Number。这是因为Java会自动匹配最近的与方法签名匹配的超类型。

现在让我们考虑以下方法调用：

```java
obj.sum(2, "John");
```

在这个例子中，我们有一个int到Integer的自动装箱作为第一个参数。但是，此方法名称没有sum(Integer, String)重载。因此，Java将遍历所有参数超类型，从最近的父对象转换为Object，直到找到匹配项。在这种情况下，它绑定到sum(Object, Object)。 

**要更改默认绑定，我们可以使用显式参数转换**，如下所示：

```java
obj.sum((Object) 2, (Object) 3);
obj.sum((Number) 2, (Number) 3);
```

## 6. 可变参数

现在让我们将注意力转移到**[可变参数](https://www.baeldung.com/java-varargs)如何影响方法的有效签名和静态绑定**。

这里我们有一个使用可变参数的重载方法：

```java
public Number sum(Object term1, Object term2) {
    System.out.println("Adding objects");
    return term1.hashCode() + term2.hashCode();
}

public Number sum(Object term1, Object... term2) {
    System.out.println("Adding variable arguments: " + term2.length);
    int result = term1.hashCode();
    for (Object o : term2) {
        result += o.hashCode();
    }
    return result;
}
```

那么这些方法的有效签名是什么？我们已经看到sum(Object, Object)是第一个的签名。可变参数本质上是数组，因此编译后第二个的有效签名是sum(Object, Object[])。 

一个棘手的问题是，当我们只有两个参数时如何选择方法绑定？

让我们考虑以下调用：

```java
obj.sum(new Object(), new Object());
obj.sum(new Object(), new Object(), new Object());
obj.sum(new Object(), new Object[]{new Object()});
```

显然，第一个调用将绑定到sum(Object, Object)，第二个调用将绑定到sum(Object, Object[])。要强制Java使用两个对象调用第二个方法，我们必须像第三个调用一样将其包装在一个数组中。

这里要注意的最后一点是，声明以下方法将与可变参数版本冲突：

```java
public Number sum(Object term1, Object[] term2) {
    // ...
}
```

## 7. 总结

在本教程中，我们了解到方法签名由名称和参数类型列表组成。修饰符、返回类型、参数名称和异常列表无法区分重载方法，因此不是签名的一部分。

我们还研究了类型擦除和可变参数如何隐藏有效的方法签名，以及我们如何覆盖Java的静态方法绑定。