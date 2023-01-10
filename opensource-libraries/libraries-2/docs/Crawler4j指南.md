## 1. 简介

每次我们使用我们最喜欢的搜索引擎时，我们都会看到正在使用网络爬虫。它们还常用于从网站上抓取和分析数据。

在本教程中，我们将学习如何使用[crawler4j](https://github.com/yasserg/crawler4j)来设置和运行我们自己的网络爬虫。crawler4j 是一个开源的Java项目，可以让我们轻松地做到这一点。

## 2.设置

让我们使用[Maven Central](https://search.maven.org/search?q=a:crawler4j AND g:edu.uci.ics)查找最新版本并引入 Maven 依赖项：

```xml
<dependency>
    <groupId>edu.uci.ics</groupId>
    <artifactId>crawler4j</artifactId>
    <version>4.4.0</version>
</dependency>
```

## 3. 创建爬虫

### 3.1. 简单的 HTML 爬虫

我们将首先创建一个基本的爬虫程序，用于爬取[https://baeldung.com](https://baeldung.com/)上的 HTML 页面。

让我们通过在我们的爬虫类中扩展WebCrawler并定义一个模式来排除某些文件类型来创建我们的爬虫：

```java
public class HtmlCrawler extends WebCrawler {

    private final static Pattern EXCLUSIONS
      = Pattern.compile(".(.(css|js|xml|gif|jpg|png|mp3|mp4|zip|gz|pdf))$");

    // more code
}
```

在每个爬虫类中，我们必须重写并实现两个方法： shouldVisit和visit。

现在让我们使用我们创建的EXCLUSIONS模式创建我们的shouldVisit方法：

```java
@Override
public boolean shouldVisit(Page referringPage, WebURL url) {
    String urlString = url.getURL().toLowerCase();
    return !EXCLUSIONS.matcher(urlString).matches() 
      && urlString.startsWith("https://www.baeldung.com/");
}
```

然后，我们可以在visit方法中对访问过的页面进行我们的处理：

```java
@Override
public void visit(Page page) {
    String url = page.getWebURL().getURL();

    if (page.getParseData() instanceof HtmlParseData) {
        HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
        String title = htmlParseData.getTitle();
        String text = htmlParseData.getText();
        String html = htmlParseData.getHtml();
        Set<WebURL> links = htmlParseData.getOutgoingUrls();

        // do something with the collected data
    }
}
```

编写爬虫后，我们需要配置并运行它：

```java
File crawlStorage = new File("src/test/resources/crawler4j");
CrawlConfig config = new CrawlConfig();
config.setCrawlStorageFolder(crawlStorage.getAbsolutePath());

int numCrawlers = 12;

PageFetcher pageFetcher = new PageFetcher(config);
RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
RobotstxtServer robotstxtServer= new RobotstxtServer(robotstxtConfig, pageFetcher);
CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

controller.addSeed("https://www.baeldung.com/");

CrawlController.WebCrawlerFactory<HtmlCrawler> factory = HtmlCrawler::new;

controller.start(factory, numCrawlers);
```

我们配置了一个临时存储目录，指定了爬行线程数，并为爬虫设置了一个起始 URL。

我们还应该注意CrawlController.start()方法是一个阻塞操作。该调用之后的任何代码只会在爬虫完成运行后执行。

### 3.2. 图片爬虫

默认情况下，crawler4j 不爬取二进制数据。在下一个示例中，我们将启用该功能并抓取 Baeldung 上的所有 JPEG。

让我们首先使用一个构造函数定义ImageCrawler类，该构造函数采用一个目录来保存图像：

```java
public class ImageCrawler extends WebCrawler {
    private final static Pattern EXCLUSIONS
      = Pattern.compile(".(.(css|js|xml|gif|png|mp3|mp4|zip|gz|pdf))$");
    
    private static final Pattern IMG_PATTERNS = Pattern.compile(".(.(jpg|jpeg))$");
    
    private File saveDir;
    
    public ImageCrawler(File saveDir) {
        this.saveDir = saveDir;
    }

    // more code

}
```

接下来，让我们实现shouldVisit方法：

```java
@Override
public boolean shouldVisit(Page referringPage, WebURL url) {
    String urlString = url.getURL().toLowerCase();
    if (EXCLUSIONS.matcher(urlString).matches()) {
        return false;
    }

    if (IMG_PATTERNS.matcher(urlString).matches() 
        || urlString.startsWith("https://www.baeldung.com/")) {
        return true;
    }

    return false;
}
```

现在，我们准备好实现visit方法了：

```java
@Override
public void visit(Page page) {
    String url = page.getWebURL().getURL();
    if (IMG_PATTERNS.matcher(url).matches() 
        && page.getParseData() instanceof BinaryParseData) {
        String extension = url.substring(url.lastIndexOf("."));
        int contentLength = page.getContentData().length;

        // write the content data to a file in the save directory
    }
}
```

运行我们的ImageCrawler类似于运行HttpCrawler，但我们需要将其配置为包含二进制内容：

```java
CrawlConfig config = new CrawlConfig();
config.setIncludeBinaryContentInCrawling(true);

// ... same as before
        
CrawlController.WebCrawlerFactory<ImageCrawler> factory = () -> new ImageCrawler(saveDir);
        
controller.start(factory, numCrawlers);
```

### 3.3. 收集数据

现在我们已经看过几个基本示例，让我们扩展我们的HtmlCrawler以在我们的抓取过程中收集一些基本统计信息。

首先，让我们定义一个简单的类来保存一些统计数据：

```java
public class CrawlerStatistics {
    private int processedPageCount = 0;
    private int totalLinksCount = 0;
    
    public void incrementProcessedPageCount() {
        processedPageCount++;
    }
    
    public void incrementTotalLinksCount(int linksCount) {
        totalLinksCount += linksCount;
    }
    
    // standard getters
}
```

接下来，让我们修改HtmlCrawler以通过构造函数接受CrawlerStatistics实例：

```java
private CrawlerStatistics stats;
    
public HtmlCrawler(CrawlerStatistics stats) {
    this.stats = stats;
}
```

使用我们的新CrawlerStatistics对象，让我们修改访问方法以收集我们想要的内容：

```java
@Override
public void visit(Page page) {
    String url = page.getWebURL().getURL();
    stats.incrementProcessedPageCount();

    if (page.getParseData() instanceof HtmlParseData) {
        HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
        String title = htmlParseData.getTitle();
        String text = htmlParseData.getText();
        String html = htmlParseData.getHtml();
        Set<WebURL> links = htmlParseData.getOutgoingUrls();
        stats.incrementTotalLinksCount(links.size());

        // do something with collected data
    }
}
```

现在，让我们回到我们的控制器并为HtmlCrawler提供一个CrawlerStatistics实例：

```java
CrawlerStatistics stats = new CrawlerStatistics();
CrawlController.WebCrawlerFactory<HtmlCrawler> factory = () -> new HtmlCrawler(stats);
```

### 3.4. 多个爬虫

基于我们之前的示例，现在让我们看看如何从同一个控制器运行多个爬虫。

建议每个爬虫都使用自己的临时存储目录，因此我们需要为每个要运行的爬虫创建单独的配置。

CrawlControllers可以共享一个RobotstxtServer，但除此之外，我们基本上需要所有内容的副本。

到目前为止，我们已经使用CrawlController.start方法来运行我们的爬虫并注意到它是一种阻塞方法。要运行多个，我们将结合使用CrawlerController.startNonBlocking和CrawlController.waitUntilFinish。

现在，让我们创建一个控制器来同时运行HtmlCrawler和ImageCrawler：

```java
File crawlStorageBase = new File("src/test/resources/crawler4j");
CrawlConfig htmlConfig = new CrawlConfig();
CrawlConfig imageConfig = new CrawlConfig();
        
// Configure storage folders and other configurations
        
PageFetcher pageFetcherHtml = new PageFetcher(htmlConfig);
PageFetcher pageFetcherImage = new PageFetcher(imageConfig);
        
RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcherHtml);

CrawlController htmlController
  = new CrawlController(htmlConfig, pageFetcherHtml, robotstxtServer);
CrawlController imageController
  = new CrawlController(imageConfig, pageFetcherImage, robotstxtServer);
        
// add seed URLs
        
CrawlerStatistics stats = new CrawlerStatistics();
CrawlController.WebCrawlerFactory<HtmlCrawler> htmlFactory = () -> new HtmlCrawler(stats);
        
File saveDir = new File("src/test/resources/crawler4j");
CrawlController.WebCrawlerFactory<ImageCrawler> imageFactory
  = () -> new ImageCrawler(saveDir);
        
imageController.startNonBlocking(imageFactory, 7);
htmlController.startNonBlocking(htmlFactory, 10);

htmlController.waitUntilFinish();
imageController.waitUntilFinish();
```

## 4.配置

我们已经看到了一些我们可以配置的东西。现在，让我们来看看其他一些常见的设置。

设置应用于我们在控制器中指定的CrawlConfig实例。

### 4.1. 限制抓取深度

默认情况下，我们的爬虫会尽可能深入地爬行。为了限制它们的深度，我们可以设置爬行深度：

```java
crawlConfig.setMaxDepthOfCrawling(2);
```

种子 URL 被认为处于深度 0，因此爬网深度 2 将超越种子 URL 两层。

### 4.2. 要获取的最大页数

另一种限制我们的爬虫将覆盖多少页面的方法是设置要抓取的最大页面数：

```java
crawlConfig.setMaxPagesToFetch(500);
```

### 4.3. 最大传出链接

我们还可以限制每个页面的传出链接数量：

```java
crawlConfig.setMaxOutgoingLinksToFollow(2000);
```

### 4.4. 礼貌延迟

由于非常高效的爬虫很容易对 Web 服务器造成压力，因此 crawler4j 具有所谓的礼貌延迟。默认情况下，它设置为 200 毫秒。如果需要，我们可以调整这个值：

```java
crawlConfig.setPolitenessDelay(300);
```

### 4.5. 包含二进制内容

我们已经在ImageCrawler中使用了包含二进制内容的选项：

```java
crawlConfig.setIncludeBinaryContentInCrawling(true);
```

### 4.6. 包括 HTTPS

默认情况下，爬虫将包含 HTTPS 页面，但我们可以将其关闭：

```java
crawlConfig.setIncludeHttpsPages(false);
```

### 4.7. 可恢复爬行

如果我们有一个长时间运行的爬虫，我们希望它自动恢复，我们可以设置可恢复爬行。打开它可能会导致它运行得更慢：

```java
crawlConfig.setResumableCrawling(true);
```

### 4.8. 用户代理字符串

crawler4j 的默认用户代理字符串是[crawler4j](https://github.com/yasserg/crawler4j/)。让我们自定义：

```java
crawlConfig.setUserAgentString("baeldung demo (https://github.com/yasserg/crawler4j/)");
```

我们刚刚在这里介绍了一些基本配置。如果我们对一些更高级或晦涩的配置选项感兴趣，我们可以查看[CrawConfig类。](https://github.com/yasserg/crawler4j/blob/master/crawler4j/src/main/java/edu/uci/ics/crawler4j/crawler/CrawlConfig.java)

## 5.总结

在本文中，我们使用 crawler4j 创建了我们自己的网络爬虫。我们从爬取 HTML 和图像的两个简单示例开始。然后，我们以这些示例为基础，了解如何收集统计数据并同时运行多个爬虫。