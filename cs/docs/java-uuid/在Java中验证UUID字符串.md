## 1. 概述

在本教程中，我们将了解一些在Java中验证 UUID(通用唯一标识符)字符串的方法。

[我们将通过UUID](https://www.baeldung.com/java-uuid)类方法之一，然后我们将使用正则表达式。

## 2. 使用UUID.fromString()

检查字符串是否为UUID 的最快方法之一是尝试使用属于UUID类的静态方法fromString映射它。让我们试试看：

```java
@Test
public void whenValidUUIDStringIsValidated_thenValidationSucceeds() {
    String validUUID = "26929514-237c-11ed-861d-0242ac120002";
    Assertions.assertEquals(UUID.fromString(validUUID).toString(), validUUID);

    String invalidUUID = "invalid-uuid";
    Assertions.assertThrows(IllegalArgumentException.class, () -> UUID.fromString(invalidUUID));
}
```

在上面的代码片段中，我们可以看到，如果我们尝试验证的字符串不代表 UUID，则会抛出IllegalArgumentException 。但是，对于诸如“1-1-1-1-1”之类的字符串，方法fromString将返回“00000001-0001-0001-0001-000000000001”。所以我们包含了字符串比较来处理这种情况。

有些人可能会争辩说，使用异常不是流量控制的好做法，因此我们将看到实现相同结果的不同方法。

## 3.使用正则表达式

验证 UUID 的另一种方法是使用与格式完全匹配的[正则表达式。](https://www.baeldung.com/regular-expressions-java)

首先，我们需要定义一个用于匹配字符串的模式。

```java
Pattern UUID_REGEX =
  Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
```

然后，我们可以使用此模式尝试将其与字符串匹配，以验证它是否为 UUID：

```java
@Test
public void whenUUIDIsValidatedUsingRegex_thenValidationSucceeds() {
    Pattern UUID_REGEX =
      Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    Assertions.assertTrue(UUID_REGEX.matcher("26929514-237c-11ed-861d-0242ac120002").matches());

    Assertions.assertFalse(UUID_REGEX.matcher("invalid-uuid").matches());
}
```

## 4。总结

在本文中，我们了解了如何使用正则表达式或利用 UUID 类的静态方法来验证 UUID字符串。