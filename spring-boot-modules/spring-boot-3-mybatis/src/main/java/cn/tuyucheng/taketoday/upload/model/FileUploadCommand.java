package cn.tuyucheng.taketoday.upload.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadCommand {
   private String name;
   private FilePart file;
}