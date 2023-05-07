package cn.tuyucheng.taketoday.jasperreports;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.util.JRSwapFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

@SpringBootApplication
public class JasperApplication {

   @Value("${directory}")
   private String directory;

   public static void main(String[] args) {
      SpringApplication.run(JasperApplication.class, args);
   }

   @Bean
   JasperReport report() throws JRException {
      JasperReport jr;
      File f = new File("personReport.jasper");
      if (f.exists()) {
         jr = (JasperReport) JRLoader.loadObject(f);
      } else {
         jr = JasperCompileManager.compileReport("src/main/resources/report.jrxml");
         JRSaver.saveObject(jr, "personReport.jasper");
      }
      return jr;
   }

   @Bean
   JRFileVirtualizer fileVirtualizer() {
      return new JRFileVirtualizer(100, directory);
   }

   @Bean
   JRSwapFileVirtualizer swapFileVirtualizer() {
      JRSwapFile sf = new JRSwapFile(directory, 1024, 100);
      return new JRSwapFileVirtualizer(20, sf, true);
   }
}