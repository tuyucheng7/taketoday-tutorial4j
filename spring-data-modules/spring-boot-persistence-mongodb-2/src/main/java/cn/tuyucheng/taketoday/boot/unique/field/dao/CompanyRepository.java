package cn.tuyucheng.taketoday.boot.unique.field.dao;

import cn.tuyucheng.taketoday.boot.unique.field.data.Company;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CompanyRepository extends MongoRepository<Company, String> {
	Optional<Company> findByEmail(String email);
}