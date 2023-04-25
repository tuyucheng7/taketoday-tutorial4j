## 1. 简介

在本快速教程中，**我们将介绍在使用流行的测试框架[Mockito](https://site.mockito.org/)时可能会看到的警告之一**。

即，指的是已弃用的MockitoJUnitRunner类。我们将了解为什么会出现此警告以及如何处理它。

最后，让我们提醒一下，我们可以使用MockitoJUnitRunner来指示Mockito初始化我们用@Mock或@Spy标注的测试替身，以及其他Mockito注解。

要了解有关使用Mockito进行测试的更多信息，请在此处查看我们的[Mockito系列](https://www.baeldung.com/tag/mockito/)。

## 2. 为什么显示这个警告

如果我们使用2.2.20(2016年11月)之前的Mockito版本，则会出现此弃用警告。

让我们简单回顾一下它背后的历史。在早期版本的Mockito中，如果我们想使用Mockito JUnit Runner，我们需要导入的包是：

```java
import org.mockito.runners.MockitoJUnitRunner;
```

从2.2.20版本开始，与JUnit相关的类已重新组合到特定的JUnit包中。我们可以在这里找到包：

```java
import org.mockito.junit.MockitoJUnitRunner;
```

因此，原来的org.mockito.runners.MockitoJUnitRunner现在已被弃用。该类的逻辑现在属于org.mockito.junit.runners.MockitoJUnitRunner。

虽然删除警告不是强制性的，但建议这样做。**Mockito版本3将删除此类**。

## 3. 解决方案

在本节中，我们将解释三种不同的解决方案来解决此弃用警告：

-   更新以使用正确的导入
-   使用MockitoAnnotations初始化字段
-   使用MockitoRule

### 3.1 更新导入

让我们从最简单的解决方案开始，即简单地**更改包导入语句**：

```java
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExampleTest {
    // ...
}
```

就这样！更改应该相当容易。

### 3.2 使用MockitoAnnotations初始化字段

在下一个示例中，**我们将使用MockitoAnnotations类以不同的方式初始化mock**：

```java
import org.junit.Before;
import org.mockito.MockitoAnnotations;

public class ExampleTest {
    
    @Before 
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    // ...
}
```

首先，我们删除对MockitoJUnitRunner的引用。相反，我们调用MockitoAnnotations类的静态initMocks()方法。

我们在测试类的JUnit @Before方法中执行此操作。这会在执行每个测试之前使用Mockito注解初始化任何字段。

### 3.3 使用MockitoRule

但是，正如我们已经提到的，MockitoJUnitRunner绝不是强制性的。在最后一个示例中，**我们将研究使用MockitoRule使@Mock工作的另一种方法**：

```java
import org.junit.Rule;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class ExampleTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    // ...
}
```

最后，在这个例子中，JUnit Rule初始化任何用@Mock标注的mock。

因此，这意味着MockitoAnnotations#initMocks(Object)或@RunWith(MockitoJUnitRunner.class)的显式使用不是必需的。

## 4. 总结

总而言之，在这篇简短的文章中，我们看到了几个关于如何修复MockitoJUnitRunner类弃用警告的选项。