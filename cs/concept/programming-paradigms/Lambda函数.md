## 1. 概述

在本教程中，我们将讨论 lambda 函数及其在编程中的使用。

## 2.“Lambda”从何而来？

匿名函数、lambda 表达式或函数文字都是一回事。Lambda(或 lambda)是某些语言(如 Python)中赋予匿名函数的名称。这些是不受显式标识符约束的函数。这个名字来源于 Lambda 演算，这是一种数学系统，用于表达计算，由 Alonzo Church 博士在 1930 年代引入。

Church 博士令人难以置信的工作旨在探索数学基础和将复杂函数简化为简单的单参数“lambda”表达式。这些是没有名称的小函数，用于通过称为柯里化的过程创建更大的函数。柯里化意味着多参数函数被分解为简单的 1 参数函数。

这意味着复杂的功能可以被分解成匿名的小块。

此外，当你不一定要为非常简单的任务定义完整的函数时，创建简单的匿名函数会很方便。

## 3. 基本实现

在编程中，对此有很多应用，但最大的应用是排序、闭包和柯里化。为了便于阅读，我将使用 Python、JavaScript 和 C++ 实现以下代码。

lambda 函数的语法相当简单。

以下是 Python 中的几个示例：

```ruby
lambda x : x+1
lambda x,y : x+y
```

我们可以观察到“lambda”这个词象征着匿名函数。冒号之前的是函数需要的变量。冒号后面是函数的逻辑。

JavaScript 中的情况有些不同：

```javascript
//Traditional Anonymous Function
function(a){
    return a + 1;
}
```

这是一个匿名函数。它不能被命名。因此，对于大多数 JavaScript 应用程序，我们希望使用箭头“=>”函数来代替：

```javascript
// we can define them anonymously:
a => a + 1;
// or issue a name:
let myfunction = a => a+1;
```

这个箭头函数可以命名，更类似于 Python 中实现的 lambda 函数。

我们可以看到，箭头左侧是将要使用的变量，右侧是将要执行的逻辑。

转到 C++，我们可以看到箭头符号仍然存在(至少从 C++ 11 开始)。我们可以看到它们具有以下一般结构：

```cpp
[ capture clause ] (parameters) -> return-type  
{   
    method   
} 
```

我们可以像前面的那样制作一个示例函数：

```java
[](double a) -> double {return a + 1}
```

当然，如果我们想访问其他变量，我们可以像这样在捕获子句中指定它们：

```cpp
[variable](double a) -> double {
    if (a < variable) {
        return 0;
    } else {
        return a;
    }
}
```

这些捕获子句在 C++ 中很有趣，因为我们经常会看到 lambda 函数可以嵌套在更大的函数中。

## 4.分拣

通常，我们的目标是按特定逻辑对列表中的[项目进行排序。](https://www.baeldung.com/cs/choose-sorting-algorithm)如果逻辑简单，我们可以使用 lambda 函数来实现。


假设我们想在 Python 中按每个条目的长度对列表进行排序：

```python
a= ['somethingevenlonger', 'somethinglonger', 'something']
a.sort(key=lambda x : len(x))
print(a)
```

其输出是：

```python
['something', 'somethinglonger', 'somethingevenlonger']

```

在这个例子中，我们可以看到我们的 lambda 函数是：

```python
lambda x : len(x)
```

此函数返回参数“x”的长度。

排序方法使用此 lambda 函数作为键。

同样，在 JavaScript 中，我们可以使用箭头函数对字符串进行排序：


```javascript
const arr = [1,2,3,4,5,6] 

function sortNums(arr) {
    let sorted = arr.sort((a, b) => b - a);
    console.log(sorted.join(", "));
}

// Log to console
console.log(arr)
sortNums(arr)

```

以下代码的输出如下：

```javascript
[1 ,2 ,3 ,4 ,5 ,6]
6, 5, 4, 3, 2, 1
```

使用 C++，我们可以获得相同的结果：

```cpp
#include <iostream>
#include <functional>
#include <vector>
#include <algorithm>

int main() {
    auto vectors = std::vector<int> { 7, 5, 16, 8 };
    auto Lambda = [] (int s1, int s2) -> bool {
        return s1 < s2;
    };

    std::sort(vectors.begin(), vectors.end(), Lambda);

    for (auto v : vectors)
        std::cout<<v<<'.';
}
```

请注意，此处使用的 lambda 函数称为 Lambda 并采用两个整数参数。它根据参数大小之间的比较返回“真”或“假”布尔语句。

我们可以看到终端如何按顺序打印值：

```cpp
5.7.8.16.
```

## 5.关闭

有时，术语[闭包](https://www.baeldung.com/cs/closure)可与术语“lambda 函数”或“匿名函数”互换使用。但是，闭包实际上是分配了非局部变量的函数实例。我们可以将这些视为可以在范围内收集变量的匿名对象。

我们可以使用 Python 中的以下示例来演示这一点：

```python
def a(x):
    def b(y):
        return x + y
    return b  # Returns a closure.
```

我们可以看到b需要a(x)方法中的x变量。因为它没有在b内定义，所以它将使用前一个作用域 ( a )上的x 。

或者，我们可以使用 lambda 函数来获得相同的结果：

```python
def c(x):
    return lambda y: x + y  # Return a closure.
```

这两个都产生数学上相同的结果：

```python
result1 = a(4)
print(result1(4))

result2 = c(4)
print (result2(4))

# Both of these return 8
```

在 JavaScript 中，我们还可以实现闭包：

```javascript
function wrapper(x) {
    let f1 = (x) => {x=x+1;console.log(x);}
    return f1;
}

let x = 0;

let lambda = ()=>{x=x+1;console.log(x);}
lambda();
lambda();
lambda();

let w=wrapper();
w(x);
w(x);
w(x);

console.log(x);
```

此脚本输出以下内容：

```javascript
1
2
3
4
4
4
3

```

我们可以看到调用wrapper函数实际上并不是在修改变量x的值。这是因为它正在修改自己的局部变量 x。

下面是这个概念的另一个 C++ 实现：

```cpp
#include <iostream>
#include <functional>

std::function<void(void)> WrapClosure() {
    int x = 1;
    return [x](){std::cout << x << std::endl;};
}

int main() {
    int x = 1;
    auto lambda = [&x](){
        x += 1;
        std::cout << x << std::endl;
    };
    
    std::function<void(void)> wrapper = WrapClosure();
    lambda();
    lambda();
    lambda();
    std::cout << "x is equal to "<<x<<std::endl;
    wrapper();
    wrapper();
    wrapper();
    std::cout << "x is equal to "<<x<<std::endl;
}
```

输出如下：

```cpp
2
3
4
x is now equal to 4
1
1
1
x is now equal to 4
```

我们可以看到 lambda 包含来自较大主函数作用域中使用的x变量的地址 ( & ) 。在使用闭包时要注意这一点。由于 lambda 的范围有限，简单地使用x不会修改 main 函数中的x变量。

## 6.柯里化

柯里化是采用复杂函数并将它们转换为一系列单参数函数的过程。这有很多优点。最明显的一个是功能可以修改，不同的功能可以从相同的代码演变而来。

假设我们正在尝试用 Python 将两个数字相乘：

```python
def multiply(x,y):
    return xy

def multiplier(x):
    return lambda y: xy
```

到目前为止，这两个函数都做同样的事情。但是使用第二个函数，我们可以创建额外的函数：

```python
twice = multiplier(2)
tentimes = multiplier(10)
```

现在可以调用这些函数并将其重新用于其他目的。调用这些函数将如下所示：

```python
twice(4)
tentimes(4)
```

这与使用以下代码相同：

```python
multiply(4,2)
multiply(4,10)
```

使用 JavaScript，我们也可以实现这个概念：

```javascript
function multiply(x,y) {
    return xy;
}
function multiplier(x) {
    return (y)=>xy;
}

let timesfour = multiplier(4);
let timesfive = multiplier(5);

console.log(timesfour(2));
console.log(multiply(4,2));
console.log(timesfive(5));
console.log(multiply(5,5));
```

我们可以确认输出：

```javascript
8
8
25
25
```

在 C++ 中也是如此：

```cpp
#include <iostream>
#include <functional>

void multiply(int x,int y){
    std::cout<<xy<<std::endl;
}

auto multiplier(int x){
    return [x](int y){std::cout<<xy;};
}

int main(){
    multiply(2,3);
    auto timestwo = multiplier(2);
   
    timestwo(3);
}
```

这里，timestwo(3)和multiplier(2,3)输出相同的值：6。

## 7.过滤

Python 有一个常用的函数，叫做filter。此函数允许使用布尔逻辑表达式解析值。它具有以下语法：

```python
filter(object, iterable)
```

函数中的第一个参数可以是 lambda 函数。第二个必须是可迭代的，最简单的例子可能是一个列表。

这通常用于处理需要显示“所有小于 X 的项目”或“所有等于 Y 的项目”的列表。

下面的代码将演示如何使用这些列表操作中的第一个：

```python
iterable = [1,2,3,4,6]
a = filter(lambda x : x<3, iterable)

print(list(a))
```

同样，在 JavaScript 中，我们有：

```javascript
iterable=[1,3,4,5,6]
console.log(iterable.filter(element => element > 4));
```

这输出：

```javascript
[ 5, 6 ]
```

最后，使用 C++，我们可以像这样制作我们自己的过滤器函数：

```cpp
#include <iostream>     
#include <algorithm>    
#include <vector>       

int main () {
    std::vector<int> iterable = {1,2,3,5,15};
    std::vector<int> result (iterable.size());

    // copy only positive numbers:
    auto it = std::copy_if (iterable.begin(), iterable.end(), result.begin(), [](int i){return !(i>4);} );
    result.resize(std::distance(result.begin(),it));  // shrink container to new size

    std::cout << "result contains:";
    for (int& x: result) std::cout << ' ' << x;

    return 0;
}
```

这将输出以下内容：

```cpp
result contains: 1 2 3
```

## 8. 映射

映射类似于过滤器函数，但它不是使用布尔表达式来过滤值，而是实际更改值。

在 Python 中执行此操作的另一个有趣工具是 map 函数。

语法如下：

```python
map(object, iterable_1, iterable_2, ...)
```

这意味着我们可以对多个可迭代对象使用同一个对象。

当我们想对一个或多个可迭代对象中的所有项目应用相同的转换时，这会派上用场：

```python
numbers_list = [1, 1, 1, 1, 1, 1, 1]
mapped_list = list(map(lambda a: a + 1, numbers_list))
print(mapped_list)
```

以下代码的输出是：

```python
[2, 2, 2, 2, 2, 2, 2]

```

如我们所见，它向列表中的所有项目添加了“1”。

我们可以使用 JavaScript 箭头函数使用数组的 map 方法实现类似的行为：

```javascript
iterable=[1,3,4,5,6]
console.log(iterable.map(element => element + 4));
```

这将 4 添加到我们数组中的每个数字并返回：

```javascript
[ 5, 7, 8, 9, 10 ]
```

在C++中，我们也可以这样实现：

```cpp
#include <iostream>     
#include <algorithm>    
#include <vector>       

int main () {
    std::vector< int > arr = { 1,2,3,4,5,6,7,8,9 };
    std::for_each( arr.begin(), arr.end(), 
      [](int & x)
      {                                          //^^^ take argument by reference
          x += 2; 
          std::cout<<x<<'.';
      });
  
    return 0;
}
```

我们可以看到我们将 2 添加到向量中的每个项目。这是输出的样子：

```cpp
3.4.5.6.7.8.9.10.11.
```

## 9.总结

在本文中，我们讨论了 lambda 函数及其在编程中的使用。