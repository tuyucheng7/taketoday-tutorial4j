## 1. 概述

在这篇简短的文章中，我们将使用Java 8 Stream API和[Introspector](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/java/beans/Introspector.html)类来调用在POJO中找到的所有getter。

我们将创建一个getter流，检查返回值并查看字段值是否为null。

## 2. 设置

我们唯一需要的设置是创建一个简单的POJO类：

```java
public class Customer {

	private Integer id;
	private String name;
	private String emailId;
	private Long phoneNumber;

	// standard getters and setters
}
```

## 3. 调用Getter方法

我们将使用Introspector分析Customer类；这为发现目标类支持的属性、事件和方法提供了一种简单的方法。

我们首先收集Customer类的所有PropertyDescriptor实例，PropertyDescriptor捕获Java Bean属性的所有信息：

```java
PropertyDescriptor[] propDescArr = Introspector
	.getBeanInfo(Customer.class, Object.class)
    .getPropertyDescriptors();
```

现在我们遍历所有PropertyDescriptor实例，并为每个属性调用read方法：

```java
return Arrays.stream(propDescArr)
    .filter(nulls(customer))
    .map(PropertyDescriptor::getName)
    .collect(Collectors.toList());
```

我们在上面使用的null谓词检查是否可以读取属性调用getter并仅过滤null值：

```java
private static Predicate<PropertyDescriptor> nulls(Customer customer) { 
    return = pd -> { 
        Method getterMethod = pd.getReadMethod(); 
        boolean result = false; 
        return (getterMethod != null && getterMethod.invoke(customer) == null); 
    }; 
}
```

最后，我们现在创建一个Customer的实例，将一些属性设置为null并测试我们的实现：

```java
@Test
void givenCustomer_whenAFieldIsNull_thenFieldNameInResult() {
    Customer customer = new Customer(1, "John", null, null);
	    
    List<String> result = Utils.getNullPropertiesList(customer);
    List<String> expectedFieldNames = Arrays.asList("emailId","phoneNumber");
	    
    assertTrue(result.size() == expectedFieldNames.size());
    assertTrue(result.containsAll(expectedFieldNames));      
}
```

## 4. 总结

在这个简短的教程中，我们充分利用了Java 8 Stream API和Introspector实例调用所有getter并检索空属性列表。