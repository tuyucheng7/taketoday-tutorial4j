## 1. 概述

SonarQube是一个开源和独立的服务，通过度量代码质量和代码覆盖率来概述我们源代码的整体健康状况。

在本教程中，我们将介绍使用SonarQube和JaCoCo测量代码覆盖率的过程。

## 2. 说明

### 2.1 代码覆盖率

[代码覆盖率](https://www.baeldung.com/cs/code-coverage)(也称为测试覆盖率)，是衡量在测试中运行了多少应用程序代码的度量。从本质上讲，**这是许多团队用来检查测试质量的指标，因为它代表了已测试和运行的生产代码的百分比**。

这让开发团队确信他们的程序已经过广泛的错误测试，并且应该相对没有错误。

### 2.2 SonarQube和JaCoCo

[SonarQube](https://www.baeldung.com/sonar-qube)检查和评估影响我们代码库的所有内容，从微小的风格细节到关键的设计错误。这使开发人员能够访问和跟踪代码分析数据，范围从风格错误、潜在错误和代码缺陷，到设计效率低下、代码重复、缺乏测试覆盖率和过度复杂性。

它还定义了一个质量门，这是一组基于度量的布尔条件。此外，SonarQube可以帮助我们了解我们的代码是否可以投入生产。

SonarQube与[JaCoCo](https://www.baeldung.com/jacoco)集成使用，JaCoCo是一个免费的Java代码覆盖库。

## 3. Maven配置

### 3.1 下载SonarQube

我们可以从其[官网](https://www.sonarqube.org/downloads/)下载SonarQube。

要启动SonarQube，请在Windows机器上运行名为StartSonar.bat的文件，在Linux或macOS上运行名为sonar.sh的文件。该文件位于解压缩下载的bin目录中。

### 3.2 设置SonarQube和JaCoCo的属性

让我们首先添加定义JaCoCo版本、插件名称、报告路径和Sonar语言的必要属性：

```xml
<properties>
    <!-- JaCoCo Properties -->
    <jacoco.version>0.8.6</jacoco.version>
    <sonar.java.coveragePlugin>jacoco</sonar.java.coveragePlugin>
    <sonar.dynamicAnalysis>reuseReports</sonar.dynamicAnalysis>
    <sonar.jacoco.reportPath>${project.basedir}/../target/jacoco.exec</sonar.jacoco.reportPath>
    <sonar.language>java</sonar.language>
</properties>
```

属性sonar.jacoco.reportPath指定生成JaCoCo报告的位置。

### 3.3 JaCoCo的依赖和插件

JaCoCo Maven插件提供对JaCoCo运行时代理的访问，该代理记录执行覆盖率数据并创建代码覆盖率报告。

让我们将相关依赖项添加到pom.xml中：

```xml
<dependency>
    <groupId>org.jacoco</groupId> 
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.6</version>
</dependency>
```

接下来，让我们配置将我们的Maven项目与JaCoCo集成的插件：

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>${jacoco.version}</version>
    <executions>
        <execution>
            <id>jacoco-initialize</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>jacoco-site</id>
            <phase>package</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## 4. SonarQube实践

现在我们已经在pom.xml文件中定义了所需的依赖项和插件，我们将运行mvn clean install来构建我们的项目。

然后我们将**在运行命令mvn sonar:sonar之前启动SonarQube服务器**。

成功运行此命令后，它将为我们提供指向项目代码覆盖率报告仪表板的链接：

<img src="../assets/img.png">

请注意，它会在项目的target文件夹中创建一个名为jacoco.exec的文件。

该文件是SonarQube将进一步使用的代码覆盖的结果：

<img src="../assets/img_1.png">

它还在SonarQube门户中创建仪表板。

此仪表板显示覆盖率报告，其中包含我们代码中发现的所有问题、安全漏洞、可维护性指标和重复代码块：

<img src="../assets/img_2.png">

## 5. 总结

SonarQube和JaCoCo是我们可以一起使用的两个工具，可以轻松测量代码覆盖率。

它们还通过查找代码中的代码重复、错误和其他问题来提供源代码整体健康状况的概览。这有助于我们了解我们的代码是否可用于生产。