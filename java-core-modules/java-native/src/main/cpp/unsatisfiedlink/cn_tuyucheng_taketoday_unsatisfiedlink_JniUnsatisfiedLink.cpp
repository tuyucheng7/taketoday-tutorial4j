#include "cn_tuyucheng_taketoday_unsatisfiedlink_JniUnsatisfiedLink.h"
#include <iostream>

/*
 * Class:     cn_tuyucheng_taketoday_unsatisfiedlink_JniUnsatisfiedLink
 * Method:    test
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_cn_tuyucheng_taketoday_unsatisfiedlink_JniUnsatisfiedLink_test (JNIEnv* env, jobject thisObject) {
	std::string test = "Test OK";
    std::cout << test << std::endl;
    return env->NewStringUTF(test.c_str());
}
