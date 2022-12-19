## 一、概述

在本教程中，我们会将基本指标集成到 Spring REST API中。

我们将首先使用简单的 Servlet 过滤器构建指标功能，然后使用Spring BootActuator 模块。

## 2.web.xml _

让我们首先注册一个过滤器——“ MetricFilter ”——到我们应用程序的web.xml中：

```xml
<filter>
    <filter-name>metricFilter</filter-name>
    <filter-class>org.baeldung.metrics.filter.MetricFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>metricFilter</filter-name>
    <url-pattern>/</url-pattern>
</filter-mapping>
```

请注意我们如何映射过滤器以涵盖所有传入的请求—— “/” ——这当然是完全可配置的。

## 3. Servlet过滤器

现在 - 让我们创建我们的自定义过滤器：

```java
public class MetricFilter implements Filter {

    private MetricService metricService;

    @Override
    public void init(FilterConfig config) throws ServletException {
        metricService = (MetricService) WebApplicationContextUtils
                .getRequiredWebApplicationContext(config.getServletContext())
                .getBean("metricService");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws java.io.IOException, ServletException {
        HttpServletRequest httpRequest = ((HttpServletRequest) request);
        String req = httpRequest.getMethod() + " " + httpRequest.getRequestURI();

        chain.doFilter(request, response);

        int status = ((HttpServletResponse) response).getStatus();
        metricService.increaseCount(req, status);
    }
}
```

由于过滤器不是标准 bean，我们不打算注入metricService而是通过ServletContext手动检索它。

另请注意，我们通过在此处调用doFilter API 来继续执行过滤器链。

## 4. 指标——状态码计数

接下来——让我们看一下简单的InMemoryMetricService：

```java
@Service
public class MetricService {

    private Map<Integer, Integer> statusMetric;

    public MetricService() {
        statusMetric = new ConcurrentHashMap<>();
    }
    
    public void increaseCount(String request, int status) {
        Integer statusCount = statusMetric.get(status);
        if (statusCount == null) {
            statusMetric.put(status, 1);
        } else {
            statusMetric.put(status, statusCount + 1);
        }
    }

    public Map getStatusMetric() {
        return statusMetric;
    }
}
```

我们使用内存中的ConcurrentMap来保存每种类型的 HTTP 状态代码的计数。

现在——为了显示这个基本指标——我们将把它映射到一个Controller方法：

```java
@GetMapping(value = "/status-metric")
@ResponseBody
public Map getStatusMetric() {
    return metricService.getStatusMetric();
}
```

这是一个示例响应：

```bash
{  
    "404":1,
    "200":6,
    "409":1
}
```

## 5. 指标——请求的状态代码

接下来——让我们记录 Counts by Request 的指标：

```java
@Service
public class MetricService {

    private Map<String, Map<Integer, Integer>> metricMap;

    public void increaseCount(String request, int status) {
        Map<Integer, Integer> statusMap = metricMap.get(request);
        if (statusMap == null) {
            statusMap = new ConcurrentHashMap<>();
        }

        Integer count = statusMap.get(status);
        if (count == null) {
            count = 1;
        } else {
            count++;
        }
        statusMap.put(status, count);
        metricMap.put(request, statusMap);
    }

    public Map getFullMetric() {
        return metricMap;
    }
}
```

我们将通过 API 显示指标结果：

```java
@GetMapping(value = "/metric")
@ResponseBody
public Map getMetric() {
    return metricService.getFullMetric();
}
```

以下是这些指标的样子：

```bash
{
    "GET /users":
    {
        "200":6,
        "409":1
    },
    "GET /users/1":
    {
        "404":1
    }
}
```

根据以上示例，API 具有以下活动：

-   “7”个请求“GET /users ”
-   其中“6”个导致“200”状态代码响应，只有一个在“409”中

## 6. Metric——时间序列数据

总体计数在应用程序中有些用处，但如果系统已经运行了很长时间——就很难说出这些指标的实际含义。

你需要当时的背景才能使数据有意义并易于解释。

现在让我们构建一个简单的基于时间的指标；我们将记录每分钟的状态码计数——如下所示：

```java
@Service
public class MetricService {

    private static final SimpleDateFormat DATE_FORMAT = 
      new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Map<String, Map<Integer, Integer>> timeMap;

    public void increaseCount(String request, int status) {
        String time = DATE_FORMAT.format(new Date());
        Map<Integer, Integer> statusMap = timeMap.get(time);
        if (statusMap == null) {
            statusMap = new ConcurrentHashMap<>();
        }

        Integer count = statusMap.get(status);
        if (count == null) {
            count = 1;
        } else {
            count++;
        }
        statusMap.put(status, count);
        timeMap.put(time, statusMap);
    }
}
```

和getGraphData()：

```java
public Object[][] getGraphData() {
    int colCount = statusMetric.keySet().size() + 1;
    Set<Integer> allStatus = statusMetric.keySet();
    int rowCount = timeMap.keySet().size() + 1;
    
    Object[][] result = new Object[rowCount][colCount];
    result[0][0] = "Time";

    int j = 1;
    for (int status : allStatus) {
        result[0][j] = status;
        j++;
    }
    int i = 1;
    Map<Integer, Integer> tempMap;
    for (Entry<String, Map<Integer, Integer>> entry : timeMap.entrySet()) {
        result[i][0] = entry.getKey();
        tempMap = entry.getValue();
        for (j = 1; j < colCount; j++) {
            result[i][j] = tempMap.get(result[0][j]);
            if (result[i][j] == null) {
                result[i][j] = 0;
            }
        }
        i++;
    }


    对于 (int k = 1; k < result[0].length; k++) {
        结果[0][k] = 结果[0][k].toString();
    }
   return result; 
}
```

我们现在将其映射到 API：

```java
@GetMapping(value = "/metric-graph-data")
@ResponseBody
public Object[][] getMetricData() {
    return metricService.getGraphData();
}
```

最后——我们将使用 Google Charts 将其呈现出来：

```html
<html>
<head>
<title>Metric Graph</title>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script type="text/javascript">
google.load("visualization", "1", {packages : [ "corechart" ]});

function drawChart() {
$.get("/metric-graph-data",function(mydata) {
    var data = google.visualization.arrayToDataTable(mydata);
    var options = {title : 'Website Metric',
                   hAxis : {title : 'Time',titleTextStyle : {color : '#333'}},
                   vAxis : {minValue : 0}};

    var chart = new google.visualization.AreaChart(document.getElementById('chart_div'));
    chart.draw(data, options);

});

}
</script>
</head>
<body onload="drawChart()">
    <div id="chart_div" style="width: 900px; height: 500px;"></div>
</body>
</html>
```

## 7. 使用Spring Boot1.x 执行器

在接下来的几节中，我们将连接到Spring Boot中的 Actuator 功能来展示我们的指标。

首先——我们需要将执行器依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### 7.1. 指标过滤器

接下来——我们可以将MetricFilter——变成一个实际的 Spring bean：

```java
@Component
public class MetricFilter implements Filter {

    @Autowired
    private MetricService metricService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
      throws java.io.IOException, ServletException {
        chain.doFilter(request, response);

        int status = ((HttpServletResponse) response).getStatus();
        metricService.increaseCount(status);
    }
}
```

当然，这是一个很小的简化——但是为了摆脱以前手动连接依赖关系而值得做的一个简化。

### 7.2. 使用柜台服务

现在让我们使用CounterService来计算每个状态代码的出现次数：

```java
@Service
public class MetricService {

    @Autowired
    private CounterService counter;

    private List<String> statusList;

    public void increaseCount(int status) {
        counter.increment("status." + status);
        if (!statusList.contains("counter.status." + status)) {
            statusList.add("counter.status." + status);
        }
    }
}
```

### 7.3. 使用MetricRepository导出指标

接下来——我们需要导出指标——使用MetricRepository：

```java
@Service
public class MetricService {

    @Autowired
    private MetricRepository repo;

    private List<List<Integer>> statusMetric;
    private List<String> statusList;
    
    @Scheduled(fixedDelay = 60000)
    private void exportMetrics() {
        Metric<?> metric;
        List<Integer> statusCount = new ArrayList<>();
        for (String status : statusList) {
            metric = repo.findOne(status);
            if (metric != null) {
                statusCount.add(metric.getValue().intValue());
                repo.reset(status);
            } else {
                statusCount.add(0);
            }
        }
        statusMetric.add(statusCount);
    }
}
```

请注意，我们正在存储每分钟状态代码的计数。

### 7.4. Spring Boot公共指标

我们还可以使用Spring BootPublicMetrics来导出指标，而不是使用我们自己的过滤器——如下所示：

首先，我们有每分钟导出指标的计划任务：

```java
@Autowired
private MetricReaderPublicMetrics publicMetrics;

private List<List<Integer>> statusMetricsByMinute;
private List<String> statusList;
private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

@Scheduled(fixedDelay = 60000)
private void exportMetrics() {
    List<Integer> lastMinuteStatuses = initializeStatuses(statusList.size());
    for (Metric<?> counterMetric : publicMetrics.metrics()) {
        updateMetrics(counterMetric, lastMinuteStatuses);
    }
    statusMetricsByMinute.add(lastMinuteStatuses);
}
```

当然，我们需要初始化 HTTP 状态代码列表：

```java
private List<Integer> initializeStatuses(int size) {
    List<Integer> counterList = new ArrayList<>();
    for (int i = 0; i < size; i++) {
        counterList.add(0);
    }
    return counterList;
}
```

然后我们将使用状态代码计数实际更新指标：

```java
private void updateMetrics(Metric<?> counterMetric, List<Integer> statusCount) {

    if (counterMetric.getName().contains("counter.status.")) {
        String status = counterMetric.getName().substring(15, 18); // example 404, 200
        appendStatusIfNotExist(status, statusCount);
        int index = statusList.indexOf(status);
        int oldCount = statusCount.get(index) == null ? 0 : statusCount.get(index);
        statusCount.set(index, counterMetric.getValue().intValue() + oldCount);
    }
}

private void appendStatusIfNotExist(String status, List<Integer> statusCount) {
    if (!statusList.contains(status)) {
        statusList.add(status);
        statusCount.add(0);
    }
}
```

注意：

-   PublicMetics状态计数器名称以“ counter.status ”开头，例如“ counter.status.200.root ”
-   我们在列表statusMetricsByMinute中记录每分钟的状态计数

我们可以导出收集到的数据以将其绘制成图表——如下所示：

```java
public Object[][] getGraphData() {
    Date current = new Date();
    int colCount = statusList.size() + 1;
    int rowCount = statusMetricsByMinute.size() + 1;
    Object[][] result = new Object[rowCount][colCount];
    result[0][0] = "Time";
    int j = 1;

    for (String status : statusList) {
        result[0][j] = status;
        j++;
    }

    for (int i = 1; i < rowCount; i++) {
        result[i][0] = dateFormat.format(
          new Date(current.getTime() - (60000L  (rowCount - i))));
    }

    List<Integer> minuteOfStatuses;
    List<Integer> last = new ArrayList<Integer>();

    for (int i = 1; i < rowCount; i++) {
        minuteOfStatuses = statusMetricsByMinute.get(i - 1);
        for (j = 1; j <= minuteOfStatuses.size(); j++) {
            result[i][j] = 
              minuteOfStatuses.get(j - 1) - (last.size() >= j ? last.get(j - 1) : 0);
        }
        while (j < colCount) {
            result[i][j] = 0;
            j++;
        }
        last = minuteOfStatuses;
    }
    return result;
}
```

### 7.5. 使用指标绘制图形

最后——让我们通过一个二维数组来表示这些指标——这样我们就可以将它们绘制成图表：

```java
public Object[][] getGraphData() {
    Date current = new Date();
    int colCount = statusList.size() + 1;
    int rowCount = statusMetric.size() + 1;
    Object[][] result = new Object[rowCount][colCount];
    result[0][0] = "Time";

    int j = 1;
    for (String status : statusList) {
        result[0][j] = status;
        j++;
    }

    ArrayList<Integer> temp;
    for (int i = 1; i < rowCount; i++) {
        temp = statusMetric.get(i - 1);
        result[i][0] = dateFormat.format
          (new Date(current.getTime() - (60000L  (rowCount - i))));
        for (j = 1; j <= temp.size(); j++) {
            result[i][j] = temp.get(j - 1);
        }
        while (j < colCount) {
            result[i][j] = 0;
            j++;
        }
    }

    return result;
}
```

这是我们的控制器方法getMetricData()：

```java
@GetMapping(value = "/metric-graph-data")
@ResponseBody
public Object[][] getMetricData() {
    return metricService.getGraphData();
}
```

这是一个示例响应：

```bash
[
    ["Time","counter.status.302","counter.status.200","counter.status.304"],
    ["2015-03-26 19:59",3,12,7],
    ["2015-03-26 20:00",0,4,1]
]
```

## 8. 使用Spring Boot2.x 执行器

在Spring Boot2 中，Spring Actuator 的 API 发生了重大变化。Spring 自己的指标已被[Micrometer](https://www.baeldung.com/micrometer)取代。因此，让我们使用Micrometer编写上面相同的指标示例。

### 8.1. 用MeterRegistry替换CounterService

由于我们的Spring Boot应用程序已经依赖于 Actuator 启动器，因此 Micrometer 已经自动配置。我们可以注入MeterRegistry而不是CounterService。我们可以使用不同类型的Meter来捕获指标。计数器是仪表之一：

```java
@Autowired
private MeterRegistry registry;

private List<String> statusList;

@Override
public void increaseCount(int status) {
    String counterName = "counter.status." + status;
    registry.counter(counterName).increment(1);
    if (!statusList.contains(counterName)) {
        statusList.add(counterName);
    }
}
```

### 8.2. 查看自定义指标

由于我们的指标现在已在 Micrometer 注册，首先，让我们[在应用程序配置中启用它们](https://www.baeldung.com/spring-boot-actuator-enable-endpoints#3-enabling-specific-endpoints)。现在我们可以通过导航到位于/actuator/metrics的 Actuator 端点来查看它们：

```json
{
  "names": [
    "application.ready.time",
    "application.started.time",
    "counter.status.200",
    "disk.free",
    "disk.total",
    .....
  ]
}
```

在这里我们可以看到我们的counter.status.200指标列在标准执行器指标中。此外，我们还可以通过在 URI 中提供选择器/actuator/metrics/counter.status.200来获取该指标的最新值：

```json
{
  "name": "counter.status.200",
  "description": null,
  "baseUnit": null,
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 2
    }
  ],
  "availableTags": []
}
```

### 8.3. 使用MeterRegistry导出计数

在 Micrometer 中，我们可以使用MeterRegistry导出计数器值：

```java
@Scheduled(fixedDelay = 60000)
private void exportMetrics() {
    List<Integer> statusCount = new ArrayList<>();
    for (String status : statusList) {
        Search search = registry.find(status);
        Counter counter = search.counter();
         if (counter == null) {
             statusCount.add(0);
         } else {
             statusCount.add(counter != null ? ((int) counter.count()) : 0);
             registry.remove(counter);
         }
    }
    statusMetricsByMinute.add(statusCount);
}
```

### 8.3. 使用仪表发布指标

现在我们还可以使用MeterRegistry 的 Meters 发布指标：

```java
@Scheduled(fixedDelay = 60000)
private void exportMetrics() {
    List<Integer> lastMinuteStatuses = initializeStatuses(statusList.size());

    for (Meter counterMetric : publicMetrics.getMeters()) {
        updateMetrics(counterMetric, lastMinuteStatuses);
    }
    statusMetricsByMinute.add(lastMinuteStatuses);
}

private void updateMetrics(Meter counterMetric, List<Integer> statusCount) {
    String metricName = counterMetric.getId().getName();
    if (metricName.contains("counter.status.")) {
        String status = metricName.substring(15, 18); // example 404, 200
        appendStatusIfNotExist(status, statusCount);
        int index = statusList.indexOf(status);
        int oldCount = statusCount.get(index) == null ? 0 : statusCount.get(index);
        statusCount.set(index, (int)((Counter) counterMetric).count() + oldCount);
    }
}
```

## 9.总结

在本文中，我们探讨了几种将一些基本指标功能构建到 Spring Web 应用程序中的简单方法。

请注意，计数器不是线程安全的——因此如果不使用原子序数之类的东西，它们可能不准确。这是故意的，只是因为增量应该很小，而且 100% 的准确度不是目标——相反，及早发现趋势才是。

当然，还有更成熟的方法可以在应用程序中记录 HTTP 指标，但这是一种简单、轻量级且超级有用的方法，无需使用成熟工具的额外复杂性。