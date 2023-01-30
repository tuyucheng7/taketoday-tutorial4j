## 1. 概述

许多程序员互换使用术语参数和参数，尽管它们具有不同的含义。因此，我们将在本教程中了解参数和参数之间的区别。

## 2.参数和参数

让我们看一个伪代码示例，以显示术语参数和参数的明确定义。事实上，这个程序中的一个方法或函数将两个数字作为输入并输出这些值的总和：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4766c815402ab7e61877b5c46fae4a29_l3.svg)

### 2.1. 参数

参数是我们可以在函数声明中定义的变量。事实上，我们在函数中使用了这些变量。此外，函数描述中的编程语言决定了数据类型规范。这些变量有助于函数的整个执行。此外，它们被称为局部变量，因为它们仅在函数内可用。

现在，让我们借助上面的示例详细了解这些变量。函数![和(](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4fdb7fe8b83097818a9298faa746cad7_l3.svg)a ![,](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5a10bae6f9bf9f995b728faaadc7be2c_l3.svg)b![)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c43604f278b2180dd87797c6a1b2a6db_l3.svg)在括号内包含两个值![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0e55b0b3943237ccfc96979505679274_l3.svg), 和![b](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ad69adf868bc701e561aa555db995f1f_l3.svg)。所以，![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0e55b0b3943237ccfc96979505679274_l3.svg)和![b](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ad69adf868bc701e561aa555db995f1f_l3.svg) 是参数。实际上，这是两个局部变量，生命周期仅限于函数。它们还可以采用调用函数时赋予函数的任何值。

### 2.2. 参数

参数是赋予函数执行的变量。此外，函数的局部变量采用参数的值，因此可以处理这些参数以获得最终输出。

现在，让我们看看上面伪代码中的函数调用。因此，在伪代码的末尾，我们调用了函数![和](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a8f0c998bdde3a259a2e7e618c04e828_l3.svg)。换句话说，参数是我们作为输入给出的实际值，以获得所需的输出。因此，在我们的示例中，我们选择![5](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-48348ef601c56286abf49bafe09c7af1_l3.svg)和![10](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f2dd7a07a97336ce3d17ca56d2618366_l3.svg)作为参数，那么获得的输出将为![15](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-24356614dd1cedf9fcb381d867965978_l3.svg).

## 3. 参数和参数的区别

在构建函数时，我们可以进行某些输入以处理函数中包含的指令。在这种情况下，我们经常互换使用术语“参数”和“参数”来指代这些输入。为了澄清，我们看一下差异，看看在什么情况下使用哪个。现在让我们总结一下参数和参数之间的主要区别：

 ![[begin{tabular}{|p{7cm}|p{7cm}|} hline textbf{Arguments} & textbf{Parameters}  hline 我们使用函数中的变量来发送调用函数到接收函数。 & 我们在定义函数的时候定义了参数。  hline 我们在函数调用语句中使用参数将值从调用函数传输到接收函数。 & 为了从参数中获取值，我们在函数声明中使用了局部变量。 hline 我们总是在调用时将每个参数分配给函数声明中的参数。 & 当我们调用该函数时，参数的值被分配给局部变量。 hline 实际参数是它们的另一个名称。 & 它们有时被称为形式参数。  hline end{tabular}]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f5398054cfc053ffbb4af873c30236ec_l3.svg)

## 4。总结

在本教程中，我们讨论了参数和参数之间的主要区别。我们演示了何时以及如何使用它们。