## 1. 简介

在这个快速教程中，我们将集成Drools与Spring。如果你刚刚开始使用Drools，请查看[这篇](https://www.baeldung.com/drools)介绍文章。

## 2. Maven依赖

让我们首先将以下依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.drools</groupId>
    <artifactId>drools-core</artifactId>
    <version>7.0.0.Final</version>
</dependency>
<dependency>
    <groupId>org.kie</groupId>
    <artifactId>kie-spring</artifactId>
    <version>7.0.0.Final</version>
</dependency>
```

可以在此处找到最新版本的[drools-core](https://central.sonatype.com/artifact/org.drools/drools-core/8.34.0.Final)和此处的[kie-spring](https://central.sonatype.com/artifact/org.kie/kie-spring/7.73.0.Final)。

## 3. 初始数据

现在让我们定义将在我们的示例中使用的数据。我们将根据行驶距离和夜间附加费标志计算乘车费用。

这是一个将用作Fact的简单对象：

```java
public class TaxiRide {
    private Boolean isNightSurcharge;
    private Long distanceInMile;

    // standard constructors, getters/setters
}
```

我们还定义另一个用于表示票价的业务对象：

```java
public class Fare {
    private Long nightSurcharge;
    private Long rideFare;

    // standard constructors, getters/setters
}
```

现在，让我们定义一个计算出租车费用的业务规则：

```drl
global cn.tuyucheng.taketoday.spring.drools.model.Fare rideFare;
dialect  "mvel"

rule "Calculate Taxi Fare - Scenario 1"
    when
        taxiRideInstance:TaxiRide(isNightSurcharge == false && distanceInMile < 10);
    then
      	rideFare.setNightSurcharge(0);
       	rideFare.setRideFare(70);
end
```

如我们所见，定义了一个规则来计算给定TaxiRide的总票价。

此规则接收TaxiRide对象并检查isNightSurcharge属性是否为false且distanceInMile属性值是否小于10，然后计算票价为70并将nightSurcharge属性设置为0。

计算出的输出设置为Fare对象以供进一步使用。

## 4. Spring集成

### 4.1 Spring Bean配置

现在，让我们继续进行Spring集成。

我们将定义一个Spring bean配置类-它将负责实例化TaxiFareCalculatorService bean及其依赖项：

```java
@Configuration
@ComponentScan("cn.tuyucheng.taketoday.spring.drools.service")
public class TaxiFareConfiguration {
    private static final String drlFile = "TAXI_FARE_RULE.drl";

    @Bean
    public KieContainer kieContainer() {
        KieServices kieServices = KieServices.Factory.get();

        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource(drlFile));
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        KieModule kieModule = kieBuilder.getKieModule();

        return kieServices.newKieContainer(kieModule.getReleaseId());
    }
}
```

KieServices是一个单例，充当单点入口以获取Kie提供的所有服务。KieServices是使用KieServices.Factory.get()检索的。

接下来，我们需要获取KieContainer，它是运行规则引擎所需的所有对象的占位符。

KieContainer是在其他bean的帮助下构建的，包括KieFileSystem、KieBuilder和KieModule。

让我们继续创建一个KieModule，它是定义称为KieBase的规则知识所需的所有资源的容器。

```java
KieModule kieModule = kieBuilder.getKieModule();
```

>   KieBase是一个存储库，其中包含与应用程序相关的所有知识，例如规则、流程、函数、类型模型，它隐藏在KieModule中。KieBase可以从KieContainer中获取。

创建KieModule后，我们可以继续创建KieContainer-其中包含已定义KieBase的KieModule。KieContainer是使用一个模块创建的：

```java
KieContainer kContainer = kieServices.newKieContainer(kieModule.getReleaseId());
```

### 4.2 Spring服务

让我们定义一个服务类，它通过将Fact对象传递给处理结果的引擎来执行实际的业务逻辑：

```java
@Service
public class TaxiFareCalculatorService {

    @Autowired
    private KieContainer kieContainer;

    public Long calculateFare(TaxiRide taxiRide, Fare rideFare) {
        KieSession kieSession = kieContainer.newKieSession();
        kieSession.setGlobal("rideFare", rideFare);
        kieSession.insert(taxiRide);
        kieSession.fireAllRules();
        kieSession.dispose();
        return rideFare.getTotalFare();
    }
}
```

最后，使用KieContainer实例创建一个KieSession。KieSession实例是可以插入输入数据的地方。KieSession与引擎交互，根据插入的Fact处理规则中定义的实际业务逻辑。

Global (就像一个全局变量)用于将信息传递到引擎中。我们可以使用setGlobal("key", value)设置Global；在此示例中，我们将Fare对象设置为Global以存储计算出的出租车费用。

正如我们在第4节中讨论的那样，**规则需要数据进行操作**。我们使用kieSession.insert(taxiRide)将Fact插入到会话中。

完成输入Fact的设置后，我们可以通过调用fireAllRules()请求引擎执行业务逻辑。

最后，我们需要通过调用dispose()方法来清理会话以避免内存泄漏。

## 5. 实际示例

现在，我们可以注入一个Spring上下文并查看Drools是否按预期工作：

```java
@Test
public void whenNightSurchargeFalseAndDistLessThan10_thenFixWithoutNightSurcharge() {
    TaxiRide taxiRide = new TaxiRide();
    taxiRide.setIsNightSurcharge(false);
    taxiRide.setDistanceInMile(9L);
    Fare rideFare = new Fare();
    Long totalCharge = taxiFareCalculatorService.calculateFare(taxiRide, rideFare);
 
    assertNotNull(totalCharge);
    assertEquals(Long.valueOf(70), totalCharge);
}
```

## 6. 总结

在本文中，我们通过一个简单的用例了解了Drools与Spring集成。