## 1. 概述

在本教程中，我们将展示[爬山算法](https://www.baeldung.com/cs/hill-climbing-algorithm)及其实现。我们还将研究它的优点和缺点。在直接进入之前，让我们简要讨论一下生成和测试算法方法。

## 2. 生成并测试算法

这是一个非常简单的技术，它允许我们通过算法来寻找解决方案：

1.  将当前状态定义为初始状态
2.  对当前状态应用任何可能的操作并生成可能的解决方案
3.  将新生成的解决方案与目标状态进行比较
4.  如果达到目标或无法创建新状态，则退出。否则返回步骤2

它对简单的问题非常有效。由于它是穷举搜索，因此在处理大问题空间时考虑它是不可行的。它也被称为大英博物馆算法(试图通过随机探索在大英博物馆中找到一件文物)。

这也是生物识别领域中爬山攻击背后的主要思想。这种方法可用于生成合成生物特征数据。

## 3.简单爬山算法介绍

在爬山技术中，我们从山脚开始，向上走，直到我们到达山顶。换句话说，我们从初始状态开始，不断改进解决方案，直到达到最佳状态。

它是生成和测试算法的一种变体，它会丢弃所有看起来没有希望或似乎不太可能引导我们达到目标状态的状态。为了做出这样的决定，它使用启发式(评估函数)来指示当前状态与目标状态的接近程度。

简单来说，Hill-Climbing = generate-and-test + heuristics

我们来看简单的爬山算法：

1.  将当前状态定义为初始状态
2.  循环直到达到目标状态或不能在当前状态上应用更多运算符：
    1.  将操作应用于当前状态并获得新状态
    2.  将新状态与目标进行比较
    3.  如果达到目标状态就退出
    4.  使用启发式函数评估新状态并将其与当前状态进行比较
    5.  如果新状态比当前状态更接近目标，则更新当前状态

正如我们所看到的，它通过迭代改进达到了目标状态。在爬山算法中，找到目标相当于到达山顶。

## 4.例子

爬山算法可以归类为知情搜索。所以我们可以使用它来实现任何基于节点的搜索或诸如 n-queens 问题之类的问题。为了容易理解这个概念，我们将举一个非常简单的例子。

让我们看下图：

[![初始目标](https://www.baeldung.com/wp-content/uploads/2017/06/init_goal.png)](https://www.baeldung.com/wp-content/uploads/2017/06/init_goal.png)

解决任何爬山问题的关键是选择合适的[启发式函数](https://www.baeldung.com/cs/heuristics)。

让我们定义这样的函数h：

h(x) = +1 对于支撑结构中的所有块，如果块正确定位，否则 -1 对于支撑结构中的所有块。

在这里，如果它具有与目标状态相同的支持结构，我们将调用正确定位的任何块。根据前面讨论的爬山程序，让我们看看所有迭代及其启发式方法以达到目标状态：

## [![状态迭代](https://www.baeldung.com/wp-content/uploads/2017/06/state_iterations.png)](https://www.baeldung.com/wp-content/uploads/2017/06/state_iterations.png) 5.实施

现在，让我们使用爬山算法实现相同的示例。

首先，我们需要一个State类，它将存储表示每个状态下块位置的堆栈列表。它还将存储该特定状态的启发式方法：

```java
public class State {
    private List<Stack<String>> state;
    private int heuristics;
    
    // copy constructor, setters, and getters
}
```

我们还需要一种方法来计算状态的启发值。

```java
public int getHeuristicsValue(
  List<Stack<String>> currentState, Stack<String> goalStateStack) {
 
    Integer heuristicValue;
    heuristicValue = currentState.stream()
      .mapToInt(stack -> {
          return getHeuristicsValueForStack(
            stack, currentState, goalStateStack);
    }).sum();
 
    return heuristicValue;
}

public int getHeuristicsValueForStack(
  Stack<String> stack,
  List<Stack<String>> currentState,
  Stack<String> goalStateStack) {

    int stackHeuristics = 0;
    boolean isPositioneCorrect = true;
    int goalStartIndex = 0;
    for (String currentBlock : stack) {
        if (isPositioneCorrect 
          && currentBlock.equals(goalStateStack.get(goalStartIndex))) {
            stackHeuristics += goalStartIndex;
        } else {
            stackHeuristics -= goalStartIndex;
            isPositioneCorrect = false;
        }
        goalStartIndex++;
    }
    return stackHeuristics;
}

```

此外，我们需要定义运算符方法，这将使我们获得一个新的状态。对于我们的示例，我们将定义其中两种方法：

1.  从堆栈中弹出一个块并将其推入新堆栈
2.  从堆栈中弹出一个块并将其推入其他堆栈之一

```java
private State pushElementToNewStack(
  List<Stack<String>> currentStackList,
  String block,
  int currentStateHeuristics,
  Stack<String> goalStateStack) {
 
    State newState = null;
    Stack<String> newStack = new Stack<>();
    newStack.push(block);
    currentStackList.add(newStack);
    int newStateHeuristics 
      = getHeuristicsValue(currentStackList, goalStateStack);
    if (newStateHeuristics > currentStateHeuristics) {
        newState = new State(currentStackList, newStateHeuristics);
    } else {
        currentStackList.remove(newStack);
    }
    return newState;
}
private State pushElementToExistingStacks(
  Stack currentStack,
  List<Stack<String>> currentStackList,
  String block,
  int currentStateHeuristics,
  Stack<String> goalStateStack) {

    return currentStackList.stream()
      .filter(stack -> stack != currentStack)
      .map(stack -> {
          return pushElementToStack(
            stack, block, currentStackList,
            currentStateHeuristics, goalStateStack);
        })
      .filter(Objects::nonNull)
      .findFirst()
      .orElse(null);
}

private State pushElementToStack(
  Stack stack,
  String block,
  List<Stack<String>> currentStackList,
  int currentStateHeuristics,
  Stack<String> goalStateStack) {

    stack.push(block);
    int newStateHeuristics 
      = getHeuristicsValue(currentStackList, goalStateStack);
    if (newStateHeuristics > currentStateHeuristics) {
        return new State(currentStackList, newStateHeuristics);
    }
    stack.pop();
    return null;
}
```

现在我们有了辅助方法，让我们编写一个方法来实现爬山技术。

在这里，我们不断计算比其前任更接近目标的新状态。我们不断将它们添加到我们的路径中，直到我们达到目标。

如果我们没有找到任何新状态，算法将终止并显示一条错误消息：

```java
public List<State> getRouteWithHillClimbing(
  Stack<String> initStateStack, Stack<String> goalStateStack) throws Exception {
    // instantiate initState with initStateStack
    // ...
    List<State> resultPath = new ArrayList<>();
    resultPath.add(new State(initState));

    State currentState = initState;
    boolean noStateFound = false;
    
    while (
      !currentState.getState().get(0).equals(goalStateStack)
      || noStateFound) {
        noStateFound = true;
        State nextState = findNextState(currentState, goalStateStack);
        if (nextState != null) {
            noStateFound = false;
            currentState = nextState;
            resultPath.add(new State(nextState));
        }
    }
    return resultPath;
}
```

除此之外，我们还需要findNextState方法，该方法对当前状态应用所有可能的操作以获得下一个状态：

```java
public State findNextState(State currentState, Stack<String> goalStateStack) {
    List<Stack<String>> listOfStacks = currentState.getState();
    int currentStateHeuristics = currentState.getHeuristics();

     return listOfStacks.stream()
      .map(stack -> {
          return applyOperationsOnState(
            listOfStacks, stack, currentStateHeuristics, goalStateStack);
      })
      .filter(Objects::nonNull)
      .findFirst()
      .orElse(null);
}

public State applyOperationsOnState(
  List<Stack<String>> listOfStacks,
  Stack<String> stack,
  int currentStateHeuristics,
  Stack<String> goalStateStack) {

    State tempState;
    List<Stack<String>> tempStackList 
      = new ArrayList<>(listOfStacks);
    String block = stack.pop();
    if (stack.size() == 0)
      tempStackList.remove(stack);
    tempState = pushElementToNewStack(
      tempStackList, block, currentStateHeuristics, goalStateStack);
    if (tempState == null) {
        tempState = pushElementToExistingStacks(
          stack, tempStackList, block,
          currentStateHeuristics, goalStateStack);
        stack.push(block);
    }
    return tempState;
}
```

## 6. 最速爬山算法

Steepest-Ascent Hill-Climbing 算法(梯度搜索)是 Hill Climbing 算法的变体。我们可以在我们的简单算法中稍作修改来实现它。在这个算法中，我们考虑了当前状态的所有可能状态，然后选择最好的一个作为后继者，这与简单的爬山技术不同。

换句话说，在爬山技术的情况下，我们选择比当前状态更接近目标的任何状态作为后继，而在最速爬山算法中，我们在所有可能的后继中选择最佳后继，然后更新当前状态。

## 七、缺点

爬山是一种目光短浅的技术，因为它只评估眼前的可能性。所以它可能会在少数情况下无法从中选择任何进一步的状态。让我们看看这些状态和它们的一些解决方案：

1.  Local maximum：比所有邻居都好的状态，但是存在一个比当前状态差的更好的状态；如果在解决方案的视线范围内出现局部最大值，则称为“山麓”
2.  Plateau：在这个状态下，所有相邻的状态都有相同的启发值，所以通过局部比较来选择下一个状态是不明确的
3.  岭：是高于周围状态，但不能一举到达的区域；例如，我们有四个可能的探索方向(N、E、W、S)，并且在 NE 方向存在一个区域

克服这些情况的解决方案很少：

1.  我们可以回溯到之前的状态之一并探索其他方向
2.  我们可以跳过几个状态并跳向新的方向
3.  我们可以探索几个方向来找出正确的路径

## 八. 总结

尽管爬山技术比穷举搜索好得多，但它在大型问题空间中仍然不是最优的。

我们总是可以将全局信息编码成启发式函数以做出更明智的决策，但计算复杂度将比以前高得多。

当与其他技术结合使用时，爬山算法会非常有益。