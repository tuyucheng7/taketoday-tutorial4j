## 1. 概述

在这个教程中，我们将介绍如何解决Spring中的“not eligible for auto-proxying”这个问题。

首先，我们将创建一个简单的真实代码示例，该示例会导致该警告在应用程序启动期间出现。然后，我们将解释发生这种情况的原因。

最后，我们通过展示一个有效的代码示例来提供该问题的解决方案。

## 2. “not eligible for auto proxying”的原因

### 2.1 配置示例

在我们解释原因之前，让我们构建一个例子，使该警告在应用程序启动期间出现。

首先，我们将创建一个自定义@RandomInt注解。我们使用它来标注应该插入指定范围内的随机整数的字段：

```java

@Retention(RetentionPolicy.RUNTIME)
public @interface RandomInt {
    int min();

    int max();
}
```

其次，让我们创建一个DataCache类，它是一个简单的Spring组件。
我们想为DataCache分配一个可能被使用的随机group，例如，用于支持分片。为此，我们将使用自定义注解对该字段进行标注：

```java

@Component
public class DataCache {
    @RandomInt(min = 2, max = 10)
    private int group;
    private String name;
}
```

现在，让我们看看RandomIntGenerator类。这是一个Spring组件，我们使用它来将随机int值插入到由@RandomInt注解标注的字段中：

```java

@Slf4j
@Component
public class RandomIntGenerator {
    private final Random random = new Random();
    private final DataCache dataCache;

    public RandomIntGenerator(DataCache dataCache) {
        this.dataCache = dataCache;
    }

    public int generate(int min, int max) {
        return random.nextInt(max - min) + min;
    }
}
```

需要注意的是，我们通过构造注入将DataCache自动注解到RandomIntGenerator中。

最后，让我们创建一个NotEligibleForAutoProxyRandomIntProcessor类，该类将负责查找带有@RandomInt注解的字段，并将随机值插入其中：

```java
public class NotEligibleForAutoProxyRandomIntProcessor implements BeanPostProcessor {
    private final RandomIntGenerator randomIntGenerator;

    public NotEligibleForAutoProxyRandomIntProcessor(RandomIntGenerator randomIntGenerator) {
        this.randomIntGenerator = randomIntGenerator;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            RandomInt injectRandomInt = field.getAnnotation(RandomInt.class);
            if (injectRandomInt != null) {
                int min = injectRandomInt.min();
                int max = injectRandomInt.max();
                int randomValue = randomIntGenerator.generate(min, max);
                field.setAccessible(true);
                ReflectionUtils.setField(field, bean, randomValue);
            }
        });
        return bean;
    }
}
```

它实现org.springframework.beans.factory.config.BeanPostProcessor接口，用于在类初始化之前访问带注解的字段。

### 2.2 测试

即使一切正常编译，当我们运行我们的Spring应用程序并观察输出的日志时，
我们会看到由Spring的BeanPostProcessorChecker类生成的“not eligible for auto proxying”警告：

```
00:53:18.738 [main] INFO  [o.s.c.s.PostProcessorRegistrationDelegate$BeanPostProcessorChecker] >>> 
Bean 'randomIntGenerator' of type 
[cn.tuyucheng.taketoday.component.autoproxying.RandomIntGenerator] 
is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying) 
```

更重要的是，我们看到依赖于这种机制的DataCache bean并没有按照我们的预期进行初始化：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {NotEligibleForAutoProxyRandomIntProcessor.class, DataCache.class, RandomIntGenerator.class})
class NotEligibleForAutoProxyingIntegrationTest {
    private NotEligibleForAutoProxyRandomIntProcessor proxyRandomIntProcessor;

    @Autowired
    private DataCache dataCache;

    @Test
    void givenAutowireInBeanPostProcessor_whenSpringContextInitialize_thenNotEligibleLogShouldShowAndGroupFieldNotPopulated() {
        assertEquals(0, dataCache.getGroup());
    }
}
```

然而，值得一提的是，即使显示该警告，应用程序也没有失败。

### 2.3 分析原因

该警告是由NotEligibleForAutoProxyRandomIntProcessor类及其自动装配的依赖项引起的。
**实现BeanPostProcessor接口的类在启动时被实例化，作为ApplicationContext的特殊启动阶段的一部分，在任何其他bean之前**。

而且，AOP的自动代理机制也是BeanPostProcessor接口的实现。
因此，无论是BeanPostProcessor实现还是它们直接引用的bean都没有资格进行自动代理。
这意味着Spring使用AOP的特性(例如自动装配、安全性或事务性注解)在这些类中无法正常工作。

在我们的例子中，我们能够将DataCache实例自动注入到RandomIntGenerator类中而没有任何问题。但是，group字段未填充随机整数。

## 3. 如何解决错误

为了摆脱“not eligible for auto proxying”的警告，**我们需要打破BeanPostProcessor实现与其bean依赖项之间的循环**。
在我们的例子中，我们需要告诉IoC容器延迟初始化RandomIntGenerator bean。我们可以使用Spring的@Lazy注解：

```java
public class EligibleForAutoProxyRandomIntProcessor implements BeanPostProcessor {
    private final RandomIntGenerator randomIntGenerator;

    @Lazy
    public EligibleForAutoProxyRandomIntProcessor(RandomIntGenerator randomIntGenerator) {
        this.randomIntGenerator = randomIntGenerator;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // ...
    }
}
```

当EligibleForAutoProxyRandomIntProcessor在postProcessBeforeInitialization方法中使用到RandomIntGenerator时，
Spring才会初始化该bean。
**此时，Spring的IoC容器实例化了所有现有的bean，这些bean也可以自动代理**。

如果再次我们运行我们的应用程序，我们不会在日志中看到“not eligible for auto proxying”的警告。
更重要的是，DataCache bean将有一个填充了随机整数的group字段：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EligibleForAutoProxyRandomIntProcessor.class, DataCache.class, RandomIntGenerator.class})
class EligibleForAutoProxyingIntegrationTest {
    private EligibleForAutoProxyRandomIntProcessor randomIntProcessor;

    @Autowired
    private DataCache dataCache;

    @Test
    void givenAutowireInBeanPostProcessor_whenSpringContextInitialize_thenNotEligibleLogShouldShowAndGroupFieldPopulated() {
        assertNotEquals(0, dataCache.getGroup());
    }
}
```

## 4. 总结

在本文中，我们学习了如何解决Spring中的“not eligible for auto-proxying”警告。
通过使用@Lazy延迟初始化打破bean构建过程中的依赖循环。