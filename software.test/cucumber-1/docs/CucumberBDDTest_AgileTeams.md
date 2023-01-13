## 1. 概述

敏捷软件方法论在快速发展的市场中越来越受软件开发团队的欢迎。然而，这种流行的趋势也迫使测试团队根据不断变化的需求来管理和维护他们的测试用例和测试脚本。
因此，人们应该从一开始就选择合适的测试方法，以便顺利地实现任何敏捷软件项目。在本文中，我们将讨论Cucumber BDD的特性以及以下主题：

+ Cucumber及其突出的特点：
    + 它有助于促进沟通。
    + 它是一种自动化验收测试工具。
    + 所有测试人员都可以使用Cucumber BDD参与自动化测试。

## 2. Cucumber及其突出特点

到目前为止，已经有许多成功的敏捷软件项目。多亏了使用Cucumber工具的行为驱动开发(BDD)方法。那么，Cucumber是什么?

Cucumber是一种用于运行以BDD格式创建的自动验收测试的工具。
该工具的一个显著特点是能够执行纯文本功能描述(用称为Gherkin的语言编写)作为自动化测试。

让我们看看下面的示例：

```gherkin
Feature: Update password

    Scenario: Admin user can update the user password

        Given I am in the HR system with an Admin account
        When I update password of another user
        Then I receive a message for updating password successfully
        And user password is updated to the new password
```

行为驱动开发(BDD)方法的这一特性具有以下优点：

+ 用一种广泛使用的语言编写BDD测试，这种语言的结构是围绕领域模型构建的，
  被所有团队成员(包括开发人员、测试人员、BAs和客户)广泛使用。
+ 将软件团队的技术人员与非技术人员联系起来。
+ 允许与开发人员的代码直接交互，但是我们用一种业务涉众也可以理解的语言编写BDD测试。
+ 最后，验收测试可以自动执行，而业务涉众则手动执行。

### 2.1 Cucumber有助于促进沟通

Cucumber帮助促进同一项目的技术成员和非技术成员之间的沟通。让我们看看下面的需求及其自动化测试：

```text
As an Admin User,
I would like to change the password of other user's accounts.
Feature: Update password

    Scenario: Admin user can update the user password
        Given I am in the HR system with an Admin account
        When I update password of another user
        Then I receive a message for updating password successfully
        And user's password is updated to the new password
```

通过JUnit，上面的测试场景可以实现如下：

```java
class UserAccountUnitTest {

    @Test
    void givenUserIsAdmin_whenUpdateUserAccountPassword_thenShouldSuccess() {
        // create users 
        User userAdmin = new User(UserRole.ADMIN, username, password);
        User user = new User(UserRole.VIEWER, user_username, user_password);

        // use Admin user to update another user password
        String message = userAdmin.updatePassword(user, user_new_password);

        // verify password changed
        assertEquals("Password changed successfully", message);
        assertEquals(user.getPassword(), user_new_password);
    }
}
```

我们可以使用Cucumber编写相同的测试用例：

```gherkin
Feature: Update password

    Scenario: Admin user can update the user password
        Given I am in the HR system with an Admin account
        When I update password of another user
        Then I receive a message for updating password successfully
        And user's password is updated to the new password
```

上面的两个自动化测试脚本都能很好地执行，自动完成测试。但是，你团队中的所有测试人员都进行了这些测试吗？
其他业务分析师和其他涉众是否有可能在验收测试(AT)阶段再次使用这些测试？

对于大多数手动测试人员和BAs来说，使用JUnit进行自动化测试可能很难赶上。
此外，不可能在AT中再次使用此测试。因此，基于前面提到的这些缺陷，这不能被认为是一种合适的方法。

相比之下，我们使用Cucumber以业务领域语言或自然语言开发/创建自动化测试，软件项目团队的所有成员都可以很容易地理解。
沟通对于任何开发团队都是至关重要的，尤其是在敏捷团队中。在开发人员和测试人员之间，通常会有很多持续的聊天、讨论，甚至争吵，以找出一个功能的正确行为是什么。
通过使用Cucumber，开发人员现在可以开发相同的功能规范，以供测试人员进行测试。它是一个强大的工具，因为它可以帮助降低误解和沟通失败的风险。

### 2.2 Cucumber是一种自动验收测试工具

验收测试通常由BAs/客户执行，以确保开发团队已经构建了特定的功能。这个测试阶段的一项常见任务是使用生产中具体的真实数据，根据原始需求验证系统。
Cucumber测试不仅遵循其测试场景的需求，还帮助BAs或产品经理快速调整测试数据。下面是一个稍作调整的演示：

```text
As an Admin User,
I would like to change the password of other user's accounts.
Feature: Update password

    Scenario: Admin user can update the user password
        Given I am in the HR system with an Admin account
        When I update password of another user
        Then I receive a message for updating password successfully
        And user's password is updated to the new password
```

我们使用Cucumber框架编写的自动化测试如下：

```gherkin
Feature: Update password

    Scenario Outline: Verify Updating user password feature
        Given I am in the HR system with "<account_type>" account
        And there is another user with "<old_password>" password
        When I update password of the user to "<new_password>"
        Then I got the message "<message>"
        And the user password should be "<final_password>"

        Examples:
            | account_type | old_password | new_password | message                | final_password |
            | Admin        | $Test123     | @Test123     | Password changed..     | @Test123       |
            | Viewer       | $Test123     | @Test123     | Invalid right access.. | $Test123       |
```

### 2.3 所有测试人员都可以参加Cucumber BDD的自动化测试

除了促进同一测试团队成员之间的沟通，Cucumber还有助于有效地利用测试人员的技能。每个组织都存在专业知识差距。
换句话说，某些测试人员可能在使用自动化测试的编程方面拥有丰富的技术专长，而另一些测试人员可能在同一个团队中使用有限的编程技能执行手动测试。
多亏了Cucumber，所有测试人员，不管他们的技能水平如何，都可以参与执行自动化测试的过程。

让我们看一下上面的例子：

+ 任何了解业务逻辑和工作流的测试人员都可以编写功能文件，添加更多的场景，并测试数据集。
+ 此外，任何具有基本编程知识和创建对象、访问属性、调用方法的专门知识的测试人员都可以生成步骤定义。
+ 任何具有较高编程技能水平的测试人员都可以参与构建框架、定义数据源连接等过程。

在实现Cucumber时仍然有几个重要的问题：

Cucumber使用业务领域知识帮助运行纯文本文件中提到的测试场景。因此，语言的使用和创建测试人的看法可能直接影响测试场景，从而导致误解的风险。
我们应该清楚地展示测试场景，并且它们的实现应该准确地执行每个步骤。例如，当你想验证谷歌上的搜索功能时，测试应该是：

```text
Scenario: performing a search on google
    Given I am on "www.google.com" site
    When I search for "Cucumber and BDD"
    Then ...
```

我们结合以下步骤进行以下测试：

```text
Scenario: performing a search on google
    When I search for "Cucumber and BDD"
    Then ...
```

Cucumber工具的各个阶段用一种普通语言执行。可以在各种测试场景中再次使用它们。它有助于减少创建测试的工作量。然而，保持测试的可读性和可重用性是一个很大的挑战。
如果测试的编写水平很高，任何涉众都可以理解，那么我们可以重用一些步骤。上述两个脚本都是正确的；
然而，第二个问题并不明显。因为它做的比预期的多得多：打开谷歌的网站，并使用指定的文本进行搜索。
比如，你希望扩展测试以搜索更多的文本，可以重复上面的步骤。因此，我们打开谷歌网站两次。如果不严格遵循需求，Cucumber测试工具迟早会引起误解，并且在扩展时很难维护。

```gherkin
Feature: Update password

    Scenario: Admin user can update the user password
        Given I am in the HR system with an Admin account
        When I update password of another user
        Then I receive a message for updating password successfully
        And user's password is updated to the new password

    Scenario: Viewer user cannot update the user password
        Given I am in the HR system with a Viewer account
        When I update password of another user
        Then I receive a message for not able to update the user password
        And user's password remains the same
```

相反，如果测试是通用的，并且我们可以重用来验证更新用户的姓氏，那么非技术涉众将很难跟上。此外，他们不能进行验收测试。

```text
Scenario: Admin user can update user password:
    Given I am in the "$System.HR_Page" with "admin@test.com" username and "$Test123" password
    And there is another user in "$System.HR_Page" with "user@test.com" username and "$Test123" password
    When I update "$UserTemplate.Password" of "user@test.com" user to"@Test123"
    And I save the response message as "response_message"
    Then "$response_message" should be "Password changed successfully"
    And the  "user@test.com" user's "$UserTemplate.Password" should be"@Test123"
```

在测试过程中，你必须定期调整测试场景。我们一直这样下去，直到它们达到一个完全可以接受的平衡，即所有成员都可以理解和重用。

```text
Scenario: Verify Updating user password feature
    Given I am in the HR system with "Admin" account
    And there is another user with "$Test123" password
    When I update password of the user to "@Test123"
    Then I got the message "Password changed successfully."
    And the user password should be "@Test123"
```

或者使用更多的测试数据：

```text
Scenario Outline: Verify Updating user password feature
    Given I am in the HR system with "<account_type>" account
    And there is another user with "<old_password>" password
    When I update password of the user to "<new_password>"
    Then I got the message "<message>"
    And the user password should be "<final_password>"

    Examples:
        | account_type | old_password | new_password | message                | final_password |
        | Admin        | $Test123     | @Test123     | Password changed..     | @Test123       |
        | Viewer       | $Test123     | @Test123     | Invalid right access.. | $Test123       |
```

## 3. 总结

对于想要开始使用Cucumber的测试团队，以下有几点需要重点注意：

+ 把自动化测试看作一个真正的项目一样重要。代码应该遵循编码实践、惯例等。
+ 还应该考虑合适的编辑器工具。这个编辑器应该帮助调试和编辑标准文本格式的feature文件。Aptana(免费编辑器)、
  RubyMine(商业编辑器)和Katalon Studio都是完全支持基于BDD的Cucumber的合适选项。
+ 最后，让feature文件成为一个实际的“通信”层，你可以在其中存储接收到的测试数据并格式化测试数据。它不包含域业务逻辑。

Cucumber是一个强大的工具，它在健壮的测试框架之上为我们提供了真正的通信层。该工具可以帮助从后端到前端在广泛的测试需求上运行自动化测试。
此外，Cucumber在测试团队成员之间建立了深厚的联系，这是我们在其他测试框架中很难找到的。
凭借多年的自动化测试经验，建议将Cucumber用于Web UI和Web服务测试，以一种有助于敏捷软件项目成功运行的方式实现。