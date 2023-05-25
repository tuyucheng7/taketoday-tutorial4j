package cn.tuyucheng.taketoday.reflection

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import kotlin.reflect.full.findAnnotation

annotation class TuyuchengExample(val url: String)
annotation class NotTested(val reason: String)

@NotTested("We trust developers ^_*")
@TuyuchengExample("https://www.tuyucheng.com")
class TuyuchengClass

class AnnotationReflectionUnitTest {

   @Test
   fun `when a kotlin class has annotation, then we can access the expected annotation using findAnnotation()`() {
      val theClass = TuyuchengClass::class

      val notTestedAnnotation = theClass.findAnnotation<NotTested>()
      assertNotNull(notTestedAnnotation)
      assertEquals("We trust developers ^_*", notTestedAnnotation!!.reason)
   }

   @Test
   fun `when a kotlin class has annotation, then we can get all annotations`() {
      val theClass = TuyuchengClass::class
      val annotations = theClass.annotations
      assertEquals(2, annotations.size)
   }

   @Test
   fun `when a kotlin class has annotation, then we can access the expected annotation from the annotations property`() {
      val theClass = TuyuchengClass::class
      val tuyuchengAnnotation = theClass.annotations.filterIsInstance<TuyuchengExample>().first()
      assertNotNull(tuyuchengAnnotation)
      assertEquals("https://www.tuyucheng.com", tuyuchengAnnotation.url)
   }
}