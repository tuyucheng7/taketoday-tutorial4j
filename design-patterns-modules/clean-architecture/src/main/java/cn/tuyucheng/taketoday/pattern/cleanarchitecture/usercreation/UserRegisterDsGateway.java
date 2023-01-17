package cn.tuyucheng.taketoday.pattern.cleanarchitecture.usercreation;

interface UserRegisterDsGateway {
	boolean existsByName(String identifier);

	void save(UserDsRequestModel requestModel);
}
