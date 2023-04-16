## 1. 概述

[Vaadin](https://vaadin.com/)是用于创建 Web 用户界面的服务器端Java框架。

在本教程中，我们将探讨如何在基于Spring Boot的后端上使用基于 Vaadin 的 UI。有关 Vaadin 的介绍，请参阅 [本](https://www.baeldung.com/vaadin)教程。

## 2.设置

让我们从将 Maven 依赖项添加到标准Spring Boot应用程序开始：

```xml
<dependency>
    <groupId>com.vaadin</groupId>
    <artifactId>vaadin-spring-boot-starter</artifactId>
</dependency>
```

Vaadin 也是 [Spring Initializer](https://start.spring.io/)认可的依赖项。

本教程使用的 Vaadin 版本比 starter 模块引入的默认版本更新。要使用较新的版本，只需像这样定义 Vaadin 物料清单 (BOM)：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.vaadin</groupId>
            <artifactId>vaadin-bom</artifactId>
            <version>10.0.11</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## 3.后台服务

我们将使用具有firstName和lastName属性的Employee实体对其执行 CRUD 操作：

```java
@Entity
public class Employee {

    @Id
    @GeneratedValue
    private Long id;

    private String firstName;
    private String lastName;
}
```

这是简单的、对应的 Spring Data 存储库——用于管理 CRUD 操作：

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByLastNameStartsWithIgnoreCase(String lastName);
}
```

我们在EmployeeRepository接口上声明查询方法findByLastNameStartsWithIgnoreCase 。它将返回与lastName匹配的Employee列表。

我们还用一些示例Employee s预填充数据库：

```java
@Bean
public CommandLineRunner loadData(EmployeeRepository repository) {
    return (args) -> {
        repository.save(new Employee("Bill", "Gates"));
        repository.save(new Employee("Mark", "Zuckerberg"));
        repository.save(new Employee("Sundar", "Pichai"));
        repository.save(new Employee("Jeff", "Bezos"));
    };
}
```

## 4.我需要用户界面

### 4.1. 主视图类

MainView类是 Vaadin 的 UI 逻辑的入口点。注解@Route告诉Spring Boot自动拾取它并显示在 Web 应用程序的根目录中：

```java
@Route
public class MainView extends VerticalLayout {
    private EmployeeRepository employeeRepository;
    private EmployeeEditor editor;
    Grid<Employee> grid;
    TextField filter;
    private Button addNewBtn;
}
```

我们可以通过为@Route注解提供参数来自定义显示视图的 URL：

```java
@Route(value="myhome")
```

该类使用以下 UI 组件显示在页面上：

EmployeeEditor 编辑器——显示用于提供员工信息创建和编辑的Employee表单。

Grid<Employee> grid – 显示员工列表的网格

TextField filter – 用于输入姓氏的文本字段，将根据其筛选 gird

按钮 addNewBtn – 添加新员工的按钮。显示EmployeeEditor编辑器。

它在内部使用employeeRepository来执行 CRUD 操作。

### 4.2. 将组件连接在一起

MainView扩展了VerticalLayout。VerticalLayout是一个组件容器，它按照添加的顺序(垂直)显示子组件。

接下来，我们初始化并添加组件。

我们为带有 + 图标的按钮提供标签。

```java
this.grid = new Grid<>(Employee.class);
this.filter = new TextField();
this.addNewBtn = new Button("New employee", VaadinIcon.PLUS.create());
```

我们使用HorizontalLayout水平排列过滤器文本字段和按钮。然后将这个布局、网格和编辑器添加到父垂直布局中：

```java
HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
add(actions, grid, editor);
```

提供网格高度和列名。我们还在文本字段中添加帮助文本：

```java
grid.setHeight("200px");
grid.setColumns("id", "firstName", "lastName");
grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);

filter.setPlaceholder("Filter by last name");
```

在应用程序启动时，UI 将如下所示：

[![桶1](https://www.baeldung.com/wp-content/uploads/2018/08/vaadin1.png)](https://www.baeldung.com/wp-content/uploads/2018/08/vaadin1.png)

### 4.3. 向组件添加逻辑

我们将ValueChangeMode.EAGER设置为过滤器文本字段。每次在客户端更改值时，这会将值同步到服务器。

我们还为值更改事件设置了一个侦听器，它根据过滤器中提供的文本返回过滤后的员工列表：

```java
filter.setValueChangeMode(ValueChangeMode.EAGER);
filter.addValueChangeListener(e -> listEmployees(e.getValue()));
```

在网格中选择一行时，我们将显示Employee表单，允许用户编辑名字和姓氏：

```java
grid.asSingleSelect().addValueChangeListener(e -> {
    editor.editEmployee(e.getValue());
});
```

单击添加新员工按钮时，我们将显示空白的员工表单：

```java
addNewBtn.addClickListener(e -> editor.editEmployee(new Employee("", "")));
```

最后，我们监听编辑器所做的更改，并使用来自后端的数据刷新网格：

```java
editor.setChangeHandler(() -> {
    editor.setVisible(false);
    listEmployees(filter.getValue());
});
```

listEmployees函数获取过滤后的Employee列表并更新网格：

```java
void listEmployees(String filterText) {
    if (StringUtils.isEmpty(filterText)) {
        grid.setItems(employeeRepository.findAll());
    } else {
        grid.setItems(employeeRepository.findByLastNameStartsWithIgnoreCase(filterText));
    }
}
```

### 4.4. 建立表格

我们将为用户使用一个简单的表单来添加/编辑员工：

```java
@SpringComponent
@UIScope
public class EmployeeEditor extends VerticalLayout implements KeyNotifier {

    private EmployeeRepository repository;
    private Employee employee;

    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");

    Button save = new Button("Save", VaadinIcon.CHECK.create());
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete", VaadinIcon.TRASH.create());

    HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);
    Binder<Employee> binder = new Binder<>(Employee.class);
    private ChangeHandler changeHandler;
}
```

@SpringComponent只是Springs @Component注解的别名，以避免与 Vaadins Component类发生冲突。

@UIScope将bean绑定到当前的 Vaadin UI。

目前，被编辑的Employee存储在employee成员变量中。我们通过firstName和lastName文本字段捕获Employee属性。

表单有三个按钮——保存、取消和删除。

一旦所有组件连接在一起，表格将如下所示用于行选择：

[![桶2](https://www.baeldung.com/wp-content/uploads/2018/08/vaadin2.png)](https://www.baeldung.com/wp-content/uploads/2018/08/vaadin2.png)

我们使用Binder，它使用命名约定将表单字段与Employee属性绑定：

```java
binder.bindInstanceFields(this);
```

我们根据用户操作调用相应的 EmployeeRepositor 方法：

```java
void delete() {
    repository.delete(employee);
    changeHandler.onChange();
}

void save() {
    repository.save(employee);
    changeHandler.onChange();
}
```

## 5.总结

在本文中，我们使用Spring Boot和 Spring Data JPA 编写了一个功能齐全的 CRUD UI 应用程序来实现持久化。