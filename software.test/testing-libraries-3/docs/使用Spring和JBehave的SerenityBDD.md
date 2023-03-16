## 1. 概述

之前，我们已经介绍了[Serenity BDD框架](https://www.baeldung.com/serenity-bdd)。

在本文中，我们将介绍如何将Serenity BDD与Spring集成。

## 2. Maven依赖

为了在我们的Spring项目中启用Serenity，我们需要将[serenity-core](https://central.sonatype.com/artifact/net.serenity-bdd/serenity-core/3.6.12)和[serenity-spring](https://central.sonatype.com/artifact/net.serenity-bdd/serenity-spring/3.6.12)添加到pom.xml中：

```xml
<dependency>
    <groupId>net.serenity-bdd</groupId>
    <artifactId>serenity-core</artifactId>
    <version>1.4.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>net.serenity-bdd</groupId>
    <artifactId>serenity-spring</artifactId>
    <version>1.4.0</version>
    <scope>test</scope>
</dependency>
```

我们还需要配置[serenity-maven-plugin](https://central.sonatype.com/artifact/net.serenity-bdd.maven.plugins/serenity-maven-plugin/3.6.12)，这对于生成Serenity测试报告很重要：

```xml
<plugin>
    <groupId>net.serenity-bdd.maven.plugins</groupId>
    <artifactId>serenity-maven-plugin</artifactId>
    <version>1.4.0</version>
    <executions>
        <execution>
            <id>serenity-reports</id>
            <phase>post-integration-test</phase>
            <goals>
                <goal>aggregate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## 3. Spring集成

Spring集成测试需要@RunWith [SpringJUnit4ClassRunner](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/test/context/junit4/SpringJUnit4ClassRunner.html)。但是我们不能直接将测试Runner与Serenity一起使用，因为Serenity测试需要由SerenityRunner运行。

对于Serenity的测试，我们可以使用SpringIntegrationMethodRule和SpringIntegrationClassRule来启用注入。

我们将基于一个简单的场景进行测试：给定一个数字，当与另一个数字相加时，然后返回总和。

### 3.1 SpringIntegrationMethodRule

SpringIntegrationMethodRule是应用于测试方法的[MethodRule](http://junit.org/junit4/javadoc/4.12/org/junit/rules/MethodRule.html)。Spring上下文将在@Before之前和@BeforeClass之后构建。

假设我们有一个属性要注入到我们的bean中：

```xml
<util:properties id="props">
    <prop key="adder">4</prop>
</util:properties>
```

现在让我们添加SpringIntegrationMethodRule以在我们的测试中启用值注入：

```java
@RunWith(SerenityRunner.class)
@ContextConfiguration(locations = "classpath:adder-beans.xml")
public class AdderMethodRuleIntegrationTest {

    @Rule
    public SpringIntegrationMethodRule springMethodIntegration
          = new SpringIntegrationMethodRule();

    @Steps
    private AdderSteps adderSteps;

    @Value("#{props['adder']}")
    private int adder;

    @Test
    public void givenNumber_whenAdd_thenSummedUp() {
        adderSteps.givenNumber();
        adderSteps.whenAdd(adder);
        adderSteps.thenSummedUp();
    }
}
```

它还支持Spring Test的方法级注解。如果某些测试方法弄脏了测试上下文，我们可以在其上标记[@DirtiesContext](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/test/annotation/DirtiesContext.html)：

```java
@RunWith(SerenityRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(classes = AdderService.class)
public class AdderMethodDirtiesContextIntegrationTest {

    @Steps private AdderServiceSteps adderServiceSteps;

    @Rule public SpringIntegrationMethodRule springIntegration = new SpringIntegrationMethodRule();

    @DirtiesContext
    @Test
    public void _0_givenNumber_whenAddAndAccumulate_thenSummedUp() {
        adderServiceSteps.givenBaseAndAdder(randomInt(), randomInt());
        adderServiceSteps.whenAccumulate();
        adderServiceSteps.summedUp();

        adderServiceSteps.whenAdd();
        adderServiceSteps.sumWrong();
    }

    @Test
    public void _1_givenNumber_whenAdd_thenSumWrong() {
        adderServiceSteps.whenAdd();
        adderServiceSteps.sumWrong();
    }
}
```

在上面的例子中，当我们调用adderServiceSteps.whenAccumulate()时，adderServiceSteps中注入的@Service的base字段将被更改：

```java
@ContextConfiguration(classes = AdderService.class)
public class AdderServiceSteps {

    @Autowired
    private AdderService adderService;

    private int givenNumber;
    private int base;
    private int sum;

    public void givenBaseAndAdder(int base, int adder) {
        this.base = base;
        adderService.baseNum(base);
        this.givenNumber = adder;
    }

    public void whenAdd() {
        sum = adderService.add(givenNumber);
    }

    public void summedUp() {
        assertEquals(base + givenNumber, sum);
    }

    public void sumWrong() {
        assertNotEquals(base + givenNumber, sum);
    }

    public void whenAccumulate() {
        sum = adderService.accumulate(givenNumber);
    }
}
```

具体来说，我们将总和分配给基数：

```java
@Service
public class AdderService {

    private int num;

    public void baseNum(int base) {
        this.num = base;
    }

    public int currentBase() {
        return num;
    }

    public int add(int adder) {
        return this.num + adder;
    }

    public int accumulate(int adder) {
        return this.num += adder;
    }
}
```

在第一个测试_0_givenNumber_whenAddAndAccumulate_thenSummedUp中，基数被更改，使上下文变脏。当我们尝试相加另一个数字时，我们不会得到预期的总和。

请注意，即使我们用@DirtiesContext标记了第一个测试，第二个测试仍然受到影响：相加后，总和仍然是错误的。为什么？

现在，在处理方法级别@DirtiesContext时，Serenity的Spring集成只为当前测试实例重新构建测试上下文。@Steps中的底层依赖上下文不会被重新构建。

要解决这个问题，我们可以在当前的测试实例中注入@Service，并将Service作为@Steps的显式依赖：

```java
@RunWith(SerenityRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(classes = AdderService.class)
public class AdderMethodDirtiesContextDependencyWorkaroundIntegrationTest {

    private AdderConstructorDependencySteps adderSteps;

    @Autowired private AdderService adderService;

    @Before
    public void init() {
        adderSteps = new AdderConstructorDependencySteps(adderService);
    }

    //...
}
```

```java
public class AdderConstructorDependencySteps {

    private AdderService adderService;

    public AdderConstructorDependencySteps(AdderService adderService) {
        this.adderService = adderService;
    }

    // ...
}
```

或者我们可以将条件初始化步骤放在@Before部分以避免脏上下文。但是这种解决方案在某些复杂的情况下可能不可用。

```java
@RunWith(SerenityRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(classes = AdderService.class)
public class AdderMethodDirtiesContextInitWorkaroundIntegrationTest {

    @Steps private AdderServiceSteps adderServiceSteps;

    @Before
    public void init() {
        adderServiceSteps.givenBaseAndAdder(randomInt(), randomInt());
    }

    //...
}
```

### 3.2 SpringIntegrationClassRule

要启用类级别注解，我们应该使用SpringIntegrationClassRule。假设我们有以下测试类；每个都弄脏了上下文：

```java
@RunWith(SerenityRunner.class)
@ContextConfiguration(classes = AdderService.class)
public static abstract class Base {

    @Steps AdderServiceSteps adderServiceSteps;

    @ClassRule public static SpringIntegrationClassRule springIntegrationClassRule = new SpringIntegrationClassRule();

    void whenAccumulate_thenSummedUp() {
        adderServiceSteps.whenAccumulate();
        adderServiceSteps.summedUp();
    }

    void whenAdd_thenSumWrong() {
        adderServiceSteps.whenAdd();
        adderServiceSteps.sumWrong();
    }

    void whenAdd_thenSummedUp() {
        adderServiceSteps.whenAdd();
        adderServiceSteps.summedUp();
    }
}
```

```java
@DirtiesContext(classMode = AFTER_CLASS)
public static class DirtiesContextIntegrationTest extends Base {

    @Test
    public void givenNumber_whenAdd_thenSumWrong() {
        super.whenAdd_thenSummedUp();
        adderServiceSteps.givenBaseAndAdder(randomInt(), randomInt());
        super.whenAccumulate_thenSummedUp();
        super.whenAdd_thenSumWrong();
    }
}
```

```java
@DirtiesContext(classMode = AFTER_CLASS)
public static class AnotherDirtiesContextIntegrationTest extends Base {

    @Test
    public void givenNumber_whenAdd_thenSumWrong() {
        super.whenAdd_thenSummedUp();
        adderServiceSteps.givenBaseAndAdder(randomInt(), randomInt());
        super.whenAccumulate_thenSummedUp();
        super.whenAdd_thenSumWrong();
    }
}
```

在此示例中，将为类级别@DirtiesContext重新构建所有隐式注入。

### 3.3 SpringIntegrationSerenityRunner

有一个方便的类SpringIntegrationSerenityRunner可以自动添加上面的两个集成规则。我们可以使用这个Runner运行上面的测试，以避免在我们的测试中指定方法或类测试规则：

```java
@RunWith(SpringIntegrationSerenityRunner.class)
@ContextConfiguration(locations = "classpath:adder-beans.xml")
public class AdderSpringSerenityRunnerIntegrationTest {

    @Steps private AdderSteps adderSteps;

    @Value("#{props['adder']}") private int adder;

    @Test
    public void givenNumber_whenAdd_thenSummedUp() {
        adderSteps.givenNumber();
        adderSteps.whenAdd(adder);
        adderSteps.thenSummedUp();
    }
}
```

## 4. SpringMVC集成

如果我们只需要使用Serenity测试SpringMVC组件，我们可以简单地在[Rest-Assured](https://www.baeldung.com/rest-assured-tutorial)中使用RestAssuredMockMvc，而不是serenity-spring集成。

### 4.1 Maven依赖

我们需要将[rest-assuredspring-mock-mvc](https://central.sonatype.com/artifact/io.rest-assured/spring-mock-mvc/5.3.0)依赖添加到pom.xml中：

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>spring-mock-mvc</artifactId>
    <version>3.0.3</version>
    <scope>test</scope>
</dependency>
```

### 4.2 RestAssuredMockMvc实践

现在让我们测试以下控制器：

```java
@RequestMapping(value = "/adder", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
public class PlainAdderController {

    private final int currentNumber = RandomUtils.nextInt();

    @GetMapping("/current")
    public int currentNum() {
        return currentNumber;
    }

    @PostMapping
    public int add(@RequestParam int num) {
        return currentNumber + num;
    }
}
```

我们可以像这样利用RestAssuredMockMvc的MVC Mock工具：

```java
@RunWith(SerenityRunner.class)
public class AdderMockMvcIntegrationTest {

    @Before
    public void init() {
        RestAssuredMockMvc.standaloneSetup(new PlainAdderController());
    }

    @Steps AdderRestSteps steps;

    @Test
    public void givenNumber_whenAdd_thenSummedUp() throws Exception {
        steps.givenCurrentNumber();
        steps.whenAddNumber(randomInt());
        steps.thenSummedUp();
    }
}
```

那么剩下的部分和我们使用Rest-Assured没有什么不同：

```java
public class AdderRestSteps {

    private MockMvcResponse mockMvcResponse;
    private int currentNum;

    @Step("get the current number")
    public void givenCurrentNumber() throws UnsupportedEncodingException {
        currentNum = Integer.valueOf(given()
              .when()
              .get("/adder/current")
              .mvcResult()
              .getResponse()
              .getContentAsString());
    }

    @Step("adding {0}")
    public void whenAddNumber(int num) {
        mockMvcResponse = given()
              .queryParam("num", num)
              .when()
              .post("/adder");
        currentNum += num;
    }

    @Step("got the sum")
    public void thenSummedUp() {
        mockMvcResponse
              .then()
              .statusCode(200)
              .body(equalTo(currentNum + ""));
    }
}
```

## 5. Serenity、JBehave和Spring

Serenity的Spring集成支持与[JBehave](https://www.baeldung.com/jbehave-rest-testing)无缝协作。让我们将我们的测试场景写成一个JBehave故事：

```gherkin
Scenario: A user can submit a number to adder and get the sum
Given a number
When I submit another number 5 to adder
Then I get a sum of the numbers
```

我们可以在@Service中实现逻辑并通过API公开操作：

```java
@RequestMapping(value = "/adder", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
public class AdderController {

    private AdderService adderService;

    public AdderController(AdderService adderService) {
        this.adderService = adderService;
    }

    @GetMapping("/current")
    public int currentNum() {
        return adderService.currentBase();
    }

    @PostMapping
    public int add(@RequestParam int num) {
        return adderService.add(num);
    }
}
```

现在我们可以在RestAssuredMockMvc的帮助下构建Serenity-JBehave测试，如下所示：

```java
@ContextConfiguration(classes = {AdderController.class, AdderService.class })
public class AdderIntegrationTest extends SerenityStory {

    @Autowired private AdderService adderService;

    @BeforeStory
    public void init() {
        RestAssuredMockMvc.standaloneSetup(new AdderController(adderService));
    }
}
```

```java
public class AdderStory {

    @Steps AdderRestSteps restSteps;

    @Given("a number")
    public void givenANumber() throws Exception{
        restSteps.givenCurrentNumber();
    }

    @When("I submit another number $num to adder")
    public void whenISubmitToAdderWithNumber(int num){
        restSteps.whenAddNumber(num);
    }

    @Then("I get a sum of the numbers")
    public void thenIGetTheSum(){
        restSteps.thenSummedUp();
    }
}
```

我们只能用@ContextConfiguration标记SerenityStory，然后自动启用Spring注入。这与@Steps上的@ContextConfiguration完全相同。

## 6. 总结

在本文中，我们介绍了如何将Serenity BDD与Spring集成。