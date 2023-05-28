package cn.tuyucheng.taketoday.timer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * author: Jacek Jackowiak
 */
@Startup
@Singleton
public class ProgrammaticTimerBean {

   @Inject
   Event<TimerEvent> event;

   @Resource
   TimerService timerService;

   @PostConstruct
   public void initialize() {
      ScheduleExpression scheduleExpression = new ScheduleExpression()
            .hour("*")
            .minute("*")
            .second("*/5");

      TimerConfig timerConfig = new TimerConfig();
      timerConfig.setInfo("Every 5 second timer");

      timerService.createCalendarTimer(scheduleExpression, timerConfig);
   }

   @Timeout
   public void programmaticTimout(Timer timer) {
      event.fire(new TimerEvent(timer.getInfo().toString()));
   }
}
