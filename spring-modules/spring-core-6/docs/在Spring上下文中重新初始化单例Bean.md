## 1. 概述

在本教程中，我们将研究在运行时重新初始化 [单例 Spring bean](https://www.baeldung.com/spring-bean-scopes#singleton)的方法 。

默认情况下，具有单例作用域的 Spring bean 不会在应用程序生命周期中重新初始化。但是，有时可能需要重新创建 bean — 例如，当属性更新时。我们将研究一些方法来做到这一点。

## 2.代码设置

为了理解这一点，我们将创建一个小项目。我们将创建一个从配置文件中读取配置属性并将它们保存在内存中以便更快访问的 bean。如果文件中的属性发生变化，则可能需要重新加载配置。

### 2.1. 单例 Bean

让我们从创建 ConfigManager 类开始：

```java
@Service("ConfigManager")
public class ConfigManager {

    private static final Log LOG = LogFactory.getLog(ConfigManager.class);

    private Map<String, Object> config;

    private final String filePath;

    public ConfigManager(@Value("${config.file.path}") String filePath) {
        this.filePath = filePath;
        initConfigs();
    }

    private void initConfigs() {
        Properties properties = new Properties();
        try {
            properties.load(Files.newInputStream(Paths.get(filePath)));
        } catch (IOException e) {
            LOG.error("Error loading configuration:", e);
        }
        config = new HashMap<>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            config.put(String.valueOf(entry.getKey()), entry.getValue());
        }
    }

    public Object getConfig(String key) {
        return config.get(key);
    }
}

```

关于这个类，有几点需要注意：

-   从构造函数调用方法 initConfigs()会在构造 bean 后立即加载文件。
-   initConfigs ()方法将文件的内容转换为名为config的Map。
-   getConfig()方法用于通过 键读取属性。

还有一点需要注意的是构造函数依赖注入。我们稍后会在需要替换 bean 时使用它。

配置文件位于路径 src/main/resources/config.properties并包含一个属性：![img]()

```properties
property1=value1

```

### 2.2. 控制器

为了测试 ConfigManager，让我们创建一个控制器：

```java
@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private ConfigManager configManager;

    @GetMapping("/{key}")
    public Object get(@PathVariable String key) {
        return configManager.getConfig(key);
    }
}

```

我们可以运行应用程序并通过访问 URL [http://localhost:8080/config/property1来读取配置](http://localhost:8080/config/property1)

接下来，我们要更改文件中属性的值，并在我们通过再次点击相同的 URL 读取配置时反映出来。让我们看一下执行此操作的几种方法。

## 3. 使用公共方法重新加载属性

如果我们想重新加载属性而不是重新创建对象本身，我们可以简单地创建一个公共方法来再次初始化地图。在我们的 ConfigManager中，让我们添加一个调用方法initConfigs()的方法：![img]()

```java
public void reinitializeConfig() {
    initConfigs();
}

```

当我们想要重新加载属性时，我们可以调用这个方法。让我们在控制器类中公开另一个调用reinitializeConfig()方法的 方法：

```java
@GetMapping("/reinitializeConfig")
public void reinitializeConfig() {
    configManager.reinitializeConfig();
}

```

我们现在可以运行应用程序并通过以下几个简单的步骤对其进行测试：

-   点击 URL http://localhost:8080/config/property1 返回 value1。
-   然后我们将 property1的值从value1更改为 value2。
-   然后我们可以点击 URL http://localhost:8080/config/reinitializeConfig 来重新初始化配置映射。
-   如果我们再次访问 URL http://localhost:8080/config/property1 ，我们会发现返回的值是 value2。

## 4.重新初始化单例Bean

重新初始化 bean 的另一种方法是在上下文中重新创建它。重新创建可以通过使用自定义代码和调用构造函数或通过删除 bean 并让上下文自动重新初始化它来完成。让我们看看这两种方式。

### 4.1. 替换上下文中的 Bean

我们可以从上下文中删除 bean 并将其替换为ConfigManager的新实例。让我们在我们的控制器中定义另一个方法来做到这一点：

```java
@GetMapping("/reinitializeBean")
public void reinitializeBean() {
    DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) applicationContext.getAutowireCapableBeanFactory();
    registry.destroySingleton("ConfigManager");
    registry.registerSingleton("ConfigManager", new ConfigManager(filePath)); 
}

```

首先，我们从应用程序上下文中获取DefaultSingletonBeanRegistry的实例。接下来，我们调用 destroySingleton()方法来销毁名为 ConfigManager的 bean 实例。最后，我们创建一个新的ConfigManager实例，并通过调用registerSingleton() 方法将其注册到工厂。

为了创建一个新实例，我们使用了在ConfigManager中定义的构造函数。bean 依赖的任何依赖项都必须通过构造函数传递。

registerSingleton()方法不仅在上下文中创建 bean，而且将它自动装配到依赖对象。 

调用/reinitializeBean端点更新控制器中的ConfigManager bean。我们可以使用与前一种方法相同的步骤来测试重新初始化行为。

### 4.2. 在上下文中销毁 Bean

在前面的方法中，我们需要通过构造函数传递依赖关系。有时，我们可能不需要创建 bean 的新实例，或者可能无法访问所需的依赖项。在这种情况下，另一种可能性就是销毁上下文中的 bean。

当再次请求 bean 时，上下文负责再次创建 bean。在这种情况下，它将使用与初始 bean 创建相同的步骤来创建。

为了演示这一点，让我们创建一个新的控制器方法来销毁 bean 但不会再次创建它：

```java
@GetMapping("/destroyBean")
public void destroyBean() {
    DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) applicationContext.getAutowireCapableBeanFactory();
    registry.destroySingleton("ConfigManager");
}

```

这不会改变对控制器已经持有的 bean 的引用。 要访问最新状态，我们需要[直接从上下文中读取它](https://www.baeldung.com/spring-getbean)。

让我们创建一个新的控制器来读取配置。该控制器将依赖于上下文中最新的 ConfigManager bean：![img]()

```java
@GetMapping("/context/{key}")
public Object getFromContext(@PathVariable String key) {
    ConfigManager dynamicConfigManager = applicationContext.getBean(ConfigManager.class);
    return dynamicConfigManager.getConfig(key);
}

```

我们可以使用几个简单的步骤来测试上述方法：

-   点击 URL http://localhost:8080/config/context/property1 返回 value1。
-   然后我们可以点击 URL http://localhost:8080/config/destroyBean 来销毁 ConfigManager。
-   然后我们将 property1的值从value1更改为 value2。
-   如果我们再次点击 URL http://localhost:8080/config/context/property1 ，我们会发现返回的值是 value2。

## 5.总结

在本文中，我们探讨了重新初始化单例 bean 的方法。我们研究了一种无需重新创建 bean 即可更改其属性的方法。我们还研究了在上下文中强制重新创建 bean 的方法。