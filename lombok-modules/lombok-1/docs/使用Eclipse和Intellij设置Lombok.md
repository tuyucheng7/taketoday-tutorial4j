## 1. 概述

[Lombok](https://www.baeldung.com/intro-to-project-lombok)是一个库，可以简化许多繁琐的任务并减少Java源代码的冗长程度。

当然，我们通常希望能够在 IDE 中使用该库，这需要额外的设置。

在本教程中，我们将讨论在两个最流行的JavaIDE(IntelliJ IDEA 和 Eclipse)中配置 Lombok。

## 延伸阅读：

## [使用 Lombok 的 @Builder 注解](https://www.baeldung.com/lombok-builder)

了解 Project Lombok 中的 @Builder 注解如何帮助你在实现构建器模式以创建Java类实例时减少样板代码。

[阅读更多](https://www.baeldung.com/lombok-builder)→

## [龙目岛项目介绍](https://www.baeldung.com/intro-to-project-lombok)

对标准Java代码上 Project Lombok 的许多有用用例的全面且非常实用的介绍。

[阅读更多](https://www.baeldung.com/intro-to-project-lombok)→

## 2. IntelliJ IDEA 中的 Lombok

从 IntelliJ 版本[2020.3 开始](https://www.jetbrains.com/idea/whatsnew/2020-3/#other)，我们不再需要配置 IDE 来使用 Lombok。IDE 与插件捆绑在一起。此外，注解处理将自动启用。

在早期版本的 IntelliJ 中，我们需要执行以下步骤才能使用 Lombok。此外，如果我们使用最新版本并且 IDE 无法识别 Lombok 注解，我们需要验证以下配置是否未被手动禁用。

### 2.1. 启用注解处理

Lombok 通过[APT](https://docs.oracle.com/javase/7/docs/technotes/guides/apt/GettingStarted.html)使用注解处理。因此，当编译器调用它时，库会根据原始文件中的注解生成新的源文件。

不过，默认情况下不启用注解处理。

因此，首先要做的是在我们的项目中开启注解处理。

我们需要去Preferences | 构建、执行、部署 | 编译器 | 注解处理器并确保以下内容：

-   选中启用注解处理框
-   从项目类路径选项中获取处理器已选中

[![龙目岛1](https://www.baeldung.com/wp-content/uploads/2019/01/lombok1.png)](https://www.baeldung.com/wp-content/uploads/2019/01/lombok1.png)

### 2.2. 安装 IDE 插件

虽然 Lombok 仅在编译期间生成代码，但 IDE 会突出显示原始源代码中的错误：

[![lobom2](https://www.baeldung.com/wp-content/uploads/2019/01/lobom2.png)](https://www.baeldung.com/wp-content/uploads/2019/01/lobom2.png)

有一个专用插件可以让 IntelliJ 知道要生成的源代码。安装后，错误消失，常规功能(如Find Usages 和Navigate To )开始工作。

我们需要去Preferences | Plugins，打开 Marketplace选项卡，输入“lombok”并选择Michail Plushnikov 的 Lombok Plugin：

[![龙目岛3](https://www.baeldung.com/wp-content/uploads/2019/01/lombok3.png)](https://www.baeldung.com/wp-content/uploads/2019/01/lombok3.png)

接下来，单击 插件页面上的安装按钮：

[![辣椒4](https://www.baeldung.com/wp-content/uploads/2019/01/lombok4.png)](https://www.baeldung.com/wp-content/uploads/2019/01/lombok4.png)

安装完成后，点击重启IDE按钮：

[![辣椒 5](https://www.baeldung.com/wp-content/uploads/2019/01/lombok5.png)](https://www.baeldung.com/wp-content/uploads/2019/01/lombok5.png)

## 3.Eclipse中的龙目岛

如果我们使用 Eclipse IDE，我们需要先获取 Lombok jar。最新版本位于[Maven Central](https://search.maven.org/search?q=g:org.projectlombok AND a:lombok&core=gav)上。

对于我们的示例，我们使用 [lombok-1.18.4.jar](https://search.maven.org/remotecontent?filepath=org/projectlombok/lombok/1.18.4/lombok-1.18.4.jar)。

接下来，我们可以通过java -jar命令运行 jar，安装程序 UI 将打开。这会尝试自动检测所有可用的 Eclipse 安装，但也可以手动指定位置。

一旦我们选择了安装，我们按下安装/更新按钮：

[![辣椒6](https://www.baeldung.com/wp-content/uploads/2019/01/lombok6.png)](https://www.baeldung.com/wp-content/uploads/2019/01/lombok6.png)

如果安装成功，我们就可以退出安装程序了。

安装插件后，我们需要重启IDE，确保Lombok配置正确。我们可以在“ 关于”对话框中查看：

[![辣椒7](https://www.baeldung.com/wp-content/uploads/2019/01/lombok7.png)](https://www.baeldung.com/wp-content/uploads/2019/01/lombok7.png)

## 4. 将 Lombok 添加到编译类路径

最后剩下的部分是确保 Lombok 二进制文件位于编译器类路径中。使用 Maven，我们可以将依赖项添加到pom.xml：

```xml
<dependencies>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.20</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

最新版本位于[Maven Central](https://search.maven.org/search?q=g:org.projectlombok AND a:lombok&core=gav)上。

现在一切都应该很好。源代码应该在 IDE 中正确显示，正确编译和执行：

```java
public class UserIntegrationTest {

    @Test
    public void givenAnnotatedUser_thenHasGettersAndSetters() {
        User user = new User();
        user.setFirstName("Test");
        assertEquals(user.gerFirstName(), "Test");
    }

    @Getter @Setter
    class User {
        private String firstName;
    }
}
```

## 5.总结

Lombok 在减少Java冗长和隐藏样板代码方面做得很好。在本文中，我们检查了如何为两个最流行的JavaIDE 配置该工具。