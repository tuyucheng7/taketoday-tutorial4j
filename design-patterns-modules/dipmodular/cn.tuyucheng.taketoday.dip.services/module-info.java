module cn.tuyucheng.taketoday.dip.services {
	requires cn.tuyucheng.taketoday.dip.entities;
	requires cn.tuyucheng.taketoday.dip.daos;
	uses cn.tuyucheng.taketoday.dip.daos.CustomerDao;
	exports cn.tuyucheng.taketoday.dip.services;
}