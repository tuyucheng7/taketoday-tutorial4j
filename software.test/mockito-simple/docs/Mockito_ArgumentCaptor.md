## 1. 概述

在本教程中，我们介绍在单元测试中使用Mockito ArgumentCaptor的常见用例。

## 2. 使用ArgumentCaptor

ArgumentCaptor允许我们捕获传递给方法的参数，以便对其进行检查。**当我们无法访问想要测试的方法之外的参数时，这尤其有用**。

例如，考虑一个EmailService类，它有一个我们想要测试的send方法：

```java
public class EmailService {
    private DeliveryPlatform platform;

    public EmailService(DeliveryPlatform platform) {
        this.platform = platform;
    }

    public void send(String to, String subject, String body, boolean html) {
        Format format = Format.TEXT_ONLY;
        if (html) {
            format = Format.HTML;
        }
        Email email = new Email(to, subject, body);
        email.setFormat(format);
        platform.deliver(email);
    }
    // ...
}
```

在EmailService.send()中，注意platform.deliver()携带一个Email作为参数。作为测试的一部分，我们希望检查Email的format字段是否设置为Format.HTML。为了实现这一点，我们需要捕获并检查传递给platform.deliver()的参数。

### 2.1 创建测试

首先，让我们创建单元测试类：

```java
@ExtendWith(MockitoExtension.class)
class EmailServiceUnitTest {

    @Mock
    DeliveryPlatform platform;

    @InjectMocks
    EmailService emailService;
    // ...
}
```

我们使用@Mock注解来mock DeliveryPlatform对象，它会通过@InjectMocks注解自动注入到我们的EmailService中。

### 2.2 添加一个ArgumentCaptor字段

其次，我们添加一个类型为Email的ArgumentCaptor字段，用于保存捕获的参数：

```java
@Captor
ArgumentCaptor<Email> emailCaptor;
```

### 2.3 捕获参数

第三，我们使用Mockito.verify与ArgumentCaptor捕获Email参数：

```java
Mockito.verify(platform).deliver(emailCaptor.capture());
```

然后，我们可以获取捕获的值并将其存储为新的Email对象：

```java
Email emailCaptorValue = emailCaptor.getValue();
```

### 2.4 检查捕获的值

最后，下面是完整的测试，使用断言检查捕获的Email对象：

```java
@Test
void whenDoesNotSupportHtml_expectTextOnlyEmailFormat() {
    String to = "info@tuyucheng.com";
    String subject = "Using ArgumentCaptor";
    String body = "Hey, let'use ArgumentCaptor";
    
    emailService.send(to, subject, body, false);
    
    Mockito.verify(platform).deliver(emailCaptor.capture());
    Email emailCaptorValue = emailCaptor.getValue();
    assertThat(emailCaptorValue.getFormat()).isEqualTo(Format.TEXT_ONLY);
}
```

## 3. 避免Stubbing

**虽然我们可以使用带有stubbing的ArgumentCaptor，但我们通常应该避免这样做**。在Mockito中，这通常意味着避免将ArgumentCaptor与Mockito.when一起使用。对于stubbing，我们应该使用ArgumentMatcher代替。

下面介绍我们应该避免stubbing的几个原因。

### 3.1 测试可读性降低

首先，考虑一个简单的测试：

```java
@Test
void whenUsingArgumentMatcherForValidCredentials_expectTrue() {
    Credentials credentials = new Credentials("tuyucheng", "correct_password", "correct_key");
    
    Mockito.when(platform.authenticate(Mockito.eq(credentials))).thenReturn(AuthenticationStatus.AUTHENTICATED);
    
    assertTrue(emailService.authenticatedSuccessfully(credentials));
}
```

这里我们使用Mockito.eq(credentials)来指定mock什么时候应该返回一个对象。

接下来，考虑使用ArgumentCaptor进行相同的测试：

```java
@Test
void whenUsingArgumentCaptorForValidCredentials_expectTrue() {
    Credentials credentials = new Credentials("tuyucheng", "correct_password", "correct_key");
    Mockito.when(platform.authenticate(credentialsCaptor.capture())).thenReturn(AuthenticationStatus.AUTHENTICATED);
    
    assertTrue(emailService.authenticatedSuccessfully(credentials));
    assertThat(credentialsCaptor.getValue()).isEqualTo(credentials);
}
```

与第一个测试不同，请注意我们必须在最后一行执行额外的断言来执行与Mockito.eq(credentials)相同的操作。

最后，请注意目前还不清楚credentialsCaptor.capture()指的是什么，这是因为我们必须在使用它的行之外创建捕获器，从而降低了可读性。

### 3.2 混淆故障定位

另一个原因是如果emailService.authenticatedSuccessfully没有调用platform.authenticate，我们会得到一个异常：

```text
org.mockito.exceptions.base.MockitoException: No argument value was captured!
```

这是因为我们的stubbed方法没有捕获参数。然而，真正的问题不在于我们的测试本身，而在于我们正在测试的实际方法。

换句话说，**它会将我们误导为测试中的异常，而实际缺陷在我们正在测试的方法中**。

## 4. 总结

在这篇简短的文章中，我们演示了使用ArgumentCaptor的一般用例。并且解释了避免使用带有stubbing的ArgumentCaptor的原因。