## 1. 概述

在本文中，我们将了解如何在Java中计算二次方程的解。我们将从定义什么是二次方程开始，然后我们将计算它的解，无论我们是在实数系统还是在复数系统中工作。

## 2. 二次方程的解

给定实数 a ≠ 0、b 和 c，让我们考虑以下二次方程：ax² + bx + c = 0。

### 2.1. 多项式的根

该方程的解也称为多项式ax² + bx + c的根。因此，让我们定义一个Polynom类。如果a系数等于 0 ，我们将抛出[IllegalArgumentException ：](https://www.baeldung.com/java-exceptions)

```java
public class Polynom {

    private double a;
    private double b;
    private double c;

    public Polynom(double a, double b, double c) {
        if (a==0) {
            throw new IllegalArgumentException("a can not be equal to 0");
        }
        this.a = a;
        this.b = b;
        this.c = c;
    }

    // getters and setters
}
```

我们将在实数系统中求解这个方程：为此，我们将寻找一些双解。

### 2.2. 复数系统

我们还将展示如何在复数系统中求解此方程。Java 中没有复数的默认表示，因此我们将创建自己的复数。让我们给它一个[静态](https://www.baeldung.com/java-static)方法ofReal来轻松转换实数。这将有助于以下步骤：

```java
public class Complex {

    private double realPart;
    private double imaginaryPart;

    public Complex(double realPart, double imaginaryPart) {
        this.realPart = realPart;
        this.imaginaryPart = imaginaryPart;
    }

    public static Complex ofReal(double realPart) {
        return new Complex(realPart, 0);
    }

    // getters and setters
}
```

## 3.计算判别式

量 Δ = b² – 4ac 称为二次方程的判别式。要在java中计算b平方，我们有两种解决方案：

-   b 乘以它自己
-   使用[Math.pow](https://www.baeldung.com/java-math-pow)将其提高到 2 的幂

让我们坚持使用第一种方法，并向Polynom类添加一个getDiscriminant方法：

```java
public double getDiscriminant() {
    return bb - 4ac;
}
```

## 4. 获取解决方案

根据判别式的值，我们能够知道存在多少解并计算它们。

### 4.1. 严格正判别式

如果判别式严格为正，则方程有两个实数解，(-b – √Δ) / 2a 和 (-b + √Δ) / 2a：

```java
Double solution1 = (-polynom.getB() - Math.sqrt(polynom.getDiscriminant())) / (2  polynom.getA());
Double solution2 = (-polynom.getB() + Math.sqrt(polynom.getDiscriminant())) / (2  polynom.getA());
```

如果我们在复数系统中工作，那么我们只需要进行转换：

```java
Complex solution1 = Complex.ofReal((-polynom.getB() - Math.sqrt(polynom.getDiscriminant())) / (2  polynom.getA()));
Complex solution2 = Complex.ofReal((-polynom.getB() + Math.sqrt(polynom.getDiscriminant())) / (2  polynom.getA()));
```

### 4.2. 判别式等于零

如果判别式为零，则方程有唯一实解 -b / 2a：

```java
Double solution = (double) -polynom.getB() / (2  polynom.getA());
```

同样，如果我们在复数系统中工作，我们将按以下方式转换解决方案：

```java
Complex solution = Complex.ofReal(-polynom.getB() / (2  polynom.getA()));
```

### 4.3. 严格负判别式

如果判别式严格为负，则方程在实数系中无解。然而，它可以在复数系统中解决：解决方案是(-b – i√-Δ)/ 2a及其共轭(-b + i√-Δ)/ 2a：

```java
Complex solution1 = new Complex(-polynom.getB() / (2 polynom.getA()), -Math.sqrt(-polynom.getDiscriminant()) / 2 polynom.getA());
Complex solution2 = new Complex(-polynom.getB() / (2 polynom.getA()), Math.sqrt(-polynom.getDiscriminant()) / 2 polynom.getA());
```

### 4.4. 收集结果

总而言之，让我们构建一个方法，当方程的解存在时，用它填充一个[列表。](https://www.baeldung.com/java-collections)在实数系统中，此方法如下所示：

```java
public static List<Double> getPolynomRoots(Polynom polynom) {
    List<Double> roots = new ArrayList<>();
    double discriminant = polynom.getDiscriminant();
    if (discriminant > 0) {
        roots.add((-polynom.getB() - Math.sqrt(discriminant)) / (2  polynom.getA()));
        roots.add((-polynom.getB() + Math.sqrt(discriminant)) / (2  polynom.getA()));
    } else if (discriminant == 0) {
        roots.add(-polynom.getB() / (2  polynom.getA()));
    }
    return roots;
}
```

如果我们在复杂的数字系统中工作，我们宁愿写：

```java
public static List<Complex> getPolynomRoots(Polynom polynom) {
    List<Complex> roots = new ArrayList<>();
    double discriminant = polynom.getDiscriminant();
    if (discriminant > 0) {
        roots.add(Complex.ofReal((-polynom.getB() - Math.sqrt(discriminant)) / (2  polynom.getA())));
        roots.add(Complex.ofReal((-polynom.getB() + Math.sqrt(discriminant)) / (2  polynom.getA())));
    } else if (discriminant == 0) {
        roots.add(Complex.ofReal(-polynom.getB() / (2  polynom.getA())));
    } else {
        roots.add(new Complex(-polynom.getB() / (2 polynom.getA()), -Math.sqrt(-discriminant) / 2 polynom.getA()));
        roots.add(new Complex(-polynom.getB() / (2 polynom.getA()), Math.sqrt(-discriminant) / 2 polynom.getA()));
    }
    return roots;
}
```

## 5.总结

在本教程中，我们了解了如何在Java中求解二次方程，无论我们处理的是实数还是复数。