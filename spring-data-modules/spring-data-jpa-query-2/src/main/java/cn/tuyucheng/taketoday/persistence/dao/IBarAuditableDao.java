package cn.tuyucheng.taketoday.persistence.dao;

import cn.tuyucheng.taketoday.persistence.model.Bar;
import cn.tuyucheng.taketoday.persistence.dao.common.IAuditOperations;

public interface IBarAuditableDao extends IBarDao, IAuditOperations<Bar> {
    //
}