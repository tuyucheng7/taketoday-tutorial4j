## 1. 简介

在本文中，我们介绍与Java平台模块系统一起引入的java.lang.Module API。

此API提供了一种以编程方式访问模块、从模块中检索特定信息以及通常使模块及其Module Descriptor的方法。

## 2.读取模块信息

Module类代表命名和未命名的模块，命名模块有一个名称，由Java虚拟机在创建模块层时使用模块图作为定义来构造。

未命名模块没有名称，每个ClassLoader都有一个名称。不在命名模块中的所有类都是与其类加载器相关的未命名模块的成员。

Module类的有趣之处在于它公开了允许我们从模块中检索信息的方法，例如模块名称、模块类加载器和模块中的包。

让我们看看如何找出一个模块是命名的还是未命名的。

### 2.1 命名或未命名

使用isNamed()方法，我们可以识别模块是否被命名。让我们看看如何查看给定类(如HashMap)是否是命名模块的一部分，以及如何检索其名称：

```java
Module javaBaseModule = HashMap.class.getModule();

assertThat(javaBaseModule.isNamed(), is(true));
assertThat(javaBaseModule.getName(), is("java.base"));
```

现在让我们定义一个Person类：

```java
public class Person {
    private String name;

    // constructor, getters and setters
}
```

与HashMap类所做的一样，我们可以检查Person类是否是命名模块的一部分：

```java
Module module = Person.class.getModule();

assertThat(module.isNamed(), is(false));
assertThat(module.getName(), is(nullValue()));
```

### 2.2 包

在使用模块时，了解模块中哪些包可用可能很重要。让我们看看如何检查给定的包，例如java.lang.annotation是否包含在给定的模块中：

```java
assertTrue(javaBaseModule.getPackages().contains("java.lang.annotation"));
assertFalse(javaBaseModule.getPackages().contains("java.sql"));
```

### 2.3 注解

同样，对于包，可以使用getAnnotations()方法检索模块中存在的注解。如果命名模块中不存在注解，则该方法将返回一个空数组。

让我们看看java.base模块中有多少注解：

```java
assertThat(javaBaseModule.getAnnotations().length, is(0));
```

在未命名的模块上调用时，getAnnotations()方法将返回一个空数组。

### 2.4 类加载器

得益于Module类中可用的getClassLoader()方法，我们可以检索给定模块的ClassLoader ：

```java
assertThat(
    module.getClassLoader().getClass().getName(), 
    is("jdk.internal.loader.ClassLoaders$AppClassLoader")
);
```

### 2.5 层

可以从模块中提取的另一个有价值的信息是ModuleLayer，它表示Java虚拟机中的一层模块。

模块层通知JVM可以从模块加载的类，通过这种方式，JVM准确地知道每个类是哪个模块的成员。

ModuleLayer包含与其配置、父层和层内可用模块集相关的信息。让我们看看如何检索给定模块的ModuleLayer ：

```java
ModuleLayer javaBaseModuleLayer = javaBaseModule.getLayer();
```

一旦我们得到ModuleLayer，我们就可以访问它的信息：

```java
assertTrue(javaBaseModuleLayer.configuration().findModule("java.base").isPresent());
```

一个特例是boot层，它是在Java虚拟机启动时创建的，boot层是唯一包含java.base模块的层。

## 3. 处理ModuleDescriptor

ModuleDescriptor描述了一个命名模块并定义了获取其每个组件的方法。ModuleDescriptor对象是不可变且安全的，可以由多个并发线程使用。

### 3.1 检索ModuleDescriptor

由于ModuleDescriptor与Module紧密相连，因此可以直接从Module中检索它：

```java
ModuleDescriptor moduleDescriptor = javaBaseModule.getDescriptor();
```

### 3.2 创建模块描述符

也可以使用ModuleDescriptor.Builder类或通过读取模块声明的二进制形式module-info.class创建模块描述符。

让我们看看如何使用ModuleDescriptor.Builder API创建模块描述符：

```java
ModuleDescriptor.Builder moduleBuilder = ModuleDescriptor
    .newModule("tuyucheng.base");

ModuleDescriptor moduleDescriptor = moduleBuilder.build();

assertThat(moduleDescriptor.name(), is("tuyucheng.base"));
```

这样，我们创建了一个普通模块，但如果我们想创建一个开放模块或自动模块，我们可以分别使用newOpenModule()或newAutomaticModule()方法。

### 3.3 对模块进行分类

一个模块描述符描述一个普通的、开放的或自动的模块，根据ModuleDescriptor中提供的方法，可以识别模块的类型：

```java
ModuleDescriptor moduleDescriptor = javaBaseModule.getDescriptor();

assertFalse(moduleDescriptor.isAutomatic());
assertFalse(moduleDescriptor.isOpen());
```

### 3.4 检索Requires

使用模块描述符，可以检索表示模块依赖关系的Requires集合。这可以使用requires()方法：

```java
Set<Requires> javaBaseRequires = javaBaseModule.getDescriptor().requires();
Set<Requires> javaSqlRequires = javaSqlModule.getDescriptor().requires();

Set<String> javaSqlRequiresNames = javaSqlRequires.stream()
    .map(Requires::name)
    .collect(Collectors.toSet());

assertThat(javaBaseRequires, empty());
assertThat(javaSqlRequiresNames, hasItems("java.base", "java.xml", "java.logging"));
```

所有模块(除了java.base)，都将java.base模块作为依赖项。但是，如果模块是自动模块，则依赖项集将为空，但java.base除外。

### 3.5 检索Provides

使用provide()方法可以检索模块提供的服务列表：

```java
Set<Provides> javaBaseProvides = javaBaseModule.getDescriptor().provides();
Set<Provides> javaSqlProvides = javaSqlModule.getDescriptor().provides();

Set<String> javaBaseProvidesService = javaBaseProvides.stream()
    .map(Provides::service)
    .collect(Collectors.toSet());

assertThat(javaBaseProvidesService, hasItem("java.nio.file.spi.FileSystemProvider"));
assertThat(javaSqlProvides, empty());
```

### 3.6 检索Exports

使用exports()方法，我们可以找出模块导出了哪些包作为公共API：

```java
Set<Exports> javaSqlExports = javaSqlModule.getDescriptor().exports();

Set<String> javaSqlExportsSource = javaSqlExports.stream()
    .map(Exports::source)
    .collect(Collectors.toSet());

assertThat(javaSqlExportsSource, hasItems("java.sql", "javax.sql"));
```

作为一种特殊情况，如果模块是自动模块，则导出的包集将为空。

### 3.7 检索Uses

使用uses()方法，可以检索模块的一组服务依赖项：

```java
Set<String> javaSqlUses = javaSqlModule.getDescriptor().uses();

assertThat(javaSqlUses, hasItem("java.sql.Driver"));
```

如果模块是自动模块，则服务依赖集将为空。

### 3.8 检索Opens

每当我们想要检索模块的公开包列表时，我们可以使用opens()方法：

```java
Set<Opens> javaBaseUses = javaBaseModule.getDescriptor().opens();
Set<Opens> javaSqlUses = javaSqlModule.getDescriptor().opens();

assertThat(javaBaseUses, empty());
assertThat(javaSqlUses, empty());
```

如果模块是打开的或自动的，则该集合将为空。

## 4. 处理模块

使用Module API，除了从模块中读取信息之外，我们还可以更新模块定义。

### 4.1 添加Exports

让我们看看如何更新模块，从给定模块导出给定包：

```java
Module updatedModule = module.addExports(
    "cn.tuyucheng.taketoday.java9.modules", javaSqlModule);

assertTrue(updatedModule.isExported("cn.tuyucheng.taketoday.java9.modules"));
```

只有当调用者的模块是代码所属的模块时，才能执行此操作。顺便说明一下，如果包已经由模块导出或者模块是打开的，则不会产生任何影响。

### 4.2 添加Reads

当我们想要更新模块以读取给定模块时，我们可以使用addReads()方法：

```java
Module updatedModule = module.addReads(javaSqlModule);

assertTrue(updatedModule.canRead(javaSqlModule));
```

如果我们添加模块本身，则此方法不会执行任何操作，因为所有模块都会自行读取。

同样，如果模块是一个未命名的模块或者这个模块已经读取了另一个模块，则该方法不会执行任何操作。

### 4.3 添加Opens

当我们想更新一个模块，该模块至少打开了一个包给调用方模块时，我们可以使用addOpens()将包打开到另一个模块：

```java
Module updatedModule = module.addOpens(
    "cn.tuyucheng.taketoday.java9.modules", javaSqlModule);

assertTrue(updatedModule.isOpen("cn.tuyucheng.taketoday.java9.modules", javaSqlModule));
```

如果包已经对给定模块打开，则此方法什么也不做。

### 4.4 添加Uses

每当我们想要更新一个添加服务依赖项的模块时，我们都可以选择addUses()方法：

```java
Module updatedModule = module.addUses(Driver.class);

assertTrue(updatedModule.canUse(Driver.class));
```

在未命名的模块或自动模块上调用此方法时不执行任何操作。

## 5. 总结

在本文中，我们介绍了java.lang.Module API的使用，学习了如何检索模块的信息、如何使用ModuleDescriptor访问有关模块的附加信息以及如何操作它。