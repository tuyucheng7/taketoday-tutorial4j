#include "cn_tuyucheng_taketoday_jni_RegisterNativesHelloWorldJNI.h"
#include <iostream>


JNIEXPORT jstring JNICALL hello (JNIEnv* env, jobject thisObject) {
    std::string hello = "Hello from registered native C++ !!";
    std::cout << hello << std::endl;
    return env->NewStringUTF(hello.c_str());
}

static JNINativeMethod methods[] = {
  {"sayHello", "()Ljava/lang/String;", (void*) &hello },
};


JNIEXPORT void JNICALL Java_cn_tuyucheng_taketoday_jni_RegisterNativesHelloWorldJNI_register (JNIEnv* env, jobject thsObject) {
    jclass clazz = env->FindClass("cn/tuyucheng/taketoday/jni/RegisterNativesHelloWorldJNI");

    (env)->RegisterNatives(clazz, methods, sizeof(methods)/sizeof(methods[0]));
}

