## 1. 概述

Maven允许我们使用继承的概念来构建项目，**当一个父POM定义一个插件时，所有的子模块都会继承它**。

但是如果我们不想从父POM继承插件，并且我们不能修改父POM，会发生什么？

在本教程中，我们将介绍几种不同的方法来禁用Maven插件，特别是在父POM中定义的[Maven Enforcer插件](https://maven.apache.org/enforcer/maven-enforcer-plugin/)。

## 2. 我们什么时候禁用父POM中定义的插件？

在我们进一步讨论之前，让我们考虑一下为什么我们可能需要这样做。

**Maven更喜欢约定而不是配置**，我们需要记住，虽然禁用插件对我们来说可能是最快的解决方案，但对项目来说可能不是最好的解决方案。

**当Maven项目的原作者没有预见到我们的情况，而我们又没有办法自己修改父模块时，可能会出现需要在父POM中禁用一个插件的情况**。

假设原作者假设某个特定文件应该始终存在，但是，我们的模块拥有这个文件没有意义。例如，父POM可能会强制在每个模块中存在一个许可证文件，而我们没有。我们不想添加一个可能会造成混淆的空文件，而是希望禁用强制规则。

让我们通过在Maven项目中添加一个实现maven-enforcer-plugin的父模块来设置此场景：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-enforcer-plugin</artifactId>
    <version>3.0.0</version>
</plugin>
```

接下来，让我们向插件添加一个<execution\>，以强制执行一个名为file-that-must-exist.txt的文件必须存在于每个模块的src目录中的规则：

```xml
<executions>
    <execution>
        <id>enforce-file-exists</id>
	<goals>
	    <goal>enforce</goal>
	</goals>
	<configuration>
	    <rules>
	        <requireFilesExist>
		    <files>
		        <file>${project.basedir}/src/file-that-must-exist.txt</file>
		    </files>
		</requireFilesExist>
            </rules>
	</configuration>
    </execution>
</executions>
```

如果file-that-must-exist.txt不存在，则构建将失败。

**由于子模块从其父模块继承插件，因此所有子模块都必须遵守此规则**。

让我们看一下我们可以在我们的子POM中禁用此规则的几种方法。

## 3. 我们如何禁用父POM中定义的插件？

首先，让我们假设重构Maven项目或更改父POM不是可接受的解决方案，**如果我们可以修改父模块，那么我们可以通过在父POM中实现**[pluginManagement部分](http://baeldung.com/maven-plugin-management)**来解决这个问题**。

我们可能无法修改父模块，因为我们不拥有该项目，因此我们无权在我们的模块之外进行更改。这可能是由于时间限制-重构项目需要时间，所以禁用子模块中的插件会更方便。

此外，**我们将假设该插件实际上需要被禁用**。许多插件将毫无问题地运行，即使在它们不打算使用的模块上也是如此。

例如，假设我们有一个复制Java文件的插件，如果我们有一个没有Java文件的子项目，那么该插件很可能根本不复制任何文件，它会在不引起问题的情况下执行此操作。在这种情况下，让插件继续运行更简单也更传统。

让我们假设在考虑了上述因素之后，我们肯定需要使用我们的模块禁用插件。

我们可以做到这一点的一种方法是配置skip参数。

### 3.1 配置skip参数

**许多插件都有一个skip参数，我们可以使用skip参数来禁用插件**。

如果我们看一下maven-enforcer-plugin的[文档](https://maven.apache.org/enforcer/maven-enforcer-plugin/enforce-mojo.html#skip)，我们可以看到它有一个我们可以实现的skip参数。

**对skip参数的支持应该是我们检查的第一件事，因为它是最简单和最常规的解决方案**。

让我们添加一个空的子模块，只包含POM文件。如果我们使用`mvn clean install`命令构建模块，我们会看到构建失败，这是因为file-that-must-exist.txt在我们的模块中不存在，这是由于从我们的父模块继承规则所必需的。

让我们将以下行添加到子POM以启用skip参数：

```xml
<plugin>
    <artifactId>maven-enforcer-plugin</artifactId>
    <configuration>
        <skip>true</skip>
    </configuration>
</plugin>
```

现在，如果我们运行该项目，我们会看到构建成功。

但是，并非所有插件都有skip参数。因此，如果我们使用的是一个没有skip参数的插件，我们该怎么办？

### 3.2 删除phase参数

**Maven目标仅在绑定到构建阶段时运行**。

在我们的父POM中，我们配置了enforce目标以id enforce-file-exists运行。

由于我们没有为enforce-file-exists指定phase参数，因此它将使用默认值作为执行目标。我们从[文档](https://maven.apache.org/enforcer/maven-enforcer-plugin/enforce-mojo.html)中可以看出，默认是validate构建阶段。

我们可以通过为phase参数指定替代值来在替代构建阶段执行目标。

**利用这一点，我们可以将phase参数设置为不存在的值，这意味着永远不会执行构建阶段**。因此目标不会被执行，有效地禁用了插件：

```xml
<plugin>
    <artifactId>maven-enforcer-plugin</artifactId>
    <executions>
        <execution>
            <id>enforce-file-exists</id>
	    <phase>any-value-that-is-not-a-phase</phase>
	</execution>
    </executions>
</plugin>
```

为了让以后查看我们代码的人清楚，我们希望将phase设置为一个清晰的名称，例如“none”或“null”。

然而，也许最清晰的方法是完全清除phase参数：

```xml
<execution>
    <id>enforce-file-exists</id>
    <phase/>
</execution>
```

由于执行phase现在为空，因此目标不会绑定到运行的构建阶段，这有效地禁用了插件。

我们可以看到，当我们运行构建时，enforce-file-exists根本不会为我们的子模块运行。

## 4. 总结

在本文中，我们讨论了为什么我们可能会选择禁用在父POM中定义的插件，我们看到禁用插件可能并不总是最好的做法，因为Maven更喜欢约定而不是配置。

然后，我们查看了一个简单示例，其中我们禁用了由父POM声明的maven-enforcer-plugin。

首先，**我们演示了如果插件有skip参数，我们可以配置插件的skip参数**，并且我们提到这是最传统的方法。

最后，**我们了解到清除插件的phase参数也可以有效地禁用它**。