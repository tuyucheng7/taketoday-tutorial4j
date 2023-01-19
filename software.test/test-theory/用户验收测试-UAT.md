用户验收测试 - UAT是一种由客户执行的测试，用于证明系统是否符合商定的要求。此测试发生在测试的最后阶段，然后将软件应用程序移至市场或生产环境。此类测试由客户在单独的环境(类似于生产环境)中执行，并确认系统是否满足要求规范的要求。UAT 在[系统测试](https://toolsqa.com/software-testing/istqb/system-testing/)完成并且所有或大部分主要[缺陷](https://toolsqa.com/software-testing/difference-between-error-mistake-fault-bug-failure-defect/)已修复后执行。

## 用户验收测试 - UAT

![用户验收测试 - UAT](https://toolsqa.com/gallery/Software%20testing/1.User%20Acceptance%20Testing%20-%20UAT.png)

图片来源：[测试小屋](https://blog.testlodge.com/what-is-user-acceptance-testing/)

定义：ISTQB 将验收定义为：针对用户需求、要求和业务流程进行的正式测试，以确定系统是否满足验收标准，并使用户、客户或其他授权实体能够确定是否接受系统。

用户验收测试也称为最终用户测试、验收测试和操作验收测试( OAT )。

验收测试是[黑盒测试](https://toolsqa.com/software-testing/black-box-testing/)，这意味着 UAT 用户不知道代码的内部结构。他们只是指定系统的输入并检查系统是否以正确的结果响应。

### 用户验收测试的类型

-   [Alpha 测试](https://toolsqa.com/software-testing/difference-between-alpha-testing-and-beta-testing/)：Alpha 测试在现场完成，因此开发人员和业务分析师都参与了测试团队。
-   [Beta 测试](https://toolsqa.com/software-testing/difference-between-alpha-testing-and-beta-testing/) ：Beta 测试由真实用户或客户在客户端完成，因此开发人员和业务分析师根本不参与其中。

### 谁执行 UAT？

-   客户：UAT 由组织的客户执行。他们是要求组织开发软件的人。
-   最终用户：UAT 由软件的最终用户执行。他们可以是客户自己，也可以是客户的客户。

### 用户验收测试的需求：

1.  需要进行验收测试，因为开发人员制作的软件是他们对需求的“自己”理解，实际上可能不是客户需要的。
2.  项目过程中的需求变更可能无法有效地传达给开发人员。

## UAT 是如何执行的？

### 用户验收测试的先决条件：

-   业务需求必须可用
-   应充分开发应用程序代码
-   应完成[单元测试](https://toolsqa.com/software-testing/unit-testing/)、[集成测试和系统测试](https://toolsqa.com/software-testing/integration-testing/)
-   [回归测试](https://toolsqa.com/software-testing/regression-testing/)应在没有重大缺陷的情况下完成
-   所有报告的缺陷都应在 UAT 之前修复和测试
-   应完成所有测试的可追溯性矩阵
-   UAT 环境必须准备好

### 用户验收测试流程：

UAT 由系统或软件的预期用户完成。此测试通常发生在客户端位置，称为[Beta 测试](https://toolsqa.com/software-testing/difference-between-alpha-testing-and-beta-testing/)。一旦满足 UAT 的进入标准，测试人员需要执行以下任务：

创建 UAT 计划：UAT 测试计划概述了用于验证和确保应用程序满足其业务需求的策略。

-   [它记录了 UAT、测试场景](https://toolsqa.com/software-testing/test-scenario/)和测试用例方法和测试时间表的进入和退出标准。日期、环境、参与者(谁)、角色和职责、结果及其分析过程 - 以及任何其他相关内容都将在 UAT 测试计划中找到。
-   此外，无论 QA 团队是否参与、部分参与或根本不参与 UAT，我们的工作就是计划这个阶段。

UAT 设计：在此步骤中使用从用户收集的验收标准。根据标准，QA 团队为用户提供 UAT 测试用例列表。

-   识别测试场景和测试用例：识别与高级业务流程相关的测试场景，并创建具有清晰测试步骤的测试用例。测试用例应该足以涵盖大多数 UAT 场景。业务用例是用于创建测试用例的输入。
-   测试数据的准备：最好使用 UAT 的实时数据。出于隐私和安全原因，数据应该被加扰。测试人员应该熟悉数据库流程。

UAT 测试执行：执行测试用例并报告错误(如果有)。修复后重新测试错误。测试用例帮助团队在 UAT 环境中有效地测试应用程序。UAT 发生在会议室或作战室之类的设置中，用户和 QA 团队代表坐在一起处理所有验收测试用例。一旦运行了所有测试并获得了结果，就会做出验收决定。这也称为Go/No-Go 决策。如果用户满意，那就是 Go，或者是 No-go。

确认满足业务目标：业务分析师或 UAT 测试人员需要在 UAT 测试后发送签字邮件。签收后，产品即可投入生产。UAT 测试的可交付成果是测试计划、UAT 场景和测试用例、测试结果和缺陷日志。

UAT 的退出标准：在投入生产之前，需要考虑以下几点：

-   没有出现严重缺陷
-   业务流程运行良好
-   UAT与所有利益相关者签署会议