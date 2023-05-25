package cn.tuyucheng.taketoday.interoperability

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ArrayUnitTest {

   @Test
   fun givenArray_whenValidateArrayType_thenComplete() {
      val ex = cn.tuyucheng.taketoday.interoperability.ArrayExample()
      val numArray = intArrayOf(1, 2, 3)

      assertEquals(ex.sumValues(numArray), 6)
   }

   @Test
   fun givenCustomer_whenGetSuperType_thenComplete() {
      val instance = cn.tuyucheng.taketoday.interoperability.Customer::class
      val supertypes = instance.supertypes

      assertEquals(supertypes[0].toString(), "kotlin.Any")
   }

   @Test
   fun givenCustomer_whenGetConstructor_thenComplete() {
      val instance = cn.tuyucheng.taketoday.interoperability.Customer::class.java
      val constructors = instance.constructors

      assertEquals(constructors.size, 1)
      assertEquals(constructors[0].name, "cn.tuyucheng.taketoday.interoperability.Customer")
   }

   fun makeReadFile() {
      val ax = cn.tuyucheng.taketoday.interoperability.ArrayExample()
      ax.writeList()
   }
}