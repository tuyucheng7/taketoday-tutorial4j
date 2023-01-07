## 1. 概述

在这个简短的教程中，我们将研究检查Map中是否存在键的方法 。

具体来说，我们将关注containsKey 和 获取。

## 2.包含Key

如果我们看一下[Map#containsKey](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Map.html#containsKey(java.lang.Object))[的 JavaDoc](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Map.html#containsKey(java.lang.Object))：

>    如果此映射包含指定键的映射，则返回 `true`

我们可以看到这种方法非常适合做我们想做的事情。

让我们创建一个非常简单的地图并使用containsKey验证其内容：

```java
@Test
public void whenKeyIsPresent_thenContainsKeyReturnsTrue() {
    Map<String, String> map = Collections.singletonMap("key", "value");
    
    assertTrue(map.containsKey("key"));
    assertFalse(map.containsKey("missing"));
}
```

简而言之， containsKey 告诉我们地图是否包含该键。

## 3.得到

现在，get 有时也可以工作，但它会带来一些负担，具体取决于Map实现是否支持空值。

再次查看 Map的 JavaDoc，这次是 [Map#put](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Map.html#put(K,V))，我们看到它只会抛出 NullPointerException：

>   如果指定的键或值是 null并且此映射不允许 null 键或值

由于Map 的某些实现可以具有空值(如HashMap)，因此即使存在键， get也可能返回 空值。

所以，如果我们的目标是查看一个键是否有值，那么 get将起作用：

```java
@Test
public void whenKeyHasNullValue_thenGetStillWorks() {
    Map<String, String> map = Collections.singletonMap("nothing", null);

    assertTrue(map.containsKey("nothing"));
    assertNull(map.get("nothing"));
}
```

但是，如果我们只是想检查密钥是否存在，那么我们应该坚持使用containsKey。

## 4。总结

在本文中，我们查看了containsKey。我们还仔细研究了为什么使用get 来验证密钥的存在存在风险。