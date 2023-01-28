## 1. 概述

[Apache CXF](https://cxf.apache.org/)是一个完全兼容JAX-WS的框架。

在JAX-WS标准定义的特性之上，Apache CXF提供了WSDL和Java类之间的转换能力、用于操作原始XML消息的API、对JAX-RS的支持、与Spring框架的集成等。

本教程是Apache CXF系列教程的第一篇，介绍了该框架的基本特征。它仅在源代码中使用JAX-WS标准API，同时仍在幕后利用Apache CXF，例如自动生成的WSDL元数据和CXF默认配置。

## 2. Maven依赖

使用Apache CXF所需的关键依赖项是org.apache.cxf：cxf–rt–frontend–jaxws，这提供了一个JAX-WS实现来替换内置的JDK实现：

```xml
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-rt-frontend-jaxws</artifactId>
    <version>3.1.6</version>
</dependency>
```

请注意，此工件在META-INF/services目录中包含一个名为javax.xml.ws.spi.Provider的文件。Java VM查看此文件的第一行以确定要使用的JAX-WS实现。在这种情况下，该行的内容是org.apache.cxf.jaxws.spi.ProviderImpl，指的是Apache CXF提供的实现。

在本教程中，我们不使用Servlet容器来发布服务，因此需要另一个依赖项来提供必要的Java类型定义：

```xml
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-rt-transports-http-jetty</artifactId>
    <version>3.1.6</version>
</dependency>
```

有关这些依赖项的最新版本，请查看Maven中央仓库中的[cxf-rt-frontend-jaxws](https://search.maven.org/artifact/org.apache.cxf/cxf-rt-frontend-jaxws)和[cxf-rt-transports-http-jetty](https://search.maven.org/artifact/org.apache.cxf/cxf-rt-transports-http-jetty)。

## 3. Web服务端点

让我们从用于配置服务端点的实现类开始：

```java
@WebService(endpointInterface = "cn.tuyucheng.taketoday.cxf.introduction.Tuyucheng")
public class TuyuchengImpl implements Tuyucheng {
    private Map<Integer, Student> students = new LinkedHashMap<>();

    public String hello(String name) {
        return "Hello " + name;
    }

    public String helloStudent(Student student) {
        students.put(students.size() + 1, student);
        return "Hello " + student.getName();
    }

    public Map<Integer, Student> getStudents() {
        return students;
    }
}
```

此处需要注意的最重要的事情是@WebService注解中存在endpointInterface属性，此属性指向定义Web服务抽象契约的接口。

端点接口中声明的所有方法签名都需要实现，但不需要实现接口。

这里TuyuchengImpl实现类仍然实现了下面的端点接口，以明确该接口所有声明的方法都已实现，但这样做是可选的：

```java
@WebService
public interface Tuyucheng {
    String hello(String name);

    String helloStudent(Student student);

    @XmlJavaTypeAdapter(StudentMapAdapter.class)
    Map<Integer, Student> getStudents();
}
```

默认情况下，Apache CXF使用JAXB作为其数据绑定架构。但是，由于JAXB不直接支持从getStudents方法返回的Map的绑定，**我们需要一个适配器将Map转换为JAXB可以使用的Java类**。

此外，为了将契约元素与其实现分离，我们将Student定义为一个接口，而JAXB也不直接支持接口，因此我们需要多一个适配器来处理这个问题。实际上，为了方便起见，我们可以将Student声明为一个类。将此类型用作接口只是使用适配类的又一示范。

适配器在下面的部分中进行了演示。

## 4. 自定义适配器

本节说明如何使用适配类来支持使用JAXB绑定Java接口和Map。

### 4.1 接口适配器

Student接口是这样定义的：

```java
@XmlJavaTypeAdapter(StudentAdapter.class)
public interface Student {
    String getName();
}
```

此接口仅声明一个返回字符串的方法，并将StudentAdapter指定为适配类，以将其自身映射到可应用JAXB绑定的类型或从该类型映射。

StudentAdapter类定义如下：

```java
public class StudentAdapter extends XmlAdapter<StudentImpl, Student> {
    public StudentImpl marshal(Student student) throws Exception {
        if (student instanceof StudentImpl) {
            return (StudentImpl) student;
        }
        return new StudentImpl(student.getName());
    }

    public Student unmarshal(StudentImpl student) throws Exception {
        return student;
    }
}
```

适配类必须实现XmlAdapter接口并提供编组和解组方法的实现。marshal方法将绑定类型(Student，JAXB无法直接处理的接口)转换为值类型(StudentImpl，JAXB可以处理的具体类)。unmarshal方法以相反的方式做事。

这是StudentImpl类定义：

```java
@XmlType(name = "Student")
public class StudentImpl implements Student {
    private String name;

    // constructors, getter and setter
}
```

### 4.2 Map适配器

Tuyucheng端点接口的getStudents方法返回一个Map并指示一个适配类，用于将Map转换为JAXB可以处理的类型。与StudentAdapter类类似，该适配类必须实现XmlAdapter接口的marshal和unmarshal方法：

```java
public class StudentMapAdapter extends XmlAdapter<StudentMap, Map<Integer, Student>> {
    public StudentMap marshal(Map<Integer, Student> boundMap) throws Exception {
        StudentMap valueMap = new StudentMap();
        for (Map.Entry<Integer, Student> boundEntry : boundMap.entrySet()) {
            StudentMap.StudentEntry valueEntry  = new StudentMap.StudentEntry();
            valueEntry.setStudent(boundEntry.getValue());
            valueEntry.setId(boundEntry.getKey());
            valueMap.getEntries().add(valueEntry);
        }
        return valueMap;
    }

    public Map<Integer, Student> unmarshal(StudentMap valueMap) throws Exception {
        Map<Integer, Student> boundMap = new LinkedHashMap<Integer, Student>();
        for (StudentMap.StudentEntry studentEntry : valueMap.getEntries()) {
            boundMap.put(studentEntry.getId(), studentEntry.getStudent());
        }
        return boundMap;
    }
}
```

StudentMapAdapter类将Map<Integer, Student>映射到StudentMap值类型，定义如下：

```java
@XmlType(name = "StudentMap")
public class StudentMap {
    private List<StudentEntry> entries = new ArrayList<StudentEntry>();

    @XmlElement(nillable = false, name = "entry")
    public List<StudentEntry> getEntries() {
        return entries;
    }

    @XmlType(name = "StudentEntry")
    public static class StudentEntry {
        private Integer id;
        private Student student;

        // getters and setters
    }
}
```

## 5.部署

### 5.1 服务器定义

为了部署上面讨论的Web服务，我们将使用标准的JAX-WS API。由于我们使用的是Apache CXF，该框架会做一些额外的工作，例如生成和发布WSDL模式。以下是服务服务器的定义方式：

```java
public class Server {
    public static void main(String[] args) throws InterruptedException {
        TuyuchengImpl implementor = new TuyuchengImpl();
        String address = "http://localhost:8080/tuyucheng";
        Endpoint.publish(address, implementor);
        Thread.sleep(60 * 1000);        
        System.exit(0);
    }
}
```

服务器活跃一段时间后方便测试，应该关闭它以释放系统资源。你可以根据需要通过将长参数传递给Thread.sleep方法来指定服务器的任何工作持续时间。

### 5.2 服务器部署

在本教程中，我们使用org.codehaus.mojo: exec-maven-plugin插件来实例化上述服务器并控制其生命周期。这在Maven POM文件中声明如下：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <configuration>
        <mainClass>cn.tuyucheng.taketoday.cxf.introduction.Server</mainClass>
    </configuration>
</plugin>
```

mainClass配置是指发布Web服务端点的Server类。运行此插件的java目标后，我们可以通过访问URL [http://localhost:8080/tuyucheng?wsdl](http://localhost:8080/tuyucheng?wsdl)查看Apache CXF自动生成的WSDL模式。

## 6. 测试用例

本节将引导你完成编写用于验证我们之前创建的Web服务的测试用例的步骤。

请注意，在运行任何测试之前，我们需要执行exec:java目标来启动Web服务服务器。

### 6.1 准备

第一步是为测试类声明几个字段：

```java
public class StudentTest {
    private static QName SERVICE_NAME
          = new QName("http://introduction.cxf.tuyucheng.com/", "Tuyucheng");
    private static QName PORT_NAME
          = new QName("http://introduction.cxf.tuyucheng.com/", "TuyuchengPort");

    private Service service;
    private Tuyucheng TuyuchengProxy;
    private TuyuchengImpl TuyuchengImpl;

    // other declarations
}
```

以下初始化程序块用于在运行任何测试之前启动javax.xml.ws.Service类型的service字段：

```java
{
    service = Service.create(SERVICE_NAME);
    String endpointAddress = "http://localhost:8080/tuyucheng";
    service.addPort(PORT_NAME, SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);
}
```

将JUnit依赖项添加到POM文件后，我们可以在下面的代码片段中使用@BeforeEach注解。此方法在每次重新实例化Tuyucheng字段的测试之前运行：

```java
@BeforeEach
void reinstantiateTuyuchengInstances() {
    TuyuchengImpl = new TuyuchengImpl();
    TuyuchengProxy = service.getPort(PORT_NAME, Tuyucheng.class);
}
```

TuyuchengProxy变量是Web服务端点的代理，而TuyuchengImpl只是一个简单的Java对象。该对象用于比较通过代理调用远程端点方法与调用本地方法的结果。

请注意，QName实例由两部分标识：命名空间URI和本地部分。如果Service.getPort方法的QName类型的PORT_NAME参数被省略，Apache CXF将假设该参数的Namespace URI是端点接口的包名，其本地部分是接口名后加Port，这与PORT_NAME的值完全相同。因此，在本教程中，我们可能会忽略这个参数。

### 6.2 测试实施

我们在本小节中说明的第一个测试用例是验证远程调用服务端点上的hello方法返回的响应：

```java
@Test 
void whenUsingHelloMethod_thenCorrect() {
    String endpointResponse = TuyuchengProxy.hello("Tuyucheng");
    String localResponse = TuyuchengImpl.hello("Tuyucheng");
    assertEquals(localResponse, endpointResponse);
}
```

很明显，远程端点方法返回与本地方法相同的响应，这意味着Web服务按预期工作。

下一个测试用例演示了helloStudent方法的使用：

```java
@Test
void whenUsingHelloStudentMethod_thenCorrect() {
    Student student = new StudentImpl("John Doe");
    String endpointResponse = TuyuchengProxy.helloStudent(student);
    String localResponse = TuyuchengImpl.helloStudent(student);
    assertEquals(localResponse, endpointResponse);
}
```

在这种情况下，客户端向端点提交一个Student对象，并收到一条包含学生姓名的消息作为响应。与前面的测试用例一样，远程调用和本地调用的响应是相同的。

我们在这里展示的最后一个测试用例更加复杂。根据服务端点实现类的定义，每次客户端调用端点上的helloStudent方法时，提交的Student对象将存储在缓存中。可以通过调用端点上的getStudents方法来检索此缓存。以下测试用例确认学生缓存的内容表示客户端已发送到Web服务的内容：

```java
@Test
void usingGetStudentsMethod_thenCorrect() {
    Student student1 = new StudentImpl("Adam");
    TuyuchengProxy.helloStudent(student1);

    Student student2 = new StudentImpl("Eve");
    TuyuchengProxy.helloStudent(student2);
        
    Map<Integer, Student> students = TuyuchengProxy.getStudents();       
    assertEquals("Adam", students.get(1).getName());
    assertEquals("Eve", students.get(2).getName());
}
```

## 7. 总结

本教程介绍了Apache CXF，这是一个使用Java处理Web服务的强大框架。它着重于将框架应用为标准的JAX-WS实现，同时仍然在运行时利用框架的特定功能。