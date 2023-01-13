REM Create the header with javac -h . ClassName.java
REM Remember to set your JAVA_HOME env var
g++ -c -I%JAVA_HOME%\include -I%JAVA_HOME%\include\win32 cn_tuyucheng_taketoday_jni_HelloWorldJNI.cpp -o cn_tuyucheng_taketoday_jni_HelloWorldJNI.o
g++ -c -I%JAVA_HOME%\include -I%JAVA_HOME%\include\win32 cn_tuyucheng_taketoday_jni_ExampleParametersJNI.cpp -o cn_tuyucheng_taketoday_jni_ExampleParametersJNI.o
g++ -c -I%JAVA_HOME%\include -I%JAVA_HOME%\include\win32 cn_tuyucheng_taketoday_jni_ExampleObjectsJNI.cpp -o cn_tuyucheng_taketoday_jni_ExampleObjectsJNI.o
g++ -shared -o ..\..\..\native\win32\native.dll cn_tuyucheng_taketoday_jni_HelloWorldJNI.o cn_tuyucheng_taketoday_jni_ExampleParametersJNI.o cn_tuyucheng_taketoday_jni_ExampleObjectsJNI.o -Wl,--add-stdcall-alias