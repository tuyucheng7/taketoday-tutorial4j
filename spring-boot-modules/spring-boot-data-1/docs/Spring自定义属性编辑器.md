## 1. 简介

简单地说，Spring大量使用属性编辑器来管理String值和自定义Object类型之间的转换：这是基于[Java Beans PropertyEditor](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/java/beans/PropertyEditor.html)实现的。

在本教程中，我们将通过两个不同的用例来演示**自动属性编辑器绑定和自定义属性编辑器绑定**。

## 2. 自动属性编辑器绑定

如果PropertyEditor类与它们处理的类在同一个包中，则标准JavaBeans基础结构将自动发现它们。此外，这些需要与该类具有相同的名称加上Editor后缀。

例如，如果我们创建一个CreditCard模型类，那么我们应该将属性编辑器类命名为CreditCardEditor。

现在让我们来看一个**实用的属性绑定示例**。

在我们的场景中，我们将在请求URL中将信用卡号作为路径变量传递，并将该值绑定为CreditCard对象。

让我们首先创建CreditCard模型类，定义字段rawCardNumber、银行标识号bankIdNo(前6位数字)、帐号accountNo(7到15位数字)和校验码checkCode(最后1位)：

```java
public class CreditCard {

    private String rawCardNumber;
    private Integer bankIdNo;
    private Integer accountNo;
    private Integer checkCode;

    // standard constructor, getters, setters
}
```

接下来，我们将创建CreditCardEditor类。该类实现了将作为字符串给出的信用卡号转换为CreditCard对象的业务逻辑。

**属性编辑器类应该扩展PropertyEditorSupport并实现getAsText()和setAsText()方法**：

```java
public class CreditCardEditor extends PropertyEditorSupport {

    @Override
    public String getAsText() {
        CreditCard creditCard = (CreditCard) getValue();
        return creditCard == null ? "" : creditCard.getRawCardNumber();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (!StringUtils.hasLength(text)) {
            setValue(null);
        } else {
            CreditCard creditCard = new CreditCard();
            creditCard.setRawCardNumber(text);
            String cardNo = text.replaceAll("-", "");
            if (cardNo.length() != 16)
                throw new IllegalArgumentException("Credit card format should be xxxx-xxxx-xxxx-xxxx");
            try {
                creditCard.setBankIdNo(Integer.valueOf(cardNo.substring(0, 6)));
                creditCard.setAccountNo(Integer.valueOf(cardNo.substring(6, cardNo.length() - 1)));
                creditCard.setCheckCode(Integer.valueOf(cardNo.substring(cardNo.length() - 1)));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(e);
            }
            setValue(creditCard);
        }
    }
}
```

**getAsText()方法在将对象序列化为String时调用，而setAsText()用于将String转换为另一个对象**。

由于这两个类位于同一个包中，因此我们不需要执行任何其他操作来绑定CreditCard类型的编辑器。

我们现在可以将其公开为REST API中的资源；该操作将信用卡号作为请求路径变量，Spring将该文本值绑定为CreditCard对象并将其作为方法参数传递：

```java
@RestController
@RequestMapping(value = "/property-editor")
public class PropertyEditorRestController {

    @GetMapping(value = "/credit-card/{card-no}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CreditCard parseCreditCardNumber(@PathVariable("card-no") CreditCard creditCard) {
        return creditCard;
    }
}
```

例如，对于示例请求URL /property-editor/credit-card/1234-1234-1111-0019，我们将得到响应：

```json
{
    "rawCardNumber": "1234-1234-1111-0011",
    "bankIdNo": 123412,
    "accountNo": 341111001,
    "checkCode": 9
}
```

## 3. 自定义属性编辑器绑定

以上自动绑定是因为我们严格遵守了属性编辑器类和Java Bean类在同一包下的约定，且属性编辑器类名为Java Bean类名+Editor后缀。

但实际中可能出于需求不能严格遵守该约定，此时我们必须在属性编辑器类和Java Bean类之间自定义一个绑定。

在我们的自定义属性编辑器绑定场景中，一个字符串值将作为路径变量传递到URL中，我们将该值绑定为一个ExoticType对象，该对象仅将该值保留为一个属性。

与第2节一样，我们首先创建一个模型类ExoticType。注意，在我的项目代码中，该类处于cn.tuyucheng.taketoday.propertyeditor.exotictype.model包下：

```java
public class ExoticType {
    private String name;

    // standard constructor, getters, setters
}
```

我们的自定义属性编辑器类CustomExoticTypeEditor再次扩展PropertyEditorSupport，该类位于cn.tuyucheng.taketoday.propertyeditor.exotictype.editor包下

```java
public class CustomExoticTypeEditor extends PropertyEditorSupport {

    @Override
    public String getAsText() {
        ExoticType exoticType = (ExoticType) getValue();
        return exoticType == null ? "" : exoticType.getName();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        ExoticType exoticType = new ExoticType();
        exoticType.setName(text.toUpperCase());
        setValue(exoticType);
    }
}
```

由于Spring无法检测到CustomExoticTypeEditor，**我们需要在我们的控制器类中使用@InitBinder标注的方法来注册属性编辑器**：

```java
@InitBinder
public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(ExoticType.class, new CustomExoticTypeEditor());
}
```

然后我们可以将用户输入绑定到ExoticType对象：

```java
@GetMapping(value = "/exotic-type/{value}",
    produces = MediaType.APPLICATION_JSON_VALUE)
public ExoticType parseExoticType(@PathVariable("value") ExoticType exoticType) {
    return exoticType;
}
```

对于示例请求URL /property-editor/exotic-type/passion-fruit，我们会得到以下响应：

```json
{
    "name": "PASSION-FRUIT"
}
```

## 4. 总结

在这篇简短的文章中，我们了解了如何使用自动和自定义属性编辑器绑定将人类可读的String值转换为复杂的Java类型。