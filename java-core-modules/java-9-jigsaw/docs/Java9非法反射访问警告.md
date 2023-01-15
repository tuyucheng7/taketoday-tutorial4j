## 1. 概述

在Java 9之前，Java 反射API表现出一种强大的能力：它可以不受限制地访问非公共类成员。在Java 9之后，模块化系统希望将反射API限制在合理的范围内。

在本教程中，我们概述模块系统和反射之间的关系。

## 2. 模块化系统和反射

尽管反射和模块系统在Java历史上的不同时期出现，但它们需要共同构建一个可靠的平台。

### 2.1 基础模型

Java模块系统的目标之一是强封装，强封装主要包括可读性和可访问性：

-   模块的可读性是一个粗略的概念，它关注一个模块是否依赖于另一个模块。
-   模块的可访问性是一个更好的概念，它关心一个类是否可以访问另一个类的字段或方法。它由类边界、包边界和模块边界提供。

[![j1](https://www.baeldung.com/wp-content/uploads/2022/04/j1.png)](https://www.baeldung.com/wp-content/uploads/2022/04/j1.png)

这两条规则之间的关系是可读性第一，可访问性建立在可读性之上。例如，如果一个类是公共的但没有导出，则可读性将阻止进一步使用。而且，如果非公共类在导出的包中，可读性将允许其通过，但可访问性将拒绝它。

为了提高可读性，我们可以在模块声明中使用“requires”指令，在命令行中指定“-add-reads”参数，或者调用Module.addReads方法。同样的，为了打破边界封装，我们可以在模块声明中使用“opens”指令，在命令行中指定“ --add-opens”参数，或者调用Module.addOpens方法。

即使是反射也不能破坏可读性和可访问性规则；否则会导致相应的错误或警告。需要注意的一点是：当使用反射时，运行时会自动在两个模块之间设置可读性边缘。这也意味着，如果出现问题，那是因为可访问性。

### 2.2 不同的反射用例

在Java模块系统中，有不同的模块类型，例如，命名模块、未命名模块、平台/系统模块、应用程序模块等：

[![j2](https://www.baeldung.com/wp-content/uploads/2022/04/j2.png)](https://www.baeldung.com/wp-content/uploads/2022/04/j2.png)

需要明确的是，“模块系统”和“系统模块”这两个概念可能听起来令人感到困惑，因此，我们这里使用“平台模块”概念而不是“系统模块”。

考虑到上述模块类型，不同模块类型之间存在相当多的组合。通常，除自动模块外，命名模块无法读取未命名模块。让我们只关注发生非法反射访问的三个典型场景：

[![j3](https://www.baeldung.com/wp-content/uploads/2022/04/j3.png)](https://www.baeldung.com/wp-content/uploads/2022/04/j3.png)

在上图中，深度反射是指使用反射API通过调用setAccessible(flag)方法来访问类的非公共成员，当使用反射从另一个命名模块访问一个命名模块时，我们会得到一个IllegalAccessException或InaccessibleObjectException。同样，当使用反射从未命名的模块访问命名模块的应用程序时，我们会得到相同的错误。

但是，当使用反射从未命名的模块访问平台模块时，我们得到的是IllegalAccessException或警告。警告信息有助于我们找到问题发生的位置并采取进一步的补救措施：

```text
WARNING: Illegal reflective access by $PERPETRATOR to $VICTIM
```

在上面的警告消息形式中，$PERPETRATOR代表反射类信息，$VICTIM代表被反射类的信息。并且，这一消息归因于[宽松的强封装](https://openjdk.java.net/jeps/261#Relaxed-strong-encapsulation)。

### 2.3 宽松的强封装

在Java 9之前，许多第三方库利用反射API来完成它们的内部工作。然而，模块系统的强封装规则会使大部分代码无效，尤其是那些使用深度反射来访问JDK内部API的代码，那将是不可取的。为了从Java 8顺利迁移到Java 9的模块化系统，我们需要做出妥协：放松强封装。

宽松的强封装提供了一个启动器参数–illegal-access来控制运行时行为，我们应该注意，–illegal-access参数仅在我们使用反射从未命名的模块访问平台模块时才有效；否则此参数无效。

–illegal-access参数有四个具体值：

-   permit：将每个平台模块包打开给未命名的模块，并仅显示一次警告消息
-   warn：与“permit”相同，但在每次非法反射访问操作都会显示警告消息
-   debug：与“warn”相同，并且还打印相应的堆栈跟踪
-   deny：禁用所有非法的反射访问操作

从Java 9开始，–illegal-access=permit是默认模式，要使用其他模式，我们可以在命令行中指定此参数：

```plaintext
java --illegal-access=deny cn.tuyucheng.taketoday.module.unnamed.Main
```

在Java 16中，–illegal-access=deny成为默认模式；[从Java 17开始，–illegal-access参数被完全删除](https://openjdk.java.net/jeps/403#Description)。

## 3. 如何修复反射非法访问

在Java模块系统中，需要打开一个包以允许深度反射。

### 3.1 通过模块声明

如果我们是代码作者(即模块的开发者)，我们可以在module-info.java中打开包：

```java
module tuyucheng.reflected {
    opens cn.tuyucheng.taketoday.reflected.opened;
}
```

为了更好的权衡，我们可以使用更安全的opens：

```java
module tuyucheng.reflected {
    opens cn.tuyucheng.taketoday.reflected.internal to tuyucheng.intermedium;
}
```

在将现有的代码迁移到模块化系统时，为了方便起见，我们可以打开整个模块：

```java
open module tuyucheng.reflected {
    // don't use opens directive
}
```

我们应该注意，一个开放的模块不允许内部的opens指令。

### 3.2 通过命令行

如果我们不是代码作者，我们可以在命令行中使用--add-opens参数：

```shell
--add-opens java.base/java.lang=tuyucheng.reflecting.named
```

而且，要向所有未命名的模块添加opens，我们可以使用ALL-UNNAMED：

```bash
java --add-opens java.base/java.lang=ALL-UNNAMED
```

### 3.3 在运行时

要在运行时添加Opens，我们可以使用Module.addOpens方法：

```java
srcModule.addOpens("cn.tuyucheng.taketoday.reflected.internal", targetModule);
```

在上面的代码片段中，srcModule将“cn.tuyucheng.taketoday.reflected.internal”包打开到targetModule模块。

需要注意的一点：Module.addOpens方法是caller-sensitive(调用方敏感)，只有当我们从正在修改的模块、从它授予开放访问权限的模块或从未命名的模块调用此方法时，此方法才会成功。否则，它将导致IllegalCallerException。

向目标模块添加opens的另一种方法是使用Java代理，在java.instrument模块中，Instrumentation类从Java 9开始添加了一个新的redefineModule方法，该方法可用于添加额外的reads、exports、opens、uses和provides：

```java
void redefineModule(Instrumentation inst, Module src, Module target) {
    // prepare extra reads
    Set<Module> extraReads = Collections.singleton(target);

    // prepare extra exports
    Set<String> packages = src.getPackages();
    Map<String, Set<Module>> extraExports = new HashMap<>();
    for (String pkg : packages) {
        extraExports.put(pkg, extraReads);
    }

    // prepare extra opens
    Map<String, Set<Module>> extraOpens = new HashMap<>();
    for (String pkg : packages) {
        extraOpens.put(pkg, extraReads);
    }

    // prepare extra uses
    Set<Class<?>> extraUses = Collections.emptySet();

    // prepare extra provides
    Map<Class<?>, List<Class<?>>> extraProvides = Collections.emptyMap();

    // redefine module
    inst.redefineModule(src, extraReads, extraExports, extraOpens, extraUses, extraProvides);
}
```

在上面的代码中，我们首先使用target模块来构造extraReads、extraExports和extraOpens变量；然后，我们调用Instrumentation.redefineModule方法。因此，target模块可以访问src模块。

## 4. 总结

在本教程中，我们首先介绍了模块系统的可读性和可访问性。然后，我们介绍了不同的非法反射访问用例，以及宽松的强封装如何帮助我们从Java 8迁移到Java 9模块系统；最后，我们提供了不同的方法来解决非法反射访问。