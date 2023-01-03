import cn.tuyucheng.taketoday.userdaomodule.UserDao;

module cn.tuyucheng.taketoday.userdaomodule {
	requires cn.tuyucheng.taketoday.entitymodule;
	requires cn.tuyucheng.taketoday.daomodule;
	provides cn.tuyucheng.taketoday.daomodule.Dao with UserDao;
	exports cn.tuyucheng.taketoday.userdaomodule;
}