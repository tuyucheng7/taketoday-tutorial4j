package cn.tuyucheng.taketoday.arquillan;

import cn.tuyucheng.taketoday.arquillian.CapsConvertor;
import cn.tuyucheng.taketoday.arquillian.CapsService;
import cn.tuyucheng.taketoday.arquillian.Car;
import cn.tuyucheng.taketoday.arquillian.CarEJB;
import cn.tuyucheng.taketoday.arquillian.Component;
import cn.tuyucheng.taketoday.arquillian.ConvertToLowerCase;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class ArquillianLiveTest {

   @Deployment
   public static JavaArchive createDeployment() {
      return ShrinkWrap.create(JavaArchive.class)
            .addClasses(Component.class, CapsService.class, CapsConvertor.class, ConvertToLowerCase.class, Car.class, CarEJB.class)
            .addAsResource("META-INF/persistence.xml")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
   }

   @Inject
   Component component;

   @Test
   public void givenMessage_WhenComponentSendMessage_ThenMessageReceived() {
      assertEquals("Message, MESSAGE", component.message(("MESSAGE")));
      component.sendMessage(System.out, "MESSAGE");
   }

   @Inject
   private CapsService capsService;

   @Test
   public void givenWord_WhenUppercase_ThenLowercase() {
      assertEquals("capitalize", capsService.getConvertedCaps("CAPITALIZE"));
      assertEquals("capitalize", capsService.getConvertedCaps("CAPITALIZE"));
   }

   @Inject
   private CarEJB carEJB;

   @Test
   public void testCars() {
      assertTrue(carEJB.findAllCars().isEmpty());
      Car c1 = new Car();
      c1.setName("Impala");
      Car c2 = new Car();
      c2.setName("Maverick");
      Car c3 = new Car();
      c3.setName("Aspen");
      Car c4 = new Car();
      c4.setName("Lincoln");
      carEJB.saveCar(c1);
      carEJB.saveCar(c2);
      carEJB.saveCar(c3);
      carEJB.saveCar(c4);
      assertEquals(4, carEJB.findAllCars().size());
      carEJB.deleteCar(c4);
      assertEquals(3, carEJB.findAllCars().size());
   }
}