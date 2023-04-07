## 一、概述

[使用Javadoc](https://www.baeldung.com/javadoc)是一个好主意的原因有很多。例如，我们可以从我们的 Java 代码生成 HTML，遍历它们的定义，并发现与它们相关的各种属性。

此外，它**促进了开发人员之间的沟通并** **提高了可维护性**。Java [DocLint](http://openjdk.java.net/jeps/172)是一个分析 Javadoc 的工具。只要发现错误的语法，它就会抛出警告和错误。

在本教程中，我们重点介绍如何使用它。稍后，我们将研究它在某些情况下可能产生的问题，以及一些关于如何避免这些问题的指南。

## 2. 如何使用 DocLint

假设我们有一个名为*Sample.java 的*类文件：

```java
/**
 * This sample file creates a class that
 * just displays sample string on standard output.
 *
 * @autho  Baeldung
 * @version 0.9
 * @since   2020-06-13 
 */
public class Sample {

    public static void main(String[] args) {
        // Prints Sample! on standard output.
        System.out.println("Sample!");
    }
}复制
```

故意的，这里打错了，*@author*参数写成了*@autho*。让我们看看如果我们尝试在没有 DocLint 的情况下制作 Javadoc 会发生什么*：*

[![jdoc](https://www.baeldung.com/wp-content/uploads/2021/06/jdoc.png)](https://www.baeldung.com/wp-content/uploads/2021/06/jdoc.png)

正如我们从控制台输出中看到的那样，Javadoc 引擎无法找出我们文档中的错误，也没有返回任何错误或警告。

要使 Java DocLint 返回此类警告，我们必须执行带有*–Xdoclint选项的**javadoc*命令。（我们稍后会详细解释）：

[![jdoc错误](https://www.baeldung.com/wp-content/uploads/2021/06/jdoc-error.png)](https://www.baeldung.com/wp-content/uploads/2021/06/jdoc-error.png)

正如我们在输出中看到的那样，它直接提到了我们 Java 文件第 5 行中的错误：

```java
Sample.java:5: error: unknown tag: autho
 * @autho  Baeldung
   ^复制
```

## 3. *-Xdoclint*

*-Xdoclint*参数具有三个用于不同目的的选项。我们将快速浏览每一个。

### 3.1. *没有任何*

*none*选项禁用*-Xdoclint*选项：

```java
javadoc -Xdoclint:none Sample.java复制
```

### 3.2. *团体*

当我们想要应用与某些组相关的某些检查时，此选项很有用，例如：

```bash
javadoc -Xdoclint:syntax Sample.java复制
```

有几种类型的组变量：

-   *可访问性*——检查可访问性检查器要检测的问题（例如，表格标签中没有指定标题或摘要 *属性*）
-   *html* – 检测高级 HTML 问题，例如将块元素放入内联元素或不关闭需要结束标记的元素
-   *missing* – 检查是否缺少 Javadoc 注释或标记（例如，缺少注释或类，或者方法上缺少*@return标记或类似标记）*
-   *reference – 检查与 Javadoc 标记中对 Java API 元素的引用相关的问题（例如，在**@see*中找不到项目，或在*@param*之后有错误的名称）
-   *语法*– 检查低级问题，如未转义的尖括号（< 和 >）和符号（&）以及无效的 Javadoc 标记

可以一次应用多个组：

```bash
javadoc -Xdoclint:html,syntax,accessibility Sample.java复制
```

### 3.3. *全部*

此选项一次启用所有组，但如果我们想排除其中的一些怎么办？

我们可以使用*-group*语法：

```bash
javadoc -Xdoclint:all,-missing复制
```

## 4. 如何禁用 DocLint

由于 Java DocLint**在 Java 8 之前不存在**，这会造成不必要的麻烦，尤其是在旧的第三方库中。

我们已经在上一节中看到了*javadoc命令的**none*选项。此外，还有一个选项可以**从 Maven、Gradle、Ant 等构建系统中禁用 DocLint**。我们将在接下来的几小节中看到这些。

### **4.1. 行家**

使用*maven-javadoc-plugin*，从版本 3.0.0 开始，添加了一个新的[doclint配置。](http://maven.apache.org/plugins/maven-javadoc-plugin/javadoc-mojo.html#doclint)让我们看看如何配置它来禁用 DocLint：

```java
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-javadoc-plugin</artifactId>
        <version>3.1.1</version>
        <configuration>
            <doclint>none</doclint> <!-- Turn off all checks -->
        </configuration>
        <executions>
            <execution>
                <id>attach-javadocs</id>
                <goals>
                    <goal>jar</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
</plugins>复制
```

但**通常不建议使用\*none\* 选项**，因为它会跳过所有类型的检查。我们应该改用*<doclint>all,-missing</doclint>*。

在使用早期版本（v3.0.0 之前）时，我们需要使用不同的设置：

```java
<plugins>
  <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-javadoc-plugin</artifactId>
    <configuration>
      <additionalparam>-Xdoclint:none</additionalparam>
    </configuration>
  </plugin>
</plugins>复制
```

### **4.2. 摇篮**

我们可以使用一个简单的脚本在 Gradle 项目中停用 DocLint：

```java
if (JavaVersion.current().isJava8Compatible()) {
    allprojects {
        tasks.withType(Javadoc) {
            options.addStringOption('Xdoclint:none', '-quiet')
        }
    }
}复制
```

不幸的是，Gradle 不支持*additionalparam，*就像 Maven 在上面的例子中那样，所以我们需要手动完成。

### **4.3. 蚂蚁**

Ant 像 Maven 一样使用*additionalparam ，因此我们可以设置**-Xdoclint:none*，如上所示。

## 5.结论

在本文中，我们研究了使用 Java DocLint 的各种方式。当我们想要一个干净的、容易出错的 Javadoc 时，它可以帮助我们。

有关其他深入信息，最好查看官方[文档](https://docs.oracle.com/en/java/javase/11/tools/javadoc.html)。