package cn.tuyucheng.taketoday.datajpadelete.repository;

import cn.tuyucheng.taketoday.datajpadelete.entity.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

}