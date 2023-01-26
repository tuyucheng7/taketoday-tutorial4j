## 1. JUnit Jupiter集成

Instancio通过[InstancioExtension](https://javadoc.io/doc/org.instancio/instancio-junit/latest/org/instancio/junit/InstancioExtension.html)
支持JUnit 5，并且可以与其他测试框架的Extension(JUnit 5的扩展模型)结合使用。该扩展添加了一些有用的功能，例如：

+
将[@InstancioSource](https://javadoc.io/doc/org.instancio/instancio-junit/latest/org/instancio/junit/InstancioSource.html)
与@ParameterizedTest方法一起使用的能力
+ 使用[@WithSettings](https://javadoc.io/doc/org.instancio/instancio-junit/latest/org/instancio/junit/WithSettings.html)
  注入自定义设置
+ 最重要的是支持使用[@Seed](https://javadoc.io/doc/org.instancio/instancio-junit/latest/org/instancio/junit/Seed.html)
  注解重现失败的测试

## 2. 重现失败的测试

由于使用Instancio会在每次测试运行时针对随机输入验证你的代码，因此必须能够使用先前生成的数据重现失败的测试。Instancio通过使用JUnit的publishReportEntry机制在失败消息中报告失败测试的种子值来支持此用例。

### 2.1 JUnit Jupiter测试中的种子生命周期

Instancio在每个测试方法之前初始化一个种子值，此种子值用于在测试方法执行期间创建所有对象，除非使用[withSeed(int seed)](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/InstancioApi.html#withSeed(int))
方法明确指定了另一个种子。

```java
/**
 * Seed Lifecycle in a JUnit Test
 */
@ExtendWith(InstancioExtension.class)
class ExampleTest {

    @Test
    void example() {  // ①
        Person person1 = Instancio.create(Person.class); // ②

        Person person2 = Instancio.of(Person.class)
            .withSeed(123) // ③
            .create();
        
        
        Person person3 = Instancio.create(Person.class); // ④
        
    } // ⑤
}
```

+ 1：Instancio初始化一个随机种子值，例如8276
+ 2：使用种子值8276
+ 3：使用提供的种子值123
+ 4：使用种子值8276
+ 5：种子值8276超出范围

> 注意：尽管person1和person3是使用相同的种子值8276创建的，但它们实际上是不同的对象，每个对象都包含不同的值，这是因为在整个测试方法中使用了相同的随机数生成器实例。

### 2.2 测试失败报告

当测试方法失败时，Instancio会将包含种子值的消息添加到失败测试的输出中。以以下失败测试为例：

```java
// Test failure example
@Test
void verifyShippingAddress() {
    Person person = Instancio.create(Person.class);

    // Some method under test
    Address address = shippingService.getShippingAddress(person);

    // A failing assertion
    assertThat(address).isEqualTo(person.getAddress());
}
```

失败的测试输出将包含以下消息：

```shell
Test method 'verifyShippingAddress' failed with seed: 8532
```

可以使用失败消息中报告的种子重现失败的测试，这可以通过在测试方法上放置[@Seed](https://javadoc.io/doc/org.instancio/instancio-junit/latest/org/instancio/junit/Seed.html)
注解来完成：

```java
// Reproducing a failed test
@Test
@Seed(8532) // ①
void verifyShippingAddress() {
    // snip ... same test code as above
}
```

+ 1：指定种子将重现以前生成的值

使用@Seed注解后，数据实际上变成了静态的，这样可以确定和修复根本原因。一旦测试通过，就可以删除@Seed注解，以便在每次后续测试运行时生成新数据。

## 3. 设置注入

InstancioExtension还添加了对将[Settings](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/settings/Settings.html)
注入测试类的支持，注入的Settings将被类中的每个测试方法使用，这可以使用[@WithSettings](https://javadoc.io/doc/org.instancio/instancio-junit/latest/org/instancio/junit/WithSettings.html)
注解来完成。

```java
/**
 * Injecting Settings into a test class
 */
@ExtendWith(InstancioExtension.class)
class ExampleTest {

    @WithSettings // ①
    private final Settings settings = Settings.create()
        .set(Keys.COLLECTION_MIN_SIZE, 10);

    @Test
    void example() {
        Person person = Instancio.create(Person.class); // ②

        assertThat(person.getPhoneNumbers())
            .hasSizeGreaterThanOrEqualTo(10);
    }
}
```

+ 1：注入自定义设置以供类中的每个测试方法使用。
+ 2：每个对象都将使用注入的设置创建。

> 每个测试类只能有一个@WithSettings注解字段。

Instancio还支持使用如下所示的withSettings方法覆盖注入的设置，通过该方法提供的设置优先于注入的设置(
有关更多信息，请参阅[设置优先级](https://www.instancio.org/user-guide/#settings-precedence)。

```java
/**
 * Overriding injecting Settings
 */
@ExtendWith(InstancioExtension.class)
class ExampleTest {

    @WithSettings
    private final Settings settings = Settings.create()
        .set(Keys.COLLECTION_MIN_SIZE, 10);

    @Test
    void overrideInjectedSettings() {
        Person person = Instancio.of(Person.class)
            .withSettings(Settings.create() // ①
                .set(Keys.COLLECTION_MAX_SIZE, 3))
            .create();

        assertThat(person.getPhoneNumbers())
            .as("Injected settings can be overridden")
            .hasSizeLessThanOrEqualTo(3);
    }
}
```

+ 1：传递给构建器方法的设置优先于注入的设置。

Instancio支持放置在静态和非静态字段上@WithSettings，但是，如果测试类包含@ParameterizedTest方法，则Settings字段必须是静态的。

## 4. 参数源

使用[@InstancioSource](https://javadoc.io/doc/org.instancio/instancio-junit/latest/org/instancio/junit/InstancioSource.html)
注解可以将参数直接提供给@ParameterzedTest测试方法，这适用于单个参数和多个参数，每个类代表一个参数。

>
使用@ParameterizedTest需要junit-jupiter-params依赖，有关详细信息，请参阅[JUnit文档](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests-setup)。

```java
/**
 * Using @InstancioSource with @ParameterizedTest
 */
@ExtendWith(InstancioExtension.class)
class ExampleTest {

    @ParameterizedTest
    @InstancioSource(Person.class)
    void singleArgument(Person person) {
        // snip...
    }

    @ParameterizedTest
    @InstancioSource({Foo.class, Bar.class, Baz.class})
    void multipleArguments(Foo foo, Bar bar, Baz baz) {
        // snip...
    }
}
```

应该注意的是，使用@InstancioSource有几个重要的限制，这使得它在许多情况下都不适用。

最大的限制是无法自定义生成的对象，唯一的选择是使用[设置注入](https://www.instancio.org/user-guide/#settings-injection)
自定义生成的值。但是，无法像使用构建器API那样在每个字段的基础上自定义值。

第二个限制是它不支持参数化类型。例如，不可能指定@InstancioSource(List.class)应该为List<String\>类型。