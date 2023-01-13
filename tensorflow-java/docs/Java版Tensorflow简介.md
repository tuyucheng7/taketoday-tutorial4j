## 1. 概述

[TensorFlow](https://www.tensorflow.org/)是一个用于数据流编程的开源库。这最初由谷歌开发，可用于多种平台。尽管 TensorFlow 可以在单核上工作，但它也可以轻松地从可用的多个 CPU、GPU 或 TPU中获益。

在本教程中，我们将介绍 TensorFlow 的基础知识以及如何在Java中使用它。请注意，TensorFlowJavaAPI 是一个实验性 API，因此不在任何稳定性保证范围内。我们将在本教程后面介绍使用 TensorFlowJavaAPI 的可能用例。

## 2. 基础知识

TensorFlow 计算基本上围绕两个基本概念：Graph 和 Session。让我们快速浏览它们以获得完成本教程其余部分所需的背景知识。

### 2.1. 张量流图

首先，让我们了解 TensorFlow 程序的基本构建块。计算在 TensorFlow 中表示为图表。图通常是操作和数据的有向无环图，例如：

[
![TensorFlow-Graph-1-1](https://www.baeldung.com/wp-content/uploads/2019/03/TensorFlow-Graph-1-1.jpg)](https://www.baeldung.com/wp-content/uploads/2019/03/TensorFlow-Graph-1-1.jpg)

上图表示以下方程的计算图：

```plaintext
f(x, y) = z = ax + by
```

TensorFlow 计算图由两个元素组成：

1.  张量：这些是 TensorFlow 中的核心数据单元。它们表示为计算图中的边，描述了数据在图中的流动。张量可以具有任意维数的形状。张量的维数通常称为它的秩。所以标量是 0 阶张量，向量是 1 阶张量，矩阵是 2 阶张量，依此类推。
2.  操作：这些是计算图中的节点。它们指的是在输入运算的张量上可能发生的各种计算。它们通常也会产生张量，这些张量源自计算图中的操作。

### 2.2. TensorFlow 会话

现在，TensorFlow 图只是计算的示意图，实际上不包含任何值。这样的图必须在所谓的 TensorFlow 会话中运行，以便计算图中的张量。该会话可以将一堆张量作为输入参数从图形中进行评估。然后它在图中向后运行并运行评估这些张量所需的所有节点。

有了这些知识，我们现在就可以将其应用到JavaAPI 中了！

## 3. 行家设置

我们将建立一个快速的 Maven 项目，以在Java中创建和运行 TensorFlow 图形。我们只需要[tensorflow依赖](https://search.maven.org/search?q=g:org.tensorflow AND a:tensorflow AND v:1.12.0)：

```xml
<dependency>
    <groupId>org.tensorflow</groupId>
    <artifactId>tensorflow</artifactId>
    <version>1.12.0</version>
</dependency>
```

## 4. 创建图表

现在让我们尝试使用 TensorFlowJavaAPI 构建我们在上一节中讨论的图形。更准确地说，对于本教程，我们将使用 TensorFlowJavaAPI 来求解由以下等式表示的函数：

```powershell
z = 3x + 2y
```

第一步是声明和初始化一个图：

```java
Graph graph = new Graph()
```

现在，我们必须定义所有需要的操作。请记住，TensorFlow 中的操作会消耗并产生零个或多个张量。此外，图中的每个节点都是一个操作，包括常量和占位符。这似乎有悖常理，但请稍等片刻！

Graph类有一个名为opBuilder()的通用函数，用于在 TensorFlow 上构建任何类型的操作。

### 4.1. 定义常量

首先，让我们在上图中定义常量操作。请注意，常量运算需要一个张量作为其值：

```java
Operation a = graph.opBuilder("Const", "a")
  .setAttr("dtype", DataType.fromClass(Double.class))
  .setAttr("value", Tensor.<Double>create(3.0, Double.class))
  .build();		
Operation b = graph.opBuilder("Const", "b")
  .setAttr("dtype", DataType.fromClass(Double.class))
  .setAttr("value", Tensor.<Double>create(2.0, Double.class))
  .build();
```

在这里，我们定义了一个常量类型的操作，以Double值 2.0 和 3.0馈入Tensor 。开始时可能看起来有点难以抗拒，但这就是目前JavaAPI 中的情况。这些结构在 Python 等语言中要简洁得多。

### 4.2. 定义占位符

虽然我们需要为常量提供值，但占位符在定义时不需要值。当图形在会话中运行时，需要提供占位符的值。我们将在本教程的后面部分进行介绍。

现在，让我们看看如何定义占位符：

```java
Operation x = graph.opBuilder("Placeholder", "x")
  .setAttr("dtype", DataType.fromClass(Double.class))
  .build();			
Operation y = graph.opBuilder("Placeholder", "y")
  .setAttr("dtype", DataType.fromClass(Double.class))
  .build();
```

请注意，我们不必为占位符提供任何值。这些值将在运行时作为张量提供。

### 4.3. 定义函数

最后，我们需要定义方程的数学运算，即乘法和加法以获得结果。

这些也不过是 TensorFlow 中的Operation s，而Graph.opBuilder()再次派上用场：

```java
Operation ax = graph.opBuilder("Mul", "ax")
  .addInput(a.output(0))
  .addInput(x.output(0))
  .build();			
Operation by = graph.opBuilder("Mul", "by")
  .addInput(b.output(0))
  .addInput(y.output(0))
  .build();
Operation z = graph.opBuilder("Add", "z")
  .addInput(ax.output(0))
  .addInput(by.output(0))
  .build();
```

在这里，我们定义了Operation，两个用于乘以我们的输入，最后一个用于总结中间结果。请注意，这里的操作接收的张量只是我们之前操作的输出。

请注意，我们使用索引“0”从操作中获取输出张量。正如我们之前讨论的，一个操作可以产生一个或多个张量，因此在为其检索句柄时，我们需要提及索引。因为我们知道我们的操作只返回一个Tensor，所以 '0' 工作得很好！

## 5. 图形可视化

随着图表规模的增长，很难在图表上保持一个标签。这使得以某种方式可视化它变得很重要。我们总是可以像之前创建的小图一样创建手绘图，但对于较大的图来说并不实用。TensorFlow 提供了一个名为 TensorBoard 的实用程序来促进这一点。

遗憾的是，Java API 无法生成 TensorBoard 使用的事件文件。但是使用 Python 中的 API，我们可以生成一个事件文件，例如：

```python
writer = tf.summary.FileWriter('.')
......
writer.add_graph(tf.get_default_graph())
writer.flush()
```

如果这在Java上下文中没有意义，请不要打扰，这里添加它只是为了完整性，而不是继续本教程的其余部分所必需的。

我们现在可以在 TensorBoard 中加载和可视化事件文件，例如：

```powershell
tensorboard --logdir .
```

[![我有](https://www.baeldung.com/wp-content/uploads/2019/03/Screenshot-2019-03-25-at-16.55.39-1024x666.png)](https://www.baeldung.com/wp-content/uploads/2019/03/Screenshot-2019-03-25-at-16.55.39.png)

TensorBoard 作为 TensorFlow 安装的一部分提供。

请注意这与之前手动绘制的图形之间的相似性！

## 6. 使用会话

我们现在已经在 TensorFlowJavaAPI 中为我们的简单方程创建了一个计算图。但是我们如何运行它呢？在解决这个问题之前，让我们看看此时我们刚刚创建的Graph的状态是什么。如果我们尝试打印最终操作“z”的输出：

```java
System.out.println(z.output(0));
```

这将导致类似的结果：

```java
<Add 'z:0' shape=<unknown> dtype=DOUBLE>
```

这不是我们所期望的！但如果我们回想一下我们之前讨论的内容，这实际上是有道理的。我们刚刚定义的Graph还没有运行，所以其中的张量实际上没有任何实际值。上面的输出只是说这将是一个Double类型的张量。

现在让我们定义一个Session来运行我们的Graph：

```java
Session sess = new Session(graph)
```

最后，我们现在准备好运行我们的 Graph 并获得我们一直期待的输出：

```java
Tensor<Double> tensor = sess.runner().fetch("z")
  .feed("x", Tensor.<Double>create(3.0, Double.class))
  .feed("y", Tensor.<Double>create(6.0, Double.class))
  .run().get(0).expect(Double.class);
System.out.println(tensor.doubleValue());
```

那么我们在这里做什么？它应该是相当直观的：

-   从会话中获取一个跑步者
-   通过名称“z”定义要获取的操作
-   为我们的占位符“x”和“y”输入张量
-   在会话中运行图表

现在我们看到标量输出：

```powershell
21.0
```

这就是我们所期望的，不是吗！

## 7.JavaAPI 的用例

在这一点上，TensorFlow 对于执行基本操作来说可能听起来有点矫枉过正。但是，当然，TensorFlow 旨在运行比这大得多的图。

此外， 它在现实世界模型中处理的张量在大小和秩上要大得多。这些是 TensorFlow 真正发挥作用的实际机器学习模型。

不难看出，随着图形大小的增加，使用 TensorFlow 中的核心 API 会变得非常麻烦。为此， TensorFlow 提供了像[Keras](https://www.tensorflow.org/guide/keras)这样的高级 API来处理复杂的模型。不幸的是，目前还没有对Java上的 Keras 的官方支持。

但是，我们可以使用 Python直接在 TensorFlow 中或使用像 Keras 这样的高级 API来定义和训练复杂模型。随后，我们可以导出经过训练的模型并使用TensorFlowJavaAPI 在Java中使用它。

现在，我们为什么要这样做呢？这对于我们希望在运行于Java的现有客户端中使用支持机器学习的功能的情况特别有用。例如，在 Android 设备上为用户图像推荐标题。尽管如此，在某些情况下，我们对机器学习模型的输出感兴趣，但不一定要用Java创建和训练该模型。

这是 TensorFlowJavaAPI 大量使用的地方。我们将在下一节中介绍如何实现这一点。

## 8. 使用保存的模型

我们现在将了解如何将 TensorFlow 中的模型保存到文件系统，并可能以完全不同的语言和平台将其加载回来。[TensorFlow 提供 API 以一种称为Protocol Buffer](https://developers.google.com/protocol-buffers/)的语言和平台中立结构生成模型文件。

### 8.1. 将模型保存到文件系统

我们将从定义我们之前在 Python 中创建的相同图形并将其保存到文件系统开始。

让我们看看我们可以在 Python 中做到这一点：

```python
import tensorflow as tf
graph = tf.Graph()
builder = tf.saved_model.builder.SavedModelBuilder('./model')
with graph.as_default():
  a = tf.constant(2, name='a')
  b = tf.constant(3, name='b')
  x = tf.placeholder(tf.int32, name='x')
  y = tf.placeholder(tf.int32, name='y')
  z = tf.math.add(ax, by, name='z')
  sess = tf.Session()
  sess.run(z, feed_dict = {x: 2, y: 3})
  builder.add_meta_graph_and_variables(sess, [tf.saved_model.tag_constants.SERVING])
  builder.save()
```

作为Java 教程的重点，除了它生成一个名为“saved_model.pb”的文件外，我们不要过多关注Python 中这段代码的细节。与Java相比，请注意定义类似图形的简洁性！

### 8.2. 从文件系统加载模型

我们现在将“saved_model.pb”加载到Java中。Java TensorFlow API 有SavedModelBundle来处理保存的模型：

```java
SavedModelBundle model = SavedModelBundle.load("./model", "serve");	
Tensor<Integer> tensor = model.session().runner().fetch("z")
  .feed("x", Tensor.<Integer>create(3, Integer.class))
  .feed("y", Tensor.<Integer>create(3, Integer.class))
  .run().get(0).expect(Integer.class);	
System.out.println(tensor.intValue());
```

现在应该很直观地理解上面的代码在做什么。它只是从协议缓冲区加载模型图并使其中的会话可用。从那里开始，我们几乎可以用这个图做任何事情，就像我们对本地定义的图所做的那样。

## 9.总结

总而言之，在本教程中，我们了解了与 TensorFlow 计算图相关的基本概念。我们看到了如何使用 TensorFlowJavaAPI 来创建和运行这样的图。然后，我们讨论了JavaAPI 与 TensorFlow 相关的用例。

在此过程中，我们还了解了如何使用 TensorBoard 可视化图形，以及如何使用 Protocol Buffer 保存和重新加载模型。