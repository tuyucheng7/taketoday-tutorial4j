## 1. 概述

在本文中，我们介绍[Spock](https://spockframework.org/)，它是一个[Groovy](http://groovy-lang.org/)测试框架。主要是，Spock旨在通过利用Groovy功能成为传统JUnit堆栈的更强大的替代品。

Groovy是一种基于JVM的语言，与Java无缝集成。除了互操作性之外，它还提供了额外的语言概念，例如动态、具有可选类型和元编程。

通过使用Groovy，Spock引入了新的和富有表现力的方法来测试我们的Java应用程序，这在普通的Java代码中是不可能的。我们将在本文中探讨Spock的一些高级概念，并提供一些实际的逐步示例。

## 2. Maven依赖

在开始之前，让我们添加Maven依赖项：

```xml
<dependency>
    <groupId>org.spockframework</groupId>
    <artifactId>spock-core</artifactId>
    <version>1.3-groovy-2.4</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy-all</artifactId>
    <version>2.4.7</version>
    <scope>test</scope>
</dependency>
```

我们添加了Spock和Groovy依赖，但是，由于Groovy是一种新的JVM语言，我们需要包含gmavenplus插件才能编译和运行它：

```xml
<plugin>
    <groupId>org.codehaus.gmavenplus</groupId>
    <artifactId>gmavenplus-plugin</artifactId>
    <version>1.5</version>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
                <goal>testCompile</goal>
            </goals>
        </execution>
     </executions>
</plugin>
```

请注意，我们仅将Groovy和Spock用于测试目的，这就是为什么这些依赖项是测试范围的。

## 3. Spock测试的结构

### 3.1 Specifications和Features

当我们在Groovy中编写测试时，我们需要将它们添加到src/test/Groovy目录，而不是src/test/java。让我们在此目录中创建第一个测试，将其命名为FirstSpecification.groovy：

```groovy
class FirstSpecification extends Specification {

}
```

请注意，我们继承了Specification类，每个Spock类都必须继承它以使框架对其可用。这样做使我们能够实现第一个feature：

```groovy
def "one plus one should equal two"() {
	expect:
	1 + 1 == 2
}
```

在对代码的代码进行解释之前，还值得注意的是，**在Spock中，我们所说的feature在某种程度上与我们在JUnit中所说的test方法同义**。**所以每当我们提到一个feature时，我们实际上是在指一个test**。

现在，让我们分析一下我们的feature。在编写第一个feature的过程中，我们应该能够立即看到它与Java之间的一些差异。

第一个区别是feature方法名写成普通字符串，在JUnit中，我们通常会使用驼峰或下划线来分隔单词的方法名称，这样就不会具有表达力或人类可读性。

接下来，我们的测试代码存在于一个expect块中，稍后我们将更详细地介绍块，但本质上它们是划分测试的不同步骤的一种逻辑方法。

最后，我们没有编写断言语句，这是因为断言是隐式的，当我们的语句等于true时通过，当它等于false时失败。同样，我们稍后会更详细地介绍断言。

### 3.2 块

有时在编写JUnit测试时，我们可能会注意到没有一种表达方式可以将其分解为多个部分。例如，如果我们遵循行为驱动开发，我们最终可能会使用注释来表示given、when、then部分：

```java
@Test
void givenTwoAndTwo_whenAdding_thenResultIsFour() {
   // Given
   int first = 2;
   int second = 4;

   // When
   int result = 2 + 2;

   // Then
   assertTrue(result == 4)
}
```

Spock用块解决了这个问题，**块是Spock使用标签分解测试阶段的原生方式**。这些可用的标签是given、when、then和more：

1. Setup(由Given起别名)：在这里，我们在运行测试之前执行所需的任何设置。这是一个隐式块，不在任何块中的代码属于它的一部分
2. When：这是我们对正在测试的内容实际执行的地方，换句话说，我们调用被测方法的地方
3. Then：这就是断言所属的块，在Spock中，这些被评估为普通的布尔断言，稍后将介绍
4. Expect：这是在同一块内执行我们的测试和断言的一种方式，根据我们发现更具表现力的内容，我们可能会或可能不会选择使用此块
5. Cleanup：在这里，我们拆除任何测试依赖性资源，例如，我们可能希望从文件系统中删除任何文件或删除写入数据库的测试数据

我们再次编写一个类似的测试，这次充分利用块的功能：

```groovy
def "two plus two should equal four"() {
    given:
    int left = 2
    int right = 2

    when:
    int result = left + right

    then:
    result == 4
}
```

正如我们所看到的，块有助于我们的测试变得更具可读性。

### 3.3 利用Groovy特性进行断言

**在then和expect块中，断言是隐式的**。大多数情况下，每条语句都会被评估，如果它不正确则失败。当将其与各种Groovy特性结合起来时，它可以很好地消除对断言库的需求，下面是一个集合断言的例子：

```groovy
def "Should be able to remove from list"() {
	given:
	def list = [1, 2, 3, 4]
        
	when:
	list.remove(0)
        
	then:
	list == [2, 3, 4]
}
```

虽然我们在本文中只是简要介绍了Groovy，但有必要解释一下这里发生的事情。

首先，Groovy为我们提供了更简单的创建集合的方法，我们可以用方括号声明元素，然后在内部将实例化一个集合。其次，由于Groovy是动态的，我们可以使用def，这意味着我们没有为变量声明类型。

最后，在简化我们的测试的背景下，展示的最有用的特性是运算符重载。这意味着在内部，将调用equals()方法来比较两个集合，而不是像Java中那样进行引用比较。

还值得演示当我们的测试失败时会发生什么，当我们故意使测试失败，然后观察控制台的输出：

```shell
Condition not satisfied:

list == [1, 3, 4]
|    |
|    false
[2, 3, 4]

org.spockframework.runtime.SpockComparisonFailure at FirstSpecification.groovy:31
```

虽然所做的只是在两个集合上调用equals()，但Spock足够聪明，可以对失败的断言进行分解，为我们提供了最有用的调试信息。

### 3.4. 断言异常

Spock还为我们提供了一种检查异常的表达方式，在JUnit中，我们的方法可能是使用try-catch块，在我们的测试方法上声明expected，或者使用第三方库。而Spock的原生断言提供了一种开箱即用的异常处理方式：

```groovy
def "Should get an index out of bounds when removing a non-existent element"() {
	given:
	def list = [1, 2, 3, 4]
        
	when:
	list.remove(4)
    
	then:
	thrown(IndexOutOfBoundsException)
}
```

在这里，我们不必引入额外的库。另一个优点是thrown()方法将断言异常的类型，但不会停止测试的执行。

## 4. 数据驱动测试

### 4.1 什么是数据驱动测试？

**本质上，数据驱动测试是指我们使用不同的参数和断言多次测试相同的行为**，一个典型的例子是测试数学运算，比如对一个数字求平方。根据操作数的各种排列，结果会有所不同。在Java中，我们可能更熟悉的术语是参数化测试。

### 4.2 在Java中实现参数化测试

在某些情况下，使用JUnit实现参数化测试是很好的：

```java
@RunWith(Parameterized.class)
public class FibonacciTest {
    
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { 1, 1 }, { 2, 4 }, { 3, 9 }  
        });
    }

    private int input;

    private int expected;

    public FibonacciTest (int input, int expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void test() {
        assertEquals(fExpected, Math.pow(3, 2));
    }
}
```

正如我们所看到的那样，有很多冗长的代码，而且代码的可读性也不是很好。我们必须创建一个位于测试方法之外的二维对象数组，甚至还要创建一个用于注入各种测试值的包装对象。

### 4.3 在Spock中使用数据表

与JUnit相比，Spock的一个重要功能是它如何干净地实现参数化测试，同样，在Spock中，这被称为数据驱动测试。现在，让我们再次实现相同的测试，这次我们使用Spock和**数据表**，它提供了一种更方便的方式来执行参数化测试：

```groovy
def "numbers to the power of two"(int a, int b, int c) {
	expect:
	Math.pow(a, b) == c
        
	where:
	a | b | c
	1 | 2 | 1
	2 | 2 | 4
	3 | 2 | 9
}
```

正如我们所看到的，我们只是写了一个简单且富有表现力的数据表，其中包含我们所有的参数。

此外，它属于它应该存在的位置，也就是与测试的主体在一起，并且没有样板代码。该测试是富有表现力的，具有人类可读的名称，以及纯粹的expect和where块来分解逻辑部分。

### 4.4 当数据表失败时

当我们的测试失败时，Spock所体现出来的强大表现得更为突出：

```shell
Condition not satisfied:

Math.pow(a, b) == c
     |   |  |  |  |
     4.0 2  2  |  1
               false

Expected :1

Actual   :4.0
```

同样，Spock给了我们一个非常有用的错误信息，我们可以准确地看到数据表的哪一行导致失败以及原因是什么。

## 5. Mock

### 5.1 什么是Mock？

Mock是一种改变我们的被测服务与之协作的类的行为的方法，这是一种能够隔离其依赖项来测试业务逻辑的有用方法。

一个典型的例子是用简单的假装的类来替换一个进行网络调用的类。要更深入的了解Mock，可以参考[这篇文章]()。

### 5.2 使用Spock进行Mock

Spock有自己的Mock框架，利用了Groovy为JVM带来的有用概念，首先，让我们实例化一个Mock：

```groovy
PaymentGateway paymentGateway = Mock()
```

在这种情况下，我们的Mock类型由变量类型推断，由于Groovy是一种动态语言，我们还可以提供类型参数，使我们不必将Mock分配给任何特定类型：

```groovy
def paymentGateway = Mock(PaymentGateway)
```

现在，每当我们调用PaymentGateway mock上的方法时，都会给出默认响应，而不会调用真实实例：

```groovy
when:
def result = paymentGateway.makePayment(12.99)
    
then:
!result
```

对此的说法是lenient mocking(宽松的mocking)，这意味着尚未定义的Mock方法将返回合理的默认值，而不是抛出异常。这是在 Spock中设计的，目的是为了让mocks变得不那么脆弱。

### 5.3 Mocks上的Stubbing方法调用

我们还可以配置在Mock上调用的方法，以某种方式响应不同的参数。下面的例子配置mock当以20调用makePayment方法时返回true：

```java
def "Should return true value for mock"() {
	given:
	def paymentGateway = Mock(PaymentGateway)
	paymentGateway.makePayment(20.0) >> true
        
	when:
	def result = paymentGateway.makePayment(20.0)
        
	then:
	result
}
```

这里有趣的是，Spock利用Groovy的运算符重载来Stubbing方法调用。对于Java，我们必须调用真正的方法，这可以说意味着生成的代码更加冗长，并且可能表达能力较差。

如果我们不再关心我们的方法参数并且总是想返回true，我们可以只使用下划线：

```groovy
paymentGateway.makePayment(_) >> true
```

如果我们想在不同的响应之间交替，我们可以提供一个集合，每个元素将按顺序返回：

```groovy
paymentGateway.makePayment(_) >>> [true, true, false, true]
```

### 5.4 Verification

我们可能想要对mock做的另一件事是断言使用预期参数调用了各种方法，换句话说，我们应该验证我们与Mock的交互。

一个典型的验证用例是，如果我们的mock上的方法具有void返回类型，在这种情况下，由于没有结果可供我们操作，因此我们无法通过被测方法进行测试的推断行为。一般来说，如果返回了某个值，那么被测试的方法就可以对其进行操作，并且该操作的结果就是我们所断言的。

下面验证一个返回类型为void的方法是否被调用：

```groovy
def "Should verify notify was called"() {
	given:
	def notifier = Mock(Notifier)
        
	when:
	notifier.notify('foo')
    
	then:
	1 * notifier.notify('foo')
}
```

Spock再次利用Groovy的运算符重载机制，通过将我们的mocks方法调用乘以1，我们表示我们期望它被调用了多少次。

如果我们的方法根本没有被调用，或者没有像我们指定的那样被调用多次，那么我们的测试将无法给我们提供信息丰富的Spock错误消息。让我们通过期望它被调用两次来证明这一点：

```groovy
2  notifier.notify('foo')
```

接下来，我们看看给出的错误提示，它包含很多有用的信息：

```groovy
Too few invocations for:

2  notifier.notify('foo')   (1 invocation)
```

与Stubbing一样，我们也可以执行更宽松的验证匹配。如果我们不关心方法参数是什么，我们可以使用下划线：

```groovy
2  notifier.notify(_)
```

或者，如果我们想确保它没有被特定参数调用，我们可以使用not运算符：

```groovy
2  notifier.notify(!'foo')
```

## 6. 总结

在本文中，我们简要介绍了使用Spock编写测试，Groovy使我们的测试比典型的JUnit堆栈更具表现力，并介绍了Specification和Feature的概念，演示了Spock中数据驱动测试的使用，以及如何通过原生Spock功能进行Mock和断言。
