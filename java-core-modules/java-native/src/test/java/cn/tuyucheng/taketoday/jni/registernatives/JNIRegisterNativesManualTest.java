package cn.tuyucheng.taketoday.jni.registernatives;

import cn.tuyucheng.taketoday.jni.RegisterNativesHelloWorldJNI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JNIRegisterNativesManualTest {
   @BeforeEach
   public void setup() {
      System.loadLibrary("native");
   }

   @Test
   public void whenRegisteredNativeHelloWorld_thenOutputIsAsExpected() {
      RegisterNativesHelloWorldJNI helloWorld = new RegisterNativesHelloWorldJNI();
      helloWorld.register();

      String helloFromNative = helloWorld.sayHello();

      assertNotNull(helloFromNative);
      assertTrue(helloFromNative.equals("Hello from registered native C++ !!"));
   }
}
