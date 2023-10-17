## 1. 简介

[Cucumber](https://www.baeldung.com/cucumber-rest-api-testing)是一个支持行为驱动开发(BDD))的测试自动化工具，它运行以描述系统行为的纯文本Gherkin语法编写的Feature文件。

在本教程中，我们介绍将Cucumber与Gradle集成的方法，以便在项目构建中运行BDD Feature。

## 2. 设置

首先，我们使用Gradle Wrapper构建一个Gradle项目。接下来，我们将[cucumber-java](https://search.maven.org/artifact/io.cucumber/cucumber-java)依赖项添加到build.gradle：

```groovy
testImplementation 'io.cucumber:cucumber-java:7.0.0'
```

## 3. 使用自定义任务运行

为了使用Gradle运行我们的Feature，))我们将创建一个使用Cucumber的CLI的任务))。

### 3.1 配置

首先我们将所需的配置添加到项目的build.gradle文件中：

```groovy
configurations {
    cucumberRuntime {
        extendsFrom testImplementation
    }
}
```

接下来，我们编写自定义cucumberCli任务：

```groovy
task cucumberCli() {
    dependsOn assemble, testClasses
    doLast {
        javaexec {
            main = "io.cucumber.core.cli.Main"
            classpath = configurations.cucumberRuntime + sourceSets.main.output + sourceSets.test.output
            args = [
                    '--plugin', 'pretty',
                    '--plugin', 'html:target/cucumber-report.html',
                    '--glue', 'cn.tuyucheng.taketoday.cucumber',
                    'src/test/resources']
        }
    }
}
```

此任务配置为运行src/test/resources目录下的.feature文件中找到的所有测试场景。

Main类的–glue参数指定运行场景所需的步骤定义文件的位置。

–plugin参数指定测试报告的格式和位置。我们可以组合几个值以生成所需格式的报告，例如pretty和HTML，如我们的示例所示。

还有[其他参数](https://github.com/cucumber/cucumber-jvm/blob/main/core/src/main/resources/io/cucumber/core/options/USAGE.txt)可供配置，例如，有一些参数可以根据名称和标签过滤测试。

### 3.2 Scenario

现在，我们在src/test/resources/features/account_credited.feature文件中为我们的应用程序实现一个简单的场景：

```gherkin
Feature: Account is credited with amount

    Scenario: Credit amount
        Given account balance is 0.0
        When the account is credited with 10.0
        Then account should have a balance of 10.0
```

接下来，我们实现运行场景所需的相应步骤定义类：

```java
public class StepDefinitions {

	private Account account;

	@Given("account balance is {double}")
	public void givenAccountBalance(Double initialBalance) {
		account = new Account(initialBalance);
	}

	@When("the account is credited with {double}")
	public void whenAccountIsCredited(Double amount) {
		account.credit(amount);
	}

	@Then("account should have a balance of {double}")
	public void thenAccountShouldHaveBalance(Double expectedBalance) {
		assertEquals(expectedBalance, account.getBalance());
	}
}

// Unit Class
public class Account {

	private Double balance;

	public Account(Double initialBalance) {
		this.balance = initialBalance;
	}

	public void credit(Double amount) {
		balance += amount;
	}

	public Double getBalance() {
		return balance;
	}
}
```

### 3.3 运行任务

最后，让我们从命令行运行我们的cucumberCli任务：

```shell
$ gradle cucumberCli

> Task :cucumberCli

Scenario: Credit amount                      # src/test/resources/features/account_credited.feature:3
  Given account balance is 0.0               # cn.tuyucheng.taketoday.cucumber.StepDefinitions.givenAccountBalance(java.lang.Double)
  When the account is credited with 10.0     # cn.tuyucheng.taketoday.cucumber.StepDefinitions.whenAccountIsCredited(java.lang.Double)
  Then account should have a balance of 10.0 # cn.tuyucheng.taketoday.cucumber.StepDefinitions.thenAccountShouldHaveBalance(java.lang.Double)

1 Scenarios (1 passed)
3 Steps (3 passed)
0m0.208s
```

可以看到，我们的Feature文件已经与Gradle集成，运行成功，输出显示在控制台上。此外，HTML格式的Cucumber测试报告可在build/cucumber文件夹中找到。

## 4. 使用JUnit运行

我们也可以使用JUnit来运行Cucumber场景，而不是在Gradle中创建自定义任务。

首先我们需要添加[cucumber-junit](https://search.maven.org/artifact/io.cucumber/cucumber-junit)依赖项：

```groovy
testImplementation 'io.cucumber:cucumber-junit:7.0.0'
```

由于我们使用的是JUnit 5，我们还需要添加[junit-vintage-engine](https://search.maven.org/artifact/org.junit.vintage/junit-vintage-engine)依赖项：

```groovy
testImplementation 'org.junit.vintage:junit-vintage-engine:5.8.1'
```

接下来，我们在测试文件夹中创建一个空的测试启动类：

```java
@RunWith(Cucumber.class)
@CucumberOptions(
		plugin = {"pretty", "html:build/cucumber/cucumber-report.html"},
		features = {"src/test/resources"}
)
public class RunCucumberTest {

}
```

在这里，我们在@RunWith注解中使用了JUnit Cucumber Runner。此外，所有CLI Runner参数，例如features和plugin，都可以通过[@CucumberOptions](https://github.com/cucumber/cucumber-jvm/blob/main/junit/src/main/java/io/cucumber/junit/CucumberOptions.java)注解指定。

现在，))执行标准的gradle test任务会找到并运行所有功能测试，以及任何其他单元测试))：

```shell
$ gradle test

> Task :test

RunCucumberTest > Account is credited with amount > cn.tuyucheng.taketoday.cucumber.RunCucumberTest.Credit amount PASSED

BUILD SUCCESSFUL in 2s
```

## 5. 使用插件运行

最后一种方法是使用第三方插件，))该插件提供了从Gradle构建运行Feature文件的能力))。

在我们的示例中，我们将使用[gradle-cucumber-runner](https://github.com/tsundberg/gradle-cucumber-runner)插件来运行Cucumber JVM。在背后，这会将所有调用转发到我们之前使用的CLI Runner。

让我们将它包含在我们的项目中：

```groovy
plugins {
    id "se.thinkcode.cucumber-runner" version "0.0.8"
}
```

这会在我们的构建中添加一个cucumber任务，现在我们可以使用默认设置运行它：

```shell
$ gradle cucumber
```

值得注意的是，这不是官方的Cucumber插件，还有其他提供类似功能的插件。

## 6. 总结

在本文中，我们演示了几种使用Gradle配置和运行BDD测试的方法。

最初，我们介绍了如何使用CLI Runner创建自定义任务。然后，我们演示了使用Cucumber JUnit Runner使用现有的Gradle任务执行Feature文件。最后，我们使用第三方插件来运行Cucumber，而无需创建我们自己的自定义任务。