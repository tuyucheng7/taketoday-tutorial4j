## 1. 概述

BDD术语最早是由Dan North在2006年提出的。

BDD鼓励使用自然的、人类可读的语言编写测试，重点关注应用程序的行为。

它定义了一种结构清晰的测试编写方式，遵循三个部分(Arrange、Act、Assert)：

+ 给定一些先决条件(Arrange)
+ 当某个动作发生时(Act)
+ 然后验证输出(Assert)

**Mockito附带一个BDDMockito类，该类引入了BDD友好的API**。
允许我们采用更加友好的BDD方法，使用given()安排测试，并使用then()进行断言。

在本文中，我们介绍如何编写基于BDD的Mockito测试。

## 2. 项目构建

### 2.1 maven依赖

Mockito的BDD风格是mockito-core库的一部分，因此我们添加如下依赖：

```xml

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>4.0.0</version>
    <scope>test</scope>
</dependency>
```

### 2.2 import

如果我们包含以下静态导入，我们的测试可以变得更具可读性：

```text
import static org.mockito.BDDMockito.*;
```

请注意，BDDMockito继承了Mockito，因此我们可以使用传统Mockito API提供的任何方法。

## 3. Mockito与BDDMockito

Mockito中的传统mock是在Arrange步骤中使用when(obj).then*()执行的。

然后，可以在Assert步骤中使用verify()来验证与mock的交互。

BDDMockito为各种Mockito方法提供BDD别名，因此我们可以使用given(而不是when)编写Arrange步骤，
同样，我们可以使用then(而不是verify)编写Assert步骤。

让我们看一个使用传统Mockito的测试案例：

```text
when(phoneBookRepository.contains(momContactName)).thenReturn(false);
 
phoneBookService.register(momContactName, momPhoneNumber);
 
verify(phoneBookRepository).insert(momContactName, momPhoneNumber);
```

让我们看看这与BDDMockito的比较:

```text
given(phoneBookRepository.contains(momContactName))
    .willReturn(false);
 
phoneBookService.register(momContactName, momPhoneNumber);
 
then(phoneBookRepository)
    .should()
    .insert(momContactName, momPhoneNumber);
```

## 4. 使用BDDMockito进行mock

让我们测试PhoneBookService类，我们需要mock PhoneBookRepository：

```java
public class PhoneBookService {

    private final PhoneBookRepository phoneBookRepository;

    public PhoneBookService(PhoneBookRepository phoneBookRepository) {
        this.phoneBookRepository = phoneBookRepository;
    }

    public void register(String name, String phone) {
        if (!name.isEmpty() && !phone.isEmpty() && !phoneBookRepository.contains(name)) {
            phoneBookRepository.insert(name, phone);
        }
    }

    public String search(String name) {
        if (!name.isEmpty() && phoneBookRepository.contains(name)) {
            return phoneBookRepository.getPhoneNumberByContactName(name);
        }
        return null;
    }
}

public interface PhoneBookRepository {

    void insert(String name, String phone);

    String getPhoneNumberByContactName(String name);

    boolean contains(String name);
}
```

BDDMockito允许我们返回一个可能是固定的或动态的值。它还允许我们抛出异常：

### 4.1 返回固定值

使用BDDMockito，我们可以轻松地将Mockito配置为在调用我们的mock对象目标方法时返回一个固定的结果：

```java
class BDDMockitoUnitTest {

    @Test
    void givenEmptyPhoneNumber_whenRegister_thenFail() {
        given(phoneBookRepository.contains(momContactName)).willReturn(false);

        phoneBookService.register(xContactName, "");

        then(phoneBookRepository).should(never()).insert(momContactName, momPhoneNumber);
    }
}
```

### 4.2 返回动态值

BDDMockito允许我们提供一种更复杂的方法来返回值。我们可以根据输入返回动态结果：

```java
class BDDMockitoUnitTest {

    @Test
    void givenValidContactName_whenSearchInPhoneBook_thenReturnPhoneNumber() {
        given(phoneBookRepository.contains(momContactName)).willReturn(true);
        given(phoneBookRepository.getPhoneNumberByContactName(momContactName))
                .will((InvocationOnMock invocation) -> {
                    if (invocation.getArgument(0).equals(momContactName)) {
                        return momPhoneNumber;
                    } else {
                        return null;
                    }
                });

        String phoneNumber = phoneBookService.search(momContactName);

        then(phoneBookRepository).should().contains(momContactName);
        then(phoneBookRepository).should().getPhoneNumberByContactName(momContactName);
        assertEquals(phoneNumber, momPhoneNumber);
    }
}
```

### 4.3 抛出异常

告诉Mockito抛出异常也非常简单：

```java
class BDDMockitoUnitTest {

    @Test
    void givenLongPhoneNumber_whenRegister_thenFail() {
        given(phoneBookRepository.contains(xContactName)).willReturn(false);
        willThrow(new RuntimeException())
                .given(phoneBookRepository).insert(any(String.class), eq(tooLongPhoneNumber));

        try {
            phoneBookService.register(xContactName, tooLongPhoneNumber);
            fail("Should throw exception");
        } catch (RuntimeException ignored) {
        }

        then(phoneBookRepository).should(never()).insert(momContactName, tooLongPhoneNumber);
    }
}
```

请注意我们是如何交换given和will*的位置的，如果我们mock一个没有返回值的方法，那么这是必需的。

另外，我们使用(any, eq)之类的参数匹配器来提供一种更通用的基于标准而不是依赖于固定值的mock方式。

## 5. 总结

在这个教程中，我们介绍了使用BDDMockito类编写BDD风格测试的基本用例，并且我们介绍了Mockito和BDDMockito之间的一些差异。