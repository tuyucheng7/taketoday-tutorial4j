## 1. 概述

有时在使用Java编程时，以编程方式查找我们正在使用的Java版本可能会有所帮助。在本教程中，我们将介绍几种获取Java版本的方法。

## 2. Java版本命名约定

直到Java 9，Java版本都没有遵循[Semantic Versioning](https://www.baeldung.com/cs/semantic-versioning)。格式为1.X.Y_Z。 X和Y分别表示主要和次要版本。Z用于表示更新版本，用下划线“_”分隔。例如，1.8.0_181。对于Java 9及更高版本，Java版本遵循语义版本控制。语义版本控制使用XYZ格式。它指的是主要、次要和补丁(major、minor和patch)。例如，11.0.7。

## 3. 获取Java版本

### 3.1 使用System.getProperty

系统属性是Java运行时提供的键值对，java.version是提供Java版本的系统属性。让我们定义一个获取版本的方法：

```java
public void givenJava_whenUsingSystemProp_thenGetVersion() {
    int expectedVersion = 8;
    String[] versionElements = System.getProperty("java.version").split("\\.");
    int discard = Integer.parseInt(versionElements[0]);
    int version;
    if (discard == 1) {
        version = Integer.parseInt(versionElements[1]);
    } else {
        version = discard;
    }
    Assertions.assertThat(version).isEqualTo(expectedVersion);
}
```

为了支持这两种Java[版本格式](https://www.baeldung.com/java-comparing-versions)，我们应该检查第一个数字直到点。

### 3.2 使用Apache Commons Lang 3

获取Java版本的第二种方法是通过[Apache Commons Lang 3](https://www.baeldung.com/java-commons-lang-3)库。首先我们需要将[commons-lang3](https://search.maven.org/search?q=a:commons-lang3) Maven依赖添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

我们将使用[SystemUtils](https://www.baeldung.com/java-commons-lang-3#the-systemutils-class)类来获取有关Java平台的信息。让我们为此目的定义一个方法：

```java
public void givenJava_whenUsingCommonsLang_thenGetVersion() {
    int expectedVersion = 8;
    String[] versionElements = SystemUtils.JAVA_SPECIFICATION_VERSION.split("\\.");
    int discard = Integer.parseInt(versionElements[0]);
    int version;
    if (discard == 1) {
        version = Integer.parseInt(versionElements[1]);
    } else {
        version = discard;
    }
    Assertions.assertThat(version).isEqualTo(expectedVersion);
}
```

我们正在使用JAVA_SPECIFICATION_VERSION来镜像java.specification.version系统属性中的可用值。

### 3.3 使用Runtime.version()

在Java 9及以上版本中，我们可以使用Runtime.version()来获取版本信息。让我们为此目的定义一个方法：

```java
public void givenJava_whenUsingRuntime_thenGetVersion(){
    String expectedVersion = "15";
    Runtime.Version runtimeVersion = Runtime.version();
    String version = String.valueOf(runtimeVersion.version().get(0));
    Assertions.assertThat(version).isEqualTo(expectedVersion);
}
```

## 4. 总结

在本文中，我们将介绍几种获取Java版本的方法。