# Create the header with javac -h . ClassName.java
# Remember to set your JAVA_HOME env var
g++ -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/darwin cn_tuyucheng_taketoday_jni_HelloWorldJNI.cpp -o cn_tuyucheng_taketoday_jni_HelloWorldJNI.o
g++ -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/darwin cn_tuyucheng_taketoday_jni_ExampleParametersJNI.cpp -o cn_tuyucheng_taketoday_jni_ExampleParametersJNI.o
g++ -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/darwin cn_tuyucheng_taketoday_jni_ExampleObjectsJNI.cpp -o cn_tuyucheng_taketoday_jni_ExampleObjectsJNI.o
g++ -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/darwin cn_tuyucheng_taketoday_jni_RegisterNativesHelloWorldJNI.cpp -o cn_tuyucheng_taketoday_jni_RegisterNativesHelloWorldJNI.o
g++ -dynamiclib -o ../../../native/macos/libnative.dylib cn_tuyucheng_taketoday_jni_HelloWorldJNI.o cn_tuyucheng_taketoday_jni_ExampleParametersJNI.o cn_tuyucheng_taketoday_jni_ExampleObjectsJNI.o cn_tuyucheng_taketoday_jni_RegisterNativesHelloWorldJNI.o -lc