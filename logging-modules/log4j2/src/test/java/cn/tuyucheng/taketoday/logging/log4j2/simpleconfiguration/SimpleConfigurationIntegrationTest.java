/**
 * This class invokes the configuration factory through the run time property,
 * as defined in section 4.2 of the "Programmatic Configuration with Log4j 2"
 **/
package cn.tuyucheng.taketoday.logging.log4j2.simpleconfiguration;

import cn.tuyucheng.taketoday.logging.log4j2.Log4j2BaseIntegrationTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SimpleConfigurationIntegrationTest extends Log4j2BaseIntegrationTest {

   @BeforeAll
   static void setUp() {
      PluginManager.addPackage("cn.tuyucheng.taketoday.logging.log4j2.simpleconfiguration");
   }

   @Test
   void givenSimpleConfigurationPlugin_whenUsingFlowMarkers_thenLogsCorrectly() throws Exception {
      Logger logger = LogManager.getLogger(this.getClass());
      Marker markerContent = MarkerManager.getMarker("FLOW");
      logger.debug(markerContent, "Debug log message");
      logger.info(markerContent, "Info log message");
      logger.error(markerContent, "Error log message");
   }
}