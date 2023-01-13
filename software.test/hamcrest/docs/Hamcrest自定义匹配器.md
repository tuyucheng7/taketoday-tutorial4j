## 1. 简介

除了内置的匹配器，**Hamcrest还支持创建自定义匹配器**。在本教程中，我们介绍如何创建自定义匹配器和使用它们。

## 2. Maven

只需要将以下Maven依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>java-hamcrest</artifactId>
    <version>2.0.0.0</version>
    <scope>test</scope>
</dependency>
```

## 3. TypeSafeMatcher介绍

**在开始之前，了解TypeSafeMatcher类是很重要的，我们必须扩展这个类来创建我们自己的匹配器**。

TypeSafeMatcher是一个抽象类，因此所有子类都必须实现以下方法：

-   matchesSafely(T t)：包含主要的匹配逻辑
-   describeTo(Description description)：当我们的匹配逻辑不满足时，自定义客户端将得到的消息

我们可以在第一个方法中看到，**TypeSafeMatcher是参数化的，所以我们在使用它时必须声明一个类型**，该类型将是我们被测试对象的类型。

## 4. 创建onlyDigits匹配器

对于我们的第一个用例，我们创建一个匹配器，**如果某个字符串仅包含数字，则该匹配器返回true**。

因此，应用于“123”的onlyDigits应该返回true，而“hello1”和“bye”应该返回false。

### 4.1 匹配器创建

首先我们创建一个扩展TypeSafeMatcher的类：

```java
public class IsOnlyDigits extends TypeSafeMatcher<String> {
   
    @Override
    protected boolean matchesSafely(String s) {
        // ...
    }

    @Override
    public void describeTo(Description description) {
        // ...
    }
}
```

请注意，由于我们要测试的对象是String文本值，因此我们使用String参数化TypeSafeMatcher的子类。

下面添加我们的实现逻辑：

```java
public class IsOnlyDigits extends TypeSafeMatcher<String> {

	@Override
	protected boolean matchesSafely(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("only digits");
	}
}
```

如我们所见，matchesSafey方法尝试将输入字符串参数解析为Integer，如果成功，则返回true，否则返回false。另一方面，describeTo添加了一个代表我们期望的文本，我们会在接下来使用匹配器时看到这一点。

**我们还需要最后一步：即声明一个访问匹配器的静态方法**，这样的话它的行为与内置匹配器的其余部分一样。

因此，我们添加以下方法：

```java
public static Matcher<String> onlyDigits() {
    return new IsOnlyDigits();
}
```

至此，我们的自定义匹配器可以正式使用了。

### 4.2 匹配器使用

```java
@Test
void givenAString_whenIsOnlyDigits_thenCorrect() {
    String digits = "1234";

    assertThat(digits, onlyDigits());
}
```

上面的测试将通过，因为输入字符串仅包含数字。请记住，**为了使其更易读，我们可以使用匹配器is作为任何其他匹配器的包装器**：

```java
assertThat(digits, is(onlyDigits()));
```

最后，如果我们使用“123ABC”运行相同的测试，输出消息将是：

```powershell
java.lang.AssertionError: 
Expected: only digits
     but: was "123ABC"
```

**这就是自定义匹配器中describeTo方法的作用，正如我们所说，对测试中的预期内容进行适当的描述是非常重要的**。

## 5. divisibleBy

如果我们想创建一个匹配器来定义一个数字是否可以被另一个数字整除呢？对于这种情况，**我们必须将其中一个参数保存在某个地方**。

让我们看看如何做到这一点：

```java
public class IsDivisibleBy extends TypeSafeMatcher<Integer> {

    private Integer divider;

    // constructors

    @Override
    protected boolean matchesSafely(Integer dividend) {
        if (divider == 0) {
            return false;
        }
        return ((dividend % divider) == 0);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("divisible by " + divider);
    }

    public static Matcher<Integer> divisibleBy(Integer divider) {
        return new IsDivisibleBy(divider);
    }
}
```

很简单，**我们只是在类中添加了一个新属性，并在构造过程中对其进行赋值**。然后，我们将它作为参数传递给静态方法：

```java
@Test
void givenAnEvenInteger_whenDivisibleByTwo_thenCorrect() {
    Integer ten = 10;
    Integer two = 2;

    assertThat(ten,is(divisibleBy(two)));
}

@Test
void givenAnOddInteger_whenNotDivisibleByTwo_thenCorrect() {
    Integer eleven = 11;
    Integer two = 2;

    assertThat(eleven,is(not(divisibleBy(two))));
}
```

就这样，我们实现了一个使用多个输入的匹配器。

## 6. 总结

Hamcrest提供的匹配器涵盖了开发人员在编写断言时通常要处理的大多数用例。更重要的是，如果没有涵盖任何特定情况，Hamcrest还支持创建自定义匹配器以便在特定场景下使用，它们的创建方式非常简单，并且**它们的使用方式与库中自带的内置匹配器完全相同**。
