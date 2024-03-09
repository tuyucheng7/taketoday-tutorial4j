package cn.tuyucheng.taketoday.readresolvevsreadobject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SingletonUnitTest {

   private static final String SINGLETON_SER = "singleton.ser";

   @AfterEach
   public void tearDown() {
      final File file = new File(SINGLETON_SER);
      if (file.exists()) {
         file.deleteOnExit();
      }
   }

   @Test
   public void testSingletonObj_withNoReadResolve() throws ClassNotFoundException, IOException {
      // Serialization
      FileOutputStream fos = new FileOutputStream(SINGLETON_SER);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      Singleton actualSingletonObject = Singleton.getInstance();
      oos.writeObject(actualSingletonObject);

      // Deserialization
      Singleton deserializedSingletonObject = null;
      FileInputStream fis = new FileInputStream(SINGLETON_SER);
      ObjectInputStream ois = new ObjectInputStream(fis);
      deserializedSingletonObject = (Singleton) ois.readObject();
      // remove readResolve() from Singleton class and uncomment this to test.
      // assertNotEquals(actualSingletonObject.hashCode(), deserializedSingletonObject.hashCode());

      fos.close();
      oos.close();
      fis.close();
      ois.close();
   }

   @Test
   public void testSingletonObj_withCustomReadResolve()
         throws ClassNotFoundException, IOException {
      // Serialization
      FileOutputStream fos = new FileOutputStream(SINGLETON_SER);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      Singleton actualSingletonObject = Singleton.getInstance();
      oos.writeObject(actualSingletonObject);

      // Deserialization
      Singleton deserializedSingletonObject;
      FileInputStream fis = new FileInputStream(SINGLETON_SER);
      ObjectInputStream ois = new ObjectInputStream(fis);
      deserializedSingletonObject = (Singleton) ois.readObject();
      assertEquals(actualSingletonObject.hashCode(), deserializedSingletonObject.hashCode());

      fos.close();
      oos.close();
      fis.close();
      ois.close();
   }
}
