## 1. 简介

在本文中，我们将探索如何使用MaxMind GeoIP2 Java API和免费的GeoLite2数据库从IP地址获取地理位置数据。

我们还将使用一个简单的Spring MVC Web演示应用程序实际看到这一点。

## 2. 开始

首先，你需要从MaxMind下载GeoIP2 API和GeoLite2数据库。

### 2.1 Maven依赖

要在你的Maven项目中包含MaxMind GeoIP2 API，请将以下内容添加到pom.xml文件：

```xml
<dependency>
    <groupId>com.maxmind.geoip2</groupId>
    <artifactId>geoip2</artifactId>
    <version>2.8.0</version>
</dependency>
```

要获取最新版本的API，你可以在[Maven Central](https://search.maven.org/artifact/com.maxmind.geoip2/geoip2/4.0.0/jar)上找到它。

### 2.2 下载数据库

接下来，你需要下载[GeoLite2数据库](https://dev.maxmind.com/geoip/geoip2/geolite2/)。对于本教程，我们使用GeoLite2 City数据库的二进制gzip版本。

解压存档后，你将拥有一个名为GeoLite2-City.mmdb的文件。这是专有MaxMind二进制格式的IP到位置映射的数据库。

## 3. 使用GeoIP2 Java API

让我们使用GeoIP2 Java API从数据库中获取给定IP地址的位置数据。首先，让我们创建一个DatabaseReader来查询数据库：

```java
File database = new File(dbLocation);
DatabaseReader dbReader = new DatabaseReader.Builder(database).build();
```

接下来，让我们使用city()方法获取IP地址的城市数据：

```java
CityResponse response = dbReader.city(ipAddress);
```

CityResponse对象包含多条信息，而不仅仅是城市名称。下面是一个示例JUnit测试，展示了如何打开数据库、获取IP地址的城市信息以及从CityResponse中提取此信息：

```java
@Test
public void givenIP_whenFetchingCity_thenReturnsCityData() throws IOException, GeoIp2Exception {
    String ip = "your-ip-address";
    String dbLocation = "your-path-to-mmdb";
        
    File database = new File(dbLocation);
    DatabaseReader dbReader = new DatabaseReader.Builder(database).build();
        
    InetAddress ipAddress = InetAddress.getByName(ip);
    CityResponse response = dbReader.city(ipAddress);
        
    String countryName = response.getCountry().getName();
    String cityName = response.getCity().getName();
    String postal = response.getPostal().getCode();
    String state = response.getLeastSpecificSubdivision().getName();
}
```

## 4. 在Web应用程序中使用GeoIP

让我们看一个示例Web应用程序，该应用程序从用户的公共IP地址获取地理定位数据并在地图上显示该位置。

我们将从一个[基本的Spring Web MVC应用程序](https://www.baeldung.com/spring-mvc-tutorial)开始。然后我们将编写一个控制器，它在POST请求中接受一个IP地址，并返回一个包含从GeoIP2 API推导的城市、纬度和经度的JSON响应。

最后，我们将编写一些HTML和JavaScript，将用户的公共IP地址加载到表单中，向我们的Controller提交Ajax POST请求，并在Google地图中显示结果。

### 4.1 响应实体类

让我们从定义将保存地理定位响应的类开始：

```java
public class GeoIP {
    private String ipAddress;
    private String city;
    private String latitude;
    private String longitude;
    // constructors, getters and setters... 
}
```

### 4.2 服务等级

现在让我们编写使用GeoIP2 Java API和GeoLite2数据库获取地理位置数据的服务类：

```java
public class RawDBDemoGeoIPLocationService {
    private DatabaseReader dbReader;

    public RawDBDemoGeoIPLocationService() throws IOException {
        File database = new File("your-mmdb-location");
        dbReader = new DatabaseReader.Builder(database).build();
    }

    public GeoIP getLocation(String ip) throws IOException, GeoIp2Exception {
        InetAddress ipAddress = InetAddress.getByName(ip);
        CityResponse response = dbReader.city(ipAddress);

        String cityName = response.getCity().getName();
        String latitude =
              response.getLocation().getLatitude().toString();
        String longitude =
              response.getLocation().getLongitude().toString();
        return new GeoIP(ip, cityName, latitude, longitude);
    }
}
```

### 4.3 Spring控制器

让我们看一下Spring MVC的控制器，它将“ipAddress”请求参数发送到我们的服务类以获取地理定位响应数据：

```java
@RestController
public class GeoIPTestController {
    private RawDBDemoGeoIPLocationService locationService;

    public GeoIPTestController() throws IOException {
        locationService = new RawDBDemoGeoIPLocationService();
    }

    @PostMapping("/GeoIPTest")
    public GeoIP getLocation(@RequestParam(value="ipAddress", required=true) String ipAddress) throws Exception {

        GeoIPLocationService<String, GeoIP> locationService = new RawDBDemoGeoIPLocationService();
        return locationService.getLocation(ipAddress);
    }
}
```

### 4.4 HTML表单

让我们添加前端代码来调用我们的Spring控制器，从包含IP地址的 HTML 表单开始：

```html
<body>
    <form id="ipForm" action="GeoIPTest" method="POST">
        <input type="text" name = "ipAddress" id = "ip"/>
        <input type="submit" name="submit" value="submit" /> 
    </form>
    ...
</body>
```

### 4.5 在客户端上加载公共IP地址

现在让我们使用[jQuery和ipify.org](https://www.ipify.org/) JavaScript API使用用户的公共IP地址预填充“ipAddress”文本字段：

```javascript
<script src
   ="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js">
</script>
  
<script type="text/javascript">
    $(document).ready (function () {
        $.get( "https://api.ipify.org?format=json", 
          function( data ) {
             $("#ip").val(data.ip) ;
        });
...
</script>
```

### 4.6 提交Ajax POST请求

提交表单后，我们将向Spring控制器发出Ajax POST请求，以检索包含地理定位数据的JSON响应：

```javascript
$( "#ipForm" ).submit(function( event ) {
    event.preventDefault();
    $.ajax({
        url: "GeoIPTest",
        type: "POST",
        contentType: 
         "application/x-www-form-urlencoded; charset=UTF-8", 
        data: $.param( {ipAddress : $("#ip").val()} ),
        complete: function(data) {},
        success: function(data) {
            $("#status").html(JSON.stringify(data));
            if (data.ipAddress !=null) {
                showLocationOnMap(data);
            }
        },
        error: function(err) {
            $("#status").html("Error:"+JSON.stringify(data));
            },
        });
});
```

### 4.7 示例JSON响应

来自我们的 Spring控制器的JSON响应将具有以下格式：

```json
{
    "ipAddress":"your-ip-address",
    "city":"your-city",
    "latitude":"your-latitude",
    "longitude":"your-longitude"
}
```

### 4.8 在谷歌地图上显示位置

要在Google地图上显示位置，你需要在HTML代码中包含Google Maps API：

```html
<script src="https://maps.googleapis.com/maps/api/js?key=YOUR-API-KEY" 
async defer></script>
```

你可以使用Google Developer Console获取Google地图的API密钥。

你还需要定义一个HTML <div\>标签来包含地图图像：

```html
<div id="map" style="height: 500px; width:100%; position:absolute"></div>
```

你可以使用以下JavaScript函数在Google地图上显示坐标：

```javascript
function showLocationOnMap (location) {
    var map;
    map = new google.maps.Map(document.getElementById('map'), {
        center: {
            lat: Number(location.latitude),
            lng: Number(location.longitude)},
        zoom: 15
    });
    var marker = new google.maps.Marker({
        position: {
            lat: Number(location.latitude),
            lng: Number(location.longitude)},
        map: map,
        title:
            "Public IP:"+location.ipAddress
            +" @ "+location.city
    });
}
```

启动Web应用程序后，打开地图页面的URL：

```bash
http://localhost:8080/spring-mvc-xml/GeoIpTest.jsp
```

你将看到你的连接的当前公共IP地址加载到文本框中：

<img src="../assets/img.png">

请注意，GeoIP2和ipify都支持IPv4地址和IPv6地址。

当你提交表单时，你将看到JSON响应文本，包括与你的公共IP地址对应的城市、纬度和经度，在其下方，你将看到指向你所在位置的Google地图：

<img src="../assets/img_1.png">

## 5. 总结

在本教程中，我们使用JUnit测试回顾了MaxMind GeoIP2 Java API和免费的MaxMind GeoLite2 City数据库的用法。

然后我们构建了一个 Spring MVC控制器和服务来从IP地址获取地理位置数据(城市、纬度、经度)。

最后，我们构建了一个HTML/JavaScript前端来演示如何使用此功能在Google地图上显示用户的位置。

本产品包括由MaxMind创建的GeoLite2数据，可从[http://www.maxmind.com](https://www.maxmind.com/)获得。