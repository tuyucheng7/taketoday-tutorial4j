## 1. 概述

在本文中，我们演示如何以编程方式找出我们的应用程序正在使用哪个版本的Spring、JDK和Java。

## 2. 如何获取Spring版本

为了获取当前使用的Spring版本，我们将使用SpringVersion类的getVersion方法：

```java

@SpringBootTest
class VersionObtainedUnitTest {

    @Test
    void testGetSpringVersion() {
        String res = version.getSpringVersion();
        assertEquals("5.3.13", SpringVersion.getVersion());
    }
}
```

## 3. 获取JDK版本

接下来，让我们获取我们项目中当前使用的JDK版本。需要注意的是，Java和JDK不是一回事，因此它们的版本号不同。

如果我们使用Spring 4.x版本，则有一个名为JdkVersion的类可用于获取此信息。
然而，这个类已从Spring 5.x中删除，因此我们必须考虑到这一点并解决它。

在内部，Spring 4.x的JdkVersion类使用SystemProperties类获取版本，因此我们也可以这样做。
**利用类SystemProperties，我们可以访问属性java.version**：

```java

@SpringBootTest
class VersionObtainedUnitTest {

    @Test
    void testGetJdkVersion() {
        String res = SystemProperties.get("java.version");
        assertEquals("17.0.1", res);
    }
}
```

或者，我们可以不使用Spring类直接访问该属性：

```
assertEquals("17.0.1", System.getProperty("java.version"));
```

## 4. 获取Java版本

最后，让我们看看如何获取应用程序正在运行的Java版本。**为此，我们将使用JavaVersion类**：

```java

@SpringBootTest
class VersionObtainedUnitTest {

    @Test
    void testGetJavaVersion() {
        String res = JavaVersion.getJavaVersion().toString();
        assertEquals("17", res);
    }
}
```

上面，我们调用JavaVersion#getJavaVersion方法。默认情况下，这将返回具有特定Java版本的枚举，例如JavaVersion.SEVENTEEN。
为了保持格式与上述方法一致，我们使用其toString方法对其进行解析。

## 5. 总结

在本文中，我们演示了如何通过Spring获取应用程序当前使用的Spring、JDK和Java版本。