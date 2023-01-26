## 1. 配置

Instancio配置由[Settings](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/settings/Settings.html)
类封装(键和对应值的Map)，该Settings类提供了一些用于创建设置的静态方法。

```java
// Settings static factory methods
1. Settings.create()
2. Settings.defaults()
3. Settings.from(Map<Object, Object> map)
4. Settings.from(Settings other)
```

1. 创建空白Settings的新实例。
2. 创建一个包含默认设置的新实例。
3. 从Map或java.util.Properties创建Settings。
4. 创建其他Settings的副本(克隆操作)。

可以通过编程方式或通过属性文件覆盖设置。

> 注意：要检查所有键和默认值，只需System.out.println(Settings.defaults())

## 2. 以编程方式覆盖设置

要以编程方式覆盖设置，可以将一个Settings实例传递给构建器API：

```java
// Supplying custom settings
1. Settings overrides = Settings.create()
2.     .set(Keys.COLLECTION_MIN_SIZE, 10)
3.     .set(Keys.STRING_ALLOW_EMPTY, true)
4.     .set(Keys.SEED, 12345L) // seed is of type long (note the 'L')
5.     .lock();

6. Person person = Instancio.of(Person.class)
7.     .withSettings(overrides)
8.     .create();
```

+ 2：[Keys](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/settings/Keys.html)
  类为Instancio支持的所有键提供静态字段。
+ 5：lock()方法使Settings实例不可变，这是一个可选的方法调用。如果Settings在多个方法或类之间共享，它可用于防止修改。
+ 7：传入的Settings实例将覆盖默认设置。

> 范围设置自动调整：更新范围设置(例如COLLECTION_MIN_SIZE和COLLECTION_MAX_SIZE)时，如果新的最小值高于当前最大值，则范围界限会自动调整，反之亦然。

[Keys](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/settings/Keys.html)
类为每个key对象定义一个属性key，例如：

+ Keys.COLLECTION_MIN_SIZE -> "collection.min.size"
+ Keys.STRING_ALLOW_EMPTY -> "string.allow.empty"

使用这些属性key，还可以使用属性文件覆盖配置值。

## 3. 使用属性文件覆盖设置

可以使用instancio.properties覆盖默认设置，Instancio会自动从类路径的根目录加载这个文件。下面的清单显示了可以配置的所有属性键。

```properties
# Sample configuration properties
1. array.elements.nullable=false
2. array.max.length=6
3. array.min.length=2
4. array.nullable=false
5. boolean.nullable=false
6. byte.max=127
7. byte.min=1
8. byte.nullable=false
9. character.nullable=false
10. collection.elements.nullable=false
11. collection.max.size=6
12. collection.min.size=2
13. collection.nullable=false
14. double.max=10000
15. double.min=1
16. double.nullable=false
17. float.max=10000
18. float.min=1
19. float.nullable=false
20. integer.max=10000
21. integer.min=1
22. integer.nullable=false
23. long.max=10000
24. long.min=1
25. long.nullable=false
26. map.keys.nullable=false
27. map.values.nullable=false
28. map.max.size=6
29. map.min.size=2
30. map.nullable=false
31. mode=STRICT
32. hint.after.generate=POPULATE_NULLS_AND_DEFAULT_PRIMITIVES
33. overwrite.existing.values=true
34. assignment.type=FIELD
35. on.set.field.error=IGNORE
36. on.set.method.error=ASSIGN_FIELD
37. on.set.method.not.found=ASSIGN_FIELD
38. setter.style=SET
39. seed=12345
40. short.max=10000
41. short.min=1
42. short.nullable=false
43. string.allow.empty=false
44. string.field.prefix.enabled=false
45. string.max.length=10
46. string.min.length=3
47. string.nullable=false
48. subtype.java.util.Collection=java.util.ArrayList
49. subtype.java.util.List=java.util.ArrayList
50. subtype.java.util.Map=java.util.HashMap
51. subtype.java.util.SortedMap=java.util.TreeMap
```

+ 1,10,26-27：\*.elements.nullable、map.keys.nullable、map.values.nullable指定Instancio是否可以为数组/集合元素和Map的键和值生成null值
+ 4：其他的\*.nullable属性指定Instancio是否可以为给定类型生成null值
+ 31：指定模式(STRICT或LENIENT)，请参阅[选择器严格性](https://www.instancio.org/user-guide/#selector-strictness)
+ 35：指定全局种子值
+
48：带有subtype前缀的属性用于指定抽象类型的默认实现，或者通常将类型映射到子类型。[这与subtype mapping](https://www.instancio.org/user-guide/#subtype-mapping)
的机制相同，但通过属性进行配置

## 4. 设置优先级

Instancio图层设置彼此叠加，每一层都覆盖以前的设置，这是按以下顺序完成的：

1. Settings.defaults()
2. 来自instancio.properties的设置
3. 使用InstancioExtension时@WithSettings注解注入的设置(
   请参见[设置注入](https://www.instancio.org/user-guide/#settings-injection))
4.
使用构建器API的[withSettings(Settings)](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/InstancioApi.html#withSettings(org.instancio.settings.Settings))
方法提供的设置

在没有任何其他配置的情况下，Instancio使用Settings.defaults()
返回的默认值。如果instancio.properties在类路径的根目录中找到，它将覆盖默认值。最后，还可以在运行时使用@WithSettings注解或[withSettings(Settings)](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/InstancioApi.html#withSettings(org.instancio.settings.Settings))
方法覆盖设置，后者优先于其他一切。