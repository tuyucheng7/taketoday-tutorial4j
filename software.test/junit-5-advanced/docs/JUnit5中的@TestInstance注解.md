## 1. 概述

测试类通常包含引用被测试系统、Mock或测试中使用的数据资源的成员变量。**默认情况下，JUnit 4和JUnit 5在运行每个测试方法之前都会创建一个新的测试类实例**。这在测试之间提供了一个清晰的状态分离。

在本教程中，我们将学习[JUnit 5](https://www.baeldung.com/junit-5)如何允许我们使用@TestInstance注解修改测试类的生命周期。我们还将看到这如何帮助我们管理大型资源或更复杂的测试之间的关系。

## 2. 默认测试生命周期

让我们首先看一下JUnit 4和JUnit 5共有的默认测试类生命周期：

```java
class AdditionUnitTest {

    private int sum = 1;

    @Test
    void addingTwoToSumReturnsThree() {
        sum += 2;
        assertEquals(3, sum);
    }

    @Test
    void addingThreeToSumReturnsFour() {
        sum += 3;
        assertEquals(4, sum);
    }
}
```

此代码很容易成为JUnit 4或5测试代码，除了JUnit 5缺少不需要的public关键字。

这些测试之所以通过，是因为在调用每个测试方法之前会创建一个新的AdditionTest实例。这意味着在执行每个测试之前，变量sum的值总是设置为1。

如果测试对象只有一个共享实例，那么变量sum在每次测试后都会保持其状态。结果就会导致第二个测试失败。

## 3. @BeforeClass和@BeforeAll注解

有时我们需要一个对象跨多个测试存在。假设我们想读取一个大文件用作测试数据，由于在每次测试之前重复这个过程可能会很耗时，因此我们可能更希望只读取一次并在整个测试中保留它。

JUnit 4通过其@BeforeClass注解解决了这个问题：

```java
public class TweetSerializerJUnit4UnitTest {
    private static String largeContent;
    private static String content;
    private static String smallContent;

    private static Tweet tweet;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setUpFixture() throws IOException {
        content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
        smallContent = "Lorem ipsum dolor";
        largeContent = new String(Files.readAllBytes(Paths.get("src/test/resources/lorem-ipsum.txt")));
        tweet = new Tweet();
        tweet.setId("AX1346");
    }
}
```

我们应该注意，**我们必须将使用JUnit 4的@BeforeClass标注的变量和方法修饰为static**。

JUnit 5提供了一种不同的方法。它提供了用于静态方法的@BeforeAll注解，用于处理类的静态成员。

但是，如果测试实例生命周期更改为PER_CLASS，则@BeforeAll也可以与实例方法和实例变量一起使用。

## 4. @TestInstance注解

@TestInstance注解允许我们配置JUnit 5测试的生命周期。

**@TestInstance有两种模式**。一个是LifeCycle.PER_METHOD(默认值)，另一个是LifeCycle.PER_CLASS。后者可以让JUnit只创建测试类的一个实例，并在测试方法之间重用它。

让我们用@TestInstance注解来标注我们的测试类，并使用LifeCycle.PER_CLASS模式。

```java
@TestInstance(Lifecycle.PER_CLASS)
class TweetSerializerUnitTest {
    private String largeContent;
    private String content;
    private String smallContent;

    private Tweet tweet;

    @BeforeAll
    void setUpFixture() throws IOException {
        content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
        smallContent = "Lorem ipsum dolor";
        largeContent = new String(Files.readAllBytes(Paths.get("src/test/resources/lorem-ipsum.txt")));
        tweet = new Tweet();
        tweet.setId("AX1346");
    }
}
```

正如我们所见，没有一个变量或方法是静态的。**当我们使用PER_CLASS生命周期时，我们可以使用@BeforeAll标注实例方法**。

我们还应该注意到，一个测试对实例变量状态所做的更改现在将对其他测试可见。

## 5. @TestInstance(PER_CLASS)的使用

### 5.1 开销昂贵的资源

当在每次测试之前实例化一个类非常耗时的时候，此注解很有用。例如，建立数据库连接或者加载一个大文件。

以前解决这个问题会导致静态变量和实例变量的复杂混合，现在使用共享测试类实例会更简洁。

### 5.2 有意共享状态

**在单元测试中，测试之间共享状态通常是一种反常规模式，但在集成测试中可能很有用**。PER_CLASS生命周期支持有意共享状态的顺序测试。这可能是必要的，以避免以后的测试必须重复以前测试的步骤，尤其是在将被测试系统恢复到正确状态的速度很慢的情况下。

在共享状态时，为了按顺序执行所有测试，JUnit 5为我们提供了类级别的[@TestMethodOrder](https://www.baeldung.com/junit-5-test-order)注解。然后我们可以在测试方法上使用[@Order](https://www.baeldung.com/junit-5-test-order)注解，按照我们指定的顺序执行它们。

```java
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class OrderUnitTest {

    private int sum;

    @BeforeAll
    void init() {
        sum = 1;
    }

    @Test
    @Order(1)
    void firstTest() {
        sum += 2;
        assertEquals(3, sum);
    }

    @Test
    @Order(2)
    void secondTest() {
        sum += 3;
        assertEquals(6, sum);
    }
}
```

### 5.3 共享某些状态

共享同一个测试类实例的挑战在于，有些成员变量可能需要在测试之间进行清理，而有些成员变量可能需要在整个测试期间进行维护。

**我们可以使用带有[@BeforeEach](https://www.baeldung.com/junit-before-beforeclass-beforeeach-beforeall)或@AfterEach注解的方法重置需要在测试之间清理的变量**。

## 6. 总结

在本教程中，我们了解了@TestInstance注解以及如何使用它来配置JUnit 5测试的生命周期。

我们还研究了为什么在处理共享资源或有意编写顺序测试方面共享测试类的单个实例可能很有用。