package cn.tuyucheng.taketoday.loadtesting.repository;

import cn.tuyucheng.taketoday.loadtesting.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

   List<Transaction> findByCustomerRewardsId(Integer rewardsId);
}
