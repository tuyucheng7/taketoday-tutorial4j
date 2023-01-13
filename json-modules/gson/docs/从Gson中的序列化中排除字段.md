## 1. 概述

在这个简短的教程中，我们将探讨从 Gson 序列化中排除Java类及其子类的一个或多个字段的可用选项。

## 2.初始设置

让我们首先定义我们的类：

```java
@Data
@AllArgsConstructor
public class MyClass {
    private long id;
    private String name;
    private String other;
    private MySubClass subclass;
}

@Data
@AllArgsConstructor
public class MySubClass {
    private long id;
    private String description;
    private String otherVerboseInfo;
}

```

为了方便起见，我们用[Lombok](https://www.baeldung.com/intro-to-project-lombok)对它们进行了注解(getter、setter、构造函数的语法糖……)。

现在让我们填充它们：

```java
MySubClass subclass = new MySubClass(42L, "the answer", "Verbose field not to serialize")
MyClass source = new MyClass(1L, "foo", "bar", subclass);

```

我们的目标是防止MyClass.other和MySubClass.otherVerboseInfo字段被序列化。

我们期望得到的输出是：

```javascript
{
  "id":1,
  "name":"foo",
  "subclass":{
    "id":42,
    "description":"the answer"
  }
}

```

在爪哇中：

```java
String expectedResult = "{"id":1,"name":"foo","subclass":{"id":42,"description":"the answer"}}";

```

## 3.瞬态修改器

我们可以用transient修饰符标记一个字段：

```java
public class MyClass {
    private long id;
    private String name;
    private transient String other;
    private MySubClass subclass;
}

public class MySubClass {
    private long id;
    private String description;
    private transient String otherVerboseInfo;
}

```

Gson 序列化器将忽略声明为瞬态的每个字段：

```java
String jsonString = new Gson().toJson(source);
assertEquals(expectedResult, jsonString);

```

虽然这非常快，但它也有一个严重的缺点：每个序列化工具都会考虑 transient，不仅仅是 Gson。

Transient 是从序列化中排除的Java方式，那么我们的字段也将被Serializable的序列化以及管理我们的对象的每个库工具或框架过滤掉。

此外，transient关键字始终适用于序列化和反序列化，这可能会受到限制，具体取决于用例。

## 4. @Expose注解

Gson com.google.gson.annotations [@Expose](https://static.javadoc.io/com.google.code.gson/gson/2.8.0/com/google/gson/annotations/Expose.html)注解以相反的方式工作。

我们可以用它来声明要序列化哪些字段，而忽略其他字段：

```java
public class MyClass {
    @Expose 
    private long id;
    @Expose 
    private String name;
    private String other;
    @Expose 
    private MySubClass subclass;
}

public class MySubClass {
    @Expose 
    private long id;
    @Expose 
    private String description;
    private String otherVerboseInfo;
}   

```

为此，我们需要使用 GsonBuilder 实例化 Gson：

```java
Gson gson = new GsonBuilder()
  .excludeFieldsWithoutExposeAnnotation()
  .create();
String jsonString = gson.toJson(source);
assertEquals(expectedResult, jsonString);

```

这次我们可以在字段级别控制是否应该对序列化、反序列化或两者(默认)进行过滤。

让我们看看如何防止MyClass.other被序列化，但允许它在从 JSON 反序列化期间被填充：

```java
@Expose(serialize = false, deserialize = true) 
private String other;

```

虽然这是 Gson 提供的最简单的方法，并且不会影响其他库，但它可能意味着代码冗余。如果我们有一个类有一百个字段，而我们只想排除一个字段，我们需要写九十九个注解，这是大材小用。

## 5.排除策略

高度可定制的解决方案是使用[com.google.gson。排除策略](https://github.com/google/gson/blob/master/gson/src/main/java/com/google/gson/ExclusionStrategy.java)。

它允许我们定义(外部或匿名内部类)一个策略来指示 GsonBuilder 是否使用自定义标准序列化字段(和/或类)。

```java
Gson gson = new GsonBuilder()
  .addSerializationExclusionStrategy(strategy)
  .create();
String jsonString = gson.toJson(source);

assertEquals(expectedResult, jsonString);

```

让我们看一些使用智能策略的例子。

### 5.1. 使用类和字段名称

当然，我们也可以硬编码一个或多个字段/类名：

```java
ExclusionStrategy strategy = new ExclusionStrategy() {
    @Override
    public boolean shouldSkipField(FieldAttributes field) {
        if (field.getDeclaringClass() == MyClass.class && field.getName().equals("other")) {
            return true;
        }
        if (field.getDeclaringClass() == MySubClass.class && field.getName().equals("otherVerboseInfo")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
};

```

这是快速而直接的，但不是很可重用，并且在我们重命名我们的属性时也容易出错。

### 5.2. 与业务标准

由于我们只需要返回一个布尔值，我们就可以在该方法中实现我们喜欢的每个业务逻辑。

在下面的示例中，我们会将每个以“other”开头的字段标识为不应序列化的字段，无论它们属于哪个类：

```java
ExclusionStrategy strategy = new ExclusionStrategy() {
    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes field) {
        return field.getName().startsWith("other");
    }
};

```

### 5.3. 使用自定义注解

另一种聪明的方法是创建自定义注解：

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Exclude {}

```

然后我们可以利用ExclusionStrategy以使其与@Expose注解完全相同，但相反：

```java
public class MyClass {
    private long id;
    private String name;
    @Exclude 
    private String other;
    private MySubClass subclass;
}

public class MySubClass {
    private long id;
    private String description;
    @Exclude 
    private String otherVerboseInfo;
}

```

这是策略：

```java
ExclusionStrategy strategy = new ExclusionStrategy() {
    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes field) {
        return field.getAnnotation(Exclude.class) != null;
    }
};

```

[这个 StackOverflow 回答](https://stackoverflow.com/a/27986860/1654265)首先描述了这种技术。

它允许我们一次编写注解和策略，并动态注解我们的字段而无需进一步修改。

### 5.4. 将排除策略扩展到反序列化

无论我们使用哪种策略，我们始终可以控制应该应用它的位置。

仅在序列化期间：

```java
Gson gson = new GsonBuilder().addSerializationExclusionStrategy(strategy)

```

仅在反序列化期间：

```java
Gson gson = new GsonBuilder().addDeserializationExclusionStrategy(strategy)

```

总是：

```java
Gson gson = new GsonBuilder().setExclusionStrategies(strategy);

```

## 六. 总结

我们已经看到在 Gson 序列化期间从类及其子类中排除字段的不同方法。

我们还探讨了每种解决方案的主要优点和缺陷。