## 1. 简介

逻辑回归是机器学习 (ML) 从业者工具箱中的重要工具。

在本教程中，我们将探讨逻辑回归背后的主要思想。

首先，让我们从 ML 范式和算法的简要概述开始。

## 2.概述

ML 使我们能够解决可以用人性化的术语表述的问题。然而，这一事实对我们软件开发人员来说可能是一个挑战。我们已经习惯于解决我们可以用计算机友好的术语表达的问题。例如，作为人类，我们可以轻松地检测照片上的物体或确定短语的情绪。我们如何为计算机制定这样的问题？

为了提出解决方案，在 ML 中有一个称为训练的特殊阶段。在此阶段，我们将输入数据提供给我们的算法，以便它尝试得出一组最佳参数(所谓的权重)。我们可以为算法提供的输入数据越多，我们期望从中得到的预测就越精确。

训练是迭代 ML 工作流程的一部分：

[![毫升1](https://www.baeldung.com/wp-content/uploads/2019/09/ml1.png)](https://www.baeldung.com/wp-content/uploads/2019/09/ml1.png)
我们从获取数据开始。通常，数据来自不同的来源。因此，我们必须使其具有相同的格式。我们还应该控制数据集公平地代表研究领域。如果模型从未在红苹果上进行过训练，则很难预测。

接下来，我们应该构建一个模型来使用数据并能够做出预测。在 ML 中，没有适用于所有情况的预定义模型。

在搜索正确的模型时，很容易发生我们构建模型、训练它、查看其预测并丢弃该模型的情况，因为我们对它所做的预测不满意。在这种情况下，我们应该退一步建立另一个模型并再次重复该过程。

## 3. 机器学习范式

在 ML 中，根据我们掌握的输入数据类型，我们可以挑出三种主要范式：

-   监督学习(图像分类、对象识别、情感分析)
-   无监督学习(异常检测)
-   强化学习(游戏策略)

我们要在本教程中描述的案例属于监督学习。

## 4.机器学习工具箱

在 ML 中，有一组工具可供我们在构建模型时应用。让我们提一下其中的一些：

-   线性回归
-   逻辑回归
-   神经网络
-   支持向量机
-   k-最近邻

在构建具有高预测性的模型时，我们可能会结合使用多种工具。事实上，对于本教程，我们的模型将使用逻辑回归和神经网络。

## 5. 机器学习库

尽管Java不是最流行的 ML 模型原型设计语言， 但它作为在包括 ML 在内的许多领域创建强大软件的可靠工具而享有盛誉。因此，我们可能会找到用Java编写的 ML 库。

在这方面，我们可能会提到事实上的标准库[Tensorflow](https://www.tensorflow.org/install/lang_java)，它也有Java版本。另一个值得一提的是名为[Deeplearning4j](https://www.baeldung.com/deeplearning4j)的深度学习库。这是一个非常强大的工具，我们也将在本教程中使用它。

## 6. 数字识别的逻辑回归

逻辑回归的主要思想是建立一个尽可能精确地预测输入数据标签的模型。

我们训练模型，直到所谓的损失函数或目标函数达到某个最小值。损失函数取决于实际模型预测和预期预测(输入数据的标签)。我们的目标是最小化实际模型预测与预期模型预测的差异。

如果我们对这个最小值不满意，我们应该建立另一个模型并再次进行训练。

为了看到逻辑回归的作用，我们用手写数字的识别来说明它。这个问题已经成为经典问题。Deeplearning4j 库有一系列实际[示例](https://github.com/deeplearning4j/dl4j-examples/tree/master/dl4j-examples/src/main/java/org/deeplearning4j/examples)，展示了如何使用其 API。本教程与代码相关的部分在很大程度上基于[MNIST 分类器](https://github.com/eclipse/deeplearning4j-examples/tree/master/dl4j-examples/src/main/java/org/deeplearning4j/examples/quickstart/modeling/convolution)。

### 6.1. 输入数据

作为输入数据，我们使用著名的[MNIST](https://en.wikipedia.org/wiki/MNIST_database)手写数字数据库。作为输入数据，我们有 28×28 像素的灰度图像。每个图像都有一个自然标签，即图像代表的数字：

[![毫升2](https://www.baeldung.com/wp-content/uploads/2019/09/ml2.png)](https://www.baeldung.com/wp-content/uploads/2019/09/ml2.png)

为了估计我们要构建的模型的效率，我们将输入数据分成训练集和测试集：

```java
DataSetIterator train = new RecordReaderDataSetIterator(...);
DataSetIterator test = new RecordReaderDataSetIterator(...);
```

一旦我们将输入图像标记并分成两组，“数据细化”阶段就结束了，我们可以进入“模型构建”阶段。

### 6.2. 建筑模型

正如我们所提到的，没有在所有情况下都适用的模型。尽管如此，经过多年对 ML 的研究，科学家们发现了在识别手写数字方面表现非常出色的模型。在这里，我们使用所谓的[LeNet-5](http://yann.lecun.com/exdb/lenet/)模型。

LeNet-5 是一个神经网络，由一系列层组成，可将 28×28 像素图像转换为十维向量：

[![毫升3](https://www.baeldung.com/wp-content/uploads/2019/09/ml3.png)](https://www.baeldung.com/wp-content/uploads/2019/09/ml3.png)
十维输出向量包含输入图像的标签为 0、1、2 等的概率。

例如，如果输出向量具有以下形式：

```java
{0.1, 0.0, 0.3, 0.2, 0.1, 0.1, 0.0, 0.1, 0.1, 0.0}
```

这意味着输入图像为零的概率为 0.1，为一为 0，为二为 0.3，等等。我们看到最大概率 (0.3) 对应于标签 3。

让我们深入了解模型构建的细节。我们省略了特定于Java的细节并专注于 ML 概念。

我们通过创建一个MultiLayerNetwork对象来设置模型：

```java
MultiLayerNetwork model = new MultiLayerNetwork(config);
```

在其构造函数中，我们应该传递一个MultiLayerConfiguration对象。这正是描述神经网络几何结构的对象。为了定义网络几何，我们应该定义每一层。

让我们展示我们如何使用第一个和第二个来做到这一点：

```java
ConvolutionLayer layer1 = new ConvolutionLayer
    .Builder(5, 5).nIn(channels)
    .stride(1, 1)
    .nOut(20)
    .activation(Activation.IDENTITY)
    .build();
SubsamplingLayer layer2 = new SubsamplingLayer
    .Builder(SubsamplingLayer.PoolingType.MAX)
    .kernelSize(2, 2)
    .stride(2, 2)
    .build();
```

我们看到层的定义包含大量对整个网络性能有显着影响的临时参数。这正是我们在所有人的景观中找到一个好的模型的能力变得至关重要的地方。

现在，我们准备构建MultiLayerConfiguration对象：

```java
MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
    // preparation steps
    .list()
    .layer(0, layer1)
    .layer(1, layer2)
    // other layers and final steps
    .build();
```

我们传递给MultiLayerNetwork构造函数。

### 6.3. 训练

我们构建的模型包含 431080 个参数或权重。我们不打算在这里给出这个数字的精确计算，但我们应该知道只有第一层有超过 24x24x20 = 11520 个权重。

训练阶段很简单：

```java
model.fit(train);

```

最初，431080 个参数有一些随机值，但经过训练后，它们获得了一些决定模型性能的值。我们可以评估模型的预测能力：

```java
Evaluation eval = model.evaluate(test);
logger.info(eval.stats());
```

LeNet-5 模型甚至在一次训练迭代(epoch)中就达到了几乎 99% 的相当高的准确率。如果我们想获得更高的准确性，我们应该使用普通的 for 循环进行更多迭代：

```java
for (int i = 0; i < epochs; i++) {
    model.fit(train);
    train.reset();
    test.reset();
}

```

### 6.4. 预言

现在，当我们训练模型并且我们对其对测试数据的预测感到满意时，我们可以在一些全新的输入上尝试该模型。为此，让我们创建一个新类MnistPrediction，我们将从文件系统中选择的文件加载图像：

```java
INDArray image = new NativeImageLoader(height, width, channels).asMatrix(file);
new ImagePreProcessingScaler(0, 1).transform(image);
```

可变图像包含我们正在缩小为 28×28 灰度的图片。我们可以将它提供给我们的模型：

```java
INDArray output = model.output(image);
```

变量输出将包含图像为零、一、二等的概率。

现在让我们玩一点，写一个数字 2，将这张图像数字化并将其提供给模型。我们可能会得到这样的东西：

[![毫升4](https://www.baeldung.com/wp-content/uploads/2019/09/ml4.png)](https://www.baeldung.com/wp-content/uploads/2019/09/ml4.png)
如我们所见，最大值为 0.99 的分量的索引为 2。这意味着模型已经正确识别了我们的手写数字。

## 七. 总结

在本教程中，我们描述了机器学习的一般概念。我们在应用于手写数字识别的逻辑回归示例中说明了这些概念。