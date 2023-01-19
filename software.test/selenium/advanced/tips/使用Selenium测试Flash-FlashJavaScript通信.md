## 使用 Selenium 测试 Adobe Flash

Flash 文件是网页的动画和/或交互式内容。这些类型的内容主要是由 Flash 开发环境创建的。然而，一些可用的插件允许你在 Ecllipse 和类似的开发环境中创建 Flash 内容。

### 问题陈述

Flash 文件是封闭文件，呈现在网页的容器中。对于 Mozilla 和 chrome 上的前 Flash 播放器以及 IE 中的 ActiveX 控件。容器嵌入`<object/>`或`<embed/>`标记在 HTML 中。因此，当你打开包含 Flash 文件的网页时，浏览器首先加载播放器(Flash 或 ActiveX)，然后将文件加载到其中以显示内容。使用任何 HTML 元素都无法访问 Flash 对象模型。

### 那么我们如何使用 Selenium 测试 Flash 内容呢？

对此的直接回答是 Selenium 没有与你的 Flash 内容交互的界面。Flash 文件是用 ActionScript 编写的，与 JavaScript 非常相似。与其他语言一样，Flash 文件也包含编程元素。例如，它们也有按钮、文本框等。与这些元素交互会导致 Flash 文件调用一些内部方法来完成任务。例如，Flash 内容上的按钮可能会导致 Flash 内容的背景颜色发生变化。为了拯救 ActionScript，它向开发人员公开了一个名为 ExternalInterface 的类。如果你想向外界公开 Flash 文件的内部方法，则该类尤为重要。外部世界是浏览器或任何托管内容的编程控件。因此，如果我们将 Flash 文件中的内部方法暴露给浏览器，那么我们就可以使用 JavaScript 直接调用它们。这里使用了 Selenium，它可以让你在网页上执行和注入 JavaScript。

### 何时自动化 Flash 内容测试？

截至目前，最好对自动化 Flash 内容进行可靠的投资回报计算。原因是测试人员必须在 Flash 程序中编写大量函数并将它们公开以供浏览器调用。如果你的投资回报率证明是有益的，请继续编写代码。所以我想到的下一个问题是

### 我们如何在 Flash 中公开方法？

这是一个相当简单的过程。你所要做的就是使用 ExternalInterface 类。我们将从一个小例子开始。这是一个包含 3 个按钮的小型 Flash 应用程序。加、减和乘。这三个按钮在单击时会向文本控件发送文本并记下按钮的名称。因此，如果你单击添加，你会发现文本“添加”被添加到文本控件中。

要查看示例应用程序，请单击[此处](https://drive.google.com/drive/folders/0B3SaC4MIQsEEZkwwcDhHaVQ4OFk?usp=sharing)。

现在的任务是我们如何从 Selenium 以编程方式完成它。这就是一个简单的 Flash 文件的样子。

```java
package

{

import flash.display.Sprite;

import flash.events.;

import flash.external.ExternalInterface;

import flash.text.TextField;

import flash.utils.Timer;

import flash.text.TextFieldType;

import flash.text.TextFieldAutoSize;

import flash.system.Security;

public class SimpleCalculation extends Sprite

{

private var outputNumber:TextField;

private var AddByOne:Sprite;

private var SubtractFive:Sprite;

private var MultiplyTwo:Sprite;

public function SimpleCalculation()

{

/

This line allows the ActionScrit object to communicate between domains.

If this is not used we cannot call functions from browser

/

Security.allowDomain("");

//Creating the text field

outputNumber = new TextField();

outputNumber.width = 450;

outputNumber.height = 325;

outputNumber.multiline = true;

outputNumber.wordWrap = true;

outputNumber.border = true;

outputNumber.text = "0"

addChild(outputNumber);

//creating button object

AddByOne = new Sprite();

AddByOne.mouseEnabled = true;

AddByOne.x = outputNumber.width + 10;

AddByOne.graphics.beginFill(0xcccccc);

AddByOne.graphics.drawRoundRect(0, 0, 80, 18, 10, 10);

AddByOne.graphics.endFill();

AddByOne.addEventListener(MouseEvent.CLICK, addNumberCallFromBrowser);

addChild(AddByOne);

SubtractFive = new Sprite();

SubtractFive.mouseEnabled = true;

SubtractFive.x = outputNumber.width + 10;

SubtractFive.graphics.beginFill(0xcccccc);

SubtractFive.graphics.drawRoundRect(0, 20, 80, 18, 10, 10);

SubtractFive.graphics.endFill();

SubtractFive.addEventListener(MouseEvent.CLICK, substractNumberCallFromBrowser);

addChild(SubtractFive);

MultiplyTwo = new Sprite();

MultiplyTwo.mouseEnabled = true;

MultiplyTwo.x = outputNumber.width + 10;

MultiplyTwo.graphics.beginFill(0xcccccc);

MultiplyTwo.graphics.drawRoundRect(0, 40, 80, 18, 10, 10);

MultiplyTwo.graphics.endFill();

MultiplyTwo.addEventListener(MouseEvent.CLICK, multiplyNumberCallFromBrowser);

addChild(MultiplyTwo);

outputNumber.text = "Completed loading \n";

initApp();

}

private function addNumberCallFromBrowser(event:MouseEvent):void

{

Add();

}

private function multiplyNumberCallFromBrowser(event:MouseEvent):void

{

Multiply();

}

public function substractNumberCallFromBrowser(event:MouseEvent):void

{

Subtract();

}

/

The primary purpose of this function

is to initialize the external interface calls.

External interface calls fail if Javascript is not ready

on the page when we make the call. This causes a faliure when

JavaScripts makes a call to the expected functions

NOTE: this function will try for 5 seconds to get the JavaScript ready

notification

/

private function initApp():void

{

var counter:Number = 0;

if(ExternalInterface.available)

{

while(counter < 300)

{

if(isJavaScripLoaded())

{

outputNumber.appendText("Java script loaded \n");

ExternalInterface.addCallback("Multiply" , Multiply);

ExternalInterface.addCallback("Subtract" , Subtract);

ExternalInterface.addCallback("Add" , Add);

counter= 399;

}

else

{

outputNumber.appendText("Javascript not ready yet \n");

}

counter = counter + 1;

outputNumber.appendText("Javascript not ready yet! trying again " + String(counter) + "\n");

}

}

}

private function isJavaScripLoaded():Boolean

{

var sa:Boolean = ExternalInterface.call("isJavaScriptReady");

return sa;

}

private function Add():void

{

outputNumber.appendText("Add was called \n");

}

private function Subtract():void

{

outputNumber.appendText("Subtract was called \n");

}

private function Multiply():void

{

outputNumber.appendText("Multiply was called \n");

}

}

}
```

上面的代码是在非常接近 java 脚本的 ActionScript 中。与其深入研究 ActionScript 的血淋淋的细节以及这段代码的结构，不如让我们只看这里的重要部分 这是一个简单的 Flash 文件，它具有三个定义的按钮

-   添加一个
-   减五
-   乘二

和一个文本框

-   输出编号

让我们看看 AddByOne 做了什么。当有人在浏览器上显示 时单击此按钮，将调用 addNumberCallFromBrowser() 方法。此方法与 OnClick 鼠标事件挂钩。这意味着只要有人点击这个元素，addNumberCallFromBrowser() 就会被执行。现在转到此方法的代码部分，你会注意到它依次调用 Add() 方法。如你所见，Add() 方法在文本框控件中打印文本。同样三个按钮都有对应的功能，如代码所示。现在，如果我们要测试这个应用程序，我们会对使用 Selenium 单击三个按钮中的任何一个感兴趣，对吗？是的，也可以围绕它构建一些更复杂的场景，让我们只关注单击按钮的任务。最终，通过单击我们想要测试 Add()、Substract() 或 Multiply() 方法的按钮？正确的？因此，要从浏览器执行此操作，我们必须将这些方法公开给外界。案例中的浏览器，具体来说是在托管它的浏览器实例中运行的 JavaScript 引擎。这是在 initApp() 方法中完成的。你可以看到我们有

```java
ExternalInterface.addCallback("Multiply" , Multiply);

ExternalInterface.addCallback("Subtract" , Subtract);

ExternalInterface.addCallback("Add" , Add);
```

这将使我们能够直接从 JavaScript 调用此方法。这是托管此 Flash 文件的页面，你可以使用它来运行 selenium 代码。

现在是硒部分

如上所述，我们刚刚向浏览器公开了我们的方法(乘法、加法和减法)。我们如何调用这些方法？

很简单，通过使用 selenium 注入 java 脚本。我希望你们熟悉 Selenium 的用法并且可以轻松理解这部分内容。否则尝试从我们网站上的教程开始，[在这里](https://toolsqa.com/)

单击测试应用程序页面的查看源代码，你会发现 .Swf 文件使用 id:SimpleCalculation 托管

我们知道 SimpleCalulation 对象暴露了 Add() 方法。所以像这样的 javascript 可以解决问题并调用 Add() 方法

" document.getElementById('SimpleCalculation').Add() "

C# 上的完整 selenium 代码如下所示

```java
InternetExplorerDriver ie = new InternetExplorerDriver(options);

ie.Navigate().GoToUrl(@"https://googledrive.com/host/0B3SaC4MIQsEEZkwwcDhHaVQ4OFk/calc.html");

ie.ExecuteScript("document.getElementById('SimpleCalculation').Add()");

ie.ExecuteScript("document.getElementById('SimpleCalculation').Multiply()");

ie.ExecuteScript("document.getElementById('SimpleCalculation').Subtract()");

ie.ExecuteScript("CallAdd()"); //Look at the page html source to understand this and following methods

ie.ExecuteScript("CallMultiply()");

ie.ExecuteScript("CallSubtract()");
```