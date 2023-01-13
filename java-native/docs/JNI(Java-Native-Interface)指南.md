## 1. 简介

正如我们所知，Java 的主要优势之一是它的可移植性——这意味着一旦我们编写和编译代码，这个过程的结果就是独立于平台的字节码。

简而言之，它可以在任何能够运行Java虚拟机的机器或设备上运行，并且它将像我们预期的那样无缝运行。

然而，有时我们确实需要使用为特定架构本地编译的代码。

可能有一些需要使用本机代码的原因：

-   需要处理一些硬件
-   要求非常苛刻的过程的性能改进
-   我们想要重用而不是用Java重写的现有库。

为了实现这一点，JDK 在我们的 JVM 中运行的字节码和本机代码(通常用 C 或 C++ 编写)之间引入了一个桥梁。

该工具称为JavaNative Interface。在本文中，我们将了解如何使用它编写一些代码。

## 2. 它是如何工作的

### 2.1. 本机方法：JVM 与编译代码相遇

Java 提供了native关键字，用于指示方法实现将由本机代码提供。

通常，在制作原生可执行程序时，我们可以选择使用静态库或共享库：

-   静态库——在链接过程中，所有库二进制文件都将作为我们可执行文件的一部分包含在内。因此，我们将不再需要库，但它会增加可执行文件的大小。
-   共享库——最终的可执行文件只有对库的引用，而不是代码本身。它要求我们运行可执行文件的环境可以访问我们程序使用的所有库文件。

后者对 JNI 有意义，因为我们不能将字节码和本机编译代码混合到同一个二进制文件中。

因此，我们的共享库会将本机代码单独保存在其.so/.dll/.dylib文件中(取决于我们使用的操作系统)，而不是我们类的一部分。

native关键字将我们的方法转换为一种抽象方法：

```java
private native void aNativeMethod();
```

主要区别在于它不是由另一个Java类实现，而是在一个单独的本机共享库中实现。

将在内存中构造一个表，其中包含指向我们所有本机方法的实现的指针，以便可以从我们的Java代码中调用它们。

### 2.2. 所需组件

以下是我们需要考虑的关键组件的简要说明。我们将在本文后面进一步解释它们

-  Java代码——我们的类。它们将包括至少一种本机方法。
-   本机代码——我们本机方法的实际逻辑，通常用 C 或 C++ 编码。
-   JNI 头文件——这个用于 C/C++ 的头文件(include/jni.h到 JDK 目录中)包括我们可以在本机程序中使用的 JNI 元素的所有定义。
-   C/C++ 编译器——我们可以在 GCC、Clang、Visual Studio 或任何其他我们喜欢的编译器之间进行选择，只要它能够为我们的平台生成本地共享库即可。

### 2.3. 代码中的 JNI 元素(Java 和 C/C++)

Java元素：

-   “native”关键字——正如我们已经介绍过的，任何标记为 native 的方法都必须在本地共享库中实现。
-   System.loadLibrary(String libname) – 一种静态方法，可将共享库从文件系统加载到内存中，并使其导出的函数可用于我们的Java代码。

C/C++ 元素(其中许多在jni.h中定义)

-   JNIEXPORT- 将共享库中的函数标记为可导出，因此它将包含在函数表中，因此 JNI 可以找到它
-   JNICALL – 结合JNIEXPORT，它确保我们的方法可用于 JNI 框架
-   JNIEnv——一个包含方法的结构，我们可以使用我们的本机代码来访问Java元素
-   JavaVM——一种让我们可以操作正在运行的 JVM(甚至启动一个新的)的结构，向它添加线程、销毁它等等……

## 3.你好世界JNI

接下来，让我们看看 JNI 在实践中是如何工作的。

在本教程中，我们将使用 C++ 作为本地语言，使用 G++ 作为编译器和链接器。

我们可以使用我们喜欢的任何其他编译器，但这里是在 Ubuntu、Windows 和 MacOS 上安装 G++ 的方法：

-   Ubuntu Linux –在终端中运行命令“sudo apt-get install build-essential”
-   Windows——[安装 MinGW](http://www.mingw.org/)
-   MacOS——在终端运行命令“g++”，如果它不存在，它会安装它。

### 3.1. 创建Java类

让我们通过实现经典的“Hello World”开始创建我们的第一个 JNI 程序。

首先，我们创建以下Java类，其中包含将执行工作的本机方法：

```java
package com.baeldung.jni;

public class HelloWorldJNI {

    static {
        System.loadLibrary("native");
    }
    
    public static void main(String[] args) {
        new HelloWorldJNI().sayHello();
    }

    // Declare a native method sayHello() that receives no arguments and returns void
    private native void sayHello();
}
```

如我们所见，我们将共享库加载到静态块中。这可确保它在我们需要时随时随地准备就绪。

或者，在这个简单的程序中，我们可以在调用我们的本机方法之前加载库，因为我们没有在其他任何地方使用本机库。

### 3.2. 在 C++ 中实现方法

现在，我们需要在 C++ 中创建本地方法的实现。

在 C++ 中，定义和实现通常分别存储在.h和.cpp文件中。

首先，要创建方法的定义，我们必须使用Java编译器的-h标志。需要注意的是，对于java 9之前的版本，我们应该使用[javah](https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javah.html)工具，而不是javac -h命令：

```bash
javac -h . HelloWorldJNI.java
```

这将生成一个com_baeldung_jni_HelloWorldJNI.h文件，其中包含作为参数传递的类中包含的所有本机方法，在这种情况下，只有一个：

```cpp
JNIEXPORT void JNICALL Java_com_baeldung_jni_HelloWorldJNI_sayHello
  (JNIEnv , jobject);

```

正如我们所见，函数名是使用完全限定的包、类和方法名自动生成的。

另外，我们可以注意到一些有趣的事情是我们将两个参数传递给我们的函数；指向当前JNIEnv 的指针；以及该方法附加到的Java对象，即我们的HelloWorldJNI类的实例。

现在，我们必须创建一个新的.cpp文件来实现sayHello函数。这是我们将执行将“Hello World”打印到控制台的操作的地方。

我们将使用与包含标头的 .h 相同的名称命名我们的.cpp文件，并添加此代码以实现本机功能：

```cpp
JNIEXPORT void JNICALL Java_com_baeldung_jni_HelloWorldJNI_sayHello
  (JNIEnv env, jobject thisObject) {
    std::cout << "Hello from C++ !!" << std::endl;
}

```

### 3.3. 编译和链接

至此，我们已经准备好所有需要的部分，并且在它们之间建立了连接。

我们需要从 C++ 代码构建我们的共享库并运行它！

为此，我们必须使用 G++ 编译器，不要忘记从我们的JavaJDK 安装中包含 JNI 头文件。

Ubuntu版本：

```bash
g++ -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux com_baeldung_jni_HelloWorldJNI.cpp -o com_baeldung_jni_HelloWorldJNI.o
```

视窗版本：

```java
g++ -c -I%JAVA_HOME%include -I%JAVA_HOME%includewin32 com_baeldung_jni_HelloWorldJNI.cpp -o com_baeldung_jni_HelloWorldJNI.o
```

苹果操作系统版本；

```bash
g++ -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/darwin com_baeldung_jni_HelloWorldJNI.cpp -o com_baeldung_jni_HelloWorldJNI.o
```

一旦我们为我们的平台编译代码到文件com_baeldung_jni_HelloWorldJNI.o中，我们必须将它包含在一个新的共享库中。无论我们决定如何命名，都是传递给System.loadLibrary方法的参数。

我们将我们的命名为“native”，我们将在运行Java代码时加载它。

然后 G++ 链接器将 C++ 目标文件链接到我们的桥接库中。

Ubuntu版本：

```bash
g++ -shared -fPIC -o libnative.so com_baeldung_jni_HelloWorldJNI.o -lc
```

视窗版本：

```bash
g++ -shared -o native.dll com_baeldung_jni_HelloWorldJNI.o -Wl,--add-stdcall-alias
```

macOS 版本：

```bash
g++ -dynamiclib -o libnative.dylib com_baeldung_jni_HelloWorldJNI.o -lc
```

就是这样！

我们现在可以从命令行运行我们的程序。

但是，我们需要将完整路径添加到包含我们刚刚生成的库的目录。这样Java就会知道在哪里寻找我们的原生库：

```bash
java -cp . -Djava.library.path=/NATIVE_SHARED_LIB_FOLDER com.baeldung.jni.HelloWorldJNI
```

控制台输出：

```bash
Hello from C++ !!
```

## 4. 使用高级 JNI 功能

打招呼很好，但不是很有用。通常，我们希望在Java和 C++ 代码之间交换数据，并在我们的程序中管理这些数据。

### 4.1. 向我们的本地方法添加参数

我们将向本地方法添加一些参数。让我们创建一个名为ExampleParametersJNI的新类，其中包含两个使用不同类型的参数和返回的本机方法：

```java
private native long sumIntegers(int first, int second);
    
private native String sayHelloToMe(String name, boolean isFemale);
```

然后，像之前一样用“javac -h”重复创建一个新的 .h 文件的过程。

现在使用新的 C++ 方法的实现创建相应的 .cpp 文件：

```cpp
...
JNIEXPORT jlong JNICALL Java_com_baeldung_jni_ExampleParametersJNI_sumIntegers 
  (JNIEnv env, jobject thisObject, jint first, jint second) {
    std::cout << "C++: The numbers received are : " << first << " and " << second << std::endl;
    return (long)first + (long)second;
}
JNIEXPORT jstring JNICALL Java_com_baeldung_jni_ExampleParametersJNI_sayHelloToMe 
  (JNIEnv env, jobject thisObject, jstring name, jboolean isFemale) {
    const char nameCharPointer = env->GetStringUTFChars(name, NULL);
    std::string title;
    if(isFemale) {
        title = "Ms. ";
    }
    else {
        title = "Mr. ";
    }

    std::string fullName = title + nameCharPointer;
    return env->NewStringUTF(fullName.c_str());
}
...
```

我们使用了JNIEnv类型的指针env来访问 JNI 环境实例提供的方法。

在这种情况下， JNIEnv允许我们将 Java字符串传递到我们的 C++ 代码中，然后退出而不用担心实现。

[我们可以在Oracle 官方文档](https://docs.oracle.com/en/java/javase/11/docs/specs/jni/types.html)中查看Java类型和 C JNI 类型的等价性。

为了测试我们的代码，我们必须重复前面HelloWorld示例的所有编译步骤。

### 4.2. 从本机代码使用对象和调用Java方法

在最后一个示例中，我们将了解如何将Java对象操作到我们的本机 C++ 代码中。

我们将开始创建一个新的类UserData，我们将使用它来存储一些用户信息：

```java
package com.baeldung.jni;

public class UserData {
    
    public String name;
    public double balance;
    
    public String getUserInfo() {
        return "[name]=" + name + ", [balance]=" + balance;
    }
}
```

然后，我们将使用一些本机方法创建另一个名为ExampleObjectsJNI的Java类，我们将使用这些方法管理UserData类型的对象：

```java
...
public native UserData createUser(String name, double balance);
    
public native String printUserData(UserData user);

```

再一次，让我们创建.h标头，然后在新的.cpp文件上创建本地方法的 C++ 实现：

```cpp
JNIEXPORT jobject JNICALL Java_com_baeldung_jni_ExampleObjectsJNI_createUser
  (JNIEnv env, jobject thisObject, jstring name, jdouble balance) {
  
    // Create the object of the class UserData
    jclass userDataClass = env->FindClass("com/baeldung/jni/UserData");
    jobject newUserData = env->AllocObject(userDataClass);
	
    // Get the UserData fields to be set
    jfieldID nameField = env->GetFieldID(userDataClass , "name", "Ljava/lang/String;");
    jfieldID balanceField = env->GetFieldID(userDataClass , "balance", "D");
	
    env->SetObjectField(newUserData, nameField, name);
    env->SetDoubleField(newUserData, balanceField, balance);
    
    return newUserData;
}

JNIEXPORT jstring JNICALL Java_com_baeldung_jni_ExampleObjectsJNI_printUserData
  (JNIEnv env, jobject thisObject, jobject userData) {
  	
    // Find the id of theJavamethod to be called
    jclass userDataClass=env->GetObjectClass(userData);
    jmethodID methodId=env->GetMethodID(userDataClass, "getUserInfo", "()Ljava/lang/String;");

    jstring result = (jstring)env->CallObjectMethod(userData, methodId);
    return result;
}


```

同样，我们使用JNIEnv env指针从正在运行的 JVM 访问所需的类、对象、字段和方法。

通常，我们只需要提供完整的类名来访问Java类，或者提供正确的方法名和签名来访问对象方法。

我们甚至在本机代码中创建了com.baeldung.jni.UserData类的实例。一旦我们有了这个实例，我们就可以用类似于Java反射的方式操作它的所有属性和方法。

我们可以将JNIEnv的所有其他方法查看到[Oracle 官方文档](https://docs.oracle.com/en/java/javase/11/docs/specs/jni/functions.html)中。

## 4.使用JNI的缺点

JNI 桥接确实有其缺陷。

主要缺点是对底层平台的依赖；我们基本上失去了Java的“一次编写，随处运行”的特性。这意味着我们必须为每个我们想要支持的平台和架构的新组合构建一个新的库。想象一下，如果我们支持 Windows、Linux、Android、MacOS……，这会对构建过程产生怎样的影响？

JNI不仅给我们的程序增加了一层复杂度。它还在运行到 JVM 的代码和我们的本机代码之间增加了一个代价高昂的通信层：我们需要在编组/解组过程中转换Java和 C++ 之间以两种方式交换的数据。

有时甚至没有类型之间的直接转换，所以我们必须编写我们的等价物。

## 5.总结

为特定平台编译代码(通常)比运行字节码更快。

这在我们需要加快要求苛刻的过程时非常有用。此外，当我们没有其他选择时，例如当我们需要使用管理设备的库时。

然而，这是有代价的，因为我们必须为我们支持的每个不同平台维护额外的代码。

这就是为什么在没有Java替代品的情况下只使用 JNI通常是个好主意。