## 1. 简介

MapUtils是 Apache Commons Collections 项目中可用的工具之一。

简而言之，它提供实用方法和装饰器来处理java.util.Map和java.util.SortedMap实例。

## 2.设置

让我们从添加[依赖项](https://search.maven.org/classic/#search|ga|1|a%3A"commons-collections4")开始：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.1</version>
</dependency>
```

## 3.实用方法

### 3.1. 从数组创建地图

现在，让我们设置将用于创建地图的数组：

```java
public class MapUtilsTest {
    private String[][] color2DArray = new String[][] {
        {"RED", "#FF0000"},
        {"GREEN", "#00FF00"},
        {"BLUE", "#0000FF"}
    };
    private String[] color1DArray = new String[] {
        "RED", "#FF0000",
        "GREEN", "#00FF00",
        "BLUE", "#0000FF"
    };
    private Map<String, String> colorMap;

    //...
}
```

让我们看看如何从二维数组创建地图：

```java
@Test
public void whenCreateMapFrom2DArray_theMapIsCreated() {
    this.colorMap = MapUtils.putAll(
      new HashMap<>(), this.color2DArray);

    assertThat(
      this.colorMap, 
      is(aMapWithSize(this.color2DArray.length)));
    
    assertThat(this.colorMap, hasEntry("RED", "#FF0000"));
    assertThat(this.colorMap, hasEntry("GREEN", "#00FF00"));
    assertThat(this.colorMap, hasEntry("BLUE", "#0000FF"));
}
```

我们也可以使用一维数组。在这种情况下，数组被视为备用索引中的键和值：

```java
@Test
public void whenCreateMapFrom1DArray_theMapIsCreated() {
    this.colorMap = MapUtils.putAll(
      new HashMap<>(), this.color1DArray);
    
    assertThat(
      this.colorMap, 
      is(aMapWithSize(this.color1DArray.length / 2)));

    assertThat(this.colorMap, hasEntry("RED", "#FF0000"));
    assertThat(this.colorMap, hasEntry("GREEN", "#00FF00"));
    assertThat(this.colorMap, hasEntry("BLUE", "#0000FF"));
}
```

### 3.2. 打印地图的内容

很多时候在调试或调试日志中，我们想打印整个地图：

```java
@Test
public void whenVerbosePrintMap_thenMustPrintFormattedMap() {
    MapUtils.verbosePrint(System.out, "Optional Label", this.colorMap);
}
```

结果：

```java
Optional Label = 
{
    RED = #FF0000
    BLUE = #0000FF
    GREEN = #00FF00
}
```

我们还可以使用debugPrint()来额外打印值的数据类型。

### 3.3. 获取值

MapUtils提供了一些方法，以null安全的方式从给定键的映射中提取值。

例如，getString()从Map获取一个String。String值是通过toString()获得的。如果值为空或转换失败，我们可以选择指定要返回的默认值：

```java
@Test
public void whenGetKeyNotPresent_thenMustReturnDefaultValue() {
    String defaultColorStr = "COLOR_NOT_FOUND";
    String color = MapUtils
      .getString(this.colorMap, "BLACK", defaultColorStr);
    
    assertEquals(color, defaultColorStr);
}
```

请注意，这些方法是null安全的，即它们可以安全地处理null映射参数：

```java
@Test
public void whenGetOnNullMap_thenMustReturnDefaultValue() {
    String defaultColorStr = "COLOR_NOT_FOUND";
    String color = MapUtils.getString(null, "RED", defaultColorStr);
    
    assertEquals(color, defaultColorStr);
}
```

即使地图为null ，这里的颜色也会获得COLOR_NOT_FOUND的值。

### 3.4. 反转地图

我们还可以轻松地反转地图：

```java
@Test
public void whenInvertMap_thenMustReturnInvertedMap() {
    Map<String, String> invColorMap = MapUtils.invertMap(this.colorMap);

    int size = invColorMap.size();
    Assertions.assertThat(invColorMap)
      .hasSameSizeAs(colorMap)
      .containsKeys(this.colorMap.values().toArray(new String[] {}))
      .containsValues(this.colorMap.keySet().toArray(new String[] {}));
}
```

这会将colorMap反转为：

```java
{
    #00FF00 = GREEN
    #FF0000 = RED
    #0000FF = BLUE
}
```

如果源映射为多个键关联相同的值，那么在反转后其中一个值将随机成为一个键。

### 3.5. 空检查

如果Map为null或为空，则isEmpty()方法返回true 。

safeAddToMap()方法可防止向Map添加空元素。

## 4.装饰器

这些方法为Map 添加了额外的功能。

在大多数情况下，最好不要存储对装饰 Map 的引用。

### 4.1. 固定大小的地图

fixedSizeMap()返回由给定地图支持的固定大小地图。可以更改元素但不能添加或删除元素：

```java
@Test(expected = IllegalArgumentException.class)
public void whenCreateFixedSizedMapAndAdd_thenMustThrowException() {
    Map<String, String> rgbMap = MapUtils
      .fixedSizeMap(MapUtils.putAll(new HashMap<>(), this.color1DArray));
    
    rgbMap.put("ORANGE", "#FFA500");
}
```

### 4.2. 预测图

predicatedMap()方法返回一个Map确保所有持有的元素匹配提供的谓词：

```java
@Test(expected = IllegalArgumentException.class)
public void whenAddDuplicate_thenThrowException() {
    Map<String, String> uniqValuesMap 
      = MapUtils.predicatedMap(this.colorMap, null, 
        PredicateUtils.uniquePredicate());
    
    uniqValuesMap.put("NEW_RED", "#FF0000");
}
```

在这里，我们使用PredicateUtils.uniquePredicate()为值指定谓词。任何向该映射中插入重复值的尝试都将生成java.lang。非法参数异常。

我们可以通过实现Predicate接口来实现自定义谓词。

### 4.3. 懒图

lazyMap()返回一个地图，其中的值在请求时被初始化。

如果传递给此地图的Map.get(Object)方法的键在地图中不存在，则Transformer实例将用于创建将与请求的键相关联的新对象：

```java
@Test
public void whenCreateLazyMap_theMapIsCreated() {
    Map<Integer, String> intStrMap = MapUtils.lazyMap(
      new HashMap<>(),
      TransformerUtils.stringValueTransformer());
    
    assertThat(intStrMap, is(anEmptyMap()));
    
    intStrMap.get(1);
    intStrMap.get(2);
    intStrMap.get(3);
    
    assertThat(intStrMap, is(aMapWithSize(3)));
}
```

## 5.总结

在本快速教程中，我们探索了 Apache Commons Collections MapUtils类，并研究了可以简化各种常见地图操作的各种实用方法和装饰器。