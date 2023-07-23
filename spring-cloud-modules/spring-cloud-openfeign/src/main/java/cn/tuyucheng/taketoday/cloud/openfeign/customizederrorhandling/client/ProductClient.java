package cn.tuyucheng.taketoday.cloud.openfeign.customizederrorhandling.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.tuyucheng.taketoday.cloud.openfeign.customizederrorhandling.config.FeignConfig;
import cn.tuyucheng.taketoday.cloud.openfeign.defaulterrorhandling.model.Product;

@FeignClient(name = "product-client-2", url = "http://localhost:8088/product/", configuration = FeignConfig.class)
public interface ProductClient {

   @RequestMapping(value = "{id}", method = RequestMethod.GET)
   Product getProduct(@PathVariable(value = "id") String id);
}