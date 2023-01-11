package cn.tuyucheng.taketoday.boot.unique.field.dao;

import cn.tuyucheng.taketoday.boot.unique.field.data.Sale;
import cn.tuyucheng.taketoday.boot.unique.field.data.SaleId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SaleRepository extends MongoRepository<Sale, String> {
	Optional<Sale> findBySaleId(SaleId id);
}