## 1. 概述

随着 DevOps 技术的进步，一天内多次构建和部署应用程序是很常见的。

因此，每个构建都被分配了一个唯一的版本号，因此我们可以区分构建。有时，需要以编程方式比较版本字符串。

在本文中，我们将探讨几种通过各种库在Java中比较版本字符串的方法。最后，我们将编写一个自定义程序来处理通用版本字符串比较。

## 2.使用maven-artifact

首先，让我们探讨[Maven](https://www.baeldung.com/maven)如何处理版本比较。

### 2.1. Maven 依赖

首先，我们将最新的[maven-artifact](https://search.maven.org/search?q=g:org.apache.maven a:maven-artifact) Maven 依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.apache.maven</groupId>
    <artifactId>maven-artifact</artifactId>
    <version>3.6.3</version>
</dependency>
```

### 2.2. 比较版本

让我们探索[ComparableVersion](https://maven.apache.org/ref/3.6.3/maven-artifact/apidocs/org/apache/maven/artifact/versioning/ComparableVersion.html)类。它提供了具有无限数量的版本组件的版本比较的通用实现。

它包含一个compareTo方法，当一个版本大于或小于另一个版本时，比较的结果将大于或小于0，分别为：

```java
ComparableVersion version1_1 = new ComparableVersion("1.1");
ComparableVersion version1_2 = new ComparableVersion("1.2");
ComparableVersion version1_3 = new ComparableVersion("1.3");

assertTrue(version1_1.compareTo(version1_2) < 0);
assertTrue(version1_3.compareTo(version1_2) > 0);
```

在这里，我们可以确认1.1版本小于1.2版本，1.3版本大于1.2版本。

但是，当比较相同版本时，我们将得到 0 作为结果：

```java
ComparableVersion version1_1_0 = new ComparableVersion("1.1.0");
assertEquals(0, version1_1.compareTo(version1_1_0));
```

### 2.3. 版本分隔符和限定符 

此外，ComparableVersion类将点 (.) 和连字符 (-) 作为分隔符，其中点分隔主要版本和次要版本，连字符定义限定符：

```java
ComparableVersion version1_1_alpha = new ComparableVersion("1.1-alpha");
assertTrue(version1_1.compareTo(version1_1_alpha) > 0);
```

在这里，我们可以确认 1.1 版本大于 1.1-alpha 版本。

ComparableVersion支持一些众所周知的限定符，例如alpha、beta、milestone、RC和snapshot(按从低到高的顺序)：

```java
ComparableVersion version1_1_beta = new ComparableVersion("1.1-beta");
ComparableVersion version1_1_milestone = new ComparableVersion("1.1-milestone");
ComparableVersion version1_1_rc = new ComparableVersion("1.1-rc");
ComparableVersion version1_1_snapshot = new ComparableVersion("1.1-snapshot");

assertTrue(version1_1_alpha.compareTo(version1_1_beta) < 0);
assertTrue(version1_1_beta.compareTo(version1_1_milestone) < 0);
assertTrue(version1_1_rc.compareTo(version1_1_snapshot) < 0);
assertTrue(version1_1_snapshot.compareTo(version1_1) < 0);
```

此外，它允许我们定义未知限定符并尊重它们的顺序，在已经讨论过的已知限定符之后，不区分大小写的词法顺序：

```java
ComparableVersion version1_1_c = new ComparableVersion("1.1-c");
ComparableVersion version1_1_z = new ComparableVersion("1.1-z");
ComparableVersion version1_1_1 = new ComparableVersion("1.1.1");
        
assertTrue(version1_1_c.compareTo(version1_1_z) < 0);
assertTrue(version1_1_z.compareTo(version1_1_1) < 0);
```

## 3.使用gradle-core

与 Maven 一样，[Gradle](https://www.baeldung.com/gradle)也具有处理版本比较的内置功能。

### 3.1. Maven 依赖

[首先，让我们从Gradle Releases repo](https://repo.gradle.org/gradle/libs-releases-local/)添加最新的[gradle-core](https://mvnrepository.com/artifact/org.gradle/gradle-core/6.1.1) Maven 依赖项：

```xml
<dependency>
    <groupId>org.gradle</groupId>
    <artifactId>gradle-core</artifactId>
    <version>6.1.1</version>
</dependency>
```

### 3.2. 版本号

Gradle提供的[VersionNumber](https://github.com/gradle/gradle/blob/master/subprojects/core/src/main/java/org/gradle/util/VersionNumber.java)类比较两个版本，类似于Maven的ComparableVersion类：

```java
VersionNumber version1_1 = VersionNumber.parse("1.1");
VersionNumber version1_2 = VersionNumber.parse("1.2");
VersionNumber version1_3 = VersionNumber.parse("1.3");

assertTrue(version1_1.compareTo(version1_2) < 0);
assertTrue(version1_3.compareTo(version1_2) > 0);

VersionNumber version1_1_0 = VersionNumber.parse("1.1.0");
assertEquals(0, version1_1.compareTo(version1_1_0));

```

### 3.3. 版本组件

与ComparableVersion类不同，VersionNumber类仅支持五个版本组件—— Major、Minor、Micro、Patch和Qualifier：

```java
VersionNumber version1_1_1_1_alpha = VersionNumber.parse("1.1.1.1-alpha"); 
assertTrue(version1_1.compareTo(version1_1_1_1_alpha) < 0); 

VersionNumber version1_1_beta = VersionNumber.parse("1.1.0.0-beta"); 
assertTrue(version1_1_beta.compareTo(version1_1_1_1_alpha) < 0);
```

### 3.4. 版本方案

此外，VersionNumber支持几个不同的版本方案，如Major.Minor.Micro-Qualifier和Major.Minor.Micro.Patch-Qualifier：

```java
VersionNumber version1_1_1_snapshot = VersionNumber.parse("1.1.1-snapshot");
assertTrue(version1_1_1_1_alpha.compareTo(version1_1_1_snapshot) < 0);
```

## 4.使用杰克逊核心

### 4.1. Maven 依赖

与其他依赖项类似，让我们将最新的[jackson-core](https://search.maven.org/search?q=g:com.fasterxml.jackson.core a:jackson-core) Maven 依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-core</artifactId>
    <version>2.11.1</version>
</dependency>
```

### 4.2. 版本

然后，我们可以检查[Jackson](https://www.baeldung.com/jackson)的[Version](https://javadoc.io/doc/com.fasterxml.jackson.core/jackson-core/latest/com/fasterxml/jackson/core/Version.html)类，它可以保存组件的版本控制信息以及可选的groupId和artifactId值。

因此，Version类的构造函数允许我们定义groupId和artifactId，以及Major、Minor和Patch等组件：

```java
public Version (int major, int minor, int patchLevel, String snapshotInfo, String groupId, String artifactId) {
    //...
}
```

因此，让我们使用Version类比较几个版本：

```java
Version version1_1 = new Version(1, 1, 0, null, null, null);
Version version1_2 = new Version(1, 2, 0, null, null, null);
Version version1_3 = new Version(1, 3, 0, null, null, null);

assertTrue(version1_1.compareTo(version1_2) < 0);
assertTrue(version1_3.compareTo(version1_2) > 0);

Version version1_1_1 = new Version(1, 1, 1, null, null, null);
assertTrue(version1_1.compareTo(version1_1_1) < 0);
```

### 4.3. snapshotInfo组件_

比较两个版本时不使用snapshotInfo组件：

```java
Version version1_1_snapshot = new Version(1, 1, 0, "snapshot", null, null); 
assertEquals(0, version1_1.compareTo(version1_1_snapshot));
```

此外，Version类提供了isSnapshot方法来检查版本是否包含快照组件：

```java
assertTrue(version1_1_snapshot.isSnapshot());
```

### 4.4. groupId和artifactId组件_

此外，此类比较groupId和artifactId版本组件的词法顺序：

```java
Version version1_1_maven = new Version(1, 1, 0, null, "org.apache.maven", null);
Version version1_1_gradle = new Version(1, 1, 0, null, "org.gradle", null);
assertTrue(version1_1_maven.compareTo(version1_1_gradle) < 0);
```

## 5. 使用Semver4J

Semver4j库允许我们遵循Java中语义[版本控制](https://www.baeldung.com/cs/semantic-versioning)规范的规则。

### 5.1. Maven 依赖

首先，我们将添加最新的[semver4j](https://search.maven.org/search?q=g:com.vdurmont a:semver4j) Maven 依赖项：

```xml
<dependency>
    <groupId>com.vdurmont</groupId>
    <artifactId>semver4j</artifactId>
    <version>3.1.0</version>
</dependency>
```

### 5.2. 服务器

然后，我们可以使用[Semver](https://github.com/vdurmont/semver4j/blob/master/src/main/java/com/vdurmont/semver4j/Semver.java)类来定义一个版本：

```java
Semver version1_1 = new Semver("1.1.0");
Semver version1_2 = new Semver("1.2.0");
Semver version1_3 = new Semver("1.3.0");

assertTrue(version1_1.compareTo(version1_2) < 0);
assertTrue(version1_3.compareTo(version1_2) > 0);

```

在内部，它将版本解析为Major、Minor和Patch等组件。

### 5.3. 版本比较

此外，Semver类带有各种内置方法，如isGreaterThan、isLowerThan和 isEqualTo用于版本比较：

```java
Semver version1_1_alpha = new Semver("1.1.0-alpha"); 
assertTrue(version1_1.isGreaterThan(version1_1_alpha)); 

Semver version1_1_beta = new Semver("1.1.0-beta"); 
assertTrue(version1_1_alpha.isLowerThan(version1_1_beta)); 

assertTrue(version1_1.isEqualTo("1.1.0"));
```

同样，它提供了diff方法来返回两个版本之间的主要区别：

```java
assertEquals(VersionDiff.MAJOR, version1_1.diff("2.1.0"));
assertEquals(VersionDiff.MINOR, version1_1.diff("1.2.3"));
assertEquals(VersionDiff.PATCH, version1_1.diff("1.1.1"));
```

### 5.4. 版本稳定性

此外，Semver类带有isStable方法来检查版本的稳定性，由是否存在后缀决定：

```java
assertTrue(version1_1.isStable());
assertFalse(version1_1_alpha.isStable());
```

## 6. 定制解决方案

我们已经看到了一些比较版本字符串的解决方案。如果它们不适用于特定用例，我们可能不得不编写自定义解决方案。

这是一个适用于一些基本情况的简单示例——如果我们需要更多，它总是可以扩展。

这里的想法是使用点分隔符标记版本字符串，然后比较每个字符串标记的整数转换，从左边开始。如果标记的整数值相同，则检查下一个标记，继续此步骤直到我们发现差异(或直到我们到达任一字符串中的最后一个标记)：

```java
public static int compareVersions(String version1, String version2) {
    int comparisonResult = 0;
    
    String[] version1Splits = version1.split(".");
    String[] version2Splits = version2.split(".");
    int maxLengthOfVersionSplits = Math.max(version1Splits.length, version2Splits.length);

    for (int i = 0; i < maxLengthOfVersionSplits; i++){
        Integer v1 = i < version1Splits.length ? Integer.parseInt(version1Splits[i]) : 0;
        Integer v2 = i < version2Splits.length ? Integer.parseInt(version2Splits[i]) : 0;
        int compare = v1.compareTo(v2);
        if (compare != 0) {
            comparisonResult = compare;
            break;
        }
    }
    return comparisonResult;
}
```

让我们通过比较几个版本来验证我们的解决方案：

```java
assertTrue(VersionCompare.compareVersions("1.0.1", "1.1.2") < 0);
assertTrue(VersionCompare.compareVersions("1.0.1", "1.10") < 0);
assertTrue(VersionCompare.compareVersions("1.1.2", "1.0.1") > 0);
assertTrue(VersionCompare.compareVersions("1.1.2", "1.2.0") < 0);
assertEquals(0, VersionCompare.compareVersions("1.3.0", "1.3"));
```

此代码有一个限制，它只能比较由点分隔的整数组成的版本号。

因此，为了比较字母数字版本字符串，我们可以使用正则表达式来分隔字母并比较词汇顺序。

## 七、总结

在本文中，我们研究了在Java中比较版本字符串的各种方法。

首先，我们检查了 Maven 和 Gradle 等构建框架提供的内置解决方案，分别使用maven-artifact和 gradle-core依赖项。然后，我们探索了jackson-core和semver4j库的版本比较功能。

最后，我们为通用版本字符串比较编写了一个自定义解决方案。