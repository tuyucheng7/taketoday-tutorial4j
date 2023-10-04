package cn.tuyucheng.taketoday.stringtemplates;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringTemplatesUnitTest {

   @Test
   public void whenStringConcat_thenReturnComposedString() {
      StringCompositionTechniques stringCompositionTechniques = new StringCompositionTechniques();
      assertEquals("Today's weather is pleasant, with a temperature of 25 degrees Celsius", stringCompositionTechniques.composeUsingPlus("pleasant", "25", "Celsius"));
   }

   @Test
   public void whenStringBuffer_thenReturnComposedString() {
      StringCompositionTechniques stringCompositionTechniques = new StringCompositionTechniques();
      assertEquals("Today's weather is pleasant, with a temperature of 25 degrees Celsius", stringCompositionTechniques.composeUsingStringBuffer("pleasant", "25", "Celsius"));
   }

   @Test
   public void whenStringBuilder_thenReturnComposedString() {
      StringCompositionTechniques stringCompositionTechniques = new StringCompositionTechniques();
      assertEquals("Today's weather is pleasant, with a temperature of 25 degrees Celsius", stringCompositionTechniques.composeUsingStringBuilder("pleasant", "25", "Celsius"));
   }

   @Test
   public void whenStringFormatter_thenReturnComposedFormattedString() {
      StringCompositionTechniques stringCompositionTechniques = new StringCompositionTechniques();
      assertEquals("Today's weather is pleasant, with a temperature of 25 degrees Celsius", stringCompositionTechniques.composeUsingFormatters("pleasant", "25", "Celsius"));
   }

   @Test
   public void whenMessageFormatter_thenReturnComposedFormattedString() {
      StringCompositionTechniques stringCompositionTechniques = new StringCompositionTechniques();
      assertEquals("Today's weather is pleasant, with a temperature of 25 degrees Celsius", stringCompositionTechniques.composeUsingMessageFormatter("pleasant", "25", "Celsius"));
   }

   @Test
   public void whenUsingStringTemplateSTR_thenReturnInterpolatedString() {
      StringTemplateExamples templateExamples = new StringTemplateExamples();
      assertEquals("Today's weather is pleasant, with a temperature of 25 degrees Celsius", templateExamples.interpolationUsingSTRProcessor("pleasant", "25", "Celsius"));
   }

   @Test
   public void whenUsingMultilineStringTemplateSTR_thenReturnInterpolatedString() {
      StringTemplateExamples templateExamples = new StringTemplateExamples();
      assertEquals("{\n" + "  \"feelsLike\": \"pleasant\",\n" + "  \"temperature\": \"25\",\n" + "  \"unit\": \"Celsius\"\n" + "}\n", templateExamples.interpolationOfJSONBlock("pleasant", "25", "Celsius"));
   }

   @Test
   public void whenUsingExpressionSTR_thenReturnInterpolatedString() {
      StringTemplateExamples templateExamples = new StringTemplateExamples();
      assertEquals("Today's weather is pleasant, with a temperature of 25 degrees Celsius", templateExamples.interpolationWithExpressions());
   }

   @Test
   public void whenUsingExpressionRAW_thenReturnInterpolatedString() {
      StringTemplateExamples templateExamples = new StringTemplateExamples();
      assertEquals("Today's weather is pleasant, with a temperature of 25 degrees Celsius", templateExamples.interpolationWithTemplates());
   }

   @Test
   public void whenUsingExpressionFMT_thenReturnInterpolatedString() {
      StringTemplateExamples templateExamples = new StringTemplateExamples();
      assertEquals("{\n" + "  \"feelsLike\": \"pleasant\",\n" + "  \"temperature\": \"25.86\",\n" + "  \"unit\": \"Celsius\"\n" + "}\n", templateExamples.interpolationOfJSONBlockWithFMT("pleasant", 25.8636F, "Celsius"));
   }

   @Test
   public void givenJSONString_whenInterpolate_thenReturnJsonObject() {
      StringTemplate.Processor<JsonObject, RuntimeException> JSON = StringTemplate.Processor.of(
            st -> JsonParser.parseString(st.interpolate())
                  .getAsJsonObject()
      );

      String username = "Doe";
      String phone = "1234567";
      String address = "China, Wuhan";

      JsonObject doc = JSON."""
            {
                "username":"\{username}",
                "phone":   "\{phone}",
                "address": "\{address}"
            }
            """;

      assertThat(doc.get("username").getAsString()).isEqualTo(username);
      assertThat(doc.get("phone").getAsString()).isEqualTo(phone);
      assertThat(doc.get("address").getAsString()).isEqualTo(address);
   }

   @Test
   public void givenCustomDBTemplateProcessor_whenQueryByName_thenReturnedResultSetShouldCorrect() {
      final String url = "jdbc:h2:mem:test", user = "sa", password = "", driver = "org.h2.Driver";

      try {
         Class.forName(driver);
         var conn = DriverManager.getConnection(url, user, password);

         var stmt = conn.createStatement();

         stmt.execute("""
               create table users(
                   id int primary key,
                   name varchar(40),
                   password varchar(40))
               """);

         stmt.executeUpdate("INSERT INTO users VALUES(1,'jack','12'), (2,'mike','34'), (3,'joe','56'), (4,'smith','78')");

         var DB = new QueryProcessor(conn);
         var name = "smith";
         ResultSet rs = DB."SELECT * FROM users WHERE name = \{name}";

         assertThat(rs.next()).isTrue();
         assertThat(rs.getInt("password")).isEqualTo(78);
      } catch (SQLException | ClassNotFoundException e) {
         throw new RuntimeException(e);
      }
   }
}