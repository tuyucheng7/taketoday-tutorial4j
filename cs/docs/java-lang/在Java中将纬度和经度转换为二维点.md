## 1. 概述

在实现使用地图的应用程序时，我们通常会遇到坐标转换的问题。很多时候，我们需要将经纬度转换成二维点来显示。幸运的是，要解决这个问题，我们可以利用墨卡托投影的公式。

在本教程中，我们将介绍墨卡托投影并学习如何实现它的两个变体。

## 2.墨卡托投影

墨[卡托投影](https://en.wikipedia.org/wiki/Mercator_projection)是佛兰芒制图师杰拉尔杜斯·墨卡托 (Gerardus Mercator) 于 1569 年引入的一种地图投影。地图投影将地球上的经纬度坐标转换为平面上的一个点。换句话说，它将地球表面的一个点平移到平面地图上的一个点。

有两种方法可以实现墨卡托投影。伪墨卡托投影将地球视为一个球体。真正的墨卡托投影将地球建模为椭圆体。我们将实现这两个版本。

让我们从两个墨卡托投影实现的基类开始：

```java
abstract class Mercator {
    final static double RADIUS_MAJOR = 6378137.0;
    final static double RADIUS_MINOR = 6356752.3142;

    abstract double yAxisProjection(double input);
    abstract double xAxisProjection(double input);
}
```

此类还提供以米为单位测量的地球的主要和次要半径。众所周知，地球不完全是一个球体。因此，我们需要两个半径。首先， 大半径是地球中心到赤道的距离。其次， 小半径是地球中心到北极和南极的距离。

### 2.1. 球面墨卡托投影

伪投影模型将地球视为一个球体。与地球将被投影到更准确的形状的椭圆投影相反。这种方法使我们能够快速估计更精确但计算量更大的椭圆投影。因此，此投影中距离的直接测量值将是近似值。

此外，地图上形状的比例将略有变化。由于纬度和地图上物体形状的比例，如国家、湖泊、河流等，没有精确保存。

这也称为 [Web 墨卡托](https://en.wikipedia.org/wiki/Web_Mercator_projection) 投影——通常用于包括 Google 地图在内的 Web 应用程序。

让我们实现这种方法：

```java
public class SphericalMercator extends Mercator {

    @Override
    double xAxisProjection(double input) {
        return Math.toRadians(input)  RADIUS_MAJOR;
    }

    @Override
    double yAxisProjection(double input) {
        return Math.log(Math.tan(Math.PI / 4 + Math.toRadians(input) / 2))  RADIUS_MAJOR;
    }
}
```

关于这种方法首先要注意的是，这种方法用一个常数表示地球的半径，而不是实际的两个。其次，我们可以看到我们已经实现了两个用于转换为x 轴投影和y 轴投影的函数。在上面的类中，我们使用了 java 提供的Math库来帮助我们简化代码。

让我们测试一个简单的转换：

```java
Assert.assertEquals(2449028.7974520186, sphericalMercator.xAxisProjection(22));
Assert.assertEquals(5465442.183322753, sphericalMercator.yAxisProjection(44));
```

值得注意的是，此投影会将点映射到 (-20037508.34, -23810769.32, 20037508.34, 23810769.32) 的边界框(左、下、右、上)。

### 2 .2。椭圆墨卡托投影

真实投影将地球建模为椭圆体。该投影给出 了地球上任何地方物体的准确比例 。当然，它尊重地图上的对象，但不是 100% 准确。然而，这种方法不是最常用的，因为它在计算上很复杂。

让我们实现这种方法：

```java
class EllipticalMercator extends Mercator {
    @Override
    double yAxisProjection(double input) {

        input = Math.min(Math.max(input, -89.5), 89.5);
        double earthDimensionalRateNormalized = 1.0 - Math.pow(RADIUS_MINOR / RADIUS_MAJOR, 2);

        double inputOnEarthProj = Math.sqrt(earthDimensionalRateNormalized)  
          Math.sin( Math.toRadians(input));

        inputOnEarthProj = Math.pow(((1.0 - inputOnEarthProj) / (1.0+inputOnEarthProj)), 
          0.5  Math.sqrt(earthDimensionalRateNormalized));
        
        double inputOnEarthProjNormalized = 
          Math.tan(0.5  ((Math.PI  0.5) - Math.toRadians(input))) / inputOnEarthProj;
        
        return (-1)  RADIUS_MAJOR  Math.log(inputOnEarthProjNormalized);
    }

    @Override
    double xAxisProjection(double input) {
        return RADIUS_MAJOR  Math.toRadians(input);
    }
}
```

从上面我们可以看出这种方法在 y 轴上的投影有多复杂。这是因为它应该考虑非圆形地球形状。虽然真正的墨卡托方法看起来很复杂，但比球面方法更准确，因为它使用半径来表示地球一小一大。

让我们测试一个简单的转换：

```java
Assert.assertEquals(2449028.7974520186, ellipticalMercator.xAxisProjection(22));
Assert.assertEquals(5435749.887511954, ellipticalMercator.yAxisProjection(44));
```

此投影会将点映射到 (-20037508.34, -34619289.37, 20037508.34, 34619289.37) 的边界框。

## 3 . 总结

如果我们需要将纬度和经度坐标转换到二维表面上，我们可以使用墨卡托投影。根据我们实施所需的精度，我们可以使用球形或椭圆形方法。