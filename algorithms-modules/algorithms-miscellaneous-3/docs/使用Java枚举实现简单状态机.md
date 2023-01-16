## 1. 概述

在本教程中，我们将了解[状态机](https://www.baeldung.com/cs/state-machines)以及如何使用枚举在Java中实现它们。

我们还将解释此实现与为每个状态使用接口和具体类相比的优势。

## 2.Java 枚举

[Java Enum](https://www.baeldung.com/a-guide-to-java-enums)是一种特殊类型的类，它定义了一个常量列表。这允许类型安全的实现和更易读的代码。

例如，假设我们有一个 HR 软件系统，可以批准员工提交的请假申请。此请求由团队负责人审核，团队负责人将其上报给部门经理。部门经理是负责批准请求的人。

保存休假请求状态的最简单枚举是：

```java
public enum LeaveRequestState {
    Submitted,
    Escalated,
    Approved
}
```

我们可以引用这个枚举的常量：

```java
LeaveRequestState state = LeaveRequestState.Submitted;
```

枚举也可以包含方法。我们可以在枚举中写一个抽象方法，它会强制每个枚举实例都实现这个方法。这对于状态机的实现非常重要，我们将在下面看到。

由于Java枚举隐式地扩展了类java.lang.Enum，它们不能扩展另一个类。但是，它们可以实现一个接口，就像任何其他类一样。

下面是一个包含抽象方法的枚举示例：

```java
public enum LeaveRequestState {
    Submitted {
        @Override
        public String responsiblePerson() {
            return "Employee";
        }
    },
    Escalated {
        @Override
        public String responsiblePerson() {
            return "Team Leader";
        }
    },
    Approved {
        @Override
        public String responsiblePerson() {
            return "Department Manager";
        }
    };

    public abstract String responsiblePerson();
}
```

请注意在最后一个枚举常量末尾使用分号。当常量后面有一个或多个方法时，需要分号。

在本例中，我们使用 responsiblePerson()方法扩展了第一个示例。这告诉我们负责执行每个操作的人。因此，如果我们尝试检查升级状态的负责人，它会给我们“Team Leader”：

```java
LeaveRequestState state = LeaveRequestState.Escalated;
assertEquals("Team Leader", state.responsiblePerson());
```

同样，如果我们检查谁负责批准请求，它会给我们“部门经理”：

```java
LeaveRequestState state = LeaveRequestState.Approved;
assertEquals("Department Manager", state.responsiblePerson());
```

## 3. 状态机

状态机(也称为有限状态机或有限自动机)是一种用于构建抽象机的计算模型。这些机器在给定时间只能处于一种状态。每个状态都是系统的一种状态，它会改变为另一种状态。这些状态变化称为转换。

使用图表和符号在数学上可能会变得复杂，但对于我们程序员来说事情要容易得多。

[状态模式](https://www.baeldung.com/java-state-design-pattern)是著名的 GoF 二十三种设计模式之一。这种模式借用了数学中模型的概念。它允许对象根据其状态为同一对象封装不同的行为。我们可以对状态之间的转换进行编程，然后定义单独的状态。

为了更好地解释这个概念，我们将扩展请假请求示例以实现状态机。

## 4. 枚举作为状态机

我们将重点关注Java中状态机的枚举实现。[其他实现](https://www.baeldung.com/java-state-design-pattern)也是可能的，我们将在下一节中对它们进行比较。

使用枚举的状态机实现的要点是我们不必处理显式设置状态。相反，我们可以只提供如何从一种状态转换到另一种状态的逻辑。让我们开始吧：

```java
public enum LeaveRequestState {

    Submitted {
        @Override
        public LeaveRequestState nextState() {
            return Escalated;
        }

        @Override
        public String responsiblePerson() {
            return "Employee";
        }
    },
    Escalated {
        @Override
        public LeaveRequestState nextState() {
            return Approved;
        }

        @Override
        public String responsiblePerson() {
            return "Team Leader";
        }
    },
    Approved {
        @Override
        public LeaveRequestState nextState() {
            return this;
        }

        @Override
        public String responsiblePerson() {
            return "Department Manager";
        }
    };

    public abstract LeaveRequestState nextState(); 
    public abstract String responsiblePerson();
}
```

在此示例中，状态机转换是使用枚举的抽象方法实现的。更准确地说，在每个枚举常量上使用nextState()，我们指定到下一个状态的转换。如果需要，我们还可以实现previousState()方法。

下面是检查我们的实现的测试：

```java
LeaveRequestState state = LeaveRequestState.Submitted;

state = state.nextState();
assertEquals(LeaveRequestState.Escalated, state);

state = state.nextState();
assertEquals(LeaveRequestState.Approved, state);

state = state.nextState();
assertEquals(LeaveRequestState.Approved, state);
```

我们以Submitted初始状态开始请假请求。然后，我们使用上面实现的nextState()方法验证状态转换。

请注意，由于 Approved是最终状态，因此不会发生其他转换。

## 5. 使用Java枚举实现状态机的优势

[具有接口和实现类的状态机](https://www.baeldung.com/java-state-design-pattern)的实现可能需要开发和维护大量代码。

由于Java枚举在其最简单的形式中是常量列表，因此我们可以使用枚举来定义我们的状态。由于枚举也可以包含行为，我们可以使用方法来提供状态之间的转换实现。

将所有逻辑都放在一个简单的枚举中可以提供一个干净直接的解决方案。

## 六. 总结

在本文中，我们研究了状态机以及如何使用枚举在Java中实现它们。我们给出了一个例子并进行了测试。

最后，我们还讨论了使用枚举来实现状态机的优势。作为接口和实现解决方案的替代方案，枚举提供了一种更清晰、更易于理解的状态机实现。