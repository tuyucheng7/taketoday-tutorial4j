package cn.tuyucheng.taketoday.spring.data.cosmosdb.repository;

import cn.tuyucheng.taketoday.spring.data.cosmosdb.entity.Product;
import com.microsoft.azure.spring.data.cosmosdb.repository.CosmosRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CosmosRepository<Product, String> {
    List<Product> findByProductName(String productName);
}