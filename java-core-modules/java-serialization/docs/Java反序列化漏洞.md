## 1. 概述

在本教程中，我们将探讨攻击者如何在Java代码中使用反序列化来利用系统。

我们将从研究攻击者可能用来利用系统的一些不同方法开始。然后，我们将研究成功攻击的含义。最后，我们将研究一些最佳实践来帮助避免这些类型的攻击。

## 2. 反序列化漏洞

Java 广泛使用反序列化从输入源创建对象。

这些输入源是字节流，有多种格式(一些标准格式包括 JSON 和 XML)。合法的系统功能或跨网络与可信来源的通信使用反序列化。但是，不受信任或恶意的字节流可以利用易受攻击的反序列化代码。

我们之前关于[Java 序列化的](https://www.baeldung.com/java-serialization)文章更深入地介绍了序列化和反序列化的工作原理。

### 2.1. 攻击向量

让我们讨论攻击者如何使用反序列化来利用系统。

对于可序列化的类，它必须符合[Serializable](https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/io/Serializable.html) 接口。实现Serializable 的类使用方法readObject和writeObject。这些方法分别反序列化和序列化类的对象实例。

一个典型的实现可能是这样的：

```java
public class Thing implements Serializable {
    private static final long serialVersionUID = 0L;

    // Class fields

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        // Custom attribute setting
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject(); 
        // Custom attribute getting
    }
}
```

当类具有通用或松散定义的字段并使用反射在这些字段上设置属性时，它们会变得容易受到攻击：

```java
public class BadThing implements Serializable {
    private static final long serialVersionUID = 0L;

    Object looselyDefinedThing;
    String methodName;

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        try {
            Method method = looselyDefinedThing.getClass().getMethod(methodName);
            method.invoke(looselyDefinedThing);
        } catch (Exception e) {
            // handle error...
        }
    }

    // ...
}
```

让我们分解上面的内容，看看发生了什么。

首先，我们的BadThing 类有一个looselyDefinedThing字段，它是 Object 类型。 这是模糊的，允许攻击者将该字段设为类路径上可用的任何类型。

接下来，使此类容易受到攻击的原因是readObject方法包含调用looselyDefinedThing上的方法的自定义代码。我们要调用的方法通过反射使用字段methodName(也可以由攻击者控制)。

如果类MyCustomAttackObject在系统的类路径上，则上面的代码在执行时等效于以下代码：

```java
BadThing badThing = new BadThing();
badThing.looselyDefinedThing = new MyCustomAttackObject();
badThing.methodName = "methodThatTriggersAttack";

Method method = looselyDefinedThing.getClass().getMethod(methodName);
method.invoke(methodName);
public class MyCustomAttackObject implements Serializable {
    public static void methodThatTriggersAttack() {
        try {
            Runtime.getRuntime().exec("echo "Oh, no! I've been hacked"");
        } catch (IOException e) {
            // handle error...
        }
    }
}
```

通过使用MyCustomAttackObject类，攻击者已经能够在主机上执行命令。

这个特定的命令是无害的。但是，如果此方法能够采用自定义命令，则攻击者可以实现的目标的可能性是无限的。

仍然存在的问题是，“为什么有人首先要在他们的类路径上有这样一个类？”。

允许攻击者执行恶意代码的类广泛存在于许多框架和软件使用的开源和第三方库中。它们通常不像上面的例子那么简单，但涉及使用多个类和反射来执行类似的命令。

以这种方式使用多个类通常称为小工具链。开源工具[ysoserial](https://github.com/frohoff/ysoserial)维护着一个可用于攻击的小工具链的活动列表。

### 2.2. 启示

现在我们知道了攻击者如何获得远程命令执行的访问权，让我们讨论一下攻击者可能能够在我们的系统上实现的一些含义。

根据运行 JVM 的用户拥有的访问级别，攻击者可能已经在机器上拥有更高的权限，这将允许他们访问系统中的大多数文件并窃取信息。

一些反序列化漏洞允许攻击者执行自定义Java代码，这可能导致拒绝服务攻击、窃取用户会话或未经授权访问资源。

由于每个反序列化漏洞都不同，每个系统设置也不同，因此攻击者可以实现的目标千差万别。出于这个原因，漏洞数据库将反序列化漏洞视为高风险。

## 3. 预防的最佳实践

既然我们已经介绍了我们的系统可能如何被利用，我们将介绍一些可以遵循的最佳实践，以帮助防止此类攻击并限制潜在利用的范围。

请注意，防止漏洞利用没有灵丹妙药，本节并未详尽列出所有预防措施：

-   我们应该使开源库保持最新。在可用时优先更新到最新版本的库。
-   积极检查漏洞数据库，例如[国家漏洞数据库](https://nvd.nist.gov/)或[CVE Mitre](https://cve.mitre.org/)(仅举几例)以查找新声明的漏洞，并确保我们没有暴露
-   验证用于反序列化的输入字节流的来源(使用安全连接并验证用户等)
-   如果输入来自用户输入字段，请确保在反序列化之前验证这些字段并授权用户
-   创建自定义反序列化代码时，请遵循[owasp 备忘单进行反序列化](https://cheatsheetseries.owasp.org/cheatsheets/Deserialization_Cheat_Sheet.html)
-   限制 JVM 可以在主机上访问的内容，以减少攻击者在能够利用我们的系统时可以做的事情的范围

## 4。总结

在本文中，我们介绍了攻击者如何使用反序列化来利用易受攻击的系统。此外，我们还介绍了一些在Java系统中保持良好安全卫生的做法。