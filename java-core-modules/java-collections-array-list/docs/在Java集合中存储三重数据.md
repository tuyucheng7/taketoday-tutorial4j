## 一、概述

在本教程中，我们将首先了解什么是三元组，然后讨论如何在 Java [*ArrayList*](https://www.baeldung.com/java-arraylist)中存储三元组元素。

## 2. 什么是三元组？

我们可能听说过[*Pair*](https://www.baeldung.com/java-pairs)类型，它总是包含两个值，例如键值关联。三元组与一对非常相似。唯一的区别是**三元组始终具有三个值**而不是两个。例如，一个 3D 坐标可以被认为是一个三重结构：*x=-100L, y=0L, z=200L*。

在 3D 坐标示例中，三元组中的三个值具有相同的类型：*Long*。但是，**三元组中的三种值类型不一定相同。**例如，*name=”Lionel Messi”, birthday=24 June 1987 (Date), number=10*是足球运动员的另一种三元组结构。在此示例中，三元组中的三个值具有不同的类型：*String*、*Date*和*Integer*。

接下来，我们将看到一个更详细的三元组示例，并讨论将三元组对象存储在*ArrayList*中的正确方法。

## 3. 示例：算术题生成器

假设我们想为学生构建一个算术问题生成器。例如，“ *100 + 200 = ?* ”是一个问题。它由第一个数字、第二个数字和一个运算符组成。因此，我们有三个值。我们将把这三个部分存储为三元组。

此外，我们已将可接受的运算符定义为[*Enum*](https://www.baeldung.com/a-guide-to-java-enums)：

```java
enum OP {
    PLUS("+"), MINUS("-"), MULTIPLY("x");
    final String opSign;
                                         
    OP(String x) {
        this.opSign = x;
    }
}复制
```

如我们所见，我们只接受三个运算符。

问题生成逻辑非常简单。但首先，让我们创建一个方法来生成来自三个部分的问题：

```java
String createQuestion(Long num1, OP operator, Long num2) {
    long result;
    switch (operator) {
        case PLUS:
            result = num1 + num2;
            break;
        case MINUS:
            result = num1 - num2;
            break;
        case MULTIPLY:
            result = num1 * num2;
            break;
        default:
            throw new IllegalArgumentException("Unknown operator");
    }
    return String.format("%d %s %d = ? ( answer: %d )", num1, operator.opSign, num2, result);
}复制
```

如果我们在列表的三重结构中有值，我们可以将这三个值传递给上面的方法并创建问题。为简单起见，我们将使用单元测试断言来验证是否可以创建预期的问题。假设我们希望通过三元组生成三个问题：

```java
List<String> EXPECTED_QUESTIONS = Arrays.asList(
    "100 - 42 = ? ( answer: 58 )",
    "100 + 42 = ? ( answer: 142 )",
    "100 x 42 = ? ( answer: 4200 )");复制
```

现在，是时候考虑中心问题了：如何将三元组结构存储在列表中？

当然，如果我们只关注这个问题，我们可以创建一个包含三个参数的*QuestionInput类。*然而，**我们的目标是通常将三元组结构存储在一个列表中**，这意味着我们的解决方案应该解决“问题生成”问题并且也适用于“3D 坐标”和“足球运动员”示例。

通常，可能会想到两种想法来将三元组存储在列表中：

-   *List<List>* – 将三个值存储在列表或数组中（我们在教程中以*List*为例）并将列表嵌套在外部列表中：*List<List>*
-   *List<Triple<…>> –*创建一个[通用的](https://www.baeldung.com/java-generics) *Triple*类

那么接下来，让我们看看他们的行动。此外，我们将讨论它们的优缺点。

## 4. 三元组列表

我们知道**我们可以将任何类型的元素添加到原始列表中**。那么接下来，让我们看看如何将三元组存储为列表。

### 4.1. 将三元结构存储为三个元素的列表

对于每个三重结构，我们可以创建一个列表。然后将这三个值添加到列表中。那么接下来，让我们将数学问题三元组存储为列表：

```java
List myTriple1 = new ArrayList(3);
myTriple1.add(100L);
myTriple1.add(OP.MINUS);
myTriple1.add(42L);

List myTriple2 = new ArrayList(3);
myTriple2.add(100L);
myTriple2.add(OP.PLUS);
myTriple2.add(42L);

List myTriple3 = new ArrayList(3);
myTriple3.add(100L);
myTriple3.add(OP.MULTIPLY);
myTriple3.add(42L);

List<List> listOfTriples = new ArrayList<>(Arrays.asList(myTriple1, myTriple2, myTriple3));复制
```

如上面的代码所示，我们创建了三个原始*ArrayList*对象来携带三个三元组。最后，我们将三个原始列表添加到外部列表*listOfTriples*中。

### 4.2. 关于类型安全的一句话

列表的原始用法允许我们将不同类型的值放入列表中，例如*Long*和*OP*。因此，这种方法可以用于任何三重结构。

然而，另一方面，**当我们在 raw 中使用列表时，****我们失去了[类型安全](https://www.baeldung.com/cs/type-safety-programming)**。一个例子可以快速解释它：

```java
List oopsTriple = new ArrayList(3);
oopsTriple.add("Oops");
oopsTriple.add(911L);
oopsTriple.add("The type is wrong");

listOfTriples.add(oopsTriple);
assertEquals(4, listOfTriples.size());复制
```

如我们所见，我们创建了带有不同三重结构的*oopsTriple列表。*此外，当我们向其添加*oopsTriple时，* *listOftriples 毫无怨言地*接受了它。

所以现在，*listOfTriples*包含两种不同的三元组：*Long*、*OP*、*Long*和*String*、*Long*、*String*。因此，当我们使用*listOfTriples*列表中的三元组时，我们必须检查该三元组是否属于预期类型。

### 4.3. 在列表中使用三元组

现在我们了解了“作为列表的三元组”方法的优缺点，让我们看看如何使用*listOfTriples*中的三元组来生成算术题：

```java
List<String> questions = listOfTriples.stream()
    .filter(
        triple -> triple.size() == 3
          && triple.get(0) instanceof Long
          && triple.get(1) instanceof OP
          && triple.get(2) instanceof Long
    ).map(triple -> {
        Long left = (Long) triple.get(0);
        String op = (String) triple.get(1);
        Long right = (Long) triple.get(2);
        return createQuestion(left, op, right);
    }).collect(Collectors.toList());

assertEquals(EXPECTED_QUESTIONS, questions);复制
```

如上面的代码片段所示，**我们使用 Java [Stream API](https://www.baeldung.com/java-8-streams)的 \*map()\*将三元组列表转换为生成的问题列表**。但是，由于无法保证内部原始列表的类型，我们必须检查每个原始列表中的元素是否符合*Long*、*OP*和*Long*。因此，在我们调用 *map()*方法之前， 我们调用了*filter()*来跳过无法识别的三元组，例如*String*、*Long*和*String* 一个。

此外，由于列表的原始用法，没有合约可以保证原始列表中的三个元素的类型为：*Long*、*OP*和*Long*。因此，**我们必须在将列表元素传递给\*createQuestion()\*方法之前将它们显式转换为所需的类型。**

如果我们运行测试，它就会通过。所以这种方法解决了我们的问题。它的优势是显而易见的。**我们可以在列表中存储任何三元组结构而无需创建新类**。但是我们失去了类型安全。因此，**我们必须在使用原始值之前执行类型检查**。此外，我们必须将值转换为所需的类型。

想象一下，我们在应用程序中对许多不同的三重结构使用这种方法，然后我们必须在类型检查和转换上投入大量精力。**它使代码的可读性降低、难以维护且容易出错。因此，不推荐使用这种方法。**

## 5. 创建通用*三元*组

现在我们了解了“三元组作为列表”方法的优缺点，让我们尝试找到一种直接且类型安全的方法来将三元组存储在列表中。

### 5.1. 通用*三重*类

Java 泛型带来的好处之一是类型安全。接下来，让我们创建一个通用的*Triple*类：

```java
public class Triple<L, M, R> {

    private final L left;
    private final M middle;
    private final R right;

    public Triple(L left, M middle, R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public M getMiddle() {
        return middle;
    }

    public R getRight() {
        return right;
    }
}
复制
```

这堂课非常简单。在此示例中，**我们将\*Triple设置为\* [不可变](https://www.baeldung.com/java-immutable-object)**的。如果要求是可变的，我们可以去掉 [*final*](https://www.baeldung.com/java-final)关键字，加上相应的setter。

### 5.2. 初始化三元组并存储在列表中

接下来，让我们创建三个三元组对象并将它们添加到*listOfTriples*列表中：

```java
Triple<Long, OP, Long> triple1 = new Triple<>(100L, OP.MINUS, 42L);
Triple<Long, OP, Long> triple2 = new Triple<>(100L, OP.PLUS, 42L);
Triple<Long, OP, Long> triple3 = new Triple<>(100L, OP.MULTIPLY, 42L);

List<Triple<Long, OP, Long>> listOfTriples = new ArrayList<>(Arrays.asList(triple1, triple2, triple3));复制
```

如我们所见，由于我们的通用*Triple*类具有类型参数，**因此上面的代码中没有原始用法。此外，类型是安全的**。

接下来，让我们测试如果我们创建一个无效的 Triple 对象并尝试将其添加到列表中会发生什么：

```java
Triple<String, Long, String> tripleOops = new Triple<>("Oops", 911L, "The type is wrong");
listOfTriples.add(tripleOops);复制
```

如果我们添加上面的两行，代码将无法编译：

```bash
java: incompatible types: 
com...Triple<...String, ...Long, ...String> cannot be converted to com...Triple<...Long, ...OP, ...Long>复制
```

因此，**这种类型安全的方法可以保护我们免于落入类型错误的陷阱**。

### 5.3. 使用*三元*组

由于类型是安全的，我们可以直接使用这些值而无需执行任何类型检查和类型转换：

```java
List<String> questions = listOfTriples.stream()
  .map(triple -> createQuestion(triple.getLeft(), triple.getMiddle(), triple.getRight()))
  .collect(Collectors.toList());

assertEquals(EXPECTED_QUESTIONS, questions);复制
```

如果我们试一试，测试就会通过。与“三元组作为列表”方法相比，上面的代码更清晰，可读性也更高。

## 六，结论

在本文中，我们通过示例探讨了如何在列表中存储三元组。我们已经讨论了为什么我们应该创建一个通用的*Triple*类型而不是使用三元组作为列表。