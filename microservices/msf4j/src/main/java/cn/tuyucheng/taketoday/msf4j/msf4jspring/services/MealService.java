package cn.tuyucheng.taketoday.msf4j.msf4jspring.services;

import cn.tuyucheng.taketoday.msf4j.msf4jspring.domain.Meal;
import cn.tuyucheng.taketoday.msf4j.msf4jspring.repositories.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MealService {
    @Autowired
    private MealRepository mealRepository;

    public Meal find(int id) {
        return mealRepository.find(id);
    }

    public List<Meal> findAll() {
        return mealRepository.findAll();
    }

    public void create(Meal meal) {
        mealRepository.create(meal);
    }
}