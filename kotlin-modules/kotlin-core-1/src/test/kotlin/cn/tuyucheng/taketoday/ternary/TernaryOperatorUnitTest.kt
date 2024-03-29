package cn.tuyucheng.taketoday.ternary

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TernaryOperatorUnitTest {

   @Test
   fun `using If`() {
      val a = true
      val result = if (a) "yes" else "no"
      assertEquals("yes", result)
   }

   @Test
   fun `using When`() {
      val a = true
      val result = when (a) {
         true -> "yes"
         false -> "no"
      }
      assertEquals("yes", result)
   }

   @Test
   fun `using elvis`() {
      val a: String? = null
      val result = a ?: "Default"

      assertEquals("Default", result)
   }
}