package cn.tuyucheng.taketoday.boot.daos;

import org.springframework.data.repository.CrudRepository;

import cn.tuyucheng.taketoday.boot.domain.Bar;

import java.io.Serializable;

public interface BarCrudRepository extends CrudRepository<Bar, Serializable> {
	//
}