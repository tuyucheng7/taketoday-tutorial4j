## 1. 概述

在这个简短的教程中，我们将快速了解如何在Java中将[cookie](https://www.baeldung.com/cookies-java)与[Selenium WebDriver](https://www.baeldung.com/java-selenium-with-junit-and-testng)一起使用。

## 2. 使用Cookie

**操作cookie的一个日常用例是在测试之间保持我们的会话**。

一个更简单的场景是当我们想要测试我们的后端是否正确设置了cookie时。

在接下来的部分中，我们将简要讨论如何处理cookie，同时提供简单的代码示例。

### 2.1 项目构建

我们需要将[selenium-java](https://central.sonatype.com/artifact/org.seleniumhq.selenium/selenium-java/4.8.1)依赖项添加到我们的项目中：

```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>3.14.0</version>
</dependency>
```

接下来，我们应该下载最新版本的[Chrome驱动程序](https://chromedriver.chromium.org/downloads)。

现在让我们设置测试类：

```java
public class SeleniumCookiesJUnitLiveTest {

    private WebDriver driver;
    private String navUrl;

    @Before
    public void setUp() {
        Capabilities capabilities = DesiredCapabilities.chrome();
        driver = new ChromeDriver(capabilities);
        navUrl = "https://baeldung.com";
    }
}
```

### 2.2 读取Cookies

接下来，我们将实现一个简单的测试来验证导航到网页后我们的驱动程序中是否存在cookie：

```java
@Test
public void whenNavigate_thenCookiesExist() {
    driver.navigate().to(navUrl);
    Set<Cookie> cookies = driver.manage().getCookies();
    
    assertThat(cookies, is(not(empty())));
}
```

**通常，我们可能想要搜索指定的cookie**：

```java
@Test
public void whenNavigate_thenLpCookieExists() {
    driver.navigate().to(navUrl);
    Cookie lpCookie = driver.manage().getCookieNamed("lp_120073");

    assertThat(lpCookie.getValue(), containsString("www.baeldung.com"));
}
```

### 2.3 Cookie属性

**cookie可以与域相关联，具有到期日期等等**。

让我们来看看一些常见的cookie属性：

```java
@Test
public void whenNavigate_thenLpCookieHasCorrectProps() {
    driver.navigate().to(navUrl);
    Cookie lpCookie = driver.manage().getCookieNamed("lp_120073");

    assertThat(lpCookie.getDomain(), equalTo(".baeldung.com"));
    assertThat(lpCookie.getPath(), equalTo("/"));
    assertThat(lpCookie.getExpiry(), is(not(nullValue())));
    assertThat(lpCookie.isSecure(), equalTo(false));
    assertThat(lpCookie.isHttpOnly(), equalTo(false));
}
```

### 2.4 添加Cookie

**添加cookie是一个简单的过程**。

我们创建cookie并使用addCookie方法将其添加到驱动程序中：

```java
@Test
public void whenAddingCookie_thenItIsPresent() {
    driver.navigate().to(navUrl);
    Cookie cookie = new Cookie("foo", "bar");
    driver.manage().addCookie(cookie);
    Cookie driverCookie = driver.manage().getCookieNamed("foo");
    
    assertThat(driverCookie.getValue(), equalTo("bar"));
}
```

### 2.5 删除Cookie

**正如我们所期望的，我们也可以使用deleteCookie()方法删除cookie**：

```java
@Test
public void whenDeletingCookie_thenItIsAbsent() {
    driver.navigate().to(navUrl);
    Cookie lpCookie = driver.manage().getCookieNamed("lp_120073");
    
    assertThat(lpCookie, is(not(nullValue())));
    
    driver.manage().deleteCookie(lpCookie);
    Cookie deletedCookie = driver.manage().getCookieNamed("lp_120073");
    
    assertThat(deletedCookie, is(nullValue()));
}
```

### 2.6 重写Cookie

虽然没有明确的方法来覆盖cookie，但有一个简单的方法。

我们可以删除cookie并添加一个名称相同但值不同的新cookie：

```java
@Test
public void whenOverridingCookie_thenItIsUpdated() {
    driver.navigate().to(navUrl);
    Cookie lpCookie = driver.manage().getCookieNamed("lp_120073");
    driver.manage().deleteCookie(lpCookie);

    Cookie newLpCookie = new Cookie("lp_120073", "foo");
    driver.manage().addCookie(newLpCookie);
    Cookie overriddenCookie = driver.manage().getCookieNamed("lp_120073");

    assertThat(overriddenCookie.getValue(), equalTo("foo"));
}
```

## 3. 总结

在这个快速教程中，我们通过快速实用的示例学习了如何在Java中使用Selenium WebDriver来处理cookie。