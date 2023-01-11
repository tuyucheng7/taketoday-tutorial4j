## 1. 简介

在本教程中，我们将重点关注使用@Async传播 Spring Security 主体。


默认情况下，Spring Security Authentication 绑定到ThreadLocal - 因此，当执行流在带有 @Async 的新线程中运行时，这不会是经过身份验证的上下文。

这并不理想——让我们解决它。

## 2.Maven依赖

为了在 Spring Security 中使用异步集成，我们需要在pom.xml的依赖项中包含以下部分：

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
    <version>5.7.3</version>
</dependency>

```

可以在[此处](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.security")找到最新版本的 Spring Security 依赖项。

## 3. 使用@Async 的Spring Security 传播

我们先写一个简单的例子：

```java
@RequestMapping(method = RequestMethod.GET, value = "/async")
@ResponseBody
public Object standardProcessing() throws Exception {
    log.info("Outside the @Async logic - before the async call: "
      + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    
    asyncService.asyncCall();
    
    log.info("Inside the @Async logic - after the async call: "
      + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    
    return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
}
```

我们要检查 Spring SecurityContext是否传播到新线程。首先，我们在异步调用之前记录上下文，接下来我们运行异步方法，最后我们再次记录上下文。asyncCall ()方法具有以下实现：

```java
@Async
@Override
public void asyncCall() {
    log.info("Inside the @Async logic: "
      + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
}
```

正如我们所看到的，只有一行代码会在异步方法的新线程中输出上下文。

## 4. 默认配置

默认情况下，@Async方法内的安全上下文将具有空值。

特别是，如果我们将运行异步逻辑，我们将能够在主程序中记录Authentication对象，但是当我们将其记录在@Async中时，它将为null。这是一个示例日志输出：

```plaintext
web - 2016-12-30 22:41:58,916 [http-nio-8081-exec-3] INFO
  o.baeldung.web.service.AsyncService -
  Outside the @Async logic - before the async call:
  org.springframework.security.core.userdetails.User@76507e51:
  Username: temporary; ...

web - 2016-12-30 22:41:58,921 [http-nio-8081-exec-3] INFO
  o.baeldung.web.service.AsyncService -
  Inside the @Async logic - after the async call:
  org.springframework.security.core.userdetails.User@76507e51:
  Username: temporary; ...

  web - 2016-12-30 22:41:58,926 [SimpleAsyncTaskExecutor-1] ERROR
  o.s.a.i.SimpleAsyncUncaughtExceptionHandler -
  Unexpected error occurred invoking async method
  'public void com.baeldung.web.service.AsyncServiceImpl.asyncCall()'.
  java.lang.NullPointerException: null
```

因此，如所见，在执行程序线程中，我们的调用失败并出现 NPE，正如预期的那样——因为 Principal 在那里不可用。

## 5. 异步安全上下文配置

如果我们想访问异步线程内部的主体，就像我们可以在外部访问它一样，我们需要创建DelegatingSecurityContextAsyncTaskExecutor bean：

```java
@Bean 
public DelegatingSecurityContextAsyncTaskExecutor taskExecutor(ThreadPoolTaskExecutor delegate) { 
    return new DelegatingSecurityContextAsyncTaskExecutor(delegate); 
}
```

通过这样做，Spring 将在每个@Async调用中使用当前的SecurityContext 。

现在，让我们再次运行应用程序并查看日志信息以确保情况确实如此：

```plaintext
web - 2016-12-30 22:45:18,013 [http-nio-8081-exec-3] INFO
  o.baeldung.web.service.AsyncService -
  Outside the @Async logic - before the async call:
  org.springframework.security.core.userdetails.User@76507e51:
  Username: temporary; ...

web - 2016-12-30 22:45:18,018 [http-nio-8081-exec-3] INFO
  o.baeldung.web.service.AsyncService -
  Inside the @Async logic - after the async call:
  org.springframework.security.core.userdetails.User@76507e51:
  Username: temporary; ...

web - 2016-12-30 22:45:18,019 [SimpleAsyncTaskExecutor-1] INFO
  o.baeldung.web.service.AsyncService -
  Inside the @Async logic:
  org.springframework.security.core.userdetails.User@76507e51:
  Username: temporary; ...
```

我们在这里——正如我们预期的那样，我们在异步执行器线程中看到了相同的主体。


## 6.用例

在一些有趣的用例中，我们可能希望确保SecurityContext像这样传播：

-   我们想要发出多个可以并行运行并且可能需要大量时间来执行的外部请求
-   我们需要在本地进行一些重要的处理，并且我们的外部请求可以并行执行
-   其他代表即发即弃的场景，例如发送电子邮件

## 7. 总结

在这个快速教程中，我们展示了 Spring 对使用传播的SecurityContext发送异步请求的支持。从编程模型的角度来看，新功能看似简单。

请注意，如果多个方法调用之前以同步方式链接在一起，则转换为异步方法可能需要同步结果。