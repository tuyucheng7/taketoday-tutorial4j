## 1. 简介

[JavaFX](https://www.baeldung.com/javafx)是一个强大的工具，旨在为不同平台构建应用程序 UI。它不仅提供 UI 组件，还提供不同的有用工具，例如属性和可观察集合。

ListView组件便于管理集合。也就是说，我们不需要显式定义DataModel或更新ListView元素。一旦ObjervableList发生变化，它就会反映在ListView小部件中。

但是，这种方法需要一种方法来在 JavaFX ListView中显示我们的自定义项。本教程描述了一种设置域对象在ListView中的外观的方法。

## 2. 细胞工厂

### 2.1. 默认行为

默认情况下，JavaFX 中的 ListView使用toString()方法来显示对象。

所以显而易见的方法是覆盖它：

```java
public class Person {
    String firstName;
    String lastName;

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}

```

这种方法适用于学习和概念示例。但是，这不是最好的方法。

首先，我们的域类承担显示实现。因此，这种方法与单一职责原则相矛盾。

其次，其他子系统可能会使用 toString()。例如，我们使用 toString()方法来记录对象的状态。日志可能需要比ListView的项目更多的字段。因此，在这种情况下，单个toString()实现无法满足每个模块的需求。

### 2.2. 在 ListView 中显示自定义对象的单元工厂

让我们考虑一种更好的方法来在 JavaFX ListView中显示我们的自定义对象。

ListView中的每个项目都显示有一个ListCell类的实例。ListCell有一个名为 text的属性。单元格显示其文本值。

因此，要自定义ListCell实例中的文本，我们应该更新其文本属性。我们在哪里可以做？ListCell有一个名为 updateItem的方法。当项目的单元格出现时，它会调用 updateItem。updateItem 方法也会在单元格更改时运行。所以我们应该从默认的ListCell类继承我们自己的实现。在此实现中，我们需要覆盖 updateItem。

但是我们怎样才能让ListView使用我们的自定义实现而不是默认实现呢？

ListView可能有一个单元工厂。细胞工厂默认为空。我们应该设置它来自定义ListView显示对象的方式。

让我们用一个例子来说明细胞工厂：

```java
public class PersonCellFactory implements Callback<ListView<Person>, ListCell<Person>> {
    @Override
    public ListCell<Person> call(ListView<Person> param) {
        return new ListCell<>(){
            @Override
            public void updateItem(Person person, boolean empty) {
                super.updateItem(person, empty);
                if (empty || person == null) {
                    setText(null);
                } else {
                    setText(person.getFirstName() + " " + person.getLastName());
                }
            }
        };
    }
}
```

CellFactory应该实现 JavaFX 回调。JavaFX 中的 回调 接口类似于标准的JavaFunction接口。但是，由于历史原因，JavaFX使用了 Callback接口。

我们应该调用updateItem方法的默认实现 。此实现会触发默认操作，例如将单元格连接到对象并为空列表显示一行。

updateItem方法的默认实现也调用setText。然后设置将在单元格中显示的文本。

### 2.3. 使用自定义小部件在 JavaFX ListView 中显示自定义项

ListCell为我们提供了将自定义小部件设置为内容的机会。要在自定义小部件中显示我们的域对象，我们应该做的就是使用setGraphics()而不是setCell()。

假设，我们必须将每一行显示为一个CheckBox。我们来看看合适的细胞工厂：

```java
public class CheckboxCellFactory implements Callback<ListView<Person>, ListCell<Person>> {
    @Override
    public ListCell<Person> call(ListView<Person> param) {
        return new ListCell<>(){
            @Override
            public void updateItem(Person person, boolean empty) {
                super.updateItem(person, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (person != null) {
                    setText(null);
                    setGraphic(new CheckBox(person.getFirstName() + " " + person.getLastName()));
                } else {
                    setText("null");
                    setGraphic(null);
                }
            }
        };
    }
}
```

在此示例中，我们将 text属性设置为null。如果文本和图形属性都存在，文本将显示在小部件旁边。

当然，我们可以根据自定义元素数据设置CheckBox回调逻辑和其他属性。它需要一些编码，与设置小部件文本的方式相同。

## 3.总结

在本文中，我们考虑了一种在 JavaFX ListView中显示自定义项的方法。我们看到ListView允许一种非常灵活的方式来设置它。我们甚至可以在我们的 ListView 单元格中显示自定义小部件。