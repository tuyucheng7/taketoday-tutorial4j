#include "cn_tuyucheng_taketoday_jni_HelloWorldJNI.h"
#include <iostream>

/*
 * Class:     cn_tuyucheng_taketoday_jni_HelloWorldJNI
 * Method:    sayHello
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_cn_tuyucheng_taketoday_jni_HelloWorldJNI_sayHello (JNIEnv* env, jobject thisObject) {
	std::string hello = "Hello from C++ !!";
    std::cout << hello << std::endl;
    return env->NewStringUTF(hello.c_str());
}