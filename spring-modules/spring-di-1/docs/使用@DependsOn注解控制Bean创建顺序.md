## 1. 概述

默认情况下，Spring管理bean的生命周期并安排它们的初始化顺序。

但是，我们仍然可以根据需要对其进行自定义，我们可以通过SmartLifeCycle接口或@DependsOn注解来管理初始化顺序。

## 2. Maven依赖

```xml

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.3.13</version>
</dependency>
```

## 3. @DependsOn

我们应该使用这个注解来指定bean依赖。Spring保证在尝试初始化当前bean之前先初始化定义的bean。

假设我们有一个依赖于FileReader和FileWriter的FileProcessor，在这种情况下，FileReader和FileWriter应该在FileProcessor之前初始化。

## 4. 配置

```java

@Configuration
@ComponentScan("cn.tuyucheng.taketoday.dependson")
public class Config {

    @Bean("fileProcessor")
    @DependsOn({"fileReader", "fileWriter"})
    public FileProcessor fileProcessor() {
        return new FileProcessor(file);
    }

    @Bean("fileReader")
    public FileReader fileReader() {
        return new FileReader(file);
    }

    @Bean("fileWriter")
    public FileWriter fileWriter() {
        return new FileWriter(file);
    }
}
```

FileProcessor使用@DependsOn指定其依赖bean，我们还可以使用@DependsOn标注组件类：

```java

@Component
@DependsOn({"filereader", "fileWriter"})
public class FileProcessor {
}
```

## 5. 用法

我们创建一个File文件，每个bean都会更新File中的text属性。
FileReader将其更新为read，FileWriter将其更新为write并且FileProcessor将文本更新为processed：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
public class FileProcessorIntegrationTest {

    @Autowired
    ApplicationContext context;

    @Autowired
    File file;

    @Test
    public void whenAllBeansCreated_FileTextEndsWithProcessed() {
        context.getBean("fileProcessor");
        assertTrue(file.getText().endsWith("processed"));
    }
}
```

### 5.1 缺少依赖bean

在缺少依赖bean的情况下，Spring抛出一个BeanCreationException，其中包含一个基本异常NoSuchBeanDefinitionException。

例如，dummyFileProcessor bean依赖于dummyFileWriter bean。
由于dummyFileWriter不存在，它会抛出BeanCreationException：

```java
class FileProcessorIntegrationTest {

    @Test
    void whenDependentBeanNotAvailable_ThrowsNoSuchBeanDefinitionException() {
        assertThrows(BeanCreationException.class, () -> context.getBean("dummyFileProcessor"));
    }
}
```

### 5.2 循环依赖

此外，在这种情况下，它会抛出BeanCreationException并突出显示bean具有循环依赖关系：

```java

@Configuration
@ComponentScan("cn.tuyucheng.taketoday.dependson")
public class TestConfig {

    @Autowired
    File file;

    @Bean("dummyFileProcessorCircular")
    @DependsOn({"dummyFileReaderCircular"})
    @Lazy
    public FileProcessor dummyFileProcessorCircular() {
        return new FileProcessor(file);
    }

    @Bean("dummyFileReaderCircular")
    @DependsOn({"dummyFileProcessorCircular"})
    @Lazy
    public FileReader dummyFileReaderCircular() {
        return new FileReader(file);
    }
}
```

**如果bean最终依赖于自身，就会发生循环依赖，从而产生一个循环**：

```text
Bean1 -> Bean4 -> Bean6 -> Bean1
```

## 6. 关键点

最后，在使用@DependsOn注解时，我们应该注意以下几点：

+ **在使用@DependsOn时，我们必须使用组件扫描**。
+ 如果通过XML声明@DependsOn注解类，则忽略@DependsOn注解元数据。

## 7. 总结

在构建具有复杂依赖需求的系统时，@DependsOn特别有用。

它可以确保Spring在加载我们的依赖类之前已经处理了那些所需Bean的所有初始化。