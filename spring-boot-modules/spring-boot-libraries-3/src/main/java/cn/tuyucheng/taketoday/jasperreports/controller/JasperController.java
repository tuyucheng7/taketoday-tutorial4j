package cn.tuyucheng.taketoday.jasperreports.controller;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
public class JasperController {

   protected Logger logger = Logger.getLogger(JasperController.class.getName());
   public static int count = 0;

   @Autowired
   JRFileVirtualizer fv;
   @Value("${directory}")
   private String directory;

   @Autowired
   DataSource datasource;
   @Autowired
   JasperReport jasperReport;

   @ResponseBody
   @RequestMapping(value = "/pdf/{age}")
   public ResponseEntity<InputStreamResource> getReport(@PathVariable("age") int age) {
      logger.info("getReport(" + age + ")");
      Map<String, Object> m = new HashMap<>();
      m.put("age", age);
      String name = ++count + "personReport.pdf";
      return generateReport(name, m);
   }

   @ResponseBody
   @RequestMapping(value = "/pdf/fv/{age}")
   public ResponseEntity<InputStreamResource> getReportFv(@PathVariable("age") int age) {
      logger.info("getReportFv(" + age + ")");
      Map<String, Object> m = new HashMap<>();
      m.put(JRParameter.REPORT_VIRTUALIZER, fv);
      m.put("age", age);
      String name = ++count + "personReport.pdf";
      return generateReport(name, m);
   }

   @ResponseBody
   @RequestMapping(value = "/pdf/sfv/{age}")
   public ResponseEntity<InputStreamResource> getReportSfv(@PathVariable("age") int age) {
      logger.info("getReportSfv(" + age + ")");
      JRSwapFile sf = new JRSwapFile(directory, 1024, 100);
      JRSwapFileVirtualizer sfv = new JRSwapFileVirtualizer(20, sf, true);
      Map<String, Object> m = new HashMap<>();
      m.put(JRParameter.REPORT_VIRTUALIZER, sfv);
      m.put("age", age);
      String name = ++count + "personReport.pdf";
      return generateReport(name, m);
   }

   private ResponseEntity<InputStreamResource> generateReport(String name, Map<String, Object> params) {
      FileInputStream st;
      Connection cc = null;
      try {
         cc = datasource.getConnection();
         JasperPrint p = JasperFillManager.fillReport(jasperReport, params, cc);
         JRPdfExporter exporter = new JRPdfExporter();
         SimpleOutputStreamExporterOutput c = new SimpleOutputStreamExporterOutput(name);
         exporter.setExporterInput(new SimpleExporterInput(p));
         exporter.setExporterOutput(c);
         exporter.exportReport();

         st = new FileInputStream(name);
         HttpHeaders responseHeaders = new HttpHeaders();
         responseHeaders.setContentType(MediaType.valueOf("application/pdf"));
         responseHeaders.setContentDispositionFormData("attachment", name);
         responseHeaders.setContentLength(st.available());
         return new ResponseEntity<>(new InputStreamResource(st), responseHeaders, HttpStatus.OK);
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         fv.cleanup();
         if (cc != null)
            try {
               cc.close();
            } catch (SQLException e) {
               e.printStackTrace();
            }
      }
      return null;
   }
}