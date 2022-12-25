## 1. 概述

Spring的@Value注解提供了一种将属性值注入组件的便捷方式，**为属性可能不存在的情况提供合理的默认值也非常有用**。

这就是我们将在本教程中重点介绍的内容-如何为@Value Spring注解指定默认值。

有关@Value的更详细的快速指南，请参阅[此处]()的文章。

## 2. 字符串默认值

让我们看一下为String属性设置默认值的基本语法：

```java
@Value("${some.key:my default value}")
private String stringWithDefaultValue;
```

如果some.key无法解析，则stringWithDefaultValue会被设置为”my default value“。

同样，我们可以设置一个零长度的字符串作为默认值：

```java
@Value("${some.key:})"
private String stringWithBlankDefaultValue;
```

## 3. 原始类型

要为boolean和int等基本类型设置默认值，我们可以使用文本值：

```java
@Value("${some.key:true}")
private boolean booleanWithDefaultValue;
@Value("${some.key:42}")
private int intWithDefaultValue;
```

如果我们愿意，我们可以通过将类型更改为Boolean和Integer来使用原始类型对应的包装器类型。

## 4. 数组

我们还可以将逗号分隔的值列表注入到数组中：

```java
@Value("${some.key:one,two,three}")
private String[] stringArrayWithDefaults;

@Value("${some.key:1,2,3}")
private int[] intArrayWithDefaults;
```

在上面的第一个示例中，值one、two和three作为默认值注入到stringArrayWithDefaults中。

在第二个示例中，值1、2和3作为默认值注入到intArrayWithDefaults中。

## 5. 使用SpEL

我们还可以使用Spring表达式语言(SpEL)来指定表达式和默认值。

在下面的示例中，我们希望将some.system.key设置为系统属性，如果未设置该系统属性，我们希望使用”'my default system property value“作为默认值：

```java
@Value("#{systemProperties['some.key'] ?: 'my default system property value'}")
private String spelWithDefaultValue;
```

## 6. 总结

在这篇快速文章中，我们介绍了如何为我们希望使用Spring的@Value注解注入其值的属性设置默认值。