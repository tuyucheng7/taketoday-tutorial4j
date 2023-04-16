## 1. 概述

本文介绍了在 JetBrains 的 Java IDE IntelliJ IDEA 中编辑、构建和运行 Java 应用程序所需的键盘快捷键。键盘快捷键可以节省我们的时间，因为我们可以将手放在键盘上并更快地完成工作。

[我们在上一篇文章中](https://www.baeldung.com/intellij-refactoring)研究了使用 IntelliJ IDEA 进行重构，因此我们不在此处介绍这些快捷方式。

## 2. 一条捷径

如果我们只记得一个IntelliJ IDEA 快捷键，那么它一定是Help – Find Action，在 Windows 中是 Ctrl + Shift + A，在macOS中是 Shift + Cmd + A。此快捷键打开一个搜索窗口，其中包含所有菜单项和其他 IDE 操作，无论它们是否具有键盘快捷键。我们可以立即键入以缩小搜索范围，使用光标键选择一个函数，然后使用Enter 键执行它。

从现在开始，我们将在菜单项名称后面的括号中直接列出键盘快捷键。如果 Windows 和 macOS 之间的快捷方式不同(通常如此)，那么我们会先使用 Windows 快捷方式，然后再使用 macOS 快捷方式。

在 macOS 计算机上，Alt键通常称为Option。在本文中我们仍将其称为Alt以保持我们的快捷方式简短。

## 3. 设置

让我们从配置 IntelliJ IDEA 和我们的项目开始。

我们在 Windows 中使用File – Settings ( Ctrl + Alt + S )来设置 IntelliJ ，在 macOS 中使用IntelliJ IDEA – Preferences ( Cmd +, )。要配置我们当前的项目，我们在项目视图中选择顶级元素。它有项目名称。然后我们可以使用File – Project Structure ( Ctrl + Alt + Shift + S / Cmd + ; )打开它的配置。

## 4.导航到文件

配置完成后，我们就可以开始编码了。首先，我们需要找到我们要处理的文件。

我们通过浏览左侧的项目视图来选择文件。我们还可以使用文件 - 新建(Alt + Insert / Cmd + N )在当前选定的位置创建新文件。要删除当前选定的文件/文件夹，我们触发编辑 – 删除(删除/ ⌫)。我们可以使用Windows 上的Esc和macOS 上的⎋从项目视图切换回编辑器。没有菜单项。

要直接打开一个类，我们使用Navigate – Class ( Ctrl + N / Cmd + O )。这适用于 Java 类和其他语言的类，例如 TypeScript 或 Dart。如果我们想打开任何文件，例如 HTML 或文本文件，我们使用导航 - 文件(Ctrl + Shift + N / Shift + Cmd + O)。

所谓切换器就是当前打开文件的列表。我们只能通过快捷键Ctrl + Tab看到切换器，因为它没有菜单项。最近打开的文件列表可通过View – Recent ( Ctrl + E / Cmd + E )获得。如果我们再次按下该快捷方式，那么我们只会看到最近更改的文件。

我们使用Navigate – Last Edit Location ( Ctrl + Shift + Backspace / Shift + Cmd + ⌫ )转到上次代码更改的位置。IntelliJ 还跟踪我们的编辑器文件位置。我们可以使用Navigate – Back ( Ctrl + Alt + Left / Cmd + [ ) 和Navigate – Forward ( Ctrl + Alt + Right / Cmd + ] )来浏览历史。

## 5. 在文件中导航

我们到达了我们想要处理的文件。现在我们需要导航到那里的正确位置。

我们直接使用Navigate – File Structure ( Ctrl + F12 / Cmd + F12 )跳转到一个类的字段或方法。与Help – Find Action一样，我们可以立即键入以缩小显示的成员范围，使用光标键选择一个成员，然后使用Enter跳转到该成员。如果我们想在当前文件中突出显示成员的用法，我们使用Edit – Find Usages – Find Usages in File ( Ctrl + F7 / Cmd + F7 )。

我们使用Navigate – Declaration or Usages ( Ctrl + B / Cmd + B )到达基类或方法的定义。顾名思义，调用基类或方法本身的功能会显示其用法。由于这是一个非常常用的功能，它有一个鼠标快捷键：Ctrl + Click on Windows 和Cmd + Click on macOS。如果我们需要在我们的项目中查看一个类或方法的所有使用，我们调用编辑 - 查找用法 - 查找用法(Alt + F7)。

我们的代码经常调用其他方法。如果我们将光标放在方法调用括号内，那么View – Parameter Info ( Ctrl + P / Cmd + P ) 会显示方法参数的信息。在默认的IntelliJ IDEA配置中，这个参数信息会在短暂的延迟后自动出现。

要查看类型或方法的快速文档窗口，我们需要View – Quick Documentation ( Ctrl + Q / F1 )。在默认的 IntelliJ IDEA 配置中，如果我们将鼠标光标移到类型或方法上并稍等片刻，快速文档会自动出现。

## 6.编辑文件

### 6.1. 更改代码

一旦我们到达正确的文件和正确的位置，我们就可以开始编辑我们的代码。

当我们开始键入变量、方法或类型的名称时，IntelliJ IDEA 会帮助我们使用代码 – 代码完成 – 基本( Ctrl + Space )完成这些名称。在默认的 IntelliJ IDEA 配置中，此功能也会在短暂延迟后自动启动。我们可能需要输入一个右括号，并且必须在末尾加上一个分号。Code – Code Completion – Complete Current Statement ( Ctrl + Shift + Enter / Shift + Cmd + Enter ) 完成我们的当前行。

代码 - 覆盖方法(Ctrl + O)让我们选择继承的方法来覆盖。使用Code – Generate ( Alt + Insert / Cmd + N )，我们可以创建通用方法，如 getter、setter 或toString()。

我们可以使用Code – Surround with ( Ctrl + Alt + T / Alt + Cmd +T ) 在我们的代码周围放置控制结构，例如if语句。我们甚至可以用Code – Comment with Block Comment注释掉一整块代码。在 Windows 中是Ctrl + Shift + / ，在 macOS 中是 Alt + Cmd + /。

IntelliJ IDEA 会自动保存我们的代码，例如，在运行之前。我们仍然可以使用File – Save all ( Ctrl + S / Cmd + S )手动保存所有文件。

### 6.2. 浏览代码

有时，我们需要在文件中移动代码。代码 – 向上移动语句( Ctrl + Shift + Up / Alt + Shift +Up ) 和代码 – 向下移动语句( Ctrl + Shift + Down / Alt + Shift +Down ) 为当前选定的代码执行此操作。如果我们没有选择任何内容，则移动当前行。同样，Edit – Duplicate Line or Selection ( Ctrl + D / Cmd + D ) 复制所选代码或当前行。

我们可以使用Navigate – Next Highlighted Error ( F2 ) 和Navigate – Previous Highlighted Error ( Shift + F2 )循环浏览当前文件中的错误。如果我们将光标放在不正确的代码上并按下Alt + Enter，IntelliJ IDEA 将建议修复。此快捷方式没有菜单项。如果没有错误，该快捷方式也可能会建议对我们的代码进行更改。

## 7.查找和替换

我们经常需要查找和替换代码。以下是我们如何在当前文件或所有文件中执行此操作。

要在我们当前的文件中查找文本，我们使用Edit – Find – Find ( Ctrl + F / Cmd + F )。要替换当前文件中的文本，我们使用编辑 – 查找 – 替换( Ctrl + R / Cmd + R )。在这两种情况下，我们都使用编辑 – 查找 –查找下一个事件( F3 / Cmd + G ) 和编辑 – 查找 –查找上一个 事件( Shift + F3 / Shift + Cmd + G ) 浏览搜索结果。

我们还可以使用Edit – Find – Find in Files ( Ctrl + Shift + F / Shift + Cmd + F )在所有文件中查找文本。同样，在文件中编辑 – 查找 – 替换 (Ctrl + Shift + R / Shift + Cmd +R)替换我们所有文件中的文本。我们仍然可以使用F3 / Cmd + G和Shift + F3 / Shift + Cmd + G来浏览我们的搜索结果。

## 8.构建并运行

我们希望在完成编码后运行我们的项目。

当我们运行我们的项目时，IntelliJ IDEA 通常会自动构建我们的项目。使用Build – Build Project ( Ctrl + F9 / Cmd + F9 )，我们手动验证我们最近的代码更改是否仍然可以编译。我们可以使用Build – Rebuild Project ( Ctrl + Shift + F9 / Shift + Cmd + F9 )从头开始重建我们的整个项目。

要使用当前运行配置运行我们的项目，我们使用Run – Run '(configuration name)' ( Shift + F10 / Ctrl + R )。我们使用Run – Run… ( Alt + Shift + F10 / Ctrl + Alt + R )执行特定的运行配置。同样，我们可以使用Run – Debug '(configuration name)' ( Shift + F9 / Ctrl + D ) 调试当前运行配置，使用Run – Debug ( Alt + Shift + F9 / Ctrl + Alt)调试任何其他运行配置+ D)。

## 九、调试

我们的项目会有错误。调试帮助我们找到并修复这些错误。

调试器在断点处停止。我们使用Run – View Breakpoints ( Ctrl + Shift + F8 / Shift + Cmd + F8 )查看当前断点。我们可以使用Run – Toggle Breakpoint – Line Breakpoint ( Ctrl + F8 / Cmd + F8 )在当前行切换断点。

当我们的代码在调试过程中遇到断点时，我们可以使用Run – Debugging Actions – Step Over ( F8 )跳过当前行。因此，如果该行是一个方法，我们将一次性执行整个方法。或者，我们可以使用Run – Debugging Actions – Step Into ( F7 )深入研究当前行的方法。

调试时，我们可能希望运行我们的代码，直到当前方法完成。这就是Run – Debugging Actions – Step Out ( Shift + F8 ) 所做的。如果我们希望我们的程序运行到光标所在的行，那么运行 – 调试操作 – 运行到光标( Alt + F9 ) 即可完成此操作。如果我们希望我们的程序一直运行到遇到下一个断点，那么运行 – 调试操作 – 恢复程序( F9 ) 就可以做到这一点。

## 10. 混帐

我们的程序通常驻留在 Git 存储库中。IntelliJ IDEA 对 Git 有很好的支持。

我们有一个键盘快捷键可以为我们提供所有可能的 Git 操作：Git – VCS 操作(Alt + ` / Ctrl + V)。正如预期的那样，我们可以使用光标选择项目并按Enter键执行它们。这也是实现默认情况下没有键盘快捷键的常用功能的好方法，例如Show History或Show Diff。

如果我们想从远程 Git 存储库更新我们的项目，那么我们去Git – 更新项目( Ctrl + T / Cmd + T )。当我们需要在 Git 中提交更改时，可以使用Git – Commit ( Ctrl + K / Cmd + K )。要将我们的更改还原到 Git 中的内容，我们使用Git – Uncommitted Changes – Rollback ( Ctrl + Alt + Z / Alt + Cmd + Z )。而Git – Push ( Ctrl + Shift + K / Shift + Cmd + K ) 将我们的更改推送到远程 Git 存储库。

## 11.总结

键盘快捷键可以节省我们的时间，因为我们可以将手放在键盘上并更快地完成工作。本文介绍了在 IntelliJ IDEA 中配置、导航、编辑、查找和替换、运行和调试程序的快捷方式。

我们还查看了使用 Git 的快捷方式。