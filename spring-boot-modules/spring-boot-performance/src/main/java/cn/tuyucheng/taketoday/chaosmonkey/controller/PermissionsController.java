package cn.tuyucheng.taketoday.chaosmonkey.controller;

import cn.tuyucheng.taketoday.chaosmonkey.service.PermissionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/permissions")
public class PermissionsController {

	@Autowired
	private PermissionsService permissionsService;

	@GetMapping
	public List<String> getAllPermissions() {
		return permissionsService.getAllPermissions();
	}
}