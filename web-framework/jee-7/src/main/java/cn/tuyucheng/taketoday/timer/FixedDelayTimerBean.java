package cn.tuyucheng.taketoday.timer;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

/**
 * Created by  ccristianchiovari on 5/2/16.
 */
@Singleton
public class FixedDelayTimerBean {

   @EJB
   private WorkerBean workerBean;

   @Lock(LockType.READ)
   @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
   public void atSchedule() throws InterruptedException {
      workerBean.doTimerWork();
   }

}