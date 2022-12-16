package cn.tuyucheng.taketoday.serializable.singleton;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

// Unit test for the Singleton class.
class SingletonUnitTest {

	// Checks that when a Singleton instance is serialized and then deserialized, its state is preserved.
	@Test
	void givenSingleton_whenSerializedAndDeserialized_thenStatePreserved() {
		Singleton s1 = Singleton.getInstance();

		s1.setState("State One");

		try (
			FileOutputStream fos = new FileOutputStream("singleton_test.txt");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			FileInputStream fis = new FileInputStream("singleton_test.txt");
			ObjectInputStream ois = new ObjectInputStream(fis)) {

			// Serializing.
			oos.writeObject(s1);

			// Deserializing.
			Singleton s2 = (Singleton) ois.readObject();

			// Checking if the state is preserved.
			assertEquals(s1.getState(), s2.getState());
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// Checking that when a Singleton instance is serialized and then deserialized, then there are two instances of the Singleton class.
	@Test
	void givenSingleton_whenSerializedAndDeserialized_thenTwoInstances() {
		Singleton s1 = Singleton.getInstance();

		try (
			FileOutputStream fos = new FileOutputStream("singleton_test.txt");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			FileInputStream fis = new FileInputStream("singleton_test.txt");
			ObjectInputStream ois = new ObjectInputStream(fis)) {

			// Serializing.
			oos.writeObject(s1);

			// Deserializing.
			Singleton s2 = (Singleton) ois.readObject();

			// Checking if s1 and s2 are not the same instance.
			assertNotEquals(s1, s2);

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}