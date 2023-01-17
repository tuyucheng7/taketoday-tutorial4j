package cn.tuyucheng.taketoday.mybatis.mapper;

import cn.tuyucheng.taketoday.mybatis.model.Address;
import cn.tuyucheng.taketoday.mybatis.model.Person;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

public interface AddressMapper {

	@Insert("Insert into address (streetAddress,personId) values(#{streetAddress},#{personId})")
	@Options(useGeneratedKeys = true, flushCache = true)
	Integer saveAddress(Address address);

	@Select("SELECT addressId, streetAddress FROM Address WHERE addressId = #{addressId}")
	@Results(value = {@Result(property = "addressId", column = "addressId"),
		@Result(property = "streetAddress", column = "streetAddress"),
		@Result(property = "person", column = "personId", javaType = Person.class, one = @One(select = "getPerson"))})
	Address getAddresses(Integer addressId);

	@Select("SELECT personId FROM address WHERE addressId = #{addressId})")
	Person getPerson(Integer addressId);
}