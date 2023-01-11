package cn.tuyucheng.taketoday.boot.json.convertfile.web;

import cn.tuyucheng.taketoday.boot.json.convertfile.ImportUtils;
import cn.tuyucheng.taketoday.boot.json.convertfile.service.ImportJsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/import-json")
public class ImportJsonController {
	@Autowired
	private ImportJsonService service;

	@PostMapping("/{collection}")
	public String postJson(@RequestBody String jsonStrings, @PathVariable String collection) {
		List<String> jsonLines = ImportUtils.lines(jsonStrings);
		return service.importTo(collection, jsonLines);
	}

	@PostMapping("/file/{collection}")
	public String postJsonFile(@RequestPart("parts") MultipartFile jsonStringsFile, @PathVariable String collection) throws IOException {
		List<String> jsonLines = ImportUtils.lines(jsonStringsFile);
		return service.importTo(collection, jsonLines);
	}
}