package cn.tuyucheng.taketoday.arangodb.repository;

import cn.tuyucheng.taketoday.arangodb.model.Article;
import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.repository.ArangoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends ArangoRepository<Article, String> {

    Iterable<Article> findByAuthor(String author);

    @Query("FOR a IN articles FILTER a.author == @author SORT a.publishDate ASC RETURN a")
    Iterable<Article> getByAuthor(@Param("author") String author);
}