package cn.tuyucheng.taketoday.dddhexagonalspring.infrastracture.configuration;

import cn.tuyucheng.taketoday.dddhexagonalspring.infrastracture.repository.cassandra.SpringDataCassandraOrderRepository;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@EnableCassandraRepositories(basePackageClasses = SpringDataCassandraOrderRepository.class)
public class CassandraConfiguration {

}
