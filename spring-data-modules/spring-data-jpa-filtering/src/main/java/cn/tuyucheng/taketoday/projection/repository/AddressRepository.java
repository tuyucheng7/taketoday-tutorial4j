package cn.tuyucheng.taketoday.projection.repository;

import cn.tuyucheng.taketoday.projection.model.Address;
import cn.tuyucheng.taketoday.projection.view.AddressView;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface AddressRepository extends Repository<Address, Long> {
	List<AddressView> getAddressByState(String state);
}