## 1. 概述

JUnit 5提供了多种注册Extension的方法，有关其中一些方法的概述，请参阅我们的[JUnit 5 Extension指南]()。

在本教程中，我们重点介绍使用@RegisterExtension注解以编程方式注册JUnit 5 Extension。

## 2. @RegisterExtension

我们可以将此注解应用于测试类中的成员变量，**这种方法的一个优点是我们可以直接将Extension作为测试内容中的对象进行访问**。JUnit将在适当的阶段调用Extension方法。

例如，如果一个Extension实现了BeforeEachCallback，JUnit将在执行测试方法之前调用其对应的接口方法。

## 3. 在静态字段上使用@RegisterExtension

当与静态字段一起使用时，JUnit将在应用基于类级注解@ExtendWith注册的Extension之后应用此Extension。

此外，JUnit将调用Extension的类级和方法级回调。

例如，以下Extension同时实现BeforeAllCallback，BeforeEachCallback两个Extension接口：

```java
public class BeforeExtension implements BeforeAllCallback, BeforeEachCallback {
    private String type;
    private static final Logger LOGGER = LoggerFactory.getLogger(BeforeExtension.class);

    public BeforeExtension(String type) {
        this.type = type;
    }

    public BeforeExtension() {
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        LOGGER.info("Type {} In before All : {}", type, context.getDisplayName());
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        LOGGER.info("Type {} In before Each : {}", type, context.getDisplayName());
    }

    public String getType() {
        return type;
    }
}
```

让我们将此Extension应用于测试类的静态字段：

```java
class BeforeExtensionUnitTest {

    @RegisterExtension
    static BeforeExtension beforeExtension = new BeforeExtension("static version");

    @Test
    void demoTest() {
        assertEquals("instance version", instanceLevelExtension.getType());
    }
}
```

输出显示来自beforeAll()和beforeEach()方法打印的日志：

```shell
... [main] DEBUG cn.tuyucheng.taketoday.junit5.registerextension.RegisterExtensionSampleExtension - Type static version In beforeAll : RegisterExtensionUnitTest
... [main] DEBUG cn.tuyucheng.taketoday.junit5.registerextension.RegisterExtensionSampleExtension - Type static version In beforeEach : demoTest()
```

## 4. 在实例字段上使用@RegisterExtension

如果我们使用@RegisterExtension标注非静态字段，JUnit只会在处理完所有TestInstancePostProcessor回调后应用Extension。在这种情况下，JUnit将忽略像beforeAll这样的类级回调。

在上面的示例中，我们删除beforeExtension变量的static修饰符：

```java
@RegisterExtension
BeforeExtension beforeExtension = new BeforeExtension("instance version");
```

现在JUnit将只调用beforeEach()方法，如输出中所示：

```shell
... [main] DEBUG cn.tuyucheng.taketoday.junit5.registerextension.RegisterExtensionSampleExtension - Type instance version In beforeEach : demoTest()
```

## 5. 总结

在本文中，我们演示了如何使用@RegisterExtension注解以编程方式注册JUnit 5中的Extension，并介绍了将Extension应用于静态字段与实例字段之间的区别。