package cn.tuyucheng.taketoday.localandtimezone;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.DefaultTimeZone;

import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

@DefaultTimeZone("CET")
public class TimeZoneUnitTest {

   @Test
   @DefaultTimeZone("CET")
   void test_with_short_zone_id() {
      assertThat(TimeZone.getDefault()).isEqualTo(TimeZone.getTimeZone("CET"));
   }

   @Test
   @DefaultTimeZone("Africa/Juba")
   void test_with_long_zone_id() {
      assertThat(TimeZone.getDefault()).isEqualTo(TimeZone.getTimeZone("Africa/Juba"));
   }

   @Test
   void test_with_class_level_configuration() {
      assertThat(TimeZone.getDefault()).isEqualTo(TimeZone.getTimeZone("CET"));
   }

   @Test
   @DefaultTimeZone("Africa/Juba")
   void test_with_method_level_configuration() {
      assertThat(TimeZone.getDefault()).isEqualTo(TimeZone.getTimeZone("Africa/Juba"));
   }
}