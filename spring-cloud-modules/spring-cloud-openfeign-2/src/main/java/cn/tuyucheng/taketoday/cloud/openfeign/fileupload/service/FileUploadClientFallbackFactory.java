package cn.tuyucheng.taketoday.cloud.openfeign.fileupload.service;

import cn.tuyucheng.taketoday.cloud.openfeign.exception.BadRequestException;
import cn.tuyucheng.taketoday.cloud.openfeign.exception.NotFoundException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUploadClientFallbackFactory implements FallbackFactory<FileUploadClientWithFallbackFactory> {
	@Override
	public FileUploadClientWithFallbackFactory create(Throwable cause) {
		return new FileUploadClientWithFallbackFactory() {
			@Override
			public String fileUpload(MultipartFile file) {
				if (cause instanceof BadRequestException) {
					return "Bad Request!!!";
				}
				if (cause instanceof NotFoundException) {
					return "Not Found!!!";
				}
				if (cause instanceof Exception) {
					return "Exception!!!";
				}
				return "Successfully Uploaded file!!!";
			}
		};
	}
}