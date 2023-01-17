package cn.tuyucheng.taketoday.pattern.cleanarchitecture.usercreation;

public interface UserInputBoundary {
	UserResponseModel create(UserRequestModel requestModel);
}
