package cn.tuyucheng.taketoday.persistence.service;

import cn.tuyucheng.taketoday.persistence.dao.common.IAuditOperations;
import cn.tuyucheng.taketoday.persistence.model.Bar;

public interface IBarAuditableService extends IBarService, IAuditOperations<Bar> {

}