package cn.tuyucheng.taketoday.springcloudgateway.custompredicates.config;

import cn.tuyucheng.taketoday.springcloudgateway.custompredicates.factories.GoldenCustomerRoutePredicateFactory;
import cn.tuyucheng.taketoday.springcloudgateway.custompredicates.factories.GoldenCustomerRoutePredicateFactory.Config;
import cn.tuyucheng.taketoday.springcloudgateway.custompredicates.service.GoldenCustomerService;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomPredicatesConfig {


    @Bean
    public GoldenCustomerRoutePredicateFactory goldenCustomer(GoldenCustomerService goldenCustomerService) {
        return new GoldenCustomerRoutePredicateFactory(goldenCustomerService);
    }


    //@Bean
    public RouteLocator routes(RouteLocatorBuilder builder, GoldenCustomerRoutePredicateFactory gf) {

        return builder.routes()
              .route("dsl_golden_route", r ->
                    r.predicate(gf.apply(new Config(true, "customerId")))
                          .and()
                          .path("/dsl_api/**")
                          .filters(f -> f.stripPrefix(1))
                          .uri("https://httpbin.org")
              )
              .route("dsl_common_route", r ->
                    r.predicate(gf.apply(new Config(false, "customerId")))
                          .and()
                          .path("/dsl_api/**")
                          .filters(f -> f.stripPrefix(1))
                          .uri("https://httpbin.org")
              )
              .build();
    }

}
