package cn.tuyucheng.taketoday.spring.data.persistence.search;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class StudentApplicationUnitTest {

   @Autowired
   private StudentRepository studentRepo;
   private List<Student> students;

   @BeforeEach
   void fillData() {
      students = new ArrayList<>();
      int count = 10;
      Random r = new Random();
      List<Integer> scores = r.ints(0, 101)
            .distinct()
            .limit(count)
            .boxed()
            .toList();

      for (int i = 0; i < count; i++) {
         Integer score = scores.get(i);
         Student s = new Student("Student-" + i, score);
         students.add(s);
      }

      studentRepo.saveAll(students);
      Comparator<Student> c = Comparator.comparing(Student::getScore);
      c = c.reversed();
      students.sort(c);
   }

   @AfterEach
   void clearData() {
      studentRepo.deleteAll();
   }

   @Test
   void givenStudentScores_whenMoreThanOne_thenFindFirst() {
      Student student = studentRepo.findFirstByOrderByScoreDesc();
      Student s = students.get(0);
      assertEquals(student, s);
   }

   @Test
   void givenStudentScores_whenMoreThan3_thenFindFirstThree() {
      List<Student> firstThree = studentRepo.findFirst3ByOrderByScoreDesc();
      List<Student> sList = students.subList(0, 3);
      assertArrayEquals(firstThree.toArray(), sList.toArray());
   }

   @Test
   void givenStudentScores_whenNameMatches_thenFindFirstStudent() {
      String matchString = "3";
      Student student = studentRepo.findFirstByNameLike("%" + matchString + "%", Sort.by("score")
            .descending());
      Student s = students.stream()
            .filter(a -> a.getName()
                  .contains(matchString))
            .findFirst()
            .orElse(null);
      assertEquals(student, s);
   }

   @Test
   void givenStudentScores_whenBetweenRange_thenFindFirstTwoStudents() {
      List<Student> topTwoBetweenRange = studentRepo.findFirst2ByScoreBetween(50, 60, Sort.by("score")
            .descending());
      List<Student> _students = students.stream()
            .filter(a -> a.getScore() >= 50 && a.getScore() <= 60)
            .limit(2)
            .toList();
      assertArrayEquals(_students.toArray(), topTwoBetweenRange.toArray());
   }
}