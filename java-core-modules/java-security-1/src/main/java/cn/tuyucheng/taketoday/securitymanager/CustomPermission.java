package cn.tuyucheng.taketoday.securitymanager;

import java.security.BasicPermission;

public class CustomPermission extends BasicPermission {
	public CustomPermission(String name) {
		super(name);
	}

	public CustomPermission(String name, String actions) {
		super(name, actions);
	}
}
