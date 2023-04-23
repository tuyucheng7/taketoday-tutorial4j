package cn.tuyucheng.taketoday.annotations;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Optional;

@NoRepositoryBean
public interface MyUtilityRepository<T, ID extends Serializable> extends CrudRepository<T, ID> {

	Optional<T> findById(ID id);
}