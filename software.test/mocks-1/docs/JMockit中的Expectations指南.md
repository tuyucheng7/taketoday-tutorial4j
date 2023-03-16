## 1. 介绍

本文是JMockit系列的第二部分。你可能需要阅读[第一篇文章](https://www.baeldung.com/jmockit-101)，因为我们假设你已经熟悉JMockit的基础知识。

我们将更深入地关注期望。我们将展示如何定义更具体或通用的参数匹配以及更高级的定义值的方法。

## 2. 参数值匹配

以下方法适用于Expectations和Verifications。

### 2.1 “Any”字段

JMockit提供了一组实用字段，用于使参数匹配更通用。其中一个实用程序是anyX字段。

这些将检查是否传递了任何值，并且每个原始类型(和相应的包装类)都有一个值，一个用于字符串，一个“通用”类型的Object。

让我们看一个例子：

```java
public interface ExpectationsCollaborator {
    String methodForAny1(String s, int i, Boolean b);
    void methodForAny2(Long l, List<String> lst);
}

@Test
public void test(@Mocked ExpectationsCollaborator mock) throws Exception {
    new Expectations() {{
        mock.methodForAny1(anyString, anyInt, anyBoolean); 
        result = "any";
    }};

    Assert.assertEquals("any", mock.methodForAny1("barfooxyz", 0, Boolean.FALSE));
    mock.methodForAny2(2L, new ArrayList<>());

    new FullVerifications() {{
        mock.methodForAny2(anyLong, (List<String>) any);
    }};
}
```

你必须考虑到，在使用any字段时，你需要将其强制转换为预期的类型。[文档](https://jmockit.github.io/tutorial/Mocking.html#expectation)中提供了完整的字段列表。

### 2.2 “With”方法

JMockit还提供了几种方法来帮助进行泛型参数匹配。这些是withX方法。

这些允许比anyX字段更高级的匹配。我们可以在这里看到一个示例，其中我们将为一个方法定义一个期望，该方法将由一个包含foo的字符串、一个不等于1的整数、一个非空布尔值和List类的任何实例触发：

```java
public interface ExpectationsCollaborator {
    String methodForWith1(String s, int i);
    void methodForWith2(Boolean b, List<String> l);
}

@Test
public void testForWith(@Mocked ExpectationsCollaborator mock) throws Exception {
    new Expectations() {{
        mock.methodForWith1(withSubstring("foo"), withNotEqual(1));
        result = "with";
    }};

    assertEquals("with", mock.methodForWith1("barfooxyz", 2));
    mock.methodForWith2(Boolean.TRUE, new ArrayList<>());

    new Verifications() {{
        mock.methodForWith2(withNotNull(), withInstanceOf(List.class));
    }};
}
```

你可以在JMockit的[文档](https://jmockit.github.io/tutorial/Mocking.html#expectation)中查看withX方法的完整列表。

考虑到特殊的with(Delegate)和withArgThat(Matcher)将在它们自己的小节中介绍。

### 2.3 Null与Not Null

要理解的一点是null不用于定义已将null传递给mock的参数。

实际上，null用作**语法糖**来定义将传递任何对象(因此它只能用于引用类型的参数)。要专门验证给定参数是否接收到null引用，可以使用withNull()匹配器。

对于下一个示例，我们将定义mock的行为，当传递的参数是：任何字符串、任何列表和null引用时应该触发该行为：

```java
public interface ExpectationsCollaborator {
    String methodForNulls1(String s, List<String> l);
    void methodForNulls2(String s, List<String> l);
}

@Test
public void testWithNulls(@Mocked ExpectationsCollaborator mock){
    new Expectations() {{
        mock.methodForNulls1(anyString, null); 
        result = "null";
    }};
    
    assertEquals("null", mock.methodForNulls1("blablabla", new ArrayList<String>()));
    mock.methodForNulls2("blablabla", null);
    
    new Verifications() {{
        mock.methodForNulls2(anyString, (List<String>) withNull());
    }};
}
```

注意区别：null表示任何列表，withNull()表示对列表的null引用。特别是，这避免了将值强制转换为声明的参数类型的需要(请参阅必须强制转换第三个参数，而不是第二个参数)。

能够使用它的唯一条件是至少一个显式参数匹配器已用于期望(with方法或any字段)。

### 2.4 “Times”字段

有时，我们希望限制mock方法的**预期调用次数**。为此，JMockit有保留字times、minTimes和maxTimes(这三个都只允许非负整数)。

```java
public interface ExpectationsCollaborator {
    void methodForTimes1();
    void methodForTimes2();
    void methodForTimes3();
}

@Test
public void testWithTimes(@Mocked ExpectationsCollaborator mock) {
    new Expectations() {{
        mock.methodForTimes1(); times = 2;
        mock.methodForTimes2();
    }};
    
    mock.methodForTimes1();
    mock.methodForTimes1();
    mock.methodForTimes2();
    mock.methodForTimes3();
    mock.methodForTimes3();
    mock.methodForTimes3();
    
    new Verifications() {{
        mock.methodForTimes3(); minTimes = 1; maxTimes = 3;
    }};
}
```

在此示例中，我们定义了methodForTimes1()的两个调用(不是一个，不是三个，正好两个)应该使用times = 2来完成。

然后我们使用默认行为(如果没有给出重复约束，被使用minTimes = 1)来定义至少对methodForTimes2()进行一次调用。

最后，使用minTimes = 1，其次是maxTimes = 3；我们定义了methodForTimes3()会发生一到三个调用。

考虑到可以为相同的期望指定minTimes和maxTimes，只要首先分配minTimes。另一方面，times只能单独使用。

### 2.5 自定义参数匹配

有时参数匹配并不像简单地指定一个值或使用一些预定义的实用程序(anyX或withX)那样直接。

对于这种情况，JMockit依赖于[Hamcrest](http://hamcrest.org/)的Matcher接口。你只需要为特定的测试场景定义一个匹配器并将该匹配器与withArgThat()调用一起使用。

让我们看一个将特定类与传递的对象匹配的示例：

```java
public interface ExpectationsCollaborator {
    void methodForArgThat(Object o);
}

public class Model {
    public String getInfo(){
        return "info";
    }
}

@Test
public void testCustomArgumentMatching(@Mocked ExpectationsCollaborator mock) {
    new Expectations() {{
        mock.methodForArgThat(withArgThat(new BaseMatcher<Object>() {
            @Override
            public boolean matches(Object item) {
                return item instanceof Model && "info".equals(((Model) item).getInfo());
            }

            @Override
            public void describeTo(Description description) { }
        }));
    }};
    mock.methodForArgThat(new Model());
}
```

## 3. 返回值

现在让我们看看返回值；请记住，以下方法仅适用于Expectations，因为不能为Verifications定义返回值。

### 3.1 Result和Returns(...)

使用JMockit时，你可以通过三种不同的方式来定义调用mock方法的预期结果。在这三个中，我们现在将讨论前两个(最简单的)，它们肯定会涵盖90%的日常用例。

这两个是result字段和returns(Object...)方法：

-   使用result字段，你可以为任何返回非void的mock方法定义一个返回值。此返回值也可以是要抛出的异常(这次适用于非void和void返回方法)。

    -   可以执行多个result字段分配，以便为多个方法调用**返回多个值**(你可以混合返回值和要抛出的错误)。
    -   当分配给result列表或值数组(与mock方法的返回类型相同的类型，此处没有异常)时，将实现相同的行为。

-   returns(Object...)方法是用于同时返回多个值的语法糖。

使用代码片段更容易显示：

```java
public interface ExpectationsCollaborator{
    String methodReturnsString();
    int methodReturnsInt();
}

@Test
public void testResultAndReturns(@Mocked ExpectationsCollaborator mock) {
    new Expectations() {{
        mock.methodReturnsString();
        result = "foo";
        result = new Exception();
        result = "bar";
        returns("foo", "bar");
        mock.methodReturnsInt();
        result = new int[]{1, 2, 3};
        result = 1;
    }};

    assertEquals("Should return foo", "foo", mock.methodReturnsString());
    try {
        mock.methodReturnsString();
        fail("Shouldn't reach here");
    } catch (Exception e) {
        // NOOP
    }
    assertEquals("Should return bar", "bar", mock.methodReturnsString());
    assertEquals("Should return 1", 1, mock.methodReturnsInt());
    assertEquals("Should return 2", 2, mock.methodReturnsInt());
    assertEquals("Should return 3", 3, mock.methodReturnsInt());
    assertEquals("Should return foo", "foo", mock.methodReturnsString());
    assertEquals("Should return bar", "bar", mock.methodReturnsString());
    assertEquals("Should return 1", 1, mock.methodReturnsInt());
}
```

在这个例子中，我们定义了前三个对methodReturnsString()的调用，预期的返回是(按顺序)“foo”、一个异常和“bar”。我们使用对result字段的三种不同分配来实现这一点。

然后在第**14行**，我们定义了对于第四次和第五次调用，“foo”和“bar”应该使用returns(Object...)方法返回。

对于methodReturnsInt()，我们在第**13行**定义通过将具有不同结果的数组分配给result字段来返回1、2和最后的3，在第**15行**我们定义通过对result字段的简单分配来返回1。

如你所见，有多种方法可以为mock方法定义返回值。

### 3.2 Delegate

最后，我们将介绍定义返回值的第三种方式：Delegate接口。此接口用于在定义mock方法时定义更复杂的返回值。

我们看一个简单的解释示例：

```java
public interface ExpectationsCollaborator {
    int methodForDelegate(int i);
}

@Test
public void testDelegate(@Mocked ExpectationsCollaborator mock) {
    new Expectations() {{
        mock.methodForDelegate(anyInt);
            
        result = new Delegate() {
            int delegate(int i) throws Exception {
                if (i < 3) {
                    return 5;
                } else {
                    throw new Exception();
                }
            }
        };
    }};

    assertEquals("Should return 5", 5, mock.methodForDelegate(1));
    try {
        mock.methodForDelegate(3);
        fail("Shouldn't reach here");
    } catch (Exception e) {
    }
}
```

使用委托者的方法是为其创建一个新实例并将其分配给returns字段。在这个新实例中，你应该创建一个具有与mock方法相同的参数和返回类型的新方法(你可以使用任何名称)。在这个新方法中，使用你想要的任何实现来返回所需的值。

在示例中，我们做了一个实现，其中当传递给mock方法的值小于3时应返回5，否则抛出异常(注意我们必须使用times = 2，以便第二次调用是预期的，因为我们通过定义返回值丢失了默认行为)。

它可能看起来有很多代码，但在某些情况下，这将是实现我们想要的结果的唯一方法。

## 4. 总结

有了这个，我们实际上展示了为我们的日常测试创建期望和验证所需的一切。

我们会在JMockit上发布更多文章，敬请期待了解更多信息。

### 4.1 系列文章

该系列的所有文章：

-[JMockit 101](https://www.baeldung.com/jmockit-101)
-[JMockit期望指南](https://www.baeldung.com/jmockit-expectations)
-[JMockit高级用法](https://www.baeldung.com/jmockit-advanced-usage)