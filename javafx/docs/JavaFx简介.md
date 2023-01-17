## 1. 简介

JavaFX 是一个用于使用Java构建富客户端应用程序的库。它提供了一个 API，用于设计在几乎所有支持Java的设备上运行的 GUI 应用程序。

在本教程中，我们将重点介绍并介绍其一些关键功能。

## 2.JavaFX API

在Java8、9 和 10 中，无需额外设置即可开始使用 JavaFX 库。从 JDK 11 开始，该项目将从 JDK 中删除。

### 2.1. 建筑学

JavaFX 使用硬件加速图形管道进行渲染，称为Prism。更重要的是，为了充分加速图形使用，它通过内部使用DirectX和OpenGL来利用软件或硬件渲染机制。

JavaFX 有一个依赖于平台的Glass窗口工具包层来连接到本机操作系统。它使用操作系统的事件队列来安排线程的使用。此外，它异步处理窗口、事件、计时器。

媒体和Web引擎支持媒体播放和 HTML/CSS 支持。

让我们看看JavaFX 应用程序的[主要结构是什么样的：](https://docs.oracle.com/javafx/2/get_started/img/helloworld_scenegraph.png)

![你好世界场景图](https://www.baeldung.com/wp-content/uploads/2018/04/helloworld_scenegraph.png)

 

在这里，我们注意到两个主要容器：

-   Stage是应用程序的主要容器和入口点。它代表主窗口并作为start()方法的参数传递。
-   Scene是一个容器，用于保存 UI 元素，例如 Image Views、Buttons、Grids、TextBoxes。

场景可以被替换或切换到另一个场景。这表示分层对象的图形，称为场景图。该层次结构中的每个元素都称为节点。单个节点有其 ID、样式、效果、事件处理程序、状态。

此外，场景还包含布局容器、图像、媒体。

### 2.2. 线程

在系统级别，JVM 创建单独的线程来运行和呈现应用程序：

-   棱镜渲染线程——负责单独渲染场景图。
-   应用程序线程——是任何 JavaFX 应用程序的主线程。所有活动节点和组件都附加到此线程。

### 2.3. 生命周期

javafx.application.Application类具有以下生命周期方法：

-   init() – 在创建应用程序实例后调用。此时，JavaFX API 还没有准备好，所以我们不能在这里创建图形组件。
-   start(Stage stage) – 所有的图形组件都在这里创建。此外，图形活动的主线程从这里开始。
-   stop() – 在应用程序关闭之前调用；例如，当用户关闭主窗口时。在应用程序终止之前覆盖此方法以进行一些清理很有用。

静态launch()方法启动 JavaFX 应用程序。

### 2.4. FXML

JavaFX 使用一种特殊的 FXML 标记语言来创建视图界面。

这提供了一个基于 XML 的结构，用于将视图与业务逻辑分开。XML 在这里更合适，因为它能够非常自然地表示场景图层次结构。

最后，为了加载.fxml文件，我们使用FXMLLoader类，它生成场景层次结构的对象图。

## 3. 开始

为了实用，让我们构建一个允许搜索人员列表的小型应用程序。

首先，让我们添加一个Person模型类——代表我们的领域：

```java
public class Person {
    private SimpleIntegerProperty id;
    private SimpleStringProperty name;
    private SimpleBooleanProperty isEmployed;

    // getters, setters
}
```

请注意，为了包装int、String和布尔值，我们如何使用javafx.beans.property包中的SimpleIntegerProperty、SimpleStringProperty、SimpleBooleanProperty类。

接下来，让我们创建扩展Application抽象类的Main类：

```java
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
          Main.class.getResource("/SearchController.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        Scene scene = new Scene(page);

        primaryStage.setTitle("Title goes here");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

我们的主类覆盖了start()方法，它是程序的入口点。

然后，FXMLLoader将对象图层次结构从SearchController.fxml加载到AnchorPane中。

开始一个新的Scene后，我们将其设置为 primary Stage。我们还为我们的窗口设置标题并显示()它。

请注意，包含main()方法以便能够在没有JavaFX Launcher的情况下运行 JAR 文件是很有用的。

### 3.1. FXML 视图

现在让我们更深入地研究SearchController XML 文件。

对于我们的搜索应用程序，我们将添加一个文本字段以输入关键字和搜索按钮：

```xml
<AnchorPane 
  xmlns:fx="http://javafx.com/fxml"
  xmlns="http://javafx.com/javafx"
  fx:controller="com.baeldung.view.SearchController">
    <children>

        <HBox id="HBox" alignment="CENTER" spacing="5.0">
            <children>
                <Label text="Search Text:"/>
                <TextField fx:id="searchField"/>
                <Button fx:id="searchButton"/>
            </children>
        </HBox>

        <VBox fx:id="dataContainer"
              AnchorPane.leftAnchor="10.0"
              AnchorPane.rightAnchor="10.0"
              AnchorPane.topAnchor="50.0">
        </VBox>

    </children>
</AnchorPane>
```

AnchorPane是这里的根容器，也是图形层次结构的第一个节点。在调整窗口大小时，它会将孩子重新定位到其锚点。fx: controller属性将Java类与标记关联起来。

还有一些其他可用的内置布局：

-   BorderPane – 将布局分为五个部分：顶部、右侧、底部、左侧、中心
-   HBox——将子组件排列在水平面板中
-   VBox——子节点排列成垂直列
-   GridPane – 用于创建包含行和列的网格

在我们的示例中，在水平HBox面板内部，我们使用Label放置文本，使用TextField输入，使用Button。我们使用fx:id标记元素，以便稍后在Java代码中使用它们。

VBox面板是我们将显示搜索结果的地方。

然后，将它们映射到Java字段——我们使用@FXML注解：

```java
public class SearchController {
 
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private VBox dataContainer;
    @FXML
    private TableView tableView;
    
    @FXML
    private void initialize() {
        // search panel
        searchButton.setText("Search");
        searchButton.setOnAction(event -> loadData());
        searchButton.setStyle("-fx-background-color: #457ecd; -fx-text-fill: #ffffff;");

        initTable();
    }
}
```

填充@FXML注解字段后，将自动调用initialize() 。在这里，我们能够对 UI 组件执行进一步的操作——例如注册事件侦听器、添加样式或更改文本属性。

在initTable()方法中，我们将创建包含结果的表，有 3 列，并将其添加到dataContainer VBox：

```java
private void initTable() {        
    tableView = new TableView<>();
    TableColumn id = new TableColumn("ID");
    TableColumn name = new TableColumn("NAME");
    TableColumn employed = new TableColumn("EMPLOYED");
    tableView.getColumns().addAll(id, name, employed);
    dataContainer.getChildren().add(tableView);
}
```

最后，此处描述的所有这些逻辑将产生以下窗口：

[![盒子](https://www.baeldung.com/wp-content/uploads/2018/04/HBox.png)](https://www.baeldung.com/wp-content/uploads/2018/04/HBox.png)

## 4.绑定API

现在已经处理了视觉方面的问题，让我们开始研究绑定数据。

绑定 API 提供了一些接口，用于在另一个对象的值发生更改时通知对象。

我们可以使用bind()方法或通过添加侦听器来绑定一个值。

单向绑定仅提供一个方向的绑定：

```java
searchLabel.textProperty().bind(searchField.textProperty());
```

在这里，搜索字段中的任何更改都会更新标签的文本值。

相比之下，双向绑定在两个方向上同步两个属性的值。

绑定字段的另一种方法是ChangeListeners：

```java
searchField.textProperty().addListener((observable, oldValue, newValue) -> {
    searchLabel.setText(newValue);
});
```

Observable接口允许观察对象值的变化。

为了举例说明这一点，最常用的实现是javafx.collections.ObservableList<T>接口：

```java
ObservableList<Person> masterData = FXCollections.observableArrayList();
ObservableList<Person> results = FXCollections.observableList(masterData);
```

在这里，任何模型更改，如元素的插入、更新或删除，都会立即通知 UI 控件。

masterData列表将包含Person对象的初始列表，结果列表将是我们在搜索时显示的列表。

我们还必须更新initTable()方法以将表中的数据绑定到初始列表，并将每一列连接到Person类字段：

```java
private void initTable() {        
    tableView = new TableView<>(FXCollections.observableList(masterData));
    TableColumn id = new TableColumn("ID");
    id.setCellValueFactory(new PropertyValueFactory("id"));
    TableColumn name = new TableColumn("NAME");
    name.setCellValueFactory(new PropertyValueFactory("name"));
    TableColumn employed = new TableColumn("EMPLOYED");
    employed.setCellValueFactory(new PropertyValueFactory("isEmployed"));

    tableView.getColumns().addAll(id, name, employed);
    dataContainer.getChildren().add(tableView);
}
```

## 5.并发

在场景图中使用 UI 组件不是线程安全的，因为它只能从应用程序线程访问。javafx.concurrent包在这里帮助多线程。

让我们看看如何在后台线程中执行数据搜索：

```java
private void loadData() {
    String searchText = searchField.getText();
    Task<ObservableList<Person>> task = new Task<ObservableList<Person>>() {
        @Override
        protected ObservableList<Person> call() throws Exception {
            updateMessage("Loading data");
            return FXCollections.observableArrayList(masterData
                    .stream()
                    .filter(value -> value.getName().toLowerCase().contains(searchText))
                    .collect(Collectors.toList()));
        }
    };
}
```

在这里，我们创建一个一次性任务javafx.concurrent.Task对象并覆盖call()方法。

call()方法完全在后台线程上运行，并将结果返回给应用程序线程。这意味着在此方法中对 UI 组件的任何操作都将引发运行时异常。

但是可以调用updateProgress()、updateMessage()来更新Application线程项。当任务状态转换为 SUCCEEDED 状态时，onSucceeded()事件处理程序从应用程序线程调用：

```java
task.setOnSucceeded(event -> {
    results = task.getValue();
    tableView.setItems(FXCollections.observableList(results));
});

```

在同一个回调中，我们将tableView数据更新为新的结果列表。

Task是Runnable的，所以要启动它，我们只需要使用task参数启动一个新线程：

```java
Thread th = new Thread(task);
th.setDaemon(true);
th.start();
```

setDaemon(true)标志表示线程将在完成工作后终止。

## 6. 事件处理

我们可以将事件描述为应用程序可能感兴趣的操作。

例如，鼠标单击、按键、窗口大小调整等用户操作由javafx.event.Event类或其任何子类处理或通知。

此外，我们区分三种类型的事件：

-   InputEvent – 所有类型的键和鼠标操作，如KEY_PRESSED、KEY_TYPED、KEY_RELEASED或MOUSE_PRESSES、MOUSE_RELEASED
-   ActionEvent – 代表各种动作，例如触发按钮或完成关键帧
-   窗口事件——WINDOW_SHOWING、WINDOW_SHOWN

为了演示，下面的代码片段捕获了在searchField上按下Enter键的事件：

```java
searchField.setOnKeyPressed(event -> {
    if (event.getCode().equals(KeyCode.ENTER)) {
        loadData();
    }
});
```

## 7.风格

我们可以通过对其应用自定义设计来更改 JavaFX 应用程序的 UI。

默认情况下，JavaFX 使用modena.css作为整个应用程序的 CSS 资源。这是jfxrt.jar的一部分。

要覆盖默认样式，我们可以在场景中添加一个样式表：

```java
scene.getStylesheets().add("/search.css");
```

我们也可以使用内联样式；例如，为特定节点设置样式属性：

```java
searchButton.setStyle("-fx-background-color: slateblue; -fx-text-fill: white;");
```

## 八. 总结

这篇简短的文章涵盖了 JavaFX API 的基础知识。我们了解了内部结构并介绍了其架构、生命周期和组件的关键功能。

因此，我们学习了并且现在能够创建一个简单的 GUI 应用程序。