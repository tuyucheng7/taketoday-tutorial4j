## 1. 概述

在这篇简短的文章中，我们重点关注如何使用自定义测试Runner运行JUnit测试。**要指定自定义Runner，我们需要使用@RunWith注解**。

## 2. Maven依赖

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>  
</dependency>
```

## 3. 实现自定义Runner

在下面的示例中，我们演示如何编写我们自己的自定义Runner，并使用@RunWith运行它。

**JUnit Runner是一个扩展JUnit抽象Runner类的类，它负责运行JUnit测试，通常使用反射**。

下面，我们定义一个这样的类：

```java
public class TestRunner extends Runner {

	private Class testClass;

	public TestRunner(Class testClass) {
		super();
		this.testClass = testClass;
	}

	@Override
	public Description getDescription() {
		return Description.createTestDescription(testClass, "My runner description");
	}

	@Override
	public void run(RunNotifier notifier) {
		System.out.println("running the tests from MyRunner: " + testClass);
		try {
			Object testObject = testClass.newInstance();
			for (Method method : testClass.getMethods()) {
				if (method.isAnnotationPresent(Test.class)) {
					notifier.fireTestStarted(Description.createTestDescription(testClass, method.getName()));
					method.invoke(testObject);
					notifier.fireTestFinished(Description.createTestDescription(testClass, method.getName()));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
```

getDescription方法继承自Describable，返回一个Description，其中包含稍后导出并可能由各种工具使用的信息。在run方法实现中，我们使用反射调用目标测试方法。

我们定义了一个接收Class参数的构造函数；这是JUnit的要求。在运行时，JUnit会将目标测试类传递给此构造函数。RunNotifier用于触发包含有关测试进度信息的事件。

下面我们在测试类中使用该Runner：

```java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
}
```

```java
@RunWith(TestRunner.class)
public class CalculatorTest {
    Calculator calculator = new Calculator();

    @Test
    public void testAddition() {
        Syste.out.println("in testAddition");
        assertEquals("addition", 8, calculator.add(5, 3));
    }
}
```

这是测试的结果：

```shell
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running cn.tuyucheng.taketoday.junit.CalculatorTest
running the tests from MyRunner: class cn.tuyucheng.taketoday.junit.CalculatorTest
in testAddition
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.002 sec

Results :

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
```

## 4. 更特殊的Runner

**我们也可以扩展Runner的一个特殊子类：ParentRunner或BlockJUnit4Runner，而不是像我们在上一个示例中那样扩展低级Runner类**。

抽象的ParentRunner类以分层方式运行测试。BlockJUnit4Runner是一个具体的类，如果我们更喜欢自定义某些方法，我们可能会扩展这个类。

让我们看一个例子：

```java
public class BlockingTestRunner extends BlockJUnit4ClassRunner {
	public BlockingTestRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected Statement methodInvoker(FrameworkMethod method, Object test) {
		System.out.println("invoking: " + method.getName());
		return super.methodInvoker(method, test);
	}
}
```

使用@RunWith(JUnit4.class)标注类将始终调用当前版本的JUnit中的默认JUnit 4 Runner；此类为当前默认的JUnit 4类Runner设置别名：

```java
@RunWith(JUnit4.class)
public class CalculatorTest {
    Calculator calculator = new Calculator();

    @Test
    public void testAddition() {
        assertEquals("addition", 8, calculator.add(5, 3));
    }
}
```

## 5. 总结

JUnit Runners具有很强的适应性，可以让开发人员改变测试执行过程和整个测试过程。如果我们只想进行微小的更改，那么使用BlockJUnit4Class Runner是个好主意。

一些流行的第三方Runner实现包括SpringJUnit4ClassRunner、MockitoJUnitRunner、HierarchicalContextRunner、CucumberRunner等等。