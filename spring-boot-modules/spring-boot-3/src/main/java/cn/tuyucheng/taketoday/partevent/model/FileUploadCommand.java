package cn.tuyucheng.taketoday.partevent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadCommand {
	private String name;
	private FilePart file;
}