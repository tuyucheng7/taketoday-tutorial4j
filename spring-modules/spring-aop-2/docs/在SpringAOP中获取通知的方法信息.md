## 1. 概述

在本教程中，我们演示如何使用Spring AOP切面获取有关方法签名、参数和注解的所有信息。

## 2. Maven依赖

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

## 3. 创建切入点注解

我们创建一个@AccountOperation注解，使用它作为切面的切入点：

```java

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccountOperation {
    String operation();
}
```

**请注意，创建注解对于定义切入点不是必需的**。
也就是说，我们可以使用Spring AOP提供的切入点定义语言来定义其他切入点类型，比如类中的某些方法、以某个前缀开头的方法等。

## 4. 创建相关类

### 4.1 Account

让我们创建一个包含accountNumber和balance属性的Account POJO，我们会在Service方法中使用它作为方法参数：

```java
public class Account {

    private String accountNumber;
    private double balance;

    // getters、setters、toString ...
}
```

### 4.2 Service

现在我们创建包含两个方法的BankAccountService类，并使用@AccountOperation注解进行标注，这样就可以在我们的切面中获取这些方法的信息。
请注意，withdraw方法抛出一个检查异常WithdrawLimitException，用来演示如何获取方法抛出的异常信息。

另外，注意getBalance方法没有声明@AccountOperation注解，因此它不会被切面拦截：

```java

@Component
public class BankAccountService {

    @AccountOperation(operation = "deposit")
    public void deposit(Account account, Double amount) {
        account.setBalance(account.getBalance() + amount);
    }

    @AccountOperation(operation = "withdraw")
    public void withdraw(Account account, Double amount) throws WithdrawLimitException {
        if (amount > 500.0) {
            throw new WithdrawLimitException("Withdraw limit exceeded.");
        }

        account.setBalance(account.getBalance() - amount);
    }

    public double getBalance() {
        return RandomUtils.nextDouble();
    }
}

public class WithdrawLimitException extends RuntimeException {

    public WithdrawLimitException(String message) {
        super(message);
    }
}
```

## 5. 定义切面

让我们创建一个BankAccountAspect，从BankAccountService中调用的相关方法获取所有必要信息：

```java

@Aspect
@Component
public class BankAccountAspect {

    @Before(value = "@annotation(cn.tuyucheng.taketoday.method.info.AccountOperation)")
    public void getAccountOperationInfo(JoinPoint joinPoint) {

        // Method Information
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        System.out.println("full method description: " + signature.getMethod());
        System.out.println("method name: " + signature.getMethod().getName());
        System.out.println("declaring type: " + signature.getDeclaringType());

        // Method args
        System.out.println("Method args names:");
        Arrays.stream(signature.getParameterNames()).forEach(s -> System.out.println("arg name: " + s));

        System.out.println("Method args types:");
        Arrays.stream(signature.getParameterTypes()).forEach(s -> System.out.println("arg type: " + s));

        System.out.println("Method args values:");
        Arrays.stream(joinPoint.getArgs()).forEach(o -> System.out.println("arg value: " + o.toString()));

        // Additional Information
        System.out.println("returning type: " + signature.getReturnType());
        System.out.println("method modifier: " + Modifier.toString(signature.getModifiers()));
        Arrays.stream(signature.getExceptionTypes()).forEach(aClass -> System.out.println("exception type: " + aClass));

        // Method annotation
        Method method = signature.getMethod();
        AccountOperation accountOperation = method.getAnnotation(AccountOperation.class);
        System.out.println("Account operation annotation: " + accountOperation);
        System.out.println("Account operation value: " + accountOperation.operation());
    }
}
```

我们将切入点定义为注解，因此，由于BankAccountService中的getBalance方法没有使用@AccountOperation进行标注，切面不会拦截它。

### 5.1 获取有关方法签名的信息

为了能够获取目标方法的签名信息，我们需要从JoinPoint对象中检索MethodSignature：

```text
MethodSignature signature = (MethodSignature) joinPoint.getSignature();

System.out.println("full method description: " + signature.getMethod());
System.out.println("method name: " + signature.getMethod().getName());
System.out.println("declaring type: " + signature.getDeclaringType());
```

现在我们调用Service的withdraw()方法：

```java

@SpringBootTest
class BankAccountServiceIntegrationTest {

    @Autowired
    BankAccountService bankAccountService;
    private Account account;

    @BeforeEach
    public void setup() {
        account = new Account();
        account.setAccountNumber("12345");
        account.setBalance(2000.0);
    }

    @Test
    void withdraw() {
        bankAccountService.withdraw(account, 500.0);
        assertEquals(1500.0, account.getBalance());
    }
}
```

运行withdraw()测试方法后，我们可以在控制台中看到以下输出：

```text
full method description: public void cn.tuyucheng.taketoday.method.info.BankAccountService.withdraw
(cn.tuyucheng.taketoday.method.info.Account,java.lang.Double) throws cn.tuyucheng.taketoday.method.info.WithdrawLimitException
method name: withdraw
declaring type: class cn.tuyucheng.taketoday.method.info.BankAccountService
```

### 5.2 获取关于参数的信息

要检索有关方法参数的信息，我们可以使用MethodSignature对象：

```text
System.out.println("Method args names:");
Arrays.stream(signature.getParameterNames()).forEach(s -> System.out.println("arg name: " + s));

System.out.println("Method args types:");
Arrays.stream(signature.getParameterTypes()).forEach(s -> System.out.println("arg type: " + s));

System.out.println("Method args values:");
Arrays.stream(joinPoint.getArgs()).forEach(o -> System.out.println("arg value: " + o.toString()));
```

```java
class BankAccountServiceIntegrationTest {

    @Test
    void deposit() {
        bankAccountService.deposit(account, 500.0);
        assertEquals(2500.0, account.getBalance());
    }
}
```

这是我们在控制台上能够看到的：

```text
Method args names:
arg name: account
arg name: amount
Method args types:
arg type: class cn.tuyucheng.taketoday.method.info.Account
arg type: class java.lang.Double
Method args values:
arg value: Account{accountNumber='12345', balance=2000.0}
arg value: 500.0
```

### 5.3 获取有关方法注解的信息

我们可以通过Method类的getAnnotation()方法获取关于注解的信息：

```text
Method method = signature.getMethod();
AccountOperation accountOperation = method.getAnnotation(AccountOperation.class);
System.out.println("Account operation annotation: " + accountOperation);
System.out.println("Account operation value: " + accountOperation.operation());
```

现在我们重新运行之前的withdraw()测试并观察输出：

```text
Account operation annotation: @cn.tuyucheng.taketoday.method.info.AccountOperation(operation="withdraw")
Account operation value: withdraw
```

### 5.4 获取其它信息

我们也可以获取一些关于方法的额外信息，比如它们的返回类型、它们的修饰符以及它们抛出的异常(如果有的话)：

```text
System.out.println("returning type: " + signature.getReturnType());
System.out.println("method modifier: " + Modifier.toString(signature.getModifiers()));
Arrays.stream(signature.getExceptionTypes()).forEach(aClass -> System.out.println("exception type: " + aClass));
```

现在我们编写一个新的测试方法withdrawWhenLimitReached，使withdraw()方法超过其定义的取款限制：

```java
class BankAccountServiceIntegrationTest {

    @Test
    void withdrawWhenLimitReached() {
        assertThatExceptionOfType(WithdrawLimitException.class)
                .isThrownBy(() -> bankAccountService.withdraw(account, 600.0));
        assertEquals(2000.0, account.getBalance());
    }
}
```

然后观察控制台输出：

```text
returning type: void
method modifier: public
exception type: class cn.tuyucheng.taketoday.method.info.WithdrawLimitException
```

最后一个测试演示getBalance()方法。
正如我们之前所说，它不会被切面拦截，因为方法声明中没有指定@AccountOperation注解：

```java
class BankAccountServiceIntegrationTest {

    @Test
    void getBalance() {
        bankAccountService.getBalance();
    }
}
```

运行此测试时，控制台中没有任何输出。

## 6. 总结

在本文中，我们演示了如何使用Spring AOP切面获取有关方法的所有可用信息。