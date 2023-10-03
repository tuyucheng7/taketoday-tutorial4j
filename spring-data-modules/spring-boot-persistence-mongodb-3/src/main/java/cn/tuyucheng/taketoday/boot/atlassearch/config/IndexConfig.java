package cn.tuyucheng.taketoday.boot.atlassearch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IndexConfig {

   @Value("${cn.tuyucheng.taketoday.atlas-search.index.query}")
   private String queryIndex;

   @Value("${cn.tuyucheng.taketoday.atlas-search.index.facet}")
   private String facetIndex;

   public String getFacetIndex() {
      return facetIndex;
   }

   public String getQueryIndex() {
      return queryIndex;
   }
}