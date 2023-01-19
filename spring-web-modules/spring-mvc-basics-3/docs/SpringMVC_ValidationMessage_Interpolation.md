## 1. 概述

消息插值是用于为Java Bean Validation约束创建错误消息的过程。
例如，我们可以通过为使用javax.validation.constraints.NotNull注解的字段提供空值来查看错误消息。

在本教程中，我们学习如何使用默认的Spring消息插值以及如何创建我们自己的插值机制。

## 2. 默认消息插值

首先，让我们考虑一个带有默认@NotNull注解违规消息的HTTP 400响应示例：

```json
{
    ....
    "status": 400,
    "error": "Bad Request",
    "errors": [
        {
            ....
            "defaultMessage": "must not be null",
            ....
        }
    ],
    "message": "Validation failed for object='notNullRequest'. Error count: 1",
    ....
}
```

**Spring从消息描述符中检索约束冲突消息详细信息**。
每个约束使用message属性定义其默认消息描述符，当然，我们可以用自定义值覆盖它。

例如，我们使用POST方法创建一个简单的REST控制器：

```java

@RestController
public class ValidationController {

    @PostMapping("/test-not-null")
    public void testNotNull(@Valid @RequestBody NotNullRequest request) {

    }
}
```

请求体会映射到NotNullRequest对象，该对象只有一个带有@NotNull注解的String属性：

```java

@Getter
@Setter
public class NotNullRequest {

    @NotNull(message = "stringValue has to be present")
    private String stringValue;
}
```

现在，当我们发送一个POST请求，但该验证检查失败时，我们可以看到自定义的错误消息：

```json
{
    ...
    "errors": [
        {
            ...
            "defaultMessage": "stringValue has to be present",
            ...
        }
    ],
    ...
}
```

唯一改变的值是defaultMessage，但是我们仍然会得到很多关于错误码、对象名称、字段名等的大量信息。
为了限制显示值的数量，我们可以实现REST API的自定义错误消息处理。

## 3. 使用消息表达式进行插值

**在Spring中，我们可以使用统一表达式语言来定义我们的消息描述符，这允许根据条件逻辑定义错误消息，还可以启用高级格式化选项**。

为了更清楚地理解它，让我们看几个例子。

在每个约束注解中，我们都可以访问正在验证的字段的实际值：

```java
public class ValidationExamples {

    @Size(
            min = 5,
            max = 14,
            message = "The author email '${validatedValue}' must be between {min} and {max} characters long"
    )
    private String authorEmail;
}
```

最终的错误消息将包含属性的实际值以及@Size注解的min和max参数：

```text
"defaultMessage": "The author email 'toolongemail@gmail.com' must be between 5 and 14 characters long"
```

请注意，对于访问外部变量，我们使用${}语法，但对于从验证注解访问其他属性，我们使用{}。

也可以使用三元运算符：

```java
public class ValidationExamples {

    @Min(
            value = 1,
            message = "There must be at least {value} test{value > 1 ? 's' : ''} in the test case"
    )
    private int testCount;
}
```

Spring会将三元运算符转换为错误消息中的单个值：

```text
"defaultMessage": "There must be at least 2 tests in the test case"
```

我们也可以调用外部变量的方法：

```java
public class ValidationExamples {

    private static final Formatter formatter = new Formatter();

    @DecimalMin(
            value = "50",
            message = "The code coverage ${formatter.format('%1$.2f', validatedValue)} must be higher than {value}%"
    )
    private double codeCoverage;
}
```

无效的输入会产生带有格式化值的错误消息：

```text
"defaultMessage": "The code coverage 44.44 must be higher than 50%"
```

从这些例子中可以看出，消息表达式中使用了“{}”、“$”和“/”等字符，因此我们需要在使用它们之前用反斜杠字符对它们进行转义：“\{\}”、“\$”和“\\”。

## 4. 自定义消息插值

在某些情况下，我们希望实现自定义消息插值引擎，为此，我们必须首先实现javax.validation.MessageInterpolator接口：

```java
public class MyMessageInterpolator implements MessageInterpolator {

    private static final Logger logger = LoggerFactory.getLogger(MyMessageInterpolator.class);

    private final MessageInterpolator defaultInterpolator;

    public MyMessageInterpolator(MessageInterpolator interpolator) {
        this.defaultInterpolator = interpolator;
    }

    @Override
    public String interpolate(String messageTemplate, Context context) {
        messageTemplate = messageTemplate.toUpperCase();
        return defaultInterpolator.interpolate(messageTemplate, context, Locale.getDefault());
    }

    @Override
    public String interpolate(String messageTemplate, Context context, Locale locale) {
        messageTemplate = messageTemplate.toUpperCase();
        return defaultInterpolator.interpolate(messageTemplate, context, locale);
    }
}
```

在这个简单的实现中，我们只是将错误消息更改为大写，此时，我们错误消息如下所示：

```text
"defaultMessage": "THE CODE COVERAGE 44.44 MUST BE HIGHER THAN 50%"
```

我们还需要在javax.validation.Validation工厂中注册我们的插值器：

```text
Validation.byDefaultProvider().configure().messageInterpolator(
    new MyMessageInterpolator(Validation.byDefaultProvider().configure().getDefaultMessageInterpolator())
);
```

## 5. 总结

在本文中，我们了解了默认Spring消息插值的工作原理以及如何创建自定义消息插值引擎。