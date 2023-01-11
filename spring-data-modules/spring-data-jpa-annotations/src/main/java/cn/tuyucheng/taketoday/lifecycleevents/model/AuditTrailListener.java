package cn.tuyucheng.taketoday.lifecycleevents.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.*;

public class AuditTrailListener {
	private static final Log log = LogFactory.getLog(AuditTrailListener.class);

	@PrePersist
	@PreUpdate
	@PreRemove
	private void beforeAnyUpdate(User user) {
		if (user.getId() == 0) {
			log.info("[USER AUDIT] About to add a user");
		} else {
			log.info("[USER AUDIT] About to update/delete user: " + user.getId());
		}
	}

	@PostPersist
	@PostUpdate
	@PostRemove
	private void afterAnyUpdate(User user) {
		log.info("[USER AUDIT] add/update/delete complete for user: " + user.getId());
	}

	@PostLoad
	private void afterLoad(User user) {
		log.info("[USER AUDIT] user loaded from database: " + user.getId());
	}
}