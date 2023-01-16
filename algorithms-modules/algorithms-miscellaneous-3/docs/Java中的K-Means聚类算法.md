## 1. 概述

聚类是一类无监督算法的总称，用于发现彼此密切相关的事物、人或想法组。

在这个看似简单的单行定义中，我们看到了一些流行语。究竟什么是聚类？什么是无监督算法？

在本教程中，我们将首先阐明这些概念。然后，我们将了解它们如何在Java中表现出来。

## 2. 无监督算法

在我们使用大多数学习算法之前，我们应该以某种方式向它们提供一些样本数据，并允许算法从这些数据中学习。在机器学习术语中，我们称该样本数据集为训练数据。此外，整个过程称为训练过程。

无论如何，我们可以根据训练过程中所需的监督量对学习算法进行分类。 此类别中的两种主要学习算法类型是：

-   监督学习：在监督算法中，训练数据应包括每个点的实际解决方案。例如，如果我们要训练我们的垃圾邮件过滤算法，我们将样本电子邮件及其标签(即垃圾邮件或非垃圾邮件)提供给算法。从数学上讲，我们将从包含 xs 和 ys的训练集中推断出f(x) 。
-   无监督学习：当训练数据中没有标签时，该算法是无监督的。例如，我们有大量关于音乐家的数据，我们将在数据中发现相似音乐家的群体。

## 3.聚类

聚类是一种无监督算法，用于发现类似事物、想法或人的群体。与监督算法不同，我们不使用已知标签的示例来训练聚类算法。相反，聚类试图在训练集中找到没有数据点作为标签的结构。

### 3.1. K-均值聚类

K-Means 是一种聚类算法，具有一个基本属性：聚类的数量是预先定义的。除了 K-Means 之外，还有其他类型的聚类算法，如层次聚类、亲和传播或谱聚类。

### 3.2. K 均值如何工作

假设我们的目标是在数据集中找到几个相似的组，例如：

[![第一步](https://www.baeldung.com/wp-content/uploads/2019/08/Date-6.png)](https://www.baeldung.com/wp-content/uploads/2019/08/Date-6.png)

K-Means 以 k 个随机放置的质心开始。质心，顾名思义，是集群的中心点。例如，我们在这里添加四个随机质心：

[![随机质心](https://www.baeldung.com/wp-content/uploads/2019/08/Date-7.png)](https://www.baeldung.com/wp-content/uploads/2019/08/Date-7.png)

然后我们将每个现有数据点分配给它最近的质心：

[![任务](https://www.baeldung.com/wp-content/uploads/2019/08/Date-8.png)](https://www.baeldung.com/wp-content/uploads/2019/08/Date-8.png)

分配后，我们将质心移动到分配给它的点的平均位置。请记住，质心应该是簇的中心点：

[![日期 10](https://www.baeldung.com/wp-content/uploads/2019/08/Date-10.png)](https://www.baeldung.com/wp-content/uploads/2019/08/Date-10.png)

 

每次我们完成重定位质心时，当前迭代都会结束。我们重复这些迭代，直到多个连续迭代之间的分配停止变化：

[![日期副本](https://www.baeldung.com/wp-content/uploads/2019/08/Date-copy.png)](https://www.baeldung.com/wp-content/uploads/2019/08/Date-copy.png)

当算法终止时，这四个集群会按预期找到。现在我们知道了 K-Means 是如何工作的，让我们用Java来实现它。

### 3.3. 特征表示

在对不同的训练数据集进行建模时，我们需要一个数据结构来表示模型属性及其对应的值。例如，音乐家可以拥有一个 genre 属性，其值为 Rock 。我们通常使用术语特征来指代属性及其值的组合。

为了为特定的学习算法准备数据集，我们通常使用一组通用的数值属性来比较不同的项目。例如，如果我们让我们的用户用一个流派标记每个艺术家，那么在一天结束时，我们可以计算每个艺术家被标记为一个特定流派的次数：

[![屏幕截图 1398-04-29-at-22.30.58](https://www.baeldung.com/wp-content/uploads/2019/08/Screen-Shot-1398-04-29-at-22.30.58.png)](https://www.baeldung.com/wp-content/uploads/2019/08/Screen-Shot-1398-04-29-at-22.30.58.png)

像 Linkin Park 这样的艺术家的特征向量是 [rock -> 7890, nu-metal -> 700, alternative -> 520, pop -> 3]。 因此，如果我们能找到一种将属性表示为数值的方法，那么我们就可以通过比较两个不同的项目(例如艺术家)对应的向量条目来简单地比较它们。

由于数值向量是如此通用的数据结构，我们将使用它们来表示特征。 以下是我们如何在Java中实现特征向量：

```java
public class Record {
    private final String description;
    private final Map<String, Double> features;

    // constructor, getter, toString, equals and hashcode
}
```

### 3.4. 寻找相似的物品

在 K-Means 的每次迭代中，我们需要一种方法来找到最接近数据集中每个项目的质心。计算两个特征向量之间距离的最简单方法之一是使用[Euclidean Distance](https://en.wikipedia.org/wiki/Euclidean_distance)。[p1, q1]和[p2, q2]等两个向量之间的欧氏距离 等于：

[![4febdae84cbc320c19dd13eac5060a984fd438d8](https://www.baeldung.com/wp-content/uploads/2019/08/4febdae84cbc320c19dd13eac5060a984fd438d8.svg)](https://www.baeldung.com/wp-content/uploads/2019/08/4febdae84cbc320c19dd13eac5060a984fd438d8.svg)

让我们用Java实现这个功能。一、抽象：

```java
public interface Distance {
    double calculate(Map<String, Double> f1, Map<String, Double> f2);
}
```

除了欧几里德距离，还有其他方法可以计算不同项目之间的距离或相似性，例如皮尔逊[相关系数](https://www.baeldung.com/cs/correlation-coefficient)。这种抽象使得在不同距离度量之间切换变得容易。

让我们看看欧氏距离的实现：

```java
public class EuclideanDistance implements Distance {

    @Override
    public double calculate(Map<String, Double> f1, Map<String, Double> f2) {
        double sum = 0;
        for (String key : f1.keySet()) {
            Double v1 = f1.get(key);
            Double v2 = f2.get(key);

            if (v1 != null && v2 != null) {
                sum += Math.pow(v1 - v2, 2);
            }
        }

        return Math.sqrt(sum);
    }
}
```

首先，我们计算相应条目之间的平方差之和。然后，通过应用 sqrt 函数，我们计算实际的欧氏距离。

### 3.5. 质心表示

质心与普通特征位于同一空间，因此我们可以将它们表示为类似于特征：

```java
public class Centroid {

    private final Map<String, Double> coordinates;

    // constructors, getter, toString, equals and hashcode
}
```

现在我们已经有了一些必要的抽象，是时候编写我们的 K-Means 实现了。快速浏览一下我们的方法签名：

```java
public class KMeans {

    private static final Random random = new Random();

    public static Map<Centroid, List<Record>> fit(List<Record> records, 
      int k, 
      Distance distance, 
      int maxIterations) { 
        // omitted
    }
}
```

让我们分解这个方法签名：

-   数据集 是一组特征向量。由于每个特征向量都是一个Record， 那么数据集类型就是List<Record>
-   k 参数决定了聚类的 数量，我们应该提前提供
-   distance 封装了我们要计算两个特征之间差异的方式
-   当分配在几次连续迭代中停止更改时，K-Means 终止。除了这个终止条件之外，我们还可以为迭代次数设置一个上限。maxIterations 参数确定上限 
-   当 K-Means 终止时，每个质心应该有一些分配的特征，因此我们使用 Map<Centroid, List<Record>> 作为返回类型。基本上，每个映射条目对应一个集群

### 3.6. 质心生成

第一步是生成 k 个随机放置的质心。

虽然每个质心可以包含完全随机的坐标，但最好在每个属性的最小值和最大值之间生成随机坐标。在不考虑可能值范围的情况下生成随机质心会导致算法收敛得更慢。

首先，我们应该计算每个属性的最小值和最大值，然后生成每对属性之间的随机值：

```java
private static List<Centroid> randomCentroids(List<Record> records, int k) {
    List<Centroid> centroids = new ArrayList<>();
    Map<String, Double> maxs = new HashMap<>();
    Map<String, Double> mins = new HashMap<>();

    for (Record record : records) {
        record.getFeatures().forEach((key, value) -> {
            // compares the value with the current max and choose the bigger value between them
            maxs.compute(key, (k1, max) -> max == null || value > max ? value : max);

            // compare the value with the current min and choose the smaller value between them
            mins.compute(key, (k1, min) -> min == null || value < min ? value : min);
        });
    }

    Set<String> attributes = records.stream()
      .flatMap(e -> e.getFeatures().keySet().stream())
      .collect(toSet());
    for (int i = 0; i < k; i++) {
        Map<String, Double> coordinates = new HashMap<>();
        for (String attribute : attributes) {
            double max = maxs.get(attribute);
            double min = mins.get(attribute);
            coordinates.put(attribute, random.nextDouble()  (max - min) + min);
        }

        centroids.add(new Centroid(coordinates));
    }

    return centroids;
}
```

现在，我们可以将每条记录分配给这些随机质心之一。

### 3.7. 任务

首先，给定一个 Record，我们应该找到离它最近的质心：

```java
private static Centroid nearestCentroid(Record record, List<Centroid> centroids, Distance distance) {
    double minimumDistance = Double.MAX_VALUE;
    Centroid nearest = null;

    for (Centroid centroid : centroids) {
        double currentDistance = distance.calculate(record.getFeatures(), centroid.getCoordinates());

        if (currentDistance < minimumDistance) {
            minimumDistance = currentDistance;
            nearest = centroid;
        }
    }

    return nearest;
}
```

每条记录都属于它最近的质心簇：

```java
private static void assignToCluster(Map<Centroid, List<Record>> clusters,  
  Record record, 
  Centroid centroid) {
    clusters.compute(centroid, (key, list) -> {
        if (list == null) {
            list = new ArrayList<>();
        }

        list.add(record);
        return list;
    });
}
```

### 3.8. 质心迁移

如果在一次迭代后质心不包含任何分配，那么我们将不会重新定位它。否则，我们应该将每个属性的质心坐标重新定位到所有已分配记录的平均位置：

```java
private static Centroid average(Centroid centroid, List<Record> records) {
    if (records == null || records.isEmpty()) { 
        return centroid;
    }

    Map<String, Double> average = centroid.getCoordinates();
    records.stream().flatMap(e -> e.getFeatures().keySet().stream())
      .forEach(k -> average.put(k, 0.0));
        
    for (Record record : records) {
        record.getFeatures().forEach(
          (k, v) -> average.compute(k, (k1, currentValue) -> v + currentValue)
        );
    }

    average.forEach((k, v) -> average.put(k, v / records.size()));

    return new Centroid(average);
}
```

由于我们可以重新定位单个质心，现在可以实现 relocateCentroids 方法：

```java
private static List<Centroid> relocateCentroids(Map<Centroid, List<Record>> clusters) {
    return clusters.entrySet().stream().map(e -> average(e.getKey(), e.getValue())).collect(toList());
}
```

这个简单的单行代码遍历所有质心，重新定位它们，并返回新的质心。

### 3.9. 把它们放在一起

在每次迭代中，在将所有记录分配到最近的质心之后，首先，我们应该将当前分配与上一次迭代进行比较。

如果分配相同，则算法终止。否则，在跳转到下一次迭代之前，我们应该重新定位质心：

```java
public static Map<Centroid, List<Record>> fit(List<Record> records, 
  int k, 
  Distance distance, 
  int maxIterations) {

    List<Centroid> centroids = randomCentroids(records, k);
    Map<Centroid, List<Record>> clusters = new HashMap<>();
    Map<Centroid, List<Record>> lastState = new HashMap<>();

    // iterate for a pre-defined number of times
    for (int i = 0; i < maxIterations; i++) {
        boolean isLastIteration = i == maxIterations - 1;

        // in each iteration we should find the nearest centroid for each record
        for (Record record : records) {
            Centroid centroid = nearestCentroid(record, centroids, distance);
            assignToCluster(clusters, record, centroid);
        }

        // if the assignments do not change, then the algorithm terminates
        boolean shouldTerminate = isLastIteration || clusters.equals(lastState);
        lastState = clusters;
        if (shouldTerminate) { 
            break; 
        }

        // at the end of each iteration we should relocate the centroids
        centroids = relocateCentroids(clusters);
        clusters = new HashMap<>();
    }

    return lastState;
}
```

## 4. 示例：在 Last.fm 上发现相似的艺术家

Last.fm 通过记录用户收听内容的详细信息来构建每个用户音乐品味的详细档案。在本节中，我们将找到相似艺术家的集群。为了构建适合此任务的数据集，我们将使用 Last.fm 中的三个 API：

1.  获取Last.fm[上顶级艺术家集合的API。](https://www.last.fm/api/show/chart.getTopArtists)
2.  另一个用于查找[流行标签](https://www.last.fm/api/show/chart.getTopTags)的 API 。每个用户都可以用某物来标记艺术家，例如 摇滚。 因此，Last.fm 维护着一个包含这些标签及其频率的数据库。
3.  [以及一个获取艺术家热门标签](https://www.last.fm/api/show/artist.getTopTags)的 API ，按受欢迎程度排序。由于有很多这样的标签，我们将只保留那些位于顶级全局标签中的标签。

### 4.1. Last.fm 的 API

[要使用这些 API，我们应该从 Last.fm](https://www.last.fm/api/authentication)获取API 密钥并在每个 HTTP 请求中发送它。我们将使用以下[Retrofit](https://www.baeldung.com/retrofit)服务来调用这些 API：

```java
public interface LastFmService {

    @GET("/2.0/?method=chart.gettopartists&format=json&limit=50")
    Call<Artists> topArtists(@Query("page") int page);

    @GET("/2.0/?method=artist.gettoptags&format=json&limit=20&autocorrect=1")
    Call<Tags> topTagsFor(@Query("artist") String artist);

    @GET("/2.0/?method=chart.gettoptags&format=json&limit=100")
    Call<TopTags> topTags();

    // A few DTOs and one interceptor
}
```

那么，让我们在 Last.fm 上找到最受欢迎的艺术家：

```java
// setting up the Retrofit service

private static List<String> getTop100Artists() throws IOException {
    List<String> artists = new ArrayList<>();
    // Fetching the first two pages, each containing 50 records.
    for (int i = 1; i <= 2; i++) {
        artists.addAll(lastFm.topArtists(i).execute().body().all());
    }

    return artists;
}
```

同样，我们可以获取 top 标签：

```java
private static Set<String> getTop100Tags() throws IOException {
    return lastFm.topTags().execute().body().all();
}
```

最后，我们可以构建一个艺术家数据集以及他们的标签频率：

```java
private static List<Record> datasetWithTaggedArtists(List<String> artists, 
  Set<String> topTags) throws IOException {
    List<Record> records = new ArrayList<>();
    for (String artist : artists) {
        Map<String, Double> tags = lastFm.topTagsFor(artist).execute().body().all();
            
        // Only keep popular tags.
        tags.entrySet().removeIf(e -> !topTags.contains(e.getKey()));

        records.add(new Record(artist, tags));
    }

    return records;
}
```

### 4.2. 形成艺术家集群

现在，我们可以将准备好的数据集提供给我们的 K-Means 实现：

```java
List<String> artists = getTop100Artists();
Set<String> topTags = getTop100Tags();
List<Record> records = datasetWithTaggedArtists(artists, topTags);

Map<Centroid, List<Record>> clusters = KMeans.fit(records, 7, new EuclideanDistance(), 1000);
// Printing the cluster configuration
clusters.forEach((key, value) -> {
    System.out.println("-------------------------- CLUSTER ----------------------------");

    // Sorting the coordinates to see the most significant tags first.
    System.out.println(sortedCentroid(key)); 
    String members = String.join(", ", value.stream().map(Record::getDescription).collect(toSet()));
    System.out.print(members);

    System.out.println();
    System.out.println();
});
```

如果我们运行这段代码，那么它会将集群可视化为文本输出：

```plaintext
------------------------------ CLUSTER -----------------------------------
Centroid {classic rock=65.58333333333333, rock=64.41666666666667, british=20.333333333333332, ... }
David Bowie, Led Zeppelin, Pink Floyd, System of a Down, Queen, blink-182, The Rolling Stones, Metallica, 
Fleetwood Mac, The Beatles, Elton John, The Clash

------------------------------ CLUSTER -----------------------------------
Centroid {Hip-Hop=97.21428571428571, rap=64.85714285714286, hip hop=29.285714285714285, ... }
Kanye West, Post Malone, Childish Gambino, Lil Nas X, A$AP Rocky, Lizzo, xxxtentacion, 
Travi$ Scott, Tyler, the Creator, Eminem, Frank Ocean, Kendrick Lamar, Nicki Minaj, Drake

------------------------------ CLUSTER -----------------------------------
Centroid {indie rock=54.0, rock=52.0, Psychedelic Rock=51.0, psychedelic=47.0, ... }
Tame Impala, The Black Keys

------------------------------ CLUSTER -----------------------------------
Centroid {pop=81.96428571428571, female vocalists=41.285714285714285, indie=22.785714285714285, ... }
Ed Sheeran, Taylor Swift, Rihanna, Miley Cyrus, Billie Eilish, Lorde, Ellie Goulding, Bruno Mars, 
Katy Perry, Khalid, Ariana Grande, Bon Iver, Dua Lipa, Beyoncé, Sia, P!nk, Sam Smith, Shawn Mendes, 
Mark Ronson, Michael Jackson, Halsey, Lana Del Rey, Carly Rae Jepsen, Britney Spears, Madonna, 
Adele, Lady Gaga, Jonas Brothers

------------------------------ CLUSTER -----------------------------------
Centroid {indie=95.23076923076923, alternative=70.61538461538461, indie rock=64.46153846153847, ... }
Twenty One Pilots, The Smiths, Florence + the Machine, Two Door Cinema Club, The 1975, Imagine Dragons, 
The Killers, Vampire Weekend, Foster the People, The Strokes, Cage the Elephant, Arcade Fire, 
Arctic Monkeys

------------------------------ CLUSTER -----------------------------------
Centroid {electronic=91.6923076923077, House=39.46153846153846, dance=38.0, ... }
Charli XCX, The Weeknd, Daft Punk, Calvin Harris, MGMT, Martin Garrix, Depeche Mode, The Chainsmokers, 
Avicii, Kygo, Marshmello, David Guetta, Major Lazer

------------------------------ CLUSTER -----------------------------------
Centroid {rock=87.38888888888889, alternative=72.11111111111111, alternative rock=49.16666666, ... }
Weezer, The White Stripes, Nirvana, Foo Fighters, Maroon 5, Oasis, Panic! at the Disco, Gorillaz, 
Green Day, The Cure, Fall Out Boy, OneRepublic, Paramore, Coldplay, Radiohead, Linkin Park, 
Red Hot Chili Peppers, Muse
```

由于质心坐标按平均标签频率排序，我们可以很容易地发现每个集群中的主要类型。例如，最后一个集群是一群优秀的老摇滚乐队，或者第二个集群是充满说唱明星的集群。

虽然这种聚类很有意义，但在大多数情况下，它并不完美，因为数据只是从用户行为中收集的。

## 5.可视化

片刻之前，我们的算法以终端友好的方式可视化了艺术家集群。如果我们将集群配置转换为 JSON 并将其提供给 D3.js，然后使用几行 JavaScript，我们将拥有一个很好的人性化[Radial Tidy-Tree](https://observablehq.com/@d3/radial-tidy-tree?collection=@d3/d3-hierarchy)：

[![屏幕截图 1398-05-04-at-12.09.40](https://www.baeldung.com/wp-content/uploads/2019/08/Screen-Shot-1398-05-04-at-12.09.40.png)](https://www.baeldung.com/wp-content/uploads/2019/08/Screen-Shot-1398-05-04-at-12.09.40.png)

我们必须将我们的 Map<Centroid, List<Record>>转换为具有类似于[此 d3.js 示例](https://raw.githubusercontent.com/d3/d3-hierarchy/v1.1.8/test/data/flare.json)的模式的 JSON 。

## 6.簇数

K-Means 的基本属性之一是我们应该提前定义集群的数量。到目前为止，我们对k使用了一个静态值，但确定这个值可能是一个具有挑战性的问题。有两种常见的计算簇数的方法：

1.  领域知识
2.  数学启发式

如果我们足够幸运，我们对域了解得如此之多，那么我们也许能够简单地猜出正确的数字。否则，我们可以应用一些启发式方法，如 Elbow Method 或 Silhouette Method 来了解集群的数量。

在继续之前，我们应该知道这些启发式方法虽然有用，但只是启发式方法，可能无法提供明确的答案。

### 6.1. 肘法

要使用肘部法，我们应该首先计算每个聚类质心与其所有成员之间的差异。当我们将更多不相关的成员分组到一个集群中时，质心与其成员之间的距离会增加，因此集群质量会降低。

执行此距离计算的一种方法是使用 Sum of Squared Errors 。 误差平方和或 SSE 等于质心与其所有成员之间的差平方和：

```java
public static double sse(Map<Centroid, List<Record>> clustered, Distance distance) {
    double sum = 0;
    for (Map.Entry<Centroid, List<Record>> entry : clustered.entrySet()) {
        Centroid centroid = entry.getKey();
        for (Record record : entry.getValue()) {
            double d = distance.calculate(centroid.getCoordinates(), record.getFeatures());
            sum += Math.pow(d, 2);
        }
    }
        
    return sum;
}
```

然后，我们可以针对不同的k值运行 K-Means 算法， 并计算每个值的 SSE：

```java
List<Record> records = // the dataset;
Distance distance = new EuclideanDistance();
List<Double> sumOfSquaredErrors = new ArrayList<>();
for (int k = 2; k <= 16; k++) {
    Map<Centroid, List<Record>> clusters = KMeans.fit(records, k, distance, 1000);
    double sse = Errors.sse(clusters, distance);
    sumOfSquaredErrors.add(sse);
}
```

归根结底，可以通过根据 SSE 绘制集群数量来找到合适的k ：

[![屏幕截图 1398-05-04-at-17.01.36](https://www.baeldung.com/wp-content/uploads/2019/08/Screen-Shot-1398-05-04-at-17.01.36.png)](https://www.baeldung.com/wp-content/uploads/2019/08/Screen-Shot-1398-05-04-at-17.01.36.png)

通常，随着集群数量的增加，集群成员之间的距离会减小。但是，我们不能为k 选择任意大的值 ，因为只有一个成员的多个集群会破坏集群的整个目的。

肘部方法背后的想法是找到一个合适的 k 值，使 SSE 在该值附近急剧下降。例如， k=9 在这里可能是一个很好的候选者。

## 七. 总结

在本教程中，首先，我们介绍了机器学习中的一些重要概念。然后我们熟悉了 K-Means 聚类算法的机制。最后，我们为 K-Means 编写了一个简单的实现，使用来自 Last.fm 的真实数据集测试了我们的算法，并以漂亮的图形方式可视化了聚类结果。