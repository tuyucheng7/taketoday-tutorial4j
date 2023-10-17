## 一、概述

在这个简短的教程中，我们将学习**两种在 Java 中获取桌面路径的方法**。第一种方式是使用*System.getProperty()*方法，第二种方式是使用*FileSystemView*类的*getHomeDirectory()*方法 。

## 2. 使用*System.getProperty()*

Java的*System*类提供了*Properties*对象，它存储了当前工作环境的不同配置和属性。***我们对我们案例的\***一个特定属性感兴趣：**包含用户主目录的****user.home**属性。***[可以使用System.getProperty()](https://www.baeldung.com/java-system-get-property-vs-system-getenv)\*****方法****检索此属性**，该方法允许获取特定系统属性的值。

让我们看一个如何使用*user.home*属性并在 Java 中获取桌面路径的示例：

```java
String desktopPath = System.getProperty("user.home") + File.separator +"Desktop";复制
```

要获取桌面路径，**我们必须在***user.home*的**属性值之后添加*****“/Desktop”\***字符串。

## 3. 使用*FileSystemView.getHomeDirectory()*

在 Java 中获取桌面路径的另一种方法是使用*FileSystemView*类，它提供有关文件系统及其组件的有价值的信息。此外，**我们可以使用\*getHomeDirectory()\*方法将用户的主目录作为\*File\*对象**获取。

让我们看看如何利用这个类来获取桌面路径：

```java
FileSystemView view = FileSystemView.getFileSystemView();
File file = view.getHomeDirectory();
String desktopPath = file.getPath();复制
```

在我们的示例中，我们首先使用*getFileSystemView()*方法获取*FileSystemView*类的实例，其次，我们对该实例调用*getHomeDirectory()*方法以获取用户的主目录作为*File*对象。最后，我们使用*File*。*getPath()*方法获取桌面路径作为*String*。

## 4。结论

在这篇快速文章中，我们解释了如何使用两种方法在 Java 中获取桌面路径。 第一个使用*System.getProperty()*方法从系统中获取*user.home属性，第二个使用**FileSystemView类的**getHomeDirectory()*方法 。