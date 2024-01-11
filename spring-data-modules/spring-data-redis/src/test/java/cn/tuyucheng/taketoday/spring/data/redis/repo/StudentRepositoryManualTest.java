package cn.tuyucheng.taketoday.spring.data.redis.repo;

import cn.tuyucheng.taketoday.spring.data.redis.config.RedisConfig;
import cn.tuyucheng.taketoday.spring.data.redis.model.Student;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import redis.embedded.RedisServerBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RedisConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class StudentRepositoryManualTest {

   @Autowired
   private StudentRepository studentRepository;

   private static redis.embedded.RedisServer redisServer;

   @BeforeAll
   static void startRedisServer() throws IOException {
      redisServer = new RedisServerBuilder().port(6379).setting("maxmemory 128M").build();
      redisServer.start();
   }

   @AfterAll
   static void stopRedisServer() throws IOException {
      redisServer.stop();
   }

   @Test
   void whenSavingStudent_thenAvailableOnRetrieval() throws Exception {
      final Student student = new Student("Eng2015001", "John Doe", Student.Gender.MALE, 1);
      studentRepository.save(student);
      final Student retrievedStudent = studentRepository.findById(student.getId()).get();
      assertEquals(student.getId(), retrievedStudent.getId());
   }

   @Test
   void whenUpdatingStudent_thenAvailableOnRetrieval() throws Exception {
      final Student student = new Student("Eng2015001", "John Doe", Student.Gender.MALE, 1);
      studentRepository.save(student);
      student.setName("Richard Watson");
      studentRepository.save(student);
      final Student retrievedStudent = studentRepository.findById(student.getId()).get();
      assertEquals(student.getName(), retrievedStudent.getName());
   }

   @Test
   void whenSavingStudents_thenAllShouldAvailableOnRetrieval() throws Exception {
      final Student engStudent = new Student("Eng2015001", "John Doe", Student.Gender.MALE, 1);
      final Student medStudent = new Student("Med2015001", "Gareth Houston", Student.Gender.MALE, 2);
      studentRepository.save(engStudent);
      studentRepository.save(medStudent);
      List<Student> students = new ArrayList<>();
      studentRepository.findAll().forEach(students::add);
      assertEquals(students.size(), 2);
   }

   @Test
   void whenDeletingStudent_thenNotAvailableOnRetrieval() throws Exception {
      final Student student = new Student("Eng2015001", "John Doe", Student.Gender.MALE, 1);
      studentRepository.save(student);
      studentRepository.deleteById(student.getId());
      final Student retrievedStudent = studentRepository.findById(student.getId()).orElse(null);
      assertNull(retrievedStudent);
   }
}