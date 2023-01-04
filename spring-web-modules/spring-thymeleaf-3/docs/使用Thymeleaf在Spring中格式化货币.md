## 1. 简介

在本教程中，我们将学习如何使用[Thymeleaf](https://www.thymeleaf.org/)按区域设置货币格式。

## 2. Maven依赖

让我们从导入[Spring Boot Thymeleaf依赖项](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-thymeleaf)开始：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-thymeleaf</artifactId> 
    <version>2.2.7.RELEASE</version>
</dependency>
```

## 3. 项目设置

我们的项目将是一个简单的Spring Web应用程序，它根据用户的区域设置显示货币。让我们在resources/templates/currencies中创建我们的Thymeleaf模板currencies.html：

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Currency table</title>
</head>
</html>
```

我们还可以创建一个控制器类来处理我们的请求：

```java
@Controller
public class CurrenciesController { 
    @GetMapping(value = "/currency")
    public String exchange(@RequestParam(value = "amount") String amount, Locale locale) {
        return "currencies/currencies";
    }
}
```

## 4. 格式化

当谈到货币时，我们需要根据请求者的语言环境来格式化它们。

在这种情况下，我们将在每个请求中发送Accept-Language标头以表示我们用户的语言环境。

### 4.1 货币

Thymeleaf提供的[Numbers](https://www.thymeleaf.org/apidocs/thymeleaf/3.0.11.RELEASE/org/thymeleaf/expression/Numbers.html)类支持格式化货币。那么，让我们通过调用formatCurrency方法来更新我们的视图

```html
<p th:text="${#numbers.formatCurrency(param.amount)}"></p>
```

当我们运行我们的示例时，我们将看到正确格式化的货币：

```java
@Test
public void whenCallCurrencyWithUSALocale_ThenReturnProperCurrency() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/currency")
        .header("Accept-Language", "en-US")
        .param("amount", "10032.5"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("$10,032.50")));
}
```

由于我们将Accept-Language标头设置为美国，因此货币格式为小数点和美元符号。

### 4.2 货币数组

我们也可以使用Numbers类来格式化数组。因此，我们将向我们的控制器添加另一个请求参数：

```java
@GetMapping(value = "/currency")
public String exchange(@RequestParam(value = "amount") String amount, @RequestParam(value = "amountList") List amountList, Locale locale) {
    return "currencies/currencies";
}
```

接下来，我们可以更新视图以包含对listFormatCurrency方法的调用：

```html
<p th:text="${#numbers.listFormatCurrency(param.amountList)}"></p>
```

现在让我们看看结果是什么样的：

```java
@Test
public void whenCallCurrencyWithUkLocaleWithArrays_ThenReturnLocaleCurrencies() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/currency")
        .header("Accept-Language", "en-GB")
        .param("amountList", "10", "20", "30"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("£10.00, £20.00, £30.00")));
}
```

结果显示添加了正确的英国格式的货币列表。

### 4.3 尾随零

使用[Strings#replace](https://www.thymeleaf.org/apidocs/thymeleaf/3.0.11.RELEASE/org/thymeleaf/expression/Strings.html)，我们可以删除尾随的零。

```html
<p th:text="${#strings.replace(#numbers.formatCurrency(param.amount), '.00', '')}"></p>
```

现在我们可以看到没有尾随双零的全部金额：

```java
@Test
public void whenCallCurrencyWithUSALocaleWithoutDecimal_ThenReturnCurrencyWithoutTrailingZeros() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/currency")
        .header("Accept-Language", "en-US")
        .param("amount", "10032"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("$10,032")));
}
```

### 4.4 小数点

根据区域设置，小数的格式可能不同。因此，如果我们想用逗号代替小数点，可以使用Numbers类提供的formatDecimal方法：

```html
<p th:text="${#numbers.formatDecimal(param.amount, 1, 2, 'COMMA')}"></p>
```

让我们看看测试的结果：

```java
@Test
public void whenCallCurrencyWithUSALocale_ThenReturnReplacedDecimalPoint() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/currency")
        .header("Accept-Language", "en-US")
        .param("amount", "1.5"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("1,5")));
}
```

该值将被格式化为“1,5”。

## 5. 总结

在这个简短的教程中，我们展示了如何将Thymeleaf与Spring Web一起使用，以使用用户的区域设置来处理货币。