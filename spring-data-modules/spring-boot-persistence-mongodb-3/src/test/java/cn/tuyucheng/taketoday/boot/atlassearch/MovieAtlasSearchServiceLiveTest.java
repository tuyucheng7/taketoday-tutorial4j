package cn.tuyucheng.taketoday.boot.atlassearch;

import cn.tuyucheng.taketoday.boot.atlassearch.service.MovieAtlasSearchService;
import com.mongodb.client.model.search.SearchScore;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext
@ExtendWith(SpringExtension.class)
@TestPropertySource("/embedded.properties")
@SpringBootTest(classes = MongoDbAtlasSearchApplication.class)
class MovieAtlasSearchServiceLiveTest {

   @Autowired
   private MovieAtlasSearchService service;

   @Test
   void givenScoreBoost_thenFirstItemContainsPlot() {
      final String plot = "space";

      Document movies = service.late90sMovies(0, 1, plot, SearchScore.boost(2));

      assertTrue(movies.getList("rows", Document.class)
            .iterator()
            .next()
            .getString("fullplot")
            .contains(plot));
   }

   @Test
   void givenFacetOperator_thenCorrespondingBucketsReturned() {
      final String genre = "Sci-Fi";

      Document meta = service.genresThroughTheDecades(genre);

      Long lowerBound = meta
            .get("count", Document.class)
            .getLong("lowerBound");

      Document genresFacetFirstBucket = meta.get("facet", Document.class)
            .get("genresFacet", Document.class)
            .getList("buckets", Document.class)
            .iterator()
            .next();

      Document yearFacetFirstBucket = meta.get("facet", Document.class)
            .get("yearFacet", Document.class)
            .getList("buckets", Document.class)
            .iterator()
            .next();

      assertEquals(lowerBound, genresFacetFirstBucket.getLong("count"));
      assertEquals(genre, genresFacetFirstBucket.getString("_id"));
      assertNotNull(yearFacetFirstBucket);
   }
}