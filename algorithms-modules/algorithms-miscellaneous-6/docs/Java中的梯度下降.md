## 1. 简介

在本教程中，我们将学习[梯度下降](https://www.baeldung.com/cs/understanding-gradient-descent)算法。我们将用Java实现该算法并逐步说明它。

## 2.什么是梯度下降？

梯度下降是一种优化算法，用于查找给定函数的局部最小值。它广泛用于高级机器学习算法中，以最大限度地减少损失函数。

Gradient 是坡度的另一种说法，descent 是向下的意思。顾名思义，梯度下降沿着函数的斜率下降，直到到达终点。

## 3.梯度下降的性质

梯度下降找到一个局部最小值，它可以不同于全局最小值。起始局部点作为算法的参数给出。

这是一种迭代算法，在每一步中，它都会尝试沿着斜坡向下移动并更接近局部最小值。

实际上，该算法是回溯的。我们将在本教程中说明并实现回溯梯度下降。

## 4. 分步说明

梯度下降需要一个函数和一个起点作为输入。让我们定义并绘制一个函数：

![公式](https://www.baeldung.com/wp-content/uploads/2020/03/formula.jpg)![GD1](https://www.baeldung.com/wp-content/uploads/2020/03/GD1.jpg)

我们可以从任何想要的点开始。让我们从x =1 开始：

![GD2](https://www.baeldung.com/wp-content/uploads/2020/03/GD2.jpg)

在第一步中，梯度下降以预定义的步长沿着斜坡下降：

![GD3](https://www.baeldung.com/wp-content/uploads/2020/03/GD3.jpg)

接下来，它以相同的步长更进一步。然而，这次它最终的y比最后一步更大：

![GD4](https://www.baeldung.com/wp-content/uploads/2020/03/GD4.jpg)

这表明该算法已通过局部最小值，因此它以较小的步长向后退：

![GD5](https://www.baeldung.com/wp-content/uploads/2020/03/GD5.jpg)

随后，只要当前y大于前一个y，步长就会降低并取反。迭代继续进行，直到达到所需的精度。

正如我们所看到的，梯度下降在这里找到了一个局部最小值，但它不是全局最小值。如果我们从x =-1 而不是x =1 开始，就会找到全局最小值。

## 5.Java实现

有几种方法可以实现梯度下降。这里我们不计算函数的导数来找到斜率的方向，所以我们的实现也适用于不可微函数。

让我们定义precision和stepCoefficient并给它们初始值：

```java
double precision = 0.000001;
double stepCoefficient = 0.1;
```

在第一步中，我们没有用于比较的前一个y 。我们可以增加或减少x的值以查看y 是降低还是升高。正的stepCoefficient意味着我们正在增加x的值。

现在让我们执行第一步：

```java
double previousX = initialX;
double previousY = f.apply(previousX);
currentX += stepCoefficient  previousY;
```

在上面的代码中，f是一个Function<Double, Double>，initialX是一个double，两者都作为输入提供。

另一个需要考虑的关键点是梯度下降不能保证收敛。为了避免陷入循环，让我们限制迭代次数：

```java
int iter = 100;
```

稍后，我们将在每次迭代时将iter减 1。因此，我们将在最多 100 次迭代时退出循环。

现在我们有了一个previousX，我们可以设置我们的循环：

```java
while (previousStep > precision && iter > 0) {
    iter--;
    double currentY = f.apply(currentX);
    if (currentY > previousY) {
        stepCoefficient = -stepCoefficient/2;
    }
    previousX = currentX;
    currentX += stepCoefficient  previousY;
    previousY = currentY;
    previousStep = StrictMath.abs(currentX - previousX);
}
```

在每次迭代中，我们计算新的y并将其与之前的y进行比较。如果currentY大于previousY，我们改变方向并减小步长。

循环继续，直到我们的步长小于所需的精度。最后，我们可以返回currentX作为局部最小值：

```java
return currentX;
```

## 六. 总结

在本文中，我们逐步介绍了梯度下降算法。