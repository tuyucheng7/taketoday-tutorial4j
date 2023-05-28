package conf;

import ninja.Filter;

import java.util.List;

public class Filters implements ninja.application.ApplicationFilters {

   @Override
   public void addFilters(List<Class<? extends Filter>> filters) {
      // Add your application - wide filters here
      // filters.add(MyFilter.class);
   }
}
