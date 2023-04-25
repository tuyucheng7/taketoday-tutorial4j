package cn.tuyucheng.taketoday.mvc.velocity.service;

import cn.tuyucheng.taketoday.mvc.velocity.domain.Tutorial;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TutorialsService implements ITutorialsService {

    public List<Tutorial> listTutorials() {
        return Arrays.asList(new Tutorial(1, "Guava", "Introduction to Guava", "GuavaAuthor"), new Tutorial(2, "Android", "Introduction to Android", "AndroidAuthor"));
    }
}
