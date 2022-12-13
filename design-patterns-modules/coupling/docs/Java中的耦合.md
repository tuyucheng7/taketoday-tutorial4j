## 1. 简介

在本教程中，我们将学习Java中的耦合，介绍耦合的类型和对每种类型的描述。最后，我们简要描述[依赖倒置原则]()和控制反转以及它们与耦合的关系。

## 2. Java中的耦合

当我们谈论耦合时，我们描述了我们系统中的类相互依赖的程度。我们在开发过程中的目标是减少耦合。

考虑以下场景：我们正在设计一个元数据收集器应用程序，此应用程序为我们收集元数据。它以XML格式获取元数据，然后将获取的元数据导出到CSV文件，仅此而已。正如我们在设计中所理解的那样，最初的方法可能是：

<img src="../assets/img.png">

我们的模块负责获取、处理和导出数据。然而，这是一个糟糕的设计，这种设计违反了[单一职责原则]()。因此，为了改进我们的第一个设计，我们需要分离关注点。在此更改之后，我们的设计如下所示：

<img src="../assets/img_1.png">

现在，我们的设计被解耦为两个新模块，XML Fetch和Export CSV，同时，元数据收集器模块依赖于两者。这种设计比我们最初的方法要好，但它仍在最佳实践的路上。在以下各节中，我们将介绍如何根据良好的耦合实践改进设计。

## 3. 紧密耦合

**当一组类彼此高度依赖，或者我们有承担很多责任的类时称为紧密耦合。另一种情况是当一个对象创建另一个对象供其使用时，紧密耦合代码很难维护**。

因此，让我们使用我们的基础应用程序来查看此行为。首先，定义我们的XMLFetch类：

```java
public class XMLFetch {
    public List<Object> fetchMetadata() {
        List<Object> metadata = new ArrayList<>();
        // Do some stuff
        return metadata;
    }
}
```

接下来是CSVExport类：

```java
public class CSVExport {
    public File export(List<Object> metadata) {
        System.out.println("Exporting data...");
        // Export Metadata
        File outputCSV = null;
        return outputCSV;
    }
}
```

最后是我们的MetadataCollector类：

```java
public class MetadataCollector {
    private XMLFetch xmlFetch = new XMLFetch();
    private CSVExport csvExport = new CSVExport();

    public void collectMetadata() {
        List<Object> metadata = xmlFetch.fetchMetadata();
        csvExport.export(metadata);
    }
}
```

我们可以注意到，我们的MetadataCollector类依赖于XMLFetch和CSVExport类。此外，MetadataCollector还负责创建它们。

如果我们需要改进我们的收集器，可能会添加一个新的数据JSON提取器并以PDF格式导出数据，我们需要在我们的类中包含这些新元素，因此“改进”后的MetadataCollector类可能如下所示：

```java
public class MetadataCollector {
    // ...
    private CSVExport csvExport = new CSVExport();
    private PDFExport pdfExport = new PDFExport();

    public void collectMetadata(int inputType, int outputType) {
        if (outputType == 1) {
            List<Object> metadata = null;
            if (inputType == 1) {
                metadata = xmlFetch.fetchMetadata();
            } else {
                metadata = jsonFetch.fetchMetadata();
            }
            csvExport.export(metadata);
        } else {
            List<Object> metadata = null;
            if (inputType == 1) {
                metadata = xmlFetch.fetchMetadata();
            } else {
                metadata = jsonFetch.fetchMetadata();
            }
            pdfExport.export(metadata);
        }
    }
}
```

元数据收集器模块需要一些标志来处理新功能，根据标志值，将实例化相应的子模块。然而，每一个新功能的添加不仅让我们的代码变得更复杂，也让维护变得更加困难。这是紧密耦合的典型迹象，我们必须避免它。

## 4. 松散耦合

在开发过程中，我们所有类之间的关系数量需要尽可能少，这称为松散耦合。**松散耦合是指对象从外部源获取要使用的对象，我们的对象彼此独立**。松散耦合的代码减少了维护工作，此外，它为系统提供了更多的灵活性。

松散耦合通过依赖倒置原则来表达。在下一节中，我们将对其进行描述。

## 5. 依赖倒置原则

**[依赖倒置原则(DIP)]()指的是高层模块不应依赖于低层模块来履行其职责，两者都应该依赖于抽象**。

在我们的设计中，这是根本问题。元数据收集器(高层)模块依赖于获取XML和导出CSV数据(低层)模块。

但是我们可以做些什么来改进我们的设计呢？DIP向我们展示了解决方案，但它没有谈论如何实现它。在这种情况下，它是控制反转(IoC)采取行动的时候。IoC表示在我们的模块之间定义抽象的方式，总结起来就是DIP的实现方式。

因此，让我们将DIP和IoC应用于我们当前的示例。首先，我们需要定义一个用于获取数据的接口和一个用于导出数据的接口。让我们看看如何通过代码做到这一点：

```java
public interface FetchMetadata {
    List<Object> fetchMetadata();
}
```

很简单，不是吗？现在定义我们的导出接口：

```java
public interface ExportMetadata {
    File export(List<Object> metadata);
}
```

此外，我们需要在相应的类中实现这些接口。简而言之，我们需要更新当前的类：

```java
public class XMLFetch implements FetchMetadata {
    @Override
    public List<Object> fetchMetadata() {
        List<Object> metadata = new ArrayList<>();
        // Do some stuff
        return metadata;
    }
}
```

接下来，我们需要更新CSVExport类：

```java
public class CSVExport implements ExportMetadata {
    @Override
    public File export(List<Object> metadata) {
        System.out.println("Exporting data...");
        // Export Metadata
        File outputCSV = null;
        return outputCSV;
    }
}
```

此外，我们更新了主模块的代码以支持新的设计更改，下面是更改后的MetadataCollector类：

```java
public class MetadataCollector {
    private FetchMetadata fetchMetadata;
    private ExportMetadata exportMetadata;

    public MetadataCollector(FetchMetadata fetchMetadata, ExportMetadata exportMetadata) {
        this.fetchMetadata = fetchMetadata;
        this.exportMetadata = exportMetadata;
    }

    public void collectMetadata() {
        List<Object> metadata = fetchMetadata.fetchMetadata();
        exportMetadata.export(metadata);
    }
}
```

我们可以观察到代码中的两个主要变化。首先，该类仅依赖于抽象，而不依赖于具体类型。另一方面，我们去除了对低级模块的依赖，无需在收集器模块中保留与低级模块创建相关的任何逻辑，与这些模块的交互是通过标准接口进行的。这种设计的优点是我们可以添加新的模块来获取和导出数据，并且我们的收集器代码不需要改变。

通过应用DIP和IoC，我们改进了系统设计。**通过反转(改变)控制，应用程序变得解耦、可测试、可扩展和可维护**。下图是当前设计的描述：

<img src="../assets/img_2.png">

## 6. 总结

在本文中，我们介绍了Java中的耦合。首先我们了解了一般的耦合定义，然后，我们介绍了紧密耦合和松散耦合的区别，并学习了将DIP与IoC一起应用以实现松散耦合的代码。本文的例子是按照示例设计完成的，通过应用良好的设计模式，我们可以观察我们的代码是如何在每一步中得到改进的。