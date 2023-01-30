## 1. 概述

[在本教程中，我们将探讨Oracle Java Development Kit](https://www.oracle.com/technetwork/java/index.html)和[OpenJDK](https://openjdk.java.net/)之间的区别。首先，我们将仔细研究它们中的每一个，然后我们将对它们进行比较。最后，我们将列出其他 JDK 实现。

## 2. Oracle JDK 和 Java SE 的历史

JDK(Java Development Kit)是用于Java平台编程的软件开发环境。它包含一个完整的 Java 运行时环境，即所谓的私有运行时。它之所以这样命名，是因为它包含比独立 JRE 更多的工具，以及开发 Java 应用程序所需的其他组件。

Oracle 强烈建议使用术语 JDK 来指代 Java SE(标准版)开发工具包(还有企业版和微型版平台)。

让我们来看看 Java SE 的历史：

-   JDK 测试版 – 1995
-   JDK 1.0 – 1996 年 1 月
-   JDK 1.1 – 1997 年 2 月
-   J2SE 1.2 – 1998 年 12 月
-   J2SE 1.3 – 2000 年 5 月
-   J2SE 1.4 – 2002 年 2 月
-   J2SE 5.0 – 2004 年 9 月
-   Java SE 6 – 2006 年 12 月
-   Java SE 7 – 2011 年 7 月
-   Java SE 8 (LTS) – 2014 年 3 月
-   Java SE 9 – 2017 年 9 月
-   Java SE 10 (18.3) – 2018 年 3 月
-   Java SE 11 (18.9 LTS) – 2018 年 9 月
-   Java SE 12 (19.3) – 2019 年 3 月

注意：不再支持斜体版本。

我们可以看到，Java SE 的主要版本大约每两年发布一次，直到 Java SE 7。从 Java SE 6 到 Java SE 6 花了五年时间，再过三年到 Java SE 8。

自 Java SE 10 以来，我们开始期待每六个月发布一次新版本。但是，并非所有版本都是长期支持 (LTS) 版本。由于 Oracle 的发布计划，LTS 产品发布只会每三年发布一次。

Java SE 11 是最新的 LTS 版本，而 Java SE 8 将在 2020 年 12 月之前获得免费公开更新，用于非商业用途。

这个开发包是在2010年甲骨文收购Sun Microsystems后得到现在的名字的，之前的名字是SUN JDK，是Java编程语言的官方实现。

## 3. OpenJDK

OpenJDK 是 Java SE 平台版的免费开源实现。它最初于 2007 年发布，是 Sun Microsystems 于 2006 年开始开发的结果。

需要强调的是，OpenJDK 是从 SE 7 版本开始的一个 Java 标准版的官方参考实现。

最初，它仅基于 JDK 7，但从Java 10 开始，Java SE 平台的开源参考实现由[JDK 项目](https://openjdk.java.net/projects/jdk/)负责。而且，就像 Oracle 一样，JDK 项目也将每六个月发布一次新功能。

我们应该注意到，在这个长期运行的项目之前，有一些 JDK 发布项目发布了一个功能，然后就停止了。

现在让我们检查一下 OpenJDK 版本：

-   OpenJDK 6 项目——基于 JDK 7，但经过修改以提供 Java 6 的开源版本
-   OpenJDK 7 项目——2011 年 7 月 28 日
-   OpenJDK 7u 项目——该项目开发 Java Development Kit 7 的更新
-   OpenJDK 8 项目 – 2014 年 3 月 18 日
-   OpenJDK 8u 项目——该项目开发 Java Development Kit 8 的更新
-   OpenJDK 9 项目——2017 年 9 月 21 日
-   JDK 项目发布 2018 年 3 月 10 日至 20 日
-   JDK 项目发布 2018 年 9 月 11 日至 25 日
-   JDK 项目发布 12 –[稳定阶段](https://openjdk.java.net/jeps/3)

## 4. 甲骨文 JDK 与 OpenJDK

在本节中，我们将重点介绍 Oracle JDK 和 OpenJDK 之间的主要区别。

### 4.1. 发布时间表

正如我们提到的，Oracle 将每三年发布一次版本，而OpenJDK 将每六个月发布一次。

Oracle 为其版本提供长期支持。另一方面，OpenJDK 只支持对一个版本的更改，直到下一个版本发布。

### 4.2. 执照

Oracle JDK 根据 Oracle 二进制代码许可协议获得许可，而OpenJDK 拥有 GNU 通用公共许可证 (GNU GPL) 第 2 版，但链接例外。

使用 Oracle 的平台时有一些许可方面的问题。[正如 Oracle宣布](https://java.com/en/download/release_notice.jsp)的那样，如果没有商业许可，2019 年 1 月之后发布的 Oracle Java SE 8 的公共更新将无法用于商业、商业或生产用途。但是，OpenJDK 是完全开源的，可以自由使用。

### 4.3. 表现

两者之间没有真正的技术差异，因为 Oracle JDK 的构建过程基于 OpenJDK 的构建过程。

就性能而言，Oracle 在响应能力和 JVM 性能方面要好得多。由于它对企业客户的重视，它更加关注稳定性。

相比之下，OpenJDK 更频繁地发布版本。结果，我们可能会遇到不稳定的问题。根据[社区反馈](https://www.reddit.com/r/java/comments/6g86p9/openjdk_vs_oraclejdk_which_are_you_using/)，我们知道一些 OpenJDK 用户遇到了性能问题。

### 4.4. 特征

如果我们比较功能和选项，我们会发现 Oracle 产品具有 Flight Recorder、Java Mission Control 和 Application Class-Data Sharing 功能，而OpenJDK 具有 Font Renderer 功能。

此外，Oracle 有更多垃圾收集选项和更好的渲染器。

### 4.5. 发展与普及

Oracle JDK 完全由 Oracle 公司开发，而OpenJDK 由 Oracle、OpenJDK 和 Java Community 共同开发。但是，Red Hat、Azul Systems、IBM、Apple Inc. 和 SAP AG 等一流公司也积极参与其开发。

正如我们从上一小节的链接中看到的那样，当谈到在其工具中使用 Java 开发工具包的顶级公司(例如 Android Studio 或 IntelliJ IDEA)的受欢迎程度时，Oracle JDK 曾经是更受欢迎的，但两者都这些公司已经切换到基于 OpenJDK 的 JetBrains[构建](https://bintray.com/jetbrains/intellij-jdk/)。

此外，主要的 Linux 发行版(Fedora、Ubuntu、Red Hat Enterprise Linux)提供 OpenJDK 作为默认的 Java SE 实现。

## 5. 自 Java 11 以来的变化

正如我们在 [Oracle 的博文](https://blogs.oracle.com/java-platform-group/oracle-jdk-releases-for-java-11-and-later)中看到的那样，从 Java 11 开始有一些重要的变化。

首先，当将[Oracle ](https://openjdk.java.net/legal/gplv2+ce.html)[JDK](https://www.oracle.com/technetwork/java/javase/terms/license/index.html)作为Oracle产品的一部分使用或服务，或者当开源软件不受欢迎时。

每个许可证将有不同的构建，但它们在功能上是相同的，只有一些外观和包装上的差异。

此外，传统的“商业功能”，例如飞行记录器、Java 任务控制和应用程序类数据共享，以及 Z 垃圾收集器，现在都可以在 OpenJDK 中使用。因此，从 Java 11 开始，Oracle JDK 和 OpenJDK 构建本质上是相同的。

让我们看看主要区别：

-   Oracle 的 Java 11 套件在使用-XX:+UnlockCommercialFeatures选项时发出警告，而在 OpenJDK 构建中，此选项会导致错误
-   Oracle JDK 提供了一个配置来向“Advanced Management Console”工具提供使用日志数据
-   Oracle 一直要求第三方加密提供程序由已知证书签名，而 OpenJDK 中的加密框架具有开放的加密接口，这意味着可以使用哪些提供程序没有限制
-   Oracle JDK 11 将继续包括安装程序、品牌和 JRE 打包，而 OpenJDK 构建目前以zip和tar.gz文件形式提供
-   由于Oracle 版本中存在一些附加模块，javac –release命令对于 Java 9 和 Java 10 目标的行为不同
-   java –version和java -fullversion命令的输出将区分 Oracle 构建与 OpenJDK 构建

## 6. 其他 JDK 实现

现在让我们快速浏览一下其他活跃的 Java Development Kit 实现。

### 6.1. 免费和开源

以下按字母顺序列出的实现是开源的，可以免费使用：

-   采用OpenJDK
-   亚马逊正确
-   祖鲁蓝
-   Bck2Brwsr
-   可可
-   代号一
-   双JVM
-   Eclipse OpenJ9
-   GraalVM CE
-   这不是虚拟机
-   热点
-   全部的
-   虚拟机
-   果胶 JVM
-   Jikes RVM(Jikes 研究虚拟机)
-   JVM.go
-   利比里亚JDK
-   远的
-   玛克辛
-   多操作系统引擎
-   绳子虚拟机
-   虚拟机

### 6.2. 专有实施

还有受版权保护的实现：

-   Azul Zing JVM
-   中东欧-日本
-   怡东喷射机
-   虚拟机EE
-   艾姆斯公司
-   牙买加VM (aicas)
-   JBlend(Aplix)
-   MicroJvm(IS2T——工业智能软件技术)
-   OJVM
-   PTC最小值
-   SAP虚拟机
-   用于 Java 的 Waratek CloudVM

除了上面列出的[活动实现之外，我们还可以看到](https://en.wikipedia.org/wiki/List_of_Java_virtual_machines#Active)[非活动实现](https://en.wikipedia.org/wiki/List_of_Java_virtual_machines#Inactive)的列表 和每个实现的简短描述。

## 七、总结

在本文中，我们重点介绍了当今最流行的两种 Java 开发工具包。

首先，我们描述了它们中的每一个，然后我们强调了它们之间的区别。我们还特别关注了自 Java 11 以来的变化和差异。最后，我们列出了目前可用的其他活跃实现。