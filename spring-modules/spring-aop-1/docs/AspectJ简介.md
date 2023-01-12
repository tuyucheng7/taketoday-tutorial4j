## 1. 概述

**本文是对AspectJ的快速介绍**。

首先，我们演示如何启用面向切面编程，然后我们重点关注编译时、编译后和加载时织入之间的区别。

## 2. 简介

AOP是一种编程范式，旨在通过允许分离横切关注点来提高模块化。
它通过在不修改代码本身的情况下向现有代码添加额外的行为来实现这一点。相反，我们单独声明要修改的代码。

AspectJ使用Java编程语言的扩展实现了关注点和横切关注点的织入。

## 3. Maven依赖

AspectJ根据其用途提供不同的库。在本文中，我们重点介绍使用编译时、编译后和加载时织入创建切面和Weaver所需的依赖项。

### 3.1 AspectJ运行时

运行AspectJ程序时，类路径应包含类和切面以及AspectJ运行时库aspectjrt.jar：

```xml

<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjrt</artifactId>
    <version>1.9.7</version>
</dependency>
```

### 3.2 AspectJWeaver

除了AspectJ运行时依赖项之外，我们还需要包含aspectjweaver.jar以在加载时向Java类引入通知：

```xml

<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.9.7</version>
</dependency>
```

## 4. 切面创建

AspectJ提供了AOP的实现，包含三个核心概念：

+ JoinPoint(连接点)
+ PointCut(切入点)
+ Advice(通知)

我们通过创建一个简单的程序验证用户帐户余额来演示这些概念。

首先，我们创建一个Account类，它包含一个余额变量balance和一个withdraw方法：

```java
public class Account {

    int balance = 20;

    public boolean withdraw(int amount) {
        if (balance < amount) {
            return false;
        }
        balance = balance - amount;
        return true;
    }
}
```

然后创建一个AccountAspect.aj文件记录帐户信息并验证帐户余额(注意AspectJ文件以“.aj”文件扩展名结尾)：

```aspectj
public aspect AccountAspect {
    private static final Logger logger = LoggerFactory.getLogger(AccountAspect.class);
    final int MIN_BALANCE = 10;

    pointcut callWithDraw(int amount, Account account):
            call(boolean cn.tuyucheng.taketoday.aspectj.Account.withdraw(int)) && args(amount) && target(account);

    before(int amount, Account account): callWithDraw(amount, account) {
        logger.info("Balance before withdrawal: {}", account.balance);
        logger.info("Withdraw amount: {}", amount);
    }

    boolean around(int amount, Account account): callWithDraw(amount, account) {
        if (account.balance < amount) {
            logger.info("Withdrawal Rejected!");
            return false;
        }
        return proceed(amount, account);
    }

    after(int amount, Account balance): callWithDraw(amount, balance) {
        logger.info("Balance after withdrawal : {}", balance.balance);
    }
}
```

正如我们所见，我们在withdraw方法中添加了一个切入点，并创建了三个引用定义的切入点的通知。

为了便于理解，我们引入以下定义：

+ **Aspect(切面)**：跨多个对象的关注点的模块化，每个切面都侧重于特定的横切功能。
+ **JoinPoint(连接点)**：脚本执行过程中的一个点，例如方法执行或属性访问。
+ **Advice(通知)**：切面在特定连接点采取的操作。
+ **Pointcut(切入点)**：匹配连接点的正则表达式。通知与切入点表达式相关联，并在与切入点匹配的任何连接点处运行。

接下来，我们需要将这些切面织入到我们的代码中。
以下部分介绍三种不同类型的织入：AspectJ中的编译时织入、编译后织入和加载时织入。

## 5. 编译时织入

最简单的织入方法是编译时织入，当我们拥有切面的源代码和我们使用切面的代码时，AspectJ编译器将从源代码编译并生成一个织入类文件作为输出。
之后，在执行你的代码时，织入过程输出类作为普通Java类加载到JVM中。

我们可以下载AspectJ开发工具，因为它包含一个捆绑的AspectJ编译器。
AJDT最重要的特性之一是它是一个可视化横切关注点的工具，有助于调试切入点规范。
甚至在部署代码之前，我们就可以将组合效果可视化。

我们使用Mojo的AspectJ Maven插件将AspectJ切面织入到使用AspectJ编译器的类中。

```xml

<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>aspectj-maven-plugin</artifactId>
    <version>${aspectj-plugin.version}</version>
    <configuration>
        <complianceLevel>1.8</complianceLevel>
        <source>1.8</source>
        <target>1.8</target>
        <showWeaveInfo>true</showWeaveInfo>
        <verbose>true</verbose>
        <Xlint>ignore</Xlint>
        <encoding>UTF-8</encoding>
        <excludes>
            <exclude>**/pointcutadvice/**</exclude>
        </excludes>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

我们为Account类添加一些测试用例：

```java
class AccountUnitTest {

    private Account account;

    @BeforeEach
    void before() {
        account = new Account();
    }

    @Test
    void givenBalance20AndMinBalance10_whenWithdraw5_thenSuccess() {
        assertTrue(account.withdraw(5));
    }

    @Test
    void givenBalance20AndMinBalance10_whenWithdraw100_thenFail() {
        assertFalse(account.withdraw(100));
    }
}
```

当我们运行测试用例时，控制台中输出以下日志则表示我们成功织入了切面源代码：

```text
19:31:54.575 [main] INFO cn.tuyucheng.taketoday.aspectj.AccountAspect - Balance before withdrawal: 20
19:31:54.577 [main] INFO cn.tuyucheng.taketoday.aspectj.AccountAspect - Withdraw amount: 5
19:31:54.577 [main] INFO cn.tuyucheng.taketoday.aspectj.AccountAspect - Balance after withdrawal : 15
19:31:54.587 [main] INFO cn.tuyucheng.taketoday.aspectj.AccountAspect - Balance before withdrawal: 20
19:31:54.588 [main] INFO cn.tuyucheng.taketoday.aspectj.AccountAspect - Withdraw amount: 100
19:31:54.588 [main] INFO cn.tuyucheng.taketoday.aspectj.AccountAspect - Withdrawal Rejected!
19:31:54.588 [main] INFO cn.tuyucheng.taketoday.aspectj.AccountAspect - Balance after withdrawal : 20
```

## 6. 编译后织入

编译后织入(有时也称为二进制织入)用于织入现有的类文件和JAR文件。
与编译时织入一样，用于织入的切面可以是源代码形式或二进制形式，并且本身可以由切面织入。

为了使用Mojo的AspectJ Maven插件来做到这一点，我们需要设置我们想要在插件配置中织入的所有JAR文件：

```xml

<configuration>
    <weaveDependencies>
        <weaveDependency>
            <groupId>org.agroup</groupId>
            <artifactId>to-weave</artifactId>
        </weaveDependency>
        <weaveDependency>
            <groupId>org.anothergroup</groupId>
            <artifactId>gen</artifactId>
        </weaveDependency>
    </weaveDependencies>
</configuration>
```

包含要织入的类的JAR文件必须在Maven项目中指定为<dependencies\>，
并在AspectJ Maven插件的<configuration\>标签中指定为<weaveDependencies\>。

## 7. 加载时织入

加载时织入只是延迟到类加载器加载class文件并将class定义给JVM的二进制织入。

为了支持这一点，需要一个或多个“织入类加载器(weaving class loaders)”。
这些要么由运行时环境显式提供，要么使用“织入代理(weaving agent)”启用。

### 7.1 启用加载时织入

AspectJ加载时织入可以使用AspectJ代理启用，该代理可以参与类加载过程并在VM中定义任何类型之前织入它们。
我们将javaagent参数指定为JVM -javaagent:pathto/aspectjweaver.jar或使用Maven插件来配置javaagent：

```xml

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
    <configuration>
        <argLine>
            -javaagent:"${settings.localRepository}"/org/aspectj/
            aspectjweaver/${aspectj.version}/
            aspectjweaver-${aspectj.version}.jar
        </argLine>
        <useSystemClassLoader>true</useSystemClassLoader>
        <forkMode>always</forkMode>
    </configuration>
</plugin>
```

### 7.2 配置织入器

AspectJ的加载时织入代理是通过使用aop.xml文件来配置的。
它在类路径上的META-INF目录中查找一个或多个aop.xml文件并聚合内容以确定织入器配置。

aop.xml文件包含两个关键部分：

+ **aspects**：定义织入的一个或多个切面，并控制在织入过程中要使用的切面。
  aspect元素可以可选地包含一个或多个include和exclude元素(默认情况下，所有定义的切面都用于织入)。
+ **weaver**：为织入器定义织入器参数并指定应该织入的类型集。如果未指定include元素，则织入器可见的所有类型都将被织入。

让我们为织入器配置一个切面：

```xml

<aspectj>
    <aspects>
        <aspect name="cn.tuyucheng.taketoday.aspectj.AccountAspect"/>
        <weaver options="-verbose -showWeaveInfo">
            <include within="cn.tuyucheng.taketoday.aspectj.*"/>
        </weaver>
    </aspects>
</aspectj>
```

可以看到，我们配置了一个aspect指向AccountAspect，只有cn.tuyucheng.taketoday.aspectj包中的源代码会被AspectJ织入。

## 8. 注解切面

除了基于AspectJ代码的切面声明风格外，AspectJ 5还支持基于注解的切面声明风格。
我们将支持这种开发风格的注解集非正式地称为“@AspectJ”注解。

让我们创建一个注解：

```java

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Secured {
    boolean isLocked() default false;
}
```

我们使用@Secured注解来启用或禁用一个方法：

```java
public class SecuredMethod {
    private static final Logger logger = LoggerFactory.getLogger(SecuredMethod.class);

    @Secured(isLocked = true)
    public void lockedMethod() throws Exception {
        logger.info("lockedMethod");
    }

    @Secured(isLocked = false)
    public void unlockedMethod() {
        logger.info("unlockedMethod");
    }
}
```

接下来，我们使用AspectJ注解风格添加一个切面，并根据@Secured注解的属性检查权限：

```java

@Aspect
public class SecuredMethodAspect {
    private static final Logger logger = LoggerFactory.getLogger(SecuredMethodAspect.class);

    @Pointcut("@annotation(secured)")
    public void callAt(Secured secured) {
    }

    @Around(value = "callAt(secured)", argNames = "pjp,secured")
    public Object around(ProceedingJoinPoint pjp, Secured secured) throws Throwable {
        if (secured.isLocked()) {
            logger.info(pjp.getSignature().toLongString() + " is locked");
            return null;
        } else {
            return pjp.proceed();
        }
    }
}
```

接下来，我们使用加载时织入器织入我们的类和切面，并将aop.xml放在META-INF文件夹下：

```xml

<aspectj>
    <aspects>
        <aspect name="cn.tuyucheng.taketoday.aspectj.SecuredMethodAspect"/>
        <weaver options="-verbose -showWeaveInfo">
            <include within="cn.tuyucheng.taketoday.aspectj.*"/>
        </weaver>
    </aspects>
</aspectj>
```

最后，我们添加单元测试运行并观察结果：

```java
class SecuredMethodUnitTest {

    @Test
    void testMethod() throws Exception {
        SecuredMethod service = new SecuredMethod();
        service.unlockedMethod();
        service.lockedMethod();
    }
}
```

当我们运行测试用例时，我们可以从控制台输出中看出是否成功地在源代码中织入了我们的切面和类：

```text
20:11:55.900 [main] INFO cn.tuyucheng.taketoday.aspectj.SecuredMethod - unlockedMethod
20:11:55.904 [main] INFO cn.tuyucheng.taketoday.aspectj.SecuredMethodAspect - public void cn.tuyucheng.taketoday.aspectj.SecuredMethod.lockedMethod() is locked
```

我们观察到，unlockedMethod方法并没有实际被调用，因为在我们的切面中根据@Secured注解的值，它已经从切面类中返回。

## 9. 总结

在本文中，我们介绍了有关AspectJ的基本概念。