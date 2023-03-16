## 1. 概述

Checkstyle是一个开源工具，它可以根据一组可配置的规则检查代码。

在本教程中，我们将了解**如何通过Maven和使用IDE插件将[Checkstyle](http://checkstyle.sourceforge.net/)集成到Java项目中**。

以下各节中提到的插件不相互依赖，可以单独集成到我们的构建或IDE中。例如，我们的pom.xml中不需要Maven插件来在我们的Eclipse IDE中运行验证。

## 2. Checkstyle Maven插件

### 2.1 Maven配置

要将Checkstyle添加到项目中，我们需要在pom.xml的reporting部分添加插件：

```xml
<reporting>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-checkstyle-plugin</artifactId>
            <version>3.0.0</version>
            <configuration>
                <configLocation>checkstyle.xml</configLocation>
            </configuration>
        </plugin>
    </plugins>
</reporting>
```

**这个插件带有两个预定义的检查，一个Sun风格的检查和一个Google风格的检查**。项目的默认检查是sun_checks.xml。

要使用我们的自定义配置，我们可以指定我们的配置文件，如上面的示例所示。使用此配置，插件现在将读取我们的自定义配置，而不是提供的默认配置。

该插件的最新版本可以在[Maven Central](https://central.sonatype.com/artifact/org.apache.maven.plugins/maven-checkstyle-plugin/3.2.1)上找到。

### 2.2 报告生成

现在我们的Maven插件已经配置完毕，我们可以通过运行mvn site命令为我们的代码生成报告。**构建完成后，该报告可以在target/site文件夹中找到，文件名称为checkstyle.html**。

Checkstyle报告分为三个主要部分：

**Files**：报告的这一部分为我们提供了**发生违规行为的文件列表**。它还向我们显示了违规行为的严重性级别。报告的Files部分如下所示：

<img src="../assets/img.png">

**Rules**：**报告的这一部分概述了用于检查违规行为的规则**。它显示了规则的类别、违规的数量和这些违规行为的严重程度。以下是显示Rules部分的报告示例：

<img src="../assets/img_1.png">

**Details**：最后，**报告的Details部分为我们提供了已发生违规行为的详细信息**。提供的详细信息是行号级别的。以下是报告的示例详细信息部分：

<img src="../assets/img_2.png">

### 2.3 构建集成

如果需要对编码风格进行严格的检查，**我们可以将插件配置为在代码不符合标准时构建失败**。

我们通过在插件定义中添加一个执行目标来做到这一点：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>${checkstyle-maven-plugin.version}</version>
    <configuration>
        <configLocation>checkstyle.xml</configLocation>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

configLocation属性定义了要参考哪个配置文件进行验证。

在我们的例子中，配置文件是checkstyle.xml。execution部分中指定的目标check要求插件在构建的verify阶段运行，并在发生违反编码标准时强制构建失败。

现在，如果我们运行mvn clean install命令，它将扫描文件是否存在违规行为，如果发现任何违规行为，构建将失败。

我们也可以使用mvn checkstyle:check只运行插件的check目标，而无需配置执行目标。如果存在任何验证错误，运行此步骤也会导致构建失败。

## 3. Eclipse插件

### 3.1 配置

就像Maven集成一样，Eclipse使我们能够使用我们的自定义配置。

要导入我们的配置，请转到Window -> Preferences -> Checkstyle。在“Global Check Configurations”部分，单击“NEW”。

这将打开一个对话框，为我们提供指定自定义配置文件的选项。

### 3.2 报告浏览

现在我们的插件已经配置好了，我们可以使用它来分析我们的代码。

要检查项目的编码风格，请在Eclipse Project Explorer中右键单击项目并选择CheckStyle -> Check Code with Checkstyle。

**该插件将在Eclipse文本编辑器中为我们提供有关Java代码的反馈**。它还将为项目生成违规报告，该报告可作为Eclipse中的视图使用。

要查看违规报告，请转到Window -> Show View -> Other，然后搜索Checkstyle。应显示“Violations”和“Violations Chart”选项。

选择任一选项都会向我们展示按类型分组的违规行为。以下是示例项目的违规饼状图：

<img src="../assets/img_16.png">

**单击饼状图的某个部分会将我们带到代码中实际违规的列表**。

或者，我们可以打开Eclipse IDE的Problem视图并检查插件报告的问题。

下面是Eclipse IDE的Problem视图示例：

<img src="../assets/img_17.png">

单击任何警告都会将我们带到发生违规的代码。

## 4. IntelliJ IDEA插件

### 4.1 配置

与Eclipse一样，IntelliJ IDEA也使我们能够在项目中使用我们自己的自定义配置。

在IDE中，打开Settings并搜索”Checkstyle“，将显示一个窗口，可以选择我们的checks。单击+按钮，将打开一个窗口，让我们指定要使用的文件的位置。

现在，我们选择一个配置XML文件并单击Next。这将打开上一个窗口并显示我们新添加的自定义配置选项。我们选择新配置并单击OK开始在我们的项目中使用它。

### 4.2 报告浏览

现在我们的插件已经配置好了，让我们使用它来检查违规行为。要检查特定项目的违规行为，请转到Analyze -> Inspect Code。

**从检查结果中可以查看Checkstyle部分下的违规情况**。下面是一个示例报告：

<img src="../assets/img_3.png">

单击违规将带我们到文件中发生违规的确切行。

## 5. 自定义Checkstyle配置

在Maven报告生成部分(第2.2节)，我们使用自定义配置文件来执行我们自己的编码标准检查。

如果我们不想使用打包的Google或Sun检查，**我们可以创建自己的自定义配置XML文件**。

以下是用于上述检查的自定义配置文件：

```xml
<!DOCTYPE module PUBLIC
        "-//Puppy Crawl//DTD Check Configuration 1.3//EN"
        "http://www.puppycrawl.com/dtds/configuration_1_3.dtd">
<module name="Checker">
    <module name="TreeWalker">
        <module name="AvoidStarImport">
            <property name="severity" value="warning"/>
        </module>
    </module>
</module>
```

### 5.1 DOCTYPE定义

DOCTYPE定义的第一行是文件的重要组成部分，它告诉从哪里下载DTD，以便系统可以理解配置。

**如果我们不在配置文件中包含此定义，则此文件不是有效的配置文件**。

### 5.2 模块

配置文件主要由模块组成。**一个模块有一个属性name，它代表模块的作用**。name属性的值对应于插件代码中的一个类，该类在插件运行时执行。

让我们了解上面配置中存在的不同模块。

### 5.3 模块详情

- **Checker**：模块在以Checker模块为根的树中结构化，此模块定义由配置的所有其他模块继承的属性。
- **TreeWalker**：此模块检查各个Java源文件并定义适用于检查此类文件的属性。
- **AvoidStarImport**：此模块为在我们的Java代码中不使用“*”导入设置了一个标准。它还有一个property定义，要求插件将此类问题的严重性报告为警告。因此，只要在代码中发现此类违规行为，都会针对它们发出警告。

要了解有关自定义配置的更多信息，请点击[此链接](http://checkstyle.sourceforge.net/config.html)。

## 6. Spring-Rest项目的报表分析

在本节中，我们将阐明Checkstyle所做的分析，使用上面第5节中创建的自定义配置，以[GitHub上的spring-rest项目](https://github.com/eugenp/tutorials/tree/master/testing-modules/testing-libraries)为例。

### 6.1 违规报告生成

我们已将配置导入到Intellij IDEA，下面是为该项目生成的违规报告：

<img src="../assets/img_4.png">

此处报告的警告指出，应避免在代码中使用通配符导入。我们有3个文件不符合这个标准。当我们单击警告时，它会将我们定向到存在违规的Java文件。

以下是SimplePostController.java文件如何显示报告的警告：

<img src="../assets/img_5.png" align="left">

### 6.2 问题解决

一般来说，使用“*”导入不是一个好的做法，因为当两个或多个包包含相同的类时，它可能会产生冲突。

例如，考虑List类，它在java.util和java.awt包中都存在。如果我们同时使用java.util.\*和java.awt.\*的导入，我们的编译器将无法编译代码，因为List在两个包中都可用。

**为了解决上述问题，我们可以通过导入单独的某个类然后保存代码**。现在，当我们再次运行插件时，我们不会再看到任何违规行为，并且我们的代码现在遵循自定义配置中设置的标准。

## 7. 总结

在本文中，我们介绍了在Java项目中集成Checkstyle的基础知识。

我们了解到，它是一个简单而强大的工具，用于确保开发人员遵守组织设定的编码标准。