## 1. 概述

本文演示如何使用Spring的数据绑定机制，通过将自动原语应用于对象转换，使我们的代码更加清晰易读。

默认情况下，Spring只知道如何转换简单类型。
换句话说，一旦我们将数据提交给控制器的Int、String或Boolean类型的参数，它会自动绑定到适当的Java类型。

但在实际项目中，这还不够，因为我们可能需要绑定更复杂的对象类型。

## 2. 将单个对象绑定到请求参数

我们从最简单开始，首先绑定一个简单类型；我们必须提供Converter<S, T\>接口的自定义实现，其中S是我们要转换的原始类型，T是我们要转换的目标类型：

```java
@Component
public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

    @Override
    public LocalDateTime convert(String source) {
        return LocalDateTime.parse(source, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
```

现在，我们可以在控制器中使用以下语法：

```java
@RestController
public class GenericEntityController {

    @GetMapping("/entity/findbydate/{date}")
    public GenericEntity findByDate(@PathVariable("date") LocalDateTime date) {
        // ...
    }
}
```

### 2.1 使用枚举作为请求参数

接下来，我们介绍如何**将枚举用作请求参数**。

在这里，我们有一个简单的枚举Modes：

```java
public enum Modes {

    ALPHA, BETA
}
```

我们构建一个字符串到枚举的转换器，如下所示：

```java
public class StringToEnumConverter implements Converter<String, Modes> {

    @Override
    public Modes convert(String from) {
        return Modes.valueOf(from);
    }
}
```

然后，我们需要注册该转换器：

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToEnumConverter());
    }
}
```

现在我们可以将Modes作为请求参数：

```java
public class GenericEntityController {

    @GetMapping
    public GenericEntity getStringToMode(@RequestParam("mode") Modes mode) {
        // ...
    }
}
```

或者作为路径变量：

```java
public class GenericEntityController {

    @GetMapping("/entity/findbymode/{mode}")
    public GenericEntity findByEnum(@PathVariable("mode") Modes mode) {
        // ...
    }
}
```

## 3. 绑定对象的层次结构

有时我们需要转换对象层次结构的整个树，因此使用更集中的绑定而不是一组单独的转换器是有意义的。

在这个例子中，我们将AbstractEntity作为基类：

```java
public abstract class AbstractEntity {

    long id;

    public AbstractEntity(long id) {
        this.id = id;
    }
}
```

以及子类Foo和Bar：

```java
public class Foo extends AbstractEntity {

    private String name;

    // standard constructors, getters, setters ...
}
```

```java
public class Bar extends AbstractEntity {

    private int value;

    // standard constructors, getters, setters ...
}
```

**在这种情况下，我们可以实现ConverterFactory<S, R\>，其中S将是我们要转换的类型，R是定义我们可以转换为的类范围的基类型**：

```java
public class StringToAbstractEntityConverterFactory implements ConverterFactory<String, AbstractEntity> {

    @Override
    public <T extends AbstractEntity> Converter<String, T> getConverter(Class<T> targetClass) {
        return new StringToAbstractEntityConverter<>(targetClass);
    }

    private static class StringToAbstractEntityConverter<T extends AbstractEntity> implements Converter<String, T> {

        private final Class<T> targetClass;

        public StringToAbstractEntityConverter(Class<T> targetClass) {
            this.targetClass = targetClass;
        }

        @Override
        public T convert(String source) {
            long id = Long.parseLong(source);
            if (this.targetClass == Foo.class) {
                return (T) new Foo(id);
            } else if (this.targetClass == Bar.class) {
                return (T) new Bar(id);
            } else {
                return null;
            }
        }
    }
}
```

如我们所见，唯一必须实现的方法是getConverter()，它返回所需类型的转换器，然后将转换过程委托给该转换器。

然后，我们需要注册这个ConverterFactory：

```java
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new StringToAbstractEntityConverterFactory());
    }
}
```

最后，我们可以在控制器中使用它：

```java
@RestController
@RequestMapping("/string-to-abstract")
public class AbstractEntityController {

    @GetMapping("/foo/{foo}")
    public ResponseEntity<Object> getStringToFoo(@PathVariable Foo foo) {
        return ResponseEntity.ok(foo);
    }

    @GetMapping("/bar/{bar}")
    public ResponseEntity<Object> getStringToBar(@PathVariable Bar bar) {
        return ResponseEntity.ok(bar);
    }
}
```

## 4. 绑定域对象

在某些情况下，我们希望将数据绑定到对象，但它要么以非直接方式(例如，来自Session、Header或Cookie变量)，要么甚至存储在数据源中。
在这些情况下，我们需要使用不同的解决方案。

### 4.1 自定义参数解析器

首先，我们为这些参数定义一个注解：

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Version {

}
```

然后，我们实现一个自定义的HandlerMethodArgumentResolver：

```java
@Component
public class HeaderVersionArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(final MethodParameter methodParameter) {
        return methodParameter.getParameterAnnotation(Version.class) != null;
    }

    @Override
    public Object resolveArgument(final MethodParameter methodParameter, final ModelAndViewContainer modelAndViewContainer, final NativeWebRequest nativeWebRequest, final WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();

        return request.getHeader("Version");
    }
}
```

最后我们需要让Spring知道该参数解析器的存在：

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new HeaderVersionArgumentResolver());
    }
}
```

现在我们可以在控制器中使用它：

```java
@RestController
public class GenericEntityController {

    @GetMapping("/entity/findbyversion")
    public ResponseEntity findByVersion(@Version String version) {
        return version != null ? new ResponseEntity(entityList.stream().findFirst().get(), HttpStatus.OK) : new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
```

我们可以看到，HandlerMethodArgumentResolver的resolveArgument()方法返回一个Object。
换句话说，我们可以返回任何对象，而不仅仅是String。

## 5. 总结

+ 对于单个简单类型到对象的转换，我们应该使用Converter实现。
+ 为了封装一系列对象的转换逻辑，我们可以尝试ConverterFactory的实现。
+ 对于任何间接而来的数据，或者需要应用额外的逻辑来检索相关的数据，最好使用HandlerMethodArgumentResolver。