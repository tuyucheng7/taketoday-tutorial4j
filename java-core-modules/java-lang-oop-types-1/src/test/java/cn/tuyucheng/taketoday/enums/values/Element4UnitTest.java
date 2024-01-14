/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.tuyucheng.taketoday.enums.values;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author chris
 */
public class Element4UnitTest {

   public Element4UnitTest() {
   }

   @BeforeAll
   public static void setUpClass() {
   }

   @AfterAll
   public static void tearDownClass() {
   }

   @BeforeEach
   public void setUp() {
   }

   @AfterEach
   public void tearDown() {
   }

   @Test
   public void whenLocatebyLabel_thenReturnCorrectValue() {
      for (Element4 e4 : Element4.values()) {
         assertSame(e4, Element4.valueOfLabel(e4.label));
      }
   }

   @Test
   public void whenLocatebyAtmNum_thenReturnCorrectValue() {
      for (Element4 e4 : Element4.values()) {
         assertSame(e4, Element4.valueOfAtomicNumber(e4.atomicNumber));
      }
   }

   @Test
   public void whenLocatebyAtmWt_thenReturnCorrectValue() {
      for (Element4 e4 : Element4.values()) {
         assertSame(e4, Element4.valueOfAtomicWeight(e4.atomicWeight));
      }
   }

   /**
    * Test of toString method, of class Element4.
    */
   @Test
   public void whenCallingToString_thenReturnLabel() {
      for (Element4 e4 : Element4.values()) {
         assertEquals(e4.label, e4.toString());
      }
   }

}
