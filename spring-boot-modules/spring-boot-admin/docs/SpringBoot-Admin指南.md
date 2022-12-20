## 1. 概述

[Spring Boot Admin](https://github.com/codecentric/spring-boot-admin)是一个Web应用程序，用于管理和监控Spring Boot应用程序。每个应用程序都被视为客户端并注册到管理服务器。在幕后，魔力是由Spring Boot Actuator端点赋予的。

在本文中，我们将描述配置Spring Boot Admin服务器的步骤以及应用程序如何成为客户端。

## 2. Admin服务器设置

首先，我们需要创建一个简单的Spring Boot Web应用程序并添加以下[Maven依赖](https://search.maven.org/classic/#search|ga|1|spring-boot-admin-starter-server)：

```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-server</artifactId>
    <version>2.4.1</version>
</dependency>
```

在此之后，@EnableAdminServer注解将可用，因此我们将把它添加到Spring Boot主类上，如下例所示：

```java
@EnableAdminServer
@SpringBootApplication(exclude = AdminServerHazelcastAutoConfiguration.class)
public class SpringBootAdminServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAdminServerApplication.class, args);
    }
}
```

此时，我们已准备好启动服务器并注册客户端应用程序。

## 3. 设置客户端

现在，在我们设置Admin服务器之后，我们可以将我们的第一个Spring Boot应用程序注册为客户端。客户端程序必须添加以下[Maven依赖项](https://search.maven.org/classic/#search|ga|1|spring-boot-admin-starter-client)：

```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-client</artifactId>
    <version>2.4.1</version>
</dependency>
```

接下来，我们需要配置客户端以了解Admin服务器的基本URL。为此，我们只需添加以下属性：

```properties
spring.boot.admin.client.url=http://localhost:8080
```

**从Spring Boot 2开始，默认情况下不会公开除health和info之外的端点**。

我们可以通过以下配置公开所有端点：

```properties
management.endpoints.web.exposure.include=
management.endpoint.health.show-details=always
```

## 4. 安全配置

**Spring Boot Admin服务器可以访问应用程序的敏感端点，因此建议我们为Admin和客户端应用程序添加一些安全配置**。

首先，我们重点介绍如何配置Admin服务器的安全性，首先必须添加以下[Maven依赖](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-security")：

```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-server-ui-login</artifactId>
    <version>1.5.7</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
    <version>2.4.0</version>
</dependency>
```

这将启用安全性并向Admin应用程序添加登录界面。

接下来，我们添加一个安全配置类，如下所示：

```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final AdminServerProperties adminServer;

    public WebSecurityConfig(AdminServerProperties adminServer) {
        this.adminServer = adminServer;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(this.adminServer.getContextPath() + "/");

        http.authorizeRequests()
                .antMatchers(this.adminServer.getContextPath() + "/assets/").permitAll()
                .antMatchers(this.adminServer.getContextPath() + "/login").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage(this.adminServer.getContextPath() + "/login")
                .successHandler(successHandler)
                .and()
                .logout()
                .logoutUrl(this.adminServer.getContextPath() + "/logout")
                .and()
                .httpBasic()
                .and()
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers(
                        new AntPathRequestMatcher(this.adminServer.getContextPath() +
                                "/instances", HttpMethod.POST.toString()),
                        new AntPathRequestMatcher(this.adminServer.getContextPath() +
                                "/instances/", HttpMethod.DELETE.toString()),
                        new AntPathRequestMatcher(this.adminServer.getContextPath() + "/actuator/"))
                .and()
                .rememberMe()
                .key(UUID.randomUUID().toString())
                .tokenValiditySeconds(1209600);
        return http.build();
    }
}
```

有一个简单的安全配置，但在添加它后，我们会注意到客户端无法再注册到服务器。

为了将客户端注册到新的安全服务器，我们必须在客户端的属性文件中添加更多配置：

```properties
spring.boot.admin.client.username=admin
spring.boot.admin.client.password=admin
```

我们正处于保护Admin服务器的位置。在生产系统中，我们试图监控的应用程序自然会受到保护。因此，我们也会为客户端添加安全性，我们会在管理服务器的UI界面中注意到客户端信息不再可用。

我们必须添加一些我们将发送到Admin服务器的元数据，服务器使用此信息连接到客户端的端点：

```properties
spring.security.user.name=client
spring.security.user.password=client
spring.boot.admin.client.instance.metadata.user.name=${spring.security.user.name}
spring.boot.admin.client.instance.metadata.user.password=${spring.security.user.password}
```

通过HTTP发送凭据当然不安全，因此通信需要通过HTTPS。

## 5. 监控和管理功能

可以将Spring Boot Admin配置为仅显示我们认为有用的信息，我们只需要更改默认配置并添加我们自己需要的指标：

```properties
spring.boot.admin.routes.endpoints=env, metrics, trace, jolokia, info, configprops
```

随着我们的深入，我们将看到还有一些其他功能可以探索。我们正在谈论使用Jolokia的JMX bean管理以及Loglevel管理。

Spring Boot Admin还支持使用Hazelcast进行集群，我们只需要添加以下[Maven依赖](https://search.maven.org/classic/#search|ga|1|g%3A"com.hazelcast" AND a%3A"hazelcast")，让自动配置完成剩下的工作：

```xml
<dependency>
    <groupId>com.hazelcast</groupId>
    <artifactId>hazelcast</artifactId>
    <version>4.0.3</version>
</dependency>
```

如果我们想要一个持久实例的Hazelcast，我们将使用自定义配置：

```java
@Configuration
public class HazelcastConfig {

    @Bean
    public Config hazelcast() {
        MapConfig eventStoreMap = new MapConfig("spring-boot-admin-event-store")
                .setInMemoryFormat(InMemoryFormat.OBJECT)
                .setBackupCount(1)
                .setEvictionConfig(new EvictionConfig().setEvictionPolicy(EvictionPolicy.NONE))
                .setMergePolicyConfig(new MergePolicyConfig(PutIfAbsentMergePolicy.class.getName(), 100));

        MapConfig sentNotificationsMap = new MapConfig("spring-boot-admin-application-store")
                .setInMemoryFormat(InMemoryFormat.OBJECT)
                .setBackupCount(1)
                .setEvictionConfig(new EvictionConfig().setEvictionPolicy(EvictionPolicy.LRU))
                .setMergePolicyConfig(new MergePolicyConfig(PutIfAbsentMergePolicy.class.getName(), 100));

        Config config = new Config();
        config.addMapConfig(eventStoreMap);
        config.addMapConfig(sentNotificationsMap);
        config.setProperty("hazelcast.jmx", "true");

        config.getNetworkConfig()
                .getJoin()
                .getMulticastConfig()
                .setEnabled(false);
        TcpIpConfig tcpIpConfig = config.getNetworkConfig()
                .getJoin()
                .getTcpIpConfig();
        tcpIpConfig.setEnabled(true);
        tcpIpConfig.setMembers(Collections.singletonList("127.0.0.1"));
        return config;
    }
}
```

## 6. 通知

接下来，让我们讨论在我们的注册客户端发生问题时从Admin服务器接收通知的可能性。以下通知程序可用于配置：

-   Email
-   PagerDuty
-   OpsGenie
-   Hipchat
-   Slack
-   Let's Chat

### 6.1 电子邮件通知

我们将首先关注为我们的Admin服务器配置邮件通知。为此，我们必须添加[mail starter依赖](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-mail")，如下所示：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
    <version>2.4.0</version>
</dependency>
```

在此之后，我们必须添加一些邮件配置：

```properties
spring.mail.host=smtp.example.com
spring.mail.username=smtp_user
spring.mail.password=smtp_password
spring.boot.admin.notify.mail.to=admin@example.com
```

现在，每当我们的注册客户将其状态从UP更改为OFFLINE或其他状态时，都会向上面配置的地址发送一封电子邮件。对于其他通知程序，配置类似。

### 6.2 Hipchat通知

正如我们将看到的，与Hipchat的集成非常简单；只有几个必须设置的属性：

```properties
spring.boot.admin.notify.hipchat.auth-token=<generated_token>
spring.boot.admin.notify.hipchat.room-id=<room-id>
spring.boot.admin.notify.hipchat.url=https://yourcompany.hipchat.com/v2/
```

定义好这些后，我们会在Hipchat房间中注意到，只要客户端状态发生变化，我们就会收到通知。

### 6.3 自定义通知配置

我们可以配置一个自定义通知系统，为此我们可以使用一些强大的工具。我们可以使用提醒通知程序发送预定通知，直到客户端状态发生变化。

或者，也许我们想向一组经过过滤的客户端发送通知。为此，我们可以使用过滤通知程序：

```java
@Configuration
public class NotifierConfiguration {
    private final InstanceRepository repository;
    private final ObjectProvider<List<Notifier>> otherNotifiers;

    public NotifierConfiguration(InstanceRepository repository, ObjectProvider<List<Notifier>> otherNotifiers) {
        this.repository = repository;
        this.otherNotifiers = otherNotifiers;
    }

    @Bean
    public FilteringNotifier filteringNotifier() {
        CompositeNotifier delegate = new CompositeNotifier(this.otherNotifiers.getIfAvailable(Collections::emptyList));
        return new FilteringNotifier(delegate, this.repository);
    }

    @Bean
    public LoggingNotifier notifier() {
        return new LoggingNotifier(repository);
    }

    @Primary
    @Bean(initMethod = "start", destroyMethod = "stop")
    public RemindingNotifier remindingNotifier() {
        RemindingNotifier remindingNotifier = new RemindingNotifier(filteringNotifier(), repository);
        remindingNotifier.setReminderPeriod(Duration.ofMinutes(5));
        remindingNotifier.setCheckReminderInverval(Duration.ofSeconds(60));
        return remindingNotifier;
    }
}
```

## 7. 总结

本介绍教程涵盖了使用Spring Boot Admin监控和管理其Spring Boot应用程序所必须执行的简单步骤。自动配置允许我们仅添加一些次要配置，并在最后拥有一个功能齐全的管理服务器。