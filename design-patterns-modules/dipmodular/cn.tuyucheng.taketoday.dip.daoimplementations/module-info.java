module cn.tuyucheng.taketoday.dip.daoimplementations {
	requires cn.tuyucheng.taketoday.dip.entities;
	requires cn.tuyucheng.taketoday.dip.daos;
	provides cn.tuyucheng.taketoday.dip.daos.CustomerDao with cn.tuyucheng.taketoday.dip.daoimplementations.SimpleCustomerDao;
	exports cn.tuyucheng.taketoday.dip.daoimplementations;
}