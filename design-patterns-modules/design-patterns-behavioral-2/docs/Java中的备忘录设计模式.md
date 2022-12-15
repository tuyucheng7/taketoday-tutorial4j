## 1. 概述

在本教程中，我们介绍什么是备忘录设计模式以及如何使用它。

首先，我们介绍一些理论。然后，我们将创建一个示例来说明该模式的用法。

## 2. 什么是备忘录设计模式

[四人帮在他们的书中](https://www.oreilly.com/library/view/design-patterns-elements/0201633612/)描述的备忘录设计模式是一种行为设计模式，**备忘录设计模式提供了一种实现可撤销操作的解决方案**。我们可以通过在给定时刻保存对象的状态并在需要撤消此后执行的操作时恢复它来做到这一点。

实际上，需要保存其状态的对象称为Originator(发起者)，Caretaker(看守人)是触发状态保存和恢复的对象，称为备忘录。

备忘录对象应该向Caretaker公开尽可能少的信息，这是为了确保我们不会将Originator的内部状态暴露给外界，因为这会破坏封装原则。但是，Originator应该访问足够的信息才能恢复到原始状态。

下面是一个关于该模式的类图，说明不同的对象如何相互交互：

<img src="../assets/img.png">

正如我们所见，Originator可以生产和使用备忘录。同时，Caretaker仅在恢复之前保留状态，Originator的内部表示对外部世界是隐藏的。

在这里，我们使用单个字段来表示Originator的状态，尽管我们**不限于一个字段并且可以根据需要使用任意数量的字段**。另外，备忘录对象中保存的状态不必与Originator的完整状态相匹配，只要保存的信息足以恢复Originator的状态，我们就可以开始了。

## 3. 什么时候使用备忘录设计模式

通常，备忘录设计模式将在某些操作不可执行的情况下使用，因此需要回滚到以前的状态。但是，**如果Originator的状态很重，使用备忘录设计模式会导致昂贵的创建过程和增加内存使用**。

## 4. 备忘录模式的例子

### 4.1 初始示例

现在让我们看一个备忘录设计模式的例子，假设我们有一个文本编辑器：

```java
public class TextEditor {

    private TextWindow textWindow;

    public TextEditor(TextWindow textWindow) {
        this.textWindow = textWindow;
    }
}
```

它有一个文本窗口，其中包含当前输入的文本，并提供了一种添加更多文本的方法：

```java
public class TextWindow {

    private StringBuilder currentText;

    public TextWindow() {
        this.currentText = new StringBuilder();
    }

    public void addText(String text) {
        currentText.append(text);
    }
}
```

### 4.2 备忘录

现在，假设我们希望我们的文本编辑器实现一些保存和撤消功能。**保存时，我们希望保存当前文本，因此，当撤消后续更改时，我们将恢复保存的文本**。

为此，我们将使用备忘录设计模式。首先，我们创建一个对象来保存窗口的当前文本：

```java
public class TextWindowState {

    private String text;

    public TextWindowState(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
```

这个对象就是我们的备忘录，如我们所见，我们选择使用String而不是StringBuilder来防止外部人员对当前文本进行任何更新。

### 4.3 发起人

**之后，我们必须为TextWindow类提供创建和使用备忘录对象的方法，使TextWindow成为我们的Originator**：

```java
private StringBuilder currentText;

public TextWindowState save() {
    return new TextWindowState(currentText.toString());
}

public void restore(TextWindowState save) {
    currentText = new StringBuilder(save.getText());
}
```

save()方法允许我们创建对象，而restore()方法使用它来恢复以前的状态。

### 4.4 看守人

最后，我们必须更新我们的TextEditor类。**作为Caretaker，它将保存Originator的状态并在需要时请求恢复它**：

```java
private TextWindowState savedTextWindow;

public void hitSave() {
    savedTextWindow = textWindow.save();
}

public void hitUndo() {
    textWindow.restore(savedTextWindow);
}
```

### 4.5 测试解决方案

让我们通过示例运行看看它是否有效。想象一下，我们在编辑器中添加一些文本，保存它，然后再添加一些文本，最后撤消。为了实现这一点，我们将在TextEditor上添加一个print()方法，该方法返回当前文本的字符串：

```java
TextEditor textEditor = new TextEditor(new TextWindow());
textEditor.write("The Memento Design Pattern\n");
textEditor.write("How to implement it in Java?\n");
textEditor.hitSave();
 
textEditor.write("Buy milk and eggs before coming home\n");
 
textEditor.hitUndo();

assertThat(textEditor.print()).isEqualTo("The Memento Design Pattern\nHow to implement it in Java?\n");
```

正如我们所看到的，最后一句话不是当前文本的一部分，因为备忘录在添加它之前被保存了。

## 5. 总结

在这篇简短的文章中，我们解释了备忘录设计模式及其用途，并通过一个示例说明了它在简单文本编辑器中的用法。