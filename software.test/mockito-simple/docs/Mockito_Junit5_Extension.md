## 1. 概述

在这个教程中，我们演示如何将Mockito与JUnit 5中的Extension模型集成。要了解有关JUnit 5 Extension模型的更多信息，请查看[本文](../../junit-5/docs/Junit5_Extension.md)。

首先，我们演示如何创建一个Extension，该Extension可以自动为任何使用@Mock注解的类属性或方法参数创建mock对象。然后我们将在JUnit 5测试类中使用Mockito Extension。

## 2. Maven配置

### 2.1 依赖

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>5.8.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>4.0.0</version>
    <scope>test</scope>
</dependency>
```

### 2.2 Surefire插件

我们还需要配置Maven Surefire插件以使用新的JUnit Platform Launcher运行我们的测试类：

```xml
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
    <dependencies>
        <dependency>
             <groupId>org.junit.platform</groupId>
             <artifactId>junit-platform-surefire-provider</artifactId>
             <version>1.3.2</version>
         </dependency>
     </dependencies>
</plugin>
```

### 2.3 JUnit 4 IDE兼容性依赖

为了使我们的测试用例与JUnit4(vintage)兼容，对于尚未支持JUnit 5的IDE，让我们包含以下依赖：

```xml
<dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-runner</artifactId>
    <version>1.8.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.vintage</groupId>
    <artifactId>junit-vintage-engine</artifactId>
    <version>5.8.1</version>
    <scope>test</scope>
</dependency>
```

此外，我们应该考虑用@RunWith(JUnitPlatform.class)标注所有测试类。

## 3. Mockito Extension

Mockito为库中的JUnit5 Extension提供了一个实现 - mockito-junit-jupiter。

我们需要在pom.xml中包含此依赖项：

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>4.0.0</version>
    <scope>test</scope>
</dependency>
```

## 4. 构建测试类

现在，让我们创建一个测试类，并使用@ExtendWith注解注册MockitoExtension：

```java
@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    private UserService userService;

    private SettingRepository settingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailClient mailClient;

    private User user;
}
```

我们可以使用@Mock注解为一个实例变量注入mock，并且可以在测试类的任何地方使用它：

```java
@Mock
UserRepository userRepository;
```

此外，我们还可以将mock对象注入到方法参数中：

```java
@BeforeEach
void init(@Mock SettingRepository settingRepository) {
    userService = new DefaultUserService(userRepository, settingRepository, mailClient);
    lenient().when(settingRepository.getUserMinAge()).thenReturn(10);
    when(settingRepository.getUserNameMinLength()).thenReturn(4);
    lenient().when(userRepository.isUsernameAlreadyExists(any(String.class))).thenReturn(false);
    this.settingRepository = settingRepository;
}
```

请注意Mockito.lenient()的用法，当一个初始化的mock在执行期间未被某个测试方法调用时，Mockito会抛出UnsupportedStubbingException。我们可以在初始化mock时使用此方法来避免这种严格的stubbing检查。

我们甚至可以将mock对象注入到测试方法参数中：

```java
@Test
void givenValidUser_whenSaveUser_thenSucceed(@Mock MailClient mailClient) {
    // Given
    user = new User("Jerry", 12);
    when(userRepository.insert(any(User.class))).then(new Answer<User>() {
        int sequence = 1;

        @Override
        public User answer(InvocationOnMock invocation) throws Throwable {
            User user = invocation.getArgument(0);
            user.setId(sequence++);
            return user;
        }
    });

    userService = new DefaultUserService(userRepository, settingRepository, mailClient);

    // When
    User insertedUser = userService.register(user);

    // Then
    verify(userRepository).insert(user);
    assertNotNull(user.getId());
    verify(mailClient).sendUserRegistrationMail(insertedUser);
}
```

请注意，我们作为测试参数注入的MailClient mock与我们在init方法中注入的实例不同。

## 5. 总结

Junit 5为扩展提供了一个很灵活的模型。我们演示了一个简单的MockitoExtension，它可以简化我们的mock创建逻辑。