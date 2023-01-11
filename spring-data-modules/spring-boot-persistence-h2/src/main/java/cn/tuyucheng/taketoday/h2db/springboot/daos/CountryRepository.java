package cn.tuyucheng.taketoday.h2db.springboot.daos;

import cn.tuyucheng.taketoday.h2db.springboot.models.Country;
import org.springframework.data.repository.CrudRepository;

public interface CountryRepository extends CrudRepository<Country, Integer> {
}