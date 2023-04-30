## 1. 概述

从Spring 5开始，我们现在可以使用一个有趣的功能来帮助我们编写更安全的代码。
此功能称为null-safety，一组注解起到了保护作用，可以监视潜在的null引用。

null-safety特性并没有让我们摆脱不安全的代码，而是**在编译时产生警告**。此类警告可能会在运行时防止灾难性的空指针异常(NPE)。

## 2. @NonNull注解

@NonNull注解是null-safety特性的所有注解中最重要的注解。
**我们可以在任何需要对象引用的地方使用这个注解声明非空约束**：字段、方法参数或方法的返回值。

假设我们有一个名为Person的类：

```java
public class Person {
    private String fullName;

    void setFullName(String fullName) {
        if (fullName != null && fullName.isEmpty()) {
            fullName = null;
        }
        this.fullName = fullName;
    }
    // getter
}
```

这个类定义是有效的，但是有一个缺陷，fullName字段可能被设置为null。如果发生这种情况，我们在使用fullName时可能会遇到NPE。

Spring null-safety特性使工具能够报告这种危险。例如，如果我们在IntelliJ IDEA中编写代码并使用@NonNull注解标注fullName字段，
我们将看到一个警告：

<img src="../../../spring-modules/spring-core-2/assets/NonNull-1.png">

多亏这个提示，我们可以提前知道问题并能够采取适当的措施来避免运行时故障。

## 3. @NonNullFields注解

@NonNull注解有助于保证noll-safety安全。但是，如果使用此注解标注所有非空字段，我们的代码可能会变得非常臃肿。

为此我们可以通过另一个注解@NonNullFields来避免@NonNull的泛滥。
**此注解适用于包级别，通知我们的开发工具，注解包中的所有字段默认为非null**。

要启用@NonNullFields注解，我们需要在包的根目录中创建一个名为package-info.java的文件，并使用@NonNullFields标注该包：

```java
@NonNullFields
package cn.tuyucheng.taketoday.nullibility;
```

让我们在Person类中声明另一个属性，称为nickName：

```java
package cn.tuyucheng.taketoday.spring.nullibility;

public class Person {
    private String nickName;

    void setNickName(@Nullable String nickName) {
        if (nickName != null && nickName.isEmpty()) {
            nickName = null;
        }
        this.nickName = nickName;
    }
    // other declarations
}
```

这一次，我们没有用@NonNull修饰nickName字段，但仍然看到类似的警告：

<img src="../../../spring-modules/spring-core-2/assets/NonNull-2.png">

@NonNullFields注解使我们的代码不那么冗长，同时确保与@NonNull提供的安全级别相同。

## 4. @Nullable注解

@NonNullFields注解通常比@NonNull更可取，因为它有助于减少样板代码。有时我们希望从包级别指定的非空约束中免除某些字段。

让我们回到nickName字段并用@Nullable注解修饰它：

```
@Nullable
private String nickName;
```

我们以前看到的警告现在已经消失了：

<img src="../../../spring-modules/spring-core-2/assets/NonNull-3.png">

在这种情况下，**我们使用@Nullable注解来覆盖字段上@NonNullFields的语义**。

## 5. @NonNullApi注解

顾名思义，@NonNullFields注解仅适用于字段。**如果我们想对方法的参数和返回值产生同样的影响，我们需要@NonNullApi**。

与@NonNullFields一样，我们必须在package-info.java文件中指定@NonNullApi注解：

```java
@NonNullApi
@NonNullFields
package cn.tuyucheng.taketoday.spring.nullibility;
```

让我们为nickName字段定义一个getter方法：

```java
package cn.tuyucheng.taketoday.spring.nullibility;

public class Person {
    @Nullable
    private String nickName;

    String getNickName() {
        return nickName;
    }
    // other declarations
}
```

在@NonNullApi注解生效的情况下，会产生一条警告，提示getNickName()方法可能产生空值：

<img src="../../../spring-modules/spring-core-2/assets/NonNull-4.png">

请注意，就像@NonNullFields注解一样，我们可以在方法级别使用@Nullable注解覆盖@NonNullApi。

## 6. 总结

Spring null-safety是一个很棒的特性，它有助于减少NPE的可能性。但是，在使用此功能时，我们需要注意两点：

+ 它只能在支持的开发工具中使用，例如IntelliJ IDEA。
+ 它不会在运行时强制执行空检查，我们仍然需要自己编写代码来避免NPE。