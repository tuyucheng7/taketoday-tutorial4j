## 1. 简介

[Lombok](https://www.baeldung.com/intro-to-project-lombok)是一个库，可以帮助我们在编写Java应用程序时显着减少样板代码。

在本教程中，我们将了解如何使用此库制作只对单个属性进行更改的不可变对象的副本。

## 2.用法

当使用不可变对象时，根据设计不允许设置器，我们可能需要一个与当前对象相似的对象，但只有一个属性不同。这可以使用 Lombok 的@With注解来实现：

```java
public class User {
    private final String username;
    private final String emailAddress;
    @With
    private final boolean isAuthenticated;

    //getters, constructors
}
```

上面的注解在幕后生成以下内容：

```java
public class User {
    private final String username;
    private final String emailAddress;
    private final boolean isAuthenticated;

    //getters, constructors

    public User withAuthenticated(boolean isAuthenticated) {
        return this.isAuthenticated == isAuthenticated ? this : new User(this.username, this.emailAddress, isAuthenticated);
    }
}
```

然后我们可以使用上面生成的方法来创建原始对象的变异副本：

```java
User immutableUser = new User("testuser", "test@mail.com", false);
User authenticatedUser = immutableUser.withAuthenticated(true);

assertNotSame(immutableUser, authenticatedUser);
assertFalse(immutableUser.isAuthenticated());
assertTrue(authenticatedUser.isAuthenticated());
```

此外，我们可以选择注解整个类，这将为所有属性生成withX() 方法。

## 三、要求

要正确使用@With注解，我们需要提供一个全参数构造函数。正如我们从上面的示例中看到的，生成的方法需要 this 来创建原始对象的克隆。

我们可以使用 Lombok 自己的@AllArgsConstructor或@Value注解来满足这个要求。或者，我们也可以手动提供此构造函数，同时确保类中非静态属性的顺序与构造函数的顺序相匹配。

我们应该记住，如果在静态字段上使用@With注解，它什么也不做。这是因为静态属性不被视为对象状态的一部分。此外，Lombok 会跳过以$符号开头的字段的方法生成。

## 4.高级用法

让我们研究一下使用此注解时的一些高级场景。

### 4.1. 抽象类

我们可以在抽象类的字段上使用@With注解：

```java
public abstract class Device {
    private final String serial;
    @With
    private final boolean isInspected;

    //getters, constructor
}
```

但是，我们需要为生成的withInspected() 方法提供一个实现。这是因为 Lombok 不知道我们的抽象类的具体实现来创建它的克隆：

```java
public class KioskDevice extends Device {

    @Override
    public Device withInspected(boolean isInspected) {
        return new KioskDevice(getSerial(), isInspected);
    }

    //getters, constructor
}
```

### 4.2. 命名约定

如上所述，Lombok 将跳过以$符号开头的字段。但是，如果字段以字符开头，那么它是首字母大写的，最后，with是生成方法的前缀。

或者，如果字段以下划线开头，则with只是简单地作为生成方法的前缀：

```java
public class Holder {
    @With
    private String variableA;
    @With
    private String _variableB;
    @With
    private String $variableC;

    //getters, constructor excluding $variableC
}
```

根据上面的代码，我们看到只有前两个变量 会为它们生成withX() 方法：

```java
Holder value = new Holder("a", "b");

Holder valueModifiedA = value.withVariableA("mod-a");
Holder valueModifiedB = value.with_variableB("mod-b");
// Holder valueModifiedC = value.with$VariableC("mod-c"); not possible
```

### 4.3. 方法生成的例外

我们应该注意，除了以$符号开头的字段之外，如果我们的类中已经存在， Lombok 将不会生成withX() 方法：

```java
public class Stock {
    @With
    private String sku;
    @With
    private int stockCount;

    //prevents another withSku() method from being generated
    public Stock withSku(String sku) {
        return new Stock("mod-" + sku, stockCount);
    }

    //constructor
}
```

在上面的场景中，不会生成新的withSku()方法。

此外，Lombok在以下情况下会跳过 方法生成：

```java
public class Stock {
    @With
    private String sku;
    private int stockCount;

    //also prevents another withSku() method from being generated
    public Stock withSKU(String... sku) {
        return sku == null || sku.length == 0 ?
          new Stock("unknown", stockCount) :
          new Stock("mod-" + sku[0], stockCount);
    }

    //constructor
}
```

我们可以注意到上面withSKU()方法的不同命名。

基本上，如果出现以下情况，Lombok 将跳过方法生成：

-   与生成的方法名存在相同的方法(忽略大小写)
-   现有方法与生成的方法具有相同数量的参数(包括 var-args)

### 4.4. 对生成的方法进行空验证

与其他 Lombok 注解类似，我们可以对使用@With注解生成的方法进行空检查：

```java
@With
@AllArgsConstructor
public class ImprovedUser {
    @NonNull
    private final String username;
    @NonNull
    private final String emailAddress;
}
```

Lombok 将为我们生成以下代码以及所需的空检查：

```java
public ImprovedUser withUsername(@NonNull String username) {
    if (username == null) {
        throw new NullPointerException("username is marked non-null but is null");
    } else {
        return this.username == username ? this : new ImprovedUser(username, this.emailAddress);
    }
}

public ImprovedUser withEmailAddress(@NonNull String emailAddress) {
    if (emailAddress == null) {
        throw new NullPointerException("emailAddress is marked non-null but is null");
    } else {
        return this.emailAddress == emailAddress ? this : new ImprovedUser(this.username, emailAddress);
    }
}
```

## 5.总结

在本文中，我们了解了如何使用 Lombok 的@With注解来生成具有单个字段更改的特定对象的克隆。

我们还了解了此方法生成的实际工作方式和时间，以及如何通过空检查等额外验证来增强它。