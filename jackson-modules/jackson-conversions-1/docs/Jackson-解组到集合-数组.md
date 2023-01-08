## 1. 概述

本教程将展示如何使用 Jackson 2 将 JSON 数组反序列化为Java数组或集合。

如果你想更深入地了解并学习你可以使用 Jackson 2 做的其他很酷的事情——请转到[主要的 Jackson 教程](https://www.baeldung.com/jackson)。

## 2.解组到数组

Jackson 可以轻松反序列化为Java数组：

```java
@Test
public void givenJsonArray_whenDeserializingAsArray_thenCorrect() 
  throws JsonParseException, JsonMappingException, IOException {
 
    ObjectMapper mapper = new ObjectMapper();
    List<MyDto> listOfDtos = Lists.newArrayList(
      new MyDto("a", 1, true), new MyDto("bc", 3, false));
    String jsonArray = mapper.writeValueAsString(listOfDtos);
 
    // [{"stringValue":"a","intValue":1,"booleanValue":true},
    // {"stringValue":"bc","intValue":3,"booleanValue":false}]

    MyDto[] asArray = mapper.readValue(jsonArray, MyDto[].class);
    assertThat(asArray[0], instanceOf(MyDto.class));
}
```

## 3.解组到集合

将相同的 JSON 数组读入Java集合有点困难——默认情况下，Jackson 将无法获得完整的通用类型信息，而是创建一个LinkedHashMap实例的集合：

```java
@Test
public void givenJsonArray_whenDeserializingAsListWithNoTypeInfo_thenNotCorrect() 
  throws JsonParseException, JsonMappingException, IOException {
 
    ObjectMapper mapper = new ObjectMapper();

    List<MyDto> listOfDtos = Lists.newArrayList(
      new MyDto("a", 1, true), new MyDto("bc", 3, false));
    String jsonArray = mapper.writeValueAsString(listOfDtos);

    List<MyDto> asList = mapper.readValue(jsonArray, List.class);
    assertThat((Object) asList.get(0), instanceOf(LinkedHashMap.class));
}
```

有两种方法可以帮助 Jackson 理解正确的类型信息——我们可以为此目的使用库提供的TypeReference ：

```java
@Test
public void givenJsonArray_whenDeserializingAsListWithTypeReferenceHelp_thenCorrect() 
  throws JsonParseException, JsonMappingException, IOException {
 
    ObjectMapper mapper = new ObjectMapper();

    List<MyDto> listOfDtos = Lists.newArrayList(
      new MyDto("a", 1, true), new MyDto("bc", 3, false));
    String jsonArray = mapper.writeValueAsString(listOfDtos);

    List<MyDto> asList = mapper.readValue(
      jsonArray, new TypeReference<List<MyDto>>() { });
    assertThat(asList.get(0), instanceOf(MyDto.class));
}
```

或者我们可以使用接受JavaType的重载readValue方法：

```java
@Test
public void givenJsonArray_whenDeserializingAsListWithJavaTypeHelp_thenCorrect() 
  throws JsonParseException, JsonMappingException, IOException {
    ObjectMapper mapper = new ObjectMapper();

    List<MyDto> listOfDtos = Lists.newArrayList(
      new MyDto("a", 1, true), new MyDto("bc", 3, false));
    String jsonArray = mapper.writeValueAsString(listOfDtos);

    CollectionType javaType = mapper.getTypeFactory()
      .constructCollectionType(List.class, MyDto.class);
    List<MyDto> asList = mapper.readValue(jsonArray, javaType);
 
    assertThat(asList.get(0), instanceOf(MyDto.class));
}
```

最后一点是MyDto类需要有无参数的默认构造函数——如果没有，Jackson 将无法实例化它：

```bash
com.fasterxml.jackson.databind.JsonMappingException: 
No suitable constructor found for type [simple type, class org.baeldung.jackson.ignore.MyDto]: 
can not instantiate from JSON object (need to add/enable type information?)
```

## 4. 总结

将 JSON 数组映射到 java 集合是 Jackson 最常用于的任务之一，这些解决方案对于获得正确的、类型安全的映射至关重要。