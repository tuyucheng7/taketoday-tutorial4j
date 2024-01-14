package cn.tuyucheng.taketoday.deepcopyarraylist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class DeepCopyArrayListUnitTest {

   @Test
   public void whenCreatingCopyWithCloneable_thenObjectsShouldNotBeSame() {
      Course course = new Course(1, "Spring Masterclass");
      Student student1 = new Student(1, "John", course);
      Student student2 = new Student(2, "David", course);
      List<Student> students = new ArrayList<>();
      students.add(student1);
      students.add(student2);

      List<Student> deepCopy = Student.deepCopyUsingCloneable(students);

      Assertions.assertEquals(students.get(0), deepCopy.get(0));
      Assertions.assertNotSame(students.get(0), deepCopy.get(0));
      Assertions.assertEquals(students.get(1), deepCopy.get(1));
      Assertions.assertNotSame(students.get(1), deepCopy.get(1));

   }

   @Test
   public void whenCreatingDeepCopyWithCopyConstructor_thenObjectsShouldNotBeSame() {

      Course course = new Course(1, "Spring Masterclass");
      Student student1 = new Student(1, "John", course);
      Student student2 = new Student(2, "David", course);

      List<Student> students = new ArrayList<>();
      students.add(student1);
      students.add(student2);

      List<Student> deepCopy = Student.deepCopyUsingCopyConstructor(students);

      Assertions.assertEquals(students.get(0), deepCopy.get(0));
      Assertions.assertNotSame(students.get(0), deepCopy.get(0));
      Assertions.assertEquals(students.get(1), deepCopy.get(1));
      Assertions.assertNotSame(students.get(1), deepCopy.get(1));
   }

   @Test
   public void whenCreatingDeepCopyWithSerializationUtils_thenObjectsShouldNotBeSame() {

      Course course = new Course(1, "Spring Masterclass");
      Student student1 = new Student(1, "John", course);
      Student student2 = new Student(2, "David", course);

      List<Student> students = new ArrayList<>();
      students.add(student1);
      students.add(student2);

      List<Student> deepCopy = Student.deepCopyUsingSerialization(students);

      Assertions.assertEquals(students.get(0), deepCopy.get(0));
      Assertions.assertNotSame(students.get(0), deepCopy.get(0));
      Assertions.assertEquals(students.get(1), deepCopy.get(1));
      Assertions.assertNotSame(students.get(1), deepCopy.get(1));
   }

   @Test
   public void whenCreatingDeepCopyWithJackson_thenObjectsShouldNotBeSame() {

      Course course = new Course(1, "Spring Masterclass");
      Student student1 = new Student(1, "John", course);
      Student student2 = new Student(2, "David", course);

      List<Student> students = new ArrayList<>();
      students.add(student1);
      students.add(student2);

      List<Student> deepCopy = Student.deepCopyUsingJackson(students);

      Assertions.assertEquals(students.get(0), deepCopy.get(0));
      Assertions.assertNotSame(students.get(0), deepCopy.get(0));
      Assertions.assertEquals(students.get(1), deepCopy.get(1));
      Assertions.assertNotSame(students.get(1), deepCopy.get(1));
   }
}
