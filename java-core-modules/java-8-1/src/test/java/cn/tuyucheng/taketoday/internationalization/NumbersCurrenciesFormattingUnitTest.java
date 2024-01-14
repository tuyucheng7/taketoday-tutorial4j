package cn.tuyucheng.taketoday.internationalization;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class NumbersCurrenciesFormattingUnitTest {

   @Test
   public void givenDifferentLocalesAndDoubleNumber_whenNumberInstance_thenDifferentOutput() {
      Locale usLocale = Locale.US;
      Locale plLocale = new Locale("pl", "PL");
      Locale deLocale = Locale.GERMANY;
      double number = 102_300.456d;

      NumberFormat usNumberFormat = NumberFormat.getInstance(usLocale);
      NumberFormat plNumberFormat = NumberFormat.getInstance(plLocale);
      NumberFormat deNumberFormat = NumberFormat.getInstance(deLocale);

      Assertions.assertEquals(usNumberFormat.format(number), "102,300.456");
      Assertions.assertEquals(plNumberFormat.format(number), "102 300,456");
      Assertions.assertEquals(deNumberFormat.format(number), "102.300,456");
   }

   @Test
   public void givenDifferentLocalesAndDoubleAmount_whenCurrencyInstance_thenDifferentOutput() {
      Locale usLocale = Locale.US;
      Locale plLocale = new Locale("pl", "PL");
      Locale deLocale = Locale.GERMANY;
      BigDecimal number = new BigDecimal(102_300.456d);

      NumberFormat usNumberFormat = NumberFormat.getCurrencyInstance(usLocale);
      NumberFormat plNumberFormat = NumberFormat.getCurrencyInstance(plLocale);
      NumberFormat deNumberFormat = NumberFormat.getCurrencyInstance(deLocale);

      Assertions.assertEquals(usNumberFormat.format(number), "$102,300.46");
      Assertions.assertEquals(plNumberFormat.format(number), "102 300,46 zł");
      Assertions.assertEquals(deNumberFormat.format(number), "102.300,46 €");
   }

   @Test
   public void givenLocaleAndNumber_whenSpecificDecimalFormat_thenSpecificOutput() {
      Locale.setDefault(Locale.FRANCE);
      BigDecimal number = new BigDecimal(102_300.456d);

      DecimalFormat zeroDecimalFormat = new DecimalFormat("000000000.0000");
      DecimalFormat hashDecimalFormat = new DecimalFormat("###,###.#");
      DecimalFormat dollarDecimalFormat = new DecimalFormat("$###,###.##");

      Assertions.assertEquals(zeroDecimalFormat.format(number), "000102300,4560");
      Assertions.assertEquals(hashDecimalFormat.format(number), "102 300,5");
      Assertions.assertEquals(dollarDecimalFormat.format(number), "$102 300,46");
   }

   @Test
   public void givenLocaleAndNumber_whenSpecificDecimalFormatSymbols_thenSpecificOutput() {
      Locale.setDefault(Locale.FRANCE);
      BigDecimal number = new BigDecimal(102_300.456d);

      DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
      decimalFormatSymbols.setGroupingSeparator('^');
      decimalFormatSymbols.setDecimalSeparator('@');
      DecimalFormat separatorsDecimalFormat = new DecimalFormat("$###,###.##");
      separatorsDecimalFormat.setGroupingSize(4);
      separatorsDecimalFormat.setCurrency(Currency.getInstance(Locale.JAPAN));
      separatorsDecimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

      Assertions.assertEquals(separatorsDecimalFormat.format(number), "$10^2300@46");
   }
}
