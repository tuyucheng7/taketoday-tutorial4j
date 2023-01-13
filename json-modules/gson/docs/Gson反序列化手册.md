## 1. 将 JSON 反序列化为单个基本对象

让我们从简单开始——我们将把一个简单的 json 解组为一个Java对象——Foo：

```java
public class Foo {
    public int intValue;
    public String stringValue;

    // + standard equals and hashCode implementations
}
```

和解决方案：

```java
@Test
public void whenDeserializingToSimpleObject_thenCorrect() {
    String json = "{"intValue":1,"stringValue":"one"}";

    Foo targetObject = new Gson().fromJson(json, Foo.class);

    assertEquals(targetObject.intValue, 1);
    assertEquals(targetObject.stringValue, "one");
}
```

## 延伸阅读：

## [从 Gson 中的序列化中排除字段](https://www.baeldung.com/gson-exclude-fields-serialization)

探索可用于从 Gson 中的序列化中排除字段的选项。

[阅读更多](https://www.baeldung.com/gson-exclude-fields-serialization)→

## [Gson 序列化手册](https://www.baeldung.com/gson-serialization-guide)

了解如何使用 Gson 库序列化实体。

[阅读更多](https://www.baeldung.com/gson-serialization-guide)→

## [杰克逊对格森](https://www.baeldung.com/jackson-vs-gson)

使用 Jackson 和 Gson 进行序列化的快速实用指南。

[阅读更多](https://www.baeldung.com/jackson-vs-gson)→

## 2.将JSON反序列化为通用对象

接下来——让我们使用泛型定义一个对象：

```java
public class GenericFoo<T> {
    public T theValue;
}
```

并将一些 json 解组到这种类型的对象中：

```java
@Test
public void whenDeserializingToGenericObject_thenCorrect() {
    Type typeToken = new TypeToken<GenericFoo<Integer>>() { }.getType();
    String json = "{"theValue":1}";

    GenericFoo<Integer> targetObject = new Gson().fromJson(json, typeToken);

    assertEquals(targetObject.theValue, new Integer(1));
}
```

## 3. 将带有额外未知字段的 JSON 反序列化为对象

接下来——让我们反序列化一些包含额外未知字段的复杂 json ：

```java
@Test
public void givenJsonHasExtraValues_whenDeserializing_thenCorrect() {
    String json = 
      "{"intValue":1,"stringValue":"one","extraString":"two","extraFloat":2.2}";
    Foo targetObject = new Gson().fromJson(json, Foo.class);

    assertEquals(targetObject.intValue, 1);
    assertEquals(targetObject.stringValue, "one");
}
```

如你所见，Gson 将忽略未知字段并简单地匹配它能够匹配的字段。

## 4. 将字段名称不匹配的 JSON 反序列化为对象

现在，让我们看看 Gson 如何处理包含与Foo对象的字段不匹配的字段的 json 字符串：

```java
@Test
public void givenJsonHasNonMatchingFields_whenDeserializingWithCustomDeserializer_thenCorrect() {
    String json = "{"valueInt":7,"valueString":"seven"}";

    GsonBuilder gsonBldr = new GsonBuilder();
    gsonBldr.registerTypeAdapter(Foo.class, new FooDeserializerFromJsonWithDifferentFields());
    Foo targetObject = gsonBldr.create().fromJson(json, Foo.class);

    assertEquals(targetObject.intValue, 7);
    assertEquals(targetObject.stringValue, "seven");
}
```

请注意，我们注册了一个自定义反序列化器——它能够正确解析 json 字符串中的字段并将它们映射到我们的Foo：

```java
public class FooDeserializerFromJsonWithDifferentFields implements JsonDeserializer<Foo> {

    @Override
    public Foo deserialize
      (JsonElement jElement, Type typeOfT, JsonDeserializationContext context) 
      throws JsonParseException {
        JsonObject jObject = jElement.getAsJsonObject();
        int intValue = jObject.get("valueInt").getAsInt();
        String stringValue = jObject.get("valueString").getAsString();
        return new Foo(intValue, stringValue);
    }
}
```

## 5. 将 JSON 数组反序列化为Java对象数组

接下来，我们将把一个 json 数组反序列化为一个Foo对象的Java 数组：

```java
@Test
public void givenJsonArrayOfFoos_whenDeserializingToArray_thenCorrect() {
    String json = "[{"intValue":1,"stringValue":"one"}," +
      "{"intValue":2,"stringValue":"two"}]";
    Foo[] targetArray = new GsonBuilder().create().fromJson(json, Foo[].class);

    assertThat(Lists.newArrayList(targetArray), hasItem(new Foo(1, "one")));
    assertThat(Lists.newArrayList(targetArray), hasItem(new Foo(2, "two")));
    assertThat(Lists.newArrayList(targetArray), not(hasItem(new Foo(1, "two"))));
}
```

## 6. 将 JSON 数组反序列化为Java集合

接下来，一个json数组直接变成一个Java Collection：

```java
@Test
public void givenJsonArrayOfFoos_whenDeserializingCollection_thenCorrect() {
    String json = 
      "[{"intValue":1,"stringValue":"one"},{"intValue":2,"stringValue":"two"}]";
    Type targetClassType = new TypeToken<ArrayList<Foo>>() { }.getType();

    Collection<Foo> targetCollection = new Gson().fromJson(json, targetClassType);
    assertThat(targetCollection, instanceOf(ArrayList.class));
}
```

## 7. 将 JSON 反序列化为嵌套对象

接下来，让我们定义我们的嵌套对象——FooWithInner：

```java
public class FooWithInner {
    public int intValue;
    public String stringValue;
    public InnerFoo innerFoo;

    public class InnerFoo {
        public String name;
    }
}
```

以下是如何反序列化包含此嵌套对象的输入：

```java
@Test
public void whenDeserializingToNestedObjects_thenCorrect() {
    String json = "{"intValue":1,"stringValue":"one","innerFoo":{"name":"inner"}}";

    FooWithInner targetObject = new Gson().fromJson(json, FooWithInner.class);

    assertEquals(targetObject.intValue, 1);
    assertEquals(targetObject.stringValue, "one");
    assertEquals(targetObject.innerFoo.name, "inner");
}
```

## 8. 使用自定义构造函数反序列化 JSON

最后，让我们看看如何在反序列化过程中强制使用特定的构造函数而不是默认的 - 无参数构造函数 - 使用InstanceCreator：

```java
public class FooInstanceCreator implements InstanceCreator<Foo> {

    @Override
    public Foo createInstance(Type type) {
        return new Foo("sample");
    }
}
```

下面是如何在反序列化中使用我们的FooInstanceCreator：

```java
@Test
public void whenDeserializingUsingInstanceCreator_thenCorrect() {
    String json = "{"intValue":1}";

    GsonBuilder gsonBldr = new GsonBuilder();
    gsonBldr.registerTypeAdapter(Foo.class, new FooInstanceCreator());
    Foo targetObject = gsonBldr.create().fromJson(json, Foo.class);

    assertEquals(targetObject.intValue, 1);
    assertEquals(targetObject.stringValue, "sample");
}
```

请注意，当我们使用以下构造函数时， Foo.stringValue等于示例而不是 null：

```java
public Foo(String stringValue) {
    this.stringValue = stringValue;
}
```

## 9.总结

本文展示了如何利用 Gson 库来解析 JSON 输入——遍历单个对象和多个对象的最常见用例。