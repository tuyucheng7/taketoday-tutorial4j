## 1. 简介

### 1.1 概述

在这篇文章中，我们将讨论**mocking**：它是什么，为什么要使用它，以及如何使用一些最常用的Java Mock库来mock同一测试用例的几个例子。

我们将从mock概念的一些正式/半正式定义开始；然后我们将介绍所测试的案例，跟进每个库的示例并得出一些结论。这里选择的库是[Mockito](http://mockito.org/)、[EasyMock](http://easymock.org/)和[JMockit](https://jmockit.github.io/)。

### 1.2 使用Mock的原因

我们将开始假设你已经按照一些以测试为中心的驱动开发方法(TDD、ATDD或BDD)进行编码。或者只是你想为依靠依赖项来实现其功能的现有类创建一个测试。

在任何情况下，当对一个类进行单元测试时，**我们只想测试它的功能，而不是测试它的依赖项**(因为我们信任这些依赖项的实现是正确的，或者因为我们将自己测试它)。

为了实现这一点，我们需要为被测对象提供一个我们可以控制该依赖项的替代品。通过这种方式，我们可以强制对象返回极端值、抛出异常或简单地将耗时的方法减少到固定的返回值。

种受控的替换就是**mock**，它将帮助你简化测试编码并减少测试执行时间。

### 1.3 Mock概念和定义

让我们看看Martin Fowler撰写的一篇[文章](http://martinfowler.com/articles/mocksArentStubs.html)中的四个定义，该文章总结了每个人都应该了解的关于mock的基础知识：

-   **Dummy**对象被传递，但从未实际使用过。通常，它们仅用于填充参数列表。
-   **Fack**对象具有有效的实现，但通常会采取一些捷径，这使得它们不适合生产(内存数据库就是一个很好的例子)。
-   **Stubs**为测试过程中的方法调用提供固定的答案，通常根本不响应测试编程之外的任何内容。Stubs还可以记录有关调用的信息，例如电子邮件网关存根会记住它“发送”的消息，或者可能只记住它“发送”的消息数量。
-   **Mocks**就是我们在这里讨论的：预先编程了期望的对象，这些对象形成了它们期望接收的调用的规范。

### 1.4 Mock还是不Mock：这是个问题

**并非一切都必须被mock**。有时最好进行集成测试，因为mock该方法/功能只会带来很少的实际好处。在我们的测试用例(将在下一点显示)中，它将测试LoginDao。

LoginDao将使用一些第三方库进行数据库访问，mock它只包括确保已为调用准备好参数，但我们仍然需要测试调用是否返回了我们想要的数据。

出于这个原因，它不会包含在这个例子中(尽管我们可以编写单元测试，对第三方库调用进行mock调用，并使用DBUnit编写集成测试来测试第三方库的实际性能)。

## 2. 测试用例

考虑到上一节中的所有内容，让我们提出一个非常典型的测试用例，以及我们将如何使用mock来测试它(当使用mock有意义时)。这将有助于我们有一个通用的场景，以便之后能够比较不同的mock库。

### 2.1 建议案例

建议的测试用例将是具有分层架构的应用程序中的登录过程。

登录请求将由使用服务的控制器处理，该服务使用DAO(在DB中查找用户凭据)。我们不会深入探讨每一层的实现，而是更多地关注每一层**组件之间的交互**。

这样，我们将拥有一个LoginController、一个LoginService和一个LoginDAO。让我们看一个图表进行澄清：

<img src="../assets/img_1.png">

### 2.2 实现

我们现在将遵循用于测试用例的实现，这样我们就可以了解测试中发生了什么(或应该发生什么)。

我们将从用于所有操作的模型UserForm开始，它只保存用户名和密码(我们使用public访问修饰符来简化)，username字段的getter方法以允许对该属性进行mock：

```java
public class UserForm {
    public String password;
    public String username;

    public String getUsername() {
        return username;
    }
}
```

随后是LoginDAO，它没有任何具体功能实现，因为我们只希望它有一个login方法，这样我们可以在需要时mock它们：

```java
public class LoginDao {
    public int login(UserForm userForm) {
        return 0;
    }
}
```

LoginService将在其login方法中使用LoginDao，LoginService还有一个setCurrentUser方法返回void来测试该mock。

```java
public class LoginService {
    private LoginDao loginDao;
    private String currentUser;

    public boolean login(UserForm userForm) {
        assert null != userForm;
        int loginResults = loginDao.login(userForm);
        return loginResults == 1;
    }

    public void setCurrentUser(String username) {
        if (null != username) {
            this.currentUser = username;
        }
    }

    public void setLoginDao(LoginDao loginDao) {
        this.loginDao = loginDao;
    }

    // standard setters and getters
}
```

最后，LoginController将使用LoginService作为其login方法。这将包括：

-   不会调用mock服务的情况
-   仅调用一个方法的情况
-   将调用所有方法的情况
-   将测试异常抛出的情况

```java
public class LoginController {
    public LoginService loginService;

    public String login(UserForm userForm) {
        if (null == userForm) {
            return "ERROR";
        } else {
            boolean logged;

            try {
                logged = loginService.login(userForm);
            } catch (Exception e) {
                return "ERROR";
            }

            if (logged) {
                loginService.setCurrentUser(userForm.getUsername());
                return "OK";
            } else {
                return "KO";
            }
        }
    }
}
```

现在我们已经了解了我们试图测试的内容，让我们看看我们将如何使用每个库来mock它。

## 3. 测试设置

### 3.1 Mockito

对于Mockito，我们将使用[2.8.9](https://www.javadoc.io/doc/org.mockito/mockito-core/2.8.9)版本。

创建和使用mock的最简单方法是通过@Mock和@InjectMocks注解。第一个将为用于定义字段的类创建一个mock，第二个将尝试将所述创建的mock注入到带注解的mock中。

还有更多注解，例如@Spy，它允许你创建部分mock(在非mock方法中使用正常实现的mock)。

话虽如此，在执行任何使用所述mock以使所有这些“魔法”起作用的测试之前，你需要先调用MockitoAnnotations.initMocks(this)。这通常在@BeforeEach注解方法中完成，或者也可以使用MockitoExtension。

```java
class LoginControllerIntegrationTest {

    @Mock
    private LoginDao loginDao;

    @Spy
    @InjectMocks
    private LoginService spiedLoginService;

    @Mock
    private LoginService loginService;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        loginController = new LoginController();
        MockitoAnnotations.openMocks(this);
    }
}
```

### 3.2 EasyMock

对于EasyMock，我们将使用[3.4](https://mvnrepository.com/artifact/org.easymock/easymock/3.4)版本([javadoc](http://easymock.org/api/))。请注意，对于EasyMock，要让mock开始“工作”，你必须在每个测试方法上调用EasyMock.replay(mock)，否则你将得到异常。

mock和测试类也可以通过注解来定义，但在这种情况下，我们将使用EasyMockRunner运行测试类，而不是调用静态方法来使其工作。

mocks是使用@Mock注解创建的，被测试的对象是用@TestSubject注解创建的(它将从创建的mock中注入其依赖项)。必须以内联方式创建测试对象。

```java
@RunWith(EasyMockRunner.class)
public class LoginControllerTest {

    @Mock
    private LoginDao loginDao;

    @Mock
    private LoginService loginService;

    @TestSubject
    private LoginController loginController = new LoginController();
}
```

### 3.3 JMockit

对于JMockit，我们将使用[1.24](http://jmockit.github.io/changes.html#1.24)版本([javadoc](http://jmockit.github.io/))。

JMockit的设置与Mockito一样简单，除了没有针对部分mock的特定注解(实际上也不需要)，并且你必须使用JMockit作为测试Runner。

mock是使用@Injectable注解(将只创建一个mock实例)或使用@Mocked注解(将为注解字段的类的每个实例创建mock)定义的。

使用@Tested注解创建测试实例(并注入其mock依赖项)。

```java
@RunWith(JMockit.class)
public class LoginControllerTest {

    @Injectable
    private LoginDao loginDao;

    @Injectable
    private LoginService loginService;

    @Tested
    private LoginController loginController;
}
```

## 4. 验证没有Mock调用

### 4.1 Mockito

为了验证mock在Mockito中没有收到任何调用，你可以使用接收mock的方法verifyNoInteractions()。

```java
@Test
void assertThatNoMethodHasBeenCalled() {
	loginController.login(null);
	Mockito.verifyNoInteractions(loginService);
}
```

### 4.2 EasyMock

为了验证mock没有收到任何调用，你只需不指定行为，重放mock，最后验证它。

```java
@Test
public void assertThatNoMethodHasBeenCalled() {
    EasyMock.replay(loginService);
    loginController.login(null);
    EasyMock.verify(loginService);
}
```

### 4.3 JMockit

为了验证mock没有收到任何调用，你只需不指定对该mock的期望并为所述mock执行FullVerifications(mock)。

```java
@Test
public void assertThatNoMethodHasBeenCalled() {
    loginController.login(null);
    new FullVerifications(loginService) {};
}
```

## 5. 定义Mock方法调用和验证对Mock的调用

### 5.1 Mockito

对于**mock方法调用**，你可以使用Mockito.when(mock.method(args)).thenReturn(value)。在这里，你可以为多个调用返回不同的值，只需将它们添加为更多参数即可：thenReturn(value1, value2, value-n, ...)。

请注意，你不能使用此语法mock void返回方法。在上述情况下，你将使用上述方法的验证(如第11行所示)。

要**验证对mock的调用**，你可以使用Mockito.verify(mock).method(args)，并且你还可以使用verifyNoMoreInteractions(mock)验证没有对mock进行更多调用。

为了**验证参数**，你可以传递特定值或使用预定义的匹配器，如any()、anyString()、anyInt()。这样的匹配器还有很多，甚至可以定义匹配器，我们将在以下示例中看到。

```java
@Test
public void assertTwoMethodsHaveBeenCalled() {
    UserForm userForm = new UserForm();
    userForm.username = "foo";
    Mockito.when(loginService.login(userForm)).thenReturn(true);

    String login = loginController.login(userForm);

    Assert.assertEquals("OK", login);
    Mockito.verify(loginService).login(userForm);
    Mockito.verify(loginService).setCurrentUser("foo");
}

@Test
public void assertOnlyOneMethodHasBeenCalled() {
    UserForm userForm = new UserForm();
    userForm.username = "foo";
    Mockito.when(loginService.login(userForm)).thenReturn(false);

    String login = loginController.login(userForm);

    Assert.assertEquals("KO", login);
    Mockito.verify(loginService).login(userForm);
    Mockito.verifyNoMoreInteractions(loginService);
}
```

### 5.2 EasyMock

对于**mock方法调用**，你使用EasyMock.expect(mock.method(args)).andReturn(value)。

为了**验证对mock的调用**，你可以使用EasyMock.verify(mock)，但你必须始终**在调用EasyMock.replay(mock)之后调用它**。

为了**验证参数**，你可以传递特定的值，或者可以使用预定义的匹配器，例如isA(Class.class)、anyString()、anyInt()，以及[更多](http://easymock.org/user-guide.html#verification-expectations)此类匹配器，并且可以再次定义你的匹配器。

```java
@Test
public void assertTwoMethodsHaveBeenCalled() {
    UserForm userForm = new UserForm();
    userForm.username = "foo";
    EasyMock.expect(loginService.login(userForm)).andReturn(true);
    loginService.setCurrentUser("foo");
    EasyMock.replay(loginService);

    String login = loginController.login(userForm);

    Assert.assertEquals("OK", login);
    EasyMock.verify(loginService);
}

@Test
public void assertOnlyOneMethodHasBeenCalled() {
    UserForm userForm = new UserForm();
    userForm.username = "foo";
    EasyMock.expect(loginService.login(userForm)).andReturn(false);
    EasyMock.replay(loginService);

    String login = loginController.login(userForm);

    Assert.assertEquals("KO", login);
    EasyMock.verify(loginService);
}
```

### 5.3 JMockit

使用JMockit，你已经定义了[测试步骤](https://jmockit.github.io/tutorial/Mocking.html#model)：记录、重播和验证。

**记录**是在一个新的Expectations(){{}}块中完成的(你可以在其中定义多个mock的操作)，**重播**只需通过调用测试类的方法(应该调用一些mock对象)来完成，**验证**是在一个新的Verifications(){{}}块中完成(你可以在其中定义多个mock的验证)。

对于**mock方法调用**，你可以使用mock.method(args); result = value；在任何Expectations块内。在这里，你可以使用return (value1, value2, ..., valuen);为多个调用返回不同的值。而不是result = value;。

为了[验证对mock的调用](https://jmockit.github.io/tutorial/Mocking.html#verification)，你可以使用new Verifications(){{mock.call(value)}}或new Verifications(mock){{}}来验证之前定义的每个预期调用。

为了**验证参数**，你可以传递特定值，或者使用[预定义的值](https://jmockit.github.io/tutorial/Mocking.html#argumentMatching)，例如any、anyString、anyLong以及更多此类特殊值，并且可以再次定义你的匹配器(必须是Hamcrest匹配器)。

```java
@Test
public void assertTwoMethodsHaveBeenCalled() {
    UserForm userForm = new UserForm();
    userForm.username = "foo";
    new Expectations() {{
        loginService.login(userForm); result = true;
        loginService.setCurrentUser("foo");
    }};

    String login = loginController.login(userForm);

    Assert.assertEquals("OK", login);
    new FullVerifications(loginService) {};
}

@Test
public void assertOnlyOneMethodHasBeenCalled() {
    UserForm userForm = new UserForm();
    userForm.username = "foo";
    new Expectations() {{
        loginService.login(userForm); result = false;
        // no expectation for setCurrentUser
    }};

    String login = loginController.login(userForm);

    Assert.assertEquals("KO", login);
    new FullVerifications(loginService) {};
}
```

## 6. Mock异常抛出

### 6.1 Mockito

可以在Mockito.when(mock.method(args))之后使用.thenThrow(ExceptionClass.class) mock异常抛出。

```java
@Test
public void mockExceptionThrowing() {
    UserForm userForm = new UserForm();
    Mockito.when(loginService.login(userForm)).thenThrow(IllegalArgumentException.class);

    String login = loginController.login(userForm);

    Assert.assertEquals("ERROR", login);
    Mockito.verify(loginService).login(userForm);
    Mockito.verifyNoInteractions(loginService);
}
```

### 6.2 EasyMock

在EasyMock.expect(...)调用之后，可以使用.andThrow(new ExceptionClass()) mock异常抛出。

```java
@Test
public void mockExceptionThrowing() {
    UserForm userForm = new UserForm();
    EasyMock.expect(loginService.login(userForm)).andThrow(new IllegalArgumentException());
    EasyMock.replay(loginService);

    String login = loginController.login(userForm);

    Assert.assertEquals("ERROR", login);
    EasyMock.verify(loginService);
}
```

### 6.3 JMockit

使用JMockito mock异常抛出特别容易。只需返回一个Exception作为mock方法调用的结果，而不是“正常”返回。

```java
@Test
public void mockExceptionThrowing() {
    UserForm userForm = new UserForm();
    new Expectations() {{
        loginService.login(userForm); result = new IllegalArgumentException();
        // no expectation for setCurrentUser
    }};

    String login = loginController.login(userForm);

    Assert.assertEquals("ERROR", login);
    new FullVerifications(loginService) {};
}
```

## 7. Mock对象传递

### 7.1 Mockito

你也可以创建一个mock作为方法调用的参数传递。使用Mockito，你可以使用单行代码来做到这一点。

```java
@Test
public void mockAnObjectToPassAround() {
    UserForm userForm = Mockito.when(Mockito.mock(UserForm.class).getUsername())
        .thenReturn("foo").getMock();
    Mockito.when(loginService.login(userForm)).thenReturn(true);

    String login = loginController.login(userForm);

    Assert.assertEquals("OK", login);
    Mockito.verify(loginService).login(userForm);
    Mockito.verify(loginService).setCurrentUser("foo");
}
```

### 7.2 EasyMock

可以使用EasyMock.mock(Class.class)单行创建mock。之后，你可以使用EasyMock.expect(mock.method())为执行做准备，始终要记住在使用它之前调用EasyMock.replay(mock)。

```java
@Test
public void mockAnObjectToPassAround() {
    UserForm userForm = EasyMock.mock(UserForm.class);
    EasyMock.expect(userForm.getUsername()).andReturn("foo");
    EasyMock.expect(loginService.login(userForm)).andReturn(true);
    loginService.setCurrentUser("foo");
    EasyMock.replay(userForm);
    EasyMock.replay(loginService);

    String login = loginController.login(userForm);

    Assert.assertEquals("OK", login);
    EasyMock.verify(userForm);
    EasyMock.verify(loginService);
}
```

### 7.3 JMockit

要仅mock一个方法的对象，你可以简单地将mock的对象作为参数传递给测试方法。然后，你可以像任何其他mock一样创建期望。

```java
@Test
public void mockAnObjectToPassAround(@Mocked UserForm userForm) {
    new Expectations() {{
        userForm.getUsername(); result = "foo";
        loginService.login(userForm); result = true;
        loginService.setCurrentUser("foo");
    }};
    
    String login = loginController.login(userForm);

    Assert.assertEquals("OK", login);
    new FullVerifications(loginService) {};
    new FullVerifications(userForm) {};
}
```

## 8. 自定义参数匹配

### 8.1 Mockito

有时，mock调用的参数匹配需要比固定值或anyString()更复杂一些。对于这种情况，Mockito具有与argThat(ArgumentMatcher<>)一起使用的匹配器类。

```java
@Test
public void argumentMatching() {
    UserForm userForm = new UserForm();
    userForm.username = "foo";
    // default matcher
    Mockito.when(loginService.login(Mockito.any(UserForm.class))).thenReturn(true);

    String login = loginController.login(userForm);

    Assert.assertEquals("OK", login);
    Mockito.verify(loginService).login(userForm);
    // complex matcher
    Mockito.verify(loginService).setCurrentUser(ArgumentMatchers.argThat(
        new ArgumentMatcher<String>() {
            @Override
            public boolean matches(String argument) {
                return argument.startsWith("foo");
            }
        }
    ));
}
```

### 8.2 EasyMock

使用EasyMock自定义参数匹配稍微复杂一些，因为你需要创建一个静态方法，在该方法中创建实际的匹配器，然后使用EasyMock.reportMatcher(IArgumentMatcher)报告它。

创建此方法后，你可以通过调用该方法在你的mock期望中使用它(如第1行中的示例所示)。

```java
@Test
public void argumentMatching() {
    UserForm userForm = new UserForm();
    userForm.username = "foo";
    // default matcher
    EasyMock.expect(loginService.login(EasyMock.isA(UserForm.class))).andReturn(true);
    // complex matcher
    loginService.setCurrentUser(specificArgumentMatching("foo"));
    EasyMock.replay(loginService);

    String login = loginController.login(userForm);

    Assert.assertEquals("OK", login);
    EasyMock.verify(loginService);
}

private static String specificArgumentMatching(String expected) {
    EasyMock.reportMatcher(new IArgumentMatcher() {
        @Override
        public boolean matches(Object argument) {
            return argument instanceof String 
              && ((String) argument).startsWith(expected);
        }

        @Override
        public void appendTo(StringBuffer buffer) {
            //NOOP
        }
    });
    return null;
}
```

### 8.3 JMockit

与JMockit匹配的自定义参数是通过特殊的withArgThat(Matcher)方法(接收[Hamcrest](http://hamcrest.org/)的Matcher对象)完成的。

```java
@Test
public void argumentMatching() {
    UserForm userForm = new UserForm();
    userForm.username = "foo";
    // default matcher
    new Expectations() {{
        loginService.login((UserForm) any);
        result = true;
        // complex matcher
        loginService.setCurrentUser(withArgThat(new BaseMatcher<String>() {
            @Override
            public boolean matches(Object item) {
                return item instanceof String && ((String) item).startsWith("foo");
            }

            @Override
            public void describeTo(Description description) {
                // NOOP
            }
        }));
    }};

    String login = loginController.login(userForm);

    Assert.assertEquals("OK", login);
    new FullVerifications(loginService) {};
}
```

## 9. 部分mock

### 9.1 Mockito

Mockito允许以两种方式进行部分mock(在某些方法中使用真实实现而不是mock方法调用的mock)。

你可以在普通的mock方法调用定义中使用.thenCallRealMethod()，也可以创建一个**spy**而不是mock，在这种情况下，默认行为将是在所有非mock方法中调用真正的实现。

```java
@Test
public void partialMocking() {
    // use partial mock
    loginController.loginService = spiedLoginService;
    UserForm userForm = new UserForm();
    userForm.username = "foo";
    // let service's login use implementation so let's mock DAO call
    Mockito.when(loginDao.login(userForm)).thenReturn(1);

    String login = loginController.login(userForm);

    Assert.assertEquals("OK", login);
    // verify mocked call
    Mockito.verify(spiedLoginService).setCurrentUser("foo");
}
```

### 9.2 EasyMock

使用EasyMock时，部分mock也会变得更加复杂，因为你需要在创建mock时定义哪些方法将被mock。

这是通过EasyMock.partialMockBuilder(Class.class).addMockedMethod("methodName").createMock()完成的。完成此操作后，你可以将mock用作任何其他非部分mock。

```java
@Test
public void partialMocking() {
    UserForm userForm = new UserForm();
    userForm.username = "foo";
    // use partial mock
    LoginService loginServicePartial = EasyMock.partialMockBuilder(LoginService.class)
        .addMockedMethod("setCurrentUser").createMock();
    loginServicePartial.setCurrentUser("foo");
    // let service's login use implementation so let's mock DAO call
    EasyMock.expect(loginDao.login(userForm)).andReturn(1);

    loginServicePartial.setLoginDao(loginDao);
    loginController.loginService = loginServicePartial;
    
    EasyMock.replay(loginDao);
    EasyMock.replay(loginServicePartial);

    String login = loginController.login(userForm);

    Assert.assertEquals("OK", login);
    // verify mocked call
    EasyMock.verify(loginServicePartial);
    EasyMock.verify(loginDao);
}
```

### 9.3 JMockit

使用JMockit进行部分mock特别容易。在Expectations(){{}}中没有定义mock行为的每个方法调用都**使用“真实”实现**。

现在让我们假设我们想要部分mock LoginService类来mock setCurrentUser()方法，同时使用login()方法的实际实现。

为此，我们首先创建一个LoginService实例并将其传递给Expectations块。然后，我们只记录对setCurrentUser()方法的期望：

```java
@Test
public void partialMocking() {
    LoginService partialLoginService = new LoginService();
    partialLoginService.setLoginDao(loginDao);
    loginController.loginService = partialLoginService;

    UserForm userForm = new UserForm();
    userForm.username = "foo";
        
    new Expectations(partialLoginService) {{
        // let's mock DAO call
        loginDao.login(userForm); result = 1;
            
        // no expectation for login method so that real implementation is used
            
        // mock setCurrentUser call
        partialLoginService.setCurrentUser("foo");
    }};

    String login = loginController.login(userForm);

    Assert.assertEquals("OK", login);
    // verify mocked call
    new Verifications() {{
        partialLoginService.setCurrentUser("foo");
    }};     
}
```

## 10. 总结

在这篇文章中，我们比较了三个Java Mock库，每个库都有其优点和缺点。

-   所有这三个都可以通过注解轻松配置，以帮助你定义mock和被测对象，Runner使mock注入尽可能轻松。
    -   我们会说Mockito会在这里获胜，因为它有一个用于部分mock的特殊注解，但JMockit甚至不需要它，所以假设它是这两者之间的平局。
-   他们三个都或多或少地遵循**记录-重播-验证模式**，但在我们看来，最好的方法是JMockit，因为它强制你在块中使用它们，因此测试变得更加结构化。
-   **易用性**很重要，因此你可以尽可能少地定义测试。JMockit将因其固定不变的结构而成为首选。
-   Mockito或多或少是最知名的，因此**社区**会更大。
-   每次你想使用mock时都必须调用replay显然是不行的，所以我们会为EasyMock加上一个减号。
-   **一致性/简单性**对我来说也很重要。我们喜欢JMockit返回结果的方式，这种方式对于“正常”结果和异常结果是一样的。

话虽这么说，我们将选择JMockit作为赢家，尽管到目前为止我们一直在使用Mockito，因为我们已经被它的简单性和固定结构所吸引，并且将从现在开始尝试使用它。