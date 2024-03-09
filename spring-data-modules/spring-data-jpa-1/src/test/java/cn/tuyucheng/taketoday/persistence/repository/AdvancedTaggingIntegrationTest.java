package cn.tuyucheng.taketoday.persistence.repository;

import cn.tuyucheng.taketoday.config.StudentJpaConfig;
import cn.tuyucheng.taketoday.inmemory.persistence.dao.ManyStudentRepository;
import cn.tuyucheng.taketoday.inmemory.persistence.dao.ManyTagRepository;
import cn.tuyucheng.taketoday.inmemory.persistence.dao.StudentRepository;
import cn.tuyucheng.taketoday.inmemory.persistence.model.KVTag;
import cn.tuyucheng.taketoday.inmemory.persistence.model.ManyStudent;
import cn.tuyucheng.taketoday.inmemory.persistence.model.ManyTag;
import cn.tuyucheng.taketoday.inmemory.persistence.model.SkillTag;
import cn.tuyucheng.taketoday.inmemory.persistence.model.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {StudentJpaConfig.class}, loader = AnnotationConfigContextLoader.class)
@Transactional
@DirtiesContext
class AdvancedTaggingIntegrationTest {
   @Autowired
   private StudentRepository studentRepository;

   @Autowired
   private ManyStudentRepository manyStudentRepository;

   @Autowired
   private ManyTagRepository manyTagRepository;

   @Test
   void givenStudentWithSkillTags_whenSave_thenGetByNameAndSkillTag() {
      Student student = new Student(1, "Will");
      SkillTag skill1 = new SkillTag("java", 5);
      student.setSkillTags(Arrays.asList(skill1));
      studentRepository.save(student);

      Student student2 = new Student(2, "Joe");
      SkillTag skill2 = new SkillTag("java", 1);
      student2.setSkillTags(Arrays.asList(skill2));
      studentRepository.save(student2);

      List<Student> students = studentRepository.retrieveByNameFilterByMinimumSkillTag("java", 3);
      assertEquals(1, students.size(), "size incorrect");
   }

   @Test
   void givenStudentWithKVTags_whenSave_thenGetByTagOk() {
      Student student = new Student(0, "John");
      student.setKVTags(Arrays.asList(new KVTag("department", "computer science")));
      studentRepository.save(student);

      Student student2 = new Student(1, "James");
      student2.setKVTags(Arrays.asList(new KVTag("department", "humanities")));
      studentRepository.save(student2);

      List<Student> students = studentRepository.retrieveByKeyTag("department");
      assertEquals(2, students.size(), "size incorrect");
   }

   @Test
   void givenStudentWithManyTags_whenSave_theyGetByTagOk() {
      ManyTag tag = new ManyTag("full time");
      manyTagRepository.save(tag);

      ManyStudent student = new ManyStudent("John");
      student.setManyTags(Collections.singleton(tag));
      manyStudentRepository.save(student);

      List<ManyStudent> students = manyStudentRepository.findByManyTags_Name("full time");
      assertEquals(1, students.size(), "size incorrect");
   }
}