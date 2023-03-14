## 1. 概述

[我们可能想知道像IntelliJ IDEA](https://www.baeldung.com/intellij-basics)和 Eclipse这样被广泛认可的 IDE 是如何实现[调试功能的](https://www.baeldung.com/eclipse-debugging)。这些工具严重依赖于JavaPlatform Debugger Architecture (JPDA)。

在这篇介绍性文章中，我们将讨论 JPDA 下可用的Java调试接口 API (JDI)。

同时，我们将逐步编写自定义调试器程序，熟悉方便的 JDI 接口。

## 2. JPDA简介

Java 平台调试器架构 (JPDA) 是一组用于调试Java的精心设计的接口和协议。

它提供三个专门设计的接口，为桌面系统中的开发环境实现自定义调试器。

首先，Java 虚拟机工具接口 (JVMTI) 帮助我们交互和控制在[JVM](https://www.baeldung.com/jvm-vs-jre-vs-jdk)中运行的应用程序的执行。

然后是JavaDebug Wire Protocol (JDWP)，它定义了被测应用程序(调试对象)和调试器之间使用的协议。

最后，使用Java调试接口 (JDI) 来实现调试器应用程序。

## 3. 什么是JDI？

Java Debug Interface API 是Java提供的一组接口，用于实现调试器的前端。JDI 是 JPDA 的最高层。

使用 JDI 构建的调试器可以调试在任何支持 JPDA 的 JVM 中运行的应用程序。同时，我们可以将其hook到调试的任何一层。

它提供了访问 VM 及其状态以及访问调试对象变量的能力。同时，它允许设置断点、步进、观察点和处理线程。

## 4.设置

我们需要两个独立的程序——一个被调试程序和一个调试器——来理解 JDI 的实现。

首先，我们将编写一个示例程序作为调试对象。

让我们用几个字符串变量和println语句创建一个JDIExampleDebuggee类：

```java
public class JDIExampleDebuggee {
    public static void main(String[] args) {
        String jpda = "Java Platform Debugger Architecture";
        System.out.println("Hi Everyone, Welcome to " + jpda); // add a break point here

        String jdi = "Java Debug Interface"; // add a break point here and also stepping in here
        String text = "Today, we'll dive into " + jdi;
        System.out.println(text);
    }
}
```

然后，我们将编写一个调试器程序。

让我们创建一个JDIExampleDebugger类，它具有用于保存调试程序 ( debugClass ) 的属性和断点的行号 ( breakPointLines )：

```java
public class JDIExampleDebugger {
    private Class debugClass; 
    private int[] breakPointLines;

    // getters and setters
}
```

### 4.1. 启动连接器

首先，调试器需要一个连接器来与目标虚拟机 (VM) 建立连接。

然后，我们需要将调试对象设置为连接器的主要参数。最后，连接器应该启动 VM 进行调试。

为此，JDI 提供了一个Bootstrap类，它提供了LaunchingConnector的一个实例。LaunchingConnector提供了[默认参数](https://docs.oracle.com/en/java/javase/11/docs/specs/jpda/conninv.html#connectors)的映射，我们可以在其中设置主要参数。

因此，让我们将connectAndLaunchVM方法添加到JDIDebuggerExample类：

```java
public VirtualMachine connectAndLaunchVM() throws Exception {
 
    LaunchingConnector launchingConnector = Bootstrap.virtualMachineManager()
      .defaultConnector();
    Map<String, Connector.Argument> arguments = launchingConnector.defaultArguments();
    arguments.get("main").setValue(debugClass.getName());
    return launchingConnector.launch(arguments);
}
```

现在，我们将主要方法添加到JDIDebuggerExample类以调试JDIExampleDebuggee：

```java
public static void main(String[] args) throws Exception {
 
    JDIExampleDebugger debuggerInstance = new JDIExampleDebugger();
    debuggerInstance.setDebugClass(JDIExampleDebuggee.class);
    int[] breakPoints = {6, 9};
    debuggerInstance.setBreakPointLines(breakPoints);
    VirtualMachine vm = null;
    try {
        vm = debuggerInstance.connectAndLaunchVM();
        vm.resume();
    } catch(Exception e) {
        e.printStackTrace();
    }
}
```

让我们编译我们的两个类，JDIExampleDebuggee(被调试器)和JDIExampleDebugger(调试器)：

```shell
javac -g -cp "/Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/lib/tools.jar" 
com/baeldung/jdi/.java
```

让我们详细讨论这里使用的javac命令。

-g选项生成所有调试信息，没有它，我们可能会看到AbsentInformationException。

而-cp将在类路径中添加tools.jar以编译类。


JDK 的tools.jar下提供了所有 JDI 库。因此，请务必在编译和执行时将tools.jar添加到类路径中。

就是这样，现在我们准备好执行我们的自定义调试器JDIExampleDebugger：

```shell
java -cp "/Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/lib/tools.jar:." 
JDIExampleDebugger
```

注意“：” 使用tools.jar。这会将tools.jar添加到当前运行时的类路径中(在 Windows 上使用“;.”)。

### 4.2. Bootstrap和ClassPrepareRequest

在这里执行调试器程序不会给出任何结果，因为我们还没有为调试准备好类并设置断点。

VirtualMachine类具有eventRequestManager方法来创建各种请求，如ClassPrepareRequest、BreakpointRequest和StepEventRequest 。

因此，让我们将enableClassPrepareRequest方法添加到JDIExampleDebugger类中。

这将过滤JDIExampleDebuggee类并启用ClassPrepareRequest：

```java
public void enableClassPrepareRequest(VirtualMachine vm) {
    ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
    classPrepareRequest.addClassFilter(debugClass.getName());
    classPrepareRequest.enable();
}
```

### 4.3. ClassPrepareEvent和 BreakpointRequest

一旦启用了JDIExampleDebuggee类的ClassPrepareRequest，VM 的事件队列将开始拥有ClassPrepareEvent的实例。

使用ClassPrepareEvent，我们可以获得设置断点的位置并创建BreakPointRequest。

为此，让我们将setBreakPoints方法添加到JDIExampleDebugger类：

```java
public void setBreakPoints(VirtualMachine vm, ClassPrepareEvent event) throws AbsentInformationException {
    ClassType classType = (ClassType) event.referenceType();
    for(int lineNumber: breakPointLines) {
        Location location = classType.locationsOfLine(lineNumber).get(0);
        BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
        bpReq.enable();
    }
}
```

### 4.4. BreakPointEvent和StackFrame

到目前为止，我们已经为调试准备了类并设置了断点。现在，我们需要捕获BreakPointEvent并显示变量。

JDI提供了StackFrame类，用来获取被调试者所有可见变量的列表。

因此，让我们将displayVariables方法添加到JDIExampleDebugger类：

```java
public void displayVariables(LocatableEvent event) throws IncompatibleThreadStateException, 
AbsentInformationException {
    StackFrame stackFrame = event.thread().frame(0);
    if(stackFrame.location().toString().contains(debugClass.getName())) {
        Map<LocalVariable, Value> visibleVariables = stackFrame
          .getValues(stackFrame.visibleVariables());
        System.out.println("Variables at " + stackFrame.location().toString() +  " > ");
        for (Map.Entry<LocalVariable, Value> entry : visibleVariables.entrySet()) {
            System.out.println(entry.getKey().name() + " = " + entry.getValue());
        }
    }
}
```

## 5.调试目标

在这一步，我们只需要更新JDIExampleDebugger的主要方法即可开始调试。

因此，我们将使用已经讨论过的方法，如enableClassPrepareRequest、setBreakPoints和displayVariables ：


```java
try {
    vm = debuggerInstance.connectAndLaunchVM();
    debuggerInstance.enableClassPrepareRequest(vm);
    EventSet eventSet = null;
    while ((eventSet = vm.eventQueue().remove()) != null) {
        for (Event event : eventSet) {
            if (event instanceof ClassPrepareEvent) {
                debuggerInstance.setBreakPoints(vm, (ClassPrepareEvent)event);
            }
            if (event instanceof BreakpointEvent) {
                debuggerInstance.displayVariables((BreakpointEvent) event);
            }
            vm.resume();
        }
    }
} catch (VMDisconnectedException e) {
    System.out.println("Virtual Machine is disconnected.");
} catch (Exception e) {
    e.printStackTrace();
}
```

现在首先，让我们用已经讨论过的javac命令再次编译JDIDebuggerExample类。

最后，我们将执行调试器程序以及所有更改以查看输出：

```shell
Variables at com.baeldung.jdi.JDIExampleDebuggee:6 > 
args = instance of java.lang.String[0] (id=93)
Variables at com.baeldung.jdi.JDIExampleDebuggee:9 > 
jpda = "Java Platform Debugger Architecture"
args = instance of java.lang.String[0] (id=93)
Virtual Machine is disconnected.
```

欢呼！我们已经成功调试了JDIExampleDebuggee类。同时，我们在断点位置(第 6 行和第 9 行)显示了变量的值。

因此，我们的自定义调试器已准备就绪。

### 5.1. 步骤请求

调试还需要单步执行代码并在后续步骤中检查变量的状态。因此，我们将在断点处创建一个步骤请求。

在创建StepRequest 的实例时，我们必须提供步骤的大小和深度。我们将分别定义[STEP_LINE](https://docs.oracle.com/en/java/javase/11/docs/api/jdk.jdi/com/sun/jdi/request/StepRequest.html#STEP_LINE)和[STEP_OVER](https://docs.oracle.com/en/java/javase/11/docs/api/jdk.jdi/com/sun/jdi/request/StepRequest.html#STEP_OVER)。

让我们编写一个方法来启用步骤请求。

为简单起见，我们将从最后一个断点(第 9 行)处开始：

```java
public void enableStepRequest(VirtualMachine vm, BreakpointEvent event) {
    // enable step request for last break point
    if (event.location().toString().
        contains(debugClass.getName() + ":" + breakPointLines[breakPointLines.length-1])) {
        StepRequest stepRequest = vm.eventRequestManager()
            .createStepRequest(event.thread(), StepRequest.STEP_LINE, StepRequest.STEP_OVER);
        stepRequest.enable();    
    }
}
```

现在，我们可以更新JDIExampleDebugger的主要方法，以在它是BreakPointEvent时启用步骤请求：

```java
if (event instanceof BreakpointEvent) {
    debuggerInstance.enableStepRequest(vm, (BreakpointEvent)event);
}
```

### 5.2. 步骤事件

与BreakPointEvent类似，我们也可以在StepEvent中显示变量。

让我们相应地更新主要方法：

```java
if (event instanceof StepEvent) {
    debuggerInstance.displayVariables((StepEvent) event);
}
```

最后，我们将执行调试器以在单步执行代码时查看变量的状态：

```shell
Variables at com.baeldung.jdi.JDIExampleDebuggee:6 > 
args = instance of java.lang.String[0] (id=93)
Variables at com.baeldung.jdi.JDIExampleDebuggee:9 > 
args = instance of java.lang.String[0] (id=93)
jpda = "Java Platform Debugger Architecture"
Variables at com.baeldung.jdi.JDIExampleDebuggee:10 > 
args = instance of java.lang.String[0] (id=93)
jpda = "Java Platform Debugger Architecture"
jdi = "Java Debug Interface"
Variables at com.baeldung.jdi.JDIExampleDebuggee:11 > 
args = instance of java.lang.String[0] (id=93)
jpda = "Java Platform Debugger Architecture"
jdi = "Java Debug Interface"
text = "Today, we'll dive intoJavaDebug Interface"
Variables at com.baeldung.jdi.JDIExampleDebuggee:12 > 
args = instance of java.lang.String[0] (id=93)
jpda = "Java Platform Debugger Architecture"
jdi = "Java Debug Interface"
text = "Today, we'll dive intoJavaDebug Interface"
Virtual Machine is disconnected.
```

如果比较输出，我们会发现调试器从第 9 行开始介入，并在所有后续步骤中显示变量。

## 6.读取执行输出

我们可能会注意到JDIExampleDebuggee类的println语句不是调试器输出的一部分。

根据 JDI 文档，如果我们通过LaunchingConnector 启动 VM，则其输出和错误流必须由Process对象读取。

因此，让我们将它添加到main方法的finally子句中：

```java
finally {
    InputStreamReader reader = new InputStreamReader(vm.process().getInputStream());
    OutputStreamWriter writer = new OutputStreamWriter(System.out);
    char[] buf = new char[512];
    reader.read(buf);
    writer.write(buf);
    writer.flush();
}
```

现在，执行调试器程序还将JDIExampleDebuggee类中的println语句添加到调试输出中：

```shell
Hi Everyone, Welcome toJavaPlatform Debugger Architecture
Today, we'll dive intoJavaDebug Interface
```

## 七. 总结

在本文中，我们探索了Java平台调试器架构 (JPDA) 下可用的Java调试接口 (JDI) API。

在此过程中，我们利用 JDI 提供的便捷接口构建了一个自定义调试器。同时，我们还为调试器添加了步进功能。

由于这只是对 JDI 的介绍，建议查看[JDI API](https://github.com/openjdk-mirror/jdk7u-jdk)下可用的其他接口的实现。