## 1. 概述

在本教程中，**我们将介绍Spring中的@AliasFor注解**。

首先，我们看一些框架内部使用它的例子。接下来，我们给出一些自定义示例。

## 2. 注解说明

[@AliasFor](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/annotation/AliasFor.html)自4.2版本起成为框架的一部分。几个核心[Spring注解](https://www.baeldung.com/spring-core-annotations)已更新，现在包含此注解。

我们可以使用它来修饰单个注解或由元注解组成的注解中的属性。元注解是可以应用于另一个注解的注解。

在同一个注解中，**我们使用@AliasFor为属性声明别名，以便我们可以互换使用它们**。或者，我们可以在组合注解中使用它来覆盖其元注解中的属性。换句话说，当我们**用@AliasFor修饰组合注解中的属性时，它会覆盖其元注解中的指定属性**。

有趣的是，许多核心的Spring注解，如[@Bean](https://www.baeldung.com/spring-core-annotations#bean)、[@ComponentScan](https://www.baeldung.com/spring-component-scanning)、[@Scope](https://www.baeldung.com/spring-bean-scopes)、[@RequestMapping](https://www.baeldung.com/spring-requestmapping)和[@RestController](https://www.baeldung.com/spring-controller-vs-restcontroller)现在使用@AliasFor来配置它们的内部属性别名。

这是注解的定义：

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface AliasFor {
    @AliasFor("attribute")
    String value() default "";

    @AliasFor("value")
    String attribute() default "";

    Class<? extends Annotation> annotation() default Annotation.class;
}
```

重要的是，**我们可以隐式和显式地使用这个注解**。隐式用法仅限于注解中的别名。相比之下，也可以对元注解中的属性进行显式使用。

我们将在以下各节中通过示例详细介绍这一点。

## 3. 注解中的显式别名

让我们考虑一个核心Spring注解[@ComponentScan](https://www.baeldung.com/spring-component-scanning)，来理解单个注解中的显式别名：

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repeatable(ComponentScans.class)
public @interface ComponentScan {

    @AliasFor("basePackages")
    String[] value() default {};

    @AliasFor("value")
    String[] basePackages() default {};
    // ...
}
```

正如我们所看到的，value在这里被明确定义为basePackages的别名，反之亦然。这意味着**我们可以互换使用它们**。

因此，这两种用法是相似的：

```java
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.aliasfor")

@ComponentScan(value = "cn.tuyucheng.taketoday.aliasfor")
```

此外，由于这两个属性也被标记为default，因此我们可以更简洁地编写：

```java
@ComponentScan("cn.tuyucheng.taketoday.aliasfor")
```

此外，Spring对这种情况有一些实施要求。首先，别名属性应该声明相同的默认值。此外，它们应该具有相同的返回类型。如果我们违反了这些约束中的任何一个，框架会抛出AnnotationConfigurationException。

## 4. 元注解中属性的显式别名

接下来，让我们看一个元注解的示例，并从中创建一个组合注解。然后，**我们将看到自定义别名的显式用法**。

首先，让我们将框架注解@RequestMapping视为我们的元注解：

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface RequestMapping {
    String name() default "";

    @AliasFor("path")
    String[] value() default {};

    @AliasFor("value")
    String[] path() default {};

    RequestMethod[] method() default {};
    // ...
}
```

接下来，我们将从它创建一个组合注解@MyMapping：

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping
public @interface MyMapping {

    @AliasFor(annotation = RequestMapping.class, attribute = "method")
    RequestMethod[] action() default {};
}
```

我们可以看到，在@MyMapping中，action是@RequestMapping中method属性的显式别名。也就是说，**我们组合注解中的action会覆盖元注解中的method属性**。

与注解中的别名类似，元注解属性别名也必须具有相同的返回类型。例如，在我们的例子中是RequestMethod[]。此外，属性annotation应该引用元注解，就像我们上面使用的annotation = RequestMapping.class一样。

为了演示，让我们添加一个名为MyMappingController的控制器类。我们将使用自定义注解修饰它的方法。

具体来说，这里我们将只添加两个属性route和action：

```java
@Controller
public class MyMappingController {

    @MyMapping(action = RequestMethod.PATCH, route = "/test")
    public void mappingMethod() {
    }
}
```

最后，为了了解显式别名的行为，让我们添加一个简单的测试：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MyMappingController.class)
class AliasForUnitTest {

    Class<?> controllerClass;
    @Autowired
    private ConfigurableApplicationContext context;

    @BeforeEach
    public void setControllerBean() {
        MyMappingController controllerBean = context.getBean(MyMappingController.class);
        controllerClass = controllerBean.getClass();
    }

    @Test
    void givenComposedAnnotation_whenExplicitAlias_thenMetaAnnotationAttributeOverridden() {
        for (Method method : controllerClass.getMethods()) {
            if (method.isAnnotationPresent(MyMapping.class)) {
                MyMapping annotation = AnnotationUtils.findAnnotation(method, MyMapping.class);
                RequestMapping metaAnnotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
                assertEquals(RequestMethod.PATCH, annotation.action()[0]);
                assertEquals(0, metaAnnotation.method().length);
            }
        }
    }
}
```

正如我们所看到的，我们自定义注解的属性action已经覆盖了元注解@RequestMapping的属性method。

## 5. 注解中的隐式别名

为了理解这一点，让我们**在@MyMapping中添加更多别名**：

```java
@AliasFor(annotation = RequestMapping.class, attribute = "path")
String[] value() default {};

@AliasFor(annotation = RequestMapping.class, attribute = "path")
String[] mapping() default {};

@AliasFor(annotation = RequestMapping.class, attribute = "path")
String[] route() default {};
```

在这种情况下，value、mapping和route是@RequestMapping中path的显式元注解重写。因此，它们也是彼此的隐式别名。换句话说，**对于@MyMapping，我们可以互换使用这三个属性**。

为了演示这一点，我们使用上一小节编写的控制器。下面是另一个测试：

```java
@Test
void givenComposedAnnotation_whenImplictAlias_thenAttributesEqual() {
    for (Method method : controllerClass.getMethods()) {
        if (method.isAnnotationPresent(MyMapping.class)) {
            MyMapping annotationOnBean = AnnotationUtils.findAnnotation(method, MyMapping.class);
            assertEquals(annotationOnBean.mapping()[0], annotationOnBean.route()[0]);
            assertEquals(annotationOnBean.value()[0], annotationOnBean.route()[0]);
        }
    }
}
```

值得注意的是，我们没有在控制器方法的注解中定义属性value和mapping。但是，**它们仍然隐含地带有与route相同的值**。

## 6. 总结

在本教程中，**我们了解了Spring框架中的@AliasFor注解**。在我们的示例中，我们包含了显式和隐式两种情况下的使用场景。