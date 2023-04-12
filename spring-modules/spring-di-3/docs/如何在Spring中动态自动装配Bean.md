## 1. 概述

在本文中，我们将演示如何在Spring中**动态地自动注入bean**。

我们首先演示一个动态自动装配可能会有所帮助的真实用例。除此之外，我们将演示如何在Spring中以两种不同的方式解决它。

## 2. 动态自动装配用例

**在需要动态更改Spring的bean执行逻辑的地方**，动态自动装配很有用。它特别适用于根据一些运行时变量选择要执行的代码的地方。

为了演示一个真实的用例，让我们创建一个控制世界不同地区的服务器的应用程序。出于这个原因，我们创建一个带有两个简单方法的接口：

```java
public interface RegionService {
    boolean isServerActive(int serverId);

    String getISOCountryCode();
}
```

和两个实现类：

```java

@Service("CNregionService")
public class CNRegionService implements RegionService {

    @Override
    public boolean isServerActive(int serverId) {
        return false;
    }

    @Override
    public String getISOCountryCode() {
        return "CN";
    }
}

@Service("USregionService")
public class USRegionService implements RegionService {
    @Override
    public boolean isServerActive(int serverId) {
        return true;
    }

    @Override
    public String getISOCountryCode() {
        return "US";
    }
}
```

假设我们有一个网站，用户可以选择检查服务器在所选区域是否处于活动状态。
**因此，我们希望有一个Service类，根据用户的输入动态更改RegionService接口实现**。
毫无疑问，这是动态bean自动注入发挥作用的地方。

## 3. 使用BeanFactory

BeanFactory是用于访问Spring bean容器的顶层接口。特别是，它包含获取特定bean的有用方法。
由于BeanFactory也是一个Spring bean，我们可以自动装配并直接在我们的类中使用它：

```java

@Service
public class BeanFactoryDynamicAutowireService {
    private static final String SERVICE_NAME_SUFFIX = "regionService";
    private final BeanFactory beanFactory;

    @Autowired
    public BeanFactoryDynamicAutowireService(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public boolean isServerActive(String isoCountryCode, int serverId) {
        RegionService service = beanFactory.getBean(getRegionServiceBeanName(isoCountryCode), RegionService.class);
        return service.isServerActive(serverId);
    }

    private String getRegionServiceBeanName(String isoCountryCode) {
        return isoCountryCode + SERVICE_NAME_SUFFIX;
    }
}
```

我们使用了getBean()方法的重载版本来获取具有给定名称和所需类型的bean。

**虽然这是可行的，但我们还是更倾向于依赖一些更地道的东西；也就是说，使用依赖注入**。

## 4. 使用接口

为了通过依赖注入解决这个问题，我们将依赖Spring一个鲜为人知的特性。

除了标准的单字段自动注入之外，**Spring还允许我们能够将实现特定接口的所有bean收集到一个Map中**：

```java

@Service
public class CustomMapFromListDynamicAutowireService {
    private final Map<String, RegionService> servicesByCountryCode;

    @Autowired
    public CustomMapFromListDynamicAutowireService(List<RegionService> regionServices) {
        servicesByCountryCode = regionServices.stream().collect(Collectors.toMap(RegionService::getISOCountryCode, Function.identity()));
    }

    public boolean isServerActive(String isoCountryCode, int serverId) {
        RegionService service = servicesByCountryCode.get(isoCountryCode);
        return service.isServerActive(serverId);
    }
}
```

我们在构造函数中创建了一个Map，该Map按国家代码保存实现类。
此外，我们可以稍后在方法中使用它来获取特定实现类，以检查给定服务器是否在特定区域中处于活动状态。

以下是测试类：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DynamicAutowireConfig.class)
class DynamicAutowireIntegrationTest {

    @Autowired
    private BeanFactoryDynamicAutowireService beanFactoryDynamicAutowireService;

    @Autowired
    private CustomMapFromListDynamicAutowireService customMapFromListDynamicAutowireService;

    @Test
    void givenDynamicallyAutowiredBean_whenCheckingServerInGB_thenServerIsNotActive() {
        assertThat(beanFactoryDynamicAutowireService.isServerActive("CN", 101), is(false));
        assertThat(customMapFromListDynamicAutowireService.isServerActive("CN", 101), is(false));
    }

    @Test
    void givenDynamicallyAutowiredBean_whenCheckingServerInUS_thenServerIsActive() {
        assertThat(beanFactoryDynamicAutowireService.isServerActive("US", 101), is(true));
        assertThat(customMapFromListDynamicAutowireService.isServerActive("US", 101), is(true));
    }
}
```

## 5. 总结

在这个教程中，我们介绍了如何在Spring中使用两种不同的方法动态地自动装配bean。