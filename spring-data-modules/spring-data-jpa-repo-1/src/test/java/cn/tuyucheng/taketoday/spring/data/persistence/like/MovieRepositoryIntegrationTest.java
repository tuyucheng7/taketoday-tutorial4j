package cn.tuyucheng.taketoday.spring.data.persistence.like;

import cn.tuyucheng.taketoday.spring.data.persistence.like.model.Movie;
import cn.tuyucheng.taketoday.spring.data.persistence.like.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@ExtendWith(SpringExtension.class)
@Sql(scripts = {"/test-movie-data.sql"})
@SpringBootTest(classes = LikeApplication.class)
@Sql(scripts = "/test-movie-cleanup.sql", executionPhase = AFTER_TEST_METHOD)
class MovieRepositoryIntegrationTest {
   @Autowired
   private MovieRepository movieRepository;

   @Autowired
   private DataSource dataSource;

   @Test
   void givenPartialTitle_WhenFindByTitleContaining_ThenMoviesShouldReturn() {
      List<Movie> results = movieRepository.findByTitleContaining("in");
      assertEquals(3, results.size());

      results = movieRepository.findByTitleLike("%in%");
      assertEquals(3, results.size());

      results = movieRepository.findByTitleIsContaining("in");
      assertEquals(3, results.size());

      results = movieRepository.findByTitleContains("in");
      assertEquals(3, results.size());
   }

   @Test
   void givenStartOfRating_WhenFindByRatingStartsWith_ThenMoviesShouldReturn() {
      List<Movie> results = movieRepository.findByRatingStartsWith("PG");
      assertEquals(6, results.size());
   }

   @Test
   void givenLastName_WhenFindByDirectorEndsWith_ThenMoviesShouldReturn() {
      List<Movie> results = movieRepository.findByDirectorEndsWith("Burton");
      assertEquals(1, results.size());
   }

   @Test
   void givenPartialTitle_WhenFindByTitleContainingIgnoreCase_ThenMoviesShouldReturn() {
      List<Movie> results = movieRepository.findByTitleContainingIgnoreCase("the");
      assertEquals(2, results.size());
   }

   @Test
   void givenPartialTitle_WhenSearchByTitleLike_ThenMoviesShouldReturn() {
      List<Movie> results = movieRepository.searchByTitleLike("in");
      assertEquals(3, results.size());
   }

   @Test
   void givenStartOfRating_SearchFindByRatingStartsWith_ThenMoviesShouldReturn() {
      List<Movie> results = movieRepository.searchByRatingStartsWith("PG");
      assertEquals(6, results.size());
   }

   @Test
   void givenLastName_WhenSearchByDirectorEndsWith_ThenMoviesShouldReturn() {
      List<Movie> results = movieRepository.searchByDirectorEndsWith("Burton");
      assertEquals(1, results.size());
   }

   @Test
   void givenPartialRating_findByRatingNotContaining_ThenMoviesShouldReturn() {
      List<Movie> results = movieRepository.findByRatingNotContaining("PG");
      assertEquals(1, results.size());
   }

   @Test
   void givenPartialDirector_WhenFindByDirectorNotLike_ThenMoviesShouldReturn() {
      List<Movie> results = movieRepository.findByDirectorNotLike("An%");
      assertEquals(5, results.size());
   }
}