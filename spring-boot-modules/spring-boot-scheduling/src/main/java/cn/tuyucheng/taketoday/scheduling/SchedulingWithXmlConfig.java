package cn.tuyucheng.taketoday.scheduling;

public class SchedulingWithXmlConfig {

   public void scheduleFixedDelayTask() {
      System.out.println(STR."Fixed delay task - \{System.currentTimeMillis() / 1000}");
   }

   public void scheduleFixedRateTask() {
      System.out.println(STR."Fixed rate task - \{System.currentTimeMillis() / 1000}");
   }

   public void scheduleTaskUsingCronExpression() {
      System.out.println(STR."schedule tasks using cron expressions - \{System.currentTimeMillis() / 1000}");
   }
}