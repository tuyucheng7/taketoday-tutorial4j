package cn.tuyucheng.taketoday.spring.data.cosmosdb;

import cn.tuyucheng.taketoday.spring.data.cosmosdb.entity.Product;
import cn.tuyucheng.taketoday.spring.data.cosmosdb.repository.ProductRepository;
import com.azure.data.cosmos.PartitionKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
class AzureCosmosDbApplicationManualTest {

    @Autowired
    ProductRepository productRepository;

    @Test
    void givenProductIsCreated_whenCallFindById_thenProductIsFound() {
        Product product = new Product();
        product.setProductid("1001");
        product.setProductCategory("Shirt");
        product.setPrice(110.0);
        product.setProductName("Blue Shirt");

        productRepository.save(product);
        Product retrievedProduct = productRepository.findById("1001", new PartitionKey("Shirt"))
              .orElse(null);
        Assert.notNull(retrievedProduct, "Retrieved Product is Null");
    }
}