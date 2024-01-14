package cn.tuyucheng.taketoday.memoryleaks.innerclass;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class StaticInnerClassMemoryLeakUnitTest {
   @Test
   @Disabled // Test deliberately ignored as memory leak tests consume lots of resources
   public void givenUsingInnerClass_whenInitializingInnerClass_thenInnerClassHoldsReferenceOfOuterObject() {
      InnerClassWrapper.SimpleInnerClass simpleInnerClassObj = new InnerClassWrapper().new SimpleInnerClass();
      System.out.print("Debug Point - VisuaLVM");
   }

   @Test
   @Disabled // Test deliberately ignored as memory leak tests consume lots of resources
   public void givenUsingStaticNestedClass_whenInitializingInnerClass_thenStaticNestedClassDoesntReferenceOuterObject() {
      StaticNestedClassWrapper.StaticNestedClass staticNestedClassObj = new StaticNestedClassWrapper.StaticNestedClass();
      System.out.print("Debug Point - VisuaLVM");
   }
}
