## 1. 概述

在本教程中，我们将介绍[Smooks 框架](http://www.smooks.org/)。

我们将描述它是什么，列出它的主要特性，并最终学习如何使用它的一些更高级的功能。

首先简单说明一下这个框架要实现什么。

## 2.吸烟

Smooks 是数据处理应用程序的框架——处理结构化数据，例如 XML 或 CSV。

它提供 API 和配置模型，允许我们定义预定义格式(例如 XML 到 CSV、XML 到 JSON 等)之间的转换。

我们还可以使用许多工具来设置我们的映射——包括 FreeMarker 或 Groovy 脚本。

除了转换之外，Smooks 还提供其他功能，例如消息验证或数据拆分。

### 2.1. 主要特征

让我们来看看 Smooks 的主要用例：

-   消息转换——将数据从各种源格式转换为各种输出格式
-   消息充实——用来自数据库等外部数据源的附加数据填充消息
-   数据拆分——处理大文件 (GB) 并将它们拆分成较小的文件
-  Java绑定——从消息构造和填充Java对象
-   消息验证——执行正则表达式等验证，甚至创建自己的验证规则

## 3.初始配置

让我们从需要添加到pom.xml的 Maven 依赖项开始：

```xml
<dependency>
    <groupId>org.milyn</groupId>
    <artifactId>milyn-smooks-all</artifactId>
    <version>1.7.0</version>
</dependency>
```

最新版本可以在[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"org.milyn" AND a%3A"milyn-smooks-all")上找到。

## 4.Java绑定

现在让我们开始关注将消息绑定到Java类。我们将在这里完成一个简单的 XML 到Java的转换。

### 4.1. 基本概念

我们将从一个简单的例子开始。考虑以下 XML：

```xml
<order creation-date="2018-01-14">
    <order-number>771</order-number>
    <order-status>IN_PROGRESS</order-status>
</order>
```

为了使用 Smooks 完成此任务，我们必须做两件事：准备 POJO 和 Smooks 配置。

让我们看看我们的模型是什么样的：

```java
public class Order {

    private Date creationDate;
    private Long number;
    private Status status;
    // ...
}


public enum Status {
    NEW, IN_PROGRESS, FINISHED
}
```

现在，让我们继续进行 Smooks 映射。

基本上，映射是一个包含转换逻辑的 XML 文件。在本文中，我们将使用三种不同类型的规则：

-   bean –定义具体结构部分到Java类的映射
-   value – 定义 bean 特定属性的映射。可以包含更高级的逻辑，如解码器，用于将值映射到某些数据类型(如日期或小数格式)
-   wiring——允许我们将一个 bean 连接到其他 bean(例如Supplier bean 将连接到Order bean)

让我们来看看我们将在我们的案例中使用的映射：

```xml
<?xml version="1.0"?>
<smooks-resource-list 
  xmlns="http://www.milyn.org/xsd/smooks-1.1.xsd"
  xmlns:jb="http://www.milyn.org/xsd/smooks/javabean-1.2.xsd">

    <jb:bean beanId="order" 
      class="com.baeldung.smooks.model.Order" createOnElement="order">
        <jb:value property="number" data="order/order-number" />
        <jb:value property="status" data="order/order-status" />
        <jb:value property="creationDate" 
          data="order/@creation-date" decoder="Date">
            <jb:decodeParam name="format">yyyy-MM-dd</jb:decodeParam>
        </jb:value>
    </jb:bean>
</smooks-resource-list>
```

现在，配置准备就绪，让我们尝试测试我们的 POJO 是否构造正确。

首先，我们需要构造一个 Smooks 对象并将输入 XML 作为流传递：

```java
public Order converOrderXMLToOrderObject(String path) 
  throws IOException, SAXException {
 
    Smooks smooks = new Smooks(
      this.class.getResourceAsStream("/smooks-mapping.xml"));
    try {
        JavaResult javaResult = new JavaResult();
        smooks.filterSource(new StreamSource(this.class
          .getResourceAsStream(path)), javaResult);
        return (Order) javaResult.getBean("order");
    } finally {
        smooks.close();
    }
}
```

最后，断言配置是否正确完成：

```java
@Test
public void givenOrderXML_whenConvert_thenPOJOsConstructedCorrectly() throws Exception {
    XMLToJavaConverter xmlToJavaOrderConverter = new XMLToJavaConverter();
    Order order = xmlToJavaOrderConverter
      .converOrderXMLToOrderObject("/order.xml");

    assertThat(order.getNumber(), is(771L));
    assertThat(order.getStatus(), is(Status.IN_PROGRESS));
    assertThat(
      order.getCreationDate(), 
      is(new SimpleDateFormat("yyyy-MM-dd").parse("2018-01-14"));
}
```

### 4.2. 高级绑定——引用其他 Bean 和列表

让我们用supplier和order-items标签扩展我们之前的例子：

```xml
<order creation-date="2018-01-14">
    <order-number>771</order-number>
    <order-status>IN_PROGRESS</order-status>
    <supplier>
        <name>Company X</name>
        <phone>1234567</phone>
    </supplier>
    <order-items>
        <item>
            <quanitiy>1</quanitiy>
            <code>PX1234</code>
            <price>9.99</price>
        </item>
        <item>
            <quanitiy>1</quanitiy>
            <code>RX990</code>
            <price>120.32</price>
        </item>
    </order-items>
</order>
```

现在让我们更新我们的模型：

```java
public class Order {
    // ..
    private Supplier supplier;
    private List<Item> items;
    // ...
}
public class Item {

    private String code;
    private Double price;
    private Integer quantity;
    // ...
}

public class Supplier {

    private String name;
    private String phoneNumber;
    // ...
}
```

我们还必须使用供应商和项目bean 定义来扩展配置映射。

请注意，我们还定义了分离项bean，它将保存ArrayList中的所有项元素。

最后，我们将使用 Smooks布线属性，将它们捆绑在一起。

看一下映射在这种情况下的样子：

```xml
<?xml version="1.0"?>
<smooks-resource-list 
  xmlns="http://www.milyn.org/xsd/smooks-1.1.xsd"
  xmlns:jb="http://www.milyn.org/xsd/smooks/javabean-1.2.xsd">

    <jb:bean beanId="order" 
      class="com.baeldung.smooks.model.Order" createOnElement="order">
        <jb:value property="number" data="order/order-number" />
        <jb:value property="status" data="order/order-status" />
        <jb:value property="creationDate" 
          data="order/@creation-date" decoder="Date">
            <jb:decodeParam name="format">yyyy-MM-dd</jb:decodeParam>
        </jb:value>
        <jb:wiring property="supplier" beanIdRef="supplier" />
        <jb:wiring property="items" beanIdRef="items" />
    </jb:bean>

    <jb:bean beanId="supplier" 
      class="com.baeldung.smooks.model.Supplier" createOnElement="supplier">
        <jb:value property="name" data="name" />
        <jb:value property="phoneNumber" data="phone" />
    </jb:bean>

    <jb:bean beanId="items" 
      class="java.util.ArrayList" createOnElement="order">
        <jb:wiring beanIdRef="item" />
    </jb:bean>
    <jb:bean beanId="item" 
      class="com.baeldung.smooks.model.Item" createOnElement="item">
        <jb:value property="code" data="item/code" />
        <jb:value property="price" decoder="Double" data="item/price" />
        <jb:value property="quantity" decoder="Integer" data="item/quantity" />
    </jb:bean>

</smooks-resource-list>
```

最后，我们将在之前的测试中添加一些断言：

```java
assertThat(
  order.getSupplier(), 
  is(new Supplier("Company X", "1234567")));
assertThat(order.getItems(), containsInAnyOrder(
  new Item("PX1234", 9.99,1),
  new Item("RX990", 120.32,1)));
```

## 5.消息验证

Smooks 带有基于规则的验证机制。让我们来看看它们是如何使用的。

规则的定义存储在配置文件中，嵌套在ruleBases标签中，它可以包含许多ruleBase元素。

每个ruleBase元素必须具有以下属性：

-   name –唯一名称，仅供参考
-   src –规则源文件的路径
-   provider – 完全限定的类名，它实现了 RuleProvider接口

Smooks 附带两个开箱即用的提供程序：RegexProvider和MVELProvider。

第一个用于验证类似正则表达式样式的各个字段。

第二个用于在文档的全局范围内执行更复杂的验证。让我们看看他们的行动。

### 5.1. 正则表达式提供者

让我们使用RegexProvider来验证两件事：客户姓名的格式和电话号码。作为源的RegexProvider需要一个Java属性文件，该文件应包含以键值方式进行的正则表达式验证。

为了满足我们的要求，我们将使用以下设置：

```plaintext
supplierName=[A-Za-z0-9]
supplierPhone=^[0-9-+]{9,15}$
```

### 5.2. MVEL提供者

我们将使用MVELProvider来验证每个订单商品的总价是否低于 200。作为来源，我们将准备一个包含两列的 CSV 文件：规则名称和 MVEL 表达式。

为了检查价格是否正确，我们需要以下条目：

```plaintext
"max_total","orderItem.quantity  orderItem.price < 200.00"
```

### 5.3. 验证配置

一旦我们准备好ruleBases的源文件，我们将继续实施具体的验证。

验证是 Smooks 配置中的另一个标记，它包含以下属性：

-   executeOn——经过验证的元素的路径
-   名称——对规则库的引用
-   onFail – 指定验证失败时将采取的操作

让我们将验证规则应用于我们的 Smooks 配置文件并检查它的外观(请注意，如果我们想要使用MVELProvider，我们将被迫使用Java绑定，因此这就是我们导入以前的 Smooks 配置的原因)：

```xml
<?xml version="1.0"?>
<smooks-resource-list 
  xmlns="http://www.milyn.org/xsd/smooks-1.1.xsd"
  xmlns:rules="http://www.milyn.org/xsd/smooks/rules-1.0.xsd"
  xmlns:validation="http://www.milyn.org/xsd/smooks/validation-1.0.xsd">

    <import file="smooks-mapping.xml" />

    <rules:ruleBases>
        <rules:ruleBase 
          name="supplierValidation" 
          src="supplier.properties" 
          provider="org.milyn.rules.regex.RegexProvider"/>
        <rules:ruleBase 
          name="itemsValidation" 
          src="item-rules.csv" 
          provider="org.milyn.rules.mvel.MVELProvider"/>
    </rules:ruleBases>

    <validation:rule 
      executeOn="supplier/name" 
      name="supplierValidation.supplierName" onFail="ERROR"/>
    <validation:rule 
      executeOn="supplier/phone" 
      name="supplierValidation.supplierPhone" onFail="ERROR"/>
    <validation:rule 
      executeOn="order-items/item" 
      name="itemsValidation.max_total" onFail="ERROR"/>

</smooks-resource-list>
```

现在，配置准备就绪，让我们尝试测试供应商电话号码的验证是否会失败。

同样，我们必须构造Smooks对象并将输入 XML 作为流传递：

```java
public ValidationResult validate(String path) 
  throws IOException, SAXException {
    Smooks smooks = new Smooks(OrderValidator.class
      .getResourceAsStream("/smooks/smooks-validation.xml"));
    try {
        StringResult xmlResult = new StringResult();
        JavaResult javaResult = new JavaResult();
        ValidationResult validationResult = new ValidationResult();
        smooks.filterSource(new StreamSource(OrderValidator.class
          .getResourceAsStream(path)), xmlResult, javaResult, validationResult);
        return validationResult;
    } finally {
        smooks.close();
    }
}

```

最后断言，如果发生验证错误：

```java
@Test
public void givenIncorrectOrderXML_whenValidate_thenExpectValidationErrors() throws Exception {
    OrderValidator orderValidator = new OrderValidator();
    ValidationResult validationResult = orderValidator
      .validate("/smooks/order.xml");

    assertThat(validationResult.getErrors(), hasSize(1));
    assertThat(
      validationResult.getErrors().get(0).getFailRuleResult().getRuleName(), 
      is("supplierPhone"));
}
```

## 6. 消息转换

接下来我们要做的是将消息从一种格式转换为另一种格式。

在 Smooks 中，这种技术也称为模板化，它支持：

-   FreeMarker(首选选项)
-   XSL
-   字符串模板

在我们的示例中，我们将使用 FreeMarker 引擎将 XML 消息转换为与 EDIFACT 非常相似的内容，甚至为基于 XML 顺序的电子邮件消息准备一个模板。

让我们看看如何为 EDIFACT 准备模板：

```plaintext
UNA:+.? '
UNH+${order.number}+${order.status}+${order.creationDate?date}'
CTA+${supplier.name}+${supplier.phoneNumber}'
<#list items as item>
LIN+${item.quantity}+${item.code}+${item.price}'
</#list>
```

对于电子邮件：

```plaintext
Hi,
Order number #${order.number} created on ${order.creationDate?date} is currently in ${order.status} status.
Consider contacting the supplier "${supplier.name}" with phone number: "${supplier.phoneNumber}".
Order items:
<#list items as item>
${item.quantity} X ${item.code} (total price ${item.price  item.quantity})
</#list>
```

这次 Smooks 配置非常基本(只记得导入之前的配置以导入Java绑定设置)：

```xml
<?xml version="1.0"?>
<smooks-resource-list 
  xmlns="http://www.milyn.org/xsd/smooks-1.1.xsd"
  xmlns:ftl="http://www.milyn.org/xsd/smooks/freemarker-1.1.xsd">

    <import file="smooks-validation.xml" />

    <ftl:freemarker applyOnElement="#document">
        <ftl:template>/path/to/template.ftl</ftl:template>
    </ftl:freemarker>

</smooks-resource-list>
```

这次我们只需要将StringResult传递给 Smooks 引擎：

```java
Smooks smooks = new Smooks(config);
StringResult stringResult = new StringResult();
smooks.filterSource(new StreamSource(OrderConverter.class
  .getResourceAsStream(path)), stringResult);
return stringResult.toString();
```

我们当然可以测试它：

```java
@Test
public void givenOrderXML_whenApplyEDITemplate_thenConvertedToEDIFACT()
  throws Exception {
    OrderConverter orderConverter = new OrderConverter();
    String edifact = orderConverter.convertOrderXMLtoEDIFACT(
      "/smooks/order.xml");

   assertThat(edifact,is(EDIFACT_MESSAGE));
}
```

## 七. 总结

在本教程中，我们重点介绍了如何将消息转换为不同的格式，或者使用 Smooks 将它们转换为Java对象。我们还了解了如何根据正则表达式或业务逻辑规则执行验证。