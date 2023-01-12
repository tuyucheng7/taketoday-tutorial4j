## 1. 概述

在本教程中，我们将了解使用开源框架[Jersey](https://jersey.github.io/)的 Bean 验证。

正如我们在之前的文章中已经看到的，Jersey 是一个用于开发 RESTful Web 服务的开源框架。我们可以在关于如何[使用 Jersey 和 Spring 创建 API 的介绍中获得有关 Jersey 的更多详细信息。](https://www.baeldung.com/jersey-rest-api-with-spring)

## 2. Jersey 中的 Bean 验证

验证是验证某些数据是否遵守一个或多个预定义约束的过程。当然，这是大多数应用程序中非常常见的用例。

Java Bean[验证](https://beanvalidation.org/)框架 (JSR-380) 已成为在Java中处理此类操作的事实标准。要回顾JavaBean 验证的基础知识，请参阅我们之前的[教程](https://www.baeldung.com/javax-validation)。

Jersey 包含一个扩展模块来支持 Bean Validation。要在我们的应用程序中使用此功能，我们首先需要对其进行配置。在下一节中，我们将了解如何配置我们的应用程序。

## 3. 应用设置

[现在，让我们基于出色的Jersey MVC 支持](https://www.baeldung.com/jersey-mvc)文章中的简单 Fruit API 示例进行构建 。

### 3.1. Maven 依赖项

首先，让我们将 Bean Validation 依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.glassfish.jersey.ext</groupId>
    <artifactId>jersey-bean-validation</artifactId>
    <version>2.27</version>
</dependency>
```

我们可以从 [Maven Central](https://search.maven.org/classic/#search|ga|1|jersey-bean-validation)获取最新版本。

### 3.2. 配置服务器

在 Jersey 中，我们通常会在自定义资源配置类中注册要使用的扩展功能。

但是，对于bean validation extension，不需要做这个注册。幸运的是，这是 Jersey 框架自动注册的少数几个扩展之一。

最后，为了向客户端发送验证错误，我们将向我们的自定义资源配置添加一个服务器属性：

```java
public ViewApplicationConfig() {
    packages("com.baeldung.jersey.server");
    property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
}

```

## 4. 验证 JAX-RS 资源方法

在本节中，我们将解释使用约束注解验证输入参数的两种不同方式：

-   使用内置的 Bean Validation API 约束
-   创建自定义约束和验证器

### 4.1. 使用内置约束注解

让我们从查看内置约束注解开始：

```java
@POST
@Path("/create")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public void createFruit(
    @NotNull(message = "Fruit name must not be null") @FormParam("name") String name, 
    @NotNull(message = "Fruit colour must not be null") @FormParam("colour") String colour) {

    Fruit fruit = new Fruit(name, colour);
    SimpleStorageService.storeFruit(fruit);
}

```

在此示例中，我们使用两个表单参数name和color创建一个新的Fruit。我们使用@NotNull注解，它已经是 Bean Validation API 的一部分。

这对我们的表单参数施加了一个简单的非空约束。如果其中一个参数为null，则将返回注解中声明的消息。

当然，我们将通过单元测试来证明这一点：

```java
@Test
public void givenCreateFruit_whenFormContainsNullParam_thenResponseCodeIsBadRequest() {
    Form form = new Form();
    form.param("name", "apple");
    form.param("colour", null);
    Response response = target("fruit/create").request(MediaType.APPLICATION_FORM_URLENCODED)
        .post(Entity.form(form));

    assertEquals("Http Response should be 400 ", 400, response.getStatus());
    assertThat(response.readEntity(String.class), containsString("Fruit colour must not be null"));
}

```

在上面的例子中，我们使用JerseyTest支持类来测试我们的水果资源。我们发送一个带有空颜色的 POST 请求，并检查响应是否包含预期的消息。

有关内置验证约束的列表，请查看[文档](https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#section-builtin-constraints)。

### 4.2. 定义自定义约束注解

有时我们需要施加更复杂的约束。我们可以通过定义我们自己的自定义注解来做到这一点。

使用我们简单的 Fruit API 示例，假设我们需要验证所有水果都具有有效的序列号：

```java
@PUT
@Path("/update")
@Consumes("application/x-www-form-urlencoded")
public void updateFruit(@SerialNumber @FormParam("serial") String serial) {
    //...
}

```

在此示例中，参数serial必须满足@SerialNumber定义的约束，我们将在接下来对其进行定义。

我们将首先定义约束注解：

```java
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { SerialNumber.Validator.class })
    public @interface SerialNumber {

    String message()

    default "Fruit serial number is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

```

接下来，我们将定义验证器类SerialNumber.Validator：

```java
public class Validator implements ConstraintValidator<SerialNumber, String> {
    @Override
    public void initialize(SerialNumber serial) {
    }

    @Override
    public boolean isValid(String serial, 
        ConstraintValidatorContext constraintValidatorContext) {
        
        String serialNumRegex = "^d{3}-d{3}-d{4}$";
        return Pattern.matches(serialNumRegex, serial);
    }
}

```

这里的关键点是Validator类必须实现ConstraintValidator，其中T是我们要验证的值的类型，在我们的例子中是一个String。

最后，我们在isValid方法中实现自定义验证逻辑。

## 5. 资源验证

此外，Bean Validation API 还允许我们使用@Valid注解来验证对象。

在下一节中，我们将解释使用此注解验证资源类的两种不同方式：

-   一、请求资源验证
-   二、Response资源验证

让我们首先将 @Min注解添加到我们的Fruit对象：

```java
@XmlRootElement
public class Fruit {

    @Min(value = 10, message = "Fruit weight must be 10 or greater")
    private Integer weight;
    //...
}

```

### 5.1. 请求资源验证

首先，我们将在FruitResource类中使用@Valid启用验证：

```java
@POST
@Path("/create")
@Consumes("application/json")
public void createFruit(@Valid Fruit fruit) {
    SimpleStorageService.storeFruit(fruit);
}

```

在上面的例子中，如果我们尝试创建一个重量小于 10 的水果，我们将得到一个验证错误。

### 5.2. 响应资源验证

同样，在下一个示例中，我们将看到如何验证响应资源：

```java
@GET
@Valid
@Produces("application/json")
@Path("/search/{name}")
public Fruit findFruitByName(@PathParam("name") String name) {
    return SimpleStorageService.findByName(name);
}
```

请注意，我们如何使用相同的@Valid 注解。但这次我们在资源方法级别使用它来确保响应有效。

## 6. 自定义异常处理器

在最后一部分中，我们将简要介绍如何创建自定义异常处理程序。当我们想要在违反特定约束时返回自定义响应时，这很有用。

让我们从定义FruitExceptionMapper开始：

```java
public class FruitExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(prepareMessage(exception))
            .type("text/plain")
            .build();
    }

    private String prepareMessage(ConstraintViolationException exception) {
        StringBuilder message = new StringBuilder();
        for (ConstraintViolation<?> cv : exception.getConstraintViolations()) {
            message.append(cv.getPropertyPath() + " " + cv.getMessage() + "n");
        }
        return message.toString();
    }
}
```

首先，我们定义一个自定义的异常映射提供者。为此，我们使用ConstraintViolationException实现ExceptionMapper接口。

因此，我们将看到，当抛出此异常时，我们的自定义异常映射器实例的toResponse方法将被调用。

此外，在这个简单的示例中，我们遍历所有违规并附加每个属性和消息以在响应中发回。

接下来，为了使用我们的自定义异常映射器，我们需要注册我们的提供者：

```java
@Override
protected Application configure() {
    ViewApplicationConfig config = new ViewApplicationConfig();
    config.register(FruitExceptionMapper.class);
    return config;
}
```

最后，我们添加一个端点以返回无效的Fruit以显示异常处理程序的运行情况：

```java
@GET
@Produces(MediaType.TEXT_HTML)
@Path("/exception")
@Valid
public Fruit exception() {
    Fruit fruit = new Fruit();
    fruit.setName("a");
    fruit.setColour("b");
    return fruit;
}

```

## 七. 总结

总而言之，在本教程中，我们探索了 Jersey Bean Validation API 扩展。

首先，我们首先介绍如何在 Jersey 中使用 Bean Validation API。此外，我们还了解了如何配置示例 Web 应用程序。

最后，我们了解了使用 Jersey 进行验证的几种方法以及如何编写自定义异常处理程序。