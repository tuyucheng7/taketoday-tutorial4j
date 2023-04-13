## 1. 概述

命名发布版本时通常使用[语义版本控制](https://www.baeldung.com/cs/semantic-versioning)。例如，这些规则适用于MAJOR.MINOR.REVISION等版本格式：

-   MAJOR：主要功能和潜在的重大更改
-   MINOR：向后兼容的功能
-   REVISION：向后兼容的修复和改进

与语义版本控制一起，项目通常使用标签来进一步阐明特定版本的状态。事实上，通过使用这些标签，我们可以提供有关构建生命周期或工件发布位置的提示。

在这篇简短的文章中，我们将研究主要Spring项目采用的版本命名方案。

## 2. Spring框架和Spring Boot

除了语义版本控制，我们还可以看到Spring Framework和Spring Boot使用这些标签：

-   BUILD-SNAPSHOT
-   M[number\]
-   RC[number\]
-   RELEASE

**BUILD-SNAPSHOT是当前的开发版本**。Spring团队每天都会构建此项目并将其部署到https://repo.spring.io/ui/native/snapshot。

**里程碑版本(M1、M2、M3...)标志着发布过程中的一个重要阶段**。团队在开发迭代完成时构建此工件并将其部署到https://repo.spring.io/ui/native/milestone。

**发布候选版本(RC1、RC2、RC3等)是构建最终版本之前的最后一步**。为了最大程度地减少代码更改，在此阶段应仅修复bug。它也部署到https://repo.spring.io/ui/native/milestone。

**在发布过程的最后，Spring团队会生成一个RELEASE**。因此，这通常是唯一可用于生产的工件。我们也可以将此版本称为GA，即General Availability。

**这些标签按字母顺序排列**，以确保构建和依赖管理器正确确定某个版本是否比另一个版本更新。例如，Maven 2错误地认为1.0-SNAPSHOT比1.0-RELEASE更新。Maven 3修复了这个行为。因此，当我们的命名方案不是最优时，我们可能会遇到奇怪的行为。

## 3. 伞形项目

伞形项目，如Spring Cloud和Spring Data，是独立但相关的子项目之上的项目。为避免与这些子项目发生冲突，伞形项目采用不同的命名方案。**每个Release Train都有一个特殊的名称，而不是编号版本**。

按字母顺序排列，伦敦地铁站是Spring Cloud发布名称的灵感来源-对于启动器来说是Angel、Brixton、Finchley、Greenwich和Hoxton。

除了上面显示的Spring标签外，**它还定义了一个Service Release标签(SR1, SR2...)**。如果我们发现严重错误，则可以生成服务版本。

重要的是要认识到Spring Cloud版本仅与特定的Spring Boot版本兼容。作为参考，[Spring Cloud项目页面](https://spring.io/projects/spring-cloud)包含兼容性表格。

## 4. 总结

如上所示，拥有清晰的版本命名方案很重要。虽然一些像里程碑或候选版本这样的版本可能是稳定的，但我们应该始终使用生产就绪的工件。你的命名方案是什么？它比这个有什么优势？