## 1. 概述

在本快速教程中，我们将探索jcabi-aspectsJava库，这是一组方便的注解，使用面向方面的编程 (AOP) 修改Java应用程序的行为。

jcabi-aspects库提供了诸如@Async、@Loggable和@RetryOnFailure 之类的注解，它们对于使用 AOP 高效地执行某些操作很有用。同时，它们有助于减少我们应用程序中样板代码的数量。该库需要[AspectJ](https://www.baeldung.com/aspectj)将方面编织到已编译的类中。

## 2.设置

首先，我们将最新的[jcabi-aspects](https://search.maven.org/search?q=g:com.jcabi a:jcabi-aspects) Maven 依赖项添加到pom.xml中：

```xml
<dependency>
    <groupId>com.jcabi</groupId>
    <artifactId>jcabi-aspects</artifactId>
    <version>0.22.6</version>
</dependency>

```

jcabi -aspects库需要 AspectJ 运行时支持才能运行。因此，让我们添加[aspectjrt](https://search.maven.org/search?q=g:org.aspectj a:aspectjrt) Maven 依赖：

```xml
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjrt</artifactId>
    <version>1.9.2</version>
    <scope>runtime</scope>
</dependency>
```

接下来，让我们添加[jcabi-maven-plugin](https://search.maven.org/search?q=g:com.jcabi a:jcabi-maven-plugin)插件，该插件在编译时将二进制文件与 AspectJ 方面编织在一起。该插件提供了执行自动编织的ajc目标：

```xml
<plugin>
    <groupId>com.jcabi</groupId>
    <artifactId>jcabi-maven-plugin</artifactId>
    <version>0.14.1</version>
    <executions>
        <execution>
            <goals>
                <goal>ajc</goal>
            </goals>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjtools</artifactId>
            <version>1.9.2</version>
        </dependency>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.9.2</version>
        </dependency>
    </dependencies>
</plugin>
```

最后，让我们使用 Maven 命令编译类：

```shell
mvn clean package
```

jcabi-maven-plugin在编译时生成的日志如下所示：

```shell
[INFO] --- jcabi-maven-plugin:0.14.1:ajc (default) @ jcabi ---
[INFO] jcabi-aspects 0.18/55a5c13 started new daemon thread jcabi-loggable for watching of 
  @Loggable annotated methods
[INFO] Unwoven classes will be copied to /jcabi/target/unwoven
[INFO] Created temp dir /jcabi/target/jcabi-ajc
[INFO] jcabi-aspects 0.18/55a5c13 started new daemon thread jcabi-cacheable for automated
  cleaning of expired @Cacheable values
[INFO] ajc result: 11 file(s) processed, 0 pointcut(s) woven, 0 error(s), 0 warning(s)
```

现在我们知道如何将库添加到我们的项目中，让我们看看它的注解是否在起作用。

## 3. @异步

@Async注解允许[异步](https://aspects.jcabi.com/apidocs-0.22.6/com/jcabi/aspects/Async.html)执行方法。但是，它仅与返回void或[Future](https://www.baeldung.com/java-future)类型的方法兼容。

让我们编写一个异步显示数字阶乘的displayFactorial方法：

```java
@Async
public static void displayFactorial(int number) {
    long result = factorial(number);
    System.out.println(result);
}

```

然后，我们将重新编译该类，让 Maven 为@Async注解编织切面。最后，我们可以运行我们的示例：

```shell
[main] INFO com.jcabi.aspects.aj.NamedThreads - 
jcabi-aspects 0.22.6/3f0a1f7 started new daemon thread jcabi-async for Asynchronous method execution
```

从日志中我们可以看出，该库创建了一个单独的守护线程jcabi-async来执行所有异步操作。

现在，让我们使用@Async注解返回一个Future实例：

```java
@Async
public static Future<Long> getFactorial(int number) {
    Future<Long> factorialFuture = CompletableFuture.supplyAsync(() -> factorial(number));
    return factorialFuture;
}
```

如果我们在不返回void或Future的方法上使用@Async ，则在调用它时将在运行时抛出异常。

## 4. @可缓存

@Cacheable[注解](https://aspects.jcabi.com/apidocs-0.22.6/com/jcabi/aspects/Cacheable.html)允许缓存方法的结果以避免重复计算。

例如，让我们编写一个返回最新汇率的cacheExchangeRates方法：

```java
@Cacheable(lifetime = 2, unit = TimeUnit.SECONDS)
public static String cacheExchangeRates() {
    String result = null;
    try {
        URL exchangeRateUrl = new URL("https://api.exchangeratesapi.io/latest");
        URLConnection con = exchangeRateUrl.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        result = in.readLine();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return result;
}
```

在这里，缓存结果的生命周期为 2 秒。同样，我们可以使用以下方法使结果永远可缓存：

```java
@Cacheable(forever = true)
```

一旦我们重新编译类并再次执行它，库将记录处理缓存机制的两个守护线程的详细信息：

```shell
[main] INFO com.jcabi.aspects.aj.NamedThreads - 
jcabi-aspects 0.22.6/3f0a1f7 started new daemon thread jcabi-cacheable-clean for automated 
  cleaning of expired @Cacheable values
[main] INFO com.jcabi.aspects.aj.NamedThreads - 
jcabi-aspects 0.22.6/3f0a1f7 started new daemon thread jcabi-cacheable-update for async 
  update of expired @Cacheable values
```

当我们调用我们的cacheExchangeRates方法时，该库将缓存结果并记录执行的详细信息：

```shell
[main] INFO com.baeldung.jcabi.JcabiAspectJ - #cacheExchangeRates(): 
'{"rates":{"CAD":1.458,"HKD":8.5039,"ISK":137.9,"P..364..:4.5425},"base":"EUR","date":"2020-02-10"}'
  cached in 560ms, valid for 2s
```

因此，如果再次调用(2 秒内)，cacheExchangeRates 将从缓存中返回结果：

```shell
[main] INFO com.baeldung.jcabi.JcabiAspectJ - #cacheExchangeRates(): 
'{"rates":{"CAD":1.458,"HKD":8.5039,"ISK":137.9,"P..364..:4.5425},"base":"EUR","date":"2020-02-10"}'
  from cache (hit #1, 563ms old)
```

如果该方法抛出异常，则不会缓存结果。

## 5. @可登录

该库使用 SLF4J 日志记录工具为简单日志记录提供[@Loggable注解。](https://aspects.jcabi.com/apidocs-0.22.6/com/jcabi/aspects/Loggable.html)

让我们将@Loggable注解添加到我们的displayFactorial和 cacheExchangeRates方法中：

```java
@Loggable
@Async
public static void displayFactorial(int number) {
    ...
}

@Loggable
@Cacheable(lifetime = 2, unit = TimeUnit.SECONDS)
public static String cacheExchangeRates() {
    ...
}
```

然后，在重新编译之后，注解将记录方法名称、返回值和执行时间：

```shell
[main] INFO com.baeldung.jcabi.JcabiAspectJ - #displayFactorial(): in 1.16ms
[main] INFO com.baeldung.jcabi.JcabiAspectJ - #cacheExchangeRates(): 
'{"rates":{"CAD":1.458,"HKD":8.5039,"ISK":137.9,"P..364..:4.5425},"base":"EUR","date":"2020-02-10"}'
  in 556.92ms
```

## 6. @日志异常

与@Loggable类似，我们可以使用[@LogExceptions](https://aspects.jcabi.com/apidocs-0.22.6/com/jcabi/aspects/LogExceptions.html)注解来仅记录方法抛出的异常。

让我们在将抛出ArithmeticException 的方法divideByZero上使用@LogExceptions：

```java
@LogExceptions
public static void divideByZero() {
    int x = 1/0;
}
```

该方法的执行将记录异常并抛出异常：

```shell
[main] WARN com.baeldung.jcabi.JcabiAspectJ - java.lang.ArithmeticException: / by zero
    at com.baeldung.jcabi.JcabiAspectJ.divideByZero_aroundBody12(JcabiAspectJ.java:77)

java.lang.ArithmeticException: / by zero
    at com.baeldung.jcabi.JcabiAspectJ.divideByZero_aroundBody12(JcabiAspectJ.java:77)
    ...
```

## 7. @安静地

@Quietly注解类似于[@LogExceptions](https://aspects.jcabi.com/apidocs-0.22.6/com/jcabi/aspects/Quietly.html) ，只是它不传播该方法抛出的任何异常。相反，它只是记录它们。

让我们将@Quietly注解添加到我们的divideByZero方法中：

```java
@Quietly
public static void divideByZero() {
    int x = 1/0;
}
```

因此，注解将吞下异常并仅记录否则会抛出的异常的详细信息：

```shell
[main] WARN com.baeldung.jcabi.JcabiAspectJ - java.lang.ArithmeticException: / by zero
    at com.baeldung.jcabi.JcabiAspectJ.divideByZero_aroundBody12(JcabiAspectJ.java:77)
```

@Quietly注解仅与具有void返回类型的方法兼容。

## 8. @重试失败

@RetryOnFailure注解允许我们在发生异常或失败时重复执行方法[。](https://aspects.jcabi.com/apidocs-0.22.6/com/jcabi/aspects/RetryOnFailure.html)


例如，让我们将@RetryOnFailure注解添加到我们的divideByZero方法中：

```java
@RetryOnFailure(attempts = 2)
@Quietly
public static void divideByZero() {
    int x = 1/0;
}
```

因此，如果该方法抛出异常，AOP 建议将尝试执行它两次：

```shell
[main] WARN com.baeldung.jcabi.JcabiAspectJ - 
#divideByZero(): attempt #1 of 2 failed in 147µs with java.lang.ArithmeticException: / by zero
[main] WARN com.baeldung.jcabi.JcabiAspectJ - 
#divideByZero(): attempt #2 of 2 failed in 110µs with java.lang.ArithmeticException: / by zero
```

此外，我们可以定义其他参数，如delay、unit和types，同时声明@RetryOnFailure注解：

```java
@RetryOnFailure(attempts = 3, delay = 5, unit = TimeUnit.SECONDS, 
  types = {java.lang.NumberFormatException.class})
```

在这种情况下，AOP 建议将尝试该方法三次，两次尝试之间有 5 秒的延迟，仅当该方法抛出NumberFormatException时。

## 9. @UnitedThrow

@UnitedThrow注解允许我们捕获方法抛出的所有异常并将其包装在我们指定的异常中[。](https://aspects.jcabi.com/apidocs-0.22.6/com/jcabi/aspects/UnitedThrow.html)因此，它统一了方法抛出的异常。

例如，让我们创建一个 抛出IOException和InterruptedException的方法processFile：

```java
@UnitedThrow(IllegalStateException.class)
public static void processFile() throws IOException, InterruptedException {
    BufferedReader reader = new BufferedReader(new FileReader("baeldung.txt"));
    reader.readLine();
    // additional file processing
}
```

在这里，我们添加了注解以将所有异常包装到IllegalStateException中。因此，当调用该方法时，异常的堆栈跟踪将如下所示：

```shell
java.lang.IllegalStateException: java.io.FileNotFoundException: baeldung.txt (No such file or directory)
    at com.baeldung.jcabi.JcabiAspectJ.processFile(JcabiAspectJ.java:92)
    at com.baeldung.jcabi.JcabiAspectJ.main(JcabiAspectJ.java:39)
Caused by: java.io.FileNotFoundException: baeldung.txt (No such file or directory)
    at java.io.FileInputStream.open0(Native Method)
    ...
```

## 10.总结

在本文中，我们探讨了jcabi-aspectsJava库。

首先，我们已经了解了使用jcabi-maven-plugin在我们的 Maven 项目中设置库的快速方法。

然后，我们检查了一些方便的注解，如@Async、@Loggable和@RetryOnFailure，它们使用 AOP 修改Java应用程序的行为。