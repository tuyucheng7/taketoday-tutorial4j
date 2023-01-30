## 1. 概述

在本教程中，我们将研究和比较两种类型的条件语句：if-else和switch语句。

## 2. 计算机编程中的条件结构

我们使用条件语句根据条件执行一组特定的指令。此条件通常是一个计算结果为true或false的布尔表达式：

![If-else 流程](https://www.baeldung.com/wp-content/uploads/sites/4/2022/08/ielse.jpg)

if-else语句就是这种情况。但是，条件逻辑可能有两个以上的分支：

![开关流](https://www.baeldung.com/wp-content/uploads/sites/4/2022/08/switch.jpg)

在这种情况下，我们使用switch语句。

## 3. If-Else 和 Switch 的区别

现在让我们比较一下这两种说法。

### 3.1. If-Else 和 Switch 的可读性

switch块比链式if-else语句更具可读性和可维护性。扩展switch case 块比扩展if-else块更容易，因为我们必须评估所有以前的 if-else条件才能正确插入新的else块。相比之下，我们可以很容易地在 switch 语句中添加和删除标签。因此，switch语句使代码更易于更改和维护。

多个if-else分支更难阅读和理解，尤其是当它们嵌套且数量众多时。因此，链接的if-else块非常容易出错，因为我们很容易错过else语句。这可能会引入错误。

就代码占用空间而言，if-else梯形图的代码行数少于开关块。在if-else块的情况下，我们不必使用break和default 。然而，我们发现if-else语句块是重复的，而switch块是顺序的。

### 3.2. Switch 中的常量大小写标签

对于大多数现代编程语言，如C、C++和Java，开关条件必须在[编译时知道其每个案例的值。](https://www.baeldung.com/cs/runtime-vs-compile-time)这意味着我们不能使用变量作为案例值。我们必须只对 switch case 使用常量表达式。然而，很少有像Swift这样的编程语言允许我们在 case 语句中使用变量和表达式。

另一方面，在if-else块中，在执行代码时评估条件，因此它支持变量和表达式。

### 3.3. Switch掉壳问题

switch语句在大多数编程语言中都存在大小写下降的问题。当程序员忘记在任何单个 case 块中添加break语句时，就会出现此问题。结果，代码将执行那个 case 块，然后移动到下一个 case 块来执行它。一直持续到代码找到break语句或执行所有情况。

让我们考虑以下 C 代码片段。它将打印123Unknown 值而不仅仅是打印1：

```c
int main()
{
    int value=1;
   
    switch(value)
    {
        case 1:
            printf("1");
	case 2:
            printf("2");
	case 3:
	    printf("3");
	default:
	    printf("Unknown value");
    }
}
```

然而，在Swift 中， switch 语句没有 fall-through，所以我们不需要break来定位流程。

### 3.4. Switch的内部实现

大多数编译器将switch构造实现为跳转[表](https://www.baeldung.com/cs/hash-tables)。跳转表是一种抽象数据结构，现代编译器使用它来将流程控制转移到另一个位置。跳转表的实际实现可能因案例数量而异。当只有少数情况时，它可以是字典或[地图](https://www.baeldung.com/cs/ml-map-object-detection)的形式，而对于大量情况，它通常是[哈希 表](https://www.baeldung.com/cs/hash-tables)。

编译器将 case 与 switch 表达式进行匹配，然后通过在表中进行[跳转来执行它。](https://www.baeldung.com/cs/jump-search-algorithm)因此，当我们的 case 标签靠得很近时，我们的switch语句将比if-else更有效。这是因为大多数跳转在[内存中都是](https://www.baeldung.com/cs/memory-allocation)[连续](https://www.baeldung.com/cs/functional-programming)的。要进行跳转，我们只需将一个值添加到指向表基址的基址程序计数器。

### 3.5. 交换机内部示例

我们可以通过一个例子更好地理解它。这是一个简单的开关块：

```c
switch (i) 
{
   case 1: printf("case 1"); break;
   case 11: printf("case 11"); break;
   case 111: printf("case 111"); break;
}

```

对于这个switch语句，我们的编译器可以为每种情况生成以下粗函数调用：

```c
void case1() { printf("case 1"); }
void case11() { printf("case 11"); }
void case111() { printf("case 111"); }
```

然后，它将使用一个函数指针在运行时进行正确的调用：

```c
typedef void (pswitchfunccallback)(void);
pswitchfunccallback func_array[3] = {case1, case11, case111};

if ((unsigned)i<111)    
    func_array[i]();
```

这将具有不同案例块的数量![好的)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4845f61c61d7b8e9566629499a8850bc_l3.svg)的时间复杂度。![钾](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7fb8d8d37cb2b48aee9e97aee7728d8f_l3.svg)每个函数的地址都存储在跳转表中，并由优化的哈希函数访问。

### 3.6. If-Else内部实现

另一方面，大多数编译器为if-else条件块构造[二叉树。](https://www.baeldung.com/cs/binary-search-trees)对于典型的 if-else 块，编译器将其转换为标记列表，然后根据这些标记创建抽象语法树。稍后根据比较来评估此树以选择特定分支。

因此，这是通过进行固有的缓慢[比较](https://www.baeldung.com/cs/bidirectional-search)来实现的。

### 3.7. If-Else 和 Switch 的速度

一般来说，在值集相对较小且每个值出现的可能性相同的情况下，我们发现 switch case 的[执行时间](https://www.baeldung.com/cs/compile-load-execution-time)低于if-else块的执行时间。在值集很大且输入值不统一的情况下，如果最频繁的值在链的早期被条件覆盖，则if-else块的性能会更好。

然而，实际上，这两个语句的执行时间之间的差异可以忽略不计。

## 4。总结

在本文中，我们讨论了if-else和switch语句。

我们发现if-else条件分支对布尔数据值表现良好，而switch语句对固定数据值表现更好。

就速度而言，我们更喜欢if-else，因为大多数情况下只有少数几个值会出现，而如果所有情况的可能性均等，我们提倡使用switch 。

就可读性而言，switch语句通常更具可读性和简洁性。