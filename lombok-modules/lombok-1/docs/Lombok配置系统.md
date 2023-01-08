## 1. 简介

在本教程中，我们将讨论 Lombok 的配置参数。我们将讨论许多不同的选项以及如何正确设置我们的配置。

## 2. 配置概述

[Lombok](https://www.baeldung.com/intro-to-project-lombok)是一个库，可以帮助我们消除Java应用程序的几乎所有标准样板。我们将测试许多属性和配置。首先是添加 [Lombok](https://search.maven.org/search?q=a:lombok AND g: org.projectlombok)依赖项：

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.20</version>
    <scope>provided</scope>
</dependency>
```

Lombok 的配置系统为我们提供了许多有价值的设置，这些设置在我们项目的所有组件中经常是相同的。然而，它也让我们改变或自定义 Lombok 的行为，有时甚至定义了所有可用功能中可以使用或不能使用的功能。例如，如果使用任何实验性功能，我们可以告诉 Lombok 显示警告或错误。

要开始定义或自定义 Lombok 的行为，我们必须创建一个名为lombok.config 的文件。这个文件可以放在我们的项目、源代码或任何包的根目录下。创建后，子目录中的所有源文件都将继承此类文件中定义的配置。可以有多个配置文件。例如，我们可以在我们的根目录中定义一个具有一般属性的配置文件，并在给定的包中创建另一个定义其他属性的配置文件。

新配置将影响给定包的所有类和所有子包。此外，在同一属性的多个定义的情况下，更接近类或成员的定义优先。

## 三、基本配置

首先要提到的事情之一是要讨论的功能属性太多。因此，我们只会看到最常见的。要检查可用选项，让我们转到[Lombok 的页面](https://projectlombok.org/download)，下载 jar，然后在终端中运行以下命令：

```bash
java -jar lombok.jar config -g --verbose

```

因此，我们将看到所有属性及其可能值的完整列表以及解释其目标的简短描述。

现在，让我们看一个典型的lombok.config文件：

```properties
config.stopBubbling = true
lombok.anyconstructor.addconstructorproperties = false
lombok.addLombokGeneratedAnnotation = true
lombok.experimental.flagUsage = WARNING

# ... more properties

```

文件中使用的属性仅用于说明目的。我们稍后会讨论它们。但是在这里，我们可以观察到 Lombok 属性的格式及其定义。

让我们从config.stopBubbling 属性开始——这个选项告诉配置系统不要在父目录中搜索配置文件。将此属性添加到工作区或项目的根目录是一种很好的做法。默认情况下，它的值为false。

## 四、主要性能

### 4.1. 全局配置键

全局配置键是可能影响许多配置系统本身的配置。接下来，我们将看到此类键的一些示例。

我们要讨论的第一个键是lombok.anyConstructor.addConstructorProperties。 它将@java.beans.ConstructorProperties注解添加到所有带参数的构造函数。通常，在构造函数上使用反射的框架需要此注解来映射属性并知道构造函数中参数的正确顺序。这是 Lombok 版本中的代码：

```java
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Account {
    private double balance;
    private String accountHolder;
}
```

这是生成的代码：

```java
public class Account {
    private double balance;
    private String accountHolder;

    @ConstructorProperties({"balance", "accountHolder"})
    @Generated
    public Account(double balance, String accountHolder) {
        this.balance = balance;
        this.accountHolder = accountHolder;
    }
 
    @Generated
    public Account() {}

    // default generated getter and setters
}
```

在上面的代码片段中，我们可以看到包含@ConstructorProperties注解的生成类。

接下来，我们有lombok.addLombokGeneratedAnnotation 。 如果为true ，Lombok 将使用@lombok.Generated标记所有生成的方法。当从包扫描或代码覆盖工具中删除 Lombok 生成的方法时，这会派上用场。

另一个有用的键是lombok.addNullAnnotations。 此属性支持许多内置选项，例如 javax (JSR305)、eclipse、JetBrains、NetBeans、Android 等。也可以使用我们自己定义的注解，比如CUSTOM:com.example.NonNull:example.Nullable。只要有意义，Lombok 就会添加nullable或NotNull注解。

最后，我们有 lombok.addSuppressWarnings，如果为false，Lombok 将停止添加注解@SuppressWarnings(“all”)，这是当前的默认行为。如果我们对生成的代码使用静态分析器，这将很有用。

### 4.2. 其他配置键

作为第一个特定于功能的键lombok.accessors.chain， 如果为true，则会更改 setter 方法的行为。这些方法将返回this而不是void return 。允许调用链接，如下所示：

```java
@Test
void should_initialize_account() {
    Account myAccount = new Account()
      .setBalance(2000.00)
      .setAccountHolder("John Snow");

    assertEquals(2000.00, myAccount.getBalance());
    assertEquals("John Snow", myAccount.getAccountHolder());
}
```

与前一个类似，lombok.accessors.fluent 使 Lombok 删除前缀集并仅使用属性名称来命名访问器方法。

lombok.log.fieldName键在用户配置时更改生成的日志字段的名称。默认情况下，lombok.log.fieldName 键使用log来命名字段，但在我们的示例中，我们将其更改为domainLog：

```properties
#Log name customization
lombok.log.fieldName = domainLog
```

然后我们可以看到它的实际效果：

```java
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Log
public class Account {

    // same as defined previously

   public Account withdraw(double amount) {
        if (this.balance - abs(amount) < 0) {
            domainLog.log(Level.INFO, "Transaction denied for account holder: %s", this.accountHolder);
            throw new IllegalArgumentException(String.format("Not enough balance, you have %.2f", this.balance));
        }

        this.balance -= abs(amount);
        return this;
    }
}
```

接下来是 lombok.(featureName).flagUsage。这组属性具有warning、error 和allow作为可能的值。我们可以使用它们来控制在我们的项目中允许使用哪些 Lombok 特性。例如，如果使用了任何实验性特征，可以使用单词experimental和值warning在日志中输出消息：

```bash
/home/dev/repository/git/tutorials/lombok/src/main/java/com/baeldung/lombok/configexamples/TransactionLog.java:9:
 warning: Use of any lombok.experimental feature is flagged according to lombok configuration.
@Accessors(prefix = {"op"})
```

### 4.3. 特殊配置键

有些键不是常见的键值属性，例如lombok.copyableAnnotations。该属性是不同的，因为它表示完全限定注解类型的列表。当添加到某个字段时，Lombok 会将这些注解到与该字段相关的构造函数、getter 和 setter。此功能的一个典型用例是 Spring 的 bean 定义，其中注解@Qualifier和@Value经常必须到构造函数参数中。其他框架也利用了此功能。

要向列表添加注解，用户必须使用以下表达式：lombok.copyableAnnotations += com.test.MyAnnotation。该库使用该机制来传播之前提到的可为空的注解：

```java
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Log
public class Account {

    @NonNull
    private Double balance = 0.;
    @NonNull
    private String accountHolder = "";

    // other methods
}
```

现在，Lombok 生成的代码：

```java
public class Account {

    @Generated
    private static final Logger domainLog = Logger.getLogger(Account.class.getName());
    @NonNull
    private Double balance = 0.0D;
    @NonNull
    private String accountHolder = "";

    @ConstructorProperties({"balance", "accountHolder"})
    @Generated
    public Account(@NonNull Double balance, @NonNull String accountHolder) {
        if (balance == null) {
            throw new NullPointerException("balance is marked non-null but is null");
        } else if (accountHolder == null) {
            throw new NullPointerException("accountHolder is marked non-null but is null");
        } else {
            this.balance = balance;
            this.accountHolder = accountHolder;
        }
    }

    @NonNull
    @Generated
    public Double getBalance() {
        return this.balance;
    }

    @NonNull
    @Generated
    public String getAccountHolder() {
        return this.accountHolder;
    }

    // Rest of the class members...

}
```

最后，我们有一个 清晰的 lombok.(anyConfigKey)指令。 它将任何配置键恢复为其默认值。如果有人更改了任何父配置文件中给定键的值，它现在将被忽略。我们可以使用指令clear，后跟任何 Lombok 配置键：

```properties
clear lombok.addNullAnnotations
```

### 4.4. 文件指令

现在，我们对 Lombok 的配置系统如何工作及其一些特性有了很好的了解。当然，这并不是所有可用功能的详尽列表，但从这里开始，我们必须清楚地了解如何使用它。最后但同样重要的是，让我们看看如何将在当前配置文件中的另一个文件中定义的配置导入。

要在另一个配置文件中导入配置文件，指令必须位于文件之上，路径可以是相对路径也可以是绝对路径：

```properties
##     relative or absolute path  
import lombok_feature.config

config.stopBubbling = true
lombok.anyconstructor.addconstructorproperties=false
lombok.addLombokGeneratedAnnotation = true
lombok.addSuppressWarnings = false

```

只是为了说明，导入的文件：

```properties
# lombok_feature.config file

lombok.experimental.flagUsage = warning
```

## 5.总结

在本文中，我们研究了 Lombok 的配置系统、它的不同属性，以及它们如何影响它的功能。尽管如前所述，还有更多选项可用，但我们只查看了最常见的选项。[请随时在Lombok 页面](https://projectlombok.org/features/configuration)上查看更多信息。