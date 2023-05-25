package cn.tuyucheng.taketoday.introduction

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class KotlinJavaInteroperabilityUnitTest {

   @Test
   fun givenLowercaseString_whenExecuteMethodFromJavaStringUtils_shouldReturnStringUppercase() {
      //given
      val name = "tom"

      //when
      val res = cn.tuyucheng.taketoday.introduction.StringUtils.toUpperCase(name)

      //then
      assertEquals(res, "TOM")
   }
}
