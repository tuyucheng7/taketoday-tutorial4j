package cn.tuyucheng.taketoday.dip.daointerfaces;

import cn.tuyucheng.taketoday.dip.entities.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {

	Optional<Customer> findById(int id);

	List<Customer> findAll();
}