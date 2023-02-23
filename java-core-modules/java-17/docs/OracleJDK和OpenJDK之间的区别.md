## 1. 概述

在本教程中，我们将探讨[Oracle JDK(Java Development Kit)](https://www.oracle.com/technetwork/java/index.html)和[OpenJDK](https://openjdk.java.net/)之间的区别。首先，我们将仔细研究它们中的每一个，然后我们将对它们进行比较。最后，我们将列出其他JDK实现。

## 2. Oracle JDK和Java SE的历史

JDK(Java Development Kit)是用于Java平台编程的软件开发环境。它包含一个完整的Java运行时环境，即所谓的私有运行时。它之所以这样命名，是因为它包含比独立JRE更多的工具，以及开发Java应用程序所需的其他组件。

Oracle强烈建议使用术语JDK来指代Java SE(标准版)开发工具包(还有企业版和微型版平台)。

让我们来看看Java SE的历史：

-   *JDK Beta：1995*
-   *JDK 1.0：1996年1月*
-   *JDK 1.1：1997年2月*
-   *J2SE 1.2：1998年12月*
-   *J2SE 1.3：2000年5月*
-   *J2SE 1.4：2002年2月*
-   *J2SE 5.0：2004年9月*
-   *Java SE 6：2006年12月*
-   *Java SE 7：2011年7月*
-   Java SE 8(LTS)：2014年3月
-   *Java SE 9：2017年9月*
-   *Java SE 10(18.3)：2018年3月*
-   Java SE 11(18.9 LTS)：2018年9月
-   *Java SE 12(19.3)：2019年3月*
-   *Java SE 13：2019年9月*
-   *Java SE 14：2020年3月*
-   *Java SE 15：2020年9月*
-   *Java SE 16：2021年3月*
-   Java SE 17(LTS)：2021年9月
-   *Java SE 18：2022年3月*
-   Java SE 19：2022年9月
-   Java SE 20：2023年3月
-   Java SE 21(LTS)：2023年9月

注意：不再支持斜体版本。

我们可以看到，Java SE的主要版本大约每两年发布一次，直到Java SE 7。从Java SE 6到Java SE 7花了五年时间，再过三年到Java SE 8。

自Java SE 10以来，我们开始期待每六个月发布一次新版本。但是，并非所有版本都是长期支持(LTS)版本。由于Oracle的发布计划，LTS产品发布只会每三年发布一次。

Java SE 17是最新的LTS版本，而Java SE 8在2020年12月之前获得了免费的公共更新，用于非商业用途。

这个开发包是在2010年甲骨文收购Sun Microsystems后得到现在的名字的，在此之前，它的名字是SUN JDK，是Java编程语言的官方实现。

## 3. OpenJDK

OpenJDK是Java SE平台版的免费开源实现。它最初于2007年发布，是Sun Microsystems于2006年开始开发的结果。

需要强调的是，OpenJDK是从SE 7版本开始的一个Java标准版的官方参考实现。

最初，它仅基于JDK 7，但从Java 10开始，Java SE平台的开源参考实现由[JDK项目](https://openjdk.java.net/projects/jdk/)负责。而且，就像Oracle一样，JDK项目也将每六个月发布一次新功能。

我们应该注意到，在这个长期运行的项目之前，有一些JDK发布项目发布了一个功能，然后就停止了。

现在让我们看看OpenJDK版本：

-   OpenJDK 6项目：基于JDK 7，但经过修改以提供Java 6的开源版本
-   OpenJDK 7项目：2011年7月28日
-   OpenJDK 7u项目：该项目开发Java Development Kit 7的更新
-   OpenJDK 8项目：2014年3月18日
-   OpenJDK 8u项目：该项目开发Java Development Kit 8的更新
-   OpenJDK 9项目：2017年9月21日
-   JDK 项目发布 2018年3月10日至20日
-   JDK 项目发布 2018年9月11日至25日
-   JDK 项目发布 12：[稳定阶段](https://openjdk.java.net/jeps/3)

## 4. Oracle JDK与OpenJDK

在本节中，我们将重点介绍Oracle JDK和OpenJDK之间的主要区别。

### 4.1 发布时间表

正如我们提到的，Oracle将每三年发布一次版本，而OpenJDK将每六个月发布一次。

Oracle为其版本提供长期支持。另一方面，OpenJDK只支持对一个版本的更改，直到下一个版本发布。

### 4.2 许可证

Oracle JDK根据Oracle二进制代码许可协议获得许可，而OpenJDK拥有GNU通用公共许可证(GNU GPL)第2版，但链接例外。

使用Oracle的平台时有一些许可方面的问题。[正如Oracle宣布的](https://java.com/en/download/release_notice.jsp)那样，如果没有商业许可，2019年1月之后发布的Oracle Java SE 8的公共更新将无法用于商业、商业或生产用途。但是，OpenJDK是完全开源的，可以自由使用。

### 4.3 表现

两者之间没有真正的技术差异，因为Oracle JDK的构建过程基于OpenJDK的构建过程。

就性能而言，Oracle在响应能力和JVM性能方面要好得多。由于它对企业客户的重视，它更加关注稳定性。

相比之下，OpenJDK更频繁地发布版本。结果，我们可能会遇到不稳定的问题。根据[社区反馈](https://www.reddit.com/r/java/comments/6g86p9/openjdk_vs_oraclejdk_which_are_you_using/)，我们知道一些OpenJDK用户遇到了性能问题。

### 4.4 特征

如果我们比较功能和选项，我们会发现Oracle产品具有Flight Recorder、Java Mission Control和Application Class-Data Sharing功能，而OpenJDK具有Font Renderer功能。

此外，Oracle有更多垃圾收集选项和更好的渲染器。

### 4.5 发展与普及

Oracle JDK完全由Oracle公司开发，而OpenJDK由Oracle、OpenJDK和Java社区共同开发。但是，Red Hat、Azul Systems、IBM、Apple Inc和SAP AG等一流公司也积极参与其开发。

正如我们从上一小节的链接中看到的那样，当谈到在其工具中使用Java开发工具包的顶级公司(例如Android Studio或IntelliJ IDEA)的受欢迎程度时，Oracle JDK曾经是更受欢迎的，但两者都这些公司已经切换到基于OpenJDK的JetBrains[构建](https://bintray.com/jetbrains/intellij-jdk/)。

此外，主要的Linux发行版(Fedora、Ubuntu、Red Hat Enterprise Linux)提供OpenJDK作为默认的Java SE实现。

## 5. 自Java11以来的变化

正如我们在Oracle的[博文](https://blogs.oracle.com/java-platform-group/oracle-jdk-releases-for-java-11-and-later)中看到的那样，从Java 11开始有一些重要的变化。

首先，当将Oracle [JDK](https://openjdk.java.net/legal/gplv2+ce.html)作为Oracle产品的一部分使用[或](https://www.oracle.com/technetwork/java/javase/terms/license/index.html)服务，或者当开源软件不受欢迎时。

每个许可证将有不同的构建，但它们在功能上是相同的，只有一些外观和包装上的差异。

此外，传统的“商业功能”，例如飞行记录器、Java任务控制和应用程序类数据共享，以及Z垃圾收集器，现在都可以在OpenJDK中使用。因此，从Java 11开始，Oracle JDK和OpenJDK构建本质上是相同的。

让我们看看主要区别：

-   Oracle 的Java11 套件在使用-XX:+UnlockCommercialFeatures选项时发出警告 ，而在 OpenJDK 构建中，此选项会导致错误
-   Oracle JDK 提供了一个配置来向“Advanced Management Console”工具提供使用日志数据
-   Oracle 一直要求第三方加密提供程序由已知证书签名，而 OpenJDK 中的加密框架具有开放的加密接口，这意味着可以使用哪些提供程序没有限制
-   Oracle JDK 11 将继续包括安装程序、品牌和 JRE 打包，而 OpenJDK 构建目前以 zip 和 tar.gz 文件形式提供
-    由于 Oracle 版本中存在一些附加模块，javac –release命令 对于Java9 和Java10 目标的行为不同
-   java –version 和 java -fullversion命令的输出 将区分 Oracle 构建与 OpenJDK 构建

## 6. 其他JDK实现

现在让我们快速浏览一下其他活跃的JDK实现。

### 6.1 免费和开源

以下按字母顺序列出的实现是开源的，可以免费使用：

-   AdoptOpenJDK
-   Amazon Corretto
-   Azul Zulu
-   Bck2Brwsr
-   CACAO
-   Codename One
-   DoppioJVM
-   Eclipse OpenJ9
-   GraalVM CE
-   HaikuVM
-   HotSpot
-   Jamiga
-   JamVM
-   Jelatine JVM
-   Jikes RVM (Jikes Research Virtual Machine)
-   JVM.go
-   Liberica JDK
-   leJOS
-   Maxine
-   MicroSoft JVM
-   Multi-OS Engine
-   RopeVM
-   uJVM

### 6.2 专有实施

还有受版权保护的实现：

-   Azul Zing JVM
-   CEE-J
-   Excelsior JET
-   GraalVM EE
-   Imsys AB
-   JamaicaVM(aicas)
-   JBlend(Aplix)
-   MicroJvm(IS2T – Industrial Smart Software Technology)
-   OJVM
-   PTC Perc
-   SAP JVM
-   Waratek CloudVM for Java

除了上面列出的[活动实现之外，我们还可以看到](https://en.wikipedia.org/wiki/List_of_Java_virtual_machines#Active)[非活动实现](https://en.wikipedia.org/wiki/List_of_Java_virtual_machines#Inactive) 的列表 和每个实现的简短描述。

### 6.3 Amazon Corretto JDK和OpenJDK之间的区别

Java开发工具包的一种流行实现是Amazon Corretto。尽管它基于OpenJDK，但它们之间存在一些差异。

Amazon Corretto是OpenJDK的免费、多平台、生产就绪型发行版。它由Amazon Web Services(AWS)开发、维护和支持，专为在AWS和其他云平台上运行Java应用程序而设计。Amazon Corretto免费提供长期支持(LTS)和安全更新。

OpenJDK和Amazon Corretto JDK之间的另一个主要区别是Amazon Corretto包括专门设计用于提高生产环境中的性能、安全性和可靠性的额外功能和优化。

总而言之，OpenJDK和Amazon Corretto JDK都是JDK的实现，但它们的设计目的不同。OpenJDK是一种开源开发工具，而Amazon Corretto JDK是OpenJDK的生产就绪分发版，专为在云环境中使用而设计，提供免费的长期支持。

## 7. 总结

在本文中，我们重点介绍了当今最流行的两种Java开发工具包。

首先，我们描述了它们中的每一个，然后我们强调了它们之间的区别。我们还特别关注了自Java 11以来的变化和差异。最后，我们列出了目前可用的其他活跃实现。