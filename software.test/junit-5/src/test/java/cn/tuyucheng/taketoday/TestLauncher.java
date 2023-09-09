package cn.tuyucheng.taketoday;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.io.PrintWriter;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class TestLauncher {

   public static void main(String[] args) {
      LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
            .selectors(selectClass("cn.tuyucheng.taketoday.EmployeesUnitTest"))
            .configurationParameter("junit.conditions.deactivate", "cn.tuyucheng.taketoday.extensions.*")
            .configurationParameter("junit.jupiter.extensions.autodetection.enabled", "true")
            .build();

      TestPlan plan = LauncherFactory.create().discover(request);
      Launcher launcher = LauncherFactory.create();
      SummaryGeneratingListener summaryGeneratingListener = new SummaryGeneratingListener();
      launcher.execute(request, summaryGeneratingListener);
      launcher.execute(request);

      summaryGeneratingListener.getSummary().printTo(new PrintWriter(System.out));
   }
}