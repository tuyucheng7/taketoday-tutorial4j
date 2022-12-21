## 1. 简介

在我们的[Spring Boot测试]()教程中，我们看到了如何使用@DataJpaTest注解。

在本教程中，我们将了解**如何解决错误“无法找到@SpringBootConfiguration”**。

## 2. 原因

@DataJpaTest注解帮助我们设置JPA测试，为此，它初始化应用程序，忽略不相关的部分。例如，它会忽略MVC控制器。

但是，要初始化应用程序，它需要配置。

为此，它会在当前包中搜索并在包层次结构中向上搜索，直到找到配置。

例如，让我们在cn.tuyucheng.taketoday.data.jpa包中添加一个@DataJpaTest，然后，它将在以下位置搜索配置类：

-   cn.tuyucheng.taketoday.data.jpa
-   cn.tuyucheng.taketoday.data
-   等等

但是，当没有找到配置时，应用程序会报错：

```shell
Unable to find a @SpringBootConfiguration, you need to use @ContextConfiguration or @SpringBootTest(classes=...) with your test java.lang.IllegalStateException
```

例如，发生这种情况可能是因为配置类位于更具体的包中，例如cn.tuyucheng.taketoday.data.jpa.application。**如果我们将配置类移动到cn.tuyucheng.taketoday.data.jpa，Spring现在能够找到它**。

另一方面，我们可以有一个没有任何@SpringBootConfiguration的模块。在下一节中，我们将研究这种情况。

## 3. 缺少@SpringBootConfiguration

如果我们的模块不包含任何@SpringBootConfiguration怎么办？这可能有多种原因。让我们假设，对于本教程，我们有一个仅包含模型类的模块。

因此，解决方案很简单，让我们在测试代码中添加一个@SpringBootApplication：

```java
@SpringBootApplication
public class TestApplication {}
```

现在我们有了一个带注解的类，Spring能够引导我们的测试。

为了验证我们的设置，我们注入一个TestEntityManager并验证它是否已设置：

```java
@ExtendWith(SpringExtension.class)
@DataJpaTest
class DataJpaUnitTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void givenACorrectSetup_thenAnEntityManagerWillBeAvailable() {
        assertNotNull(entityManager);
    }
}
```

**当Spring可以在其自己的包或其父包之一中找到@SpringBootConfiguration时，此测试成功**。

## 4. 总结

在这个简短的教程中，我们介绍了错误的两个不同原因：“无法找到@SpringBootConfiguration”。

首先，我们了解了找不到配置类的情况，这是因为它的位置所导致的，我们通过将配置类移动到另一个位置来解决它。

其次，我们介绍了没有配置类可用的场景，这是通过向我们的测试代码库添加@SpringBootApplication来解决这个问题。