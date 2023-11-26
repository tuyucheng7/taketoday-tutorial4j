package cn.tuyucheng.taketoday.aliasfor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MyMappingController.class)
public class AliasForUnitTest {

   @Autowired
   private ConfigurableApplicationContext context;

   Class controllerClass;

   @BeforeEach
   void setControllerBean() {
      MyMappingController controllerBean = context.getBean(MyMappingController.class);
      controllerClass = controllerBean.getClass();
   }

   @Test
   void givenComposedAnnotation_whenExplicitAlias_thenMetaAnnotationAttributeOverridden() {
      for (Method method : controllerClass.getMethods()) {
         if (method.isAnnotationPresent(MyMapping.class)) {
            MyMapping annotation = AnnotationUtils.findAnnotation(method, MyMapping.class);
            RequestMapping metaAnnotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);

            assertEquals(RequestMethod.PATCH, annotation.action()[0]);

            assertEquals(0, metaAnnotation.method().length);
         }
      }
   }

   @Test
   void givenComposedAnnotation_whenImplicitAlias_thenAttributesEqual() {
      for (Method method : controllerClass.getMethods()) {
         if (method.isAnnotationPresent(MyMapping.class)) {
            MyMapping annotationOnBean = AnnotationUtils.findAnnotation(method, MyMapping.class);

            assertEquals(annotationOnBean.mapping()[0], annotationOnBean.route()[0]);
            assertEquals(annotationOnBean.value()[0], annotationOnBean.route()[0]);
         }
      }
   }
}