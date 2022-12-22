## 1. 概述

从Spring Boot 1.3开始，我们可以**使用EnvironmentPostProcessor在刷新应用程序上下文之前自定义应用程序的环境**。

在本教程中，让我们看一下如何将自定义属性加载并转换到环境中，然后访问这些属性。

## 2. Spring环境

Spring中的Environment抽象表示当前应用程序正在运行的环境，同时，它趋向于统一各种属性源中属性的访问方式，如属性文件、JVM系统属性、系统环境变量、Servlet上下文参数等。

**因此在大多数情况下，自定义环境意味着在将各种属性暴露给我们的bean之前对其进行操作**。

## 3. 一个简单的例子

现在让我们构建一个简单的价格计算应用程序，它将以基于毛额或基于净额的模式计算价格，来自第三方的系统环境变量将决定选择哪种计算模式。

### 3.1 实现EnvironmentPostProcessor

为此，让我们实现EnvironmentPostProcessor接口。

我们将使用它来读取几个环境变量：

```bash
calculation_mode=GROSS 
gross_calculation_tax_rate=0.15
```

我们将使用后处理器以特定于应用程序的方式公开这些内容，在本例中使用自定义前缀：

```bash
cn.tuyucheng.taketoday.environmentpostprocessor.calculation.mode=GROSS
cn.tuyucheng.taketoday.environmentpostprocessor.gross.calculation.tax.rate=0.15
```

然后，我们可以非常简单地将我们的新属性添加到环境中：

```java
@Order(Ordered.LOWEST_PRECEDENCE)
public class PriceCalculationEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        PropertySource<?> system = environment.getPropertySources()
              .get(SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME);
        if (!hasOurPriceProperties(system)) {
            // error handling code omitted
        }
        Map<String, Object> prefixed = names.stream()
              .collect(Collectors.toMap(this::rename, system::getProperty));
        environment.getPropertySources()
              .addAfter(SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, new MapPropertySource("prefixer", prefixed));
    }
}
```

让我们看看我们在这里做了什么。首先，**我们要求环境为我们提供环境变量的PropertySource**，调用生成的system.getProperty类似于调用Java的System.getenv().get。

然后，只要这些属性存在于环境中，**我们就会创建一个新Map**，加上前缀。为简洁起见，我们跳过重命名的内容，但请查看完整实现的代码示例。**生成的map具有与system相同的值，但带有prefixer键**。

最后，我们将把新的PropertySource添加到环境中。现在，如果一个bean请求cn.tuyucheng.taketoday.environmentpostprocessor.calculation.mode，环境将参考我们的Map。

请注意，顺便说一句，[EnvironmentPostProcessor的Javadoc](https://github.com/spring-projects/spring-boot/blob/v2.1.3.RELEASE/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/env/EnvironmentPostProcessor.java#L31)鼓励我们实现Ordered接口或使用[@Order注解]()。

当然，这只是一个单一的属性来源。Spring Boot使我们能够迎合众多的来源和格式。

### 3.2 在spring.factories中注册

要在Spring Boot引导过程中调用该实现，我们需要在META-INF/spring.factories中注册类：

```properties
org.springframework.boot.env.EnvironmentPostProcessor=
  cn.tuyucheng.taketoday.environmentpostprocessor.PriceCalculationEnvironmentPostProcessor
```

### 3.3 使用@Value注解访问属性

在示例中，我们有一个PriceCalculator接口，其中包含两个实现：GrossPriceCalculator和NetPriceCalculator。

在我们的实现中，**我们可以只使用[@Value]()来检索我们的新属性**：

```java
public class GrossPriceCalculator implements PriceCalculator {
    @Value("${cn.tuyucheng.taketoday.environmentpostprocessor.gross.calculation.tax.rate}")
    double taxRate;

    @Override
    public double calculate(double singlePrice, int quantity) {
        // calcuation implementation omitted
    }
}
```

这很好，因为它与我们访问任何其他属性的方式相同，例如我们在application.properties中定义的属性。

### 3.4 访问Spring Boot自动配置中的属性

现在，让我们看一个复杂的案例，我们在Spring Boot自动配置中访问前面的属性。

我们将创建自动配置类来读取这些属性，此类将根据不同的属性值初始化并注入应用程序上下文中的bean：

```java
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class PriceCalculationAutoConfig {
    @Bean
    @ConditionalOnProperty(name = "cn.tuyucheng.taketoday.environmentpostprocessor.calculation.mode", havingValue = "NET")
    @ConditionalOnMissingBean
    public PriceCalculator getNetPriceCalculator() {
        return new NetPriceCalculator();
    }

    @Bean
    @ConditionalOnProperty(name = "cn.tuyucheng.taketoday.environmentpostprocessor.calculation.mode", havingValue = "GROSS")
    @ConditionalOnMissingBean
    public PriceCalculator getGrossPriceCalculator() {
        return new GrossPriceCalculator();
    }
}
```

与EnvironmentPostProcessor实现类似，自动配置类也需要在META-INF/spring.factories中注册：

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=
  cn.tuyucheng.taketoday.environmentpostprocessor.autoconfig.PriceCalculationAutoConfig
```

这是有效的，因为**自定义EnvironmentPostProcessor实现在Spring Boot自动配置之前启动**，这种组合使Spring Boot自动配置功能更加强大。

并且，有关Spring Boot自动配置的更多细节，请查看有关[使用Spring Boot进行自定义自动配置]()的文章。

## 4. 测试自定义实现

我们可以通过运行以下命令在Windows中设置系统环境变量：

```bash
set calculation_mode=GROSS
set gross_calculation_tax_rate=0.15
```

或者在Linux/Unix中，我们可以导出它们：

```bash
export calculation_mode=GROSS 
export gross_calculation_tax_rate=0.15
```

之后，我们可以使用mvn spring-boot:run命令开始测试：

```bash
mvn spring-boot:run
  -Dstart-class=cn.tuyucheng.taketoday.environmentpostprocessor.PriceCalculationApplication
  -Dspring-boot.run.arguments="100,4"
```

## 5. 总结

**总而言之，EnvironmentPostProcessor实现能够从不同位置加载各种格式的任意文件**。此外，我们可以进行任何需要的转换，以使属性在环境中随时可用以供以后使用。当我们将基于Spring Boot的应用程序与第三方配置集成时，这种自由当然很有用。