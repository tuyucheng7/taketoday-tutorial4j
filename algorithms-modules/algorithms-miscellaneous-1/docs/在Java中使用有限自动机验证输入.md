## 1. 概述

如果你学习过 CS，那么你无疑已经学习过有关编译器或类似内容的课程；在这些课程中，教授了有限自动机(也称为有限状态机)的概念。这是一种形式化语言语法规则的方法。

[你可以在此处](https://www.cs.rochester.edu/u/nelson/courses/csc_173/fa/fa.html)和[此处](https://en.wikipedia.org/wiki/Deterministic_finite_automaton)阅读有关该主题的更多信息。

那么这个被遗忘的概念对我们这些不需要为构建新编译器而烦恼的高级程序员有什么帮助呢？

好吧，事实证明，这个概念可以简化很多业务场景，并为我们提供推理复杂逻辑的工具。

举个简单的例子，我们也可以在没有外部第三方库的情况下验证输入。

## 2.算法

简而言之，这样的机器声明状态以及从一种状态到另一种状态的方法。如果通过它放置流，则可以使用以下算法(伪代码)验证其格式：

```c
for (char c in input) {
    if (automaton.accepts(c)) {
        automaton.switchState(c);
        input.pop(c);
    } else {
        break;
    }
}
if (automaton.canStop() && input.isEmpty()) {
    print("Valid");
} else {
    print("Invalid");
}
```

我们说自动机“接受”给定的字符如果有任何箭头从当前状态出发，它上面有字符。切换状态意味着跟随指针并将当前状态替换为箭头指向的状态。

最后，当循环结束时，我们检查自动机是否“可以停止”(当前状态为双循环)以及该输入是否已用完。

## 3. 一个例子

让我们为 JSON 对象编写一个简单的验证器，以查看算法的运行情况。这是接受对象的自动机：

[![JSON DFA 2](https://www.baeldung.com/wp-content/uploads/2017/03/json_dfa-2.png)](https://www.baeldung.com/wp-content/uploads/2017/03/json_dfa-2.png)

请注意，值可以是以下之一：字符串、整数、布尔值、空值或其他 JSON 对象。为了简洁起见，在我们的示例中，我们将只考虑字符串。

### 3.1. 编码

实现有限[状态机](https://www.baeldung.com/cs/state-machines)非常简单。我们有以下内容：

```java
public interface FiniteStateMachine {
    FiniteStateMachine switchState(CharSequence c);
    boolean canStop();
}
 
interface State {
    State with(Transition tr);
    State transit(CharSequence c);
    boolean isFinal();
}
 
interface Transition {
    boolean isPossible(CharSequence c);
    State state();
}

```

它们之间的关系是：

-   状态机有一个当前状态并告诉我们它是否可以停止(如果状态是最终状态)
-   一个状态有一个可以遵循的转换列表(外向箭头)
-   Transition告诉我们字符是否被接受并给我们下一个状态

```java
publi class RtFiniteStateMachine implements FiniteStateMachine {

    private State current;

    public RtFiniteStateMachine(State initial) {
        this.current = initial;
    }

    public FiniteStateMachine switchState(CharSequence c) {
        return new RtFiniteStateMachine(this.current.transit(c));
    }

    public boolean canStop() {
        return this.current.isFinal();
    }
}
```

请注意，FiniteStateMachine实现是不可变的。这主要是为了可以多次使用它的单个实例。

接下来，我们有实现RtState。with(Transition)方法返回添加transition后的实例，为了流畅。状态还告诉我们它是否是最终的(双圈)。

```java
public class RtState implements State {

    private List<Transition> transitions;
    private boolean isFinal;

    public RtState() {
        this(false);
    }
    
    public RtState(boolean isFinal) {
        this.transitions = new ArrayList<>();
        this.isFinal = isFinal;
    }

    public State transit(CharSequence c) {
        return transitions
          .stream()
          .filter(t -> t.isPossible(c))
          .map(Transition::state)
          .findAny()
          .orElseThrow(() -> new IllegalArgumentException("Input not accepted: " + c));
    }

    public boolean isFinal() {
        return this.isFinal;
    }

    @Override
    public State with(Transition tr) {
        this.transitions.add(tr);
        return this;
    }
}
```

最后，RtTransition检查转换规则并可以给出下一个状态：

```java
public class RtTransition implements Transition {

    private String rule;
    private State next;
    public State state() {
        return this.next;
    }

    public boolean isPossible(CharSequence c) {
        return this.rule.equalsIgnoreCase(String.valueOf(c));
    }

    // standard constructors
}
```

使用此实现，你应该能够构建任何状态机。开头描述的算法非常简单：

```java
String json = "{"key":"value"}";
FiniteStateMachine machine = this.buildJsonStateMachine();
for (int i = 0; i < json.length(); i++) {
    machine = machine.switchState(String.valueOf(json.charAt(i)));
}
 
assertTrue(machine.canStop());
```

检查测试类RtFiniteStateMachineTest以查看buildJsonStateMachine()方法。请注意，它添加了比上图更多的状态，以正确捕获字符串周围的引号。

## 4. 总结

有限自动机是可用于验证结构化数据的绝佳工具。

然而，它们并不广为人知，因为当涉及到复杂输入时它们会变得复杂(因为转换只能用于一个字符)。然而，在检查一组简单的规则时，它们非常有用。

最后，如果你想使用有限状态机做一些更复杂的工作，[StatefulJ](https://github.com/statefulj/statefulj)和[squirrel](https://github.com/hekailiang/squirrel)是两个值得研究的库。