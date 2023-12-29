package cn.tuyucheng.taketoday.spring.ai.service;

import cn.tuyucheng.taketoday.spring.ai.dto.PoetryDto;

public interface PoetryService {

   String getCatHaiku();

   PoetryDto getPoetryByGenreAndTheme(String genre, String theme);
}