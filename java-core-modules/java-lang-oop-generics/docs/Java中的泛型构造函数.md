## 1. 概述

我们之前讨论了[Java泛型](https://www.baeldung.com/java-generics)的基础知识。在本教程中，我们将了解Java中的泛型构造函数。

**泛型构造函数是至少具有一个泛型类型参数的构造函数**。

我们将看到泛型构造函数不必位于泛型类中，并且并非泛型类中的所有构造函数都必须是泛型的。

## 2. 非泛型类

首先，我们有一个简单的类Entry，它不是一个泛型类：

```java
public class Entry {
    private String data;
    private int rank;
}
```

在这个类中，我们将添加两个构造函数：一个带有两个参数的基本构造函数和一个泛型构造函数。

### 2.1 基本构造函数

第一个Entry构造函数是一个带有两个参数的简单构造函数：

```java
public Entry(String data, int rank) {
    this.data = data;
    this.rank = rank;
}
```

现在，让我们使用这个基本构造函数来创建一个Entry对象：

```java
@Test
public void givenNonGenericConstructor_whenCreateNonGenericEntry_thenOK() {
    Entry entry = new Entry("sample", 1);
    
    assertEquals("sample", entry.getData());
    assertEquals(1, entry.getRank());
}
```

### 2.2 泛型构造函数

接下来，我们的第二个构造函数是一个泛型构造函数：

```java
public <E extends Rankable & Serializable> Entry(E element) {
    this.data = element.toString();
    this.rank = element.getRank();
}
```

**尽管Entry类不是泛型的，但它有一个泛型的构造函数，因为它有一个E类型的参数元素**。

泛型类型E是有界的，应该同时实现Rankable和Serializable接口。

现在，让我们看一下Rankable接口，它有一个方法：

```java
public interface Rankable {
    int getRank();
}
```

并且，假设我们有一个类Product实现了Rankable接口：

```java
public class Product implements Rankable, Serializable {
    private String name;
    private double price;
    private int sales;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public int getRank() {
        return sales;
    }
}
```

然后我们可以使用泛型构造函数使用Product创建Entry对象：

```java
@Test
public void givenGenericConstructor_whenCreateNonGenericEntry_thenOK() {
    Product product = new Product("milk", 2.5);
    product.setSales(30);
 
    Entry entry = new Entry(product);
    
    assertEquals(product.toString(), entry.getData());
    assertEquals(30, entry.getRank());
}
```

## 3. 泛型类

接下来，我们将看看一个名为GenericEntry的泛型类：

```java
public class GenericEntry<T> {
    private T data;
    private int rank;
}
```

我们还将在此类中添加与上一节相同的两种类型的构造函数。

### 3.1 基本构造函数

首先，让我们为我们的GenericEntry类编写一个简单的非泛型构造函数：

```java
public GenericEntry(int rank) {
    this.rank = rank;
}
```

**尽管GenericEntry是一个泛型类，但这是一个没有泛型类型参数的简单构造函数**。

现在，我们可以使用这个构造函数来创建一个GenericEntry<String\>：

```java
@Test
public void givenNonGenericConstructor_whenCreateGenericEntry_thenOK() {
    GenericEntry<String> entry = new GenericEntry<String>(1);
    
    assertNull(entry.getData());
    assertEquals(1, entry.getRank());
}
```

### 3.2 泛型构造函数

接下来，让我们将第二个构造函数添加到我们的类中：

```java
public GenericEntry(T data, int rank) {
    this.data = data;
    this.rank = rank;
}
```

**这是一个泛型构造函数，因为它有一个泛型类型T的数据参数**。请注意，我们不需要在构造函数声明中添加<T\>，因为它隐式存在于此。

现在，让我们测试我们的泛型构造函数：

```java
@Test
public void givenGenericConstructor_whenCreateGenericEntry_thenOK() {
    GenericEntry<String> entry = new GenericEntry<String>("sample", 1);
    
    assertEquals("sample", entry.getData());
    assertEquals(1, entry.getRank());        
}
```

## 4. 不同类型的泛型构造函数

在我们的泛型类中，我们还可以有一个构造函数，其泛型类型不同于类的泛型类型：

```java
public <E extends Rankable & Serializable> GenericEntry(E element) {
    this.data = (T) element;
    this.rank = element.getRank();
}
```

此GenericEntry构造函数具有类型为E的参数元素，该类型不同于T类型。让我们看看它的实际效果：

```java
@Test
public void givenGenericConstructorWithDifferentType_whenCreateGenericEntry_thenOK() {
    Product product = new Product("milk", 2.5);
    product.setSales(30);
 
    GenericEntry<Serializable> entry = new GenericEntry<Serializable>(product);

    assertEquals(product, entry.getData());
    assertEquals(30, entry.getRank());
}
```

注意：

-   在我们的示例中，我们使用Product(E)创建了一个Serializable(T)类型的GenericEntry
-   只有当类型E的参数可以转换为T时，我们才能使用此构造函数

## 5. 多种泛型

接下来，我们有具有两个泛型类型的泛型类MapEntry：

```java
public class MapEntry<K, V> {
    private K key;
    private V value;

    public MapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
```

**MapEntry有一个带有两个参数的泛型构造函数，每个参数都是不同的类型**。让我们在一个简单的单元测试中使用它：

```java
@Test
public void givenGenericConstructor_whenCreateGenericEntryWithTwoTypes_thenOK() {
    MapEntry<String,Integer> entry = new MapEntry<String,Integer>("sample", 1);
    
    assertEquals("sample", entry.getKey());
    assertEquals(1, entry.getValue().intValue());        
}
```

## 6. 通配符

最后，我们可以在泛型构造函数中使用通配符：

```java
public GenericEntry(Optional<? extends Rankable> optional) {
    if (optional.isPresent()) {
        this.data = (T) optional.get();
        this.rank = optional.get().getRank();
    }
}
```

在这里，我们在这个GenericEntry构造函数中使用通配符来绑定Optional类型：

```java
@Test
public void givenGenericConstructorWithWildCard_whenCreateGenericEntry_thenOK() {
    Product product = new Product("milk", 2.5);
    product.setSales(30);
    Optional<Product> optional = Optional.of(product);
 
    GenericEntry<Serializable> entry = new GenericEntry<Serializable>(optional);
    
    assertEquals(product, entry.getData());
    assertEquals(30, entry.getRank());
}
```

请注意，我们应该能够将Optional参数类型(在我们的例子中为Product)转换为GenericEntry类型(在我们的例子中为Serializable)。

## 7. 总结

在本文中，我们学习了如何在泛型和非泛型类中定义和使用泛型构造函数。