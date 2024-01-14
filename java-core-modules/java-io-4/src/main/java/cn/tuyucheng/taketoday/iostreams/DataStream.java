package cn.tuyucheng.taketoday.iostreams;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class DataStream {
   public static void textDataProcessingByteStream(String fileName, String content) throws IOException {
      PrintStream out = new PrintStream(fileName);
      out.print(content);
      out.flush();
      out.close();
   }

   public static void textDataProcessingCharStream(String fileName, String content) throws IOException {
      PrintWriter out = new PrintWriter(fileName);
      out.print(content);
      out.flush();
      out.close();
   }

   public static void nonTextDataProcessing(String fileName, String streamOutputFile, String writerOutputFile) throws IOException {
      FileInputStream inputStream = new FileInputStream(fileName);
      PrintStream printStream = new PrintStream(streamOutputFile);

      int b;
      while ((b = inputStream.read()) != -1) {
         printStream.write(b);
      }
      printStream.close();

      FileReader reader = new FileReader(fileName);
      PrintWriter writer = new PrintWriter(writerOutputFile);

      int c;
      while ((c = reader.read()) != -1) {
         writer.write(c);
      }
      writer.close();
      inputStream.close();
   }
}