## 1. 概述

在本快速教程中，我们将重点关注Spring中[@Valid](https://docs.oracle.com/javaee/7/api/javax/validation/Valid.html)和[@Validated](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/validation/annotation/Validated.html)注解之间的区别。

验证用户的输入是我们大多数应用程序中的常见功能，在Java生态系统中，我们专门使用[Java标准Bean Validation API](https://www.baeldung.com/javax-validation)来支持这一点，它从4.0版本开始与Spring很好地集成。**@Valid和@Validated注解源自此标准Bean Validation API**。

在接下来的部分中，我们将更详细地探讨它们。

## 2. @Valid和@Validated注解

在Spring中，**我们使用JSR-303的@Valid注解进行方法级别的验证，我们还使用它来标记要验证的成员属性**。但是，此注解不支持组验证。

组有助于限制验证期间应用的约束，一个特定的用例是UI向导。在第一步中，我们可能有某个字段子组，在后续步骤中，可能有另一个组属于同一个bean。所以我们需要在每个步骤中对这些有限的字段应用约束，但是@Valid不支持这个。

在这种情况下，**对于组级别，我们必须使用Spring的@Validated**，它是JSR-303的@Valid的变体，这在方法级别使用。对于标记成员属性，我们继续使用@Valid注解。

现在让我们深入研究并通过示例查看这些注解的用法。

## 3. 例子

让我们考虑一个使用Spring Boot开发的简单用户注册表单，首先，我们只有名称和密码属性：

```java
public class UserAccount {

    @NotNull
    @Size(min = 4, max = 15)
    private String password;

    @NotBlank
    private String name;

    // standard constructors / setters / getters / toString
}
```

接下来，让我们看看控制器。在这里，我们将使用带有@Valid注解的saveBasicInfo方法来验证用户输入：

```java
@RequestMapping(value = "/saveBasicInfo", method = RequestMethod.POST)
public String saveBasicInfo(@Valid @ModelAttribute("useraccount") UserAccount useraccount, BindingResult result, ModelMap model) {
    if (result.hasErrors()) {
        return "error";
    }
    return "success";
}
```

现在让我们测试一下这个方法：

```java
@Test
public void givenSaveBasicInfo_whenCorrectInput_thenSuccess() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.post("/saveBasicInfo")
        .accept(MediaType.TEXT_HTML)
        .param("name", "test123")
        .param("password", "pass"))
        .andExpect(view().name("success"))
        .andExpect(status().isOk())
        .andDo(print());
}
```

在确认测试运行成功后，我们将扩展功能。下一个合乎逻辑的步骤是将其转换为多步骤注册表单，就像大多数向导的情况一样。第一步的名称和密码保持不变，在第二步中，我们将获取其他信息，例如年龄和电话。然后我们将使用这些附加字段更新我们的域对象：

```java
public class UserAccount {
    
    @NotNull
    @Size(min = 4, max = 15)
    private String password;
 
    @NotBlank
    private String name;
 
    @Min(value = 18, message = "Age should not be less than 18")
    private int age;
 
    @NotBlank
    private String phone;
    
    // standard constructors / setters / getters / toString
}
```

然而，这一次我们会注意到之前的测试失败了，这是因为我们没有传递age和phone字段，它们仍然不在UI的图片中。为了支持这种行为，我们需要组验证和@Validated注解。

为此，我们需要对创建两个不同组的字段进行分组。首先，我们需要创建两个标记接口，每个组或每个步骤都有一个单独的接口，我们可以参考我们关于[组验证](https://www.baeldung.com/javax-validation-groups)的文章来了解具体的实现方式。在这里，让我们关注注解的差异。

我们将在第一步使用BasicInfo接口，在第二步使用AdvanceInfo。此外，我们将更新我们的UserAccount类以使用这些标记接口：

```java
public class UserAccount {
    
    @NotNull(groups = BasicInfo.class)
    @Size(min = 4, max = 15, groups = BasicInfo.class)
    private String password;
 
    @NotBlank(groups = BasicInfo.class)
    private String name;
 
    @Min(value = 18, message = "Age should not be less than 18", groups = AdvanceInfo.class)
    private int age;
 
    @NotBlank(groups = AdvanceInfo.class)
    private String phone;
    
    // standard constructors / setters / getters / toString
}
```

此外，我们将更新我们的控制器以使用@Validated注解而不是@Valid：

```java
@RequestMapping(value = "/saveBasicInfoStep1", method = RequestMethod.POST)
public String saveBasicInfoStep1(@Validated(BasicInfo.class) @ModelAttribute("useraccount") UserAccount useraccount, BindingResult result, ModelMap model) {
    if (result.hasErrors()) {
        return "error";
    }
    return "success";
}
```

作为此更新的结果，我们的测试现在可以成功运行，我们还将测试这个新方法：

```java
@Test
public void givenSaveBasicInfoStep1_whenCorrectInput_thenSuccess() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.post("/saveBasicInfoStep1")
        .accept(MediaType.TEXT_HTML)
        .param("name", "test123")
        .param("password", "pass"))
        .andExpect(view().name("success"))
        .andExpect(status().isOk())
        .andDo(print());
}
```

这也成功运行，因此，**我们可以看到@Validated的使用对于组验证是多么重要**。

接下来，让我们看看@Valid是如何触发嵌套属性验证的。

## 4. 使用@Valid注解标记嵌套对象

**@Valid注解用于标记嵌套属性，特别是，这会触发嵌套对象的验证**。例如，在我们当前的场景中，我们可以创建一个UserAddress对象：

```java
public class UserAddress {

    @NotBlank
    private String countryCode;

    // standard constructors / setters / getters / toString
}
```

为确保此嵌套对象的验证，我们将使用@Valid注解标注该属性：

```java
public class UserAccount {
    
    //...
    
    @Valid
    @NotNull(groups = AdvanceInfo.class)
    private UserAddress useraddress;
    
    // standard constructors / setters / getters / toString 
}
```

## 5. 优点和缺点

让我们看看在Spring中使用@Valid和@Validated注解的一些优缺点。

**@Valid注解确保整个对象的验证**。重要的是，它执行整个对象图的验证。**但是，这会为仅需要部分验证的场景带来问题**。

**另一方面，我们可以使用@Validated进行分组验证，包括上面的部分验证**。但是，在这种情况下，经过验证的实体必须知道所有组或使用它们的用例的验证规则。这里我们混合了关注点，这可能会导致反模式。

## 6. 总结

在这篇简短的文章中，我们探讨了@Valid和@Validated注解之间的主要区别。

最后，对于任何基本验证，我们将在我们的方法调用中使用JSR @Valid注解。另一方面，对于任何组验证，包括[组序列](https://docs.oracle.com/javaee/7/api/javax/validation/GroupSequence.html)，我们需要 在我们的方法调用中使用Spring的@Validated注解。还需要@Valid 注解来触发嵌套属性的验证。