## 1. 概述

在本教程中，我们将使用Java中的 Deeplearning4j 库构建和训练卷积神经网络模型。

有关如何设置库的更多信息，请参阅我们[关于 Deeplearning4j 的指南](https://www.baeldung.com/deeplearning4j)。

## 2.图像分类

### 2.1. 问题陈述

假设我们有一组图像。每个图像代表一个特定类别的对象。此外，图像上的对象属于唯一已知的类别。因此，问题陈述是建立能够识别给定图像上对象类别的模型。

例如，假设我们有一组包含十个手势的图像。我们建立一个模型并对其进行训练以对它们进行分类。然后在训练之后，我们可能会传递其他图像并对它们上的手势进行分类。当然，给定的手势应该属于已知的类。

### 2.2. 图像表示

在计算机内存中，图像可以表示为数字矩阵。每个数字都是一个像素值，范围从 0 到 255。

灰度图像是二维矩阵。类似地，RGB 图像是具有宽度、高度和深度维度的 3D 矩阵。

正如我们所见，图像是一组数字。因此，我们可以构建多层网络模型来训练它们对图像进行分类。

## 3. 卷积神经网络

卷积神经网络 (CNN) 是一种具有特定结构的多层网络模型。CNN 的结构可以分为两个块：卷积层和完全连接(或密集)层。让我们看看它们中的每一个。

### 3.1. 卷积层

每个卷积层都是一组方形矩阵，称为内核。最重要的是，我们需要它们对输入图像执行卷积。它们的数量和大小可能会有所不同，具体取决于给定的数据集。我们大多使用 3×3 或 5×5 的内核，很少使用 7×7 的。确切的大小和数量是通过反复试验选择的。

此外，我们在训练开始时随机选择核矩阵的变量。它们是网络的权重。

为了执行卷积，我们可以使用内核作为滑动窗口。我们将核权重乘以相应的图像像素并计算总和。然后我们可以使用步幅(向右移动)和填充(向下移动)移动内核以覆盖图像的下一个块。因此，我们将获得可用于进一步计算的值。

简而言之，有了这一层，我们得到了一个卷积图像。一些变量可能小于零。这通常意味着这些变量不如其他变量重要。这就是为什么应用[ReLU](https://en.wikipedia.org/wiki/Rectifier_(neural_networks))函数是进一步减少计算量的好方法。

### 3.2. 子采样层

子采样(或池化)层是网络的一层，通常在卷积层之后使用。卷积之后，我们得到了很多计算变量。然而，我们的任务是从中选择最有价值的。

该方法是将[滑动窗口算法](https://www.baeldung.com/cs/sliding-window-algorithm)应用于卷积图像。在每一步，我们都会在预定义大小的方形窗口中选择最大值，通常在 2×2 和 5×5 像素之间。结果，我们将有更少的计算参数。因此，这将减少计算量。

### 3.3. 致密层

密集(或全连接)层是由多个神经元组成的层。我们需要这一层来执行分类。此外，可能有两个或多个这样的结果层。重要的是，最后一层的大小应该等于分类的类数。

网络的输出是图像属于每个类别的概率。为了预测概率，我们将使用[Softmax](https://en.wikipedia.org/wiki/Softmax_function)激活函数。

### 3.4. 优化技术

为了进行训练，我们需要优化权重。请记住，我们最初随机选择这些变量。神经网络是一个很大的功能。而且，它有很多未知参数，我们的权重。

当我们将图像传递给网络时，它会为我们提供答案。然后，我们可以建立一个损失函数，这将取决于这个答案。在监督学习方面，我们也有一个实际的答案——真正的班级。我们的任务是最小化这个损失函数。如果我们成功了，那么我们的模型就是训练有素的。

为了最小化函数，我们必须更新网络的权重。为了做到这一点，我们可以计算损失函数对每个这些未知参数的导数。然后，我们可以更新每个权重。

我们可能会增加或减少权重值以找到损失函数的局部最小值，因为我们知道斜率。而且，这个过程是迭代的，称为[梯度下降](https://www.baeldung.com/java-gradient-descent)。反向传播使用梯度下降将权重更新从网络的末端传播到网络的开头。

在本教程中，我们将使用[随机梯度下降](https://en.wikipedia.org/wiki/Stochastic_gradient_descent)(SGD) 优化算法。主要思想是我们在每一步随机选择一批训练图像。然后我们应用反向传播。

### 3.5. 评估指标

最后，在训练网络之后，我们需要获得有关我们的模型执行情况的信息。

最常用的指标是准确性。这是正确分类的图像与所有图像的比率。同时，召回率、准确率和 F1 分数也是图像分类的非常重要的[指标](https://medium.com/analytics-vidhya/confusion-matrix-accuracy-precision-recall-f1-score-ade299cf63cd)。

## 4. 数据集准备

在本节中，我们将准备图像。让我们在本教程中使用嵌入式[CIFAR10数据集。](https://en.wikipedia.org/wiki/CIFAR-10)我们将创建迭代器来访问图像：

```java
public class CifarDatasetService implements IDataSetService {

    private CifarDataSetIterator trainIterator;
    private CifarDataSetIterator testIterator;

    public CifarDatasetService() {
         trainIterator = new CifarDataSetIterator(trainBatch, trainImagesNum, true);
         testIterator = new CifarDataSetIterator(testBatch, testImagesNum, false);
    }

    // other methods and fields declaration

}
```

我们可以自己选择一些参数。TrainBatch和testBatch分别是每个训练和评估步骤的图像数量。TrainImagesNum和testImagesNum是用于训练和测试的图像数量。一个纪元持续trainImagesNum / trainBatch steps。因此，拥有 2048 个批量大小 = 32 的训练图像将导致每个时期有 2048 / 32 = 64 个步骤。

## 5. Deeplearning4j 中的卷积神经网络

### 5.1. 建立模型

接下来，让我们从头开始构建我们的 CNN 模型。为此，我们将使用卷积层、子采样(池化)层和完全连接(密集)层。

```java
MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
  .seed(1611)
  .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
  .learningRate(properties.getLearningRate())
  .regularization(true)
  .updater(properties.getOptimizer())
  .list()
  .layer(0, conv5x5())
  .layer(1, pooling2x2Stride2())
  .layer(2, conv3x3Stride1Padding2())
  .layer(3, pooling2x2Stride1())
  .layer(4, conv3x3Stride1Padding1())
  .layer(5, pooling2x2Stride1())
  .layer(6, dense())
  .pretrain(false)
  .backprop(true)
  .setInputType(dataSetService.inputType())
  .build();

network = new MultiLayerNetwork(configuration);
```

在这里，我们指定学习率、更新算法、模型的输入类型和分层架构。我们可以对这些配置进行试验。因此，我们可以训练许多具有不同架构和训练参数的模型。此外，我们可以比较结果并选择最佳模型。

### 5.2. 训练模型

然后，我们将训练构建的模型。这可以通过几行代码来完成：

```java
public void train() {
    network.init();    
    IntStream.range(1, epochsNum + 1).forEach(epoch -> {
        network.fit(dataSetService.trainIterator());
    });
}
```

epochs的数量是我们可以自己指定的参数。我们有一个小数据集。结果，几百个纪元就足够了。

### 5.3. 评估模型

最后，我们可以评估现在训练的模型。Deeplearning4j 库提供了轻松完成此操作的能力：

```java
public Evaluation evaluate() {
   return network.evaluate(dataSetService.testIterator());
}
```

评估是一个对象，它包含在训练模型后计算出的指标。这些是准确率、精确率、召回率和 F1 分数。此外，它还有一个友好的可打印界面：

```yaml
==========================Scores=====================
# of classes: 11
Accuracy: 0,8406
Precision: 0,7303
Recall: 0,6820
F1 Score: 0,6466
=====================================================
```

## 六. 总结

在本教程中，我们了解了 CNN 模型的架构、优化技术和评估指标。此外，我们使用Java中的 Deeplearning4j 库实现了该模型。