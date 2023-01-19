## 1. 简介

SuanShu是一个Java数学库，用于数值分析、统计、[求根](http://redmine.numericalmethod.com/projects/public/repository/show/SuanShu-20120606) 、线性代数、优化等。它提供的功能之一是实数和复数的功能。

该库有一个开源版本，也有一个需要许可证的版本——具有不同形式的许可证：学术、商业和贡献者。

请注意，下面的示例通过 pom.xml使用许可版本。开源版本目前在 Maven 存储库中不可用；许可版本需要运行许可服务器。因此，GitHub 中没有针对此包的任何测试。

## 2. Setup for SuanShu

让我们从将 Maven 依赖项添加到 pom.xml开始：

```xml
<dependencies>
    <dependency>
        <groupId>com.numericalmethod</groupId>
        <artifactId>suanshu</artifactId>
        <version>4.0.0</version>
    </dependency>
</dependencies>
<repositories>
    <repository>
        <id>nm-repo</id>
        <name>Numerical Method's Maven Repository</name>
        <url>http://repo.numericalmethod.com/maven/</url>
        <layout>default</layout>
    </repository>
</repositories>
```

## 3. 使用向量

SuanShu 库提供了 密集向量和稀疏向量的类。密集向量是大多数元素具有非零值的向量，与大多数值具有零值的稀疏向量相反。

密集向量的实现仅使用Java实数/复数数组，而稀疏向量的实现使用 Java条目数组，其中每个条目都有一个索引和一个实数/复数值。

我们可以看到当我们有一个大多数值为零的大向量时，这将如何在存储方面产生巨大差异。大多数数学库在需要支持大尺寸向量时都会使用这种方法。

让我们看看一些基本的向量运算。

### 3.1. 添加向量

使用add()方法添加 2 个向量非常简单：

```java
public void addingVectors() throws Exception {
    Vector v1 = new DenseVector(new double[] {1, 2, 3, 4, 5});
    Vector v2 = new DenseVector(new double[] {5, 4, 3, 2, 1});
    Vector v3 = v1.add(v2);
    log.info("Adding vectors: {}", v3);
}
```

我们将看到的输出是：

```shell
[6.000000, 6.000000, 6.000000, 6.000000, 6.000000]
```

我们还可以使用 add(double)方法将相同的数字添加到所有元素。

### 3.2. 缩放向量

缩放向量(即乘以常数)也很容易：

```java
public void scaleVector() throws Exception {
    Vector v1 = new DenseVector(new double[]{1, 2, 3, 4, 5});
    Vector v2 = v1.scaled(2.0);
    log.info("Scaling a vector: {}", v2);
}
```

输出：

```shell
[2.000000, 4.000000, 6.000000, 8.000000, 10.000000]
```

### 3.3. 矢量内积

计算 2 个向量的内积需要调用innerProduct(Vector)方法：

```java
public void innerProductVectors() throws Exception {
    Vector v1 = new DenseVector(new double[]{1, 2, 3, 4, 5});
    Vector v2 = new DenseVector(new double[]{5, 4, 3, 2, 1});
    double inner = v1.innerProduct(v2);
    log.info("Vector inner product: {}", inner);
}
```

### 3.4. 处理错误

该库验证我们正在操作的向量是否与我们正在执行的操作兼容。例如，将大小为 2 的向量添加到大小为 3 的向量应该是不可能的。所以下面的代码应该会导致异常：

```java
public void addingIncorrectVectors() throws Exception {
    Vector v1 = new DenseVector(new double[] {1, 2, 3});
    Vector v2 = new DenseVector(new double[] {5, 4});
    Vector v3 = v1.add(v2);
}
```

确实如此——运行这段代码会导致：

```plaintext
Exception in thread "main" com.numericalmethod.suanshu.vector.doubles.IsVector$SizeMismatch: vectors do not have the same size: 3 and 2
    at com.numericalmethod.suanshu.vector.doubles.IsVector.throwIfNotEqualSize(IsVector.java:101)
    at com.numericalmethod.suanshu.vector.doubles.dense.DenseVector.add(DenseVector.java:174)
    at com.baeldung.suanshu.SuanShuMath.addingIncorrectVectors(SuanShuMath.java:21)
    at com.baeldung.suanshu.SuanShuMath.main(SuanShuMath.java:8)
```

## 4. 使用矩阵

除了向量之外，该库还提供对矩阵运算的支持。与向量类似，矩阵支持密集和稀疏格式，以及实数和复数。

### 4.1. 添加矩阵

添加矩阵就像使用向量一样简单：

```java
public void addingMatrices() throws Exception {
    Matrix m1 = new DenseMatrix(new double[][]{
        {1, 2, 3},
        {4, 5, 6}
    });

    Matrix m2 = new DenseMatrix(new double[][]{
        {3, 2, 1},
        {6, 5, 4}
    });

    Matrix m3 = m1.add(m2);
    log.info("Adding matrices: {}", m3);
}
```

### 4.2. 乘法矩阵

数学库可用于矩阵相乘：

```java
public void multiplyMatrices() throws Exception {
    Matrix m1 = new DenseMatrix(new double[][]{
        {1, 2, 3},
        {4, 5, 6}
    });

    Matrix m2 = new DenseMatrix(new double[][]{
        {1, 4},
        {2, 5},
        {3, 6}
    });

    Matrix m3 = m1.multiply(m2);
    log.info("Multiplying matrices: {}", m3);
}
```

将 2×3 矩阵与 3×2 矩阵相乘将得到 2×2 矩阵。

为了证明库对矩阵大小进行了正确的检查，让我们尝试做一个应该会失败的乘法：

```java
public void multiplyIncorrectMatrices() throws Exception {
    Matrix m1 = new DenseMatrix(new double[][]{
        {1, 2, 3},
        {4, 5, 6}
    });

    Matrix m2 = new DenseMatrix(new double[][]{
        {3, 2, 1},
        {6, 5, 4}
    });

    Matrix m3 = m1.multiply(m2);
}
```

执行将产生以下输出。

```shell
Exception in thread "main" com.numericalmethod.suanshu.matrix.MatrixMismatchException:
    matrix with 3 columns and matrix with 2 rows cannot multiply due to mis-matched dimension
    at com.numericalmethod.suanshu.datastructure.DimensionCheck.throwIfIncompatible4Multiplication(DimensionCheck.java:164)
    at com.numericalmethod.suanshu.matrix.doubles.matrixtype.dense.DenseMatrix.multiply(DenseMatrix.java:374)
    at com.baeldung.suanshu.SuanShuMath.multiplyIncorrectMatrices(SuanShuMath.java:98)
    at com.baeldung.suanshu.SuanShuMath.main(SuanShuMath.java:22)
```

### 4.3. 计算矩阵逆

手动计算矩阵的逆可能是一个漫长的过程，但 SuanShu 数学库使它变得简单：

```java
public void inverseMatrix() {
    Matrix m1 = new DenseMatrix(new double[][]{
        {1, 2},
        {3, 4}
    });

    Inverse m2 = new Inverse(m1);
    log.info("Inverting a matrix: {}", m2);
}
```

我们可以使用 SuanShu 库来验证这一点，但将矩阵与其逆相乘：结果应该是单位矩阵。我们可以通过在上面的方法中添加以下内容来做到这一点：

```java
log.info("Verifying a matrix inverse: {}", m1.multiply(m2));
```

## 5. 求解多项式

SuanShu 提供支持的其他领域之一是多项式。它提供了评估多项式的方法，也提供了寻找其根的方法(多项式评估为 0 的输入值)。

### 5.1. 创建多项式

可以通过指定其系数来创建多项式。所以像3x 2 -5x+1这样的多项式可以用以下方法创建：

```java
public Polynomial createPolynomial() {
    return new Polynomial(new double[]{3, -5, 1});
}
```

如我们所见，我们首先从最高级别的系数开始。

### 5.2. 评估多项式

evaluate()方法可用于计算多项式。这可以针对真实和复杂的输入来完成。

```java
public void evaluatePolynomial(Polynomial p) {
    log.info("Evaluating a polynomial using a real number: {}", p.evaluate(5));
    log.info("Evaluating a polynomial using a complex number: {}", p.evaluate(new Complex(1, 2)));
}
```

我们将看到的输出是：

```shell
51.0
-13.000000+2.000000i
```

### 5.3. 求多项式的根

SuanShu 数学库使查找多项式的根变得容易。它提供了众所周知的算法来确定各种次数的多项式的根，并根据多项式的最高次数，PolyRoot 类选择最佳方法：

```java
public void solvePolynomial() {
    Polynomial p = new Polynomial(new double[]{2, 2, -4});
    PolyRootSolver solver = new PolyRoot();
    List<? extends Number> roots = solver.solve(p);
    log.info("Finding polynomial roots: {}", roots);
}
```

输出：

```shell
[-2.0, 1.0]
```

因此，为该样本多项式找到了 2 个实根：-2 和 1。当然，也支持复根。

## 六. 总结

本文只是对算数数学库的简单介绍。