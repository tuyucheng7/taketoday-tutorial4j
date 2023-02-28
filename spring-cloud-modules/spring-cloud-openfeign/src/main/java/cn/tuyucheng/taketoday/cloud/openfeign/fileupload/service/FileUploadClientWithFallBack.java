package cn.tuyucheng.taketoday.cloud.openfeign.fileupload.service;

import cn.tuyucheng.taketoday.cloud.openfeign.fileupload.config.FeignSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "file", url = "http://localhost:8081", configuration = FeignSupportConfig.class, fallback = FileUploadClientWithFallbackImpl.class)
public interface FileUploadClientWithFallBack {
	@PostMapping(value = "/upload-error", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	String fileUpload(@RequestPart(value = "file") MultipartFile file);
}