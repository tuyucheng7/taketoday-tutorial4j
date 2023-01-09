## 1. 概述

在本文中，我们将快速介绍如何在Java中将cookie与Selenium WebDriver一起使用。

## 2. 使用Cookie

**操作cookie的一个用例是在测试之间保持我们的会话**。

一个更简单的场景是当我们想要测试我们的后端是否正确设置了cookie时。

在接下来的部分中，我们将简要讨论如何处理cookie，同时提供简单的代码示例。

### 2.1 项目构建

我们需要将selenium-java依赖项添加到我们的项目中：

```
<dependency>
  <groupId>org.seleniumhq.selenium</groupId>
  <artifactId>selenium-java</artifactId>
  <version>3.141.59</version>
</dependency>
```

接下来，我们应该下载最新版本的[Gecko驱动程序](https://github.com/mozilla/geckodriver/releases)。

现在让我们配置我们的测试类：

```java
public class SeleniumCookiesJUnitLiveTest {

  private WebDriver driver;
  private String navUrl;

  @Before
  public void setUp() {
    System.setProperty("webdriver.firefox.bin", "D:softwareFireFoxfirefox.exe");
    System.setProperty("webdriver.gecko.driver", "D:java-workspaceintellij-workspacetesting-develop-in-actiontesting-in-action-selenium-junit-testngsrcmainresourcesgeckodriver.exe");
    Capabilities capabilities = DesiredCapabilities.firefox();
    driver = new FirefoxDriver(capabilities);
    navUrl = "https://baeldung.com";
    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
  }
}
```

### 2.2 读取Cookies

接下来，我们将实现一个简单的测试，以在我们导航到网页后验证驱动程序中是否存在cookie：

```java
public class SeleniumCookiesJUnitLiveTest {

  @Test
  public void whenNavigate_thenCookiesExist() {
    driver.navigate().to(navUrl);
    Set<Cookie> cookies = driver.manage().getCookies();
    assertThat(cookies, is(not(empty())));
  }
}
```

**通常，我们可能想要搜索指定的cookie**：

```java
public class SeleniumCookiesJUnitLiveTest {

  @Test
  public void whenNavigate_thenLpCookieExists() {
    driver.navigate().to(navUrl);
    Cookie lpCookie = driver.manage().getCookieNamed("lp_120073");
    assertThat(lpCookie, is(not(nullValue())));
  }
}
```

### 2.3 Cookie属性

**cookie可以与域名相关联，具有到期日期等等**。

我们来看看一些常见的cookie属性：

```java
public class SeleniumCookiesJUnitLiveTest {

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
}
```

### 2.4 添加Cookie

**添加cookie是一个简单的过程**。

我们创建cookie并使用addCookie方法将其添加到驱动程序：

```java
public class SeleniumCookiesJUnitLiveTest {

  @Test
  public void whenAddingCookie_thenItIsPresent() {
    driver.navigate().to(navUrl);
    Cookie cookie = new Cookie("foo", "bar");
    driver.manage().addCookie(cookie);
    Cookie driverCookie = driver.manage().getCookieNamed("foo");
    assertThat(driverCookie.getValue(), equalTo("bar"));
  }
}
```

### 2.5 删除Cookie

**正如我们所料，我们还可以使用deleteCookie()方法删除cookie**：

```java
public class SeleniumCookiesJUnitLiveTest {

  @Test
  public void whenDeletingCookie_thenItIsAbsent() {
    driver.navigate().to(navUrl);
    Cookie lpCookie = driver.manage().getCookieNamed("lp_120073");
    assertThat(lpCookie, is(not(nullValue())));
    driver.manage().deleteCookie(lpCookie);
    Cookie deletedCookie = driver.manage().getCookieNamed("lp_120073");
    assertThat(deletedCookie, is(nullValue()));
  }
}
```

### 2.6 重写Cookie

虽然没有明确的方法来覆盖cookie，但有一种简单的方法。

我们可以删除cookie并添加一个名称相同但值不同的新cookie：

```java
public class SeleniumCookiesJUnitLiveTest {
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
}
```

## 3. 总结

在这个快速教程中，我们通过快速实用的示例学习了如何在Java中使用Selenium WebDriver来处理cookie。