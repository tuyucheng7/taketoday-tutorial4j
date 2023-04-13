## 1. 简介

在本文中，我们将介绍如何使用[ASM](http://asm.ow2.org/)库通过添加字段、添加方法和更改现有方法的行为来**操作现有的Java类**。

## 2. 依赖

我们需要将ASM依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.ow2.asm</groupId>
    <artifactId>asm</artifactId>
    <version>6.0</version>
</dependency>
<dependency>
    <groupId>org.ow2.asm</groupId>
    <artifactId>asm-util</artifactId>
    <version>6.0</version>
</dependency>
```

我们可以从Maven Central获取最新版本的[asm](https://central.sonatype.com/artifact/org.ow2.asm/asm/9.5)和[asm-util](https://central.sonatype.com/artifact/org.ow2.asm/asm-util/9.5)。

## 3. ASM API基础

ASM API提供了两种与Java类进行转换和生成的交互方式：基于事件和基于树。

### 3.1 基于事件的API

该API很大程度上**基于访问者模式**，在感觉上**类似于处理XML文档的SAX解析模型**。它的核心由以下组件组成：

-   ClassReader：帮助读取class文件，是改造一个class的开始
-   ClassVisitor：提供读取原始class文件后用于转换class的方法
-   ClassWriter：用于输出class转换的最终产物

在ClassVisitor中，我们拥有所有访问者方法，我们将使用这些方法来接触给定Java类的不同组件(字段、方法等)。我们通过**提供ClassVisitor的子类**来实现给定类中的任何更改来实现这一点。

由于需要保持有关Java约定和生成的字节码的输出class的完整性，因此此类**需要严格的顺序**来调用其方法以生成正确的输出。

基于事件的API中的ClassVisitor方法按以下顺序调用：

```plaintext
visit
visitSource?
visitOuterClass?
( visitAnnotation | visitAttribute )*
( visitInnerClass | visitField | visitMethod )*
visitEnd
```

### 3.2 基于树的API

这个API是一个**更面向对象的API**，类似于处理XML文档的JAXB模型。

它仍然基于基于事件的API，但它引入了ClassNode根类。此类用作类结构的入口点。

## 4. 使用基于事件的ASM API

我们将使用ASM修改java.lang.Integer类。此时我们需要掌握一个基本概念：**ClassVisitor类包含创建或修改类的所有部分所需的所有访问者方法**。

我们只需要覆盖必要的访问者方法来实现我们的更改。让我们从设置必备组件开始：

```java
public class CustomClassWriter {

    static String className = "java.lang.Integer"; 
    static String cloneableInterface = "java/lang/Cloneable";
    ClassReader reader;
    ClassWriter writer;

    public CustomClassWriter() {
        reader = new ClassReader(className);
        writer = new ClassWriter(reader, 0);
    }
}
```

我们以此为基础，将Cloneable接口添加到stockInteger类中，同时我们还添加了一个字段和一个方法。

### 4.1 使用字段

让我们创建我们的ClassVisitor来向Integer类添加一个字段：

```java
public class AddFieldAdapter extends ClassVisitor {
	private String fieldName;
	private String fieldDefault;
	private int access = org.objectweb.asm.Opcodes.ACC_PUBLIC;
	private boolean isFieldPresent;

	public AddFieldAdapter(String fieldName, int fieldAccess, ClassVisitor cv) {
		super(ASM4, cv);
		this.cv = cv;
		this.fieldName = fieldName;
		this.access = fieldAccess;
	}
}
```

接下来，让我们**覆盖visitField方法**，我们首先**检查我们计划添加的字段是否已经存在，并设置一个标志来指示状态**。

我们仍然必须**将方法调用转发给父类**-这需要发生，因为类中的每个字段都会调用visitField方法。**无法转发调用意味着不会向类写入任何字段**。

此方法还允许我们**修改现有字段的可见性或类型**：

```java
@Override
public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
    if (name.equals(fieldName)) {
        isFieldPresent = true;
    }
    return cv.visitField(access, name, desc, signature, value); 
}
```

我们首先检查在前面的visitField方法中设置的标志，然后再次调用visitField方法，这次提供名称、访问修饰符和描述。此方法返回FieldVisitor的一个实例。

**visitEnd方法是按访问者方法顺序调用的最后一个方法**。这是**执行字段插入逻辑的推荐位置**。

然后，我们需要调用此对象的visitEnd方法来**表示我们已完成对该字段的访问**：

```java
@Override
public void visitEnd() {
    if (!isFieldPresent) {
        FieldVisitor fv = cv.visitField(access, fieldName, fieldType, null, null);
        if (fv != null) {
            fv.visitEnd();
        }
    }
    cv.visitEnd();
}
```

**重要的是要确保使用的所有ASM组件都来自org.objectweb.asm包**-许多库在内部使用ASM库，IDE可以自动插入捆绑的ASM库。

我们现在在addField方法中使用我们的适配器，**使用我们添加的字段获得java.lang.Integer的转换版本**：

```java
public class CustomClassWriter {
	AddFieldAdapter addFieldAdapter;
	//...
	public byte[] addField() {
		addFieldAdapter = new AddFieldAdapter(
			"aNewBooleanField",
			org.objectweb.asm.Opcodes.ACC_PUBLIC,
			writer);
		reader.accept(addFieldAdapter, 0);
		return writer.toByteArray();
	}
}
```

我们覆盖了visitField和visitEnd方法。

与字段有关的所有事情都发生在visitField方法中。这意味着我们还可以通过更改传递给visitField方法的所需值来修改现有字段(例如，将私有字段转换为公共字段)。

### 4.2 使用方法

在ASM API中生成整个方法比类中的其他操作更复杂。这涉及大量的低级字节码操作，因此超出了本文的范围。

但是，对于大多数实际用途，我们可以**修改现有方法以使其更易于访问**(也许将其公开以便可以覆盖或重载)或**修改类以使其可扩展**。

让我们公开toUnsignedString方法：

```java
public class PublicizeMethodAdapter extends ClassVisitor {
	public PublicizeMethodAdapter(int api, ClassVisitor cv) {
		super(ASM4, cv);
		this.cv = cv;
	}

	public MethodVisitor visitMethod(
		int access,
		String name,
		String desc,
		String signature,
		String[] exceptions) {
		if (name.equals("toUnsignedString0")) {
			return cv.visitMethod(
				ACC_PUBLIC + ACC_STATIC,
				name,
				desc,
				signature,
				exceptions);
		}
		return cv.visitMethod(access, name, desc, signature, exceptions);
	}
}
```

就像我们修改字段一样，我们只是**拦截访问方法并更改我们想要的参数**。

在这种情况下，我们使用org.objectweb.asm.Opcodes包中的访问修饰符来**更改方法的可见性**。然后我们插入我们的ClassVisitor：

```java
public byte[] publicizeMethod() {
    pubMethAdapter = new PublicizeMethodAdapter(writer);
    reader.accept(pubMethAdapter, 0);
    return writer.toByteArray();
}
```

### 4.3 使用类

与修改方法一样，我们**通过拦截适当的访问者方法来修改类**。在这种情况下，我们拦截visit，这是访问者层次结构中的第一个方法：

```java
public class AddInterfaceAdapter extends ClassVisitor {

	public AddInterfaceAdapter(ClassVisitor cv) {
		super(ASM4, cv);
	}

	@Override
	public void visit(
		int version,
		int access,
		String name,
		String signature,
		String superName, String[] interfaces) {
		String[] holding = new String[interfaces.length + 1];
		holding[holding.length - 1] = cloneableInterface;
		System.arraycopy(interfaces, 0, holding, 0, interfaces.length);
		cv.visit(V1_8, access, name, signature, superName, holding);
	}
}
```

我们覆盖visit方法以将Cloneable接口添加到Integer类要支持的接口数组中。我们将其插入，就像适配器的所有其他用途一样。

## 5. 使用修改后的类

所以我们修改了Integer类。现在我们需要能够加载和使用类的修改版本。

除了简单地将writer.toByteArray的输出作为class文件写入磁盘外，还有一些其他方法可以与我们自定义的Integer类进行交互。

### 5.1 使用TraceClassVisitor

ASM库提供了TraceClassVisitor实用程序类，我们将使用它来**检查修改后的类**。因此我们可以**确认我们的更改已经发生**。

因为TraceClassVisitor是一个ClassVisitor，我们可以将它用作标准ClassVisitor的替代品：

```java
PrintWriter pw = new PrintWriter(System.out);

public PublicizeMethodAdapter(ClassVisitor cv) {
    super(ASM4, cv);
    this.cv = cv;
    tracer = new TraceClassVisitor(cv,pw);
}

public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
	if (name.equals("toUnsignedString0")) {
	    System.out.println("Visiting unsigned method");
	    return tracer.visitMethod(ACC_PUBLIC + ACC_STATIC, name, desc, signature, exceptions);
	}
	return tracer.visitMethod(access, name, desc, signature, exceptions);
}

public void visitEnd(){
    tracer.visitEnd();
    System.out.println(tracer.p.getText());
}
```

我们在这里所做的是使用TraceClassVisitor调整我们传递给我们之前的PublicizeMethodAdapter的ClassVisitor。

现在，所有访问都将通过我们的跟踪器完成，然后它可以打印出转换后的类的内容，显示我们对其所做的任何修改。

虽然ASM文档指出TraceClassVisitor可以打印到提供给构造函数的PrintWriter，但这在最新版本的ASM中似乎无法正常工作。

幸运的是，我们可以访问类中的底层printer，并且能够在重写的visitEnd方法中手动打印出跟踪器的文本内容。

### 5.2 使用Java检测

这是一个更优雅的解决方案，它允许我们通过[Instrumentation](https://docs.oracle.com/en/java/javase/11/docs/api/java.instrument/java/lang/instrument/package-summary.html)在更近的层次上使用JVM。

为了检测java.lang.Integer类，**我们编写了一个代理，该代理将配置为JVM的命令行参数**。代理需要两个组件：

-   实现名为premain的方法的类
-   [ClassFileTransformer](https://docs.oracle.com/en/java/javase/11/docs/api/java.instrument/java/lang/instrument/ClassFileTransformer.html)的实现，我们将有条件地提供我们类的修改版本

```java
public class Premain {
	public static void premain(String agentArgs, Instrumentation inst) {
		inst.addTransformer(new ClassFileTransformer() {
			@Override
			public byte[] transform(
				ClassLoader l,
				String name,
				Class c,
				ProtectionDomain d,
				byte[] b)
				throws IllegalClassFormatException {
				if(name.equals("java/lang/Integer")) {
					CustomClassWriter cr = new CustomClassWriter(b);
					return cr.addField();
				}
				return b;
			}
		});
	}
}
```

我们现在使用Maven jar插件在JAR清单文件中定义我们的premain实现类：

```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-jar-plugin</artifactId>
	<version>2.4</version>
	<configuration>
		<archive>
			<manifestEntries>
				<Premain-Class>com.baeldung.examples.asm.instrumentation.Premain</Premain-Class>
				<Can-Retransform-Classes>true</Can-Retransform-Classes>
			</manifestEntries>
		</archive>
	</configuration>
</plugin>
```

到目前为止，构建和打包我们的代码会生成我们可以作为代理加载的jar。在假设的“YourClass.class”中使用我们自定义的Integer类：

```shell
java YourClass -javaagent:"/path/to/theAgentJar.jar"
```

## 6. 总结

虽然我们在这里单独实现了我们的转换，但ASM允许我们将多个适配器链接在一起以实现类的复杂转换。

除了我们在这里检查的基本转换之外，ASM还支持与注解、泛型和内部类的交互。

我们已经看到了ASM库的一些强大功能—它消除了我们在使用第三方库甚至标准JDK类时可能会遇到的许多限制。

ASM在一些最流行的库(Spring、AspectJ、JDK等)的底层被广泛使用，以在运行中执行许多“魔法”。