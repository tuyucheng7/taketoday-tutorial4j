## 1. 概述

在本系列的[第一篇文章](https://www.baeldung.com/spring-mvc-form-tutorial)中，我们介绍了表单标签库的使用以及如何将数据绑定到控制器。

在本文中，我们将介绍Spring MVC提供的各种标签来帮助我们创建和验证表单。

## 2. input标签

我们将从input标签开始。该标签默认使用绑定值和type='text'呈现HTML输入标签：

```html
<form:input path="name" />
```

从Spring 3.1开始，你可以使用其他特定于HTML5的类型，例如电子邮件、日期等。例如，如果我们想创建一个电子邮件字段，我们可以使用type='email'：

```html
<form:input type="email" path="email" />
```

类似地，要创建一个日期字段，我们可以使用type='date'，它将在许多与HTML5兼容的浏览器中呈现一个日期选择器：

```html
<form:input type="date" path="dateOfBirth" />
```

## 3. password标签

此标签使用绑定值呈现type='password'的HTML输入标签。此HTML输入掩盖了输入到字段中的值：

```html
<form:password path="password" />
```

## 4. textarea标签

此标签呈现HTML文本区域：

```html
<form:textarea path="notes" rows="3" cols="20"/>
```

我们可以用与HTML textarea相同的方式指定行数和列数。

## 5. checkbox和checkboxes标签

checkbox标签呈现一个带有type='checkbox'的HTML输入标签。Spring MVC的表单标签库为复选框标签提供了不同的方法，应该可以满足我们所有的复选框需求：

```html
<form:checkbox path="receiveNewsletter" />
```

上面的例子生成了一个经典的单复选框，带有一个布尔值。如果我们将绑定值设置为true，则默认情况下将选中此复选框。

以下示例生成多个复选框。在这种情况下，复选框值在JSP页面中被硬编码：

```html
Bird watching: <form:checkbox path="hobbies" value="Bird watching"/>
Astronomy: <form:checkbox path="hobbies" value="Astronomy"/>
Snowboarding: <form:checkbox path="hobbies" value="Snowboarding"/>
```

在这里，绑定值是数组或java.util.Collection类型：

```java
String[] hobbies;
```

checkboxes标签的用途是用来呈现多个复选框，其中复选框值是在运行时生成的：

```html
<form:checkboxes items="${favouriteLanguageItem}" path="favouriteLanguage" />
```

为了生成值，我们传入一个Array、一个List或一个Map，其中包含items属性中的可用选项。我们可以在控制器中初始化我们的值：

```java
List<String> favouriteLanguageItem = new ArrayList<String>();
favouriteLanguageItem.add("Java");
favouriteLanguageItem.add("C++");
favouriteLanguageItem.add("Perl");
```

通常，绑定属性是一个集合，因此它可以保存用户选择的多个值：

```java
List<String> favouriteLanguage;
```

## 6. radiobutton和radiobuttons标签

此标签呈现一个type='radio'的HTML输入标签：

```html
Male: <form:radiobutton path="sex" value="M"/>
Female: <form:radiobutton path="sex" value="F"/>
```

典型的使用模式将涉及多个标签实例，这些实例具有绑定到同一属性的不同值：

```html
private String sex;
```

就像checkboxes标签一样，radiobuttons标签呈现多个带有type='radio'的HTML输入标签：

```html
<form:radiobuttons items="${jobItem}" path="job" />
```

在这种情况下，我们可能希望将可用选项作为Array、List或Map传递，其中包含items属性中的可用选项：

```java
List<String> jobItem = new ArrayList<String>();
jobItem.add("Full time");
jobItem.add("Part time");
```

## 7. select标签

此标签呈现HTML选择元素：

```html
<form:select path="country" items="${countryItems}" />
```

为了生成值，我们传入一个Array、一个List或一个Map，其中包含items属性中的可用选项。再一次，我们可以在控制器中初始化我们的值：

```java
Map<String, String> countryItems = new LinkedHashMap<String, String>();
countryItems.put("US", "United States");
countryItems.put("IT", "Italy");
countryItems.put("UK", "United Kingdom");
countryItems.put("FR", "France");
```

select标签还支持使用嵌套的option和options标签。

option标签呈现单个HTML option，而options标签呈现HTML option标签列表。

options标签采用包含items属性中可用选项的Array、List或Map，就像select标签一样：

```html
<form:select path="book">
    <form:option value="-" label="--Please Select--"/>
    <form:options items="${books}" />
</form:select>
```

当我们有一次选择多个项目的需要时，我们可以创建一个多列表框。要呈现这种类型的列表，只需在select标签中添加multiple="true"属性

```html
<form:select path="fruit" items="${fruit}" multiple="true"/>
```

这里的绑定属性是一个数组或一个java.util.Collection：

```java
List<String> fruit;
```

## 8. hidden标签

此标签使用绑定值呈现type='hidden'的HTML输入标签：

```html
<form:hidden path="id" value="12345" />
```

## 9. errors标签

字段错误消息由与控制器关联的验证器生成。我们可以使用errors标签来呈现这些字段错误消息：

```html
<form:errors path="name" cssClass="error" />
```

这将显示路径属性中指定的字段的错误。默认情况下，错误消息在span标签中呈现，将.errors作为id附加到路径值，以及可选的cssClass属性中的CSS类，可用于设置输出样式：

```html
<span id="name.errors" class="error">Name is required!</span>
```

要用不同的元素而不是默认的span标签包含错误消息，我们可以在元素属性中指定首选元素：

```html
<form:errors path="name" cssClass="error" element="div" />
```

这会在div元素中呈现错误消息：

```html
<div id="name.errors" class="error">Name is required!</div>
```

除了能够显示特定输入元素的错误之外，我们还可以显示给定页面的完整错误列表(无论字段如何)。这是通过使用通配符实现的：

```html
<form:errors path="*" />
```

### 9.1 Validator

要显示给定字段的错误，我们需要定义一个验证器：

```java
public class PersonValidator implements Validator {

    @Override
    public boolean supports(Class clazz) {
        return Person.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "required.name");
    }
}
```

在这种情况下，如果字段名称为空，验证器将返回由资源包中的required.name标识的错误消息。

资源包在Spring XML配置文件中定义如下：

```xml
<bean class="org.springframework.context.support.ResourceBundleMessageSource" id="messageSource">
     <property name="basename" value="messages" />
</bean>
```

或者采用纯Java配置风格：

```java
@Bean
public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasenames("messages");
    return messageSource;
}
```

错误消息在messages.properties文件中定义：

```properties
required.name=Name is required!
```

要应用此验证，我们需要在我们的控制器中包含对验证器的引用，并在用户提交表单时调用控制器方法中的验证方法：

```java
@RequestMapping(value = "/addPerson", method = RequestMethod.POST)
public String submit(@ModelAttribute("person") Person person, BindingResult result, ModelMap modelMap) {
    validator.validate(person, result);

    if (result.hasErrors()) {
        return "personForm";
    }
    
    modelMap.addAttribute("person", person);
    return "personView";
}
```

### 9.2 JSR 303 Bean验证

从Spring 3开始，我们可以使用JSR 303(通过@Valid注解)进行bean验证。为此，我们需要在类路径上有一个JSR 303验证器框架。我们将使用Hibernate Validator(参考实现)。以下是我们需要包含在POM中的依赖项：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>5.1.1.Final</version>
</dependency>
```

为了使Spring MVC通过@Valid注解支持JSR 303验证，我们需要在Spring配置文件中启用以下内容：

```xml
<mvc:annotation-driven/>
```

或者在Java配置中使用相应的注解@EnableWebMvc ：

```java
@EnableWebMvc
@Configuration
public class ClientWebConfigJava implements WebMvcConfigurer {
    // All web configuration will go here
}
```

接下来，我们需要使用@Valid注解来注解我们想要验证的控制器方法 ：

```java
@RequestMapping(value = "/addPerson", method = RequestMethod.POST)
public String submit(@Valid @ModelAttribute("person") Person person, BindingResult result, ModelMap modelMap) {
    if(result.hasErrors()) {
        return "personForm";
    }
     
    modelMap.addAttribute("person", person);
    return "personView";
}
```

现在我们可以注解实体的属性以使用Hibernate验证程序注解对其进行验证：

```java
@NotEmpty
private String password;
```

默认情况下，如果我们将密码输入字段留空，此注解将显示“may not be empty” 。

我们可以通过在验证器示例中定义的资源包中创建一个属性来覆盖默认错误消息。消息的键遵循规则AnnotationName.entity.fieldname：

```properties
NotEmpty.person.password=Password is required!
```

## 10. 总结

在本教程中，我们探讨了Spring提供的用于处理表单的各种标签。

我们还查看了用于显示验证错误的标签以及显示自定义错误消息所需的配置。

当项目在本地运行时，可以在以下位置访问表单示例：

http://localhost:8080/spring-mvc-xml/person