package cn.tuyucheng.taketoday.chaosmonkey.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class PermissionsService {

	public List<String> getAllPermissions() {
		return Arrays.asList("CREATE", "READ", "UPDATE", "DELETE");
	}
}