## 1. 简介

在本文中，我们将使用[deeplearning4j](https://deeplearning4j.konduit.ai/) (dl4j) 库创建一个简单的神经网络——一种现代且强大的机器学习工具。

在我们开始之前，并不是说本指南不需要对线性代数、统计学、机器学习理论和许多其他主题的深入了解，这些知识对于一个有基础的 ML 工程师来说是必需的。

## 2.什么是深度学习？

神经网络是由相互连接的节点层组成的计算模型。

节点是类似神经元的数字数据处理器。他们从输入中获取数据，对这些数据应用一些权重和函数，然后将结果发送到输出。这样的网络可以用一些源数据的例子来训练。

训练本质上是在节点中保存一些数字状态(权重)，这些状态稍后会影响计算。训练示例可能包含具有特征的数据项和这些项的某些已知类别(例如，“这组 16×16 像素包含一个手写字母“a”)。

训练完成后，神经网络可以 从新数据中获取信息，即使它之前没有见过这些特定的数据项。一个经过良好建模和训练的网络可以识别图像、手写字母、语音、处理统计数据以产生商业智能结果等等。

近年来，随着高性能和并行计算的进步，深度神经网络成为可能。这种网络不同于简单的神经网络，因为 它们由多个中间(或[隐藏](https://www.baeldung.com/cs/neural-networks-hidden-layers-criteria))层组成。这种结构允许网络以更复杂的方式(以递归、循环、卷积方式等)处理数据，并从中提取更多信息。

## 3. 设置项目

要使用该库，我们至少需要Java7。此外，由于某些本机组件，它仅适用于 64 位 JVM 版本。

在开始使用指南之前，让我们检查一下是否满足要求：

```bash
$ java -version
java version "1.8.0_131"
Java(TM) SE Runtime Environment (build 1.8.0_131-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.131-b11, mixed mode)
```

首先，让我们将所需的库添加到我们的 Maven pom.xml文件中。我们将把库的版本提取到一个属性条目中(对于最新版本的库，请查看[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"org.deeplearning4j")存储库)：

```xml
<properties>
    <dl4j.version>0.9.1</dl4j.version>
</properties>

<dependencies>

    <dependency>
        <groupId>org.nd4j</groupId>
        <artifactId>nd4j-native-platform</artifactId>
        <version>${dl4j.version}</version>
    </dependency>

    <dependency>
        <groupId>org.deeplearning4j</groupId>
        <artifactId>deeplearning4j-core</artifactId>
        <version>${dl4j.version}</version>
    </dependency>
</dependencies>
```

请注意，nd4j-native-platform依赖项是几个可用的实现之一。

它依赖于可用于许多不同平台(macOS、Windows、Linux、Android 等)的本地库。如果我们想在支持 CUDA 编程模型的显卡上执行计算，我们也可以将后端切换到nd4j-cuda-8.0-platform 。

## 4.准备数据

### 4.1. 准备数据集文件

我们将编写机器学习的“Hello World”——[鸢尾花数据集](https://en.wikipedia.org/wiki/Iris_flower_data_set)的分类。这是一组从不同物种的花( Iris setosa、Iris versicolor和Iris virginica)中收集的数据。

这些物种的花瓣和萼片的长度和宽度不同。很难编写一个精确的算法来对输入数据项进行分类(即，确定特定花属于哪个物种)。但是训练有素的神经网络可以快速且几乎没有错误地对其进行分类。

我们将使用此数据的 CSV 版本，其中第 0..3 列包含物种的不同特征，第 4 列包含记录的类别或物种，编码值为 0、1 或 2：

```bash
5.1,3.5,1.4,0.2,0
4.9,3.0,1.4,0.2,0
4.7,3.2,1.3,0.2,0
…
7.0,3.2,4.7,1.4,1
6.4,3.2,4.5,1.5,1
6.9,3.1,4.9,1.5,1
…
```

### 4.2. 矢量化和读取数据

我们用数字对类别进行编码，因为神经网络使用数字。将现实世界的数据项转换为一系列数字(向量)称为向量化——deeplearning4j 使用[datavec](https://github.com/deeplearning4j/deeplearning4j/tree/master/datavec)库来执行此操作。

首先，让我们使用这个库来输入包含矢量化数据的文件。创建CSVRecordReader时，我们可以指定要跳过的行数(例如，如果文件有标题行)和分隔符(在我们的例子中是逗号)：

```java
try (RecordReader recordReader = new CSVRecordReader(0, ',')) {
    recordReader.initialize(new FileSplit(
      new ClassPathResource("iris.txt").getFile()));

    // …
}
```

要遍历记录，我们可以使用DataSetIterator接口的多种实现中的任何一种。数据集可能非常庞大，分页或缓存值的能力可能会派上用场。

但是我们的小数据集只包含 150 条记录，所以让我们通过调用iterator.next()一次将所有数据读入内存。

我们还指定了类列的索引，在我们的例子中，它与特征计数 (4)和类总数(3) 相同。

另外请注意，我们需要打乱数据集以摆脱原始文件中的类排序。

我们指定一个常量随机种子 (42) 而不是默认的System.currentTimeMillis()调用，以便洗牌的结果始终相同。这使我们每次运行程序时都能获得稳定的结果：

```java
DataSetIterator iterator = new RecordReaderDataSetIterator(
  recordReader, 150, FEATURES_COUNT, CLASSES_COUNT);
DataSet allData = iterator.next();
allData.shuffle(42);
```

### 4.3. 归一化和分裂

在训练之前我们应该对数据做的另一件事是对其进行归一化。[规范化](https://www.baeldung.com/cs/normalize-table-features)是一个两阶段过程：

-   收集有关数据的一些统计数据(拟合)
-   以某种方式更改(转换)数据以使其统一

对于不同类型的数据，规范化可能不同。

例如，如果我们要处理各种尺寸的图像，我们应该先收集尺寸统计数据，然后将图像缩放到统一尺寸。

但是对于数字，归一化通常意味着将它们转换为所谓的正态分布。NormalizerStandardize类可以帮助我们：

```java
DataNormalization normalizer = new NormalizerStandardize();
normalizer.fit(allData);
normalizer.transform(allData);
```

现在数据已准备就绪，我们需要将数据集分成两部分。

第一部分将用于培训课程。我们将使用数据的第二部分(网络根本看不到)来测试经过训练的网络。

这将使我们能够验证分类是否正常工作。我们将使用 65% 的数据 (0.65) 进行训练，剩下的 35% 用于测试：

```java
SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(0.65);
DataSet trainingData = testAndTrain.getTrain();
DataSet testData = testAndTrain.getTest();
```

## 5.准备网络配置

### 5.1. 流利的配置生成器

现在我们可以使用花哨的流畅构建器来构建我们的网络配置：

```java
MultiLayerConfiguration configuration 
  = new NeuralNetConfiguration.Builder()
    .iterations(1000)
    .activation(Activation.TANH)
    .weightInit(WeightInit.XAVIER)
    .learningRate(0.1)
    .regularization(true).l2(0.0001)
    .list()
    .layer(0, new DenseLayer.Builder().nIn(FEATURES_COUNT).nOut(3).build())
    .layer(1, new DenseLayer.Builder().nIn(3).nOut(3).build())
    .layer(2, new OutputLayer.Builder(
      LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
        .activation(Activation.SOFTMAX)
        .nIn(3).nOut(CLASSES_COUNT).build())
    .backprop(true).pretrain(false)
    .build();
```

即使使用这种构建网络模型的简化、流畅的方式，也有很多东西需要消化，还有很多参数需要调整。让我们分解这个模型。

### 5.2. 设置网络参数

iterations()构建器方法指定优化迭代次数。

迭代优化意味着对训练集执行多次传递，直到网络收敛到一个好的结果。

通常，在真实和大型数据集上进行训练时，我们使用多个时期(数据通过网络的完整传递)和每个时期的一次迭代。但由于我们的初始数据集是最小的，我们将使用一个时期和多次迭代。

activation()是在节点内部运行以确定其输出的函数。

最简单的激活函数是线性的f(x) = x。但事实证明，只有非线性函数才能让网络通过使用少数几个节点来解决复杂的任务。

我们可以在org.nd4j.linalg.activations.Activation枚举中查找许多可用的不同激活函数。如果需要，我们还可以编写激活函数。但我们将使用提供的双曲正切 (tanh) 函数。

weightInit ()方法指定了为网络设置初始权重的多种方法之一。正确的初始权重会对训练结果产生深远的影响。在不深入数学的情况下，让我们将其设置为高斯分布形式 ( WeightInit.XAVIER )，因为这通常是一个不错的开始选择。

可以在org.deeplearning4j.nn.weights.WeightInit枚举中查找所有其他权重初始化方法。

学习率是影响网络学习能力的关键参数。

在更复杂的情况下，我们可能会花费大量时间来调整此参数。但是对于我们的简单任务，我们将使用非常重要的值 0.1 并使用learningRate()构建器方法对其进行设置。

训练神经网络的问题之一是当网络“记住”训练数据时出现过度拟合的情况。

当网络为训练数据设置过高的权重并对任何其他数据产生不良结果时，就会发生这种情况。

为了解决这个问题，我们将使用.regularization(true).l2(0.0001)行设置 l2 正则化。正则化会因权重过大而“惩罚”网络并防止过度拟合。

### 5.3. 构建网络层

接下来，我们创建一个密集(也称为完全连接)层网络。

第一层应包含与训练数据 (4) 中的列相同数量的节点。

第二个密集层将包含三个节点。这是我们可以改变的值，但前一层的输出数量必须相同。

最终输出层应包含与类数 (3) 相匹配的节点数。网络结构如图所示：

[![无标题图](https://www.baeldung.com/wp-content/uploads/2017/10/Untitled-Diagram-300x212.png)](https://www.baeldung.com/wp-content/uploads/2017/10/Untitled-Diagram.png)

训练成功后，我们将拥有一个通过其输入接收四个值并向其三个输出之一发送信号的网络。这是一个简单的分类器。

最后，为了完成网络构建，我们设置反向传播(最有效的训练方法之一)并使用.backprop(true).pretrain(false)行禁用预训练。

## 6. 创建和训练网络

现在让我们根据配置创建一个神经网络，初始化并运行它：

```java
MultiLayerNetwork model = new MultiLayerNetwork(configuration);
model.init();
model.fit(trainingData);
```

现在我们可以使用其余的数据集来测试经过训练的模型，并使用三个类别的评估指标来验证结果：

```java
INDArray output = model.output(testData.getFeatureMatrix());
Evaluation eval = new Evaluation(3);
eval.eval(testData.getLabels(), output);
```

如果我们现在打印出eval.stats()，我们将看到我们的网络非常擅长对鸢尾花进行分类，尽管它确实有 3 次将类别 1 误认为是类别 2。

```bash
Examples labeled as 0 classified by model as 0: 19 times
Examples labeled as 1 classified by model as 1: 16 times
Examples labeled as 1 classified by model as 2: 3 times
Examples labeled as 2 classified by model as 2: 15 times

==========================Scores========================================
# of classes: 3
Accuracy: 0.9434
Precision: 0.9444
Recall: 0.9474
F1 Score: 0.9411
Precision, recall & F1: macro-averaged (equally weighted avg. of 3 classes)
========================================================================
```

流畅的配置构建器允许我们快速添加或修改网络层，或调整一些其他参数以查看我们的模型是否可以改进。

## 七. 总结

在本文中，我们使用 deeplearning4j 库构建了一个简单但功能强大的神经网络。