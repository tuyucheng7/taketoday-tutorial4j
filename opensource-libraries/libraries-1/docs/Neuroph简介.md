## 1. 简介

本文介绍了[Neuroph——](http://neuroph.sourceforge.net/)一个用于创建神经网络和利用机器学习的开源库。

在本文中，我们了解了核心概念和几个有关如何将它们组合在一起的示例。

## 2.神经病

我们可以使用以下方式与 Neuroph 互动：

-   基于 GUI 的工具
-   一个Java库

这两种方法都依赖于一个底层类层次结构，它从神经元层构建人工神经网络。

我们将专注于编程方面，但会参考 Neuroph 基于 GUI 的方法中的几个共享类，以帮助阐明我们在做什么。

有关基于 GUI 的方法的更多信息，请查看 Neuroph[文档](http://neuroph.sourceforge.net/documentation.html)。

### 2.1. 依赖关系

如果为了使用 Neuroph，我们需要添加以下 Maven 条目：

```xml
<dependency>
    <groupId>org.beykery</groupId>
    <artifactId>neuroph</artifactId>
    <version>2.92</version>
</dependency>
```

最新版本可以[在 Maven Central](https://search.maven.org/classic/#search|ga|1|a%3A"neuroph")上找到。

## 3. 关键类和概念

使用的所有基本概念构建块都有相应的Java类。

神经元连接到层，然后将层分组到NeuralNetworks中。NeuralNetworks随后使用LearningRules和DataSets进行训练。

### 3.1. 神经元

Neuron类有四个主要属性：

1.  inputConnection：神经元之间的加权连接
2.  inputFunction：指定应用于传入连接数据的权重和向量和
3.  transferFunction：指定应用于传出数据的权重和向量和
    
4.  output：将transferFunctions和inputFunctions应用于inputConnection所产生的输出值

这四个主要属性共同构成了行为：

```java
output = transferFunction(inputFunction(inputConnections));
```

### 3.2. 层

层本质上是神经元的分组，使得层中的每个神经元(通常)仅与前后层中的神经元连接。

因此，层通过存在于它们的神经元上的加权函数在它们之间传递信息。

神经元可以添加到图层中：


```java
Layer layer = new Layer(); 
layer.addNeuron(n);
```

### 3.3. 神经网络

顶级超类NeuralNetwork被细分为几种常见的人工神经网络，包括卷积神经网络(子类ConvolutionalNetwork)、Hopfield 神经网络(子类Hopfield)和多层感知器神经网络(子类MultilayerPerceptron)。

所有神经网络都由层组成，这些层通常组织成三分法：

1.  输入层
2.  隐藏层
3.  输出层

如果我们使用 NeuralNetwork 的子类(例如 Perceptron)的构造函数，我们可以使用这个简单的方法传递Layer s、每个Layer的Neuron数量以及它们的索引：

```java
NeuralNetwork ann = new Perceptron(2, 4, 1);
```

有时我们会想手动执行此操作(很高兴看到引擎盖下发生了什么)。向神经网络添加层的基本操作是这样完成的：

```java
NeuralNetwork ann = new NeuralNetwork();   
Layer layer = new Layer();
ann.addLayer(0, layer);
ann.setInputNeurons(layer.getNeurons());

```

第一个参数指定层在NeuralNetwork中的索引；第二个参数指定图层本身。手动添加的层应使用ConnectionFactory类连接：

```java
ann.addLayer(0, inputLayer);    
ann.addLayer(1, hiddenLayerOne); 
ConnectionFactory.fullConnect(ann.getLayerAt(0), ann.getLayerAt(1));
```

第一层和最后一层也应该相连：

```java
ConnectionFactory.fullConnect(ann.getLayerAt(0), 
  ann.getLayerAt(ann.getLayersCount() - 1), false);
ann.setOutputNeurons(ann.getLayerAt(
  ann.getLayersCount() - 1).getNeurons());
```

请记住，神经网络的强度和功率在很大程度上取决于：

1.  神经网络中的层数
2.  每层中的神经元数量(以及它们之间的加权函数)，以及
3.  训练算法的有效性/数据集的准确性

### 3.4. 训练我们的神经网络 

神经网络使用DataSet和LearningRule类进行训练。

DataSet用于表示和提供要学习或用于训练NeuralNetwork的信息。DataSet的特征在于它们的输入大小、输出大小和行数 (DataSetRow)。

```java
int inputSize = 2; 
int outputSize = 1; 
DataSet ds = new DataSet(inputSize, outputSize);

DataSetRow rOne 
  = new DataSetRow(new double[] {0, 0}, new double[] {0});
ds.addRow(rOne);
DataSetRow rTwo 
  = new DataSetRow(new double[] {1, 1}, new double[] {0});
ds.addRow(rTwo);
```

LearningRule指定NeuralNetwork教授或训练DataSet的方式。LearningRule的子类包括BackPropagation和SupervisedLearning。

```java
NeuralNetwork ann = new NeuralNetwork();
//...
BackPropagation backPropagation = new BackPropagation();
backPropagation.setMaxIterations(1000);
ann.learn(ds, backPropagation);
```

## 4. 把它们放在一起

现在让我们将这些构建块放在一个真实的例子中。我们将从将多个层组合到熟悉的输入层、隐藏层和输出层模式开始，这些模式以大多数神经网络架构为例。

### 4.1. 图层

我们将通过组合四个层来组装我们的神经网络。我们的目标是构建一个 (2, 4, 4, 1)神经网络。

让我们首先定义我们的输入层：

```java
Layer inputLayer = new Layer();
inputLayer.addNeuron(new Neuron());
inputLayer.addNeuron(new Neuron());
```

接下来，我们实现隐藏层一：

```java
Layer hiddenLayerOne = new Layer();
hiddenLayerOne.addNeuron(new Neuron());
hiddenLayerOne.addNeuron(new Neuron());
hiddenLayerOne.addNeuron(new Neuron());
hiddenLayerOne.addNeuron(new Neuron());
```

隐藏层二：

```java
Layer hiddenLayerTwo = new Layer(); 
hiddenLayerTwo.addNeuron(new Neuron()); 
hiddenLayerTwo.addNeuron(new Neuron()); 
hiddenLayerTwo.addNeuron(new Neuron()); 
hiddenLayerTwo.addNeuron(new Neuron());
```

最后，我们定义输出层：

```java
Layer outputLayer = new Layer();
outputLayer.addNeuron(new Neuron()); 

```

### 4.2. 神经网络

接下来，我们可以将它们组合成一个NeuralNetwork：

```java
NeuralNetwork ann = new NeuralNetwork();
ann.addLayer(0, inputLayer);
ann.addLayer(1, hiddenLayerOne);
ConnectionFactory.fullConnect(ann.getLayerAt(0), ann.getLayerAt(1));
ann.addLayer(2, hiddenLayerTwo);
ConnectionFactory.fullConnect(ann.getLayerAt(1), ann.getLayerAt(2));
ann.addLayer(3, outputLayer);
ConnectionFactory.fullConnect(ann.getLayerAt(2), ann.getLayerAt(3));
ConnectionFactory.fullConnect(ann.getLayerAt(0), 
  ann.getLayerAt(ann.getLayersCount()-1), false);
ann.setInputNeurons(inputLayer.getNeurons());
ann.setOutputNeurons(outputLayer.getNeurons());
```

### 4.3. 训练

出于训练目的，让我们通过指定输入和结果输出向量的大小来组合一个数据集：

```java
int inputSize = 2;
int outputSize = 1;
DataSet ds = new DataSet(inputSize, outputSize);
```

我们将一个基本行添加到我们的数据集中，遵守上面定义的输入和输出约束——我们在这个例子中的目标是教我们的网络进行基本的 XOR(异或)操作：

```java
DataSetRow rOne
  = new DataSetRow(new double[] {0, 1}, new double[] {1});
ds.addRow(rOne);
DataSetRow rTwo
  = new DataSetRow(new double[] {1, 1}, new double[] {0});
ds.addRow(rTwo);
DataSetRow rThree 
  = new DataSetRow(new double[] {0, 0}, new double[] {0});
ds.addRow(rThree);
DataSetRow rFour
  = new DataSetRow(new double[] {1, 0}, new double[] {1});
ds.addRow(rFour);
```

接下来，让我们使用内置的BackPropogation LearningRule训练我们的神经网络：

```java
BackPropagation backPropagation = new BackPropagation();
backPropagation.setMaxIterations(1000);
ann.learn(ds, backPropagation);

```

### 4.4. 测试

现在我们的神经网络已经训练好了，让我们来测试一下。对于作为DataSetRow传递到我们的DataSet的每一对逻辑值，我们运行以下类型的测试：

```java
ann.setInput(0, 1);
ann.calculate();
double[] networkOutputOne = ann.getOutput();

```

需要记住的重要一点是，NeuralNetworks仅在包含 0 和 1 的区间内输出一个值。要输出其他值，我们必须对数据进行规范化和非规范化。

在这种情况下，对于逻辑运算，0 和 1 非常适合这项工作。输出将是：

```plaintext
Testing: 1, 0 Expected: 1.0 Result: 1.0
Testing: 0, 1 Expected: 1.0 Result: 1.0
Testing: 1, 1 Expected: 0.0 Result: 0.0
Testing: 0, 0 Expected: 0.0 Result: 0.0

```

我们看到我们的神经网络成功预测了正确答案！

## 5.总结

我们刚刚回顾了 Neuroph 使用的基本概念和类。