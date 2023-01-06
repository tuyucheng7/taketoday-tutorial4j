package cn.tuyucheng.taketoday.reactive.controller;

import cn.tuyucheng.taketoday.reactive.service.ReactiveUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
public class UploadController {
	final ReactiveUploadService uploadService;

	public UploadController(ReactiveUploadService uploadService) {
		this.uploadService = uploadService;
	}

	@PostMapping(path = "/upload")
	@ResponseBody
	public Mono<HttpStatus> uploadPdf(@RequestParam("file") final MultipartFile multipartFile) {
		return uploadService.uploadPdf(multipartFile.getResource());
	}

	@PostMapping(path = "/upload/multipart")
	@ResponseBody
	public Mono<HttpStatus> uploadMultipart(@RequestParam("file") final MultipartFile multipartFile) {
		return uploadService.uploadMultipart(multipartFile);
	}
}