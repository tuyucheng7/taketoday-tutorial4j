package cn.tuyucheng.taketoday.pagination.repository;

import cn.tuyucheng.taketoday.pagination.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ProductRepository extends ReactiveSortingRepository<Product, UUID> {
   Flux<Product> findAllBy(Pageable pageable);
}