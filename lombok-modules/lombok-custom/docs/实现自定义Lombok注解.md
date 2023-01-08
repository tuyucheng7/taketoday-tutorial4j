## 1. 概述

在本教程中，我们将使用 Lombok 实现自定义注解，以移除围绕在应用程序中实现单例的样板。

Lombok 是一个功能强大的Java库，旨在减少Java中的样板代码。如果你不熟悉它，在这里你可以找到[对 Lombok 的所有特性的介绍](https://www.baeldung.com/intro-to-project-lombok)。

重要说明：Lombok 1.14.8 是我们可以用来学习本教程的最新兼容版本。从 1.16.0 版本开始，Lombok 隐藏了它的内部 API，并且不再可能以我们在这里展示的方式创建自定义注解。

## 2. Lombok作为注解处理器

Java 允许应用程序开发人员在编译阶段处理注解；最重要的是，根据注解生成新文件。因此，像 Hibernate 这样的库允许开发人员减少样板代码并改用注解。

[本教程](https://www.baeldung.com/java-annotation-processing-builder)深入介绍了注解处理。

同样，Project Lombok 也可以作为一个注解处理器。它通过将注解委托给特定的处理程序来处理注解。

委托时，它将注解代码的编译器抽象语法树 (AST) 发送给处理程序。因此，它允许处理程序通过扩展 AST 来修改代码。

## 3. 实现自定义注解

### 3.1. 扩展龙目岛

令人惊讶的是，Lombok 并不容易扩展和添加自定义注解。

事实上，较新版本的 Lombok 使用 Shadow ClassLoader (SCL) 将 Lombok 中的.class文件隐藏为.scl文件。因此，它迫使开发人员分叉 Lombok 源代码并在那里实现注解。

从积极的方面来说，它简化了使用实用函数扩展自定义处理程序和 AST 修改的过程。

### 3.2. 单例注解

通常，实现一个 Singleton 类需要大量的代码。对于不使用依赖注入框架的应用程序，这只是样板文件。

例如，这是实现 Singleton 类的一种方法：

```java
public class SingletonRegistry {
    private SingletonRegistry() {}
    
    private static class SingletonRegistryHolder {
        private static SingletonRegistry registry = new SingletonRegistry();
    }
    
    public static SingletonRegistry getInstance() {
        return SingletonRegistryHolder.registry;
    }
	
    // other methods
}
```

相反，如果我们实现它的注解版本，它会是这样的：

```java
@Singleton
public class SingletonRegistry {}
```

并且，单例注解：

```java
@Target(ElementType.TYPE)
public @interface Singleton {}
```

这里需要强调的是，Lombok Singleton 处理程序将通过修改 AST 来生成我们在上面看到的实现代码。

由于每个编译器的 AST 都不同，因此每个编译器都需要一个自定义的 Lombok 处理程序。Lombok 允许为javac(由 Maven/Gradle 和 Netbeans 使用)和 Eclipse 编译器自定义处理程序。

在接下来的部分中，我们将为每个编译器实现我们的注解处理程序。

## 4. 为javac实现一个处理程序

### 4.1. Maven 依赖

[让我们先拉取Lombok](https://search.maven.org/search?q=g:org.projectlombok AND a:lombok&core=gav)所需的依赖项：

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.14.8</version>
</dependency>
```

此外，我们还需要Java 附带的tools.jar来访问和修改javac AST。但是，没有它的 Maven 存储库。将其包含在 Maven 项目中的最简单方法是将其添加到配置文件中：

```xml
<profiles>
    <profile>
        <id>default-tools.jar</id>
            <activation>
                <property>
                    <name>java.vendor</name>
                    <value>Oracle Corporation</value>
                </property>
            </activation>
            <dependencies>
                <dependency>
                    <groupId>com.sun</groupId>
                    <artifactId>tools</artifactId>
                    <version>${java.version}</version>
                    <scope>system</scope>
                    <systemPath>${java.home}/../lib/tools.jar</systemPath>
                </dependency>
            </dependencies>
    </profile>
</profiles>
```

### 4.2. 扩展JavacAnnotationHandler

为了实现自定义javac处理程序，我们需要扩展 Lombok 的JavacAnnotationHandler：

```java
public class SingletonJavacHandler extends JavacAnnotationHandler<Singleton> {
    public void handle(
      AnnotationValues<Singleton> annotation,
      JCTree.JCAnnotation ast,
      JavacNode annotationNode) {}
}
```

接下来，我们将实现handle()方法。在这里，注解 AST 由 Lombok 作为参数提供。

### 4.3. 修改 AST

这就是事情变得棘手的地方。通常，更改现有 AST 并不那么简单。

幸运的是， Lombok 在JavacHandlerUtil和JavacTreeMaker中提供了许多实用函数，用于生成代码并将其注入 AST。考虑到这一点，让我们使用这些函数并为我们的SingletonRegistry 创建代码：


```java
public void handle(
  AnnotationValues<Singleton> annotation,
  JCTree.JCAnnotation ast,
  JavacNode annotationNode) {
    Context context = annotationNode.getContext();
    Javac8BasedLombokOptions options = Javac8BasedLombokOptions
      .replaceWithDelombokOptions(context);
    options.deleteLombokAnnotations();
    JavacHandlerUtil
      .deleteAnnotationIfNeccessary(annotationNode, Singleton.class);
    JavacHandlerUtil
      .deleteImportFromCompilationUnit(annotationNode, "lombok.AccessLevel");
    JavacNode singletonClass = annotationNode.up();
    JavacTreeMaker singletonClassTreeMaker = singletonClass.getTreeMaker();
    addPrivateConstructor(singletonClass, singletonClassTreeMaker);

    JavacNode holderInnerClass = addInnerClass(singletonClass, singletonClassTreeMaker);
    addInstanceVar(singletonClass, singletonClassTreeMaker, holderInnerClass);
    addFactoryMethod(singletonClass, singletonClassTreeMaker, holderInnerClass);
}
```

需要指出的是，Lombok 提供的 deleteAnnotationIfNeccessary()和deleteImportFromCompilationUnit()方法用于删除注解及其任何导入。

现在，让我们看看如何实现其他私有方法来生成代码。首先，我们将生成私有构造函数：

```java
private void addPrivateConstructor(
  JavacNode singletonClass,
  JavacTreeMaker singletonTM) {
    JCTree.JCModifiers modifiers = singletonTM.Modifiers(Flags.PRIVATE);
    JCTree.JCBlock block = singletonTM.Block(0L, nil());
    JCTree.JCMethodDecl constructor = singletonTM
      .MethodDef(
        modifiers,
        singletonClass.toName("<init>"),
        null, nil(), nil(), nil(), block, null);

    JavacHandlerUtil.injectMethod(singletonClass, constructor);
}
```

接下来，内部SingletonHolder类：

```java
private JavacNode addInnerClass(
  JavacNode singletonClass,
  JavacTreeMaker singletonTM) {
    JCTree.JCModifiers modifiers = singletonTM
      .Modifiers(Flags.PRIVATE | Flags.STATIC);
    String innerClassName = singletonClass.getName() + "Holder";
    JCTree.JCClassDecl innerClassDecl = singletonTM
      .ClassDef(modifiers, singletonClass.toName(innerClassName),
      nil(), null, nil(), nil());
    return JavacHandlerUtil.injectType(singletonClass, innerClassDecl);
}
```

现在，我们将在 holder 类中添加一个实例变量：

```java
private void addInstanceVar(
  JavacNode singletonClass,
  JavacTreeMaker singletonClassTM,
  JavacNode holderClass) {
    JCTree.JCModifiers fieldMod = singletonClassTM
      .Modifiers(Flags.PRIVATE | Flags.STATIC | Flags.FINAL);

    JCTree.JCClassDecl singletonClassDecl
      = (JCTree.JCClassDecl) singletonClass.get();
    JCTree.JCIdent singletonClassType
      = singletonClassTM.Ident(singletonClassDecl.name);

    JCTree.JCNewClass newKeyword = singletonClassTM
      .NewClass(null, nil(), singletonClassType, nil(), null);

    JCTree.JCVariableDecl instanceVar = singletonClassTM
      .VarDef(
        fieldMod,
        singletonClass.toName("INSTANCE"),
        singletonClassType,
        newKeyword);
    JavacHandlerUtil.injectField(holderClass, instanceVar);
}
```

最后，让我们添加一个用于访问单例对象的工厂方法：

```java
private void addFactoryMethod(
  JavacNode singletonClass,
  JavacTreeMaker singletonClassTreeMaker,
  JavacNode holderInnerClass) {
    JCTree.JCModifiers modifiers = singletonClassTreeMaker
      .Modifiers(Flags.PUBLIC | Flags.STATIC);

    JCTree.JCClassDecl singletonClassDecl
      = (JCTree.JCClassDecl) singletonClass.get();
    JCTree.JCIdent singletonClassType
      = singletonClassTreeMaker.Ident(singletonClassDecl.name);

    JCTree.JCBlock block
      = addReturnBlock(singletonClassTreeMaker, holderInnerClass);

    JCTree.JCMethodDecl factoryMethod = singletonClassTreeMaker
      .MethodDef(
        modifiers,
        singletonClass.toName("getInstance"),
        singletonClassType, nil(), nil(), nil(), block, null);
    JavacHandlerUtil.injectMethod(singletonClass, factoryMethod);
}
```

显然，工厂方法从 holder 类返回实例变量。让我们也实现它：

```java
private JCTree.JCBlock addReturnBlock(
  JavacTreeMaker singletonClassTreeMaker,
  JavacNode holderInnerClass) {

    JCTree.JCClassDecl holderInnerClassDecl
      = (JCTree.JCClassDecl) holderInnerClass.get();
    JavacTreeMaker holderInnerClassTreeMaker
      = holderInnerClass.getTreeMaker();
    JCTree.JCIdent holderInnerClassType
      = holderInnerClassTreeMaker.Ident(holderInnerClassDecl.name);

    JCTree.JCFieldAccess instanceVarAccess = holderInnerClassTreeMaker
      .Select(holderInnerClassType, holderInnerClass.toName("INSTANCE"));
    JCTree.JCReturn returnValue = singletonClassTreeMaker
      .Return(instanceVarAccess);

    ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
    statements.append(returnValue);

    return singletonClassTreeMaker.Block(0L, statements.toList());
}
```

结果，我们为单例类修改了 AST。

### 4.4. 向 SPI 注册处理程序

到目前为止，我们只实现了一个 Lombok 处理程序来为我们的SingletonRegistry 生成 AST。在这里，重要的是要重申 Lombok 是一个注解处理器。

通常，注解处理器是通过META-INF/services发现的。Lombok 也以相同的方式维护一个处理程序列表。此外，它使用名为 SPI 的框架来自动更新处理程序列表。

出于我们的目的，我们将使用[metainf-services](https://search.maven.org/search?q=g:org.kohsuke.metainf-services AND a:metainf-services&core=gav)：

```xml
<dependency>
    <groupId>org.kohsuke.metainf-services</groupId>
    <artifactId>metainf-services</artifactId>
    <version>1.8</version>
</dependency>
```

现在，我们可以向 Lombok 注册我们的处理程序：

```java
@MetaInfServices(JavacAnnotationHandler.class)
public class SingletonJavacHandler extends JavacAnnotationHandler<Singleton> {}
```

这将在编译时生成一个lombok.javac.JavacAnnotationHandler文件。此行为对于所有 SPI 框架都很常见。

## 5. 为 Eclipse IDE 实现处理程序

### 5.1. Maven 依赖

与我们为访问javac的 AST 而添加的tools.jar类似，我们将为Eclipse IDE添加[eclipse jdt ：](https://search.maven.org/search?q=g:org.eclipse.jdt AND a:core&core=gav)

```xml
<dependency>
    <groupId>org.eclipse.jdt</groupId>
    <artifactId>core</artifactId>
    <version>3.3.0-v_771</version>
</dependency>
```

### 5.2. 扩展EclipseAnnotationHandler

现在，我们将为Eclipse 处理程序扩展EclipseAnnotationHandler ：

```java
@MetaInfServices(EclipseAnnotationHandler.class)
public class SingletonEclipseHandler
  extends EclipseAnnotationHandler<Singleton> {
    public void handle(
      AnnotationValues<Singleton> annotation,
      Annotation ast,
      EclipseNode annotationNode) {}
}
```

与 SPI 注解MetaInfServices 一起，此处理程序充当我们的Singleton注解的处理器。因此，无论何时在 Eclipse IDE 中编译类，处理程序都会将带注解的类转换为单例实现。

### 5.3. 修改 AST

在我们的处理程序注册到 SPI 后，我们现在可以开始为 Eclipse 编译器编辑 AST：

```java
public void handle(
  AnnotationValues<Singleton> annotation,
  Annotation ast,
  EclipseNode annotationNode) {
    EclipseHandlerUtil
      .unboxAndRemoveAnnotationParameter(
        ast,
        "onType",
        "@Singleton(onType=", annotationNode);
    EclipseNode singletonClass = annotationNode.up();
    TypeDeclaration singletonClassType
      = (TypeDeclaration) singletonClass.get();
    
    ConstructorDeclaration constructor
      = addConstructor(singletonClass, singletonClassType);
    
    TypeReference singletonTypeRef 
      = EclipseHandlerUtil.cloneSelfType(singletonClass, singletonClassType);
    
    StringBuilder sb = new StringBuilder();
    sb.append(singletonClass.getName());
    sb.append("Holder");
    String innerClassName = sb.toString();
    TypeDeclaration innerClass
      = new TypeDeclaration(singletonClassType.compilationResult);
    innerClass.modifiers = AccPrivate | AccStatic;
    innerClass.name = innerClassName.toCharArray();
    
    FieldDeclaration instanceVar = addInstanceVar(
      constructor,
      singletonTypeRef,
      innerClass);
    
    FieldDeclaration[] declarations = new FieldDeclaration[]{instanceVar};
    innerClass.fields = declarations;
    
    EclipseHandlerUtil.injectType(singletonClass, innerClass);
    
    addFactoryMethod(
      singletonClass,
      singletonClassType,
      singletonTypeRef,
      innerClass,
      instanceVar);
}
```

接下来，私有构造函数：

```java
private ConstructorDeclaration addConstructor(
  EclipseNode singletonClass,
  TypeDeclaration astNode) {
    ConstructorDeclaration constructor
      = new ConstructorDeclaration(astNode.compilationResult);
    constructor.modifiers = AccPrivate;
    constructor.selector = astNode.name;
    
    EclipseHandlerUtil.injectMethod(singletonClass, constructor);
    return constructor;
}
```

对于实例变量：

```java
private FieldDeclaration addInstanceVar(
  ConstructorDeclaration constructor,
  TypeReference typeReference,
  TypeDeclaration innerClass) {
    FieldDeclaration field = new FieldDeclaration();
    field.modifiers = AccPrivate | AccStatic | AccFinal;
    field.name = "INSTANCE".toCharArray();
    field.type = typeReference;
    
    AllocationExpression exp = new AllocationExpression();
    exp.type = typeReference;
    exp.binding = constructor.binding;
    
    field.initialization = exp;
    return field;
}

```

最后，工厂方法：

```java
private void addFactoryMethod(
  EclipseNode singletonClass,
  TypeDeclaration astNode,
  TypeReference typeReference,
  TypeDeclaration innerClass,
  FieldDeclaration field) {
    
    MethodDeclaration factoryMethod
      = new MethodDeclaration(astNode.compilationResult);
    factoryMethod.modifiers 
      = AccStatic | ClassFileConstants.AccPublic;
    factoryMethod.returnType = typeReference;
    factoryMethod.sourceStart = astNode.sourceStart;
    factoryMethod.sourceEnd = astNode.sourceEnd;
    factoryMethod.selector = "getInstance".toCharArray();
    factoryMethod.bits = ECLIPSE_DO_NOT_TOUCH_FLAG;
    
    long pS = factoryMethod.sourceStart;
    long pE = factoryMethod.sourceEnd;
    long p = (long) pS << 32 | pE;
    
    FieldReference ref = new FieldReference(field.name, p);
    ref.receiver = new SingleNameReference(innerClass.name, p);
    
    ReturnStatement statement
      = new ReturnStatement(ref, astNode.sourceStart, astNode.sourceEnd);
    
    factoryMethod.statements = new Statement[]{statement};
    
    EclipseHandlerUtil.injectMethod(singletonClass, factoryMethod);
}
```

此外，我们必须将此处理程序插入到 Eclipse 引导类路径中。一般通过在eclipse.ini中添加如下参数来完成：

```plaintext
-Xbootclasspath/a:singleton-1.0-SNAPSHOT.jar
```

## 6. IntelliJ 中的自定义注解

一般来说，每个编译器都需要一个新的 Lombok handler，就像我们之前实现的javac和 Eclipse handler。

相反，IntelliJ 不支持 Lombok 处理程序。它通过[插件](https://github.com/mplushnikov/lombok-intellij-plugin)提供 Lombok 支持。

因此，插件必须明确支持任何新注解。这也适用于添加到 Lombok 的任何注解。

## 七. 总结

在本文中，我们使用 Lombok 处理程序实现了自定义注解。我们还简要地查看了在各种 IDE 中可用的不同编译器中对我们的Singleton注解的 AST 修改。