## 1. 概述

在这个简短的教程中，我们将了解[JavaFX ](https://www.baeldung.com/javafx)Button组件并了解我们如何处理用户交互。

## 2. 应用设置

首先，让我们创建一个能够专注于事件处理程序的小型应用程序。让我们从创建一个包含按钮的简单[FXML布局开始：](https://www.baeldung.com/javafx#fxml)

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.?>
<?import javafx.scene.layout.?>
<BorderPane xmlns:fx="http://javafx.com/fxml"
    xmlns="http://javafx.com/javafx"
    fx:controller="com.baeldung.button.eventhandler.ButtonEventHandlerController"
    prefHeight="200.0" prefWidth="300.0">
    <center>
        <Button fx:id="button" HBox.hgrow="ALWAYS"/>
    </center>

    <bottom>
        <Label fx:id="label" text="Test label"/>
    </bottom>
</BorderPane>
```

让我们创建ButtonEventHandlerController类。它负责连接 UI 元素和应用程序逻辑。我们将在初始化方法中设置按钮的标签：

```java
public class ButtonEventHandlerController {

    private static final Logger logger = LoggerFactory.getLogger(ButtonEventHandlerController.class);

    @FXML
    private Button button;

    @FXML
    private Label label;

    @FXML
    private void initialize() {
        button.setText("Click me");
    }
}
```

让我们启动应用程序。我们应该在中心看到一个标题为“Click me”的按钮，在窗口底部看到一个测试标签：

[![应用预览](https://www.baeldung.com/wp-content/uploads/2022/01/javafx_button_event_handler_app_preview-1.png)](https://www.baeldung.com/wp-content/uploads/2022/01/javafx_button_event_handler_app_preview-1.png)

## 3.点击事件

让我们从处理简单的点击事件开始，并向初始化方法添加事件处理程序：

```java
button.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
        logger.info("OnAction {}", event);
    }
});
```

现在让我们测试一下。当我们点击按钮时，会出现一条新的日志消息：

```plaintext
INFO c.b.b.e.ButtonEventHandlerController - OnAction javafx.event.ActionEvent[source=Button[id=searchButton, styleClass=button]'Click me']
```

因为事件处理程序接口只有一个方法，我们可以将其视为[函数式接口](https://www.baeldung.com/java-8-functional-interfaces)并用单个 lambda 表达式替换这些行以使我们的代码更易于阅读：

```java
searchButton.setOnAction(event -> logger.info("OnAction {}", event));
```

让我们尝试添加另一个点击事件处理程序。我们可以简单地这一行并更改日志消息，以便在我们测试应用程序时能够看到差异：

```java
button.setOnAction(event -> logger.info("OnAction {}", event));
button.setOnAction(event -> logger.info("OnAction2 {}", event));
```

现在，当我们点击按钮时，我们只会看到“OnAction 2”消息。发生这种情况是因为第二个setOnAction方法调用用第二个事件处理程序替换了第一个事件处理程序。

## 4.不同的事件

我们也可以处理其他事件类型，例如鼠标按下/释放、拖动和键盘事件。

让我们为按钮添加悬停效果。当光标开始悬停在按钮上时，我们将显示一个阴影，并在光标离开时移除效果：

```java
Effect shadow = new DropShadow();
searchButton.setOnMouseEntered(e -> searchButton.setEffect(shadow));
searchButton.setOnMouseExited(e -> searchButton.setEffect(null));

```

## 5. 重用事件处理器

在某些情况下，我们可能希望多次使用同一个事件处理程序。让我们创建一个事件处理程序，当我们单击鼠标辅助按钮时，它将增加按钮的字体大小：

```java
EventHandler<MouseEvent> rightClickHandler = event -> {
    if (MouseButton.SECONDARY.equals(event.getButton())) {
        button.setFont(new Font(button.getFont().getSize() + 1));
    }
};
```

但是，它没有任何功能，因为我们没有将它与任何事件相关联。让我们将此事件处理程序用于按钮和标签的鼠标按下事件：

```java
button.setOnMousePressed(rightClickHandler);
label.setOnMousePressed(rightClickHandler);
```

现在，当我们测试应用程序并在标签或按钮上单击辅助鼠标按钮时，我们看到字体大小增加了。

## 六. 总结

我们学习了如何向 JavaFX 按钮添加事件处理程序并根据事件类型执行不同的操作。