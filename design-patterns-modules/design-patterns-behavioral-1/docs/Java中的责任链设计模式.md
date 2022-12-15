## 1. 简介

在本文中，我们介绍一种广泛使用的行为型设计模式：责任链。

## 2. 责任链

[维基百科](https://en.wikipedia.org/wiki/Chain-of-responsibility_pattern)将责任链定义为由“命令对象的来源和一系列处理对象”组成的设计模式。

链中的每个处理对象负责某一类型的命令，处理完成后，它将命令转发给链中的下一个处理器。

责任链模式非常适用于：

-   **解耦命令的发送方和接收方**
-   **在处理时选择处理策略**

## 3. 示例

我们将使用责任链来创建用于处理身份验证请求的链。因此，输入身份验证提供程序将是命令，每个身份验证处理器将是一个单独的处理器对象。

首先为我们的处理器创建一个抽象基类：

```java
public abstract class AuthenticationProcessor {

    public AuthenticationProcessor nextProcessor;

    // standard constructors

    public abstract boolean isAuthorized(AuthenticationProvider authProvider);
}
```

接下来，我们创建扩展AuthenticationProcessor的具体处理器：

```java
public class OAuthProcessor extends AuthenticationProcessor {

    public OAuthProcessor(AuthenticationProcessor nextProcessor) {
        super(nextProcessor);
    }

    @Override
    public boolean isAuthorized(AuthenticationProvider authProvider) {
        if (authProvider instanceof OAuthTokenProvider) {
            return true;
        } else if (nextProcessor != null) {
            return nextProcessor.isAuthorized(authProvider);
        }

        return false;
    }
}
```

```java
public class UsernamePasswordProcessor extends AuthenticationProcessor {

    public UsernamePasswordProcessor(AuthenticationProcessor nextProcessor) {
        super(nextProcessor);
    }

    @Override
    public boolean isAuthorized(AuthenticationProvider authProvider) {
        if (authProvider instanceof UsernamePasswordProvider) {
            return true;
        } else if (nextProcessor != null) {
            return nextProcessor.isAuthorized(authProvider);
        }
        return false;
    }
}
```

在这里，我们为传入的授权请求创建了两个具体的处理器：UsernamePasswordProcessor和OAuthProcessor。对于每一个，我们都重写了isAuthorized方法。

接下来我们编写几个测试：

```java
class ChainOfResponsibilityTest {

    private static AuthenticationProcessor getChainOfAuthProcessor() {
        AuthenticationProcessor oAuthProcessor = new OAuthProcessor(null);
        return new UsernamePasswordProcessor(oAuthProcessor);
    }

    @Test
    void givenOAuthProvider_whenCheckingAuthorized_thenSuccess() {
        AuthenticationProcessor authProcessorChain = getChainOfAuthProcessor();

        assertTrue(authProcessorChain.isAuthorized(new OAuthTokenProvider()));
    }

    @Test
    void givenSamlProvider_whenCheckingAuthorized_thenSuccess() {
        AuthenticationProcessor authProcessorChain = getChainOfAuthProcessor();

        assertFalse(authProcessorChain.isAuthorized(new SamlTokenProvider()));
    }
}
```

上面的示例创建了一个身份验证处理器链：UsernamePasswordProcessor -> OAuthProcessor。在第一个测试中，授权成功，而在另一个测试中，授权失败。

首先，UsernamePasswordProcessor检查身份验证提供程序是否是UsernamePasswordProvider的实例。

UsernamePasswordProcessor不是预期的输入，而是委托给OAuthProcessor。

最后，OAuthProcessor处理命令。在第一个测试中，有一个匹配并且测试通过。在第二种情况下，链中没有更多的处理器，因此测试失败。

## 4. 实现原则

在实现责任链时，我们需要牢记一些重要原则：

-   **链中的每个处理器都有其用于处理命令的实现**
    -   在我们上面的示例中，所有处理器都有其isAuthorized的实现
-   **链中的每个处理器都应该引用下一个处理器**
    -   上面，UsernamePasswordProcessor委托给OAuthProcessor
-   **每个处理器负责委托给下一个处理器，因此请注意丢弃的命令**
    -   同样在我们的示例中，如果命令是SamlProvider的实例，则请求可能不会得到处理并且将是未经授权的
-   **处理器不应形成递归循环**
    -   在我们的示例中，我们的链中没有循环：UsernamePasswordProcessor -> OAuthProcessor。但是，如果我们明确地将UsernamePasswordProcessor设置为OAuthProcessor的下一个处理器，那么我们最终会在链中形成一个循环：UsernamePasswordProcessor -> OAuthProcessor -> UsernamePasswordProcessor。在构造函数中使用下一个处理器可以帮助解决这个问题
-   **链中只有一个处理器处理给定的命令**
    -   在我们的示例中，如果传入命令包含OAuthTokenProvider的实例，则只有OAuthProcessor会处理该命令

## 5. 真实用例

在Java世界中，我们每天都受益于责任链。**一个典型的例子是Java中的Servlet过滤器**，它允许多个过滤器处理一个HTTP请求，尽管在这种情况下，**每个过滤器都会调用链而不是下一个过滤器**。

让我们看一下下面的代码片段，以便更好地理解Servlet过滤器中的这种模式：

```java
public class CustomFilter implements Filter {

    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        // process the request

        // pass the request (i.e. the command) along the filter chain
        chain.doFilter(request, response);
    }
}
```

如上面的代码片段所示，我们需要调用FilterChain的doFilter方法，以便将请求传递给链中的下一个处理器。

## 6. 缺点

-   大多数情况下，它很容易被破坏：
    -   如果一个处理器无法调用下一个处理器，则该命令将被丢弃
    -   如果一个处理器调用了错误的处理器，则可能导致循环
-   它可能创建深层堆栈跟踪，这会影响性能
-   它可能导致处理器之间的重复代码，从而增加维护

## 7. 总结

在本文中，我们介绍了责任链设计模式的优缺点，并演示了一个身份验证请求流的实际示例。