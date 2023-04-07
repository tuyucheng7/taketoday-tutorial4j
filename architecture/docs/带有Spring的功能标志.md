## 1. 概述

在本文中，我们将简要定义功能标志，并提出一种固执己见且务实的方法来在 Spring Boot 应用程序中实现它们。然后，我们将深入研究利用不同 Spring Boot 功能的更复杂的迭代。

我们将讨论可能需要功能标记的各种场景，并讨论可能的解决方案。我们将使用比特币矿工示例应用程序来执行此操作。

## 2. 功能标志

功能标志——有时称为功能切换——是一种机制，允许我们启用或禁用应用程序的特定功能，而无需修改代码，或者在理想情况下重新部署我们的应用程序。

根据给定功能标志所需的动态，我们可能需要在全局、每个应用程序实例或更细粒度地配置它们——可能是每个用户或请求。

与软件工程中的许多情况一样，重要的是尝试使用最直接的方法来解决手头的问题而不增加不必要的复杂性。

功能标志是一种强大的工具，如果使用得当，可以为我们的系统带来可靠性和稳定性。然而，当它们被滥用或维护不足时，它们很快就会成为复杂性和令人头疼的问题的来源。

在许多情况下，功能标志可以派上用场：

基于主干的开发和重要功能

在基于主干的开发中，特别是当我们想要保持频繁集成时，我们可能会发现自己还没有准备好发布某个功能。功能标志可以派上用场，使我们能够继续发布而不使我们的更改可用，直到完成。

特定于环境的配置

我们可能会发现自己需要某些功能来为 E2E 测试环境重置我们的数据库。

或者，我们可能需要为非生产环境使用与生产环境不同的安全配置。

因此，我们可以利用功能标志在正确的环境中切换正确的设置。

A/B测试

为同一个问题发布多个解决方案并衡量影响是一项引人注目的技术，我们可以使用功能标志来实现。

金丝雀发布

在部署新功能时，我们可能会决定逐步进行，从一小部分用户开始，并在我们验证其行为的正确性时扩大其采用范围。功能标志使我们能够实现这一目标。

在以下部分中，我们将尝试提供一种实用的方法来解决上述情况。

让我们分解功能标记的不同策略，从最简单的场景开始，然后进入更精细和更复杂的设置。

## 3. 应用级特征标志

如果我们需要解决前两个用例中的任何一个，应用程序级功能标志是让事情正常进行的简单方法。

一个简单的功能标志通常会涉及一个属性和一些基于该属性值的配置。

### 3.1. 使用 Spring 配置文件的功能标志

在 Spring 中，我们可以[利用 profiles](https://www.baeldung.com/spring-profiles)。方便的是，配置文件使我们能够有选择地配置某些 bean。通过一些围绕它们的构造，我们可以快速为应用程序级功能标志创建一个简单而优雅的解决方案。

假设我们正在构建一个比特币挖矿系统。我们的软件已经投入生产，我们的任务是创建一个实验性的、改进的挖掘算法。

在我们的JavaConfig中，我们可以分析我们的组件：

```java
@Configuration
public class ProfiledMiningConfig {

    @Bean
    @Profile("!experimental-miner")
    public BitcoinMiner defaultMiner() {
        return new DefaultBitcoinMiner();
    }

    @Bean
    @Profile("experimental-miner")
    public BitcoinMiner experimentalMiner() {
        return new ExperimentalBitcoinMiner();
    }
}复制
```

然后，使用之前的配置，我们只需要包含我们的配置文件以选择加入我们的新功能。一般配置我们的应用程序并[特别启用配置文件](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html)的方法有[很多](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)。同样，有一些[测试实用程序](https://docs.spring.io/spring/docs/current/spring-framework-reference/testing.html#activeprofiles)可以让我们的生活更轻松。

只要我们的系统足够简单，我们就可以创建一个基于环境的配置来确定要应用哪些功能标志以及要忽略哪些功能标志。

假设我们有一个基于卡片而不是表格的新 UI，以及之前的实验性矿工。

我们希望在我们的验收环境 (UAT) 中启用这两个功能。我们可以在application.yml文件中创建以下配置文件组：

```plaintext
spring:
  profiles:
    group:
      uat: experimental-miner,ui-cards复制
```

有了之前的属性，我们只需要在 UAT 环境中启用 UAT 配置文件即可获得所需的功能集。当然，我们也可以在我们的项目中添加一个application-uat.yml文件来为我们的环境设置包含额外的属性。

在我们的例子中，我们希望uat配置文件也包括experimental-miner和ui-cards。

注意：如果我们使用的是 2.4.0 之前的 Spring Boot 版本，我们将使用UAT 配置文件特定文档中的spring.profiles.include属性来配置其他配置文件。与spring.profiles.active 相比，前者使我们能够以添加的方式包含配置文件。

### 3.2. 使用自定义属性的功能标志

配置文件是完成工作的一种非常简单的方法。但是，我们可能出于其他目的需要配置文件。或者，我们可能想要构建一个更加结构化的特征标志基础设施。

对于这些场景，自定义属性可能是一个理想的选择。

让我们利用@ConditionalOnProperty和我们的命名空间重写我们之前的例子：

```java
@Configuration
public class CustomPropsMiningConfig {

    @Bean
    @ConditionalOnProperty(
      name = "features.miner.experimental", 
      matchIfMissing = true)
    public BitcoinMiner defaultMiner() {
        return new DefaultBitcoinMiner();
    }

    @Bean
    @ConditionalOnProperty(
      name = "features.miner.experimental")
    public BitcoinMiner experimentalMiner() {
        return new ExperimentalBitcoinMiner();
    }
}复制
```

前面的示例构建在 Spring Boot 的条件配置之上，并配置一个或另一个组件，具体取决于该属性是设置为true还是false(或完全省略)。

结果与 3.1 中的非常相似，但是现在，我们有了自己的命名空间。有了我们的命名空间，我们就可以创建有意义的 YAML/属性文件：

```plaintext
#[...] Some Spring config

features:
  miner:
    experimental: true
  ui:
    cards: true
    
#[...] Other feature flags复制
```

此外，这个新设置允许我们为我们的功能标志添加前缀——在我们的例子中，使用功能前缀。

这似乎是一个小细节，但随着我们的应用程序的增长和复杂性的增加，这个简单的迭代将帮助我们控制我们的功能标志。

让我们谈谈这种方法的其他好处。

### 3.3. 使用@ConfigurationProperties

一旦我们获得一组前缀属性，我们就可以创建一个[用 @ConfigurationProperties 装饰的 POJO](https://www.baeldung.com/configuration-properties-in-spring-boot)，以在我们的代码中获得一个编程句柄。

按照我们正在进行的示例：

```java
@Component
@ConfigurationProperties(prefix = "features")
public class ConfigProperties {

    private MinerProperties miner;
    private UIProperties ui;

    // standard getters and setters

    public static class MinerProperties {
        private boolean experimental;
        // standard getters and setters
    }

    public static class UIProperties {
        private boolean cards;
        // standard getters and setters
    }
}复制
```

通过将我们的功能标志的状态放在一个有凝聚力的单元中，我们开辟了新的可能性，使我们能够轻松地将这些信息公开给我们系统的其他部分，例如 UI，或下游系统。

### 3.4. 公开功能配置

我们的比特币挖矿系统进行了 UI 升级，但尚未完全准备好。出于这个原因，我们决定对其进行功能标记。我们可能有一个使用 React、Angular 或 Vue 的单页应用程序。

无论使用何种技术，我们都需要知道启用了哪些功能，以便我们可以相应地呈现我们的页面。

让我们创建一个简单的端点来为我们的配置提供服务，以便我们的 UI 可以在需要时查询后端：

```java
@RestController
public class FeaturesConfigController {

    private ConfigProperties properties;

    // constructor

    @GetMapping("/feature-flags")
    public ConfigProperties getProperties() {
        return properties;
    }
}复制
```

可能有更复杂的方法来提供此信息，例如[创建自定义执行器端点](https://www.baeldung.com/spring-boot-actuators)。但就本指南而言，控制器端点感觉是一个足够好的解决方案。

### 3.5. 保持营地清洁

虽然这听起来很明显，但一旦我们深思熟虑地实现了我们的功能标志，同样重要的是在不再需要它们时保持纪律性地摆脱它们。

第一个用例的功能标志——基于主干的开发和重要功能——通常是短暂的。这意味着我们将需要确保我们的ConfigProperties、我们的 Java 配置和我们的YAML文件保持干净和最新。

## 4. 更细粒度的特征标志

有时我们发现自己处于更复杂的场景中。对于 A/B 测试或金丝雀发布，我们以前的方法根本不够。

要获得更细粒度的功能标志，我们可能需要创建我们的解决方案。这可能涉及自定义我们的用户实体以包含特定于功能的信息，或者可能扩展我们的网络框架。

然而，用功能标志污染我们的用户可能不是一个对每个人都有吸引力的想法，并且还有其他解决方案。

作为替代方案，我们可以利用一些内置工具，[例如 Togglz](https://www.togglz.org/)。这个工具增加了一些复杂性，但提供了一个很好的开箱即用的解决方案，并[提供了与 Spring Boot 的一流集成](https://www.togglz.org/documentation/spring-boot-starter.html)。

Togglz 支持不同的[激活策略](https://www.togglz.org/documentation/activation-strategies.html)：

1.  用户名：与特定用户关联的标志
2.  逐步推出：为一定比例的用户群启用标志。这对于 Canary 版本很有用，例如，当我们想要验证我们的功能的行为时
3.  发布日期：我们可以安排在特定日期和时间启用标志。这可能对产品发布、协调发布或优惠和折扣有用
4.  客户端 IP：基于客户端 IP 的标记功能。在将特定配置应用于特定客户时，这些可能会派上用场，因为他们具有静态 IP
5.  服务器 IP：在这种情况下，服务器的 IP 用于确定是否应启用某个功能。这可能对金丝雀发布也很有用，其方法与逐步推出略有不同——比如当我们想要评估实例中的性能影响时
6.  ScriptEngine：我们可以启用基于[任意脚本的](https://docs.oracle.com/en/java/javase/11/docs/api/java.scripting/javax/script/ScriptEngine.html)功能标志。这可以说是最灵活的选择
7.  系统属性：我们可以设置某些系统属性来确定功能标志的状态。这与我们用最直接的方法取得的成果非常相似

## 5.总结

在本文中，我们有机会讨论功能标志。此外，我们还讨论了 Spring 如何在不添加新库的情况下帮助我们实现其中的一些功能。

我们首先定义此模式如何帮助我们处理一些常见用例。

接下来，我们使用 Spring 和 Spring Boot 开箱即用的工具构建了一些简单的解决方案。有了这个，我们想出了一个简单而强大的功能标记结构。

在下面，我们比较了几种选择。从更简单、更不灵活的解决方案转向更复杂但更复杂的模式。

最后，我们简要地提供了一些指南来构建更强大的解决方案。当我们需要更高的粒度时，这很有用。