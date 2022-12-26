package cn.tuyucheng.taketoday.chaosmonkey.service;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@ExcludeFromJacocoGeneratedReport
public class PermissionsService {

	public List<String> getAllPermissions() {
		return Arrays.asList("CREATE", "READ", "UPDATE", "DELETE");
	}
}