package cn.tuyucheng.taketoday.cloud.openfeign.fileupload.service;

import cn.tuyucheng.taketoday.cloud.openfeign.exception.BadRequestException;
import cn.tuyucheng.taketoday.cloud.openfeign.exception.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUploadClientWithFallbackImpl implements FileUploadClientWithFallBack {

	@Override
	public String fileUpload(MultipartFile file) {
		try {
			throw new NotFoundException("hi, something wrong");
		} catch (Exception ex) {

			if (ex instanceof BadRequestException) {
				return "Bad Request!!!";
			}
			if (ex instanceof NotFoundException) {
				return "Not Found!!!";
			}
			if (ex instanceof Exception) {
				return "Exception!!!";
			}
			return "Successfully Uploaded file!!!";
		}
	}
}