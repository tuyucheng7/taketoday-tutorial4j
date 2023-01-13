## 1. 简介

在本教程中，我们将使用[Google 的 Gson 库探索](https://github.com/google/gson)List的一些高级[序列化](https://www.baeldung.com/gson-serialization-guide)和[反序列](https://www.baeldung.com/gson-deserialization-guide)化案例。

## 2.对象列表

一个常见的用例是序列化和反序列化 POJO 列表。

考虑类：

```java
public class MyClass {
    private int id;
    private String name;

    public MyClass(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // getters and setters
}
```

下面是我们将如何序列化List<MyClass>：

```java
@Test
public void givenListOfMyClass_whenSerializing_thenCorrect() {
    List<MyClass> list = Arrays.asList(new MyClass(1, "name1"), new MyClass(2, "name2"));

    Gson gson = new Gson();
    String jsonString = gson.toJson(list);
    String expectedString = "[{"id":1,"name":"name1"},{"id":2,"name":"name2"}]";

    assertEquals(expectedString, jsonString);
}
```

正如我们所见，序列化非常简单。

然而，反序列化是棘手的。这是一种不正确的做法：

```java
@Test(expected = ClassCastException.class)
public void givenJsonString_whenIncorrectDeserializing_thenThrowClassCastException() {
    String inputString = "[{"id":1,"name":"name1"},{"id":2,"name":"name2"}]";

    Gson gson = new Gson();
    List<MyClass> outputList = gson.fromJson(inputString, ArrayList.class);

    assertEquals(1, outputList.get(0).getId());
}
```

在这里，尽管反序列化后我们会得到一个大小为 2 的列表，但它不会是MyClass的列表。因此，第 6 行抛出ClassCastException。

Gson 可以序列化任意对象的集合，但不能在没有附加信息的情况下反序列化数据。那是因为用户无法指示结果对象的类型。相反，在反序列化时，Collection必须是特定的通用类型。

反序列化列表的正确方法是：

```java
@Test
public void givenJsonString_whenDeserializing_thenReturnListOfMyClass() {
    String inputString = "[{"id":1,"name":"name1"},{"id":2,"name":"name2"}]";
    List<MyClass> inputList = Arrays.asList(new MyClass(1, "name1"), new MyClass(2, "name2"));

    Type listOfMyClassObject = new TypeToken<ArrayList<MyClass>>() {}.getType();

    Gson gson = new Gson();
    List<MyClass> outputList = gson.fromJson(inputString, listOfMyClassObject);

    assertEquals(inputList, outputList);
}
```

在这里，我们使用 Gson 的TypeToken来确定要反序列化的正确类型—— ArrayList<MyClass>。用于获取listOfMyClassObject的习语实际上定义了一个匿名本地内部类，其中包含一个返回完全参数化类型的方法getType() 。

## 3. 多态对象列表

### 3.1. 问题

考虑一个动物类层次结构的例子：

```java
public abstract class Animal {
    // ...
}

public class Dog extends Animal {
    // ...
}

public class Cow extends Animal {
    // ...
}
```

我们如何序列化和反序列化List<Animal>？我们可以像在上一节中使用的那样使用TypeToken<ArrayList<Animal>> 。但是，Gson 仍然无法确定存储在列表中的对象的具体数据类型。

### 3.2. 使用自定义反序列化器

解决此问题的一种方法是将类型信息添加到序列化的 JSON。我们在 JSON 反序列化期间尊重该类型信息。为此，我们需要编写自己的自定义序列化器和反序列化器。

首先，我们将在基类Animal中引入一个名为type的新String字段。它存储它所属的类的简单名称。

让我们来看看我们的示例类：

```java
public abstract class Animal {
    public String type = "Animal";
}
public class Dog extends Animal {
    private String petName;

    public Dog() {
        petName = "Milo";
        type = "Dog";
    }

    // getters and setters
}
public class Cow extends Animal {
    private String breed;

    public Cow() {
        breed = "Jersey";
        type = "Cow";
    }

    // getters and setters
}
```

序列化将像以前一样继续工作，没有任何问题：

```java
@Test 
public void givenPolymorphicList_whenSerializeWithTypeAdapter_thenCorrect() {
    String expectedString
      = "[{"petName":"Milo","type":"Dog"},{"breed":"Jersey","type":"Cow"}]";

    List<Animal> inList = new ArrayList<>();
    inList.add(new Dog());
    inList.add(new Cow());

    String jsonString = new Gson().toJson(inList);

    assertEquals(expectedString, jsonString);
}
```

为了反序列化列表，我们必须提供一个自定义反序列化器：

```java
public class AnimalDeserializer implements JsonDeserializer<Animal> {
    private String animalTypeElementName;
    private Gson gson;
    private Map<String, Class<? extends Animal>> animalTypeRegistry;

    public AnimalDeserializer(String animalTypeElementName) {
        this.animalTypeElementName = animalTypeElementName;
        this.gson = new Gson();
        this.animalTypeRegistry = new HashMap<>();
    }

    public void registerBarnType(String animalTypeName, Class<? extends Animal> animalType) {
        animalTypeRegistry.put(animalTypeName, animalType);
    }

    public Animal deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject animalObject = json.getAsJsonObject();
        JsonElement animalTypeElement = animalObject.get(animalTypeElementName);

        Class<? extends Animal> animalType = animalTypeRegistry.get(animalTypeElement.getAsString());
        return gson.fromJson(animalObject, animalType);
    }
}
```

在这里，animalTypeRegistry映射维护类名和类类型之间的映射。

在反序列化时，我们首先提取出新添加的类型字段。使用此值，我们在animalTypeRegistry映射上进行查找以获取具体数据类型。然后将此数据类型传递给fromJson()。

让我们看看如何使用我们的自定义解串器：

```java
@Test
public void givenPolymorphicList_whenDeserializeWithTypeAdapter_thenCorrect() {
    String inputString
      = "[{"petName":"Milo","type":"Dog"},{"breed":"Jersey","type":"Cow"}]";

    AnimalDeserializer deserializer = new AnimalDeserializer("type");
    deserializer.registerBarnType("Dog", Dog.class);
    deserializer.registerBarnType("Cow", Cow.class);
    Gson gson = new GsonBuilder()
      .registerTypeAdapter(Animal.class, deserializer)
      .create();

    List<Animal> outList = gson.fromJson(inputString, new TypeToken<List<Animal>>(){}.getType());

    assertEquals(2, outList.size());
    assertTrue(outList.get(0) instanceof Dog);
    assertTrue(outList.get(1) instanceof Cow);
}
```

### 3.3. 使用RuntimeTypeAdapterFactory

编写自定义解串器的另一种方法是使用[Gson 源代码中的](https://github.com/google/gson/blob/master/extras/src/main/java/com/google/gson/typeadapters/RuntimeTypeAdapterFactory.java)RuntimeTypeAdapterFactory类。但是，库不会公开它供用户使用。因此，我们必须在我们的Java项目中创建该类的副本。

完成后，我们可以使用它来反序列化我们的列表：

```java
@Test
public void givenPolymorphicList_whenDeserializeWithRuntimeTypeAdapter_thenCorrect() {
    String inputString
      = "[{"petName":"Milo","type":"Dog"},{"breed":"Jersey","type":"Cow"}]";

    Type listOfAnimals = new TypeToken<ArrayList<Animal>>(){}.getType();

    RuntimeTypeAdapterFactory<Animal> adapter = RuntimeTypeAdapterFactory.of(Animal.class, "type")
      .registerSubtype(Dog.class)
      .registerSubtype(Cow.class);

    Gson gson = new GsonBuilder().registerTypeAdapterFactory(adapter).create();

    List<Animal> outList = gson.fromJson(inputString, listOfAnimals);

    assertEquals(2, outList.size());
    assertTrue(outList.get(0) instanceof Dog);
    assertTrue(outList.get(1) instanceof Cow);
}
```

请注意，底层机制仍然相同。

我们在序列化的时候还需要引入类型信息。稍后可以在反序列化期间使用类型信息。因此，为了使该解决方案起作用，每个类中仍然需要字段类型。我们只是不必编写自己的反序列化器。

RuntimeTypeAdapterFactory根据传递给它的字段名称和注册的子类型提供正确的类型适配器。

## 4. 总结

在本文中，我们了解了如何使用 Gson 序列化和反序列化对象列表。