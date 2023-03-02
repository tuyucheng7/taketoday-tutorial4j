## 1. 概述

在本文中，我们将通过[Postman](https://learning.postman.com/docs/sending-requests/supported-api-frameworks/making-soap-requests/)发送[SOAP](https://www.baeldung.com/spring-boot-soap-web-service)请求。在此之前，我们会将WSDL从我们的[Country SOAP服务](https://www.baeldung.com/spring-boot-soap-web-service)导入到API平台。

## 2. 设置

在我们可以在Postman中发出SOAP请求之前，我们需要一个正常运行的SOAP服务。运行我们的[Country SOAP服务](https://www.baeldung.com/spring-boot-soap-web-service#1-build-and-run-the-project)后，端点将位于http://localhost:8080/ws，而[WSDL](https://github.com/eugenp/tutorials/blob/master/spring-soap/src/main/resources/countries.wsdl)可以在http://localhost:8080/ws/countries.wsdl找到。

## 3. 测试Postman的SOAP请求

使用Postman测试我们的端点有四个步骤。

### 3.1 导入SOAP WSDL

从[Postman 8.4.0](https://blog.postman.com/postman-now-supports-wsdl/)开始，我们可以将[WSDL](http://localhost:8080/ws/countries.wsdl)导入Postman。我们可以直接导入我们[国家的Postman集合](https://github.com/eugenp/tutorials/tree/master/spring-soap/src/main/resources)。以下是从WSDL创建新集合的几个步骤。

首先，让我们点击Collections：

<img src="../assets/img.png">

接下来，让我们通过提供其URL来导入我们的WSDL：

<img src="../assets/img_1.png">

你也可以直接使用[countries.wsdl WDSL文件](https://github.com/eugenp/tutorials/blob/master/spring-soap/src/main/resources/countries.wsdl)导入。

我们的服务是从WSDL中获取的。我们将跳过高级设置并使用默认设置导入：

<img src="../assets/img_2.png">

导入后，我们应该能够看到我们所有的SOAP服务：

<img src="../assets/img_3.png">

**Postman负责为每个请求设置正确的URL、内容类型和标头**。

### 3.2 添加正文数据

接下来，让我们通过在envelope标头中添加Spain作为国家名称和tuyucheng命名空间来自定义我们的请求正文：

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:gs="http://www.tuyucheng.com/springsoap/gen">
    <soapenv:Header/>
    <soapenv:Body>
        <gs:getCountryRequest>
            <gs:name>Spain</gs:name>
        </gs:getCountryRequest>
    </soapenv:Body>
</soapenv:Envelope>
```

### 3.3 设置请求标头

通过导入我们的WSDL，Postman已经为我们设置了适当的标头。Content-Type设置为text/xml并适用于我们的请求。text/xml优于application/xml。不明确支持text/xml的MIME用户代理(和Web用户代理)会将其视为text/plain，例如，通过将XML MIME实体显示为纯文本。

如果请求需要其他内容类型，我们可以取消选择Postman自动添加的Content-Type标头。然后，我们在Key字段中添加一个包含Content-Type的新行，并在Value字段中添加我们新的内容类型名称。

如果服务返回状态代码500，我们应该添加一个额外的标头“SOAPAction: #POST”。

### 3.4 发送SOAP请求

最后，让我们点击“Send”按钮来调用SOAP服务。如果我们的调用成功，Postman会在下面的选项卡中显示包含有关西班牙信息的响应：

```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
        <ns2:getCountryResponse xmlns:ns2="http://www.tuyucheng.com/springsoap/gen">
            <ns2:country>
                <ns2:name>Spain</ns2:name>
                <ns2:population>46704314</ns2:population>
                <ns2:capital>Madrid</ns2:capital>
                <ns2:currency>EUR</ns2:currency>
            </ns2:country>
        </ns2:getCountryResponse>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

这是Postman控制台中的输出：

[![4](https://www.baeldung.com/wp-content/uploads/2022/08/4.png)](https://www.baeldung.com/wp-content/uploads/2022/08/4.png)

## 4. 总结

在本文中，我们学习了如何通过Postman发送SOAP请求。我们首先看到了如何将WSDL导入Postman。然后，我们成功地向我们的国家服务发送了一个请求。