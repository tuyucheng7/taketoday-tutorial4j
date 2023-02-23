## 1. 概述

在本教程中，我们将介绍Java中的访问修饰符，这些修饰符用于设置对类、变量、方法和构造函数的访问级别。

**简单地说，有四种访问修饰符**：public、private、protected和default(无关键字)。

在我们开始之前，让我们注意顶级类只能使用public或default访问修饰符。在成员级别，我们可以使用所有四个。

## 2. default

当我们没有显式使用任何关键字时，Java将设置对给定类、方法或属性的默认访问。默认访问修饰符也称为package-private，这意味着**所有成员在同一个包中都是可见的**，但不能从其他包访问：

```java
package cn.tuyucheng.taketoday.accessmodifiers;

public class SuperPublic {
    static void defaultMethod() {
        // ...
    }
}
```

defaultMethod()可以在同一包的另一个类中访问：

```java
package cn.tuyucheng.taketoday.accessmodifiers;

import cn.tuyucheng.taketoday.accessmodifiers.SuperPublic;

public class Public {
    public Public() {
        SuperPublic.defaultMethod(); // Available in the same package.
    }
}
```

但是，它在其他包中不可用。

## 3. public

如果我们将public关键字添加到一个类、方法或属性中，那么我们就**可以让整个世界都可以使用它**，即所有包中的所有其他类都可以使用它。这是限制最少的访问修饰符：

```java
package cn.tuyucheng.taketoday.accessmodifiers;

public class SuperPublic {
    public static void publicMethod() {
        // ...
    }
}
```

publicMethod()在另一个包中可用：

```java
package cn.tuyucheng.taketoday.accessmodifiers.another;

import accessmodifiers.cn.tuyucheng.taketoday.SuperPublic;

public class AnotherPublic {
    public AnotherPublic() {
        SuperPublic.publicMethod(); // Available everywhere. Let's note different package.
    }
}
```

有关public关键字在应用于类、接口、嵌套公共类或接口和方法时的行为方式的更多详细信息，请参阅[专门的文章](https://www.baeldung.com/java-public-keyword)。

## 4. private

任何带有private关键字的方法、属性或构造函数**只能从同一个类访问**。这是限制性最强的访问修饰符，也是封装概念的核心。所有数据都将对外界隐藏：

```java
package cn.tuyucheng.taketoday.accessmodifiers;

public class SuperPublic {
    static private void privateMethod() {
        // ...
    }

    private void anotherPrivateMethod() {
        privateMethod(); // available in the same class only.
    }
}
```

这篇更[详细的文章](https://www.baeldung.com/java-private-keyword)将展示private关键字在应用于字段、构造函数、方法和内部类时的行为方式。

## 5. protected

在public和private访问级别之间，存在protected访问修饰符。

如果我们使用protected关键字声明一个方法、属性或构造函数，**我们可以从同一个包(与package-private访问级别一样)访问该成员，此外还可以从其类的所有子类访问该成员**，即使它们位于其他包中：

```java
package cn.tuyucheng.taketoday.accessmodifiers;

public class SuperPublic {
    static protected void protectedMethod() {
        // ...
    }
}
```

protectedMethod()在子类中可用(与包无关)：

```java
package cn.tuyucheng.taketoday.accessmodifiers.another;

import accessmodifiers.cn.tuyucheng.taketoday.SuperPublic;

public class AnotherSubClass extends SuperPublic {
    public AnotherSubClass() {
        SuperPublic.protectedMethod(); // Available in subclass. Let's note different package.
    }
}
```

[专门](https://www.baeldung.com/java-protected-access-modifier)的文章详细介绍了关键字在字段、方法、构造函数、内部类中的使用情况以及在同一包或不同包中的可访问性。

## 6. 比较

下表总结了可用的访问修饰符。我们可以看到，无论使用何种访问修饰符，一个类始终可以访问其成员：

|  修饰符  | 类 | 包 | 子类 | 世界 |
| :------: | :--: | :--: | :--: | :--: |
|  public  |  是  |  是  |  是  |  是  |
| protected |  是  |  是  |  是  |  否  |
|  default  |  是  |  是  |  否  |  否  |
| private |  是  |  否  |  否  |  否  |

## 7. 总结

在这篇简短的文章中，我们回顾了Java中的访问修饰符。

最好对任何给定成员使用最严格的访问级别，以防止滥用。除非有充分的理由不这样做，否则我们应该始终使用private访问修饰符。

仅当成员是API的一部分时才应使用公共访问级别。