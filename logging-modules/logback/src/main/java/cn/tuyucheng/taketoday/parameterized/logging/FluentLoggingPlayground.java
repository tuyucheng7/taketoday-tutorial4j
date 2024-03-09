package cn.tuyucheng.taketoday.parameterized.logging;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Slf4j
public class FluentLoggingPlayground {

   public static void main(String[] args) {

      Exception exceptionCause = new Exception(new IllegalArgumentException("Something unprocessable"));

      LOGGER.atInfo()
            .setMessage("App is running at {}, zone = {}")
            .addArgument(LocalDateTime.now())
            .addArgument(ZonedDateTime.now().getZone())
            .log();

      LOGGER.atInfo()
            .setMessage("App is running at {}, zone = {}")
            .addArgument(LocalDateTime.now())
            .addArgument(ZonedDateTime.now().getZone())
            .setCause(exceptionCause)
            .log();

      LOGGER.atInfo()
            .setMessage("App is running at")
            .addKeyValue("time", LocalDateTime.now())
            .addKeyValue("zone", ZonedDateTime.now().getZone())
            .setCause(exceptionCause)
            .log();
   }
}