## 1. 概述

在本教程中，我们将概述什么是[SirixDB](https://sirix.io/)及其最重要的设计目标。

接下来，我们将介绍一个基于游标的低级事务 API。

## 2. SirixDB 特点

SirixDB 是一种日志结构的临时[NoSQL](https://www.baeldung.com/eclipse-jnosql)文档存储，用于存储进化数据。它永远不会覆盖磁盘上的任何数据。因此，我们能够有效地恢复和查询数据库中资源的完整修订历史。SirixDB 确保为每个新修订创建最少的存储开销。

目前，SirixDB 提供了两种内置的原生数据模型，即二进制 XML 存储和 JSON 存储。

### 2.1. 设计目标

一些最重要的核心原则和设计目标是：

-   并发——SirixDB 包含很少的锁，旨在尽可能适用于多线程系统
-   异步 REST API——操作可以独立发生；每个事务都绑定到一个特定的修订版，并且只允许一个资源上的一个读写事务与 N 个只读事务并发
-   版本控制/修订历史——SirixDB 存储数据库中每个资源的修订历史，同时将存储开销保持在最低水平。读写性能可调。这取决于版本控制类型，我们可以指定它来创建资源
-   数据完整性——与 ZFS 一样，SirixDB 将页面的完整校验和存储在父页面中。这意味着将来几乎所有数据损坏都可以在读取时检测到，因为 SirixDB 开发人员的目标是在未来对数据库进行分区和
-   写时语义——与文件系统 Btrfs 和 ZFS 类似，SirixDB 使用 CoW 语义，这意味着 SirixDB 从不覆盖数据。相反，数据库页面片段被并写入新位置
-   按修订和按记录进行版本控制——SirixDB 不仅按页面进行版本控制，而且按记录进行版本控制。因此，每当我们更改数据页中的一小部分
    记录时，它不必整个页面并将其写入磁盘或闪存驱动器上的新位置。相反，我们可以在创建数据库资源期间指定从备份系统或滑动快照算法已知的几种版本控制策略之一。SirixDB 使用我们指定的版本控制类型来对数据页进行版本控制
-   保证原子性(没有 WAL)——系统永远不会进入不一致状态(除非有硬件故障)，这意味着意外断电永远不会损坏系统。[这是在没有预写日志 ( WAL](https://en.wikipedia.org/wiki/Write-ahead_logging) )开销的情况下完成的
-   日志结构和 SSD 友好——SirixDB 在提交期间将所有内容批量写入和同步到闪存驱动器。它永远不会覆盖提交的数据

在以后的文章中将重点转移到更高级别之前，我们首先要介绍以 JSON 数据为例的低级 API。例如，用于查询 XML 和 JSON 数据库的 XQuery-API 或异步的、临时的 RESTful API。我们基本上可以使用具有细微差别的相同低级 API 来存储、遍历和比较 XML 资源。

为了使用 SirixDB，我们至少要使用[Java 11](https://www.baeldung.com/java-11-string-api)。

## 3. Maven依赖嵌入SirixDB

为了遵循示例，我们首先必须包括[sirix -core依赖](https://search.maven.org/search?q=g:io.sirix AND a:sirix-core&core=gav)项，例如，通过 Maven：

```xml
<dependency>
    <groupId>io.sirix</groupId>
    <artifactId>sirix-core</artifactId>
    <version>0.9.3</version>
</dependency>
```

或者通过摇篮：

```html
dependencies {
    compile 'io.sirix:sirix-core:0.9.3'
}

```

## 4. SirixDB 中的树编码

SirixDB 中的节点通过firstChild/leftSibling/rightSibling/parentNodeKey/nodeKey编码引用其他节点：

[![编码](https://www.baeldung.com/wp-content/uploads/2019/08/encoding.png)](https://www.baeldung.com/wp-content/uploads/2019/08/encoding.png)

图中的数字是使用简单的序列号生成器自动生成的唯一、稳定的节点 ID。

每个节点可能有第一个孩子、左兄弟节点、右兄弟节点和父节点。此外，SirixDB 能够存储子节点的数量、后代的数量以及每个节点的哈希值。

在接下来的部分中，我们将介绍 SirixDB 的核心低级 JSON API。

## 5. 使用单一资源创建数据库

首先，我们要展示如何使用单一资源创建数据库。该资源将从 JSON 文件导入，并以 SirixDB 的内部二进制格式永久存储：

```java
var pathToJsonFile = Paths.get("jsonFile");
var databaseFile = Paths.get("database");

Databases.createJsonDatabase(new DatabaseConfiguration(databaseFile));

try (var database = Databases.openJsonDatabase(databaseFile)) {
    database.createResource(ResourceConfiguration.newBuilder("resource").build());

    try (var manager = database.openResourceManager("resource");
         var wtx = manager.beginNodeTrx()) {
        wtx.insertSubtreeAsFirstChild(JsonShredder.createFileReader(pathToJsonFile));
        wtx.commit();
    }
}
```

我们首先创建一个数据库。然后我们打开数据库并创建第一个资源。存在用于创建资源的各种选项([请参阅官方文档](https://sirix.io/documentation.html))。

然后，我们在资源上打开一个读写事务以导入 JSON 文件。该事务提供了一个光标，用于通过moveToX方法进行导航。此外，事务提供了插入、删除或修改节点的方法。请注意，XML API 甚至提供了在资源中移动节点和从其他 XML 资源节点的方法。

为了正确关闭打开的读写事务、资源管理器和数据库，我们使用Java的[try-with-resources](https://www.baeldung.com/java-try-with-resources)语句。

我们举例说明了在 JSON 数据上创建数据库和资源，但是创建 XML 数据库和资源几乎是相同的。

在下一节中，我们将打开数据库中的资源并显示导航轴和方法。

## 6. 打开数据库中的资源并导航

### 6.1. JSON资源中的预订导航

为了浏览树结构，我们可以在提交后重用读写事务。然而，在下面的代码中，我们将再次打开资源并在最近的修订版上开始一个只读事务：

```java
try (var database = Databases.openJsonDatabase(databaseFile);
     var manager = database.openResourceManager("resource");
     var rtx = manager.beginNodeReadOnlyTrx()) {
    
    new DescendantAxis(rtx, IncludeSelf.YES).forEach((unused) -> {
        switch (rtx.getKind()) {
            case OBJECT:
            case ARRAY:
                LOG.info(rtx.getDescendantCount());
                LOG.info(rtx.getChildCount());
                LOG.info(rtx.getHash());
                break;
            case OBJECT_KEY:
                LOG.info(rtx.getName());
                break;
            case STRING_VALUE:
            case BOOLEAN_VALUE:
            case NUMBER_VALUE:
            case NULL_VALUE:
                LOG.info(rtx.getValue());
                break;
            default:
        }
    });
}
```

我们使用后代轴按预定顺序(深度优先)遍历所有节点。根据资源配置，默认情况下，节点哈希是为所有节点自下而上构建的。

数组节点和对象节点没有名称也没有值。我们可以使用相同的轴遍历 XML 资源，只是节点类型不同。

SirixDB 提供了一堆轴，例如所有 XPath 轴，用于在 XML 和 JSON 资源中导航。此外，它还提供了一个LevelOrderAxis、一个PostOrderAxis 、一个用于链接轴的NestedAxis和几个ConcurrentAxis变体以同时和并行地获取节点。

在下一节中，我们将展示如何使用VisitorDescendantAxis，它根据节点访问者的返回类型进行预序迭代。

### 6.2. 访客后代轴

由于基于不同节点类型定义行为非常普遍，因此 SirixDB 使用[访问者模式](https://www.baeldung.com/java-visitor-pattern)。

我们可以将访问者指定为名为VisitorDescendantAxis的特殊轴的构建器参数。 对于每种类型的节点，都有一个等效的访问方法。例如，对于对象键节点，它是方法VisitResult visit(ImmutableObjectKeyNode node)。

每个方法都返回VisitResult类型的值。VisitResult接口的唯一实现是以下枚举：

```java
public enum VisitResultType implements VisitResult {
    SKIPSIBLINGS,
    SKIPSUBTREE,
    CONTINUE,
    TERMINATE
}
```

VisitorDescendantAxis按预定顺序遍历树结构。它使用VisitResultType来指导遍历：

-   SKIPSIBLINGS意味着遍历应该继续而不访问光标指向的当前节点的右兄弟节点
-   SKIPSUBTREE表示不访问本节点的后代继续
-   如果遍历应该按预定顺序继续，我们使用CONTINUE
-   我们也可以使用TERMINATE立即终止遍历

Visitor接口中每个方法的默认实现都会为每个节点类型返回VisitResultType.CONTINUE。因此，我们只需要为我们感兴趣的节点实现方法。如果我们已经实现了一个实现了名为MyVisitor的Visitor接口的类，我们可以按以下方式使用VisitorDescendantAxis ：

```java
var axis = VisitorDescendantAxis.newBuilder(rtx)
  .includeSelf()
  .visitor(new MyVisitor())
  .build();

while (axis.hasNext()) axis.next();
```

为遍历中的每个节点调用MyVisitor中的方法。参数rtx是只读事务。遍历从光标当前指向的节点开始。

### 6.3. 时间旅行轴

SirixDB 最显着的特征之一是彻底的版本控制。因此，SirixDB 不仅提供了各种轴在一个修订版中迭代树结构。我们还可以使用以下轴之一进行时间导航：

-   第一轴
-   最后一个轴
-   上一个轴
-   下一轴
-   所有时间轴
-   未来轴
-   过去轴

构造函数将资源管理器和事务游标作为参数。光标导航到每个修订版中的相同节点。

如果轴中的另一个修订版——以及相应修订版中的节点——存在，则轴返回一个新事务。返回值是在各个修订版上打开的只读事务，而游标指向不同修订版中的同一节点。

我们将为PastAxis展示一个简单示例：

```java
var axis = new PastAxis(resourceManager, rtx);
if (axis.hasNext()) {
    var trx = axis.next();
    // Do something with the transactional cursor.
}
```

### 6.4. 过滤

SirixDB 提供了几个过滤器，我们可以将它们与FilterAxis结合使用。例如，以下代码遍历对象节点的所有子节点，并使用键“a”过滤对象键节点，如{“a”:1, “b”: “foo”}中所示。

```java
new FilterAxis<JsonNodeReadOnlyTrx>(new ChildAxis(rtx), new JsonNameFilter(rtx, "a"))
```

FilterAxis可选择将多个过滤器作为其参数。过滤器要么是JsonNameFilter，用于过滤对象键中的名称，要么是节点类型过滤器之一：ObjectFilter、ObjectRecordFilter、ArrayFilter、StringValueFilter、NumberValueFilter、BooleanValueFilter和NullValueFilter。

该轴可以按如下方式用于 JSON 资源，以按名称为“foobar”的对象键名称进行过滤：

```java
var axis = new VisitorDescendantAxis.Builder(rtx).includeSelf().visitor(myVisitor).build();
var filter = new JsonNameFilter(rtx, "foobar");
for (var filterAxis = new FilterAxis<JsonNodeReadOnlyTrx>(axis, filter); filterAxis.hasNext();) {
    filterAxis.next();
}
```

或者，我们可以简单地在轴上流式传输(根本不使用FilterAxis)，然后按谓词进行过滤。

在以下示例中，rtx的类型为NodeReadOnlyTrx ：

```java
var axis = new PostOrderAxis(rtx);
var axisStream = StreamSupport.stream(axis.spliterator(), false);

axisStream.filter((unusedNodeKey) -> new JsonNameFilter(rtx, "a"))
  .forEach((unused) -> / Do something with the transactional cursor /);
```

## 7.修改数据库中的资源

显然，我们希望能够修改资源。SirixDB 在每次提交期间存储一个新的紧凑快照。

打开资源后，我们必须像之前看到的那样启动单个读写事务。

### 7.1. 简单的更新操作

一旦我们导航到我们想要修改的节点，我们就可以根据节点类型更新名称或值：

```java
if (wtx.isObjectKey()) wtx.setObjectKeyName("foo");
if (wtx.isStringValue()) wtx.setStringValue("foo");
```

我们可以通过insertObjectRecordAsFirstChild和insertObjectRecordAsRightSibling插入新的对象记录。所有节点类型都存在类似的方法。对象记录由两个节点组成：对象键节点和对象值节点。

SirixDB 检查一致性，因此如果在特定节点类型上不允许方法调用，它会抛出未经检查的SirixUsageException 。

例如，如果游标位于对象节点上，则对象记录(即键/值对)只能作为第一个子项插入。我们使用insertObjectRecordAsX方法插入对象键节点以及其他节点类型之一作为值。

我们还可以链接更新方法——对于这个例子，wtx位于一个对象节点上：

```java
wtx.insertObjectRecordAsFirstChild("foo", new StringValue("bar"))
   .moveToParent().trx()
   .insertObjectRecordAsRightSibling("baz", new NullValue());
```

首先，我们插入一个名为“foo”的对象键节点作为对象节点的第一个子节点。然后，创建一个StringValueNode作为新创建的对象记录节点的第一个子节点。

方法调用后光标移动到值节点。因此，我们首先必须将光标移动到对象键节点，再次是父节点。然后，我们能够插入下一个对象键节点及其子节点NullValueNode作为右兄弟节点。

### 7.2. 批量插入

也存在更复杂的批量插入方法，正如我们在导入 JSON 数据时已经看到的那样。SirixDB 提供了一种将 JSON 数据作为第一个孩子 ( insertSubtreeAsFirstChild ) 和右兄弟 ( insertSubtreeAsRightSibling ) 插入的方法。

要插入一个基于字符串的新子树，我们可以使用：

```java
var json = "{"foo": "bar","baz": [0, "bla", true, null]}";
wtx.insertSubtreeAsFirstChild(JsonShredder.createStringReader(json));
```

JSON API 目前不提供子树的可能性。但是，XML API 可以。我们能够从 SirixDB 中的另一个 XML 资源子树：

```java
wtx.copySubtreeAsRightSibling(rtx);
```

在这里，只读事务 ( rtx ) 当前指向的节点及其子树被为读写事务 ( wtx ) 指向的节点的新右兄弟节点。

SirixDB 始终应用内存中的更改，然后在事务提交期间将它们刷新到磁盘或闪存驱动器。唯一的例外是如果内存中缓存由于内存限制而不得不将一些条目逐出到临时文件中。

我们可以commit()或rollback()事务。请注意，我们可以在两个方法调用之一之后重用事务。

在调用批量插入时，SirixDB 还在底层应用了一些优化。

在下一节中，我们将看到有关如何启动读写事务的其他可能性。

### 7.3. 启动读写事务

正如我们所见，我们可以开始读写事务并通过调用commit方法创建新的快照。但是，我们也可以启动一个自动提交的事务游标：

```java
resourceManager.beginNodeTrx(TimeUnit.SECONDS, 30);
resourceManager.beginNodeTrx(1000);
resourceManager.beginNodeTrx(1000, TimeUnit.SECONDS, 30);
```

我们要么每 30 秒自动提交，每 1000 次修改后，要么每 30 秒和每 1000 次修改后自动提交。

我们还可以启动读写事务，然后恢复到以前的修订版，我们可以将其作为新修订版提交：

```java
resourceManager.beginNodeTrx().revertTo(2).commit();
```

其间的所有修订仍然可用。一旦我们提交了多个修订版，我们就可以通过指定确切的修订版号或时间戳来打开特定的修订版：

```java
var rtxOpenedByRevisionNumber = resourceManager.beginNodeReadOnlyTrx(2);

var dateTime = LocalDateTime.of(2019, Month.JUNE, 15, 13, 39);
var instant = dateTime.atZone(ZoneId.of("Europe/Berlin")).toInstant();
var rtxOpenedByTimestamp = resourceManager.beginNodeReadOnlyTrx(instant);
```

## 8.比较修订

要计算资源的任意两个修订版之间的差异，一旦存储在 SirixDB 中，我们可以调用差异算法：

```java
DiffFactory.invokeJsonDiff(
  new DiffFactory.Builder(
    resourceManager,
    2,
    1,
    DiffOptimized.HASHED,
    ImmutableSet.of(observer)));
```

构建器的第一个参数是资源管理器，我们已经用过好几次了。接下来的两个参数是要比较的修订。第四个参数是一个枚举，我们用它来确定 SirixDB 是否应该考虑哈希以加速差异计算。

如果一个节点由于 SirixDB 中的更新操作而发生变化，所有祖先节点也会调整它们的哈希值。如果两个修订版中的哈希和节点键相同，SirixDB 在遍历两个修订版时会跳过子树，因为当我们指定DiffOptimized.HASHED时子树没有变化。

一组不可变的观察者是最后一个参数。观察者必须实现以下接口：

```java
public interface DiffObserver {
    void diffListener(DiffType diffType, long newNodeKey, long oldNodeKey, DiffDepth depth);
    void diffDone();
}
```

作为第一个参数的diffListener方法指定每个修订版中两个节点之间遇到的差异类型。接下来的两个参数是两个修订版中比较节点的稳定唯一节点标识符。最后一个参数depth指定了 SirixDB 刚刚比较的两个节点的深度。

## 9.序列化为JSON

在某个时间点，我们想要将 SirixDBs 二进制编码中的 JSON 资源序列化回 JSON：

```java
var writer = new StringWriter();
var serializer = new JsonSerializer.Builder(resourceManager, writer).build();
serializer.call();
```

要序列化修订版 1 和 2：

```java
var serializer = new
JsonSerializer.Builder(resourceManager, writer, 1, 2).build();
serializer.call();
```

以及所有存储的修订：

```java
var serializer = new
JsonSerializer.Builder(resourceManager, writer, -1).build();
serializer.call();

```

## 10.总结

我们已经了解了如何使用低级事务游标 API 来管理 SirixDB 中的 JSON 数据库和资源。更高级别的 API 隐藏了一些复杂性。