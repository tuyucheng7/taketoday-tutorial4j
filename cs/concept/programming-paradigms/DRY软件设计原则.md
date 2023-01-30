## 1. 概述

在本教程中，我们将了解 DRY 软件设计原则。

## 2. 定义

DRY 代表不要重复自己。这是一个软件开发原则，其目标是消除逻辑重复。

DRY 最初是在[The Pragmatic Programmer](https://www.amazon.com/Pragmatic-Programmer-Journeyman-Master/dp/020161622X)一书中引入的，它确保特定的逻辑在代码库中只出现一次。

## 3. 一个例子

例如，编写一个包含特定逻辑的函数，然后在我们的代码中多次调用它，就是应用 DRY 原则的一种形式。

这是一个伪代码，它接收两个华氏温度并在应用 DRY 之前将它们转换为摄氏温度：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9a8998cb4274f5363470d9b1d6932f1f_l3.svg)

现在这里是应用 DRY 后的相同程序：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fbb12c359e65e3b41d5c35ea6f4c3d15_l3.svg)

我们可以看到，在应用 DRY 之后，将华氏度转换为摄氏度的逻辑在我们的代码中只出现了一次。

## 4.DRY的优势

DRY 原则的优点包括：

-   它使代码库更易于维护，因为如果我们想更改逻辑或添加逻辑，我们只需要在一个地方更改它，而不是逻辑出现的多个位置
-   它使代码更易于阅读，因为代码中的冗余会更少

值得一提的是，滥用 DRY(在我们不需要的地方创建函数、进行不必要的抽象等)会导致我们的代码更加复杂，而不是简单。

## 5. DRY的对立面

WET(可以代表 We Enjoy Typing, Write Every Time, and Waste Everyone's Time)是指我们在代码中多次编写相同的逻辑，这违反了 DRY 原则。结果，代码变得更难阅读。此外，如果我们想要更改逻辑，我们必须更改它在代码库中的所有外观，从而使代码更难维护。

## 6.总结

在这篇简短的文章中，我们了解了 DRY 软件原理及其优势。