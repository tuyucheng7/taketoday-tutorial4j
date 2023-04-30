## 1. 概述

Java Bean Validation规范的 2.0 版本增加了几个新特性，其中包括验证容器元素的可能性。

这个新功能利用了Java8 中引入的类型注解。因此它需要Java8 或更高版本才能工作。

可以将验证注解添加到容器中，例如集合、可选对象和其他内置以及自定义容器。

有关Java Bean Validation的介绍以及如何设置我们需要的Maven依赖项，请在此处查看我们[之前的文章](https://www.baeldung.com/javax-validation)。

在以下部分中，我们将专注于验证每种类型容器的元素。

## 2. 收藏元素

我们可以向类型为java.util.Iterable、java.util.List和java.util.Map的集合的元素添加验证注解。

让我们看一个验证列表元素的示例：

```java
public class Customer {    
     List<@NotBlank(message="Address must not be blank") String> addresses;
    
    // standard getters, setters 
}
```

在上面的示例中，我们为Customer类定义了一个addresses属性，其中包含不能为空Strings的元素。

请注意，@ NotBlank验证适用于String元素，而不适用于整个集合。如果集合为空，则不应用任何验证。

让我们验证一下，如果我们尝试向地址列表添加一个空字符串，验证框架将返回一个ConstraintViolation：

```java
@Test
public void whenEmptyAddress_thenValidationFails() {
    Customer customer = new Customer();
    customer.setName("John");

    customer.setAddresses(Collections.singletonList(" "));
    Set<ConstraintViolation<Customer>> violations = 
      validator.validate(customer);
    
    assertEquals(1, violations.size());
    assertEquals("Address must not be blank", 
      violations.iterator().next().getMessage());
}
```

接下来，让我们看看如何验证Map类型集合的元素：

```java
public class CustomerMap {
    
    private Map<@Email String, @NotNull Customer> customers;
    
    // standard getters, setters
}
```

请注意，我们可以为Map元素的键和值添加验证注解。

让我们验证添加带有无效电子邮件的条目是否会导致验证错误：

```java
@Test
public void whenInvalidEmail_thenValidationFails() {
    CustomerMap map = new CustomerMap();
    map.setCustomers(Collections.singletonMap("john", new Customer()));
    Set<ConstraintViolation<CustomerMap>> violations
      = validator.validate(map);
 
    assertEquals(1, violations.size());
    assertEquals(
      "Must be a valid email", 
      violations.iterator().next().getMessage());
}
```

## 3.可选值

验证约束也可以应用于Optional值：

```java
private Integer age;

public Optional<@Min(18) Integer> getAge() {
    return Optional.ofNullable(age);
}
```

让我们创建一个年龄太小的客户——并验证这是否会导致验证错误：

```java
@Test
public void whenAgeTooLow_thenValidationFails() {
    Customer customer = new Customer();
    customer.setName("John");
    customer.setAge(15);
    Set<ConstraintViolation<Customer>> violations
      = validator.validate(customer);
 
    assertEquals(1, violations.size());
}
```

另一方面，如果年龄为空，则Optional值未被验证：

```java
@Test
public void whenAgeNull_thenValidationSucceeds() {
    Customer customer = new Customer();
    customer.setName("John");
    Set<ConstraintViolation<Customer>> violations
      = validator.validate(customer);
 
    assertEquals(0, violations.size());
}
```

## 4.非通用容器元素

除了为类型参数添加注解外，我们还可以对非泛型容器应用验证，只要有一个带有@UnwrapByDefault注解的类型的值提取器。

值提取器是从容器中提取值以进行验证的类。

参考实现包含OptionalInt、OptionalLong和OptionalDouble的值提取器：

```java
@Min(1)
private OptionalInt numberOfOrders;
```

在这种情况下，@Min注解适用于包装的Integer值，而不适用于容器。

## 5.自定义容器元素

除了内置的值提取器之外，我们还可以定义自己的值提取器并将它们注册到容器类型中。

通过这种方式，我们可以向自定义容器的元素添加验证注解。

让我们添加一个包含companyName属性的新Profile类：

```java
public class Profile {
    private String companyName;
    
    // standard getters, setters 
}
```

接下来，我们要在Customer类中添加一个带有@NotBlank注解的Profile属性——验证companyName不是空字符串：

```java
@NotBlank
private Profile profile;
```

为此，我们需要一个值提取器来确定要应用于companyName属性的验证，而不是直接应用于配置文件对象。

让我们添加一个实现ValueExtractor接口并覆盖extractValue()方法的ProfileValueExtractor类：

```java
@UnwrapByDefault
public class ProfileValueExtractor 
  implements ValueExtractor<@ExtractedValue(type = String.class) Profile> {

    @Override
    public void extractValues(Profile originalValue, 
      ValueExtractor.ValueReceiver receiver) {
        receiver.value(null, originalValue.getCompanyName());
    }
}
```

这个类还需要指定使用@ExtractedValue注解提取的值的类型。

此外，我们还添加了@UnwrapByDefault注解，指定验证应应用于解包值而不是容器。

最后，我们需要通过将名为javax.validation.valueextraction.ValueExtractor的文件添加到META-INF/services目录来注册该类，其中包含我们的ProfileValueExtractor类的全名：

```plaintext
org.baeldung.javaxval.container.validation.valueextractors.ProfileValueExtractor
```

现在，当我们使用空的companyName验证具有profile属性的Customer对象时，我们将看到验证错误：

```java
@Test
public void whenProfileCompanyNameBlank_thenValidationFails() {
    Customer customer = new Customer();
    customer.setName("John");
    Profile profile = new Profile();
    profile.setCompanyName(" ");
    customer.setProfile(profile);
    Set<ConstraintViolation<Customer>> violations
     = validator.validate(customer);
 
    assertEquals(1, violations.size());
}
```

请注意，如果你正在使用hibernate-validator-annotation-processor，将验证注解添加到自定义容器类，当它被标记为@UnwrapByDefault时，将导致版本 6.0.2 中的编译错误。

这是一个[已知问题](http://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#validator-annotationprocessor-known-issues)，可能会在未来的版本中得到解决。

## 六. 总结

在本文中，我们展示了如何使用Java Bean Validation 2.0 验证多种类型的容器元素。