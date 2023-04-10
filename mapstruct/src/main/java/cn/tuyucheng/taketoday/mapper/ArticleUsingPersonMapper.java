package cn.tuyucheng.taketoday.mapper;

import cn.tuyucheng.taketoday.dto.ArticleDTO;
import cn.tuyucheng.taketoday.dto.PersonDTO;
import cn.tuyucheng.taketoday.entity.Article;
import cn.tuyucheng.taketoday.entity.Person;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = PersonMapper.class)
public interface ArticleUsingPersonMapper {

	ArticleUsingPersonMapper INSTANCE = Mappers.getMapper(ArticleUsingPersonMapper.class);

	ArticleDTO articleToArticleDto(Article article);
}