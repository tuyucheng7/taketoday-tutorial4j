package cn.tuyucheng.taketoday.dynamicvalidation.dao;

import cn.tuyucheng.taketoday.dynamicvalidation.model.ContactInfoExpression;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ContactInfoExpressionRepository extends Repository<ContactInfoExpression, String> {
	Optional<ContactInfoExpression> findById(String id);
}
