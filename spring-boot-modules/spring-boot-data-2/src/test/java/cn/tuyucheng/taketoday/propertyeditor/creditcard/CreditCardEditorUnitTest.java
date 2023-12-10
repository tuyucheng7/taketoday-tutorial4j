package cn.tuyucheng.taketoday.propertyeditor.creditcard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CreditCardEditorUnitTest {

   private CreditCardEditor creditCardEditor;

   @BeforeEach
   void setup() {
      creditCardEditor = new CreditCardEditor();
   }

   @Test
   void whenInvalidCardNoWithLessDigits_thenThrowsException() {
      assertThrows(IllegalArgumentException.class, () -> creditCardEditor.setAsText("123-123-123-123"));
   }

   @Test
   void whenInvalidCardNoWithNonDigits_thenThrowsException() {
      assertThrows(IllegalArgumentException.class, () -> creditCardEditor.setAsText("1234-1234-xxxx-yyyy"));
   }

   @Test
   void whenCardNoWithNonDigits_parseCreditCard() {
      creditCardEditor.setAsText("1234-5678-9123-4560");

      CreditCard creditCard = (CreditCard) creditCardEditor.getValue();
      assertNotNull(creditCard);
      assertEquals(123456, creditCard.getBankIdNo().intValue());
      assertEquals(789123456, creditCard.getAccountNo().intValue());
      assertEquals(0, creditCard.getCheckCode().intValue());
   }
}