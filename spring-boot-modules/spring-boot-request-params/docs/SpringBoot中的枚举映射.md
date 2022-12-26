## 1. 概述

在本教程中，我们将探讨在Spring Boot中实现不区分大小写的枚举映射的不同方法。

首先，我们将了解枚举在Spring中默认是如何映射的。然后，我们将学习如何应对区分大小写的挑战。

## 2. Spring默认枚举映射

Spring在处理请求参数时依赖于几个内置的转换器来处理字符串转换。

通常，当我们将枚举作为请求参数传递时，**它会在后台使用**[StringToEnumConverterFactory](https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/convert/support/StringToEnumConverterFactory.java)**将传递的字符串转换为枚举**。

根据设计，此转换器调用Enum.valueOf(Class, String)，**这意味着给定的字符串必须与声明的枚举常量之一完全匹配**。

例如，让我们考虑Level枚举：

```java
public enum Level {
    LOW, MEDIUM, HIGH
}
```

接下来，让我们创建一个接收枚举作为参数的[处理程序方法]()：

```java
@RestController
@RequestMapping("enummapping")
public class EnumMappingController {

    @GetMapping("/get")
    public String getByLevel(@RequestParam(required = false) Level level){
        return level.name();
    }
}
```

让我们使用[CURL]()向http://localhost:8080/enummapping/get?level=MEDIUM发送请求：

```bash
curl http://localhost:8080/enummapping/get?level=MEDIUM
```

处理程序方法发回MEDIUM，即枚举常量MEDIUM的名称。

现在，让我们传递medium而不是MEDIUM，看看会发生什么：

```bash
curl http://localhost:8080/enummapping/get?level=medium
{"timestamp":"2022-12-26T12:41:11.440+00:00","status":400,"error":"Bad Request","path":"/enummapping/get"}
```

正如我们所看到的，请求被认为是无效的并且应用程序失败并出现错误：

```shell
Failed to convert value of type 'java.lang.String' to required type 'cn.tuyucheng.taketoday.enummapping.enums.Level'; 
nested exception is org.springframework.core.convert.ConversionFailedException: Failed to convert from type [java.lang.String] to type [@org.springframework.web.bind.annotation.RequestParam cn.tuyucheng.taketoday.enummapping.enums.Level] for value 'medium'; 
...
```

查看异常的堆栈跟踪，我们可以看到Spring抛出了ConversionFailedException，它没有将medium识别为枚举常量。

## 3. 不区分大小写的枚举映射

Spring提供了几种方便的方法来解决映射枚举时区分大小写的问题，接下来我们逐一介绍。

### 3.1 使用ApplicationConversionService

[ApplicationConversionService](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/convert/ApplicationConversionService.html)类附带有一组已配置的转换器和格式化程序。

在这些开箱即用的转换器中，我们可以找到StringToEnumIgnoringCaseConverterFactory。**顾名思义，它以不区分大小写的方式将字符串转换为枚举**。

首先，我们需要添加和配置ApplicationConversionService：

```java
@Configuration
public class EnumMappingConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        ApplicationConversionService.configure(registry);
    }
}
```

此类使用适用于大多数Spring Boot应用程序的现成转换器配置[FormatterRegistry](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/format/FormatterRegistry.html)。

现在，让我们使用测试用例确认一切都按预期工作：

```java
@RunWith(SpringRunner.class)
@WebMvcTest(EnumMappingController.class)
public class EnumMappingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenPassingLowerCaseEnumConstant_thenConvert() throws Exception {
        mockMvc.perform(get("/enummapping/get?level=medium"))
              .andExpect(status().isOk())
              .andExpect(content().string(Level.MEDIUM.name()));
    }
}
```

正如我们所见，作为参数传递的medium值已成功转换为MEDIUM。

### 3.2 使用自定义转换器

另一种解决方案是使用自定义转换器，在这里，我们将使用Apache Commons Lang3库。

首先，我们需要添加它的[依赖](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.commons" AND a%3A"commons-lang3")：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

**这里的基本思想是创建一个转换器，将Level常量的字符串表示形式转换为真正的Level常量**：

```java
public class StringToLevelConverter implements Converter<String, Level> {

    @Override
    public Level convert(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        return EnumUtils.getEnum(Level.class, source.toUpperCase());
    }
}
```

**从技术角度来看，自定义转换器是一个实现Converter<S,T\>接口的简单类**。

如我们所见，我们将String对象转换为大写；然后，我们使用Apache Commons Lang3库中的[EnumUtils](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/EnumUtils.html)工具类从大写字符串中获取Level常量。

现在，让我们添加拼图中最后一个缺失的部分，我们需要告诉Spring我们新的自定义转换器。为此，我们将使用与之前相同的FormatterRegistry，**它提供了addConverter()方法来注册自定义转换器**：

```java
@Override
public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new StringToLevelConverter());
}
```

就是这样，我们的StringToLevelConverter现在在[ConversionService](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/convert/ConversionService.html)中可用。

现在，我们可以像使用任何其他转换器一样使用它：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EnumMappingMainApplication.class)
public class StringToLevelConverterIntegrationTest {

    @Autowired
    ConversionService conversionService;

    @Test
    public void whenConvertStringToLevelEnumUsingCustomConverter_thenSuccess() {
        assertThat(conversionService.convert("low", Level.class)).isEqualTo(Level.LOW);
    }
}
```

如上所示，测试用例确认“low”值已转换为Level.LOW。

### 3.3 使用自定义属性编辑器

Spring在后台使用多个内置[属性编辑器]()来管理String值和Java对象之间的转换。

同样，我们可以创建一个自定义属性编辑器来将String对象映射到Level常量。

例如，让我们将自定义编辑器命名为LevelEditor：

```java
public class LevelEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {
        if (StringUtils.isBlank(text)) {
            setValue(null);
        } else {
            setValue(EnumUtils.getEnum(Level.class, text.toUpperCase()));
        }
    }
}
```

正如我们所看到的，我们需要扩展[PropertyEditorSupport](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/java/beans/PropertyEditorSupport.html)类并覆盖setAsText()方法。

**覆盖setAsText()的想法是将给定字符串的大写版本转换为Level枚举**。

值得注意的是，PropertyEditorSupport也提供了getAsText()方法，它在将Java对象序列化为字符串时调用。所以，我们不需要在这里重写它。

我们需要注册我们的LevelEditor，因为Spring不会自动检测自定义属性编辑器。**为此，我们需要在**[Spring控制器](https://www.baeldung.com/spring-controllers)**中创建一个用@InitBinder注解标注的方法**：

```java
@InitBinder
public void initBinder(WebDataBinder dataBinder) {
    dataBinder.registerCustomEditor(Level.class, new LevelEditor());
}
```

现在我们将所有部分放在一起，让我们确认我们的自定义属性编辑器LevelEditor使用测试用例工作：

```java
public class LevelEditorIntegrationTest {

    @Test
    public void whenConvertStringToLevelEnumUsingCustomPropertyEditor_thenSuccess() {
        LevelEditor levelEditor = new LevelEditor();
        levelEditor.setAsText("lOw");

        assertThat(levelEditor.getValue()).isEqualTo(Level.LOW);
    }
}
```

这里要提到的另一件重要的事情是EnumUtils.getEnum()在找到时返回枚举，否则返回null。

所以，为了避免NullPointerException，我们需要稍微改变我们的处理方法：

```java
public String getByLevel(@RequestParam(required = false) Level level) {
    if (level != null) {
        return level.name();
    }
    return "undefined";
}
```

现在，让我们添加一个简单的测试用例来测试它：

```java
@Test
public void whenPassingUnknownEnumConstant_thenReturnUndefined() throws Exception {
    mockMvc.perform(get("/enummapping/get?level=unknown"))
        .andExpect(status().isOk())
        .andExpect(content().string("undefined"));
}
```

## 4. 总结

在本文中，我们学习了在Spring中实现不区分大小写的枚举映射的多种方法。

在此过程中，我们研究了一些使用内置和自定义转换器来完成此操作的方法。然后，我们了解了如何使用自定义属性编辑器实现相同的目标。