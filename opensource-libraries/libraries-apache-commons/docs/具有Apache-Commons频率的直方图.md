## 1. 概述

在本教程中，我们将了解如何借助 Apache Commons [Frequency](https://commons.apache.org/proper/commons-math/userguide/stat.html#a1.3_Frequency_distributions)类在直方图上呈现数据。

Frequency 类是[本文](https://www.baeldung.com/apache-commons-math)探讨的 Apache Commons Math 库的一部分。

直方图是连接条形图，显示数据集中一系列数据的出现。它与条形图的不同之处在于它用于显示连续的定量变量的分布，而条形图用于显示分类数据。

## 2.项目依赖

在本文中，我们将使用具有以下依赖项的 Maven 项目：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-math3</artifactId>
    <version>3.6.1</version>
</dependency>
<dependency>
    <groupId>org.knowm.xchart</groupId>
    <artifactId>xchart</artifactId>
    <version>3.8.2</version>
</dependency>
```

commons-math3库 包含 Frequency 类，我们将使用它来确定数据集中变量的出现。我们将使用 xchart 库在 GUI 中显示直方图 。

 可以在 Maven Central 上找到最新版本的[commons-math3](https://search.maven.org/classic/#search|ga|1|a%3A"commons-math3" g%3A"org.apache.commons")和[xchart 。](https://search.maven.org/classic/#search|ga|1|a%3A"xchart" g%3A"org.knowm.xchart")

## 3.计算变量的频率

对于本教程，我们将使用包含特定学校学生年龄的数据集。我们希望看到不同年龄组的频率，并观察它们在直方图上的分布。

让我们用 List 集合表示数据集，并使用它来填充 Frequency 类的实例：

```java
List<Integer> datasetList = Arrays.asList(
  36, 25, 38, 46, 55, 68, 
  72, 55, 36, 38, 67, 45, 22, 
  48, 91, 46, 52, 61, 58, 55);
Frequency frequency = new Frequency();
datasetList.forEach(d -> frequency.addValue(Double.parseDouble(d.toString())));
```

现在我们已经填充了 Frequency 类的实例，我们将获取 bin 中每个年龄的计数并将其相加，以便我们可以获得特定年龄组中年龄的总频率：

```java
datasetList.stream()
  .map(d -> Double.parseDouble(d.toString()))
  .distinct()
  .forEach(observation -> {
      long observationFrequency = frequency.getCount(observation);
      int upperBoundary = (observation > classWidth)
        ? Math.multiplyExact( (int) Math.ceil(observation / classWidth), classWidth)
        : classWidth;
      int lowerBoundary = (upperBoundary > classWidth)
        ? Math.subtractExact(upperBoundary, classWidth)
        : 0;
      String bin = lowerBoundary + "-" + upperBoundary;

      updateDistributionMap(lowerBoundary, bin, observationFrequency);
  });
```

从上面的代码片段中，我们首先 使用 Frequency类的getCount()确定观察的 频率。该方法返回观察出现的总次数。 

使用当前观察，我们通过计算其相对于类宽度(即 10 )的上限和下限来动态确定它所属的组。

上边界和下边界连接起来形成一个 bin，它 使用updateDistributionMap()与observationFrequency一起存储在distributionMap中。

如果 bin已经存在，我们更新频率，否则我们将其添加为键并将当前观察的频率设置为它的值。请注意，我们跟踪处理过的观察结果以避免重复。

Frequency类还具有确定数据集中变量的百分比和累积百分比的方法。 

## 4. 绘制直方图

现在我们已经将原始数据集处理成年龄组及其各自频率的地图，我们可以使用 xchart库在直方图图表中显示数据：

```java
CategoryChart chart = new CategoryChartBuilder().width(800).height(600)
  .title("Age Distribution")
  .xAxisTitle("Age Group")
  .yAxisTitle("Frequency")
  .build();

chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
chart.getStyler().setAvailableSpaceFill(0.99);
chart.getStyler().setOverlapped(true);

List yData = new ArrayList();
yData.addAll(distributionMap.values());
List xData = Arrays.asList(distributionMap.keySet().toArray());
chart.addSeries("age group", xData, yData);

new SwingWrapper<>(chart).displayChart();
```

我们使用图表生成器创建了 CategoryChart的实例，然后我们对其进行配置并使用 x 轴和 y 轴的数据填充它。

我们最终使用 SwingWrapper 在 GUI 中显示图表：

[![xchart直方图](https://www.baeldung.com/wp-content/uploads/2018/06/22112_xchart_histogram-300x226.png)](https://www.baeldung.com/wp-content/uploads/2018/06/22112_xchart_histogram.png)

从上面的直方图中，我们可以看到80-90岁的学生没有，而50-60岁的学生占主导地位。这很可能是博士生或博士后学生。

我们也可以说直方图服从正态分布。

## 5.总结

在本文中，我们研究了如何利用 Apache commons-math3库的Frequency 类的强大功能。

库中还有其他有趣的统计、几何、遗传算法等类。它的文档可以在[这里](https://commons.apache.org/proper/commons-math/userguide/index.html)找到。