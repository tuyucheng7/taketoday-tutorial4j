package cn.tuyucheng.taketoday.annotation.scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AnnotationScannerApplication.class)
class SampleAnnotationScannerUnitTest {
   @Autowired
   private List<SampleAnnotationScanner> scannerList;

   @Test
   void givenPackage_whenScanAnnotatedClasses_thenAnnotationValues() {
      final List<String> annotatedClasses = scannerList.stream()
            .filter(SampleAnnotationScanner::supportsClassScan)
            .map(SampleAnnotationScanner::scanAnnotatedClasses)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

      assertNotNull(annotatedClasses);
      assertEquals(4, annotatedClasses.size());
      annotatedClasses.forEach(annotValue -> assertEquals("SampleAnnotatedClass",
            annotValue));
   }

   @Test
   void givenPackage_whenScanAnnotatedMethods_thenAnnotationValues() {
      final List<String> annotatedMethods = scannerList.stream()
            .filter(SampleAnnotationScanner::supportsMethodScan)
            .map(SampleAnnotationScanner::scanAnnotatedMethods)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

      assertNotNull(annotatedMethods);
      assertEquals(3, annotatedMethods.size());
      annotatedMethods.forEach(annotValue -> assertEquals("annotatedMethod", annotValue));
   }
}