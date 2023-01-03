module cn.tuyucheng.taketoday.mainppmodule {
	requires cn.tuyucheng.taketoday.entitymodule;
	requires cn.tuyucheng.taketoday.daomodule;
	requires cn.tuyucheng.taketoday.userdaomodule;
	uses cn.tuyucheng.taketoday.userdaomodule.UserDao;
}