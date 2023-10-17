## 1. 概述

在本教程中，我们将了解package-info.java的用途及其用途。简单的说，package-info就是一个Java文件，可以添加到任何Java包中。

## 2. package-info的用途

package-info.java文件目前有两个用途：

-   包级文档的地方
-   包级注解的主页

除了上述之外，还可以根据需要扩展用例。将来，如果需要添加任何包级功能，这个文件将是一个完美的地方。

让我们详细检查当前的用例。

## 3. 包装文件

在Java版本 5 之前，与包相关的文档放在 HTML 文件package.html中。这只是一个普通的 HTML 文件，在body标签内放置了 Javadoc 注解。

随着JDK 5的出现，package.html让位于一个新选项package-info.java，现在它比package.html更受青睐。

让我们看一个package-info.java文件中的包文档示例：

```java
/
  This module is about impact of the final keyword on performance
  <p>
  This module explores  if there are any performance benefits from
  using the final keyword in our code. This module examines the performance
  implications of using final on a variable, method, and class level.
  </p>
 
  @since 1.0
  @author baeldung
  @version 1.1
 /
package com.baeldung.finalkeyword;
```

上面的package-info.java将生成 Javadoc：

[![包Javadoc](https://www.baeldung.com/wp-content/uploads/2021/04/PackageJavadoc-1-1024x447.png)](https://www.baeldung.com/wp-content/uploads/2021/04/PackageJavadoc-1.png)

所以，就像我们在其他地方写一个Javadoc一样，我们可以把包Javadoc放在一个Java源文件中。

## 4.包注解

假设我们必须对整个包应用一个注解。在这种情况下，package-info.java可以为我们提供帮助。

考虑一种情况，我们需要默认将字段、参数和返回值声明为非空。我们可以通过简单地在我们的package-info.java文件中包含非空参数和返回值的@NonNullApi注解以及非空字段的@NonNullFields注解来实现这一目标。 

@NonNullFields和@NonNullApi会将字段、参数和返回值标记为非空，除非它们被显式标记为@Nullable：

```java
@NonNullApi
@NonNullFields
package com.baeldung.nullibility;

import org.springframework.lang.NonNullApi;
import org.springframework.lang.NonNullFields;
```

在包级别可以使用各种注解。比如在Hibernate项目中，我们有一个[类注解](https://docs.jboss.org/hibernate/orm/6.0/javadocs/org/hibernate/annotations/package-summary.html)，JAXB项目中也有[包级别的注解](https://docs.oracle.com/javase/8/docs/api/javax/xml/bind/annotation/package-summary.html)。

## 5. 如何创建包信息文件

创建包信息文件非常简单：我们可以手动创建它或寻求 IDE 帮助来生成它。

在 IntelliJ IDEA 中，我们可以右键单击包并选择New-> package-info.java：

[![截图来自-2021-04-05-11-21-15](https://www.baeldung.com/wp-content/uploads/2021/04/Screenshot-from-2021-04-05-11-21-15.png)](https://www.baeldung.com/wp-content/uploads/2021/04/Screenshot-from-2021-04-05-11-21-15.png)

Eclipse 的NewJavaPackage选项允许我们生成一个package-info.java：

[![包装信息](https://www.baeldung.com/wp-content/uploads/2021/04/PackageInfo.png)](https://www.baeldung.com/wp-content/uploads/2021/04/PackageInfo.png)

上述方法也适用于现有包。选择已有的包，New->Package选项，勾选Create package-info.java选项。

在我们的项目编码指南中强制包含package-info.java始终是一个好习惯。[Sonar](https://rules.sonarsource.com/java/tag/convention/RSPEC-1228)或[Checkstyle](https://checkstyle.sourceforge.io/config_javadoc.html#JavadocPackage)等工具可以帮助实现这一目标。

## 六，总结

HTML 和Java文件用法之间的主要区别在于，对于Java文件，我们还有使用Java注解的额外可能性。因此，包信息 java文件不仅是包 Javadocs 的所在地，而且还是包范围内的注解。此外，将来可以扩展此用例列表。