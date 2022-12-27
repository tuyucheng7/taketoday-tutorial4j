package cn.tuyucheng.taketoday.webservice;

import cn.tuyucheng.taketoday.webservice.generated.Product;

public interface ProductRepository {

	Product findProduct(String id);
}