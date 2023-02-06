## 1. 概述

在本教程中，我们将探索嵌套，即Java 11中引入的新访问控制上下文。

## 2. Java 11之前

### 2.1 嵌套类型

Java允许类和接口相互[嵌套](https://www.baeldung.com/java-nested-classes)。这些**嵌套类型可以不受限制地相互访问**，包括私有字段、方法和构造函数。

考虑以下嵌套类示例：

```java
public class Outer {

    public void outerPublic() {
    }

    private void outerPrivate() {
    }

    class Inner {

        public void innerPublic() {
            outerPrivate();
        }
    }
}
```

在这里，虽然方法outerPrivate()是private，但可以从方法innerPublic()访问它。

**我们可以将顶级类型以及嵌套在其中的所有类型描述为形成一个嵌套。嵌套中的两个成员被描述为巢友**。

因此，在上面的示例中，Outer和Inner一起形成一个嵌套，并且是彼此的巢友。

### 2.2 桥接法

JVM访问规则不允许巢友之间的私有访问。理想情况下，我们应该得到上述示例的编译错误。但是，Java源代码编译器通过引入间接级别来允许访问。

例如，**对私有成员的调用被编译为对目标类中编译器生成的、包私有的桥接方法的调用，进而调用预期的私有方法**。

这发生在幕后。这种桥接方法略微增加了已部署应用程序的大小，并且可能会混淆用户和工具。

### 2.3 使用反射

这样做的另一个后果是核心反射也拒绝访问。这是令人惊讶的，因为反射调用的行为应该与源级调用相同。

例如，如果我们尝试从Inner类反射地调用outerPrivate()：

```java
public void innerPublicReflection(Outer ob) throws Exception {
    Method method = ob.getClass().getDeclaredMethod("outerPrivate");
    method.invoke(ob);
}
```

我们会得到一个异常：

```bash
java.lang.IllegalAccessException: 
Class cn.tuyucheng.taketoday.Outer$Inner can not access a member of class cn.tuyucheng.taketoday.Outer with modifiers "private"
```

Java 11试图解决这些问题。

## 3. 基于嵌套的访问控制

**Java 11在JVM中引入了嵌套的概念和相关的访问规则**，这简化了Java源代码编译器的工作。

为了实现这一点，类文件格式现在包含两个新属性：

1.  一个嵌套成员(通常是顶级类)被指定为嵌套宿主。它包含一个属性(NestMembers)来标识其他静态已知的嵌套成员。
2.  每个其他嵌套成员都有一个属性(NestHost)来标识其嵌套宿主。

因此，要使类型C和D要成为巢友，它们必须具有相同的嵌套宿主。如果类型C在其NestHost属性中列出D，则它声称是D托管的嵌套的成员。如果D还在其NestMembers属性中列出C，则验证成员资格。此外，类型D隐含地是它所托管的嵌套的成员。

现在编译器**不需要生成桥接方法**。

最后，基于嵌套的访问控制从核心反射中移除了令人惊讶的行为。因此，上一节中显示的方法innerPublicReflection()将毫无异常地执行。

## 4. Nestmate反射API

**Java 11提供了使用核心反射查询新类文件属性的方法**。java.lang.Class类包含以下三个新方法。

### 4.1 getNestHost()

这将返回该Class对象所属的嵌套的嵌套宿主：

```java
@Test
public void whenGetNestHostFromOuter_thenGetNestHost() {
    is(Outer.class.getNestHost().getName()).equals("cn.tuyucheng.taketoday.Outer");
}

@Test
public void whenGetNestHostFromInner_thenGetNestHost() {
    is(Outer.Inner.class.getNestHost().getName()).equals("cn.tuyucheng.taketoday.Outer");
}
```

Outer和Inner类都属于嵌套宿主cn.tuyucheng.taketoday.Outer。

### 4.2 isNestmateOf()

这确定给定的Class是否是这个Class对象的巢友：

```java
@Test
public void whenCheckNestmatesForNestedClasses_thenGetTrue() {
    is(Outer.Inner.class.isNestmateOf(Outer.class)).equals(true);
}
```

### 4.3 getNestMembers()

这将返回一个包含Class对象的数组，这些对象表示该Class对象所属的嵌套的所有成员：

```java
@Test
public void whenGetNestMembersForNestedClasses_thenGetAllNestedClasses() {
    Set<String> nestMembers = Arrays.stream(Outer.Inner.class.getNestMembers())
        .map(Class::getName)
        .collect(Collectors.toSet());

    is(nestMembers.size()).equals(2);

    assertTrue(nestMembers.contains("cn.tuyucheng.taketoday.Outer"));
    assertTrue(nestMembers.contains("cn.tuyucheng.taketoday.Outer$Inner"));
}
```

## 5. 编译细节

### 5.1 Java 11之前的桥接方法

让我们深入了解编译器生成的桥接方法的细节。我们可以通过反汇编生成的class文件来看到这一点：

```shell
$ javap -c Outer
Compiled from "Outer.java"
public class cn.tuyucheng.taketoday.Outer {
    public cn.tuyucheng.taketoday.Outer();
        Code:
            0: aload_0
            1: invokespecial #2                  // Method java/lang/Object."<init>":()V
            4: return

    public void outerPublic();
        Code:
            0: return

    static void access$000(cn.tuyucheng.taketoday.Outer);
        Code:
            0: aload_0
            1: invokespecial #1                  // Method outerPrivate:()V
            4: return
}
```

在这里，除了默认构造函数和公共方法outerPublic()之外，**请注意方法access$000()。编译器将此作为桥接方法生成**。

innerPublic()通过这个方法调用outerPrivate()：

```shell
$ javap -c Outer\$Inner
Compiled from "Outer.java"
class cn.tuyucheng.taketoday.Outer$Inner {
    final cn.tuyucheng.taketoday.Outer this$0;

    cn.tuyucheng.taketoday.Outer$Inner(cn.tuyucheng.taketoday.Outer);
        Code:
            0: aload_0
            1: aload_1
            2: putfield      #1                  // Field this$0:Lcn/tuyucheng/taketoday/Outer;
            5: aload_0
            6: invokespecial #2                  // Method java/lang/Object."<init>":()V
            9: return

    public void innerPublic();
        Code:
            0: aload_0
            1: getfield      #1                  // Field this$0:Lcn/tuyucheng/taketoday/Outer;
            4: invokestatic  #3                  // Method cn/tuyucheng/taketoday/Outer.access$000:(Lcn/tuyucheng/taketoday/Outer;)V
            7: return
}
```

注意第19行的注释。这里，innerPublic()调用桥接方法access$000()。

### 5.2 Java 11的巢友

Java 11编译器将生成以下反汇编的Outer类文件：

```shell
$ javap -c Outer
Compiled from "Outer.java"
public class cn.tuyucheng.taketoday.Outer {
    public cn.tuyucheng.taketoday.Outer();
        Code:
            0: aload_0
            1: invokespecial #1                  // Method java/lang/Object."<init>":()V
            4: return

    public void outerPublic();
        Code:
            0: return
}
```

请注意，没有编译器生成的桥接方法。此外，Inner类现在可以直接调用outerPrivate()方法：

```shell
$ javap -c Outer\$Inner.class 
Compiled from "Outer.java"
class cn.tuyucheng.taketoday.Outer$Inner {
    final cn.tuyucheng.taketoday.Outer this$0;

    cn.tuyucheng.taketoday.Outer$Inner(cn.tuyucheng.taketoday.Outer);
        Code:
            0: aload_0
            1: aload_1
            2: putfield      #1                  // Field this$0:Lcn/tuyucheng/taketoday/Outer;
            5: aload_0
            6: invokespecial #2                  // Method java/lang/Object."<init>":()V
            9: return

    public void innerPublic();
        Code:
            0: aload_0
            1: getfield      #1                  // Field this$0:Lcn/tuyucheng/taketoday/Outer;
            4: invokevirtual #3                  // Method cn/tuyucheng/taketoday/Outer.outerPrivate:()V
            7: return
}
```

## 6. 总结

在本文中，我们探讨了Java 11中引入的基于嵌套的访问控制。