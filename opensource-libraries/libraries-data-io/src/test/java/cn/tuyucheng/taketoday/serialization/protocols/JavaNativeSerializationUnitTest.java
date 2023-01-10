package cn.tuyucheng.taketoday.serialization.protocols;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertEquals;

public class JavaNativeSerializationUnitTest {

	@Test
	public void whenUsingJavaNativeSerialization_ThenObjectIsTheSameAfterDeserialization() throws IOException, ClassNotFoundException {

		User user = new User();
		user.setId(1);
		user.setName("Mark");

		String filePath = "src/test/resources/protocols/user.txt";

		FileOutputStream fileOutputStream = new FileOutputStream(filePath);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		objectOutputStream.writeObject(user);
		objectOutputStream.flush();
		objectOutputStream.close();

		FileInputStream fileInputStream = new FileInputStream(filePath);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
		User deserializedUser = (User) objectInputStream.readObject();
		objectInputStream.close();

		assertEquals(1, deserializedUser.getId());
		assertEquals("Mark", deserializedUser.getName());
	}
}