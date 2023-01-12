## 1. 概述

在本文中，我们将研究使用[SonarQube](https://www.sonarqube.org/)进行的静态源代码分析——这是一个用于确保代码质量的开源平台。

让我们从一个核心问题开始——为什么首先要分析源代码？简而言之，就是在项目的整个生命周期内确保质量、可靠性和可维护性；编写糟糕的代码库的维护成本总是更高。

[好的，现在让我们开始从下载页面](https://www.sonarqube.org/downloads/)下载最新的 LTS 版本的 SonarQube ，并按照本[快速入门指南](https://docs.sonarqube.org/latest/setup/get-started-2-minutes/)中的概述设置我们的本地服务器。

## 2.分析源代码

现在我们已经登录，我们需要通过指定名称来创建令牌——可以是我们的用户名或任何其他选择的名称，然后单击生成按钮。

稍后我们将在分析我们的项目时使用该令牌。我们还需要选择项目的主要语言(Java)和构建技术(Maven)。

让我们在pom.xml中定义插件：

```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.sonarsource.scanner.maven</groupId>
                <artifactId>sonar-maven-plugin</artifactId>
                <version>3.4.0.905</version>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

最新版本的插件可[在此处](https://search.maven.org/classic/#search|gav|1|g%3A"org.sonarsource.scanner.maven" AND a%3A"sonar-maven-plugin")获得。现在，我们需要从项目目录的根目录执行此命令来扫描它：

```bash
mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 
  -Dsonar.login=the-generated-token
```

我们需要用上面的令牌替换生成的令牌。

我们在本文中使用的项目可[在此处](https://github.com/eugenp/tutorials/tree/master/security-modules/cas/cas-secured-app)获得。

我们将 SonarQube 服务器的主机 URL 和登录名(生成的令牌)指定为 Maven 插件的参数。

执行该命令后，结果将在项目仪表板上可用 – 在http://localhost:9000。

我们可以将其他参数传递给 Maven 插件，甚至可以从 Web 界面设置；声纳主机。url、sonar.projectKey和sonar.sources是必需的，而其他是可选的。

其他分析参数及其默认值在[这里](https://docs.sonarqube.org/latest/analysis/analysis-parameters/)。另请注意，每种语言插件都有分析兼容源代码的规则。

## 三、分析结果

现在我们已经分析了我们的第一个项目，我们可以转到http://localhost:9000的 Web 界面并刷新页面。

在那里我们将看到报告摘要：

[![概述](https://www.baeldung.com/wp-content/uploads/2018/02/9029390_overview.jpg)](https://www.baeldung.com/wp-content/uploads/2018/02/9029390_overview.jpg)

发现的问题可以是错误、漏洞、代码味道、覆盖率或重复。每个类别都有相应的问题数或百分比值。

此外，问题可以具有五个不同的严重级别之一：阻塞、严重、主要、次要和信息。就在项目名称前面是一个显示质量门状态的图标——通过(绿色)或失败(红色)。

单击项目名称将带我们进入专用仪表板，我们可以在其中更详细地探索项目特有的问题。

我们可以从项目仪表板中查看项目代码、活动并执行管理任务——每一项都在单独的选项卡上可用。

虽然有一个全局问题选项卡，但项目仪表板上的问题选项卡仅显示特定于相关项目的问题：

[![问题](https://www.baeldung.com/wp-content/uploads/2018/02/383732889_issues.jpg)](https://www.baeldung.com/wp-content/uploads/2018/02/383732889_issues.jpg)

问题选项卡始终显示类别、严重性级别、标签以及纠正问题所需的计算工作量(关于时间)。

在问题选项卡中，可以将问题分配给其他用户、对其发表评论并更改其严重性级别。单击问题本身将显示有关该问题的更多详细信息。

问题选项卡左侧带有复杂的过滤器。这些有助于查明问题。那么如何知道代码库是否足够健康以部署到生产环境中呢？这就是质量门的用途。

## 4. SonarQube 质量门

在本节中，我们将了解 SonarQube 的一个关键特性——Quality Gate。然后我们将看到一个如何设置自定义的示例。

### 4.1. 什么是质量门？

质量门是项目在符合生产发布资格之前必须满足的一组条件。它回答了一个问题：我能否将我的代码以其当前状态推送到生产环境？

在修复现有代码的同时确保“新”代码的代码质量是长期维护良好代码库的一种好方法。Quality Gate 有助于设置规则，以验证在后续分析中添加到代码库中的每个新代码。

Quality Gate 中设置的条件仍然影响未修改的代码段。如果我们能够防止出现新问题，随着时间的推移，我们将消除所有问题。

这种方法相当于从源头[解决漏水问题。](https://docs.sonarqube.org/7.4/user-guide/fixing-the-water-leak/)这给我们带来了一个特定的术语——泄漏期。这是项目的两个分析/版本之间的时间段。

如果我们在同一个项目上重新运行分析，项目仪表板的概览选项卡将显示泄漏期间的结果：

[![泄漏期](https://www.baeldung.com/wp-content/uploads/2018/02/36326789289_leak_period.jpg)](https://www.baeldung.com/wp-content/uploads/2018/02/36326789289_leak_period.jpg)

在 Web 界面中，“质量门”选项卡是我们可以访问所有定义的质量门的地方。默认情况下，SonarQube 方式预装在服务器中。

如果出现以下情况，SonarQube 方式的默认配置会将代码标记为失败：

-   新代码覆盖率低于80%
-   新代码中重复行的百分比大于 3
-   可维护性、可靠性或安全等级低于 A

有了这种理解，我们就可以创建自定义质量门。

### 4.2. 添加自定义质量门

首先，我们需要单击“质量门”选项卡，然后单击页面左侧的“创建”按钮。我们需要给它起个名字——baeldung。

现在我们可以设置我们想要的条件：

[![创建自定义门 1](https://www.baeldung.com/wp-content/uploads/2018/02/create-custom-gate-1.png)](https://www.baeldung.com/wp-content/uploads/2018/02/create-custom-gate-1.png)

从Add Condition下拉菜单中，选择Blocker Issues；它会立即出现在条件列表中。

我们将指定大于作为运算符，将错误列设置为零 (0)并检查Over Leak Period列：

[![创建自定义门 2](https://www.baeldung.com/wp-content/uploads/2018/02/create-custom-gate-2.png)](https://www.baeldung.com/wp-content/uploads/2018/02/create-custom-gate-2.png)

然后我们将单击“添加”按钮来使更改生效。让我们按照与上述相同的步骤添加另一个条件。

我们将从“添加条件”下拉列表中选择问题并选中“超过泄漏期”列。

O perator列的值将设置为“小于”，我们将添加一 (1) 作为Error列的值。这意味着如果添加的新代码中的问题数量少于 1，则将 Quality Gate 标记为 failed。

我知道这在技术上没有意义，但让我们用它来学习。不要忘记单击“添加”按钮来保存规则。

最后一步，我们需要将项目附加到我们的自定义质量门。我们可以通过向下滚动页面到“项目”部分来完成此操作。

在那里我们需要点击全部然后标记我们选择的项目。我们也可以将其设置为页面右上角的默认质量门。

我们将再次扫描项目源代码，就像我们之前使用 Maven 命令所做的那样。完成后，我们将转到项目选项卡并刷新。

这一次，该项目将不符合质量门标准，因此将失败。为什么？因为在我们的一项规则中我们已经指定，如果没有新问题，它应该失败。

让我们回到 Quality Gates 选项卡并将问题的条件更改为is greater than。我们需要单击更新按钮来实现此更改。

这次将通过对源代码的新扫描。

## 5. 将 SonarQube 集成到 CI 中

使 SonarQube 成为持续集成过程的一部分是可能的。如果代码分析不满足质量门条件，这将自动使构建失败。

为了实现这一目标，我们将使用[SonarCloud](https://sonarcloud.io/)，它是 SonaQube 服务器的云托管版本。[我们可以在这里](https://sonarcloud.io/sessions/new)创建一个帐户。

从我的账户 > 组织中，我们可以看到组织密钥，它通常采用xxxx-github或xxxx-bitbucket的形式。

同样从我的帐户 > 安全性，我们可以像在服务器的本地实例中那样生成一个令牌。记下令牌和组织密钥以备后用。

在本文中，我们将使用 Travis CI，我们将[在此处](https://travis-ci.org/)使用现有的 Github 配置文件创建一个帐户。它将加载我们所有的项目，我们可以打开任何项目的开关以在其上激活 Travis CI。

我们需要将我们在 SonarCloud 上生成的令牌添加到 Travis 环境变量中。我们可以通过单击我们为 CI 激活的项目来执行此操作。

然后，我们将单击“更多选项”>“设置”，然后向下滚动到“环境变量”：

[![特拉维斯那里 1](https://www.baeldung.com/wp-content/uploads/2018/02/travis-ci-1.png)](https://www.baeldung.com/wp-content/uploads/2018/02/travis-ci-1.png)

我们将添加一个名为SONAR_TOKEN的新条目，并使用在 SonarCloud 上生成的令牌作为值。Travis CI 将加密并隐藏它，不让公众看到：

[![那里的特拉维斯 2](https://www.baeldung.com/wp-content/uploads/2018/02/travis-ci-2.png)](https://www.baeldung.com/wp-content/uploads/2018/02/travis-ci-2.png)

最后，我们需要添加一个.travis.yml文件到我们项目的根目录，内容如下：

```plaintext
language: java
sudo: false
install: true
addons:
  sonarcloud:
    organization: "your_organization_key"
    token:
      secure: "$SONAR_TOKEN"
jdk:
  - oraclejdk8
script:
  - mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package sonar:sonar
cache:
  directories:
    - '$HOME/.m2/repository'
    - '$HOME/.sonar/cache'
```

请记住将你的组织密钥替换为上述组织密钥。提交新代码并推送到 Github 存储库将触发 Travis CI 构建，进而激活声纳扫描。

## 六. 总结

在本教程中，我们了解了如何在本地设置 SonarQube 服务器以及如何使用 Quality Gate 来定义项目是否适合生产发布的标准。

SonarQube[文档](https://docs.sonarqube.org/latest/)包含有关该平台其他方面的更多信息。