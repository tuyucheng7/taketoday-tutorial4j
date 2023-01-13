# Create the header with javac -h . ClassName.java
# Remember to set your JAVA_HOME env var
g++ -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux cn_tuyucheng_taketoday_jni_HelloWorldJNI.cpp -o cn_tuyucheng_taketoday_jni_HelloWorldJNI.o
g++ -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux cn_tuyucheng_taketoday_jni_ExampleParametersJNI.cpp -o cn_tuyucheng_taketoday_jni_ExampleParametersJNI.o
g++ -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux cn_tuyucheng_taketoday_jni_ExampleObjectsJNI.cpp -o cn_tuyucheng_taketoday_jni_ExampleObjectsJNI.o
g++ -shared -fPIC -o ../../../native/linux_x86_64/libnative.so cn_tuyucheng_taketoday_jni_HelloWorldJNI.o cn_tuyucheng_taketoday_jni_ExampleParametersJNI.o cn_tuyucheng_taketoday_jni_ExampleObjectsJNI.o -lc
# Don't forget to set java.library.path to point to the folder where you have the libnative you're loading.