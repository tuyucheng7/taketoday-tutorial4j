package cn.tuyucheng.taketoday.mapper;

import cn.tuyucheng.taketoday.dto.ArticleDTO;
import cn.tuyucheng.taketoday.dto.PersonDTO;
import cn.tuyucheng.taketoday.entity.Article;
import cn.tuyucheng.taketoday.entity.Person;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ArticleMapper {

	ArticleMapper INSTANCE = Mappers.getMapper(ArticleMapper.class);

	ArticleDTO articleToArticleDto(Article article);

	default PersonDTO personToPersonDto(Person person) {
		return Mappers.getMapper(PersonMapper.class).personToPersonDTO(person);
	}
}