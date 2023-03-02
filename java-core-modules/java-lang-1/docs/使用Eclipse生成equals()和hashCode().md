## 1. 概述

在本文中，我们探索使用EclipseIDE生成equals()和hashCode()方法。我们将说明Eclipse的代码自动生成是多么强大和方便，并强调仍然需要对代码进行勤奋的测试。

## 2. 规则

Java中的equals()用于检查2个对象是否相等。测试这一点的一个好方法是确保对象是对称的、自反的和可传递的。也就是说，对于三个非空对象a、b和c：

- 对称：a.equals(b)当且仅当b.equals(a)
- 自反：a.equals(a)
- 传递性：如果a.equals(b)和b.equals(c)那么a.equals(c)

hashCode()必须遵守一条规则：

- 2个equals()对象必须具有相同的hashCode()值

## 3. 使用基元类

让我们考虑一个仅由原始成员变量组成的Java类：

```java
public class PrimitiveClass {

    private boolean primitiveBoolean;
    private int primitiveInt;

    // constructor, getters and setters
}
```

我们使用EclipseIDE使用“Source->Generate hashCode() and equals()”来生成equals()和hashCode()。Eclipse提供了这样一个对话框：

[![eclipse-等于-hascode](https://www.baeldung.com/wp-content/uploads/2016/10/eclipse-equals-hascode.png)](https://www.baeldung.com/wp-content/uploads/2016/10/eclipse-equals-hascode.png)

我们可以通过选择“全选”来确保包含所有成员变量。

请注意，插入点下方列出的选项：影响生成代码的样式。在这里，我们不选择任何这些选项，选择“确定”并将方法添加到我们的类中：

```java
@Override
public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime  result + (primitiveBoolean ? 1231 : 1237);
    result = prime  result + primitiveInt;
    return result;
}

@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PrimitiveClass other = (PrimitiveClass) obj;
    if (primitiveBoolean != other.primitiveBoolean) return false;
    if (primitiveInt != other.primitiveInt) return false;
    return true;
}
```

生成的hashCode()方法以质数(31)的声明开始，对原始对象执行各种操作并根据对象的状态返回其结果。

equals()首先检查两个对象是否是同一个实例(==)，如果是则返回true。

接下来，它检查比较对象是否为非null以及两个对象是否属于同一类，如果不是则返回false。

最后，equals()检查每个成员变量是否相等，如果其中任何一个不相等则返回false。

所以我们可以编写简单的测试：

```java
PrimitiveClass aObject = new PrimitiveClass(false, 2);
PrimitiveClass bObject = new PrimitiveClass(false, 2);
PrimitiveClass dObject = new PrimitiveClass(true, 2);

assertTrue(aObject.equals(bObject) && bObject.equals(aObject));
assertTrue(aObject.hashCode() == bObject.hashCode());

assertFalse(aObject.equals(dObject));
assertFalse(aObject.hashCode() == dObject.hashCode());
```

## 4. 类与集合和泛型

现在，让我们考虑一个更复杂的带有集合和泛型的Java类：

```java
public class ComplexClass {

    private List<?> genericList;
    private Set<Integer> integerSet;

    // constructor, getters and setters
}
```

我们再次使用Eclipse 'Source->Generate hashCode() and equals()'。请注意hashCode()使用instanceOf来比较类对象，因为我们在对话框的Eclipse选项中选择了“使用‘instanceof’来比较类型”。我们得到：

```java
@Override
public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime  result + ((genericList == null)
      ? 0 : genericList.hashCode());
    result = prime  result + ((integerSet == null)
      ? 0 : integerSet.hashCode());
    return result;
}

@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ComplexClass)) return false;
    ComplexClass other = (ComplexClass) obj;
    if (genericList == null) {
        if (other.genericList != null)
            return false;
    } else if (!genericList.equals(other.genericList))
        return false;
    if (integerSet == null) {
        if (other.integerSet != null)
            return false;
    } else if (!integerSet.equals(other.integerSet))
        return false;
    return true;
}
```

生成的hashCode()方法依赖于AbstractList.hashCode()和AbstractSet.hashCode()核心Java方法。这些遍历集合，对每个项目的hashCode()值求和并返回结果。

类似地，生成的equals()方法使用AbstractList.equals()和AbstractSet.equals()，它们通过比较集合的字段来比较集合的相等性。

我们可以通过测试一些例子来验证健壮性：

```java
ArrayList<String> strArrayList = new ArrayList<String>();
strArrayList.add("abc");
strArrayList.add("def");
ComplexClass aObject = new ComplexClass(strArrayList, new HashSet<Integer>(45,67));
ComplexClass bObject = new ComplexClass(strArrayList, new HashSet<Integer>(45,67));
        
ArrayList<String> strArrayListD = new ArrayList<String>();
strArrayListD.add("lmn");
strArrayListD.add("pqr");
ComplexClass dObject = new ComplexClass(strArrayListD, new HashSet<Integer>(45,67));
        
assertTrue(aObject.equals(bObject) && bObject.equals(aObject));
assertTrue(aObject.hashCode() == bObject.hashCode());

assertFalse(aObject.equals(dObject));
assertFalse(aObject.hashCode() == dObject.hashCode());
```

## 5. 继承

让我们考虑一下使用继承的Java类：

```java
public abstract class Shape {
    public abstract double area();

    public abstract double perimeter();
}

public class Rectangle extends Shape {
    private double width;
    private double length;
   
    @Override
    public double area() {
        return width * length;
    }

    @Override
    public double perimeter() {
        return 2 * (width + length);
    }
    // constructor, getters and setters
}

public class Square extends Rectangle {
    Color color;
    // constructor, getters and setters
}
```

如果我们尝试在Square类上执行“Source->Generate hashCode() and equals()”，Eclipse会警告我们“超类‘Rectangle’不会重新声明equals()和hashCode()：生成的代码可能无法正常运行'。

同样，当我们尝试在Rectangle类上生成hashCode()和equals()时，我们会收到有关超类“Shape”的警告。

Eclipse将允许我们不顾警告继续前进。对于Rectangle，它扩展了一个抽象的Shape类，该类不能实现hashCode()或equals()，因为它没有具体的成员变量。对于这种情况，我们可以忽略Eclipse。

然而，Square类继承了Rectangle的宽度和长度成员变量，以及它自己的颜色变量。在Square中创建hashCode()和equals()而不首先对Rectangle执行相同的操作意味着在equals()/hashCode()中仅使用颜色：

```java
@Override
public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime  result + ((color == null) ? 0 : color.hashCode());
    return result;
}
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Square other = (Square) obj;
    if (color == null) {
        if (other.color != null)
            return false;
    } else if (!color.equals(other.color))
        return false;
    return true;
}
```

快速测试告诉我们，如果只是宽度不同，则Square的equals()/hashCode()是不够的，因为宽度不包括在equals()/hashCode()计算中：

```java
Square aObject = new Square(10, Color.BLUE);     
Square dObject = new Square(20, Color.BLUE);

Assert.assertFalse(aObject.equals(dObject));
Assert.assertFalse(aObject.hashCode() == dObject.hashCode());
```

让我们通过使用Eclipse为Rectangle类生成equals()/hashCode()来解决这个问题：

```java
@Override
public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(length);
    result = prime  result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(width);
    result = prime  result + (int) (temp ^ (temp >>> 32));
    return result;
}

@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Rectangle other = (Rectangle) obj;
    if (Double.doubleToLongBits(length)
      != Double.doubleToLongBits(other.length)) return false;
    if (Double.doubleToLongBits(width)
      != Double.doubleToLongBits(other.width)) return false;
    return true;
}
```

我们必须在Square类中重新生成equals()/hashCode()，因此会调用Rectangle的equals()/hashCode()。在这一代代码中，我们选择了Eclipse对话框中的所有选项，因此我们看到了注解、instanceOf比较和if块：

```java
@Override
public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime  result + ((color == null) ? 0 : color.hashCode());
    return result;
}


@Override
public boolean equals(Object obj) {
    if (this == obj) {
        return true;
    }
    if (!super.equals(obj)) {
        return false;
    }
    if (!(obj instanceof Square)) {
        return false;
    }
    Square other = (Square) obj;
    if (color == null) {
        if (other.color != null) {
            return false;
       }
    } else if (!color.equals(other.color)) {
        return false;
    }
    return true;
}
```

从上面重新运行我们的测试，我们现在通过了，因为Square的hashCode()/equals()计算正确。

## 6. 总结

Eclipse IDE非常强大，允许自动生成样板代码-getter/setter、各种类型的构造函数、equals()和hashCode()。

通过了解Eclipse正在做什么，我们可以减少花在这些编码任务上的时间。但是，我们仍然必须谨慎使用并通过测试来验证我们的代码，以确保我们已经处理了所有预期的情况。