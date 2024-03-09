package cn.tuyucheng.taketoday.hexformat;

import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringFormattingUnitTest {

   private HexFormat hexFormat = HexFormat.of().withPrefix("[").withSuffix("]").withDelimiter(", ");

   @Test
   public void givenInitialisedHexFormatWithFormattedStringOptions_whenByteArrayIsPassed_thenHexStringRepresentationFormattedCorrectlyIsReturned() {
      assertEquals("[48], [0c], [11]", hexFormat.formatHex(new byte[]{72, 12, 17}));
   }
}
