## 1. 概述

在本文中，我们将了解Spring的类型转换。

Spring为内置类型提供了开箱即用的各种转换器；这意味着与字符串、整数、布尔值和许多其他类型等基本类型相互转换。

除此之外，Spring还提供了一个实体类型转换SPI用于开发我们的自定义转换器。

## 2. 内置转换器

我们将从Spring中开箱即用的转换器开始；让我们看一下字符串到整数的转换：

```java
@Autowired
ConversionService conversionService;

@Test
void whenConvertStringToIntegerUsingDefaultConverter_thenSuccess() {
    assertThat(conversionService.convert("25", Integer.class)).isEqualTo(25);
}
```

我们在这里唯一需要做的就是自动装配Spring提供的ConversionService并调用convert()方法。第一个参数是我们要转换的值，第二个参数是我们要转换为的目标类型。

除了这个字符串转化为整数的例子，还有很多其他的组合可供我们使用。

## 3. 创建自定义转换器

让我们看一个将Employee的字符串表示形式转换为Employee实例的示例。

这是Employee类：

```java
public class Employee {

    private long id;
    private double salary;

    // standard constructors, getters, setters
}
```

我们将转换的字符串是一个逗号分隔的对，表示id和salary。例如，“1,50000.00”。

**为了创建我们的自定义转换器，我们需要实现Converter<S, T\>接口并实现convert()方法**：

```java
public class StringToEmployeeConverter implements Converter<String, Employee> {

    @Override
    public Employee convert(String from) {
        String[] data = from.split(",");
        return new Employee(Long.parseLong(data[0]), Double.parseDouble(data[1]));
    }
}
```

我们还需要通过将StringToEmployeeConverter添加到FormatterRegistry来告诉Spring这个新转换器。这可以通过实现WebMvcConfigurer并重写addFormatters()方法来完成：

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToEmployeeConverter());
    }
}
```

就是这样。我们的新转换器现在可用于ConversionService，我们可以像使用任何其他内置转换器一样使用它：

```java
@Test
void whenConvertStringToEmployee_thenSuccess() {
    Employee employee = conversionService.convert("1,50000.00", Employee.class);
    Employee actualEmployee = new Employee(1, 50000.00);
    
    assertThat(conversionService.convert("1,50000.00", Employee.class)).isEqualToComparingFieldByField(actualEmployee);
}
```

### 3.1 隐式转换

**除了使用ConversionService进行这些显式转换之外，Spring还能够在Controller方法中为所有已注册的转换器隐式转换值**：

```java
@RestController
public class StringToEmployeeConverterController {

    @GetMapping("/string-to-employee")
    public ResponseEntity<Object> getStringToEmployee(@RequestParam("employee") Employee employee) {
        return ResponseEntity.ok(employee);
    }
}
```

这是使用转换器的一种更自然的方式。让我们添加一个测试来看看它的实际效果：

```java
@Autowired
private MockMvc mockMvc;

@Test
void getStringToEmployeeTest() throws Exception {
    mockMvc.perform(get("/string-to-employee?employee=1,2000"))
        .andDo(print())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.salary", is(2000.0)))
        .andExpect(status().isOk());
}
```

如你所见，测试将打印请求和响应的所有详细信息。下面是作为响应的一部分返回的JSON格式的Employee对象：

```json
{
    "id": 1,
    "salary": 2000.0
}
```

## 4. 创建ConverterFactory

也可以创建一个ConverterFactory来按需创建转换器，这在为枚举创建转换器时特别有用。

让我们来看一个非常简单的枚举：

```java
public enum Modes {

    ALPHA, BETA
}
```

接下来，让我们创建一个StringToEnumConverterFactory，它可以生成用于将String转换为任何枚举的转换器：

```java
@Component
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    @Override
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnumConverter(targetType);
    }

    private static class StringToEnumConverter<T extends Enum> implements Converter<String, T> {

        private Class<T> enumType;

        public StringToEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        public T convert(String source) {
            return (T) Enum.valueOf(this.enumType, source.trim());
        }
    }
}
```

正如我们所见，工厂类内部使用了Converter接口的实现。

这里需要注意的一点是，虽然我们将使用我们的Modes枚举来演示用法，但我们没有在StringToEnumConverterFactory的任何地方提到枚举。**我们的工厂类足够通用，可以根据需要为任何枚举类型生成转换器**。

下一步是注册这个工厂类，就像我们在前面的例子中注册我们的转换器一样：

```java
@Override
public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new StringToEmployeeConverter());
    registry.addConverterFactory(new StringToEnumConverterFactory());
}
```

现在ConversionService已准备好将字符串转换为枚举：

```java
@Test
void whenConvertStringToEnum_thenSuccess() {
    assertThat(conversionService.convert("ALPHA", Modes.class)).isEqualTo(Modes.ALPHA);
}
```

## 5. 创建GenericConverter

**GenericConverter为我们提供了更大的灵活性来创建更通用的转换器，但以牺牲类型安全为代价**。

让我们考虑一个将Integer、Double或String转换为BigDecimal值的示例。我们不需要为此编写三个转换器，一个简单的GenericConverter就可以达到此目的。

第一步是告诉Spring支持哪些类型的转换，我们通过创建一组ConvertiblePair来做到这一点：

```java
public class GenericBigDecimalConverter implements GenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        ConvertiblePair[] pairs = new ConvertiblePair[]{
              new ConvertiblePair(Number.class, BigDecimal.class),
              new ConvertiblePair(String.class, BigDecimal.class)
        };
        return ImmutableSet.copyOf(pairs);
    }
}
```

下一步是覆盖GenericConverter中的convert()方法：

```java
@Override
public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
    if (sourceType.getType() == BigDecimal.class) {
        return source;
    }
    if (sourceType.getType() == String.class) {
        String number = (String) source;
        return new BigDecimal(number);
    } else {
        Number number = (Number) source;
        BigDecimal converted = new BigDecimal(number.doubleValue());
        return converted.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }
}
```

convert()方法尽可能简单。但是，TypeDescriptor在获取有关源类型和目标类型的详细信息方面为我们提供了极大的灵活性。

正如你可能已经猜到的那样，下一步是注册这个转换器：

```java
@Override
public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new StringToEmployeeConverter());
    registry.addConverterFactory(new StringToEnumConverterFactory());
    registry.addConverter(new GenericBigDecimalConverter());
}
```

使用此转换器类似于我们已经看到的其他示例：

```java
@Test
void whenConvertingToBigDecimalUsingGenericConverter_thenSuccess() {
    assertThat(conversionService.convert(Integer.valueOf(11), BigDecimal.class))
        .isEqualTo(BigDecimal.valueOf(11.00).setScale(2, BigDecimal.ROUND_HALF_EVEN));
    
    assertThat(conversionService.convert(Double.valueOf(25.23), BigDecimal.class))
        .isEqualByComparingTo(BigDecimal.valueOf(Double.valueOf(25.23)));
    
    assertThat(conversionService.convert("2.32", BigDecimal.class))
        .isEqualTo(BigDecimal.valueOf(2.32));
}
```

## 6. 总结

在本教程中，我们通过各种示例了解了如何使用和扩展Spring的类型转换系统。