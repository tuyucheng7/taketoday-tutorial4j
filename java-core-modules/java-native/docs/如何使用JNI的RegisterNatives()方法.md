## 1. 概述

在这个简短的教程中，我们将了解[JNI ](https://www.baeldung.com/jni)RegisterNatives()方法，该方法用于创建Java和 C++ 函数之间的映射。

首先，我们将解释 JNI RegisterNatives()的工作原理。然后，我们将展示如何在java.lang.Object的 registerNatives()方法中使用它。最后，我们将展示如何在我们自己的Java和 C++ 代码中使用该功能。

## 2. JNI RegisterNatives 方法

JVM 有两种方法来查找[本地](https://www.baeldung.com/java-native)方法并将其与Java代码链接。第一个是以特定方式调用本机函数，以便 JVM 可以找到它。另一种方法是使用 JNI RegisterNatives()方法。

顾名思义，RegisterNatives()使用作为参数传递的类注册本机方法。通过使用这种方法，我们可以随意命名我们的 C++ 函数。

事实上，java.lang.Object的 registerNatives()方法使用的是第二种方法。让我们看看[OpenJDK 8中](http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/687fd7c7986d/src/share/native/java/lang/Object.c)java.lang.Object的 registerNatives() 方法在 C 中的实现：

```c
static JNINativeMethod methods[] = {
    {"hashCode",    "()I",                    (void )&JVM_IHashCode},
    {"wait",        "(J)V",                   (void )&JVM_MonitorWait},
    {"notify",      "()V",                    (void )&JVM_MonitorNotify},
    {"notifyAll",   "()V",                    (void )&JVM_MonitorNotifyAll},
    {"clone",       "()Ljava/lang/Object;",   (void )&JVM_Clone},
};

JNIEXPORT void JNICALL
Java_java_lang_Object_registerNatives(JNIEnv env, jclass cls)
{
    (env)->RegisterNatives(env, cls,
                            methods, sizeof(methods)/sizeof(methods[0]));
}

```

首先，初始化method[]数组以存储Java和 C++ 函数名称之间的映射。然后，我们看到一个以非常具体的方式命名的方法，Java_java_lang_Object_registerNatives。

通过这样做，JVM 能够将它链接到本机java.lang.Object的 registerNatives() 方法。在其中，method[]数组用于 RegisterNatives()方法调用。

现在，让我们看看如何在我们自己的代码中使用它。

## 3.使用RegisterNatives方法

让我们从Java类开始：

```java
public class RegisterNativesHelloWorldJNI {

    public native void register();
    public native String sayHello();

    public static void main(String[] args) {
        RegisterNativesHelloWorldJNI helloWorldJNI = new RegisterNativesHelloWorldJNI();
        helloWorldJNI.register();
        helloWorldJNI.sayHello();
    }
}

```

我们定义了两个本地方法，register()和sayHello()。 前者将使用RegisterNatives()方法注册自定义 C++ 函数，以便在调用本机sayHello() 方法时使用。

让我们看看Java的register()本地方法的 C++ 实现 ：

```cpp
static JNINativeMethod methods[] = {
  {"sayHello", "()Ljava/lang/String;", (void) &hello },
};


JNIEXPORT void JNICALL Java_com_baeldung_jni_RegisterNativesHelloWorldJNI_register (JNIEnv env, jobject thsObject) {
    jclass clazz = env->FindClass("com/baeldung/jni/RegisterNativesHelloWorldJNI");

    (env)->RegisterNatives(clazz, methods, sizeof(methods)/sizeof(methods[0]));
}

```

与java.lang.Object示例类似，我们首先创建一个数组来保存Java和 C++ 方法之间的映射。

然后，我们看到一个使用完全限定的Java_com_baeldung_jni_RegisterNativesHelloWorldJNI_register名称调用的函数。不幸的是，必须以这种方式调用它，以便 JVM 找到它并将其与Java代码链接。 

该函数做了两件事。首先，它找到所需的Java类。然后，它调用RegisterNatives()方法并将类和映射数组传递给它。

现在，我们可以随意调用第二个本地方法sayHello()：

```cpp
JNIEXPORT jstring JNICALL hello (JNIEnv env, jobject thisObject) {
    std::string hello = "Hello from registered native C++ !!";
    std::cout << hello << std::endl;
    return env->NewStringUTF(hello.c_str());
}
```

我们没有使用完全限定名称，而是使用了一个更短、更有意义的名称。

最后，让我们从RegisterNativesHelloWorldJNI 类运行main()方法：

```bash
Hello from registered native C++ !!
```

## 4. 总结

在本文中，我们讨论了 JNI RegisterNatives()方法。首先，我们解释了java.lang.Object.registerNatives()方法在幕后的作用。然后，我们讨论了为什么使用 JNI RegisterNatives() 方法可能会有用。最后，我们展示了如何在我们自己的Java和 C++ 代码中使用它。