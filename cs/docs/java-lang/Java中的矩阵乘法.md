## 1. 概述

在本教程中，我们将了解如何在Java中将两个矩阵相乘。

由于语言本身不存在矩阵概念，我们将自己实现它，我们还将使用一些库来了解它们如何处理矩阵乘法。

最后，我们将对我们探索的不同解决方案进行一些基准测试，以确定最快的解决方案。

## 2. 例子

让我们首先设置一个示例，我们将能够在整个教程中引用它。

首先，我们想象一个 3×2 矩阵：

[![第一个矩阵 1](https://www.baeldung.com/wp-content/uploads/2019/07/firstMatrix-1.png)](https://www.baeldung.com/wp-content/uploads/2019/07/firstMatrix-1.png)

现在让我们想象第二个矩阵，这次是两行四列：

[![第二Matrux 1](https://www.baeldung.com/wp-content/uploads/2019/07/secondMatrux-1.png)](https://www.baeldung.com/wp-content/uploads/2019/07/secondMatrux-1.png)

然后，将第一个矩阵乘以第二个矩阵，得到一个 3×4 矩阵：

[![乘法矩阵 1](https://www.baeldung.com/wp-content/uploads/2019/07/multiplicatedMatrix-1.png)](https://www.baeldung.com/wp-content/uploads/2019/07/multiplicatedMatrix-1.png)

提醒一下，此结果是通过使用以下公式计算结果矩阵的每个单元格获得的：

[![乘法算法1](https://www.baeldung.com/wp-content/uploads/2019/07/multiplicationAlgorithm-1.png)](https://www.baeldung.com/wp-content/uploads/2019/07/multiplicationAlgorithm-1.png)

其中 r是矩阵 A的行数，c 是矩阵 B的列数， n是矩阵A的列数，它必须与矩阵B的行数匹配。

## 3.矩阵乘法

### 3.1. 自己实施

让我们从我们自己的矩阵实现开始。

我们将保持简单，只使用二维双精度数组：

```java
double[][] firstMatrix = {
  new double[]{1d, 5d},
  new double[]{2d, 3d},
  new double[]{1d, 7d}
};

double[][] secondMatrix = {
  new double[]{1d, 2d, 3d, 7d},
  new double[]{5d, 2d, 8d, 1d}
};
```

这些是我们示例的两个矩阵。让我们创建一个预期的乘法结果：

```java
double[][] expected = {
  new double[]{26d, 12d, 43d, 12d},
  new double[]{17d, 10d, 30d, 17d},
  new double[]{36d, 16d, 59d, 14d}
};
```

现在一切就绪，让我们来实现乘法算法。我们将首先创建一个空结果数组并遍历其单元格以在每个单元格中存储预期值：

```java
double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix) {
    double[][] result = new double[firstMatrix.length][secondMatrix[0].length];

    for (int row = 0; row < result.length; row++) {
        for (int col = 0; col < result[row].length; col++) {
            result[row][col] = multiplyMatricesCell(firstMatrix, secondMatrix, row, col);
        }
    }

    return result;
}
```

最后，我们来实现单个单元格的计算。为了实现这一点，我们将使用前面示例演示中显示的公式：

```java
double multiplyMatricesCell(double[][] firstMatrix, double[][] secondMatrix, int row, int col) {
    double cell = 0;
    for (int i = 0; i < secondMatrix.length; i++) {
        cell += firstMatrix[row][i]  secondMatrix[i][col];
    }
    return cell;
}
```

最后，让我们检查算法的结果是否符合我们的预期结果：

```java
double[][] actual = multiplyMatrices(firstMatrix, secondMatrix);
assertThat(actual).isEqualTo(expected);
```

### 3.2. eJML

我们要看的第一个库是 EJML，它代表[EfficientJavaMatrix Library](http://ejml.org/wiki/index.php?title=Main_Page)。在编写本教程时，它是最新更新的Java矩阵库之一。其目的是在计算和内存使用方面尽可能高效。

我们必须将[依赖项添加到](https://search.maven.org/search?q=g:org.ejml AND a:ejml-all)pom.xml中的库：

```xml
<dependency>
    <groupId>org.ejml</groupId>
    <artifactId>ejml-all</artifactId>
    <version>0.38</version>
</dependency>
```

我们将使用与之前几乎相同的模式：根据我们的示例创建两个矩阵，并检查它们相乘的结果是否与我们之前计算的结果相同。

因此，让我们使用 EJML 创建我们的矩阵。为了实现这一点，我们将使用库提供的 SimpleMatrix类。

它可以将二维双精度数组作为其构造函数的输入：

```java
SimpleMatrix firstMatrix = new SimpleMatrix(
  new double[][] {
    new double[] {1d, 5d},
    new double[] {2d, 3d},
    new double[] {1d ,7d}
  }
);

SimpleMatrix secondMatrix = new SimpleMatrix(
  new double[][] {
    new double[] {1d, 2d, 3d, 7d},
    new double[] {5d, 2d, 8d, 1d}
  }
);
```

现在，让我们定义乘法的预期矩阵：

```java
SimpleMatrix expected = new SimpleMatrix(
  new double[][] {
    new double[] {26d, 12d, 43d, 12d},
    new double[] {17d, 10d, 30d, 17d},
    new double[] {36d, 16d, 59d, 14d}
  }
);
```

现在我们都设置好了，让我们看看如何将两个矩阵相乘。SimpleMatrix 类提供了一个 mult ()方法，将另一个SimpleMatrix作为参数并返回两个矩阵的乘积：

```java
SimpleMatrix actual = firstMatrix.mult(secondMatrix);
```

让我们检查获得的结果是否与预期的结果相匹配。

由于 SimpleMatrix没有覆盖 equals()方法，我们不能依赖它来进行验证。但是，它提供了另一种选择： isIdentical()方法，该方法不仅采用另一个矩阵参数，而且采用双容错参数来忽略由于双精度引起的微小差异：

```java
assertThat(actual).matches(m -> m.isIdentical(expected, 0d));
```

EJML 库的矩阵乘法到此结束。让我们看看其他人提供的是什么。

### 3.3. ND4J

现在让我们试试[ND4J 库](https://deeplearning4j.konduit.ai/nd4j/tutorials/quickstart)。ND4J 是一个计算库，是[deeplearning4j](https://deeplearning4j.konduit.ai/)项目的一部分。除其他外，ND4J 还提供矩阵计算功能。

首先，我们必须获取[库依赖](https://search.maven.org/search?q=g:org.nd4j AND a:nd4j-native)项：

```xml
<dependency>
    <groupId>org.nd4j</groupId>
    <artifactId>nd4j-native</artifactId>
    <version>1.0.0-beta4</version>
</dependency>
```

请注意，我们在这里使用的是测试版，因为 GA 版本似乎存在一些错误。

为了简洁起见，我们不会重写二维双数组，只关注它们如何与每个库一起使用。因此，对于 ND4J，我们必须创建一个INDArray。为此，我们将调用 Nd4j.create()工厂方法并向其传递一个代表矩阵的双精度数组：

```java
INDArray matrix = Nd4j.create(/ a two dimensions double array /);
```

与上一节一样，我们将创建三个矩阵：我们要将两个矩阵相乘，一个是预期结果。

之后，我们想要使用INDArray.mmul()方法实际执行前两个矩阵之间的乘法：

```java
INDArray actual = firstMatrix.mmul(secondMatrix);
```

然后，我们再次检查实际结果是否与预期结果相符。这次我们可以依赖相等性检查：

```java
assertThat(actual).isEqualTo(expected);
```

这演示了如何使用 ND4J 库进行矩阵计算。

### 3.4. 阿帕奇公地

现在让我们谈谈[Apache Commons Math3 模块](https://commons.apache.org/proper/commons-math/)，它为我们提供包括矩阵操作在内的数学计算。

同样，我们必须在pom.xml中指定[依赖](https://search.maven.org/search?q=g:org.apache.commons AND a:commons-math3)项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-math3</artifactId>
    <version>3.6.1</version>
</dependency>
```

设置完成后，我们可以使用RealMatrix 接口及其 Array2DRowRealMatrix实现来创建我们常用的矩阵。实现类的构造函数以一个二维double数组作为参数：

```java
RealMatrix matrix = new Array2DRowRealMatrix(/ a two dimensions double array /);
```

对于矩阵乘法，RealMatrix接口提供了一个 multiply()方法，该 方法采用另一个 RealMatrix参数：

```java
RealMatrix actual = firstMatrix.multiply(secondMatrix);
```

我们最终可以验证结果是否等于我们的预期：

```java
assertThat(actual).isEqualTo(expected);
```

让我们看看下一个图书馆！

### 3.5. LA4J

这个名为 LA4J，代表[Linear Algebra for Java](http://la4j.org/)。

让我们也为此添加[依赖项：](https://search.maven.org/search?q=g:org.la4j AND a:la4j)

```xml
<dependency>
    <groupId>org.la4j</groupId>
    <artifactId>la4j</artifactId>
    <version>0.6.0</version>
</dependency>
```

现在，LA4J 的工作方式与其他库非常相似。它提供了一个 带有 Basic2DMatrix实现的Matrix接口，该接口采用二维双精度数组作为输入：

```java
Matrix matrix = new Basic2DMatrix(/ a two dimensions double array /);
```

与 Apache Commons Math3 模块中一样，乘法方法是 multiply()并以另一个 Matrix作为其参数：

```java
Matrix actual = firstMatrix.multiply(secondMatrix);
```

我们可以再次检查结果是否符合我们的预期：

```java
assertThat(actual).isEqualTo(expected);
```

现在让我们来看看我们的最后一个库：Colt。

### 3.6. 小马

[Colt](https://dst.lbl.gov/ACSSoftware/colt/)是 CERN 开发的库。它提供支持高性能科学和技术计算的功能。

与之前的库一样，我们必须获得[正确的依赖项](https://search.maven.org/search?q=g:colt AND a:colt)：

```xml
<dependency>
    <groupId>colt</groupId>
    <artifactId>colt</artifactId>
    <version>1.2.0</version>
</dependency>
```

为了用 Colt 创建矩阵，我们必须使用DoubleFactory2D 类。它带有三个工厂实例：dense、sparse和 rowCompressed。每个都经过优化以创建匹配类型的矩阵。

出于我们的目的，我们将使用密集实例。这次，调用的方法是 make()，它再次采用二维双精度数组，生成一个DoubleMatrix2D对象：

```java
DoubleMatrix2D matrix = doubleFactory2D.make(/ a two dimensions double array /);
```

一旦我们的矩阵被实例化，我们就会想要将它们相乘。这一次，矩阵对象上没有方法可以做到这一点。我们必须创建一个Algebra类的实例，它有一个 mult()方法，它接受两个矩阵作为参数：

```java
Algebra algebra = new Algebra();
DoubleMatrix2D actual = algebra.mult(firstMatrix, secondMatrix);
```

然后，我们可以将实际结果与预期结果进行比较：

```java
assertThat(actual).isEqualTo(expected);
```

## 4. 基准测试

现在我们已经完成了对矩阵乘法的不同可能性的探索，让我们检查一下哪些是最高性能的。

### 4.1. 小矩阵

让我们从小矩阵开始。这里有一个 3×2 和一个 2×4 矩阵。

为了实施性能测试，我们将使用[JMH 基准测试库](https://www.baeldung.com/java-microbenchmark-harness)。让我们使用以下选项配置基准测试类：

```java
public static void main(String[] args) throws Exception {
    Options opt = new OptionsBuilder()
      .include(MatrixMultiplicationBenchmarking.class.getSimpleName())
      .mode(Mode.AverageTime)
      .forks(2)
      .warmupIterations(5)
      .measurementIterations(10)
      .timeUnit(TimeUnit.MICROSECONDS)
      .build();

    new Runner(opt).run();
}
```

这样，JMH 将为每个用@Benchmark注解的方法进行两次完整运行，每次运行五次预热迭代(不计入平均计算)和十次测量迭代。至于测量，它将收集不同库的平均执行时间，以微秒为单位。

然后我们必须创建一个包含数组的状态对象：

```java
@State(Scope.Benchmark)
public class MatrixProvider {
    private double[][] firstMatrix;
    private double[][] secondMatrix;

    public MatrixProvider() {
        firstMatrix =
          new double[][] {
            new double[] {1d, 5d},
            new double[] {2d, 3d},
            new double[] {1d ,7d}
          };

        secondMatrix =
          new double[][] {
            new double[] {1d, 2d, 3d, 7d},
            new double[] {5d, 2d, 8d, 1d}
          };
    }
}
```

这样，我们确保数组初始化不是基准测试的一部分。之后，我们仍然必须创建执行矩阵乘法的方法，使用MatrixProvider对象作为数据源。我们不会在这里重复代码，因为我们之前已经看到了每个库。

最后，我们将使用我们的 main方法运行基准测试过程。这给了我们以下结果：

```plaintext
Benchmark                                                           Mode  Cnt   Score   Error  Units
MatrixMultiplicationBenchmarking.apacheCommonsMatrixMultiplication  avgt   20   1,008 ± 0,032  us/op
MatrixMultiplicationBenchmarking.coltMatrixMultiplication           avgt   20   0,219 ± 0,014  us/op
MatrixMultiplicationBenchmarking.ejmlMatrixMultiplication           avgt   20   0,226 ± 0,013  us/op
MatrixMultiplicationBenchmarking.homemadeMatrixMultiplication       avgt   20   0,389 ± 0,045  us/op
MatrixMultiplicationBenchmarking.la4jMatrixMultiplication           avgt   20   0,427 ± 0,016  us/op
MatrixMultiplicationBenchmarking.nd4jMatrixMultiplication           avgt   20  12,670 ± 2,582  us/op
```

正如我们所见， EJML和Colt的性能非常好，每个操作大约需要五分之一微秒，而ND4j的性能较差，每个操作需要十多微秒。其他图书馆的表演介于两者之间。

此外，值得注意的是，当将预热迭代次数从 5 次增加到 10 次时，所有库的性能都会提高。

### 4.2. 大型矩阵

现在，如果我们采用更大的矩阵，比如 3000×3000，会发生什么？为了检查会发生什么，让我们首先创建另一个状态类来提供该大小的生成矩阵：

```java
@State(Scope.Benchmark)
public class BigMatrixProvider {
    private double[][] firstMatrix;
    private double[][] secondMatrix;

    public BigMatrixProvider() {}

    @Setup
    public void setup(BenchmarkParams parameters) {
        firstMatrix = createMatrix();
        secondMatrix = createMatrix();
    }

    private double[][] createMatrix() {
        Random random = new Random();

        double[][] result = new double[3000][3000];
        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[row].length; col++) {
                result[row][col] = random.nextDouble();
            }
        }
        return result;
    }
}
```

如我们所见，我们将创建 3000×3000 个二维双精度数组，其中填充了随机实数。

现在让我们创建基准测试类：

```java
public class BigMatrixMultiplicationBenchmarking {
    public static void main(String[] args) throws Exception {
        Map<String, String> parameters = parseParameters(args);

        ChainedOptionsBuilder builder = new OptionsBuilder()
          .include(BigMatrixMultiplicationBenchmarking.class.getSimpleName())
          .mode(Mode.AverageTime)
          .forks(2)
          .warmupIterations(10)
          .measurementIterations(10)
          .timeUnit(TimeUnit.SECONDS);

        new Runner(builder.build()).run();
    }

    @Benchmark
    public Object homemadeMatrixMultiplication(BigMatrixProvider matrixProvider) {
        return HomemadeMatrix
          .multiplyMatrices(matrixProvider.getFirstMatrix(), matrixProvider.getSecondMatrix());
    }

    @Benchmark
    public Object ejmlMatrixMultiplication(BigMatrixProvider matrixProvider) {
        SimpleMatrix firstMatrix = new SimpleMatrix(matrixProvider.getFirstMatrix());
        SimpleMatrix secondMatrix = new SimpleMatrix(matrixProvider.getSecondMatrix());

        return firstMatrix.mult(secondMatrix);
    }

    @Benchmark
    public Object apacheCommonsMatrixMultiplication(BigMatrixProvider matrixProvider) {
        RealMatrix firstMatrix = new Array2DRowRealMatrix(matrixProvider.getFirstMatrix());
        RealMatrix secondMatrix = new Array2DRowRealMatrix(matrixProvider.getSecondMatrix());

        return firstMatrix.multiply(secondMatrix);
    }

    @Benchmark
    public Object la4jMatrixMultiplication(BigMatrixProvider matrixProvider) {
        Matrix firstMatrix = new Basic2DMatrix(matrixProvider.getFirstMatrix());
        Matrix secondMatrix = new Basic2DMatrix(matrixProvider.getSecondMatrix());

        return firstMatrix.multiply(secondMatrix);
    }

    @Benchmark
    public Object nd4jMatrixMultiplication(BigMatrixProvider matrixProvider) {
        INDArray firstMatrix = Nd4j.create(matrixProvider.getFirstMatrix());
        INDArray secondMatrix = Nd4j.create(matrixProvider.getSecondMatrix());

        return firstMatrix.mmul(secondMatrix);
    }

    @Benchmark
    public Object coltMatrixMultiplication(BigMatrixProvider matrixProvider) {
        DoubleFactory2D doubleFactory2D = DoubleFactory2D.dense;

        DoubleMatrix2D firstMatrix = doubleFactory2D.make(matrixProvider.getFirstMatrix());
        DoubleMatrix2D secondMatrix = doubleFactory2D.make(matrixProvider.getSecondMatrix());

        Algebra algebra = new Algebra();
        return algebra.mult(firstMatrix, secondMatrix);
    }
}
```

当我们运行这个基准测试时，我们得到了完全不同的结果：

```plaintext
Benchmark                                                              Mode  Cnt    Score    Error  Units
BigMatrixMultiplicationBenchmarking.apacheCommonsMatrixMultiplication  avgt   20  511.140 ± 13.535   s/op
BigMatrixMultiplicationBenchmarking.coltMatrixMultiplication           avgt   20  197.914 ±  2.453   s/op
BigMatrixMultiplicationBenchmarking.ejmlMatrixMultiplication           avgt   20   25.830 ±  0.059   s/op
BigMatrixMultiplicationBenchmarking.homemadeMatrixMultiplication       avgt   20  497.493 ±  2.121   s/op
BigMatrixMultiplicationBenchmarking.la4jMatrixMultiplication           avgt   20   35.523 ±  0.102   s/op
BigMatrixMultiplicationBenchmarking.nd4jMatrixMultiplication           avgt   20    0.548 ±  0.006   s/op
```

正如我们所看到的，自制实现和 Apache 库现在比以前更糟糕，需要将近 10 分钟来执行两个矩阵的乘法。

柯尔特用了 3 分钟多一点，虽然好一些，但仍然很长。EJML 和 LA4J 在将近 30 秒内运行时表现相当不错。但是，是 ND4J 在[CPU 后端](https://deeplearning4j.konduit.ai/v/en-1.0.0-beta7/config/backends/performance-issues) 上以不到一秒的速度赢得了这个基准测试。

### 4.3. 分析

这向我们表明，基准测试结果确实取决于矩阵的特性，因此很难指出一个赢家。

## 5.总结

在本文中，我们学习了如何在Java中自行或使用外部库来乘以矩阵。在探索了所有解决方案之后，我们对所有解决方案进行了基准测试，发现除了 ND4J 之外，它们在小矩阵上的表现都非常好。另一方面，在较大的矩阵上，ND4J 处于领先地位。