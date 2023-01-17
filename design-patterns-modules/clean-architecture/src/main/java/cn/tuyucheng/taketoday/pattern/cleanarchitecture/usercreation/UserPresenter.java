package cn.tuyucheng.taketoday.pattern.cleanarchitecture.usercreation;

interface UserPresenter {
	UserResponseModel prepareSuccessView(UserResponseModel user);

	UserResponseModel prepareFailView(String error);
}
