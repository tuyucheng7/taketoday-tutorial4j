package cn.tuyucheng.taketoday.localandtimezone;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.DefaultLocale;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@DefaultLocale(language = "zh")
public class LocaleUnitTest {

   @Test
   @DefaultLocale(language = "fr")
   void test_with_language() {
      assertThat(Locale.getDefault()).isEqualTo(Locale.forLanguageTag("fr"));
   }

   @Test
   @DefaultLocale(language = "en")
   void test_with_language_only() {
      assertThat(Locale.getDefault()).isEqualTo(new Locale.Builder().setLanguage("en").build());
   }

   @Test
   @DefaultLocale(language = "en", country = "EN")
   void test_with_language_and_country() {
      assertThat(Locale.getDefault()).isEqualTo(new Locale.Builder().setLanguage("en").setRegion("EN").build());
   }

   @Test
   @DefaultLocale(language = "ja", country = "JP", variant = "japanese")
   void test_with_language_and_country_and_variant() {
      assertThat(Locale.getDefault())
            .isEqualTo(new Locale.Builder().setLanguage("ja").setRegion("JP").setVariant("japanese").build());
   }

   @Test
   void test_with_class_level_configuration() {
      assertThat(Locale.getDefault()).isEqualTo(new Locale.Builder().setLanguage("zh").build());
   }

   @Test
   @DefaultLocale(language = "en")
   void test_with_method_level_configuration() {
      assertThat(Locale.getDefault()).isEqualTo(new Locale.Builder().setLanguage("en").build());
   }
}