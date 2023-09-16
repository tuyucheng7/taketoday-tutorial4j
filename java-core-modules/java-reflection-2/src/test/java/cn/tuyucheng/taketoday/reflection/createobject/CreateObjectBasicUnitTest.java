package cn.tuyucheng.taketoday.reflection.createobject;

import cn.tuyucheng.taketoday.reflection.createobject.basic.BronzeJobCard;
import cn.tuyucheng.taketoday.reflection.createobject.basic.GoldJobCard;
import cn.tuyucheng.taketoday.reflection.createobject.basic.MaintenanceJob;
import cn.tuyucheng.taketoday.reflection.createobject.basic.PaintJob;
import cn.tuyucheng.taketoday.reflection.createobject.basic.RepairJob;
import cn.tuyucheng.taketoday.reflection.createobject.basic.SilverJobCard;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateObjectBasicUnitTest {
   @Test
   public void givenBronzeJobCard_whenJobTypeRepairAndMaintenance_thenStartJob() throws ClassNotFoundException,
         InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

      BronzeJobCard bronzeJobCard1 = new BronzeJobCard();
      bronzeJobCard1.setJobType("cn.tuyucheng.taketoday.reflection.createobject.basic.RepairJob");
      assertEquals("Start Bronze Repair Job", bronzeJobCard1.startJob());

      BronzeJobCard bronzeJobCard2 = new BronzeJobCard();
      bronzeJobCard2.setJobType("cn.tuyucheng.taketoday.reflection.createobject.basic.MaintenanceJob");
      assertEquals("Start Bronze Maintenance Job", bronzeJobCard2.startJob());
   }

   @Test
   public void givenBronzeJobCard_whenJobTypePaint_thenFailJob() throws ClassNotFoundException,
         InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

      BronzeJobCard bronzeJobCard = new BronzeJobCard();
      bronzeJobCard.setJobType("cn.tuyucheng.taketoday.reflection.createobject.basic.PaintJob");
      assertEquals("Bronze Job Failed", bronzeJobCard.startJob());
   }

   @Test
   public void givenSilverJobCard_whenJobTypeRepairAndMaintenance_thenStartJob() throws InvocationTargetException,
         NoSuchMethodException, InstantiationException, IllegalAccessException {

      SilverJobCard silverJobCard1 = new SilverJobCard();
      silverJobCard1.setJobType(RepairJob.class);
      assertEquals("Start Silver Repair Job", silverJobCard1.startJob());

      SilverJobCard silverJobCard2 = new SilverJobCard();
      silverJobCard2.setJobType(MaintenanceJob.class);
      assertEquals("Start Silver Maintenance Job", silverJobCard2.startJob());
   }

   @Test
   public void givenSilverJobCard_whenJobTypePaint_thenFailJob() throws ClassNotFoundException,
         InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

      SilverJobCard silverJobCard = new SilverJobCard();
      silverJobCard.setJobType(PaintJob.class);
      assertEquals("Silver Job Failed", silverJobCard.startJob());
   }

   @Test
   public void givenGoldJobCard_whenJobTypeRepairMaintenanceAndPaint_thenStartJob() throws InvocationTargetException,
         NoSuchMethodException, InstantiationException, IllegalAccessException {

      GoldJobCard<RepairJob> goldJobCard1 = new GoldJobCard<RepairJob>();
      goldJobCard1.setJobType(RepairJob.class);
      assertEquals("Start Gold Repair Job", goldJobCard1.startJob());

      GoldJobCard<MaintenanceJob> goldJobCard2 = new GoldJobCard<MaintenanceJob>();
      goldJobCard2.setJobType(MaintenanceJob.class);
      assertEquals("Start Gold Maintenance Job", goldJobCard2.startJob());

      GoldJobCard<PaintJob> goldJobCard3 = new GoldJobCard<PaintJob>();
      goldJobCard3.setJobType(PaintJob.class);
      assertEquals("Start Gold Paint Job", goldJobCard3.startJob());
   }
}