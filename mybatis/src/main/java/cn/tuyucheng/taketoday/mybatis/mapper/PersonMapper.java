package cn.tuyucheng.taketoday.mybatis.mapper;

import cn.tuyucheng.taketoday.mybatis.model.Address;
import cn.tuyucheng.taketoday.mybatis.model.Person;
import cn.tuyucheng.taketoday.mybatis.utils.MyBatisUtil;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.StatementType;

import java.util.List;
import java.util.Map;

public interface PersonMapper {

	@Insert("Insert into person(name) values (#{name})")
	Integer save(Person person);

	@Update("Update Person set name= #{name} where personId=#{personId}")
	void updatePerson(Person person);

	@Delete("Delete from Person where personId=#{personId}")
	void deletePersonById(Integer personId);

	@Select("SELECT person.personId, person.name FROM person WHERE person.personId = #{personId}")
	Person getPerson(Integer personId);

	@Select("Select personId,name from Person where personId=#{personId}")
	@Results(value = {@Result(property = "personId", column = "personId"), @Result(property = "name", column = "name"),
		@Result(property = "addresses", javaType = List.class, column = "personId", many = @Many(select = "getAddresses"))

	})
	Person getPersonById(Integer personId);

	@Select("select addressId,streetAddress,personId from address where personId=#{personId}")
	Address getAddresses(Integer personId);

	@Select("select * from Person ")
	@MapKey("personId")
	Map<Integer, Person> getAllPerson();

	@SelectProvider(type = MyBatisUtil.class, method = "getPersonByName")
	Person getPersonByName(String name);

	@Select(value = "{ CALL getPersonByProc( #{personId, mode=IN, jdbcType=INTEGER})}")
	@Options(statementType = StatementType.CALLABLE)
	Person getPersonByProc(Integer personId);
}