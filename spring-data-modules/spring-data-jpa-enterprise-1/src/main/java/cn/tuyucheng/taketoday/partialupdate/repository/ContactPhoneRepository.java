package cn.tuyucheng.taketoday.partialupdate.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import cn.tuyucheng.taketoday.partialupdate.model.ContactPhone;

@Repository
public interface ContactPhoneRepository extends CrudRepository<ContactPhone, Long> {
    ContactPhone findById(long id);

    ContactPhone findByCustomerId(long id);
}