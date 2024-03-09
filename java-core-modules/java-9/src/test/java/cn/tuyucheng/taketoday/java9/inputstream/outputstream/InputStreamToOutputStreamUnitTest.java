package cn.tuyucheng.taketoday.java9.inputstream.outputstream;

import com.google.common.io.ByteStreams;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InputStreamToOutputStreamUnitTest {

   /**
    * Reads all bytes from an input stream and writes them to an output stream.
    *
    * @param source - input stream to copy data from
    * @param target - output stream to copy data too
    */
   void copy(InputStream source, OutputStream target) throws IOException {
      byte[] buf = new byte[8192];
      int length;
      while ((length = source.read(buf)) != -1) {
         target.write(buf, 0, length);
      }
   }

   @Test
   public void givenUsingJavaEight_whenCopyingInputStreamToOutputStream_thenCorrect() throws IOException {
      String initialString = "Hello World!";

      try (InputStream inputStream = new ByteArrayInputStream(initialString.getBytes());
           ByteArrayOutputStream targetStream = new ByteArrayOutputStream()) {
         copy(inputStream, targetStream);

         assertEquals(initialString, new String(targetStream.toByteArray()));
      }
   }

   @Test
   public void givenUsingJavaEight_whenCopyingLongInputStreamToOutputStream_thenCorrect() throws IOException {
      String initialString = randomAlphabetic(20480);

      try (InputStream inputStream = new ByteArrayInputStream(initialString.getBytes());
           ByteArrayOutputStream targetStream = new ByteArrayOutputStream()) {
         copy(inputStream, targetStream);

         assertEquals(initialString, new String(targetStream.toByteArray()));
      }
   }

   @Test
   public void givenUsingJavaNine_whenCopyingInputStreamToOutputStream_thenCorrect() throws IOException {
      String initialString = "Hello World!";

      try (InputStream inputStream = new ByteArrayInputStream(initialString.getBytes());
           ByteArrayOutputStream targetStream = new ByteArrayOutputStream()) {
         inputStream.transferTo(targetStream);

         assertEquals(initialString, new String(targetStream.toByteArray()));
      }
   }

   @Test
   public void givenUsingGuava_whenCopyingInputStreamToOutputStream_thenCorrect() throws IOException {
      String initialString = "Hello World!";

      try (InputStream inputStream = new ByteArrayInputStream(initialString.getBytes());
           ByteArrayOutputStream targetStream = new ByteArrayOutputStream()) {
         ByteStreams.copy(inputStream, targetStream);

         assertEquals(initialString, new String(targetStream.toByteArray()));
      }
   }

   @Test
   public void givenUsingCommonsIO_whenCopyingInputStreamToOutputStream_thenCorrect() throws IOException {
      String initialString = "Hello World!";

      try (InputStream inputStream = new ByteArrayInputStream(initialString.getBytes());
           ByteArrayOutputStream targetStream = new ByteArrayOutputStream()) {
         IOUtils.copy(inputStream, targetStream);

         assertEquals(initialString, new String(targetStream.toByteArray()));
      }
   }
}
