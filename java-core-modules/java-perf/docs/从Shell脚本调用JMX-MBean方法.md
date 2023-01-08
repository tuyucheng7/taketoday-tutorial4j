## 1. 概述

在本教程中，我们将了解如何从 shell 脚本和最常用的可用工具访问[MBean 。](https://www.baeldung.com/java-management-extensions)

首先要注意的是[JMX](https://www.baeldung.com/java-management-extensions)是基于[RMI](https://www.baeldung.com/java-rmi)的。因此，协议的处理是在Java中进行的。但我们可以将它包装在一个 shell 脚本中，以便从命令行调用它。换句话说，如果我们想要自动化，这特别有用。

尽管易于实施，但大多数 JMX 工具都被放弃或变得不可用。因此，让我们检查一些工具，然后编写我们自己的工具。

## 2. 编写一个简单的 MBean

为了测试我们的工具，我们需要一个 MBean。首先，让我们创建一个简单的计算器，从它的界面开始：

```java
public interface CalculatorMBean {

    Integer sum();

    Integer getA();

    void setA(Integer a);

    Integer getB();

    void setB(Integer b);
}
```

然后，让我们看看实现：

```java
public class Calculator implements CalculatorMBean {

    private Integer a = 0;
    private Integer b = 0;

    // getters and setters

    @Override
    public Integer sum() {
        return a + b;
    }
}
```

现在让我们创建一个简单的 CLI 应用程序来注册我们的 MBean。这段代码是标准的，关键部分是我们为 MBean 选择的实现类和名称。我们将通过调用MBeanServer上的registerMBean() 、传递Calculator的实例并实例化ObjectName来完成此操作。

澄清一下，ObjectName接收一个任意字符串。但是，为了[遵循约定](https://www.oracle.com/java/technologies/javase/management-extensions-best-practices.html)，我们将包名作为域，以及“键=值”对列表：

```java
public class JmxCalculatorMain {

    public static void main(String[] args) throws Exception {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        server.registerMBean(
          new Calculator(), new ObjectName("com.baeldung.jxmshell:type=basic,name=calculator")
        );

        // ...
    }
}

```

此外，唯一需要的键是“ type ”，这与我们的场景无关。但这会将我们的 MBean 与域中的其他 MBean 组合在一起。

最后，为了防止我们的应用程序终止，我们将让它等待用户输入：

```java
try (Scanner scanner = new Scanner(System.in)) {
    System.out.println("<press enter to terminate>");
    scanner.nextLine();
}
```

为了简化我们的示例，我们将在[端口](https://www.baeldung.com/jmx-ports) 11234上运行我们的应用程序。另外，让我们使用这些[JVM 参数](https://www.baeldung.com/jvm-parameters)禁用身份验证和 SSL ：

```bash
-Dcom.sun.management.jmxremote.port=11234
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
```

现在，我们准备尝试一些工具。

## 3. 从 Shell 脚本使用 Jmxterm

[Jmxterm是一种交互式 CLI 工具，可作为使用](https://docs.cyclopsgroup.org/jmxterm)[JConsole](https://docs.oracle.com/javase/7/docs/technotes/guides/management/jconsole.html)监视 MBean 的替代方法。但是，通过一些输入操作，我们可以创建一个脚本以非交互方式使用它。让我们从下载[jmxterm-1.0.4-uber.jar](https://github.com/jiaqi/jmxterm/releases/tag/v1.0.4)并将其放入/tmp文件夹开始。

### 3.1. 在脚本中包装 Jmxterm

我们将在脚本中使用 Jmxterm jar 的一些功能：执行 MBean 操作并获取或设置属性值。为了简化它，我们将为它的jar位置、MBean地址和名称、操作和最终命令定义默认值。

所以，让我们创建一个名为 jmxterm.sh 的文件：

```bash
#!/bin/sh

jar='/tmp/jmxterm-1.0.4-uber.jar'
address='localhost:11234'

mbean='com.baeldung.jxmshell:name=calculator,type=basic'
operation='sum'
command="info -b $mbean"
```

我们应该注意，只有在运行JmxCalculatorMain后才能访问该地址。我们将在操作变量中保存来自 MBean 的方法名称或属性。同时，命令变量保存发送到 Jmxterm 的最终命令。作为默认值，info命令显示可用的操作和属性，-b 指定要显示的 MBean。

然后，我们将进行一些[参数解析](https://www.baeldung.com/linux/bash-parse-command-line-arguments)，通过构建命令变量来处理–run、–set和–get选项：

```bash
while test $# -gt 0
do
    case "$1" in
    --run)
        shift; operation=$1
        command="run -b ${mbean} ${operation}"
    ;;
    --set)
        shift; operation="$1"
        shift; attribute_value="$1"
    
        command="set -b ${mbean} ${operation} ${attribute_value}"
    ;;
    --get)
        shift; operation="$1"

        command="get -b ${mbean} ${operation}"
    ;;
    esac
    shift
done
```

使用—run ，我们执行一个 MBean 方法。同样，通过--set ，我们设置了一个属性值。并且，通过—get ，我们可以获得它的当前值。

最后，我们[回显](https://www.baeldung.com/linux/echo-command#:~:text=The echo command writes text to standard output (stdout).&text=Some common usages of the,echo command in later sections.)我们的命令，将它与我们正在运行的应用程序的地址一起通过管道[传输](https://www.baeldung.com/linux/pipes-redirection)到 Jmxterm jar 。-v silent选项关闭冗长，而-n 告诉 Jmxterm 不要打印用户提示。因此，此选项很有用，因为我们没有以交互方式使用它：

```bash
echo $command | java -jar $jar -l $address -n -v silent 
```

### 3.2. 操纵圣母

使我们的脚本[可执行](https://www.baeldung.com/linux/chown-chmod-permissions)后，我们将在不带参数的情况下运行它以查看其默认行为。假设脚本在[当前目录](https://www.baeldung.com/linux/run-script-different-working-dir)中，让我们运行它：

```bash
./jmxterm.sh
```

这将查询有关我们的 MBean 的信息：

```bash
# attributes
  %0   - A (java.lang.Integer, rw)
  %1   - B (java.lang.Integer, rw)
# operations
  %0   - java.lang.Integer sum()
```

我们可以注意到set从我们的 setter 名称中删除，它们出现在属性 部分。

所以，让我们调用setA()：

```bash
./jmxterm.sh --set A 1
```

因此，如果一切正常，则不会有任何输出。现在让我们调用getA()来检查当前值：

```bash
./jmxterm.sh --get A
```

这是我们得到的输出：

```bash
A = 1;
```

现在，让我们将B设置为 2 并调用sum()：

```bash
./jmxter.sh --set B 2
./jmxter.sh --run sum
```

这是输出：

```bash
3
```

该解决方案非常适用于简单的 MBean 操作。

## 4. 从命令行调用 cmdline-jmxclient

我们的下一个工具是[cmdline-jmxclient](http://crawler.archive.org/cmdline-jmxclient)。我们将使用[0.10.3](http://crawler.archive.org/cmdline-jmxclient/cmdline-jmxclient-0.10.3.jar)版本，它的工作方式与 Jmxterm 类似，但没有交互选项。

为了简洁起见，让我们看一个如何从命令行调用它的示例。首先，我们将设置属性值，然后执行操作：

```bash
jar=cmdline-jmxclient-0.10.3.jar
address=localhost:11234
mbean=com.baeldung.jxmshell:name=calculator,type=basic
java -jar $jar - $address $mbean A=1
java -jar $jar - $address $mbean B=1
java -jar $jar - $address $mbean sum
```

值得注意的是，由于我们的 MBean 不需要身份验证，因此我们传递“ - ”而不是身份验证参数。

以下是运行这些命令的输出：

```bash
11/11/2022 22:10:15 -0300 org.archive.jmx.Client sum: 2
```

与此工具的主要区别在于它的输出格式。此外，它更轻巧，大小约为 20 KB，而 Jmxterm 的大小超过 6 MB。

## 5. 编写自定义解决方案

由于可用的选项已经很老了，现在是了解 MBean 的底层工作原理的绝佳时机。因此，让我们开始使用javax.management包中所需类的包装器来实现我们的解决方案 ：

```java
public class JmxConnectionWrapper {

    private final Map<String, MBeanAttributeInfo> attributeMap;
    private final MBeanServerConnection connection;
    private final ObjectName objectName;

    public JmxConnectionWrapper(String url, String beanName) throws Exception {
        objectName = new ObjectName(beanName);

        connection = JMXConnectorFactory.connect(new JMXServiceURL(url))
          .getMBeanServerConnection();

        MBeanInfo bean = connection.getMBeanInfo(objectName);
        MBeanAttributeInfo[] attributes = bean.getAttributes();

        attributeMap = Stream.of(attributes)
          .collect(Collectors.toMap(MBeanAttributeInfo::getName, Function.identity()));
    }

    // ...
}
```

首先，我们的构造函数接收一个要连接的 URL 和一个 MBean 名称。我们保留一个ObjectName的引用以在将来的调用中使用，然后开始与JMXConnectorFactory 的连接。然后，我们得到一个MBeanInfo引用，我们从中提取属性。最后，我们将MBeanAttributeInfo[]转换为映射。稍后我们将使用此映射来确定参数是属性还是操作。

现在，让我们编写一些辅助方法。我们将从获取和设置属性值的方法开始。当我们收到一个值时，我们在返回当前值之前设置它：

```java
public Object attributeValue(String name, String value) throws Exception {
    if (value != null)
        connection.setAttribute(objectName, new Attribute(name, Integer.valueOf(value)));

    return connection.getAttribute(objectName, name);
}
```

因为我们知道我们的 MBean 只包含Integer属性，所以我们使用Integer.valueOf()来构造我们的属性值。但是，为了获得更健壮的解决方案，我们需要使用attributeMap中的信息来确定正确的类型。

同样，因为我们知道我们的 MBean 只包含无参数操作，所以我们在连接上调用invoke()并为参数和签名传递空数组：

```java
public Object invoke(String operation) throws Exception {
    Object[] params = new Object[] {};
    String[] signature = new String[] {};
    return connection.invoke(objectName, operation, params, signature);
}
```

我们将使用它来调用 MBean 中与属性无关的任何方法。

### 5.1 编写 CLI 部分

最后，让我们创建一个[CLI 应用程序](https://www.baeldung.com/java-command-line-arguments)来从 shell 操作我们的 MBean：

```java
public class JmxInvoker {

    public static void main(String... args) throws Exception {
        String attributeValue = null;
        if (args.length > 3) {
            attributeValue = args[3];
        }

        String result = execute(args[0], args[1], args[2], attributeValue);
        System.out.println(result);
    }

    public static String execute(
      String url, String mBeanName, String operation, String attributeValue) {
        JmxConnectionWrapper connection = new JmxConnectionWrapper(url, mBeanName);

        // ...
    }
}
```

我们的main()方法将参数传递给execute()，它处理它们以决定我们是否要执行操作或获取/设置属性：

```java
if (connection.hasAttribute(operation)) {
    Object value = connection.attributeValue(operation, attributeValue);
    return operation + "=" + value;
} else {
    Object result = connection.invoke(operation);
    return operation + "(): " + result;
}
```

如果我们的 MBean 包含一个具有操作名称的属性，我们将调用attributeValue()。否则，我们调用invoke()。

### 5.2. 从命令行调用它

假设我们[将我们的应用程序打包在一个 jar 中](https://www.baeldung.com/java-create-jar)并将其放在jar变量指定的位置，让我们定义我们的默认值：

```bash
jar=/tmp/jmx-invoker.jar
address='service:jmx:rmi:///jndi/rmi://localhost:11234/jmxrmi'
invoker='custom.jmxshell.cn.tuyucheng.taketoday.JmxInvoker'
mbean='com.baeldung.jxmshell:name=calculator,type=basic'

```

然后，我们运行这些命令来设置属性并在我们的MBean中执行sum()方法，方法与之前的解决方案类似：

```bash
$ java -cp $jar $invoker $address $mbean A 1
A=1

$ java -cp $jar $invoker $address $mbean B 1
B=1
$ java -cp $jar $invoker $address $mbean sum
sum(): 2
```

此自定义JmxInvoker有助于构建 MBean 处理程序并在解析 MBean 时拥有完全控制权。

## 六，总结

在本文中，我们看到了用于管理 MBean 的工具以及如何从 shell 脚本使用它们。然后，我们创建了一个自定义工具来处理它们。最后，存储库中提供了更多脚本示例。