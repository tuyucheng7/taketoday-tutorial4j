package cn.tuyucheng.taketoday.thymeleaf.controller;

import cn.tuyucheng.taketoday.thymeleaf.model.Student;
import cn.tuyucheng.taketoday.thymeleaf.utils.StudentUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;

/**
 * Handles requests for the student model.
 */
@Controller
public class StudentController {

    @RequestMapping(value = "/saveStudent", method = RequestMethod.POST)
    public String saveStudent(@Valid @ModelAttribute Student student, BindingResult errors, Model model) {
        if (!errors.hasErrors()) {
            // get mock objects
            List<Student> students = StudentUtils.buildStudents();
            // add current student
            students.add(student);
            model.addAttribute("students", students);
        }
        return ((errors.hasErrors()) ? "addStudent.html" : "listStudents.html");
    }

    @RequestMapping(value = "/addStudent", method = RequestMethod.GET)
    public String addStudent(Model model) {
        model.addAttribute("student", new Student());
        return "addStudent.html";
    }

    @RequestMapping(value = "/listStudents", method = RequestMethod.GET)
    public String listStudent(Model model) {
        model.addAttribute("students", StudentUtils.buildStudents());
        return "listStudents.html";
    }
}