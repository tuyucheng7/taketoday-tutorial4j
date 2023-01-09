## 1. 概述

在这个简短的教程中，我们重点介绍如何在Mockito中mock final类和方法。

与其他Mockito框架相关的文章一样，我们将使用下面显示的MyList类作为测试用例中的mock对象。

```java
public class MyList extends AbstractList {

    final public int finalMethod() {
        return 0;
    }
}
```

并且在本文中，我们添加一个继承MyList的final类：

```java
public final class FinalList extends MyList {

    @Override
    public int size() {
        return 1;
    }
}
```

## 2. 为final的方法和类配置Mockito

在可以使用Mockito mock最终类和方法之前，我们需要对其进行配置以激活内联MockMaker。

我们需要在项目的src/test/resources/mockito-extensions目录中添加一个名为org.mockito.plugins.MockMaker的文本文件，并添加以下内容：

```text
mock-maker-inline
```

Mockito在加载时检查mockito-extensions目录中的配置文件，该文件启用对mock最终类和方法的支持。

## 3. mock final方法

**正确配置Mockito后，我们就可以像其他任何方法一样mock final方法：**

```java
class MockFinalsUnitTest {

    @Test
    void whenMockFinalMethodMockWorks() {
        MyList myList = new MyList();

        MyList mock = mock(MyList.class);
        when(mock.finalMethod()).thenReturn(1);

        assertThat(mock.finalMethod()).isNotEqualTo(myList.finalMethod());
    }
}
```

通过创建MyList的具体实例和mock实例，我们可以比较使用这两个实例分别调用finalMethod()返回的值，以此验证我们的mock行为是正确的。

## 4. mock final类

**mock一个final类跟mock任何其他类一样简单**：

```java
@Test
void whenMockFinalClassMockWorks() {
    FinalList finalList = new FinalList();
    
    FinalList mock = mock(FinalList.class);
    when(mock.size()).thenReturn(2);
    
    assertThat(mock.size()).isNotEqualTo(finalList.size());
}
```

与上面的测试类似，我们创建了final类的一个具体实例和一个mock实例，mock了一个方法，并验证了mock实例的行为是否不同。

## 5. 总结

在本文中，我们介绍了如何使用Mockito的扩展来mock最终类和方法。