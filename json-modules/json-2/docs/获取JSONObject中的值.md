## 1. 概述

在本教程中，我们将深入探讨在JSONObject实例中获取值的细节。

Java中对JSON支持的一般介绍，请查看 [JSON-Java介绍](https://www.baeldung.com/java-org-json)。

## 2. JSONObject结构

JSONObject是一种类似地图的结构。它将其数据保存为一组键值对。虽然键是String类型，但值可能是多种类型。此外，值类型可以是 primitive 或 compound。基元是String、 Number 和 Boolean 类型，或JSONObject.NULL对象。复合是JSONObject和JSONArray类型。因此，JSON 数据可能具有任意的复杂性和嵌套。

因此，在嵌套结构中获取值需要更多的努力。从这里开始，让我们参考以下假想员工的 JSON 数据：

```json
{
  "name" : "Bob",
  "profession" : "Software engineer",
  "department" : "Research",
  "age" : 40,
  "family" : [
    {
      "wife" : {
        "name" : "Alice",
        "profession" : "Doctor",
        "age" : 38
      }
    },
    {
      "son" : {
        "name" : "Peter",
        "occupation" : "Schoolboy",
        "age" : 11
      }
    }
  ],
  "performance" : [
    {
      "2020" : 4.5
    },
    {
      "2021" : 4.8
    }
  ]
}
```

## 3. JSONObject的Getter方法

首先，让我们看看JSONObject类提供了哪些 getter API。有两组方法——get()和opt()方法。两组之间的区别在于，get()方法会在找不到键时抛出异常，而opt()方法不会抛出异常，而是根据方法返回 null 或特定值。

此外，每个组都有一个通用方法和几个带有类型转换的特定方法。通用方法返回一个Object实例，而特定方法返回一个已经转换的实例。让我们使用通用的get()方法获取 JSON 数据的“family”字段。我们假设JSON数据已经初步加载到jsonObject变量中，该变量是JSONObject类型：

```java
    JSONArray family = (JSONArray) jsonObject.get("family");
```

我们可以使用JSONArray的特定 getter 以更具可读性的方式执行相同的操作：

```java
    JSONArray family = jsonObject.getJSONArray("family");
```

## 4. 直接获取值

在这种方法中，我们通过获取到所需值的路径上的每个中间值来直接获取值。下面的代码展示了如何直接获取员工儿子的姓名：

```java
    JSONArray family = jsonObject.getJSONArray("family");
    JSONObject sonObject = family.getJSONObject(1);
    JSONObject sonData = sonObject.getJSONObject("son");
    String sonName = sonData.getString("name");
    Assertions.assertEquals(sonName, "Peter");
```

正如我们所看到的，直接获取值的方法有局限性。首先，我们需要知道 JSON 数据的确切结构。其次，我们需要知道每个值的数据类型，才能正确使用JSONObject的getter方法。

此外，当 JSON 数据的结构是动态的时，我们需要在代码中添加彻底的检查。例如，对于所有get()方法，我们需要将代码包含在try-catch块中。此外，对于opt()方法，我们需要添加空值或特定值检查。

## 5. 递归获取值

相比之下，在 JSON 数据中获取值的递归方法灵活且不易出错。在实现这种方法时，我们需要考虑 JSON 数据的嵌套结构。

首先，当key的值是JSONObject或JSONArray类型时，我们需要在该值中向下传播递归搜索。其次，当在当前递归调用中找到key时，我们需要将其映射值添加到返回结果中，无论该值是否为原始类型。

下面的方法实现了递归搜索：

```java
    public List<String> getValuesInObject(JSONObject jsonObject, String key) {
        List<String> accumulatedValues = new ArrayList<>();
        for (String currentKey : jsonObject.keySet()) {
            Object value = jsonObject.get(currentKey);
            if (currentKey.equals(key)) {
                accumulatedValues.add(value.toString());
            }

            if (value instanceof JSONObject) {
                accumulatedValues.addAll(getValuesInObject((JSONObject) value, key));
            } else if (value instanceof JSONArray) {
                accumulatedValues.addAll(getValuesInArray((JSONArray) value, key));
            }
        }

        return accumulatedValues;
    }

    public List<String> getValuesInArray(JSONArray jsonArray, String key) {
        List<String> accumulatedValues = new ArrayList<>();
        for (Object obj : jsonArray) {
            if (obj instanceof JSONArray) {
                accumulatedValues.addAll(getValuesInArray((JSONArray) obj, key));
            } else if (obj instanceof JSONObject) {
                accumulatedValues.addAll(getValuesInObject((JSONObject) obj, key));
            }
        }

        return accumulatedValues;
    }
```

为了简单起见，我们提供了两种不同的方法：一种用于在JSONObject中进行递归搜索，另一种用于JSONArray实例中。JSONObject是一个类映射结构，而JSONArray是一个类数组结构。因此，迭代对他们来说是不同的。因此，将所有逻辑都放在一个方法中会使带有类型转换和 if-else 分支的代码复杂化。

最后，我们来编写`getValuesInObject()` 方法的测试代码：

```java
    @Test
    public void getAllAssociatedValuesRecursively() {
        List<String> values = jsonObjectValueGetter.getValuesInObject(jsonObject, "son");
        Assertions.assertEquals(values.size(), 1);

        String sonString = values.get(0);
        Assertions.assertTrue(sonString.contains("Peter"));
        Assertions.assertTrue(sonString.contains("Schoolboy"));
        Assertions.assertTrue(sonString.contains("11"));

        values = jsonObjectValueGetter.getValuesInObject(jsonObject, "name");
        Assertions.assertEquals(values.size(), 3);

        Assertions.assertEquals(values.get(0), "Bob");
        Assertions.assertEquals(values.get(1), "Alice");
        Assertions.assertEquals(values.get(2), "Peter");
    }
```

## 六，总结

在本文中，我们讨论了获取JSONObject 中的值。 可以[在 GitHub 上](https://github.com/eugenp/tutorials/tree/master/json-modules/json-2)找到本次讨论中使用的片段的完整代码。