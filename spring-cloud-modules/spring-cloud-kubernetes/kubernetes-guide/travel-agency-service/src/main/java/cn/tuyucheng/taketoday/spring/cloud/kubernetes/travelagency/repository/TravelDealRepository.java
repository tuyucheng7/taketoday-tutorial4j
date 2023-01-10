package cn.tuyucheng.taketoday.spring.cloud.kubernetes.travelagency.repository;

import cn.tuyucheng.taketoday.spring.cloud.kubernetes.travelagency.model.TravelDeal;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TravelDealRepository extends MongoRepository<TravelDeal, String> {

    public List<TravelDeal> findByDestination(String destination);

}