package cn.tuyucheng.taketoday.swagger.basepath.service;

import cn.tuyucheng.taketoday.swagger.basepath.model.City;

import java.util.List;

public interface CityService {
   List<City> getAllCities();

   City getCityById(Long id);

   City createCity(City city);
}