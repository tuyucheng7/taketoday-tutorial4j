## 1. 概述

监督学习是机器学习的一个子集。它包括使用标记的经验数据训练机器学习模型。换句话说，从经验中收集的数据。

在本教程中，我们将学习在 Kotlin中应用[监督学习。](https://www.baeldung.com/machine-learning-intro#supervised)我们将看一下两种算法；一简单一复杂。在此过程中，我们还将讨论正确的数据集准备。

## 2.算法

监督学习模型建立在机器学习算法之上。存在多种算法。

让我们从了解一些一般概念开始，然后我们将继续介绍一些众所周知的算法。

### 2.1. 数据集准备

首先，我们需要一个数据集。完美的数据集是预先标记的，其特征是相关的并被转换为模型的输入。

不幸的是，完美的数据集并不存在。因此，我们需要准备数据。

在进行监督学习时，数据准备至关重要。让我们首先看一下我们受[Wine Quality Dataset](https://www.kaggle.com/rajyellow46/wine-quality)启发的数据集：

| 类型   | 酸度 | 二氧化硫 | 酸碱度 |
| ------ | ---- | -------- | ------ |
| 红色的 | .27  | 45       | 3      |
| 白色的 | .3   | 14       | 3.26   |
|        | .28  | 47       | 2.98   |
| 白色的 | .18  |          | 3.22   |
| 红色的 |      | 16       | 3.17   |

首先，让我们从处理数据集中缺失的单元格开始。存在多种技术来处理数据集中的缺失数据。

例如，在我们的例子中，我们将删除包含缺少的葡萄酒类型的行，因为该类型在这里很重要，因为它有助于解释其他特征。另一方面， 我们还将用特征现有值的平均值替换缺失的数字：

| 类型   | 酸度    | 二氧化硫 | 酸碱度 |
| ------ | ------- | -------- | ------ |
| 红色的 | .27     | 45       | 3      |
| 白色的 | .3      | 14       | 3.26   |
| 白色的 | .18     | 31   | 3.22   |
| 红色的 | .26 | 16       | 3.17   |

为了澄清，我们还确保根据列的小数精度将它们四舍五入。

其次，让我们继续将白色和红色类别转换为数值。我们认为这个特征是一个分类特征。至于缺失数据，多种技术适用于分类数据。

在我们的例子中，如前所述，我们将用数值替换值：

| 类型  | 酸度 | 二氧化硫 | 酸碱度 |
| ----- | ---- | -------- | ------ |
| 0 | .27  | 45       | 3      |
| 1 | .3   | 14       | 3.26   |
| 1 | .18  | 31       | 3.22   |
| 0 | .26  | 16       | 3.17   |

我们准备数据集的最后一步是特征缩放。澄清一下，特征缩放是在相同的值范围内获取多个特征的过程，通常是 [0, 1] 或 [-1, 1]。

例如，对于我们的数据集，我们将使用最小-最大缩放比例。

最小-最大缩放是一种技术，包括创建从特定列的最小值到最大值的缩放。

例如，在 Dioxide 列中，14变为 0 作为最小值，而47将变为 1。因此，所有其他值将介于两者之间：

| 类型 | 酸度    | 二氧化硫 | 酸碱度  |
| ---- | ------- | -------- | ------- |
| 0    | .75 | .94  | .07 |
| 1    | 1   | 0    | 1   |
| 1    | 0   | .52  | .86 |
| 0    | .67 | .06  | .68 |

最后，既然我们的数据集已经准备好了，我们就可以专注于模型了。

### 2.2. 该模型

该模型是从我们的数据集中获取输入并产生输出的算法。此外，可以在这篇[介绍性文章](https://www.baeldung.com/machine-learning-intro)中找到有关监督学习算法的更多信息。

让我们以人工神经网络为例，通过一些例子来解释即将到来的概念：

[![模型 2-1](https://www.baeldung.com/wp-content/uploads/2019/11/The-Model-2-1.png)](https://www.baeldung.com/wp-content/uploads/2019/11/The-Model-2-1.png)

在处理机器学习模型时，我们倾向于谈论不同种类的参数。它们在模型训练以适应模型应具有的最佳行为时进化。例如，我们将考虑输入数据的乘法器，这些乘法器将在训练期间进行调整。一些可能的参数在图中用红色值表示。

存在其他类型的参数，称为超参数。图中的蓝色值表示一些可能的超参数。这些参数是在训练期之前定义的。让我们考虑一下人工神经网络中所需的神经元数量。另一种超参数是使用什么样的激活函数或者使用什么样的损失函数。

现在让我们来看看两种机器学习算法：线性回归和人工神经网络。当然还有其他算法(SVM、逻辑回归、决策树或朴素贝叶斯等等)，但我们不会在本文中重点介绍它们。

### 2.3. 线性回归

顾名思义，线性回归是一种回归算法。它存在多种变体：

-   简单线性回归通过另一个标量变量解释一个标量变量。例如，房子的价格可以用平方英尺数来解释
-   多元线性回归也解释了一个标量变量。但这一次，我们将接收多个变量，而不是将一个变量作为输入。在预测房价的同一案例中，我们将考虑其他变量，例如房间数量、浴室数量、到学校的距离和公共交通选择。
-   Polynomial Linear Regression解决的问题与多元线性回归相同，但它所得出的预测并不是不断演变的。例如，房价可以呈指数上涨，而不是持续上涨
-   最后，General Linear Models将同时解释多个变量而不是一个变量

存在其他类型的线性回归，但不太常见。

简而言之，线性回归采用数据集(蓝点)并通过它们投射一条合理的线(红色)，以从新数据中推断出猜想：

[![简单线性回归示例 Baeldung](https://www.baeldung.com/wp-content/uploads/2019/11/Simple-Linear-Regression-Example-Baeldung.png)](https://www.baeldung.com/wp-content/uploads/2019/11/Simple-Linear-Regression-Example-Baeldung.png)

### 2.4. 人工神经网络

人工神经网络是更复杂的机器学习算法。

它们由一层或多层人工神经元组成，每一层由一个或多个神经元组成。神经元是接受输入并将输出前馈给其他神经元或最终输出的数学函数。

前馈完成后，执行反向传播以校正函数的变量。需要反向传播来降低模型的成本，并根据定义提高其准确性和精度。这是在训练期间使用训练数据集完成的。

人工神经网络可用于解决回归和分类问题。例如，他们通常擅长图像识别、语音识别、医学诊断或机器翻译。

我们可以想象一个由输入和输出神经元以及神经元隐藏层(也称为密集层)组成的人工神经网络：

[![深度神经网络](https://www.baeldung.com/wp-content/uploads/2019/11/deep-neural-network.png)](https://www.baeldung.com/wp-content/uploads/2019/11/deep-neural-network.png)

## 3. 使用原生 Kotlin 的演示

首先，我们将了解如何使用Kotlin或任何其他语言本地创建模型。让我们以简单的线性回归为例。

提醒一下，简单线性回归是一种使用独立变量预测因变量的模型。例如，它可以根据孩子的数量预测一个家庭所需的牛奶量。

### 3.1. 公式

我们来看看简单线性回归的公式以及如何得到所需变量的值：

```bash
# Variance
variance = sumOf(x => (x - meanX)²)
# Covariance
covariance = sumOf(x, y => (x - meanX)  (y - meanY))
# Slope
slope = coveriance / variance
# Y Intercept
yIntercept = meanY - slope x meanX
# Simple Linear Regression
dependentVariable = slope x independentVariable + yIntercept

```

### 3.2. 公式的 Kotlin 实现

现在让我们将此伪代码转换为 Kotlin，并使用一个数据集来表示每年数千人的平均工资：

```java
// Dataset
val xs = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
val ys = arrayListOf(25, 35, 49, 60, 75, 90, 115, 130, 150, 200)
// Variance
val variance = xs.sumByDouble { x -> (x - xs.average()).pow(2) }
// Covariance
val covariance = xs.zip(ys) { x, y -> (x - xs.average())  (y - ys.average()) }.sum()
// Slope
val slope = covariance / variance
// Y Intercept
val yIntercept = ys.average() - slope  xs.average()
// Simple Linear Regression
val simpleLinearRegression = { independentVariable: Int -> slope  independentVariable + yIntercept }

```

现在我们已经建立了一个模型，我们可以用它来预测值。例如，拥有2.5年或7.5年经验的人应该有权赚多少钱？让我们测试一下：

```java
val twoAndAHalfYearsOfExp = simpleLinearRegression.invoke(2.5) // 38.99
val sevenAndAHalfYearsOfExp = simpleLinearRegression.invoke(7.5) // 128.84

```

### 3.3. 评估结果

结果似乎符合预期的行为。但是我们如何评价这个说法呢？

我们将使用损失函数(在本例中为 R²)来计算工作年限对因变量工资的解释程度：

```bash
# SST
sst = sumOf(y => (y - meanY)²)
# SSR
ssr = sumOf(y => (y - prediction)²)
# R²
r² = (sst - ssr) / sst

```

在科特林中：

```java
// SST
val sst = ys.sumByDouble { y -> (y - ys.average()).pow(2) }
// SSR
val ssr = xs.zip(ys) { x, y -> (y - simpleLinearRegression.invoke(x.toDouble())).pow(2) }.sum()
// R²
val rsquared = (sst - ssr) / sst

```

最后，我们得到0.95的 R²，这意味着多年的经验可以准确解释员工工资的 95%。因此，这绝对是一个很好的预测模型。其他变量可以是谈判技巧或认证数量，例如用于解释剩余的 5%。或者可能只是随机性。

## 4. 使用 Deeplearning4j 的演示

出于本演示的目的，我们将使用[Zalando MNIST](https://github.com/zalandoresearch/fashion-mnist)数据集来训练卷积神经网络。该数据集由 28×28 的鞋子、包和其他八种服装的图像组成。

### 4.1. Maven 依赖项

首先，我们首先将[Deeplearning4j依赖项添加](https://search.maven.org/search?q=g:"org.deeplearning4j" AND a:"deeplearning4j-core")到我们简单的 Maven Kotlin 项目中：

```xml
<dependency>
    <groupId>org.deeplearning4j</groupId>
    <artifactId>deeplearning4j-core</artifactId>
    <version>1.0.0-beta5</version>
</dependency>
```

另外，让我们添加[nd4j依赖项](https://search.maven.org/search?q=g:"org.nd4j" AND a:"nd4j-native-platform")。ND4J 提供了一个 API 来执行多维矩阵计算：

```xml
<dependency>
    <groupId>org.nd4j</groupId>
    <artifactId>nd4j-native-platform</artifactId>
    <version>1.0.0-beta5</version>
</dependency>
```

### 4.2. 数据集

现在我们已经添加了所需的依赖项，让我们下载并准备数据集。它可以从[Zalando MNIST GitHub 页面](https://github.com/zalandoresearch/fashion-mnist)获得。我们将通过在像素向量的末尾附加标签来准备它：

```java
private const val OFFSET_SIZE = 4 //in bytes
private const val NUM_ITEMS_OFFSET = 4
private const val ITEMS_SIZE = 4
private const val ROWS = 28
private const val COLUMNS = 28
private const val IMAGE_OFFSET = 16
private const val IMAGE_SIZE = ROWS  COLUMNS

fun getDataSet(): MutableList<List<String>> {
    val labelsFile = File("train-labels-idx1-ubyte")
    val imagesFile = File("train-images-idx3-ubyte")

    val labelBytes = labelsFile.readBytes()
    val imageBytes = imagesFile.readBytes()

    val byteLabelCount = Arrays.copyOfRange(labelBytes, NUM_ITEMS_OFFSET, NUM_ITEMS_OFFSET + ITEMS_SIZE)
    val numberOfLabels = ByteBuffer.wrap(byteLabelCount).int

    val list = mutableListOf<List<String>>()

    for (i in 0 until numberOfLabels) {
        val label = labelBytes[OFFSET_SIZE + ITEMS_SIZE + i]
        val startBoundary = i  IMAGE_SIZE + IMAGE_OFFSET
        val endBoundary = i  IMAGE_SIZE + IMAGE_OFFSET + IMAGE_SIZE
        val imageData = Arrays.copyOfRange(imageBytes, startBoundary, endBoundary)

        val imageDataList = imageData.iterator()
          .asSequence()
          .asStream().map { b -> b.toString() }
          .collect(Collectors.toList())
        imageDataList.add(label.toString())
        list.add(imageDataList)
    }
    return list
}
```

### 4.3. 构建人工神经网络

现在让我们构建我们的神经网络。为此，我们需要：

-   多个卷积层——这些层已被证明在图像识别方面是最佳的。事实上，在区域而不是每个像素中工作的事实将允许更好的形状识别
-   池化层与卷积层一起使用——池化层用于将从卷积层接收到的不同池化值聚合到单个单元格中
-   多个批量归一化层，通过归一化不同层的输出来避免过度拟合，无论是神经元的卷积层还是简单的前馈层
-   多个常规前馈神经元层桥接最后一个卷积层的输出和神经网络模型

```java
private fun buildCNN(): MultiLayerNetwork {
    val multiLayerNetwork = MultiLayerNetwork(NeuralNetConfiguration.Builder()
      .seed(123)
      .l2(0.0005)
      .updater(Adam())
      .weightInit(WeightInit.XAVIER)
      .list()
      .layer(0, buildInitialConvolutionLayer())
      .layer(1, buildBatchNormalizationLayer())
      .layer(2, buildPoolingLayer())
      .layer(3, buildConvolutionLayer())
      .layer(4, buildBatchNormalizationLayer())
      .layer(5, buildPoolingLayer())
      .layer(6, buildDenseLayer())
      .layer(7, buildBatchNormalizationLayer())
      .layer(8, buildDenseLayer())
      .layer(9, buildOutputLayer())
      .setInputType(InputType.convolutionalFlat(28, 28, 1))
      .backprop(true)
      .build())
    multiLayerNetwork.init()
    return multiLayerNetwork
}

```

### 4.4. 训练模型

我们现在已经准备好模型和数据集。我们仍然需要一个训练程序：

```java
private fun learning(cnn: MultiLayerNetwork, trainSet: RecordReaderDataSetIterator) {
    for (i in 0 until 10) {
        cnn.fit(trainSet)
    }
}

```

### 4.5. 测试模型

此外，我们需要一段代码针对测试数据集测试模型：

```java
private fun testing(cnn: MultiLayerNetwork, testSet: RecordReaderDataSetIterator) {
    val evaluation = Evaluation(10)
    while (testSet.hasNext()) {
        val next = testSet.next()
        val output = cnn.output(next.features)
        evaluation.eval(next.labels, output)
    }
    println(evaluation.stats())
    println(evaluation.confusionToString())
}

```

### 4.6. 运行监督学习

我们终于可以一个接一个地调用所有这些方法，看看我们模型的性能：

```java
val dataset = getDataSet()
dataset.shuffle()
val trainDatasetIterator = createDatasetIterator(dataset.subList(0, 50_000))
val testDatasetIterator = createDatasetIterator(dataset.subList(50_000, 60_000))

val cnn = buildCNN()
learning(cnn, trainDatasetIterator)
testing(cnn, testDatasetIterator)

```

### 4.7. 结果

最后，经过几分钟的训练，我们将能够看到我们的模型表现如何：

```bash
==========================Scores========================================
 # of classes:    10
 Accuracy:        0,8609
 Precision:       0,8604
 Recall:          0,8623
 F1 Score:        0,8608
Precision, recall & F1: macro-averaged (equally weighted avg. of 10 classes)
========================================================================
   Predicted:         0      1      2      3      4      5      6      7      8      9
   Actual:
0  0          |     855      3     15     33      7      0     60      0      8      0
1  1          |       3    934      2     32      2      0      5      0      2      0
2  2          |      16      2    805      8     92      1     59      0      7      0
3  3          |      17     19      4    936     38      0     32      0      1      0
4  4          |       5      5     90     35    791      0    109      0      9      0
5  5          |       0      0      0      0      0    971      0     25      0     22
6  6          |     156      8    105     36     83      0    611      0     16      0
7  7          |       0      0      0      0      0     85      0    879      1     23
8  8          |       5      2      1      6      2      5      8      1    889      2
9  9          |       0      0      0      0      0     18      0     60      0    938

```

## 5.总结

我们已经了解了如何使用监督学习来训练使用 Kotlin 的机器学习模型。我们使用 Deeplearning4j 来帮助我们处理复杂的算法。简单的算法甚至可以在没有任何库的情况下本地实现。