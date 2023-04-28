package cn.tuyucheng.taketoday.protobuf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseController {

	@Autowired
	CourseRepository courseRepo;

	@RequestMapping("/courses/{id}")
	BaeldungTraining.Course customer(@PathVariable Integer id) {
		return courseRepo.getCourse(id);
	}
}
