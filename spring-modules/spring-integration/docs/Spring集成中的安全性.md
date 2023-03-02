## 1. 简介

在本文中，我们将重点介绍如何在集成流程中同时使用Spring Integration和Spring Security。

因此，我们将设置一个简单的安全消息流来演示在Spring Integration中使用Spring Security。此外，我们将提供多线程消息通道中的SecurityContext传播示例。

有关使用该框架的更多详细信息，可以参考我们对[Spring Integration的介绍](https://www.baeldung.com/spring-integration)。

## 2. Spring Integration配置

### 2.1 依赖

首先，我们需要将Spring Integration依赖项添加到我们的项目中。

由于我们将使用DirectChannel、PublishSubscribeChannel和ServiceActivator设置一个简单的消息流，因此我们需要spring-integration-core依赖项。

此外，我们还需要spring-integration-security依赖项才能在Spring Integration中使用Spring Security：

```xml
<dependency>
    <groupId>org.springframework.integration</groupId>
    <artifactId>spring-integration-security</artifactId>
    <version>5.0.3.RELEASE</version>
</dependency>
```

我们还使用了Spring Security，因此我们将spring-security-config添加到我们的项目中：

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
    <version>5.0.3.RELEASE</version>
</dependency>
```

我们可以在Maven Central查看上述所有依赖项的最新版本：[spring-integration-security](https://central.sonatype.com/artifact/org.springframework.integration/spring-integration-security/6.0.3)、[spring-security-config](https://central.sonatype.com/artifact/org.springframework.security/spring-security-config/6.0.2)。

### 2.2 基于Java的配置

我们的示例将使用基本的Spring Integration组件。因此，我们只需要使用@EnableIntegration注解在我们的项目中启用Spring Integration：

```java
@Configuration
@EnableIntegration
public class SecuredDirectChannel {
    // ...
}
```

## 3. 安全消息通道

首先，**我们需要一个ChannelSecurityInterceptor实例，它将拦截通道上的所有send和receive调用，并决定是否可以执行或拒绝该调用**：

```java
@Autowired
@Bean
public ChannelSecurityInterceptor channelSecurityInterceptor(AuthenticationManager authenticationManager, AccessDecisionManager customAccessDecisionManager) {
    ChannelSecurityInterceptor channelSecurityInterceptor = new ChannelSecurityInterceptor();

    channelSecurityInterceptor.setAuthenticationManager(authenticationManager);

    channelSecurityInterceptor.setAccessDecisionManager(customAccessDecisionManager);

    return channelSecurityInterceptor;
}
```

AuthenticationManager和AccessDecisionManager bean定义为：

```java
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends GlobalMethodSecurityConfiguration {

    @Override
    @Bean
    public AuthenticationManager
    authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public AccessDecisionManager customAccessDecisionManager() {
        List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<>();
        decisionVoters.add(new RoleVoter());
        decisionVoters.add(new UsernameAccessDecisionVoter());
        AccessDecisionManager accessDecisionManager = new AffirmativeBased(decisionVoters);
        return accessDecisionManager;
    }
}
```

在这里，我们使用两个AccessDecisionVoter:RoleVoter和一个自定义的UsernameAccessDecisionVoter。

现在，我们可以使用ChannelSecurityInterceptor来保护我们的通道。我们需要做的是通过@SecureChannel注解来装饰通道：

```java
@Bean(name = "startDirectChannel")
@SecuredChannel(interceptor = "channelSecurityInterceptor", sendAccess = { "ROLE_VIEWER","jane" })
public DirectChannel startDirectChannel() {
    return new DirectChannel();
}

@Bean(name = "endDirectChannel")
@SecuredChannel(interceptor = "channelSecurityInterceptor", sendAccess = {"ROLE_EDITOR"})
public DirectChannel endDirectChannel() {
    return new DirectChannel();
}
```

@SecureChannel接收三个属性：

-   interceptor属性：指的是ChannelSecurityInterceptor bean。
-   sendAccess和receiveAccess属性：包含在通道上调用send或receive操作的策略。

在上面的示例中，我们希望只有拥有ROLE_VIEWER或用户名jane的用户才能从startDirectChannel发送消息。

此外，只有拥有ROLE_EDITOR的用户才能向endDirectChannel发送消息。

我们在自定义AccessDecisionManager的支持下实现了这一点：RoleVoter或UsernameAccessDecisionVoter返回肯定的响应，即授予访问权限。

## 4. 保护ServiceActivator

值得一提的是，我们还可以通过Spring方法安全来保护我们的ServiceActivator。因此，我们需要启用方法安全注解：

```java
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends GlobalMethodSecurityConfiguration {
    // ....
}
```

为简单起见，在本文中，我们将仅使用Spring pre和post注解，因此我们将@EnableGlobalMethodSecurity注解添加到我们的配置类并将prePostEnabled设置为true。

现在我们可以使用@PreAuthorization注解来保护我们的ServiceActivator：

```java
@ServiceActivator(inputChannel = "startDirectChannel", outputChannel = "endDirectChannel")
@PreAuthorize("hasRole('ROLE_LOGGER')")
public Message<?> logMessage(Message<?> message) {
    Logger.getAnonymousLogger().info(message.toString());
    return message;
}
```

这里的ServiceActivator接收来自startDirectChannel的消息，并将消息输出到endDirectChannel。

此外，仅当当前身份验证主体具有角色ROLE_LOGGER时，才能访问该方法。

## 5. 安全上下文传播

**默认情况下，Spring SecurityContext是线程绑定的**。这意味着SecurityContext不会传播到子线程。

对于以上所有示例，我们同时使用DirectChannel和ServiceActivator-它们都在单个线程中运行；因此，SecurityContext在整个流程中都可用。

但是，**当将QueueChannel、ExecutorChannel和PublishSubscribeChannel与Executor一起使用时，消息将从一个线程传输到其他线程**。在这种情况下，我们需要将SecurityContext传播到所有接收消息的线程。

让我们创建另一个以PublishSubscribeChannel通道开始的消息流，并且两个ServiceActivator订阅该通道：

```java
@Bean(name = "startPSChannel")
@SecuredChannel(interceptor = "channelSecurityInterceptor", sendAccess = "ROLE_VIEWER")
public PublishSubscribeChannel startChannel() {
    return new PublishSubscribeChannel(executor());
}

@ServiceActivator(inputChannel = "startPSChannel", outputChannel = "finalPSResult")
@PreAuthorize("hasRole('ROLE_LOGGER')")
public Message<?> changeMessageToRole(Message<?> message) {
    return buildNewMessage(getRoles(), message);
}

@ServiceActivator(inputChannel = "startPSChannel", outputChannel = "finalPSResult")
@PreAuthorize("hasRole('ROLE_VIEWER')")
public Message<?> changeMessageToUserName(Message<?> message) {
    return buildNewMessage(getUsername(), message);
}
```

在上面的示例中，我们有两个ServiceActivator订阅了startPSChannel。该通道需要具有角色ROLE_VIEWER的身份验证主体才能向其发送消息。

同样，仅当身份验证主体具有ROLE_LOGGER角色时，我们才能调用changeMessageToRole服务。

此外，仅当身份验证主体具有角色ROLE_VIEWER时，才能调用changeMessageToUserName服务。

同时，startPSChannel将在ThreadPoolTaskExecutor的支持下运行：

```java
@Bean
public ThreadPoolTaskExecutor executor() {
    ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
    pool.setCorePoolSize(10);
    pool.setMaxPoolSize(10);
    pool.setWaitForTasksToCompleteOnShutdown(true);
    return pool;
}
```

因此，两个ServiceActivator将运行在两个不同的线程中。**要将SecurityContext传播到这些线程，我们需要向我们的消息通道添加一个SecurityContextPropagationChannelInterceptor**：

```java
@Bean
@GlobalChannelInterceptor(patterns = { "startPSChannel" })
public ChannelInterceptor securityContextPropagationInterceptor() {
    return new SecurityContextPropagationChannelInterceptor();
}
```

请注意我们是如何使用@GlobalChannelInterceptor注解装饰SecurityContextPropagationChannelInterceptor的。我们还将startPSChannel添加到它的patterns属性中。

因此，上面的配置声明当前线程的SecurityContext将传播到从startPSChannel派生的任何线程。

## 6. 测试

让我们开始使用一些JUnit测试来验证我们的消息流。

### 6.1 依赖

当然，此时我们需要spring-security-test依赖：

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <version>5.0.3.RELEASE</version>
    <scope>test</scope>
</dependency>
```

同样，可以从Maven Central检查最新版本：[spring-security-test](https://central.sonatype.com/artifact/org.springframework.security/spring-security-test/6.0.2)。

### 6.2 测试安全通道

首先，我们尝试向我们的startDirectChannel发送一条消息：

```java
@Test(expected = AuthenticationCredentialsNotFoundException.class)
public void givenNoUser_whenSendToDirectChannel_thenCredentialNotFound() {
    startDirectChannel.send(new GenericMessage<String>(DIRECT_CHANNEL_MESSAGE));
}
```

由于通道是安全的，因此在发送消息而不提供authentication对象时，我们预计会出现AuthenticationCredentialsNotFoundException异常。

接下来，我们提供一个具有角色ROLE_VIEWER的用户，并向我们的startDirectChannel发送一条消息：

```java
@Test
@WithMockUser(roles = { "VIEWER" })
public void givenRoleViewer_whenSendToDirectChannel_thenAccessDenied() {
    expectedException.expectCause(IsInstanceOf.<Throwable> instanceOf(AccessDeniedException.class));

    startDirectChannel.send(new GenericMessage<String>(DIRECT_CHANNEL_MESSAGE));
 }
```

现在，即使我们的用户可以将消息发送到startDirectChannel(因为他具有角色ROLE_VIEWER)，但他不能调用请求具有角色ROLE_LOGGER的用户的logMessage服务。

在这种情况下，将抛出一个MessageHandlingException，其原因是AccessDeniedException。

测试将抛出MessageHandlingException，原因是AccessDeniedException。因此，我们使用ExpectedException规则的实例来验证原因异常。

接下来，我们为用户提供用户名jane和两个角色：ROLE_LOGGER和ROLE_EDITOR。

然后尝试再次向startDirectChannel发送消息：

```java
@Test
@WithMockUser(username = "jane", roles = { "LOGGER", "EDITOR" })
public void givenJaneLoggerEditor_whenSendToDirectChannel_thenFlowCompleted() {
    startDirectChannel.send(new GenericMessage<String>(DIRECT_CHANNEL_MESSAGE));
    assertEquals(DIRECT_CHANNEL_MESSAGE, messageConsumer.getMessageContent());
}
```

消息将在我们的整个流程中成功传播，从startDirectChannel开始到logMessage激活器，然后转到endDirectChannel。这是因为提供的authentication对象具有访问这些组件所需的所有权限。

### 6.3 测试SecurityContext传播

在声明测试用例之前，我们可以使用PublishSubscribeChannel查看示例的整个流程：

-   该流程以具有策略sendAccess = "ROLE_VIEWER"的startPSChannel开始
-   两个ServiceActivator订阅该通道：一个具有安全注解@PreAuthorize("hasRole('ROLE_LOGGER')")，一个具有安全注解@PreAuthorize("hasRole('ROLE_VIEWER')")

因此，首先我们为用户提供角色ROLE_VIEWER并尝试向我们的通道发送消息：

```java
@Test
@WithMockUser(username = "user", roles = { "VIEWER" })
public void givenRoleUser_whenSendMessageToPSChannel_thenNoMessageArrived() throws IllegalStateException, InterruptedException {
    startPSChannel.send(new GenericMessage<String>(DIRECT_CHANNEL_MESSAGE));

    executor
        .getThreadPoolExecutor()
        .awaitTermination(2, TimeUnit.SECONDS);

    assertEquals(1, messageConsumer.getMessagePSContent().size());
    assertTrue(messageConsumer.getMessagePSContent().values().contains("user"));
}
```

**由于我们的用户只有角色ROLE_VIEWER，因此消息只能通过startPSChannel和一个ServiceActivator**。

因此，在流程结束时，我们只收到一条消息。

让我们为用户提供角色ROLE_VIEWER和ROLE_LOGGER：

```java
@Test
@WithMockUser(username = "user", roles = { "LOGGER", "VIEWER" })
public void givenRoleUserAndLogger_whenSendMessageToPSChannel_then2GetMessages() throws IllegalStateException, InterruptedException {
    startPSChannel.send(new GenericMessage<String>(DIRECT_CHANNEL_MESSAGE));

    executor
        .getThreadPoolExecutor()
        .awaitTermination(2, TimeUnit.SECONDS);

    assertEquals(2, messageConsumer.getMessagePSContent().size());
    assertTrue(messageConsumer
        .getMessagePSContent()
        .values().contains("user"));
    assertTrue(messageConsumer
        .getMessagePSContent()
        .values().contains("ROLE_LOGGER,ROLE_VIEWER"));
}
```

现在，我们可以在流程结束时收到这两条消息，因为用户拥有所需的所有必要权限。

## 7. 总结

在本教程中，我们探讨了在Spring Integration中使用Spring Security来保护消息通道和ServiceActivator的可能性。