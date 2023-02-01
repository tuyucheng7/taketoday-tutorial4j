## 1. 概述

在本教程中，我们将讨论与Java生态系统新版本[Java 17](https://jdk.java.net/17/release-notes)相关的新闻，包括新功能及其发布过程中的变化、LTS支持和许可证。

## 2. JEP列表

首先，让我们谈谈在Java开发人员的生活中哪些因素会影响日常工作。

### 2.1 恢复始终严格的浮点数语义([JEP 306](https://openjdk.java.net/jeps/306))

这个JEP主要用于科学应用，它使浮点运算始终严格。**默认的浮点运算是strict或strictfp，这两种操作都保证在每个平台上浮点计算的结果相同**。

在Java 1.2之前，strictfp行为也是默认行为。但是，由于硬件问题，架构师发生了变化，关键字strictfp是重新启用此类行为所必需的。因此，没有必要再使用这个关键字了。

### 2.2 增强型伪随机数生成器([JEP 356](https://openjdk.java.net/jeps/356))

同样与更多特殊用例相关的是，JEP 356为伪随机数生成器(PRNG)提供了新的接口和实现。

因此，可以更轻松地互换使用不同的算法，而且它还为基于流的编程提供了更好的支持：

```java
public IntStream getPseudoInts(String algorithm, int streamSize) {
	// returns an IntStream with size @streamSize of random numbers generated using the @algorithm
	// where the lower bound is 0 and the upper is 100 (exclusive)
	return RandomGeneratorFactory.of(algorithm)
			.create()
			.ints(streamSize, 0, 100);
}
```

java.util.Random、SplittableRandom和SecureRandom等遗留随机数类现在扩展了新的RandomGenerator接口。

### 2.3 新的macOS渲染管道([JEP 382](https://openjdk.java.net/jeps/382))

这个JEP为macOS实现了一个Java 2D内部渲染管道，因为苹果不推荐在Swing GUI内部使用OpenGL API(在macOS 10.14中)。新的实现使用Apple Metal API，除了内部引擎外，现有API没有任何变化。

### 2.4 macOS/AArch64端口([JEP 391](https://openjdk.java.net/jeps/391))

苹果宣布了一项将其计算机产品线从X64过渡到AArch64的长期计划，这个JEP将JDK移植到macOS平台的AArch64上运行。

### 2.5 删除已弃用的Applet API([JEP 398](https://openjdk.java.net/jeps/398))

尽管这对于许多使用Applet API开始他的开发生涯的Java开发人员来说可能是悲哀的，但许多Web浏览器已经取消了或者宣布正在取消对Java插件的支持。随着API变得无关紧要，此版本将其标记为删除，它自版本9以来已被标记为已弃用。

### 2.6 强封装JDK内部结构([JEP 403](https://openjdk.java.net/jeps/403))

JEP 403代表着向强封装JDK内部结构又迈进了一步，因为它删除了–illegal-access这一标志。平台将忽略该标志，如果存在该标志，控制台将发出一条警告，通知该标志已停用。

**此功能将阻止JDK用户访问内部API，除了像sun.misc.Unsafe这样的关键API**。

### 2.7 Switch的模式匹配(预览版)([JEP 406](https://openjdk.java.net/jeps/406))

这是通过增强switch表达式和语句的模式匹配来实现模式匹配的又一步，它减少了定义这些表达式所需的样板代码，并提高了语言的表达能力。

让我们看一下新功能的两个示例：

```java
static record Human(String name, int age, String profession) {
}

public String checkObject(Object obj) {
	return switch (obj) {
		case Human h -> "Name: %s, age: %s and profession: %s".formatted(h.name(), h.age(), h.profession());
		case JEP409.Circle c -> "This is a circle";
		case JEP409.Shape s -> "It is just a shape";
		case null -> "It is null";
		default -> "It is an object";
	};
}

public String checkShape(JEP409.Shape shape) {
	return switch (shape) {
		case JEP409.Triangle t && (t.getNumberOfSides() != 3) -> "This is a weird triangle";
		case JEP409.Circle c && (c.getNumberOfSides() != 0) -> "This is a weird circle";
		default -> "Just a normal shape";
	};
}
```

### 2.8 移除RMI Activation([JEP 407](https://openjdk.java.net/jeps/407))

RMI在版本15中标记为删除，该JEP在版本17中从平台中删除了RMI Activation API。

### 2.9 密封类([JEP 409](https://openjdk.java.net/jeps/409))

密封类是[Project Amber](https://openjdk.java.net/projects/amber/)的一部分，这个JEP正式向该语言引入了一项新功能，该功能在JDK版本[15](https://openjdk.java.net/jeps/360)和[16](https://openjdk.java.net/jeps/397)中以预览模式提供。

该功能限制了哪些其他类或接口可以扩展或实现密封组件，显示与模式匹配相关的另一项改进与JEP 406相结合，将允许对类型、转换和行为代码模式进行更复杂和更清晰的检查。

让我们看看它的实际效果：

```java
int getNumberOfSides(Shape shape) {
	return switch (shape) {
		case WeirdTriangle t -> t.getNumberOfSides();
		case Circle c -> c.getNumberOfSides();
		case Triangle t -> t.getNumberOfSides();
		case Rectangle r -> r.getNumberOfSides();
		case Square s -> s.getNumberOfSides();
	};
}
```

### 2.10 删除实验性AOT和JIT编译器([JEP 410](https://openjdk.java.net/jeps/410))

作为实验性功能，Ahead-Of-Time(AOT)编译器([JEP-295](https://openjdk.java.net/jeps/295))和GraalVM的Just-In-Time(JIT)编译器([JEP-317](https://openjdk.java.net/jeps/317))分别引入JDK 9和JDK 10中，这两个特性的维护成本很高。

另一方面，他们没有被大量采用。因此，该JEP将它们从平台中移除，但开发人员仍然可以使用[GraalVM](https://www.graalvm.org/)来利用它们。

### 2.11 弃用安全管理器以移除([JEP 411](https://openjdk.java.net/jeps/411))

旨在保护客户端Java代码的安全管理器是另一个由于不再相关而被标记为要删除的功能。

### 2.12 外部函数和内存API(孵化器)([JEP 412](https://openjdk.java.net/jeps/412))

Foreign Function和Memory API允许Java开发人员从JVM外部访问代码并管理堆外的内存，目标是取代[JNI API](https://docs.oracle.com/en/java/javase/16/docs/specs/jni/index.html)，并与旧API相比提高安全性和性能。

这个API是由[Project Panama](https://openjdk.java.net/projects/panama/)开发的另一个特性，它已经由JEP [393](https://openjdk.java.net/jeps/393)、[389](https://openjdk.java.net/jeps/389)、[383](https://openjdk.java.net/jeps/383)和[370](https://openjdk.java.net/jeps/370)进行了改进和提前终止。

使用此功能，我们可以从Java类调用C库：

```c
private static final SymbolLookup libLookup;

static {
    // loads a particular C library
    var path = JEP412.class.getResource("/print_name.so").getPath();
    System.load(path);
    libLookup = SymbolLookup.loaderLookup();
}
```

首先，有必要加载我们希望通过API调用的目标库。

接下来，我们需要指定目标方法的签名并最终调用它：

```java
public String getPrintNameFormat(String name) {
    
	var printMethod = libLookup.lookup("printName");
    
	if (printMethod.isPresent()) {
		var methodReference = CLinker.getInstance()
				.downcallHandle(
						printMethod.get(),
						MethodType.methodType(MemoryAddress.class, MemoryAddress.class),
						FunctionDescriptor.of(CLinker.C_POINTER, CLinker.C_POINTER)
				);
		try {
			var nativeString = CLinker.toCString(name, newImplicitScope());
			var invokeReturn = methodReference.invoke(nativeString.address());
			var memoryAddress = (MemoryAddress) invokeReturn;
			return CLinker.toJavaString(memoryAddress);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}
	throw new RuntimeException("printName function not found.");
}

```

### 2.13 Vector API(第二个孵化器)([JEP 414](https://openjdk.java.net/jeps/414))

Vector API处理SIMD(单指令、多数据)类型的操作，即并行执行的各种指令集。它利用支持矢量指令的专用CPU硬件，并允许执行诸如管道之类的指令。

因此，新的API将使开发人员能够实现更高效的代码，充分利用底层硬件的潜力。

此操作的日常用例包括科学代数线性应用程序、图像处理、字符处理以及任何繁重的算术应用程序或任何需要对多个独立操作数应用运算的应用程序。

让我们使用API来说明一个简单的向量乘法示例：

```java
private static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

public void newVectorComputation(float[] a, float[] b, float[] c) {
	for (var i = 0; i < a.length; i += SPECIES.length()) {
		var m = SPECIES.indexInRange(i, a.length);
		var va = FloatVector.fromArray(SPECIES, a, i, m);
		var vb = FloatVector.fromArray(SPECIES, b, i, m);
		var vc = va.mul(vb);
		vc.intoArray(c, i, m);
	}
}

public void commonVectorComputation(float[] a, float[] b, float[] c) {
	for (var i = 0; i < a.length; i++) {
		c[i] = a[i] * b[i];
	}
}
```

### 2.14 特定于上下文的反序列化过滤器([JEP 415](https://openjdk.java.net/jeps/415))

[JEP 290](https://openjdk.java.net/jeps/290)首次在JDK 9中引入，使我们能够验证来自不受信任来源的传入序列化数据，这是许多安全问题的常见来源。这种验证发生在JVM级别，从而提供了更高的安全性和健壮性。

使用JEP 415，应用程序可以配置在JVM级别定义的特定于上下文和动态选择的反序列化过滤器，每个反序列化操作都将调用此类过滤器。

## 3. LTS定义

这些变化不仅仅停留在代码中，流程也在发生变化。

众所周知，Java平台版本的历史悠久且不精确。尽管被设计为在发布之间有一个三年的节奏，但它通常变成了一个四年的过程。

此外，鉴于创新和快速响应成为必需的市场新动态，负责平台发展的团队决定改变发布节奏以适应新的现实发展。

因此，自Java 10(2018年3月20日发布)以来，采用了新的六个月功能发布模型。

### 3.1 六个月功能发布模型

**新的六个月功能发布模型允许平台开发人员在准备就绪时发布功能**，这消除了将功能推送到版本中的压力，否则他们将不得不等待三到四年才能为平台用户提供该功能。

新模型还改善了用户与平台架构师之间的反馈周期，这是因为功能可以在孵化模式下提供，并且只有在多次交互后才能发布以供常规使用。

### 3.2 LTS模型

由于企业应用程序广泛使用Java，因此稳定性至关重要。此外，保持对所有这些版本的支持和提供补丁更新的成本很高。

为此，创建了长期支持(LTS)版本，为用户提供扩展支持。因此，由于bug修复、性能改进和安全补丁，此类版本自然会变得更加稳定和安全。对于Oracle而言，这种支持通常持续八年。

自引入发布模型变更以来，LTS版本为JavaSE 11(2018年9月发布)和JavaSE 17(2021年9月发布)。尽管如此，版本17还是为该模型带来了一些新东西。简而言之，**LTS版本之间的间隔现在是两年而不是三年，这使得Java 21(计划于2023年9月)可能成为下一个LTS**。

另一点值得一提的是，这种发布模式并不是有多新型，它也是根据效仿并改编自其他项目，例如Mozilla Firefox、Ubuntu和其他证明了该模型的项目。

## 4. 新版本发布流程

我们基于[JEP 3](https://openjdk.java.net/jeps/3)撰写本文，因为它描述了流程中的所有更改。请阅读它以获取更多详细信息，我们在这里提供一个简明的总结。

考虑到上面描述的新模型，再结合平台的持续开发和新的六个月发布节奏(通常是6月和12月)，Java将发展得更快。JDK的开发团队将按照下面描述的过程启动下一个功能版本的发布周期。

该过程从[主线](https://github.com/openjdk/jdk)的fork开始，然后在稳定仓库JDK/JDK$N(例如[JDK17](https://github.com/openjdk/jdk17))中继续开发。在那里，开发继续侧重于版本的稳定性。

在我们深入研究这个过程之前，让我们澄清一些术语：

-   **Bugs**：在这种情况下，bugs意味着工单或任务：
    -   **Current**：这些要么是与当前版本(即将发布的新版本)相关的实际错误，要么是对该版本中已包含的新功能的调整(新JEP)
    -   **Targeted**：与旧版本相关，并计划在此新版本中修复或解决

-    **Priorities**：从P1到P5，其中P1最重要，重要性逐渐降低到P5

### 4.1 新格式

稳定过程将在接下来的三个月中进行：

+   JDK/JDK$N仓库的工作方式类似于发布分支，此时，不会有新的JEP进入仓库
+   接下来，这个仓库中的开发将被稳定并移植到主线上，其他开发将继续
+   Ramp Down Phase 1(RDP 1)：持续四到五周，开发人员放弃所有currents P4-P5和targeted P1-P3(取决于[延迟](https://openjdk.java.net/jeps/3#Bug-Deferral-Process)、[修复](https://openjdk.java.net/jeps/3#Fix-Request-Process)或[增强](https://openjdk.java.net/jeps/3#Late-Enhancement-Request-Process))。这意味着P5+测试/文档错误和targeted P3+代码错误是可选的
+   Ramp Down Phase 2(RDP 2)：持续三到四周，现在他们推迟所有currents P3-P5和targeted P1-P3(取决于[推迟](https://openjdk.java.net/jeps/3#Bug-Deferral-Process)、[修复](https://openjdk.java.net/jeps/3#Fix-Request-Process)或[增强](https://openjdk.java.net/jeps/3#Late-Enhancement-Request-Process))
+   最后，团队发布一个发布候选版本并向公众开放。此阶段持续两到五周，并且仅解决current P1修复(使用[修复](https://openjdk.java.net/jeps/3#Fix-Request-Process))。

一旦所有这些周期完成，新版本将成为正式发布(GA)版本。

## 5. 接下来是什么？

JDK架构师继续致力于许多旨在实现平台现代化的项目，目标是提供更好的开发体验和更健壮、更高性能的API。

因此，JDK 18应该会在六个月后发布，尽管这个版本不太可能包含重大或破坏性的变化。我们可以在官方[OpenJDK 项目](https://openjdk.java.net/projects/jdk/18/)门户中关注针对此版本的建议JEP列表。

影响当前和未来版本的另一条相关新闻是适用于Oracle JDK发行版(或Hotspot)的新的[免费条款和条件](https://www.oracle.com/downloads/licenses/no-fee-license.html)许可证。在大多数情况下，Oracle为生产环境和其他环境免费提供其发行版，但也有一些例外。同样，请参阅链接。

如前所述，新流程的目标是下一个LTS版本为21，计划于2023年9月发布。

## 6. 总结

在本文中，我们查看了有关新Java 17版本的新闻，了解了它的最新进展、新功能、支持定义和发布周期过程。