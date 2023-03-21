package cn.tuyucheng.taketoday.roles.custom.persistence.dao;

import cn.tuyucheng.taketoday.roles.custom.persistence.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Organization findByName(String name);
}
