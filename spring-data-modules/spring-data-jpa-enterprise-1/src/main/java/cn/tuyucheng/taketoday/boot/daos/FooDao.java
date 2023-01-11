package cn.tuyucheng.taketoday.boot.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cn.tuyucheng.taketoday.boot.domain.Foo;

public interface FooDao extends JpaRepository<Foo, Long> {

	@Query("SELECT f FROM Foo f WHERE LOWER(f.name) = LOWER(:name)")
	Foo retrieveByName(@Param("name") String name);
}