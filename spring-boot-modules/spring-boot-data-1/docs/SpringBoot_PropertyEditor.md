## 1. property editors介绍

简单地说，Spring大量使用属性编辑器来管理String值和自定义对象类型之间的转换：这是基于Java Beans PropertyEditor实现的。

在本教程中，我们将介绍两个不同的用例，用于演示自动属性编辑器绑定和自定义属性编辑器绑定。

## 2. 自动属性编辑器绑定

如果PropertyEditor类与它们处理的类在同一个包中，标准JavaBeans基础结构将自动发现它们。
此外，这些需要与该类具有相同的名称加上Editor后缀。

例如，如果我们创建一个CreditCard模型类，那么我们应该将属性编辑器命名为CreditCardEditor。

现在让我们看一个实际的属性绑定示例。

在我们的场景中，我们将在请求URL中将信用卡号作为路径变量传递，并将该值绑定为CreditCard对象。

让我们首先创建CreditCard模型类，定义字段rawCardNumber、银行标识号bankIdNo(前6位)、
帐号accountNo(7到15为)和校验码checkCode(最后一位)：

```java

@Data
public class CreditCard {
    private String rawCardNumber;
    private Integer bankIdNo;
    private Integer accountNo;
    private Integer checkCode;
}
```

接下来，我们将创建CreditCardEditor类。该类实现将作为字符串给出的信用卡号转换为CreditCard对象的业务逻辑。

**属性编辑器类应该继承PropertyEditorSupport并实现getAsText()和setAsText()方法**：

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

getAsText()方法在将对象序列化为String时调用，而setAsText()用于将String转换为另一个对象。

由于这两个类位于同一个包中，我们不需要做任何其他事情来为类型CreditCard绑定编辑器。

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

当我们请求localhost:8080/property-editor/credit-card/1234-1234-1111-0019时，会获得以下json响应：

```json
{
    "rawCardNumber": "1234-1234-1111-0011",
    "bankIdNo": 123412,
    "accountNo": 341111001,
    "checkCode": 9
}
```

## 3. 自定义属性编辑器绑定

以上自动绑定是因为我们严格遵守了属性编辑器类和Java Bean类在同一包下，且属性编辑器类名为Java Bean类名+Editor后缀。

但实际中可能出于需求不能严格遵守该约定，此时我们必须在属性编辑器类和Java Bean类之间自定义一个绑定。

在我们的自定义属性编辑器绑定场景中，一个字符串值将作为路径变量传递到URL中，
我们将该值绑定为一个ExoticType对象，该对象仅将该值作为属性保留。

首先创建一个简单ExoticType类，注意，在我的项目代码中，该类处于cn.tuyucheng.taketoday.propertyeditor.exotictype.model包下：

```java

@Data
public class ExoticType {
    private String name;
}
```

然后创建一个CustomExoticTypeEditor继承PropertyEditorSupport类，
该类位于cn.tuyucheng.taketoday.propertyeditor.exotictype.editor包下

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

现在我们有了基本的两个类，但此时并不能使用。
由于Spring无法检测到CustomExoticTypeEditor，我们需要在控制器类中使用@InitBinder标注一个方法来注册属性编辑器:

```java
public class PropertyEditorRestController {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(ExoticType.class, new CustomExoticTypeEditor());
    }
}
```

然后我们就可以将输入的字符串绑定到一个ExoticType对象：

```java
public class PropertyEditorRestController {

    @GetMapping(value = "/exotic-type/{value}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ExoticType parseExoticType(@PathVariable("value") ExoticType exoticType) {
        return exoticType;
    }
}
```

当我们请求localhost:8080/property-editor/exotic-type/passion-fruit时，会获得以下json响应：

```json
{
    "name": "PASSION-FRUIT"
}
```

## 4. 总结

在这篇文章中，我们了解了如何使用自动和自定义属性编辑器绑定将人类可读的String值转换为复杂的Java类型。