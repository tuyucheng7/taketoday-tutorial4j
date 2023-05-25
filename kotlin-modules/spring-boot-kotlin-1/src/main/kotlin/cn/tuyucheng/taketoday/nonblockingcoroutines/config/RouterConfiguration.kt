package cn.tuyucheng.taketoday.nonblockingcoroutines.config

import cn.tuyucheng.taketoday.nonblockingcoroutines.handlers.ProductsHandler
import kotlinx.coroutines.FlowPreview
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RouterConfiguration {

   @FlowPreview
   @Bean
   fun productRoutes(productsHandler: ProductsHandler) = coRouter {
      GET("/", productsHandler::findAll)
      GET("/{id}", productsHandler::findOne)
      GET("/{id}/stock", productsHandler::findOneInStock)
   }
}