package cn.tuyucheng.taketoday.objectmapper;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import cn.tuyucheng.taketoday.objectmapper.dao.CounterDao;
import cn.tuyucheng.taketoday.objectmapper.dao.UserDao;

@Mapper
public interface DaoMapper {

    @DaoFactory
    UserDao getUserDao(@DaoKeyspace CqlIdentifier keyspace);

    @DaoFactory
    CounterDao getUserCounterDao(@DaoKeyspace CqlIdentifier keyspace);
}