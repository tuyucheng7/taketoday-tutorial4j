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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author chris
 */
public class Element2UnitTest {
   private static final Logger LOGGER = LoggerFactory.getLogger(Element2UnitTest.class);

   public Element2UnitTest() {
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
      for (Element2 e2 : Element2.values()) {
         assertSame(e2, Element2.valueOfLabel(e2.label));
      }
   }

   /**
    * Test of toString method, of class Element2.
    */
   @Test
   public void whenCallingToString_thenReturnLabel() {
      for (Element2 e2 : Element2.values()) {
         assertEquals(e2.label, e2.toString());
      }
   }
}
